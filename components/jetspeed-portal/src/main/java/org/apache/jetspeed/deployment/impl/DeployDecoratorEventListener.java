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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventListener;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.DeploymentStatus;
import org.apache.jetspeed.util.DirectoryHelper;

/**
 * <p>
 * DirectFolderEventListener
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 */
public class DeployDecoratorEventListener implements DeploymentEventListener
{
    protected static final Logger log = LoggerFactory.getLogger("deployment");
    protected String           deployToDir;

    public DeployDecoratorEventListener(String deployToDir) throws FileNotFoundException
    {
        File checkFile = new File(deployToDir);
        if (checkFile.exists())
        {
            try
            {
                this.deployToDir = checkFile.getCanonicalPath();
            }
            catch (IOException e) {}
        }
        else
        {
            throw new FileNotFoundException("The deployment directory, " + checkFile.getAbsolutePath()
                                            + ", does not exist");
        }
    }

    public void initialize()
    {
    // nothing to do
    }

    /**
     * <p>
     * invokeDeploy
     * </p>
     * 
     * @see org.apache.jetspeed.deployment.DeploymentEventListener#invokeDeploy(org.apache.jetspeed.deployment.DeploymentEvent)
     * @param event
     * @throws DeploymentException
     */
    public void invokeDeploy(DeploymentEvent event) throws DeploymentException
    {
        String fileName = event.getName();
        if (!fileName.endsWith(".jar") && !fileName.endsWith(".zip"))
        {
            return;
        }

        // get decorator configuration if available
        PropertiesConfiguration conf = getDecoratorConfiguration(event);
        // silently return if configuration not available, (assumes
        // probably not a decorator)
        if (conf == null)
        {
            return;
        }

        // process decorator by id
        String id = conf.getString("id");
        if (id == null)
        {
            throw new DeploymentException("Unable to deploy decorator, \"id\" attribute not defined in configuration");
        }
        
        log.info("Found decorator deployment archive " + id);

        try
        {
            // construct decorator deploy path
            String baseDeployPath = getBaseDeployPath(conf);
            String deployPath = baseDeployPath + File.separator + id;
            File deployPathFile = new File(deployPath);

            // undeploy decorator if it already exists and is a redeploy or
            // skip deployment if initial deployment
            if (deployPathFile.exists())
            {
                invokeUndeploy(deployPathFile);
            }

            // redeploy/deploy decorator w/o META_INF jar metadata
            log.info("Deploying decorator " + id + " to " + deployPath);
            JarExpander.expand(event.getDeploymentObject().getFile(), deployPathFile);
            File metaInf = new File(deployPathFile, "META-INF");
            if (metaInf.exists())
            {
                DirectoryHelper cleanup = new DirectoryHelper(metaInf);
                cleanup.remove();
                cleanup.close();
            }

            // detect language/country localized decorator components
            final List localeSpecificDeployPathsList = getLocaleSpecificDeployPaths(deployPathFile);

            // deploy individual locale specific decorator components
            Iterator deployPathsIter = localeSpecificDeployPathsList.iterator();
            while (deployPathsIter.hasNext())
            {
                File localeDeployPathFile = (File) deployPathsIter.next();

                // deploy to locale specific location
                File deployToPathFile = new File(baseDeployPath
                                                 + localeDeployPathFile.getPath().substring(deployPath.length())
                                                 + File.separator + id);
                log.info("Deploying locale specific decorator component to " + deployToPathFile.getPath());
                deployToPathFile.mkdirs();

                // deploy decorator components by moving from deployed decorator
                File[] filesToDeploy = localeDeployPathFile.listFiles(new FileFilter()
                {
                    public boolean accept(File pathname)
                    {
                        return !localeSpecificDeployPathsList.contains(pathname);
                    }
                });
                for (int i = 0; (i < filesToDeploy.length); i++)
                {
                    filesToDeploy[i].renameTo(new File(deployToPathFile, filesToDeploy[i].getName()));
                }
            }

            // cleanup locale specific deployment directories
            Iterator cleanupDeployPathsIter = localeSpecificDeployPathsList.iterator();
            while (cleanupDeployPathsIter.hasNext())
            {
                File cleanupLocaleDeployPathFile = (File) cleanupDeployPathsIter.next();
                if (cleanupLocaleDeployPathFile.exists())
                {
                    DirectoryHelper cleanup = new DirectoryHelper(cleanupLocaleDeployPathFile);
                    cleanup.remove();
                    cleanup.close();
                }
            }

            log.info("Decorator " + id + " deployed successfuly.");
            event.setStatus(DeploymentStatus.STATUS_OKAY);
        }
        catch (DeploymentException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DeploymentException("Error deploying decorator " + id, e);
        }
    }

    /**
     * <p>
     * invokeUndeploy
     * </p>
     * 
     * @throws DeploymentException
     */
    public void invokeUndeploy(File deployPathFile) throws DeploymentException
    {

        if (deployPathFile == null || !deployPathFile.exists() ||
            !deployPathFile.isDirectory() || deployPathFile.getParentFile() == null ||
            !deployToDir.equals(deployPathFile.getParentFile().getParent()))
        {
            throw new DeploymentException("Cannot undeploy decorator at " + deployPathFile + ": invalid decorator path");
        }

        String id = deployPathFile.getName();

        try
        {
            // undeploy decorator
            log.info("Undeploying decorator " + id + " at " + deployPathFile.getAbsolutePath());

            // detect language/country localized decorator components
            final List localeSpecificDeployPathsList = getLocaleSpecificDeployPaths(deployPathFile.getParentFile());

            // undeploy individual locale specific decorator components depth first
            for (int i = localeSpecificDeployPathsList.size() - 1; i > -1; i--)
            {
                File localeDeployPathFile = new File((File) localeSpecificDeployPathsList.get(i), id);
                if (localeDeployPathFile.exists())
                {
                    log.info("Undeploying locale specific decorator component at " + localeDeployPathFile.getPath());
                    DirectoryHelper cleanup = new DirectoryHelper(localeDeployPathFile);
                    cleanup.remove();
                    cleanup.close();
                    localeDeployPathFile.getParentFile().delete();
                }
            }

            // now undeploy the decorator root itself
            DirectoryHelper cleanup = new DirectoryHelper(deployPathFile);
            cleanup.remove();
            cleanup.close();

            log.info("Decorator " + id + " undeployed successfuly.");
        }
        catch (Exception e)
        {
            throw new DeploymentException("Error undeploying decorator " + id, e);
        }
    }

    /**
     * <p>
     * getDecorationConfiguration
     * </p>
     * 
     * @param event @
     * @return configuration
     * @throws DeploymentException
     */
    private PropertiesConfiguration getDecoratorConfiguration(DeploymentEvent event) throws DeploymentException
    {
        InputStream stream = null;
        try
        {
            if (event.getDeploymentObject() == null)
            {
                return null;
            }
            stream = event.getDeploymentObject().getConfiguration("decorator.properties");
            if (stream == null)
            {
                return null;
            }
            else
            {
                PropertiesConfiguration configuration = new PropertiesConfiguration();
                configuration.load(stream);
                return configuration;
            }
        }
        catch (Exception e1)
        {
            throw new DeploymentException("Error reading decorator.properties from " + event.getPath(), e1);
        }
        finally
        {
            if (stream != null)
            {
                try
                {
                    stream.close();
                }
                catch (IOException e)
                {

                }
            }
        }
    }

    /**
     * <p>
     * getBaseDeployPath
     * </p>
     * 
     * @param configuration
     * @return base deploy path
     */
    private String getBaseDeployPath(PropertiesConfiguration configuration)
    {
        // construct decorator deploy base path
        String layoutType = configuration.getString("decorates", "generic");
        if (layoutType.equalsIgnoreCase("any"))
        {
            layoutType = "generic";
        }
        return deployToDir + File.separator + layoutType ;
    }

    /**
     * <p>
     * getLocaleSpecificDeployPaths
     * </p>
     * 
     * @param rootPath
     * @return locale paths list
     */
    private List getLocaleSpecificDeployPaths(File rootPath)
    {
        // detect language/country localized deploy paths
        List localeSpecificDeployPathsList = new ArrayList();
        File[] localeLanguageSpecificRoots = rootPath.listFiles(new FileFilter()
        {
            public boolean accept(File pathname)
            {
                // filter language code dirs, (assume length test is accurate enough)
                return (pathname.isDirectory() && (pathname.getName().length() == 2));
            }
        });
        for (int i = 0; (i < localeLanguageSpecificRoots.length); i++)
        {
            localeSpecificDeployPathsList.add(localeLanguageSpecificRoots[i]);
            File[] localeCountrySpecificPaths = localeLanguageSpecificRoots[i].listFiles(new FileFilter()
            {
                public boolean accept(File pathname)
                {
                    // filter country code dirs, (assume length test is accurate enough)
                    return (pathname.isDirectory() && (pathname.getName().length() == 2));
                }
            });
            for (int j = 0; (j < localeCountrySpecificPaths.length); j++)
            {
                localeSpecificDeployPathsList.add(localeCountrySpecificPaths[j]);
            }
        }
        return localeSpecificDeployPathsList;
    }
}