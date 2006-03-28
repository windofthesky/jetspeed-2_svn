/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerImpl;
import org.apache.pluto.core.InternalActionResponse;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.DynamicInformationProvider;
import org.apache.pluto.services.information.InformationProviderAccess;
import org.apache.pluto.services.information.PortletURLProvider;

/**
 * Desktop Portlet Container implementation. This implementation does not
 * redirect, but instead returns back the 'redirect' URL in the response to the
 * Ajax client for client-side aggregation.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class DesktopPortletContainerImpl extends PortletContainerImpl implements
        PortletContainer
{

    /**
     * This redirect does not redirect, instead returns the redirect URL in the response
     */
    protected void redirect(String location, PortletWindow portletWindow,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            InternalActionResponse _actionResponse) throws IOException
    {
        if (location == null && _actionResponse != null)
        {
            DynamicInformationProvider provider = InformationProviderAccess
                    .getDynamicProvider(servletRequest);

            // TODO: don't send changes in case of exception -> PORTLET:SPEC:17

            PortletMode portletMode = provider.getPortletMode(portletWindow);
            WindowState windowState = provider.getWindowState(portletWindow);

            // get the changings of this portlet entity that might be set during
            // action handling
            // change portlet mode
            if (_actionResponse.getChangedPortletMode() != null)
            {
                portletMode = _actionResponse.getChangedPortletMode();
                InformationProviderAccess.getDynamicProvider(servletRequest)
                        .getPortletActionProvider(portletWindow)
                        .changePortletMode(portletMode);
            }
            // change window state
            if (_actionResponse.getChangedWindowState() != null)
            {
                windowState = _actionResponse.getChangedWindowState();
                InformationProviderAccess.getDynamicProvider(servletRequest)
                        .getPortletActionProvider(portletWindow)
                        .changePortletWindowState(windowState);
            }
            // get render parameters
            Map renderParameter = _actionResponse.getRenderParameters();

            PortletURLProvider redirectURL = provider
                    .getPortletURLProvider(portletWindow);

            if (provider.getPortletMode(portletWindow) != null)
            {
                redirectURL.setPortletMode(portletMode);
            }
            if (provider.getWindowState(portletWindow) != null)
            {
                redirectURL.setWindowState(windowState);
            }
            if (servletRequest.isSecure())
            {
                redirectURL.setSecure(); // TBD
            }
            redirectURL.clearParameters();
            redirectURL.setParameters(renderParameter);

            location = servletResponse
                    .encodeRedirectURL(redirectURL.toString());
        }

        javax.servlet.http.HttpServletResponse redirectResponse = servletResponse;
        while (redirectResponse instanceof javax.servlet.http.HttpServletResponseWrapper)
        {
            redirectResponse = (javax.servlet.http.HttpServletResponse) ((javax.servlet.http.HttpServletResponseWrapper) redirectResponse)
                    .getResponse();
        }
        //redirectResponse.sendRedirect(location);
        System.out.println("+++ >>>> location is " + location);
        redirectResponse.getWriter().print(location);
    }

}
