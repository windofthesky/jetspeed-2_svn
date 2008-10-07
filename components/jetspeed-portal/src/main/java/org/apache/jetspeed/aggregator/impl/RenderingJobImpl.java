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

package org.apache.jetspeed.aggregator.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;

import javax.portlet.UnavailableException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.CurrentWorkerContext;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.PortletTrackingManager;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.components.portletentity.PortletEntityImpl;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;

/**
 * The RenderingJob is responsible for storing all necessary objets for
 * asynchronous portlet rendering as well as implementing the rendering logic
 * in its Runnable method.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a>Woonsan Ko</a>
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
    protected PortletRenderer renderer = null;
    protected ContentFragment fragment = null;
    protected RequestContext requestContext = null;
    protected PortletTrackingManager portletTracking = null;

    protected PortletDefinition portletDefinition;
    protected PortletContent portletContent;
    protected PortalStatistics statistics;
    protected ContentDispatcherCtrl dispatcher;
    protected boolean contentIsCached;
    
    protected int expirationCache = 0;

    protected Map workerAttributes;

    protected long startTimeMillis = 0;
    protected long timeout;
    
    public RenderingJobImpl(PortletContainer container,
                            PortletRenderer renderer,
                            PortletDefinition portletDefinition,
                            PortletContent portletContent, 
                            ContentFragment fragment, 
                            ContentDispatcherCtrl dispatcher,
                            HttpServletRequest request, 
                            HttpServletResponse response, 
                            RequestContext requestContext, 
                            PortletWindow window,
                            PortalStatistics statistics,
                            int expirationCache,
                            boolean contentIsCached)
    {
        this.container = container;
        this.renderer = renderer;
        this.portletTracking = renderer.getPortletTrackingManager();        
        this.statistics = statistics;
        this.portletDefinition = portletDefinition;
        this.fragment = fragment;
        this.dispatcher = dispatcher;
        this.request = request;
        this.response = response;
        this.requestContext = requestContext; 
        this.window = window;
        this.portletContent = portletContent; 
        ((MutablePortletEntity)window.getPortletEntity()).setFragment(fragment);
        this.expirationCache = expirationCache;
        this.contentIsCached = contentIsCached;
    }

    public RenderingJobImpl(PortletContainer container, 
                            PortletRenderer renderer,
                            PortletDefinition portletDefinition,
                            PortletContent portletContent, 
                            ContentFragment fragment,
                            ContentDispatcherCtrl dispatcher,
                            HttpServletRequest request, 
                            HttpServletResponse response, 
                            RequestContext requestContext, 
                            PortletWindow window,
                            PortalStatistics statistics,
                            int expirationCache,
                            boolean contentIsCached,
                            Map workerAttrs)
    {
        this(container, renderer, portletDefinition, portletContent, fragment, dispatcher,
                        request, response, requestContext, window, statistics, expirationCache, contentIsCached);
        
        if (workerAttrs != null)
        {
            this.workerAttributes = Collections.synchronizedMap(workerAttrs);
        }
    }

    /**
     * Sets portlet timout in milliseconds.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets portlet timout in milliseconds.
     */
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * Checks if the portlet rendering is timeout
     */
    public boolean isTimeout() {
        if ((this.timeout > 0) && (this.startTimeMillis > 0)) {
            return (System.currentTimeMillis() - this.startTimeMillis > this.timeout);
        }

        return false;
    }

    /**
     * Checks if queue is empty, if not try to empty it by calling
     * the WorkerMonitor. When done, pause until next scheduled scan.
     */
    public void run()
    {       
        try
        {
            if (this.timeout > 0) 
            {
                CurrentWorkerContext.setParallelRenderingMode(true);
                this.startTimeMillis = System.currentTimeMillis();
            }

            // A little baby hack to make sure the worker thread has PortletContent to write too.
            fragment.setPortletContent(portletContent);
            execute();                     
        }
        finally
        {
            synchronized (portletContent)
            {
               if (log.isDebugEnabled()) log.debug("Notifying completion of rendering job for fragment " + fragment.getId());                
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
        boolean isParallelMode = false;
        PortletWindow curWindow = this.window;
        try
        {
            if (log.isDebugEnabled()) log.debug("Rendering OID "+this.window.getId()+" "+ this.request +" "+this.response);

            // if the current thread is worker, then store attribues in that.
            if (this.workerAttributes != null)
            {
                isParallelMode = CurrentWorkerContext.getParallelRenderingMode();
                if (isParallelMode)
                {
                    Collection attrNames = Arrays.asList(this.workerAttributes.keySet().toArray());
                    
                    Iterator itAttrNames = attrNames.iterator();
                    while (itAttrNames.hasNext()) 
                    {
                        String name = (String) itAttrNames.next();
                        CurrentWorkerContext.setAttribute(name, this.workerAttributes.get(name));
                    }
                    
                    // The portletEntity stores its portletDefinition into the ThreadLocal member,
                    // before the worker starts doing a rendering job.
                    // So the thread contexts are different from each other.
                    // Therefore, in parallel mode, we have to clear threadlocal fragmentPortletDefinition cache
                    // of portletEntity and to replace the portletDefinition with one of current worker context.
                    // Refer to org.apache.jetspeed.components.portletentity.PortletEntityImpl class
                    
                    curWindow = (PortletWindow) 
                        CurrentWorkerContext.getAttribute(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE); 
                    PortletEntityImpl curEntity = (PortletEntityImpl) curWindow.getPortletEntity();
                    PortletDefinition oldPortletDefinition = curEntity.getPortletDefinition();
                    PortletDefinition curPortletDefinition = (PortletDefinition)
                        CurrentWorkerContext.getAttribute(PortalReservedParameters.PORTLET_DEFINITION_ATTRIBUTE);
                    
                    if (!oldPortletDefinition.getId().equals(curPortletDefinition.getId())) {
                        curEntity.setPortletDefinition(curPortletDefinition);
                    }
                }
            }
            
            if (isParallelMode)
            {
                ServletRequest servletRequest = ((HttpServletRequestWrapper)((HttpServletRequestWrapper) this.request).getRequest()).getRequest();
                
                synchronized (servletRequest)
                {
                    this.request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
                    this.request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
                    this.request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, requestContext);
                    this.request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_OBJECTS, requestContext.getObjects());            
                  //  this.request.setAttribute(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE,dispatcher);
                }
            }
            else
            {
                this.request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
                this.request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
                this.request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, requestContext);
                this.request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_OBJECTS, requestContext.getObjects());            
              //  this.request.setAttribute(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE,dispatcher);
            }
            
            container.renderPortlet(this.window, this.request, this.response);               
            this.response.flushBuffer();                           
        }
        catch (Throwable t)
        {
            if (t instanceof UnavailableException)
            {
                // no need to dump a full stack trace to the log
                log.error("Error rendering portlet OID " + curWindow.getId() + ": " + t.toString());
            }
            else
            {
                log.error("Error rendering portlet OID " + curWindow.getId(), t);
            }
            fragment.overrideRenderedContent(t.getMessage());
        }
        finally
        {
            try
            {
                if (isParallelMode)
                {
                    this.renderer.addTitleToHeader(curWindow, fragment,
                                                   this.request, this.response,
                                                   this.dispatcher, this.contentIsCached);
                
                    CurrentWorkerContext.removeAllAttributes();
                }
                
                if (fragment.getType().equals(ContentFragment.PORTLET))
                {
                    long end = System.currentTimeMillis();
                    boolean exceededTimeout = portletTracking.exceededTimeout(end - start, window);
                    
                    if (statistics != null)
                    {
                        statistics.logPortletAccess(requestContext, fragment.getName(), PortalStatistics.HTTP_OK, end - start);
                    }
                    if (exceededTimeout)
                    {
                        // took too long to render
                        log.info("Portlet Exceeded timeout: " + curWindow.getPortletEntity().getPortletDefinition().getName() + " for window " + curWindow.getId());
                        portletTracking.incrementRenderTimeoutCount(curWindow);
                    }
                    else
                    {
                        portletTracking.success(curWindow);
                    }
                }
            }
            finally
            {
                synchronized (portletContent)
                {
                    if (fragment.getOverriddenContent() != null)
                    {
                        portletContent.completeWithError();
                    }
                    else
                    {
                        portletContent.complete();
                    }
                }
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

    public PortletDefinition getPortletDefinition()
    {
        return this.portletDefinition;
    }

    public HttpServletRequest getRequest()
    {
        return this.request;
    }

    public HttpServletResponse getResponse()
    {
        return this.response;
    }

    public ContentFragment getFragment()
    {
        return this.fragment;
    }

    public RequestContext getRequestContext()
    {
        return this.requestContext;
    }

    public int getExpirationCache()
    {
        return this.expirationCache;
    }

    public ContentDispatcherCtrl getDispatcher()
    {
        return this.dispatcher;
    }

    public boolean isContentCached() 
    {
        return this.contentIsCached;
    }
    
    public void setWorkerAttribute(String name, Object value)
    {
        if (this.workerAttributes == null)
        {
            this.workerAttributes = Collections.synchronizedMap(new HashMap());
        }
        
        if (value != null)
        {
            this.workerAttributes.put(name, value);
        }
        else
        {
            this.workerAttributes.remove(name);
        }
    }
    
    public Object getWorkerAttribute(String name)
    {
        Object value = null;
        
        if (this.workerAttributes != null)
        {
            value = this.workerAttributes.get(name);
        }
        
        return value;
    }
    
    public void removeWorkerAttribute(String name)
    {
        if (this.workerAttributes != null)
        {
            this.workerAttributes.remove(name);
        }
    }
}
