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
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.pluto.container.PortletContainerException;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.FilterManager;
import org.apache.pluto.container.PortletInvokerService;

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
    private PortalContext portalContext;
    private PortletFactory portletFactory;
    private ContainerRequestResponseUnwrapper requestResponseUnwrapper;
    private String servletMappingName;
    
    public JetspeedPortletInvokerService(ServletConfig servletConfig, PortalContext portalContext, PortletFactory portletFactory)
    {
        this(servletConfig, portalContext, portletFactory, new DefaultContainerRequestResponseUnwrapper());
    }
    
    public JetspeedPortletInvokerService(ServletConfig servletConfig, PortalContext portalContext, 
                                         PortletFactory portletFactory, ContainerRequestResponseUnwrapper requestResponseUnwrapper)
    {
        this.servletConfig = servletConfig;
        this.portalContext = portalContext;
        this.portletFactory = portletFactory;
        this.requestResponseUnwrapper = requestResponseUnwrapper;
        this.servletMappingName = portalContext.getConfigurationProperty(INVOKER_SERVLET_MAPPING_NAME, DEFAULT_MAPPING_NAME);
    }
    
    public void action(PortletRequestContext requestContext, ActionRequest request, ActionResponse response, FilterManager filterManager)
    throws IOException, PortletException, PortletContainerException
    {
        getInvoker(requestContext).invoke(requestContext, 
                                          request, response, 
                                          PortletWindow.Action.ACTION, 
                                          (org.apache.jetspeed.container.FilterManager)filterManager);
    }

    public void admin(PortletRequestContext requestContext, PortletRequest request, PortletResponse response)
    throws IOException, PortletException, PortletContainerException
    {
        throw new PortletContainerException("Unsupported action ADMIN");
    }

    public void event(PortletRequestContext requestContext, EventRequest request, EventResponse response, FilterManager filterManager)
    throws IOException, PortletException, PortletContainerException
    {
        getInvoker(requestContext).invoke(requestContext, 
                                          request, response, 
                                          PortletWindow.Action.EVENT, 
                                          (org.apache.jetspeed.container.FilterManager)filterManager);
    }

    public void load(PortletRequestContext requestContext, PortletRequest request, PortletResponse response)
    throws IOException, PortletException, PortletContainerException
    {
        getInvoker(requestContext).invoke(requestContext, 
                                          request, response, 
                                          PortletWindow.Action.LOAD, null);
    }

    public void render(PortletRequestContext requestContext, RenderRequest request, RenderResponse response, FilterManager filterManager)
    throws IOException, PortletException, PortletContainerException
    {
        getInvoker(requestContext).invoke(requestContext, 
                                          request, response, 
                                          PortletWindow.Action.RENDER, 
                                          (org.apache.jetspeed.container.FilterManager)filterManager);
    }

    public void serveResource(PortletRequestContext requestContext, ResourceRequest request, ResourceResponse response, FilterManager filterManager)
    throws IOException, PortletException, PortletContainerException
    {
        getInvoker(requestContext).invoke(requestContext, 
                                          request, response, 
                                          PortletWindow.Action.RESOURCE, 
                                          (org.apache.jetspeed.container.FilterManager)filterManager);
    }

    protected JetspeedPortletInvoker getInvoker(PortletRequestContext requestContext)
    {
        JetspeedPortletInvoker invoker;
        PortletDefinition portletDefinition = (PortletDefinition)requestContext.getPortletWindow().getPortletDefinition();
        if (portletDefinition.getApplication().getApplicationType() == PortletApplication.LOCAL)
        {
            invoker = new LocalPortletInvoker(portalContext, requestResponseUnwrapper);
        }
        else
        {
            invoker =  new ServletPortletInvoker(requestResponseUnwrapper, servletMappingName);

        }
        invoker.activate(portletFactory, portletDefinition, servletConfig);
        return invoker;        
    }
}