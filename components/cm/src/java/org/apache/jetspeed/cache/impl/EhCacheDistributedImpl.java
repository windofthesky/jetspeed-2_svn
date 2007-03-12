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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.RegisteredEventListeners;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.cache.JetspeedCache;

public class EhCacheDistributedImpl extends EhCacheImpl implements JetspeedCache, CacheEventListener
{


	private Map refList = Collections.synchronizedMap(new HashMap());


	public EhCacheDistributedImpl(Cache ehcache)
	{
		super(ehcache);
		RegisteredEventListeners listeners = ehcache
				.getCacheEventNotificationService();
		listeners.registerListener(this);

	}

	public CacheElement get(Object key)
	{
		return get((Serializable)key);
	}


	public CacheElement get(Serializable key)
	{
		Element element = ehcache.get(key);
		if (element == null)
			return null;
		return new EhCacheDistributedElementImpl(element);
	}


	public boolean isKeyInCache(Object key)
	{
		if ((key == null) || (!(key instanceof Serializable)))
			return false;
		return ehcache.isKeyInCache(key);
	}

	public boolean isKeyInCache(Serializable key)
	{
		return ehcache.isKeyInCache(key);
	}

	public void put(CacheElement element)
	{
		EhCacheDistributedElementImpl impl = (EhCacheDistributedElementImpl) element;
		ehcache.put(impl.getImplElement());
		refList.put(impl.getKey(), impl);
		notifyListeners(true, CacheElement.ActionAdded,impl.getKey(),impl.getContent());
	}

	public CacheElement createElement(Object key, Object content)
	{
		return new EhCacheDistributedElementImpl((Serializable)key, (DistributedCacheObject)content);
	}

	public CacheElement createElement(Serializable key, DistributedCacheObject content)
	{
		return new EhCacheDistributedElementImpl(key, content);
	}


	public boolean remove(Object key)
	{
		return remove ((Serializable) key);
	}

	public boolean remove(Serializable key)
	{
		Element element = ehcache.get(key);
		refList.remove(key);
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
		refList.remove(key);
		if (element == null)
			return false;
		return ehcache.removeQuiet(key);

	}

	public void evictContentForUser(String user)
	{
		return;
	}

	public String createCacheKey(String primary, String secondary)
	{
		return primary;
	}

	public Object clone() throws CloneNotSupportedException
	{
		return null;
	}
	
   public void dispose()
    {
		if (refList != null)
		{
			Map temp = refList;
			refList = null;
			temp.clear();
		}
		else 
			return;
		if (this.ehcache != null)
		{
			ehcache = null;
		}
    }
	
	public void notifyElement( Ehcache cache, boolean local,Element arg1, int action)
	{
		if (cache != this.ehcache)
		{
			System.out.println ("Cache=" + cache.getName() + " is not my cache=" + this.ehcache.getName());
			return;
		}
		try
		{
			EhCacheDistributedElementImpl e = (EhCacheDistributedElementImpl) refList
					.get(arg1.getKey());
			if (e != null)
			{
				if (action < 0)
					refList.remove(arg1.getKey());
				else if (action == CacheElement.ActionAdded)
					refList.put(arg1.getKey(), arg1);
				e.notifyChange(action);
				notifyListeners(local, action,arg1.getKey(),arg1.getObjectValue());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void notifyElementEvicted(Ehcache cache, Element arg1)
	{
			notifyElement(cache, false, arg1,CacheElement.ActionEvicted);
	}

	public void notifyElementExpired(Ehcache cache, Element arg1)
	{
		notifyElement(cache, false, arg1,CacheElement.ActionExpired);
	}

	public void notifyElementPut(Ehcache cache, Element arg1)
			throws CacheException
	{
		
			notifyElement(cache, false, arg1, CacheElement.ActionAdded);
	}

	public void notifyElementRemoved(Ehcache cache, Element arg1)
			throws CacheException
	{
		notifyElement(cache, false, arg1,CacheElement.ActionRemoved);
	}

	public void notifyElementUpdated(Ehcache cache, Element arg1)
			throws CacheException
	{
		notifyElement(cache, false,arg1,CacheElement.ActionChanged);
	}
	public void notifyRemoveAll(Ehcache cache)
	{
		if (cache != this.ehcache)
		{
			System.out.println ("Cache=" + cache.getName() + " is not my cache=" + this.ehcache.getName());
			return;
		}
		try
		{
			Iterator it = refList.entrySet().iterator();
			while (it.hasNext())
			{
				EhCacheDistributedElementImpl e = (EhCacheDistributedElementImpl)it.next();
				notifyListeners(false, CacheElement.ActionRemoved,e.getKey(),e);
				e.notifyChange(CacheElement.ActionRemoved);
			}
			refList.clear();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	
	}

}
