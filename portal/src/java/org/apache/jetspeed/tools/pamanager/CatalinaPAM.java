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



import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.tools.pamanager.servletcontainer.TomcatManager;


/**
 * This is the catalina specific implemenation for deplyment of Portlet Applications.
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a> 
  * @version $Id$
 */


public class CatalinaPAM extends FileSystemPAM implements  Lifecycle
{
    // Implementaion of deplyment interface
    public final static String PAM_PROPERTY_SERVER = "server";
    public final static String PAM_PROPERTY_PORT = "port";
    public final static String PAM_PROPERTY_USER = "user";
    public final static String PAM_PROPERTY_PASSWORD = "password";

    private TomcatManager tomcatManager = null;
    protected static final Log log = LogFactory.getLog("deployment");


    /**
     * @param registry
     * @param defaultLocale
     */
    public CatalinaPAM( PortletRegistryComponent registry, Locale defaultLocale )
    {
        super(registry, defaultLocale);
        
    }
    public void connect(Map params)
    throws PortletApplicationException
    {      
        try
        {
            int port = 8080;
            String server = (String)params.get(PAM_PROPERTY_SERVER);
            Integer portNumber = (Integer)params.get(PAM_PROPERTY_PORT);
            if (null != portNumber)
            {
                port = portNumber.intValue();
            }
            String username = (String)params.get(PAM_PROPERTY_USER);
            String password = (String)params.get(PAM_PROPERTY_PASSWORD);
            
            tomcatManager = new TomcatManager(server, port, username, password);
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }        
    }
    
    // Interface not supported by this implementation 
    public void deploy(String webAppsDir, String warFile, String paName) 
    throws PortletApplicationException
    {
        try
        {            
            super.deploy(webAppsDir, warFile, paName);
            checkResponse(tomcatManager.install(webAppsDir + "/" + paName, paName));
			
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * Deploys the specified war file to the webapps directory on the Application Server.
     * The appServer parameter specifies a specific Application Server.
     * 
     * 
     * @param warFile The warFile containing the Portlet Application
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public void deploy(String warFile, String paName) throws PortletApplicationException
    {
		super.deploy(null, warFile, paName, 0);	
        try
        {
            checkResponse(tomcatManager.install(warFile, paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * Prepares the specified war file for deployment.
     * 
     * @param paName The Portlet Application name 
     * @throws PortletApplicationException
     */

    public void undeploy(String paName) throws PortletApplicationException
    {
        try
        {
            checkResponse(tomcatManager.remove(paName));
            super.undeploy(paName);
        }
        catch (UnsupportedOperationException usoe)
        {
            // ignore FS PAM not suporting this
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /** Undeploys application.
    * 
    * @param webAppsDir The webapps directory inside the Application Server
    * @param paName The Portlet Application name 
    * @throws PortletApplicationException
    */

    public void undeploy(String webAppsDir, String paName) throws PortletApplicationException
    {
        try
        {
            checkResponse(tomcatManager.remove(paName));
            super.undeploy(webAppsDir, paName);
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    // Implementaion of Lifecycle interface
    /**
    * Starts the specified Portlet Application on the Application Server
    * 
    * @param paName The Portlet Application name
    * @throws PortletApplicationException
    */

    public void start(String paName) throws PortletApplicationException
    {
        try
        {
            checkResponse(tomcatManager.start(paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }

    }

    /**
     * Stops a portlet application from running on the Application Server
     * 
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public void stop(String paName) throws PortletApplicationException
    {
        try
        {
            checkResponse(tomcatManager.stop(paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * Reloads a portlet application.
     * 
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */
    public void reload(String paName) throws PortletApplicationException
    {
        try
        {
            
            checkResponse(tomcatManager.reload(paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * 
     * @param response
     * @throws PortletApplicationException
     */
    private void checkResponse(String response) throws PortletApplicationException
    {
        if (response == null || (!response.startsWith("OK") && response.indexOf("Application already exists at path") == -1) )
        {
            if (response == null)
            {
                response = "null response";
            }

            throw new PortletApplicationException("Catalina container action failed, \"" + response + "\"");
        }
        else
        {
            log.info("Catalina deployment response: " + response);
        }
    }

    /**
     * @see org.apache.jetspeed.tools.pamanager.Deployment#deploy(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
     */
    public void deploy(String webAppsDir, String warFile, String paName, String deploymentDbAlias, int startState)
        throws PortletApplicationException
    {

		super.deploy(webAppsDir, warFile, paName, startState);		
        try
        {
            checkResponse(tomcatManager.install(warFile, paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }



}
