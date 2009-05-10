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

import java.util.List;

/**
 * <p>
 *  Monitor active Jetspeed Caches by retrieving snapshots of cache statistics, recalculating cache statistics, and resetting the cache statistics
 *  Warning, the methods calculateStatistics can be computationally expensive 
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface JetspeedCacheMonitor
{
    /**
     * Retrieve a snapshot of all cache states and statistics
     * 
     * @return a list of cache states
     */
    List<CacheMonitorState> snapshotStatistics();
    
    /**
     * Retrieve a snapshot of a single cache states and statistics without performing expensive object count calculations
     * 
     * @param cacheName the name of the cache
     * @return a single cache statistics snapshot
     */    
    CacheMonitorState snapshotStatistics(String cacheName);
    
    /**
     * Reset the statistics for all caches
     */
    void resetStatistics();
    
    /**
     * Reset the statistics for the given cache
     * 
     * @param cacheName the name of the cache
     */
    void resetStatistics(String cacheName);
    
    /**
     * Recalculate the statistics for all caches, including cache size calculations. This may take considerable amount of time depending on the size of the caches.
     * @return new statistics list freshly calculated
     */
    List<CacheMonitorState> calculateStatistics();
    
    /**
     * Recalculate the statistics for the given cache, including cache size calculations. This may take considerable amount of time depending on the size of the caches.
     * 
     * @param cacheName the name of the cache
     * @return new statistics freshly calculated
     */
    CacheMonitorState calculateStatistics(String cacheName);    
    
}
 