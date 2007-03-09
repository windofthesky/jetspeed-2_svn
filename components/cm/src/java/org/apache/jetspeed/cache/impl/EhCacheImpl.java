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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;

public class EhCacheImpl implements JetspeedCache
{
    protected Cache ehcache;
    protected List localListeners = new ArrayList();
    protected List remoteListeners = new ArrayList();
    
    public EhCacheImpl(Cache ehcache)
    {
        this.ehcache = ehcache;
     }

    public CacheElement get(Object key)
    {
        Element element = ehcache.get(key);
        if (element == null)
            return null;
        return new EhCacheElementImpl(element);
    }

    public int getTimeToIdleSeconds()
    {
        return (int)ehcache.getTimeToIdleSeconds();
    }

    public int getTimeToLiveSeconds()
    {
        return (int)ehcache.getTimeToLiveSeconds();
    }

    public boolean isKeyInCache(Object key)
    {
        return ehcache.isKeyInCache(key);
    }

    
    public void put(CacheElement element)
    {
    	EhCacheElementImpl impl = (EhCacheElementImpl)element;
        ehcache.put(impl.getImplElement());
		notifyListeners(true, CacheElement.ActionAdded,impl.getKey(),impl.getContent());
    }
    
    public CacheElement createElement(Object key, Object content)
    {
    	if (!((key instanceof Serializable) &&   (content instanceof Serializable)))
    		return null;
   	    return new EhCacheElementImpl((Serializable)key, (Serializable)content);
    }

    public boolean remove(Object key)
    {
        Element element = ehcache.get(key);
        if (element == null)
            return false;
        boolean isRemoved = ehcache.remove(key);
        if (isRemoved)
    		notifyListeners(true, CacheElement.ActionRemoved,key,null);
        return isRemoved;
    }
    
    public boolean removeQuiet(Object key)
    {
        Element element = ehcache.get(key);
        if (element == null)
            return false;
        return ehcache.removeQuiet(key);
    }   

    public void clear()
    {
        ehcache.removeAll();
		notifyListeners(true, CacheElement.ActionRemoved,null,null);
    }
    
    public void evictContentForUser(String user)
    {
    	return;
    }
    
    public String createCacheKey(String primary, String secondary)
    {
        return primary;
    }

    public void addEventListener(JetspeedCacheEventListener listener, boolean local)
    {
    	if (local)
    		localListeners.add(listener);
    	else
    		remoteListeners.add(listener);
    		
    }
    
    public void removeEventListener(JetspeedCacheEventListener listener, boolean local)
    {
        if (local)
        	localListeners.remove(listener);
        else
        	remoteListeners.remove(listener);
        	
    }
   
    // ------------------------------------------------------
    
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public void dispose()
    {
    }

    protected void notifyListeners(boolean local, int action, Object key, Object value)
    {
    	List listeners = (local?localListeners:remoteListeners);
        for (int ix = 0; ix < listeners.size(); ix++)
        {
        	try
        	{
        		JetspeedCacheEventListener listener = (JetspeedCacheEventListener)listeners.get(ix);
        		switch (action)
        		{
        			case CacheElement.ActionAdded:
        				listener.notifyElementAdded(this,local, key,value);
        				break;
        			case CacheElement.ActionChanged:
        				listener.notifyElementChanged(this,local, key,value);
        				break;
        			case CacheElement.ActionRemoved:
        				listener.notifyElementRemoved(this,local, key,value);
        				break;
        			case CacheElement.ActionEvicted:
        				listener.notifyElementEvicted(this,local, key,value);
        				break;
        			case CacheElement.ActionExpired:
        				listener.notifyElementExpired(this,local, key,value);
        				break;
        		}
        	}
        	catch (Exception e)
        	{
        		e.printStackTrace();
        		
        	}
        }
    	
    	
    	
    }

}
