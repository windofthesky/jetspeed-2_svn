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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.RegisteredEventListeners;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.apache.jetspeed.request.RequestContext;

public class EhCacheDistributedImpl extends EhCacheImpl implements JetspeedCache, CacheEventListener
{

    protected List localListeners = new ArrayList();
    protected List remoteListeners = new ArrayList();

	private Map refList = Collections.synchronizedMap(new HashMap());
	private boolean removeAllLocal = false;


	public EhCacheDistributedImpl(Ehcache ehcache)
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
        if (!(key instanceof Serializable))
        {
            throw new IllegalArgumentException("The cache key must be serializable.");
        }
        if (!(content instanceof DistributedCacheObject))
        {
            throw new IllegalArgumentException("The cache content must be a distributed cache object.");
        }
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

    public void clear()
    {
        // invoke removeAll with local flag set
        synchronized (refList)
        {
            removeAllLocal = true;
            super.clear();
            removeAllLocal = false;
        }
        notifyListeners(true, CacheElement.ActionRemoved,null,null);
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

	public void evictContentForUser(RequestContext context)
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
					refList.put(arg1.getKey(), new EhCacheDistributedElementImpl(arg1));
				e.notifyChange(action);
	            notifyListeners(local, action, arg1.getKey(), arg1.getObjectValue());
			}
			else
			{
	            notifyListeners(local, action, arg1.getKey(), null);
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
		    // synchronize on refList to ensure exclusive
		    // operation on refList and removeAllLocal flag
		    synchronized (refList)
		    {
		        // notify all listeners of element removal
		        // and each element of its removal
		        Iterator it = refList.values().iterator();
		        while (it.hasNext())
		        {
		            EhCacheDistributedElementImpl e = (EhCacheDistributedElementImpl)it.next();
		            notifyListeners(removeAllLocal, CacheElement.ActionRemoved,e.getKey(),e.getContent());
		            e.notifyChange(CacheElement.ActionRemoved);
		        }
		        refList.clear();
		    }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
