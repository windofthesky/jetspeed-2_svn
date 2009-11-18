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
package org.apache.jetspeed.pipeline.valve.impl;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ActionValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestDiagnostics;
import org.apache.jetspeed.request.RequestDiagnosticsFactory;
import org.apache.pluto.container.PortletContainer;

/**
 * <p>
 * ActionValveImpl
 * </p>
 * 
 * Default implementation of the ActionValve interface.  Expects to be
 * called after the ContainerValve has set up the appropriate action window
 * within the request context.  This should come before ANY rendering takes
 * place.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ActionValveImpl extends AbstractValve implements ActionValve
{

    private static final Logger log = LoggerFactory.getLogger(ActionValveImpl.class);
    private PortletContainer container;
    private boolean patchResponseCommitted = false;
    private JetspeedContentCache portletContentCache;

    public ActionValveImpl(PortletContainer container, JetspeedContentCache portletContentCache)
    {
        this.container = container;
        this.portletContentCache = portletContentCache;
    }
    
    public ActionValveImpl(PortletContainer container, JetspeedContentCache portletContentCache, boolean patchResponseCommitted)
    {
        this.container = container;
        this.portletContentCache = portletContentCache;        
        this.patchResponseCommitted = patchResponseCommitted;
    }

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {     
        boolean responseCommitted = false;
        boolean failure = false;
        try
        {            
            PortletWindow actionWindow = request.getActionWindow();
            if (actionWindow != null)
            {
                HttpServletRequest servletRequest = request.getRequest();
                HttpServletResponse serlvetResponse = request.getResponse();
                
                actionWindow.setAttribute("JETSPEED_ACTION", request);
                container.doAction(actionWindow,servletRequest,serlvetResponse);
                // The container redirects the client after PortletAction processing
                // so there is no need to continue the pipeline
                
                // clear the cache for all portlets on the current page
                clearPortletCacheForPage(request, actionWindow);
                
                if (patchResponseCommitted)
                {
                    responseCommitted = true;
                }
                else
                {
                    responseCommitted = serlvetResponse.isCommitted();
                }
                request.setAttribute(PortalReservedParameters.PIPELINE, null); // clear the pipeline
            }
        }
        catch (IllegalStateException e)
        {
            log.error("Illegal State Exception. Response was written to in Action Phase", e);
            failure = true;
            responseCommitted = true;
        }
        catch (Throwable t)
        {
            failure = true;
            RequestDiagnostics rd = RequestDiagnosticsFactory.newRequestDiagnostics();
            RequestDiagnosticsFactory.fillInPortletWindow(rd, request.getActionWindow(), t);
            PipelineException pe = new PipelineException(t);
            pe.setRequestDiagnostics(rd);
            throw pe;
        }
        finally
        {
            // Check if an action was processed and if its response has been committed
            // (Pluto will redirect the client after PorletAction processing)
            if ( responseCommitted )
            {
                log.info("Action processed and response committed (pipeline processing stopped)");
            }
            else if (!failure)
            {
                // Pass control to the next Valve in the Pipeline
                context.invokeNext(request);
            }
        }
    }

    protected void clearPortletCacheForPage(RequestContext request, PortletWindow actionWindow)
    throws JetspeedException
    {
        ContentPage page = request.getPage();
        if (null == page)
        {
            throw new JetspeedException("Failed to find PSML Pin ContentPageAggregator.build");
        }
        ContentFragment root = page.getRootFragment();
        if (root == null)
        {
            throw new JetspeedException("No root ContentFragment found in ContentPage");
        }
        if (!isNonStandardAction(actionWindow))
        {
            notifyFragments(root, request, page);
            
            // if the fragment is rendered from a decorator template, the target cache would not be cleared by the above notification.
            // so, let's clear target cache of action window directly again.
            String fragmentId = actionWindow.getWindowId();
            if (page.getFragmentById(fragmentId) == null)
            {
                clearTargetCache(fragmentId, request);
            }
        }
        else
        {
            ContentFragment fragment = page.getFragmentById(actionWindow.getWindowId());
            
            if (fragment != null)
            {
                clearTargetCache(fragment, request);
            }
            else
            {
                clearTargetCache(actionWindow.getId().toString(), request);
            }
        }
    }
    
    /**
     * Actions can be marked as non-standard if they don't participate in
     * JSR-168 standard action behavior. By default, actions are supposed
     * to clear the cache of all other portlets on the page.
     * By setting this parameter, we can ignore the standard behavior
     * and not clear the cache on actions. This is useful for portlets
     * which never participate with other portlets.
     * 
     */    
    protected boolean isNonStandardAction(PortletWindow actionWindow)
    {
        PortletDefinition portletDefinition = actionWindow.getPortletDefinition();
        Collection<LocalizedField> actionList = portletDefinition.getMetadata().getFields(PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_NON_STANDARD_ACTION);
        if (actionList != null && !actionList.isEmpty()) 
        {
            return true;
        }
        return false;
    }
   
    protected void notifyFragments(ContentFragment f, RequestContext context, ContentPage page)
    {
        if (f.getFragments() != null && f.getFragments().size() > 0)
        {
            for (Object child : f.getFragments())
            {
                if (!"hidden".equals(f.getState()))
                {
                    notifyFragments((ContentFragment)child, context, page);
                }
            }
        }    
        ContentCacheKey cacheKey = portletContentCache.createCacheKey(context, f.getId());
        if (portletContentCache.isKeyInCache(cacheKey))
        {
            portletContentCache.remove(cacheKey);
            portletContentCache.invalidate(context);
        }
    }

    protected void clearTargetCache(ContentFragment f, RequestContext context)
    {
        clearTargetCache(f.getId(), context);
    }
    
    protected void clearTargetCache(String fragmentId, RequestContext context)
    {
        ContentCacheKey cacheKey = portletContentCache.createCacheKey(context, fragmentId);
        
        if (portletContentCache.isKeyInCache(cacheKey))
        {
            portletContentCache.remove(cacheKey);
            portletContentCache.invalidate(context);
        }
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        // TODO Auto-generated method stub
        return "ActionValveImpl";
    }
}
