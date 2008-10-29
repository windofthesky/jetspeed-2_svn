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
package org.apache.jetspeed.components.portletpreferences;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.pluto.internal.InternalPortletPreference;

/**
 * <p>
 * Jetspeed Portlet Preference Map, return these to Pluto
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedPreferencesMap implements Map<String, InternalPortletPreference>, DistributedCacheObject
{
    private static final long serialVersionUID = 1L;
    
    private Map<String, InternalPortletPreference> map = new HashMap<String, InternalPortletPreference>();
    
    public JetspeedPreferencesMap()
    {
    }
    
    public void clear()
    {
        map.clear(); 
    }

    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    public Set<java.util.Map.Entry<String, InternalPortletPreference>> entrySet()
    {
        return map.entrySet();
    }

    public InternalPortletPreference get(Object key)
    {
        return map.get(key);
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public Set<String> keySet()
    {
        return map.keySet();
    }

    public InternalPortletPreference put(String key,
            InternalPortletPreference value)
    {
        return map.put(key, value);
    }

    public void putAll(
            Map<? extends String, ? extends InternalPortletPreference> other)
    {
        map.putAll(other);
    }

    public InternalPortletPreference remove(Object key)
    {
        return map.remove(key);
    }

    public int size()
    {
        return map.size();
    }

    public Collection<InternalPortletPreference> values()
    {
        return map.values();
    }

    public void notifyChange(int action)
    {
        // TODO: 2.2        
    }

}
