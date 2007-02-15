/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.components.portletregistry;

import java.util.Properties;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.impl.EhCacheElementImpl;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.cache.ObjectCache;

/**
 * OJB cache 
 * 
 * @author dtaylor
 *
 */
public class RegistryApplicationCache implements ObjectCache
{
    private static JetspeedCache oidCache;
    private static PortletRegistry registry;

    public RegistryApplicationCache(PersistenceBroker broker, Properties props)
    {
    }
   
    public synchronized static void cacheInit(PortletRegistry r, JetspeedCache o)
    {
        registry = r;
        oidCache = o;
    }

    public Object lookup(Identity oid)
    {
        return cacheLookup(oid);
    }    
    public synchronized static Object cacheLookup(Identity oid)
    {
        CacheElement element = oidCache.get(oid);
        if (element != null)
        {
            return element.getContent();
        }
        return null;
    }    
    
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.cache.ObjectCache#cache(org.apache.ojb.broker.Identity, java.lang.Object)
     */
    public void cache(Identity oid, Object obj)
    {
        cacheAdd(oid, obj);
    }
    public synchronized static void cacheAdd(Identity oid, Object obj)
    {
        CacheElement entry = (CacheElement)oidCache.get(oid);
        if (entry != null)
        {
            oidCache.remove(oid);
            entry = new EhCacheElementImpl(oid, obj);
            oidCache.put(entry);
        }
        else
        {
            //MutablePortletApplication proxy = MutablePortletApplicationProxy.createProxy((MutablePortletApplication)obj);
            entry = new EhCacheElementImpl(oid, obj);
            oidCache.put(entry);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.cache.ObjectCache#clear()
     */
    public void clear()
    {
        cacheClear();
    }
    public synchronized static void cacheClear()
    {
        oidCache.clear();
    }


    /* (non-Javadoc)
     * @see org.apache.ojb.broker.cache.ObjectCache#remove(org.apache.ojb.broker.Identity)
     */
    public void remove(Identity oid)
    {
        cacheRemove(oid);
    }
    /**
     * cacheRemove
     *
     * Remove identified object from object and node caches.
     *
     * @param oid object identity
     */
    public synchronized static void cacheRemove(Identity oid)
    {
        oidCache.remove(oid);
    }
    
}
