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
    public Object getComponent(String componentName)
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
    public Object getComponent(String containerName, String componentName)
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
