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
 * <p>JSF 1.0 PRD2, 6.1.1</p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat</a>
 */
public class PortletExternalContextImpl extends ExternalContext
{
	private static final Log log = LogFactory.getLog(PortletExternalContextImpl.class);
	
	private static final String INIT_PARAMETER_MAP_ATTRIBUTE = InitParameterMap.class.getName();
    
    private PortletContext portletContext;
    private PortletRequest portletRequest;
    private PortletResponse portletResponse;
    private Map applicationMap;
    private Map sessionMap;
    private Map requestMap;
    private Map requestParameterMap;
    private Map requestParameterValuesMap;
    private Map requestHeaderMap;
    private Map requestHeaderValuesMap;
    private Map requestCookieMap;
    private Map initParameterMap;
    private String requestPathInfo;
    private String requestServletPath;
    
    public PortletExternalContextImpl(PortletContext portletContext,
    								  PortletRequest portletRequest,
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


    public Object getSession(boolean create)
    {
        return this.portletRequest.getPortletSession(create);
    }

    public Object getContext()
    {
        return this.portletContext;
    }

    public Object getRequest()
    {
        return this.portletRequest;
    }

    public Object getResponse()
    {
        return this.portletResponse;
    }

    public Map getApplicationMap()
    {
        if (this.applicationMap == null)
        {
            this.applicationMap = new ApplicationMap(this.portletContext);
        }
        return this.applicationMap;
    }

    public Map getSessionMap()
    {
        if (this.sessionMap == null)
        {
            this.sessionMap = new SessionMap(this.portletRequest);
        }
        return this.sessionMap;
    }

    public Map getRequestMap()
    {
        if (this.requestMap == null)
        {
            this.requestMap = new RequestMap(this.portletRequest);
        }
        return this.requestMap;
    }

    public Map getRequestParameterMap()
    {
        if (this.requestParameterMap == null)
        {
            this.requestParameterMap = new RequestParameterMap(this.portletRequest);
        }
        return this.requestParameterMap;
    }

    public Map getRequestParameterValuesMap()
    {
        if (this.requestParameterValuesMap == null)
        {
            this.requestParameterValuesMap = new RequestParameterValuesMap(this.portletRequest);
        }
        return this.requestParameterValuesMap;
    }

    public Iterator getRequestParameterNames()
    {
        final Enumeration enum = this.portletRequest.getParameterNames();
        Iterator it = new Iterator()
        {
            public boolean hasNext() {
                return enum.hasMoreElements();
            }

            public Object next() {
                return enum.nextElement();
            }

            public void remove() {
                throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
            }
        };
        return it;
    }

    public Map getRequestHeaderMap()
    {
        return null;
    }

    public Map getRequestHeaderValuesMap()
    {
        return null;
    }

    public Map getRequestCookieMap()
    {
        return null;
    }

    public Locale getRequestLocale()
    {
        return this.portletRequest.getLocale();
    }

    public String getRequestPathInfo()
    {
    	if (null == this.requestPathInfo)
    	{
        	this.requestPathInfo = (String) this.portletRequest.getAttribute("javax.servlet.include.path_info");
    	}
    	return  this.requestPathInfo;
    }

    public String getRequestContextPath()
    {
        return this.portletRequest.getContextPath();
    }

    public String getInitParameter(String s)
    {
        return this.portletContext.getInitParameter(s);
    }

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

    public Set getResourcePaths(String s)
    {
        return this.portletContext.getResourcePaths(s);
    }

    public InputStream getResourceAsStream(String s)
    {
        return this.portletContext.getResourceAsStream(s);
    }

    public String encodeActionURL(String s)
    {
    	return this.portletResponse.encodeURL(s);
    }

    public String encodeResourceURL(String s)
    {
        return this.portletResponse.encodeURL(s);
    }

    public String encodeNamespace(String s)
    {
        return s;
    }

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
    	PortletRequestDispatcher portletRequestDispatcher
            = this.portletContext.getRequestDispatcher(requestURI);
        try
        {
        	portletRequestDispatcher.include((RenderRequest) this.portletRequest, (RenderResponse) this.portletResponse);
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

    public String getRequestServletPath()
    {
        return (String) this.portletRequest.getAttribute(FacesPortlet.VIEW_ID);
    }

    public String getAuthType()
    {
        return this.portletRequest.getAuthType();
    }

    public String getRemoteUser()
    {
        return this.portletRequest.getRemoteUser();
    }

    public boolean isUserInRole(String role)
    {
        return this.portletRequest.isUserInRole(role);
    }

    public Principal getUserPrincipal()
    {
        return this.portletRequest.getUserPrincipal();
    }

    public void log(String message) {
        this.portletContext.log(message);
    }

    public void log(String message, Throwable t) {
    	this.portletContext.log(message, t);
    }

    public void redirect(String url) throws IOException
    {
    }

    public Iterator getRequestLocales()
    {
        return new EnumerationIterator(this.portletRequest.getLocales());
    }

    public URL getResource(String s) throws MalformedURLException
    {
        return this.portletContext.getResource(s);
    }
}
