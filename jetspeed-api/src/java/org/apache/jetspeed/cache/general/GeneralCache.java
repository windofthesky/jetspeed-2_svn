/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.cache.general;

/**
 * <p>
 * GeneralCache
 * </p>
 * <p>
 *  A very general, re-useable interface to wrap or create different caching implementations.
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface GeneralCache
{
    
    /**
     * 
     * <p>
     * get
     * </p>
     *
     * @param key
     * @return
     */
    Object get(String key);
    
    /**
     * 
     * <p>
     * put
     * </p>
     *
     * @param key
     * @param value
     */
    void put(String key, Object value);
    
    /**
     * 
     * <p>
     * contains
     * </p>
     *
     * @param key
     * @return
     */
    boolean contains(String key);
    
    /**
     * 
     * <p>
     * remove
     * </p>
     *
     * @param key
     * @return
     */
    Object remove(String key);
    
    

}
