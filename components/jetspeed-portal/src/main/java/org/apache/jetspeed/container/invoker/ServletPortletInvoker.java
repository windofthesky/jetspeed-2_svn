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

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * ServletPortletInvoker invokes portlets in another web application, calling a 
 * portlet's render or action method via a cross context request dispatcher.
 * In order for this class to work, a servlet must be special servlet must be 
 * infused into the web (portlet) application. This servlet knows how to delegate
 * to portlets and package their response back into a servlet response.
 * The context name of the servlet should be configurable. The default context name is "/container"
 * ServletPortletInvokerFactory is the factory for creating portlet invokers that 
 * use Jetspeed Container servlet. 
 * <h3>Sample Factory Configuration</h3>
 * <pre>
 * <code>
 * factory.invoker.servlet = org.apache.jetspeed.container.invoker.ServletPortletInvoker
 * factory.invoker.servlet.pool.size = 50
 * factory.invoker.servlet.mapping.name = /container
 * </code> 
 * </pre>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletPortletInvoker implements JetspeedPortletInvoker
{
    private final static Logger log = LoggerFactory.getLogger(ServletPortletInvoker.class);

    protected PortletFactory portletFactory;
    protected ServletContext jetspeedContext;
    protected ServletConfig jetspeedConfig;
    protected PortletDefinition portletDefinition;
    protected boolean activated = false;
    protected String servletMappingName;
    
    /**
     * requestResponseUnwrapper used to unwrap container request or portlet response
     * to find the real servlet request or servlet response.
     */
    protected ContainerRequestResponseUnwrapper requestResponseUnwrapper;

    public ServletPortletInvoker(ContainerRequestResponseUnwrapper requestResponseUnwrapper, String servletMappingName)
    {
        this.requestResponseUnwrapper = requestResponseUnwrapper;
        this.servletMappingName = servletMappingName;
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
     * Creates a servlet request dispatcher to dispatch to another web application to render the portlet.
     * NOTE: this method requires that your container supports cross-context dispatching.
     * Cross-context dispatching is known to work on Tomcat, Catalina, Tomcat-5.
     *
     * @param portletRequest
     * @param portletResponse
     * @param methodID
     * @throws PortletException
     * @throws IOException
     */
    public void invoke(PortletRequestContext requestContext, PortletRequest portletRequest, PortletResponse portletResponse, 
                       PortletWindow.Action action, FilterManager filter)
        throws PortletException, IOException
    {
        PortletWindowImpl window = (PortletWindowImpl)requestContext.getPortletWindow();
        PortletDefinition portletDefinition = window.getPortletDefinition();
        PortletApplication app = portletDefinition.getApplication();

        String appContextPath = app.getContextPath();

        ServletContext appContext = jetspeedContext.getContext(appContextPath);
        if (null == appContext)
        {
            String message = "Failed to find Servlet context for Portlet Application: " + appContextPath;
            log.error(message);
            throw new PortletException(message);
        }
        PortletInstance portletInstance = portletFactory.getPortletInstance(appContext, portletDefinition);
        RequestDispatcher dispatcher = appContext.getRequestDispatcher(servletMappingName);
        if (null == dispatcher)
        {
            String message = "Failed to get Request Dispatcher for Portlet Application: "+appContextPath+", servlet: "+servletMappingName;
            log.error(message);
            throw new PortletException(message);
        }

        boolean useForward = window.getAttribute(PortalReservedParameters.PORTLET_CONTAINER_INVOKER_USE_FORWARD) != null;

        try
        {
            PortletResponseContext responseContext = (PortletResponseContext)portletRequest.getAttribute(PortletInvokerService.RESPONSE_CONTEXT);
            ((JetspeedRequestContext)window.getRequestContext()).setCurrentPortletWindow(window);
            window.setInvocationState(action, requestContext, responseContext, portletRequest, portletResponse, portletInstance);
            window.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, window.getFragment());
            window.setAttribute(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE, window);
            window.setAttribute(PortalReservedParameters.PORTLET_DEFINITION_ATTRIBUTE, portletDefinition);
            window.setAttribute(PortalReservedParameters.PORTLET_FILTER_MANAGER_ATTRIBUTE, filter);

            ServletRequest request = this.requestResponseUnwrapper.unwrapContainerRequest(requestContext.getContainerRequest());
            ServletResponse response = this.requestResponseUnwrapper.unwrapContainerResponse(requestContext.getContainerResponse());
            
            if (useForward)
            {
                dispatcher.forward(request, response);
            }
            else
            {
                dispatcher.include(request, response);
            }
            
        }
        catch (Exception e)
        {
            String message =
                "Failed to dispatch."+(useForward?"forward":"include")+" for Portlet Application: "+appContextPath+", servlet: "+servletMappingName;
            log.error(message, e);
            throw new PortletException(message, e);
        }
        finally
        {
            ((JetspeedRequestContext)window.getRequestContext()).setCurrentPortletWindow(null);
        }
    }
}