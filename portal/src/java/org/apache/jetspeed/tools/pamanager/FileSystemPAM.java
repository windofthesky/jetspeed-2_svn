/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.tools.pamanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;

/**
 * This is the catalina specific implemenation for deplyment of Portlet
 * Applications.
 * 
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann </a>
 * @version $Id$
 */

public class FileSystemPAM implements PortletApplicationManagement
{
    // Implementation of deplyment interface
    public final int DEPLOY_WAR = 0;
    public final int UPDATE_WEB_XML = 1;
    public final int UPDATE_REGISTRY = 2;
    
    public static final String SYS_PROPS_DEPLOY_TO_DIR = "org.apache.jetspeed.deploy.target.dir";

    private static final Log log = LogFactory.getLog("deployment");
 
    //private DeployUtilities util;
    private PortletRegistryComponent registry;

    public FileSystemPAM()
    {
        super();
        registry = (PortletRegistryComponent) Jetspeed.getComponentManager().getComponent(
                PortletRegistryComponent.class);
    }

    /**
     * <p>
     * deploy
     * </p>
     * 
     * @see org.apache.jetspeed.tools.pamanager.Deployment#deploy(java.lang.String,
     *          java.lang.String, java.lang.String)
     * @param webAppsDir
     * @param warFile
     * @param paName
     * @throws PortletApplicationException
     */
    public void deploy( String webAppsDir, String warFile, String paName ) throws PortletApplicationException
    {
        sysDeploy(webAppsDir, warFile, paName, DEPLOY_WAR);

    }

    /**
     * <p>
     * deploy
     * </p>
     * 
     * @see org.apache.jetspeed.tools.pamanager.Deployment#deploy(java.lang.String,
     *          java.lang.String, java.lang.String)
     * @param webAppsDir
     * @param warFile
     * @param paName
     * @param startState
     *                  The deployment state where deployment should start: 0 deploy
     *                  war - 1 Update Web XML - 2 Update Registry
     * @throws PortletApplicationException
     */
     public void deploy( String webAppsDir, String warFile, String paName, int startState )
            throws PortletApplicationException
    {
        sysDeploy(webAppsDir, warFile, paName, startState);

    }


 
    
 

    /**
     * <p>
     * deploy
     * </p>
     * 
     * @param warFile path to the war file or war file structure.
     * @param paName of the portlet applicaiton being deployed.
     * @throws java.lang.IllegalStateException if the <code>org.apache.jetspeed.deploy.target.dir</code>
     * system property has not been defined
     */
    public void deploy( String warFile, String paName ) throws PortletApplicationException, IllegalStateException
    {

        String webAppsDir = System.getProperty(SYS_PROPS_DEPLOY_TO_DIR);
        if(webAppsDir == null)
        {
            throw new IllegalStateException("to use FileSystemPAM.deploy(String, String) you must specify the target directory in "+
                                                                 "the system property "+SYS_PROPS_DEPLOY_TO_DIR);
        }
        deploy(webAppsDir, warFile, paName);
    }

    public void register( String webApplicationName, String portletApplicationName, String warFile ) throws PortletApplicationException
    {
        sysDeploy(webApplicationName, warFile, portletApplicationName, UPDATE_REGISTRY);
    }

    /**
     * Unregisters application.
     * 
     * @param webAppsDir
     *                  The webapps directory inside the Application Server
     * @param paName
     *                  The Portlet Application name
     * @throws PortletApplicationException
     */

    public void unregister( String webAppsDir, String paName ) throws PortletApplicationException
    {
        PersistenceStore store = registry.getPersistenceStore();
        
        try
        {
            store.getTransaction().begin();
            MutablePortletApplication app = (MutablePortletApplication) registry.getPortletApplication(paName);
            
            if (app == null)
            {
                log.warn("Error retrieving Application from Registry Database. Application not found: " + paName);
                return;
            }

            // remove entries from the registry
            log.info("Remove all registry entries defined for portlet application "+paName);

            registry.removeApplication(app);
            store.getTransaction().commit();
        }
        catch (Exception re)
        {

            log.error("Failed to unregister internal portlet application: " + re.toString()
                    + " attempting to rollback changes", re);

            throw new PortletApplicationException(re.getMessage());
        }

    }

    /**
     * Undeploys application.
     * 
     * @param webAppsDir
     *                  The webapps directory inside the Application Server
     * @param paName
     *                  The Portlet Application name
     * @throws PortletApplicationException
     */

    public void undeploy( String webAppsDir, String paName ) throws PortletApplicationException
    {       
        
        try
        {
            // First unergister the application from Registry
            unregister(webAppsDir, paName);
        }
        catch (PortletApplicationException e)
        {
            log.warn("Undeploy could not unregister portlet application, " + paName + ", from the database.  "
                    + "Continuing removal of web application directory.");
        }
        PortletApplicationWar paWar = null;
        try
        {
            paWar = new PortletApplicationWar(webAppsDir+"/"+paName, paName, "/"+paName, Jetspeed.getDefaultLocale(),  paName );
            paWar.removeWar();
            log.info("FileSystem un-deployment completed successfully.");

        }
        catch (FileNotFoundException fnfe)
        {
            log.warn(webAppsDir+"/"+paName+" does not exist, so skipping deletion");
        }
        catch (Exception re)
        {
            log.error("Failed to undeploy portlet application: " + re.toString(), re);
            throw new PortletApplicationException(re.getMessage());
        }
        finally
        {
            if(paWar != null )
            {
                try
                {
                    paWar.close();
                }
                catch (IOException e1)
                {

                }
            }
        }

    }

    /**
     * Undeploys application.
     * 
     * @param paName
     *                  The Portlet Application name
     * @throws PortletApplicationException
     */
    public void undeploy( String paName ) throws PortletApplicationException
    {
        throw new UnsupportedOperationException("FileSystemPAM.undeploy(String paName) is not supported.");

    }

   
    protected void sysDeploy( String webAppsDir, String warFile, String paName, int startState )
            throws PortletApplicationException
    {

        // State of deployment -- use integer to signal the state
        // 0 Initial state
        // 1 Archive deployed
        // 2 WEB XML updated
        // 3 Registry updated
        //
        int nState = DEPLOY_WAR; //Initialize
        MutablePortletApplication app = null;    
        
        PortletApplicationWar paWar = null;                
        try
        {
            paWar = new PortletApplicationWar(warFile, paName, "/"+paName, Jetspeed.getDefaultLocale(),  paName );
            
            String portletAppDirectory = webAppsDir+"/"+paName;
            log.info("Portlet application deployment target directory is "+portletAppDirectory);
            
            if (startState <= nState)
            {                
                paWar.copyWar(portletAppDirectory);                
            }

            nState = UPDATE_WEB_XML;

            if (startState <= nState)
            {
                paWar.processWebXML(portletAppDirectory+"/WEB-INF/web.xml");
            }

            nState = UPDATE_REGISTRY;

            if (startState <= nState)
            {
                registerApplication(webAppsDir, paName, paWar);
            }

            nState = UPDATE_REGISTRY;

            // DONE
            log.info("FileSystem deployment done.");

        }
        catch (PortletApplicationException pae)
        {
            log.error("PortletApplicationException encountered deploying portlet application: " + pae.toString()
                    + " attempting rollback...", pae);
            rollback(nState, webAppsDir, paName, app);
            throw pae;
        }
        catch (Throwable t)
        {
            log.error(
                    "Unexpected exception deploying portlet application: " + t.toString() + " attempting rollback...",
                    t);
            rollback(nState, webAppsDir, paName, app);
            throw new PortletApplicationException(t);
        }
        finally
        {
            if(paWar != null)
            {
                try
                {
                    paWar.close();
                }
                catch (IOException e)
                {

                }
            }
        }

    }

    protected void registerApplication( String webAppsDir, String paName, PortletApplicationWar paWar ) throws PortletApplicationException,
            RegistryException
    {
        MutablePortletApplication app;
        PersistenceStore store = registry.getPersistenceStore();        
       
        try
        { 
            app = paWar.createPortletApp();
           
            if (app == null)
            {
                String msg = "Error loading portlet.xml: ";
                log.error(msg);
                throw new PortletApplicationException(msg);
            }

            if (webAppsDir.indexOf(JetspeedPortletContext.LOCAL_PA_ROOT) > -1)
            {
                app.setApplicationType(MutablePortletApplication.LOCAL);
            }
            else
            {
                app.setApplicationType(MutablePortletApplication.WEBAPP);
            }

            // load the web.xml
            log.info("Loading web.xml into memory....");
            MutableWebApplication webapp = paWar.createWebApp();
            paWar.validate(); 
            app.setWebApplicationDefinition(webapp);

            // save it to the registry
            log.info("Saving the portlet.xml in the registry...");
            store.getTransaction().begin();
            registry.registerPortletApplication(app);
            log.info("Committing registry changes...");
            store.getTransaction().commit();
        }
        catch (Exception e)
        {
            String msg = "Unable to register portlet application, " + paName
                                + ", through the portlet registry: " + e.toString();
            log.error(msg, e);
            store.getTransaction().rollback();
            throw new RegistryException(msg, e);
        }

    }

    protected void rollback( int nState, String webAppsDir, String paName, MutablePortletApplication app )
    {
        log.info("Exception in deploy. Rollback of application deployment...");
        if (nState >= 2 && app != null)
        {
            rollbackRegistry(app);
        }

        if (nState >= 1)
        {
            rollbackFileSystem(webAppsDir, paName);
        }
    }

    protected void rollbackFileSystem( String webAppsDir, String paName )
    {
        
        String portletAppDir = webAppsDir+"/"+ paName;
        PortletApplicationWar paWar = null;
        try
        {
            // Remove the webapps directory

            log.info("Rollback: Remove " + portletAppDir + " and all sub-directories.");
                                
            paWar = new PortletApplicationWar(portletAppDir, paName, "/"+paName, Jetspeed.getDefaultLocale(),  paName );
            paWar.removeWar();
           
        }
        catch (FileNotFoundException fnfe)
        {
            log.warn(portletAppDir+" could not be found, skipping deletion.", fnfe);
        }
        catch (Exception e)
        {
            log.error("Error removing file system deployment artifacts: " + e.toString(), e);
        }
        finally
        {
            if(paWar != null)
            {
                try
                {
                    paWar.close();
                }
                catch (IOException e1)
                {

                }
            }
        }
    }

    /**
     * Roles back any registry changes that have been made
     * 
     * @param app
     */
    protected void rollbackRegistry( MutablePortletApplication app )
    {
        PersistenceStore store = registry.getPersistenceStore();
        try
        {

            // remove entries from the registry
            // registry.processPortletApplicationTree(app, "remove");
            store.getTransaction().begin();
            log.info("Saving the portlet.xml in the registry...");
            registry.removeApplication(app);
            store.getTransaction().commit();
        }

        catch (Exception e1)
        {
            store.getTransaction().rollback();
            log.error("Error processing rollback.  Attempting to rollback registry transaction.", e1);

        }
    }

    public void connect( Map params ) throws PortletApplicationException
    {
    }

}