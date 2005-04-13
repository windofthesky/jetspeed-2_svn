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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


/**
 * PortletServletContextImpl
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PortletServletContextImpl implements ServletContext
{
    private ServletContext context;
    public PortletServletContextImpl(ServletContext context)
    {
        this.context = context;
    }
    public Object getAttribute(String arg0)
    {
        return context.getAttribute(arg0);
    }
    public Enumeration getAttributeNames()
    {
        return context.getAttributeNames();
    }
    public ServletContext getContext(String arg0)
    {
        ServletContext refContext = context.getContext(arg0);
        if (refContext == context)
            return this;
        else
            return refContext;
    }
    public String getInitParameter(String arg0)
    {
        return context.getInitParameter(arg0);
    }
    public Enumeration getInitParameterNames()
    {
        return context.getInitParameterNames();
    }
    public int getMajorVersion()
    {
        return context.getMajorVersion();
    }
    public String getMimeType(String arg0)
    {
        return context.getMimeType(arg0);
    }
    public int getMinorVersion()
    {
        return context.getMinorVersion();
    }
    public RequestDispatcher getNamedDispatcher(String arg0)
    {
        RequestDispatcher dispatcher = context.getNamedDispatcher(arg0);
        if (dispatcher != null)
            dispatcher = new PortletServletRequestDispatcher(dispatcher, arg0,
                    true);
        return dispatcher;
    }
    public String getRealPath(String arg0)
    {
        return context.getRealPath(arg0);
    }
    public RequestDispatcher getRequestDispatcher(String arg0)
    {
        RequestDispatcher dispatcher = context.getRequestDispatcher(arg0);
        if (dispatcher != null)
            dispatcher = new PortletServletRequestDispatcher(dispatcher, arg0,
                    false);
        return dispatcher;
    }
    public URL getResource(String arg0) throws MalformedURLException
    {
        return context.getResource(arg0);
    }
    public InputStream getResourceAsStream(String arg0)
    {
        return context.getResourceAsStream(arg0);
    }
    public Set getResourcePaths(String arg0)
    {
        return context.getResourcePaths(arg0);
    }
    public String getServerInfo()
    {
        return context.getServerInfo();
    }
    
    /**
     * @deprecated Deprecated. As of Java Servlet API 2.1, 
     * with no direct replacement. 
     */
    public Servlet getServlet(String arg0) throws ServletException
    {
        return context.getServlet(arg0);
    }
    public String getServletContextName()
    {
        return context.getServletContextName();
    }
    
    /**
     * @deprecated  As of Java Servlet API 2.0, 
     * with no replacement.
     */
    public Enumeration getServletNames()
    {
        return context.getServletNames();
    }
    
    /**
     * @deprecated  As of Java Servlet API 2.0, 
     * with no replacement.
     */
    public Enumeration getServlets()
    {
        return context.getServlets();
    }
    
    /**
     * @deprecated As of Java Servlet API 2.1, use 
     * log(String message, Throwable throwable) instead.
     */
    public void log(Exception arg0, String arg1)
    {
        context.log(arg0, arg1);
    }
    public void log(String arg0)
    {
        context.log(arg0);
    }
    public void log(String arg0, Throwable arg1)
    {
        context.log(arg0, arg1);
    }
    public void removeAttribute(String arg0)
    {
        context.removeAttribute(arg0);
    }
    public void setAttribute(String arg0, Object arg1)
    {
        context.setAttribute(arg0, arg1);
    }
    public String toString()
    {
        return context.toString();
    }
}