/**
 * Created on Oct 21, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.container.url;

import org.apache.jetspeed.container.url.impl.PortalControlParameter;
import org.apache.jetspeed.request.RequestContext;

/**
 * <p>
 * PortalURL
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PortalURL
{
    /** HTTP protocol. */
    public static final String HTTP = "http";

    /** HTTPS protocol. */
    public static final String HTTPS = "https";

    void setControlParameter(PortalControlParameter pcp);
    
    PortalControlParameter getControlParameter();
    
    public abstract void init(RequestContext context);
    /**
     * Adds a navigational information pointing to a portal part, e.g. PageGroups
     * or Pages
     * 
     * @param nav    the string pointing to a portal part
     */
    //public abstract void addGlobalNavigation(String nav);
    /**
     * Sets the local navigation. Because the local navigation is always handled
     * by the Browser, therefore the local navigation cleared.
     */
    //public abstract void setLocalNavigation();
    /**
     * Adds a navigational information pointing to a local portal part inside
     * of a global portal part, e.g. a portlet on a page
     * 
     * @param nav    the string pointing to a local portal part
     */
    //public abstract void addLocalNavigation(String nav);
    /**
     * Returns true if the given string is part of the global navigation of this URL
     * 
     * @param nav    the string to check
     * @return true, if the string is part of the navigation
     */
    //public abstract boolean isPartOfGlobalNavigation(String nav);
    /**
     * Returns true if the given string is part of the local navigation of this URL
     * 
     * @param nav    the string to check
     * @return true, if the string is part of the navigation
     */
    //public abstract boolean isPartOfLocalNavigation(String nav);
    //public abstract String getGlobalNavigationAsString();
    //public abstract String getLocalNavigationAsString();
    //public abstract String getControlParameterAsString(PortalControlParameter controlParam);
    //public abstract String getRequestParameterAsString(PortalControlParameter controlParam);
    //public abstract String toString();
    public abstract String toString(PortalControlParameter controlParam, Boolean p_secure);
    // public abstract void analyzeControlInformation(PortalControlParameter control);
    // public abstract void setRenderParameter(PortletWindow portletWindow, String name, String[] values);
    // public abstract void clearRenderParameters(PortletWindow portletWindow);
    public abstract String getBaseURL();
    //public abstract String getContext();
}