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
package org.apache.jetspeed.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ThreadLocalHashMap
 * @param <K>
 * @param <V>
 * @version $Id$
 */
public class ThreadLocalHashMap<K, V> implements Map<K, V>
{
    private static ThreadLocal<Map<?, ?>> tlMap = new ThreadLocal<Map<?, ?>>();
    
    public ThreadLocalHashMap()
    {
        this(true);
    }
    
    public ThreadLocalHashMap(boolean clear)
    {
        clear();
    }
    
    public int size()
    {
        Map<K, V> map = getThreadLocalMap();
        return (map == null ? 0 : map.size());
    }
    
    public boolean isEmpty()
    {
        Map<K, V> map = getThreadLocalMap();
        return (map == null ? true : map.isEmpty());
    }

    public boolean containsKey(Object key)
    {
        Map<K, V> map = getThreadLocalMap();
        return (map == null ? false : map.containsKey(key));
    }
    
    public boolean containsValue(Object value)
    {
        Map<K, V> map = getThreadLocalMap();
        return (map == null ? false : map.containsValue(value));
    }

    public V get(Object key)
    {
        Map<K, V> map = getThreadLocalMap();
        return (map == null ? null : map.get(key));
    }

    public V put(K key, V value)
    {
        Map<K, V> map = getThreadLocalMap();
        
        if (map == null)
        {
            map = new HashMap<K, V>();
            setThreadLocalMap(map);
        }
        
        return map.put(key, value);
    }

    public V remove(Object key)
    {
        Map<K, V> map = getThreadLocalMap();
        
        if (map != null)
        {
            return map.remove(key);
        }
        
        return null;
    }

    public void putAll(Map<? extends K, ? extends V> m)
    {
        Map<K, V> map = getThreadLocalMap();
        
        if (map == null)
        {
            map = new HashMap<K, V>();
            setThreadLocalMap(map);
        }
        
        map.putAll(m);
    }

    public void clear()
    {
        Map<K, V> map = getThreadLocalMap();
        
        if (map != null)
        {
            map.clear();
        }
    }
    
    public Set<K> keySet()
    {
        Map<K, V> map = getThreadLocalMap();
        
        if (map != null)
        {
            return map.keySet();
        }
        
        return Collections.emptySet();
    }

    public Collection<V> values()
    {
        Map<K, V> map = getThreadLocalMap();
        
        if (map != null)
        {
            return map.values();
        }
        
        return Collections.emptyList();
    }

    public Set<Map.Entry<K, V>> entrySet()
    {
        Map<K, V> map = getThreadLocalMap();
        
        if (map != null)
        {
            return map.entrySet();
        }
        
        return Collections.emptySet();
    }
    
    @SuppressWarnings("unchecked")
    private Map<K, V> getThreadLocalMap()
    {
        return (Map<K, V>) tlMap.get();
    }
    
    private void setThreadLocalMap(Map<K, V> map)
    {
        tlMap.set(map);
    }
}
