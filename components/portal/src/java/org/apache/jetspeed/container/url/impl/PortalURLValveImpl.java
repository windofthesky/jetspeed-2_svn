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

import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.desktop.JetspeedDesktop;

/**
 * Creates the PortalURL for the current Request
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class PortalURLValveImpl extends AbstractValve
{
    private NavigationalStateComponent navComponent;

    public PortalURLValveImpl(NavigationalStateComponent navComponent)
    {
        this.navComponent = navComponent;
    }
    
    public void invoke(RequestContext request, ValveContext context)
        throws PipelineException
    {
        try
        {  
            if ( request.getPortalURL() == null )
            {
                String encoding = request.getRequestParameter(JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER);
                if (encoding != null && encoding.equals(JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER_VALUE))
                {
                    request.setPortalURL(navComponent.createDesktopURL(request.getRequest(), request.getCharacterEncoding()));
                    request.setAttribute( JetspeedDesktop.DESKTOP_ENABLED_REQUEST_ATTRIBUTE, Boolean.TRUE );
                }
                else
                {
                    request.setPortalURL(navComponent.createURL(request.getRequest(), request.getCharacterEncoding()));
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
        return "PortalURLValveImpl";
    }
}
