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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.aggregator.PortletAccessDeniedException;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.PortletTrackingManager;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.aggregator.WorkerMonitor;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAccessController;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.pluto.container.PortletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
    protected final static Logger log = LoggerFactory.getLogger(PortletRendererImpl.class);

    protected WorkerMonitor workMonitor;
    protected PortletContainer container;
    protected PortalStatistics statistics;

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
     * The default OutOfService message
     */
    public static final String DEFAULT_OUT_OF_SERVICE_MESSAGE = "Portlet is not responding and has been taken out of service.";
    
    /**
     * The OutOfService message
     */
    protected String outOfServiceMessage = DEFAULT_OUT_OF_SERVICE_MESSAGE;

    protected boolean autoRefreshEnabled = true;

    public PortletRendererImpl(PortletContainer container, 
                               WorkerMonitor workMonitor,
                               PortalStatistics statistics,
                               PortletTrackingManager portletTracking,
                               boolean checkSecurityConstraints,
                               SecurityAccessController accessController,
                               JetspeedCache portletContentCache)
    {
        this.container = container;
        this.workMonitor = workMonitor;
        this.statistics = statistics;
        this.portletTracking = portletTracking;
        this.checkSecurityConstraints = checkSecurityConstraints;
        this.accessController = accessController;
        this.portletContentCache = portletContentCache;
    }

    public PortletRendererImpl(PortletContainer container, 
                               WorkerMonitor workMonitor,
                               PortalStatistics statistics)
    {
        this(container, workMonitor, statistics, null, false, null, null);
    }

    public PortletRendererImpl(PortletContainer container, 
                               WorkerMonitor workMonitor)
    {
        this( container, workMonitor, null );
    }
    
    public void start()
    {
        if (Jetspeed.getConfiguration() != null) {
            this.autoRefreshEnabled = Jetspeed.getConfiguration().getBoolean(PortalConfigurationConstants.AUTO_REFRESH_ENABLED, true);
        }
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
     */
    public void renderNow( ContentFragment fragment, RequestContext requestContext )
    {
        renderNow(fragment, requestContext, false);
    }
    /**
     * Render the specified Page fragment. Result is returned in the
     * PortletResponse.
     */
    public void renderNow( ContentFragment fragment, RequestContext requestContext, boolean spawned )
    {
        try
        {
            RenderingJob rJob = createRenderingJob(fragment, requestContext);
            if (rJob != null)
            {
                if (spawned)
                {
                    ArrayList<RenderingJob> jobs = new ArrayList<RenderingJob>();
                    jobs.add(rJob);
                    processRenderingJob(rJob, true);
                    waitForRenderingJobs(jobs);
                }
                else
                {
                    rJob.execute();
                }
            }
        }
        catch (PortletAccessDeniedException e)
        {
            fragment.overrideRenderedContent(e.getLocalizedMessage());                        
        }        
        catch (Exception e)
        {
            fragment.overrideRenderedContent(e.getLocalizedMessage());
            log.error(e.getMessage(),e);
        }
    }

    /**
     * Render the specified Page fragment. Result is returned in the
     * PortletResponse.
     */
    public void renderNow( ContentFragment fragment, HttpServletRequest request, HttpServletResponse response )
    {
        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        renderNow(fragment, requestContext);
    }
    
    protected int getExpirationCache(PortletDefinition portletDefinition)
    {
        return portletDefinition == null ? 0 : portletDefinition.getExpirationCache();
    }
    
    /** 
     * 
     * Create a rendering job for the specified Page fragment.
     * The method returns a rendering job which should be passed to 'processRenderingJob(RenderingJob job)' method.
     * @return portlet rendering job to pass to render(RenderingJob job) method
     * @throws RuntimeException
     */
    public RenderingJob createRenderingJob(ContentFragment fragment, RequestContext requestContext)
    throws PortletAccessDeniedException
    {
        RenderingJob job = null;
        try
        {
            PortletWindow portletWindow = requestContext.getPortletWindow(fragment);
            if (!portletWindow.isValid())
            {
                return null;
            }
            PortletDefinition portletDefinition = portletWindow.getPortletDefinition();     

            long timeoutMetadata = this.getTimeoutOnJob(portletDefinition);
            portletTracking.setExpiration(portletWindow, timeoutMetadata);            
            
            if ((checkSecurityConstraints || this.enforceSecurityConstraint(portletDefinition)) && 
                !checkSecurityConstraint(portletDefinition, fragment))
            {
                throw new PortletAccessDeniedException("Access Denied.");
            }
            if (portletTracking.isOutOfService(portletWindow))
            {
                log.info("Taking portlet out of service: " + portletDefinition.getUniqueName() + " for window " + portletWindow.getId());
                fragment.overrideRenderedContent(outOfServiceMessage);
                return null;
            }
            int expirationCache = getExpirationCache(portletDefinition);
            if (expirationCache != 0)
            {
                portletTracking.setExpiration(portletWindow, expirationCache);
                if (retrieveCachedContent(requestContext, portletWindow, expirationCache, portletDefinition))
                {
                    return null;
                }
            }
            // autoRefresh feature
            if (autoRefreshEnabled) {
                long refreshRate = this.getRefreshRate(portletDefinition);
                if (refreshRate != -1) {
                    portletWindow.getFragment().setRefreshRate(refreshRate);
                    String refreshFunction = this.getRefreshFunction(portletDefinition);
                    if (refreshFunction != null) {
                        portletWindow.getFragment().setRefreshFunction(refreshFunction);
                    }
                }
            }
            job = buildRenderingJob( portletWindow, requestContext, true, portletDefinition, timeoutMetadata );
        }
        catch (PortletAccessDeniedException pade)
        {
            throw pade;
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
     */
    public void processRenderingJob(RenderingJob job)
    {
        processRenderingJob(job, false);
    }

    protected void processRenderingJob(RenderingJob job, boolean parallelOnly)
    {
        try
        {
            if (parallelOnly || job.getTimeout() > 0)
            {
                workMonitor.process(job);
            }
            else
            {
                job.execute();
            }
        }
        catch (Exception e1)
        {
            log.error("render() failed: " + e1.toString(), e1);
            job.getFragment().overrideRenderedContent(e1.getLocalizedMessage());            
        }
    }
    
    /**
     * Wait for all rendering jobs in the collection to finish successfully or otherwise. 
     * @param renderingJobs the Collection of rendering job objects to wait for.
     */
    public void waitForRenderingJobs(List<RenderingJob> renderingJobs)
    {
        this.workMonitor.waitForRenderingJobs(renderingJobs);
    }

    /**
     * Retrieve cached content, if content retrieved successfully return true, if no content found return false
     * @param requestContext
     * @param portletWindow
     * @param expiration
     * @param portletDefinition
     * @return true when content found, otherwise false
     */
    protected boolean retrieveCachedContent(RequestContext requestContext, 
                                            PortletWindow portletWindow, int expiration, 
                                            PortletDefinition portletDefinition)
        throws Exception
    {
        ContentFragment fragment = portletWindow.getFragment();
        ContentCacheKey cacheKey = portletContentCache.createCacheKey(requestContext, fragment.getId());        
        CacheElement cachedElement = portletContentCache.get(cacheKey);
        
        if (cachedElement != null)
        {
            PortletContent portletContent = (PortletContent) cachedElement.getContent();
            
            PortletMode portletMode = portletContent.getPortletMode();
            WindowState windowState = portletContent.getWindowState();
            
            if (portletWindow.getPortletMode().equals(portletMode) && portletWindow.getWindowState().equals(windowState))
            {
                fragment.setPortletContent(portletContent);
                return true;
            }
        }
        
        return false;
    }
    
    protected PortletContent createPortletContent(RequestContext request, PortletWindow portletWindow, int expirationCache)
    {
        ContentCacheKey cacheKey = portletContentCache.createCacheKey(request, portletWindow.getFragment().getId());
        String title = portletWindow.getFragment().getTitle();            
        if (title == null)
        {
            title = request.getPreferedLanguage(portletWindow.getPortletDefinition()).getTitle();
        }
        if (title == null)
        {
            title = portletWindow.getPortletDefinition().getPortletName();
        }
        return new PortletContentImpl(cacheKey, expirationCache, title, portletWindow.getPortletMode(), portletWindow.getWindowState());
    }
    
    protected RenderingJob buildRenderingJob( PortletWindow portletWindow, 
                                              RequestContext requestContext, boolean isParallel,
                                              PortletDefinition portletDefinition, 
                                              long timeoutMetadata)
    {
        int expirationCache = getExpirationCache(portletDefinition);
        PortletContent portletContent= createPortletContent(requestContext, portletWindow, expirationCache);

        RenderingJob rJob = null;
               
        portletWindow.getFragment().setPortletContent(portletContent);
        
        rJob = new RenderingJobImpl(container, this, portletDefinition,
                                    requestContext.getRequest(), requestContext.getResponse(), requestContext, portletWindow, 
                                    statistics, expirationCache);
        
        if (isParallel)
        {
            setTimeoutOnJob(timeoutMetadata, rJob);
        }
        
        return rJob;
    }
 
    protected long getTimeoutOnJob(PortletDefinition portletDefinition)
    {
        long timeoutMetadata = 0;
        Collection<LocalizedField> timeoutFields = null;

        if (portletDefinition != null)
        {
            timeoutFields = portletDefinition.getMetadata().getFields(PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_RENDER_TIMEOUT);
        }

        if (timeoutFields != null && !timeoutFields.isEmpty()) 
        {
            try 
            {
                timeoutMetadata = Long.parseLong(timeoutFields.iterator().next().getValue());
            }
            catch (NumberFormatException nfe) 
            {
                log.warn("Invalid timeout metadata: " + nfe.getMessage());
            }
        }       
        return timeoutMetadata;
    }

    protected long getRefreshRate(PortletDefinition portletDefinition)
    {
        long refreshRate = -1;
        Collection<LocalizedField> refreshFields = null;

        if (portletDefinition != null)
        {
            refreshFields = portletDefinition.getMetadata().getFields(PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_REFRESH_RATE);
        }

        if (refreshFields != null && !refreshFields.isEmpty())
        {
            try
            {
                refreshRate = Long.parseLong(refreshFields.iterator().next().getValue());
            }
            catch (NumberFormatException nfe)
            {
                log.warn("Invalid refreshRate metadata: " + nfe.getMessage());
            }
        }
        return refreshRate;
    }

    protected String getRefreshFunction(PortletDefinition portletDefinition)
    {
        String refreshFunction = null;
        Collection<LocalizedField> refreshFields = null;

        if (portletDefinition != null)
        {
            refreshFields = portletDefinition.getMetadata().getFields(PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_REFRESH_FUNCTION);
        }

        if (refreshFields != null && !refreshFields.isEmpty())
        {
            refreshFunction = refreshFields.iterator().next().getValue();
        }
        return refreshFunction;
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
    
    protected boolean enforceSecurityConstraint(PortletDefinition portlet)
    {
        Collection c = portlet.getMetadata().getFields("render-time.security-constraints");
        if (c != null) 
        {
            Iterator it = c.iterator();
            if (it.hasNext()) 
            {
                LocalizedField field = (LocalizedField) it.next();
                return Boolean.parseBoolean(field.getValue());
            }
        }
        return false;
    }
    
    protected boolean checkSecurityConstraint(PortletDefinition portlet, ContentFragment fragment)
    {
        if (fragment.getType().equals(Fragment.PORTLET))
        {
            if (accessController != null)
            {
                return accessController.checkPortletAccess(portlet, JetspeedActions.MASK_VIEW);
            }
        }
        return true;
    }
 
    protected void addToCache(RequestContext context, PortletContent content)
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
        context.getPortalURL().getNavigationalState().registerPortletContentCachedForPublicRenderParameters(context, content);
    }    
    
    public void notifyContentComplete(RequestContext context, PortletWindow window)
    {
        PortletContent content = window.getFragment().getPortletContent();
        if (content.getExpiration() != 0)
        {
            addToCache(context, content);
        }
    }
    
    public PortletTrackingManager getPortletTrackingManager()
    {
        return this.portletTracking;
    }
}
