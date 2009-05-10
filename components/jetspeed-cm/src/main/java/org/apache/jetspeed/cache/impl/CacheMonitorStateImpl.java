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

import org.apache.jetspeed.cache.CacheMonitorState;


public class CacheMonitorStateImpl implements CacheMonitorState
{
    private String name;
    private float averageGetTime;
    private long cacheHits;
    private long cacheMisses;
    private long diskStoreSize;
    private long evictionCount;
    private long inMemoryHits;
    private long inMemorySize;
    private long memoryStoreSize;
    private long objectCount;
    private long size;
    private long onDiskHits;  
    private long maxElementsInMemory;
    private long maxElementsOnDisk;
    private long timeToIdle;
    private long timeToLive;
    
    public CacheMonitorStateImpl(String name)
    {
        this.name = name;
    }

    public String getCacheName()
    {
        return name;
    }
    
    public float getAverageGetTime()
    {
        return averageGetTime;
    }

    public long getCacheHits()
    {
        return cacheHits;
    }

    public long getCacheMisses()
    {
        return cacheMisses;
    }

    public long getDiskStoreSize()
    {
        return diskStoreSize;
    }

    public long getEvictionCount()
    {
        return evictionCount;
    }

    public long getInMemoryHits()
    {
        return inMemoryHits;
    }

    public long getInMemorySize()
    {
        return inMemorySize;
    }

    public long getMemoryStoreSize()
    {
        return memoryStoreSize;
    }

    public long getObjectCount()
    {
        return objectCount;
    }
    
    public long getSize()
    {
        return size;
    }

    public long getOnDiskHits()
    {
        return onDiskHits;
    }
    
    
    public long getMaxElementsInMemory()
    {
        return maxElementsInMemory;
    }

    public long getMaxElementsOnDisk()
    {
        return maxElementsOnDisk;
    }
    
    public long getTimeToIdle()
    {
        return timeToIdle;
    }

    public long getTimeToLive()
    {
        return timeToLive;
    }        
    
    protected void setAverageGetTime(float averageGetTime)
    {
        this.averageGetTime = averageGetTime;
    }

    
    protected void setCacheHits(long cacheHits)
    {
        this.cacheHits = cacheHits;
    }

    
    protected void setCacheMisses(long cacheMisses)
    {
        this.cacheMisses = cacheMisses;
    }

    
    protected void setDiskStoreSize(long diskStoreSize)
    {
        this.diskStoreSize = diskStoreSize;
    }

    
    protected void setEvictionCount(long evictionCount)
    {
        this.evictionCount = evictionCount;
    }

    
    protected void setInMemoryHits(long inMemoryHits)
    {
        this.inMemoryHits = inMemoryHits;
    }

    
    protected void setInMemorySize(long inMemorySize)
    {
        this.inMemorySize = inMemorySize;
    }

    
    protected void setMemoryStoreSize(long memoryStoreSize)
    {
        this.memoryStoreSize = memoryStoreSize;
    }

    
    protected void setObjectCount(long objectCount)
    {
        this.objectCount = objectCount;
    }

    
    protected void setSize(long size)
    {
        this.size = size;
    }

    
    protected void setOnDiskHits(long onDiskHits)
    {
        this.onDiskHits = onDiskHits;
    }
 
    
    protected void setMaxElementsInMemory(long maxElementsInMemory)
    {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    
    protected void setMaxElementsOnDisk(long maxElementsOnDisk)
    {
        this.maxElementsOnDisk = maxElementsOnDisk;
    }

    
    protected void setTimeToIdle(long timeToIdle)
    {
        this.timeToIdle = timeToIdle;
    }

    protected void setTimeToLive(long timeToLive)
    {
        this.timeToLive = timeToLive;
    }
    
}