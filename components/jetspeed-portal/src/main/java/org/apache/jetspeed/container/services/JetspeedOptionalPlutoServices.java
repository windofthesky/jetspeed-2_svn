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
package org.apache.jetspeed.container.services;

import org.apache.pluto.NamespaceMapper;
import org.apache.pluto.OptionalContainerServices;
import org.apache.pluto.spi.optional.PortalAdministrationService;
import org.apache.pluto.spi.optional.PortletEnvironmentService;
import org.apache.pluto.spi.optional.PortletInfoService;
import org.apache.pluto.spi.optional.PortletInvokerService;
import org.apache.pluto.spi.optional.PortletPreferencesService;
import org.apache.pluto.spi.optional.PortletRegistryService;
import org.apache.pluto.spi.optional.RequestAttributeService;
import org.apache.pluto.spi.optional.UserInfoService;

/**
 * Service accessor for all Pluto *optional* container services
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedOptionalPlutoServices implements OptionalContainerServices
{
    protected NamespaceMapper namespaceMapper;
    protected PortalAdministrationService adminService;
    protected PortletEnvironmentService environmentService;
    protected PortletInfoService portletInfoService;    
    protected UserInfoService userInfoService;
    protected RequestAttributeService requestAttributeService;
    protected PortletInvokerService invokerService;
    protected PortletPreferencesService preferencesService;
    protected PortletRegistryService registryService;
    
    public JetspeedOptionalPlutoServices(NamespaceMapper namespaceMapper, PortalAdministrationService adminService,
            PortletEnvironmentService environmentService, PortletInfoService portletInfoService,
            UserInfoService userInfoService, RequestAttributeService requestAttributeService,
            PortletInvokerService invokerService)
    {
        this.namespaceMapper = namespaceMapper;
        this.adminService = adminService;
        this.environmentService = environmentService;
        this.portletInfoService = portletInfoService;
        this.userInfoService = userInfoService;
        this.requestAttributeService = requestAttributeService;
        this.invokerService = invokerService;
    }
    
    public NamespaceMapper getNamespaceMapper()
    {
        return namespaceMapper;
    }

    public PortalAdministrationService getPortalAdministrationService()
    {
        return adminService;
    }

    public PortletEnvironmentService getPortletEnvironmentService()
    {
        return environmentService;
    }

    public PortletInfoService getPortletInfoService()
    {
        return portletInfoService;
    }

    public PortletInvokerService getPortletInvokerService()
    {
        return invokerService;
    }

    public PortletPreferencesService getPortletPreferencesService()
    {
        return preferencesService;
    }

    public PortletRegistryService getPortletRegistryService()
    {
        return registryService;
    }

    public RequestAttributeService getRequestAttributeService()
    {
        return this.requestAttributeService;
    }

    public UserInfoService getUserInfoService()
    {
        return userInfoService;
    }

}
