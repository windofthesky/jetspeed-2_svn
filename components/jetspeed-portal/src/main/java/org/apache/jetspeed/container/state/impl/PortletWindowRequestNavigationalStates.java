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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.pluto.om.window.PortletWindow;

public class PortletWindowRequestNavigationalStates
{
    private String characterEncoding;
    private Map pwnStates = new HashMap();
    private PortletWindow maximizedWindow;
    private PortletWindow actionWindow;
    private PortletWindow resourceWindow;
    
    public PortletWindowRequestNavigationalStates(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }
    
    public String getCharacterEncoding()
    {
        return characterEncoding;
    }
    
    public Iterator getWindowIdIterator()
    {
        return pwnStates.keySet().iterator();
    }
    
    public void removePortletWindowNavigationalState(String windowId)
    {        
        boolean removed = pwnStates.remove(windowId) != null;
        if (removed)
        {
            if (maximizedWindow != null && windowId.equals(maximizedWindow.getId().toString()))
            {
                maximizedWindow = null;
            }
            if (actionWindow != null && windowId.equals(actionWindow.getId().toString()))
            {
                actionWindow = null;
            }
            if (resourceWindow != null && windowId.equals(actionWindow.getId().toString()))
            {
                resourceWindow = null;
            }
        }
    }
    
    public PortletWindowRequestNavigationalState getPortletWindowNavigationalState(String windowId)
    {
        return (PortletWindowRequestNavigationalState)pwnStates.get(windowId);
    }
    
    public void addPortletWindowNavigationalState(String windowId, PortletWindowRequestNavigationalState pwnState)
    {
        pwnStates.put(windowId, pwnState);
    }
    
    public PortletWindow getMaximizedWindow()
    {
        return maximizedWindow;
    }
    
    public void setMaximizedWindow(PortletWindow maximizedWindow)
    {
        this.maximizedWindow = maximizedWindow;
    }

    public PortletWindow getActionWindow()
    {
        return actionWindow;
    }
    public void setActionWindow(PortletWindow actionWindow)
    {
        this.actionWindow = actionWindow;
    }
    public void setResourceWindow(PortletWindow resourceWindow)
    {
        this.resourceWindow = resourceWindow;
    }
    public PortletWindow getResourceWindow()
    {
        return resourceWindow;
    }
}