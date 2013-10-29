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
package org.apache.jetspeed.cache;

import org.apache.jetspeed.request.RequestContext;

import java.util.List;

/**
 * <p>
 *  Provides interface to Jetspeed for cache related activities
 *  Abstraction around actual cache implementation
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface JetspeedCache
{
    /**
     * Retrieve an object from the cache
     * 
     * @param key The key used to find the object
     * @return the found object or null
     */
    CacheElement get(Object key);
    
    /**
     * clear all content in the cache
     *
     */
    void clear();
    
    /**
     * Put an object into the cache, adding it, or replacing if exists
     * @param object
     */
    void put(CacheElement object);
    
    /**
     * Create a cached element 
     * 
     * @param key
     * @param content
     * @return
     */
    CacheElement createElement(Object key, Object content);
    
    boolean isKeyInCache(Object key);
    
    /**
     * Remove an object from the cache
     * @param key
     * @return true if the object was removed, false otherwise
     */
    boolean remove(Object key);
    
    /**
     * Remove object from cache, do not notify listeners
     * 
     * @param key
     * @return trie if the object was removed, false otherwise
     */
    boolean removeQuiet(Object key);
    
    /**
     * 
     * @return the default idle time in seconds for this cache
     */
    int getTimeToIdleSeconds();
    
    /**
     * 
     * @return the default idle time in seconds for this cache
     */
    int getTimeToLiveSeconds();

    /**
     * Evict all cached content for the given username 
     * 
     * @param username unique user identifier
     */
    void evictContentForUser(String username);

    /**
     * Evict all cached content for the given session identifier 
     * 
     * @param sessionId unique session identifier
     */    
    void evictContentForSession(String sessionId);
    
    /**
     * Create a portlet content cache key based on dynamic request context information and a window id
     * 
     * @param rc
     * @param windowId
     * @since 2.1.2
     * @return
     */
    ContentCacheKey createCacheKey(RequestContext rc, String windowId);
    
    /**
     * Add a cache listener for supported cache events, either for local or remote cache events
     * 
     * @param listener
     * @param local if true, listen to local events, if false, listen to remote 
     */         
    void addEventListener(JetspeedCacheEventListener listener, boolean local);
    
    void removeEventListener(JetspeedCacheEventListener listener, boolean local);
    
    /**
     * Returns a list of all elements in the cache, whether or not they are expired.
     * The returned keys are unique and can be considered a set. 
     * @return the list of keys
     */
    List getKeys();
    
    /**
     * get the size of the cache
     *
     * @return the size of the cache
     */
    int getSize();
    
    /**
     * Returns whether this cache is currently part of a distributed cache cluster.
     * 
     * @return distributed flag
     */
    boolean isDistributed();
    
    /**
     * get the maximum size of the cache
     *
     * @return the maximum size of the cache
     */
    int getMaxSize();
}
