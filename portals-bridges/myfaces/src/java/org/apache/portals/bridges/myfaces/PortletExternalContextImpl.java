/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.portals.bridges.myfaces;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sourceforge.myfaces.util.EnumerationIterator;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * <p>
 * JSF 1.0 PRD2, 6.1.1
 * </p>
 * <p>
 * See MyFaces project for servlet implementation.
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 */
public class PortletExternalContextImpl extends ExternalContext
{
    private static final Log log = LogFactory.getLog(PortletExternalContextImpl.class);

    /** The init parameter map attribute. */
    private static final String INIT_PARAMETER_MAP_ATTRIBUTE = InitParameterMap.class.getName();

    /** The portlet context. */
    private PortletContext portletContext;

    /** The portlet request. */
    private PortletRequest portletRequest;

    /** The portlet response. */
    private PortletResponse portletResponse;

    /** The application map. */
    private Map applicationMap;

    /** The session map. */
    private Map sessionMap;

    /** The request map. */
    private Map requestMap;

    /** The request parameter map. */
    private Map requestParameterMap;

    /** The request parameter values map. */
    private Map requestParameterValuesMap;

    /** The request header map. */
    private Map requestHeaderMap;

    /** The request header values map. */
    private Map requestHeaderValuesMap;

    /** The request cookie map. */
    private Map requestCookieMap;

    /** The init parameter map. */
    private Map initParameterMap;

    /** The request path info. */
    private String requestPathInfo;

    /** The request servlet path. */
    private String requestServletPath;

    /**
     * @param portletContext The {@link PortletContext}.
     * @param portletRequest The {@link PortletRequest}.
     * @param portletResponse The {@link PortletResponse}.
     */
    public PortletExternalContextImpl(PortletContext portletContext, PortletRequest portletRequest,
            PortletResponse portletResponse)
    {
        this.portletContext = portletContext;
        this.portletRequest = portletRequest;
        this.portletResponse = portletResponse;
        this.applicationMap = null;
        this.sessionMap = null;
        this.requestMap = null;
        this.requestParameterMap = null;
        this.requestParameterValuesMap = null;
        this.requestHeaderMap = null;
        this.requestHeaderValuesMap = null;
        this.requestCookieMap = null;
        this.initParameterMap = null;
        this.requestPathInfo = null;
        this.requestServletPath = null;
    }

    /**
     * <p>
     * Reset the member variables.
     * </p>
     */
    public void release()
    {
        this.portletContext = null;
        this.portletRequest = null;
        this.portletResponse = null;
        this.applicationMap = null;
        this.sessionMap = null;
        this.requestMap = null;
        this.requestParameterMap = null;
        this.requestParameterValuesMap = null;
        this.requestHeaderMap = null;
        this.requestHeaderValuesMap = null;
        this.requestCookieMap = null;
        this.initParameterMap = null;
        this.requestPathInfo = null;
        this.requestServletPath = null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getSession(boolean)
     */
    public Object getSession(boolean create)
    {
        return this.portletRequest.getPortletSession(create);
    }

    /**
     * @see javax.faces.context.ExternalContext#getContext()
     */
    public Object getContext()
    {
        return this.portletContext;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequest()
     */
    public Object getRequest()
    {
        return this.portletRequest;
    }

    /**
     * @see javax.faces.context.ExternalContext#getResponse()
     */
    public Object getResponse()
    {
        return this.portletResponse;
    }

    /**
     * @see javax.faces.context.ExternalContext#getApplicationMap()
     */
    public Map getApplicationMap()
    {
        if (this.applicationMap == null)
        {
            this.applicationMap = new ApplicationMap(this.portletContext);
        }
        return this.applicationMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getSessionMap()
     */
    public Map getSessionMap()
    {
        if (this.sessionMap == null)
        {
            this.sessionMap = new SessionMap(this.portletRequest);
        }
        return this.sessionMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestMap()
     */
    public Map getRequestMap()
    {
        if (this.requestMap == null)
        {
            this.requestMap = new RequestMap(this.portletRequest);
        }
        return this.requestMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestParameterMap()
     */
    public Map getRequestParameterMap()
    {
        if (this.requestParameterMap == null)
        {
            this.requestParameterMap = new RequestParameterMap(this.portletRequest);
        }
        return this.requestParameterMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestParameterValuesMap()
     */
    public Map getRequestParameterValuesMap()
    {
        if (this.requestParameterValuesMap == null)
        {
            this.requestParameterValuesMap = new RequestParameterValuesMap(this.portletRequest);
        }
        return this.requestParameterValuesMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestParameterNames()
     */
    public Iterator getRequestParameterNames()
    {
        final Enumeration enum = this.portletRequest.getParameterNames();
        Iterator it = new Iterator()
        {
            public boolean hasNext()
            {
                return enum.hasMoreElements();
            }

            public Object next()
            {
                return enum.nextElement();
            }

            public void remove()
            {
                throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
            }
        };
        return it;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestHeaderMap()
     */
    public Map getRequestHeaderMap()
    {
        // TODO Hack to fix issue with MyFaces 1.0.6
        if (this.requestHeaderMap == null)
        {
            requestHeaderMap = new HashMap();
            requestHeaderMap.put("Content-Type", portletRequest.getResponseContentType());
        }
        return requestHeaderMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestHeaderValuesMap()
     */
    public Map getRequestHeaderValuesMap()
    {
        return requestHeaderValuesMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestCookieMap()
     */
    public Map getRequestCookieMap()
    {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestLocale()
     */
    public Locale getRequestLocale()
    {
        return this.portletRequest.getLocale();
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestPathInfo()
     */
    public String getRequestPathInfo()
    {
        if (null == this.requestPathInfo)
        {
            this.requestPathInfo = (String) this.portletRequest.getAttribute("javax.servlet.include.path_info");
        }
        return this.requestPathInfo;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestContextPath()
     */
    public String getRequestContextPath()
    {
        return this.portletRequest.getContextPath();
    }

    /**
     * @see javax.faces.context.ExternalContext#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String s)
    {
        return this.portletContext.getInitParameter(s);
    }

    /**
     * @see javax.faces.context.ExternalContext#getInitParameterMap()
     */
    public Map getInitParameterMap()
    {
        if (this.initParameterMap == null)
        {
            if ((this.initParameterMap = (Map) this.portletContext.getAttribute(INIT_PARAMETER_MAP_ATTRIBUTE)) == null)
            {
                this.initParameterMap = new InitParameterMap(this.portletContext);
                this.portletContext.setAttribute(INIT_PARAMETER_MAP_ATTRIBUTE, this.initParameterMap);
            }
        }
        return this.initParameterMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getResourcePaths(java.lang.String)
     */
    public Set getResourcePaths(String s)
    {
        return this.portletContext.getResourcePaths(s);
    }

    /**
     * @see javax.faces.context.ExternalContext#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream(String s)
    {
        return this.portletContext.getResourceAsStream(s);
    }

    /**
     * @see javax.faces.context.ExternalContext#encodeActionURL(java.lang.String)
     */
    public String encodeActionURL(String s)
    {
        return this.portletResponse.encodeURL(s);
    }

    /**
     * @see javax.faces.context.ExternalContext#encodeResourceURL(java.lang.String)
     */
    public String encodeResourceURL(String s)
    {
        return this.portletResponse.encodeURL(s);
    }

    /**
     * @see javax.faces.context.ExternalContext#encodeNamespace(java.lang.String)
     */
    public String encodeNamespace(String s)
    {
        return s;
    }

    /**
     * @see javax.faces.context.ExternalContext#dispatch(java.lang.String)
     */
    public void dispatch(String requestURI) throws IOException, FacesException
    {
        if (!(this.portletResponse instanceof RenderResponse))
        {
            throw new IllegalArgumentException("Only RenderResponse can be dispatched");
        }
        if (!(this.portletRequest instanceof RenderRequest))
        {
            throw new IllegalArgumentException("Only RenderRequest can be dispatched");
        }
        PortletRequestDispatcher portletRequestDispatcher = this.portletContext.getRequestDispatcher(requestURI);
        try
        {
            portletRequestDispatcher
                    .include((RenderRequest) this.portletRequest, (RenderResponse) this.portletResponse);
        }
        catch (PortletException e)
        {
            if (e.getMessage() != null)
            {
                throw new FacesException(e.getMessage(), e);
            }
            else
            {
                throw new FacesException(e);
            }
        }
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestServletPath()
     */
    public String getRequestServletPath()
    {
        return (String) this.portletRequest.getAttribute(FacesPortlet.VIEW_ID);
    }

    /**
     * @see javax.faces.context.ExternalContext#getAuthType()
     */
    public String getAuthType()
    {
        return this.portletRequest.getAuthType();
    }

    /**
     * @see javax.faces.context.ExternalContext#getRemoteUser()
     */
    public String getRemoteUser()
    {
        return this.portletRequest.getRemoteUser();
    }

    /**
     * @see javax.faces.context.ExternalContext#isUserInRole(java.lang.String)
     */
    public boolean isUserInRole(String role)
    {
        return this.portletRequest.isUserInRole(role);
    }

    /**
     * @see javax.faces.context.ExternalContext#getUserPrincipal()
     */
    public Principal getUserPrincipal()
    {
        return this.portletRequest.getUserPrincipal();
    }

    /**
     * @see javax.faces.context.ExternalContext#log(java.lang.String)
     */
    public void log(String message)
    {
        this.portletContext.log(message);
    }

    /**
     * @see javax.faces.context.ExternalContext#log(java.lang.String, java.lang.Throwable)
     */
    public void log(String message, Throwable t)
    {
        this.portletContext.log(message, t);
    }

    /**
     * @see javax.faces.context.ExternalContext#redirect(java.lang.String)
     */
    public void redirect(String url) throws IOException
    {
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestLocales()
     */
    public Iterator getRequestLocales()
    {
        return new EnumerationIterator(this.portletRequest.getLocales());
    }

    /**
     * @see javax.faces.context.ExternalContext#getResource(java.lang.String)
     */
    public URL getResource(String s) throws MalformedURLException
    {
        return this.portletContext.getResource(s);
    }
}