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

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.pluto.spi.RequestPropertyProvider;
import org.w3c.dom.Element;

/**
 * TODO: 2.2 implement 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class RequestPropertyProviderImpl implements RequestPropertyProvider
{
    public RequestPropertyProviderImpl() 
    {}
    
    public Map<String, String[]> getProperties(HttpServletRequest request,
            org.apache.pluto.PortletWindow portletWindow)
    {
        return Collections.EMPTY_MAP;
    }

    public void setProperty(HttpServletRequest request,
            org.apache.pluto.PortletWindow portletWindow, String property, String value)
    {
        request.setAttribute(property + portletWindow.getId().getStringId()
                + "__str", value);
    }

    public void addProperty(HttpServletRequest request,
            org.apache.pluto.PortletWindow portletWindow, String property, String value)
    {
        if (request
                .getAttribute(property + portletWindow.getId().getStringId()) == null)
        {
            request.setAttribute(
                    property + portletWindow.getId().getStringId(), value);
        }
        else
        {
            String[] tmp = (String[]) request.getAttribute(property
                    + portletWindow.getId().getStringId() + "__str");
            String[] values = new String[tmp.length];
            for (int i = 0; i < tmp.length; i++)
            {
                values[i] = tmp[i];
            }
            values[tmp.length] = value;
        }
    }

    public void addProperty(HttpServletRequest request,
            org.apache.pluto.PortletWindow portletWindow, String property, Element value)
    {
        if (request
                .getAttribute(property + portletWindow.getId().getStringId()) == null)
        {
            request.setAttribute(
                    property + portletWindow.getId().getStringId(), value);
        }
        else
        {
            String[] tmp = (String[]) request.getAttribute(property
                    + portletWindow.getId().getStringId() + "__str");
            String[] values = new String[tmp.length];
            for (int i = 0; i < tmp.length; i++)
            {
                values[i] = tmp[i];
            }
            values[tmp.length] = value.toString();
        }

    }

    public void addCookieProperty(HttpServletRequest request,
            org.apache.pluto.PortletWindow portletWindow, Cookie cookie)
    {
        if (request.getAttribute(portletWindow.getId().getStringId() + "__coo") == null)
        {
            request.setAttribute(portletWindow.getId().getStringId() + "__coo",
                    cookie);
        }
        else
        {
            Cookie[] tmp = (Cookie[]) request.getAttribute(portletWindow
                    .getId().getStringId()
                    + "__coo");
            Cookie[] values = new Cookie[tmp.length];
            for (int i = 0; i < tmp.length; i++)
            {
                values[i] = tmp[i];
            }
            values[tmp.length] = cookie;
        }
    }

    public Cookie[] getCookieProperty(HttpServletRequest request,
            org.apache.pluto.PortletWindow portletWindow)
    {
        return (Cookie[]) request.getAttribute(portletWindow.getId()
                .getStringId()
                + "__coo"); 
    }

}