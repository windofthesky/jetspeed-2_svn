/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.tools.pamanager;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.exception.JetspeedException;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * This is the interface that defines the Lifecycle-related methods to control
 * Portlet Applications.
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
  * @version $Id$
 */

public class PortletApplicationManager implements JetspeedEngineConstants
{
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
        String strAppServer = "";
        String strPortalName = "jetspeed";
        String applicationType = "webapp";
		String strUserName = "";
		String strPassword = "";
		String strServer = "localhost";
		int intServerPort = 8080;

        while (i < args.length && args[i].startsWith("-"))
        {
            arg = args[i++];

            // use this type of check for arguments that require arguments
            if (arg.equalsIgnoreCase("-PortletAppName"))
            {
                if (i < args.length)
                    strPortletAppName = args[i++];
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
            else if (arg.equalsIgnoreCase("-ApplicationServer"))
            {
                if (i < args.length)
                    strAppServer = args[i++];
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
            return;
        }

        String strAppRoot = strWebAppDir + strPortalName;

        try
        {
            // Start the registry service -- it's needed by many actions
            Configuration properties =
                (Configuration) new PropertiesConfiguration(strAppRoot + "/WEB-INF/conf/jetspeed.properties");

            properties.setProperty(APPLICATION_ROOT_KEY, strAppRoot);

            // Override the properties with PAM specifice settings
            overrideProperties(strAppRoot, properties);

            engine = Jetspeed.createEngine(properties, strAppRoot, null);

        }
        catch (Exception e)
        {
            System.out.println("Failed connecting to registry service. Error: " + e.getMessage());
        }

        // Invoke the functions
        if (strAction.compareToIgnoreCase("deploy") == 0)
        {
            // Make sure that the WarFileName and the ApplicationServer are defined
            if (strWarFileName.length() == 0)
            {
                System.out.println(
                    "\nDeploy action requires the war file name. Use '-WarFileName file.war' to specify the file name");
                return;
            }
            else
            {
                if (strAppServer.length() == 0)
                {
                    if (applicationType.equals("local"))
                    {
                        String portletAppRoot = strAppRoot + "/WEB-INF/apps/";
                        deploy(portletAppRoot, strWarFileName, strPortletAppName);
                    }
                    else
                    {
                        // Requires WebAppDir
                        if (strWebAppDir.length() == 0)
                        {
                            System.out.println(
                                "\nDeploy action requires the definition of the ApplicationServer or the Web application directory.");
                            return;
                        }

                        // File deploy uses Directory and warfile
                        deploy(strWebAppDir, strWarFileName, strPortletAppName);
                    }
                }
                else
                {
                    // Uses war file and applicationServerName
                    deployServer(strWarFileName, strAppServer, strPortletAppName, strServer, intServerPort, strUserName, strPassword);
                }
            }
        }
        else if (strAction.compareToIgnoreCase("register") == 0)
        {
            // Requires WebAppDir
            if (strWebAppDir.length() == 0)
            {
                System.out.println("\nRegister action requires the definition of the Web application directory.");
                return;
            }

            register(strWebAppDir, strWarFileName, strPortletAppName);
        }
        else if (strAction.compareToIgnoreCase("unregister") == 0)
        {
            // Application server can be null -- using Catalina as default
            unregister(strWebAppDir, strPortletAppName, strAppServer);
        }
        else if (strAction.compareToIgnoreCase("undeploy") == 0)
        {
            // Application server can be null -- using Catalina as default
            undeploy(strWebAppDir, strPortletAppName, strAppServer, strServer, intServerPort, strUserName, strPassword);
        }
        else if (strAction.compareToIgnoreCase("start") == 0)
        {
            // Application server can be null -- using Catalina as default
            start(strAppServer, strPortletAppName, strServer, intServerPort, strUserName, strPassword);
        }
        else if (strAction.compareToIgnoreCase("stop") == 0)
        {
            // Application server can be null -- using Catalina as default
            stop(strAppServer, strPortletAppName, strServer, intServerPort, strUserName, strPassword);
        }
        else if (strAction.compareToIgnoreCase("reload") == 0)
        {
            // Application server can be null -- using Catalina as default
            reload(strAppServer, strPortletAppName, strServer, intServerPort, strUserName, strPassword);
        }
        else
        {
            System.out.println("\nAction: " + strAction + " not recognized by the PortletApplicationManager.");
            helpScreen();
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
        }
        System.out.println("Done");
        // return;
        System.exit(0);
    }

    public static void helpScreen()
    {
        System.out.println("\nPortletApplicationManager [options]\n");
        System.out.println("\t-action\t\t\t{deploy|undeploy|start|stop|reload}\n");
        System.out.println("\t-PortletAppName\t\t{AppName}\n");
        System.out.println("\t-WebAppDir\t\t{Path to target WebApp directory}\n");
        System.out.println("\t-WarFileName\t\t{Path to war file to deploy}\n");
        System.out.println("\t-ApplicationServer\t{Application server}\n");
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
     * @param webAppsDir The webapps directory inside the Application Server
     * @param warFile The warFile containing the Portlet Application
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public static void register(String webAppsDir, String warFile, String strPortletAppName)
    {
        // Invoke FileSystemPAM
        FileSystemPAM fs = new FileSystemPAM();
        try
        {
            System.out.println("Calling FileSystemPAM...");

            fs.deploy(webAppsDir, warFile, strPortletAppName, 2);
        }
        catch (PortletApplicationException e)
        {
            //e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Unregisterd a deployed portlet application
     *
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public static void unregister(String strWebAppDir, String paName, String appServer)
    {
        if (strWebAppDir.length() != 0)
        {
            // FileSystem undeploy
            FileSystemPAM dc = new FileSystemPAM();
            try
            {
                dc.unregister(strWebAppDir, paName);
            }
            catch (PortletApplicationException e)
            {
                e.printStackTrace(System.out);
            }
        }

    }

    /**
     * Deploys the specified war file to the webapps dirctory specified.
     *
     * @param webAppsDir The webapps directory inside the Application Server
     * @param warFile The warFile containing the Portlet Application
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public static void deploy(String webAppsDir, String warFile, String strPortletAppName)
    {
        // Invoke FileSystemPAM
        FileSystemPAM fs = new FileSystemPAM();
        try
        {
            System.out.println("Calling FileSystemPAM...");

            fs.deploy(webAppsDir, warFile, strPortletAppName);
        }
        catch (PortletApplicationException e)
        {
            //e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Deploys the specified war file to the webapps directory on the Application Server.
     * The appServer parameter specifies a specific Application Server.
     *
     * @param warFile The warFile containing the Portlet Application
     * @param appServer The Application Server name receiving the Portlet Application.
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public static void deployServer(String warFile, String appServer, String strPortletAppName, String strServer, int intServerPort, String strUser, String strPassword)
    {
        Object dc;
        if (appServer == null || (appServer.compareToIgnoreCase("catalina") == 0))
        {
            
            try
            {
				dc = new CatalinaPAM(strServer, intServerPort, strUser, strPassword);
                ((Deployment) dc).deploy(warFile, strPortletAppName);
            }
            catch (PortletApplicationException e)
            {
                e.printStackTrace(System.out);
            }

        }
        else
        {
            System.out.println("Application Server: " + appServer + " not supported");
        }
    }

    /**
     * Prepares the specified war file for deployment.
     *
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public static void undeploy(String strWebAppDir, String paName, String appServer, String strServer, int intServerPort, String strUser, String strPassword)
    {
        Object dc;
        if (strWebAppDir.length() == 0)
        {
            if (appServer == null || (appServer.compareToIgnoreCase("catalina") == 0))
            {                
                try
                {
					dc = new CatalinaPAM(strServer, intServerPort, strUser, strPassword);
                    ((Deployment) dc).undeploy(paName);
                }
                catch (PortletApplicationException e)
                {
                    e.printStackTrace(System.out);
                }

            }
            else
            {
                System.out.println("Application Server: " + appServer + " not supported");
            }
        }
        else
        {
            // FileSystem undeploy
            dc = new FileSystemPAM();
            try
            {
                ((Deployment) dc).undeploy(strWebAppDir, paName);
            }
            catch (PortletApplicationException e)
            {
                e.printStackTrace(System.out);
            }
        }

    }

    /**
    * Starts the specified Portlet Application on the Application Server
    *
    * @param paName The Portlet Application name
    * @throws PortletApplicationException
    */

    public static void start(String paName, String appServer, String strServer, int intServerPort, String strUser, String strPassword)
    {
        Object dc;
        if (appServer == null || (appServer.compareToIgnoreCase("catalina") == 0))
        {
            
            try
            {
				dc = new CatalinaPAM(strServer, intServerPort, strUser, strPassword);
                ((Lifecycle) dc).start(paName);
            }
            catch (PortletApplicationException e)
            {
                e.printStackTrace(System.out);
            }

        }
        else
        {
            System.out.println("Application Server: " + appServer + " not supported");
        }

    }

    /**
     * Stops a portlet application from running on the Application Server
     *
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public static void stop(String paName, String appServer, String strServer, int intServerPort, String strUser, String strPassword)
    {
        Object dc;
        if (appServer == null || (appServer.compareToIgnoreCase("catalina") == 0))
        {            
            try
            {
				dc = new CatalinaPAM(strServer, intServerPort, strUser, strPassword);
                ((Lifecycle) dc).stop(paName);
            }
            catch (PortletApplicationException e)
            {
                e.printStackTrace(System.out);
            }

        }
        else
        {
            System.out.println("Application Server: " + appServer + " not supported");
        }

    }

    /**
     * Reloads a portlet application.
     *
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public static void reload(String paName, String appServer, String strServer, int intServerPort, String strUser, String strPassword)
    {
        Object dc;
        if (appServer == null || (appServer.compareToIgnoreCase("catalina") == 0))
        {            
            try
            {
				dc = new CatalinaPAM(strServer, intServerPort, strUser, strPassword);
                ((Lifecycle) dc).reload(paName);
            }
            catch (PortletApplicationException e)
            {
                e.printStackTrace(System.out);
            }

        }
        else
        {
            System.out.println("Application Server: " + appServer + " not supported");
        }
    }

    /*
      * Method to override jetspeed properties.
      * @param properties The base configuration properties for the Jetspeed system.
      */
    public static void overrideProperties(String strApplicationRoot, Configuration properties) throws IOException
    {
        String testPropsPath = strApplicationRoot + "/WEB-INF/conf/pam.properties";
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
}
