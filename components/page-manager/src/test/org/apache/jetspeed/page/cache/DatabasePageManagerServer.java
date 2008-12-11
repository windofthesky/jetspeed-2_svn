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
package org.apache.jetspeed.page.cache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.jexl.Script;
import org.apache.commons.jexl.ScriptFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.jndi.JetspeedTestJNDIComponent;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.page.PageManager;

/**
 * DatabasePageManagerServer
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class DatabasePageManagerServer
{
    protected static Log log = LogFactory.getLog(DatabasePageManagerServer.class);
    
    // Constants
    
    public static final String SCRIPT_RESULT_LINE_PREFIX = "> ";
    
    // Members
    
    private JetspeedTestJNDIComponent jndiDS;
    protected ClassPathXmlApplicationContext ctx;
    private String baseDir;
    private PageManager pageManager;
    private JexlContext jexlContext;
    private boolean exit;
    
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
        final String [] bootConfigurations = new String[]{"boot/datasource.xml"};
        final String [] configurations = new String[]{"database-page-manager.xml", "transaction.xml"};
        baseDir = System.getProperty("basedir");
        if ((baseDir == null) || (baseDir.length() == 0))
        {
            baseDir = System.getProperty("user.dir");
        }
        final String appRoot = baseDir+"/target/test-classes/webapp";
        final ApplicationContext bootContext = new ClassPathXmlApplicationContext(bootConfigurations, true);
        ctx = new ClassPathXmlApplicationContext(configurations, false, bootContext);
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties p = new Properties();
        p.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, appRoot);
        ppc.setProperties(p);
        ctx.addBeanFactoryPostProcessor(ppc);
        ctx.refresh();

        // access page manager
        pageManager = (PageManager)ctx.getBean("pageManager");
        
        // craete jexl context
        jexlContext = JexlHelper.createContext();
        jexlContext.getVars().put("pageManager", pageManager);
        jexlContext.getVars().put("pageManagerServer", this);
        
        log.info( "DatabasePageManager server initialized");
    }
    
    /**
     * Terminate page manager server instance.
     * 
     * @throws Exception
     */
    public void terminate() throws Exception
    {
        // tear down jetspeed component manager and test datasource
        ctx.close();
        jndiDS.tearDown();

        log.info( "DatabasePageManager server terminated");
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
            final DatabasePageManagerServer server = new DatabasePageManagerServer();
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
