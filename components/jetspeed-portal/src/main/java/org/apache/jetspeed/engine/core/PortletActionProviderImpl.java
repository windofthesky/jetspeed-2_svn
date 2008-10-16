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
package org.apache.jetspeed.engine.core;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.pluto.services.information.PortletActionProvider;
import org.apache.jetspeed.container.state.MutableNavigationalState;

/**
 * Handle operations that the portlet may perform in an action method.
 * This service is request based.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletActionProviderImpl implements PortletActionProvider
{
    private PortletWindow portletWindow;
    private MutableNavigationalState navstate;
    
    public PortletActionProviderImpl(MutableNavigationalState navstate, PortletWindow portletWindow)
    {
        this.portletWindow = portletWindow;
        this.navstate = navstate;       
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.services.information.PortletActionProvider#changePortletMode(PortletWindow, PortletMode)
     */
    public void changePortletMode(PortletMode mode)
    {        
        if (mode != null)
        {
            navstate.setMode(portletWindow, mode);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.services.information.PortletActionProvider#changePortletWindowState(PortletWindow, WindowState)
     */
    public void changePortletWindowState(WindowState state)
    {
        if (state != null)
        {
            navstate.setState(portletWindow,state);
        }
    }
}
