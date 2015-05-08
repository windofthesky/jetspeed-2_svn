/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.cache;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.cache.impl.EhCacheConfigResource;
import org.apache.jetspeed.components.test.AbstractJexlSpringTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * TestDistributedJSPMCache - test JetspeedSecurityPersistenceManager cache
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class TestDistributedJSPMCache extends AbstractJexlSpringTestCase
{
    protected static Logger log = LoggerFactory.getLogger(TestDistributedJSPMCache.class);

    private static final long CACHE_NOTIFICATION_STARTUP_WAIT = 10000;
    private static final long CACHE_NOTIFICATION_WAIT = 2000;
    private static final long CACHE_NOTIFICATION_POLL = 250;

    /**
     * Creates the test suite.
     *
     * @return a test suite that includes all methods starting with "test"
     */
    public static Test suite()
    {
        return new TestSuite(TestDistributedJSPMCache.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        // setup cache properties
        EhCacheConfigResource.getInstance(EhCacheConfigResource.EHCACHE_CONFIG_RESOURCE_DISTRIBUTED_CACHE, true);

        // continue setup
        super.setUp();
    }

    @Override
    protected String testProgramSystemPropertyValueFilter(String propertyName, int index, String propertyValue)
    {
        if (propertyName.equals(EhCacheConfigResource.EHCACHE_PORT_PROP_NAME))
        {
            return Integer.toString(Integer.parseInt(propertyValue)+index);
        }
        return propertyValue;
    }

    @Override
    protected Map<String,String> testProgramSystemProperties()
    {
        Map<String,String> systemProperties = super.testProgramSystemProperties();
        systemProperties.put("log4j.configuration", "log4j-stdout.properties");
        return systemProperties;
    }

    /**
     * Tests distributed cache operation for JSPM
     */
    public void testDistributedJSPMCache()
    {
        String result;

        // check for distributed database support
        String databaseName = System.getProperty("org.apache.jetspeed.database.default.name");
        if ((databaseName != null) && databaseName.equals("derby"))
        {
            System.out.println("Database support not distributed: system limitation... test skipped");
            log.warn("Database support not distributed: system limitation... test skipped");
            return;
        }

        // create and start servers
        final TestProgram server0 = new TestProgram("server-0", DistributedJSPMCacheServer.class, 0);
        final TestProgram server1 = new TestProgram("server-1", DistributedJSPMCacheServer.class, 1);
        try
        {
            // start servers
            server0.start();
            server1.start();

            // wait until servers have started
            server0.execute("");
            server1.execute("");

            // check to ensure servers have distributed page manager caches
            boolean server0Distributed = false;
            boolean server1Distributed = false;
            final long distributedCheckStarted = System.currentTimeMillis();
            do
            {
                // check servers
                if (!server0Distributed)
                {
                    result = server0.execute("jspmCache.isDistributed();");
                    assertTrue(!result.contains("Exception"));
                    server0Distributed = result.endsWith("true");
                }
                if (!server1Distributed)
                {
                    result = server1.execute("jspmCache.isDistributed();");
                    assertTrue(!result.contains("Exception"));
                    server1Distributed = result.endsWith("true");
                }

                // wait if servers not distributed
                if (!server0Distributed || !server1Distributed)
                {
                    sleep(server0, server1, CACHE_NOTIFICATION_POLL);
                }
            }
            while ((!server0Distributed || !server1Distributed) && (System.currentTimeMillis()-distributedCheckStarted < CACHE_NOTIFICATION_STARTUP_WAIT));
            if (!server0Distributed && !server1Distributed)
            {
                System.out.println("Server JSPM cache not distributed: possible system limitation... test skipped");
                log.warn("Server JSPM cache not distributed: possible system limitation... test skipped");
                return;
            }
            assertTrue(server0Distributed);
            assertTrue(server1Distributed);

            // start transaction
            result = server0.execute("jspmCacheServer.beginTransaction();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("jspmCacheServer.beginTransaction();");
            assertTrue(!result.contains("Exception"));
            try
            {
                // create default domain if necessary
                boolean createdDefaultDomain = false;
                result = server0.execute("(jspm.getDomainByName(\"[default]\") != null);");
                assertTrue(!result.contains("Exception"));
                if (!Boolean.parseBoolean(getResultValue(result)))
                {
                    result = server0.execute("defaultDomain = new(\"org.apache.jetspeed.security.impl.SecurityDomainImpl\");");
                    assertTrue(!result.contains("Exception"));
                    result = server0.execute("defaultDomain.setName(\"[default]\")");
                    assertTrue(!result.contains("Exception"));
                    result = server0.execute("jspm.addDomain(defaultDomain);");
                    assertTrue(!result.contains("Exception"));
                    result = server0.execute("jspmCacheServer.commitTransaction();");
                    assertTrue(!result.contains("Exception"));
                    result = server1.execute("jspmCacheServer.commitTransaction();");
                    assertTrue(!result.contains("Exception"));
                    createdDefaultDomain = true;
                    sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
                    result = server0.execute("jspmCacheServer.beginTransaction();");
                    assertTrue(!result.contains("Exception"));
                    result = server1.execute("jspmCacheServer.beginTransaction();");
                    assertTrue(!result.contains("Exception"));
                }

                // clear caches
                result = server0.execute("jspmCache.clear();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCache.clear();");
                assertTrue(!result.contains("Exception"));

                // perform some queries to load the caches
                result = server0.execute("jspm.getPrincipals(\"\", userType).size();");
                assertTrue(!result.contains("Exception"));
                int server0Principals = Integer.parseInt(getResultValue(result));
                result = server1.execute("jspm.getPrincipals(\"\", userType).size();");
                assertTrue(!result.contains("Exception"));
                int server1Principals = Integer.parseInt(getResultValue(result));
                assertEquals(server0Principals, server1Principals);
                result = server0.execute("jspm.getPermissions().size();");
                assertTrue(!result.contains("Exception"));
                int server0Permissions = Integer.parseInt(getResultValue(result));
                result = server1.execute("jspm.getPermissions().size();");
                assertTrue(!result.contains("Exception"));
                int server1Permissions = Integer.parseInt(getResultValue(result));
                assertEquals(server1Permissions, server1Permissions);
                result = server0.execute("jspm.getAllDomains().size();");
                assertTrue(!result.contains("Exception"));
                int server0Domains = Integer.parseInt(getResultValue(result));
                result = server1.execute("jspm.getAllDomains().size();");
                assertTrue(!result.contains("Exception"));
                int server1Domains = Integer.parseInt(getResultValue(result));
                assertEquals(server0Domains, server1Domains);

                // test principal instance and query cache operation
                result = server0.execute("testUser = new(\"org.apache.jetspeed.security.impl.UserImpl\", \"TEST-USER\");");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("jspm.addPrincipal(testUser, null);");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("jspm.getPrincipals(\"\", userType).size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server0Principals+1, Integer.parseInt(getResultValue(result)));
                result = server0.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
                result = server0.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspm.getPrincipals(\"\", userType).size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server1Principals+1, Integer.parseInt(getResultValue(result)));
                result = server1.execute("testUser = jspm.getPrincipal(\"TEST-USER\", userType);");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspm.removePrincipal(testUser);");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspm.getPrincipals(\"\", userType).size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server1Principals, Integer.parseInt(getResultValue(result)));
                result = server0.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
                result = server0.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("jspm.getPrincipals(\"\", userType).size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server0Principals, Integer.parseInt(getResultValue(result)));

                // test permission instance and query cache operation
                result = server0.execute("testPermission = new(\"org.apache.jetspeed.security.spi.impl.PersistentJetspeedPermissionImpl\", \"TEST-PERMISSION-TYPE\", \"TEST-PERMISSION\");");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("testPermission.setActions(\"TEST-PERMISSION-ACTION\");");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("jspm.addPermission(testPermission);");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("jspm.getPermissions().size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server0Permissions+1, Integer.parseInt(getResultValue(result)));
                result = server0.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
                result = server0.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspm.getPermissions().size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server1Permissions+1, Integer.parseInt(getResultValue(result)));
                result = server1.execute("testPermission = jspm.getPermissions(\"TEST-PERMISSION-TYPE\", \"TEST-PERMISSION\").get(0);");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspm.removePermission(testPermission);");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspm.getPermissions().size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server1Permissions, Integer.parseInt(getResultValue(result)));
                result = server0.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
                result = server0.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("jspm.getPermissions().size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server0Permissions, Integer.parseInt(getResultValue(result)));

                // test domain instance and query cache operation
                result = server0.execute("testDomain = new(\"org.apache.jetspeed.security.impl.SecurityDomainImpl\");");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("testDomain.setName(\"TEST-DOMAIN\");");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("jspm.addDomain(testDomain);");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("jspm.getAllDomains().size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server0Domains+1, Integer.parseInt(getResultValue(result)));
                result = server0.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
                result = server0.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspm.getAllDomains().size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server1Domains+1, Integer.parseInt(getResultValue(result)));
                result = server1.execute("testDomain = jspm.getDomainByName(\"TEST-DOMAIN\");");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspm.removeDomain(testDomain);");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspm.getAllDomains().size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server1Domains, Integer.parseInt(getResultValue(result)));
                result = server0.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.commitTransaction();");
                assertTrue(!result.contains("Exception"));
                sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
                result = server0.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("jspmCacheServer.beginTransaction();");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("jspm.getAllDomains().size();");
                assertTrue(!result.contains("Exception"));
                assertEquals(server0Domains, Integer.parseInt(getResultValue(result)));

                // remove default domain if created
                if (createdDefaultDomain)
                {
                    result = server0.execute("defaultDomain = jspm.getDomainByName(\"[default]\");");
                    assertTrue(!result.contains("Exception"));
                    result = server0.execute("jspm.removeDomain(defaultDomain);");
                    assertTrue(!result.contains("Exception"));
                    result = server0.execute("jspmCacheServer.commitTransaction();");
                    assertTrue(!result.contains("Exception"));
                }
            }
            finally
            {
                // rollback transaction
                server0.execute("jspmCacheServer.rollbackTransaction();");
                server1.execute("jspmCacheServer.rollbackTransaction();");
            }
        }
        catch (final Exception e)
        {
            log.error("Server test exception: "+e, e);
            fail( "Server test exception: "+e);
        }
        finally
        {
            // silently shutdown servers
            try
            {
                server0.shutdown(CACHE_NOTIFICATION_STARTUP_WAIT);
            }
            catch (final Exception e)
            {
                log.error( "Server shutdown exception: "+e, e);
            }
            try
            {
                server1.shutdown(CACHE_NOTIFICATION_STARTUP_WAIT);
            }
            catch (final Exception e)
            {
                log.error( "Server shutdown exception: "+e, e);
            }
        }
    }
}
