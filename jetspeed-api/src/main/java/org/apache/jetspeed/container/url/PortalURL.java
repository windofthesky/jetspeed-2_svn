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
package org.apache.jetspeed.container.url;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.state.NavigationalState;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * PortalURL defines the interface for manipulating Jetspeed Portal URLs.
 * These URLs are used internally by the portal and are not available to
 * Portlet Applications.
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 *
 */
public interface PortalURL
{
    enum URLType
    { 
        ACTION("action"), 
        RESOURCE("resource"), 
        RENDER("render"); 
        
        private final String name;
        
        private URLType(String name)
        {
            this.name = name;
        }
        
        public String toString()
        {
            return name;
        }
    }

    /** HTTP protocol. */
    public static final String HTTP = "http";

    /** HTTPS protocol. */
    public static final String HTTPS = "https";
    
    /** Portal Path Info Query parameter. */
    public static final String PATH_INFO_QUERY = "_portalpath";
    
    /** Portal Path Info HTTP Header name. */
    public static final String PATH_INFO_HEADER = "X-Portal-Path";
    
    /**
     * @return true if only relative urls should be generated (without scheme, servername, port)
     */
    boolean isRelativeOnly();
    
    /**
     * Gets the Base URL for this portal.
     * 
     * @return The Base URL of the portal.
     */
    String getBaseURL();   

    /**
     * Gets a secure version of the Base URL for this portal.
     * 
     * @return The secure Base URL of the portal.
     */
    String getBaseURL(boolean secure);

    /**
     * Gets the global navigational path of the current request.
     * <br>
     * The path does not contain the NavigationalState parameter
     * 
     * @return The the global navigational path of the current request.
     */
    String getPath();
    
    /**
     * Returns the current Portal base path. 
     * <br>
     * This path can be used as base for root relative pages and resources which don't need
     * the NavigationalState.
     * @return the current Portal base path without NavigationalState
     */
    String getBasePath();

    /**
     * Returns the current Portal Page base path without possible encoded
     * NavigationalState parameter.
     * <br>
     * This path can be used as base for page relative resources which don't need
     * the NavigationalState.
     * @return the current Portal Page base path without NavigationalState
     */
    String getPageBasePath();

    /**
     * @return true if the current request is secure
     */
    boolean isSecure();

    /**
     * Gets the NavigationalState for access to the current request portal control parameters
     * @return the NavigationalState of the PortalURL
     */
    NavigationalState getNavigationalState();    

    /**
     * Create a new PortletURL for a PortletWindow including request or action parameters.
     * <br>
     * The Portal Navigational State is encoded within the URL
     * 
     * @param window the PortalWindow
     * @param parameters the new request or action parameters for the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @param action indicates if an actionURL or renderURL is created
     * @param secure indicates if a secure url is required 
     * @return a new actionURL or renderURL as String
     * @deprecated
     */
    String createPortletURL(PortletWindow window, Map<String, String[]> parameters, PortletMode mode, WindowState state, boolean action, boolean secure);
    
    /**
     * Create a new PortletURL for a PortletWindow including request or action parameters.
     * <br>
     * The Portal Navigational State is encoded within the URL
     * 
     * @param window the PortalWindow
     * @param parameters the new request or action parameters for the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @param urlType indicates if an actionURL, Resource or renderURL is to created
     * @param secure indicates if a secure url is required 
     * @return a new actionURL or renderURL as String
     * @deprecated
     */
    String createPortletURL(PortletWindow window, Map<String, String[]> parameters, PortletMode mode, WindowState state, URLType urlType, boolean secure);

    /**
     * Create a new PortletURL for a PortletWindow including request or action parameters.
     * <br>
     * The Portal Navigational State is encoded within the URL
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
     * @param urlType indicates if an actionURL, Resource or renderURL is to created
     * @param secure indicates if a secure url is required 
     * @return a new actionURL or renderURL as String
     */
    String createPortletURL(PortletWindow window, Map<String, String[]> parameters, String actionScopeId, boolean actionScopeRendered,
                            String cacheLevel, String resourceId, Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                            PortletMode mode, WindowState state, URLType urlType, boolean secure);

    /**
     * Create a new PortletURL for a PortletWindow retaining its (request) parameters.
     * <br>
     * The Portal Navigational State is encoded within the URL
     * 
     * @param window the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @param secure
     * @param secure indicates if a secure url is required 
     * @return a new renderURL as String
     */
    String createPortletURL(PortletWindow window, PortletMode mode, WindowState state, boolean secure);
    
    /**
     * Sets the @link{javax.servlet.http.HttpServletRequest} that will be used 
     * to generate urls.
     * @param request
     */
    void setRequest(HttpServletRequest request);
    
    void setCharacterEncoding(String characterEncoding);
    
    /**
     * Creates the navigational encoding for a given window
     * Similiar to createPortletURL above
     * 
     * @param window the PortalWindow
     * @param parameters the new request or action parameters for the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @param action indicates if an actionURL or renderURL is created
     * @return a new navigational state as String
     * @deprecated
     */
    String createNavigationalEncoding(PortletWindow window, Map<String, String[]> parameters, PortletMode mode, WindowState state, boolean action);
    
    /**
     * Creates the navigational encoding for a given window
     * Similiar to createPortletURL above
     * 
     * @param window the PortalWindow
     * @param parameters the new request or action parameters for the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @param urlType indicates if an actionURL, Resource or renderURL is to created
     * @return a new navigational state as String
     * @deprecated
     */
    String createNavigationalEncoding(PortletWindow window, Map<String, String[]> parameters, PortletMode mode, WindowState state, URLType urlType);

    /**
     * Creates the navigational encoding for a given window
     * Similiar to createPortletURL above
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
     * @param urlType indicates if an actionURL, Resource or renderURL is to created
     * @return a new navigational state as String
     */
    String createNavigationalEncoding(PortletWindow window, Map<String, String[]> parameters, String actionScopeId, boolean actionScopeRendered,
                                      String cacheLevel, String resourceId, Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                                      PortletMode mode, WindowState state, URLType urlType);
    
    /**
     * Creates the navigational encoding for a given window
     * Similiar to createPortletURL above
     * 
     * @param window the PortalWindow
     * @param mode the new PortletMode for the PortalWindow
     * @param state the new WindowState for the PortalWindow
     * @return a new renderURL as String
     */    
    String createNavigationalEncoding(PortletWindow window, PortletMode mode, WindowState state);

    /**
     * @return a Portal URL with encoded current navigational state
     */
    String getPortalURL();
    
    /**
     * @return true if navigational state was provided on the url
     */
    boolean hasEncodedNavState();
    
    /**
     * @return true if navigational state is encoded as pathInfo
     */
    boolean isPathInfoEncodingNavState();
    

}