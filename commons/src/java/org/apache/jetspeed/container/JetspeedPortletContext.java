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
