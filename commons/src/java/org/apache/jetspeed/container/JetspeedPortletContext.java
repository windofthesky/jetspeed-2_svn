/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.container;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;

import org.apache.jetspeed.dispatcher.JetspeedRequestDispatcher;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.container.namespace.NamespaceMapper;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

/**
 * Implements the Portlet API Portlet Context class
 *
 * TODO: on LOCAL apps, we need to merge in web.xml props. See PLT 10.3
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortletContext implements PortletContext, InternalPortletContext
{
    /**
     * The path to the Local Portlet Apps directory
     */
    public static final String LOCAL_PA_ROOT = "/WEB-INF/apps";

    private static final int MAJOR_VERSION = 2;
    private static final int MINOR_VERSION = 0;
    private static final String JETSPEED_APPLICATION_INFO =
        "Jakarta Jetspeed Portal Server, Version " + MAJOR_VERSION + "." + MINOR_VERSION;

    private ServletContext servletContext;
    private MutablePortletApplication application;

    public JetspeedPortletContext(ServletContext servletContext, PortletApplicationDefinition application)
    {
        this.servletContext = servletContext;
        this.application = (MutablePortletApplication)application;
    }

    public int getMajorVersion()
    {
        return MAJOR_VERSION;
    }

    public int getMinorVersion()
    {
        return MINOR_VERSION;
    }

    // Delegated methods

    public java.util.Set getResourcePaths(String path)
    {
        return servletContext.getResourcePaths(path);
    }

    public javax.portlet.PortletRequestDispatcher getRequestDispatcher(String path)
    {
        String localizedPath = localizePath(path, this.application);
        RequestDispatcher rd = servletContext.getRequestDispatcher(localizedPath);


        // TODO: factory
        return new JetspeedRequestDispatcher(rd);
    }

    public PortletRequestDispatcher getNamedDispatcher(String name)
    {
        // TODO: localize name

        RequestDispatcher rd = servletContext.getNamedDispatcher(name);
        // TODO: factory

        return new JetspeedRequestDispatcher(rd);
    }

    public String getMimeType(String file)
    {
        return servletContext.getMimeType(file);
    }

    public InputStream getResourceAsStream(String path)
    {
        return servletContext.getResourceAsStream(localizePath(path, this.application));
    }

    public java.lang.Object getAttribute(java.lang.String name)
    {
        String attributeName = NamespaceMapper.encode(application.getId().toString(), name);
        Object attribute = servletContext.getAttribute(attributeName);

        if (attribute == null)
        {
            // TBD, not sure, if this should be done for all attributes or only javax.servlet.
            attribute = servletContext.getAttribute(name);
        }
        return attribute;
    }

    public void log(java.lang.String msg)
    {
        // TODO: setup a logger for the portlet application
        servletContext.log(msg);
    }

    public void log(java.lang.String message, java.lang.Throwable throwable)
    {
        // TODO: setup a logger for the portlet application
        servletContext.log(message, throwable);
    }

    public String getRealPath(String path)
    {
        return servletContext.getRealPath(localizePath(path, this.application));
    }

    public java.net.URL getResource(String path) throws java.net.MalformedURLException
    {
        return servletContext.getResource(localizePath(path, this.application));
    }

    public Enumeration getAttributeNames()
    {
        Enumeration attributes = servletContext.getAttributeNames();

        Vector portletAttributes = new Vector();

        while (attributes.hasMoreElements())
        {
            String attribute = (String) attributes.nextElement();

            String portletAttribute = NamespaceMapper.encode(application.getId().toString(), attribute);

            if (portletAttribute != null) // it is in the portlet's namespace
            {
                portletAttributes.add(portletAttribute);
            }
        }

        return portletAttributes.elements();
    }

    public java.lang.String getInitParameter(java.lang.String name)
    {
        return servletContext.getInitParameter(name);
    }

    public java.util.Enumeration getInitParameterNames()
    {
        return servletContext.getInitParameterNames();
    }

    public void removeAttribute(java.lang.String name)
    {
        servletContext.removeAttribute(NamespaceMapper.encode(application.getId().toString(), name));
    }

    public void setAttribute(java.lang.String name, java.lang.Object object)
    {
        servletContext.setAttribute(NamespaceMapper.encode(application.getId().toString(), name), object);
    }

    public String getServerInfo()
    {
        return JETSPEED_APPLICATION_INFO;
    }

    // internal portlet context implementation

    public ServletContext getServletContext()
    {
        return this.servletContext;
    }

    /**
     * @see org.apache.jetspeed.container.InternalPortletContext#getApplication()
     */
    public PortletApplicationDefinition getApplication()
    {
        return this.application;
    }

    public String getPortletContextName()
    {
        return servletContext.getServletContextName();
    }

    private String localizePath(String path, MutablePortletApplication app)
    {
        if (path == null)
        {
            return "/";
        }
        if (app.getApplicationType() == MutablePortletApplication.WEBAPP)
        {
            return path;
        }

        StringBuffer pathBuffer = new StringBuffer(LOCAL_PA_ROOT);
        pathBuffer.append(app.getWebApplicationDefinition().getContextRoot());
        if (!path.startsWith("/"))
        {
            pathBuffer.append("/");
        }
        pathBuffer.append(path);
        String result = pathBuffer.toString();
        return result;
    }
}
