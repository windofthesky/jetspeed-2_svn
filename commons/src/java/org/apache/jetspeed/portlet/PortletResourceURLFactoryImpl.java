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
package org.apache.jetspeed.portlet;

import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.portals.bridges.common.PortletResourceURLFactory;

/**
 * Jetspeed specific implementation of PortletResourceURLFactory.
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PortletResourceURLFactoryImpl implements PortletResourceURLFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.portals.bridges.common.PortletResourceURLFactory#createResourceURL(javax.portlet.PortletConfig,
     *      javax.portlet.RenderRequest, javax.portlet.RenderResponse,
     *      java.util.Map)
     */
    public String createResourceURL(PortletConfig config, RenderRequest request, RenderResponse response, Map parameters)
            throws PortletException
    {
        PortletURL url = response.createRenderURL();
        url.setWindowState(WindowState.MAXIMIZED);
        if (parameters != null)
        {
            url.setParameters(parameters);
        }
        ContentFragment fragment = (ContentFragment)request.getAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE);
        StringBuffer path = new StringBuffer(url.toString());
        path.append(path.indexOf("?") > -1 ? '&' : '?');
        path.append(PortalReservedParameters.PIPELINE);
        path.append('=');
        path.append(PortalReservedParameters.PORTLET_PIPELINE);
        path.append('&');
        path.append(PortalReservedParameters.PORTLET_ENTITY);
        path.append('=');
        path.append(fragment.getId());
        path.append('&');
        path.append(PortalReservedParameters.NAV_STATE);
        path.append('=');
        path.append(PortalReservedParameters.TRANSIENT_NAV_STATE);
        return path.toString();
    }
}
