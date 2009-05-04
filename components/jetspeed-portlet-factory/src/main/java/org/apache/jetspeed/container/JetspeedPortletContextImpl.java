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

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.om.portlet.JetspeedServiceReference;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;
import org.apache.pluto.container.RequestDispatcherService;
import org.apache.pluto.container.impl.PortletContextImpl;
import org.apache.portals.bridges.common.ServletContextProvider;

/**
 * Implements the Portlet API Portlet Context class
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortletContextImpl extends PortletContextImpl implements JetspeedPortletContext
{
    private static final String CONTAINER_SUPPORTED_RUNTIME_OPTION = "container.supported.runtimeOption";

    private ServletContextProvider servletContextProvider;
    
    public JetspeedPortletContextImpl(ServletContext servletContext, 
                                      PortletApplication application, 
                                      ContainerInfo containerInfo, 
                                      PortalConfiguration configuration,
                                      RequestDispatcherService rdService,
                                      ServletContextProvider servletContextProvider)
    {        
        super(servletContext, application, containerInfo, Arrays.asList(configuration.getStringArray(CONTAINER_SUPPORTED_RUNTIME_OPTION)), rdService);
        this.servletContextProvider = servletContextProvider;
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
        return super.getAttribute(name);
    }

    public PortletApplication getApplicationDefinition()
    {
        return (PortletApplication)portletApp;
    }
    
    public ServletContextProvider getServletContextProvider()
    {
        return servletContextProvider;
    }
}
