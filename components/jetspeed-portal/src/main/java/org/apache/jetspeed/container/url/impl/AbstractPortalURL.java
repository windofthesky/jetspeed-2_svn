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
package org.apache.jetspeed.container.url.impl;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.pipeline.PipelineMapper;
import org.apache.jetspeed.util.ArgUtil;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * AbstractPortalURL delivers the base implemention for parsing Jetspeed Portal URLs and creating new Portlet URLs.
 * Not implemented is the encoding and decoding of the NavigationState parameter in the URL, allowing concrete
 * implementations to supply different algorithms for it like encoding it as pathInfo or as query string parameter.
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public abstract class AbstractPortalURL implements PortalURL
{
    public static final String DEFAULT_NAV_STATE_PARAMETER = "_ns";

    protected static String navStateParameter;
    
    protected NavigationalState navState;
    protected BasePortalURL base = null;
    
    protected static Boolean relativeOnly;
    protected static String pagePipeline;
    protected static String portletPipeline;
    protected static Object lock = new Object();
    protected String contextPath;
    protected String basePath;
    protected String path;
    protected String encodedNavState;
    protected String secureBaseURL;
    protected String nonSecureBaseURL;
    protected String characterEncoding = "UTF-8";

    public AbstractPortalURL(NavigationalState navState, PortalContext portalContext, BasePortalURL base)
    {
        this(navState, portalContext);        
        this.base = base;
    }
    
    public AbstractPortalURL(NavigationalState navState, PortalContext portalContext)
    {
        if ( navStateParameter == null )
        {
            navStateParameter = 
                portalContext.getConfigurationProperty("portalurl.navigationalstate.parameter.name", 
                        DEFAULT_NAV_STATE_PARAMETER);
        }
        this.navState = navState;
        if ( relativeOnly == null )
        {
            relativeOnly = new Boolean(portalContext.getConfiguration().getBoolean("portalurl.relative.only", false));
        }
        if (pagePipeline == null) {
            synchronized (lock) {
                String pagePipelineName = portalContext.getConfiguration().getString(JetspeedEngineConstants.PIPELINE_DEFAULT, "/portal");
                PipelineMapper pipelineMapper = Jetspeed.getEngine().getComponentManager().lookupComponent("pipeline-mapper");
                pagePipeline = pipelineMapper.getMappedPathByPipelineId(pagePipelineName);
                portletPipeline = pipelineMapper.getMappedPathByPipelineId(PortalReservedParameters.PORTLET_PIPELINE);
            }
        }
    }
    
    public AbstractPortalURL(String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        this(navState, portalContext);
        this.characterEncoding = characterEncoding;
    }
    
    public AbstractPortalURL(HttpServletRequest request, String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        this(characterEncoding, navState, portalContext);
        setRequest(request);
    }
    
    public boolean isRelativeOnly()
    {
        return relativeOnly.booleanValue();
    }
    
    public static String getNavigationalStateParameterName()
    {
        return navStateParameter;
    }
    
    public String createNavigationalEncoding(PortletWindow window, Map<String, String[]> parameters, PortletMode mode, WindowState state, boolean action)
    {
        return createNavigationalEncoding(window, parameters, null, false, null, null, null, null, 
                                          mode, state, action ? URLType.ACTION : URLType.RENDER);
    }
    
    public String createNavigationalEncoding(PortletWindow window, Map<String, String[]> parameters, PortletMode mode, WindowState state, URLType urlType)
    {
        return createNavigationalEncoding(window, parameters, null, false, null, null, null, null, 
                                          mode, state, urlType);
    }

    public String createNavigationalEncoding(PortletWindow window, Map<String, String[]> parameters, String actionScopeId, boolean actionScopeRendered,
                                             String cacheLevel, String resourceId, Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                                             PortletMode mode, WindowState state, URLType urlType)
    {
        try
        {
            String ns = getNavigationalState().encode(window, parameters, actionScopeId, actionScopeRendered, cacheLevel, resourceId, privateRenderParameters, publicRenderParameters,
                                                      mode, state, urlType);
            return getNavigationalStateParameterName() + ":" + ns;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return "";            
        }    
    }
    
    public String createNavigationalEncoding(PortletWindow window, PortletMode mode, WindowState state)
    {
        try
        {
            String ns = getNavigationalState().encode(window, mode, state);
            return getNavigationalStateParameterName() + ":" + ns;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return "";            
        }                
    }
    
    protected void decodeBaseURL(HttpServletRequest request)
    {
        if (base == null)
        {
            base = new BasePortalURLImpl();
            base.setServerScheme(request.getScheme());
            base.setServerName(request.getServerName());
            base.setServerPort(request.getServerPort());
            base.setSecure(request.isSecure());            
        }
        if ( relativeOnly.booleanValue() )
        {
            this.secureBaseURL = this.nonSecureBaseURL = "";
        }
        else
        {
            StringBuffer buffer;
            
            buffer = new StringBuffer(HTTPS);
            buffer.append("://").append(base.getServerName());
            if (base.getServerPort() != 443 && base.getServerPort() != 80)
            {
                buffer.append(":").append(base.getServerPort());
            }
            this.secureBaseURL = buffer.toString();
            
            buffer = new StringBuffer(HTTP);
            buffer.append("://").append(base.getServerName());
            if (base.getServerPort() != 443 && base.getServerPort() != 80)
            {
                 buffer.append(":").append(base.getServerPort());
            }
            this.nonSecureBaseURL = buffer.toString();
        }
    }
    
    protected void decodeBasePath(HttpServletRequest request)
    {
        this.contextPath = (String) request.getAttribute(ContainerConstants.PORTAL_CONTEXT);
        if (contextPath == null)
        {
            contextPath = request.getContextPath();
        }
        if (contextPath == null)
        {
            contextPath = "";
        }
        String servletPath = request.getServletPath();
        if (servletPath == null)
        {
            servletPath = "";
        }
        else {
            if (servletPath.startsWith(portletPipeline)) {
                servletPath = pagePipeline;
            }
        }
        this.basePath = contextPath + servletPath;
    }

    protected void setEncodedNavigationalState(String encodedNavigationalState)
    {
        this.encodedNavState = encodedNavigationalState;        
        try
        {
            navState.init(encodedNavState, characterEncoding);
        }
        catch (UnsupportedEncodingException e)
        {
            IllegalStateException ise = new IllegalStateException("An unsupported encoding was defined for this NavigationalState.");
            ise.initCause(e);
            throw ise;
        }
    }

    protected void setPath(String path)
    {
        this.path = path;
    }

    public String getBaseURL()
    {
        return getBaseURL(base.isSecure());
    }
    
    public String getBaseURL(boolean secure)
    {
        // TODO: delivering both secure and non-secure baseURL for PLT.7.1.2
        //       currently only the baseURL as decoded (secure or non-secure) is returned
        //       and the secure parameter is ignored
        return secure ? secureBaseURL : nonSecureBaseURL;
    }
    
    public String getBasePath()
    {
        return basePath;
    }
    
    public String getPath()
    {
        return path;
    }    

    public String getPageBasePath()
    {
        if (null == path || (1 == path.length() && '/' == path.charAt(0)))
        {
            return basePath;
        }
        else if (-1 != path.indexOf('/'))
        {
            return basePath + path.substring(0, path.lastIndexOf('/'));
        }
        else
        {
            return basePath + path;
        }
    }
    
    public boolean isSecure()
    {
        return base.isSecure();
    }
        
    public NavigationalState getNavigationalState()
    {
        return navState;
    }

    public String createPortletURL(PortletWindow window, Map<String, String[]> parameters, PortletMode mode, WindowState state, boolean action, boolean secure)
    {
        return createPortletURL(window, parameters, null, false, null, null, null, null,
                                mode, state, action ? URLType.ACTION : URLType.RENDER, secure);
    }
    
    public String createPortletURL(PortletWindow window, Map<String, String[]> parameters, PortletMode mode, WindowState state, URLType urlType, boolean secure)
    {
        return createPortletURL(window, parameters, null, false, null, null, null, null,
                                mode, state, urlType, secure);
    }
    
    public String createPortletURL(PortletWindow window, Map<String, String[]> parameters, String actionScopeId, boolean actionScopeRendered,
                                   String cacheLevel, String resourceId, Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                                   PortletMode mode, WindowState state, URLType urlType, boolean secure)
    {
        try
        {
            String ns = getNavigationalState().encode(window, parameters, actionScopeId, actionScopeRendered, cacheLevel, resourceId, privateRenderParameters, publicRenderParameters,
                                                      mode, state, urlType);
            return createPortletURL(ns, secure);
        }
        catch (UnsupportedEncodingException e)
        {
            // should never happen
            e.printStackTrace();
            // to keep the compiler happy
            return null;
        }
    }

    public String createPortletURL(PortletWindow window, PortletMode mode, WindowState state, boolean secure)
    {
        try
        {
            String ns = navState.encode(window,mode,state);
            return createPortletURL(ns, secure);
        }
        catch (UnsupportedEncodingException e)
        {
            // should never happen
            e.printStackTrace();
            // to keep the compiler happy
            return null;
        }
    }    

    protected abstract void decodePathAndNavigationalState(HttpServletRequest request);
    
    protected abstract String createPortletURL(String encodedNavState, boolean secure);

    public void setRequest(HttpServletRequest request)
    {
        ArgUtil.assertNotNull(HttpServletRequest.class, request, this, "setRequest");
        decodeBaseURL(request);        
        decodeBasePath(request);        
        decodePathAndNavigationalState(request);        
    }

    public void setCharacterEncoding(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }
    
    public String getPortalURL()
    {
        try
        {
            return createPortletURL(navState.encode(), isSecure());
        }
        catch (UnsupportedEncodingException e)
        {
            // should never happen
            e.printStackTrace();
            // to keep the compiler happy
            return null;
        }
    }

    public boolean hasEncodedNavState()
    {
        return encodedNavState != null;
    }

    public boolean isPathInfoEncodingNavState()
    {
        return false;
    }
}
