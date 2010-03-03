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
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.jexl.Script;
import org.apache.commons.jexl.ScriptFactory;
import org.apache.jetspeed.components.JetspeedBeanDefinitionFilter;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.jndi.JetspeedTestJNDIComponent;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageManagerTestShared;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.PrincipalsSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DatabasePageManagerServer
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class DatabasePageManagerServer
{
    protected static Logger log = LoggerFactory.getLogger(DatabasePageManagerServer.class);
    
    // Constants
    
    public static final String SCRIPT_RESULT_LINE_PREFIX = "> ";
    
    // Members
    
    private JetspeedTestJNDIComponent jndiDS;
    private String baseDir;
    private SpringComponentManager scm;
    private PageManager pageManager;
    private JexlContext jexlContext;
    private boolean exit;
    private String user;
    private String groups;
    private String roles;
    private Subject userSubject;
    
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
        final JetspeedBeanDefinitionFilter beanDefinitionFilter = new JetspeedBeanDefinitionFilter("default,jdbcDS");
        final String [] bootConfigurations = new String[]{"boot/datasource.xml"};
        final String [] configurations = new String[]{"database-page-manager.xml", "transaction.xml"};
        baseDir = System.getProperty("basedir");
        if ((baseDir == null) || (baseDir.length() == 0))
        {
            baseDir = System.getProperty("user.dir");
        }
        final String appRoot = baseDir+"/target/test-classes/webapp";
        scm = new SpringComponentManager(beanDefinitionFilter, bootConfigurations, configurations, appRoot, false);
        scm.start();

        // access page manager
        pageManager = (PageManager)scm.getComponent("pageManager");
        
        // create jexl context
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
        // shutdown page manager
        pageManager.shutdown();

        // tear down jetspeed component manager and test datasource
        scm.stop();
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
e.printStackTrace(System.out);
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
     * Get server exit flag.
     * 
     * @return server exit flag
     */
    public boolean isExit()
    {
        return exit;
    }
    
    /**
     * Get user principal name.
     * 
     * @return user principal name
     */
    public String getUser()
    {
        return user;
    }
    
    /**
     * Set user principal name.
     * 
     * @param user user principal name
     */
    public void setUser(String user)
    {
        this.user = user;
        this.userSubject = null;
    }
    
    /**
     * Get user group principal names.
     * 
     * @return CSV list of group principal names
     */
    public String getGroups()
    {
        return groups;
    }

    /**
     * Set user group principal names.
     * 
     * @param groups CSV list of group principal names
     */
    public void setGroups(String groups)
    {
        this.groups = groups;
        this.userSubject = null;
    }

    /**
     * Get user role principal names.
     * 
     * @return CSV list of role principal names
     */
    public String getRoles()
    {
        return roles;
    }

    /**
     * Set user role principal names.
     * 
     * @param roles CSV list of role principal names
     */
    public void setRoles(String roles)
    {
        this.roles = roles;
        this.userSubject = null;
    }

    /**
     * Get or create and cache user subject.
     * 
     * @return user subject
     */
    public Subject getUserSubject()
    {
        if ((userSubject == null) && (user != null))
        {
            Set userPrincipals = new PrincipalsSet();
            userPrincipals.add(new PageManagerTestShared.TestUser(user));
            if (groups != null)
            {
                String [] groupsArray = groups.split(",");
                for (int i = 0; (i < groupsArray.length); i++)
                {
                    userPrincipals.add(new PageManagerTestShared.TestGroup(groupsArray[i].trim()));                                    
                }
            }
            if (roles != null)
            {
                String [] rolesArray = roles.split(",");
                for (int i = 0; (i < rolesArray.length); i++)
                {
                    userPrincipals.add(new PageManagerTestShared.TestRole(rolesArray[i].trim()));                                    
                }
            }
            userSubject = new Subject(true, userPrincipals, new HashSet(), new HashSet());            
        }
        return userSubject;
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
                        // get user and execute script
                        Subject userSubject = server.getUserSubject();
                        if (userSubject != null)
                        {                            
                            // execute script as user
                            final String executeScriptLine = scriptLine;
                            final String [] executeResultLine = new String[]{null};
                            Exception executeException = (Exception)JSSubject.doAsPrivileged(userSubject, new PrivilegedAction()
                            {
                                public Object run()
                                {
                                    try
                                    {
                                        executeResultLine[0] = server.execute(executeScriptLine);
                                        return null;
                                    }
                                    catch (Exception e)
                                    {
                                        return e;
                                    }
                                    finally
                                    {
                                        JSSubject.clearSubject();
                                    }
                                }
                            }, null);
                            if (executeException != null)
                            {
                                throw executeException;
                            }
                            resultLine = executeResultLine[0];
                        }
                        else
                        {
                            // execute script anonymously
                            resultLine = server.execute(scriptLine);
                        }
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
