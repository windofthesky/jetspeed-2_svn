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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.portlet.PortletConfig;
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
    private PortletConfig portletConfig;
    private ServletContext servletContext;
    private Cookie cookies[];
    private JetspeedRequestContext requestContext;
    
    private Map<String, String[]> privateParameters;
    private Map<String, String[]> publicRenderParameters;
    
    public PortletRequestContextImpl(PortletContainer container, HttpServletRequest containerRequest,
                                     HttpServletResponse containerResponse, PortletWindow window)
    {
        this.container = container;
        this.containerRequest = containerRequest;
        this.containerResponse = containerResponse;
        this.window = window;
        this.requestContext = (JetspeedRequestContext)window.getRequestContext();
    }
    
    protected JetspeedRequestContext getRequestContext()
    {
        return requestContext;
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
        List<String> publicRenderParameterNames = window.getPortletDefinition().getSupportedPublicRenderParameters();
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
            NavigationalState ns = requestContext.getPortalURL().getNavigationalState();
            mergeRequestParameters = ns.getPortletWindowOfAction() != null || ns.getPortletWindowOfResource() != null;
            Map<String, String[]> paramMap = ns.getParameterMap(window);
            
            if (paramMap != null)
            {
                privateParameters.putAll(paramMap);
            }
            
            PortletDefinition portletDef = window.getPortletDefinition();
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
            
            //get request params
            if (mergeRequestParameters)
            {
                String encoding = (String)containerRequest.getAttribute(PortalReservedParameters.PREFERED_CHARACTERENCODING_ATTRIBUTE);
                boolean decode = containerRequest.getAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE) == null
                        && encoding != null;
                if (decode)
                {
                    containerRequest.setAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE,new Boolean(true));
                }
                for (Enumeration parameters = containerRequest.getParameterNames(); parameters.hasMoreElements();)
                {
                    String paramName = (String) parameters.nextElement();
                    String[] paramValues = containerRequest.getParameterValues(paramName);

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
        // no need to clone: container is supposed to do so
        return privateParameters;
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

    public PortletConfig getPortletConfig()
    {
        return portletConfig;
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
        Locale preferedLocale = requestContext.getLocale();
        return preferedLocale != null ? preferedLocale : containerRequest.getLocale();
    }

    public void init(PortletConfig portletConfig, ServletContext servletContext, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    {
        this.portletConfig = portletConfig;
        this.servletContext = servletContext;
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
    }
    
    public Object getAttribute(String name)
    {
        Object value = servletRequest.getAttribute(name);        
        return value != null ? value : window.getAttribute(name);
    }

    @SuppressWarnings("unchecked")
    public Enumeration<String> getAttributeNames()
    {
        HashSet<String> names = new HashSet<String>();
        Enumeration<String> e;
        for (e = servletRequest.getAttributeNames(); e.hasMoreElements();  )
        {
            try
            {
                names.add(e.nextElement());
            }
            catch(NoSuchElementException nse)
            {
                // ignore potential concurrent changes when run in parallel mode
            }
        }
        for (String name : window.getAttributes().keySet())
        {
            names.add(name);
        }
        return Collections.enumeration(names);
    }

    public void setAttribute(String name, Object value)
    {
        if (value == null)
        {
            window.removeAttribute(name);
        }
        else
        {
            window.setAttribute(name,value);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String[]> getProperties()
    {
        HashMap<String, String[]> properties = new HashMap<String, String[]>();
        for (Enumeration<String> names = servletRequest.getHeaderNames(); names.hasMoreElements(); )
        {
            String name = names.nextElement();
            ArrayList<String> values = new ArrayList<String>();
            for (Enumeration<String> headers = servletRequest.getHeaders(name); headers.hasMoreElements(); )
            {
                values.add(headers.nextElement());
            }
            int size = values.size();
            if (size > 0)
            {
                properties.put(name, values.toArray(new String[size]));
            }
        }
        return properties;
    }

    public Map<String, String[]> getPublicParameterMap()
    {
        if (publicRenderParameters == null)
        {
            publicRenderParameters = requestContext.getPortalURL().getNavigationalState().getPublicRenderParameterMap(window);
        }
        
        if (publicRenderParameters == null)
        {
            publicRenderParameters = Collections.emptyMap();
        }
        
        // no need to clone: the container is supposed to do so
        return publicRenderParameters;
    }
}
