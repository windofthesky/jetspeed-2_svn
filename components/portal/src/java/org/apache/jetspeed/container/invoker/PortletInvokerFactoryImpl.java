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
package org.apache.jetspeed.container.invoker;

import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.pluto.factory.PortletInvokerFactory;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * Portlet Invoker Factory creates portlet invokers based on the servlet context.
 * This class is part of the contract between Pluto and the Jetspeed Portal as defined
 * in the interfaces under <code>org.apache.pluto.factory</code>
 * The Pluto container uses portlet invokers to abstract access to portlets.
 * An invoker interfaces defines which actions are performed between the portal and container,
 * namely action, render and optionally load. Portlet invoker factories are implemented by
 * the portal implementation. The Pluto container uses pluggable portlet invoker factories
 * in order to get portlet invokers, and then invoke methods on portlets (render, action, load). 
 * </p>
 * <p>
 * The Portlet Invoker Factory is a Pluto factory. Pluto defines a basic lifecycle for Pluto
 * factory services in the <code>org.apach.pluto.factory.Factory</code> interface with
 * standard <code>init</code> and <code>destroy</code> methods.
 * </p>
 * <p>
 * The Jetspeed portlet invoker factory supports two kinds of invokers: local and servlet.
 * Local portlet invokers call portlets located in the same web applications.
 * With local invokers, a simple java method invocation is called on the portlet.
 * Servlet portlet invokers call portlets located in another web application.
 * With servlet invokers, the servlet request dispatcher is used to call methods on the portlet. 
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletInvokerFactoryImpl
    implements PortletInvokerFactory
{
    
    public final static String INVOKER_SERVLET_MAPPING_NAME = "factory.invoker.servlet.mapping.name";
    public final static String DEFAULT_MAPPING_NAME = "/container";
    
    private final static Log log = LogFactory.getLog(PortletInvokerFactoryImpl.class);

    /** The servlet configuration for the Jetspeed portal */
    private final ServletConfig servletConfig;

    private final PortalContext portalContext;
    
    private final PortletFactory portletFactory;
    
    private final ServletPortletInvokerFactory servletPortletInvokerFactory;
    
    private final LocalPortletInvokerFactory localPortletInvokerFactory;
            
    public PortletInvokerFactoryImpl(ServletConfig servletConfig, PortalContext portalContext, 
            PortletFactory portletFactory, ServletPortletInvokerFactory servletPortletInvokerFactory, LocalPortletInvokerFactory localPortletInvokerFactory)
    {
        this.servletConfig = servletConfig;        
        this.portalContext = portalContext;        
        this.portletFactory = portletFactory;
        this.servletPortletInvokerFactory = servletPortletInvokerFactory;
        this.localPortletInvokerFactory = localPortletInvokerFactory;                
    }
               
    /* (non-Javadoc)
     * @see org.apache.pluto.factory.Factory#init(javax.servlet.ServletConfig, java.util.Map)
     */
    public void init(ServletConfig config, Map properties)
    throws Exception
    {
        // does absolutely nothing
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.factory.Factory#destroy()
     */
    public void destroy()
    throws Exception
    {
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.factory.PortletInvokerFactory#getPortletInvoker(org.apache.pluto.om.portlet.PortletDefinition)
     */
    public PortletInvoker getPortletInvoker(PortletDefinition portletDefinition)
    {
        MutablePortletApplication app = (MutablePortletApplication)portletDefinition.getPortletApplicationDefinition();
        if(app == null)
        {
        	throw new IllegalStateException("Portlet definition \""+portletDefinition.getName()+"\" is not assigned to a portlet application.");
        }
        
        if (app.getApplicationType() == MutablePortletApplication.LOCAL)
        {
            LocalPortletInvoker localPortletInvoker = localPortletInvokerFactory.createInstance();
            localPortletInvoker.activate(portletFactory, portletDefinition, servletConfig);
            return localPortletInvoker;           
        }
        else
        {             
            ServletPortletInvoker servletPortletInvoker = servletPortletInvokerFactory.createInstance();
            String servletMappingName = portalContext.getConfigurationProperty(INVOKER_SERVLET_MAPPING_NAME, DEFAULT_MAPPING_NAME);            
            servletPortletInvoker.activate(portletFactory, portletDefinition, servletConfig, servletMappingName);            
            return servletPortletInvoker;
        }

    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.factory.PortletInvokerFactory#releasePortletInvoker(org.apache.pluto.invoker.PortletInvoker)
     */
    public void releasePortletInvoker(PortletInvoker invoker)
    {
        // this is now taken care off by Spring's CommonsPoolingTargetSource
//        try
//        {
//            if (invoker instanceof ServletPortletInvoker)
//            {
//                servletInvokerFactory.releaseObject(invoker);                
//            }
//            else
//            {
//                localInvokerFactory.releaseObject(invoker);                            
//            }
//        }
//        catch (Exception e)
//        {
//            log.error(e);
//        }
    }
    
}
