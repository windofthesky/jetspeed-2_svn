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
package org.apache.jetspeed.aggregator.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.PortletWindowFactory;
import org.apache.jetspeed.aggregator.UnknownPortletDefinitionException;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregsitry.PortletRegistryComponent;
import org.apache.jetspeed.container.PortletContainerFactory;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <h4>PortletRendererService<br />
 * Jetspeed-2 Rendering service.</h4>
 * <p>This service process all portlet rendering requests and interfaces with the portlet
 * container to generate the resulting markup</p>
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class PortletRendererImpl extends BaseCommonService implements PortletRenderer
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(PortletRendererImpl.class);

    private WorkerMonitor monitor;

    private PortletContainer container = null;

    /**
     */
    public void init() throws InitializationException
    {
        this.monitor = new WorkerMonitor();
        this.monitor.init();

        try
        {
            this.container = PortletContainerFactory.getPortletContainer();
        }
        catch (PortletContainerException e)
        {            
            log.error("Failed to get PortletContainer: " + e.toString(), e);
            return;
        }

        setInit(true);
    }

    /**
     */
    public void shutdown()
    {
    }

    /**
        Render the specified Page fragment.
        Result is returned in the PortletResponse.
     */
    public void renderNow(Fragment fragment, RequestContext request)
    {
        //
        // create the portlet window and render the portlet
        //
        HttpServletRequest servletRequest = null;
        try
        {
            PortletWindow portletWindow = getPortletWindow(fragment);

            servletRequest = request.getRequestForWindow(portletWindow);
            HttpServletResponse servletResponse = request.getResponseForWindow(portletWindow);

            servletRequest.setAttribute("org.apache.jetspeed.ContentDispatcher",getDispatcher(request,true));
            servletRequest.setAttribute("org.apache.jetspeed.Fragment",fragment);
            servletRequest.setAttribute("org.apache.jetspeed.Page",request.getPage());

            // should we decorate here instead of rendering Portlet ?
            container.renderPortlet(portletWindow, servletRequest, servletResponse);
        }
        catch (Throwable t)
        {            
            log.error("Failed to service portlet, portlet exception: " + t.toString(), t);
        }
        finally
        {
            if (servletRequest!=null)
            {
                servletRequest.removeAttribute("org.apache.jetspeed.ContentDispatcher");
                servletRequest.removeAttribute("org.apache.jetspeed.Fragment");
                servletRequest.removeAttribute("org.apache.jetspeed.Page");
            }
        }
    }

    /**
        Render the specified Page fragment.
        Result is returned in the PortletResponse.
     */
    public void renderNow(Fragment fragment, HttpServletRequest request, HttpServletResponse response)
    {
        //
        // create the portlet window and render the portlet
        //
        try
        {
            PortletWindow portletWindow = getPortletWindow(fragment);
            container.renderPortlet(portletWindow, request, response);
        }
        catch (Throwable t)
        {            
            log.error("Failed to service portlet, portlet exception: " + t.toString(), t);
        }
    }

    /** Render the specified Page fragment.
        The method returns before rendering is complete, rendered content can be
        accessed through the ContentDispatcher
    */
    public void render(Fragment fragment, RequestContext request) throws UnknownPortletDefinitionException
    {
        RenderingJob rJob = new RenderingJob();

        PortletWindow portletWindow = getPortletWindow(fragment);
        ContentDispatcherCtrl dispatcher = getDispatcherCtrl(request,true);

        HttpServletRequest servletRequest = request.getRequestForWindow(portletWindow);
        HttpServletResponse servletResponse = dispatcher.getResponseForWindow(portletWindow, request);

        rJob.setWindow(portletWindow);
        rJob.setContainer(this.container);
        rJob.setRequest(servletRequest);
        rJob.setResponse(servletResponse);
        rJob.setDispatcher(dispatcher);

        monitor.process(rJob);
    }

    /**
     * Retrieve the ContentDispatcher for the specified request
     */
    public ContentDispatcher getDispatcher(RequestContext request, boolean isParallel)
    {
        return (ContentDispatcher)getDispatcherCtrl(request,isParallel);
    }

    /**
     * Retrieve the ContentDispatcherCtrl for the specified request
     */
    protected ContentDispatcherCtrl getDispatcherCtrl(RequestContext request, boolean isParallel)
    {
        if (request.getContentDispatcher()==null)
        {
            request.setContentDispatcher(new ContentDispatcherImpl(isParallel));
        }

        return (ContentDispatcherCtrl)request.getContentDispatcher();
    }

    protected PortletWindow getPortletWindow(Fragment fragment) throws UnknownPortletDefinitionException
    {
        ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());
        PortletEntityAccessComponent entityAccess = (PortletEntityAccessComponent) Jetspeed.getComponentManager().getComponent(PortletEntityAccessComponent.class);
        // DST: PortletEntity portletEntity = entityAccess.getPortletEntity(oid);
        PortletEntity portletEntity = null;
        PortletWindow portletWindow = null;

        if (portletEntity==null)
        {
			PortletRegistryComponent registry = (PortletRegistryComponent) Jetspeed.getComponentManager().getComponent(PortletRegistryComponent.class);
            PortletDefinition portletDefinition = registry.getPortletDefinitionByUniqueName(fragment.getName());
            if (portletDefinition == null)
            {
                log.error("Failed to load: " + fragment.getName() + " from registry");
                throw new UnknownPortletDefinitionException("Could not locate portlet definition \""+fragment.getName()+"\".");
            }
            portletWindow = PortletWindowFactory.getWindow(portletDefinition, fragment.getName());
/*
 TODO: DST: Why the hell are we storing an entity on a getter?
 
            // fix issues, persist entity and update fragment ID
            try
            {
                entityAccess.storePortletEntity(portletWindow.getPortletEntity());
            }
            catch (Exception e)
            {
                log.error("Error persisting new portletEntity", e);
            }
*/
            fragment.setId(portletWindow.getId().toString());
            oid = portletWindow.getId();
        }
        else
        {
            portletWindow = PortletWindowFactory.getWindow(portletEntity, oid);
        }

        return portletWindow;
    }
}