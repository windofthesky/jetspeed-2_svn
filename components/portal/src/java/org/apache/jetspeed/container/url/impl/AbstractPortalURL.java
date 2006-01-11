/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.container.url.impl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.pluto.om.window.PortletWindow;

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
    
    private static String navStateParameter;
    
    private NavigationalState navState;
    private BasePortalURL base = null;
    
    private String contextPath;
    private String basePath;
    private String path;
    private String encodedNavState;
    private String secureBaseURL;
    private String nonSecureBaseURL;
    private String characterEncoding = "UTF-8";
    

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
    
    public static String getNavigationalStateParameterName()
    {
        return navStateParameter;
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
        if ( null == path || (1 == path.length() && '/' == path.charAt(0)) )
        {
            return basePath;
        }
        else if ( -1 != path.indexOf('/') && !path.endsWith("/") )
        {
            return basePath + path.substring(0, path.lastIndexOf('/') );
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

    public String createPortletURL(PortletWindow window, Map parameters, PortletMode mode, WindowState state, boolean action, boolean secure)
    {
        try
        {
            return createPortletURL(navState.encode(window,parameters,mode,state,action), secure);
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
            return createPortletURL(navState.encode(window,mode,state), secure);
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
    
}
