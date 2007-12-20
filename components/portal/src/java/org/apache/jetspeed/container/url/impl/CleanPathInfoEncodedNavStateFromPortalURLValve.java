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

import java.io.IOException;

import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * This Valve will clean encoded navstate from the browser url by sending a client side redirect
 * to the same url with the navstate removed.
 * <p>
 * This Valve will only do this:
 * <ul>
 *   <li>on a GET Render request (not for Resource or Action requests)</li>
 *   <li>the request is not served by the Desktop</li>
 *   <li>the navstate is encoded as PathInfo</li>
 *   <li>all the navstate is maintained in the session (portlet mode, window state and render parameters)</li>
 * </ul>
 * </p>
 * <p>
 * This valve needs to be added to the portal pipeline *after* the ContainerValve to ensure navstate is properly synchronized with the session.
 * </p>
 * <p>
 * Caveats:<br/>
 * <ul>
 *   <li>bookmarking browser url will no longer retain nav state, but with SessionFullNavigationState this wasn't really reliable anyway.</li>
 *   <li>back button history is no longer maintained by browsers for GET render urls, somewhat similar to Ajax based requests (e.g. Desktop)</li>
 * </ul>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 * 
 */
public class CleanPathInfoEncodedNavStateFromPortalURLValve extends AbstractValve
{
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        NavigationalState state = request.getPortalURL().getNavigationalState();
        PortalURL portalURL = request.getPortalURL();

        Boolean desktopEnabled = (Boolean) request.getAttribute(JetspeedDesktop.DESKTOP_ENABLED_REQUEST_ATTRIBUTE);

        if (request.getRequest().getMethod().equals("GET") && portalURL.hasEncodedNavState()
                && portalURL.isPathInfoEncodingNavState() && state.isNavigationalParameterStateFull()
                && state.isRenderParameterStateFull() && state.getPortletWindowOfAction() == null
                && state.getPortletWindowOfResource() == null
                && (desktopEnabled == null || desktopEnabled.booleanValue() == false))
        {
            try
            {
                StringBuffer location = new StringBuffer(request.getPortalURL().getBasePath());
                String str = request.getPortalURL().getPath();
                if (str != null)
                {
                    location.append(str);
                }
                str = request.getRequest().getQueryString();
                if (str != null && str.length() > 0)
                {
                    location.append('?').append(request.getRequest().getQueryString());
                }
                request.getResponse().sendRedirect(request.getResponse().encodeRedirectURL(location.toString()));
            }
            catch (IOException e)
            {
                throw new PipelineException(e);
            }
        }
        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);
    }
}
