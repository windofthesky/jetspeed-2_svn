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
package org.apache.jetspeed.engine.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.PortletDispatcherIncludeAware;
import org.apache.jetspeed.container.namespace.JetspeedNamespaceMapper;
import org.apache.jetspeed.container.namespace.JetspeedNamespaceMapperFactory;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.util.Enumerator;

/**
 * This request wrappers the servlet request and is used within the container to
 * communicate to the invoked servlet.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class ServletRequestImpl extends HttpServletRequestWrapper implements PortletDispatcherIncludeAware
{
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    /** Logger */
    private static final Log log = LogFactory.getLog(ServletRequestImpl.class);

    PortletWindow portletWindow = null;
    private JetspeedNamespaceMapper nameSpaceMapper = null;
    private ServletRequest currentRequest = null;

    private Map portletParameters;
    private ObjectID webAppId;
    
    private boolean included;

    public ServletRequestImpl( HttpServletRequest servletRequest, PortletWindow window )
    {
        super(servletRequest);
        nameSpaceMapper = ((JetspeedNamespaceMapperFactory) Jetspeed.getComponentManager().getComponent(
                org.apache.pluto.util.NamespaceMapper.class)).getJetspeedNamespaceMapper();
        this.portletWindow = window;        
        PortletDefinition portletDef = portletWindow.getPortletEntity().getPortletDefinition();
        if(portletDef != null)
        {
            webAppId = portletDef.getPortletApplicationDefinition().getWebApplicationDefinition().getId();
        }
        else
        {
            // This happens when an entity is referencing a non-existent portlet
            webAppId = window.getId();
        }
    }

    protected HttpServletRequest _getHttpServletRequest()
    {
        return (HttpServletRequest) super.getRequest();
    }

    //  ServletRequestWrapper overlay

    public String getParameter( String name )
    {
        Object value = this.getParameterMap().get(name);
        if (value == null)
        {
            return (null);
        }
        else if (value instanceof String[])
        {
            return (((String[]) value)[0]);
        }
        else if (value instanceof String)
        {
            return ((String) value);
        }
        else
        {
            return (value.toString());
        }
    }

    public Map getParameterMap()
    {
        if (currentRequest == null || currentRequest != getRequest() )
        {
            // Cache the parameters for as long as the wrapped request stays the same.
            // According to Servlet 2.3 SRV.6.2.2 the passed on ServletRequest object
            // to an dispatched Servlet must remain the same (this one).
            // Tomcat solves this by injecting a new ServletRequest of its own above
            // this one (the getRequest() object).
            // So, when that one has changed since the last time the parameters have 
            // been accessed, flush the cache and rebuild the map.
            currentRequest = getRequest();            
            portletParameters = new HashMap();

            // get portlet params
            JetspeedRequestContext context = (JetspeedRequestContext) getAttribute("org.apache.jetspeed.request.RequestContext");
            if (context != null)
            {
                PortalURL url = context.getPortalURL();
                Iterator iter = url.getNavigationalState().getParameterNames(portletWindow);
                while (iter.hasNext())
                {
                    String name = (String) iter.next();
                    String[] values = url.getNavigationalState().getParameterValues(portletWindow, name);
                    portletParameters.put(name, values);

                }
            }

            String encoding = (String) getRequest().getAttribute(PortalReservedParameters.PREFERED_CHARACTERENCODING_ATTRIBUTE);
            boolean decode = getRequest().getAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE) == null
                    && encoding != null;
            if (decode)
            {
                getRequest().setAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE,
                        new Boolean(true));
            }

            //get servlet params
            for (Enumeration parameters = getRequest().getParameterNames(); parameters.hasMoreElements();)
            {
                String paramName = (String) parameters.nextElement();
                String[] paramValues = (String[]) getRequest().getParameterValues(paramName);
                String[] values = (String[]) portletParameters.get(paramName);

                if (decode)
                {
                    for (int i = 0; i < paramValues.length; i++)
                    {
                        try
                        {
                            paramValues[i] = new String(paramValues[i].getBytes("ISO-8859-1"), encoding);
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            ;
                        }
                    }
                }

                if (values != null)
                {
                    String[] temp = new String[paramValues.length + values.length];
                    System.arraycopy(paramValues, 0, temp, 0, paramValues.length);
                    System.arraycopy(values, 0, temp, paramValues.length, values.length);
                    paramValues = temp;
                }
                portletParameters.put(paramName, paramValues);
            }
        }
        return Collections.unmodifiableMap(portletParameters);

    }

    public Enumeration getParameterNames()
    {
        return Collections.enumeration(this.getParameterMap().keySet());
    }

    public String[] getParameterValues( String name )
    {
        return (String[]) this.getParameterMap().get(name);
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getAttribute(java.lang.String)
     */
    public Object getAttribute( String name )
    {
        Object value = super.getAttribute(name);
        if (name.equals(PortletRequest.USER_INFO))
        {
            JetspeedRequestContext context = (JetspeedRequestContext) getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
            if (null != context)
            {
                String entityID = "--NULL--";
                PortletEntity entity = portletWindow.getPortletEntity();
                if (entity != null)
                {
                    entityID = entity.getId().toString();
                }
                PortletApplicationEntity portletAppEntity = portletWindow.getPortletEntity()
                        .getPortletApplicationEntity();
                PortletApplicationDefinition portletAppDef = entity.getPortletDefinition()
                        .getPortletApplicationDefinition();

                if (null != portletAppDef)
                {
                    value = context.getUserInfoMap(portletAppDef.getId());
                    if (log.isDebugEnabled() && (null != value))
                        log.debug(PortletRequest.USER_INFO + " map size: " + ((Map) value).size());
                }
                else
                {
                    log.error("Entity is null:" + entityID);
                }

            }
        }
        else
        {
            if (null == value)
            {
                PortletRequest pr = (PortletRequest) super.getAttribute("javax.portlet.request");
                if (pr != null)
                {
                    value = super.getAttribute(nameSpaceMapper.encode(portletWindow.getId(),
                            name));
                }
            }
        }
        return value;
    }

    /**
     * @see javax.servlet.ServletRequest#getLocale()
     */
    public Locale getLocale()
    {
        //Locale preferedLocale = (Locale) getSession().getAttribute(RequestContext.PREFERED_LOCALE_SESSION_KEY);
        RequestContext requestContext = (RequestContext) _getHttpServletRequest().getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        Locale preferedLocale = requestContext.getLocale();
        if (preferedLocale != null)
        {
            return preferedLocale;
        }

        return super.getLocale();
    }

    /**
     * @see javax.servlet.ServletRequest#getLocales()
     */
    public Enumeration getLocales()
    {
        RequestContext requestContext = (RequestContext) _getHttpServletRequest().getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        Locale preferedLocale = requestContext.getLocale();
        if (preferedLocale != null)
        {
            return getLocaleEnum(preferedLocale);
        }

        return super.getLocales();
    }

    /**
     * <p>
     * getLocaleEnum
     * </p>
     *
     * @param preferedLocale
     * @return
     */
    protected Enumeration getLocaleEnum( Locale preferedLocale )
    {
        ArrayList locales = new ArrayList();
        locales.add(preferedLocale);
        Enumeration localeEnums = super.getLocales();
        while (localeEnums.hasMoreElements())
        {
            locales.add(localeEnums.nextElement());
        }
        Iterator i = locales.iterator();
        return new Enumerator(locales);
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
     */
    public String getHeader( String name )
    {
        if(name.equals(ACCEPT_LANGUAGE))
        {
            return getLocale().getLanguage();   
        }
        else
        {
            return super.getHeader(name);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
     */
    public Enumeration getHeaders( String name )
    {
        if(name.equals(ACCEPT_LANGUAGE))
        {      
            return getLocaleEnum(getLocale());         
        }
        else
        {
            return super.getHeaders(name);
        }        

    }

    /**
     * <p>
     * setAttribute
     * </p>
     * 
     * @see javax.servlet.ServletRequest#setAttribute(java.lang.String,
     *      java.lang.Object)
     * @param arg0
     * @param arg1
     */
    public void setAttribute( String name, Object value )
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Attribute name == null");
        }

        // This allows us to make jetpseed objects avaiable to portlets
        // This makes the portlet non-portable but is a must admin portlets
        if (name.startsWith("org.apache.jetspeed"))
        {
            if (value == null)
            {
                this.removeAttribute(name);
            }
            else
            {
                String encodedKey = nameSpaceMapper.encode(portletWindow.getId(), name);
                this._getHttpServletRequest().setAttribute(
                        encodedKey, value);
            }
        }
        super.setAttribute(name, value);
    }
    /**
     * <p>
     * getHeaderNames
     * </p>
     *
     * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
     * @return
     */
    public Enumeration getHeaderNames()
    {
        return super.getHeaderNames();
    }
    
    /**
     * @param included when true, JSR-168 PLT.16.3.3 rules need to be enforced
     */
    public void setPortletDispatcherIncluded(boolean included)
    {
        this.included = included;
    }
    
    /*
     * JSR-168 PLT.16.3.3 cxxix
     */
  	public String getProtocol()
  	{
        return (included ? null : super.getProtocol() );
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxix
     */
  	public String getRemoteAddr()
  	{
        return (included ? null : super.getRemoteAddr() );
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxix
     */
  	public String getRemoteHost()
  	{
        return (included ? null : super.getRemoteHost() );
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxix
     */
  	public StringBuffer getRequestURL()
  	{
        return (included ? null : super.getRequestURL());
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxx
     */
    public String getPathInfo()
  	{
        return (included ? (String)super.getAttribute("javax.servlet.include.path_info") : super.getPathInfo());
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxx
     */
  	public String getPathTranslated()
  	{
        return (included ? null : super.getPathTranslated());
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxx
     */
  	public String getQueryString()
  	{
        return (included ? (String)super.getAttribute("javax.servlet.include.query_string") : super.getQueryString());
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxx
     */
  	public String getRequestURI()
  	{
        return (included ? (String)super.getAttribute("javax.servlet.include.request_uri") : super.getRequestURI());
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxx
     */
  	public String getServletPath()
  	{
        return (included ? (String)super.getAttribute("javax.servlet.include.servlet_path") : super.getServletPath());
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxxi
     */
  	public String getContextPath() 
  	{
        return (included ? (String)super.getAttribute("javax.servlet.include.context_path") : super.getContextPath());
  	}

    /*
     * JSR-168 PLT.16.3.3 cxxxiv
     */
    public int getContentLength()
    {
        return (included ? 0 : super.getContentLength());
    }

    /*
     * JSR-168 PLT.16.3.3 cxxix
     */
    public String getRealPath(String arg0)
    {
        return (included ? null : super.getRealPath(arg0));
    }

    /*
     * JSR-168 PLT.16.3.3 cxxxii
     */
    public BufferedReader getReader() throws IOException
    {
        return (included ? null : super.getReader());
    }

    /*
     * JSR-168 PLT.16.3.3 cxxxii
     */
    public String getCharacterEncoding()
    {
        return (included ? null : super.getCharacterEncoding());
    }

    /*
     * JSR-168 PLT.16.3.3 cxxxii
     */
    public String getContentType()
    {
        return (included ? null : super.getContentType());
    }

    /*
     * JSR-168 PLT.16.3.3 cxxxii
     */
    public ServletInputStream getInputStream() throws IOException
    {
        return (included ? null : super.getInputStream());
    }

    /*
     * JSR-168 PLT.16.3.3 cxxxii
     */
    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException
    {
        if ( !included )
        {
            super.setCharacterEncoding(arg0);
        }
    }
}
