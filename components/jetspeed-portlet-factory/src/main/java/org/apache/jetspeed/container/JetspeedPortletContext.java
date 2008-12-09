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
package org.apache.jetspeed.container;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletRequestDispatcher;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.JetspeedServiceReference;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;
import org.apache.pluto.internal.impl.PortletContextImpl;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

/**
 * Implements the Portlet API Portlet Context class
 * TODO: 2.2 deprecate ContainerInfo and use central configuration (see ContainerRuntimeOptions)
 * TODO: on LOCAL apps, we need to merge in web.xml props. See PLT 10.3
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortletContext extends PortletContextImpl implements InternalPortletContext
{
    /**
     * The path to the Local Portlet Apps directory
     */
    public static final String LOCAL_PA_ROOT = "/WEB-INF/apps";

    protected PortletFactory factory;
    protected PortalContext portalContext;
    
    public JetspeedPortletContext(PortalContext portalContext, ServletContext servletContext, PortletApplication application, PortletFactory factory)
    {
        super(servletContext, (PortletApplicationDefinition)application);
        this.portalContext = portalContext;
        this.factory = factory;
    }

    public int getMajorVersion()
    {
        return ContainerInfo.getMajorSpecificationVersion();
    }

    public int getMinorVersion()
    {
        return ContainerInfo.getMinorSpecificationVersion();
    }

    public javax.portlet.PortletRequestDispatcher getRequestDispatcher(String path)
    {
        String localizedPath = localizePath(path, (PortletApplication)this.portletApp);
        RequestDispatcher rd = null;        
        try
        {
            rd = servletContext.getRequestDispatcher(localizedPath);
        }
        catch (Exception e)
        {
            // Portlet API says: return null
        }
        if (rd != null)
        {
            return factory.createRequestDispatcher(rd, path);
        }
        return null;
    }

    public PortletRequestDispatcher getNamedDispatcher(String name)
    {
        RequestDispatcher rd = null;
        try
        {
            rd = servletContext.getNamedDispatcher(name);
        }
        catch (Exception e)
        {
            // Portlet API says: return null
        }
        if (rd != null)
        {
            return factory.createRequestDispatcher(rd);
        }
        return null;
    }

    public InputStream getResourceAsStream(String path)
    {
        return servletContext.getResourceAsStream(localizePath(path, (PortletApplication)this.portletApp));
    }

    public java.lang.Object getAttribute(java.lang.String name)
    {
        if ( name == null )
        {
            throw new IllegalArgumentException("Required parameter name is null");
        }
        
        if (name.startsWith("cps:"))
        {
            String serviceName = name.substring("cps:".length());
            
            // validate service
            Collection<JetspeedServiceReference> validServices = ((PortletApplication)portletApp).getJetspeedServices();
            if (null == validServices)
            {
                return null;
            }
            boolean found = false;
            for (JetspeedServiceReference validService : validServices)
            {
                if (validService.getName().equals(serviceName))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                return null;
            }
            // return the service
            PortletServices services = JetspeedPortletServices.getSingleton();
            return services.getService(serviceName);
        }
        return servletContext.getAttribute(name);
    }

    public String getRealPath(String path)
    {
        return servletContext.getRealPath(localizePath(path, (PortletApplication)this.portletApp));
    }

    public java.net.URL getResource(String path) throws java.net.MalformedURLException
    {
        return servletContext.getResource(localizePath(path, (PortletApplication)this.portletApp));
    }

    public String getServerInfo()
    {
        return ContainerInfo.getServerInfo();
    }

    private String localizePath(String path, PortletApplication app)
    {
        if (path == null)
        {
            return "/";
        }
        return path;
        // TODO: local PA with own/extra resource paths support
    }
    
    public PortletApplication getPortletApplicationDefinition()
    {
        return (PortletApplication)this.portletApp;
    }

    private List<String> DUMMY_CONFIGURATION = new LinkedList<String>(); // TODO: 2.2 implement
    
    protected List<String> getSupportedContainerRuntimeOptions()
    {
        // TODO: 2.2 - pull these out of jetspeed.properties or something similiar
        return DUMMY_CONFIGURATION;
    }

    public PortletApplication getApplicationDefinition()
    {
        return (PortletApplication)this.portletApp;
    }
    
    public String getContextPath()
    {
        if (getApplicationDefinition().getApplicationType() == PortletApplication.WEBAPP)
        {
            return super.getContextPath();
        }
        else
        {
            return portalContext.getContextPath();
        }
    }
}
