/**
 * Created on Oct 21, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.container.url;

import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

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
}