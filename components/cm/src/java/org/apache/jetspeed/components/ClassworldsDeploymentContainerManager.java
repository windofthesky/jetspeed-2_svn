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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.jetspeed.components.pico.groovy.GroovyComponentAdapter;
import org.apache.jetspeed.components.util.ComponentInfo;
import org.apache.jetspeed.components.util.ComponentPackage;
import org.apache.jetspeed.components.util.classworlds.BootStrapClassRealm;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.ClassWorldException;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.classworlds.NoSuchRealmException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 *  
 */
public class ClassworldsDeploymentContainerManager implements ContainerManager
{

    private String deploymentRoot;
    private String[] additionalDeploymentLocations;
    private ClassWorld classworld;
    private String containerId;
    private ClassRealm containerRealm;

    private boolean isBuilt = false;
    protected ArrayList fieldComponents;
    private ClassRealm bootRealm;
    private Configuration sysConfig;
    private String applicationRoot;
    private Collection defersToBootRealm;

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
    public ClassworldsDeploymentContainerManager( String containerId,
            String deploymentRoot, String additionalDeploymentLocations[],
            ClassRealm bootRealm, Configuration sysConfig, Collection defersToBootRealm )
            throws Exception
    {
        this.defersToBootRealm = defersToBootRealm;
        this.deploymentRoot = deploymentRoot;
        this.additionalDeploymentLocations = additionalDeploymentLocations;
        //this.bootRealm = new BootStrapClassRealm(bootRealm);
        this.bootRealm = bootRealm;
        this.classworld = bootRealm.getWorld();
        this.containerId = containerId;
        this.sysConfig = sysConfig != null ? sysConfig : new PropertiesConfiguration();                
        
        assembleContainerRealm();
   
        FileSystemManager fsManager = VFS.getManager();

        fieldComponents = new ArrayList();
        File deploymentFolder = new File(deploymentRoot);

        loadComponentsFromDeployment(fsManager, fieldComponents,
                deploymentFolder);

        loadAdditionalComponents(additionalDeploymentLocations, fsManager,
                fieldComponents);

        

        assembleComponentClassRealms(fieldComponents);

    }

    /**
     * @param containerId
     * @param parentClassLoader
     * @param classworld
     * @throws DuplicateRealmException
     */
    protected void assembleContainerRealm()
            throws DuplicateRealmException, NoSuchRealmException
    {
//        containerRealm = classworld.newRealm(containerId, bootRealm.getClassLoader());
//        containerRealm.setParent(bootRealm);
      //  containerRealm = bootRealm;
        

//        HashSet urlsAdded = new HashSet();
//        for (int i = 0; i < bootUrls.length; i++)
//        {
//            if (!urlsAdded.contains(bootUrls[i]))
//            {
//                containerRealm.addConstituent(bootUrls[i]);
//                urlsAdded.add(bootUrls[i]);
//            }
//        }
//        
//        Thread.currentThread().setContextClassLoader(containerRealm.getClassLoader());

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

//                    ClassLoader packageClassLoader = classworld.getRealm(
//                            packg.getPackageId()).getClassLoader();
                    ClassLoader packageClassLoader = bootRealm.getClassLoader();

                    Configuration configuration = info.getConfiguration();
                    
                    Class componentClass = info
                            .getComponentClass(packageClassLoader);
                    Object componentKey = info
                            .getComponentKey(packageClassLoader);
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
    protected void assembleComponentClassRealms( ArrayList components )
            throws ClassWorldException, MalformedURLException, IOException,
            URISyntaxException
    {
        Iterator componentItr = components.iterator();
        while (componentItr.hasNext())
        {
            ComponentPackage packg = (ComponentPackage) componentItr.next();
            Iterator urlItr = packg.getUrlsToLoad().iterator();
//            ClassRealm pkgRealm = classworld.newRealm(packg.getPackageId(),
//                    null);
            ClassRealm pkgRealm = containerRealm;
            pkgRealm.setParent(containerRealm);
            if(defersToBootRealm.contains(packg.getPackageId()))
            {                
                continue;
            }
            while (urlItr.hasNext())
            {
                URL url = (URL) urlItr.next();
                String path = url.toString();

                pkgRealm.addConstituent(url);
                // pkgRealm.importFrom(packg.getPackageId(),
                // "org.apache.log4j");

            }
            
//            pkgRealm.importFrom("boot", "org.apache.commons.logging");
//
//            String[] exports = packg.getExportedPackages();
//            ClassLoader pcl = pkgRealm.getClassLoader();
//            
//            
//            for (int i = 0; i < exports.length; i++)
//            {
//             bootRealm.importFrom(packg.getPackageId(), exports[i]);                
//            }
        }
        //        Thread.currentThread().setContextClassLoader(
        //                containerRealm.getClassLoader());
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
                if (!additionalDeploymentLocations[i].endsWith("/")
                        && !additionalDeploymentLocations[i].endsWith("\\"))
                {
                    depLocation = new File(additionalDeploymentLocations[i]
                            + "/");
                }
                else
                {
                    depLocation = new File(additionalDeploymentLocations[i]);
                }

                if (!depLocation.exists())
                {
                    throw new FileNotFoundException("The deployment location, "
                            + additionalDeploymentLocations
                            + ", does nto exist.");
                }

                FileObject depFileObject = fsManager.resolveFile(depLocation
                        .getAbsolutePath()
                        + "/");
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
        return containerRealm.getClassLoader();
    }

}