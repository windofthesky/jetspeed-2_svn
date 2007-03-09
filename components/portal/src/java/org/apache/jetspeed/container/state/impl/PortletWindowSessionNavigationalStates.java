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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

public class PortletWindowSessionNavigationalStates implements Serializable
{
    private static final class PageState implements Serializable
    {
        public Map windowStates = new HashMap();
        public String maximizedWindowId;
    }
    
    private final boolean storeParameters;
    private Map pageStates = new HashMap();

    public PortletWindowSessionNavigationalStates(boolean storeParameters)
    {
        this.storeParameters = storeParameters;
    }
    
    public void sync(RequestContext context, Page page, PortletWindowRequestNavigationalStates requestStates, JetspeedCache cache)
    {
        PageState pageState = (PageState)pageStates.get(page.getId());
        if ( pageState == null )
        {
            pageState = new PageState();
            pageStates.put(page.getId(), pageState);
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
        
        Iterator iter = requestStates.getWindowIdIterator();
        String actionWindowId = requestStates.getActionWindow() != null ? requestStates.getActionWindow().getId().toString() : null;
        boolean actionRequestState = false;
        // now synchronize requestStates and sessionStates
        while ( iter.hasNext() )
        {
            requestState = requestStates.getPortletWindowNavigationalState((String)iter.next());
            sessionState = (PortletWindowBaseNavigationalState)pageState.windowStates.get(requestState.getWindowId());
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
                pageState.windowStates.put(requestState.getWindowId(),sessionState);
            }

            actionRequestState = actionWindowId != null && actionWindowId.equals(requestState.getWindowId());
            boolean changed = syncStates(actionRequestState, requestState, sessionState);      
            if (changed)
            {
                removeFromCache(context, requestState.getWindowId(), cache);
            }
        }
        
        // now copy missing requestStates from the pageState
        iter = pageState.windowStates.keySet().iterator();
        String windowId;
        while ( iter.hasNext() )
        {
            windowId = (String)iter.next();
            requestState = requestStates.getPortletWindowNavigationalState(windowId);
            PortletWindow portletWindow = null;
            if ( requestState == null )
            {
                requestState = new PortletWindowRequestNavigationalState(windowId);
                boolean changed = syncStates(false, requestState,(PortletWindowBaseNavigationalState)pageState.windowStates.get(windowId));
                requestStates.addPortletWindowNavigationalState(windowId, requestState);
                if (changed)
                {
                    removeFromCache(context, requestState.getWindowId(), cache);
                }
            }
        }        
    }
    
    private boolean modeChanged(PortletMode req, PortletMode ses)
    {
        if (req == null)
        {
            if (ses != null && !ses.equals(PortletMode.VIEW))
                return true;
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
            if (ses != null && !ses.equals(WindowState.NORMAL))
                return true;
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

    
    private boolean syncStates(boolean actionRequestState, PortletWindowRequestNavigationalState requestState, PortletWindowBaseNavigationalState sessionState)
    {
        boolean changed = false;
        
        if (modeChanged(requestState.getPortletMode(), sessionState.getPortletMode())
                || stateChanged(requestState.getWindowState(), sessionState.getWindowState()))
            changed = true;
                       
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
                    }
                    extendedSessionState.setParametersMap(new HashMap(requestState.getParametersMap()));
                }
            }
            else if ( requestState.isClearParameters() )
            {
                extendedSessionState.setParametersMap(null);
                requestState.setClearParameters(false);
                //changed = true;
            }            
            else if ( extendedSessionState.getParametersMap() != null )
            {
                requestState.setParametersMap(new HashMap(extendedSessionState.getParametersMap()));
            }
        }
        return changed;
    }    

    protected boolean changedParameters(Map requestMap, Map sessionMap)
    {
        if (sessionMap == null || requestMap == null)
            return true;
        if (requestMap.size() != sessionMap.size())
            return true;
        Iterator ri = requestMap.entrySet().iterator();
        Iterator si = sessionMap.entrySet().iterator();
        while (ri.hasNext() && si.hasNext())
        {
            Map.Entry r = (Map.Entry)ri.next();
            Map.Entry s = (Map.Entry)si.next();
            if (!r.getKey().equals(s.getKey()))
                return true;
            String[] rvals = (String[])r.getValue();
            String[] svals = (String[])s.getValue();            
            for (int ix = 0; ix < rvals.length; ix++)
            {
                if (!rvals[ix].equals(svals[ix]))
                    return true;
            }
        }
        return false;
    }
    
    protected void removeFromCache(RequestContext context, String id, JetspeedCache cache)
    {
        String cacheKey = cache.createCacheKey(context.getUserPrincipal().getName(), id);
        if (cache.isKeyInCache(cacheKey))
        {
            cache.remove(cacheKey);
        }
        
    }
}
