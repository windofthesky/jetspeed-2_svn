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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
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
    private String serverName;    
    private String serverScheme;
    private String contextPath;
    private String basePath;
    private String path;
    private String encodedNavState;
    private String secureBaseURL;
    private String nonSecureBaseURL;
    private int serverPort;    
    private boolean secure;
    private String characterEncoding;
    
    public AbstractPortalURL(HttpServletRequest request, String characterEncoding, NavigationalState navState)
    {
        if ( navStateParameter == null )
        {
            navStateParameter = 
                Jetspeed.getContext().getConfigurationProperty("portalurl.navigationalstate.parameter.name", 
                        DEFAULT_NAV_STATE_PARAMETER);
        }
        
        this.navState = navState;
        this.characterEncoding = characterEncoding;

        if (null != request)
        {
            decodeBaseURL(request);
            
            decodeBasePath(request);
            
            decodePathAndNavigationalState(request);
        }
    }
    
    public static String getNavigationalStateParameterName()
    {
        return navStateParameter;
    }
    
    protected void decodeBaseURL(HttpServletRequest request)
    {
        this.serverName = request.getServerName();
        this.serverPort = request.getServerPort();
        this.serverScheme = request.getScheme();
        this.secure = request.isSecure();
        StringBuffer buffer = new StringBuffer(this.serverScheme);
        buffer.append("://");
        buffer.append(this.serverName);
        if ((this.serverScheme.equals(HTTP) && this.serverPort != 80) ||
                (this.serverScheme.equals(HTTPS) && this.serverPort != 443))
        {
            buffer.append(":");
            buffer.append(this.serverPort);
        }
        if ( secure )
        {
            this.secureBaseURL = buffer.toString();
        }
        else
        {
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
            // should never happen
            e.printStackTrace();
        }
    }

    protected void setPath(String path)
    {
        this.path = path;
    }

    public String getBaseURL()
    {
        return getBaseURL(secure);
    }
    
    public String getBaseURL(boolean secure)
    {
        // TODO: delivering both secure and non-secure baseURL for PLT.7.1.2
        //       currently only the baseURL as decoded (secure or non-secure) is returned
        //       and the secure parameter is ignored
        return this.secure ? secureBaseURL : nonSecureBaseURL;
    }
    
    public String getBasePath()
    {
        return basePath;
    }
    
    public String getPath()
    {
        return path;
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
}
