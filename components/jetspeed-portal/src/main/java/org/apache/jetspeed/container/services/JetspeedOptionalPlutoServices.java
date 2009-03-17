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

import org.apache.pluto.container.CCPPProfileService;
import org.apache.pluto.container.NamespaceMapper;
import org.apache.pluto.container.OptionalContainerServices;
import org.apache.pluto.container.PortletEnvironmentService;
import org.apache.pluto.container.PortletInvokerService;
import org.apache.pluto.container.PortletPreferencesService;
import org.apache.pluto.container.UserInfoService;

/**
 * Service accessor for all Pluto *optional* container services
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedOptionalPlutoServices implements OptionalContainerServices
{
    protected NamespaceMapper namespaceMapper;
    protected PortletEnvironmentService environmentService;
    protected UserInfoService userInfoService;
    protected PortletInvokerService invokerService;
    protected PortletPreferencesService preferencesService;
    private CCPPProfileService profileService;
    
    public JetspeedOptionalPlutoServices(NamespaceMapper namespaceMapper,
            PortletEnvironmentService environmentService,
            UserInfoService userInfoService,
            PortletInvokerService invokerService, PortletPreferencesService preferencesService)
    {
        this.namespaceMapper = namespaceMapper;
        this.environmentService = environmentService;
        this.userInfoService = userInfoService;
        this.invokerService = invokerService;
        this.preferencesService = preferencesService;
    }
    
    public CCPPProfileService getCCPPProfileService()
    {
        return this.profileService;
    }

    public NamespaceMapper getNamespaceMapper()
    {
        return namespaceMapper;
    }

    public PortletEnvironmentService getPortletEnvironmentService()
    {
        return environmentService;
    }

    public PortletInvokerService getPortletInvokerService()
    {
        return invokerService;
    }

    public PortletPreferencesService getPortletPreferencesService()
    {
        return preferencesService;
    }

    public UserInfoService getUserInfoService()
    {
        return userInfoService;
    }
}
