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
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.pluto.om.portlet.PortletDefinition;

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
        return createPortletURL(encodedNavState, secure, null, false);
    }
    
    protected String createPortletURL(String encodedNavState, boolean secure, PortletWindow window, boolean action)
    {   
        return createPortletURL(encodedNavState, secure, window, action, false, false);
    }
    
    protected String createPortletURL(String encodedNavState, boolean secure, PortletWindow window, boolean action, boolean resource, boolean desktopRequestNotAjax)
    {   
        StringBuffer buffer = new StringBuffer("");
        buffer.append(getBaseURL(secure));
        if (action)
        {
            buffer.append(this.baseActionPath);
        }
        else
        {
            buffer.append(this.baseRenderPath);        
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
        
        if ( !resource )
        {
        	if ( ! desktopRequestNotAjax )
            {
        		PortletEntity pe = window.getPortletEntity();
        		buffer.append( "?entity=" ).append( pe.getId() );
            
        		PortletDefinition portlet = pe.getPortletDefinition();
        		PortletApplication app = (PortletApplication)portlet.getApplication();
        		String uniqueName = app.getName() + "::" + portlet.getPortletName();
        		buffer.append( "&portlet=" ).append( uniqueName );
            }
        }
        else
        {
            buffer.append("?encoder=desktop");
        }

        return buffer.toString();
    }        
    
    public String createPortletURL(PortletWindow window, Map parameters, PortletMode mode, WindowState state, boolean action, boolean secure)
    {
        try
        {
            boolean resource = !action && parameters.containsKey(PortalReservedParameters.PORTLET_RESOURCE_URL_REQUEST_PARAMETER);
            boolean desktopRequestNotAjax = false;
            if ( parameters.containsKey(JetspeedDesktop.DESKTOP_REQUEST_NOT_AJAX_PARAMETER) )
            {
            	desktopRequestNotAjax = true;
            	parameters.remove(JetspeedDesktop.DESKTOP_REQUEST_NOT_AJAX_PARAMETER);
            }
            return createPortletURL(this.getNavigationalState().encode(window,parameters,mode,state,action), secure, window, action, resource, desktopRequestNotAjax);
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
