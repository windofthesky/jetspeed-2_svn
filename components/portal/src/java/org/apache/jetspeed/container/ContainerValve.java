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

import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.container.state.MutableNavigationalState;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Determines the action window in the current request If no action was found,
 * sets the request context's action window to null denoting that there is no
 * targeted action for this request otherwise the target action window is set
 * here
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class ContainerValve extends AbstractValve
{
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try
        {
            // create a session if not already created, necessary for Tomcat 5
            request.getRequest().getSession(true);

            // PortletContainerServices.prepare();
            MutableNavigationalState state = (MutableNavigationalState)request.getPortalURL().getNavigationalState();
            if (state != null)
            {
                boolean redirect = false;
                Page page = request.getPage();
                PortletWindow window = state.getPortletWindowOfResource();
                if (window != null && page.getFragmentById(window.getId().toString()) == null)
                {
                    // target window doesn't exists anymore or the target page is not accessible (anymore)
                    request.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                window = state.getPortletWindowOfAction();
                if (window != null && page.getFragmentById(window.getId().toString()) == null)
                {
                    // target window doesn't exists anymore or the target page is not accessible (anymore)
                    // remove any navigational state for the window
                    state.removeState(window);
                    // as this is an action request which cannot be handled, perform a direct redirect after sync state (for the other windows)
                    redirect = true;
                }
                window = state.getMaximizedWindow();
                if (window != null && page.getFragmentById(window.getId().toString()) == null)
                {
                    // target window doesn't exists anymore or the target page is not accessible (anymore)
                    // remove any navigational state for the window
                    state.removeState(window);
                }
                state.sync(request);
                if (redirect)
                {
                    // target page doesn't contain (anymore) the targeted windowOfAction 
                    // this can also occur when a session is expired and the target page isn't accessible for the anonymous user
                    // Redirect the user back to the target page (with possibly retaining the other windows navigational state).
                    request.getResponse().sendRedirect(request.getPortalURL().getPortalURL());
                    return;
                }

                PortletWindow actionWindow = state.getPortletWindowOfAction();
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
        }
        catch (Exception e)
        {
            throw new PipelineException(e);
        }
        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);
    }

    public String toString()
    {
        return "ContainerValve";
    }
}