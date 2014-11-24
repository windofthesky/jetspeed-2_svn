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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.jndi.JetspeedTestJNDIComponent;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.components.test.AbstractJexlSpringTestServer;
import org.apache.jetspeed.util.DirectoryHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PortletApplicationManagerServer
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class PortletApplicationManagerServer extends AbstractJexlSpringTestServer {

    protected static Log log = LogFactory.getLog(PortletApplicationManagerServer.class);
    
    private JetspeedTestJNDIComponent jndiDS;
    private PortletApplicationManagement portletApplicationManager;    
    
    @Override
    public void initialize() throws Exception {
        // setup jetspeed test datasource
        jndiDS = new JetspeedTestJNDIComponent();
        jndiDS.setup();

        // initialize component manager and server
        super.initialize();

        // access portal application manager
        portletApplicationManager = scm.lookupComponent("PAM");
        
        log.info("PortalApplicationManager server initialized");
    }
    
    @Override
    protected String getBeanDefinitionFilterCategories() {
        return "default,jdbcDS,xmlPageManager,security,dbSecurity";
    }

    @Override
    protected String[] getBootConfigurations() {
        return new String[]{"boot/datasource.xml"};
    }

    @Override
    protected String[] getConfigurations() {
        List<String> configurationsList = new ArrayList<String>();
        configurationsList.add("transaction.xml");
        configurationsList.add("cache.xml");
        configurationsList.add("jetspeed-base.xml");
        configurationsList.add("jetspeed-properties.xml");
        configurationsList.add("page-manager.xml");
        configurationsList.add("registry.xml");
        configurationsList.add("JETSPEED-INF/spring/RequestDispatcherService.xml");
        configurationsList.add("rc2.xml");
        configurationsList.add("deployment.xml");
        if (TestPortletApplicationManager.TEST_USE_VERSIONED_PAM)
        {
            configurationsList.add("alternate/versioned-deployment/deployment.xml");
        }
        configurationsList.add("static-bean-references.xml");
        configurationsList.add("security-managers.xml");
        configurationsList.add("security-providers.xml");
        configurationsList.add("security-spi-atn.xml");
        configurationsList.add("security-spi.xml");
        configurationsList.add("security-atn.xml");
        configurationsList.add("security-atz.xml");
        configurationsList.add("JETSPEED-INF/spring/JetspeedPrincipalManagerProviderOverride.xml");
        configurationsList.add("search.xml");
        configurationsList.add("cluster-node.xml");
        return configurationsList.toArray(new String[configurationsList.size()]);
    }

    @Override
    protected Map<String,Object> getContextVars() {
        Map<String,Object> contextVars = new HashMap<String,Object>();
        contextVars.put("portletApplicationManagerServer", this);
        return contextVars;
    }

    @Override
    public void terminate() throws Exception {
        // tear down component manager and server
        super.terminate();

        // tear down jetspeed test datasource
        jndiDS.tearDown();

        log.info("PortalApplicationManager server terminated");
    }
    
    /**
     * Start test portlet application.
     * 
     * @throws RegistryException
     */
    public void startPortletApplication() throws RegistryException {
        if (portletApplicationManager.isStarted()) {
            DirectoryHelper portletApplicationDir = new DirectoryHelper(new File(baseDir+"/src/test/testdata/"+TestPortletApplicationManager.CONTEXT_NAME));
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            portletApplicationManager.startPortletApplication(TestPortletApplicationManager.CONTEXT_NAME, TestPortletApplicationManager.CONTEXT_PATH, portletApplicationDir, contextClassLoader);
        }
    }

    /**
     * Stop test portlet application.
     * 
     * @throws RegistryException
     */
    public void stopPortletApplication() throws RegistryException {
        if (portletApplicationManager.isStarted()) {
            portletApplicationManager.stopPortletApplication(TestPortletApplicationManager.CONTEXT_NAME);
        }
    }
    
    /**
     * Unregister test portlet application.
     * 
     * @throws RegistryException
     */
    public void unregisterPortletApplication() throws RegistryException {
        if (portletApplicationManager.isStarted()) {
            portletApplicationManager.unregisterPortletApplication(TestPortletApplicationManager.CONTEXT_NAME);
        }
    }
    
    /**
     * Server main entry point.
     * 
     * @param args not used
     */
    public static void main(String [] args) {
        Throwable error = (new PortletApplicationManagerServer()).run();
        if (error != null) {
            log.error( "Unexpected exception: "+error, error);
        }
    }
}
