/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @version $Id$
 */
public class DeployPortletAppEventListener implements DeploymentEventListener
{

    protected static final Logger           log = LoggerFactory.getLogger("deployment");
    private String                       webAppDir;
    private int                           localPAPrefixLength;
    private String                       localAppDir;
    private String                       localAppStagingDir;
    private boolean                      stripLoggers;
    private PortletApplicationManagement pam;
    /**
     * @param pam
     * @param webAppDir
     * @param localAppDir
     * @param stripLoggers
     * @throws FileNotFoundException the <code>webAppDir</code> or <code>localAppDir</code> directory does not
     *                               exist.
     */
    public DeployPortletAppEventListener(PortletApplicationManagement pam, PortletRegistry registry, String webAppDir,
                                         String localAppDir, boolean stripLoggers) throws FileNotFoundException
    {
        this(pam,registry,webAppDir,localAppDir,null,stripLoggers);
    }
    /**
     * @param pam
     * @param webAppDir
     * @param localAppDir
     * @param localAppStagingDir
     * @param stripLoggers
     * @throws FileNotFoundException the <code>webAppDir</code> or <code>localAppDir</code> directory does not
     *                               exist.
     */
    public DeployPortletAppEventListener(PortletApplicationManagement pam, PortletRegistry registry, String webAppDir,
                                         String localAppDir, String localAppStagingDir, boolean stripLoggers) throws FileNotFoundException
    {
        this.pam = pam;
        this.stripLoggers = stripLoggers;
        localPAPrefixLength = PortletApplicationManagement.LOCAL_PA_PREFIX.length();

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
        if ( localAppStagingDir != null )
        {
            File localAppStagingDirFile = new File(localAppStagingDir);
            if ( !localAppStagingDirFile.exists() )
            {
                localAppStagingDirFile.mkdirs();
            }
            else if (!localAppStagingDirFile.isDirectory())
            {
                throw new FileNotFoundException("Invalid staging directory for local portlet applications: \""
                        + localAppStagingDirFile.getAbsolutePath());
            }
            try
            {
                this.localAppStagingDir = localAppStagingDirFile.getCanonicalPath();
            }
            catch (IOException e) {}
        }
    }

    protected String getWebAppDir()
    {
        return webAppDir;
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
    
    private String getEventParentPath(DeploymentEvent event)
    {
        try
        {
            return event.getDeploymentObject().getFile().getParentFile().getCanonicalPath();
        }
        catch (IOException io)
        {
            return null;
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
            if (localAppStagingDir != null && getEventParentPath(event).equals(localAppStagingDir))
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
            String fileName = event.getName();
            File toFile = new File(webAppDir, fileName);
            String contextName = fileName.substring(0, fileName.length() - 4); // strip ".war"
            new JetspeedDeploy(event.getPath(), toFile.getAbsolutePath(), contextName, stripLoggers);
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