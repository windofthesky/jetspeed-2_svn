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
    
    public JetspeedRequiredPlutoServices(PortalContext portalContext)
    {
        this.portalContext = portalContext;
    }

    public PortalContext getPortalContext()
    {
        return this.portalContext;
    }

    public EventCoordinationService getEventCoordinationService()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public FilterManagerService getFilterManagerService()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletRequestContextService getPortletRequestContextService()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletURLListenerService getPortletURLListenerService()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
