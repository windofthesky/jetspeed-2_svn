/**
 * Created on Feb 19, 2004
 *
 * 
 * @author
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
 * NanoComponentManager
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class NanoComponentManager extends NanoContainer implements ComponentManagement, ContainerManagement
{
	
	private MutablePicoContainer rootContainer;

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public NanoComponentManager(File arg0, PicoContainer arg1, ClassLoader arg2) throws IOException, ClassNotFoundException
    {
        super(arg0, arg1, arg2);
        
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public NanoComponentManager(File arg0, PicoContainer arg1) throws IOException, ClassNotFoundException
    {
        super(arg0, arg1);
        
    }

    /**
     * @param arg0
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public NanoComponentManager(File arg0) throws IOException, ClassNotFoundException
    {
        super(arg0);
        
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.lang.ClassNotFoundException
     */
    public NanoComponentManager(Reader arg0, String arg1, ClassLoader arg2) throws ClassNotFoundException
    {
        super(arg0, arg1, arg2);
        
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.lang.ClassNotFoundException
     */
    public NanoComponentManager(Reader arg0, String arg1) throws ClassNotFoundException
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
    public NanoComponentManager(Reader arg0, String arg1, PicoContainer arg2, ClassLoader arg3) throws ClassNotFoundException
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

    protected MutablePicoContainer getRootContainer()
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
     * @see org.apache.jetspeed.components.ContainerManagement#getDefaultContainer()
     */
    public MutablePicoContainer getDefaultContainer()
    {        
        return rootContainer;
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

}
