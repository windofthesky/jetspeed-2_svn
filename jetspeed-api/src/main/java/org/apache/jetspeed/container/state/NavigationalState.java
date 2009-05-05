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
package org.apache.jetspeed.container.state;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.PortalURL;

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
    /**
     * Session key for storing the PortletWindowSessionNavigationalStates
     */
    public static final String NAVSTATE_SESSION_KEY = "org.apache.jetspeed.navstate";
    
    /**
     * Session key for storing the PublicRenderParametersMap
     */
    public static final String PRP_SESSION_KEY = "org.apache.jetspeed.prp";

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
     *
     * @param context The RequestContext for this Navigational State
     * @return false if the target PortletWindow (action or resource) could not be resolved
     */
    boolean sync(RequestContext context);

    /**
     * Gets the window state for given portlet window.
     * 
     * @param window
     * @return
     */
    WindowState getState(PortletWindow window);    
    
    /**
     * Gets the internal (portal) window state for given portlet window.
     * 
     * @param window
     * @return
     */
    WindowState getMappedState(PortletWindow window);    
    
    /**
     * Gets the window state for given portlet window id.
     * 
     * @param windowId
     * @return
     * @deprecated
     */
    WindowState getState(String windowId);    
    
    /**
     * Gets the internal (portal) window state for given portlet window id.
     * 
     * @param windowId
     * @return
     */
    WindowState getMappedState(String windowId);    
    
    /**
     * Gets the portlet mode for the given portlet window.
     * 
     * @param window
     * @return
     */
    PortletMode getMode(PortletWindow window);
    
    /**
     * Gets the internal (portal) portlet mode for the given portlet window.
     * 
     * @param window
     * @return
     */
    PortletMode getMappedMode(PortletWindow window);
    
    /**
     * Gets the portlet mode for the given portlet window id.
     * 
     * @param windowId
     * @return
     * @deprecated
     */
    PortletMode getMode(String windowId);
    
    /**
     * Gets the internal (portal) portlet mode for the given portlet window id.
     * 
     * @param windowId
     * @return
     */
    PortletMode getMappedMode(String windowId);
    
    /**
     * For the current request return the (first) maximized window or
     * return null if no windows are maximized.
     * 
     * @return The maximized window or null
     */
    PortletWindow getMaximizedWindow();
        
    /**
     * Provides a "safe" copy of the client/portal request parameterMap, with the values
     * already (re)encode in the preferred or requested character encoding
     * <p>
     * This parameterMap is created early (and only once) to protect against dynamic
     * modification by certain webcontainers like Websphere which might change the contents
     * of the original request parametersMap during request dispatching with additional
     * query string parameters.
     * </p>
     * <p>
     * Furthermore, when using parallel rendering this is even more critical to do upfront
     * and only once while still in the initial portal request Thread.
     * </p>
     * @return
     */
    Map<String, String[]> getRequestParameterMap();
    
    Map<String, String[]> getParameterMap(PortletWindow window);

    boolean isActionScopedRequestAttributes(PortletWindow window);
    
    String getActionScopeId(PortletWindow window);
    
    boolean isActionScopeRendered(PortletWindow window);

    String getCacheLevel(PortletWindow window);

    String getResourceID(PortletWindow window);
    
    Map<String, String[]> getPrivateRenderParameterMap(PortletWindow window);
    
    Map<String, String[]> getPublicRenderParameterMap(PortletWindow window);

    PortalURL.URLType getURLType();
    
    PortletWindow getPortletWindowOfAction();
    
    PortletWindow getPortletWindowOfResource();
    /**
     * Returns an iterator of Portlet Window ids of all the Portlet Windows 
     * within the NavigationalState.
     * <br/>
     * Note: for an ActionRequest, this will include the window id of
     * the PortletWindowOfAction.
     * @return iterator of portletWindow ids (String)
     */
    Iterator<String> getWindowIdIterator();
    
    /**
     * Encodes the Navigational State with overrides for a specific PortletWindow into a string to be embedded within a 
     * PortalURL.
     * 
     * @param window the PortalWindow
     * @param parameters the new request or action parameters for the PortalWindow
     * @param actionScopeId the new action scope for the PortalWindow
     * @param actionScopeRendered the new action scope rendered flag for the PortalWindow
     * @param cacheLevel the new cache level for the PortalWindow resource
     * @param resourceId the new resource id for the PortalWindow resource
     * @param privateRenderParameters the new private render parameters for the PortalWindow resource
     * @param publicRenderParameters the new request, action, or resource public render parameters for the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @param action indicates if to be used in an actionURL or renderURL
     * @return encoded new Navigational State
     * @deprecated
     */
    String encode(PortletWindow window, Map<String, String[]> parameters, String actionScopeId, boolean actionScopeRendered,
                  String cacheLevel, String resourceId, Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                  PortletMode mode, WindowState state, boolean action) throws UnsupportedEncodingException;

    /**
     * Encodes the Navigational State with overrides for a specific PortletWindow into a string to be embedded within a 
     * PortalURL.
     * 
     * @param window the PortalWindow
     * @param parameters the new request or action parameters for the PortalWindow
     * @param actionScopeId the new action scope for the PortalWindow
     * @param actionScopeRendered the new action scope rendered flag for the PortalWindow
     * @param cacheLevel the new cache level for the PortalWindow resource
     * @param resourceId the new resource id for the PortalWindow resource
     * @param privateRenderParameters the new private render parameters for the PortalWindow resource
     * @param publicRenderParameters the new request, action, or resource public render parameters for the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @param urlType indicates if to be used in an actionURL, ResourceURL or renderURL
     * @return encoded new Navigational State
     */
    String encode(PortletWindow window, Map<String, String[]> parameters, String actionScopeId, boolean actionScopeRendered,
                  String cacheLevel, String resourceId, Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                  PortletMode mode, WindowState state, PortalURL.URLType urlType) throws UnsupportedEncodingException;

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
     * Encodes the current navigational State into a string to be embedded within a PortalURL.
     * 
     * @return encoded new Navigational State 
     */
    String encode() throws UnsupportedEncodingException;
    
    /**
     * @return true if WindowStates and PortletModes will be saved in the Session
     */
    boolean isNavigationalParameterStateFull();

    /**
     * @return true if render parameters will be saved in the Session
     */
    boolean isRenderParameterStateFull();
    
    void registerPortletContentCachedForPublicRenderParameters(RequestContext context, PortletContent content);
}
