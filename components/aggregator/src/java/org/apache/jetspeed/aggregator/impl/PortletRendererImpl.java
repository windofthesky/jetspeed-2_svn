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
package org.apache.jetspeed.aggregator.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.UnknownPortletDefinitionException;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;
import org.picocontainer.Startable;

/**
 * <h4>PortletRendererService<br />
 * Jetspeed-2 Rendering service.</h4>
 * <p>This service process all portlet rendering requests and interfaces with the portlet
 * container to generate the resulting markup</p>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id$
 */
public class PortletRendererImpl implements PortletRenderer, Startable
{
    protected final static Log log = LogFactory.getLog(PortletRendererImpl.class);

    private WorkerMonitor monitor;

    private PortletContainer container;
    private PortletWindowAccessor windowAccessor;

    public PortletRendererImpl(PortletContainer container, 
                               PortletWindowAccessor windowAccessor)
    {
        this.container = container;
        this.windowAccessor = windowAccessor;
    }
        
    public void start()
    {
        this.monitor = new WorkerMonitor();
        this.monitor.init();
    }
    
    public void stop()
    {
        // this.monitor.shutdown ?
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
        if (request.getContentDispatcher() == null)
        {
            request.setContentDispatcher(new ContentDispatcherImpl(isParallel, this));
        }

        return (ContentDispatcherCtrl)request.getContentDispatcher();
    }

    protected PortletWindow getPortletWindow(Fragment fragment) throws UnknownPortletDefinitionException
    {
        ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());
                        
        PortletWindow portletWindow = windowAccessor.getPortletWindow(fragment);
        if (portletWindow == null)
        {
            throw new UnknownPortletDefinitionException("Portlet Window creation failed for fragment: " + fragment.getId() + ", " + fragment.getName());
        }
        PortletEntity portletEntity = portletWindow.getPortletEntity();

        return portletWindow;
    }
}