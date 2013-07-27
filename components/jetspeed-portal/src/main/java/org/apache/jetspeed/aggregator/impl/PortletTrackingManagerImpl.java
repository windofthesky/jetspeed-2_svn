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
package org.apache.jetspeed.aggregator.impl;

import org.apache.commons.lang.BooleanUtils;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletTrackingInfo;
import org.apache.jetspeed.aggregator.PortletTrackingManager;
import org.apache.jetspeed.aggregator.RenderTrackable;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks out of service status for portlets
 *  
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletTrackingManagerImpl implements PortletTrackingManager
{
    /**
     * The out of service cache. This cache is keyed off of full portlet name <tt>portletApp::portletName</tt> which
     * holds as its element a list of windows id strings
     */
    protected final JetspeedCache trackingCache;

    /**
     * Holds failed window ids and their failure counts
     */
    protected Map<String,Integer> trackingCounts = new HashMap<String,Integer>();

    /**
     * when rendering a portlet, the default timeout period in milliseconds
     * setting to zero will disable (no timeout) the timeout
     *  
     */
    protected long defaultPortletTimeout; 
    
    /**
     * Out of service limit, if a portlet entity times out past its limit (or default limit) n consecutive times, 
     * it is taken out of service
     */
    protected int outOfServiceLimit;
    
    public PortletTrackingManagerImpl(long defaultPortletTimeout, int outOfServiceLimit, JetspeedCache trackingCache)
    {
        this.defaultPortletTimeout = defaultPortletTimeout;
        this.outOfServiceLimit = outOfServiceLimit;
        this.trackingCache = trackingCache;
    }
    
    public long getDefaultPortletTimeout()
    {
        return this.defaultPortletTimeout;
    }

    public boolean exceededTimeout(long renderTime, PortletWindow window)
    {
        if (!isEnabled()) {
            return false;
        }

        RenderTrackable trackInfo = (RenderTrackable)window;
        long defaultTimeout = this.getDefaultPortletTimeout();
        if (trackInfo.getExpiration() > 0)
        {
            return (renderTime > trackInfo.getExpiration());
        }
        else if (defaultTimeout > 0)
        {
            return (renderTime > defaultTimeout);
        }
        return false;
    }
    
    public boolean isOutOfService(PortletWindow window)
    {
        if (!isEnabled()) {
            return false;
        }

        RenderTrackable trackable = (RenderTrackable)window;
        if (trackable.getRenderTimeoutCount() >= this.outOfServiceLimit)
        {
            return true;
        }

        CacheElement element = trackingCache.get(window.getPortletDefinition().getUniqueName());
        if (element != null) {
            List<String> windows = (List<String>)element.getContent();
            if (windows.contains(window.getWindowId()))
                return true;
        }

        Integer count = trackingCounts.get(window.getWindowId());
        if (count != null && count >= this.outOfServiceLimit) {
            takeOutOfService(window);
            return true;
        }

        PortletDefinition def = window.getPortletDefinition();
        Collection<LocalizedField> fields = def.getMetadata().getFields(PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_OUT_OF_SERVICE);
        
        if (fields != null && !fields.isEmpty())
        {
            if (BooleanUtils.toBoolean(fields.iterator().next().getValue()))
            {
                addToCache(window);
                return true;
            }
        }
        
        return false;
    }
    
    public int getOutOfServiceLimit()
    {
        return this.outOfServiceLimit;
    }
    
    public void incrementRenderTimeoutCount(PortletWindow window)
    {
        if (!isEnabled()) {
            return;
        }
        RenderTrackable trackable = (RenderTrackable)window;
        trackable.incrementRenderTimeoutCount();
        Integer count = trackingCounts.get(window.getWindowId());
        if (count == null) {
            trackingCounts.put(window.getWindowId(), 1);
        }
        else {
            count = count + 1;
            trackingCounts.put(window.getWindowId(), count);
        }
    }
   
    public void success(PortletWindow window)
    {
        if (!isEnabled()) {
            return ;
        }
        RenderTrackable trackable = (RenderTrackable)window;
        removeFromCache(window);
        trackingCounts.remove(window.getWindowId());
        trackable.success();
    }
    
    public void setExpiration(PortletWindow window, long expiration)
    {
        RenderTrackable trackable = (RenderTrackable)window;
        trackable.setExpiration(expiration); // * 1000);                
    }
        
    public void takeOutOfService(PortletWindow window)
    {
        RenderTrackable trackable = (RenderTrackable)window;
        addToCache(window);
        trackingCounts.remove(window.getWindowId());
        trackable.setRenderTimeoutCount((int)this.defaultPortletTimeout + 1);
    }
    
    public void putIntoService(PortletWindow window)
    {
        RenderTrackable trackable = (RenderTrackable)window;
        removeFromCache(window);
        trackingCounts.remove(window.getWindowId());
        trackable.setRenderTimeoutCount(0);        
    }
    
    public void putIntoService(List<String> fullPortletNames)
    {
        for (String fullName : fullPortletNames) {
            trackingCache.remove(fullName);
        }
    }
    
    public PortletTrackingInfo getOutOfServiceList(String fullPortletName)
    {
        CacheElement element = trackingCache.get(fullPortletName);
        if (element != null) {
            List<String> windows = (List<String>)element.getContent();
            return new PortletTrackingInfo(fullPortletName, windows);
        }
        else {
            List<String> windows = new ArrayList<String>();
            return new PortletTrackingInfo(fullPortletName, windows);
        }
    }
    
    public List<PortletTrackingInfo> getOutOfServiceList()
    {
        List<PortletTrackingInfo> result = new ArrayList<PortletTrackingInfo>();
        List<String> keys = trackingCache.getKeys();
        for (String fullName : keys) {
            CacheElement element = trackingCache.get(fullName);
            if (element != null) {
                List<String> windows = (List<String>) element.getContent();
                result.add(new PortletTrackingInfo(fullName, windows));
            }
        }
        return result;
    }

    protected boolean addToCache(PortletWindow window) {
        String fullName = window.getPortletDefinition().getUniqueName();
        CacheElement cachedElement = trackingCache.get(fullName);
        if (cachedElement == null) {
            List<String> windowIds = new ArrayList<String>();
            windowIds.add(window.getWindowId());
            cachedElement = trackingCache.createElement(fullName, windowIds);
            trackingCache.put(cachedElement);
            return true;
        }
        else {
            List<String> windowIds = (List<String>)cachedElement.getContent();
            if (!windowIds.contains(window.getWindowId())) {
                windowIds.add(window.getWindowId());
                trackingCache.put(cachedElement);
                return true;
            }
        }
        return false;
    }

    protected boolean removeFromCache(PortletWindow window) {
        String fullName = window.getPortletDefinition().getUniqueName();
        CacheElement cachedElement = trackingCache.get(fullName);
        if (cachedElement == null) {
            return false;
        }
        List<String> windowIds = (List<String>)cachedElement.getContent();
        if (!windowIds.contains(window.getWindowId())) {
            windowIds.remove(window.getWindowId());
            trackingCache.put(cachedElement);
            return true;
        }
        return false;
    }

    public  boolean isEnabled() {
        return defaultPortletTimeout > 0;
    }
}