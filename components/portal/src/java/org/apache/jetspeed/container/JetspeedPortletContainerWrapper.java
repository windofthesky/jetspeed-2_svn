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

import java.io.IOException;
import java.util.Properties;

import javax.portlet.PortletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.engine.servlet.ServletRequestFactory;
import org.apache.jetspeed.engine.servlet.ServletResponseFactory;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.PortletContainerEnvironment;

/**
 * Portlet Container Wrapper to secure access to portlet container.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortletContainerWrapper implements PortletContainerWrapper
{
    private boolean initialized = false;
    private static final Log log = LogFactory.getLog(JetspeedPortletContainerWrapper.class);
    private final PortletContainer pluto;
    private final String containerId;
    private final Properties properties;
    private final PortletContainerEnvironment environment;
    private final ServletConfig servletConfig;
    
    private ServletRequestFactory requestFactory;
    private ServletResponseFactory responseFactory;

    public JetspeedPortletContainerWrapper(PortletContainer pluto, String containerId, 
            ServletConfig servletConfig, PortletContainerEnvironment env, Properties properties)
    {
        this.pluto = pluto;
        this.containerId = containerId;
        this.environment = env;
        this.properties = properties;
        this.servletConfig = servletConfig;
    }
    
    public JetspeedPortletContainerWrapper(PortletContainer pluto, String containerId, 
            ServletConfig servletConfig, PortletContainerEnvironment env)
    {
        this(pluto, containerId, servletConfig, env, new Properties());
    }
    
    /**
     * Allows starting of the container without providing calling the 
     * <code>init()</code> method without all of the arguments as the
     * arguments have already been provided in the constructor.
     * 
     * @throws PortletContainerException
     */
    public void start() throws PortletContainerException
    {
        log.info("Attmepting to start Pluto portal container...");
        this.init(containerId, servletConfig, environment, properties);
        log.info("Pluto portlet container successfully started.");
    }
  
    /**
     * initialization is still handled outside component architecture, since Pluto is not a component
     */
    public synchronized void init(
        String uniqueContainerId,
        ServletConfig servletConfig,
        PortletContainerEnvironment environment,
        Properties props)
        throws PortletContainerException
    {

        pluto.init(uniqueContainerId, servletConfig, environment, props);
        initialized = true;
    }

    public synchronized void shutdown() throws PortletContainerException
    {
        initialized = false;
        pluto.shutdown();
    }

    public void renderPortlet(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
        throws PortletException, IOException, PortletContainerException
    {
                
        if(portletWindow.getPortletEntity() == null)
        {
            log.warn("Could not render PortletWindow "+ portletWindow.getId() + " as it has no PortletEntity defined.");
            return;
        }        
        
        if(portletWindow.getPortletEntity().getPortletDefinition() == null)
        {
            log.warn("Could not render PortletWindow"+ portletWindow.getId() + " as it has no PortletDefintion defined.");
            return;
        }
        pluto.renderPortlet(portletWindow, servletRequest, servletResponse);
        // TODO: figure out how to access pluto-services before container kicks in
        //                              ServletObjectAccess.getServletRequest(servletRequest),
        //                              ServletObjectAccess.getServletResponse(servletResponse));
    }

    public void processPortletAction(
        PortletWindow portletWindow,
        HttpServletRequest servletRequest,
        HttpServletResponse servletResponse)
        throws PortletException, IOException, PortletContainerException
    {
        pluto.processPortletAction(portletWindow, servletRequest, servletResponse);
        //                                     ServletObjectAccess.getServletRequest(servletRequest),
        //                                     ServletObjectAccess.getServletResponse(servletResponse));
    }

    public void portletLoad(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
        throws PortletException, PortletContainerException
    {
        pluto.portletLoad(
            portletWindow,
            requestFactory.getServletRequest(servletRequest, portletWindow),
            responseFactory.getServletResponse(servletResponse));
    }

    /**
     * <p>
     * isInitialized
     * </p>
     *
     * @see org.apache.pluto.PortletContainer#isInitialized()
     * @return
     */
    public boolean isInitialized()
    {
        return initialized;
    }

    public void setRequestFactory(ServletRequestFactory requestFactory)
    {
        this.requestFactory = requestFactory;
    }

    public void setResponseFactory(ServletResponseFactory responseFactory)
    {
        this.responseFactory = responseFactory;
    }

}
