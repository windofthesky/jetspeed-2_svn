/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventListener;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.DeploymentStatus;
import org.apache.jetspeed.tools.deploy.JetspeedDeploy;
import org.apache.jetspeed.tools.pamanager.PortletApplicationManagement;
import org.apache.jetspeed.util.DirectoryHelper;

/**
 * <p>
 * DeployportletAppEventListener
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: DeployPortletAppEventListener.java 188608 2005-05-24 18:54:01Z ate $
 */
public class DeployPortletAppEventListener implements DeploymentEventListener
{

    protected static final Log           log = LogFactory.getLog("deployment");
    private String                       webAppDir;
    private String                       localAppDir;
    private boolean                      stripLoggers;
    private PortletApplicationManagement pam;
    private PortletRegistry              registry;

    /**
     * @param pam
     * @param webAppDir
     * @param localAppDir
     * @throws FileNotFoundException the <code>webAppDir</code> or <code>localAppDir</code> directory does not
     *                               exist.
     */
    public DeployPortletAppEventListener(PortletApplicationManagement pam, PortletRegistry registry, String webAppDir,
                                         String localAppDir, boolean stripLoggers) throws FileNotFoundException
    {
        this.pam = pam;
        this.registry = registry;
        this.stripLoggers = stripLoggers;

        File webAppDirFile = new File(webAppDir);

        if (webAppDirFile.exists())
        {
            try
            {
                this.webAppDir = webAppDirFile.getCanonicalPath();
            }
            catch (IOException e) {}
        }
        else
        {
            throw new FileNotFoundException("The depoyment directory for portlet applications \""
                                            + webAppDirFile.getAbsolutePath() + "\" does not exist.");
        }
        File localAppDirFile = new File(localAppDir);

        if (!localAppDirFile.exists())
        {
            localAppDirFile.mkdirs();
        }
        else if (!localAppDirFile.isDirectory())
        {
            throw new FileNotFoundException("Invalid depoyment directory for local portlet applications: \""
                                            + localAppDirFile.getAbsolutePath());
        }
        try
        {
            this.localAppDir = localAppDirFile.getCanonicalPath();
        }
        catch (IOException e) {}
    }

    public void initialize()
    {
        // start deployed local pa
        File[] localApps = new File(localAppDir).listFiles(new FileFilter()
        {
            public boolean accept(File pathname)
            {
                return pathname.isDirectory();
            }
        });
        for (int i = 0; i < localApps.length; i++)
        {
            // Check for at least WEB-INF/portlet.xml
            // This will also prevent the src/webapps/WEB-INF/apps/CVS folder
            // to be seen as local app from testcases resulting in an exception
            if ( ! new File(localApps[i],"WEB-INF/portlet.xml").exists() )
            {
                log.warn("Not a local application " + localApps[i].getName());
            }
            else
            {
                DirectoryHelper paDirHelper = new DirectoryHelper(localApps[i]);
                try
                {
                    pam.startLocalPortletApplication(localApps[i].getName(), paDirHelper,
                                                     createLocalPAClassLoader(localApps[i]));
                }
                catch (Exception e)
                {
                    log.error("Failed to start Local Portlet Application " + localApps[i], e);
                }
            }
        }
    }

    /**
     * <p>
     * invokeDeploy
     * </p>
     * 
     * @param event
     * @throws DeploymentException
     */
    public void invokeDeploy(DeploymentEvent event) throws DeploymentException
    {
        String fileName = event.getName();
        if (fileName.endsWith(".war"))
        {
            int prefixLength = PortletApplicationManagement.LOCAL_PA_PREFIX.length();
            if (fileName.length() > prefixLength
                && fileName.substring(0, prefixLength).equalsIgnoreCase(PortletApplicationManagement.LOCAL_PA_PREFIX))
            {
                deployLocalPortletApplication(event);
            }
            else
            {
                deployPortletApplication(event);
            }
        }
    }

    protected void deployPortletApplication(DeploymentEvent event) throws DeploymentException
    {
        try
        {
            File toFile = new File(webAppDir, event.getName());
            new JetspeedDeploy(event.getPath(), toFile.getAbsolutePath(), stripLoggers);
            event.setStatus(DeploymentStatus.STATUS_OKAY);
        }
        catch (Exception e)
        {
            throw new DeploymentException(e);
        }
    }

    protected void deployLocalPortletApplication(DeploymentEvent event) throws DeploymentException
    {
        try
        {
            String fileName = event.getName();
            String appName = fileName.substring(0, fileName.length() - 4);
            pam.stopLocalPortletApplication(appName);
            File targetDir = new File(localAppDir, appName);
            JarExpander.expand(event.getDeploymentObject().getFile(), targetDir);
            DirectoryHelper paDirHelper = new DirectoryHelper(targetDir);
            pam.startLocalPortletApplication(appName, paDirHelper, createLocalPAClassLoader(targetDir));
            event.setStatus(DeploymentStatus.STATUS_OKAY);
        }
        catch (Exception e)
        {
            throw new DeploymentException(e);
        }
    }

    protected ClassLoader createLocalPAClassLoader(File paDir) throws IOException
    {
        ArrayList urls = new ArrayList();
        File webInfClasses = null;

        webInfClasses = new File(paDir, ("WEB-INF/classes/"));
        if (webInfClasses.exists())
        {
            log.info("Adding " + webInfClasses.toURL() + " to class path for Local PA " + paDir.getName());
            urls.add(webInfClasses.toURL());
        }

        File webInfLib = new File(paDir, "WEB-INF/lib");

        if (webInfLib.exists())
        {
            File[] jars = webInfLib.listFiles();

            for (int i = 0; i < jars.length; i++)
            {
                File jar = jars[i];
                log.info("Adding " + jar.toURL() + " to class path for Local PA " + paDir.getName());
                urls.add(jar.toURL());
            }
        }
        return new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
    }

}