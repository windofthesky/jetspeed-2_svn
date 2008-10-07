/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.jetspeed.cache.PortletWindowCache;
import org.apache.pluto.om.window.PortletWindow;

/**
 * This implementation of {@link PortletWindowCache} is to be used ONLY for testing purposes.
 * 
 * @author <a href="mailto:scott.t.weaver@gmail.com">Scott T. Weaver</a>
 *
 */
public class HashMapWindowCache implements PortletWindowCache
{


    private Map portletEntityToWindow = new HashMap();
    private Map windows = new HashMap();
    
    public Set getAllPortletWindows()
    {
        Set windowSet = new HashSet();
        
        Iterator itr = windows.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry entry = (Entry) itr.next();                                
            windowSet.add((PortletWindow)entry.getValue());
        }
        
        return windowSet;
    }

    public PortletWindow getPortletWindow(String windowId)
    {
        return (PortletWindow) windows.get(windowId);
    }

    public PortletWindow getPortletWindowByEntityId(String portletEntityId)
    {
        if(portletEntityToWindow.containsKey(portletEntityId))
        {
            return (PortletWindow) windows.get((String) portletEntityToWindow.get(portletEntityId));
        }
        else
        {
            return null;
        }
    }

    public void putPortletWindow(PortletWindow window)
    {
        windows.put(window.getId().toString(), window);
        portletEntityToWindow.put(window.getPortletEntity().getId().toString(), window.getId().toString());

    }

    public void removePortletWindow(String windowId)
    {
        PortletWindow window = (PortletWindow) windows.get(windowId);
        if(window != null)
        {
            windows.remove(windowId);
            portletEntityToWindow.remove(window.getPortletEntity().getId().toString());
        }

    }

    public void removePortletWindowByPortletEntityId(String portletEntityId)
    {
        PortletWindow window = getPortletWindowByEntityId(portletEntityId);
        if(window != null)
        {   
            removePortletWindow(window.getId().toString());
        }

    }
}