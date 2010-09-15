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

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
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
        return (int)ehcache.getCacheConfiguration().getTimeToIdleSeconds();
    }

    public int getTimeToLiveSeconds()
    {
        return (int)ehcache.getCacheConfiguration().getTimeToLiveSeconds();
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
    	if (!(key instanceof Serializable))
    	{
    		throw new IllegalArgumentException("The cache key must be serializable.");
    	}
    	if (content instanceof Serializable)
    	{
            return new EhCacheElementImpl((Serializable)key, (Serializable)content);    	    
    	}
    	else
    	{
            return new EhCacheElementImpl((Serializable)key, content);
    	}
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
               listener.notifyElementEvicted(EhCacheImpl.this, local, element.getKey(), element.getObjectValue());
           }
           
           public void notifyElementExpired(Ehcache cache, Element element)
           {
               listener.notifyElementExpired(EhCacheImpl.this, local, element.getKey(), element.getObjectValue());
           }
           
           public void notifyElementPut(Ehcache cache, Element element)
           {
               listener.notifyElementAdded(EhCacheImpl.this, local, element.getKey(), element.getObjectValue());
           }
           
           public void notifyElementUpdated(Ehcache cache, Element element)
           {
               listener.notifyElementChanged(EhCacheImpl.this, local, element.getKey(), element.getObjectValue());
           }
           
           public void notifyElementRemoved(Ehcache cache, Element element)
           {
               listener.notifyElementRemoved(EhCacheImpl.this, local, element.getKey(), ((element == null) ? null : element.getObjectValue()));
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

        this.cacheEventListenersMap.put(listener, cacheEventListener);
        ehcache.getCacheEventNotificationService().registerListener(cacheEventListener);
    }
    
    public void removeEventListener(JetspeedCacheEventListener listener, boolean local)
    {
        CacheEventListener cacheEventListener = this.cacheEventListenersMap.remove(listener);
        
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
    
    public boolean isDistributed()
    {
        // check if cache part of a distributed cluster
        try
        {
            CacheManagerPeerProvider peerProvider = ehcache.getCacheManager().getCacheManagerPeerProvider("RMI");
            return ((peerProvider != null) && (peerProvider.listRemoteCachePeers(ehcache).size() > 0));
        }
        catch (CacheException ce)
        {
        }
        return false;
    }
   
    public int getMaxSize()
    {
        return ehcache.getCacheConfiguration().getMaxElementsInMemory()+ehcache.getCacheConfiguration().getMaxElementsOnDisk();
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
