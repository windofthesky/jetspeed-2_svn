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
package org.apache.jetspeed.container.session.impl;

import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.pluto.om.window.PortletWindow;
import org.picocontainer.Startable;

/**
 * NavigationalStateComponent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SessionNavigationalState implements NavigationalState, Startable
{
    HttpSession session;
    
    public SessionNavigationalState(HttpSession session)
    {
        this.session = session;
    }

    public SessionNavigationalState(Map session) // we need mock objects for HTTP stuff! 
    {
    }

    
    public void start()
    {
    }
    
    public void stop()
    {
    }
    
    public WindowState getState(PortletWindow window)
    {
        return null;
    }
    
    public void setState(PortletWindow window, WindowState state)
    {
    }
    
    public PortletMode getMode(PortletWindow window)
    {
        return null;
    }
    
    public void setMode(PortletWindow window, PortletMode mode)
    {
    }
    
}
