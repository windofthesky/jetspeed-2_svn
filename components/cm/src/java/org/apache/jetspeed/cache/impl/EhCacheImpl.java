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
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;

public class EhCacheImpl implements JetspeedCache, CacheEventListener
{
    protected Cache ehcache;
    protected List listeners = new ArrayList();
    
    public EhCacheImpl(Cache ehcache)
    {
        this.ehcache = ehcache;
        ehcache.getCacheEventNotificationService().registerListener(this);
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
        return ehcache.remove(key);
    }

    public void clear()
    {
        ehcache.removeAll();
    }
    
    public void evictContentForUser(String user)
    {
    	return;
    }
    
    public String createCacheKey(String primary, String secondary)
    {
        return primary;
    }

    public void addEventListener(JetspeedCacheEventListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeEventListener(JetspeedCacheEventListener listener)
    {
        listeners.remove(listener);
    }
   
    // ------------------------------------------------------
    
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public void dispose()
    {
    }

    public void notifyElementEvicted(Ehcache c, Element element)
    {
    }

    public void notifyElementExpired(Ehcache c, Element element)
    {
    }

    public void notifyElementPut(Ehcache c, Element element) throws CacheException
    {
    }

    public void notifyElementRemoved(Ehcache c, Element element) throws CacheException
    {
        for (int ix = 0; ix < listeners.size(); ix++)
        {
            //System.out.println("## element is " + element.getObjectValue().getClass());
            ((JetspeedCacheEventListener)listeners.get(ix)).notifyElementRemoved(this, element.getObjectValue());
        }
    }

    public void notifyElementUpdated(Ehcache c, Element element) throws CacheException
    {
    }

    public void notifyRemoveAll(Ehcache c)
    {
    }

}
