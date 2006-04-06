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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.page.PageManager;
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
    private static HashMap cacheByOID;
    private static LinkedList cacheLRUList;
    private static HashMap cacheByPath;
    private static int cacheSize;
    private static int cacheExpiresSeconds;
    private static boolean constraintsEnabled;
    private static boolean permissionsEnabled;
    private static PageManager pageManager;

    /**
     * cacheInit
     *
     * Initialize cache using page manager configuration.
     *
     * @param pageManager configured page manager
     */
    public synchronized static void cacheInit(DatabasePageManager dbPageManager)
    {
        if (pageManager == null)
        {
            cacheByOID = new HashMap();
            cacheLRUList = new LinkedList();
            cacheByPath = new HashMap();
            cacheSize = dbPageManager.getCacheSize();
            cacheExpiresSeconds = dbPageManager.getCacheExpiresSeconds();
            constraintsEnabled = dbPageManager.getConstraintsEnabled();
            permissionsEnabled = dbPageManager.getPermissionsEnabled();
            pageManager = dbPageManager;
        }
    }

    /**
     * setPageManagerProxy
     *
     * @param proxy proxied page manager interface used to
     *              inject into Folder instances to provide
     *              transaction/interception
     */
    public synchronized static void setPageManagerProxy(PageManager proxy)
    {
        // set/reset page manager proxy and clear cache to
        // flush any objects referencing replaced page manager
        if (pageManager != proxy)
        {
            pageManager = proxy;
            cacheClear();
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
        if (path != null)
        {
            // return valid object cached by path
            return (NodeImpl)cacheValidateEntry((Entry)cacheByPath.get(path));
        }
        return null;
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
        Entry entry = (Entry)cacheByOID.get(oid);
        if (entry != null)
        {
            // update cache LRU order
            cacheLRUList.remove(entry);
            cacheLRUList.addFirst(entry);
            // refresh cache entry
            entry.touch();
        }
        else
        {
            // create new cache entry and map
            entry = new Entry(obj, oid);
            cacheByOID.put(oid, entry);
            cacheLRUList.addFirst(entry);
            // infuse node with page manager configuration
            // or the page manager itself and add to the
            // paths cache 
            if (obj instanceof NodeImpl)
            {
                NodeImpl node = (NodeImpl)obj;
                node.setConstraintsEnabled(constraintsEnabled);
                node.setPermissionsEnabled(permissionsEnabled);
                cacheByPath.put(node.getPath(), entry);
                if (obj instanceof FolderImpl)
                {
                    ((FolderImpl)obj).setPageManager(pageManager);
                }
            }
            // trim cache as required to maintain cache size
            while (cacheLRUList.size() > cacheSize)
            {
                cacheRemoveEntry((Entry)cacheLRUList.getLast(), true);
            }
        }
    }

    /**
     * cacheClear
     *
     * Clear object and node caches.
     */
    public synchronized static void cacheClear()
    {
        // remove all cache entries
        Iterator removeIter = cacheLRUList.iterator();
        while (removeIter.hasNext())
        {
            cacheRemoveEntry((Entry)removeIter.next(), false);
        }
        // clear cache
        cacheByOID.clear();
        cacheLRUList.clear();
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
        if (oid != null)
        {
            // return valid object cached by oid
            return cacheValidateEntry((Entry)cacheByOID.get(oid));
        }
        return null;
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
        // remove from cache by oid
        cacheRemoveEntry((Entry)cacheByOID.get(oid), true);
    }

    /**
     * cacheRemove
     *
     * Remove identified object from object and node caches.
     *
     * @param path object path
     */
    public synchronized static void cacheRemove(String path)
    {
        // remove from cache by path
        cacheRemoveEntry((Entry)cacheByPath.get(path), true);
    }
    
    /**
     * cacheValidateEntry
     *
     * Validate specified entry from cache, returning cached
     * object if valid.
     *
     * @param entry cache entry to validate
     * @return validated object from cache
     */
    private synchronized static Object cacheValidateEntry(Entry entry)
    {
        if (entry != null)
        {
            if (!entry.isExpired())
            {
                // update cache LRU order
                cacheLRUList.remove(entry);
                cacheLRUList.addFirst(entry);
                // refresh cache entry and return object
                entry.touch();
                return (NodeImpl)entry.getObject();
            }
            else
            {
                // remove expired entry
                cacheRemoveEntry(entry, true);
            }
        }
        return null;
    }
    
    /**
     * cacheRemoveEntry
     *
     * Remove specified entry from cache.
     *
     * @param entry cache entry to remove
     * @param remove enable removal from cache
     */
    private synchronized static void cacheRemoveEntry(Entry entry, boolean remove)
    {
        if (entry != null)
        {
            Object removeObj = entry.getObject();
            if (remove)
            {
                // remove entry, optimize for removal from end
                // of list as cache size is met or entries expire
                if (cacheLRUList.getLast() == entry)
                {
                    cacheLRUList.removeLast();
                }
                else
                {
                    int removeIndex = cacheLRUList.lastIndexOf(entry);
                    if (removeIndex > 0)
                    {
                        cacheLRUList.remove(removeIndex);
                    }
                }
                // unmap entry
                cacheByOID.remove(entry.getOID());
                if (removeObj instanceof NodeImpl)
                {
                    cacheByPath.remove(((NodeImpl)removeObj).getPath());
                }
            }
            // reset internal FolderImpl caches
            if (removeObj instanceof FolderImpl)
            {
                ((FolderImpl)removeObj).resetAll(true);
            }
        }
    }

    /**
     * resetCachedSecurityConstraints
     *
     * Reset cached security constraints in all cached node objects.
     */
    public synchronized static void resetCachedSecurityConstraints()
    {
        // reset cached objects
        Iterator resetIter = cacheLRUList.iterator();
        while (resetIter.hasNext())
        {
            Object obj = ((Entry)resetIter.next()).getObject();
            if (obj instanceof NodeImpl)
            {
                ((NodeImpl)obj).resetCachedSecurityConstraints();
            }
        }
    }

    /**
     * Entry
     *
     * Cache entry class adding entry timestamp to track expiration
     */
    private static class Entry
    {
        public long timestamp;
        public Object object;
        public Identity oid;

        public Entry(Object object, Identity oid)
        {
            touch();
            this.object = object;
            this.oid = oid;
        }

        public boolean isExpired()
        {
            if (DatabasePageManagerCache.cacheExpiresSeconds > 0)
            {
                long now = System.currentTimeMillis();
                if (((now - timestamp) / 1000) < DatabasePageManagerCache.cacheExpiresSeconds)
                {
                    timestamp = now;
                    return false;
                }
                return true;
            }
            return false;
        }
        
        public void touch()
        {
            if (DatabasePageManagerCache.cacheExpiresSeconds > 0)
            {
                timestamp = System.currentTimeMillis();
            }
        }

        public Object getObject()
        {
            return object;
        }

        public Identity getOID()
        {
            return oid;
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

    public synchronized static void dump()
    {
        System.out.println("--------------------------1");        
        Iterator dumpIter = cacheLRUList.iterator();
        while (dumpIter.hasNext())
        {
            Entry entry = (Entry)dumpIter.next();
            Object entryObject = entry.getObject();
            if (entryObject instanceof NodeImpl)
            {
                System.out.println("entry = " + ((NodeImpl)entryObject).getPath() + ", " + entry.getOID());
            }
            else
            {
                System.out.println("entry = <none>, " + entry.getOID());
            }
        }
        System.out.println("--------------------------2");
    }
    
    protected static ThreadLocal transactionedOperations = new ThreadLocal();
    
    public static List getTransactions()
    {
        List operations = (List)transactionedOperations.get();
        if (operations == null)
        {
            operations = new LinkedList();
            transactionedOperations.set(operations);
        }
        
        return operations;
    }

    /**
     * @param principal
     *            The principal to set.
     */
    public static void addTransaction(TransactionedOperation operation)
    {
        List transactions = getTransactions();        
        transactions.add(operation);
    }
    
    public static void rollbackTransactions()
    {
        Iterator transactions = getTransactions().iterator();
        while (transactions.hasNext())
        {
            TransactionedOperation operation = (TransactionedOperation)transactions.next();
            cacheRemove(operation.getPath());
        }
    }
}
