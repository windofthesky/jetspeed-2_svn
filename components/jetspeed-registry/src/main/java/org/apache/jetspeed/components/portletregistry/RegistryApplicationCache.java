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
package org.apache.jetspeed.components.portletregistry;

import java.util.List;
import java.util.Properties;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.impl.EhCacheElementImpl;
import org.apache.jetspeed.om.portlet.PortletApplication;
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
    private static JetspeedCache nameCache;    
    private static PortletRegistry registry;
    private static List listeners = null;

    public RegistryApplicationCache(PersistenceBroker broker, Properties props)
    {
    }
       
    public synchronized static void cacheInit(PortletRegistry r, JetspeedCache o, JetspeedCache n, List l)
    {
        registry = r;
        oidCache = o;
        nameCache = n;
        listeners = l;
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
        oidCache.remove(oid);
        CacheElement entry = new EhCacheElementImpl(oid, obj);
        oidCache.put(entry);
        
        PortletApplication pa = (PortletApplication)obj;
        DistributedCacheObject wrapper = new RegistryCacheObjectWrapper(oid, pa.getName());
        nameCache.remove(pa.getName());
        CacheElement nameEntry = nameCache.createElement(pa.getName(), wrapper);
        nameCache.put(nameEntry);
               
        if (listeners != null)
        {        
            for (int ix=0; ix < listeners.size(); ix++)
            {
                RegistryEventListener listener = (RegistryEventListener)listeners.get(ix);
                listener.applicationUpdated((PortletApplication)obj);
            }
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
        nameCache.clear();
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
        PortletApplication pd = (PortletApplication)cacheLookup(oid);
        if (pd == null)
            return;
        
        oidCache.remove(oid);
        nameCache.remove(pd.getName());
        
        if (listeners != null)
        {
            for (int ix=0; ix < listeners.size(); ix++)
            {
                RegistryEventListener listener = (RegistryEventListener)listeners.get(ix);
                listener.applicationRemoved(pd);
            }        
        }

    }

    public synchronized static void cacheRemoveQuiet(String key, RegistryCacheObjectWrapper w)
    {
        RegistryCacheObjectWrapper wrapper = w;
        if (wrapper == null)
        {
            CacheElement cacheElement = nameCache.get(key);
            if (cacheElement != null)
            {
                wrapper = (RegistryCacheObjectWrapper) cacheElement.getContent();
            }
        }
        
        if (wrapper == null)
        {
            return;
        }
        
        Identity oid = wrapper.getId();      
        
        PortletApplication pd = (PortletApplication)cacheLookup(oid);
        if (pd == null)
            return;
        
        oidCache.removeQuiet(oid);
        nameCache.removeQuiet(pd.getName());        
    }
   
}
