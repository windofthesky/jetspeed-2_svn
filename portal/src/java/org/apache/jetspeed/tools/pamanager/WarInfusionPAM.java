/*
 * Copyright 2000-2005 The Apache Software Foundation.
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.PortletCache;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.exception.RegistryException;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.tools.deploy.Deploy;
import org.apache.jetspeed.tools.deploy.DeployFactory;
import org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;

/**
 * This is a test implementation of the deploy-tool based WAR file infusion
 * using JetspeedContainerServlet registration to deploy and undeploy Portlet
 * Applications.
 * 
 * @version $Id$
 */

public class WarInfusionPAM extends FileSystemPAM implements PortletApplicationManagement, DeploymentRegistration
{
    protected static final Log log = LogFactory.getLog("deployment");

    private File tempDirectory;
    private boolean deployExpandedWars;
    private DeployFactory deployFactory;

    /**
     * WarInfusionPAM
     *
     * @param webAppsDir
     * @param registry
     * @param entityAccess
     * @param windowAccess
     * @param portletCache
     * @param portletFactory
     * @param deployFactory
     */
    public WarInfusionPAM(String webAppsDir, PortletRegistry registry, PortletEntityAccessComponent entityAccess, PortletWindowAccessor windowAccess, PortletCache portletCache, PortletFactory portletFactory, DeployFactory deployFactory)
    {
        // construct base FileSystemPAM implementation
        super(webAppsDir, registry, entityAccess, windowAccess, portletCache, portletFactory, new ApplicationServerManagerStub());

        // deploy factory component
        this.deployFactory = deployFactory;

        // configure temporary directory used in deployment
        this.tempDirectory = new File(System.getProperty("java.io.tmpdir"), "jetspeed-WarInfusionPAM");
        if (!this.tempDirectory.isDirectory())
        {
            this.tempDirectory.mkdirs();
        }
        this.tempDirectory.deleteOnExit();

        // determine if other context.xml files exist in
        // deployment directory, (this assumes that jetspeed
        // itself will deploy with a context file), to
        // choose expanded or war file deployment
        File [] deployedFiles = (new File(webAppsDir)).listFiles();
        for (int i = 0; ((i < deployedFiles.length) && !this.deployExpandedWars); i++)
        {
            String deployedFileName = deployedFiles[i].getName();
            if (deployedFileName.endsWith(".xml"))
            {
                String deployedWebAppName = deployedFileName.substring(0, deployedFileName.length() - 4);
                for (int j = 0; ((j < deployedFiles.length) && !this.deployExpandedWars); j++)
                {
                    if (deployedFiles[j].getName().equals(deployedWebAppName))
                    {
                        this.deployExpandedWars = true;
                    }
                }
            }
        }
    }

    /**
     * WarInfusionPAM
     *
     * @param webAppsDir
     * @param registry
     * @param entityAccess
     * @param windowAccess
     * @param portletCache
     * @param portletFactory
     * @param deployExpandedWars
     */
    public WarInfusionPAM(String webAppsDir, PortletRegistry registry, PortletEntityAccessComponent entityAccess, PortletWindowAccessor windowAccess, PortletCache portletCache, PortletFactory portletFactory, DeployFactory deployFactory, boolean deployExpandedWars)
    {
        // construct base WarInfusionPAM implementation,
        // overriding deploy expanded wars configuration
        this(webAppsDir, registry, entityAccess, windowAccess, portletCache, portletFactory, deployFactory);
        this.deployExpandedWars = deployExpandedWars;
    }

    /**
     * Registers a portlet app deployed at the specified file system location
     * 
     * @see org.apache.jetspeed.tools.pamanager.PortletApplicationManagement#registerPortletApplication(org.apache.jetspeed.util.FileSystemHelper, java.lang.String)
     * @param fileSystem The deployed portlet application location
     * @param paName The name of the portlet application to be registered
     * @throws RegistryException
     */
    public boolean registerPortletApplication(FileSystemHelper fileSystem, String paName) throws RegistryException
    {
        // cross context invocation expected: explicitly manage class loaders
        ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader thisClassLoader = getClass().getClassLoader();
        try
        {
            // localize class loader
            if (threadClassLoader != thisClassLoader)
            {
                Thread.currentThread().setContextClassLoader(thisClassLoader);
            }

            // invoke base FileSystemPAM implementation
            return super.registerPortletApplication(fileSystem, paName);
        }
        finally
        {
            // restore class loader
            if (threadClassLoader != thisClassLoader)
            {
                Thread.currentThread().setContextClassLoader(threadClassLoader);
            }
        }
    }
    
    /**
     * Registers a deployed portlet app
     * 
     * @see org.apache.jetspeed.tools.pamanager.PortletApplicationManagement#registerPortletApplication(java.lang.String, java.lang.String, javax.servlet.ServletContext)
     * @param paName The name of the portlet application to be registered
     * @param contextName The deployed portlet context name
     * @param context The deployed portlet servlet context
     * @throws RegistryException
     */
    public boolean registerPortletApplication(String paName, String contextName, ServletContext context) throws RegistryException
    {
        // cross context invocation expected: explicitly manage class loaders
        ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader thisClassLoader = getClass().getClassLoader();
        try
        {
            // localize class loader
            if (threadClassLoader != thisClassLoader)
            {
                Thread.currentThread().setContextClassLoader(thisClassLoader);
            }

            // invoke base FileSystemPAM implementation
            return super.registerPortletApplication(paName, contextName, context);
        }
        finally
        {
            // restore class loader
            if (threadClassLoader != thisClassLoader)
            {
                Thread.currentThread().setContextClassLoader(threadClassLoader);
            }
        }
    }

    /**
     * Deploys the specified war file to the webapps directory specified.
     * 
     * @see org.apache.jetspeed.tools.pamanager.Deployment#deploy(org.apache.jetspeed.util.descriptor.PortletApplicationWar)
     * @param paWar PortletApplicationWar to deploy
     * @throws PortletApplicationException
     */
    public void deploy(PortletApplicationWar paWar) throws PortletApplicationException
    {
        // invoke base FileSystemPAM implementation
        super.deploy(paWar);
    }
    
    /**
     * Undeploys application.
     * 
     * @see org.apache.jetspeed.tools.pamanager.Deployment#undeploy(org.apache.jetspeed.util.descriptor.PortletApplicationWar)
     * @param paWar PortletApplicationWar to undeploy
     * @throws PortletApplicationException
     */
    public void undeploy(PortletApplicationWar paWar) throws PortletApplicationException
    {
        // invoke base FileSystemPAM implementation
        super.undeploy(paWar);

        // undeploy portlet app from webapps container
        // deployment directory as atomically as possible
        String paName = paWar.getPortletApplicationName();
        File deployPortletAppDirectory = new File(webAppsDir + "/" + paName);
        File deployPortletAppWarFile = new File(webAppsDir + "/" + paName + ".war");
        File deployPortletAppContextFile = new File(webAppsDir + "/" + paName + ".xml");
        deployPortletAppWarFile.delete();
        deployPortletAppContextFile.delete();
        deleteDirectory(deployPortletAppDirectory);
        if (!deployPortletAppContextFile.exists() && !deployPortletAppDirectory.exists() && !deployPortletAppWarFile.exists())
        {
            log.error("Portlet application undeployment of " + deployPortletAppWarFile.getAbsolutePath() + ", " + deployPortletAppContextFile.getAbsolutePath() + ", and/or " + deployPortletAppDirectory.getAbsolutePath() + " complete.");
        }
        else
        {
            log.error("Portlet application undeployment of " + deployPortletAppWarFile.getAbsolutePath() + ", " + deployPortletAppContextFile.getAbsolutePath() + ", and/or " + deployPortletAppDirectory.getAbsolutePath() + " failed, unable to undeploy.");
        }
    }
        
    /**
     * Redeploys application.
     * 
     * @see org.apache.jetspeed.tools.pamanager.Deployment#redeploy(org.apache.jetspeed.util.descriptor.PortletApplicationWar)
     * @param paWar PortletApplicationWar to undeploy
     * @throws PortletApplicationException
     */
    public void redeploy(PortletApplicationWar paWar) throws PortletApplicationException
    {
        // invoke base FileSystemPAM implementation
        super.redeploy(paWar);
    }

    /**
     * Get deployment path for a portlet application.
     * 
     * @see org.apache.jetspeed.tools.pamanager.Deployment#getDeploymentPath(java.lang.String)
     * @param paName  The name of the portlet application to locate
     * @throws PortletApplicationException
     */
    public String getDeploymentPath(String paName)
    {
        // invoke base FileSystemPAM implementation
        return super.getDeploymentPath(paName);
    }

    /**
     * Register a portlet application into the registry.
     * 
     * @see org.apache.jetspeed.tools.pamanager.Registration#register(org.apache.jetspeed.util.descriptor.PortletApplicationWar)
     * @param paWar PortletApplicationWar to register
     * @throws PortletApplicationException
     */
    public void register(PortletApplicationWar paWar) throws PortletApplicationException
    {
        // invoke base FileSystemPAM implementation
        super.register(paWar);
    }
        
    /**
     * Unregister a portlet application from the registry.
     * 
     * @see org.apache.jetspeed.tools.pamanager.Registration#unregister(java.lang.String)
     * @param paName The name of the portlet application to be unregistered
     * @throws PortletApplicationException
     */
    public void unregister(String paName) throws PortletApplicationException
    {
        // invoke base FileSystemPAM implementation
        super.unregister(paName);
    }

    /**
     * FileSystemPAM deployment protocol implementation: infuse
     * JetspeedContainerServlet and other edits into war file before
     * deploying and rely on servlet to register application asynchronously
     * 
     * @param paWar PortletApplicationWar to deploy
     * @param startState deployment state
     * @throws PortletApplicationException
     */
    protected void sysDeploy(PortletApplicationWar paWar, int startState) throws PortletApplicationException
    {
        int nState = 0;
        MutablePortletApplication app = null;
        String paName = paWar.getPortletApplicationName();
        try
        {
            // stepwise deployment states:
            // 0 Initial state
            // 1 Archive deployed
            // 2 WEB XML updated
            // 3 Registry updated

            // infuse and deploy portlet app war if required
            nState = DEPLOY_WAR;
            if (startState <= nState)
            {
                // infuse and copy portlet app war file to container
                // webapps directory
                File sourcePortletAppWarFile = paWar.getFileSystem().getRootDirectory();
                File deployPortletAppDirectory = new File(webAppsDir + "/" + paName);
                File deployPortletAppWarFile = new File(webAppsDir + "/" + paName + ".war");
                File deployPortletAppContextFile = new File(webAppsDir + "/" + paName + ".xml");
                log.info("Portlet application deploying from " + sourcePortletAppWarFile.getAbsolutePath() + " to " + deployPortletAppWarFile.getAbsolutePath() + " ...");
                File tempSourcePortletAppWarFile = null;
                String sourcePortletAppWar = null;
                if (sourcePortletAppWarFile.isDirectory())
                {
                    // war file is expanded directory, create temporary war file
                    tempSourcePortletAppWarFile = new File(tempDirectory, sourcePortletAppWarFile.getName() + ".jar-" + getUniqueId());
                    try
                    {
                        Map warFiles = new HashMap();
                        List warFilePaths = new ArrayList();
                        File [] rootWarFiles = sourcePortletAppWarFile.listFiles();
                        for (int i = 0; (i < rootWarFiles.length); i++)
                        {
                            addToWarFile(rootWarFiles[i], "", warFiles, warFilePaths); 
                        }

                        Manifest tempWarManifest = null;
                        File tempWarManifestFile = (File) warFiles.get("META-INF/MANIFEST.MF");
                        if (tempWarManifestFile != null)
                        {
                            tempWarManifest = new Manifest(new FileInputStream(tempWarManifestFile));
                        }
                        else
                        {
                            tempWarManifest = new Manifest();
                        }
                        warFiles.remove("META-INF/MANIFEST.MF");
                        warFilePaths.remove("META-INF/MANIFEST.MF");

                        JarOutputStream tempWar = new JarOutputStream(new FileOutputStream(tempSourcePortletAppWarFile), tempWarManifest);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        Iterator filePathsIter = warFilePaths.iterator();
                        while (filePathsIter.hasNext())
                        {
                            String path = (String)filePathsIter.next();
                            JarEntry entry = new JarEntry(path);
                            File file = (File)warFiles.get(path);
                            if (file != null)
                            {
                                entry.setSize(file.length());
                                entry.setTime(file.lastModified());
                                tempWar.putNextEntry(entry);
                                FileInputStream read = new FileInputStream(file);
                                while ((bytesRead = read.read(buffer)) != -1)
                                {
                                    tempWar.write(buffer, 0, bytesRead);
                                }
                                read.close();
                            }
                            else
                            {
                                tempWar.putNextEntry(entry);
                            }
                            tempWar.closeEntry();
                        }
                        tempWar.close();
                    }
                    catch (Exception e)
                    {
                        log.error("Unable to create temporary war file", e);
                    }
                    sourcePortletAppWar = tempSourcePortletAppWarFile.getAbsolutePath();
                }
                else
                {
                    sourcePortletAppWar = sourcePortletAppWarFile.getAbsolutePath();
                }

                // infuse JetspeedContainerServlet web.xml, portlet.xml,
                // portlet.tld, and context.xml into portlet app war file;
                // note that name of source file must match name of
                // destination war
                File tempDeployPortletAppWarFile = new File(tempDirectory, deployPortletAppWarFile.getName() + "-" + getUniqueId());
                Deploy warInfuser = deployFactory.getInstance(sourcePortletAppWar, tempDeployPortletAppWarFile.getAbsolutePath(), true);
                if (tempSourcePortletAppWarFile != null)
                {
                    tempSourcePortletAppWarFile.delete();
                }

                // extract context.xml and portlet app directory for deployment
                // if deploying expanded war files
                File tempDeployPortletAppContextFile = null;
                File tempDeployPortletAppDirectory = null;                
                if (deployExpandedWars)
                {
                    // expand portlet app directory
                    tempDeployPortletAppDirectory = new File(tempDirectory, deployPortletAppDirectory.getName() + "-" + getUniqueId());
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    JarInputStream tempWar = new JarInputStream(new FileInputStream(tempDeployPortletAppWarFile));
                    JarEntry entry = null;
                    while ((entry = tempWar.getNextJarEntry()) != null) 
                    {
                        File writeFile = new File(tempDeployPortletAppDirectory, entry.getName());
                        if (!entry.getName().endsWith("/"))
                        {
                            writeFile.getParentFile().mkdirs();
                            FileOutputStream write = new FileOutputStream(writeFile);
                            while ((bytesRead = tempWar.read(buffer)) != -1)
                            {
                                write.write(buffer, 0, bytesRead);
                            }
                            write.close();
                        }
                        else
                        {
                            writeFile.mkdirs();
                        }
                    }
                    tempWar.close();

                    // copy portlet app context.xml
                    File contextXmlFile = new File(tempDeployPortletAppDirectory, "META-INF/context.xml");
                    if (contextXmlFile.exists())
                    {
                        tempDeployPortletAppContextFile = new File(tempDirectory, deployPortletAppContextFile.getName() + "-" + getUniqueId());
                        FileInputStream read = new FileInputStream(contextXmlFile);
                        FileOutputStream copy = new FileOutputStream(tempDeployPortletAppContextFile);
                        while ((bytesRead = read.read(buffer)) != -1)
                        {
                            copy.write(buffer, 0, bytesRead);
                        }
                        copy.close();
                        read.close();
                    }
                        
                    // delete expanded portlet app war file
                    tempDeployPortletAppWarFile.delete();
                    tempDeployPortletAppWarFile = null;
                }

                // undeploy any existing artifacts; move new portlet app
                // context and expanded war file or war file into webapps
                // container deployment directory as atomically as possible
                if (tempDeployPortletAppWarFile != null)
                {
                    // undeploy and deploy portlet app war file
                    deployPortletAppWarFile.delete();
                    if (!deployPortletAppWarFile.exists())
                    {
                        tempDeployPortletAppWarFile.renameTo(deployPortletAppWarFile);
                        if (deployPortletAppWarFile.exists())
                        {
                            log.info("Portlet application deployment of " + deployPortletAppWarFile.getAbsolutePath() + " complete.");
                        }
                        else
                        {
                            log.error("Portlet application deployment of " + deployPortletAppWarFile.getAbsolutePath() + " failed, unable to deploy.");
                        }
                    }
                    else
                    {
                        log.error("Portlet application deployment of " + deployPortletAppWarFile.getAbsolutePath() + " failed, unable to undeploy.");
                    }
                }
                else if ((tempDeployPortletAppContextFile != null) && (tempDeployPortletAppDirectory != null))
                {
                    // undeploy and deploy portlet app context.xml and expanded war file directory
                    deployPortletAppContextFile.delete();
                    deleteDirectory(deployPortletAppDirectory);
                    if (!deployPortletAppContextFile.exists() && !deployPortletAppDirectory.exists())
                    {
                        tempDeployPortletAppContextFile.renameTo(deployPortletAppContextFile);
                        tempDeployPortletAppDirectory.renameTo(deployPortletAppDirectory);
                        if (deployPortletAppContextFile.exists() && deployPortletAppDirectory.exists())
                        {
                            log.info("Expanded portlet application deployment of " + deployPortletAppContextFile.getAbsolutePath() + " and " + deployPortletAppDirectory.getAbsolutePath() + " complete.");
                        }
                        else
                        {
                            deployPortletAppContextFile.delete();
                            deleteDirectory(deployPortletAppDirectory);
                            log.error("Expanded portlet application deployment of " + deployPortletAppContextFile.getAbsolutePath() + " and " + deployPortletAppDirectory.getAbsolutePath() + " failed, unable to deploy.");
                        }
                    }
                    else
                    {
                        log.error("Portlet application deployment of " + deployPortletAppContextFile.getAbsolutePath() + " and " + deployPortletAppDirectory.getAbsolutePath() + " failed, unable to undeploy.");
                    }
                }
                else if (tempDeployPortletAppDirectory != null)
                {
                    // undeploy and deploy portlet app expanded war file directory
                    deleteDirectory(deployPortletAppDirectory);
                    if (!deployPortletAppDirectory.exists())
                    {
                        tempDeployPortletAppDirectory.renameTo(deployPortletAppDirectory);
                        if (deployPortletAppDirectory.exists())
                        {
                            log.info("Expanded portlet application deployment of " + deployPortletAppDirectory.getAbsolutePath() + " complete.");
                        }
                        else
                        {
                            log.error("Expanded portlet application deployment of " + deployPortletAppDirectory.getAbsolutePath() + " failed, unable to deploy.");
                        }
                    }
                    else
                    {
                        log.error("Portlet application deployment of " + deployPortletAppDirectory.getAbsolutePath() + " failed, unable to undeploy.");
                    }
                }

                // do not complete registration synchronously: infused
                // JetspeedContainerServlet will register when webapp
                // is auto/live deployed in container.
                return;
            }

            // register portlet app if required
            nState = UPDATE_REGISTRY;
            if (startState <= nState)
            {
                // register portlet app in place
                log.info("Portlet application registration target is " + paWar.getPortletApplicationName() + " ...");
                registerApplication(paWar);
                app = (MutablePortletApplication) registry.getPortletApplication(paName);
                log.info("Portlet application registration of " + paWar.getPortletApplicationName() + " complete.");
            }
        }
        catch (PortletApplicationException pae)
        {
            log.error("PortletApplicationException encountered deploying portlet application: " + pae.toString() + " attempting rollback...", pae);
            rollback(nState, paWar, app);
            throw pae;
        }
        catch (Throwable t)
        {
            log.error("Unexpected exception deploying portlet application: " + t.toString() + " attempting rollback...", t);
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

    /**
     * FileSystemPAM deployment protocol implementation: drop class loader
     * 
     * @param paName portlet app name to unregister
     * @param purgeEntityInfo delete flag
     * @throws PortletApplicationException
     */
    protected void doUnregister(String paName, boolean purgeEntityInfo) throws PortletApplicationException
    {
        // invoke base FileSystemPAM implementation
        super.doUnregister(paName, purgeEntityInfo);
    }

    /**
     * deleteDirectory
     *
     * @param directory
     */
    private boolean deleteDirectory(File directory)
    {
        // move to tempDirectory if possible and delete
        if (directory.isDirectory())
        {
            File delete = new File(tempDirectory, directory.getName() + ".delete-" + getUniqueId());
            if (directory.renameTo(delete))
            {
                return (new DirectoryHelper(delete)).remove();
            }
            return (new DirectoryHelper(directory)).remove();
        }
        return directory.delete();
    }

    /**
     * getUniqueId - used for temporary file creation
     *
     * @return new unique id
     */
    private static long uniqueId = System.currentTimeMillis();
    private synchronized static long getUniqueId()
    {
        return uniqueId++;
    }

    /**
     * addToWarFile - recursively add a file system directory
     * of files to a list and map of File objects to be added
     * to a war file archive
     *
     * @param file
     * @param path
     * @param warFiles
     * @param warFilePaths
     */
    private static void addToWarFile(File file, String path, Map warFiles, List warFilePaths)
    {
        if (file.isDirectory())
        {
            String dirPath = path + file.getName() + "/" ;
            File [] files = file.listFiles();
            for (int i = 0; (i < files.length); i++)
            {
                addToWarFile(files[i], dirPath, warFiles, warFilePaths); 
            }
            warFilePaths.add(dirPath);
        }
        else
        {
            String filePath = path + file.getName();
            warFiles.put(filePath, file);
            warFilePaths.add(filePath);
        }
    } 

    /**
     * ApplicationServerManager stub implementation
     */
    private static class ApplicationServerManagerStub implements ApplicationServerManager
    {
        public String start(String appPath) {return null;}
        public String stop(String appPath) {return null;}
        public String reload(String appPath) {return null;}
        public String remove(String appPath) {return null;}
        public String install(String warPath, String contexPath) {return null;}
        public String deploy(String appPath, InputStream is, int size) {return null;}
        public int getHostPort() {return -1;}
        public String getHostUrl() {return null;}
        public boolean isConnected() {return true;}
        public String getAppServerTarget(String appName) {return appName;}
    }
}
