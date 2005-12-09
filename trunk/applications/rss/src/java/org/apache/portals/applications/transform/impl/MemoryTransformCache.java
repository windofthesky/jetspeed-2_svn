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
package org.apache.portals.applications.transform.impl;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.TreeMap;

import org.apache.portals.applications.transform.Transform;
import org.apache.portals.applications.transform.TransformCache;
import org.apache.portals.applications.transform.TransformCacheEntry;


/**
 * TransformCacheComponent
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class MemoryTransformCache implements TransformCache
{
    private boolean debug = true;    
    private int maxSize = 100;    
    private int evictionPercentage = 10;    
    private boolean enable = true;

    private Transform transform = null;
    private TreeMap cache = null;
    private Object lock = new Object();

    /**
     * Spring constructor injection
     *  
     */
    public MemoryTransformCache(Transform transform, int maxSize, int evictionPercentage, boolean enable, boolean debug)
    {
        this.transform = transform;
        cache = new TreeMap();
        this.maxSize = maxSize;
        this.evictionPercentage = evictionPercentage;
        
        transform.getPublisher().addObserver(this);
    }
    
    public int getMaxSize()
    {
        return maxSize;
    }

    public void setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
    }
    
    public int getEvictionPercentage()
    {
        return this.evictionPercentage;
    }
    
    public boolean isEnabled()
    {
        return enable;        
    }
    
    public void put(String key, Object document, long timeToLive)
    {
        TransformCacheEntry entry = new TransformCacheEntry(key, document, timeToLive);
        if (cache.size() > getMaxSize())
        {
            evict();
        }
        synchronized(lock)
        {
            cache.put(key, entry);
        }
        if (debug)
        {
            System.out.println("Transformed content put in cache! Transform: "
                   + key);
        }        
    }

    /**
     * The eviction policy will keep n items in the cache, and then start evicting
     * x items ordered-by least used first. 
     * n = max size of cache
     * x = (eviction_percentage/100) * n
     *
     */
    protected void evict()        
    {
        if (debug)
        {
            System.out.println("Calling evict... cacheSize: "+
                               cache.size()+" maxSize: "+getMaxSize());
        }
        synchronized (lock)
        {
            if (this.getMaxSize() >= cache.size())
            {
                return;
            }
    
            List list = new LinkedList( cache.values());
            Collections.sort(list, this);
    
            int count = 0;
            int limit = (getMaxSize() * getEvictionPercentage())/100 ;
            if (limit <= 0) limit = 1;
    
            for (Iterator it = list.iterator(); it.hasNext(); )
            {
                if (count >= limit)
                {
                    break;
                }
    
                TransformCacheEntry entry = (TransformCacheEntry) it.next();
                if (debug)
                {
                    System.out.println("Evicting: "+ entry.getKey());
                }
                cache.remove(entry.getKey());
                
                count++;
            }        
        }
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.transform.TransformCacheService#remove(java.lang.String, java.lang.String)
     */
    public Object remove(String key)
    {
        TransformCacheEntry entry = (TransformCacheEntry)cache.get(key);
        if (entry == null)
        {
            return null;
        }
        synchronized(lock)
        {
            entry = (TransformCacheEntry)cache.remove(key);
        }
        return entry;
        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.transform.TransformCacheService#get(java.lang.String, java.lang.String)
     */
    public TransformCacheEntry get(String key)
    {
        TransformCacheEntry entry = (TransformCacheEntry)cache.get(key);
        if (entry == null)
        {
            return null;
        }
        long now = new Date().getTime();
        long lifeTime = entry.getTimeToLive() * 1000;
        if ((entry.getLastAccessed() + lifeTime) < now)
        {
            return null; // expire it
        }
        if (debug)
        {
            System.out.println("Transformed content found in cache! Transform: "
                   + key);
        }
        return entry;
    }
    
    public Object getDocument(String key)
    {
        TransformCacheEntry entry = (TransformCacheEntry)get(key);
        if (entry != null)
        {
            return entry.getDocument();
        }
        return null;
    }
    
    public int compare(Object o1, Object o2)
    {
        TransformCacheEntry e1 = (TransformCacheEntry)o1;
        TransformCacheEntry e2 = (TransformCacheEntry)o2;
        if (e1.getLastAccessed() < e2.getLastAccessed())
        {
            return -1;
        }
        else if (e1.getLastAccessed() == e2.getLastAccessed())
        {
            return 0;
        }
        return 1;
    }
    
    public String constructKey(String url, String stylesheet)
    {
        return url + ":" + stylesheet;        
    }

    public void clearCache()
    {
        cache.clear();
    }
    
    public void update(Observable o, Object arg)
    {
        // TODO: write me
    }
    
}

