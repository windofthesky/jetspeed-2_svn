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
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.PortletCache;
import org.apache.jetspeed.container.PortalAccessor;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * The Jetspeed Portlet Factory is a facade the process of creating portlets.
 * 
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class JetspeedPortletFactory
{

    private static final Log log = LogFactory.getLog(JetspeedPortletFactory.class);
    
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
        Portlet portlet = null;
        Class portletClass = null;
        String handle = null;
        String portletName = portletDefinition.getId().toString();
        //String portletName = portletDefinition.getName();
        String className = portletDefinition.getClassName(); 

        try
        {                        
            portlet = PortletCache.get(portletName);
            if (null != portlet)
            {
                return portlet;
            }
            
            portlet = (Portlet)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();            
            ServletContext servletContext = servletConfig.getServletContext();
            PortletContext portletContext = 
                        PortalAccessor.createPortletContext(servletContext, 
                                                            portletDefinition.getPortletApplicationDefinition());            
            PortletConfig portletConfig = PortalAccessor.createPortletConfig(servletConfig, portletContext, portletDefinition);
            
            portlet.init(portletConfig);            
            PortletCache.add(portletName, portlet);
            
        }
        catch (Throwable e)
        {
            log.error("PortletFactory: Failed to load portlet "+className, e);
            e.printStackTrace();
            throw new PortletException( "PortletFactory: Failed to load portlet " + className +":"+e.toString(), e);
        }

        return portlet;
    }

}

