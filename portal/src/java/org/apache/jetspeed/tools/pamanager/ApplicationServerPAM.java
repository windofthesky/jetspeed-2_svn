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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.PortletCache;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;

/**
 * This is the catalina specific implemenation for deplyment of Portlet
 * Applications.
 * 
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:mavery@einnovation.com">Matt Avery</a>
 * @version $Id$
 */

public class ApplicationServerPAM extends FileSystemPAM implements Lifecycle, DeploymentRegistration
{
    // Implementaion of deplyment interface
    public final static String PAM_PROPERTY_SERVER = "server";
    public final static String PAM_PROPERTY_PORT = "port";
    public final static String PAM_PROPERTY_USER = "user";
    public final static String PAM_PROPERTY_PASSWORD = "password";

    protected static final Log log = LogFactory.getLog("deployment");
    private boolean started;

    /**
     * 
     * @param webAppsDir
     * @param registry
     * @param fsManager
     * @param entityAccess
     * @param appServerManager
     */
    public ApplicationServerPAM( String webAppsDir, PortletRegistry registry, PortletEntityAccessComponent entityAccess, PortletWindowAccessor windowAccess, ApplicationServerManager appServerManager, PortletCache portletCache )
    {       
        super(webAppsDir, registry, entityAccess, windowAccess, portletCache, appServerManager);
        ArgUtil.assertNotNull(ApplicationServerManager.class, appServerManager, this);
    }

    public void start()
    {
        if (isServerAvailable())
        {

            log.info("Deployment server port: " + appServerManager.getHostPort());
            log.info("Deployment server: " + appServerManager.getHostUrl());            
            
        }

    }

    // Interface not supported by this implementation
    public void deploy( PortletApplicationWar paWar ) throws PortletApplicationException
    {
        try
        {
            super.deploy(paWar);
            String paName = paWar.getPortletApplicationName();
            if(isServerAvailable())
            {
                checkResponse(appServerManager.install(webAppsDir + "/" + paName, paName));
            }

        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch(ApplicationAlreadyDeployedException de)
        {
            stopPortletApplication(paWar.getPortletApplicationName());
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e1)
            {
            }
            startPortletApplication(paWar.getPortletApplicationName());
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * Prepares the specified war file for deployment.
     * 
     * @param paName
     *            The Portlet Application name
     * @throws PortletApplicationException
     */

    public void undeploy( PortletApplicationWar paWar ) throws PortletApplicationException
    {
        try
        {
            if(isServerAvailable())
            {
                checkResponse(appServerManager.remove(paWar.getPortletApplicationName()));
            }
            
            super.undeploy(paWar);
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

    // Implementaion of Lifecycle interface
    /**
     * Starts the specified Portlet Application on the Application Server
     * 
     * @param paName
     *            The Portlet Application name
     * @throws PortletApplicationException
     */

    public void startPortletApplication( String paName ) throws PortletApplicationException
    {
        try
        {
            if(isServerAvailable())
            {
                checkResponse(appServerManager.start(paName));
            }
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
     * @param paName
     *            The Portlet Application name
     * @throws PortletApplicationException
     */

    public void stopPortletApplication( String paName ) throws PortletApplicationException
    {
        try
        {
            if(isServerAvailable())
            {
                checkResponse(appServerManager.stop(paName));
            }
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
     * @param paName
     *            The Portlet Application name
     * @throws PortletApplicationException
     */
    public void reloadPortletApplication( String paName ) throws PortletApplicationException
    {
        try
        {
            if(isServerAvailable())
            {
                checkResponse(appServerManager.reload(paName));
            }
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
     * @throws DeploymentException
     */
    private void checkResponse( String response ) throws PortletApplicationException, DeploymentException
    {
        if (response == null
                || (!response.startsWith("OK") && response.indexOf("Application already exists at path") == -1)
                    && response.indexOf("No context exists for path") == -1)
        {
            if (response == null)
            {
                response = "null response";
            }

            throw new PortletApplicationException("Catalina container action failed, \"" + response + "\"");
        }
        else if(response.indexOf("Application already exists at path") > -1)
        {
            throw new ApplicationAlreadyDeployedException(response);
        }
        else
        {
            log.info("Catalina deployment response: " + response);
        }
    }

    /**
     * <p>
     * stop
     * </p>
     * 
     * @see org.picocontainer.Startable#stop()
     *  
     */
    public void stop()
    {
       
    }

    private boolean isServerAvailable()
    {
        return appServerManager.isConnected();
    }
    /**
     * <p>
     * redeploy
     * </p>
     *
     * @see org.apache.jetspeed.tools.pamanager.Deployment#redeploy(org.apache.jetspeed.util.descriptor.PortletApplicationWar)
     * @param paWar
     * @throws PortletApplicationException
     */
    public void redeploy( PortletApplicationWar paWar ) throws PortletApplicationException
    {
        try
        {
            stopPortletApplication(paWar.getPortletApplicationName());
            super.redeploy(paWar);
            String paName = paWar.getPortletApplicationName();
            if(isServerAvailable())
            {
                String response = appServerManager.reload("/" + paName);
                // This means the context may have been deleted, so now let's try
                // and do a full deploy.
                if(!response.startsWith("OK") )
                {
                    deploy(paWar);
                }
            }

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