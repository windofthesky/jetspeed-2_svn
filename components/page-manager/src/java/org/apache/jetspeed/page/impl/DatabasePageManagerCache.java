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
package org.apache.jetspeed.page.impl;

import java.util.HashMap;
import java.util.Properties;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.jetspeed.page.document.impl.NodeImpl;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.cache.ObjectCache;

/**
 * DatabasePageManagerCache
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class DatabasePageManagerCache implements ObjectCache
{
    private static LRUMap cacheByOID;
    private static LRUMap cacheByPath;
    private static boolean constraintsEnabled;
    private static boolean permissionsEnabled;

    /**
     * cacheInit
     *
     * Initialize cache using page manager configuration.
     *
     * @param manager configured page manager
     */
    public synchronized static void cacheInit(DatabasePageManager pageManager)
    {
        if (cacheByOID == null)
        {
            cacheByOID = new LRUMap(pageManager.getCacheSize());
            cacheByPath = new LRUMap(pageManager.getCacheSize());
            constraintsEnabled = pageManager.getConstraintsEnabled();
            permissionsEnabled = pageManager.getPermissionsEnabled();
        }
    }

    /**
     * cacheLookup
     *
     * Lookup node instances by unique path.
     *
     * @param path node unique path
     * @return cached node
     */
    public synchronized static NodeImpl cacheLookup(String path)
    {
        Identity oid = (Identity)cacheByPath.get(path);
        NodeImpl node = (NodeImpl)cacheByOID.get(oid);
        return node;
    }

    /**
     * cacheAdd
     *
     * Add object to cache and cache node instances by unique path;
     * infuse nodes loaded by OJB with page manager configuration.
     *
     * @param oid object/node indentity
     * @param obj object/node to cache
     */
    public synchronized static void cacheAdd(Identity oid, Object obj)
    {
        cacheByOID.put(oid, obj);
        if (obj instanceof NodeImpl)
        {
            NodeImpl node = (NodeImpl)obj;
            node.setConstraintsEnabled(constraintsEnabled);
            node.setPermissionsEnabled(permissionsEnabled);
            cacheByPath.put(node.getPath(), oid);
        }
    }

    /**
     * cacheClear
     *
     * Clear object and node caches.
     */
    public synchronized static void cacheClear()
    {
        cacheByOID.clear();
        cacheByPath.clear();
    }

    /**
     * cacheLookup
     *
     * Lookup objects by identity.
     *
     * @param oid object identity
     * @return cached object
     */
    public synchronized static Object cacheLookup(Identity oid)
    {
        return cacheByOID.get(oid);
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
        Object obj = cacheByOID.remove(oid);
        if (obj instanceof NodeImpl)
        {
            NodeImpl node = (NodeImpl)obj;
            cacheByPath.remove(node.getPath());
        }
    }

    /**
     * DatabasePageManagerCache
     *
     * Construct a cache instance using OJB compliant signatures.
     *
     * @param broker broker that is to own cache
     * @param props attribute properties passed to cache
     */
    public DatabasePageManagerCache(PersistenceBroker broker, Properties props)
    {
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.cache.ObjectCache#cache(org.apache.ojb.broker.Identity, java.lang.Object)
     */
    public void cache(Identity oid, Object obj)
    {
        cacheAdd(oid, obj);
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.cache.ObjectCache#clear()
     */
    public void clear()
    {
        cacheClear();
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.cache.ObjectCache#lookup(org.apache.ojb.broker.Identity)
     */
    public Object lookup(Identity oid)
    {
        return cacheLookup(oid);
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.cache.ObjectCache#remove(org.apache.ojb.broker.Identity)
     */
    public void remove(Identity oid)
    {
        cacheRemove(oid);
    }
}
