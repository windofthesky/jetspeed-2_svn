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
package org.apache.jetspeed.portlets.php;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.jetspeed.container.JetspeedPortletContext;


/**
 * PHPServletContextProviderImpl supplies a PHPPortlet access to the
 * Servlet context of a Jetspeed Portlet.
 * @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
 * @version $Id$
 */
public class PHPServletContextProviderImpl implements PHPServletContextProvider 
{
    public ServletContext getServletContext(GenericPortlet portlet) 
    {
        return ((JetspeedPortletContext)portlet.getPortletContext()).getServletContext();
    }

    public HttpServletRequest getHttpServletRequest(GenericPortlet portlet, PortletRequest request) 
    {
        return (HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest();
    }

    public HttpServletResponse getHttpServletResponse(GenericPortlet portlet, PortletResponse response) 
    {
        return (HttpServletResponse) ((HttpServletResponseWrapper) response).getResponse();
    }
}
