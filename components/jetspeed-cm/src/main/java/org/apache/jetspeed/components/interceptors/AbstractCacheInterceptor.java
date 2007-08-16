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

import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.jetspeed.cache.general.GeneralCache;

/**
 * <p>
 * AbstractCacheInterceptor
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public abstract class AbstractCacheInterceptor implements Interceptor, MethodInterceptor, Advice
{

    protected GeneralCache cache;
    protected String uniquePrefix;
    
    /**
     *  
     */
    public AbstractCacheInterceptor( GeneralCache cache, String uniquePrefix )
    {
        super();
        this.cache = cache;
        this.uniquePrefix = uniquePrefix;
    }
    
    /**
     * 
     * @param cache
     */
    public AbstractCacheInterceptor( GeneralCache cache )
    {
        this(cache, null);
    }


    /**
     * 
     * <p>
     * buildKey
     * </p>
     *
     * @param clazz
     * @param method
     * @param arg0
     * @return
     */
    public static final String buildKey( String uniquePrefix, String arg0 )
    {
        return uniquePrefix + ":" + arg0 ;
    }
        

    /**
     * 
     * <p>
     * invoke
     * </p>
     *
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     * @param mi
     * @return
     * @throws Throwable
     */
    public Object invoke( MethodInvocation mi ) throws Throwable
    {
        Object[] args = mi.getArguments();
        Method method = mi.getMethod();
        if (args == null)
        {
            throw new IllegalArgumentException(method.getDeclaringClass() + "." + method.getName()
                    + "() receives no arguments.  "
                    + "CacheInterceptor can only intercept methods that have at least (1) argument.");
        }        
        
        Object arg0 = args[0];
        if(arg0 == null)
        {
            throw new IllegalArgumentException("CacheInterceptor requires that the first argument passed to a cached be non-null");
        }
        
        String prefix = null;
        if(uniquePrefix != null)
        {
            prefix = buildKey(uniquePrefix, arg0.toString());
        }
        else
        {
            prefix = buildKey(mi.getMethod().getDeclaringClass().getName(), arg0.toString()); 
        }
        
        return doCacheOperation(mi, prefix);       
    }
    
    protected abstract Object doCacheOperation( MethodInvocation mi, String uniqueKey ) throws Throwable;

}
