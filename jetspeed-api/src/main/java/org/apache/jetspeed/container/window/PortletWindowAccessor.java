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
package org.apache.jetspeed.container.window;

import java.util.Set;

import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.container.PortletWindow;

/**
 * Portlet Window Accessor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortletWindowAccessor.java,v 1.7 2005/04/29 13:59:46 weaver Exp $
 */
public interface PortletWindowAccessor 
{
    /**
     * Get a portlet window for the given fragment
     * 
     * @param fragment
     * @return
     * @throws FailedToRetrievePortletWindow
     * @throws PortletEntityNotStoredException 
     * @throws InconsistentWindowStateException If the window references a non-existsent PortletEntity
     */
    PortletWindow getPortletWindow(ContentFragment fragment) throws FailedToRetrievePortletWindow, PortletEntityNotStoredException;
    
    /**
     * Lookup a portlet window in the cache
     * If not found, return null
     * 
     * @param windowId 
     * @return the window from the cache or null
     */
    PortletWindow getPortletWindow(String windowId);

    /**
     * Given a portlet entity, create a portlet window for that entity.
     * 
     * @param entity
     * @param windowId
     * @return new window
     */
    PortletWindow createPortletWindow(PortletEntity entity, String windowId);

    /**
     * Create a temporary portlet window
     * This window does not have an entity associated with it.
     * 
     * @param windowId
     * @return
     */
    PortletWindow createPortletWindow(String windowId);
    
    /**
     * 
     * <p>
     * removeWindows
     * </p>
     * 
     * Removes all <code>PortletWindow</code>s associated with this
     * <code>PortletEntity</code>
     *
     * @param portletEntity
     */
    void removeWindows(PortletEntity portletEntity);
    
    /**
     * 
     * <p>
     * removeWindow
     * </p>
     * 
     * Removes a <code>PortletWindow</code> from the window cache.
     *
     * @param window
     */
    void removeWindow(PortletWindow window);
    
    /**
     *  Gets a {@link Set} of currently available {@link PortletWindow}s within
     *  the current engine instance.
     *  
     * @return {@link Set} of {@link PortletWindow}s, never returns <code>null</code>
     */
    Set getPortletWindows();
}
