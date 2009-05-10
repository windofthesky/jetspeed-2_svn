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

/**
 * <p>
 *  Individual cache information and state to provide to Jetspeed Cache Monitor
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface CacheMonitorState
{
    /**
     * @return the name of the cache
     */
    String getCacheName();

    /**
     * @return the number of elements in the memory cache
     */    
    long getMemoryStoreSize();

    /**
     * @return the size of the memory store for this cache
     */    
    long getInMemorySize();
    
    /**
     * @return the entire size of the cache including the number of elements in the memory store plus the number of elements in the disk store
     */
    long getSize();
    
    /**
     * @return the number of elements in the disk store
     */
    long getDiskStoreSize();

    /**
     * @return the average time to retrieve a cache element in milliseconds
     */
    float getAverageGetTime();
    
    /**
     * @return the number of times a requested item was found in the cache.
     */
    long getCacheHits();

    /**
     * @return the number of times a requested element was not found in the cache
     */    
    long getCacheMisses();
    
    /**
     * @return the number of cache evictions, since the cache was created, or statistics were cleared
     */    
    long getEvictionCount();
    
    /**
     * @return the number of times a requested item was found in memory 
     */    
    long getInMemoryHits();
    
    /**
     * @return gets the number of elements stored in the cache. Caclulating this can be expensive. 
     * Accordingly, this method will return three different values, depending on the statistics accuracy setting
     */    
    long getObjectCount();
    
    /**
     * @return the number of times a requested element was not found in the cache
     */    
    long getOnDiskHits();
    
    /**
     * @return get configured maximum elements in memory
     */
    long getMaxElementsInMemory();

    /**
     * @return get configured maximum elements on disk
     */
    long getMaxElementsOnDisk();
    
    /**
     * @return get configured time to idle in seconds before expiring
     */
    long getTimeToIdle();

    /**
     * @return get configured time to live in seconds before expiring
     */
    long getTimeToLive();
}
 