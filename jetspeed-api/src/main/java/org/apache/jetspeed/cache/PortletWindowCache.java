/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.cache;

import java.util.List;
import java.util.Set;

import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;

/**
 * {@link org.apache.jetspeed.cache.impl.PortletWindowCache} is an abstraction of a caching mechanism for use
 * within {@link org.apache.jetspeed.container.window.impl.PortletWindowAccessorImpl}.
 * 
 * @author <a href="mailto:scott.t.weaver@gmail.com">Scott T. Weaver</a>
 * @see PortletWindowAccessorImpl
 * @see EhPortletWindowCache
 * 
 */
public interface PortletWindowCache
{
    /**
     * Gets a {@link PortletWindow} from the cache.
     * 
     * @param windowId Id of the window to get from the cache.
     * @return {@link PortletWindow} whose <code>id</code> to 
     * {@link PortletWindow#getId()} or <code>null</code> if no window exists that matches
     * <code>windowId</code>.
     */
    PortletWindow getPortletWindow(String windowId);
    
    /**
     * Gets a {@link PortletWindow} from the cache whose {@link PortletEntity}'s ({@link PortletWindow#getPortletEntity()})
     * equals <code>portletEntityId</code>.
     * 
     * @param portletEntityId id of {@link PortletEntity} whose window want want to retrieve from cache.
     * @return {@link PortletWindow} whose {@link PortletEntity}'s id equals <code>portletEntityId</code>
     * or <code>null</code> if no windows exists in the cache that match said criteria.
     */
    PortletWindow getPortletWindowByEntityId(String portletEntityId);
    
    /**
     * Stores a {@link PortletWindow} in the cache using the {@link PortletWindow#getId()#toString()}
     * as the key for the cache.
     * 
     * @param window {@link PortletWindow} to put into the cache.
     */
    void putPortletWindow(PortletWindow window);
    
    /**
     * Removes a {@link PortletWindow} from cache using the <code>windowId</code>
     * as the cache key.
     * 
     * @param windowId Id of the {@link PortletWindow} we want to remove from the cache.
     */
    void removePortletWindow(String windowId);

    /**
     * Removes a {@link PortletWindow} from the cache whose {@link PortletEntity}'s id 
     * matches <code>portletEntityId</code>.
     * 
     * @param portletEntityId id of the {@link PortletEntity} whose parent {@link PortletWindow}
     * is to be removed from the cache.
     */
    void removePortletWindowByPortletEntityId(String portletEntityId);
    
    /**
     * 
     * @return {@link List} of all the {@link PortletWindow}s in the cache.  If no cache
     * entries exist an empty list is returned.  Never returns <code>null</code>.
     */
    Set getAllPortletWindows();

}