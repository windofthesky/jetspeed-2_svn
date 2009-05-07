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

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.desktop.JetspeedDesktop;

/**
 * DesktopEncodingPortalURL encodes action URLs to target desktop specific /action pipeline,
 * and render URLs to target desktop specific /render pipeline
 * 
 * The query parameters "entity" and "portlet" are added to each url. These parameters are needed in a /render
 * request and are used by the desktop javascript code for both /render and /action requests.
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id: PathInfoEncodingPortalURL.java 367856 2006-01-11 01:04:09Z taylor $
 */
public class DesktopEncodingPortalURL extends AbstractPortalURL
{
    private String baseActionPath = null;
    private String baseRenderPath = null;
    
    private String desktopActionPipelinePath = null;
    private String desktopRenderPipelinePath = null;
    
    
    public DesktopEncodingPortalURL(NavigationalState navState, PortalContext portalContext, String desktopRenderPipelinePath, String desktopActionPipelinePath)
    {
        super(navState, portalContext);
        initializePipelinePaths( desktopRenderPipelinePath, desktopActionPipelinePath );
    }
    
    public DesktopEncodingPortalURL(NavigationalState navState, PortalContext portalContext, String desktopRenderPipelinePath, String desktopActionPipelinePath, BasePortalURL base)
    {
        super(navState, portalContext, base);
        initializePipelinePaths( desktopRenderPipelinePath, desktopActionPipelinePath );
    }

    public DesktopEncodingPortalURL(String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(characterEncoding, navState, portalContext);
        initializePipelinePaths( null, null );
    }

    public DesktopEncodingPortalURL(HttpServletRequest request, String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(request, characterEncoding, navState, portalContext);
        initializePipelinePaths( null, null );
    }
    
    private void initializePipelinePaths( String desktopRenderPipelinePath, String desktopActionPipelinePath )
    {
        if ( desktopActionPipelinePath == null || desktopActionPipelinePath.length() == 0 )
            desktopActionPipelinePath = JetspeedDesktop.DEFAULT_DESKTOP_ACTION_PIPELINE_PATH;
        if ( desktopActionPipelinePath.charAt( 0 ) != '/' )
            desktopActionPipelinePath = "/" + desktopActionPipelinePath;
        if ( desktopActionPipelinePath.length() > 1 && desktopActionPipelinePath.charAt( desktopActionPipelinePath.length() -1 ) == '/' )
            desktopActionPipelinePath = desktopActionPipelinePath.substring( 0, desktopActionPipelinePath.length() -1 );

        if ( desktopRenderPipelinePath == null || desktopRenderPipelinePath.length() == 0 )
            desktopRenderPipelinePath = JetspeedDesktop.DEFAULT_DESKTOP_RENDER_PIPELINE_PATH;
        if ( desktopRenderPipelinePath.charAt( 0 ) != '/' )
            desktopRenderPipelinePath = "/" + desktopRenderPipelinePath;
        if ( desktopRenderPipelinePath.length() > 1 && desktopRenderPipelinePath.charAt( desktopRenderPipelinePath.length() -1 ) == '/' )
            desktopRenderPipelinePath = desktopRenderPipelinePath.substring( 0, desktopRenderPipelinePath.length() -1 );
        
        this.desktopRenderPipelinePath = desktopRenderPipelinePath;
        this.desktopActionPipelinePath = desktopActionPipelinePath;
    }

    protected void decodeBasePath(HttpServletRequest request)
    {
        super.decodeBasePath(request);
        if ( this.baseActionPath == null )
        {
            this.baseActionPath = contextPath + this.desktopActionPipelinePath;
            this.baseRenderPath = contextPath + this.desktopRenderPipelinePath;
        }
    }
    
    protected void decodePathAndNavigationalState(HttpServletRequest request)
    {
        String path = null;
        String encodedNavState = null;

        String pathInfo = request.getPathInfo();
        if (pathInfo != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(request.getPathInfo(),"/");
            StringBuffer buffer = new StringBuffer();
            String token;
            boolean foundNavState = false;
            String navStatePrefix = getNavigationalStateParameterName() +":";
            while (tokenizer.hasMoreTokens())
            {
                token = tokenizer.nextToken();
                if (!foundNavState && token.startsWith(navStatePrefix))
                {
                    foundNavState = true;
                    if ( token.length() > navStatePrefix.length() )
                    {
                        encodedNavState = token.substring(navStatePrefix.length());
                    }
                }
                else
                {
                    buffer.append("/");
                    buffer.append(token);
                }
            }
            if ( buffer.length() > 0 )
            {
                path = buffer.toString();
            }
            else
            {
                path = "/";
            }
        }
        setPath(path);
        setEncodedNavigationalState(encodedNavState);
    }

    protected String createPortletURL(String encodedNavState, boolean secure)
    {
        // org.apache.pluto.container.impl.PortletContainerImpl invokes responseContext.getResponseURL(),
        // which invokes this method without window object.
        // So, in case of action, we need to find actionWindow to avoid NPE.
        PortletWindow actionWindow = null;
        if (PortalURL.URLType.ACTION == navState.getURLType())
        {
            actionWindow = navState.getPortletWindowOfAction();
        }
        
        return createPortletURL(encodedNavState, secure, actionWindow, URLType.RENDER, false);
    }
    
    protected String createPortletURL(String encodedNavState, boolean secure, PortletWindow window, URLType urlType, boolean desktopRequestNotAjax)
    {   
        StringBuffer buffer = new StringBuffer("");
        buffer.append(getBaseURL(secure));
        boolean desktopEncoder = false;
        
        if (URLType.ACTION.equals(urlType))
        {
            buffer.append(this.baseActionPath);
        }
        else
        {
            buffer.append(this.baseRenderPath);
            if (URLType.RESOURCE.equals(urlType))
            {
                desktopEncoder = true;
            }            
        }
        if ( encodedNavState != null )
        {
            buffer.append("/");
            buffer.append(getNavigationalStateParameterName());
            buffer.append(":");
            buffer.append(encodedNavState);
        }
        if ( getPath() != null )
        {
            buffer.append(getPath());
        }
        
        if ( !desktopEncoder )
        {
        	if ( ! desktopRequestNotAjax )
            {
                buffer.append( "?entity=" ).append( window.getId().toString() );
                buffer.append( "&portlet=" ).append( window.getPortletDefinition().getUniqueName() );
            }
        }
        else
        {
            buffer.append("?encoder=desktop");
        }

        return buffer.toString();
    }        
    
    public String createPortletURL(PortletWindow window, Map<String, String[]> parameters, String actionScopeId, boolean actionScopeRendered,
            String cacheLevel, String resourceId, Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
            PortletMode mode, WindowState state, URLType urlType, boolean secure)
    {
        try
        {
            boolean desktopRequestNotAjax = false;
            if ( parameters != null && parameters.containsKey(JetspeedDesktop.DESKTOP_REQUEST_NOT_AJAX_PARAMETER) )
            {
            	desktopRequestNotAjax = true;
            	parameters.remove(JetspeedDesktop.DESKTOP_REQUEST_NOT_AJAX_PARAMETER);
            }
            String ns = getNavigationalState().encode(window, parameters, actionScopeId, actionScopeRendered, cacheLevel, resourceId, privateRenderParameters, publicRenderParameters,
                                                      mode, state, urlType);
            return createPortletURL(ns, secure, window, urlType, desktopRequestNotAjax);
        }
        catch (UnsupportedEncodingException e)
        {
            // should never happen
            e.printStackTrace();
            // to keep the compiler happy
            return null;
        }
    }    
}
