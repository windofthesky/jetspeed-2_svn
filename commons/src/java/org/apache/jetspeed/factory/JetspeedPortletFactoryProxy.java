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
package org.apache.jetspeed.factory;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.servlet.ServletConfig;

import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * The Jetspeed Portlet Factory is a facade the process of creating portlets.
 * 
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public final class JetspeedPortletFactoryProxy
{
    private static PortletFactory portletFactory;

    private static ThreadLocal tlData = new ThreadLocal();
    
    public static void setCurrentPortletDefinition(PortletDefinition pd)
    {
        tlData.set(pd);
    }
    
    public static PortletDefinition getCurrentPortletDefinition()
    {
        return (PortletDefinition)tlData.get();
    }
    
    public JetspeedPortletFactoryProxy(PortletFactory portletFactory)
    {
        if(JetspeedPortletFactoryProxy.portletFactory == null)
        {
            JetspeedPortletFactoryProxy.portletFactory = portletFactory;
        }
        else
        {
            throw new IllegalStateException("The JetspeedPortletFactoryProxy can only be called once.");
        }
    }
    
    
    /**
     * Gets a portlet by either creating it or returning a handle to it from the portlet 'cache'
     * 
     * @param portletDefinition The definition of the portlet
     * @return Portlet 
     * @throws PortletException
     */
    public static Portlet getPortlet(ServletConfig servletConfig, PortletDefinition portletDefinition) 
        throws PortletException
    {
        verifyState();
        return portletFactory.getPortlet(servletConfig, portletDefinition);
    }
    
    /**
     * <p>
     * loadPortletClass
     * </p>
     * Loads a Portlet class by first checking Thread.currentThread().getContextClassLoader()
     * then by checking all of the ClassLoaders in <code>classLoaders</code> until the
     * class is located or returns <code>null</code> if the Portlet class could not be found.
     *
     * @param className
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Portlet loadPortletClass( String className ) throws InstantiationException, IllegalAccessException
    {
       verifyState();
       return portletFactory.loadPortletClass(className);
    }
    
    /**
     * 
     * <p>
     * addClassLoader
     * </p>
     * 
     * Adds a ClassLoader to the search path, <code>classLoaders</code>, of the JetspeedPortletFactory.
     *
     * @param paId
     * @param cl
     */
    public static void addClassLoader(String paId, ClassLoader cl)
    {
        verifyState();
        portletFactory.addClassLoader(paId, cl);
    }
    
    public static void reset()
    {
        portletFactory = null;
    }
    
    private static void verifyState() throws IllegalStateException
    {
        if(portletFactory == null)
        {
            throw new IllegalStateException("JetspeedPorletFactory has not been initialized.  "+
                                            "You must first invoke the constructor with a valid PortletFactory instance.");
        }        
    }

}

