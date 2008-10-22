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

import javax.portlet.PortalContext;

import org.apache.pluto.RequiredContainerServices;
import org.apache.pluto.spi.CCPPProfileService;
import org.apache.pluto.spi.ContainerInvocationService;
import org.apache.pluto.spi.PortalCallbackService;

/**
 * Service accessor for all Pluto *required* container services and callbacks
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedRequiredPlutoServices implements RequiredContainerServices
{
    private CCPPProfileService profileService;
    private ContainerInvocationService invocationService;
    private PortalCallbackService callbackService;
    private PortalContext portalContext;
    
    public JetspeedRequiredPlutoServices(CCPPProfileService profileService, ContainerInvocationService invocationService,
            PortalCallbackService callbackService, PortalContext portalContext)
    {
        this.profileService = profileService;
        this.invocationService = invocationService;
        this.callbackService = callbackService;
        this.portalContext = portalContext;
    }

    public CCPPProfileService getCCPPProfileService()
    {
        return this.profileService;
    }

    public ContainerInvocationService getContainerInvocationService()
    {
        return this.invocationService;
    }

    public PortalCallbackService getPortalCallbackService()
    {
        return this.callbackService;
    }

    public PortalContext getPortalContext()
    {
        return this.portalContext;
    }

}
