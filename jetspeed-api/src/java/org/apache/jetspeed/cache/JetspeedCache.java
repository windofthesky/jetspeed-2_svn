/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.cache;

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
     * Evict all cached content for the given user
     * 
     * @param user
     */
    void evictContentForUser(String user);
    
    /**
     * Create a cache key from a primary segment and secondary segment
     * 
     * @param primary
     * @param secondary
     * @return
     */
    String createCacheKey(String primary, String secondary);
    
    /**
     * Add a cache listener for supported cache events
     * 
     * @param listener
     */         
    void addEventListener(JetspeedCacheEventListener listener);
    
    void removeEventListener(JetspeedCacheEventListener listener);
}