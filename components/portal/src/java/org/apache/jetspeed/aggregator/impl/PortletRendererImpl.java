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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.aggregator.UnknownPortletDefinitionException;
import org.apache.jetspeed.aggregator.WorkerMonitor;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.ContentFragment;
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
    /**
     * when rendering a portlet, the default timeout period in milliseconds
     * setting to zero will disable (no timeout) the timeout
     *  
     */
    protected long defaultPortletTimeout; 
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
    
    protected boolean overrideTitles = false;
    
    public PortletRendererImpl(PortletContainer container, 
                               PortletWindowAccessor windowAccessor,
                               WorkerMonitor workMonitor,
                               PortalStatistics statistics,
                               DynamicTitleService addTitleService,
                               long defaultPortletTimeout,
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
        this.defaultPortletTimeout = defaultPortletTimeout;
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
            long defaultPortletTimeout,
            boolean checkSecurityConstraints,
            SecurityAccessController accessController,
            JetspeedCache portletContentCache)
    {
        this(container, windowAccessor, workMonitor, statistics, 
             addTitleService, defaultPortletTimeout, checkSecurityConstraints,
             accessController, portletContentCache, false);
    }
    
    public PortletRendererImpl(PortletContainer container, 
                               PortletWindowAccessor windowAccessor,
                               WorkerMonitor workMonitor,
                               PortalStatistics statistics,
                               DynamicTitleService addTitleService)
    {
        this(container, windowAccessor, workMonitor, statistics, null, 0, false, null, null, true);
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
            RenderingJob rJob = buildRenderingJob(portletWindow, fragment, servletRequest, servletResponse,
                    requestContext, false, portletDefinition, dispatcher, null);
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
     */
    public void renderNow( ContentFragment fragment, HttpServletRequest request, HttpServletResponse response )          
    {
        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        renderNow(fragment, requestContext);
    }
    
    protected int getExpirationCache(PortletDefinitionComposite portletDefinition)
    {
        String expiration = portletDefinition.getExpirationCache();
        if (expiration == null)
            return 0;
        return Integer.parseInt(expiration);
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
            int expirationCache = getExpirationCache(portletDefinition);
            if (expirationCache != 0)
            {
                if (retrieveCachedContent(requestContext, fragment, portletWindow, expirationCache, portletDefinition))
                    return null;
                contentIsCached = true;
            }
            if (dispatcher == null)
            {
                dispatcher = createDispatcher(requestContext, fragment, expirationCache);
            }
                        
            servletRequest = requestContext.getRequestForWindow(portletWindow);
            servletResponse = dispatcher.getResponseForWindow(portletWindow, requestContext);
            rJob = buildRenderingJob(portletWindow, fragment, servletRequest, servletResponse, 
                                      requestContext, true, portletDefinition, dispatcher, null);

            if (rJob.getTimeout() > 0) 
            {
                workMonitor.process(rJob);
            } 
            else 
            {
                rJob.execute();
            }
            addTitleToHeader(portletWindow, fragment, servletRequest, servletResponse, dispatcher, contentIsCached);
        }
        catch (PortletAccessDeniedException e)
        {
            fragment.overrideRenderedContent(e.getLocalizedMessage());                        
        }
        catch (Exception e1)
        {
            log.error("render() failed: " + e1.toString(), e1);
            fragment.overrideRenderedContent(e1.getLocalizedMessage());            
        }
        return rJob;
    }

    /**
     * Retrieve cached content, if content retrieved successfully return true, if no content found return false
     * @param requestContext
     * @param fragment
     * @param portletWindow
     * @return true when content found, otherwise false
     */
    protected boolean retrieveCachedContent(RequestContext requestContext, ContentFragment fragment, 
                           PortletWindow portletWindow, int expiration, PortletDefinitionComposite portletDefinition)
    throws Exception
    {
        String cacheKey = portletContentCache.createCacheKey(requestContext.getUserPrincipal().getName(), fragment.getId());
        CacheElement cachedElement = portletContentCache.get(cacheKey);
        if (cachedElement != null)
        {
            PortletContent portletContent = (PortletContent)cachedElement.getContent();            
            fragment.setPortletContent(portletContent);
            ContentDispatcherCtrl dispatcher = new ContentDispatcherImpl(portletContent);
            HttpServletRequest servletRequest = requestContext.getRequestForWindow(portletWindow);
            HttpServletResponse servletResponse = dispatcher.getResponseForWindow(portletWindow, requestContext);            
            this.addTitleService.setDynamicTitle(portletWindow, servletRequest, dispatcher.getPortletContent(fragment).getTitle());
            return true;
        }        
        return false;
    }
    
    public ContentDispatcherCtrl createDispatcher(RequestContext request, ContentFragment fragment, int expirationCache)
    {
        String cacheKey = portletContentCache.createCacheKey(request.getUserPrincipal().getName(), fragment.getId());
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
            return portletWindow;

    }
    
    protected RenderingJob buildRenderingJob( PortletWindow portletWindow, ContentFragment fragment, HttpServletRequest request,
                                              HttpServletResponse response, RequestContext requestContext, boolean isParallel,
                                              PortletDefinitionComposite portletDefinition, ContentDispatcherCtrl dispatcher, PortletContent portletContent )
        throws PortletAccessDeniedException, FailedToRetrievePortletWindow, FailedToRenderFragmentException, PortletEntityNotStoredException
    {
        RenderingJob rJob = null;
               
        request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
        request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
        request.setAttribute(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE, dispatcher);
        request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, requestContext);                
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
        setTimeoutOnJob(portletDefinition, rJob);
        return rJob;
    }
        
    protected void setTimeoutOnJob(PortletDefinitionComposite portletDefinition, RenderingJob rJob)
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
        if (timeoutMetadata > 0) 
        {
            rJob.setTimeout(timeoutMetadata);
        } 
        else if (this.defaultPortletTimeout > 0) 
        {
            rJob.setTimeout(this.defaultPortletTimeout);
        }        
    }
    
    protected void addTitleToHeader( PortletWindow portletWindow, ContentFragment fragment, 
             HttpServletRequest request, HttpServletResponse response, ContentDispatcherCtrl dispatcher, boolean isCacheTitle)
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
            if (isCacheTitle)
            {
                String title = fragment.getTitle();
                if ( title == null )
                {
                    title = addTitleService.getDynamicTitle(portletWindow, request);
                }
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
}
