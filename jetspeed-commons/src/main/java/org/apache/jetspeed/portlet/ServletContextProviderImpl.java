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
package org.apache.jetspeed.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.portals.bridges.common.ServletContextProvider;

/**
 * ServletContextProviderImpl supplies access to the
 * Servlet context of a Jetspeed Portlet.
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class ServletContextProviderImpl implements ServletContextProvider 
{
    public ServletContext getServletContext(GenericPortlet portlet) 
    {
        return ((JetspeedPortletContext)portlet.getPortletContext()).getServletContextProvider().getServletContext(portlet);
    }

    public HttpServletRequest getHttpServletRequest(GenericPortlet portlet, PortletRequest request) 
    {
        return ((JetspeedPortletContext)portlet.getPortletContext()).getServletContextProvider().getHttpServletRequest(portlet, request);
    }

    public HttpServletResponse getHttpServletResponse(GenericPortlet portlet, PortletResponse response) 
    {
        return ((JetspeedPortletContext)portlet.getPortletContext()).getServletContextProvider().getHttpServletResponse(portlet, response);
    }
}
