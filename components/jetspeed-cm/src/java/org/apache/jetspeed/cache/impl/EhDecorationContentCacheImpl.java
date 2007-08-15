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

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.ContentCacheElement;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.ContentCacheKeyGenerator;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.cache.impl.JetspeedContentCacheKey;
import org.apache.jetspeed.decoration.Theme;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.om.page.ContentPage;

/**
 * Wrapper around actual cache implementation
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class EhDecorationContentCacheImpl extends EhCacheImpl implements JetspeedContentCache, JetspeedCacheEventListener
{

	JetspeedCache preferenceCache = null;
    ContentCacheKeyGenerator keyGenerator = null;    

    public EhDecorationContentCacheImpl(Cache ehcache, JetspeedCache preferenceCache, ContentCacheKeyGenerator keyGenerator)
    {
        this(ehcache);
        this.preferenceCache = preferenceCache;
        this.keyGenerator = keyGenerator;
        preferenceCache.addEventListener(this,false); //only listen to remote events
    }
    
    public EhDecorationContentCacheImpl(Cache ehcache, JetspeedCache preferenceCache)
    {
        this(ehcache);
        this.preferenceCache = preferenceCache;
        preferenceCache.addEventListener(this,false); //only listen to remote events
    }
        
    public EhDecorationContentCacheImpl(Cache ehcache)
    {
        super(ehcache);
    }

    public EhDecorationContentCacheImpl(Cache ehcache, ContentCacheKeyGenerator keyGenerator)
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


    public static final String KEY_THEME_KEY = 
        EhDecorationContentCacheElementImpl.KEY_SEPARATOR + "theme" + EhDecorationContentCacheElementImpl.KEY_SEPARATOR ;
	public static final int KEY_THEME_KEY_LENGTH = KEY_THEME_KEY.length();
    
	public void notifyElementRemoved(JetspeedCache cache, boolean local,
            Object key, Object element)
    {
        if (local) return; // not interested in local changes

        // System.out.println("notifying DecorationContent that element " +
        // key.toString() + " has been removed");
        if (!(key instanceof String)) return;
        String s = (String) key;
        if (!(s.startsWith(KEY_THEME_KEY))) return;
        StringTokenizer st = new StringTokenizer(s,
                EhDecorationContentCacheElementImpl.KEY_SEPARATOR);
        int count = 0;
        String te = null;
        String user = null;
        while (st.hasMoreTokens())
        {
            String temp = st.nextToken();
            switch (count)
            {
            case 0:
                break;
            case 1:
                te = temp; 
                break;
            case 2:
                user = temp;
                break;
            }
            count++;
            if (count > 2) break;
        }
        if (te != null)
        {
            removeUserEntry(user, "theme", te);     
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
        return new EhDecorationContentCacheElementImpl(element, cckey);
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
        ContentCacheKey cckey = (ContentCacheKey)key;        
        return ehcache.isKeyInCache(cckey.getKey());
    }

    public void put(CacheElement element)
    {
        ContentCacheElement ccElement = (ContentCacheElement)element;
        EhDecorationContentCacheElementImpl impl = (EhDecorationContentCacheElementImpl)element;
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
        return new EhDecorationContentCacheElementImpl(cachedElement, cckey);
    }

    public boolean remove(Object key)
    {
        CacheElement element = this.get(key);
        boolean removed = false;
        if (element == null)
            return false;
        
        ContentCacheElement ccElement = (ContentCacheElement)element;
        EhDecorationContentCacheElementImpl impl = (EhDecorationContentCacheElementImpl)element;
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
        ContentPage page = context.getPage();
        ContentCacheKey themeContentCacheKey = createCacheKey(context, page.getId());
        CacheElement themeCacheElem = get(themeContentCacheKey);
        
        if (themeCacheElem != null)
        {
            Theme theme = (Theme) themeCacheElem.getContent();
            theme.setInvalidated(true);
        }
    }
}