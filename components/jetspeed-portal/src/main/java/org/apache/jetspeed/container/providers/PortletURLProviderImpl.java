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
package org.apache.jetspeed.container.providers;

import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletWindow;
import org.apache.pluto.spi.PortletURLProvider;

/**
 * Provides access to the Portal URL manipulation 
 * TODO: 2.2 implement Portlet API 2.0 features: see methods below throwing UnsupportedOperationException
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
    private Map parameters = null;

    private PortalURL url;
    
    public PortletURLProviderImpl(RequestContext context, PortletWindow portletWindow)
    {
        this.portletWindow = portletWindow;
        
        url = context.getPortalURL();
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
        // not used, handled by JetspeedNavigationalStateCodec itself
    }

    public void setParameters(Map parameters)
    {
        this.parameters = parameters;
    }

    public String toString()
    {
        return url.createPortletURL(portletWindow,parameters,mode,state,action,secure);
    }

    public String[] getPrivateRenderParameters(String name)
    {
        throw new UnsupportedOperationException();
    }

    public String[] getPublicRenderParameters(String name)
    {
        throw new UnsupportedOperationException();
    }

    public boolean isResourceServing()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isSecureSupported()
    {
        throw new UnsupportedOperationException();
    }

    public void savePortalURL(HttpServletRequest request)
    {
        throw new UnsupportedOperationException();
    }

    public void setAction(boolean isAction)
    {
        throw new UnsupportedOperationException();
    }

    public void setPublicRenderParameters(Map parameters)
    {
        throw new UnsupportedOperationException();
    }

    public void setResourceServing(boolean isResourceServing)
    {
        throw new UnsupportedOperationException();
    }
}
