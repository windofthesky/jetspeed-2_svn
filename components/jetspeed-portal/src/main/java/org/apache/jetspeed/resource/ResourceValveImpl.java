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
package org.apache.jetspeed.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestDiagnostics;
import org.apache.jetspeed.request.RequestDiagnosticsFactory;
import org.apache.pluto.container.PortletContainer;
import org.apache.jetspeed.container.PortletWindow;

/**
 * <p>
 * ResourceValveImpl
 * </p>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 *
 */
public class ResourceValveImpl extends AbstractValve
{
    private PortletContainer container;

    public ResourceValveImpl(PortletContainer container)
    {
        this.container = container;
    }
    
    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {     
        PortletWindow resourceWindow = request.getPortalURL().getNavigationalState().getPortletWindowOfResource();
        
        if ( resourceWindow != null )
        {
            try
            {            
                HttpServletRequest servletRequest = request.getRequest();
                HttpServletResponse servletResponse = request.getResponse();
                resourceWindow.setAttribute(PortalReservedParameters.PORTLET_CONTAINER_INVOKER_USE_FORWARD, Boolean.TRUE);
                if (resourceWindow.getPortletDefinition().getApplication().getVersion().equals("1.0"))
                {
                    container.doRender(resourceWindow, servletRequest, servletResponse);
                }
                else
                {
                    container.doServeResource(resourceWindow, servletRequest, servletResponse);
                }
            }
            catch (Exception e)
            {
                RequestDiagnostics rd = RequestDiagnosticsFactory.newRequestDiagnostics();
                RequestDiagnosticsFactory.fillInPortletWindow(rd, resourceWindow, e);
                PipelineException pe = new PipelineException(e);
                pe.setRequestDiagnostics(rd);
                throw pe;
            }
        }
        else
        {
            // Pass control to the next Valve in the Pipeline
            context.invokeNext(request);
        }
    }

    public String toString()
    {
        return "ResourceValveImpl";
    }
}
