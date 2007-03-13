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
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;

/**
 * Wrapper around actual cache implementation
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class EhPortletContentCacheImpl extends EhCacheImpl implements JetspeedCache, JetspeedCacheEventListener
{
	
    public static final String KEY_ENTITY_KEY = EhPortletContentCacheElementImpl.KEY_SEPARATOR + "portlet_entity" + EhPortletContentCacheElementImpl.KEY_SEPARATOR ;
    public static final int KEY_ENTITY_KEY_LENGTH = KEY_ENTITY_KEY.length();

	   JetspeedCache preferenceCache = null;

	   public void notifyElementAdded(JetspeedCache cache, boolean local, Object key, Object element)
	{
		// TODO Auto-generated method stub
		
	}

	public void notifyElementChanged(JetspeedCache cache, boolean local, Object key, Object element)
	{
		// TODO Auto-generated method stub
		
	}

	public void notifyElementEvicted(JetspeedCache cache, boolean local, Object key, Object element)
	{
		// TODO Auto-generated method stub
		
		notifyElementRemoved(cache,local,key,element);
	}

	public void notifyElementExpired(JetspeedCache cache, boolean local, Object key, Object element)
	{
		// TODO Auto-generated method stub
		notifyElementRemoved(cache,local,key,element);
		
	}

	public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
		{
		   if (local)
			   return; //not interested in local changes
			   
		   	//System.out.println("notifying PortletContent that element " + key.toString() + " has been removed");		   
			if (!(key instanceof String))
				return;
			String s = (String) key;
			if (!(s.startsWith(KEY_ENTITY_KEY)))
				return;
			StringTokenizer st = new StringTokenizer(s,EhPortletContentCacheElementImpl.KEY_SEPARATOR);
			int count = 0;
			String pe = null; String user = null;
		     while (st.hasMoreTokens()) 
		     {
		    	 String temp = st.nextToken();
		    	 switch (count)
		    	 {
		    		 case 0: 
		    			 break;
		    		 case 1: 	pe = temp;
		    	 		break;
		    		 case 2: 	user = temp;
		    		 break;
		    	 }
		    	 count++;
		    	 if (count> 2)
		    		 break;
		     }
			 if ((pe != null) && (user != null))
				 removeOneEntry(user,pe);
		}
	    
	public EhPortletContentCacheImpl(Cache ehcache,JetspeedCache preferenceCache )
    {
        this(ehcache);
        this.preferenceCache = preferenceCache;
        preferenceCache.addEventListener(this,false); //only listen to remote events
    }

	    
    public EhPortletContentCacheImpl(Cache ehcache)
    {
        super(ehcache);
    }

    public CacheElement get(Object key)
    {
        Element element = ehcache.get(key);
        if (element == null)
            return null;
        return new EhPortletContentCacheElementImpl(element);
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
        EhPortletContentCacheElementImpl impl = (EhPortletContentCacheElementImpl)element;
        Element ehl = impl.getImplElement();
        String userKey = impl.getUserKey();
        String entity = impl.getEntityKey();
        ehcache.put(ehl);
        Element userElement = ehcache.get(userKey);
        if (userElement == null)
        {
            Map map = Collections.synchronizedMap(new HashMap());
            map.put(entity, entity);
            userElement = new Element(userKey, map);
            ehcache.put(userElement);           
        }
        else
        {
            Map map = (Map)userElement.getObjectValue();
            map.put(entity, entity);
        }        
    }
    
    public CacheElement createElement(Object key, Object content)
    {
        Element cachedElement = new Element(key, content);        
        return new EhPortletContentCacheElementImpl(cachedElement);
    }

    public boolean remove(Object key)
    {
        CacheElement element = this.get(key);
        boolean removed = false;
        if (element == null)
            return false;
        removed = ehcache.remove(key);
        EhPortletContentCacheElementImpl impl = (EhPortletContentCacheElementImpl)element;
        String userKey = impl.getUserKey();
        String entity = impl.getEntityKey();
        Element userElement = ehcache.get(userKey);
        if (userElement != null)
        {
            Map map = (Map)userElement.getObjectValue();
            if (map != null)
            {
                map.remove(entity);
            }
        }
        return removed;
    }
    
    public void removeOneEntry(String pe, String user)
    {
        String key = createCacheKey(pe,user);
        if (ehcache.remove(key))
        {
        	Element userElement = ehcache.get(user);
	        	
	        if (userElement != null)
	        {
	            Map map = (Map)userElement.getObjectValue();
	            if (map != null)
	            {
	            	map.remove(pe);
	            }
	        }
        }
    }
    
    public void evictContentForUser(String user)
    {
        Element userElement = ehcache.get(user);
        if (userElement != null)
        {
            Map map = (Map)userElement.getObjectValue();
            if (map != null)
            {
                Iterator entities = map.keySet().iterator();
                while (entities.hasNext())
                {
                    String entity = (String)entities.next();
                    String key = createCacheKey(user, entity);
                    ehcache.remove(key);
                }
            }
            ehcache.remove(user);
        }
    }
    
    public void clear()
    {
        ehcache.removeAll();
    }
    
    public String createCacheKey(String primary, String secondary)
    {
        return primary + EhPortletContentCacheElementImpl.KEY_SEPARATOR + secondary;
    }

}