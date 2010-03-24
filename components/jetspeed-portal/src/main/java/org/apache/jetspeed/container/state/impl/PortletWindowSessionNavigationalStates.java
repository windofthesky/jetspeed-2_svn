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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.container.PortletWindow;

public class PortletWindowSessionNavigationalStates implements Serializable
{
    private static final long serialVersionUID = -2891442112700830546L;

    static final class PageState implements Serializable
    {
        private static final long serialVersionUID = -2730733728229116932L;
        
        public Map<String, PortletWindowBaseNavigationalState> windowStates = Collections.synchronizedMap(new HashMap<String, PortletWindowBaseNavigationalState>());
        public String maximizedWindowId;
    }

    private final boolean storeParameters;
    private Map<String, PageState> pageStates = new HashMap<String, PageState>();

    public PortletWindowSessionNavigationalStates(boolean storeParameters)
    {
        this.storeParameters = storeParameters;
    }
    
    /*
     * JS2-806 patch
     * <p>
     *   reset all portlets on page to mode VIEW and window state NORMAL in the case of page navigation.
     * </p>
     */
    public void changeAllPortletsToViewModeAndNormalWindowState(RequestContext context, ContentPage page, PortletWindowRequestNavigationalStates requestStates, JetspeedContentCache cache, JetspeedContentCache decorationCache)
    {
        final PortletMode viewMode = PortletMode.VIEW;
        final WindowState normalWindowState = WindowState.NORMAL;
        
        PageState pageState = null;
        synchronized (pageStates)
        {
            pageState = pageStates.get(page.getId());
            if (pageState == null)
            {
                pageState = new PageState();
                pageStates.put(page.getId(), pageState);
            }
        }
        
        PortletWindowRequestNavigationalState requestState = null;
        PortletWindowBaseNavigationalState sessionState = null;

        //remove any maximized windows
        if (null != pageState.maximizedWindowId)
        {
            pageState.windowStates.remove(pageState.maximizedWindowId);
            removeFromCache(context, pageState.maximizedWindowId, cache);
            removeFromCache(context, pageState.maximizedWindowId, decorationCache);                        
            pageState.maximizedWindowId = null;
        }
        synchronized (pageState.windowStates)
        {
            Iterator<String> iter = pageState.windowStates.keySet().iterator();
            String windowId;
            while ( iter.hasNext() )
            {
                windowId = iter.next();
                requestState = requestStates.getPortletWindowNavigationalState(windowId);
                sessionState = pageState.windowStates.get(windowId);
                if ( requestState == null )
                {
                    requestState = new PortletWindowRequestNavigationalState(windowId);
                    requestState.setActionScopedRequestAttributes(sessionState.isActionScopedRequestAttributes());
                }
                //regardless, reset portlet mode and window state
                requestState.setPortletMode(viewMode);
                requestState.setWindowState(normalWindowState);
                //Now, sync up. NOTE we should not be in this method if there is an portlet action request.
                boolean changed = syncStates(false, requestStates, requestState, sessionState);
                if (changed)
                {
                    removeFromCache(context, windowId, cache);
                    removeFromCache(context, page.getId(), decorationCache);   
                    if (storeParameters)
                    {
                        ((PortletWindowExtendedNavigationalState)sessionState).resetDecoratorActionEncodings();
                    }
                }
            }      
        } 
    }
    
    public void sync(RequestContext context, ContentPage page, 
                     PortletWindowRequestNavigationalStates requestStates, 
                     JetspeedContentCache cache, JetspeedContentCache decorationCache)
    {
        PageState pageState = null;
        synchronized (pageStates)
        {
            pageState = pageStates.get(page.getId());
            if (pageState == null)
            {
                pageState = new PageState();
                pageStates.put(page.getId(), pageState);
            }
        }
        
        PortletWindowRequestNavigationalState requestState = null;
        PortletWindowBaseNavigationalState sessionState = null;

        // first synchronize MAXIMIZED window
        if ( pageState.maximizedWindowId != null )
        {
            String requestMaximizedWindowId = null;
            
            if ( requestStates.getMaximizedWindow() != null )
            {
                requestMaximizedWindowId = requestStates.getMaximizedWindow().getId().toString();
            }
                
            if ( requestMaximizedWindowId == null )
            {
                // check clearing MAXIMIZED window
                requestState = requestStates.getPortletWindowNavigationalState(pageState.maximizedWindowId);
                if ( requestState != null )
                {
                    if (requestState.getWindowState() != null)
                    {
                        pageState.maximizedWindowId = null;
                        // syncState will reset the sessionState.WindowState
                    }
                    else
                    {
                        requestStates.setMaximizedWindow(context.getPortletWindow(requestState.getWindowId()));
                    }
                }
                else
                {
                    PortletWindow maximizedWindow = context.resolvePortletWindow(pageState.maximizedWindowId);
                    if (maximizedWindow == null || !maximizedWindow.isValid())
                    {
                        // gone: remove sessionState
                        pageState.windowStates.remove(pageState.maximizedWindowId);
                        pageState.maximizedWindowId = null;
                    }
                    else
                    {
                        requestStates.setMaximizedWindow(maximizedWindow);
                    }
                }
            }
            else if ( !requestMaximizedWindowId.equals( pageState.maximizedWindowId ))
            {
                // When can a non-maximized window request maximized state while another already has it?
                // Maybe from a decoration portlet which always needs to be viewable?
                requestState = requestStates.getPortletWindowNavigationalState(pageState.maximizedWindowId);
                sessionState = pageState.windowStates.get(pageState.maximizedWindowId);
                if ( requestState == null || requestState.getWindowState() == null )
                {
                    // need to clear it ourselves first
                    sessionState.setWindowState(null);
                }
            }
        }
        
        if ( requestStates.getMaximizedWindow() != null )
        {
            // store the new MAXIMIZED window
            pageState.maximizedWindowId = requestStates.getMaximizedWindow().getId().toString();
        }
        
        // now synchronize requestStates and sessionStates
        Iterator<String> iter = requestStates.getWindowIdIterator();
        String actionWindowId = requestStates.getActionWindow() != null ? requestStates.getActionWindow().getWindowId() : null;
        boolean actionRequestState = false;
        while ( iter.hasNext() )
        {
            requestState = requestStates.getPortletWindowNavigationalState(iter.next());
            sessionState = pageState.windowStates.get(requestState.getWindowId());
            if ( sessionState == null )
            {
                if ( storeParameters )
                {
                    sessionState = new PortletWindowExtendedNavigationalState();
                }
                else
                {
                    sessionState = new PortletWindowBaseNavigationalState();
                }
                sessionState.setActionScopedRequestAttributes(requestState.isActionScopedRequestAttributes());
                pageState.windowStates.put(requestState.getWindowId(),sessionState);
            }

            actionRequestState = actionWindowId != null && actionWindowId.equals(requestState.getWindowId());
            boolean changed = syncStates(actionRequestState, requestStates, requestState, sessionState);      
            if (changed)
            {
                removeFromCache(context, requestState.getWindowId(), cache);
                removeFromCache(context, page.getId(), decorationCache);
                if (storeParameters)
                {
                    ((PortletWindowExtendedNavigationalState)sessionState).resetDecoratorActionEncodings();
                }
            }
        }

        // now copy missing requestStates from the pageState
        synchronized(pageState.windowStates)
        {
            iter = pageState.windowStates.keySet().iterator();
            String windowId;
            while ( iter.hasNext() )
            {
                windowId = iter.next();
                requestState = requestStates.getPortletWindowNavigationalState(windowId);
                if ( requestState == null )
                {
                    PortletWindow window = context.resolvePortletWindow(windowId);
                    if (window != null)
                    {
                        requestState = new PortletWindowRequestNavigationalState(windowId);
                        requestState.setPortletDefinition(window.getPortletDefinition());
                        sessionState = pageState.windowStates.get(windowId);
                        requestState.setActionScopedRequestAttributes(sessionState.isActionScopedRequestAttributes());
                        requestStates.addPortletWindowNavigationalState(windowId, requestState);
                        boolean changed = syncStates(false, requestStates, requestState, sessionState);
                        if (changed)
                        {
                            removeFromCache(context, windowId, cache);
                            removeFromCache(context, page.getId(), decorationCache);                    
                            if (storeParameters)
                            {
                                sessionState = pageState.windowStates.get(windowId);
                                ((PortletWindowExtendedNavigationalState)sessionState).resetDecoratorActionEncodings();
                            }
                        }
                    }
                    else
                    {
                        removeFromCache(context, windowId, cache);
                        removeFromCache(context, page.getId(), decorationCache);                    
                        iter.remove();
                    }
                }
            }
        }        
    }
    
    private boolean modeChanged(PortletMode req, PortletMode ses)
    {
        if (req == null)
        {
            //if (ses != null && !ses.equals(PortletMode.VIEW))
            //   return true;
            return false;
        }
        else
        {
            if (ses == null)
            {
                if (req.equals(PortletMode.VIEW))
                    return false;
                return true;
            }
        }
        return !req.equals(ses);
    }
    
    private boolean stateChanged(WindowState req, WindowState ses)
    {
        if (req == null)
        {
            //if (ses != null && !ses.equals(WindowState.NORMAL))
            //    return true;
            return false;
        }
        else
        {
            if (ses == null)
            {
                if (req.equals(WindowState.NORMAL))
                    return false;
                return true;
            }
        }
        return !req.equals(ses);
    }

    
    private boolean syncStates(boolean actionRequestState,
                               PortletWindowRequestNavigationalStates requestStates, PortletWindowRequestNavigationalState requestState, 
                               PortletWindowBaseNavigationalState sessionState)
    {
        boolean changed = false;
        
        if (modeChanged(requestState.getPortletMode(), sessionState.getPortletMode()) ||
            stateChanged(requestState.getWindowState(), sessionState.getWindowState()))
        {
            changed = true;
        }
                       
        if ( requestState.getPortletMode() != null )
        {
            if ( requestState.getPortletMode().equals(PortletMode.VIEW) )
            {
                sessionState.setPortletMode(null);
            }
            else
            {
                sessionState.setPortletMode(requestState.getPortletMode());
            }
        }
        else if ( sessionState.getPortletMode() == null )
        {
            requestState.setPortletMode(PortletMode.VIEW);
        }
        else
        {
            requestState.setPortletMode(sessionState.getPortletMode());
        }
        
        if ( requestState.getWindowState() != null )
        {
            if ( requestState.getWindowState().equals(WindowState.NORMAL) )
            {
                sessionState.setWindowState(null);
            }
            else
            {
                sessionState.setWindowState(requestState.getWindowState());
            }
        }
        else if ( sessionState.getWindowState() == null )
        {
            requestState.setWindowState(WindowState.NORMAL);
        }
        else        
        {
            requestState.setWindowState(sessionState.getWindowState());
        }
        
        if (requestState.isActionScopedRequestAttributes() != sessionState.isActionScopedRequestAttributes())
        {
            changed = true;
            sessionState.setActionScopedRequestAttributes(requestState.isActionScopedRequestAttributes());
        }
        
        if (storeParameters)
        {
            // sync parameters
            PortletWindowExtendedNavigationalState extendedSessionState = (PortletWindowExtendedNavigationalState)sessionState;
            if ( requestState.getParametersMap() != null )
            {
                if ( actionRequestState )
                {
                    // never store ActionRequest parameters in session
                    extendedSessionState.setParametersMap(null);
                }
                else 
                {
                    if (changedParameters(requestState.getParametersMap(), extendedSessionState.getParametersMap()))
                    {
                        changed = true;
                        extendedSessionState.setParametersMap(new HashMap<String, String[]>(requestState.getParametersMap()));
                    }
                }
            }
            else if ( requestState.isClearParameters() )
            {
                extendedSessionState.setParametersMap(null);
            }            
            else if ( extendedSessionState.getParametersMap() != null )
            {
                requestState.setParametersMap(new HashMap<String, String[]>(extendedSessionState.getParametersMap()));
            }
            
            // sync action scope parameters
            if (requestState.isActionScopedRequestAttributes())
            {
                if (requestState.getActionScopeId() != null)
                {
                    if (changedActionScope(requestState.getActionScopeId(), extendedSessionState.getActionScopeId()))
                    {
                        changed = true;
                    }
                    extendedSessionState.setActionScopeId(requestState.getActionScopeId());
                    extendedSessionState.setActionScopeRendered(requestState.isActionScopeRendered());
                }
                else if (requestState.isClearParameters())
                {
                    extendedSessionState.setActionScopeId(null);
                    extendedSessionState.setActionScopeRendered(false);
                }
                else if (extendedSessionState.getActionScopeId() != null)
                {
                    requestState.setActionScopeId(extendedSessionState.getActionScopeId());
                    requestState.setActionScopeRendered(extendedSessionState.isActionScopeRendered());                
                }
            }
            else
            {
                // clear out any action scope parameters
                requestState.setActionScopeId(null);
                requestState.setActionScopeRendered(false);
                extendedSessionState.setActionScopeId(null);
                extendedSessionState.setActionScopeRendered(false);
            }

            // reset clear parameters
            if (requestState.isClearParameters())
            {
                requestState.setClearParameters(false);
            }            
        }
        return changed;
    }    

    protected boolean changedParameters(Map<String, String[]> requestMap, Map<String, String[]> sessionMap)
    {
        if (requestMap == null || sessionMap == null)
        {
            return true;
        }
        if (requestMap.size() != sessionMap.size())
        {
            return true;
        }
        Iterator<Map.Entry<String, String[]>> ri = requestMap.entrySet().iterator();
        while (ri.hasNext())
        {
            Map.Entry<String, String[]> r = ri.next();
            if (changedParameterValues(r.getValue(), sessionMap.get(r.getKey())))
            {
                return true;
            }
        }
        return false;
    }
    
    protected boolean changedParameterValues(String[] requestValues, String[] sessionValues)
    {
        if ((requestValues == null) || (sessionValues == null) || (requestValues.length != sessionValues.length))
        {
            return true;
        }
        for (int ix = 0; ix < requestValues.length; ix++)
        {
            if (!requestValues[ix].equals(sessionValues[ix]))
            {
                return true;
            }
        }
        return false;
    }

    protected boolean changedActionScope(String requestActionScope, String sessionActionScope)
    {
        if ((requestActionScope == null) || (sessionActionScope == null))
        {
            return true;
        }
        return !requestActionScope.equals(sessionActionScope);
    }
    
    protected void removeFromCache(RequestContext context, String id, JetspeedContentCache cache)
    {
        if (cache == null)
            return;
        ContentCacheKey cacheKey = cache.createCacheKey(context, id);
        if (cache.isKeyInCache(cacheKey))
        {
            cache.remove(cacheKey);
        }
        cache.invalidate(context);
    }
    
    protected Map<String, PortletWindowBaseNavigationalState> getWindowStates(ContentPage page)
    {
        PageState pageState = pageStates.get(page.getId());
        return pageState != null ? pageState.windowStates : null;
    }
}
