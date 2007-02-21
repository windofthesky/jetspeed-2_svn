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
package org.apache.jetspeed.cache.impl;

import java.io.Serializable;

import net.sf.ehcache.Element;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.DistributedCacheObject;


public class EhCacheDistributedElementImpl extends EhCacheElementImpl 
{
	public EhCacheDistributedElementImpl(Element element)
    {
        super(element);
    }

	public EhCacheDistributedElementImpl(Serializable key, DistributedCacheObject value)
    {
		super(key, value);
        
    }
	    
    public void notifyChange(int action)
    {
    	((DistributedCacheObject)getContent()).notifyChange(action);
    }

	    
}
