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

import java.util.Map;

import javax.portlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.jetspeed.components.portletentity.PortletEntityImpl;

/**
 * The RenderingJob is responsible for storing all necessary objets for
 * asynchronous portlet rendering as well as implementing the rendering logic
 * in its Runnable method.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id$
 */
public class RenderingJobImpl implements RenderingJob
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(RenderingJobImpl.class);

    /** WorkerMonitor used to flush the queue */
    protected PortletWindow window = null;
    protected HttpServletRequest request = null;
    protected HttpServletResponse response = null;
    
    protected PortletContainer container = null;
    protected ContentFragment fragment = null;
    protected RequestContext requestContext = null;

    protected PortletContent portletContent;
    protected PortalStatistics statistics;

    protected Map workerAttributes;
    
    public RenderingJobImpl(PortletContainer container, 
                            PortletContent portletContent, 
                            ContentFragment fragment, 
                            HttpServletRequest request, 
                            HttpServletResponse response, 
                            RequestContext requestContext, 
                            PortletWindow window,
                            PortalStatistics statistics)
    {
        this.container = container;
        this.statistics = statistics;
        this.fragment = fragment;
        this.request = request;
        this.response = response;
        this.requestContext = requestContext; 
        this.window = window;
        this.portletContent = portletContent; 
        ((MutablePortletEntity)window.getPortletEntity()).setFragment(fragment);
        
    }

    public RenderingJobImpl(PortletContainer container, 
                            PortletContent portletContent, 
                            ContentFragment fragment, 
                            HttpServletRequest request, 
                            HttpServletResponse response, 
                            RequestContext requestContext, 
                            PortletWindow window,
                            PortalStatistics statistics,
                            Map workerAttributes)
    {
        this(container, portletContent, fragment, request, response, requestContext, window, statistics);
        this.workerAttributes = workerAttributes;
    }

    /**
     * Checks if queue is empty, if not try to empty it by calling
     * the WorkerMonitor. When done, pause until next scheduled scan.
     */
    public void run()
    {       
        try
        {
            // A little baby hack to make sure the worker thread has PortletContent to write too.
            fragment.setPortletContent(portletContent);
            execute();                     
        }
        finally
        {
            
            synchronized (portletContent)
            {
               log.debug("Notifying completion of rendering job for fragment " + fragment.getId());                
               portletContent.notifyAll();
            }
        }
    }
    
    /**
     * <p>
     * execute
     * </p>
     *
     * 
     */
    public void execute()
    {
        long start = System.currentTimeMillis();

        Map workerAsMap = null;

        try
        {
            log.debug("Rendering OID "+this.window.getId()+" "+ this.request +" "+this.response);

            // if the current thread is worker, then store attribues in that.
            if (this.workerAttributes != null)
            {
                Thread ct = Thread.currentThread();
                if (ct instanceof Map)
                {
                    workerAsMap = (Map) ct;
                    workerAsMap.putAll(this.workerAttributes);

                    // Sometimes, the portlet definition of some portlet entities are replaced.
                    // I could not find why it happens.
                    // If the portlet definition of portlet entity is not same as an attribute of worker's, then
                    // reset the portlet definition of portlet entity. (by Woonsan Ko)
                    // TODO: Investigate more and find why it happens.
                    PortletDefinition portletDefinition = 
                        (PortletDefinition) workerAsMap.get(PortalReservedParameters.PORTLET_DEFINITION_ATTRIBUTE);
                    PortletWindow window = 
                        (PortletWindow) workerAsMap.get(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE);
                    PortletEntityImpl portletEntityImpl = (PortletEntityImpl) window.getPortletEntity();
                    PortletDefinition oldPortletDefinition = portletEntityImpl.getPortletDefinition();

                    if (!oldPortletDefinition.getId().equals(portletDefinition.getId())) {
                        portletEntityImpl.setPortletDefinition(portletDefinition);
                    }
                }
            }
            
            this.request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
            this.request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
            this.request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, requestContext);
          //  this.request.setAttribute(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE,dispatcher);
            container.renderPortlet(this.window, this.request, this.response);               
            this.response.flushBuffer();                           
        }
        catch (Throwable t)
        {
            // this will happen is request is prematurely aborted
            if ( t instanceof UnavailableException)
            {
                // no need to dump a full stack trace to the log
                log.error("Error rendering portlet OID "+this.window.getId()+": "+t.toString());
            }
            else
            {
                log.error("Error rendering portlet OID " + this.window.getId(), t);
            }
            fragment.overrideRenderedContent(t.getMessage());
        }
        finally
        {
            if (workerAsMap != null)
            {
                workerAsMap.clear();
            }

            portletContent.complete();
            if (fragment.getType().equals(ContentFragment.PORTLET))
            {
                long end = System.currentTimeMillis();            
                if (statistics != null)
                    statistics.logPortletAccess(requestContext, fragment.getName(), PortalStatistics.HTTP_OK, end - start);
            }
        }

    }

    /**
     * 
     * <p>
     * getWindow
     * </p>
     *
     * @return The window this job is in charge of rendering
     */
    public PortletWindow getWindow()
    {
        return window;
    }

    /**
     * 
     * <p>
     * getPortletContent
     * </p>
     *
     * @return The portlet content this job is in charge of rendering
     */
    public PortletContent getPortletContent()
    {
        return portletContent;
    }
}
