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

import org.apache.pluto.container.EventCoordinationService;
import org.apache.pluto.container.FilterManagerService;
import org.apache.pluto.container.PortletRequestContextService;
import org.apache.pluto.container.PortletURLListenerService;
import org.apache.pluto.container.RequiredContainerServices;

/**
 * Service accessor for all Pluto *required* container services and callbacks
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedRequiredPlutoServices implements RequiredContainerServices
{
    private PortalContext portalContext;
    private EventCoordinationService eventCoordinationService;
    private PortletRequestContextService portletRequestContextService;
    private FilterManagerService filterManagerService;
    private PortletURLListenerService portletURLListenerService;

    public JetspeedRequiredPlutoServices(PortalContext portalContext, EventCoordinationService eventCoordinationService,
                                         PortletRequestContextService portletRequestContextService, FilterManagerService filterManagerService,
                                         PortletURLListenerService portletURLListenerService)
    {
        this.portalContext = portalContext;
        this.eventCoordinationService = eventCoordinationService;
        this.portletRequestContextService = portletRequestContextService;
        this.filterManagerService = filterManagerService;
        this.portletURLListenerService = portletURLListenerService;
    }

    public PortalContext getPortalContext()
    {
        return this.portalContext;
    }

    public EventCoordinationService getEventCoordinationService()
    {
        return this.eventCoordinationService;
    }

    public FilterManagerService getFilterManagerService()
    {
        return this.filterManagerService;
    }

    public PortletRequestContextService getPortletRequestContextService()
    {
        return this.portletRequestContextService;
    }

    public PortletURLListenerService getPortletURLListenerService()
    {
        return this.portletURLListenerService;
    }
}
