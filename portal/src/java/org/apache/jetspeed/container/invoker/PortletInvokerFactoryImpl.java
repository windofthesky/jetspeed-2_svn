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
package org.apache.jetspeed.container.invoker;

import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.pluto.factory.PortletInvokerFactory;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.invoker.PortletInvoker;

/**
 * Portlet Invoker Factory creates portlet invokers based on the servlet context.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletInvokerFactoryImpl
    implements PortletInvokerFactory
{
    private final static Log log = LogFactory.getLog(PortletInvokerFactoryImpl.class);
    
    public final static String INVOKER_SERVLET    = "factory.invoker.servlet";
    public final static String INVOKER_LOCAL    = "factory.invoker.local";
    
    private ServletConfig servletConfig;
    
    public void init(ServletConfig config, Map properties) 
    throws Exception
    {
        servletConfig = config;
    }
    
    public void destroy()
    throws Exception
    {
    }

    public PortletInvoker getPortletInvoker(PortletDefinition portletDefinition)
    {
        PortalContext pc = Jetspeed.getContext();
        JetspeedPortletInvoker invoker = null;
        
        MutablePortletApplication app = (MutablePortletApplication)portletDefinition.getPortletApplicationDefinition();
        if (app.getApplicationType() == MutablePortletApplication.LOCAL)
        {
            System.out.println("$$$$$$ LOCAL INVOKIN " + portletDefinition.getName());
            
            // TODO: pooling        

            try 
            {
                String className = pc.getConfigurationProperty(INVOKER_LOCAL);
                invoker = (JetspeedPortletInvoker)Class.forName(className).newInstance();
                invoker.activate(portletDefinition, servletConfig);
                return invoker;
            }
            catch (Throwable t)
            {
                log.error("failed to create LOCAL invoker, using default", t);
                // try default
                invoker = new LocalPortletInvoker();
                invoker.activate(portletDefinition, servletConfig);
                return invoker;
            }                                
        }            
        
        // TODO: pooling
        System.out.println("$$$$$$ EXTERNAL INVOKIN " + portletDefinition.getName());
        
        try 
        {
            String className = pc.getConfigurationProperty(INVOKER_SERVLET);
            //PortletInvoker invoker = new ServletPortletInvoker(portletDefinition, servletConfig);            
            invoker = (JetspeedPortletInvoker)Class.forName(className).newInstance();
            invoker.activate(portletDefinition, servletConfig);
            return invoker;
        }
        catch (Throwable t)
        {
            log.error("failed to create SERVLET invoker, using default", t);            
            invoker = new ServletPortletInvoker();
            invoker.activate(portletDefinition, servletConfig);
            return invoker;            
        }                           
    }
    
}
