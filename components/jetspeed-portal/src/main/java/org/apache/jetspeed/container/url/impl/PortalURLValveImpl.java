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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * Creates the PortalURL for the current Request
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class PortalURLValveImpl extends AbstractValve
{
    private NavigationalStateComponent navComponent;
    private boolean pathInfoParamAllowed;
    private String defaultPathInfoParam;
    
    public PortalURLValveImpl(NavigationalStateComponent navComponent)
    {
        this.navComponent = navComponent;
    }
    
    public void setPathInfoParamAllowed(boolean pathInfoParamAllowed)
    {
        this.pathInfoParamAllowed = pathInfoParamAllowed;
    }
    
    public void setDefaultPathInfoParam(String defaultPathInfoParam)
    {
        this.defaultPathInfoParam = defaultPathInfoParam;
    }
    
    public void invoke(RequestContext request, ValveContext context)
        throws PipelineException
    {
        try
        {
            if (request.getPortalURL() == null)
            {
                HttpServletRequest servletRequest = getHttpServletRequest(request);
                String encoding = request.getRequestParameter(JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER);
                
                if (JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER_VALUE.equals(encoding))
                {
                    request.setPortalURL(navComponent.createDesktopURL(servletRequest, request.getCharacterEncoding()));
                    request.setAttribute( JetspeedDesktop.DESKTOP_ENABLED_REQUEST_ATTRIBUTE, Boolean.TRUE );
                }
                else
                {
                    request.setPortalURL(navComponent.createURL(servletRequest, request.getCharacterEncoding()));
                }
            }
        }
        catch (Exception e)
        {
            throw new PipelineException(e);
        }
        
        // Pass control to the next Valve in the Pipeline
        context.invokeNext( request );
    }
    
    private HttpServletRequest getHttpServletRequest(RequestContext request)
    {
        HttpServletRequest servletRequest = request.getRequest();
        
        if (pathInfoParamAllowed)
        {
            String param = servletRequest.getParameter(PortalURL.PATH_INFO_QUERY);
            
            if (param == null)
            {
                param = servletRequest.getHeader(PortalURL.PATH_INFO_HEADER);
                
                if (param == null)
                {
                    param = defaultPathInfoParam;
                }
            }
            
            if (param != null)
            {
                final String pathInfoParam = param;
                
                servletRequest = new HttpServletRequestWrapper(servletRequest)
                {
                    @Override
                    public String getPathInfo()
                    {
                        return pathInfoParam;
                    }
                };
            }
        }
        
        return servletRequest;
    }

    public String toString()
    {
        return "PortalURLValveImpl";
    }
}
