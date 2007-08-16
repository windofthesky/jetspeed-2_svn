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
package org.apache.jetspeed.cache.general;

import java.util.HashMap;

/**
 * <p>
 * SimpleHashMapCache
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class SimpleHashMapCache implements GeneralCache
{
    
    protected HashMap cache;

    /**
     * 
     */
    public SimpleHashMapCache()
    {
        super();
        cache = new HashMap();        
    }

    /**
     * <p>
     * get
     * </p>
     *
     * @see org.apache.jetspeed.cache.general.GeneralCache#get(java.lang.String)
     * @param key
     * @return
     */
    public Object get( String key )
    {
       return cache.get(key);
    }

    /**
     * <p>
     * put
     * </p>
     *
     * @see org.apache.jetspeed.cache.general.GeneralCache#put(java.lang.String, java.lang.Object)
     * @param key
     * @param value
     */
    public void put( String key, Object value )
    {
       cache.put(key, value);

    }

    /**
     * <p>
     * contains
     * </p>
     *
     * @see org.apache.jetspeed.cache.general.GeneralCache#contains(java.lang.String)
     * @param key
     * @return
     */
    public boolean contains( String key )
    {
        return cache.containsKey(key);
    }

    /**
     * <p>
     * remove
     * </p>
     *
     * @see org.apache.jetspeed.cache.general.GeneralCache#remove(java.lang.String)
     * @param key
     */
    public Object remove( String key )
    {
       return cache.remove(key);
    }

}
