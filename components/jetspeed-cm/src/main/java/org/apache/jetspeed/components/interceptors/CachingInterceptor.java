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
package org.apache.jetspeed.components.interceptors;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.jetspeed.cache.general.GeneralCache;

/**
 * <p>
 * CacheInterceptor
 * </p>
 * <p>
 *  AoP Interceptor that can be used for generalized caching.  The only requirement is
 *  that intercepted methods must receive at least one (1) arguments.
 *  <br /> <br />  
 *  CacheInterceptor ALWAYS use the first argument in the method to build the unique cache key. 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class CachingInterceptor extends AbstractCacheInterceptor
{

   
    
    /**
     * @param cache
     */
    public CachingInterceptor( GeneralCache cache )
    {
        super(cache);
    }

    /**
     * <p>
     * doCacheOperation
     * </p>
     *
     * @param mi
     * @param uniqueKey
     * @return
     * @throws Throwable
     */
    protected Object doCacheOperation( MethodInvocation mi, String uniqueKey ) throws Throwable
    {
        if(cache.contains(uniqueKey))
        {
            return cache.get(uniqueKey);
        }
        else
        {
            Object value = mi.proceed();
            if(value != null)
            {
                cache.put(uniqueKey, value);
            }
            
            return value;
        }
    }
}