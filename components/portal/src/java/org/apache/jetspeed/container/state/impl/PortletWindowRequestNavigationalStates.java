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
        pwnStates.remove(windowId);
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
}
