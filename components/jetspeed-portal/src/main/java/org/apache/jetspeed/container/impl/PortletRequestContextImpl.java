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

package org.apache.jetspeed.container.impl;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.jetspeed.container.PortletWindow;

/**
 * @version $Id$
 *
 */
public class PortletRequestContextImpl implements PortletRequestContext
{
    private static Boolean mergePortalParametersWithPortletParameters;
    private static Boolean mergePortalParametersBeforePortletParameters;
    
    private PortletContainer container;
    private HttpServletRequest containerRequest;
    private HttpServletResponse containerResponse;
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;
    private PortletWindow window;
    private PortletContext portletContext;
    private ServletContext servletContext;
    private Cookie cookies[];
    private boolean useRequestParameters;
    
    private Map<String, String[]> privateParameters;
    
    
    // request attributes map which is cached for each paralleled worker.
    // this should be re-created when it is called for the first time or when some attributes are added/modified/removed.
    private Map<String, Object> cachedAttributes;

    public PortletRequestContextImpl(PortletContainer container, HttpServletRequest containerRequest,
                                     HttpServletResponse containerResponse, PortletWindow window, boolean useRequestParameters)
    {
        this.container = container;
        this.containerRequest = containerRequest;
        this.containerResponse = containerResponse;
        this.window = window;
        this.useRequestParameters = useRequestParameters;
        //TODO
    }
    
    private static boolean getMetaDataBooleanValue(GenericMetadata metaData, String fieldName, boolean defaultValue )
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
    
    protected Map<String, String[]> getPrivateRenderParameterMap()
    {
        return Collections.emptyMap();
    }
    
    protected boolean isPublicRenderParameter(String name)
    {
        List<String> publicRenderParameterNames = window.getPortletEntity().getPortletDefinition().getSupportedPublicRenderParameters();
        return publicRenderParameterNames.isEmpty() ? false : publicRenderParameterNames.contains(name);
    }
        
    @SuppressWarnings("unchecked")
    public Map<String, String[]> getPrivateParameterMap()
    {
        if (privateParameters == null)
        {
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
            privateParameters = new HashMap<String, String[]>();
            JetspeedRequestContext context = (JetspeedRequestContext) getAttribute("org.apache.jetspeed.request.RequestContext");
            if (context != null)
            {
                NavigationalState ns = context.getPortalURL().getNavigationalState();
                mergeRequestParameters = ns.getPortletWindowOfAction() != null || ns.getPortletWindowOfResource() != null;
                Iterator<String> iter = ns.getParameterNames(getPortletWindow());
                while (iter.hasNext())
                {
                    String name = iter.next();
                    String[] values = ns.getParameterValues(getPortletWindow(), name);
                    privateParameters.put(name, values);
                }
            }
            
            PortletDefinition portletDef = getPortletWindow().getPortletEntity().getPortletDefinition();
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
                String encoding = (String)getContainerRequest().getAttribute(PortalReservedParameters.PREFERED_CHARACTERENCODING_ATTRIBUTE);
                boolean decode = getContainerRequest().getAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE) == null
                        && encoding != null;
                if (decode)
                {
                    getContainerRequest().setAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE,
                            new Boolean(true));
                }
                for (Enumeration parameters = getContainerRequest().getParameterNames(); parameters.hasMoreElements();)
                {
                    String paramName = (String) parameters.nextElement();
                    String[] paramValues = getContainerRequest().getParameterValues(paramName);

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
                    String[] navValues = privateParameters.get(paramName);
                    if (navValues == null)
                    {
                        privateParameters.put(paramName, paramValues);
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
                        privateParameters.put(paramName, combined);
                    }
                }
            }
        }
        if (!privateParameters.isEmpty())
        {
            Map<String, String[]> result = new HashMap<String, String[]>(privateParameters.size());
            for (Map.Entry<String,String[]> entry : privateParameters.entrySet())
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

    public PortletContainer getContainer()
    {
        return container;
    }

    public Cookie[] getCookies()
    {
        if (cookies == null)
        {
            cookies = servletRequest.getCookies();
            if (cookies == null)
            {
                cookies = new Cookie[0];
            }
        }
        return cookies.length > 0 ? cookies.clone() : null;
    }

    public PortletContext getPortletContext()
    {
        return portletContext;
    }

    public ServletContext getServletContext()
    {
        return servletContext;
    }

    public PortletWindow getPortletWindow()
    {
        return window;
    }

    public HttpServletRequest getContainerRequest()
    {
        return containerRequest;
    }

    public HttpServletResponse getContainerResponse()
    {
        return containerResponse;
    }

    public HttpServletRequest getServletRequest()
    {
        return servletRequest;
    }

    public HttpServletResponse getServletResponse()
    {
        return servletResponse;
    }
    
    public Locale getPreferredLocale()
    {
        RequestContext requestContext = (RequestContext)getContainerRequest().getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        Locale preferedLocale = requestContext.getLocale();
        return preferedLocale != null ? preferedLocale : getContainerRequest().getLocale();
    }

    public void init(PortletContext portletContext, ServletContext servletContext, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    {
        this.portletContext = portletContext;
        this.servletContext = servletContext;
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
    }
    
    public Object getAttribute(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<String> getAttributeNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object value)
    {
        // TODO Auto-generated method stub
    }

    public Map<String, String[]> getProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, String[]> getPublicParameterMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
