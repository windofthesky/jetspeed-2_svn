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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.PortletCache;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.exception.RegistryException;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * This PAM is the base class for most other PAM implementations. Does most of
 * the registry and file system clean for you.
 * 
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @author <a href="mailto:mavery@einnovation.com">Matt Avery </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */

public class FileSystemPAM implements PortletApplicationManagement, DeploymentRegistration
{
    // Implementation of deplyment interface
    public final int DEPLOY_WAR = 0;
    public final int UPDATE_WEB_XML = 1;
    public final int UPDATE_REGISTRY = 2;

    private static final String PORTLET_XML = "WEB-INF/portlet.xml";
    
    public static final String SYS_PROPS_DEPLOY_TO_DIR = "org.apache.jetspeed.deploy.target.dir";
    
    private static final Log log = LogFactory.getLog("deployment");

    //private DeployUtilities util;
    protected PortletRegistryComponent registry;
    protected String webAppsDir;
    protected PortletEntityAccessComponent entityAccess;
    protected PortletWindowAccessor windowAccess;

    public FileSystemPAM( String webAppsDir, PortletRegistryComponent registry,
            PortletEntityAccessComponent entityAccess, PortletWindowAccessor windowAccess )
    {
        super();
        ArgUtil.assertNotNull(PortletRegistryComponent.class, registry, this);
        ArgUtil.assertNotNull(PortletEntityAccessComponent.class, entityAccess, this);
        this.registry = registry;
        this.entityAccess = entityAccess;
        this.webAppsDir = webAppsDir;

        this.windowAccess = windowAccess;
    }

    /**
     * <p>
     * deploy
     * </p>
     * 
     * @see org.apache.jetspeed.tools.pamanager.Deployment#deploy(java.lang.String,
     *      java.lang.String, java.lang.String)
     * @param webAppsDir
     * @param warFile
     * @param paName
     * @throws PortletApplicationException
     */
    public void deploy( PortletApplicationWar paWar ) throws PortletApplicationException
    {
        sysDeploy(paWar, DEPLOY_WAR);

    }

    public void register( PortletApplicationWar paWar ) throws PortletApplicationException
    {
        sysDeploy(paWar, UPDATE_REGISTRY);
    }

    /**
     * Unregisters application.
     * 
     * @param paName
     *            The Portlet Application name
     * @throws PortletApplicationException
     */

    public void unregister( String paName ) throws PortletApplicationException
    {
        doUnregister(paName, true);

    }

    /**
     * <p>
     * doUnregister
     * </p>
     * 
     * @param paName
     * @throws PortletApplicationException
     */
    protected void doUnregister( String paName, boolean purgeEntityInfo ) throws PortletApplicationException
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

            log.info("Removing a portlets from the PortletCache that belong to portlet application " + paName);
            PortletCache.removeAll(app);

            // remove entries from the registry
            log.info("Remove all registry entries defined for portlet application " + paName);

            Iterator portlets = app.getPortletDefinitions().iterator();

            while (portlets.hasNext())
            {
                PortletDefinition portletDefinition = (PortletDefinition) portlets.next();
                Iterator entities = entityAccess.getPortletEntities(portletDefinition).iterator();
                while (entities.hasNext())
                {
                    PortletEntity entity = (PortletEntity) entities.next();
                    if(purgeEntityInfo)
                    {                      
                      entityAccess.removePortletEntity(entity);                     
                    }
                    entityAccess.removeFromCache(entity);                    
                    windowAccess.removeWindows(entity);
                        
                }
            }

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
     *            The webapps directory inside the Application Server
     * @param paName
     *            The Portlet Application name
     * @throws PortletApplicationException
     */

    public void undeploy( PortletApplicationWar paWar ) throws PortletApplicationException
    {
        String paName = paWar.getPortletApplicationName();

        try
        {
            // First unergister the application from Registry
            unregister(paWar.getPortletApplicationName());
        }
        catch (PortletApplicationException e)
        {
            log.warn("Undeploy could not unregister portlet application, " + paName + ", from the database.  "
                    + "Continuing removal of web application directory.");
        }

        try
        {
            paWar.removeWar();
            log.info("FileSystem un-deployment completed successfully.");

        }
        catch (FileNotFoundException fnfe)
        {
            log.warn(paWar.getDeployedPath() + " does not exist or has already been deleted, so skipping deletion");
        }
        catch (Exception re)
        {
            log.error("Failed to undeploy portlet application: " + re.toString(), re);
            throw new PortletApplicationException(re.getMessage(), re);
        }
        finally
        {
            if (paWar != null)
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

    protected void sysDeploy( PortletApplicationWar paWar, int startState ) throws PortletApplicationException
    {

        // State of deployment -- use integer to signal the state
        // 0 Initial state
        // 1 Archive deployed
        // 2 WEB XML updated
        // 3 Registry updated
        //
        int nState = DEPLOY_WAR; //Initialize
        MutablePortletApplication app = null;
        String paName = paWar.getPortletApplicationName();

        try
        {
            String portletAppDirectory = webAppsDir + "/" + paName;

            log.info("Portlet application deployment target directory is " + portletAppDirectory);

            PortletApplicationWar targetWar = null;
            if (startState <= nState)
            {
                targetWar = paWar.copyWar(portletAppDirectory);
            }

            nState = UPDATE_WEB_XML;

            if (startState <= nState && targetWar != null)
            {
                targetWar.processWebXML();
            }

            nState = UPDATE_REGISTRY;

            if (startState <= nState)
            {
                registerApplication(paWar);
            }

            nState = UPDATE_REGISTRY;

            // DONE
            log.info("FileSystem deployment done.");

        }
        catch (PortletApplicationException pae)
        {
            log.error("PortletApplicationException encountered deploying portlet application: " + pae.toString()
                    + " attempting rollback...", pae);
            rollback(nState, paWar, app);
            throw pae;
        }
        catch (Throwable t)
        {
            log.error(
                    "Unexpected exception deploying portlet application: " + t.toString() + " attempting rollback...",
                    t);
            rollback(nState, paWar, app);
            throw new PortletApplicationException(t);
        }
        finally
        {
            if (paWar != null)
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

    protected void registerApplication( PortletApplicationWar paWar ) throws PortletApplicationException,
            RegistryException
    {
        MutablePortletApplication app;
        PersistenceStore store = registry.getPersistenceStore();
        String paName = paWar.getPortletApplicationName();
        
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
            
            app.setChecksum(paWar.getFileSystem().getChecksum(PORTLET_XML));

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
            String msg = "Unable to register portlet application, " + paName + ", through the portlet registry: "
                    + e.toString();
            log.error(msg, e);
            store.getTransaction().rollback();
            throw new RegistryException(msg, e);
        }

    }

    protected void rollback( int nState, PortletApplicationWar paWar, MutablePortletApplication app )
    {
        log.info("Exception in deploy. Rollback of application deployment...");
        if (nState >= 2 && app != null)
        {
            rollbackRegistry(app);
        }

        if (nState >= 1)
        {
            rollbackFileSystem(paWar);
        }
    }

    protected void rollbackFileSystem( PortletApplicationWar paWar )
    {
        String paName = paWar.getPortletApplicationName();
        String portletAppDir = webAppsDir + "/" + paName;

        try
        {
            // Remove the webapps directory

            log.info("Rollback: Remove " + portletAppDir + " and all sub-directories.");

            DirectoryHelper dirHelper = new DirectoryHelper(new File(portletAppDir));
            dirHelper.remove();

        }
        catch (Exception e)
        {
            log.error("Error removing file system deployment artifacts: " + e.toString(), e);
        }
        finally
        {
            if (paWar != null)
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

    /**
     * 
     * <p>
     * getDeploymentPath
     * </p>
     * 
     * @see org.apache.jetspeed.tools.pamanager.Deployment#getDeploymentPath(java.lang.String)
     * @param webAppPath
     * @return
     */
    public String getDeploymentPath( String webAppPath )
    {
        if (webAppPath != null)
        {
            return webAppsDir + webAppPath;
        }
        else
        {
            return webAppsDir;
        }
    }

    /**
     * <p>
     * clearPortletEntities
     * </p>
     * 
     * @see org.apache.jetspeed.tools.pamanager.PortletApplicationManagement#clearPortletEntities(org.apache.pluto.om.portlet.PortletDefinition)
     * @param portletDefinition
     */
    public void clearPortletEntities( PortletDefinition portletDefinition )
    {

        Iterator entities = entityAccess.getPortletEntities(portletDefinition).iterator();
        while (entities.hasNext())
        {
            PortletEntity entity = (PortletEntity) entities.next();
            try
            {
                windowAccess.removeWindows(entity);
                entityAccess.removePortletEntity(entity);
                String entityNodePath = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/" + entity.getId();
                if (Preferences.userRoot().nodeExists(entityNodePath))
                {
                    Preferences.userRoot().node(entityNodePath).removeNode();
                }
            }
            catch (Exception e)
            {
                log.warn("Failed to delete preference node for PortletEntity: " + entity.getId());
            }

        }
    }

    /**
     * <p>
     * reDeploy
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
            doUnregister(paWar.getPortletApplicationName(), false);
            String paName = paWar.getPortletApplicationName();
            DirectoryHelper deployedDir = new DirectoryHelper(new File(webAppsDir + "/" + paName));
            PortletApplicationWar existingWar = new PortletApplicationWar(deployedDir, paName, "/" + paName);

            existingWar.removeWar();
            existingWar.close();
            sysDeploy(paWar, DEPLOY_WAR);
        }
        catch (IOException e)
        {
            throw new PortletApplicationException(e);
        }
    }
    

    public boolean registerPortletApplication(FileSystemHelper fileSystem,
            String portletApplicationName) 
    throws RegistryException
    {
        long checksum = fileSystem.getChecksum(PORTLET_XML);        
        MutablePortletApplication pa = registry
                .getPortletApplication(portletApplicationName);
        if (pa != null)
        {            
            if (checksum == pa.getChecksum())
            {
                System.out.println("PORTLET APPLICATION REGISTRATION: NO CHANGE on CHECKSUM for portlet.xml: " 
                        + portletApplicationName);
                
                return false;
            }
            System.out.println("PORTLET APPLICATION REGISTRATION: Checksum changed on portlet.xml: " 
                                + portletApplicationName);
        }

        PortletApplicationWar paWar = null;
        try
        {
            paWar = new PortletApplicationWar(fileSystem,
                    portletApplicationName, "/" + portletApplicationName);
        } catch (IOException e)
        {
            throw new RegistryException("Failed to create PA WAR", e);
        }

        MutablePortletApplication app;
        PersistenceStore store = registry.getPersistenceStore();
        String paName = paWar.getPortletApplicationName();

        try
        {
            app = paWar.createPortletApp();

            if (app == null)
            {
                String msg = "Error loading portlet.xml: ";
                log.error(msg);
                throw new RegistryException(msg);
            }

            app.setApplicationType(MutablePortletApplication.WEBAPP);
            app.setChecksum(checksum);

            // load the web.xml
            log
                    .info("Loading web.xml into memory...."
                            + portletApplicationName);
            MutableWebApplication webapp = paWar.createWebApp();
            paWar.validate();
            app.setWebApplicationDefinition(webapp);

            // save it to the registry
            log.info("Saving the portlet.xml in the registry..."
                    + portletApplicationName);
            store.getTransaction().begin();
            registry.registerPortletApplication(app);
            log.info("Committing registry changes..." + portletApplicationName);
            store.getTransaction().commit();
        } catch (Exception e)
        {
            String msg = "Unable to register portlet application, " + paName
                    + ", through the portlet registry: " + e.toString();
            log.error(msg, e);
            store.getTransaction().rollback();
            throw new RegistryException(msg, e);
        }
        return true;
    }    

    
    
}