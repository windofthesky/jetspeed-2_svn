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

import org.apache.jetspeed.cache.ContentCacheKeyGenerator;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.UserContentCacheManager;

/**
 * @version $Id$
 *
 */
public class UserContentCacheManagerImpl implements UserContentCacheManager
{
    private JetspeedCache portletContentCache;
    private JetspeedCache decorationContentCache;
    private ContentCacheKeyGenerator generator;

    public UserContentCacheManagerImpl(JetspeedCache portletContentCache, JetspeedCache decorationContentCache, ContentCacheKeyGenerator generator)
    {
        this.portletContentCache = portletContentCache;
        this.decorationContentCache = decorationContentCache;
        this.generator = generator;
    }
    
    public void evictUserContentCache(String userName, String sessionId)
    {
        if (generator.isCacheBySessionId())
        {
            portletContentCache.evictContentForSession(sessionId);
            
            if (decorationContentCache != null)
            {
                decorationContentCache.evictContentForSession(sessionId);
            }
        }
        else
        {
            portletContentCache.evictContentForUser(userName);
            
            if (decorationContentCache != null)
            {
                decorationContentCache.evictContentForUser(userName);            
            }
        }
    }
}
