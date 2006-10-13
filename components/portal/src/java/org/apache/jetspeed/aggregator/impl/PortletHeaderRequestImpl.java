/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.aggregator.impl;

import javax.portlet.PortletPreferences;

import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.core.impl.PortletPreferencesImpl;
import org.apache.pluto.om.window.PortletWindow;


public class PortletHeaderRequestImpl implements PortletHeaderRequest
{
    private RequestContext requestContext;
    private String portletApplicationContextPath;
    private PortletWindow portletWindow;
    
    public PortletHeaderRequestImpl(RequestContext requestContext, PortletWindow portletWindow, String portletApplicationContextPath)
    {
        this.requestContext = requestContext;
        this.portletApplicationContextPath = portletApplicationContextPath;
        this.portletWindow = portletWindow;
    }
    
    public String getPortalContextPath()
    {
        return requestContext.getRequest().getContextPath();
    }
    
    public boolean isDesktopEncoder()
    {
        String requestEncoder = (String)requestContext.getRequest().getParameter("encoder");
        return((requestEncoder == null) || !requestEncoder.equals("desktop")) ? false : true;
    }
    
    
    public PortletPreferences getPreferences()
    {
        return new PortletPreferencesImpl(org.apache.pluto.Constants.METHOD_NOOP, this.portletWindow.getPortletEntity());
    }

    
    /**
     * @return Returns the portletApplicationContextPath.
     */
    public String getPortletApplicationContextPath()
    {
        return portletApplicationContextPath;
    }
    
}
