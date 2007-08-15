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

/**
 * <p>
 *  Provides interface to Jetspeed for content cache key generation
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface ContentCacheKeyGenerator
{
    /**
     * Normalized and pluggable cache key generator
     * 
     * @param context
     * @param windowId The window id of the portlet to be cached.
     * @since 2.1.2
     * @return
     */
    ContentCacheKey createCacheKey(RequestContext context, String windowId);
    
    /**
     * Create a cache key without request context information, but by providing required parameters username and windowid
     * 
     * @param username
     * @param pipeline "desktop" or "portal"
     * @param windowId
     * @return
     */
    ContentCacheKey createUserCacheKey(String username, String pipeline, String windowId);

    /**
     * Create a cache key without request context information, but by providing required parameters sessinid and windowid
     * 
     * @param sessionid
     * @param pipeline "desktop" or "portal"
     * @param windowId
     * @return
     */
    ContentCacheKey createSessionCacheKey(String sessionid, String pipeline, String windowId);
    
    /**
     *  return true if caching is by session id, not username
     *  
     * @return
     */
    boolean isCacheBySessionId();
    
    /**
     *  return true if caching is by username, not sessionid
     *  
     * @return
     */    
    boolean isCacheByUsername();
}