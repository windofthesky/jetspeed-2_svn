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
import java.io.Reader;
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
 * ComponentManager
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 *
 */
public class ComponentManager extends NanoContainer implements ComponentManagement, ContainerManagement
{
    
    private MutablePicoContainer rootContainer;

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public ComponentManager(File arg0, PicoContainer arg1, ClassLoader arg2) throws IOException, ClassNotFoundException
    {
        super(arg0, arg1, arg2);        
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public ComponentManager(File arg0, PicoContainer arg1) throws IOException, ClassNotFoundException
    {
        super(arg0, arg1);        
    }

    /**
     * @param arg0
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public ComponentManager(File arg0) throws IOException, ClassNotFoundException
    {
        super(arg0);        
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.lang.ClassNotFoundException
     */
    public ComponentManager(Reader arg0, String arg1, ClassLoader arg2) throws ClassNotFoundException
    {
        super(arg0, arg1, arg2);        
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.lang.ClassNotFoundException
     */
    public ComponentManager(Reader arg0, String arg1) throws ClassNotFoundException
    {
        super(arg0, arg1);        
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @throws java.lang.ClassNotFoundException
     */
    public ComponentManager(Reader arg0, String arg1, PicoContainer arg2, ClassLoader arg3) throws ClassNotFoundException
    {
        super(arg0, arg1, arg2, arg3);
        
    }

    /**
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.String)
     */
    public Object getComponent(Object componentName)
    {        
        
        return getRootContainer().getComponentInstance(componentName);
    }

    public MutablePicoContainer getRootContainer()
    {
        if(rootContainer == null)
        {
            ObjectReference containerRef = new SimpleReference();
            ScriptedContainerBuilder scb = this.getContainerBuilder();
            scb.buildContainer(containerRef, null, "jetspeed");
            rootContainer = (MutablePicoContainer) containerRef.get();            
        }
        
        return rootContainer;
    }

    /**
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.String, java.lang.String)
     */
    public Object getComponent(Object containerName, Object componentName)
    {
         MutablePicoContainer container = (MutablePicoContainer) getRootContainer().getComponentInstance(containerName);
         if(container != null)
         {
             return container.getComponentInstance(componentName);
         }
        
        return null;
    }

    /**
     * @see org.apache.jetspeed.components.ContainerManagement#getContainer(java.lang.String)
     */
    public MutablePicoContainer getContainer(String containerName)
    {        
        return (MutablePicoContainer) getRootContainer().getComponentInstance(containerName);
    }

    /**
     * @see org.apache.jetspeed.components.ContainerManagement#getContainers()
     */
    public Collection getContainers()
    {        
        Iterator itr = getRootContainer().getComponentInstances().iterator();
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

}
