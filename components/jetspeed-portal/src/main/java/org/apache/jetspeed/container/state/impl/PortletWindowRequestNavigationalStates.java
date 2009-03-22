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

import javax.xml.namespace.QName;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.PortalURL;

public class PortletWindowRequestNavigationalStates
{
    private String characterEncoding;
    private Map<String, PortletWindowRequestNavigationalState> pwnStates = new HashMap<String, PortletWindowRequestNavigationalState>();
    private PortalURL.URLType urlType;
    private PortletWindow maximizedWindow;
    private PortletWindow actionWindow;
    private PortletWindow resourceWindow;
    private Map<QName, String[]> publicRenderParametersMap;
    
    public PortletWindowRequestNavigationalStates(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }
    
    public String getCharacterEncoding()
    {
        return characterEncoding;
    }
    
    public Iterator<String> getWindowIdIterator()
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
    
    public PortalURL.URLType getURLType()
    {
        return urlType;
    }
    
    public void setURLType(PortalURL.URLType urlType)
    {
        this.urlType = urlType;
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

    public Map<QName, String[]> getPublicRenderParametersMap()
    {
        return publicRenderParametersMap;
    }

    public void setPublicRenderParametersMap(Map<QName, String[]> publicRenderParametersMap)
    {
        this.publicRenderParametersMap = publicRenderParametersMap;
    }

    public void setPublicRenderParameters(QName qname, String[] values)
    {
        if (publicRenderParametersMap == null)
        {
            publicRenderParametersMap = new HashMap<QName, String[]>();
        }
        publicRenderParametersMap.put(qname, values);
    }    
    
    public void addPublicRenderParametersMap(Map<QName, String[]> publicRenderParametersMap)
    {
        if (this.publicRenderParametersMap == null)
        {
            this.publicRenderParametersMap = new HashMap<QName, String[]>();
        }
        this.publicRenderParametersMap.putAll(publicRenderParametersMap);
    }
}
