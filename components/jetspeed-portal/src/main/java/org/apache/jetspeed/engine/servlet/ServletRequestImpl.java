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
package org.apache.jetspeed.engine.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
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
import org.apache.jetspeed.aggregator.CurrentWorkerContext;
import org.apache.jetspeed.container.PortletDispatcherIncludeAware;
import org.apache.jetspeed.container.namespace.JetspeedNamespaceMapper;
import org.apache.jetspeed.container.namespace.JetspeedNamespaceMapperFactory;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.jetspeed.container.PortletWindow;
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
    
    private boolean included;

    private static Boolean mergePortalParametersWithPortletParameters;
    private static Boolean mergePortalParametersBeforePortletParameters;
    
    private boolean portletMergePortalParametersWithPortletParameters;
    private boolean portletMergePortalParametersBeforePortletParameters;
    
    private Map portalParameters;
    
    private String currentIncludeQueryString;    
    private String currentForwardQueryString;    
    
    // request attributes map which is cached for each paralleled worker.
    // this should be re-created when it is called for the first time or when some attributes are added/modified/removed.
    private Map cachedAttributes;

    public ServletRequestImpl( HttpServletRequest servletRequest, PortletWindow window )
    {
        super(servletRequest);
        nameSpaceMapper = ((JetspeedNamespaceMapperFactory) Jetspeed.getComponentManager().getComponent(
                org.apache.pluto.util.NamespaceMapper.class)).getJetspeedNamespaceMapper();
        this.portletWindow = window;        
        
        
        String encoding = (String) servletRequest.getAttribute(PortalReservedParameters.PREFERED_CHARACTERENCODING_ATTRIBUTE);
        boolean decode = servletRequest.getAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE) == null
                && encoding != null;
        if (decode)
        {
            servletRequest.setAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE,
                    new Boolean(true));
        }

        //get portal servlet params
        portalParameters = new HashMap();
        for (Enumeration parameters = servletRequest.getParameterNames(); parameters.hasMoreElements();)
        {
            String paramName = (String) parameters.nextElement();
            String[] paramValues = servletRequest.getParameterValues(paramName);

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
            portalParameters.put(paramName, paramValues);
        }
        
        if (mergePortalParametersWithPortletParameters == null )
        {
            mergePortalParametersWithPortletParameters = 
                new Boolean(Jetspeed.getContext().getConfiguration().getBoolean("merge.portal.parameters.with.portlet.parameters", false));
        }
        
        if (mergePortalParametersBeforePortletParameters == null)
        {
            mergePortalParametersBeforePortletParameters = 
                new Boolean(Jetspeed.getContext().getConfiguration().getBoolean("merge.portal.parameters.before.portlet.parameters", false));
        }
                
        
        PortletDefinition portletDef = (PortletDefinition)portletWindow.getPortletEntity().getPortletDefinition();
        if(portletDef != null)
        {
            GenericMetadata metaData = portletDef.getMetadata();

            portletMergePortalParametersWithPortletParameters = 
                getMetaDataBooleanValue(
                    metaData,
                    PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_MERGE_PORTAL_PARAMETERS_WITH_PORTLET_PARAMETERS,
                    mergePortalParametersWithPortletParameters.booleanValue());
            portletMergePortalParametersBeforePortletParameters = 
                getMetaDataBooleanValue(
                    metaData,
                    PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_MERGE_PORTAL_PARAMETERS_BEFORE_PORTLET_PARAMETERS,
                    mergePortalParametersBeforePortletParameters.booleanValue());
            
        }
        else
        {
            // This happens when an entity is referencing a non-existent portlet
            portletMergePortalParametersWithPortletParameters = mergePortalParametersWithPortletParameters.booleanValue();
            portletMergePortalParametersBeforePortletParameters = mergePortalParametersBeforePortletParameters.booleanValue();
        }
    }
    
    private boolean getMetaDataBooleanValue(GenericMetadata metaData, String fieldName, boolean defaultValue )
    {
        String value = null;
        if ( metaData != null )
        {
            Collection fields = metaData.getFields(fieldName);
            if ( fields != null && !fields.isEmpty() )
            {
                value = ((LocalizedField)fields.iterator().next()).getValue();
            }
        }
        if ( value != null )
        {
            return Boolean.valueOf(value).booleanValue();
        }
        return defaultValue;
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
    
    private boolean isEqual(String one, String two)
    {
        return (one == null && two == null) || (one != null && two != null && one.equals(two));
    }
    
    private boolean checkQueryStringChanged()
    {
        boolean changed = false;
        ServletRequest request = getRequest();
        String includeQueryString = (String)request.getAttribute("javax.servlet.include.query_string");        
        String forwardQueryString = (String)request.getAttribute("javax.servlet.forward.query_string");
        
        if (!isEqual(currentIncludeQueryString,includeQueryString))
        {
            currentIncludeQueryString = includeQueryString;
            changed = true;
        }
        if (!isEqual(currentForwardQueryString,forwardQueryString))
        {
            currentForwardQueryString = forwardQueryString;
            changed = true;
        }        
        return changed;
    }

    public Map getParameterMap()
    {
        // if included or forwarded with a query string, parameterMap might have changed
        // this is/should be the only check needed, and the other "tricky" check below probably
        // can be removed.
        // I'll keep it in for now though as it hasn't been tested enough on other app servers
        boolean queryStringChanged = checkQueryStringChanged();
        
        if (queryStringChanged || currentRequest == null || currentRequest != getRequest() )
        {
            // Cache the parameters for as long as the wrapped request stays the same.
            // According to Servlet 2.3 SRV.6.2.2 the passed on ServletRequest object
            // to an dispatched Servlet must remain the same (this one).
            // Tomcat solves this by injecting a new ServletRequest of its own above
            // this one (the getRequest() object).
            // So, when that one has changed since the last time the parameters have 
            // been accessed, flush the cache and rebuild the map.
            currentRequest = getRequest();

            boolean postAllowed = false;
            
            // determine the possible additional query string parameters provided on the RequestDispatcher include path
            // per the specs, these are prepended to existing parameters or altogether new parameters
            // as we save the original "portal" parameters, we can find those query string parameters by comparing against those
            HashMap queryParameters = new HashMap();
            for ( Iterator iter = getRequest().getParameterMap().entrySet().iterator(); iter.hasNext(); )
            {
                Map.Entry entry = (Map.Entry)iter.next();
                String[] values = (String[])entry.getValue();
                String[] original = (String[])portalParameters.get(entry.getKey());
                String[] diff = null;
                if ( original == null )
                {
                    // a new parameter
                    diff = new String[values.length];
                    System.arraycopy(values,0,diff,0,values.length);
                }
                else if ( values.length > original.length )
                {
                    // we've got some additional query string parameter value(s)
                    diff = new String[values.length - original.length];
                    System.arraycopy(values,0,diff,0,values.length-original.length);
                }
                if ( diff != null )
                {
                    queryParameters.put(entry.getKey(), diff);
                }
            }

            // get portlet navigational params
            HashMap navParameters = new HashMap();
            JetspeedRequestContext context = (JetspeedRequestContext) getAttribute("org.apache.jetspeed.request.RequestContext");
            if (context != null)
            {
                NavigationalState ns = context.getPortalURL().getNavigationalState();
                postAllowed = ns.getPortletWindowOfAction() != null || ns.getPortletWindowOfResource() != null;
                Iterator iter = ns.getParameterNames(portletWindow);
                while (iter.hasNext())
                {
                    String name = (String) iter.next();
                    String[] values = ns.getParameterValues(portletWindow, name);
                    navParameters.put(name, values);
                }
            }
            
            // now first merge the keys we have into one unique set
            HashSet keys = new HashSet();
            keys.addAll(portalParameters.keySet());
            keys.addAll(queryParameters.keySet());
            keys.addAll(navParameters.keySet());
            
            // now "merge" the parameters
            // there are three different options:
            // 1) query parameters + nav parameters:
            //        portletMergePortalParametersWithPortletParameters == false && !actionRequest
            // 2) query parameters + nav parameters + portal parameters
            //           portletMergePortalParametersWithPortletParameters == true || actionRequest
            //        && portletMergePortalParametersBeforePortletParameters == false
            // 3) query parameters + portal parameters + nav parameters (odd use-case but provided because this was the "old" pre-2.1 behavior
            //           portletMergePortalParametersWithPortletParameters == true || actionRequest
            //        && portletMergePortalParametersBeforePortletParameters == true
            portletParameters = new HashMap();
            for ( Iterator iter = keys.iterator(); iter.hasNext(); )
            {
                String key = (String)iter.next();
                String[] first = (String[])queryParameters.get(key);
                String[] next = null, last = null, result = null;
                
                if ( portletMergePortalParametersWithPortletParameters == false && !postAllowed )
                {
                    next = (String[])navParameters.get(key);
                }
                else if ( portletMergePortalParametersBeforePortletParameters )
                {
                    next = (String[])portalParameters.get(key);
                    last = (String[])navParameters.get(key);
                }
                else
                {
                    next = (String[])navParameters.get(key);
                    last = (String[])portalParameters.get(key);
                }
                if ( first == null )
                {
                    if ( next == null )
                    {
                        first = last;
                        last = null;
                    }
                    else
                    {
                        first = next;
                        next = last;
                        last = null;
                    }
                }
                else if ( next == null )
                {
                    next = last;
                    last = null;
                }
                
                if ( last == null )
                {
                    if ( next == null && first != null )
                    {
                        result = new String[first.length];
                        System.arraycopy(first,0,result,0,first.length);
                    }
                    else if (next != null )
                    {
                        result = new String[first.length + next.length];
                        System.arraycopy(first,0,result,0,first.length);
                        System.arraycopy(next,0,result,first.length,next.length);
                    }
                }
                else
                {
                    result = new String[first.length + next.length + last.length];
                    System.arraycopy(first,0,result,0,first.length);
                    System.arraycopy(next,0,result,first.length,next.length);
                    System.arraycopy(last,0,result,first.length+next.length,last.length);
                    
                }
                if ( result != null )
                {
                    portletParameters.put(key, result);
                }
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
     * @see javax.servlet.http.HttpServletRequest#getAttributeNames()
     */
    public Enumeration getAttributeNames()
    {
        Enumeration attrNames = super.getAttributeNames();
        
        // In parallel mode, adjust attributes by the values of the current thread

        if (CurrentWorkerContext.getParallelRenderingMode())
        {
            // If cached attributes map is null, it should be re-created.
            
            if (cachedAttributes == null)
            {
                HashMap adjustedAttrMap = new HashMap();
                
                // first, add all attributes of original request.
                
                while (attrNames.hasMoreElements())
                {
                    String key = (String) attrNames.nextElement();
                    adjustedAttrMap.put(key, super.getAttribute(key));
                }
                
                // second, add or override all attributes by the current worker context.
                
                Enumeration cwAttrNames = CurrentWorkerContext.getAttributeNames();
                
                while (cwAttrNames.hasMoreElements())
                {
                    String key = (String) cwAttrNames.nextElement();
                    adjustedAttrMap.put(key, CurrentWorkerContext.getAttribute(key));
                }
                
                cachedAttributes = Collections.unmodifiableMap(adjustedAttrMap);
            }
            
            attrNames = Collections.enumeration(cachedAttributes.keySet());
        }
        
        return attrNames;
    }
    
    /**
     * @see javax.servlet.http.HttpServletRequest#getAttribute(java.lang.String)
     */
    public Object getAttribute( String name )
    {
        Object value = null;

        // In parallel mode, first look up from the worker.

        if (CurrentWorkerContext.getParallelRenderingMode())
        {            
            value = CurrentWorkerContext.getAttribute(name);

            // Because PortletRequestImpl class of pluto encodes the name of attribute before calling setAttribute(), 
            // we have to check the encoded name also.
            if (null == value)
            {
                // Extra code (2 lines) from Nicolas... not clear to me why this is needed, as "pr" is not used. Commenting out for now...
                //PortletRequest pr = (PortletRequest) super.getAttribute("javax.portlet.request");
                //if (pr != null)
                value = CurrentWorkerContext.getAttribute(nameSpaceMapper.encode(portletWindow.getId(), name));
            }
        }

        // If no attribute found, then look up from the request
        if (null == value) 
        {
            value = getAttributeInternal(name);
        }

        return value;
    }

    private Object getAttributeInternal( String name )
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
        
        // In parallel mode, put attribute into worker.

        if (CurrentWorkerContext.getParallelRenderingMode()) 
        {
            // when it is parallel rendering, the cached request attributes should be re-created later by setting it to null.
            cachedAttributes = null;
            
            if (null == value) 
            {
                CurrentWorkerContext.removeAttribute(name);
            } 
            else 
            {
                CurrentWorkerContext.setAttribute(name, value);
            }

            if (name.startsWith("org.apache.jetspeed"))
            {
                setAttributeInternal(name, value);
            }
        }
        else
        {
            // put attribute into request.
            setAttributeInternal(name, value);
        }
    }

    private void setAttributeInternal( String name, Object value )
    {
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
     * removeAttribute
     * </p>
     * 
     * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
     * @param arg0
     */
    public void removeAttribute( String name )
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Attribute name == null");
        }
        
        // In parallel mode, remove attribute from worker.

        if (CurrentWorkerContext.getParallelRenderingMode()) 
        {
            // when it is parallel rendering, the cached request attributes should be re-created later by setting it to null.
            cachedAttributes = null;
            
            CurrentWorkerContext.removeAttribute(name);
            
            if (name.startsWith("org.apache.jetspeed")) {
                super.removeAttribute(name);
            }
        }
        else
        {
            // remove attribute from request.
            super.removeAttribute(name);
        }        
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
