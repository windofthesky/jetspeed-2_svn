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

import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Determines the action window in the current request
 * If no action was found, sets the request context's action window to null
 * denoting that there is no targeted action for this request otherwise
 * the target action window is set here 
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class ContainerValve
       extends AbstractValve
{
    public void invoke( RequestContext request, ValveContext context )
        throws PipelineException
    {
        try
        {  
            // create a session if not already created, necessary for Tomcat 5
            request.getRequest().getSession(true);
            
            //PortletContainerServices.prepare();
            NavigationalState state = request.getPortalURL().getNavigationalState();
            if (state != null)
            {
                state.sync(request);
            }
            
            PortalURL url = request.getPortalURL();
            PortletWindow actionWindow = url.getNavigationalState().getPortletWindowOfAction();
            if (null == actionWindow)
            {
                // set to null to denote that no action was requested
                request.setActionWindow(null);           
            }
            else
            {
                // set the requested action window
                request.setActionWindow(actionWindow);                                
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
        return "ContainerValve";
    }
}