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
import java.io.IOException;

// Registry class
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.services.registry.JetspeedPortletRegistry;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.exception.RegistryException;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;

import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.persistence.TransactionStateException;

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

    private static final String DEPLOYMENT_SYSTEM = "jetspeed-deploy";

    private String deploymentDbAlias;
    /**
     * Deploys the specified war file to the webapps dirctory specified.
     * 
     * @param webAppsDir The webapps directory inside the Application Server
     * @param warFile The warFile containing the Portlet Application
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */
    public void deploy(String webAppsDir, String warFile, String paName, String deploymentDbAlias)
        throws PortletApplicationException
    {
        this.deploymentDbAlias = deploymentDbAlias;

        // Call into DeplyUtilities class
        DeployUtilities util = new DeployUtilities();

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
            util.deployArchive(webAppsDir, warFile, paName);
            nState = 1;

            util.processWebXML(util.getWebXMLPath(webAppsDir, warFile, paName), paName);
            nState = 2;

            // Application is deployed -- populate the registry with the portlet.xml
            String portletXMLPath = webAppsDir + paName + "/WEB-INF/portlet.xml";

            // load the portlet.xml
            System.out.println("Loading " + portletXMLPath + " into memory....");
            app = (MutablePortletApplication) PortletDescriptorUtilities.loadPortletDescriptor(portletXMLPath, paName);

            if (app == null)
            {
                System.out.println("Error loading: " + portletXMLPath);
                rollback(nState, webAppsDir, paName, app);
                return;
            }
            if (webAppsDir.startsWith(JetspeedPortletContext.LOCAL_PA_ROOT))
            {
                app.setApplicationType(MutablePortletApplication.LOCAL);
            }
            else
            {
                app.setApplicationType(MutablePortletApplication.WEBAPP);                
            }
            // create the web application 
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

            //Test if application exists in registry
            //Uneeded as the registry service will do this for us.
            //            if ( JetspeedPortletRegistry.getPortletApplication(app.getName()) != null)
            //            {
            //                System.out.println("Application already exists in the database. Please undeploy the application : "
            //                      + app.getName());
            //                rollback(nState, webAppsDir, paName, app );
            //                return;
            //                   
            //            }

            // save it to the registry
            System.out.println("Saving the portlet.xml in the registry...");
            // locate the deployment home
            identifyDeploymentSystem();

            JetspeedPortletRegistry.registerPortletApplication(app, DEPLOYMENT_SYSTEM);
            nState = 3;

            // DONE
            System.out.println("FileSystem deployment done.");

        }
        catch (PortletApplicationException pae)
        {
            rollback(nState, webAppsDir, paName, app);
            throw new PortletApplicationException(pae.getMessage());
        }
        catch (RegistryException re)
        {
            rollback(nState, webAppsDir, paName, app);
            throw new PortletApplicationException(re.getMessage());
        }
        catch (IOException e)
        {
            rollback(nState, webAppsDir, paName, app);
            throw new PortletApplicationException(e.getMessage());
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            
        }

    }

    // Interface not supported by FileSystemPAM

    public void deploy(String warFile, String paName) throws PortletApplicationException
    {
        System.out.println("Not supported");
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
        String portletXMLPath = webAppsDir + paName + "/WEB-INF/portlet.xml";
        try
        {
            // Remove all registry entries
            // load the portlet.xml
            System.out.println("Loading " + portletXMLPath + " into memory....");
            identifyDeploymentSystem();
            MutablePortletApplication app = (MutablePortletApplication) JetspeedPortletRegistry.getPortletApplication(paName);
            // Application app = JetspeedPortletRegistry.loadPortletApplicationSettings(portletXMLPath, paName);

            if (app == null)
            {
                System.out.println("Error retrieving Application from Registry Database. Application not found: " + paName);
                return;
            }

            // remove entries from the registry
            System.out.println("Remove all registry entries defined in " + portletXMLPath);

            // JetspeedPortletRegistry.processPortletApplicationTree(app, "remove");
            // locate the deployment home
			JetspeedPortletRegistry.beginTransaction();
            JetspeedPortletRegistry.removeApplication(app);
			JetspeedPortletRegistry.commitTransaction();
            // Remove the webapps directory
            System.out.println("Remove " + webAppsDir + paName + " and all sub-directories.");

            // Call into DeplyUtilities class
            DeployUtilities util = new DeployUtilities();
            if (util.deleteDir(new File(webAppsDir + paName)) == false)
            {
                System.out.println(
                    "Failed to delete web app directory " + webAppsDir + " .Make sure the application is no longer running.");
            }

            // DONE
            System.out.println("FileSystem un-deployment done.");

        }
        catch (Exception re)
        {
			try
            {
                JetspeedPortletRegistry.rollbackTransaction();
            }
            catch (TransactionStateException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            throw new PortletApplicationException(re.getMessage());
        }

    }

    /**
     * Alters the deployment DB alias based on the value found in
     * the user's build.properties file.
     * @throws IOException
     */
    protected void identifyDeploymentSystem() throws IOException
    {
        String dbAliasOverride;
        if (this.deploymentDbAlias == null)
        {
            String userBuildFile = System.getProperty("user.home") + File.separator + "build.properties";
            Configuration buildProps = new PropertiesConfiguration(userBuildFile, "./build.properties");

            // See if the user has overriden the deployment alias
            dbAliasOverride = buildProps.getString("deployment.db.alias");
        }
        else
        {
            dbAliasOverride = this.deploymentDbAlias;
        }

        if (dbAliasOverride != null)
        {
            // Change the deployment location to match the user's build.properties
            JetspeedPortletRegistry.setDeploymentSystem("jetspeed-deploy", dbAliasOverride);
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
        System.out.println("Not supported");

    }

    private void rollback(int nState, String webAppsDir, String paName, MutablePortletApplication app)
    {
        System.out.println("Exception in deploy. Rollback of application deployment...");

        try
        {
            if (nState >= 2 && app != null)
            {
                // remove entries from the registry
                // JetspeedPortletRegistry.processPortletApplicationTree(app, "remove");
                System.out.println("Saving the portlet.xml in the registry...");
                // locate the deployment home
                identifyDeploymentSystem();
				JetspeedPortletRegistry.beginTransaction();
                JetspeedPortletRegistry.removeApplication(app);
				JetspeedPortletRegistry.commitTransaction();
            }

        }
        catch (Exception e1)
        {
			try
            {
                JetspeedPortletRegistry.rollbackTransaction();
            }
            catch (TransactionStateException e2)
            {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            e1.printStackTrace();
        }

        try
        {

            if (nState >= 1)
            {
                // Remove the webapps directory
                System.out.println("Rollback: Remove " + webAppsDir + paName + " and all sub-directories.");

                // Call into DeplyUtilities class
                DeployUtilities util = new DeployUtilities();
                if (util.deleteDir(new File(webAppsDir + paName)) == false)
                {
                    System.out.println(
                        "Rollback: Failed to delete web app directory "
                            + webAppsDir
                            + " .Make sure the application is no longer running.");
                }
            }
        }
        catch (Exception e)
        {
            return;
        }
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
        deploy(webAppsDir, warFile, paName, null);

    }

}
