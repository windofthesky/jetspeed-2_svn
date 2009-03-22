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
import javax.xml.namespace.QName;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.container.PortletWindow;

public class PortletWindowSessionNavigationalStates implements Serializable
{
    private static final long serialVersionUID = -2891442112700830546L;

    private static final class PageState implements Serializable
    {
        private static final long serialVersionUID = -2730733728229116932L;
        
        public Map<String, PortletWindowBaseNavigationalState> windowStates = Collections.synchronizedMap(new HashMap<String, PortletWindowBaseNavigationalState>());
        public String maximizedWindowId;
    }

    private final SessionNavigationalState navState;
    private final boolean storeParameters;
    private Map<String, PageState> pageStates = new HashMap<String, PageState>();
    private Map<QName, String[]> publicRenderParametersMap = Collections.synchronizedMap(new HashMap<QName, String[]>());

    public PortletWindowSessionNavigationalStates(SessionNavigationalState navState, boolean storeParameters)
    {
        this.navState = navState;
        this.storeParameters = storeParameters;
    }
    
    /*
     * JS2-806 patch
     * <p>
     *   reset all portlets on page to mode VIEW and window state NORMAL in the case of page navigation.
     * </p>
     */
    public void changeAllPortletsToViewModeAndNormalWindowState(RequestContext context, Page page, PortletWindowRequestNavigationalStates requestStates, JetspeedContentCache cache, JetspeedContentCache decorationCache)
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
            Iterator<String> iter = requestStates.getWindowIdIterator();
            iter = pageState.windowStates.keySet().iterator();
            String windowId;
            while ( iter.hasNext() )
            {
                windowId = (String)iter.next();
                requestState = requestStates.getPortletWindowNavigationalState(windowId);
                if ( requestState == null )
                {
                    requestState = new PortletWindowRequestNavigationalState(windowId, navState.getActionScopedRequestAttributes(windowId));
                }
                //regardless, reset portlet mode and window state
                requestState.setPortletMode(viewMode);
                requestState.setWindowState(normalWindowState);
                // get the session case just in case and create a new one
                sessionState = (PortletWindowBaseNavigationalState)pageState.windowStates.get(requestState.getWindowId());
                if ( sessionState == null )
                {
                    if ( storeParameters )
                    {
                        sessionState = new PortletWindowExtendedNavigationalState(requestState.isActionScopedRequestAttributes());
                    }
                    else
                    {
                        sessionState = new PortletWindowBaseNavigationalState();
                    }
                    pageState.windowStates.put(requestState.getWindowId(),sessionState);
                }
                //Now, sync up. NOTE we should not be in this method if there is an portlet action request.
                boolean changed = syncStates(false, requestStates, requestState, (PortletWindowBaseNavigationalState)pageState.windowStates.get(windowId));
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
        } 
    }
    
    public void sync(RequestContext context, Page page, PortletWindowRequestNavigationalStates requestStates, JetspeedContentCache cache, JetspeedContentCache decorationCache)    
    {
        PageState pageState = null;
        synchronized (pageStates)
        {
            pageState = (PageState)pageStates.get(page.getId());
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
                }
                else
                {
                    // check PortletWindow still exists...
                    // depends on PortletWindowAccessor cache to be active
                    PortletWindowAccessor accessor = 
                        (PortletWindowAccessor)Jetspeed.getComponentManager().getComponent(PortletWindowAccessor.class);
                    PortletWindow maximizedWindow = accessor.getPortletWindow(pageState.maximizedWindowId);
                    if ( maximizedWindow == null )
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
                sessionState = (PortletWindowBaseNavigationalState)pageState.windowStates.get(pageState.maximizedWindowId);
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
        String actionWindowId = requestStates.getActionWindow() != null ? requestStates.getActionWindow().getId().toString() : null;
        boolean actionRequestState = false;
        while ( iter.hasNext() )
        {
            requestState = requestStates.getPortletWindowNavigationalState((String)iter.next());
            sessionState = (PortletWindowBaseNavigationalState)pageState.windowStates.get(requestState.getWindowId());
            if ( sessionState == null )
            {
                if ( storeParameters )
                {
                    sessionState = new PortletWindowExtendedNavigationalState(requestState.isActionScopedRequestAttributes());
                }
                else
                {
                    sessionState = new PortletWindowBaseNavigationalState();
                }
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
        synchronized (pageState.windowStates)
        {
            iter = pageState.windowStates.keySet().iterator();
            String windowId;
            while ( iter.hasNext() )
            {
                windowId = (String)iter.next();
                requestState = requestStates.getPortletWindowNavigationalState(windowId);
                if ( requestState == null )
                {
                    requestState = new PortletWindowRequestNavigationalState(windowId, navState.getActionScopedRequestAttributes(windowId));
                    boolean changed = syncStates(false, requestStates, requestState, (PortletWindowBaseNavigationalState)pageState.windowStates.get(windowId));
                    requestStates.addPortletWindowNavigationalState(windowId, requestState);
                    if (changed)
                    {
                        removeFromCache(context, requestState.getWindowId(), cache);
                        removeFromCache(context, page.getId(), decorationCache);                    
                        if (storeParameters)
                        {
                            sessionState = pageState.windowStates.get(windowId);
                            ((PortletWindowExtendedNavigationalState)sessionState).resetDecoratorActionEncodings();
                        }
                    }
                }
            }
        }        

        // sync session states public render parameters
        Map<QName, String[]> changedPublicRenderParametersMap = null;
        requestStates.setPublicRenderParametersMap(new HashMap<QName, String[]>());
        iter = requestStates.getWindowIdIterator();
        while (iter.hasNext())
        {
            String windowId = iter.next();
            requestState = requestStates.getPortletWindowNavigationalState(windowId);
            if (requestState.getPublicRenderParametersMap() != null)
            {
                // determine changed session states public render parameters
                for (Map.Entry<String, String[]> parameter : requestState.getPublicRenderParametersMap().entrySet())
                {
                    String parameterName = parameter.getKey();
                    String[] requestParameterValues = parameter.getValue();
                    // get qname for request public render parameter name
                    QName parameterQName = navState.getPublicRenderParameterQName(windowId, parameterName);
                    if (parameterQName != null)
                    {
                        String[] sessionParameterValues = publicRenderParametersMap.get(parameterQName);
                        if (changedParameterValues(requestParameterValues, sessionParameterValues))
                        {
                            // track changed public render parameter qnames and values
                            if (changedPublicRenderParametersMap == null)
                            {
                                changedPublicRenderParametersMap = new HashMap<QName, String[]>();
                            }
                            changedPublicRenderParametersMap.put(parameterQName, requestParameterValues);
                        }
                    }
                }
            }
        }
        
        // sync session states public render parameter changes
        if (changedPublicRenderParametersMap != null)
        {
            for (Map.Entry<QName, String[]> parameterChange : changedPublicRenderParametersMap.entrySet())
            {
                QName changedParameterQName = parameterChange.getKey();
                String[] changedParameterValues = parameterChange.getValue();
                if (changedParameterValues != null)
                {
                    // sync new or updated public render parameter value
                    publicRenderParametersMap.put(changedParameterQName, changedParameterValues);
                }
                else
                {
                    // null public render parameter value implies delete
                    publicRenderParametersMap.remove(changedParameterQName);                    
                }
            }
        }
        
        // clear cached contexts and encodings if public render parameters have changed
        if (changedPublicRenderParametersMap != null)
        {
            // find pages and windows in session state that have public render parameters
            synchronized (pageStates)
            {
                for (Map.Entry<String, PageState> sessionPageState : pageStates.entrySet())
                {
                    String pageId = sessionPageState.getKey();
                    Map<String, PortletWindowBaseNavigationalState> windowStates = sessionPageState.getValue().windowStates;
                    for (Map.Entry<String, PortletWindowBaseNavigationalState> windowState : windowStates.entrySet())
                    {
                        String windowId = windowState.getKey();
                        sessionState = windowState.getValue();
                        // test window for changed public render parameter qnames
                        if (navState.hasPublicRenderParameterQNames(windowId, changedPublicRenderParametersMap.keySet()))
                        {
                            // clear cached items associated with window
                            removeFromCache(context, windowId, cache);
                            removeFromCache(context, pageId, decorationCache);                    
                            if (storeParameters)
                            {
                                ((PortletWindowExtendedNavigationalState)sessionState).resetDecoratorActionEncodings();
                            }                            
                        }
                    }
                }
            }
        }
        
        // reset and sync request states public render parameters
        requestStates.setPublicRenderParametersMap(null);
        iter = requestStates.getWindowIdIterator();
        while (iter.hasNext())
        {
            String windowId = iter.next();
            requestState = requestStates.getPortletWindowNavigationalState(windowId);
            requestState.setPublicRenderParametersMap(null);
            // get all public render parameter names for window
            Map<String, QName> parameterNames = navState.getPublicRenderParameterNamesMap(windowId);
            if (parameterNames != null)
            {
                // get synced session public render parameter values 
                for (Map.Entry<String, QName> parameterNamesEntry : parameterNames.entrySet())
                {
                    QName parameterQName = parameterNamesEntry.getValue();
                    String[] parameterValues = publicRenderParametersMap.get(parameterQName);
                    if (parameterValues != null)
                    {
                        // refresh request state and states public render parameters
                        String parameterName = parameterNamesEntry.getKey();
                        requestState.setPublicRenderParameters(parameterName, parameterValues);
                        requestStates.setPublicRenderParameters(parameterQName, parameterValues);
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

    
    private boolean syncStates(boolean actionRequestState, PortletWindowRequestNavigationalStates requestStates, PortletWindowRequestNavigationalState requestState, PortletWindowBaseNavigationalState sessionState)
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
            
            // sync action scope parameter
            if (requestState.isActionScopedRequestAttributes() && extendedSessionState.isActionScopedRequestAttributes())
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
    
    protected Map<String, PortletWindowBaseNavigationalState> getWindowStates(Page page)
    {
        PageState pageState = (PageState)pageStates.get(page.getId());
        return pageState != null ? pageState.windowStates : null;
    }
}
