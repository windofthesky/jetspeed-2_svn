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
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.PortletURLProvider;

/**
 * Provides access to the Portal URL manipulation 
 * 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletURLProviderImpl implements PortletURLProvider
{
    private PortletWindow portletWindow = null;
    private PortletMode mode = null;
    private WindowState state = null;
    private boolean action = false;
    private boolean secure = false;
    private boolean clearParameters = false;
    private Map parameters = null;

    private RequestContext context;
    private PortalURL url;
    
    public PortletURLProviderImpl(RequestContext context, PortletWindow portletWindow)
    {
        this.portletWindow = portletWindow;
        
        // TODO: assemble this with factory
        NavigationalStateComponent nsc = (NavigationalStateComponent)Jetspeed.getComponentManager().getComponent(NavigationalStateComponent.class);
        
        context = this.context = context;
        url = nsc.createURL(context);
    }

    public PortletURLProviderImpl(RequestContext context, NavigationalState nav, PortletWindow portletWindow)
    {
        this.context = context;
        this.portletWindow = portletWindow;
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
        url.clearRenderParameters(portletWindow);
    }

    public void setParameters(Map parameters)
    {
        this.parameters = parameters;
    }

    public String toString()
    {
        if (mode != null)
        {
            url.setMode(portletWindow, mode);
        }

        if (state != null)
        {
            url.setState(portletWindow, state);
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
            url.setAction(portletWindow);
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
                    //url.setRequestParam(
                    //    NamespaceMapperAccess.getNamespaceMapper().encode(portletWindow.getId(), name),
                    //    values);
                    url.setRequestParam(name, values);
                }
                else
                {
                    url.setRenderParam(portletWindow, name, values);
                }
            }
        }
        return url.toString(secure);
    }

}
