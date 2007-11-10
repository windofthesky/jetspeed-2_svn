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
package org.apache.jetspeed.container.state.impl;

import java.util.Map;

import javax.portlet.WindowState;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;

/**
 * SessionNavigationalState, stores nav parameters in the session, not on URL
 *
 * <p>
 * Added the ability to reset portlet mode and window states to VIEW and NORMAL in the case
 * of page navigation. JS2-806
 * </p>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SessionNavigationalState extends AbstractNavigationalState
{   
    protected final Log log = LogFactory.getLog(getClass());    
    private Map currentPageWindowStates;
    private boolean clearPortletsModeAndWindowStateEnabled = false;
    
    public SessionNavigationalState(NavigationalStateCodec codec, JetspeedContentCache cache)
    {
        super(codec, cache);
    }
    
    public SessionNavigationalState(NavigationalStateCodec codec, JetspeedContentCache cache, JetspeedContentCache decorationCache)
    {
        super(codec, cache, decorationCache);
    }    

    public synchronized void sync(RequestContext context)
    {
        PortletWindowRequestNavigationalStates requestStates = getPortletWindowRequestNavigationalStates();
        
        // for Resource (PortletURL) requests, session state is never synchronized
        boolean transientNavState = requestStates.getResourceWindow() != null;
        
        String clearCacheWindowId = null;
        
        if (!transientNavState)
        {
            // Check if a maximized window is set in the request.
            // This can mean a window with state MAXIMIZED *or* SOLO.
            // With a SOLO state, also skip all synchroniziations!
            String requestMaximizedWindowId = null;
            
            if ( requestStates.getMaximizedWindow() != null )
            {
                requestMaximizedWindowId = requestStates.getMaximizedWindow().getId().toString();
                WindowState state = requestStates.getPortletWindowNavigationalState(requestMaximizedWindowId).getWindowState();
                transientNavState = JetspeedActions.SOLO_STATE.equals(state);
                clearCacheWindowId = requestMaximizedWindowId;
            }
            
        }
        if (transientNavState)
        {
            // no navState synchronizations
            
            if (clearCacheWindowId != null)
            {
                HttpSession session = context.getRequest().getSession();
                if ( session != null )
                {
                    PortletWindowSessionNavigationalStates sessionStates = (PortletWindowSessionNavigationalStates)session.getAttribute(NavigationalState.NAVSTATE_SESSION_KEY);
                    if ( sessionStates != null )
                    {
                        sessionStates.removeFromCache(context, clearCacheWindowId, cache);
                        ContentPage page = context.getPage();
                        sessionStates.removeFromCache(context, page.getId(), decorationCache);                        
                    }
                }
            }
        }
        else
        {
            HttpSession session = context.getRequest().getSession();
            if ( session != null )
            {
                PortletWindowSessionNavigationalStates sessionStates = (PortletWindowSessionNavigationalStates)session.getAttribute(NavigationalState.NAVSTATE_SESSION_KEY);
                if ( sessionStates == null )
                {
                    sessionStates = new PortletWindowSessionNavigationalStates(isRenderParameterStateFull());
                    session.setAttribute(NavigationalState.NAVSTATE_SESSION_KEY, sessionStates);
                }
                Page page = context.getPage();
                // JS2-806
                if (isClearPortletsModeAndWindowStateEnabled())
                {
                    sessionStates.changeAllPortletsToViewModeAndNormalWindowState(context, page, requestStates, cache, decorationCache);
                }
                else
                {
                    sessionStates.sync(context, (Page) context.getPage(), requestStates, cache, decorationCache);
                }
                if (isNavigationalParameterStateFull() && isRenderParameterStateFull())
                {
                    currentPageWindowStates = sessionStates.getWindowStates(page);
                }
            }
        }
    }
    
    public Map getCurrentPageWindowStates()
    {
        return currentPageWindowStates;
    }
    
    public boolean isNavigationalParameterStateFull()
    {
        return true;
    }

    public boolean isRenderParameterStateFull()
    {
        return false;
    }

    protected void setClearPortletsModeAndWindowStateEnabled(
            boolean clearPortletsModeAndWindowStateEnabled)
    {
        this.clearPortletsModeAndWindowStateEnabled = clearPortletsModeAndWindowStateEnabled;
    }

    protected boolean isClearPortletsModeAndWindowStateEnabled()
    {
        return clearPortletsModeAndWindowStateEnabled;
    }
}
