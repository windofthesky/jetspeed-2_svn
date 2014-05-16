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
package org.apache.jetspeed.request;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.util.KeyValue;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Portal Request Context is associated with each request
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: RequestContext.java,v 1.14 2005/04/29 14:00:48 weaver Exp $
 */
public interface RequestContext
{
    public final static String REQUEST_PORTALENV = PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE;

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
     * Sets the HTTP Servlet Request.  This is the Servlet
     * containers raw request object.  This request should
     * be wrapped using <code>getPortletRequestForWindow()</code> before
     * being processed by the portlet container.
     *
     * @return HttpServletRequest
     */    
    public void setRequest(HttpServletRequest request);
    
    /**
     * Gets the HTTP Servlet Response.  This is the Servlet
     * containers raw response object.  This response should
     * be wrapped using <code>getPortletResponseForWindow()</code> before
     * being processed by the portlet container.
     * @return HttpServletResponse
     */
    public HttpServletResponse getResponse();

    /**
     * Sets the HTTP Servlet Response.  This is the Servlet
     * containers raw response object.  This response should
     * be wrapped using <code>getPortletResponseForWindow()</code> before
     * being processed by the portlet container.
     * @return HttpServletResponse
     */    
    public void setResponse(HttpServletResponse response);
    
    /**
     * Gets the HTTP Servlet Config
     *
     * @return ServletConfig
     */
    public ServletConfig getConfig();

    /**
     * Gets the profile locators for this request
     *
     * @return Profile locators by locator name
     */
    public Map<String,ProfileLocator> getProfileLocators();

    /**
     * Sets the target page profile locators for this request
     *
     * @param locators The target profile locators by locator name
     */
    public void setProfileLocators(Map<String,ProfileLocator> locators);

    /**
     * Gets the target page for this request
     *
     * @return Page
     */
    public ContentPage getPage();

    /**
     * Sets the target page  for this request
     *
     * @param page The target page
     */
    public void setPage(ContentPage page);

    /**
     * Set the capabilityMap. Used by the CapabilityValve
     *
     * @param map
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
     * Gets the Portal URL for the current request.
     * 
     * @return The Portal URL object for the current request.  This method will never
     * return a <code>null</code> value.
     * @throws IllegalStateException if <code>portalUrl</code>
     * if has not been set.
     */
    public PortalURL getPortalURL();
    
    /**
     * Sets the Portal URL for the current request.
     * 
     * @throws IllegalStateException if <code>portalUrl</code>
     * has been set already.
     * @throws IllegalArgumentException if a null value is passed in.
     */
    public void setPortalURL(PortalURL portalUrl);
    
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

    public PortletWindow getPortletWindow(String windowId);
    public PortletWindow getPortletWindow(ContentFragment fragment);
    public PortletWindow getInstantlyCreatedPortletWindow(String windowId, String portletUniqueName);
    public PortletWindow resolvePortletWindow(String windowId);
    public PortletWindow getCurrentPortletWindow();
    public void setCurrentPortletWindow(PortletWindow window);
    
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
     * @param locale associated with this request.
     */
    void setLocale(Locale locale);

    /**
     * Use this method to get a request parameter on the generalized request,
     * decoupling request parameter manipulation from servlet API.
     * This parameter could be on the Http Servlet request,
     * in that case it simply passes through to the servlet request.
     *
     * @param key The parameter unique key
     * @return The object associated with the unique key
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
     * @param appName The portlet application name.
     * @return The PortletRequest.USER_INFO map.
     */
    Map<String, String> getUserInfoMap(String appName);
    
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
    
    /**
     * 
     * @return
     */
    Throwable popActionFailure(PortletWindow window); 


    /**
     * @param window the window to set failed action on
     * @param actionFailure The actionFailed to set
     */
    void setActionFailure(PortletWindow window, Throwable actionFailure);
        
    /**
     * Get the current executing pipeline
     * 
     * @return Pipeline
     */
    Pipeline getPipeline();
    
    /**
     * Set the current pipeline
     * @param pipeline
     */
    void setPipeline(Pipeline pipeline);

    /**
     * Gets the Jetspeed primary user principal associated with the authorized entity.
     *
     * @return The primary principal on this request.
     */
    Principal getUserPrincipal();
    
    /**
     * Locates a specific page using the profiler and site manager location algorithms
     * from a generalized non-profiled path to the first page matching the path
     * 
     *  @param profiler The profiler component to use in the search
     *  @param pageLayoutComponent The page layout component used to assemble the returned page
     *  @return A Content Page located by the profiler, or null if not found
     */
    ContentPage locatePage(Profiler profiler, PageLayoutComponent pageLayoutComponent, String nonProfiledPath);
    
    /**
     * Return a map of Jetspeed Request Context objects configured via Spring Map
     * 
     * @return a Map of request context objects
     * @since 2.1.2
     */
    Map<String, Object> getObjects();
    
    /**
     * The RequestContext itself is kept in a ThreadLocal, calling this method from another (parallel)
     * thread ensures its ThreadLocal instance will be synchronized with this instance as well.
     * <p>
     * This method will return true if this thread its ThreadLocal didn't yet have <em>this</em>
     * RequestContext value set. In that case the calling code block, preferably in a finally
     * statement should call clearThreadContext() to ensure the reference to this instance
     * is removed again from its ThreadLocal instance as Thread Pool solutions might reuse
     * threads.
     * </p>
     * @return true if the current thread didn't have this request context set
     *         <em>or</em> contained a different (stale/left over?) request context 
     */
    boolean ensureThreadContext();
    
    /**
     * Clears the request context from the current thread
     */
    void clearThreadContext();
    
    /**
     * Merges and returns the head elements contributed by portlets. 
     * @return
     */
    List<KeyValue<String, HeadElement>> getMergedHeadElements();
    
}

