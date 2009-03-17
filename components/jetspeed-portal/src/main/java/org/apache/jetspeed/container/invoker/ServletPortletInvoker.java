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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.CurrentWorkerContext;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.container.PortletRequestContext;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.pluto.container.FilterManager;

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
    private final static Log log = LogFactory.getLog(ServletPortletInvoker.class);

    protected PortletFactory portletFactory;
    protected ServletContext jetspeedContext;
    protected ServletConfig jetspeedConfig;
    protected PortletDefinition portletDefinition;
    protected boolean activated = false;
    protected String servletMappingName;
    
    /**
     * requestResponseUnwrapper used to unwrap portlet request or portlet response
     * to find the real servlet request or servlet response.
     */
    protected PortletRequestResponseUnwrapper requestResponseUnwrapper;
    
    public ServletPortletInvoker(PortletRequestResponseUnwrapper requestResponseUnwrapper, String servletMappingName)
    {
        this.requestResponseUnwrapper = requestResponseUnwrapper;
        this.servletMappingName = servletMappingName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.invoker.JetspeedPortletInvoker#passivate()
     */
    public void passivate()
    {
        activated = false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.invoker.JetspeedPortletInvoker#isActivated()
     */
    public boolean isActivated()
    {
        return activated;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.invoker.JetspeedPortletInvoker#activate(PortletFactory,org.apache.pluto.container.om.portlet.PortletDefinition, javax.servlet.ServletConfig)
     */
    public void activate(PortletFactory portletFactory, PortletDefinition portletDefinition, ServletConfig servletConfig)
    {
        this.portletFactory = portletFactory;
        this.jetspeedConfig = servletConfig;
        jetspeedContext = servletConfig.getServletContext();
        this.portletDefinition = portletDefinition;
        activated = true;
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
    public void invoke(PortletRequest portletRequest, PortletResponse portletResponse, Integer methodID, FilterManager filter)
        throws PortletException, IOException
    {
        // In case of parallel mode, the portletDefinition member is not thread-safe.
        // So, hide the member variable by the following local variable.
        PortletDefinition portletDefinition = null;

        // In case of parallel mode, get portlet definition object from the worker thread context.
        // Otherwise, refer the member variable.
        boolean isParallelMode = CurrentWorkerContext.getParallelRenderingMode();

        if (isParallelMode)
        {
            portletDefinition = (PortletDefinition) CurrentWorkerContext.getAttribute(PortalReservedParameters.PORTLET_DEFINITION_ATTRIBUTE);
        }
        
        if (portletDefinition == null)
        {
            portletDefinition = this.portletDefinition;
        }
        
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
            String message =
                "Failed to get Request Dispatcher for Portlet Application: "
                    + appContextPath
                    + ", servlet: "
                    + servletMappingName;
            log.error(message);
            throw new PortletException(message);
        }

        // gather all required data from request and response
        ServletRequest servletRequest = this.requestResponseUnwrapper.unwrapPortletRequest(portletRequest);
        ServletResponse servletResponse = this.requestResponseUnwrapper.unwrapPortletResponse(portletResponse);
        boolean useForward = servletRequest.getAttribute(PortalReservedParameters.PORTLET_CONTAINER_INVOKER_USE_FORWARD) != null;

        try
        {
            RequestContext requestContext = (RequestContext) servletRequest.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
            
            if (isParallelMode)
            {
                synchronized (servletRequest)
                {
                    servletRequest.setAttribute(ContainerConstants.PORTLET, portletInstance);
                    servletRequest.setAttribute(ContainerConstants.PORTLET_CONFIG, portletInstance.getConfig());
                    servletRequest.setAttribute(ContainerConstants.PORTLET_REQUEST, portletRequest);
                    servletRequest.setAttribute(ContainerConstants.PORTLET_RESPONSE, portletResponse);
                    servletRequest.setAttribute(ContainerConstants.METHOD_ID, methodID);
                    servletRequest.setAttribute(ContainerConstants.PORTLET_NAME, app.getName()+"::"+portletDefinition.getPortletName());
                    servletRequest.setAttribute(ContainerConstants.PORTAL_CONTEXT, ((HttpServletRequest) servletRequest).getContextPath());
                }
            }
            else
            {
                servletRequest.setAttribute(ContainerConstants.PORTLET, portletInstance);
                servletRequest.setAttribute(ContainerConstants.PORTLET_CONFIG, portletInstance.getConfig());
                servletRequest.setAttribute(ContainerConstants.PORTLET_REQUEST, portletRequest);
                servletRequest.setAttribute(ContainerConstants.PORTLET_RESPONSE, portletResponse);
                servletRequest.setAttribute(ContainerConstants.METHOD_ID, methodID);
                servletRequest.setAttribute(ContainerConstants.PORTLET_NAME, app.getName()+"::"+portletDefinition.getPortletName());
                servletRequest.setAttribute(ContainerConstants.PORTAL_CONTEXT, requestContext.getRequest().getContextPath());
            }

            // Store same request attributes into the worker in parallel mode.
            if (isParallelMode)
            {
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET, portletInstance);
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET_CONFIG, portletInstance.getConfig());
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET_REQUEST, portletRequest);
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET_RESPONSE, portletResponse);
                CurrentWorkerContext.setAttribute(ContainerConstants.METHOD_ID, methodID);
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET_NAME, app.getName()+"::"+portletDefinition.getPortletName());
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTAL_CONTEXT, ((HttpServletRequest) servletRequest).getContextPath());                
            }

            PortletRequestContext.createContext(portletDefinition, portletInstance, portletRequest, portletResponse);
            if (useForward)
            {
                dispatcher.forward(servletRequest, servletResponse);
            }
            else
            {
                dispatcher.include(servletRequest, servletResponse);
            }
            
        }
        catch (Exception e)
        {
            String message =
                "Failed to dispatch."+(useForward?"forward":"include")+" for Portlet Application: " + appContextPath + ", servlet: " + servletMappingName;
            log.error(message, e);
            throw new PortletException(message, e);
        }
        finally
        {
            PortletRequestContext.clearContext();

            // In parallel mode, remove all attributes of worker context.
            if (isParallelMode)
            {
                CurrentWorkerContext.removeAttribute(ContainerConstants.PORTLET);
                CurrentWorkerContext.removeAttribute(ContainerConstants.PORTLET_CONFIG);
                CurrentWorkerContext.removeAttribute(ContainerConstants.PORTLET_REQUEST);
                CurrentWorkerContext.removeAttribute(ContainerConstants.PORTLET_RESPONSE);
                CurrentWorkerContext.removeAttribute(ContainerConstants.METHOD_ID);
                CurrentWorkerContext.removeAttribute(ContainerConstants.PORTLET_NAME);
                CurrentWorkerContext.removeAttribute(ContainerConstants.PORTAL_CONTEXT);
            }

            servletRequest.removeAttribute(ContainerConstants.PORTLET);
            servletRequest.removeAttribute(ContainerConstants.PORTLET_CONFIG);
            servletRequest.removeAttribute(ContainerConstants.PORTLET_REQUEST);
            servletRequest.removeAttribute(ContainerConstants.PORTLET_RESPONSE);
            servletRequest.removeAttribute(ContainerConstants.METHOD_ID);
            servletRequest.removeAttribute(ContainerConstants.PORTLET_NAME);
            servletRequest.removeAttribute(ContainerConstants.PORTAL_CONTEXT);
        }

    }

}
