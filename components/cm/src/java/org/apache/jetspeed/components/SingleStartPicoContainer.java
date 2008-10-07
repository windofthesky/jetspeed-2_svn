/*
 * Created on May 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import java.util.Collection;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class SingleStartPicoContainer implements MutablePicoContainer
{
    protected MutablePicoContainer container;
    private boolean started=false;
    
    public SingleStartPicoContainer(MutablePicoContainer container)
    {
        this.container = container;
    }

    /**
     * @param arg0
     */
    public void addOrderedComponentAdapter( ComponentAdapter arg0 )
    {
        container.addOrderedComponentAdapter(arg0);
    }
    /**
     * 
     */
    public void dispose()
    {
        container.dispose();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        return container.equals(obj);
    }
    /**
     * @param arg0
     * @return
     */
    public ComponentAdapter getComponentAdapter( Object arg0 )
    {
        return container.getComponentAdapter(arg0);
    }
    /**
     * @param arg0
     * @return
     */
    public ComponentAdapter getComponentAdapterOfType( Class arg0 )
    {
        return container.getComponentAdapterOfType(arg0);
    }
    /**
     * @return
     */
    public Collection getComponentAdapters()
    {
        return container.getComponentAdapters();
    }
    /**
     * @param arg0
     * @return
     */
    public Object getComponentInstance( Object arg0 )
    {
        return container.getComponentInstance(arg0);
    }
    /**
     * @param arg0
     * @return
     */
    public Object getComponentInstanceOfType( Class arg0 )
    {
        return container.getComponentInstanceOfType(arg0);
    }
    /**
     * @return
     */
    public List getComponentInstances()
    {
        return container.getComponentInstances();
    }
    /**
     * @return
     */
    public PicoContainer getParent()
    {
        return container.getParent();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return container.hashCode();
    }
    
    /**
     * @param arg0
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentImplementation( Class arg0 )
            throws PicoRegistrationException
    {
        return container.registerComponentImplementation(arg0);
    }
    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentImplementation( Object arg0,
            Class arg1 ) throws PicoRegistrationException
    {
        return container.registerComponentImplementation(arg0, arg1);
    }
    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentImplementation( Object arg0,
            Class arg1, Parameter[] arg2 ) throws PicoRegistrationException
    {
        return container.registerComponentImplementation(arg0, arg1, arg2);
    }
    /**
     * @param arg0
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentInstance( Object arg0 )
            throws PicoRegistrationException
    {
        return container.registerComponentInstance(arg0);
    }
    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentInstance( Object arg0, Object arg1 )
            throws PicoRegistrationException
    {
        return container.registerComponentInstance(arg0, arg1);
    }
    /**
     * @param arg0
     */
    public void setParent( PicoContainer arg0 )
    {
        container.setParent(arg0);
    }
    /**
     * 
     */
    public void start()
    {
        if(!started)
        {
            container.start();
            started = true;
        }        
    }
    /**
     * 
     */
    public void stop()
    {
        if(started)
        {
            container.stop();
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return container.toString();
    }
    /**
     * @param arg0
     * @return
     */
    public ComponentAdapter unregisterComponent( Object arg0 )
    {
        return container.unregisterComponent(arg0);
    }
    /**
     * @param arg0
     * @return
     */
    public ComponentAdapter unregisterComponentByInstance( Object arg0 )
    {
        return container.unregisterComponentByInstance(arg0);
    }
    /**
     * @throws org.picocontainer.PicoVerificationException
     */
    public void verify() throws PicoVerificationException
    {
        container.verify();
    }
    /**
     * @param componentType
     * @return
     */
    public List getComponentAdaptersOfType( Class componentType )
    {
        return container.getComponentAdaptersOfType(componentType);
    }
    /**
     * @param componentAdapter
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponent( ComponentAdapter componentAdapter )
            throws PicoRegistrationException
    {
        return container.registerComponent(componentAdapter);
    }
}
