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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.portlet.ActionResponse;
import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.core.InternalPortletRequest;
import org.apache.pluto.core.InternalPortletResponse;
import org.apache.pluto.core.impl.PortletConfigImpl;
import org.apache.pluto.portlet.PortletUtils;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.container.PortletContextFactory;

/**
 * Portlet Invoker implementation, invokes the JetspeedContainerServlet
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletPortletInvoker implements PortletInvoker
{
    public static final String MVC_ENTRY_SERVLET = "/container";

    private final static Log log = LogFactory.getLog(ServletPortletInvoker.class);

    protected ServletContext jetspeedContext;
    private PortletDefinition portletDefinition;

    public ServletPortletInvoker(PortletDefinition portletDefinition, ServletConfig servletConfig)
    {
        System.out.println("%%% invoker.portletdef = " + portletDefinition);

        jetspeedContext = servletConfig.getServletContext();
        this.portletDefinition = portletDefinition;
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
            log.error("PortletInvokerImpl.load() - Error while dispatching portlet.", e);
            throw new PortletException(e);
        }
    }

    /**
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
        PortletApplicationDefinition app = portletDefinition.getPortletApplicationDefinition();
        System.out.println("%%% invoker.pa = " + app);

        String portletApplicationName = app.getWebApplicationDefinition().getContextRoot();
        //String portletApplicationName = "/HW_App";

        InternalPortletRequest internalPortletRequest = PortletUtils.getInternalRequest(portletRequest);

        InternalPortletResponse internalPortletResponse = PortletUtils.getInternalResponse(portletResponse);

        // gather all required data from request and response
        ServletRequest servletRequest = ((javax.servlet.http.HttpServletRequestWrapper) internalPortletRequest).getRequest();

        ServletResponse servletResponse = ((javax.servlet.http.HttpServletResponseWrapper) internalPortletResponse).getResponse();

        ServletContext appContext = jetspeedContext.getContext(portletApplicationName);
        if (null == appContext)
        {
            String message = "Failed to find Servlet context for Portlet Application: " + portletApplicationName;
            log.error(message);
            throw new PortletException();
        }

        RequestDispatcher dispatcher = appContext.getRequestDispatcher(MVC_ENTRY_SERVLET);
        if (null == dispatcher)
        {
            String message =
                "Failed to get Request Dispatcher for Portlet Application: "
                    + portletApplicationName
                    + ", servlet: "
                    + MVC_ENTRY_SERVLET;
            log.error(message);
            throw new PortletException(message);
        }

        try
        {
            servletRequest.setAttribute(ContainerConstants.METHOD_ID, methodID);

            servletRequest.setAttribute(ContainerConstants.PORTLET_REQUEST, portletRequest);
            servletRequest.setAttribute(ContainerConstants.PORTLET_RESPONSE, portletResponse);
            servletRequest.setAttribute(ContainerConstants.PORTLET_ENTITY, portletDefinition);

            PortletContext portletContext = PortletContextFactory.createPortletContext(appContext, app);
            //TODO: We need to get the ServletConfig somehow!!!
            PortletConfig portletConfig = new PortletConfigImpl(null, portletContext, portletDefinition);
            servletRequest.setAttribute(ContainerConstants.PORTLET_CONFIG, portletConfig);

            dispatcher.include(servletRequest, servletResponse);
        }
        catch (Exception e)
        {
            String message =
                "Failed to dispatch.include for Portlet Application: " + portletApplicationName + ", servlet: " + MVC_ENTRY_SERVLET;
            log.error(message, e);
            throw new PortletException(message, e);
        }
        finally
        {
            servletRequest.removeAttribute(ContainerConstants.METHOD_ID);
            servletRequest.removeAttribute(ContainerConstants.PORTLET_REQUEST);
            servletRequest.removeAttribute(ContainerConstants.PORTLET_RESPONSE);
            servletRequest.removeAttribute(ContainerConstants.PORTLET_CONFIG);
            servletRequest.removeAttribute(ContainerConstants.PORTLET_ENTITY);
        }
    }

}
