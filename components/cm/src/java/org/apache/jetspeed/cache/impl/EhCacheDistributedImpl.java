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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.RegisteredEventListeners;

import org.apache.jetspeed.cache.DistributedCacheElement;
import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.cache.DistributedJetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;

public class EhCacheDistributedImpl implements DistributedJetspeedCache, CacheEventListener
{
	private Cache ehcache;
	private Map refList = Collections.synchronizedMap(new HashMap());
    protected List listeners = new ArrayList();

	public EhCacheDistributedImpl(Cache ehcache)
	{
		this.ehcache = ehcache;
		RegisteredEventListeners listeners = ehcache
				.getCacheEventNotificationService();
		listeners.registerListener(this);

	}

	public DistributedCacheElement get(Serializable key)
	{
		Element element = ehcache.get(key);
		if (element == null)
			return null;
		return new EhCacheDistributedElementImpl(element);
	}

	public void clear()
	{
		ehcache.removeAll();
	}
	public int getTimeToIdleSeconds()
	{
		return (int) ehcache.getTimeToIdleSeconds();
	}

	public int getTimeToLiveSeconds()
	{
		return (int) ehcache.getTimeToLiveSeconds();
	}

	public boolean isKeyInCache(Serializable key)
	{
		return ehcache.isKeyInCache(key);
	}

	public void put(DistributedCacheElement element)
	{
		EhCacheDistributedElementImpl impl = (EhCacheDistributedElementImpl) element;
		ehcache.put(impl.getImplElement());
		refList.put(impl.getKey(), impl);
	}

	public DistributedCacheElement createElement(Serializable key, DistributedCacheObject content)
	{
		return new EhCacheDistributedElementImpl(key, content);
	}

	public boolean remove(Serializable key)
	{
		Element element = ehcache.get(key);
		refList.remove(key);
		if (element == null)
			return false;
		return ehcache.remove(key);
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
	
    public void addEventListener(JetspeedCacheEventListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeEventListener(JetspeedCacheEventListener listener)
    {
        listeners.remove(listener);
    }

	
	public void notifyElement(Element arg1, int action)
	{
		try
		{
			EhCacheDistributedElementImpl e = (EhCacheDistributedElementImpl) refList
					.get(arg1.getKey());
			if (e != null)
			{
				if (action < 0)
					refList.remove(arg1.getKey());
				e.notifyChange(action);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void notifyElementEvicted(Ehcache cache, Element arg1)
	{
		try
		{
			notifyElement(arg1, DistributedCacheElement.ActionEvicted);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void notifyElementExpired(Ehcache cache, Element arg1)
	{
		try
		{
			notifyElement(arg1, DistributedCacheElement.ActionExpired);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void notifyElementPut(Ehcache cache, Element arg1)
			throws CacheException
	{
		try
		{
			notifyElement(arg1, DistributedCacheElement.ActionAdded);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void notifyElementRemoved(Ehcache cache, Element arg1)
			throws CacheException
	{
		try
		{
			EhCacheDistributedElementImpl e = (EhCacheDistributedElementImpl) refList
					.get(arg1.getKey());
			if (e != null)
			{
				refList.remove(arg1.getKey());
				e.notifyChange(DistributedCacheElement.ActionRemoved);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void notifyElementUpdated(Ehcache cache, Element arg1)
			throws CacheException
	{
		try
		{
			notifyElement(arg1, DistributedCacheElement.ActionChanged);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void notifyRemoveAll(Ehcache cache)
	{
//		if (ehcache == cache)
//			System.out.println("notifyRemoveAll cache=" + cache.getName());
//		else
//			System.out.println("NOT MINE 	notifyRemoveAll cache="
//					+ cache.getName());
	}

}
