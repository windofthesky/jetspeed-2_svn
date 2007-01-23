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

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.aggregator.UnknownPortletDefinitionException;
import org.apache.jetspeed.aggregator.WorkerMonitor;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.title.DynamicTitleService;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.pluto.PortletContainer;
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
 * @version $Id: PortletRendererImpl.java,v 1.30 2005/05/20 14:54:22 ate Exp $
 */
public class PortletRendererImpl implements PortletRenderer
{
    protected final static Log log = LogFactory.getLog(PortletRendererImpl.class);

    protected WorkerMonitor workMonitor;
    protected PortletContainer container;
    protected PortletWindowAccessor windowAccessor;
    protected PortalStatistics statistics;
    protected DynamicTitleService addTitleService;
    protected long defaultPortletTimeout;

    public PortletRendererImpl(PortletContainer container, 
                               PortletWindowAccessor windowAccessor,
                               WorkerMonitor workMonitor,
                               PortalStatistics statistics,
                               DynamicTitleService addTitleService,
                               long defaultPortletTimeout)
    {
        this.container = container;
        this.windowAccessor = windowAccessor;
        this.workMonitor = workMonitor;
        this.statistics = statistics;
        this.addTitleService = addTitleService;
        this.defaultPortletTimeout = defaultPortletTimeout;
    }

    public PortletRendererImpl(PortletContainer container, 
                               PortletWindowAccessor windowAccessor,
                               WorkerMonitor workMonitor,
                               PortalStatistics statistics,
                               DynamicTitleService addTitleService)
    {
        this( container, windowAccessor, workMonitor, statistics, null, 0 );
    }

    public PortletRendererImpl(PortletContainer container, 
                               PortletWindowAccessor windowAccessor,
                               WorkerMonitor workMonitor,
                               PortalStatistics statistics)
    {
        this( container, windowAccessor, workMonitor, statistics, null );
    }
    
    public PortletRendererImpl(PortletContainer container, 
                               PortletWindowAccessor windowAccessor,
                               WorkerMonitor workMonitor)
    {
        this( container, windowAccessor, workMonitor, null );
    }
    
    public void start()
    {
        // workMonitor.start();
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
    public void renderNow( ContentFragment fragment, RequestContext requestContext )
    {

        HttpServletRequest servletRequest = null;
        HttpServletResponse servletResponse = null;
        ContentDispatcher dispatcher = null;
        PortletWindow portletWindow = null;
        
        try
        {
            portletWindow = getPortletWindow(fragment);
            ContentDispatcherCtrl dispatcherCtrl = getDispatcherCtrl(requestContext, true);
            dispatcher = getDispatcher(requestContext, true);
            servletRequest = requestContext.getRequestForWindow(portletWindow);
            servletResponse = dispatcherCtrl.getResponseForWindow(portletWindow, requestContext);

            RenderingJob rJob = buildRenderingJob(fragment, servletRequest, servletResponse, requestContext, false);
            rJob.execute();
            addTitleToHeader( portletWindow, fragment, servletRequest, servletResponse );
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
    public void renderNow( ContentFragment fragment, HttpServletRequest request, HttpServletResponse response )          
    {

        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        ContentDispatcher dispatcher = getDispatcher(requestContext, true);
        PortletWindow portletWindow = null;
        
        try
        {
            portletWindow = getPortletWindow(fragment);
            ContentDispatcherCtrl dispatcherCtrl = getDispatcherCtrl(requestContext, true);
            
            HttpServletRequest servletRequest = requestContext.getRequestForWindow(portletWindow);
            HttpServletResponse servletResponse = dispatcherCtrl.getResponseForWindow(portletWindow, requestContext);

            RenderingJob rJob = buildRenderingJob(fragment, servletRequest, servletResponse, requestContext, false);
            rJob.execute();
            addTitleToHeader( portletWindow, fragment, servletRequest, servletResponse );
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
    public RenderingJob render( ContentFragment fragment, RequestContext requestContext )
    {
        RenderingJob rJob = null;
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
            rJob = buildRenderingJob(fragment, servletRequest, servletResponse, requestContext, true);

            if (rJob.getTimeout() > 0) 
            {
                workMonitor.process(rJob);
            } 
            else 
            {
                rJob.execute();
            }

            addTitleToHeader( portletWindow, fragment, servletRequest, servletResponse );
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
        return rJob;
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

    protected PortletWindow getPortletWindow( ContentFragment fragment ) throws FailedToRetrievePortletWindow, PortletEntityNotStoredException
    {
            // ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());
            PortletWindow portletWindow = windowAccessor.getPortletWindow(fragment);
            if (portletWindow == null)
            {
                throw new FailedToRetrievePortletWindow("Portlet Window creation failed for fragment: "
                        + fragment.getId() + ", " + fragment.getName());
            }
            PortletEntity portletEntity = portletWindow.getPortletEntity();
            ((MutablePortletEntity)portletEntity).setFragment(fragment);
            return portletWindow;

    }

    protected RenderingJob buildRenderingJob( ContentFragment fragment, HttpServletRequest request,
                                              HttpServletResponse response, RequestContext requestContext, boolean isParallel ) 
        throws FailedToRetrievePortletWindow, FailedToRenderFragmentException, PortletEntityNotStoredException
    {
        RenderingJob rJob = null;
        ContentDispatcher dispatcher = null;
        
        PortletWindow portletWindow = getPortletWindow(fragment);
        PortletDefinitionComposite portletDefinition = 
            (PortletDefinitionComposite) portletWindow.getPortletEntity().getPortletDefinition();
        ContentDispatcherCtrl dispatcherCtrl = getDispatcherCtrl(requestContext, true);
        dispatcher = getDispatcher(requestContext, true);        
        request = requestContext.getRequestForWindow(portletWindow);
        response = dispatcherCtrl.getResponseForWindow(portletWindow, requestContext);
       
        
        request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
        request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
        request.setAttribute(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE, dispatcher);
        request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, requestContext);                
        request.setAttribute(PortalReservedParameters.PATH_ATTRIBUTE, requestContext.getAttribute(PortalReservedParameters.PATH_ATTRIBUTE));
        request.setAttribute(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE, portletWindow);
        PortletContent portletContent = dispatcher.getPortletContent(fragment);
        fragment.setPortletContent(portletContent);

        // In case of parallel mode, store attributes in a map to be refered by worker.
        if (isParallel)
        {
            Map workerAttrs = new HashMap();
            workerAttrs.put(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
            workerAttrs.put(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
            workerAttrs.put(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE, dispatcher);
            workerAttrs.put(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, requestContext);        
            workerAttrs.put(PortalReservedParameters.PATH_ATTRIBUTE, requestContext.getAttribute(PortalReservedParameters.PATH_ATTRIBUTE));
            workerAttrs.put(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE, portletWindow);

            // the portlet invoker is not thread safe; it stores current portlet definition as a member variable.
            // so, store portlet definition as an attribute of worker
            workerAttrs.put(PortalReservedParameters.PORTLET_DEFINITION_ATTRIBUTE, portletDefinition);

            rJob = new RenderingJobImpl(container, portletContent, fragment, request, response, requestContext, portletWindow, statistics, workerAttrs);
        }
        else
        {
            rJob = new RenderingJobImpl(container, portletContent, fragment, request, response, requestContext, portletWindow, statistics);
        }

        long timeoutMetadata = 0;
        Collection timeoutFields = null;

        if (portletDefinition != null)
        {
            timeoutFields = portletDefinition.getMetadata().getFields("timeout");
        }

        if (timeoutFields != null) 
        {
            Iterator it = timeoutFields.iterator();

            if (it.hasNext()) 
            {
                LocalizedField timeoutField = (LocalizedField) timeoutFields.iterator().next();

                try 
                {
                    timeoutMetadata = Long.parseLong(timeoutField.getValue());
                }
                catch (NumberFormatException nfe) 
                {
                    log.warn("Invalid timeout metadata: " + nfe.getMessage());
                }
            }
        }

        if (timeoutMetadata > 0) 
        {
            rJob.setTimeout(timeoutMetadata);
        } 
        else if (this.defaultPortletTimeout > 0) 
        {
            rJob.setTimeout(this.defaultPortletTimeout);
        }

        return rJob;
        
    }
    
    protected void addTitleToHeader( PortletWindow portletWindow, ContentFragment fragment, HttpServletRequest request, HttpServletResponse response )
    {
        if ( this.addTitleService != null )
        {
            try
            {
                String title = fragment.getTitle();
                if ( title == null )
                {
                    title = addTitleService.getDynamicTitle( portletWindow, request );
                }
                log.info( "PortletRenderer title: " + title );
                response.setHeader( "JS_PORTLET_TITLE", StringEscapeUtils.escapeHtml( title ) );
            }
            catch (Exception e)
            {
                log.error("Unable to reteive portlet title: " + e.getMessage(), e);
            }
        }
    }
}
