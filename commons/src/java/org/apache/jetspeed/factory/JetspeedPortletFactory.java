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
package org.apache.jetspeed.factory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletContext;
import javax.portlet.PortletConfig;

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
        String portletName =  portletDefinition.getName();
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
            System.out.println(">>>> Exception Loading Class: " + e);
            e.printStackTrace();
            throw new PortletException( "PortletFactory: Unable to load class " + className );
        }

        return portlet;
    }

}

