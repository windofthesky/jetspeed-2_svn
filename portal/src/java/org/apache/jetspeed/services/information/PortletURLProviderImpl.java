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
package org.apache.jetspeed.services.information;

import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.engine.core.PortalControlParameter;
import org.apache.jetspeed.engine.core.PortalURL;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.PortletURLProvider;
import org.apache.pluto.util.NamespaceMapperAccess;

/**
 * Provides access to the Portal URL manipulation 
 * 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletURLProviderImpl implements PortletURLProvider
{
    private DynamicInformationProviderImpl provider = null;
    private PortletWindow portletWindow = null;
    private PortletMode mode = null;
    private WindowState state = null;
    private boolean action = false;
    private boolean secure = false;
    private boolean clearParameters = false;
    private Map parameters = null;

    private PortalURL portalUrl;
    private PortalControlParameter controlURL;
    public PortletURLProviderImpl(DynamicInformationProviderImpl provider, PortletWindow portletWindow)
    {
        this.provider = provider;
        this.portletWindow = portletWindow;
        portalUrl = JetspeedRequestContext.getRequestContext(provider.request).getRequestedPortalURL();
        controlURL = new PortalControlParameter(portalUrl);
    }

    public void setPortletMode(PortletMode mode)
    {
        this.mode = mode;
    }

    public void setWindowState(WindowState state)
    {
        this.state = state;
    }

    public void setAction()
    {
        action = true;
    }

    public void setSecure()
    {
        secure = true;
    }

    public void clearParameters()
    {

        controlURL.clearRenderParameters(portletWindow);

    }

    public void setParameters(Map parameters)
    {
        this.parameters = parameters;
    }

    public String toString()
    {

        if (mode != null)
        {
            controlURL.setMode(portletWindow, mode);
        }

        if (state != null)
        {
            controlURL.setState(portletWindow, state);
        }

        // STW: Spec reference PLT:12:2
        // Had to move logic directly up into the actual clear call
        //        if (clearParameters)
        //        {
        //        	// clear any existing paramters per the spec
        //            controlURL.clearRenderParameters(portletWindow);
        //        }

        if (action)
        {
            controlURL.setAction(portletWindow);
        }

        if (parameters != null)
        {
            Iterator names = parameters.keySet().iterator();
            while (names.hasNext())
            {
                String name = (String) names.next();
                Object value = parameters.get(name);
                String[] values = value instanceof String ? new String[] {(String) value }
                : (String[]) value;
                if (action)
                {
                    controlURL.setRequestParam(
                        NamespaceMapperAccess.getNamespaceMapper().encode(portletWindow.getId(), name),
                        values);

                }
                else
                {
                    controlURL.setRenderParam(portletWindow, name, values);
                }
            }
        }

        return portalUrl.toString(controlURL, new Boolean(secure));
    }

}
