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
import java.util.ArrayList;

import org.apache.commons.lang.exception.NestableRuntimeException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.SimpleReference;

/**
 * @author Scott Weaver
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class RootContainerDaemon implements Runnable
{

    protected RootContainer rootContainer;

    protected String deploymentRoot;

    private String[] additionalDeploymentLocations;

    private ClassLoader parentClassLoader;

    public RootContainerDaemon(RootContainer rootContainer, String deploymentRoot, String additionalDeploymentLocations[], ClassLoader parentClassLoader)
    {
        this.rootContainer = rootContainer;
        this.deploymentRoot = deploymentRoot;
        this.additionalDeploymentLocations = additionalDeploymentLocations;
        this.parentClassLoader = parentClassLoader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        buildContainer();
    }

    protected void buildContainer() 
    {
        try
        {
            ArrayList components = new ArrayList();
            File deploy = new File(deploymentRoot);
            if (deploy.exists())
            {
                File[] archives = deploy.listFiles();
                for (int i = 0; i < archives.length; i++)
                {
                    components.add("zip:/" + archives[i].getAbsolutePath());
                }
            }

            if (additionalDeploymentLocations != null)
            {
                for (int i = 0; i < additionalDeploymentLocations.length; i++)
                {
                    components.add(new File(additionalDeploymentLocations[i]).toURL().toString());
                }
            }

            rootContainer.setApplicationFolders((String[]) components.toArray(new String[components.size()]));
            
            if(parentClassLoader == null)
            {
            	parentClassLoader = Thread.currentThread().getContextClassLoader();
            }
            
            FileSystemManager fsManager = VFS.getManager();
     
            DependencyAwareDeployer deployer = new DependencyAwareDeployer(fsManager);
            MutablePicoContainer parent = new ChildAwareContainer();
            SimpleReference parentRef = new SimpleReference();
            parentRef.set(parent);
            for (int i = 0; i < rootContainer.getApplicationFolders().length; i++)
            {
                rootContainer.setContainer((MutablePicoContainer) deployer.deploy(fsManager.resolveFile(rootContainer.getApplicationFolders()[i]), parentClassLoader, parentRef).get());
            }
        }
        catch (Exception e)
        {

            throw new NestableRuntimeException("Unable to start root container daemon: "+e.toString(), e);
        }       

    }
}