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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.ContentCacheElement;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.ContentCacheKeyGenerator;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.decoration.Theme;
import org.apache.jetspeed.request.RequestContext;

/**
 * Wrapper around actual cache implementation
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class EhPortletContentCacheImpl extends EhCacheImpl implements JetspeedContentCache, JetspeedCacheEventListener
{

	JetspeedCache preferenceCache = null;
    ContentCacheKeyGenerator keyGenerator = null;    

    public EhPortletContentCacheImpl(Ehcache ehcache, JetspeedCache preferenceCache, ContentCacheKeyGenerator keyGenerator)
    {
        this(ehcache);
        this.preferenceCache = preferenceCache;
        this.keyGenerator = keyGenerator;
        preferenceCache.addEventListener(this,false); //only listen to remote events
    }
    
    public EhPortletContentCacheImpl(Ehcache ehcache, JetspeedCache preferenceCache)
    {
        this(ehcache);
        this.preferenceCache = preferenceCache;
        preferenceCache.addEventListener(this,false); //only listen to remote events
    }
        
    public EhPortletContentCacheImpl(Ehcache ehcache)
    {
        super(ehcache);
    }

    public EhPortletContentCacheImpl(Ehcache ehcache, ContentCacheKeyGenerator keyGenerator)
    {
        this(ehcache);
        this.keyGenerator = keyGenerator;
    }
    

	public void notifyElementAdded(JetspeedCache cache, boolean local, Object key, Object element)
	{
	}

	public void notifyElementChanged(JetspeedCache cache, boolean local, Object key, Object element)
	{
	}

	public void notifyElementEvicted(JetspeedCache cache, boolean local, Object key, Object element)
	{
	}

	public void notifyElementExpired(JetspeedCache cache, boolean local, Object key, Object element)
	{
		notifyElementRemoved(cache,local,key,element);
	}


    public static final String KEY_ENTITY_KEY = 
        EhPortletContentCacheElementImpl.KEY_SEPARATOR + "portlet_entity" + EhPortletContentCacheElementImpl.KEY_SEPARATOR ;
	public static final int KEY_ENTITY_KEY_LENGTH = KEY_ENTITY_KEY.length();
    
	public void notifyElementRemoved(JetspeedCache cache, boolean local,
            Object key, Object element)
    {
        if (local) return; // not interested in local changes

        // System.out.println("notifying PortletContent that element " +
        // key.toString() + " has been removed");
        if (!(key instanceof String)) return;
        String s = (String) key;
        if (!(s.startsWith(KEY_ENTITY_KEY))) return;
        StringTokenizer st = new StringTokenizer(s,
                EhPortletContentCacheElementImpl.KEY_SEPARATOR);
        int count = 0;
        String pe = null;
        String user = null;
        while (st.hasMoreTokens())
        {
            String temp = st.nextToken();
            switch (count)
            {
            case 0:
                break;
            case 1:
                pe = temp; 
                break;
            case 2:
                user = temp;
                break;
            }
            count++;
            if (count > 2) break;
        }
        if ((pe != null) && (user != null))
        {
            removeUserEntry(user, "portal", pe);     
            removeUserEntry(user, "desktop", pe);
        }
    }

    void removeUserEntry(String username, String pipeline, String windowId)
    {        
        ContentCacheKey key = keyGenerator.createUserCacheKey(username, pipeline, windowId);
        if (ehcache.remove(key.getKey()))
        {
            Element userElement = ehcache.get(username);
                
            if (userElement != null)
            {
                Map map = (Map)userElement.getObjectValue();
                if (map != null)
                {
                    map.remove(windowId);
                }
            }
        }
    }
    
    public CacheElement get(Object key)
    {
        ContentCacheKey cckey = (ContentCacheKey)key;
        Element element = ehcache.get(cckey.getKey());
        if (element == null)
            return null;
        return new EhPortletContentCacheElementImpl(element, cckey);
    }

    public int getTimeToIdleSeconds()
    {
        return (int)ehcache.getCacheConfiguration().getTimeToIdleSeconds();
    }

    public int getTimeToLiveSeconds()
    {
        return (int)ehcache.getCacheConfiguration().getTimeToLiveSeconds();
    }

    public boolean isKeyInCache(Object key)
    {
        ContentCacheKey cckey = (ContentCacheKey)key;        
        return ehcache.isKeyInCache(cckey.getKey());
    }

    public void put(CacheElement element)
    {
        ContentCacheElement ccElement = (ContentCacheElement)element;
        EhPortletContentCacheElementImpl impl = (EhPortletContentCacheElementImpl)element;
        Element ehl = impl.getImplElement();        
        String userKey = ccElement.getContentCacheKey().getSessionId();
        if (userKey == null)
        {
            userKey = ccElement.getContentCacheKey().getUsername();
        }
        String windowId = ccElement.getContentCacheKey().getWindowId();
        try
        {
            ehcache.put(ehl);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Element userElement = ehcache.get(userKey);
        if (userElement == null)
        {
            Map map = Collections.synchronizedMap(new HashMap());
            map.put(windowId, ccElement.getContentCacheKey());
            userElement = new Element(userKey, map);
            ehcache.put(userElement);           
        }
        else
        {
            Map map = (Map)userElement.getObjectValue();
            map.put(windowId, ccElement.getContentCacheKey());
        }        
    }
    
    public CacheElement createElement(Object key, Object content)
    {
        ContentCacheKey cckey = (ContentCacheKey)key;
        Element cachedElement = new Element(cckey.getKey(), content);        
        return new EhPortletContentCacheElementImpl(cachedElement, cckey);
    }

    public boolean remove(Object key)
    {
        CacheElement element = this.get(key);
        boolean removed = false;
        if (element == null)
            return false;
        
        ContentCacheElement ccElement = (ContentCacheElement)element;
        EhPortletContentCacheElementImpl impl = (EhPortletContentCacheElementImpl)element;
        Element ehl = impl.getImplElement();        
        String userKey = ccElement.getContentCacheKey().getSessionId();
        if (userKey == null)
        {
            userKey = ccElement.getContentCacheKey().getUsername();
        }        
        String windowId = ccElement.getContentCacheKey().getWindowId();        
        removed = ehcache.remove(ccElement.getContentCacheKey().getKey());
        Element userElement = ehcache.get(userKey);
        if (userElement != null)
        {
            Map map = (Map)userElement.getObjectValue();
            if (map != null)
            {
                map.remove(windowId);
            }
        }
        return removed;
    }
        
    public void evictContentForUser(String username)
    {
        Element userElement = saveGet(username);
        if (userElement != null)
        {
            Map map = (Map)userElement.getObjectValue();
            if (map != null)
            {
                Iterator entities = map.values().iterator();
                while (entities.hasNext())
                {
                    ContentCacheKey ccKey = (ContentCacheKey)entities.next();
                    ehcache.remove(ccKey.getKey());
                }
            }
            ehcache.remove(username);
        }
    }

    public void evictContentForSession(String session)
    {
        Element userElement = saveGet(session);
        if (userElement != null)
        {
            Map map = (Map)userElement.getObjectValue();
            if (map != null)
            {
                Iterator entities = map.values().iterator();
                while (entities.hasNext())
                {
                    ContentCacheKey ccKey = (ContentCacheKey)entities.next();
                    ehcache.remove(ccKey.getKey());
                }
            }
            ehcache.remove(session);
        }
    }
    
    public void clear()
    {
        ehcache.removeAll();
    }
        
    public ContentCacheKey createCacheKey(RequestContext context, String windowId)
    {
        return this.keyGenerator.createCacheKey(context, windowId);        
    }
    
    protected Element saveGet(Object key)
    {
        try
        {
            return ehcache.get(key);
        }
        catch (IllegalStateException ise)
        {
            // can be thrown during shutdown for instance
            return null;
        }
    }
    
    public String createSessionKey(RequestContext context)
    {
        boolean isAjaxRequest = (context == null);
        String mode = isAjaxRequest ? "-d-" : "-p-";
        String user = context.getRequest().getRemoteUser();
        if (user == null)
            user = "guest";        
        return user + mode + context.getPage().getId();        
    }
    
    public void invalidate(RequestContext context)
    {
        String themeCacheKey = createSessionKey(context);
        Theme theme = (Theme)context.getRequest().getSession().getAttribute(themeCacheKey);        
        if (theme != null)
        {
            theme.setInvalidated(true);
        }
    }
}