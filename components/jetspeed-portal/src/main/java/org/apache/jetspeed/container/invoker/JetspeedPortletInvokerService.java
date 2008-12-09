/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletConfig;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.pluto.PortletWindow;
import org.apache.pluto.spi.FilterManager;
import org.apache.pluto.spi.optional.PortletInvokerService;

/**
 * <p>
 * Implements Pluto's portlet invoker service interfacem creating portlet invokers based on the servlet context.
 * This class is part of the contract between Pluto and the Jetspeed Portal 
 * The Pluto container uses portlet invokers to abstract access to portlets.
 * An invoker interfaces defines which actions are performed between the portal and container,
 * namely action, render and optionally load. 
 * </p>
 * <p>
 * The Jetspeed portlet invoker services supports two kinds of invokers: local and servlet.
 * Local portlet invokers call portlets located in the same web applications.
 * With local invokers, a simple java method invocation is called on the portlet.
 * Servlet portlet invokers call portlets located in another web application.
 * With servlet invokers, the servlet request dispatcher is used to call methods on the portlet. 
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortletInvokerFactoryImpl.java 706843 2008-10-22 01:34:10Z taylor $
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedPortletInvokerService implements PortletInvokerService
{
    public final static String INVOKER_SERVLET_MAPPING_NAME = "factory.invoker.servlet.mapping.name";
    public final static String DEFAULT_MAPPING_NAME = "/container";
    
    private ServletConfig servletConfig;
    private PortletFactory portletFactory;
    private PortletRequestResponseUnwrapper unwrapper;
    private String servletMappingName;
    
    public JetspeedPortletInvokerService(ServletConfig servletConfig, PortalContext portalContext, 
            PortletFactory portletFactory, PortletRequestResponseUnwrapper unwrapper)
    {
        this.servletConfig = servletConfig;
        this.portletFactory = portletFactory;
        this.unwrapper = unwrapper;
        this.servletMappingName = portalContext.getConfigurationProperty(INVOKER_SERVLET_MAPPING_NAME, DEFAULT_MAPPING_NAME);                                
    }
   
    public void action(ActionRequest request,
            ActionResponse response, PortletWindow window, FilterManager filter)
            throws IOException, PortletException
    {
        JetspeedPortletInvoker invoker = getInvoker(window);
        invoker.invoke(request, response, ContainerConstants.METHOD_ACTION, filter);
    }

    public void admin(PortletRequest request,
            PortletResponse response, PortletWindow window) throws IOException,
            PortletException
    {
        JetspeedPortletInvoker invoker = getInvoker(window);
        invoker.invoke(request, response, ContainerConstants.METHOD_ADMIN, null);        
    }

    public void event(EventRequest request,
            EventResponse response, PortletWindow window, FilterManager filter)
            throws IOException, PortletException
    {
        JetspeedPortletInvoker invoker = getInvoker(window);
        invoker.invoke(request, response, ContainerConstants.METHOD_EVENT, filter);
    }

    public void load(PortletRequest request,
            PortletResponse response, PortletWindow window) throws IOException,
            PortletException
    {
        JetspeedPortletInvoker invoker = getInvoker(window);
        invoker.invoke(request, response, ContainerConstants.METHOD_NOOP, null);
    }   
    
    public void render(RenderRequest request,
            RenderResponse response, PortletWindow window, FilterManager filter)
            throws IOException, PortletException
    {
        JetspeedPortletInvoker invoker = getInvoker(window);
        invoker.invoke(request, response, ContainerConstants.METHOD_RENDER, filter);
    }

    public void serveResource(ResourceRequest request,
            ResourceResponse response, PortletWindow window, FilterManager filter)
            throws IOException, PortletException
    {
        JetspeedPortletInvoker invoker = getInvoker(window);
        invoker.invoke(request, response, ContainerConstants.METHOD_RESOURCE, filter);
    }
    
    protected JetspeedPortletInvoker getInvoker(PortletWindow window)
    {
        PortletDefinition portletDefinition = (PortletDefinition)window.getPortletEntity().getPortletDefinition();
        PortletApplication app = (PortletApplication)portletDefinition.getApplication();
        JetspeedPortletInvoker invoker;
        if (app.getApplicationType() == PortletApplication.LOCAL)
        {
            invoker = new LocalPortletInvoker();
        }
        else
        {
            invoker =  new ServletPortletInvoker(this.unwrapper, servletMappingName);

        }
        invoker.activate(portletFactory, portletDefinition, servletConfig);
        return invoker;        
    }
    
}
