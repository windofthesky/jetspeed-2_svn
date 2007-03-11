/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 * Created on Oct 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.cache.general;

/**
 * <p>
 * InvocationCountingCache
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class InvocationCountingCache extends SimpleHashMapCache
{
    int getCount, putCount, removeCount, successGetCount, containsCount;
   

    public Object get( String key )
    {
        getCount++;
        
        Object value =  super.get(key);
        if(value != null)
        {
            successGetCount++;
        }
        
        return value;
    }
    
    public void put( String key, Object value )
    {
        putCount++;
        super.put(key, value);
    }
    
    public Object remove( String key )
    {
        removeCount++;
        return super.remove(key);
    }
    
    public boolean contains( String key )
    {
        containsCount++;
        return super.contains(key);
    }
}
