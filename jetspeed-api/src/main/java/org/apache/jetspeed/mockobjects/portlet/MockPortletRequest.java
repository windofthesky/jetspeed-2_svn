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
package org.apache.jetspeed.mockobjects.portlet;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;

/**
 * A mock portlet request, useful for unit testing and offline utilities
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class MockPortletRequest implements PortletRequest
{
    MockPortletSession session = null;
    
    public MockPortletRequest()
    {
        session = new MockPortletSession();     
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getPortletSession()
     */
    public PortletSession getPortletSession()
    {
        return session;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getPortletSession(boolean)
     */
    public PortletSession getPortletSession(boolean create)
    {
        if (session == null)
        {
            session = new MockPortletSession();
        }
        return session;
    }
    
    public Object getAttribute(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Enumeration<String> getAttributeNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getAuthType()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getContextPath()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Cookie[] getCookies()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Locale getLocale()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Enumeration<Locale> getLocales()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getParameter(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Map<String, String[]> getParameterMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Enumeration<String> getParameterNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String[] getParameterValues(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    public PortalContext getPortalContext()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public PortletMode getPortletMode()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public PortletPreferences getPreferences()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Map<String, String[]> getPrivateParameterMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Enumeration<String> getProperties(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getProperty(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Enumeration<String> getPropertyNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Map<String, String[]> getPublicParameterMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getRemoteUser()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getRequestedSessionId()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getResponseContentType()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Enumeration<String> getResponseContentTypes()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getScheme()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getServerName()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public int getServerPort()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    public Principal getUserPrincipal()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public String getWindowID()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public WindowState getWindowState()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public boolean isPortletModeAllowed(PortletMode arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isRequestedSessionIdValid()
    {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isSecure()
    {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isUserInRole(String arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isWindowStateAllowed(WindowState arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }
    public void removeAttribute(String arg0)
    {
        // TODO Auto-generated method stub
    }
    public void setAttribute(String arg0, Object arg1)
    {
        // TODO Auto-generated method stub
    }
}
