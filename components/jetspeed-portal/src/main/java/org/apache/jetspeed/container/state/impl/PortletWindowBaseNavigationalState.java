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
    private static final long serialVersionUID = 4188214497281562619L;
    private String modeName;
    private String stateName;
    private boolean actionScopedRequestAttributes;
    
    private transient PortletMode portletMode;
    private transient WindowState windowState;
    
    public PortletMode getPortletMode()
    {
        if ( portletMode == null && modeName != null )
        {
            portletMode = new PortletMode(modeName);
        }
        return portletMode ;
    }
    
    public void setPortletMode(PortletMode portletMode)
    {
        this.portletMode = portletMode;
        this.modeName = portletMode == null ? null : portletMode.toString();
    }
    
    public WindowState getWindowState()
    {
        if ( windowState == null && stateName != null )
        {
            windowState = new WindowState(stateName);
        }
        return windowState;
    }
    
    public void setWindowState(WindowState windowState)
    {
        this.windowState = windowState;
        this.stateName = windowState == null ? null : windowState.toString();
    }

    public void setActionScopedRequestAttributes(boolean actionScopedRequestAttributes)
    {
        this.actionScopedRequestAttributes = actionScopedRequestAttributes;
    }
    
    public boolean isActionScopedRequestAttributes()
    {
        return actionScopedRequestAttributes;
    }
}
