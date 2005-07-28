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
package org.apache.portals.bridges.struts;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PortletServletRequestWrapper
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PortletServletRequestWrapper extends HttpServletRequestWrapper
{
    private static final Log log = LogFactory.getLog(PortletServletRequestWrapper.class);
    private ServletContext context;
    public PortletServletRequestWrapper(ServletContext context, HttpServletRequest request)
    {
        super(request);
        this.context = context;
    }

    public String getPathInfo()
    {
        return (String) getAttribute("javax.servlet.include.path_info");
    }

    public String getContextPath()
    {
        return (String) getAttribute("javax.servlet.include.context_path");
    }

    public String getRequestURI()
    {
        return (String) getAttribute("javax.servlet.include.request_uri");
    }

    public String getServletPath()
    {
        return (String) getAttribute("javax.servlet.include.servlet_path");
    }

    public String getQueryString()
    {
        return (String) getAttribute("javax.servlet.include.query_string");
    }

    public RequestDispatcher getRequestDispatcher(String relativePath)
    {
        // Below comment and workaround taken from
        // org.apache.jasper.runtime.JspRuntimeLibrary.include(...)
        // of Tomcat 4.1.29.
        //
        // FIXME - It is tempting to use request.getRequestDispatcher() to
        // resolve a relative path directly, but Catalina currently does not
        // take into account whether the caller is inside a RequestDispatcher
        // include or not. Whether Catalina *should* take that into account
        // is a spec issue currently under review. In the mean time,
        String path;
        if (!relativePath.startsWith("/"))
        {
            path = getServletPath();
            path = path.substring(0, path.lastIndexOf('/')) + '/'
                    + relativePath;
        } else
            path = relativePath;
        // Because our wrapped request actually is within the Portal context
        // using getRequest().getRequestDispatcher(path) still won't work!
        // Therefore to keep it inside the PortletContext our own
        // servletContext is
        // asked for a dispatcher. The above patch ensures that all requested
        // paths
        // are context relative.
        RequestDispatcher dispatcher = context.getRequestDispatcher(path);
        if (dispatcher != null)
            return new PortletServletRequestDispatcher(dispatcher, path, false);
        else
            return null;
    }
}
