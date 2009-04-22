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
package org.apache.jetspeed.desktop.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * DesktopEncoderRedirect Valve
 * 
 * if request parameter encoder=desktop is NOT defined,
 *    redirect to same url with /desktop pipeline,
 * otherwise,
 *    just invoke next valve
 * 
 * Used by the /render pipeline (desktop-render-pipeline) to allow
 * render requests that are not initiated via desktop javascript code to result
 * in a page level navigation to the /desktop pipeline with the correct portlet rendering
 * indicated in the original url. The encoder=desktop request parameter
 * is used by desktop javascript code to indicate that the request is an "official"
 * desktop ajax request. 
 *
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: $
 */
public class DesktopEncoderRedirectValveImpl extends AbstractValve
{
    protected Logger log = LoggerFactory.getLogger(DesktopEncoderRedirectValveImpl.class);
    
    private String desktopPipelinePath = null;
    private String desktopRenderPipelinePath = null;
    
    public DesktopEncoderRedirectValveImpl( String desktopPipelinePath, String desktopRenderPipelinePath )
    {
        if ( desktopPipelinePath == null || desktopPipelinePath.length() == 0 )
            desktopPipelinePath = JetspeedDesktop.DEFAULT_DESKTOP_PIPELINE_PATH;
        if ( desktopPipelinePath.charAt( 0 ) != '/' )
            desktopPipelinePath = "/" + desktopPipelinePath;
        if ( desktopPipelinePath.charAt( desktopPipelinePath.length() -1 ) != '/' )
            desktopPipelinePath = desktopPipelinePath + "/";

        if ( desktopRenderPipelinePath == null || desktopRenderPipelinePath.length() == 0 )
            desktopRenderPipelinePath = JetspeedDesktop.DEFAULT_DESKTOP_RENDER_PIPELINE_PATH;
        if ( desktopRenderPipelinePath.charAt( 0 ) != '/' )
            desktopRenderPipelinePath = "/" + desktopRenderPipelinePath;
        if ( desktopRenderPipelinePath.charAt( desktopRenderPipelinePath.length() -1 ) != '/' )
            desktopRenderPipelinePath = desktopRenderPipelinePath + "/";
        
        this.desktopPipelinePath = desktopPipelinePath;
        this.desktopRenderPipelinePath = desktopRenderPipelinePath;
    }
        
    public void invoke( RequestContext request, ValveContext context )
        throws PipelineException
    {
        try
        {  
            if ( request.getPortalURL() == null )
            {
                String encoding = request.getRequestParameter(JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER);
                if (encoding == null || ! encoding.equals(JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER_VALUE))
                {
                    // redirect to page url with render encoding
                    try 
                    {
                        String queryString = request.getRequest().getQueryString();
                        String location = request.getRequest().getRequestURI();
                        if ( queryString != null && queryString.length() > 0 )
                            location += "?" + queryString;
                        location = location.replaceAll( this.desktopRenderPipelinePath, this.desktopPipelinePath );
                        //log.info( "DesktopEncoderRedirectValveImpl redirecting request-uri=" + request.getRequest().getRequestURI() + " location=" + location );
                        request.getResponse().sendRedirect( location );
                    }
                    catch (IOException ioe){}
                    return;
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

    public String toString()
    {
        return "DesktopValve";
    }
}
