/* Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.jetspeed.cache.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;

/**
 * Wrapper around actual cache implementation
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class EhCacheImpl implements JetspeedCache
{
    private Cache ehcache;
    
    public EhCacheImpl(Cache ehcache)
    {
        this.ehcache = ehcache;
    }

    public CacheElement get(Object key)
    {
        Element element = ehcache.get(key);
        if (element == null)
            return null;
        return new EhCacheElementImpl(element);
    }

    public int getTimeToIdleSeconds()
    {
        return (int)ehcache.getTimeToIdleSeconds();
    }

    public int getTimeToLiveSeconds()
    {
        return (int)ehcache.getTimeToLiveSeconds();
    }

    public boolean isKeyInCache(Object key)
    {
        return ehcache.isKeyInCache(key);
    }

    public void put(CacheElement element)
    {
        EhCacheElementImpl impl = (EhCacheElementImpl)element;
        Element ehl = impl.getImplElement();
        String userKey = impl.getUserKey();
        String entity = impl.getEntityKey();
        ehcache.put(ehl);
        Element userElement = ehcache.get(userKey);
        if (userElement == null)
        {
            Map map = Collections.synchronizedMap(new HashMap());
            map.put(entity, entity);
            userElement = new Element(userKey, map);
            ehcache.put(userElement);           
        }
        else
        {
            Map map = (Map)userElement.getObjectValue();
            map.put(entity, entity);
        }        
    }
    
    public CacheElement createElement(Object key, Object content)
    {
        Element cachedElement = new Element(key, content);        
        return new EhCacheElementImpl(cachedElement);
    }

    public boolean remove(Object key)
    {
        CacheElement element = this.get(key);
        boolean removed = false;
        if (element != null)
        {
            removed = ehcache.remove(key);
        }
        EhCacheElementImpl impl = (EhCacheElementImpl)element;
        Element ehl = impl.getImplElement();
        String userKey = impl.getUserKey();
        String entity = impl.getEntityKey();
        Element userElement = ehcache.get(userKey);
        if (userElement != null)
        {
            Map map = (Map)userElement.getObjectValue();
            if (map != null)
            {
                map.remove(entity);
            }
        }
        return removed;
    }
    
    public void evictContentForUser(String user)
    {
        Element userElement = ehcache.get(user);
        if (userElement != null)
        {
            Map map = (Map)userElement.getObjectValue();
            if (map != null)
            {
                Iterator entities = map.keySet().iterator();
                while (entities.hasNext())
                {
                    String entity = (String)entities.next();
                    String key = createCacheKey(user, entity);
                    ehcache.remove(key);
                }
            }
            ehcache.remove(user);
        }
    }
    
    public String createCacheKey(String primary, String secondary)
    {
        return primary + EhCacheElementImpl.KEY_SEPARATOR + secondary;
    }

}