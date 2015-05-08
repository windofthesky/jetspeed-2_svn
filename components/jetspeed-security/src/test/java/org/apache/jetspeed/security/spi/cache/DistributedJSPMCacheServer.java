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

import org.apache.jetspeed.components.jndi.JetspeedTestJNDIComponent;
import org.apache.jetspeed.components.test.AbstractJexlSpringTestServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ojb.PersistenceBrokerTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * DistributedJSPMCacheServer - test JetspeedSecurityPersistenceManager cache
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class DistributedJSPMCacheServer extends AbstractJexlSpringTestServer
{
    protected static Logger log = LoggerFactory.getLogger(DistributedJSPMCacheServer.class);

    private JetspeedTestJNDIComponent jndiDS;
    private PersistenceBrokerTransactionManager txnManager;
    private TransactionStatus txn;

    @Override
    public void initialize() throws Exception
    {
        // setup jetspeed test datasource
        jndiDS = new JetspeedTestJNDIComponent();
        jndiDS.setup();

        // initialize component manager and server
        super.initialize();

        // lookup components
        txnManager = scm.lookupComponent("transactionManager");

        log.info("DistributedJSPMCache server initialized");
    }

    @Override
    protected String getBeanDefinitionFilterCategories()
    {
        return "security,dbSecurity,transaction,cache,jdbcDS";
    }

    @Override
    protected String[] getBootConfigurations()
    {
        return new String[]{"boot/datasource.xml"};
    }

    @Override
    protected String[] getConfigurations()
    {
        // note: override the JetspeedPrincipalManagerProvider bean to get rid of
        // dependency on SSO
        return new String[]{"security-atn.xml", "security-atz.xml", "security-managers.xml", "security-providers.xml",
                "security-spi.xml", "security-spi-atn.xml", "transaction.xml", "cache-test.xml",
                "static-bean-references.xml", "JETSPEED-INF/spring/JetspeedPrincipalManagerProviderOverride.xml",
                "JETSPEED-INF/spring/TestRegistryStubs.xml"};
    }

    @Override
    protected Map<String,Object> getContextVars()
    {
        Map<String,Object> contextVars = new HashMap<String,Object>();
        contextVars.put("jspm", scm.lookupComponent("org.apache.jetspeed.security.spi.impl.JetspeedSecurityPersistenceManager"));
        contextVars.put("userType", scm.lookupComponent("org.apache.jetspeed.security.JetspeedPrincipalType.user"));
        contextVars.put("roleType", scm.lookupComponent("org.apache.jetspeed.security.JetspeedPrincipalType.role"));
        contextVars.put("groupType", scm.lookupComponent("org.apache.jetspeed.security.JetspeedPrincipalType.group"));
        contextVars.put("jspmCache", scm.lookupComponent("org.apache.jetspeed.security.spi.impl.cache.JSPMCache"));
        contextVars.put("jspmCacheServer", this);
        return contextVars;
    }

    @Override
    public void terminate() throws Exception
    {
        // terminate component manager and server
        super.terminate();

        // tear down test datasource
        jndiDS.tearDown();

        log.info("DistributedJSPMCache server terminated");
    }

    /**
     * Begin transaction on current thread.
     */
    public void beginTransaction()
    {
        txn = txnManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));
    }

    /**
     * Commit transaction on current thread.
     */
    public void commitTransaction()
    {
        if (txn != null)
        {
            txnManager.commit(txn);
            txn = null;
        }
    }

    /**
     * Rollback transaction on current thread.
     */
    public void rollbackTransaction()
    {
        try
        {
            if (txn != null)
            {
                txnManager.rollback(txn);
                txn = null;
            }
        }
        catch (Exception e)
        {
            txn = null;
        }
    }

    /**
     * Server main entry point.
     *
     * @param args not used
     */
    public static void main(String [] args)
    {
        Throwable error = (new DistributedJSPMCacheServer()).run();
        if (error != null)
        {
            log.error( "Unexpected exception: "+error, error);
        }
    }
}
