/*
 * Copyright 2003,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.container.url.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.core.impl.PortletURLImpl;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Jetspeed extension of the Pluto PortalURLImpl providing support for session url rewriting
 * when cookies are disabled.
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedPortletURL extends PortletURLImpl
{
    public JetspeedPortletURL(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse, boolean isAction)
    {
        super(portletWindow, servletRequest, servletResponse, isAction);
    }

    public String toString()
    {
        return servletResponse.encodeURL(super.toString());
    }
}
