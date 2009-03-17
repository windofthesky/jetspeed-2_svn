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

package org.apache.jetspeed.container.services;

import org.apache.pluto.container.impl.PortletEnvironmentServiceImpl;

/**
 * Extension of the Pluto DefaultPortletEnvironment
 * to allow overriding or wrapping specific Portlet Request/Response objects
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 *
 */
public class JetspeedPortletEnvironmentService extends PortletEnvironmentServiceImpl
{
/* TODO    
    public InternalRenderResponse createRenderResponse(PortletContainer container,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response,
                                                       PortletWindow portletWindow) 
    {
        // Handling of Jetspeed custom ResourceURLs (pre Portlet API 2.0 ResourceURL extension)
        // which is served using a RenderRequest/Response while needs full control over the Response (e.g. content type)
        
        // TODO: when Portlet API 2.0 ResourceURLs are implemented we probably need some additional flag/attribute to
        // be able to separate between this kind of ResourceURLS (but still support them for backwards compatibility)
        RequestContext rc = (RequestContext)request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        PortletWindow resourceWindow = rc.getPortalURL().getNavigationalState().getPortletWindowOfResource();
        if (resourceWindow != null && portletWindow == resourceWindow)
        {
            return new ResourceRenderResponseImpl(container, portletWindow, request, response);
        }
        return new RenderResponseImpl(container, portletWindow, request, response);
    }
*/
}
