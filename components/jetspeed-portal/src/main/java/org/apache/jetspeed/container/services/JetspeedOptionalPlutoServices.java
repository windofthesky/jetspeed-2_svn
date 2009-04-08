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

import java.util.Set;

import javax.ccpp.Attribute;
import javax.ccpp.Component;
import javax.ccpp.Profile;
import javax.ccpp.ProfileDescription;
import javax.servlet.http.HttpServletRequest;

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
    private CCPPProfileService profileService = new DummyCCPPProfileServiceImpl();
    
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
    
    class DummyCCPPProfileServiceImpl implements CCPPProfileService 
    {

        /* (non-Javadoc)
         * @see org.apache.pluto.spi.CCPPProfileService#getCCPPProfile()
         */
        public Profile getCCPPProfile(HttpServletRequest httpServletRequest) 
        {
            return new DummyProfile();
            // FIXME: Here we have to return a "real" javax.ccpp.Profile
        }

    }

    // FIXME: Here we have to return a "real" javax.ccpp.Profile    
    class DummyProfile implements Profile
    {

        /*
         * (non-Javadoc)
         * 
         * @see javax.ccpp.Profile#getAttribute(java.lang.String)
         */
        public Attribute getAttribute(String arg0)
        {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.ccpp.Profile#getAttributes()
         */
        @SuppressWarnings("unchecked")
        public Set getAttributes()
        {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.ccpp.Profile#getComponent(java.lang.String)
         */
        public Component getComponent(String arg0)
        {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.ccpp.Profile#getComponents()
         */
        @SuppressWarnings("unchecked")
        public Set getComponents()
        {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.ccpp.Profile#getDescription()
         */
        public ProfileDescription getDescription()
        {
            return null;
        }

    }
    
}
