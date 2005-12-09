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
package org.apache.jetspeed.container.state;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * NavigationalState gives readonly access to the state of the Portal URL and all navigational state context
 * as well as encoding a new State for usage in a Portal URL.
 * <br>
 * Note: Support for changing the PortletMode and/or WindowState of a PortletWindow, other than for encoding a new State
 * is moved down to the {@link MutableNavigationState} interface to cleanly define the immutable contract of this 
 * interface.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface NavigationalState 
{
    public static final String NAVSTATE_SESSION_KEY = "org.apache.jetspeed.navstate";

    /*
     * Decodes an encoded Navigational State as retrieved from a Portal URL.
     * <br>
     * Note: Initializing is done only once. Subsequent init requests are ignored.
     * 
     * @param encodedState encoded Navigational State as retrieved from a Portal URL.
     * @param characterEncoding String containing the name of the chararacter encoding
     */
    void init(String encodedState, String characterEncoding) throws UnsupportedEncodingException;
    
    /**
     * Synchronize the Navigational State with saved state (if used).
     * <br>
     * Should be called by the PortalURL impl right after calling {@link #init(String)}
     *
     * @param context The RequestContext for this Navigational State
     */
    void sync(RequestContext context);

    /**
     * Gets the window state for given portlet window.
     * 
     * @param window
     * @return
     */
    WindowState getState(PortletWindow window);    
    
    /**
     * Gets the window state for given portlet window id.
     * 
     * @param windowId
     * @return
     */
    WindowState getState(String windowId);    
    
    /**
     * Gets the portlet mode for the given portlet window.
     * 
     * @param window
     * @return
     */
    PortletMode getMode(PortletWindow window);
    
    /**
     * Gets the portlet mode for the given portlet window id.
     * 
     * @param windowId
     * @return
     */
    PortletMode getMode(String windowId);
    
    /**
     * For the current request return the (first) maximized window or
     * return null if no windows are maximized.
     * 
     * @return The maximized window or null
     */
    PortletWindow getMaximizedWindow();
        
    Iterator getParameterNames(PortletWindow window);
    
    String[] getParameterValues(PortletWindow window, String parameterName);

    PortletWindow getPortletWindowOfAction();
    
    /**
     * Returns an iterator of Portlet Window ids of all the Portlet Windows 
     * within the NavigationalState.
     * <br/>
     * Note: for an ActionRequest, this will include the window id of
     * the PortletWindowOfAction.
     * @return iterator of portletWindow ids (String)
     */
    Iterator getWindowIdIterator();
    
    /**
     * Encodes the Navigational State with overrides for a specific PortletWindow into a string to be embedded within a 
     * PortalURL.
     * 
     * @param window the PortalWindow
     * @param parameters the new request or action parameters for the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @param action indicates if to be used in an actionURL or renderURL
     * @return encoded new Navigational State
     */
    String encode(PortletWindow window, Map parameters, PortletMode mode, WindowState state, boolean action) throws UnsupportedEncodingException;

    /**
     * Encodes the Navigational State with overrides for a specific PortletWindow while retaining its (request) 
     * parameters into a string to be embedded within a renderURL.
     * 
     * @param window the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @return encoded new Navigational State 
     */
    String encode(PortletWindow window, PortletMode mode, WindowState state) throws UnsupportedEncodingException;
    
    /**
     * @return true if WindowStates and PortletModes will be saved in the Session
     */
    boolean isNavigationalParameterStateFull();

    /**
     * @return true if render parameters will be saved in the Session
     */
    boolean isRenderParameterStateFull();
}
