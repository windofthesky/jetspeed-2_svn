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
package org.apache.jetspeed.container;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.pluto.container.ContainerServices;
import org.apache.pluto.container.impl.PortletContainerImpl;

/**
 * Desktop Portlet Container implementation. This implementation 
 * redirects only if the query paramater encoder=desktop is NOT specified.
 * When the encoder=desktop parameter is specified, the 'redirect' URL 
 * is returned in the response body for use by desktop javascript code.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class DesktopPortletContainerImpl extends PortletContainerImpl
{
    private String desktopPipelinePath = null;
    private String desktopActionPipelinePath = null;
    private String desktopRenderPipelinePath = null;
    
    public DesktopPortletContainerImpl(String containerName,
            ContainerServices containerServices, 
            String desktopPipelinePath, String desktopActionPipelinePath, String desktopRenderPipelinePath)
    {
        super(containerName, containerServices);
        if ( desktopPipelinePath == null || desktopPipelinePath.length() == 0 )
            desktopPipelinePath = JetspeedDesktop.DEFAULT_DESKTOP_PIPELINE_PATH;
        if ( desktopPipelinePath.charAt( 0 ) != '/' )
            desktopPipelinePath = "/" + desktopPipelinePath;
        if ( desktopPipelinePath.charAt( desktopPipelinePath.length() -1 ) != '/' )
            desktopPipelinePath = desktopPipelinePath + "/";
        
        if ( desktopActionPipelinePath == null || desktopActionPipelinePath.length() == 0 )
            desktopActionPipelinePath = JetspeedDesktop.DEFAULT_DESKTOP_ACTION_PIPELINE_PATH;
        if ( desktopActionPipelinePath.charAt( 0 ) != '/' )
            desktopActionPipelinePath = "/" + desktopActionPipelinePath;
        if ( desktopActionPipelinePath.charAt( desktopActionPipelinePath.length() -1 ) != '/' )
            desktopActionPipelinePath = desktopActionPipelinePath + "/";

        if ( desktopRenderPipelinePath == null || desktopRenderPipelinePath.length() == 0 )
            desktopRenderPipelinePath = JetspeedDesktop.DEFAULT_DESKTOP_RENDER_PIPELINE_PATH;
        if ( desktopRenderPipelinePath.charAt( 0 ) != '/' )
            desktopRenderPipelinePath = "/" + desktopRenderPipelinePath;
        if ( desktopRenderPipelinePath.charAt( desktopRenderPipelinePath.length() -1 ) != '/' )
            desktopRenderPipelinePath = desktopRenderPipelinePath + "/";
        
        this.desktopPipelinePath = desktopPipelinePath;
        this.desktopActionPipelinePath = desktopActionPipelinePath;
        this.desktopRenderPipelinePath = desktopRenderPipelinePath;
    }
    
    protected void redirect(HttpServletRequest servletRequest, HttpServletResponse servletResponse, String location) throws IOException    
    {
    	String encoding = servletRequest.getParameter( JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER );
    	boolean requestIsDesktopAjax = false;
        if ( encoding != null && encoding.equals( JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER_VALUE ) )
        {   // used in cases where action request cannot be made via ajax (e.g. form has <input type=file/> element)
        	requestIsDesktopAjax = true;
        	String ajaxOverride = servletRequest.getParameter( JetspeedDesktop.DESKTOP_AJAX_REQUEST_PARAMETER );
        	if ( ajaxOverride != null && ajaxOverride.equals( "false" ) )
        	{
        		requestIsDesktopAjax = false;
        	}
        }

        // TODO: 2.2 is this still necessary?
        javax.servlet.http.HttpServletResponse redirectResponse = servletResponse;
        while (redirectResponse instanceof javax.servlet.http.HttpServletResponseWrapper)
        {
            redirectResponse = (javax.servlet.http.HttpServletResponse) ((javax.servlet.http.HttpServletResponseWrapper) redirectResponse)
                    .getResponse();
        }

        if ( requestIsDesktopAjax )
        {   // no real redirect will occur; instead, return the redirect URL in the response body
            location = location.replaceAll( this.desktopActionPipelinePath, this.desktopRenderPipelinePath );
            redirectResponse.getWriter().print( location );
        }
        else
        {   // do real redirect
            location = location.replaceAll( this.desktopActionPipelinePath, this.desktopPipelinePath );
            location = location.replaceAll( this.desktopRenderPipelinePath, this.desktopPipelinePath);
            redirectResponse.sendRedirect(location);
        }
//        System.out.println("+++ >>>> DESKTOP REDIRECT: location is " + location);        
    }
    
}
