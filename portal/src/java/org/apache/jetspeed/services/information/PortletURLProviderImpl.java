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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
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

    private RequestContext context;
    private NavigationalState nav;
    
    public PortletURLProviderImpl(DynamicInformationProviderImpl provider, PortletWindow portletWindow)
    {
        this.provider = provider;
        this.portletWindow = portletWindow;
        
        // TODO: assemble this with factory
        RequestContextComponent rcc = (RequestContextComponent)Jetspeed.getComponentManager().getComponent(RequestContextComponent.class);
        NavigationalStateComponent nsc = (NavigationalStateComponent)Jetspeed.getComponentManager().getComponent(NavigationalStateComponent.class);
        
        context = rcc.getRequestContext(provider.request);
        nav = nsc.create(context);
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
        nav.clearRenderParameters(portletWindow);
    }

    public void setParameters(Map parameters)
    {
        this.parameters = parameters;
    }

    public String toString()
    {
        if (mode != null)
        {
            nav.setMode(portletWindow, mode);
        }

        if (state != null)
        {
            nav.setState(portletWindow, state);
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
            nav.setAction(portletWindow);
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
                    nav.setRequestParam(
                        NamespaceMapperAccess.getNamespaceMapper().encode(portletWindow.getId(), name),
                        values);

                }
                else
                {
                    nav.setRenderParam(portletWindow, name, values);
                }
            }
        }
        return nav.toString(secure);
    }

}
