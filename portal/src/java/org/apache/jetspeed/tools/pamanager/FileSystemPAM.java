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

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.portletregsitry.PortletRegistryComponent;
import org.apache.jetspeed.components.portletregsitry.RegistryException;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.util.DirectoryUtils;

/**
 * This is the catalina specific implemenation for deplyment of Portlet Applications.
 *
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */

public class FileSystemPAM implements Deployment
{
    // Implementation of deplyment interface

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
        sysDeploy(webAppsDir, warFile, paName, 0);

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
     * 0 deploy war - 1 Update Web XML - 2 Update Regsitry
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
        try
        {
            // Remove all registry entries
            // load the portlet.xml
            log.info("Loading " + portletXMLPath + " into memory....");

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
        int nState = 0; //Initialize
        MutablePortletApplication app = null;

        try
        {
            if (startState <= nState)
            {
                util.deployArchive(webAppsDir, warFile, paName);
            }

            nState = 1;

            if (startState <= nState)
            {
                util.processWebXML(util.getWebXMLPath(webAppsDir, warFile, paName), paName);
            }

            nState = 2;

            if (startState <= nState)
            {
                registerApplication(webAppsDir, paName);
            }

            nState = 3;

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
        //					// create the web application
        MutableWebApplication webapp = new WebApplicationDefinitionImpl();
        if (paName.startsWith("/"))
        {
            webapp.setContextRoot(paName);
        }
        else
        {
            webapp.setContextRoot("/" + paName);
        }
        webapp.addDisplayName(Jetspeed.getDefaultLocale(), paName);
        app.setWebApplicationDefinition(webapp);

        // save it to the registry
        log.info("Saving the portlet.xml in the registry...");

        try
        {

            registry.registerPortletApplication(app);
            log.info("Committing regsitry changes...");
            PersistenceStoreContainer pContainer = (PersistenceStoreContainer)Jetspeed.getComponentManager().getComponent(PersistenceStoreContainer.class);
            PersistenceStore store = pContainer.getStoreForThread("jetspeed");
            store.getTransaction().commit();            

        }
        catch (Exception e)
        {
            String msg =
                "Unable to register portlet application, " + app.getName() + ", through the portlet registry: " + e.toString();
            log.error(msg, e);
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
        try
        {

            // remove entries from the registry
            // registry.processPortletApplicationTree(app, "remove");
            log.info("Saving the portlet.xml in the registry...");
            registry.removeApplication(app);

        }

        catch (Exception e1)
        {

            log.error("Error processing rollback.  Attempting to rollback registry transaction.", e1);

        }
    }

}
