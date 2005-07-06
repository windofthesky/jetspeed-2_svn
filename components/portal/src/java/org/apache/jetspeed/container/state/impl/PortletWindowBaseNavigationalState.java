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

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

/**
 * PortletWindowBaseNavigationalState
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class PortletWindowBaseNavigationalState implements Serializable
{
    private PortletMode portletMode;
    private WindowState windowState;
    
    public PortletMode getPortletMode()
    {
        return portletMode;
    }
    
    public void setPortletMode(PortletMode portletMode)
    {
        this.portletMode = portletMode;
    }
    
    public WindowState getWindowState()
    {
        return windowState;
    }
    
    public void setWindowState(WindowState windowState)
    {
        this.windowState = windowState;
    }
}
