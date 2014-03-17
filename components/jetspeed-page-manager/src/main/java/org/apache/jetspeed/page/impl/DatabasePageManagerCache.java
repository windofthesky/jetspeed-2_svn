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
package org.apache.jetspeed.page.impl;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.impl.BaseFragmentElementImpl;
import org.apache.jetspeed.om.page.impl.FragmentPropertyImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.impl.NodeImpl;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.cache.ObjectCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * DatabasePageManagerCache
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class DatabasePageManagerCache implements ObjectCache
{
    private static Logger log = LoggerFactory.getLogger(DatabasePageManagerCache.class);
    
    private static final String EOL = System.getProperty("line.separator");
    
    // Members
    
    private static JetspeedCache oidCache;
    private static JetspeedCache pathCache;
    private static Map<String,Identity> pathToOidMap;
    private static JetspeedCache propertiesCache;
    private static JetspeedCache propertiesPathCache;
    private static JetspeedCache principalPropertiesCache;
    private static JetspeedCache principalPropertiesPathCache;
    private static Map<String,Set<String>> propertiesCacheIndexMap;
    private static boolean constraintsEnabled;
    private static boolean permissionsEnabled;
    private static PageManager pageManager;
    private static List<String> updatePathsList = new ArrayList<String>();
    private static ThreadLocal<List<TransactionedOperation>> transactionedOperations = new ThreadLocal<List<TransactionedOperation>>();

    // Implementation
    
    /**
     * cacheInit
     *
     * Initialize cache using page manager configuration.
     *
     * @param pageManager configured page manager
     */
    public synchronized static void cacheInit(JetspeedCache oidCache, JetspeedCache pathCache, JetspeedCache propertiesCache, JetspeedCache propertiesPathCache,
                                              JetspeedCache principalPropertiesCache, JetspeedCache principalPropertiesPathCache, DatabasePageManager pageManager)
    {
        // initialize
        DatabasePageManagerCache.oidCache = oidCache;
        DatabasePageManagerCache.pathCache = pathCache;
        DatabasePageManagerCache.pathToOidMap = new HashMap<String,Identity>();
        DatabasePageManagerCache.propertiesCache = propertiesCache;
        DatabasePageManagerCache.propertiesPathCache = propertiesPathCache;
        DatabasePageManagerCache.principalPropertiesCache = principalPropertiesCache;
        DatabasePageManagerCache.principalPropertiesPathCache = principalPropertiesPathCache;
        propertiesCacheIndexMap = new HashMap<String,Set<String>>();
        constraintsEnabled = pageManager.getConstraintsEnabled();
        permissionsEnabled = pageManager.getPermissionsEnabled();
        DatabasePageManagerCache.pageManager = pageManager;
        
        // setup local oid cache listener
        final DatabasePageManager databasePageManager = pageManager;
        oidCache.addEventListener(new JetspeedCacheEventListener()
        {
            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementAdded(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementAdded(JetspeedCache cache, boolean local, Object key, Object element)
            {
                NodeImpl node = (NodeImpl)element;
                pathToOidMap.put(node.getPath(), (Identity)key);
                // infuse node with page manager configuration
                // or the page manager itself and add to the
                // paths cache
                node.setConstraintsEnabled(constraintsEnabled);
                node.setPermissionsEnabled(permissionsEnabled);
                node.setPageManager(databasePageManager);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementChanged(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementChanged(JetspeedCache cache, boolean local, Object key, Object element)
            {
                notifyElementAdded(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementEvicted(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementEvicted(JetspeedCache cache, boolean local, Object key, Object element)
            {
                notifyElementRemoved(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementExpired(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementExpired(JetspeedCache cache, boolean local, Object key, Object element)
            {
                notifyElementRemoved(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementRemoved(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
            {
                NodeImpl node = (NodeImpl)element;
                pathToOidMap.remove(node.getPath());
                // set stale flag since this object will now be orphaned
                // and should be be refetched from the page manager
                node.setStale(true);
                // reset internal FolderImpl caches
                if (node instanceof FolderImpl)
                {
                    ((FolderImpl)node).resetAll(false);
                }
            }
        }, true);
        
        // setup remote path cache listener
        pathCache.addEventListener(new JetspeedCacheEventListener()
        {
            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementAdded(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementAdded(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementChanged(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementChanged(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementEvicted(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementEvicted(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementExpired(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementExpired(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementRemoved(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
            {
                DatabasePageManagerCacheObject cacheObject = (DatabasePageManagerCacheObject)element;
                // remove cache object from local caches
                Identity oid = ((cacheObject != null) ? cacheObject.getId() : null);
                String path = ((cacheObject != null) ? cacheObject.getPath() : (String)key);
                if ((oid != null) || (path != null))
                {
                    synchronized (DatabasePageManagerCache.class)
                    {
                        if (oid != null)
                        {
                            // get object cached by oid
                            NodeImpl node = cacheLookup(oid, false);
                            // reset internal FolderImpl caches
                            if (node instanceof FolderImpl)
                            {
                                ((FolderImpl)node).resetAll(false);
                            }
                            // notify page manager of update
                            DatabasePageManagerCache.pageManager.notifyUpdatedNode(node);
                            // remove from cache
                            DatabasePageManagerCache.oidCache.remove(oid);
                        }
                        if (path != null)
                        {
                            // lookup parent object cached by path and oid
                            int pathLastSeparatorIndex = path.lastIndexOf(Folder.PATH_SEPARATOR);
                            String parentPath = ((pathLastSeparatorIndex > 0) ? path.substring(0, pathLastSeparatorIndex) : Folder.PATH_SEPARATOR);
                            NodeImpl parentNode = cacheLookup(parentPath, false);
                            // reset internal FolderImpl caches in case element removed
                            if (parentNode instanceof FolderImpl)
                            {
                                ((FolderImpl)parentNode).resetAll(false);
                            }
                            // remove all indexed fragment keys for page path from
                            // properties cache index
                            Set<String> index = propertiesCacheIndexMap.get(path);
                            if (index != null)
                            {
                                // remove all indexed fragment keys, (copy first since "quiet" removes
                                // from fragment property caches will side effect this set while iterating)
                                for (String fragmentKey : (new ArrayList<String>(index)))
                                {
                                    // parse key to extract fragment id
                                    int fragmentId = getFragmentIdFromFragmentKey(fragmentKey);
                                    // remove principal fragment property list caches that have fragment properties
                                    if (fragmentId > 0)
                                    {
                                        clearPrincipalPropertiesCache(fragmentId);
                                    }
                                    // ensure removed from fragment property cache
                                    DatabasePageManagerCache.propertiesCache.removeQuiet(fragmentKey);
                                    DatabasePageManagerCache.propertiesPathCache.removeQuiet(fragmentKey);
                                }
                                propertiesCacheIndexMap.remove(path);
                            }
                        }                        
                    }
                }
            }
        }, false);
        
        // setup local properties cache listener
        propertiesCache.addEventListener(new JetspeedCacheEventListener()
        {
            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementAdded(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementAdded(JetspeedCache cache, boolean local, Object key, Object element)
            {
                String fragmentKey = (String)key;
                DatabasePageManagerCachedFragmentPropertyList fragmentPropertyList = (DatabasePageManagerCachedFragmentPropertyList)element;
                if (fragmentPropertyList != null)
                {
                    // add cache key to properties cache index
                    String path = fragmentPropertyList.getBaseFragmentsElementPath();
                    synchronized (DatabasePageManagerCache.class)
                    {
                        Set<String> index = propertiesCacheIndexMap.get(path);
                        if (index == null)
                        {
                            index = new HashSet<String>();
                            propertiesCacheIndexMap.put(path, index);
                        }
                        index.add(fragmentKey);
                    }
                }
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementChanged(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementChanged(JetspeedCache cache, boolean local, Object key, Object element)
            {
                notifyElementAdded(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementEvicted(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementEvicted(JetspeedCache cache, boolean local, Object key, Object element)
            {
                notifyElementRemoved(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementExpired(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementExpired(JetspeedCache cache, boolean local, Object key, Object element)
            {
                notifyElementRemoved(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementRemoved(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
            {
                String fragmentKeyOrPath = (String)key;
                DatabasePageManagerCachedFragmentPropertyList fragmentPropertyList = (DatabasePageManagerCachedFragmentPropertyList)element;
                if (fragmentPropertyList != null)
                {
                    // remove single cache key from properties cache index
                    String path = fragmentPropertyList.getBaseFragmentsElementPath();
                    synchronized (DatabasePageManagerCache.class)
                    {
                        Set<String> index = propertiesCacheIndexMap.get(path);
                        if (index != null)
                        {
                            index.remove(fragmentKeyOrPath);
                            if (index.isEmpty())
                            {
                                propertiesCacheIndexMap.remove(path);
                            }
                        }
                    }
                }
                else if (fragmentKeyOrPath != null)
                {
                    // remove all indexed cache keys from properties cache index
                    synchronized (DatabasePageManagerCache.class)
                    {
                        Set<String> index = propertiesCacheIndexMap.get(fragmentKeyOrPath);
                        if (index != null)
                        {
                            // remove all indexed cache keys
                            for (String fragmentKey : index)
                            {
                                DatabasePageManagerCache.propertiesCache.removeQuiet(fragmentKey);
                            }
                            propertiesCacheIndexMap.remove(fragmentKeyOrPath);
                        }
                    }                    
                }
                else if (fragmentKeyOrPath == null)
                {
                    // remove all cache keys from properties cache index
                    synchronized (DatabasePageManagerCache.class)
                    {
                        propertiesCacheIndexMap.clear();
                    }
                }
            }
        }, true);
        
        // setup remote properties path cache listener
        propertiesPathCache.addEventListener(new JetspeedCacheEventListener()
        {
            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementAdded(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementAdded(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementChanged(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementChanged(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementEvicted(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementEvicted(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementExpired(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementExpired(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementRemoved(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
            {
                DatabasePageManagerFragmentKeyCacheObject cacheObject = (DatabasePageManagerFragmentKeyCacheObject)element;
                // remove cache object from local properties cache
                String fragmentKey = ((cacheObject != null) ? cacheObject.getFragmentKey() : (String)key);
                if (fragmentKey != null)
                {
                    // parse key to extract fragment id
                    int fragmentId = getFragmentIdFromFragmentKey(fragmentKey);
                    // remove cached objects
                    synchronized (DatabasePageManagerCache.class)
                    {
                        // remove principal fragment property list caches that have fragment properties
                        if (fragmentId > 0)
                        {
                            clearPrincipalPropertiesCache(fragmentId);
                        }
                        // remove from fragment property list cache
                        DatabasePageManagerCache.propertiesCache.remove(fragmentKey);
                    }
                }
            }
        }, false);
        
        // setup remote principal properties path cache listener
        principalPropertiesPathCache.addEventListener(new JetspeedCacheEventListener()
        {
            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementAdded(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementAdded(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementChanged(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementChanged(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementEvicted(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementEvicted(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementExpired(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementExpired(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementRemoved(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
            {
                DatabasePageManagerPrincipalKeyCacheObject cacheObject = (DatabasePageManagerPrincipalKeyCacheObject)element;
                // remove cache object from local principal properties cache
                String principalKey = ((cacheObject != null) ? cacheObject.getPrincipalKey() : (String)key);
                if (principalKey != null)
                {
                    synchronized (DatabasePageManagerCache.class)
                    {
                        DatabasePageManagerCache.principalPropertiesCache.removeQuiet(principalKey);
                    }
                }
            }
        }, false);
    }
    
    /**
     * Parse fragment id from encoded fragment key.
     * 
     * @param fragmentKey fragment key
     * @return fragment id
     */
    private static int getFragmentIdFromFragmentKey(String fragmentKey)
    {
        // parse key to extract fragment id
        int fragmentId = -1;
        int fragmentIdSeparatorIndex = fragmentKey.lastIndexOf(':');
        if (fragmentIdSeparatorIndex != -1)
        {
            try
            {
                fragmentId = Integer.parseInt(fragmentKey.substring(fragmentIdSeparatorIndex+1));
            }
            catch (NumberFormatException nfe)
            {
            }
        }
        return fragmentId;
    }
    
    /**
     * Clear principal properties cache entries that contain properties
     * for the specified fragment id.
     * 
     * @param fragmentId fragment id
     */
    private static void clearPrincipalPropertiesCache(int fragmentId)
    {
        // scan principal fragment property cache
        @SuppressWarnings("unchecked")
        List<String> principalKeys = principalPropertiesCache.getKeys();
        for (String principalKey : principalKeys)
        {
            CacheElement propertiesElement = principalPropertiesCache.get(principalKey);
            if (propertiesElement != null)
            {
                // scan cached principal fragment property list
                DatabasePageManagerCachedFragmentPropertyList cachedPrincipalFragmentPropertyList = (DatabasePageManagerCachedFragmentPropertyList)propertiesElement.getContent();
                for (FragmentProperty fragmentProperty : cachedPrincipalFragmentPropertyList)
                {
                    if (((BaseFragmentElementImpl)((FragmentPropertyImpl)fragmentProperty).getFragment()).getIdentity() == fragmentId)
                    {
                        // remove cached principal fragment property list
                        DatabasePageManagerCache.principalPropertiesCache.removeQuiet(principalKey);
                        DatabasePageManagerCache.principalPropertiesPathCache.removeQuiet(principalKey);                            
                        break;
                    }
                }
            }
        }
    }

    /**
     * Override page manager specified during create with proxy.
     *
     * @param proxy proxied page manager interface used to
     *               inject into Folder instances to provide
     *               transaction/intercept
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
     * Lookup node instances by unique path and consider 
     * cache access application read hit.
     *
     * @param path node unique path
     * @return cached node
     */
    public synchronized static NodeImpl cacheLookup(String path)
    {
        return cacheLookup(path, true);
    }

    /**
     * Lookup node instances by unique path.
     *
     * @param path node unique path
     * @param cacheRead application cache read hit
     * @return cached node
     */
    private synchronized static NodeImpl cacheLookup(String path, boolean cacheRead)
    {
        if (path != null)
        {
            // return valid object cached by path and oid
            Identity oid = pathToOidMap.get(path);
            if (oid != null)
            {
                return cacheLookup(oid, cacheRead);
            }
        }
        return null;
    }

    /**
     * Add path to list of updating paths; this list is used to
     * distinguish cache add operations that are probably the
     * result of writes vs. those that are associated with reads.
     *
     * @param path object path
     */
    public synchronized static void addUpdatePath(String path)
    {
        // add path for later examination, (duplicates allowed)
        updatePathsList.add(path);
    }

    /**
     * Add node to cache and cache instances by unique path;
     * infuse nodes loaded by OJB with page manager configuration.
     *
     * @param oid object/node identity
     * @param node object/node to cache
     */
    private synchronized static void cacheAdd(Identity oid, NodeImpl node)
    {
        String path = node.getPath();

        // add node to caches; note that removes force notification
        // of update to distributed caches
        oidCache.remove(oid);
        boolean removed = pathCache.remove(path);
        CacheElement pathElement = pathCache.createElement(path, new DatabasePageManagerCacheObject(oid, path));
        pathCache.put(pathElement);
        // if a remove was not successful from the path cache, update
        // notification to distributed peers was not performed;
        // for updates of objects evicted from the cache or newly
        // created ones, this is problematic: remove and put into
        // path cache a second time to force
        if (!removed && updatePathsList.contains(path))
        {
            pathCache.remove(path);
            pathCache.put(pathElement);
        }
        // add node to local oid cache by key after removes from
        // distributed path cache since those removes will remove
        // from local oid cache in notifications, (despite the
        // 'local' listener registration)
        CacheElement element = oidCache.createElement(oid, node);
        oidCache.put(element);
    }

    /**
     * Remove path from list of updating paths.
     *
     * @param path object path
     */
    public synchronized static void removeUpdatePath(String path)
    {
        // remove single path from list
        updatePathsList.remove(path);
    }

    /**
     * Clear object and path caches.
     */
    public synchronized static void cacheClear()
    {
        // clear locally managed mappings
        pathToOidMap.clear();
        // remove all items from oid and properties caches
        // individually to ensure notifications are run to
        // detach elements; do not invoke JetspeedCache.clear()
        for (Object remove : oidCache.getKeys())
        {
            oidCache.remove((Identity)remove);
        }
        for (Object remove : propertiesCache.getKeys())
        {
            propertiesCache.remove(remove);
        }
        for (Object remove : principalPropertiesCache.getKeys())
        {
            principalPropertiesCache.remove(remove);
        }
        // remove all items from path caches individually
        // to avoid potential distributed clear invocation
        // that would be performed against all peers; do
        // not invoke JetspeedCache.clear()
        for (Object remove : pathCache.getKeys())
        {
            pathCache.removeQuiet(remove);
        }
        for (Object remove : propertiesPathCache.getKeys())
        {
            propertiesPathCache.removeQuiet(remove);
        }
        for (Object remove : principalPropertiesPathCache.getKeys())
        {
            principalPropertiesPathCache.removeQuiet(remove);
        }
    }

    /**
     * Lookup node by identity.
     *
     * @param oid node identity
     * @param cacheRead application cache read hit
     * @return cached node
     */
    private synchronized static NodeImpl cacheLookup(Identity oid, boolean cacheRead)
    {
        if (oid != null)
        {
            // return valid object cached by oid
            CacheElement element = oidCache.get(oid);
            if (element != null)
            {
                NodeImpl node = (NodeImpl)element.getContent();

                // if cache access is considered an application
                // read hit, ping elements in oid and path caches
                // related to retrieved node to prevent them from
                // being LRU reaped from the cache and limit
                // cache churn, heap bloat, and graph calving
                if (cacheRead)
                {
                    // ping node path cache element
                    String path = node.getPath();
                    pathCache.get(path);
                    // iterate up cached parent folder hierarchy
                    Integer parentIdentity = node.getParentIdentity();
                    while (parentIdentity != null)
                    {
                        // access parent node by oid from cache and ping
                        // parent oid cache element in the process
                        Identity parentOid = new Identity(FolderImpl.class, FolderImpl.class, new Object[]{new Integer(parentIdentity)});
                        CacheElement parentElement = oidCache.get(parentOid);
                        if (parentElement != null)
                        {
                            // ping parent node path cache element
                            NodeImpl parentNode = (NodeImpl)parentElement.getContent();
                            String parentPath = parentNode.getPath();
                            pathCache.get(parentPath);
                            // get parent identity if available
                            parentIdentity = parentNode.getParentIdentity();
                        }
                        else
                        {
                            // parent folder no longer in cache, will reload in
                            // cache when parent folder is subsequently accessed
                            break;
                        }
                    }
                }
                
                return node;
            }
        }
        return null;
    }

    /**
     * Remove identified object from object and path caches.
     *
     * @param oid object identity
     */
    public synchronized static void cacheRemove(Identity oid)
    {
        // remove from cache by oid
        if (oid != null)
        {
            NodeImpl node = cacheLookup(oid, false);
            if (node != null)
            {
                String path = node.getPath();
                // remove from caches; note that removes are
                // propagated to distributed caches
                oidCache.remove(oid);
                boolean removed = pathCache.remove(path);
                // if a remove was not successful from the path cache,
                // remove notification to distributed peers was not
                // performed; this is problematic: put into path cache
                // and remove a second time to force
                if (!removed)
                {
                    CacheElement pathElement = pathCache.createElement(path, new DatabasePageManagerCacheObject(oid, path));
                    pathCache.put(pathElement);
                    pathCache.remove(path);
                }
            }
        }
    }

    /**
     * Remove identified object from object and path caches.
     *
     * @param path object path
     */
    public synchronized static void cacheRemove(String path)
    {
        // remove from cache by path
        if (path != null)
        {
            // remove from oid cache
            Identity oid = pathToOidMap.get(path);
            if (oid != null)
            {
                oidCache.remove(oid);
            }
            // remove from path cache
            CacheElement pathElement = pathCache.get(path);
            if (pathElement != null)
            {
                DatabasePageManagerCacheObject cacheObject = (DatabasePageManagerCacheObject)pathElement.getContent();
                // remove from caches; note that removes are
                // propagated to distributed caches
                pathCache.remove(path);
            }
            else
            {
                // if an object is not found in the path cache, remove
                // notification to distributed peers will not be performed;
                // this is problematic: put into path cache and remove to
                // force
                pathElement = pathCache.createElement(path, new DatabasePageManagerCacheObject(path));
                pathCache.put(pathElement);
                pathCache.remove(path);                
            }
        }
    }
    
    /**
     * Reset cached security constraints in all cached objects.
     */
    public synchronized static void resetCachedSecurityConstraints()
    {
        // reset cached objects
        for (Object reset : oidCache.getKeys())
        {
            NodeImpl node = cacheLookup((Identity)reset, false);
            if (node != null)
            {
            	node.resetCachedSecurityConstraints();
            }
        }
    }
    
    /**
     * Add new or update cached fragment property list.
     * 
     * @param fragmentKey fragment key for fragment property list
     * @param fragmentPropertyList fragment property list
     * @param update flag indicating update
     * @return transaction operation path
     */
    public synchronized static void fragmentPropertyListCacheAdd(String fragmentKey, DatabasePageManagerCachedFragmentPropertyList fragmentPropertyList, boolean update)
    {
        // remove locally cached fragment lists by fragment key
        propertiesCache.remove(fragmentKey);
        // update remote caches by fragment key
        if (update)
        {
            // perform remote update via fragment key depending
            // on scope of update operation
            boolean removed = propertiesPathCache.remove(fragmentKey);
            // if a remove was not successful from the path cache, update
            // notification to distributed peers was not performed;
            // for updates of objects evicted from the cache or newly
            // created ones, this is problematic: put into path cache a
            // and remove a second time to force
            if (!removed)
            {
                propertiesPathCache.put(propertiesPathCache.createElement(fragmentKey, new DatabasePageManagerFragmentKeyCacheObject(fragmentKey)));
                propertiesPathCache.remove(fragmentKey);
            }
        }
        // ensure cache key in properties path cache
        if (propertiesPathCache.get(fragmentKey) == null)
        {
            propertiesPathCache.put(propertiesPathCache.createElement(fragmentKey, new DatabasePageManagerFragmentKeyCacheObject(fragmentKey)));
        }
        // add fragment properties list to local cache by key after
        // removes from distributed path cache since those removes
        // will remove from local cache in notifications, (despite the
        // 'local' listener registration)
        propertiesCache.put(propertiesCache.createElement(fragmentKey, fragmentPropertyList));
    }
    
    /**
     * Lookup fragment property list by cache key.
     * 
     * @param fragmentKey fragment key for fragment property list
     * @return fragment property list
     */
    public synchronized static DatabasePageManagerCachedFragmentPropertyList fragmentPropertyListCacheLookup(String fragmentKey)
    {
        // return fragment properties list cached locally by key
        if (fragmentKey != null)
        {
            CacheElement propertiesElement = propertiesCache.get(fragmentKey);
            if (propertiesElement != null)
            {
                return (DatabasePageManagerCachedFragmentPropertyList)propertiesElement.getContent();
            }
        }
        return null;
    }

    /**
     * Remove fragment property list from local and remote caches.
     * 
     * @param fragmentKey fragment key for fragment property list
     */
    public synchronized static void fragmentPropertyListCacheRemove(String fragmentKey)
    {
        // remove fragment properties list cached locally by key
        // and notify remote caches by fragment key
        if (fragmentKey != null)
        {
            propertiesCache.remove(fragmentKey);
            boolean removed = propertiesPathCache.remove(fragmentKey);
            // if a remove was not successful from the path cache, update
            // notification to distributed peers was not performed;
            // for updates of objects evicted from the cache, this is
            // problematic: put into path cache and remove a second time
            // to force notification
            if (!removed)
            {
                propertiesPathCache.put(propertiesPathCache.createElement(fragmentKey, new DatabasePageManagerFragmentKeyCacheObject(fragmentKey)));
                propertiesPathCache.remove(fragmentKey);
            }
        }
    }

    /**
     * Add new or update cached principal fragment property list.
     * 
     * @param principalKey cache key for fragment property list
     * @param fragmentPropertyList fragment property list
     * @param update flag indicating update
     */
    public synchronized static void principalFragmentPropertyListCacheAdd(String principalKey, DatabasePageManagerCachedFragmentPropertyList fragmentPropertyList, boolean update)
    {
        // remove locally cached fragment lists by principal key
        principalPropertiesCache.remove(principalKey);
        // update remote caches by principal key
        if (update)
        {
            // perform remote update via principal key
            boolean removed = principalPropertiesPathCache.remove(principalKey);
            // if a remove was not successful from the path cache, update
            // notification to distributed peers was not performed;
            // for updates of objects evicted from the cache or newly
            // created ones, this is problematic: put into path cache a
            // and remove a second time to force
            if (!removed)
            {
                principalPropertiesPathCache.put(principalPropertiesPathCache.createElement(principalKey, new DatabasePageManagerPrincipalKeyCacheObject(principalKey)));
                principalPropertiesPathCache.remove(principalKey);
            }
        }
        // ensure cache key in properties path cache
        if (principalPropertiesPathCache.get(principalKey) == null)
        {
            principalPropertiesPathCache.put(principalPropertiesPathCache.createElement(principalKey, new DatabasePageManagerPrincipalKeyCacheObject(principalKey)));
        }
        // add fragment properties list to local cache by key after
        // removes from distributed path cache since those removes
        // will remove from local cache in notifications, (despite the
        // 'local' listener registration)
        principalPropertiesCache.put(principalPropertiesCache.createElement(principalKey, fragmentPropertyList));
    }
    
    /**
     * Lookup principal fragment property list by principal key.
     * 
     * @param principalKey principal key for fragment property list
     * @return fragment property list
     */
    public synchronized static DatabasePageManagerCachedFragmentPropertyList principalFragmentPropertyListCacheLookup(String principalKey)
    {
        // return fragment properties list cached locally by key
        if (principalKey != null)
        {
            CacheElement propertiesElement = principalPropertiesCache.get(principalKey);
            if (propertiesElement != null)
            {
                return (DatabasePageManagerCachedFragmentPropertyList)propertiesElement.getContent();
            }
        }
        return null;
    }

    /**
     * Remove principal fragment property list from local and remote caches.
     * 
     * @param principalKey principal key fragment property list
     */
    public synchronized static void principalFragmentPropertyListCacheRemove(String principalKey)
    {
        // remove fragment properties list cached locally and
        // notify remote caches by principal key
        if (principalKey != null)
        {
            principalPropertiesCache.remove(principalKey);
            boolean removed = principalPropertiesPathCache.remove(principalKey);
            // if a remove was not successful from the path cache, update
            // notification to distributed peers was not performed;
            // for updates of objects evicted from the cache, this is
            // problematic: put into path cache and remove a second time
            // to force notification
            if (!removed)
            {
                principalPropertiesPathCache.put(principalPropertiesPathCache.createElement(principalKey, new DatabasePageManagerPrincipalKeyCacheObject(principalKey)));
                principalPropertiesPathCache.remove(principalKey);                
            }
        }
    }

    /**
     * Get transactions registered on current thread
     * 
     * @return transactions list
     */
    public static List<TransactionedOperation> getTransactions()
    {
        List<TransactionedOperation> operations = transactionedOperations.get();
        if (operations == null)
        {
            operations = new LinkedList<TransactionedOperation>();
            transactionedOperations.set(operations);
        }
        return operations;
    }

    /**
     * Register transactions with current thread
     * 
     * @param operation transaction operation
     */
    public static void addTransaction(TransactionedOperation operation)
    {
        List<TransactionedOperation> transactions = getTransactions();
        transactions.add(operation);
    }
    
    /**
     * Rollback transactions registered with current thread.
     */
    public synchronized static void rollbackTransactions()
    {
        for (TransactionedOperation operation : getTransactions())
        {
            if ((operation.getTransactionType() == TransactionedOperation.ADD_OPERATION) ||
                (operation.getTransactionType() == TransactionedOperation.UPDATE_OPERATION))
            {
                cacheRemove(operation.getPath());
            }
            else if ((operation.getTransactionType() == TransactionedOperation.ADD_FRAGMENT_PROPERTIES_OPERATION) ||
                     (operation.getTransactionType() == TransactionedOperation.UPDATE_FRAGMENT_PROPERTIES_OPERATION))
            {
                fragmentPropertyListCacheRemove(operation.getFragmentKey());
            }
            else if ((operation.getTransactionType() == TransactionedOperation.ADD_PRINCIPAL_FRAGMENT_PROPERTIES_OPERATION) ||
                     (operation.getTransactionType() == TransactionedOperation.UPDATE_PRINCIPAL_FRAGMENT_PROPERTIES_OPERATION))
            {
                principalFragmentPropertyListCacheRemove(operation.getPrincipalKey());
            }
        }
    }

    /**
     * Clear transactions registered with current thread.
     */
    public static void clearTransactions()
    {
        transactionedOperations.remove();
    }

    /**
     * Returns whether this cache is currently part of a distributed cache cluster.
     * 
     * @return distributed flag
     */
    public static boolean isDistributed()
    {
        return (pathCache.isDistributed() && propertiesPathCache.isDistributed() && principalPropertiesPathCache.isDistributed());
    }

    // OJB Constructor
    
    /**
     * Construct a cache instance using OJB compliant signatures.
     *
     * @param broker broker that is to own cache
     * @param props attribute properties passed to cache
     */
    public DatabasePageManagerCache(PersistenceBroker broker, Properties props)
    {
    }

    // OJB ObjectCache Implementation
    
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.cache.ObjectCache#cache(org.apache.ojb.broker.Identity, java.lang.Object)
     */
    public void cache(Identity oid, Object obj)
    {
        if (obj instanceof NodeImpl)
        {
            cacheAdd(oid, (NodeImpl)obj);
        }
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
        return cacheLookup(oid, true);
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.cache.ObjectCache#remove(org.apache.ojb.broker.Identity)
     */
    public void remove(Identity oid)
    {
        cacheRemove(oid);
    }

    // Utilities
    
    /**
     * Dump cache paths and oids to standard out.
     */
    public synchronized static void dump()
    {
        StringBuilder dump = new StringBuilder();
        dump.append("--------------------------"+EOL);
        for (Object dumpOid : oidCache.getKeys())
        {
            Identity oid = (Identity)dumpOid;
            NodeImpl node = cacheLookup(oid, false);
            dump.append("node="+node.getPath()+", oid="+oid+EOL);
        }
        dump.append("--------------------------");
        log.debug("DatabasePageManagerCache dump:"+EOL+dump.toString());
    }    
}
