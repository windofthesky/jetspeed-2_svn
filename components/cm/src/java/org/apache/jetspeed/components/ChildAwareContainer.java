/*
 * Created on Apr 21, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * Wraps a DefaultPicoContainer to allow container expansion
 * searching capabilities.  This allows components in one container to
 * locate components in another container as along as they share the same
 * parent container.  DefaultPicoContainer only provides for a child container
 * to search up to its parent for components not available within itself.
 * ChildAwareContainer also allows the parent to search down into its children.
 * 
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 *  
 */
public class ChildAwareContainer implements MutablePicoContainer
{
    protected MutablePicoContainer wrappedContainer;
    protected List children;    
    protected ThreadLocal localLastChildIndex = new ThreadLocal();

    /**
     * @param componentAdapterFactory
     * @param parent
     */
    public ChildAwareContainer( ComponentAdapterFactory componentAdapterFactory, PicoContainer parent )
    {
        children = new ArrayList();
        wrappedContainer = new DefaultPicoContainer(componentAdapterFactory, parent);
        registerToParent(parent);

    }

    /**
     * @param parent
     */
    public ChildAwareContainer( PicoContainer parent )
    {
        children = new ArrayList();
        wrappedContainer = new DefaultPicoContainer(parent);
        registerToParent(parent);
    }

    /**
     * @param componentAdapterFactory
     */
    public ChildAwareContainer( ComponentAdapterFactory componentAdapterFactory )
    {
        children = new ArrayList();
        wrappedContainer = new DefaultPicoContainer(componentAdapterFactory);

    }

    /**
     *  
     */
    public ChildAwareContainer()
    {
        children = new ArrayList();
        wrappedContainer = new DefaultPicoContainer();
    }

    /**
     * @param componentAdapter
     */
    public void addOrderedComponentAdapter( ComponentAdapter componentAdapter )
    {
        wrappedContainer.addOrderedComponentAdapter(componentAdapter);
    }

    /**
     *  
     */
    public void dispose()
    {
        wrappedContainer.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        return wrappedContainer.equals(obj);
    }

    /**
     * @param componentKey
     * @return
     */
    public ComponentAdapter getComponentAdapter( Object componentKey )
    {
        return locateComponentInHierarchy(componentKey);
    }

    /**
     * @param componentType
     * @return
     */
    public ComponentAdapter getComponentAdapterOfType( Class componentType )
    {
        return locateComponentInHierarchy(componentType);
    }

    /**
     * @return
     */
    public Collection getComponentAdapters()
    {
        return wrappedContainer.getComponentAdapters();
    }

    /**
     * @param componentType
     * @return
     */
    public List getComponentAdaptersOfType( Class componentType )
    {
        return wrappedContainer.getComponentAdaptersOfType(componentType);
    }

    /**
     * @param componentKey
     * @return
     */
    public Object getComponentInstance( Object componentKey )
    {
        ComponentAdapter adapter = locateComponentInHierarchy(componentKey);
        return adapter != null ? adapter.getComponentInstance() : null;
    }

    /**
     * @param componentType
     * @return
     */
    public Object getComponentInstanceOfType( Class componentType )
    {
        return getComponentInstance(componentType);
    }

    /**
     * @return
     */
    public List getComponentInstances()
    {
        return wrappedContainer.getComponentInstances();
    }

    /**
     * @return
     */
    public PicoContainer getParent()
    {
        return wrappedContainer.getParent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return wrappedContainer.hashCode();
    }

    /**
     * @param componentAdapter
     * @return @throws
     *         org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponent( ComponentAdapter componentAdapter ) throws PicoRegistrationException
    {
        return wrappedContainer.registerComponent(componentAdapter);
    }

    /**
     * @param componentImplementation
     * @return @throws
     *         org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentImplementation( Class componentImplementation ) throws PicoRegistrationException
    {
        return wrappedContainer.registerComponentImplementation(componentImplementation);
    }

    /**
     * @param componentKey
     * @param componentImplementation
     * @return @throws
     *         org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentImplementation( Object componentKey, Class componentImplementation ) throws PicoRegistrationException
    {
        return wrappedContainer.registerComponentImplementation(componentKey, componentImplementation);
    }

    /**
     * @param componentKey
     * @param componentImplementation
     * @param parameters
     * @return @throws
     *         org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentImplementation( Object componentKey, Class componentImplementation, Parameter[] parameters )
            throws PicoRegistrationException
    {
        return wrappedContainer.registerComponentImplementation(componentKey, componentImplementation, parameters);
    }

    /**
     * @param componentInstance
     * @return @throws
     *         org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentInstance( Object componentInstance ) throws PicoRegistrationException
    {
        return wrappedContainer.registerComponentInstance(componentInstance);
    }

    /**
     * @param componentKey
     * @param componentInstance
     * @return @throws
     *         org.picocontainer.PicoRegistrationException
     */
    public ComponentAdapter registerComponentInstance( Object componentKey, Object componentInstance ) throws PicoRegistrationException
    {
        return wrappedContainer.registerComponentInstance(componentKey, componentInstance);
    }

    /**
     * @param parent
     */
    public void setParent( PicoContainer parent )
    {
        wrappedContainer.setParent(parent);
        registerToParent(parent);
    }

    /**
     *  
     */
    public void start()
    {
//        ClassLoader originalClassLoader=null;
//        try
//        {
//          originalClassLoader = Thread.currentThread().getContextClassLoader();
            wrappedContainer.start();
//        }
//        finally
//        {
//            if(originalClassLoader != null)
//            {
//                setC
//            }
//        }
    }

    /**
     *  
     */
    public void stop()
    {        
        wrappedContainer.stop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return wrappedContainer.toString();
    }

    /**
     * @param componentKey
     * @return
     */
    public ComponentAdapter unregisterComponent( Object componentKey )
    {
        return wrappedContainer.unregisterComponent(componentKey);
    }

    /**
     * @param componentInstance
     * @return
     */
    public ComponentAdapter unregisterComponentByInstance( Object componentInstance )
    {
        return wrappedContainer.unregisterComponentByInstance(componentInstance);
    }

    /**
     * @throws org.picocontainer.PicoVerificationException
     */
    public void verify() throws PicoVerificationException
    {
        wrappedContainer.verify();
    }

    /**
     * @param parent
     */
    protected void registerToParent( PicoContainer parent )
    {
        if (parent != null && parent instanceof ChildAwareContainer)
        {
            // a good parent always remembers her kids ;)
            ChildAwareContainer goodParent = (ChildAwareContainer) parent;
            goodParent.children.add(this);
        }
    }

    protected final ComponentAdapter locateComponentInHierarchy( Object componentKey )
    {
        try
        {
            Integer lastIndex = (Integer) localLastChildIndex.get();
            if (lastIndex == null)
            {
                lastIndex = new Integer(0);
            }

            ComponentAdapter adapter = null;
            adapter = wrappedContainer.getComponentAdapter(componentKey);

            if (adapter != null)
            {                
                return adapter;
            }

            for (int i = lastIndex.intValue(); i < children.size(); )
            {
                PicoContainer child = (PicoContainer) children.get(i);
                i++;
                localLastChildIndex.set(new Integer(i));

                if (child instanceof ChildAwareContainer)
                {
                    ChildAwareContainer goodChild = (ChildAwareContainer) child;
                    return goodChild.locateComponentInHierarchy(componentKey);
                }
            }

            return null;
        }
        finally
        {
            localLastChildIndex.set(null);
        }
    }
}