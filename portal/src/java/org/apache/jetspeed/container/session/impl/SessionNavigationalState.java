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
package org.apache.jetspeed.container.session.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.container.url.impl.PortalControlParameter;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * SessionNavigationalState, stores nav state in the session, not on URL
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SessionNavigationalState
    extends
        AbstractNavigationalState
    implements 
        NavigationalState 
{    
    private Map states;
    private Map modes;
    private Map pstates;
    private Map pmodes;
    private String portalPath = null;
    private Map pages;

    static public final boolean SESSION_BASED = true;
    
    public SessionNavigationalState(RequestContext context, NavigationalStateComponent nav)
    {
        super(context, nav);
                
        states = new HashMap();
        modes = new HashMap();
        pstates = new HashMap();            
        pmodes = new HashMap();
        pages = new HashMap();
    }
        
    public WindowState getState(PortletWindow window) 
    {
        WindowState state = (WindowState)states.get(window.getId().toString());
        if (state == null)
        {
            // optimize, no need to add it if its default            
            return WindowState.NORMAL;
        }
        return state;
    }

    public void setState(PortletWindow window, WindowState state) 
    {
        states.put(window.getId().toString(), state);
    }
    
    public PortletMode getMode(PortletWindow window) 
    {
        PortletMode mode = (PortletMode)modes.get(window.getId().toString());
        if (mode == null)
        {
            // optimize, no need to add it if its default
            return PortletMode.VIEW;
        }
        return mode;
    }

    public void setMode(PortletWindow window, PortletMode mode) 
    {
        modes.put(window.getId().toString(), mode);        
    }
    
    public PortletMode getPreviousMode(PortletWindow window)
    {
        PortletMode mode = (PortletMode)pmodes.get(window.getId().toString());
        if (mode == null)
        {
            // optimize, no need to add it if its default
            return PortletMode.VIEW;
        }
        return mode;        
    }
    
    public WindowState getPreviousState(PortletWindow window)
    {
        WindowState state = (WindowState)pstates.get(window.getId().toString());
        if (state == null)
        {
            // optimize, no need to add it if its default            
            return WindowState.NORMAL;
        }
        return state;        
    }
    
    public void sync()
    {        
        PortalURL url = context.getPortalURL();
        PortalControlParameter pcp = url.getControlParameters();
        
        String pathInfo = context.getPath();
        /*
        Iterator stateful = pcp.getStateFullControlParameter().entrySet().iterator();
        while (stateful.hasNext())
        {
            Map.Entry entry = (Map.Entry)stateful.next();
            System.out.println("STATEFUL KEY = " + entry.getKey() + ", VALUE = " + entry.getValue());            
        }
        */
        Iterator stateless = pcp.getStateLessControlParameter().entrySet().iterator();
        while (stateless.hasNext())
        {
            Map.Entry entry = (Map.Entry)stateless.next();
            String key = (String)entry.getKey();
            String windowId = nav.getWindowIdFromKey(key);
            if (null == windowId)
            {
                continue;
            }
            if (key.startsWith(nav.getNavigationKey(NavigationalStateComponent.STATE)))
            {
                String windowState = (String)entry.getValue();
                WindowState previousState = (WindowState)states.get(windowId);
                if (previousState != null)
                {
                    pstates.put(windowId, previousState);
                }
                WindowState state = nav.lookupWindowState(windowState);
                states.put(windowId, state);
                
                if (state == WindowState.MAXIMIZED)
                {
                    PortletWindowAccessor accessor = (PortletWindowAccessor) Jetspeed.getComponentManager().getComponent(PortletWindowAccessor.class);
                    PortletWindow window = accessor.getPortletWindow(windowId);
                    
                    Page page = context.getPage();
                    pages.put(page.getId(), window);
                }
                else
                {
                    Page page = context.getPage();
                    pages.put(page.getId(), null);
                }
                // System.out.println("STATE UPDATED = " + windowId + ", = " + windowState);                
            }
            else if (key.startsWith(nav.getNavigationKey(NavigationalStateComponent.MODE)))
            {
                String mode = (String)entry.getValue();
                PortletMode previousMode = (PortletMode)modes.get(windowId);
                if (previousMode != null)
                {
                    pmodes.put(windowId, previousMode);
                }
                PortletMode portletMode = nav.lookupPortletMode(mode);
                modes.put(windowId, portletMode);
                PortletWindowAccessor accessor = (PortletWindowAccessor) Jetspeed.getComponentManager().getComponent(PortletWindowAccessor.class);
                PortletWindow window = accessor.getPortletWindow(windowId);
                // System.out.println("MODE UPDATED = " + windowId + ", = " + mode);
            }           
        }
    }
    
    public PortletWindow getMaximizedWindow(Page page)
    {
        return (PortletWindow)pages.get(page.getId());
    }
}
