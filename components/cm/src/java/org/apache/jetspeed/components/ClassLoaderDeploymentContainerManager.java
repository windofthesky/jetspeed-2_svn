/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.VFSClassLoader;
import org.apache.jetspeed.components.pico.groovy.GroovyComponentAdapter;
import org.apache.jetspeed.components.util.ComponentInfo;
import org.apache.jetspeed.components.util.ComponentPackage;
import org.codehaus.classworlds.ClassWorldException;
import org.codehaus.classworlds.NoSuchRealmException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 *  
 */
public class ClassLoaderDeploymentContainerManager implements ContainerManager
{

    private String deploymentRoot;
    private String[] additionalDeploymentLocations;
    
    private String containerId;
    

    private boolean isBuilt = false;
    protected ArrayList fieldComponents;    
    private Configuration sysConfig;
    private String applicationRoot;
    private ClassLoader rootClassLoader;
    private ClassLoader containerClassLoader;
    protected ArrayList urlList;
    private FileSystemManager fsManager;
    

    /**
     * @param containerId
     * @param container
     * @param deploymentRoot
     * @param additionalDeploymentLocations
     * @param parentClassLoader
     * @param classworld
     * @throws IOException
     * @throws ClassWorldException
     */
    public ClassLoaderDeploymentContainerManager( String containerId,
            String deploymentRoot, String additionalDeploymentLocations[],
            ClassLoader rootClassLoader, Configuration sysConfig)
            throws Exception
    {
        this.rootClassLoader = rootClassLoader;
        this.deploymentRoot = deploymentRoot;
        this.additionalDeploymentLocations = additionalDeploymentLocations;
     
        this.containerId = containerId;
        this.sysConfig = sysConfig != null ? sysConfig : new PropertiesConfiguration();                
        System.out.println("Invoking VFS");
        fsManager = VFS.getManager();
        fieldComponents = new ArrayList();
        File deploymentFolder = new File(deploymentRoot);
        
        urlList = new ArrayList();
        loadComponentsFromDeployment(fsManager, fieldComponents,
                deploymentFolder);

        loadAdditionalComponents(additionalDeploymentLocations, fsManager,
                fieldComponents);        

        assembleComponentPackageClassLoaders(fieldComponents);

    }

    /**
     * @param components
     */
    protected void assembleContainer( ArrayList components,
            MutablePicoContainer container ) throws ClassNotFoundException, IOException
    {

        Iterator componentItr = components.iterator();        
        while (componentItr.hasNext())
        {
            ComponentPackage packg = (ComponentPackage) componentItr.next();
            Iterator infoItr = packg.getAllComponentInformation();
            try
            {
                while (infoItr.hasNext())
                {
                    ComponentInfo info = (ComponentInfo) infoItr.next();       
                    
                    ClassLoader packageClassLoader = packg.getPackageClassLoader();
                                        
                    Configuration configuration = info.getConfiguration();
                    
                    Class componentClass = info
                            .getComponentClass(packageClassLoader);
                    
                    Object componentKey = info
                            .getComponentKey(packageClassLoader);
                    System.out.println("Loading component "+componentKey);
                    ComponentAdapter adapter = new GroovyComponentAdapter(componentKey, componentClass, null, info
                            .isSingleton(), packageClassLoader, configuration);
                    container.registerComponent(adapter);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new IllegalStateException(
                        "Failed to register component: " + e.toString());
            }

        }
    }

    /**
     * @param components
     * @throws IOException
     * @throws NoSuchRealmException
     */
    protected void assembleComponentPackageClassLoaders( ArrayList components )
            throws ClassWorldException, MalformedURLException, IOException,
            URISyntaxException, ClassNotFoundException
    {
        Iterator componentItr = components.iterator();
        ArrayList urlsToExport = new ArrayList();
        while (componentItr.hasNext())
        {
            ComponentPackage packg = (ComponentPackage) componentItr.next();
            //Collection urlColl = packg.getUrlsToLoad();
            Collection foColl = packg.getFileObjectsToLoad();
            
            String[] exportedJars = packg.getExportedJars();
            Iterator urlItr = foColl.iterator();
            while(urlItr.hasNext())
            {
              FileObject urlToCheck = (FileObject) urlItr.next();
              String urlExtForm = urlToCheck.getName().getPath();
              for(int i=0; i < exportedJars.length; i++)
              {
                 if(urlExtForm.indexOf(exportedJars[i]) != -1)
                 {
                     System.out.println("Exporting jar url: "+urlToCheck);
                     urlsToExport.add(urlToCheck);
                 }
              }
            }
        }
        
        containerClassLoader = new VFSClassLoader((FileObject[])urlsToExport.toArray(new FileObject[urlsToExport.size()]), fsManager, rootClassLoader);
        System.err.println("URLS to export "+urlsToExport.size());
        
        componentItr = components.iterator();
        
        while (componentItr.hasNext())
        {
            ComponentPackage packg = (ComponentPackage) componentItr.next();
            String[] exportedJars = packg.getExportedJars();
            Collection urlColl = packg.getFileObjectsToLoad();    
           // URLClassLoader packageClassLoader = new URLClassLoader((URL[])urlColl.toArray(new URL[urlColl.size()]), containerClassLoader);
            VFSClassLoader packageClassLoader = new VFSClassLoader((FileObject[])urlColl.toArray(new FileObject[urlColl.size()]), fsManager, containerClassLoader);
            packg.setPackageClassLoader(packageClassLoader);      
        }
               
    }

    /**
     * @param additionalDeploymentLocations
     * @param fsManager
     * @param components
     * @throws FileNotFoundException
     * @throws FileSystemException
     * @throws IOException
     */
    protected void loadAdditionalComponents(
            String[] additionalDeploymentLocations,
            FileSystemManager fsManager, ArrayList components )
            throws FileNotFoundException, FileSystemException, IOException
    {
        if (additionalDeploymentLocations != null)
        {
            for (int i = 0; i < additionalDeploymentLocations.length; i++)
            {
                File depLocation = null;
               
                depLocation = new File(additionalDeploymentLocations[i]);
                

                if (!depLocation.exists())
                {
                    throw new FileNotFoundException("The deployment location, "
                            + additionalDeploymentLocations
                            + ", does nto exist.");
                }

                FileObject depFileObject = fsManager.toFileObject(depLocation);
                components.add(new ComponentPackage(depFileObject, sysConfig));
            }
        }
    }

    /**
     * @param fsManager
     * @param components
     * @param deploymentFolder
     * @throws FileSystemException
     * @throws IOException
     */
    protected void loadComponentsFromDeployment( FileSystemManager fsManager,
            ArrayList components, File deploymentFolder )
            throws FileSystemException, IOException
    {
        if (deploymentFolder.exists())
        {
            File[] archives = deploymentFolder.listFiles();
            for (int i = 0; i < archives.length; i++)
            {
                FileObject jaredComponent = fsManager.resolveFile("zip:/"
                        + archives[i].getAbsolutePath());
                components.add(new ComponentPackage(jaredComponent, sysConfig));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.ContainerManager#getContainer()
     */
    public PicoContainer assembleContainer( MutablePicoContainer container ) throws IOException
    {
        try
        {
            if (!isBuilt)
            {
                assembleContainer(fieldComponents, container);
                isBuilt = true;
            }
            return container;
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Error assembling container: "
                    + e.toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.ContainerManager#getContainerClassLoader()
     */
    public ClassLoader getContainerClassLoader()
    {
        return containerClassLoader;
    }

}