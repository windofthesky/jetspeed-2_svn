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
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.UnknownPortletDefinitionException;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerServices;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <h4>PortletRendererService <br />
 * Jetspeed-2 Rendering service.</h4>
 * <p>
 * This service process all portlet rendering requests and interfaces with the
 * portlet container to generate the resulting markup
 * </p>
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @version $Id$
 */
public class PortletRendererImpl implements PortletRenderer
{
    protected final static Log log = LogFactory.getLog(PortletRendererImpl.class);

    private WorkerMonitor monitor;

    private PortletContainer container;
    private PortletWindowAccessor windowAccessor;

    public PortletRendererImpl( PortletContainer container, PortletWindowAccessor windowAccessor )
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
     * Render the specified Page fragment. Result is returned in the
     * PortletResponse.
     * 
     * @throws FailedToRenderFragmentException
     * @throws FailedToRetrievePortletWindow
     */
    public void renderNow( Fragment fragment, RequestContext requestContext )
    {

        HttpServletRequest servletRequest = null;
        HttpServletResponse servletResponse = null;
        ContentDispatcher dispatcher = null;
        PortletWindow portletWindow = null;
        
        try
        {
            portletWindow = getPortletWindow(fragment);
            PortletContainerServices.prepare("jetspeed");           
            ContentDispatcherCtrl dispatcherCtrl = getDispatcherCtrl(requestContext, true);
            dispatcher = getDispatcher(requestContext, true);
            servletRequest = requestContext.getRequestForWindow(portletWindow);
            servletResponse = dispatcherCtrl.getResponseForWindow(portletWindow, requestContext);

            RenderingJob rJob = buildRenderingJob(fragment, servletRequest, servletResponse, requestContext);
            rJob.execute();

        }
        catch (Exception e)
        {
            fragment.overrideRenderedContent(e.toString());
            log.error(e.toString(), e);
        }
    }

    /**
     * Render the specified Page fragment. Result is returned in the
     * PortletResponse.
     * 
     * @throws FailedToRenderFragmentException
     * @throws FailedToRetrievePortletWindow
     */
    public void renderNow( Fragment fragment, HttpServletRequest request, HttpServletResponse response )          
    {

        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        ContentDispatcher dispatcher = getDispatcher(requestContext, true);
        PortletWindow portletWindow = null;
        
        try
        {
            portletWindow = getPortletWindow(fragment);
            PortletContainerServices.prepare("jetspeed");
            ContentDispatcherCtrl dispatcherCtrl = getDispatcherCtrl(requestContext, true);
            
            HttpServletRequest servletRequest = requestContext.getRequestForWindow(portletWindow);
            HttpServletResponse servletResponse = dispatcherCtrl.getResponseForWindow(portletWindow, requestContext);

            RenderingJob rJob = buildRenderingJob(fragment, servletRequest, servletResponse, requestContext);
            rJob.execute();
        }
        catch (Exception e)
        {
            fragment.overrideRenderedContent(e.toString());
            log.error(e.toString(), e);
        }
    }

    /**
     * Render the specified Page fragment. The method returns before rendering
     * is complete, rendered content can be accessed through the
     * ContentDispatcher
     * @throws FailedToRetrievePortletWindow
     * 
     * @throws UnknownPortletDefinitionException
     * @throws FailedToRetrievePortletWindow
     */
    public void render( Fragment fragment, RequestContext requestContext )
    {

        PortletContainerServices.prepare("jetspeed");

        PortletWindow portletWindow;
        
        ContentDispatcherCtrl dispatcherCtrl = getDispatcherCtrl(requestContext, true);
        ContentDispatcher dispatcher = getDispatcher(requestContext, true);

        HttpServletRequest servletRequest =null;
        HttpServletResponse servletResponse = null;

        try
        {
            portletWindow = getPortletWindow(fragment);
            servletRequest = requestContext.getRequestForWindow(portletWindow);
            servletResponse = dispatcherCtrl.getResponseForWindow(portletWindow, requestContext);
            RenderingJob rJob = buildRenderingJob(fragment, servletRequest, servletResponse, requestContext);
            monitor.process(rJob);            
        }
        catch (Exception e1)
        {
            servletRequest = requestContext.getRequest();
            servletResponse = dispatcherCtrl.getResponseForFragment(fragment, requestContext);
            log.error("render() failed: " + e1.toString(), e1);
            fragment.overrideRenderedContent(e1.toString());            
//            ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());
        //    ((ContentDispatcherImpl) dispatcherCtrl).notify(oid);
        }

    }

    /**
     * Retrieve the ContentDispatcher for the specified request
     */
    public ContentDispatcher getDispatcher( RequestContext request, boolean isParallel )
    {
        return (ContentDispatcher) getDispatcherCtrl(request, isParallel);
    }

    /**
     * Retrieve the ContentDispatcherCtrl for the specified request
     */
    protected ContentDispatcherCtrl getDispatcherCtrl( RequestContext request, boolean isParallel )
    {
        if (request.getContentDispatcher() == null)
        {
            request.setContentDispatcher(new ContentDispatcherImpl(isParallel));
        }

        return (ContentDispatcherCtrl) request.getContentDispatcher();
    }

    protected PortletWindow getPortletWindow( Fragment fragment ) throws FailedToRetrievePortletWindow
    {

            ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());

            PortletWindow portletWindow = windowAccessor.getPortletWindow(fragment);
            if (portletWindow == null)
            {
                throw new FailedToRetrievePortletWindow("Portlet Window creation failed for fragment: "
                        + fragment.getId() + ", " + fragment.getName());
            }
            PortletEntity portletEntity = portletWindow.getPortletEntity();

            return portletWindow;

    }

    protected RenderingJob buildRenderingJob( Fragment fragment, HttpServletRequest request,
            HttpServletResponse response, RequestContext requestContext ) throws FailedToRetrievePortletWindow,
            FailedToRenderFragmentException
    {
        ContentDispatcher dispatcher = null;
        RenderingJob rJob;

        PortletContainerServices.prepare("jetspeed");

        PortletWindow portletWindow = getPortletWindow(fragment);
        ContentDispatcherCtrl dispatcherCtrl = getDispatcherCtrl(requestContext, true);
        dispatcher = getDispatcher(requestContext, true);
        request = requestContext.getRequestForWindow(portletWindow);
        response = dispatcherCtrl.getResponseForWindow(portletWindow, requestContext);

        request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
        request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
        request.setAttribute(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE, dispatcher);
        request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, request);

        request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
        return new RenderingJob(container, dispatcher, fragment, request, response, requestContext, portletWindow);

    }
}
