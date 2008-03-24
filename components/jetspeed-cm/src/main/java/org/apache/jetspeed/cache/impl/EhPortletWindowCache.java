/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.cache.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Ehcache;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.PortletWindowCache;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <p>
 * EhPortletWindowCache
 * </p>
 * <p>
 *  Implementation of {@link PortletWindowCache} that is backed 
 *  <a href="http://ehcache.sourceforge.net/">Ehcache</a>.
 * </p>
 * @author <a href="mailto:scott.t.weaver@gmail.com">Scott T. Weaver</a>
 *
 */
public class EhPortletWindowCache extends EhCacheImpl implements PortletWindowCache {

	 /** Allows us to track {@link PortletWindow}s in cache by {@link PortletEntity#getId()}*/
    private Map portletEntityIdToEntityid;
    
    
	public EhPortletWindowCache(Ehcache ehcache) 
	{
		super(ehcache);
		portletEntityIdToEntityid = new HashMap();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.jetspeed.cache.impl.PortletWindowCache#getPortletWindow(java.lang.String)
	 */
	public PortletWindow getPortletWindow(String windowId)
	{
	    assert windowId != null;
		CacheElement cacheElement = get(windowId);
		if(cacheElement != null)
		{
		   return (PortletWindow) cacheElement.getContent();
		}
		else
		{
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.jetspeed.cache.impl.PortletWindowCache#getPortletWindowByEntityId(java.lang.String)
	 */
	public PortletWindow getPortletWindowByEntityId(String portletEntityId)
	{
	    assert portletEntityId != null;
		if(portletEntityIdToEntityid.containsKey(portletEntityId))
		{
			return (PortletWindow) getPortletWindow((String) portletEntityIdToEntityid.get(portletEntityId));
		}
		else
		{
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.jetspeed.cache.impl.PortletWindowCache#putPortletWindow(org.apache.pluto.om.window.PortletWindow)
	 */
	public void putPortletWindow(PortletWindow window)
	{
	    assert window != null;
		String windowId = window.getId().toString();
		portletEntityIdToEntityid.put(window.getPortletEntity().getId().toString(), windowId);
		put(createElement(windowId, window));
	}
	
	/* (non-Javadoc)
	 * @see org.apache.jetspeed.cache.impl.PortletWindowCache#removePortletWindow(java.lang.String)
	 */
	public void removePortletWindow(String portletWindowId)
	{
	    assert portletWindowId != null;
		PortletWindow window = getPortletWindow(portletWindowId);
		if(window != null)
		{			
			portletEntityIdToEntityid.remove(window.getPortletEntity().getId().toString());
			removeQuiet(portletWindowId);
		}		
	}
	
	public void removePortletWindowByPortletEntityId(String portletEntityId)
	{
	    assert portletEntityId != null;
		PortletWindow portletWindow = getPortletWindowByEntityId(portletEntityId);
		if(portletWindow != null)
		{
		    portletEntityIdToEntityid.remove(portletEntityId);
            removeQuiet(portletWindow.getId().toString());
		}
	}
	
	public Set getAllPortletWindows()
	{		
		Iterator keys = ehcache.getKeys().iterator();
		Set windows = new HashSet();
		while(keys.hasNext())
		{
			String key = (String) keys.next();
			PortletWindow window = getPortletWindow(key);
			if(window != null)
			{
				windows.add(window);
			}			
		}
		return windows;
	}

}
