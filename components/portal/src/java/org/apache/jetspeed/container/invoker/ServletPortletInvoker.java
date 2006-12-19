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

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.container.PortletRequestContext;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.aggregator.Worker;
import org.apache.jetspeed.aggregator.CurrentWorkerContext;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

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
     * @see org.apache.jetspeed.container.invoker.JetspeedPortletInvoker#activate(PortletFactory,org.apache.pluto.om.portlet.PortletDefinition, javax.servlet.ServletConfig)
     */
    public void activate(PortletFactory portletFactory, PortletDefinition portletDefinition, ServletConfig servletConfig)
    {
        this.portletFactory = portletFactory;
        this.jetspeedConfig = servletConfig;
        jetspeedContext = servletConfig.getServletContext();
        this.portletDefinition = portletDefinition;
        activated = true;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.invoker.JetspeedPortletInvoker#activate(PortletFactory,org.apache.pluto.om.portlet.PortletDefinition, javax.servlet.ServletConfig, java.lang.String)
     */
    public void activate(PortletFactory portletFactory, PortletDefinition portletDefinition, ServletConfig servletConfig, String servletMappingName)
    {
        this.servletMappingName = servletMappingName;
        activate(portletFactory, portletDefinition, servletConfig);
    }

    /**
     *
     * @param request
     * @param response
     * @throws PortletException
     */
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        invoke(request, response, ContainerConstants.METHOD_RENDER);
    }

    /**
     *
     */
    public void action(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        invoke(request, response, ContainerConstants.METHOD_ACTION);
    }

    /**
     *
     */
    public void load(PortletRequest request, RenderResponse response) throws PortletException
    {
        try
        {
            invoke(request, response, ContainerConstants.METHOD_NOOP);
        }
        catch (IOException e)
        {
            log.error("ServletPortletInvokerImpl.load() - Error while dispatching portlet.", e);
            throw new PortletException(e);
        }
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
    protected void invoke(PortletRequest portletRequest, PortletResponse portletResponse, Integer methodID)
        throws PortletException, IOException
    {
        // In case of parallel mode, the portletDefinition member is not thread-safe.
        // So, hide the member variable by the following local variable.
        PortletDefinition portletDefinition = null;

        // In case of parallel mode, get portlet definition object from the worker thread context.
        // Otherwise, refer the member variable.
        boolean isParallelMode = (Thread.currentThread() instanceof Worker);

        if (isParallelMode)
        {
            portletDefinition = (PortletDefinition) CurrentWorkerContext.getAttribute(PortalReservedParameters.PORTLET_DEFINITION_ATTRIBUTE);
        }
        else
        {
            portletDefinition = this.portletDefinition;
        }
        
        ClassLoader paClassLoader = portletFactory.getPortletApplicationClassLoader((PortletApplication)portletDefinition.getPortletApplicationDefinition());

        MutablePortletApplication app = (MutablePortletApplication)portletDefinition.getPortletApplicationDefinition();

        WebApplicationDefinition webApplicationDefinition = app.getWebApplicationDefinition();
        if(webApplicationDefinition == null)
        {
        	throw new IllegalStateException("Portlet application "+app.getName()+ " has no associated web application.");
        }
        String portletApplicationName = webApplicationDefinition.getContextRoot();

        // gather all required data from request and response
        ServletRequest servletRequest = ((HttpServletRequestWrapper)((HttpServletRequestWrapper)((HttpServletRequestWrapper)portletRequest).getRequest()).getRequest()).getRequest();

        ServletResponse servletResponse = ((HttpServletResponseWrapper) portletResponse).getResponse();

        ServletContext appContext = jetspeedContext.getContext(portletApplicationName);
        if (null == appContext)
        {
            String message = "Failed to find Servlet context for Portlet Application: " + portletApplicationName;
            log.error(message);
            throw new PortletException(message);
        }
        PortletInstance portletInstance = portletFactory.getPortletInstance(appContext, portletDefinition);
        RequestDispatcher dispatcher = appContext.getRequestDispatcher(servletMappingName);
        if (null == dispatcher)
        {
            String message =
                "Failed to get Request Dispatcher for Portlet Application: "
                    + portletApplicationName
                    + ", servlet: "
                    + servletMappingName;
            log.error(message);
            throw new PortletException(message);
        }

        try
        {
            servletRequest.setAttribute(ContainerConstants.PORTLET, portletInstance);
            servletRequest.setAttribute(ContainerConstants.PORTLET_CONFIG, portletInstance.getConfig());
            servletRequest.setAttribute(ContainerConstants.PORTLET_REQUEST, portletRequest);
            servletRequest.setAttribute(ContainerConstants.PORTLET_RESPONSE, portletResponse);
            servletRequest.setAttribute(ContainerConstants.METHOD_ID, methodID);
            servletRequest.setAttribute(ContainerConstants.PORTLET_NAME, app.getName()+"::"+portletDefinition.getName());
            RequestContext requestContext = (RequestContext)servletRequest.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
            servletRequest.setAttribute(ContainerConstants.PORTAL_CONTEXT, requestContext.getRequest().getContextPath());

            // Store same request attributes into the worker in parallel mode.
            if (isParallelMode)
            {
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET, portletInstance);
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET_CONFIG, portletInstance.getConfig());
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET_REQUEST, portletRequest);
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET_RESPONSE, portletResponse);
                CurrentWorkerContext.setAttribute(ContainerConstants.METHOD_ID, methodID);
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTLET_NAME, app.getName()+"::"+portletDefinition.getName());
                CurrentWorkerContext.setAttribute(ContainerConstants.PORTAL_CONTEXT, requestContext.getRequest().getContextPath());                
            }

            PortletRequestContext.createContext(portletDefinition, portletInstance, portletRequest, portletResponse);
            dispatcher.include(servletRequest, servletResponse);
            
        }
        catch (Exception e)
        {
            String message =
                "Failed to dispatch.include for Portlet Application: " + portletApplicationName + ", servlet: " + servletMappingName;
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
