/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.container.state.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Page;
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
    
    public void sync(Page page, PortletWindowRequestNavigationalStates requestStates)
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
            syncStates(actionRequestState, requestState, sessionState);            
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
                syncStates(false, requestState,(PortletWindowBaseNavigationalState)pageState.windowStates.get(windowId));
                requestStates.addPortletWindowNavigationalState(windowId, requestState);
            }
        }        
    }
    
    private void syncStates(boolean actionRequestState, PortletWindowRequestNavigationalState requestState, PortletWindowBaseNavigationalState sessionState)
    {
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
                    extendedSessionState.setParametersMap(new HashMap(requestState.getParametersMap()));
                }
            }
            else if ( requestState.isClearParameters() )
            {
                extendedSessionState.setParametersMap(null);
                requestState.setClearParameters(false);
            }            
            else if ( extendedSessionState.getParametersMap() != null )
            {
                requestState.setParametersMap(new HashMap(extendedSessionState.getParametersMap()));
            }
        }
    }    
}
