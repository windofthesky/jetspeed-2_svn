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
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletRequestDispatcher;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.JetspeedServiceReference;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;
import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;

/**
 * Implements the Portlet API Portlet Context class
 * TODO: 2.2 deprecate ContainerInfo and use central configuration (see ContainerRuntimeOptions)
 * TODO: on LOCAL apps, we need to merge in web.xml props. See PLT 10.3
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortletContextImpl implements JetspeedPortletContext
{
    /**
     * The path to the Local Portlet Apps directory
     */
    public static final String LOCAL_PA_ROOT = "/WEB-INF/apps";

    protected PortletFactory factory;
    protected PortalContext portalContext;
    
    public JetspeedPortletContextImpl(PortalContext portalContext, ServletContext servletContext, PortletApplication application, PortletFactory factory)
    {
// TODO        super(servletContext, (PortletApplicationDefinition)application);
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
        return null;// TODO
    }

    public PortletRequestDispatcher getNamedDispatcher(String name)
    {
        return null;// TODO
    }

    public InputStream getResourceAsStream(String path)
    {
        return null;// TODO
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
            Collection<JetspeedServiceReference> validServices = getApplicationDefinition().getJetspeedServices();
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
        return null; //TODO return servletContext.getAttribute(name);
    }

    public String getRealPath(String path)
    {
        return null; //TODO return servletContext.getRealPath(localizePath(path, (PortletApplication)this.portletApp));
    }

    public java.net.URL getResource(String path) throws java.net.MalformedURLException
    {
        return null; //TODO return servletContext.getResource(localizePath(path, (PortletApplication)this.portletApp));
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
        return null; //TODO return (PortletApplication)this.portletApp;
    }

    private List<String> DUMMY_CONFIGURATION = new LinkedList<String>(); // TODO: 2.2 implement
    
    protected List<String> getSupportedContainerRuntimeOptions()
    {
        // TODO: 2.2 - pull these out of jetspeed.properties or something similiar
        return DUMMY_CONFIGURATION;
    }

    public PortletApplication getApplicationDefinition()
    {
        return null; //TODO return (PortletApplication)this.portletApp;
    }
    
    public String getContextPath()
    {
        return null; //TODO 
//        
//        if (getApplicationDefinition().getApplicationType() == PortletApplication.WEBAPP)
//        {
//            return super.getContextPath();
//        }
//        else
//        {
//            return portalContext.getContextPath();
//        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.JetspeedPortletContext#getServletContext()
     */
    public ServletContext getServletContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#getAttributeNames()
     */
    public Enumeration<String> getAttributeNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#getContainerRuntimeOptions()
     */
    public Enumeration<String> getContainerRuntimeOptions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#getInitParameterNames()
     */
    public Enumeration<String> getInitParameterNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#getMimeType(java.lang.String)
     */
    public String getMimeType(String file)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#getPortletContextName()
     */
    public String getPortletContextName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#getResourcePaths(java.lang.String)
     */
    public Set<String> getResourcePaths(String path)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#log(java.lang.String, java.lang.Throwable)
     */
    public void log(String message, Throwable throwable)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#log(java.lang.String)
     */
    public void log(String msg)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object object)
    {
        // TODO Auto-generated method stub
        
    }
    
    
}
