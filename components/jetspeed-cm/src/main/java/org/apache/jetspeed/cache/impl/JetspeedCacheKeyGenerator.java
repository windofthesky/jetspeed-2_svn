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

import java.util.List;

import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.ContentCacheKeyGenerator;
import org.apache.jetspeed.request.RequestContext;

/**
 * Wrapper around actual cache implementation
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedCacheKeyGenerator implements ContentCacheKeyGenerator
{
    private List segments;
    private boolean cacheBySessionId = true;
        
    public JetspeedCacheKeyGenerator(List segments)
    {
        this.segments = segments;
        this.cacheBySessionId = (segments.contains("sessionid"));            
    }
    
    public ContentCacheKey createCacheKey(RequestContext context, String windowId)
    {
        ContentCacheKey key = new JetspeedContentCacheKey(segments, context, windowId);
        return key;
    }

    public ContentCacheKey createUserCacheKey(String username, String pipeline, String windowId)
    {
        ContentCacheKey key = new JetspeedContentCacheKey();
        key.createFromUser(username, pipeline, windowId);
        return key;
    }

    public ContentCacheKey createSessionCacheKey(String sessionId, String pipeline, String windowId)
    {
        ContentCacheKey key = new JetspeedContentCacheKey();
        key.createFromSession(sessionId, pipeline, windowId);
        return key;
    }
    
    public boolean isCacheBySessionId()
    {
        return cacheBySessionId;
    }
    
    public boolean isCacheByUsername()
    {
        return !cacheBySessionId;        
    }
}
