/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.request;

import java.util.Locale;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.om.page.Page;

import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Portal Request Context is associated with each request
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public interface RequestContext
{
    public static final String PREFERED_LANGUAGE_SESSION_KEY = "org.apache.jetspeed.prefered.language";
    public static final String PREFERED_LOCALE_SESSION_KEY = "org.apache.jetspeed.prefered.locale";
    public static final String PREFERED_CHARACTERENCODING_KEY = "org.apache.jetspeed.prefered.characterencoding";
    public final static String REQUEST_PORTALENV = "org.apache.jetspeed.request.RequestContext";    												

    /**
     * Gets the HTTP Servlet Request.  This is the Servlet
     * containers raw request object.  This request should
     * be wrapped using <code>getPortletRequestForWindow()</code> before
     * being processed by the portlet container.
     *
     * @return HttpServletRequest
     */
    public HttpServletRequest getRequest();

    /**
     * Gets the HTTP Servlet Response.  This is the Servlet
     * containers raw response object.  This response should
     * be wrapped using <code>getPortletResponseForWindow()</code> before
     * being processed by the portlet container.
     * @return HttpServletResponse
     */
    public HttpServletResponse getResponse();

    /**
     * Gets the HTTP Servlet Config
     *
     * @return ServletConfig
     */
    public ServletConfig getConfig();

    /**
     * Gets the profile locator for this request
     *
     * @return Profile
     */
    public ProfileLocator getProfileLocator();

    /**
     * Sets the target page profile for this request
     *
     * @param profile The target profile
     */
    public void setProfileLocator(ProfileLocator locator);

    /**
     * Gets the target page for this request
     *
     * @return Page
     */
    public Page getPage();

    /**
     * Sets the target page  for this request
     *
     * @param page The target page
     */
    public void setPage(Page page);

    /**
     * Gets the content dispatcher for this request
     *
     * @return ContentDispatcher
     */
    public ContentDispatcher getContentDispatcher();

    /**
     * Sets the content dispatcher for this request
     *
     * @param dispatcher The ContentDispatcher to use for this request
     */
    public void setContentDispatcher(ContentDispatcher dispatcher);

    /**
     * Set the capabilityMap. Used by the CapabilityValve
     *
     * @param capabilityMap
     */
    public void setCapabilityMap(CapabilityMap map);

    /**
     * Get the Capability Map
     *
     */
    public CapabilityMap getCapabilityMap();

    /**
     * Set the Mimetype. Set by the CapabilityValve
     *
     * @param mimeType
     */
    public void setMimeType(String mimeType);

    /**
     * Get the mimeType for the request
     *
     */
    public String getMimeType();

    /**
     * Set the mediaType. Set by the CapabilityValve
     *
     * @param mediaType
     */
    public void setMediaType(String mediaType);

    /**
     * get the Media Type
     *
     */
    public String getMediaType();

    
    /**
     * Gets the navigational state for this current request.
     * 
     * @return The navigational state context object for the current request.
     */
    public NavigationalState getNavigationalState();

    /**
     * Gets the Portal URL for the current request.
     * 
     * @return The Portal URL object for the current request.  This method will never
     * return a <code>null</code> value.
     * @throws IllegalStateException if <code>portalUrl</code>
     * if has not been set.
     */
    public PortalURL getPortalURL();
    
    /**
     * Get the target Action Window
     *
     * @return PortletWindow The target portlet action window
     */
    public PortletWindow getActionWindow();

    /**
     * Sets the target Portlet Window
     *
     * @param window
     */
    public void setActionWindow(PortletWindow window);

    /**
     * get the character encoding
     *
     *
     */
    public String getCharacterEncoding();

    /**
     * set character encoding
     *
     * @param enc
     */
    public void setCharacterEncoding(String enc);

    /**
     *
     * <p>
     * getRequestForWindow
     * </p>
     *
     * Takes a PortletWindow and generates a HttpServletRequest that
     * accurately represents that PortletWindow's request parameters
     *
     *
     * @param window PortletWindow that we are build a request for
     * @return HttpServletRequest that wraps the existing servlet
     * container's request that can interpret encoded portlet information
     * for this PortletWindow
     *
     */
    HttpServletRequest getRequestForWindow(PortletWindow window);

    /**
     *
     * <p>
     * getResponseForWindow
     * </p>
     *
     * Takes a PortletWindow and generates a HttpServletResponse that
     * accurately represents that PortletWindow's request parameters.
     *
     *
     * @param window PortletWindow that we are build a response for
     * @return HttpServletRequest that wraps the existing servlet
     * container's request that can interpret encoded portlet information
     * for this PortletWindow
     *
     *
     */
    HttpServletResponse getResponseForWindow(PortletWindow window);

    /**
     * Gets the subject associated with the authorized entity.
     * This subject can be used to provide credentials and principals.
     *
     * @return The JAAS subject on this request.
     */
    Subject getSubject();

    /**
     * Sets the subject associated with the authorized entity.
     * This subject can be used to provide credentials and principals.
     *
     * @param subject The JAAS subject on this request.
     */
    void setSubject(Subject subject);

    /**
     * Gets the locale associated with this request.
     *
     * @return The locale associated with this request.
     */
    Locale getLocale();

    /**
     * Sets the locale associated with this request.
     *
     * @param The locale associated with this request.
     */
    void setLocale(Locale locale);

    /**
     * Use this method to get a request parameter on the generalized request,
     * decoupling request parameter manipulation from servlet API.
     * This parameter could be on the Http Servlet request,
     * in that case it simply passes through to the servlet request.
     *
     * @param key The parameter unique key
     * @return The object associated with the uniqu
     */
    String getRequestParameter(String key);

    /**
     * Use this method to get a map of request parameters on the generalized request,
     * decoupling request parameter manipulation from servlet API.
     * The parameters returned could be on the Http Servlet request,
     * in that case it simply passes through to the servlet request.
     *
     * @return
     */
    Map getParameterMap();


    /**
     * Gets an attribute from the session.
     * This method is decoupled from the servlet api request to
     * facilitate abstractions for testing and other programs not
     * connected to a servlet application.
     *
     * @param key The key of the attribute
     * @return The value of the attribute
     */
    Object getSessionAttribute(String key);

    /**
     * Sets an attribute into the session.
     * This method is decoupled from the servlet api request to
     * facilitate abstractions for testing and other programs not
     * connected to a servlet application.
     *
     * @param key The key of the session attribute
     * @param value The value of the session attribute
     */
    void setSessionAttribute(String key, Object value);

    /**
     * Get a request attribute associated with this single request.
     *
     * @param key The key of the request attribute
     * @return The value of the request attribute
     */
    Object getAttribute(String key);

    /**
     * Sets an attribute into the request.
     * This method is decoupled from the servlet api request to
     * facilitate abstractions for testing and other programs not
     * connected to a servlet application.
     *
     * @param key The key of the request attribute
     * @param value The value of the request attribute
     */
    void setAttribute(String key, Object value);

    /**
     * <p>
     * Returns any extra path information associated with the URL the
     * client sent when it made this request. The extra path information
     * follows the servlet path but precedes the query string.
     * This method returns null if there was no extra path information.
     * </p>
     * <p>
     * This method should function identically to <code>HttpServletRequest.getPathInfo()</code>
     * except for that it removes ALL portal/portlet navigational state information from the
     * path info string.
     * </p>
     *
     * @return the path
     */
    String getPath();
    
    /**
     * 
     * <p>
     * setPath
     * </p>
     * Allows the manual overriding of path Jetspeed 2 will look to resolves pages and folders.
     *
     * @param path
     */
    void setPath(String path);
    
    /**
     * Returns the user info map of user attributes for a given portlet application.</p>
     * @param oid The portlet application object id.
     * @return The PortletRequest.USER_INFO map.
     */
    Map getUserInfoMap(ObjectID oid);
    
    /**
     * 
     * <p>
     * getPreferedLanguage
     * </p>
     * Returns the Language object for the <code>portlet</code> which most
     * closely matches the prefences of the currently requesting client.
     * 
     * @param portlet
     * @return <code>Language</code> that matches, as closely as possible, that of
     * the requesting client.
     */
    Language getPreferedLanguage( PortletDefinition portlet );
        
}
