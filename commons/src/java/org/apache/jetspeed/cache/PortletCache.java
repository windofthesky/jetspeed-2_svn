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
package org.apache.jetspeed.cache;

import java.util.HashMap;

import javax.portlet.Portlet;

/**
 * Very Simple Portlet Cache to manage portlets in container
 * Keeps only one object instance of a portlet
 * The uniqueness of the portlet is determined by the portlet name
 * There can be multiple java instances of the same portlet class
 * when the portlet name (from the deployment descriptor/registry)
 * differs per portlet description 
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletCache
{
    static private HashMap portlets = new HashMap();
    
    /*
     * Adds a portlet to the portlet cache. If it exists, replaces it
     * 
     * @param portlet The portlet object to add to the cache
     */
    static public void add(String name, Portlet portlet)
    {
        synchronized(portlets)
        {        
            portlets.put(name, portlet);
        }    
    }

    /*
     * Gets a portlet from the portlet cache. 
     * 
     * @param portletName The name of the portlet from the registry
     * @return The found portlet from the cache or null if not found
     */    
    static public Portlet get(String portletName)
    {
        return (Portlet)portlets.get(portletName);
    }

    /*
     * Removes a portlet from the portlet cache. 
     * 
     * @param portletClassName The full Java name of the portlet class
     */    
    static public void remove(String portletName)
    {
        synchronized(portlets)
        {        
            portlets.remove(portletName);
        }    
    }
    
}
