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
package org.apache.jetspeed.cache.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Statistics;
import org.apache.jetspeed.cache.CacheMonitorState;
import org.apache.jetspeed.cache.JetspeedCacheMonitor;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JetspeedCacheMonitorImpl implements JetspeedCacheMonitor
{
    private CacheManager cacheManager;
    private boolean calculateObjectCount = false;
    private Map<String, CalculatedState> calculatedStates = Collections.synchronizedMap(new HashMap<String, CalculatedState>()); 
    
    public JetspeedCacheMonitorImpl(CacheManager cacheManager)
    {
        this.cacheManager = cacheManager;
    }
    
    public List<CacheMonitorState> snapshotStatistics()
    {
        return snapshotAndCalculateStatistics(false) ;
    }

    public CacheMonitorState snapshotStatistics(String cacheName)
    {
        return snapshotAndCalculateStatistics(cacheName, false) ;
    }
    
    public List<CacheMonitorState> calculateStatistics()
    {
        return snapshotAndCalculateStatistics(true);        
    }
    
    public CacheMonitorState calculateStatistics(String cacheName)
    {
        return snapshotAndCalculateStatistics(cacheName, true);
    }
    
    public void resetStatistics()
    {
        String[]names = cacheManager.getCacheNames();
        for (String name : names)
        {
            resetStatistics(name);
        }
    }

    public void resetStatistics(String cacheName)
    {
        Cache cache = cacheManager.getCache(cacheName);
        cache.clearStatistics();
        calculatedStates.remove(cacheName);
    }
 
    protected List<CacheMonitorState> snapshotAndCalculateStatistics(boolean calculate)
    {
        List<CacheMonitorState> states = new LinkedList<CacheMonitorState>();        
        String[]names = cacheManager.getCacheNames();
        for (String name : names)
        {
            states.add(snapshotAndCalculateStatistics(name, calculate));
        }
        return states;        
    }
    
    protected CacheMonitorState snapshotAndCalculateStatistics(String name, boolean calculate)
    {
        Cache cache = cacheManager.getCache(name);        
        CacheMonitorStateImpl state = new CacheMonitorStateImpl(name);
        Statistics statistics = cache.getStatistics();        
        state.setMemoryStoreSize(cache.getMemoryStoreSize());
        if (calculate)
        {
            state.setInMemorySize(cache.calculateInMemorySize());
            if (calculateObjectCount)
            {
                state.setObjectCount(statistics.getObjectCount());
            }
            else
            {
                state.setObjectCount(0);
            }
            calculatedStates.put(name, new CalculatedState(state.getInMemorySize(), state.getObjectCount()));
        }
        else
        {
            CalculatedState cs = calculatedStates.get(name);
            if (cs == null)
            {
                state.setInMemorySize(0);                
                state.setObjectCount(0);
            }
            else
            {
                state.setInMemorySize(cs.inMemorySize);
                state.setObjectCount(cs.objectCount);
            }            
        }
        state.setSize(cache.getSize());
        state.setDiskStoreSize(cache.getDiskStoreSize());
        state.setAverageGetTime(statistics.getAverageGetTime());
        state.setCacheHits(statistics.getCacheHits());
        state.setCacheMisses(statistics.getCacheMisses());
        state.setEvictionCount(statistics.getEvictionCount());
        state.setInMemoryHits(statistics.getInMemoryHits());
        state.setOnDiskHits(statistics.getOnDiskHits());
        state.setMaxElementsInMemory(cache.getCacheConfiguration().getMaxElementsInMemory());
        state.setMaxElementsOnDisk(cache.getCacheConfiguration().getMaxElementsOnDisk());            
        state.setTimeToIdle(cache.getCacheConfiguration().getTimeToIdleSeconds());
        state.setTimeToLive(cache.getCacheConfiguration().getTimeToLiveSeconds());
        return state;
    }
    
    protected CacheManager getCacheManager()
    {
        return cacheManager;
    }
    
    class CalculatedState implements Serializable
    {
        private long inMemorySize;
        private long objectCount;
        
        CalculatedState(long inMemorySize, long objectCount)
        {
            this.inMemorySize = inMemorySize;
            this.objectCount = objectCount;
        }
    }
    
}