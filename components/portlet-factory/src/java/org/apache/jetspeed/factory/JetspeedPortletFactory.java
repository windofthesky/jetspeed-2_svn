/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.PortletCache;
import org.apache.jetspeed.container.PortalAccessor;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionCtrl;

/**
 * <p>
 * JetspeedPortletFactory
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JetspeedPortletFactory implements PortletFactory
{

    private PortletCache portletCache;
    private static final Log log = LogFactory.getLog(JetspeedPortletFactory.class);
    private final HashMap classLoaderMap;

    /**
     * 
     */
    public JetspeedPortletFactory(PortletCache portletCache)
    {
        super();
        this.portletCache = portletCache;
        classLoaderMap = new HashMap();
    }

    /**
     * 
     * <p>
     * addClassLoader
     * </p>
     * 
     * Adds a ClassLoader to the search path, <code>classLoaders</code>, of the JetspeedPortletFactory.
     *
     * @param cl
     */
    public void addClassLoader(String paId, ClassLoader cl)
    {
        if (paId != null && !paId.equals(""))
        {
            synchronized (classLoaderMap)
            {
                if (classLoaderMap.get(paId) == null)
                {
                    classLoaderMap.put(paId, cl);
                }
            }
        }
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
    public Portlet loadPortletClass( String className ) throws InstantiationException, IllegalAccessException
    {
        Portlet portlet = null;
        try
        {
            portlet = (Portlet)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        }
        catch (ClassCastException cce)
        {
            cce.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            synchronized (classLoaderMap)
            {                
                Iterator itr = classLoaderMap.values().iterator();
                while (itr.hasNext() && portlet == null)
                {
                    ClassLoader cl = (ClassLoader) itr.next();
                    try
                    {                        
                        portlet = (Portlet) cl.loadClass(className).newInstance();
                        if(portlet != null)
                        {
                           log.warn("Portlet class "+className+" successfuly located in classloader "+cl+".  "+
                                     "You can safely ignore any prior messages classloading messages for this class."); 
                        }
                    }
                    catch (Exception e1)
                    {
                        // move along
                        log.warn("The PortletFactory did not locate class "+className+" in classloader "+cl+".  "+
                                   "This message can be ignored if "+className+" can be located in subsequent classloaders.  "+
                                   "This message triggered by: "+e.toString());
                    }
                    
                }
            }
        }
        return portlet;
    }

    /**
     * Gets a portlet by either creating it or returning a handle to it from the portlet 'cache'
     * 
     * @param portletDefinition The definition of the portlet
     * @return Portlet 
     * @throws PortletException
     */
    public Portlet getPortlet( ServletConfig servletConfig, PortletDefinition portletDefinition ) throws PortletException
    {
        Portlet portlet = null;
        Class portletClass = null;
        String handle = null;
        String portletName = portletDefinition.getId().toString();
        //String portletName = portletDefinition.getName();
        String className = portletDefinition.getClassName(); 
        String paId = portletDefinition.getPortletApplicationDefinition().getWebApplicationDefinition().getId().toString();

        try
        {                        
            portlet = portletCache.get(portletName);
            if (null != portlet)
            {
               // ((PortletDefinitionCtrl) portletDefinition).setPortletClassLoader(portlet.getClass().getClassLoader());
                return portlet;
            }
            
            portlet = loadPortletClass(className);
            
            if(portlet == null)
            {
                throw new FileNotFoundException("Could not located portlet "+className+" in any classloader.");
            }
            
            ClassLoader cl = (ClassLoader) classLoaderMap.get(paId);
            if (cl != null)
            {
                ((PortletDefinitionCtrl) portletDefinition).setPortletClassLoader(cl);
            }
            else
            {
            ((PortletDefinitionCtrl) portletDefinition).setPortletClassLoader(portlet.getClass().getClassLoader());
            }
      
            ServletContext servletContext = servletConfig.getServletContext();
            PortletContext portletContext = 
                        PortalAccessor.createPortletContext(servletContext, 
                                                            portletDefinition.getPortletApplicationDefinition());            
            PortletConfig portletConfig = PortalAccessor.createPortletConfig(servletConfig, portletContext, portletDefinition);
            
            portlet.init(portletConfig);            
            portletCache.add(portletName, portlet);
            
        }
        catch (Throwable e)
        {
            log.error("PortletFactory: Failed to load portlet "+className, e);
            e.printStackTrace();
            throw new PortletException( "PortletFactory: Failed to load portlet " + className +":"+e.toString(), e);
        }

        return portlet;
    }
    
    public ClassLoader getPortletClassLoader(PortletDefinition portletDef)
    {
        String appId = portletDef.getPortletApplicationDefinition().getId().toString();
        if(classLoaderMap.containsKey(appId))
        {
            return (ClassLoader) classLoaderMap.get(appId);
        }
        else
        {
            throw new IllegalStateException("No classloader has been defined for portlet application "+appId);
        }
    }

}
