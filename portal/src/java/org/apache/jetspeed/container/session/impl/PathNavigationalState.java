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

import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalControlParameter;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.pluto.om.window.PortletWindow;


/**
 * PathNavigationalStateContext is based on Pluto navigational state.
 * All nav state is stored as path parameters in the URL.
 * This implementation does not currently support persisting navigational state
 * and is simply a facade delegating to the Portal URL for all state information.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PathNavigationalState
    extends
        AbstractNavigationalState
    implements 
        NavigationalState 
{
    private PortalURL url;
        
    static public final boolean SESSION_BASED = false;
    
    public PathNavigationalState(RequestContext context, NavigationalStateComponent nav)
    {
        super(context, nav);
        init(context);
    }
    
    public void init(RequestContext context)
    {
        ArgUtil.assertNotNull(RequestContext.class, context, this, "init()");
        this.url = context.getPortalURL();        
    }
    
    public WindowState getState(PortletWindow window) 
    {
        return url.getState(window);
    }
    
    public void setState(PortletWindow window, WindowState state) 
    {
        url.setState(window, state);
    }
    
    public PortletMode getMode(PortletWindow window) 
    {
        return url.getMode(window);
    }
    
    public void setMode(PortletWindow window, PortletMode mode) 
    {
        url.setMode(window, mode);
    }
    
    public PortletMode getPreviousMode(PortletWindow window) 
    {
        return url.getPreviousMode(window);
    }
    
    public WindowState getPreviousState(PortletWindow window) 
    {
        return url.getPreviousState(window);
    }
        
    public void sync()
    {
        // do nothing
    }
    
    public PortletWindow getMaximizedWindow(Page page)
    {
        ArgUtil.assertNotNull(Page.class, page, this, "getMaximizedWindow()");
        PortalControlParameter pcp = url.getControlParameters();
        Iterator stateful = pcp.getStateFullControlParameter().entrySet().iterator();
        while (stateful.hasNext())
        {
            Map.Entry entry = (Map.Entry)stateful.next();
            String key = (String)entry.getKey();
            String windowId = nav.getWindowIdFromKey(key);
            if (null == windowId)
            {
                continue;
            }
            if (key.startsWith(nav.getNavigationKey(NavigationalStateComponent.STATE)))
            {
                String windowState = (String)entry.getValue();
                WindowState state = nav.lookupWindowState(windowState);
                
                if (state == WindowState.MAXIMIZED)
                {
                    PortletWindowAccessor accessor = (PortletWindowAccessor) Jetspeed.getComponentManager().getComponent(PortletWindowAccessor.class);
                    PortletWindow window = accessor.getPortletWindow(windowId);
                    return window;
                }
                else
                {
                }
            }
        }
        return null;
    }
}
