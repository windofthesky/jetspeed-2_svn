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

import java.io.IOException;

import javax.portlet.Event;
import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.engine.servlet.ServletRequestFactory;
import org.apache.jetspeed.engine.servlet.ServletResponseFactory;
import org.apache.pluto.OptionalContainerServices;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.PortletWindow;
import org.apache.pluto.RequiredContainerServices;

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
    private final String INVALID_WINDOW_TYPE = "Window is not of valid type: ";    
    private final PortletContainer pluto;
    private final String containerId;
    
    private ServletRequestFactory requestFactory;
    private ServletResponseFactory responseFactory;

    public JetspeedPortletContainerWrapper(PortletContainer pluto, String containerId)
    {
        this.pluto = pluto;
        this.containerId = containerId;
    }
    
    public void init() throws PortletContainerException
    {
        log.info("Attmepting to start Pluto portal container...");
        pluto.init();
        initialized = true;        
        log.info("Pluto portlet container successfully started.");        
    }
    
    public synchronized void destroy() throws PortletContainerException
    {
        initialized = false;
        pluto.destroy();
    }
    
    public void doRender(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
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
        pluto.doRender(portletWindow, servletRequest, servletResponse);
    }

    public void doAction(
        PortletWindow portletWindow,
        HttpServletRequest servletRequest,
        HttpServletResponse servletResponse)
        throws PortletException, IOException, PortletContainerException
    {
        pluto.doAction(portletWindow, servletRequest, servletResponse);
    }

    
    public void doLoad(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
        throws PortletException, IOException, PortletContainerException
    {
        if (portletWindow instanceof org.apache.jetspeed.container.PortletWindow)
            throw new PortletException(INVALID_WINDOW_TYPE + org.apache.jetspeed.container.PortletWindow.class);
        org.apache.jetspeed.container.PortletWindow jpw = (org.apache.jetspeed.container.PortletWindow)portletWindow;
        pluto.doLoad(portletWindow, 
            requestFactory.getServletRequest(servletRequest, jpw),
            responseFactory.getServletResponse(servletResponse));
    }

    public void doAdmin(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
        throws PortletException, IOException, PortletContainerException
    {
        pluto.doAdmin(portletWindow, servletRequest, servletResponse);
    }

    public void doServeResource(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    throws PortletException, IOException, PortletContainerException
    {
        pluto.doServeResource(portletWindow, servletRequest, servletResponse);
    }

    public String getName()
    {
        return this.containerId;
    }

    public OptionalContainerServices getOptionalContainerServices()
    {
        return pluto.getOptionalContainerServices();
    }

    public RequiredContainerServices getRequiredContainerServices()
    {
        return pluto.getRequiredContainerServices();
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

    public void fireEvent(HttpServletRequest request,
            HttpServletResponse response, PortletWindow window, Event event)
            throws PortletException, IOException, PortletContainerException
    {
        // TODO Auto-generated method stub
        
    }
    

}
