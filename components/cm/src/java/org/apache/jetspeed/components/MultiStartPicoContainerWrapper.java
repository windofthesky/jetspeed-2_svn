/*
 * Created on Apr 19, 2004
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
public class MultiStartPicoContainerWrapper implements MutablePicoContainer
{
    
    private MutablePicoContainer fieldContainer;
    private boolean fieldStarted = false;

    /**
     * @param componentAdapter
     */
    public void addOrderedComponentAdapter( ComponentAdapter componentAdapter )
    {
        fieldContainer.addOrderedComponentAdapter(componentAdapter);
    }
    /**
     * 
     */
    public void dispose()
    {
        fieldContainer.dispose();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        return fieldContainer.equals(obj);
    }
    /**
     * @param componentKey
     * @return
     */
    public ComponentAdapter getComponentAdapter( Object componentKey )
    {
        return fieldContainer.getComponentAdapter(componentKey);
    }
    /**
     * @param componentType
     * @return
     */
    public ComponentAdapter getComponentAdapterOfType( Class componentType )
    {
        return fieldContainer.getComponentAdapterOfType(componentType);
    }
    /**
     * @return
     */
    public Collection getComponentAdapters()
    {
        return fieldContainer.getComponentAdapters();
    }
    /**
     * @param componentType
     * @return
     */
    public List getComponentAdaptersOfType( Class componentType )
    {
        return fieldContainer.getComponentAdaptersOfType(componentType);
    }
    /**
     * @param componentKey
     * @return
     */
    public Object getComponentInstance( Object componentKey )
    {
        return fieldContainer.getComponentInstance(componentKey);
    }
    /**
     * @param componentType
     * @return
     */
    public Object getComponentInstanceOfType( Class componentType )
    {
        return fieldContainer.getComponentInstanceOfType(componentType);
    }
    /**
     * @return
     */
    public List getComponentInstances()
    {
        return fieldContainer.getComponentInstances();
    }
    /**
     * @return
     */
    public PicoContainer getParent()
    {
        return fieldContainer.getParent();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return fieldContainer.hashCode();
    }
    /**
     * @param componentAdapter
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponent( ComponentAdapter componentAdapter ) throws PicoRegistrationException
    {
        return fieldContainer.registerComponent(componentAdapter);
    }
    /**
     * @param componentImplementation
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentImplementation( Class componentImplementation ) throws PicoRegistrationException
    {
        return fieldContainer.registerComponentImplementation(componentImplementation);
    }
    /**
     * @param componentKey
     * @param componentImplementation
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentImplementation( Object componentKey, Class componentImplementation ) throws PicoRegistrationException
    {
        return fieldContainer.registerComponentImplementation(componentKey, componentImplementation);
    }
    /**
     * @param componentKey
     * @param componentImplementation
     * @param parameters
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentImplementation( Object componentKey, Class componentImplementation, Parameter[] parameters )
            throws PicoRegistrationException
    {
        return fieldContainer.registerComponentImplementation(componentKey, componentImplementation, parameters);
    }
    /**
     * @param componentInstance
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentInstance( Object componentInstance ) throws PicoRegistrationException
    {
        return fieldContainer.registerComponentInstance(componentInstance);
    }
    /**
     * @param componentKey
     * @param componentInstance
     * @return
     * @throws org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentInstance( Object componentKey, Object componentInstance ) throws PicoRegistrationException
    {
        return fieldContainer.registerComponentInstance(componentKey, componentInstance);
    }
    /**
     * @param parent
     */
    public void setParent( PicoContainer parent )
    {
        fieldContainer.setParent(parent);
    }
    /**
     * 
     */
    public void start()
    {
        if(!fieldStarted)
        {
            fieldContainer.start();
            fieldStarted = true;
        }
        // skip it, don't fail
    }
    /**
     * 
     */
    public void stop()
    {
        if(fieldStarted)
        {
            fieldContainer.stop();
            fieldStarted = false;
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return fieldContainer.toString();
    }
    /**
     * @param componentKey
     * @return
     */
    public ComponentAdapter unregisterComponent( Object componentKey )
    {
        return fieldContainer.unregisterComponent(componentKey);
    }
    /**
     * @param componentInstance
     * @return
     */
    public ComponentAdapter unregisterComponentByInstance( Object componentInstance )
    {
        return fieldContainer.unregisterComponentByInstance(componentInstance);
    }
    /**
     * @throws org.picocontainer.PicoVerificationException
     */
    public void verify() throws PicoVerificationException
    {
        fieldContainer.verify();
    }
    public MultiStartPicoContainerWrapper(MutablePicoContainer inContainer)
    {
        fieldContainer = inContainer;
    }

}
