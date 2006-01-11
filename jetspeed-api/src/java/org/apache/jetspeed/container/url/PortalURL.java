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
package org.apache.jetspeed.container.url;

import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.pluto.om.window.PortletWindow;

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
    /** HTTP protocol. */
    public static final String HTTP = "http";

    /** HTTPS protocol. */
    public static final String HTTPS = "https";
    
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
     */
    String createPortletURL(PortletWindow window, Map parameters, PortletMode mode, WindowState state, boolean action, boolean secure);

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
}