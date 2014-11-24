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
package org.apache.jetspeed.tools.pamanager;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.jndi.JetspeedTestJNDIComponent;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.components.test.AbstractJexlSpringTestCase;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityDomain;
import org.apache.jetspeed.security.impl.SecurityDomainImpl;
import org.apache.jetspeed.security.spi.SecurityDomainAccessManager;
import org.apache.jetspeed.security.spi.SecurityDomainStorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestPortletApplicationManager extends AbstractJexlSpringTestCase
{
    private static final Log log = LogFactory.getLog(TestPortletApplicationManager.class);

    public static final boolean TEST_CONCURRENT_PAM_ACCESS = true;
    public static final boolean TEST_USE_VERSIONED_PAM = false;
    public static final int TEST_PORTLET_APPLICATION_RESTARTS = 5;

    public static final String CONTEXT_NAME = "test-pa";
    public static final String CONTEXT_PATH = "/"+CONTEXT_NAME;

    private static final long TEST_PROCESS_SHUTDOWN_WAIT = 5000;

    protected JetspeedTestJNDIComponent jndiDS;
    private PortletApplicationManagement portletApplicationManager;
    
    /**
     * Configure test methods.
     * 
     * @return test suite.
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortletApplicationManager.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        // setup jetspeed test datasource
        jndiDS = new JetspeedTestJNDIComponent();
        jndiDS.setup();

        // setup scripting and Spring test case
        super.setUp();

        // setup test
        portletApplicationManager = scm.lookupComponent("PAM");
        assertTrue(portletApplicationManager.isStarted());
        Class<?> portletApplicationManagerClass = scm.lookupComponent("org.apache.jetspeed.tools.pamanager.PortletApplicationManager").getClass();
        log.info("PortletApplicationManager class: " + portletApplicationManagerClass.getSimpleName());

        // unregister portlet application
        try {
            portletApplicationManager.unregisterPortletApplication(CONTEXT_NAME);
        } catch (RegistryException re) {
        }

        // create standard default security domain and user role as necessary
        // for portlet application permissions
        SecurityDomainAccessManager domainAccessManager = scm.lookupComponent("org.apache.jetspeed.security.spi.SecurityDomainAccessManager");
        if (domainAccessManager.getDomainByName(SecurityDomain.DEFAULT_NAME) == null) {
            SecurityDomainStorageManager domainStorageManager = scm.lookupComponent("org.apache.jetspeed.security.spi.SecurityDomainStorageManager");
            SecurityDomainImpl defaultSecurityDomain = new SecurityDomainImpl();
            defaultSecurityDomain.setName(SecurityDomain.DEFAULT_NAME);
            domainStorageManager.addDomain(defaultSecurityDomain);
        }
        RoleManager roleManager = scm.lookupComponent("org.apache.jetspeed.security.RoleManager");
        if (!roleManager.roleExists("user")) {
            roleManager.addRole("user");
        }
    }

    @Override
    protected String[] getConfigurations() {
        List<String> confList = new ArrayList<String>();
        confList.add("transaction.xml");
        confList.add("cache.xml");
        confList.add("jetspeed-base.xml");
        confList.add("jetspeed-properties.xml");
        confList.add("page-manager.xml");
        confList.add("registry.xml");
        confList.add("search.xml");
        confList.add("JETSPEED-INF/spring/RequestDispatcherService.xml");
        confList.add("rc2.xml");
        confList.add("static-bean-references.xml");
        confList.add("security-managers.xml");
        confList.add("security-providers.xml");
        confList.add("security-spi.xml");
        confList.add("security-atn.xml");
        confList.add("security-spi-atn.xml");
        confList.add("security-atz.xml");
        confList.add("JETSPEED-INF/spring/JetspeedPrincipalManagerProviderOverride.xml");
        confList.add("deployment.xml");
        if (TEST_USE_VERSIONED_PAM)
        {
            confList.add("alternate/versioned-deployment/deployment.xml");
        }
        confList.add("search.xml");
        confList.add("cluster-node.xml");
        return confList.toArray(new String[1]);
    }

    @Override
    protected String[] getBootConfigurations() {
        return new String[]{"boot/datasource.xml"};
    }

    @Override
    protected String getBeanDefinitionFilterCategories() {
        return "default,jdbcDS,xmlPageManager,security,dbSecurity";
    }

    @Override
    protected Properties getInitProperties() {
        Properties properties = super.getInitProperties();
        properties.setProperty("autodeployment.catalina.base", getBaseDir()+"/target");
        properties.setProperty("autodeployment.catalina.engine", "Catalina");
        properties.setProperty("autodeployment.delay", "10000");
        properties.setProperty("autodeployment.password", "test");
        properties.setProperty("autodeployment.port", "8080");
        properties.setProperty("autodeployment.server", "localhost");
        properties.setProperty("autodeployment.staging.dir", getBaseDir()+"/target");
        properties.setProperty("autodeployment.target.dir", getBaseDir()+"/target");
        properties.setProperty("autodeployment.user", "test");
        return properties;
    }

    @Override
    protected void tearDown() throws Exception {
        // unregister portlet application
        try {
            portletApplicationManager.unregisterPortletApplication(CONTEXT_NAME);
        } catch (RegistryException re) {
        }
        portletApplicationManager = null;

        // tear down test
        super.tearDown();

        // tear down jetspeed test datasource
        jndiDS.tearDown();
    }

    /**
     * Test basic PortletApplicationManager operation.
     */
    public void testPortletApplicationManager() {
        // check for distributed database support
        String databaseName = System.getProperty("org.apache.jetspeed.database.default.name");
        if ((databaseName != null) && databaseName.equals("derby")) {
            System.out.println("Database support not distributed: system limitation... test skipped");
            log.warn("Database support not distributed: system limitation... test skipped");
            return;
        }
        
        // start portlet application manager test servers
        final TestProgram server0 = new TestProgram("server-0", PortletApplicationManagerServer.class, 0);
        final TestProgram server1 = new TestProgram("server-1", PortletApplicationManagerServer.class, 1);
        try {
            // start servers
            server0.start();
            server1.start();

            // wait until servers have started
            server0.execute("");
            server1.execute("");
            
            // test starting and stopping portlet application
            String result;
            for (int i = 0; (i < TEST_PORTLET_APPLICATION_RESTARTS); i++) {
                // start portlet application
                if (TEST_CONCURRENT_PAM_ACCESS) {
                    // start portlet application asynchronously in background threads per server
                    log.info("test concurrent register/start/stop portlet application, iteration "+i+"...");
                    TestExecuteThread startPortletApplication0 = new TestExecuteThread(server0, "portletApplicationManagerServer.startPortletApplication();");
                    TestExecuteThread startPortletApplication1 = new TestExecuteThread(server1, "portletApplicationManagerServer.startPortletApplication();");
                    startPortletApplication0.start();
                    startPortletApplication1.start();
                    result = startPortletApplication0.getResult();
                    assertTrue(!result.contains("Exception"));
                    result = startPortletApplication1.getResult();
                    assertTrue(!result.contains("Exception"));
                } else {
                    // stop portlet application synchronously
                    log.info("test serial register/start/stop portlet application, iteration "+i+"...");
                    result = server0.execute("portletApplicationManagerServer.startPortletApplication();");
                    assertTrue(!result.contains("Exception"));
                    result = server1.execute("portletApplicationManagerServer.startPortletApplication();");
                    assertTrue(!result.contains("Exception"));
                }

                // stop portlet application synchronously
                result = server1.execute("portletApplicationManagerServer.stopPortletApplication();");
                assertTrue(!result.contains("Exception"));
                result = server0.execute("portletApplicationManagerServer.stopPortletApplication();");
                assertTrue(!result.contains("Exception"));

                // unregister portlet application
                log.info("test unregister portlet application, iteration "+i+"...");
                try {
                    portletApplicationManager.unregisterPortletApplication(CONTEXT_NAME);
                } catch (RegistryException re) {
                }
            }
        } catch (final Exception e) {
            log.error("Server test exception: "+e, e);
            fail("Server test exception: "+e);            
        } finally {
            // silently shutdown servers
            try {
                server0.shutdown(TEST_PROCESS_SHUTDOWN_WAIT);
            } catch (final Exception e) {
                log.error( "Server shutdown exception: "+e, e);
            }
            try {
                server1.shutdown(TEST_PROCESS_SHUTDOWN_WAIT);
            } catch (final Exception e) {
                log.error( "Server shutdown exception: "+e, e);
            }
        }
    }
    
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[]) {
        TestRunner.main(new String[] {TestPortletApplicationManager.class.getName()});
    }
}
