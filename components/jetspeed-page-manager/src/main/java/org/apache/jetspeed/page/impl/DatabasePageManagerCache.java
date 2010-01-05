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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.page.impl.FragmentPropertyList;
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
    // Members
    
    private static JetspeedCache oidCache;
    private static JetspeedCache pathCache;
    private static JetspeedCache propertiesCache;
    private static JetspeedCache propertiesPathCache;
    private static Map propertiesCacheIndexMap;
    private static boolean constraintsEnabled;
    private static boolean permissionsEnabled;
    private static PageManager pageManager;
    private static List updatePathsList = new ArrayList();
    private static ThreadLocal transactionedOperations = new ThreadLocal();

    // Implementation
    
    /**
     * cacheInit
     *
     * Initialize cache using page manager configuration.
     *
     * @param pageManager configured page manager
     */
    public synchronized static void cacheInit(final JetspeedCache oidCache, final JetspeedCache pathCache, final JetspeedCache propertiesCache, final JetspeedCache propertiesPathCache, final DatabasePageManager pageManager)
    {
        // initialize
        DatabasePageManagerCache.oidCache = oidCache;
        DatabasePageManagerCache.pathCache = pathCache;
        DatabasePageManagerCache.propertiesCache = propertiesCache;
        DatabasePageManagerCache.propertiesPathCache = propertiesPathCache;
        propertiesCacheIndexMap = new HashMap();
        constraintsEnabled = pageManager.getConstraintsEnabled();
        permissionsEnabled = pageManager.getPermissionsEnabled();
        
        // setup local oid cache listener
        oidCache.addEventListener(new JetspeedCacheEventListener()
        {
            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementAdded(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementAdded(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                final NodeImpl node = (NodeImpl)element;
                // infuse node with page manager configuration
                // or the page manager itself and add to the
                // paths cache
                node.setConstraintsEnabled(constraintsEnabled);
                node.setPermissionsEnabled(permissionsEnabled);
                node.setPageManager(pageManager);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementChanged(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementChanged(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                notifyElementAdded(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementEvicted(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementEvicted(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                notifyElementRemoved(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementExpired(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementExpired(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                notifyElementRemoved(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementRemoved(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementRemoved(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                final NodeImpl node = (NodeImpl)element;
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
            public void notifyElementAdded(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementChanged(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementChanged(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementEvicted(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementEvicted(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementExpired(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementExpired(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementRemoved(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementRemoved(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                final DatabasePageManagerCacheObject cacheObject = (DatabasePageManagerCacheObject)element;
                // remove cache object from local oid cache
                if (cacheObject != null)
                {
                    final Identity oid = cacheObject.getId();
                    final String path = cacheObject.getPath();
                    if ((oid != null) || (path != null))
                    {
                        synchronized (DatabasePageManagerCache.class)
                        {
                            if (oid != null)
                            {
                                // get object cached by oid
                                final NodeImpl node = (NodeImpl)cacheLookup(oid);
                                // reset internal FolderImpl caches
                                if (node instanceof FolderImpl)
                                {
                                    ((FolderImpl)node).resetAll(false);
                                }
                                // notify page manager of update
                                pageManager.notifyUpdatedNode(node);
                                // remove from cache
                                oidCache.removeQuiet(oid);
                            }
                            if (path != null)
                            {
                                // lookup parent object cached by path and oid
                                final int pathLastSeparatorIndex = path.lastIndexOf(Folder.PATH_SEPARATOR);
                                final String parentPath = ((pathLastSeparatorIndex > 0) ? path.substring(0, pathLastSeparatorIndex) : Folder.PATH_SEPARATOR);
                                final NodeImpl parentNode = cacheLookup(parentPath);
                                // reset internal FolderImpl caches in case element removed
                                if (parentNode instanceof FolderImpl)
                                {
                                    ((FolderImpl)parentNode).resetAll(false);
                                }
                                // remove all indexed cache keys for page path from
                                // properties cache index
                                Set index = (Set)propertiesCacheIndexMap.get(path);
                                if (index != null)
                                {
                                    // remove all indexed cache keys
                                    Iterator cacheKeyIter = index.iterator();
                                    while (cacheKeyIter.hasNext())
                                    {
                                        String cacheKey = (String)cacheKeyIter.next();
                                        propertiesCache.removeQuiet(cacheKey);
                                        propertiesPathCache.removeQuiet(cacheKey);
                                    }
                                    propertiesCacheIndexMap.remove(path);
                                }
                                // ensure removed from cache
                                pathCache.removeQuiet(path);
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
            public void notifyElementAdded(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                final String cacheKey = (String)key;
                final FragmentPropertyList fragmentPropertyList = (FragmentPropertyList)element;
                if (fragmentPropertyList != null)
                {
                    // add cache key to properties cache index
                    final String path = fragmentPropertyList.getFragmentImpl().getBaseFragmentsElement().getPath();
                    synchronized (DatabasePageManagerCache.class)
                    {
                        Set index = (Set)propertiesCacheIndexMap.get(path);
                        if (index == null)
                        {
                            index = new HashSet();
                            propertiesCacheIndexMap.put(path, index);
                        }
                        index.add(cacheKey);
                    }
                }
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementChanged(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementChanged(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                notifyElementAdded(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementEvicted(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementEvicted(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                notifyElementRemoved(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementExpired(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementExpired(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                notifyElementRemoved(cache, local, key, element);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementRemoved(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementRemoved(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                final String cacheKeyOrPath = (String)key;
                final FragmentPropertyList fragmentPropertyList = (FragmentPropertyList)element;
                if (fragmentPropertyList != null)
                {
                    // remove single cache key from properties cache index
                    final String path = fragmentPropertyList.getFragmentImpl().getBaseFragmentsElement().getPath();
                    synchronized (DatabasePageManagerCache.class)
                    {
                        Set index = (Set)propertiesCacheIndexMap.get(path);
                        if (index != null)
                        {
                            index.remove(cacheKeyOrPath);
                            if (index.isEmpty())
                            {
                                propertiesCacheIndexMap.remove(path);
                            }
                        }
                    }
                }
                else if (cacheKeyOrPath != null)
                {
                    // remove all indexed cache keys from properties cache index
                    synchronized (DatabasePageManagerCache.class)
                    {
                        Set index = (Set)propertiesCacheIndexMap.get(cacheKeyOrPath);
                        if (index != null)
                        {
                            // remove all indexed cache keys
                            Iterator cacheKeyIter = index.iterator();
                            while (cacheKeyIter.hasNext())
                            {
                                String cacheKey = (String)cacheKeyIter.next();
                                propertiesCache.removeQuiet(cacheKey);
                            }
                            propertiesCacheIndexMap.remove(cacheKeyOrPath);
                        }
                    }                    
                }
                else if (cacheKeyOrPath != null)
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
            public void notifyElementAdded(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementChanged(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementChanged(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementEvicted(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementEvicted(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementExpired(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementExpired(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.cache.JetspeedCacheEventListener#notifyElementRemoved(org.apache.jetspeed.cache.JetspeedCache, boolean, java.lang.Object, java.lang.Object)
             */
            public void notifyElementRemoved(final JetspeedCache cache, final boolean local, final Object key, final Object element)
            {
                final DatabasePageManagerCacheObject cacheObject = (DatabasePageManagerCacheObject)element;
                // remove cache object from local properties cache
                if (cacheObject != null)
                {
                    final String cacheKeyOrPath = cacheObject.getPath();
                    if (cacheKeyOrPath != null)
                    {
                        synchronized (DatabasePageManagerCache.class)
                        {
                            // find all indexed cache keys for path
                            Set index = (Set)propertiesCacheIndexMap.get(cacheKeyOrPath);
                            if (index != null)
                            {
                                // remove all indexed cache keys
                                Set indexClone = new HashSet(index);
                                Iterator cacheKeyIter = indexClone.iterator();
                                while (cacheKeyIter.hasNext())
                                {
                                    String cacheKey = (String)cacheKeyIter.next();
                                    propertiesCache.remove(cacheKey);
                                    propertiesPathCache.removeQuiet(cacheKey);
                                }
                            }
                            else
                            {
                                // assume path is cache key to remove
                                propertiesCache.remove(cacheKeyOrPath);
                                propertiesPathCache.removeQuiet(cacheKeyOrPath);
                            }
                        }
                    }
                }
            }
        }, false);
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
     * Lookup object instances by unique path.
     *
     * @param path node unique path
     * @return cached node
     */
    public synchronized static NodeImpl cacheLookup(final String path)
    {
        if (path != null)
        {
            // return valid object cached by path and oid
            final CacheElement pathElement = pathCache.get(path);
            if (pathElement != null)
            {
                final DatabasePageManagerCacheObject cacheObject = (DatabasePageManagerCacheObject)pathElement.getContent();
                return (NodeImpl)cacheLookup(cacheObject.getId());
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
    public synchronized static void addUpdatePath(final String path)
    {
        // add path for later examination, (duplicates allowed)
        updatePathsList.add(path);
    }

    /**
     * Add object to cache and cache instances by unique path;
     * infuse nodes loaded by OJB with page manager configuration.
     *
     * @param oid object/node identity
     * @param obj object/node to cache
     */
    public synchronized static void cacheAdd(final Identity oid, final Object obj)
    {
        if (obj instanceof NodeImpl)
        {
            final NodeImpl node = (NodeImpl)obj;
            final String path = node.getPath();

            // add node to caches; note that removes force notification
            // of update to distributed caches
            oidCache.remove(oid);
            final boolean removed = pathCache.remove(path);
            final CacheElement pathElement = pathCache.createElement(path, new DatabasePageManagerCacheObject(oid, path));
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
            final CacheElement element = oidCache.createElement(oid, node);
            oidCache.put(element);
        }
    }

    /**
     * Remove path from list of updating paths.
     *
     * @param path object path
     */
    public synchronized static void removeUpdatePath(final String path)
    {
        // remove single path from list
        updatePathsList.remove(path);
    }

    /**
     * Clear object and path caches.
     */
    public synchronized static void cacheClear()
    {
        // remove all items from oid and properties caches
        // individually to ensure notifications are run to
        // detach elements; do not invoke JetspeedCache.clear()
        final Iterator removeOidIter = oidCache.getKeys().iterator();
        while (removeOidIter.hasNext())
        {
            oidCache.remove((Identity)removeOidIter.next());
        }
        final Iterator removePropertiesIter = propertiesCache.getKeys().iterator();
        while (removePropertiesIter.hasNext())
        {
            propertiesCache.remove(removePropertiesIter.next());
        }
        // remove all items from path caches individually
        // to avoid potential distributed clear invocation
        // that would be performed against all peers; do
        // not invoke JetspeedCache.clear()
        final Iterator removePathIter = pathCache.getKeys().iterator();
        while (removePathIter.hasNext())
        {
            pathCache.removeQuiet(removePathIter.next());
        }
        final Iterator removePropertiesPathIter = propertiesPathCache.getKeys().iterator();
        while (removePropertiesPathIter.hasNext())
        {
            propertiesPathCache.removeQuiet(removePropertiesPathIter.next());
        }
    }

    /**
     * Lookup objects by identity.
     *
     * @param oid object identity
     * @return cached object
     */
    public synchronized static Object cacheLookup(final Identity oid)
    {
        if (oid != null)
        {
            // return valid object cached by oid
            final CacheElement element = oidCache.get(oid);
            if (element != null)
            {
                return element.getContent();
            }
        }
        return null;
    }

    /**
     * Remove identified object from object and path caches.
     *
     * @param oid object identity
     */
    public synchronized static void cacheRemove(final Identity oid)
    {
        // remove from cache by oid
        if (oid != null)
        {
            final NodeImpl node = (NodeImpl)cacheLookup(oid);
            if (node != null)
            {
                final String path = node.getPath();
                // remove from caches; note that removes are
                // propagated to distributed caches
                oidCache.remove(oid);
                final boolean removed = pathCache.remove(path);
                // if a remove was not successful from the path cache,
                // remove notification to distributed peers was not
                // performed; this is problematic: put into path cache
                // and remove a second time to force
                if (!removed)
                {
                    final CacheElement pathElement = pathCache.createElement(path, new DatabasePageManagerCacheObject(oid, path));
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
    public synchronized static void cacheRemove(final String path)
    {
        // remove from cache by path
        if (path != null)
        {
            CacheElement pathElement = pathCache.get(path);
            if (pathElement != null)
            {
                final DatabasePageManagerCacheObject cacheObject = (DatabasePageManagerCacheObject)pathElement.getContent();
                // remove from caches; note that removes are
                // propagated to distributed caches
                oidCache.remove(cacheObject.getId());
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
        final Iterator resetIter = oidCache.getKeys().iterator();
        while (resetIter.hasNext())
        {
            final NodeImpl node = (NodeImpl)cacheLookup((Identity)resetIter.next());
            node.resetCachedSecurityConstraints();
        }
    }
    
    /**
     * Add new or update cached fragment property list.
     * 
     * @param cacheKey cache key for fragment property list
     * @param fragmentPropertyList fragment property list
     * @param update flag indicating update
     * @param sharedUpdate flag indicating shared property update that requires path cache eviction
     * @return transaction operation path
     */
    public synchronized static String fragmentPropertyListCacheAdd(String cacheKey, FragmentPropertyList fragmentPropertyList, boolean update, boolean sharedUpdate)
    {
        // remove locally cached fragment lists by cache key or
        // path based on update scope
        final String path = fragmentPropertyList.getFragmentImpl().getBaseFragmentsElement().getPath();
        final String updatePathOrCacheKey = ((update && sharedUpdate) ? path : cacheKey);
        propertiesCache.remove(updatePathOrCacheKey);
        // update remote caches by cache key or path
        if (update)
        {
            // perform remote update via path or cache key depending
            // on scope of update operation
            final boolean removed = propertiesPathCache.remove(updatePathOrCacheKey);
            // if a remove was not successful from the path cache, update
            // notification to distributed peers was not performed;
            // for updates of objects evicted from the cache or newly
            // created ones, this is problematic: put into path cache a
            // and remove a second time to force
            if (!removed)
            {
                final CacheElement propertiesPathElement = propertiesPathCache.createElement(updatePathOrCacheKey, new DatabasePageManagerCacheObject(updatePathOrCacheKey));
                propertiesPathCache.put(propertiesPathElement);
                propertiesPathCache.remove(updatePathOrCacheKey);
            }
        }
        // ensure cache key in properties path cache
        if (propertiesPathCache.get(cacheKey) == null)
        {
            final CacheElement propertiesPathElement = propertiesPathCache.createElement(cacheKey, new DatabasePageManagerCacheObject(cacheKey));
            propertiesPathCache.put(propertiesPathElement);
        }
        // add fragment properties list to local cache by key after
        // removes from distributed path cache since those removes
        // will remove from local cache in notifications, (despite the
        // 'local' listener registration)
        final CacheElement propertiesElement = propertiesCache.createElement(cacheKey, fragmentPropertyList);
        propertiesCache.put(propertiesElement);
        // return update path or cache key as transaction operation path
        return updatePathOrCacheKey;
    }
    
    /**
     * Lookup fragment property list by cache key.
     * 
     * @param cacheKey cache key for fragment property list
     * @return fragment property list
     */
    public synchronized static FragmentPropertyList fragmentPropertyListCacheLookup(String cacheKey)
    {
        // return fragment properties list cached locally by key
        if (cacheKey != null)
        {
            final CacheElement propertiesElement = propertiesCache.get(cacheKey);
            if (propertiesElement != null)
            {
                return (FragmentPropertyList)propertiesElement.getContent();
            }
        }
        return null;
    }

    /**
     * Remove fragment property list from local and remote caches.
     * 
     * @param cacheKeyOrPath cache key or path for fragment property list
     */
    public synchronized static void fragmentPropertyListCacheRemove(String cacheKeyOrPath)
    {
        // remove fragment properties list cached locally by key
        // and notify remote caches by cache key or path
        if (cacheKeyOrPath != null)
        {
            propertiesCache.remove(cacheKeyOrPath);
            final boolean removed = propertiesPathCache.remove(cacheKeyOrPath);
            // if a remove was not successful from the path cache, update
            // notification to distributed peers was not performed;
            // for updates of objects evicted from the cache, this is
            // problematic: put into path cache and remove a second time
            // to force notification
            if (!removed)
            {
                final CacheElement propertiesPathElement = propertiesPathCache.createElement(cacheKeyOrPath, new DatabasePageManagerCacheObject(cacheKeyOrPath));
                propertiesPathCache.put(propertiesPathElement);
                propertiesPathCache.remove(cacheKeyOrPath);                
            }
        }
    }

    /**
     * Get transactions registered on current thread
     * 
     * @return transactions list
     */
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
     * Register transactions with current thread
     * 
     * @param operation transaction operation
     */
    public static void addTransaction(TransactionedOperation operation)
    {
        final List transactions = getTransactions();        
        transactions.add(operation);
    }
    
    /**
     * Rollback transactions registered with current thread.
     */
    public synchronized static void rollbackTransactions()
    {
        final Iterator transactions = getTransactions().iterator();
        while (transactions.hasNext())
        {
            final TransactionedOperation operation = (TransactionedOperation)transactions.next();
            if ((operation.getTransactionType() == TransactionedOperation.ADD_OPERATION) ||
                (operation.getTransactionType() == TransactionedOperation.UPDATE_OPERATION))
            {
                cacheRemove(operation.getPath());
            }
            else if ((operation.getTransactionType() == TransactionedOperation.ADD_FRAGMENT_PROPERTIES_OPERATION) ||
                     (operation.getTransactionType() == TransactionedOperation.UPDATE_FRAGMENT_PROPERTIES_OPERATION))
            {
                fragmentPropertyListCacheRemove(operation.getPath());
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
        return (pathCache.isDistributed() && propertiesPathCache.isDistributed());
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

    // Utilities
    
    /**
     * Dump cache paths and oids to standard out.
     */
    public synchronized static void dump()
    {
        System.out.println("--------------------------");        
        final Iterator dumpIter = oidCache.getKeys().iterator();
        while (dumpIter.hasNext())
        {
            final Identity oid = (Identity)dumpIter.next();
            final NodeImpl node = (NodeImpl)cacheLookup(oid);
            System.out.println("node="+node.getPath()+", oid="+oid);
        }
        System.out.println("--------------------------");
    }    
}
