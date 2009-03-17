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
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.namespace.JetspeedNamespaceMapper;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;

/**
 * This request wrappers the servlet request and is used within the container to
 * communicate to the invoked servlet.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class ServletRequestImpl extends HttpServletRequestWrapper
{
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    /** Logger */
    private static final Log log = LogFactory.getLog(ServletRequestImpl.class);

    PortletWindow portletWindow = null;
    private JetspeedNamespaceMapper nameSpaceMapper = null;

    private Map<String, String[]> portletParameters;
    
    private static Boolean mergePortalParametersWithPortletParameters;
    private static Boolean mergePortalParametersBeforePortletParameters;
    
    // request attributes map which is cached for each paralleled worker.
    // this should be re-created when it is called for the first time or when some attributes are added/modified/removed.
    private Map<String, Object> cachedAttributes;

    public ServletRequestImpl(HttpServletRequest servletRequest, PortletWindow window, JetspeedNamespaceMapper namespaceMapper)
    {
        super(servletRequest);
        this.nameSpaceMapper = namespaceMapper;
        this.portletWindow = window;        
    }
    
    private boolean getMetaDataBooleanValue(GenericMetadata metaData, String fieldName, boolean defaultValue )
    {
        String value = null;
        if ( metaData != null )
        {
            Collection<LocalizedField> fields = metaData.getFields(fieldName);
            if ( fields != null && !fields.isEmpty() )
            {
                value = fields.iterator().next().getValue();
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

    @SuppressWarnings("unchecked")
    public Map<String, String[]> getParameterMap()
    {
        if (portletParameters == null)
        {
            HttpServletRequest servletRequest = this._getHttpServletRequest();


            if (mergePortalParametersWithPortletParameters == null )
            {
                mergePortalParametersWithPortletParameters = 
                    new Boolean(Jetspeed.getContext().getConfiguration().getBoolean("merge.portal.parameters.with.portlet.parameters", false));
                mergePortalParametersBeforePortletParameters = 
                    new Boolean(Jetspeed.getContext().getConfiguration().getBoolean("merge.portal.parameters.before.portlet.parameters", false));
            }
            
            boolean mergeRequestParameters = false;
            boolean mergeRequestParametersBefore = false;
            
            // get portlet *private* navigational params
            portletParameters = new HashMap<String, String[]>();
            JetspeedRequestContext context = (JetspeedRequestContext) getAttribute("org.apache.jetspeed.request.RequestContext");
            if (context != null)
            {
                NavigationalState ns = context.getPortalURL().getNavigationalState();
                mergeRequestParameters = ns.getPortletWindowOfAction() != null || ns.getPortletWindowOfResource() != null;
                Iterator<String> iter = ns.getParameterNames(portletWindow);
                while (iter.hasNext())
                {
                    String name = iter.next();
                    String[] values = ns.getParameterValues(portletWindow, name);
                    portletParameters.put(name, values);
                }
            }
            
            PortletDefinition portletDef = portletWindow.getPortletEntity().getPortletDefinition();
            if(portletDef != null)
            {
                GenericMetadata metaData = portletDef.getMetadata();
                if (!mergeRequestParameters)
                {
                    mergeRequestParameters = 
                        getMetaDataBooleanValue(
                            metaData,
                            PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_MERGE_PORTAL_PARAMETERS_WITH_PORTLET_PARAMETERS,
                            mergePortalParametersWithPortletParameters.booleanValue());
                }
                mergeRequestParametersBefore = 
                    getMetaDataBooleanValue(
                        metaData,
                        PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_MERGE_PORTAL_PARAMETERS_BEFORE_PORTLET_PARAMETERS,
                        mergePortalParametersBeforePortletParameters.booleanValue());
                
            }
            else
            {
                // This happens when an entity is referencing a non-existent portlet
            }
            
            //get request params
            if (mergeRequestParameters)
            {
                String encoding = (String) servletRequest.getAttribute(PortalReservedParameters.PREFERED_CHARACTERENCODING_ATTRIBUTE);
                boolean decode = servletRequest.getAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE) == null
                        && encoding != null;
                if (decode)
                {
                    servletRequest.setAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE,
                            new Boolean(true));
                }
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
                    String[] navValues = portletParameters.get(paramName);
                    if (navValues == null)
                    {
                        portletParameters.put(paramName, paramValues);
                    }
                    else
                    {
                        String[] combined = new String[navValues.length+paramValues.length];
                        if (mergeRequestParametersBefore)
                        {
                            System.arraycopy(paramValues,0,combined,0,paramValues.length);
                            System.arraycopy(navValues,0,combined,paramValues.length,navValues.length);
                        }
                        else
                        {
                            System.arraycopy(navValues,0,combined,0,navValues.length);
                            System.arraycopy(paramValues,0,combined,navValues.length,paramValues.length);
                        }
                        portletParameters.put(paramName, combined);
                    }
                }
            }
        }
        if (!portletParameters.isEmpty())
        {
            Map<String, String[]> result = new HashMap<String, String[]>(portletParameters.size());
            for (Map.Entry<String,String[]> entry : portletParameters.entrySet())
            {
                if (entry.getValue() != null)
                {
                    result.put(entry.getKey(), entry.getValue().clone());
                }
            }
            return Collections.unmodifiableMap(result);
        }
        else
        {
            return Collections.emptyMap();
        }
    }

    public Enumeration<String> getParameterNames()
    {
        return Collections.enumeration(getParameterMap().keySet());
    }

    public String getParameter( String name )
    {
        String[] values = getParameterMap().get(name);
        return values == null ? null : values[0];
    }
    
    public String[] getParameterValues( String name )
    {
        return getParameterMap().get(name);
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getAttributeNames()
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getAttributeNames()
    {
        Enumeration<String> attrNames = super.getAttributeNames();
        
        // In parallel mode, adjust attributes by the values of the current thread

        if (CurrentWorkerContext.getParallelRenderingMode())
        {
            // If cached attributes map is null, it should be re-created.
            
            if (cachedAttributes == null)
            {
                HashMap<String,Object> adjustedAttrMap = new HashMap<String, Object>();
                
                // first, add all attributes of original request.
                
                while (attrNames.hasMoreElements())
                {
                    String key = attrNames.nextElement();
                    adjustedAttrMap.put(key, super.getAttribute(key));
                }
                
                // second, add or override all attributes by the current worker context.
                
                Enumeration<String> cwAttrNames = CurrentWorkerContext.getAttributeNames();
                
                while (cwAttrNames.hasMoreElements())
                {
                    String key = cwAttrNames.nextElement();
                    adjustedAttrMap.put(key, CurrentWorkerContext.getAttribute(key));
                }
                
                cachedAttributes = adjustedAttrMap;
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
        }
        
        // If no attribute found, then look up from the request
        if (null == value) 
        {
            value = super.getAttribute(name);
        }
        if (value == value)
        {
            value = super.getAttribute(nameSpaceMapper.encode(portletWindow.getId(),name));
        }

        return value;
    }

    /**
     * @see javax.servlet.ServletRequest#getLocale()
     */
    public Locale getLocale()
    {
        RequestContext requestContext = (RequestContext) _getHttpServletRequest().getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        Locale preferedLocale = requestContext.getLocale();
        if (preferedLocale != null)
        {
            return preferedLocale;
        }

        return super.getLocale();
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
            
            if (name.startsWith("org.apache.jetspeed"))
            {
                super.removeAttribute(name);
            }
        }
        else
        {
            // remove attribute from request.
            super.removeAttribute(name);
        }        
    }
}
