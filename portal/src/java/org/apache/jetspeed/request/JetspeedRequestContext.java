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
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;

import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.services.factory.FactoryManager;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.engine.servlet.ServletRequestFactory;
import org.apache.jetspeed.engine.servlet.ServletResponseFactory;
import org.apache.pluto.om.window.PortletWindow;

import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * Jetspeed Request Context is associated with each portal request.
 * The request holds the contextual information shared amongst components
 * in the portal, accessed through a common valve pipeline.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedRequestContext implements RequestContext
{
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletConfig config;
    private ProfileLocator locator;
    private Page page;
    private PortletDefinition portletDefinition;
    private Subject subject;
    private Locale locale;
    private ContentDispatcher dispatcher;

    private CapabilityMap capabilityMap;
    private String mimeType;
    private String mediaType;
    private NavigationalState navstate;
    private PortalURL url;
    private PortletWindow actionWindow;
    private String encoding;
    
    public final static String REQUEST_PORTALENV = "org.apache.jetspeed.request.RequestContext";

    /**
     * Create a new Request Context
     *
     * @param pc
     * @param request
     * @param response
     * @param config
     */
    public JetspeedRequestContext( 
                                  HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  ServletConfig config,
                                  NavigationalStateComponent navcomponent)
    {
        this.request = request;
        this.response = response;
        this.config = config;

        // set context in Request for later use
        if (null != this.request)
        {
            this.request.setAttribute(REQUEST_PORTALENV, this);
        }
        
        if (navcomponent != null)
        {
            url = navcomponent.createURL(this);
            navstate = navcomponent.create(this);            
        }
        
    }

    private JetspeedRequestContext()
    {
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public HttpServletResponse getResponse()
    {
        return response;
    }

    public ServletConfig getConfig()
    {
        return config;
    }

    public ProfileLocator getProfileLocator()
    {
        return locator;
    }

    public void setProfileLocator(ProfileLocator locator)
    {
        this.locator = locator;
    }

    public Page getPage()
    {
        return this.page;
    }

    public void setPage(Page page)
    {
        this.page = page;
    }

    public PortletDefinition getPortletDefinition()
    {
        return portletDefinition;
    }

    public void setPortletDefinition(PortletDefinition portletDefinition)
    {
        this.portletDefinition = portletDefinition;
    }

    public ContentDispatcher getContentDispatcher()
    {
        return dispatcher;
    }

    public void setContentDispatcher(ContentDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    /** Set the capabilityMap. Used by the CapabilityValve
       *
       * @param capabilityMap
       */
    public void setCapabilityMap(CapabilityMap map)
    {
        this.capabilityMap = map;
    }

    /** get the Capability Map
     *
     */
    public CapabilityMap getCapabilityMap()
    {
        return this.capabilityMap;
    }

    /** Set the Mimetype. Used by the CapabilityValve
     *
     * @param mimeType
     */
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    /** get the mimeType for the request
     *
     */
    public String getMimeType()
    {
        return this.mimeType;
    }

    /** Set the mediaType. Used by the CapabilityValve
     *
     * @param mediaType
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    /** get the Media Type
     *
     */
    public String getMediaType()
    {
        return this.mediaType;
    }

    public NavigationalState getNavigationalState()
    {
        return navstate;
    }
    
    /**
     * Get the target Portlet Action Window
     *
     * @return PortletWindow The target portlet window
     */
    public PortletWindow getActionWindow()
    {
        return actionWindow;
    }

    /**
     * Sets the target Portlet Action Window
     *
     * @param window
     */
    public void setActionWindow(PortletWindow portletWindow)
    {
        this.actionWindow = portletWindow;
    }

    /**
     * get the character encoding
     *
     *
     */
    public String getCharacterEncoding()
    {
        return this.encoding;
    }

    /**
     * set character encoding
     *
     * @param enc
     */
    public void setCharacterEncoding(String enc)
    {
        this.encoding = enc;
    }

    /**
     * <p>
     * getRequestForWindow
     * </p>
     *
     * @see org.apache.jetspeed.request.RequestContext#getRequestForWindow(org.apache.pluto.om.window.PortletWindow)
     * @param window
     * @return
     */
    public HttpServletRequest getRequestForWindow(PortletWindow window)
    {
        ServletRequestFactory reqFac =
            (ServletRequestFactory) FactoryManager.getFactory(javax.servlet.http.HttpServletRequest.class);
        HttpServletRequest requestWrapper = reqFac.getServletRequest(request, window);
        return requestWrapper;
    }

    /**
     * <p>
     * getResponseForWindow
     * </p>
     *
     * @see org.apache.jetspeed.request.RequestContext#getResponseForWindow(org.apache.pluto.om.window.PortletWindow)
     * @param window
     * @return
     */
    public HttpServletResponse getResponseForWindow(PortletWindow window)
    {
        ServletResponseFactory rspFac = (ServletResponseFactory) FactoryManager.getFactory(HttpServletResponse.class);
        HttpServletResponse wrappedResponse = rspFac.getServletResponse(response);
        return wrappedResponse;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getSubject()
     */
    public Subject getSubject()
    {
        return this.subject;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setSubject(javax.security.auth.Subject)
     */
    public void setSubject(Subject subject)
    {
        this.subject = subject;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getLocale()
     */
    public Locale getLocale()
    {
        return this.locale;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getRequestParameter(java.lang.String)
     */
    public String getRequestParameter(String key)
    {
        return request.getParameter(key);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getParameterMap()
     */
    public Map getParameterMap()
    {
        return request.getParameterMap();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getRequestAttribute(java.lang.String)
     */
    public Object getRequestAttribute(String key)
    {
        return request.getAttribute(key);
    }


    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getSessionAttribute(java.lang.String)
     */
    public Object getSessionAttribute(String key)
    {
        return request.getSession().getAttribute(key);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setSessionAttribute(java.lang.String, java.lang.Object)
     */
    public void setSessionAttribute(String key, Object value)
    {
        request.getSession().setAttribute(key, value);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String key, Object value)
    {
        request.setAttribute(key, value);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String key)
    {
        return request.getAttribute(key);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPath()
     */
    public String getPath()
    {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
        {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(pathInfo, "/");
        StringBuffer path = new StringBuffer();
        int mode = 0; // 0=navigation, 1=control information
        int count = 0;
        String name = null;
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (this.url.isNavigationalParameter(token))
            {
                break;            
            }
            if (count > 0)
            {
                path.append("/");
            }            
            path.append(token);
            count++;
        }
        String result = path.toString();
        if (result.equals("/") || result.trim().length() == 0)
        {
            return null;
        }
        return result;
    }
    
    public PortalURL getPortalURL()
    {
        return url;
    }
    
}
