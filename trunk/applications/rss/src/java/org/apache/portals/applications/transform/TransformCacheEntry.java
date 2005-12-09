/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.portals.applications.transform;

import java.util.Date;


/**
 * TransformCacheEntry
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TransformCacheEntry
{
    protected String key;
    protected Object document;
    protected long lastAccessed;
    protected long timeToLive = 15 * 60 * 1000; // in seconds, 15 minutes
    
    private TransformCacheEntry()
    {
    }

    /**
     * Constructs a TransformCacheEntry object
     *
     * @param key 
     * @param timeToLive seconds to keep this in the cache
     * @param document The user specific content being cached
     */
    public TransformCacheEntry(String key, Object document, long timeToLive)
    {
        this.key = key;
        this.document = document;
        this.timeToLive = timeToLive;
        this.lastAccessed = new Date().getTime();
    }


    /**
     * Set the cache's last accessed stamp
     *
     * @param lastAccessed the cache's last access stamp
     */
    public void setLastAccessed(long lastAccessed)
    {
        this.lastAccessed = lastAccessed;
    }

    /**
     * Get the cache's lastAccessed stamp
     *
     * @return the cache's last accessed stamp
     */
    public long getLastAccessed()
    {
        return this.lastAccessed;
    }

    /**
     * Set the Document in the cache
     *
     * @param document the document being cached
     */
    public void setDocument(Object document)
    {
        this.document = document;
    }

    /**
     * Get the Document
     *
     * @return the document being cached
     */
    public Object getDocument()
    {
        return this.document;
    }
    

    /**
     * @return Returns the key.
     */
    public String getKey()
    {
        return key;
    }
    /**
     * @param key The key to set.
     */
    public void setKey(String key)
    {
        this.key = key;
    }
    /**
     * @return Returns the timeToLive.
     */
    public long getTimeToLive()
    {
        return timeToLive;
    }
    /**
     * @param timeToLive The timeToLive in seconds
     */
    public void setTimeToLive(long timeToLive)
    {
        this.timeToLive = timeToLive;
    }
}
