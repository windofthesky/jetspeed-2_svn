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
import org.apache.jetspeed.engine.servlet.ServletObjectAccess;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.PortletContainerEnvironment;

/**
 * Portlet Container Wrapper to secure access to portlet container.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedPortletContainerWrapper.java 187042 2004-07-20 13:52:20Z weaver $
 */
public class JetspeedPortletContainerWrapper implements PortletContainerWrapper
{
    private boolean initialized = false;
    private static final Log log = LogFactory.getLog(JetspeedPortletContainerWrapper.class);
    private PortletContainer pluto;

    public JetspeedPortletContainerWrapper(PortletContainer pluto)
    {
        this.pluto = pluto;
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
            ServletObjectAccess.getServletRequest(servletRequest, portletWindow),
            ServletObjectAccess.getServletResponse(servletResponse, portletWindow));
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

}
