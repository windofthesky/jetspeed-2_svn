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

import java.util.Comparator;
import java.util.Observer;


/**
 * TransformCache
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface TransformCache extends Observer, Comparator
{
    /**
     * Get the maximum size of the cache 
     *
     * @return the current maximum size of the cache
     */
    public int getMaxSize();

    /**
     * Set the new maximum size of the cache 
     *
     * @param maxSize the maximum size of the cache
     */
    public void setMaxSize(int maxSize);
    
    /**
     * Get the eviction percentage of the cache 
     *
     * @return the eviction percentage of the cache
     */
    public int getEvictionPercentage();
    
    /**
     * Find out if TransformCache is enables 
     *
     * @return the enable flag of the cache
     */
    public boolean isEnabled();


    /**
     * Put a value in the TransformCache keyed off with the TransformId and the
     * DocumentId.
     * @param key
     * @param document
     */
    public void put(String key, Object document, long timeToLive);

    /**
     * Remove a unique value keyed off with the TransformId and DocumentId from the
     * cache.
     * @param key
     * @return Object
     */
    public Object remove(String key);

    /**
     * Retrieve the unique TransformCacheEntry keyed off with key
     * @param key
     * @return TransformCacheEntry
     */
    public TransformCacheEntry get(String key);

    /**
     * Retrieve the byte[] storing the transformed content for the transfomId
     * and the documentId combination.
     * @param transformId
     * @param documentId
     * @return Object
     */
    public Object getDocument(String key);

    /**
     * Construct the key for the TransformDocumentTreeMap cache
     * @return String
     */
    public String constructKey(String url, String stylesheet);

    
    /**
     * Clear the Transform Cache
     */
    public void clearCache();

}
