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

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.session.NavigationalStateContext;
import org.apache.jetspeed.engine.core.PortalControlParameter;
import org.apache.jetspeed.engine.core.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * PathNavigationalStateContext is based on Pluto navigational state.
 * All nav state is stored as path parameters in the URL.
 * This implementation does not currently support persisting navigational state
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PathNavigationalStateContext implements NavigationalStateContext 
{
    RequestContext context;
    PortalURL url;
    PortalControlParameter pcp;
    
    public PathNavigationalStateContext(RequestContext context)
    {
        init(context);
    }
    
    public void init(RequestContext context)
    {
        this.context = context;
        this.url = context.getRequestedPortalURL();
        this.pcp = new PortalControlParameter(url);        
    }
    
    public WindowState getState(PortletWindow window) 
    {
        return pcp.getState(window);
    }
    
    public void setState(PortletWindow window, WindowState state) 
    {
        pcp.setState(window, state);
    }
    
    public PortletMode getMode(PortletWindow window) 
    {
        return pcp.getMode(window);
    }
    
    public void setMode(PortletWindow window, PortletMode mode) 
    {
        pcp.setMode(window, mode);
    }
    
    public PortletMode getPreviousMode(PortletWindow window) 
    {
        return pcp.getPrevMode(window);
    }
    
    public WindowState getPreviousState(PortletWindow window) 
    {
        return pcp.getPrevState(window);
    }
    
}
