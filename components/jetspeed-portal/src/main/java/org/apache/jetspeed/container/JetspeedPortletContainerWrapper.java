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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.pluto.container.ContainerServices;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletContainerException;
import org.apache.pluto.container.PortletWindow;

/**
 * Portlet Container Wrapper to secure access to portlet container.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortletContainerWrapper implements PortletContainerWrapper
{
    private boolean initialized = false;
    private static final Logger log = LoggerFactory.getLogger(JetspeedPortletContainerWrapper.class);
    private final PortletContainer pluto;
    private final String containerId;
    
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
        pluto.doRender(portletWindow, servletRequest, servletResponse);
    }

    public void doAction(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    throws PortletException, IOException, PortletContainerException
    {
        pluto.doAction(portletWindow, servletRequest, servletResponse);
    }
    
    public void doLoad(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    throws PortletException, IOException, PortletContainerException
    {
        pluto.doLoad(portletWindow, servletRequest, servletResponse);
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

    public void doEvent(PortletWindow portletWindow, HttpServletRequest request, HttpServletResponse response, Event event)
    throws PortletException, IOException, PortletContainerException
    {
        pluto.doEvent(portletWindow, request, response, event);
    }
                
    public String getName()
    {
        return this.containerId;
    }

    public ContainerServices getContainerServices()
    {
        return pluto.getContainerServices();
    }

    public boolean isInitialized()
    {
        return initialized;
    }
}
