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

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.pluto.om.window.PortletWindow;
import org.picocontainer.Startable;

/**
 * PathNavigationalState
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PathNavigationalState implements NavigationalState, Startable
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.NavigationalState#getState(org.apache.pluto.om.window.PortletWindow)
     */
    public WindowState getState(PortletWindow window)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.NavigationalState#setState(org.apache.pluto.om.window.PortletWindow, javax.portlet.WindowState)
     */
    public void setState(PortletWindow window, WindowState state)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.NavigationalState#getMode(org.apache.pluto.om.window.PortletWindow)
     */
    public PortletMode getMode(PortletWindow window)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.NavigationalState#setMode(org.apache.pluto.om.window.PortletWindow, javax.portlet.PortletMode)
     */
    public void setMode(PortletWindow window, PortletMode mode)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.picocontainer.Startable#start()
     */
    public void start()
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.picocontainer.Startable#stop()
     */
    public void stop()
    {
        // TODO Auto-generated method stub
    }
}
