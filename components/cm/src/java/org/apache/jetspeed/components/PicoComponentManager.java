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
package org.apache.jetspeed.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * <p>
 * PicoComponentManager
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 *
 */
public class PicoComponentManager implements ComponentManager
{    
    protected ScriptedContainerBuilder containerBuilder;
    protected ObjectReference rootContainerRef;
    protected MutablePicoContainer rootContainer;

    /**
     * 
     * @param assemblyFile
     * @param parent
     * @param scope
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public PicoComponentManager( File assemblyFile, PicoContainer parent, String scope) throws IOException, ClassNotFoundException 
    {
        rootContainerRef = new SimpleReference();
        NanoContainer nano = new NanoContainer(assemblyFile, this.getClass().getClassLoader());
        ObjectReference parentRef = null;
        if(parent != null)
        {
            parentRef = new SimpleReference();
            parentRef.set(parent);            
        }
        containerBuilder = nano.getContainerBuilder();
        containerBuilder.buildContainer(rootContainerRef, parentRef, scope); 
        this.rootContainer = (MutablePicoContainer) rootContainerRef.get();
    }

    /**
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.String)
     */
    public Object getComponent(Object componentName)
    {        
        
        return rootContainer.getComponentInstance(componentName);
    }

    public Object getRootContainer()
    {       
        return rootContainer;
    }

    /**
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.String, java.lang.String)
     */
    public Object getComponent(Object containerName, Object componentName)
    {
         MutablePicoContainer container = (MutablePicoContainer) rootContainer.getComponentInstance(containerName);
         if(container != null)
         {
             return container.getComponentInstance(componentName);
         }
        
        return null;
    }

    /**
     * @see org.apache.jetspeed.components.ContainerManagement#getContainer(java.lang.String)
     */
    public Object getContainer(String containerName)
    {        
        return (MutablePicoContainer) rootContainer.getComponentInstance(containerName);
    }

    /**
     * @see org.apache.jetspeed.components.ContainerManagement#getContainers()
     */
    public Collection getContainers()
    {        
        Iterator itr = rootContainer.getComponentInstances().iterator();
        ArrayList list = new ArrayList();
        while(itr.hasNext())
        {
            Object comp = itr.next();
            if(comp instanceof PicoContainer)
            {
                list.add(comp);
            }
        }
        return list;
    }

    /**
     * <p>
     * stop
     * </p>
     *
     * @see org.apache.jetspeed.components.ContainerManagement#stop()
     * 
     */
    public void stop()
    {
        containerBuilder.killContainer(rootContainerRef);

    }
}
