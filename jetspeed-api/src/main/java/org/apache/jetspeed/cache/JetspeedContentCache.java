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
 *  Provides interface to Jetspeed for cache related activities
 *  Abstraction around actual cache implementation
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface JetspeedContentCache extends JetspeedCache
{
    /**
     * Creates a session key used to store associated information in the session.
     * 
     * @param context
     * @return
     */
    String createSessionKey(RequestContext context);
    
    /** 
     * remove from the cache and invalidate any associated caches or session attributes
     * 
     * @param context
     * @param key
     */
    void invalidate(RequestContext context);
}