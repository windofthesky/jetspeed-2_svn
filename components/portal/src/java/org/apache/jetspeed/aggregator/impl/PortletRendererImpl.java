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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.PortletAccessDeniedException;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.PortletTrackingManager;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.aggregator.UnknownPortletDefinitionException;
import org.apache.jetspeed.aggregator.WorkerMonitor;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAccessController;
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
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a>Woonsan Ko</a>
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

    protected PortletTrackingManager portletTracking;
    
    /**
     *  flag indicating whether to check jetspeed-portlet.xml security constraints 
     *  before rendering a portlet. If security check fails, do not display portlet content
     */
    protected boolean checkSecurityConstraints;   
    /**
     * For security constraint checks
     */
    protected SecurityAccessController accessController;
    
    /**
     * JSR 168 Portlet Content Cache
     */
    protected JetspeedCache portletContentCache;
    
    /**
     * OutOfService Cache
     */
    protected boolean overrideTitles = false;
    
    /**
     * The default OutOfService message
     */
    public static final String DEFAULT_OUT_OF_SERVICE_MESSAGE = "Portlet is not responding and has been taken out of service.";
    
    /**
     * The OutOfService message
     */
    protected String outOfServiceMessage = DEFAULT_OUT_OF_SERVICE_MESSAGE;
    
    public PortletRendererImpl(PortletContainer container, 
                               PortletWindowAccessor windowAccessor,
                               WorkerMonitor workMonitor,
                               PortalStatistics statistics,
                               DynamicTitleService addTitleService,
                               PortletTrackingManager portletTracking,
                               boolean checkSecurityConstraints,
                               SecurityAccessController accessController,
                               JetspeedCache portletContentCache,
                               boolean overrideTitles)
    {
        this.container = container;
        this.windowAccessor = windowAccessor;
        this.workMonitor = workMonitor;
        this.statistics = statistics;
        this.addTitleService = addTitleService;
        this.portletTracking = portletTracking;
        this.checkSecurityConstraints = checkSecurityConstraints;
        this.accessController = accessController;
        this.portletContentCache = portletContentCache;
        this.overrideTitles = overrideTitles;
    }

    public PortletRendererImpl(PortletContainer container, 
            PortletWindowAccessor windowAccessor,
            WorkerMonitor workMonitor,
            PortalStatistics statistics,
            DynamicTitleService addTitleService,
            PortletTrackingManager portletTracking,
            boolean checkSecurityConstraints,
            SecurityAccessController accessController,
            JetspeedCache portletContentCache)
    {
        this(container, windowAccessor, workMonitor, statistics, 
             addTitleService, portletTracking, checkSecurityConstraints,
             accessController, portletContentCache, false);
    }
    
    public PortletRendererImpl(PortletContainer container, 
                               PortletWindowAccessor windowAccessor,
                               WorkerMonitor workMonitor,
                               PortalStatistics statistics,
                               DynamicTitleService addTitleService)
    {
        this(container, windowAccessor, workMonitor, statistics, null, null, false, null, null, true);
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
    
    public void setOutOfServiceMessage(String outOfServiceMessage)
    {
        this.outOfServiceMessage = outOfServiceMessage;
    }
    
    /**
     * Render the specified Page fragment. Result is returned in the
     * PortletResponse.
     * 
     * @throws FailedToRenderFragmentException
     * @throws FailedToRetrievePortletWindow
     * @throws UnknownPortletDefinitionException
     */
    public void renderNow( ContentFragment fragment, RequestContext requestContext )
    {
        HttpServletRequest servletRequest =null;
        HttpServletResponse servletResponse = null;
        ContentDispatcherCtrl dispatcher = null;    
        boolean contentIsCached = false;
        try
        {
            PortletWindow portletWindow = getPortletWindow(fragment);
            PortletDefinitionComposite portletDefinition = 
                (PortletDefinitionComposite) portletWindow.getPortletEntity().getPortletDefinition();           
            if (checkSecurityConstraints && !checkSecurityConstraint(portletDefinition, fragment))
            {
                throw new PortletAccessDeniedException("Access Denied.");
            }
            if (portletTracking.isOutOfService(portletWindow))
            {
                log.info("Taking portlet out of service: " + portletDefinition.getUniqueName() + " for window " + fragment.getId());
                fragment.overrideRenderedContent(outOfServiceMessage);
                return;
            }
            long timeoutMetadata = this.getTimeoutOnJob(portletDefinition);
            portletTracking.setExpiration(portletWindow, timeoutMetadata);            
            int expirationCache = getExpirationCache(portletDefinition);
            if (expirationCache != 0)
            {
                if (retrieveCachedContent(requestContext, fragment, portletWindow, expirationCache, portletDefinition))
                    return;
                contentIsCached = true;
            }
            if (dispatcher == null)
            {
                dispatcher = createDispatcher(requestContext, fragment, expirationCache);
            }
            servletRequest = requestContext.getRequestForWindow(portletWindow);
            servletResponse = dispatcher.getResponseForWindow(portletWindow, requestContext);
            RenderingJob rJob = 
                buildRenderingJob(portletWindow, fragment, servletRequest, servletResponse,
                                  requestContext, false, portletDefinition, dispatcher, null, 
                                  expirationCache, contentIsCached, timeoutMetadata);
            rJob.execute();
            addTitleToHeader( portletWindow, fragment, servletRequest, servletResponse, dispatcher, contentIsCached);
        }
        catch (PortletAccessDeniedException e)
        {
            fragment.overrideRenderedContent(e.getLocalizedMessage());                        
        }        
        catch (Exception e)
        {
            fragment.overrideRenderedContent(e.getLocalizedMessage());
            log.error(e.toString(), e);
        }
    }

    /**
     * Render the specified Page fragment. Result is returned in the
     * PortletResponse.
     * 
     * @throws FailedToRenderFragmentException
     * @throws FailedToRetrievePortletWindow
     * @throws UnknownPortletDefinitionException
     * @throws PortletAccessDeniedException
     */
    public void renderNow( ContentFragment fragment, HttpServletRequest request, HttpServletResponse response )          
    {
        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        renderNow(fragment, requestContext);
    }
    
    protected int getExpirationCache(PortletDefinitionComposite portletDefinition)
    {
        if (portletDefinition == null)
            return 0;
        String expiration = portletDefinition.getExpirationCache();
        if (expiration == null)
            return 0;
        return Integer.parseInt(expiration);
    }
    
    /**
     * Render the specified Page fragment. The method returns before rendering
     * is complete, rendered content can be accessed through the Content Dispatcher
     * 
     * @return the asynchronous portlet rendering job to synchronize
     */
    public RenderingJob render( ContentFragment fragment, RequestContext requestContext )
    {
        RenderingJob job = null;

        try
        {
            job = createRenderingJob(fragment, requestContext);
        }
        catch (Exception e)
        {
            log.error("render() failed: " + e.toString(), e);
            fragment.overrideRenderedContent(e.getLocalizedMessage());            
        }

        if (job != null)
        {
            processRenderingJob(job, true);
        }

        return job;
    }       
    
    /** 
     * 
     * Create a rendering job for the specified Page fragment.
     * The method returns a rendering job which should be passed to 'processRenderingJob(RenderingJob job)' method.
     * @return portlet rendering job to pass to render(RenderingJob job) method
     * @throws FailedToRetrievePortletWindow
     * @throws UnknownPortletDefinitionException
     * @throws PortletAccessDeniedException
     */
    public RenderingJob createRenderingJob(ContentFragment fragment, RequestContext requestContext)
    {
        RenderingJob job = null;
        boolean contentIsCached = false;       
        try
        {
            PortletWindow portletWindow = getPortletWindow(fragment);
            PortletDefinitionComposite portletDefinition = 
                (PortletDefinitionComposite) portletWindow.getPortletEntity().getPortletDefinition();     

            long timeoutMetadata = this.getTimeoutOnJob(portletDefinition);
            portletTracking.setExpiration(portletWindow, timeoutMetadata);            
            
            if (checkSecurityConstraints && !checkSecurityConstraint(portletDefinition, fragment))
            {
                throw new PortletAccessDeniedException("Access Denied.");
            }
            if (portletTracking.isOutOfService(portletWindow))
            {
                fragment.overrideRenderedContent(outOfServiceMessage);
                return null;
            }
            int expirationCache = getExpirationCache(portletDefinition);
            if (expirationCache != 0)
            {
                portletTracking.setExpiration(portletWindow, expirationCache);
                contentIsCached = retrieveCachedContent(requestContext, fragment, portletWindow, 
                                                        expirationCache, portletDefinition);
                if (contentIsCached)
                {
                    return null;
                }
            }
            job = buildRenderingJob( portletWindow, fragment, requestContext, true, 
                                     portletDefinition, null, contentIsCached, timeoutMetadata );
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create rendering job", e);
        }

        return job;
    }
           
    /** 
     * 
     * Render the specified rendering job.
     * The method returns before rendering is complete when the job is processed in parallel mode.
     * When it is not parallel mode, it returns after rendering is complete.
     * @throws FailedToRenderFragmentException
     */
    public void processRenderingJob(RenderingJob job)
    {
        processRenderingJob(job, false);
    }

    protected void processRenderingJob(RenderingJob job, boolean parallelOnly)
    {
        ContentFragment fragment = null;

        try
        {
            if (parallelOnly || job.getTimeout() > 0)
            {
                workMonitor.process(job);
            }
            else
            {
                job.execute();
                addTitleToHeader(job.getWindow(), job.getFragment(), 
                                 job.getRequest(), job.getResponse(), job.getDispatcher(), 
                                 job.isContentCached());                
            }
        }
        catch (Exception e1)
        {
            log.error("render() failed: " + e1.toString(), e1);
            fragment.overrideRenderedContent(e1.getLocalizedMessage());            
        }
    }
    
    /**
     * Wait for all rendering jobs in the collection to finish successfully or otherwise. 
     * @param renderingJobs the Collection of rendering job objects to wait for.
     */
    public void waitForRenderingJobs(List renderingJobs)
    {
        this.workMonitor.waitForRenderingJobs(renderingJobs);
    }

    /**
     * Retrieve cached content, if content retrieved successfully return true, if no content found return false
     * @param requestContext
     * @param fragment
     * @param portletWindow
     * @return true when content found, otherwise false
     */
    protected boolean retrieveCachedContent(RequestContext requestContext, ContentFragment fragment, 
                                            PortletWindow portletWindow, int expiration, 
                                            PortletDefinitionComposite portletDefinition)
        throws Exception
    {
        ContentCacheKey cacheKey = portletContentCache.createCacheKey(requestContext, fragment.getId());        
        CacheElement cachedElement = portletContentCache.get(cacheKey);
        if (cachedElement != null)
        {
            PortletContent portletContent = (PortletContent)cachedElement.getContent();            
            fragment.setPortletContent(portletContent);
            ContentDispatcherCtrl dispatcher = new ContentDispatcherImpl(portletContent);
            HttpServletRequest servletRequest = requestContext.getRequestForWindow(portletWindow);

            this.addTitleService.setDynamicTitle(portletWindow, servletRequest, dispatcher.getPortletContent(fragment).getTitle());
            return true;
        }        
        return false;
    }
    
    public ContentDispatcherCtrl createDispatcher(RequestContext request, ContentFragment fragment, int expirationCache)
    {
        ContentCacheKey cacheKey = portletContentCache.createCacheKey(request, fragment.getId());                
        PortletContent content = new PortletContentImpl(this, cacheKey, expirationCache);
        ContentDispatcherCtrl dispatcher = new ContentDispatcherImpl(content); 
        return dispatcher;
    }
    
    /**
     * Retrieve the ContentDispatcher for the specified request
     */
    public ContentDispatcher getDispatcher( RequestContext request, boolean isParallel )
    {
        return request.getContentDispatcher();
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
        
        ((PortletWindowImpl) portletWindow).setInstantlyRendered(fragment.isInstantlyRendered());

        return portletWindow;
    }
    
    protected RenderingJob buildRenderingJob( PortletWindow portletWindow, ContentFragment fragment, 
                                              RequestContext requestContext, boolean isParallel,
                                              PortletDefinitionComposite portletDefinition, 
                                              PortletContent portletContent, boolean contentIsCached, long timeoutMetadata)
        throws PortletAccessDeniedException, FailedToRetrievePortletWindow, PortletEntityNotStoredException        
    {
        int expirationCache = getExpirationCache(portletDefinition);
        ContentDispatcherCtrl dispatcher = createDispatcher(requestContext, fragment, expirationCache);
        HttpServletRequest request = requestContext.getRequestForWindow(portletWindow);
        HttpServletResponse response = dispatcher.getResponseForWindow(portletWindow, requestContext);

        return buildRenderingJob( portletWindow, fragment, request, response,
                                  requestContext, isParallel,
                                  portletDefinition, dispatcher,
                                  portletContent, expirationCache, contentIsCached, timeoutMetadata );        
    }

    protected RenderingJob buildRenderingJob( PortletWindow portletWindow, ContentFragment fragment, 
                                              HttpServletRequest request, HttpServletResponse response, 
                                              RequestContext requestContext, boolean isParallel,
                                              PortletDefinitionComposite portletDefinition, 
                                              ContentDispatcherCtrl dispatcher, 
                                              PortletContent portletContent, 
                                              int expirationCache, boolean contentIsCached, long timeoutMetadata)
             throws PortletAccessDeniedException, FailedToRetrievePortletWindow, PortletEntityNotStoredException
   {    
        RenderingJob rJob = null;
               
        request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
        request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
        request.setAttribute(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE, dispatcher);
        request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, requestContext);
        request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_OBJECTS, requestContext.getObjects());                        
        request.setAttribute(PortalReservedParameters.PATH_ATTRIBUTE, requestContext.getAttribute(PortalReservedParameters.PATH_ATTRIBUTE));
        request.setAttribute(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE, portletWindow);
        
        if (portletContent == null)
        {
            portletContent = dispatcher.getPortletContent(fragment);
            fragment.setPortletContent(portletContent);
        }
        // In case of parallel mode, store attributes in a map to be refered by worker.
        if (isParallel)
        {
            Map workerAttrs = new HashMap();
            workerAttrs.put(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
            workerAttrs.put(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
            workerAttrs.put(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE, dispatcher);
            workerAttrs.put(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, requestContext);
            workerAttrs.put(PortalReservedParameters.REQUEST_CONTEXT_OBJECTS, requestContext.getObjects());                                    
            workerAttrs.put(PortalReservedParameters.PATH_ATTRIBUTE, requestContext.getAttribute(PortalReservedParameters.PATH_ATTRIBUTE));
            workerAttrs.put(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE, portletWindow);

            // the portlet invoker is not thread safe; it stores current portlet definition as a member variable.
            // so, store portlet definition as an attribute of worker
            workerAttrs.put(PortalReservedParameters.PORTLET_DEFINITION_ATTRIBUTE, portletDefinition);

            rJob = new RenderingJobImpl(container, this, portletDefinition, portletContent, fragment, dispatcher,
                                                    request, response, requestContext, portletWindow, 
                                                    statistics, expirationCache, contentIsCached, workerAttrs);
            
        }
        else
        {
            rJob = new RenderingJobImpl(container, this, portletDefinition, portletContent, fragment, dispatcher,
                                                         request, response, requestContext, portletWindow, 
                                                         statistics, expirationCache, contentIsCached );
            
        }
        
        if (isParallel)
        {
            setTimeoutOnJob(timeoutMetadata, rJob);
        }
        
        return rJob;
    }
 
    protected long getTimeoutOnJob(PortletDefinitionComposite portletDefinition)
    {
        long timeoutMetadata = 0;
        Collection timeoutFields = null;

        if (portletDefinition != null)
        {
            timeoutFields = portletDefinition.getMetadata().getFields(PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_RENDER_TIMEOUT);
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
        return timeoutMetadata;
    }
    
    protected void setTimeoutOnJob(long timeoutMetadata, RenderingJob rJob)
    {
        
        if (timeoutMetadata > 0) 
        {
            rJob.setTimeout(timeoutMetadata);
        }
        else if (this.portletTracking.getDefaultPortletTimeout() > 0) 
        {
            rJob.setTimeout(this.portletTracking.getDefaultPortletTimeout());
        }        
    }
    
    public void addTitleToHeader( PortletWindow portletWindow, ContentFragment fragment, 
                                  HttpServletRequest request, HttpServletResponse response, 
                                  ContentDispatcherCtrl dispatcher, boolean isCacheTitle )
    {
        if (overrideTitles)
        {
            try
            {
                String title = fragment.getTitle();

                if ( title == null )
                {
                    title = addTitleService.getDynamicTitle( portletWindow, request );
                }

                response.setHeader( "JS_PORTLET_TITLE", StringEscapeUtils.escapeHtml( title ) );
                dispatcher.getPortletContent(fragment).setTitle(title);          
            }
            catch (Exception e)
            {
                log.error("Unable to reteive portlet title: " + e.getMessage(), e);
            }
        }
        else
        {
            String title = null;

            if (isCacheTitle)
            {
                title = fragment.getTitle();

                if ( title == null )
                {
                    title = addTitleService.getDynamicTitle(portletWindow, request);
                }

                dispatcher.getPortletContent(fragment).setTitle(title);
            }

            if (title == null)
            {
                title = addTitleService.getDynamicTitle(portletWindow, request);
                dispatcher.getPortletContent(fragment).setTitle(title);                
            }
        }
    }
    
    protected boolean checkSecurityConstraint(PortletDefinitionComposite portlet, ContentFragment fragment)
    {
        if (fragment.getType().equals(ContentFragment.PORTLET))
        {
            if (accessController != null)
            {
                return accessController.checkPortletAccess(portlet, JetspeedActions.MASK_VIEW);
            }
        }
        return true;
    }
 
    protected void addToCache(PortletContent content)
    {
        CacheElement cachedElement = portletContentCache.createElement(content.getCacheKey(), content);
        if (content.getExpiration() == -1)
        {
            cachedElement.setTimeToIdleSeconds(portletContentCache.getTimeToIdleSeconds());
            cachedElement.setTimeToLiveSeconds(portletContentCache.getTimeToLiveSeconds());
        }
        else
        {       
            cachedElement.setTimeToIdleSeconds(content.getExpiration());
            cachedElement.setTimeToLiveSeconds(content.getExpiration());
        }
        portletContentCache.put(cachedElement);        
    }    
    
    public void notifyContentComplete(PortletContent content)
    {
        if (content.getExpiration() != 0)
            addToCache(content);
    }
    
    public PortletTrackingManager getPortletTrackingManager()
    {
        return this.portletTracking;
    }
}
