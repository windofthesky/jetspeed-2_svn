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

import java.io.File;
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
import org.apache.jetspeed.util.DirectoryUtils;

/**
 * This is the catalina specific implemenation for deplyment of Portlet Applications.
 *
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */

public class FileSystemPAM implements Deployment, Registration
{
    // Implementation of deplyment interface
    public final int DEPLOY_WAR = 0;
    public final int UPDATE_WEB_XML = 1;
    public final int UPDATE_REGISTRY = 2;

    protected String deploymentDbAlias;
    private static final Log log = LogFactory.getLog("deployment");
    protected boolean useDefaultPluginFordeploy = false;

    private DeployUtilities util;
    private PortletRegistryComponent registry;
    public FileSystemPAM()
    {
        super();
        registry = (PortletRegistryComponent) Jetspeed.getComponentManager().getComponent(PortletRegistryComponent.class);
        util = new DeployUtilities();
    }

    /**
     * <p>
     * deploy
     * </p>
     *
     * @see org.apache.jetspeed.tools.pamanager.Deployment#deploy(java.lang.String, java.lang.String, java.lang.String)
     * @param webAppsDir
     * @param warFile
     * @param paName
     * @throws PortletApplicationException
     */
    public void deploy(String webAppsDir, String warFile, String paName) throws PortletApplicationException
    {
        sysDeploy(webAppsDir, warFile, paName, DEPLOY_WAR);

    }

    /**
     * <p>
     * deploy
     * </p>
     *
     * @see org.apache.jetspeed.tools.pamanager.Deployment#deploy(java.lang.String, java.lang.String, java.lang.String)
     * @param webAppsDir
     * @param warFile
     * @param paName
     * @param startState The deployment state where deployment should start:
     * 0 deploy war - 1 Update Web XML - 2 Update Registry
     * @throws PortletApplicationException
     */
    public void deploy(String webAppsDir, String warFile, String paName, int startState) throws PortletApplicationException
    {
        sysDeploy(webAppsDir, warFile, paName, startState);

    }

    // Interface not supported by FileSystemPAM

    /**
     * This opertion is not supported
     */
    public void deploy(String warFile, String paName) throws PortletApplicationException
    {
        throw new UnsupportedOperationException("FileSystemPAM.deploy(String warFile, String paName) is not supported.");
    }

    public void register(String webApplicationName, String portletApplicationName) 
    throws PortletApplicationException
    {
        sysDeploy(webApplicationName, "", portletApplicationName, UPDATE_REGISTRY);        
    }
    
    /**
     * Unregisters application.
     *
     * @param webAppsDir The webapps directory inside the Application Server
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public void unregister(String webAppsDir, String paName) throws PortletApplicationException
    {

        String portletAppDir = util.formatWebApplicationPath(webAppsDir, paName);
        String portletXMLPath = portletAppDir + "/WEB-INF/portlet.xml";
        PersistenceStore store = registry.getPersistenceStore();
        try
        {
            // Remove all registry entries
            // load the portlet.xml
            log.info("Loading " + portletXMLPath + " into memory....");
            store.getTransaction().begin();
            MutablePortletApplication app = (MutablePortletApplication) registry.getPortletApplication(paName);
            // Application app = registry.loadPortletApplicationSettings(portletXMLPath, paName);

            if (app == null)
            {
                log.warn("Error retrieving Application from Registry Database. Application not found: " + paName);
                return;
            }

            // remove entries from the registry
            log.info("Remove all registry entries defined in " + portletXMLPath);

            // registry.processPortletApplicationTree(app, "remove");
            // locate the deployment home
            
            
            registry.removeApplication(app);
            store.getTransaction().commit();

            // Remove the webapps directory

            log.info("Remove " + portletAppDir + " and all sub-directories.");

        }
        catch (Exception re)
        {

            log.error(
                "Failed to unregister internal portlet application: " + re.toString() + " attempting to rollback changes",
                re);

            throw new PortletApplicationException(re.getMessage());
        }

    }

    /**
     * Undeploys application.
     *
     * @param webAppsDir The webapps directory inside the Application Server
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public void undeploy(String webAppsDir, String paName) throws PortletApplicationException
    {
        try
        {
            // First unergister the application from Registry
            unregister(webAppsDir, paName);

            // Call into DeplyUtilities class
            DeployUtilities util = new DeployUtilities();
            File webAppRootDir = new File(util.formatWebApplicationPath(webAppsDir, paName));
            //	prepend a slash if need be

            if (webAppRootDir.exists())
            {

                if (DirectoryUtils.rmdir(webAppRootDir) == false)
                {
                    log.error(
                        "Failed to delete web app directory "
                            + webAppsDir
                            + paName
                            + " .Make sure the application is no longer running.");
                    log.info("FileSystem un-deployment incomplete.");
                }
                else
                {
                    log.info("FileSystem un-deployment completed successfully.");
                }
            }
            else
            {
                log.info(
                    "Could not locate web application directory " + webAppRootDir.getCanonicalPath() + " so, skipping deletion.");
                log.info("FileSystem un-deployment completed successfully.");
            }

        }
        catch (Exception re)
        {

            log.error("Failed to undeploy portlet application: " + re.toString() + " attempting to rollback changes", re);

            throw new PortletApplicationException(re.getMessage());
        }

    }

    /**
     * Undeploys application.
     *
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */
    public void undeploy(String paName) throws PortletApplicationException
    {
        throw new UnsupportedOperationException("FileSystemPAM.undeploy(String paName) is not supported.");

    }

    //    private void rollback(int nState, String webAppsDir, String paName, MutablePortletApplication app)
    //    {
    //        log.info("Exception in deploy. Rollback of application deployment...");
    //
    //        try
    //        {
    //            if (nState >= 2 && app != null)
    //            {
    //                // remove entries from the registry
    //                // registry.processPortletApplicationTree(app, "remove");
    //                log.info("Saving the portlet.xml in the registry...");
    //                // locate the deployment home
    //                identifyDeploymentSystem();
    //                registry.beginTransaction();
    //                registry.removeApplication(app);
    //                registry.commitTransaction();
    //            }
    //
    //        }
    //        catch (Exception e1)
    //        {
    //            try
    //            {
    //                log.error("Error processing rollback.  Attempting to rollback registry transaction.", e1);
    //                registry.rollbackTransaction();
    //            }
    //            catch (TransactionStateException e2)
    //            {
    //                log.error("Error processing tranasction: " + e2.toString(), e2);
    //                e2.printStackTrace();
    //            }
    //
    //        }
    //
    //        try
    //        {
    //
    //            if (nState >= 1)
    //            {
    //                // Remove the webapps directory
    //                log.info("Rollback: Remove " + webAppsDir + paName + " and all sub-directories.");
    //
    //                // Call into DeplyUtilities class
    //                DeployUtilities util = new DeployUtilities();
    //                if (DirectoryUtils.rmdir(new File(webAppsDir + paName)) == false)
    //                {
    //                    log.error(
    //                        "Rollback: Failed to delete web app directory "
    //                            + webAppsDir
    //                            + " .Make sure the application is no longer running.");
    //                }
    //            }
    //        }
    //        catch (Exception e)
    //        {
    //            log.error("Error removing file system deployment artifacts: " + e.toString(), e);
    //        }
    //    }

    protected void sysDeploy(String webAppsDir, String warFile, String paName, int startState) throws PortletApplicationException
    {

        // State of deployment -- use integer to signal the state
        // 0 Initial state
        // 1 Archive deployed
        // 2 WEB XML updated
        // 3 Registry updated
        //
        int nState = DEPLOY_WAR; //Initialize
        MutablePortletApplication app = null;

        try
        {
            if (startState <= nState)
            {
                util.deployArchive(webAppsDir, warFile, paName);
            }

            nState = UPDATE_WEB_XML;

            if (startState <= nState)
            {
                util.processWebXML(util.getWebXMLPath(webAppsDir, warFile, paName), paName);
            }

            nState = UPDATE_REGISTRY;

            if (startState <= nState)
            {
                registerApplication(webAppsDir, paName);
            }

            nState = UPDATE_REGISTRY;

            // DONE
            log.info("FileSystem deployment done.");

        }
        catch (PortletApplicationException pae)
        {
            log.error(
                "PortletApplicationException encountered deploying portlet application: "
                    + pae.toString()
                    + " attempting rollback...",
                pae);
            rollback(nState, webAppsDir, paName, app);
            throw pae;
        }
        catch (Throwable t)
        {
            log.error("Unexpected exception deploying portlet application: " + t.toString() + " attempting rollback...", t);
            rollback(nState, webAppsDir, paName, app);
            throw new PortletApplicationException(t);
        }

    }

    protected void registerApplication(String webAppsDir, String paName) throws PortletApplicationException, RegistryException
    {
        MutablePortletApplication app;
        // Application is deployed -- populate the registry with the portlet.xml
        String portletAppDir = util.formatWebApplicationPath(webAppsDir, paName);
        String portletXMLPath = portletAppDir + "/WEB-INF/portlet.xml";

        // load the portlet.xml
        log.info("Loading " + portletXMLPath + " into memory....");
        app = (MutablePortletApplication) PortletDescriptorUtilities.loadPortletDescriptor(portletXMLPath, paName);

        if (app == null)
        {
            String msg = "Error loading: " + portletXMLPath;
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

        String contextRoot;
        String webXMLPath = portletAppDir + "/WEB-INF/web.xml";

        if (paName.startsWith("/")) {
            contextRoot = paName;
        } else {
            contextRoot = "/" + paName;
        }

        // load the web.xml
        log.info("Loading "+webXMLPath + " into memory....");
        MutableWebApplication webapp = (MutableWebApplication) WebDescriptorUtilities.loadDescriptor(webXMLPath, contextRoot, Jetspeed
                .getDefaultLocale(), paName);

        app.setWebApplicationDefinition(webapp);

        // validate the application definition
        PortletDescriptorUtilities.validate(app);
        
        try
        {
            String jetspeedXMLPath = portletAppDir + "/WEB-INF/jetspeed-portlet.xml";
            log.info("Loading " + jetspeedXMLPath + " into memory....");
            if(JetspeedDescriptorUtilities.loadPortletDescriptor(jetspeedXMLPath, app))
            {
                log.info("Loaded " + jetspeedXMLPath + " into memory....");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // save it to the registry
        log.info("Saving the portlet.xml in the registry...");
        PersistenceStore store = registry.getPersistenceStore();
        try
        {
            store.getTransaction().begin();
            registry.registerPortletApplication(app);
            log.info("Committing registry changes...");
            store.getTransaction().commit();                        

        }
        catch (Exception e)
        {
            String msg =
                "Unable to register portlet application, " + app.getName() + ", through the portlet registry: " + e.toString();
            log.error(msg, e);
            store.getTransaction().rollback();
            throw new RegistryException(msg, e);
        }

    }

    protected void rollback(int nState, String webAppsDir, String paName, MutablePortletApplication app)
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

    protected void rollbackFileSystem(String webAppsDir, String paName)
    {
        try
        {

            // Remove the webapps directory
            String portletAppDir = util.formatWebApplicationPath(webAppsDir, paName);
            log.info("Rollback: Remove " + portletAppDir + " and all sub-directories.");

            // Call into DeplyUtilities class
            DeployUtilities util = new DeployUtilities();
            if (DirectoryUtils.rmdir(new File(portletAppDir)) == false)
            {
                log.error(
                    "Rollback: Failed to delete web app directory "
                        + portletAppDir
                        + " .Make sure the application is no longer running.");
            }
        }
        catch (Exception e)
        {
            log.error("Error removing file system deployment artifacts: " + e.toString(), e);
        }
    }

    /**
     * Roles back any registry changes that have been made
     * @param app
     */
    protected void rollbackRegistry(MutablePortletApplication app)
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

    public void connect(Map params)
    throws PortletApplicationException
    {        
    }
    
}
