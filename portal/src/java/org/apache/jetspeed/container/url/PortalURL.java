/**
 * Created on Oct 21, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.container.url;

import java.util.Iterator;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.request.RequestContext;
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
    
    void init(RequestContext context);
    
    /**
     * Gets the Base URL for this portal.
     * 
     * @return The Base URL of the portal.
     */
    String getBaseURL();   
    
    /**
     * Encodes a window state for the given window on the URL.
     * 
     * @param window The targeted window having its state set.
     * @param state The state being encoded into the URL.
     */
    void setState(PortletWindow window, WindowState state);
    
    /**
     * Encodes a portlet mode for the given window on the URL.
     * 
     * @param window The targeted window having its mode set.
     * @param mode The portlet mode being encoded into the URL.
     */    
    void setMode(PortletWindow window, PortletMode mode);
    
    WindowState getState(PortletWindow window);        
    PortletMode getMode(PortletWindow window);        
    PortletMode getPreviousMode(PortletWindow window);    
    WindowState getPreviousState(PortletWindow window);        
    
    ///////////////////////////////////////////////
    
    boolean isNavigationalParameter(String token);
        
    Iterator getRenderParamNames(PortletWindow window);
    
    String[] getRenderParamValues(PortletWindow window, String paramName);

    PortletWindow getPortletWindowOfAction();
    
    void clearRenderParameters(PortletWindow portletWindow);
    
    void setAction(PortletWindow window);
    
    void setRequestParam(String name, String[] values);
    
    void setRenderParam(PortletWindow window, String name, String[] values);
    
    String toString();
    
    String toString(boolean secure);
    
    
}