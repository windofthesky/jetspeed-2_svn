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
package org.apache.jetspeed.decoration;

/**
 * Simple caching mechanism for storing pathed that were previously located
 * by a <code>ResourceValidator</code>.  This allows a Decoration to bypass
 * hitting the ResourceValidator repeatedly after a path is already known
 * to exist. 
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 * @see org.apache.jetspeed.decoration.ResourceValidator
 *
 */
public interface PathResolverCache
{
    /**
     * Adds a recolved <code>path</code> to the the cache using
     * its relative path as the <code>key</code>
     * @param key key relative path of the resource.
     * @param path full path to resource
     */
    void addPath(String key, String path);
    
    /**
     * Returns a previously located path using its retlative path
     * as the <code>code</code>.
     *  
     * @param key relative path of the resource.
     * @return full path to resource or <code>null</code> if no resource
     * for the key exists.
     */
    String getPath(String key);
    
    /**
     * Removes a full path to a resource from the cache using its
     * relative path as the <code>key</code>.
     * 
     * @param key
     * @return The full path to the resource or <code>null</code>
     * if the resource path was not cached.
     */
    String removePath(String key);
    
    
    /**
     * Clears the entire contents of this cache object.
     *
     */
    void clear();
    
}
