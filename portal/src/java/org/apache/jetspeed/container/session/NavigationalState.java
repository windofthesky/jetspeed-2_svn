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
package org.apache.jetspeed.container.session;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.pluto.om.window.PortletWindow;

/**
 * NavigationalState contains the state of the Portal URL and all navigational state context
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface NavigationalState 
{
    /**
     * Gets the window state for given portlet window.
     * 
     * @param window
     * @return
     */
    WindowState getState(PortletWindow window);
    
    /**
     * Sets the window state for the given portlet window.
     * 
     * @param window
     * @param state
     */
    void setState(PortletWindow window, WindowState state);
    
    /**
     * Gets the portlet mode for the given portlet window.
     * 
     * @param window
     * @return
     */
    PortletMode getMode(PortletWindow window);
    
    /**
     * Sets the portlet mode for the given portlet window.
     * 
     * @param window
     * @param mode
     */
    void setMode(PortletWindow window, PortletMode mode);
    
    /**
     * Gets the previous portlet mode for the given portlet window.
     * 
     * @param window
     * @return
     */
    PortletMode getPreviousMode(PortletWindow window);
    
    /**
     * Get the previous window state for the given portlet window.
     * 
     * @param window
     * @return
     */
    WindowState getPreviousState(PortletWindow window);        
        
}
