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
package org.apache.jetspeed.engine.core;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.PortletActionProvider;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;

/**
 * Handle operations that the portlet may perform in an action method.
 * This service is request based.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletActionProviderImpl implements PortletActionProvider
{
    HttpServletRequest request = null;
    ServletConfig config = null;
    private PortletWindow portletWindow;
    private RequestContext context;
    
    public PortletActionProviderImpl(HttpServletRequest request, ServletConfig config, PortletWindow portletWindow)
    {
        this.request = request;
        this.config = config;
        this.portletWindow = portletWindow;
        // TODO: assemble this
        RequestContextComponent rcc = (RequestContextComponent)Jetspeed.getComponentManager().getComponent(RequestContextComponent.class);
        this.context = rcc.getRequestContext(request);       
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.services.information.PortletActionProvider#changePortletMode(PortletWindow, PortletMode)
     */
    public void changePortletMode(PortletMode mode)
    {        
        NavigationalState state = context.getNavigationalState();
        if (!(state.getMode(portletWindow).equals(mode)) && mode != null)
        {
            state.setMode(portletWindow, mode);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.services.information.PortletActionProvider#changePortletWindowState(PortletWindow, WindowState)
     */
    public void changePortletWindowState(WindowState state)
    {
        NavigationalState navstate = context.getNavigationalState();
        if (!(navstate.getState(portletWindow).equals(state)) && state != null)
        {
            navstate.setState(portletWindow, state);            
        }
    }

}
