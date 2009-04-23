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
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.FilterManager;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.pluto.container.PortletInvokerService;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.PortletResponseContext;

/**
 * LocalPortletInvoker invokes local (internal) portlet applications.
 * Local portlet applications are stored within the Jetspeed Portlet application.
 * They are not separate web applications; but are stored under Jetspeed's
 * WEB-INF/apps directory.
 * <h3>Sample Configuration</h3>
 * <pre>
 * <code>
 * factory.invoker.local = org.apache.jetspeed.container.invoker.LocalPortletInvoker
 * factory.invoker.local.pool.size = 50
 * </code> 
 * </pre>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class LocalPortletInvoker implements JetspeedPortletInvoker
{
    protected PortalContext portalContext;
    protected PortletFactory portletFactory;
    protected ServletContext jetspeedContext;
    protected ServletConfig jetspeedConfig;
    protected PortletDefinition portletDefinition;
    protected boolean activated = false;
    private ContainerRequestResponseUnwrapper requestResponseUnwrapper;
    
    public LocalPortletInvoker(PortalContext portalContext, ContainerRequestResponseUnwrapper requestResponseUnwrapper)
    {
        this.portalContext = portalContext;
        this.requestResponseUnwrapper = requestResponseUnwrapper;
        activated = false;
    }
    
    public void activate(PortletFactory portletFactory, PortletDefinition portletDefinition, ServletConfig servletConfig)
    {
        this.portletFactory = portletFactory;
        this.jetspeedConfig = servletConfig;
        jetspeedContext = servletConfig.getServletContext();
        this.portletDefinition = portletDefinition;
        activated = true;        
    }

    public void passivate()
    {
        activated = false;    
    }
    
    public boolean isActivated()
    {
        return activated;
    }
    
    /**
     * Invokes the specific request denoted by the <code>method</code> parameter on a portlet.
     * The portlet is invoked with a direct method call on the portlet. It is not invoked in another web application.
     * This requires manipulation of the current thread's classpath.
     * 
     * @param requestContext
     * @param portletRequest
     * @param portletResponse
     * @param action
     * @param filter
     * @throws PortletException
     * @throws IOException
     */
    public void invoke(PortletRequestContext requestContext, PortletRequest portletRequest, PortletResponse portletResponse,
                       PortletWindow.Action action, FilterManager filter)
            throws PortletException, IOException
    {
        if (PortletWindow.Action.ACTION != action && PortletWindow.Action.RENDER != action)
        {
            return;
        }
        
        PortletWindowImpl window = (PortletWindowImpl)requestContext.getPortletWindow();
        
        PortletApplication pa = portletDefinition.getApplication();
        pa.setLocalContextPath(portalContext.getContextPath());

        ClassLoader paClassLoader = portletFactory.getPortletApplicationClassLoader(pa);
        PortletInstance portletInstance = portletFactory.getPortletInstance(jetspeedContext, portletDefinition);
        
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            PortletResponseContext responseContext = (PortletResponseContext)portletRequest.getAttribute(PortletInvokerService.RESPONSE_CONTEXT);
            ((JetspeedRequestContext)window.getRequestContext()).setCurrentPortletWindow(window);
            
            window.setInvocationState(action, requestContext, responseContext, portletRequest, portletResponse, portletInstance);
            window.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, window.getFragment());
            window.setAttribute(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE, window);
            window.setAttribute(PortalReservedParameters.PORTLET_DEFINITION_ATTRIBUTE, portletDefinition);
            
            // initialize request/response for portletRequestContext
            HttpServletRequest request = requestContext.getContainerRequest();
            HttpServletResponse response = requestContext.getContainerResponse();
            
            request = (HttpServletRequest) requestResponseUnwrapper.unwrapContainerRequest(request);
            response = (HttpServletResponse) requestResponseUnwrapper.unwrapContainerResponse(response);
            requestContext.init(window.getPortletInstance().getConfig(), this.jetspeedContext, request, response);
            window.getPortletResponseContext().init(request, response);
            
            Thread.currentThread().setContextClassLoader(paClassLoader);
            
            if (PortletWindow.Action.ACTION == action)
            {
                ActionRequest actionRequest = (ActionRequest) portletRequest;
                ActionResponse actionResponse = (ActionResponse) portletResponse;

                portletInstance.processAction(actionRequest, actionResponse);
            }
            else // if (PortletWindow.Action.RENDER == action)
            {
                RenderRequest renderRequest = (RenderRequest) portletRequest;
                RenderResponse renderResponse = (RenderResponse) portletResponse;
                renderResponse.setContentType(window.getRequestContext().getMimeType());
                portletInstance.render(renderRequest, renderResponse);
            }
        }
        catch (Throwable t)
        {
            if ( t instanceof UnavailableException )
            {
                // take it out of service
                try
                {
                    portletInstance.destroy();
                }
                catch (Throwable ignore)
                {
                    // never mind, it won't be used anymore
                }
            }
            if ( t instanceof PortletException )
            {
                throw (PortletException)t;
            }
            if ( t instanceof IOException )
            {
                throw (IOException)t;
            }
            else
            {
                throw new PortletException(t);
            }
        }
        finally
        {
            ((JetspeedRequestContext)window.getRequestContext()).setCurrentPortletWindow(null);
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }
}