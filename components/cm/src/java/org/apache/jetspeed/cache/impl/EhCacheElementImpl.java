/* Copyright 2000-2004 The Apache Software Foundation.
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

public class EhCacheElementImpl implements CacheElement
{
	Element element;
	
	public EhCacheElementImpl(Element element)
    {
        this.element = element;
    }

	public EhCacheElementImpl(Serializable key, Serializable value)
    {
        this.element = new Element(key,value);        
    }

    public EhCacheElementImpl(Serializable key, Object value)
    {
        this.element = new Element(key,value);        
    }

	public Object getKey()
	    {
	        return element.getObjectKey();
	    }
	    
	    
	    public Object getContent()
	    {
	        return element.getObjectValue();
	    }

	    public int getTimeToIdleSeconds()
	    {
	        return element.getTimeToIdle();
	    }

	    public int getTimeToLiveSeconds()
	    {
	        return element.getTimeToLive();
	    }

	    public boolean isEternal()
	    {
	        return element.isEternal();
	    }

	    public Element getImplElement()
	    {
	        return element;
	    }

	    public void setEternal(boolean eternal)
	    {
	        element.setEternal(eternal);
	    }

	    public void setTimeToIdleSeconds(int timeToIdle)
	    {
	        element.setTimeToIdle(timeToIdle);
	    }

	    public void setTimeToLiveSeconds(int timeToLive)
	    {
	        element.setTimeToLive(timeToLive);
	    }
}