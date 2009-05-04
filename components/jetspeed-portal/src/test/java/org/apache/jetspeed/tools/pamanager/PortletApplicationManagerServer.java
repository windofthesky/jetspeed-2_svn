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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.jexl.Script;
import org.apache.commons.jexl.ScriptFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.JetspeedBeanDefinitionFilter;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.jndi.JetspeedTestJNDIComponent;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.util.DirectoryHelper;

/**
 * PortletApplicationManagerServer
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class PortletApplicationManagerServer
{
    protected static Log log = LogFactory.getLog(PortletApplicationManagerServer.class);
    
    // Constants
    
    public static final String SCRIPT_RESULT_LINE_PREFIX = "> ";
    
    // Members
    
    private JetspeedTestJNDIComponent jndiDS;
    private String baseDir;
    private SpringComponentManager scm;
    private JexlContext jexlContext;
    private boolean exit;
    private PortletApplicationManagement portletApplicationManager;    
    
    // Life cycle
    
    /**
     * Initialize page manager server instance and script context.
     * 
     * @throws Exception
     */
    public void initialize() throws Exception
    {
        // setup jetspeed test datasource and component manager
        jndiDS = new JetspeedTestJNDIComponent();
        jndiDS.setup();
        final JetspeedBeanDefinitionFilter beanDefinitionFilter = new JetspeedBeanDefinitionFilter("default,jdbcDS,xmlPageManager,security,dbSecurity");
        final String [] bootConfigurations = new String[]{"boot/datasource.xml"};
        final List<String> configurationsList = new ArrayList<String>();
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
        final String[] configurations = configurationsList.toArray(new String[configurationsList.size()]);
        baseDir = System.getProperty("basedir");
        if ((baseDir == null) || (baseDir.length() == 0))
        {
            baseDir = System.getProperty("user.dir");
        }
        final String appRoot = baseDir+"/target/test-classes/webapp";
        scm = new SpringComponentManager(beanDefinitionFilter, bootConfigurations, configurations, appRoot, false);
        scm.start();

        // access portal application manager
        portletApplicationManager = (PortletApplicationManagement)scm.getComponent("PAM");
        
        // create jexl context
        jexlContext = JexlHelper.createContext();
        jexlContext.getVars().put("portletApplicationManagerServer", this);
        
        log.info( "PortalApplicationManager server initialized");
    }
    
    /**
     * Terminate page manager server instance.
     * 
     * @throws Exception
     */
    public void terminate() throws Exception
    {
        // tear down jetspeed component manager and test datasource
        scm.stop();
        jndiDS.tearDown();

        log.info( "PortalApplicationManager server terminated");
    }
    
    // Implementation
    
    /**
     * Execute a single line script against page manager server context.
     * 
     * @param scriptLine jexl script
     * @return script result line
     */
    public String execute(final String scriptLine)
    {
        // execute script line and return result line
        String resultLine = scriptLine;
        try
        {
            final Script jexlScript = ScriptFactory.createScript(scriptLine);
            final Object result = jexlScript.execute(jexlContext);
            if (result != null)
            {
                resultLine += " -> "+result;
            }
        }
        catch (final Exception e)
        {
            resultLine += " -> "+e;            
        }
        return resultLine;
    }

    /**
     * Start test portlet application.
     * 
     * @throws RegistryException
     */
    public void startPortletApplication() throws RegistryException
    {
        DirectoryHelper portletApplicationDir = new DirectoryHelper(new File(baseDir+"/src/test/testdata/"+TestPortletApplicationManager.CONTEXT_NAME));
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        portletApplicationManager.startPortletApplication(TestPortletApplicationManager.CONTEXT_NAME, TestPortletApplicationManager.CONTEXT_PATH, portletApplicationDir, contextClassLoader);
    }

    /**
     * Stop test portlet application.
     * 
     * @throws RegistryException
     */
    public void stopPortletApplication() throws RegistryException
    {
        portletApplicationManager.stopPortletApplication(TestPortletApplicationManager.CONTEXT_NAME);
    }
    
    /**
     * Unregister test portlet application.
     * 
     * @throws RegistryException
     */
    public void unregisterPortletApplication() throws RegistryException
    {
        portletApplicationManager.unregisterPortletApplication(TestPortletApplicationManager.CONTEXT_NAME);
    }
    
    /**
     * Sets server exit flag.
     */
    public void exit()
    {
        exit = true;
    }
    
    // Data access
    
    /**
     * @return server exit flag
     */
    public boolean isExit()
    {
        return exit;
    }
    
    // Application entry point
    
    /**
     * Server main entry point.
     * 
     * @param args not used
     */
    public static void main(final String [] args)
    {
        try
        {
            // create and initialize server
            final PortletApplicationManagerServer server = new PortletApplicationManagerServer();
            server.initialize();
            
            // simple server reads script lines from standard
            // input and writes results on standard output
            final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            final PrintWriter out = new PrintWriter(System.out, true);
            do
            {
                // read single line scripts to execute
                String scriptLine = in.readLine();
                if (scriptLine != null)
                {
                    scriptLine = scriptLine.trim();
                    String resultLine = "";
                    if (scriptLine.length() > 0)
                    {
                        // execute script
                        resultLine = server.execute(scriptLine);
                    }

                    // write prefixed single line results
                    out.println(SCRIPT_RESULT_LINE_PREFIX+resultLine);
                }
                else
                {
                    // exit server on input EOF
                    server.exit();
                }
            }
            while (!server.isExit());
            
            // terminate server
            server.terminate();
        }
        catch (final Throwable t)
        {
            log.error( "Unexpected exception: "+t, t);
        }
    }
}
