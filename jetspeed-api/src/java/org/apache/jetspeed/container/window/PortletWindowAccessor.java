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
package org.apache.jetspeed.container.window;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Portlet Window Accessor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface PortletWindowAccessor 
{
    /**
     * Get a portlet window for the given fragment
     * 
     * @param fragment
     * @return
     * @throws FailedToRetrievePortletWindow
     * @throws InconsistentWindowStateException If the window references a non-existsent PortletEntity
     */
    PortletWindow getPortletWindow(Fragment fragment) throws FailedToRetrievePortletWindow;
    
    /**
     * Get the portlet window for a fragment and given principal
     * @param fragment
     * @param principal
     * @return
     * @throws FailedToCreateWindowException
     * @throws FailedToRetrievePortletWindow
     * @throws InconsistentWindowStateException If the window references a non-existsent PortletEntity
     */
    PortletWindow getPortletWindow(Fragment fragment, String principal) throws FailedToCreateWindowException, FailedToRetrievePortletWindow;

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
    
    
}
