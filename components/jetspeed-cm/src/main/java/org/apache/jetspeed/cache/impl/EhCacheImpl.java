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
package org.apache.jetspeed.cache.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.apache.jetspeed.request.RequestContext;

public class EhCacheImpl implements JetspeedCache
{
    protected Ehcache ehcache;
    protected Map<JetspeedCacheEventListener, CacheEventListener> cacheEventListenersMap;
    
    public EhCacheImpl(Ehcache ehcache)
    {
        this.ehcache = ehcache;
        this.cacheEventListenersMap = new HashMap<JetspeedCacheEventListener, CacheEventListener>();
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
        ehcache.put(impl.getImplElement());
    }
    
    public CacheElement createElement(Object key, Object content)
    {
    	if (!((key instanceof Serializable) ||  !(content instanceof Serializable)))
    		throw new IllegalArgumentException("The cache key and the object to cache must be serializable."); //return null;
   	    return new EhCacheElementImpl((Serializable)key, (Serializable)content);
    }

    public boolean remove(Object key)
    {
        Element element = ehcache.get(key);
        if (element == null)
            return false;
        boolean isRemoved = ehcache.remove(key);
        return isRemoved;
    }
    
    public boolean removeQuiet(Object key)
    {
        Element element = ehcache.get(key);
        if (element == null)
            return false;
        return ehcache.removeQuiet(key);
    }   

    public void clear()
    {
        ehcache.removeAll();
    }
    
    public void evictContentForUser(String username)
    {
    	return;
    }

    public void evictContentForSession(String session)
    {
        return;
    }
    
    public void addEventListener(final JetspeedCacheEventListener listener, final boolean local)
    {
        CacheEventListener cacheEventListener = new CacheEventListener()
        {
           public void notifyElementEvicted(Ehcache cache, Element element)
           {
               listener.notifyElementEvicted(EhCacheImpl.this, local, element.getKey(), element.getValue());
           }
           
           public void notifyElementExpired(Ehcache cache, Element element)
           {
               listener.notifyElementExpired(EhCacheImpl.this, local, element.getKey(), element.getValue());
           }
           
           public void notifyElementPut(Ehcache cache, Element element)
           {
               listener.notifyElementAdded(EhCacheImpl.this, local, element.getKey(), element.getValue());
           }
           
           public void notifyElementUpdated(Ehcache cache, Element element)
           {
               listener.notifyElementChanged(EhCacheImpl.this, local, element.getKey(), element.getValue());
           }
           
           public void notifyElementRemoved(Ehcache cache, Element element)
           {
               listener.notifyElementRemoved(EhCacheImpl.this, local, element.getKey(), null);
           }
           
           public void notifyRemoveAll(Ehcache cache)
           {
               listener.notifyElementRemoved(EhCacheImpl.this, local, null, null);
           }
           
           public void dispose()
           {
           }
           
           public Object clone()
           {
               return this;
           }
        };
        
        ehcache.getCacheEventNotificationService().registerListener(cacheEventListener);
    }
    
    public void removeEventListener(JetspeedCacheEventListener listener, boolean local)
    {
        CacheEventListener cacheEventListener = this.cacheEventListenersMap.get(listener);
        
        if (cacheEventListener != null)
            ehcache.getCacheEventNotificationService().unregisterListener(cacheEventListener);
    }
    
    public int getSize()
    {
        return ehcache.getSize();
    }
    
    public List getKeys()
    {
        return ehcache.getKeys();
    }
   
    // ------------------------------------------------------
    
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public void dispose()
    {
    }

    public ContentCacheKey createCacheKey(RequestContext rc, String windowId)
    {
        return null; // not implemented here
    }

}
