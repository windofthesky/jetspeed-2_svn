/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.exception.JetspeedException;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * PortletApplicationManager
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
/**
 * This is the interface that defines the Lifecycle-related methods to control
 * Portlet Applications.
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
  * @version $Id$
 */

public class PortletApplicationManager implements JetspeedEngineConstants
{
    private static final Log log = LogFactory.getLog("deployment");
    
    /**
     * Command line utility to deploy a portlet application to an application server.
     * The command line has the following options:
     *
     * PortletApplicationManager
     * -DWebappDir={Webapps directory of application server}
     * -DWarFileName={Name of the WAR file to deploy}
     * -Daction={deploy|register|undeploy|unregister|start|stop|reload}
     * -DPortletAppName= Name of the Portlet application
     * -DApplicationServer={Catalina}
     * -DApplicationType={webapp|local}
     *    (default webapp)
     * -DServer={host name of the target server}
     *   (optional: required for if deploying to Catalina)
     * -DServerPort={port of the target server. Default: localhost}
     *   (optional: required for if deploying to Catalina. Default: 8080)
     * -DUserName={User name to access the servers management system}
     *   (optional: required for if deploying to Catalina)
     * -DPassword={Password to access the servers management system}
     *   (optional: required for if deploying to Catalina)
     * -DImpl=(full class name i.e. org.apache.jetspeed.tools.pamanager.FileSystemPAM)
     *
     *Notes: The deploy action requires the WarFileName. If no ApplicationServer
     *       is defined it requires in additionthe WebappDir.
     *       All other actions require the PortletAppName. If the ApplicationServer
     *       is not defined it will use catalina as default.
    
     */

    public static void main(String args[])
    {
        String arg;
        Engine engine = null;
        int i = 0;        

        // Read the command line
        String strWebAppDir = "";
        String strAction = "";
        String strWarFileName = "";
        String strPortletAppName = "";
        String strPortalName = "jetspeed";
        String applicationType = "webapp";
		String strUserName = "";
		String strPassword = "";
		String strServer = "localhost";
        String className = "org.apache.jetspeed.tools.pamanager.FileSystemPAM"; // default
        Deployment deployer = null;
        Registration registrator = null;
        Lifecycle lifecycle = null;
        
		int intServerPort = 8080;
        String jetspeedPropertiesFile = System.getProperty("pam.jetspeed.properties", "/WEB-INF/conf/jetspeed.properties");
        String appsDirectory = System.getProperty("pam.apps.directory", "/WEB-INF/apps/");
        
        while (i < args.length && args[i].startsWith("-"))
        {
            arg = args[i++];

            // use this type of check for arguments that require arguments
            if (arg.equalsIgnoreCase("-PortletAppName"))
            {
                if (i < args.length)
                    strPortletAppName = args[i++];
            }
            else if (arg.equalsIgnoreCase("-Impl"))
            {
                if (i < args.length)
                    className = args[i++];
            }            
            else if (arg.equalsIgnoreCase("-Action"))
            {
                if (i < args.length)
                    strAction = args[i++];
            }
            else if (arg.equalsIgnoreCase("-WebAppDir"))
            {
                if (i < args.length)
                    strWebAppDir = args[i++];
            }
            else if (arg.equalsIgnoreCase("-WarFileName"))
            {
                if (i < args.length)
                    strWarFileName = args[i++];
            }
            else if (arg.equalsIgnoreCase("-PortalName"))
            {
                if (i < args.length)
                    strPortalName = args[i++];
            }
            else if (arg.equalsIgnoreCase("-UserName"))
            {
                if (i < args.length)
                    strUserName = args[i++];
            }
			else if (arg.equalsIgnoreCase("-Password"))
			{
				if (i < args.length)
					strPassword = args[i++];
			}
			else if (arg.equalsIgnoreCase("-Server"))
			{
				if (i < args.length)
					strServer= args[i++];
			}
            else if (arg.equalsIgnoreCase("-ServerPort"))
            {
                if (i < args.length)
                    intServerPort = Integer.parseInt(args[i++]);
            }
            else if (arg.equalsIgnoreCase("-ApplicationType"))
            {
                if (i < args.length)
                {
                    applicationType = args[i++];
                }
            }
            else if (arg.equalsIgnoreCase("-h"))
            {
                helpScreen();
                return;
            }
            else if (arg.equalsIgnoreCase("-?"))
            {
                helpScreen();
                return;
            }
        }
        
        // Portlet Application Name and action are required by all functions.
        // Make sure that the values were defined from the command line.
        if (strPortletAppName.length() == 0 || strAction.length() == 0)
        {
            System.out.println(
                "\nPortlet Application Name and/or action are not defined."
                    + "Please use '-PortletAppName appName' and/or '-Action deploy' from the command line\n");
            helpScreen();
            log.error("PAM Error: Invalid parameter(s) passed, cannot process PAM request.");
            if (!log.isInfoEnabled())
            {
                logRequest(args, true);
            }            
            return;
        }

        String strAppRoot = strWebAppDir + strPortalName;

        try
        {
            // Start the registry service -- it's needed by many actions
            Configuration properties =
                (Configuration) new PropertiesConfiguration(strAppRoot + jetspeedPropertiesFile);

            properties.setProperty(APPLICATION_ROOT_KEY, strAppRoot);

            // Override the properties with PAM specifice settings
            overrideProperties(strAppRoot, properties);

            engine = Jetspeed.createEngine(properties, strAppRoot, null);            
        }
        catch (Exception e)
        {
            String msg = "PAM Error: Failed to create the Jetspeed Engine. Error: ";
            System.out.println(msg + e.getMessage());
            log.error(msg, e);
            shutdownAndExit(engine);
        }

        logRequest(args, false);
        
        try
        {
            System.out.println("Ready to run PAM implementation: " + className);
            System.out.print("Supporting interfaces: Deployment");
            Class clas = Class.forName(className);
            deployer = (Deployment)clas.newInstance(); 
            if (deployer instanceof Registration)
            {                
                System.out.print(", Registration");
                registrator = (Registration)deployer;
            }
            if (deployer instanceof Lifecycle)
            {
                System.out.print(", Lifecycle");
                lifecycle = (Lifecycle)deployer;
            }            
            System.out.println();
        }
        catch (Exception e)
        {
            String msg = "PAM Error: Failed to create PAM implementation class object: " + className + " Error: ";
            System.out.println(msg  + e.getMessage());
            log.error(msg, e);
            shutdownAndExit(engine);                        
        }
        
        try
        {
            // Invoke the functions
            if (strAction.compareToIgnoreCase("deploy") == 0)
            {
                // Make sure that the WarFileName and the ApplicationServer are defined
                if (strWarFileName.length() == 0)
                {
                    System.out.println(
                        "\nDeploy action requires the war file name. Use '-WarFileName file.war' to specify the file name");
                    log.error("PAM Error: Web application (WAR) file name not specified.");
                    shutdownAndExit(engine);                
                }
                else
                {
                    if (applicationType.equals("local"))
                    {
                        String portletAppRoot = strAppRoot + appsDirectory;
                        deploy(deployer, portletAppRoot, strWarFileName, strPortletAppName); // [RUN]
                    }
                    else
                    {
                        // Requires WebAppDir
                        if (strWebAppDir.length() == 0)
                        {
                            System.out.println(
                                "\nDeploy action requires the definition of the ApplicationServer or the Web application directory.");
                            log.error("PAM Deploy Error: Web application (WAR) directory name not specified.");
                            shutdownAndExit(engine);                        
                        }
    
                        // File deploy uses Directory and warfile
                        deploy(deployer, strWebAppDir, strWarFileName, strPortletAppName); // [RUN]
                    }
                }
            }
            else if (strAction.compareToIgnoreCase("register") == 0)
            {
                // Requires WebAppDir
                if (strWebAppDir.length() == 0)
                {
                    System.out.println("\nRegister action requires the definition of the Web application directory.");
                    log.error("PAM Register Error: Web application (WAR) directory name not specified.");                        
                    shutdownAndExit(engine);
                }
                if (null == registrator)
                {
                    String msg = "PAM Register Error: Registration interface not supported by implementation: " + className;            
                    System.out.println("\n" + msg);
                    log.error(msg);
                    shutdownAndExit(engine);                                
                }
    
                register(registrator, strWebAppDir, strPortletAppName); // [RUN]
            }
            else if (strAction.compareToIgnoreCase("unregister") == 0)
            {          
                if (null == registrator)
                {
                    String msg = "PAM Register Error: Registration interface not supported by implementation: " + className;            
                    System.out.println("\n" + msg);
                    log.error(msg);  
                    shutdownAndExit(engine);                
                }
                
                // Application server can be null -- using Catalina as default
                unregister(registrator, strWebAppDir, strPortletAppName); // [RUN]
            }
            else if (strAction.compareToIgnoreCase("undeploy") == 0)
            {
                if (null == lifecycle)
                {
                    String msg = "PAM Lifecycle Error: Lifecycle interface not supported by implementation: " + className;            
                    System.out.println("\n" + msg);
                    log.error(msg);
                    shutdownAndExit(engine);                                
                }
                
                undeploy(deployer, strWebAppDir, strPortletAppName, strServer, intServerPort, strUserName, strPassword); // [RUN]
            }
            else if (strAction.compareToIgnoreCase("start") == 0)
            {
                if (null == lifecycle)
                {
                    String msg = "PAM Lifecycle Error: Lifecycle interface not supported by implementation: " + className;            
                    System.out.println("\n" + msg);
                    log.error(msg);
                    shutdownAndExit(engine);                                
                }
    
                start(lifecycle, strPortletAppName, strServer, intServerPort, strUserName, strPassword); // [RUN]
            }
            else if (strAction.compareToIgnoreCase("stop") == 0)
            {
                stop(lifecycle, strPortletAppName, strServer, intServerPort, strUserName, strPassword); // [RUN]
            }
            else if (strAction.compareToIgnoreCase("reload") == 0)
            {
                if (null == lifecycle)
                {
                    String msg = "PAM Lifecycle Error: Lifecycle interface not supported by implementation: " + className;            
                    System.out.println("\n" + msg);
                    log.error(msg);
                    shutdownAndExit(engine);                                
                }
                // Application server can be null -- using Catalina as default
                reload(lifecycle, strPortletAppName, strServer, intServerPort, strUserName, strPassword); // [RUN]
            }
            else
            {
                System.out.println("\nAction: " + strAction + " not recognized by the PortletApplicationManager.");
                helpScreen();
            }
        }
        catch (Exception e)
        {
            String msg = "PAM Error: Failed during execution of " + strAction + ", error = " + e.getMessage();
            System.out.println(msg);
            log.error(msg);                        
            shutdownAndExit(engine);                    
        }
        
        try
        {
            if (engine != null)
            {
                engine.shutdown();
            }
        }
        catch (JetspeedException e1)
        {
            System.out.println("Failed shutting down the engine. Error: " + e1.getMessage());
            log.error("PAM Error: Failed shutting down the engine.", e1);                        
            System.exit(0);            
        }
        
        String msg = "PAM: completed operation " + strAction;
        System.out.println(msg);
        log.info(msg);
        System.exit(0);
    }

    public static void shutdownAndExit(Engine engine)
    {
        try
        {
            if (engine != null)
            {
                engine.shutdown();
            }
        }
        catch (JetspeedException e1)
        {
            System.out.println("Failed shutting down the engine. Error: " + e1.getMessage());
            log.error("PAM Error: Failed shutting down the engine.", e1);                        
        }
        
        System.exit(0);                    
    }
    
    public static void helpScreen()
    {
        System.out.println("\nPortletApplicationManager [options]\n");
        System.out.println("\t-action\t\t\t{deploy|undeploy|start|stop|reload}\n");
        System.out.println("\t-PortletAppName\t\t{AppName}\n");
        System.out.println("\t-WebAppDir\t\t{Path to target WebApp directory}\n");
        System.out.println("\t-WarFileName\t\t{Path to war file to deploy}\n");
        System.out.println("\t-Impl\t\t{class name of implementation}\n");
        System.out.println("\t-ApplicationType\t{webapp|local}\n");

        System.out.println("\nNotes:");
        System.out.println("-Each command requires at least the action and the PortletAppName options.");
        System.out.println("-File system deploy requires the WebAppDir and War file option");
        System.out.println("-Deploy and undeploy actions require the WarFileName");

        System.out.println(
            "\nExample: PortletApplicationManager -action deploy -PortletAppName DemoApp -ApplicationServer Catalina\n");

    }

    // Implementaion of the API's

    /**
     * Registers the already deployed WAR into the Portal registry
     *
     * @param registrator PAM implementation supporting Registration interface
     * @param webApplicationName The webapps directory or name inside the Application Server
     * @param portletApplicationName The Portlet Application name
     * @throws PortletApplicationException
     */

    public static void register(Registration registrator,
                                String webApplicationName, 
                                String portletApplicationName)
    throws PortletApplicationException
    {
        System.out.println("Registering Web Application [" + webApplicationName + "] to Portlet Application [" + portletApplicationName + "]...");
        registrator.register(webApplicationName, portletApplicationName);
        System.out.println("...PAM Register done");        
    }

    /**
     * Unregisterd a deployed portlet application
     *
     * @param registrator PAM implementation supporting Registration interface
     * @param webApplicationName The webapps directory or name inside the Application Server
     * @param portletApplicationName The Portlet Application name
     */

    public static void unregister(Registration registrator, 
                                  String webApplicationName, 
                                  String portletApplicationName)
    throws PortletApplicationException    
    {
        System.out.println("Unregistering Portlet Application [" + portletApplicationName + "...");
        registrator.unregister(webApplicationName, portletApplicationName);
        System.out.println("...PAM Unregister done");        
    }

    /**
     * Deploys the specified war file to the webapps dirctory specified.
     *
     * @param webApplicationName The webapps directory or name inside the Application Server
     * @param warFile The warFile containing the Portlet Application
     * @param portletApplicationName The Portlet Application name
     * @throws PortletApplicationException
     */

    public static void deploy(Deployment deployer,
                              String webApplicationName, 
                              String warFile,
                              String portletApplicationName)
    throws PortletApplicationException        
    {
        System.out.println("Deploying Web Application [" + webApplicationName + "] to Portlet Application [" + portletApplicationName + "]...");
        deployer.deploy(webApplicationName, warFile, portletApplicationName);
        System.out.println("...PAM Deploy done");        
    }

    /**
     * Prepares the specified war file for deployment.
     *
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */    
    public static void undeploy(Deployment deployer,
                                String webApplicationName, 
                                String portletApplicationName, 
                                String host, 
                                int port, 
                                String user, 
                                String password)
    throws PortletApplicationException    
    {
        Map map = new HashMap();        
        if (deployer instanceof CatalinaPAM)
        {
            map.put(CatalinaPAM.PAM_PROPERTY_SERVER, host);
            map.put(CatalinaPAM.PAM_PROPERTY_PORT, new Integer(port));
            map.put(CatalinaPAM.PAM_PROPERTY_USER, user);
            map.put(CatalinaPAM.PAM_PROPERTY_PASSWORD, password);            
        }
        System.out.println("Un-deploying Web Application [" + webApplicationName + "], Portlet Application [" + portletApplicationName + "]...");
        deployer.connect(map);
        deployer.undeploy(webApplicationName, portletApplicationName);
        System.out.println("...PAM Undeploy done");                                
    }

    /**
     * Starts the specified Portlet Application on the Application Server
     * 
     * @param lifecycle
     * @param portletApplicationName
     * @param host
     * @param port
     * @param user
     * @param password
     * @throws PortletApplicationException
     */
    public static void start(Lifecycle lifecycle,
                             String portletApplicationName, 
                             String host, 
                             int port, 
                             String user,
                             String password)
    throws PortletApplicationException
    {
        Map map = new HashMap();        
        if (lifecycle instanceof CatalinaPAM)
        {
            map.put(CatalinaPAM.PAM_PROPERTY_SERVER, host);
            map.put(CatalinaPAM.PAM_PROPERTY_PORT, new Integer(port));
            map.put(CatalinaPAM.PAM_PROPERTY_USER, user);
            map.put(CatalinaPAM.PAM_PROPERTY_PASSWORD, password);            
        }
        System.out.println("Starting Portlet Application [" + portletApplicationName + "...");
        if (lifecycle instanceof Deployment)
        {
            ((Deployment)lifecycle).connect(map);
        }
        lifecycle.start(portletApplicationName);
        System.out.println("...PAM Start done");                        
    }

    /**
     * Stops a portlet application from running on the Application Server
     *
     * @param lifecycle
     * @param portletApplicationName
     * @param appServer
     * @param host
     * @param port
     * @param user
     * @param password
     */    
    public static void stop(Lifecycle lifecycle, 
                            String portletApplicationName, 
                            String host, 
                            int port, 
                            String user, 
                            String password)
    throws PortletApplicationException        
    {
        Map map = new HashMap();        
        if (lifecycle instanceof CatalinaPAM)
        {
            map.put(CatalinaPAM.PAM_PROPERTY_SERVER, host);
            map.put(CatalinaPAM.PAM_PROPERTY_PORT, new Integer(port));
            map.put(CatalinaPAM.PAM_PROPERTY_USER, user);
            map.put(CatalinaPAM.PAM_PROPERTY_PASSWORD, password);            
        }
        System.out.println("Stopping Portlet Application [" + portletApplicationName + "...");
        if (lifecycle instanceof Deployment)
        {
            ((Deployment)lifecycle).connect(map);
        }
        lifecycle.stop(portletApplicationName);
        System.out.println("...PAM Stop done");                
    }

    /**
     * Reloads a portlet application.
     * 
     * @param lifecycle
     * @param portletApplicationName
     * @param appServer
     * @param host
     * @param port
     * @param user
     * @param password
     * @throws PortletApplicationException
     */    
    public static void reload(Lifecycle lifecycle, 
                              String portletApplicationName, 
                              String host, 
                              int port, 
                              String user, 
                              String password)
    throws PortletApplicationException    
    {        
        Map map = new HashMap();        
        if (lifecycle instanceof CatalinaPAM)
        {
            map.put(CatalinaPAM.PAM_PROPERTY_SERVER, host);
            map.put(CatalinaPAM.PAM_PROPERTY_PORT, new Integer(port));
            map.put(CatalinaPAM.PAM_PROPERTY_USER, user);
            map.put(CatalinaPAM.PAM_PROPERTY_PASSWORD, password);            
        }
        System.out.println("Reloading Portlet Application [" + portletApplicationName + "...");
        if (lifecycle instanceof Deployment)
        {
            ((Deployment)lifecycle).connect(map);
        }
        lifecycle.reload(portletApplicationName);
        System.out.println("...PSM Reload done");        
    }

    /*
      * Method to override jetspeed properties.
      * @param properties The base configuration properties for the Jetspeed system.
      */
    public static void overrideProperties(String strApplicationRoot, Configuration properties) throws IOException
    {
        String pamPropertiesFile = System.getProperty("pam.properties", "/WEB-INF/conf/pam.properties");
        
        String testPropsPath = strApplicationRoot + pamPropertiesFile;
        File testFile = new File(testPropsPath);
        if (testFile.exists())
        {
            FileInputStream is = new FileInputStream(testPropsPath);
            Properties props = new Properties();
            props.load(is);

            Iterator it = props.entrySet().iterator();
            while (it.hasNext())
            {
                Entry entry = (Entry) it.next();
                //if (entry.getValue() != null && ((String)entry.getValue()).length() > 0)
                properties.setProperty((String) entry.getKey(), (String) entry.getValue());
            }
        }
    }
    
    public static void logRequest(String[] args, boolean logAsError)
    {
        String startMsg = "Starting a PAM request. Parameters: ";
        if (logAsError)
        {
            log.error(startMsg);
        }
        else
        {
            log.info(startMsg);            
        }
        
        int ix;
        
        for (ix = 0; ix < args.length; ix++)
        {
            String paramName = args[ix];            
            String paramValue = "--PARAMS OUT OF BALANCE--";
            if (ix < args.length)
            {
                paramValue = args[++ix];
            }

            if (logAsError)
            {            
                log.error(paramName + " : " + paramValue);
            }
            else
            {
                log.info(paramName + " : " + paramValue);
            }
        }
    }
}
