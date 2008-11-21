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

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
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
    private Map<String, String[]> privateParameters = null;
    private Map<String, String[]> publicParameters = null;
    private Map<String, String[]> requestParameters = null;
    private boolean resource = false;

    private PortalURL url;
    
    public PortletURLProviderImpl(RequestContext context, PortletWindow portletWindow)
    {
        this.portletWindow = portletWindow;
        
        url = context.getPortalURL();
        privateParameters = url.getNavigationalState().getParameterMap(portletWindow);
    }

    public void setPortletMode(PortletMode mode)
    {
        this.mode = mode;
    }

    public void setWindowState(WindowState state)
    {
        this.state = state;
    }

    public void setSecure()
    {
        secure = true;
    }

    public void clearParameters()
    {
        // TODO: old comment "not used, handled by JetspeedNavigationalStateCodec itself" ???
        privateParameters = null;
    }

    public String toString()
    {
        // TODO: handle publicParameters, resource url, resourceID, cacheability (last two needs to be added to the PortletURLPRovider interface)
        return url.createPortletURL(portletWindow,privateParameters,mode,state,action,secure);
    }
    
    public void setParameters(Map parameters)
    {
        this.privateParameters = parameters;
    }

    public String[] getPrivateRenderParameters(String name)
    {
        return privateParameters != null ? privateParameters.get(name) : null;
    }

    public String[] getPublicRenderParameters(String name)
    {
        // TODO
        return null;
    }

    public boolean isResourceServing()
    {
        return resource;
    }

    public boolean isSecureSupported()
    {
        // TODO: review logic in Pluto PortletURLProviderImpl and PortletContainerImpl usage of this method (seems wrong...).
        return false;
    }

    public void savePortalURL(HttpServletRequest request)
    {
        // TODO: what should be done here?
    }

    public void setAction(boolean isAction)
    {
        action = isAction;
    }

    public void setPublicRenderParameters(Map parameters)
    {
        // TODO        
    }

    public void setResourceServing(boolean isResourceServing)
    {
        resource = isResourceServing;
    }

    public Map<String, String[]> getRenderParameters()
    {
        return this.requestParameters;
    }

    public Map<String, String[]> parseRenderParameters(Map<String, String[]> parentMap, String queryString)
    {
        return this.requestParameters = parentMap;
    }
    
}
