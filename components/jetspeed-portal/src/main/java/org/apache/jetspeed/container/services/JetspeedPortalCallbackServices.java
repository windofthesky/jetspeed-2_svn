/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.jetspeed.container.services;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.container.providers.EventProviderImpl;
import org.apache.jetspeed.container.providers.PortletURLProviderImpl;
import org.apache.jetspeed.container.providers.ResourceURLProviderImpl;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.EventContainer;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.spi.EventProvider;
import org.apache.pluto.spi.FilterManager;
import org.apache.pluto.spi.PortalCallbackService;
import org.apache.pluto.spi.PortletURLListener;
import org.apache.pluto.spi.PortletURLProvider;
import org.apache.pluto.spi.PropertyManager;
import org.apache.pluto.spi.ResourceURLProvider;

/**
 * Callback Service accessor for all Pluto *required* container providers
 * (callbacks) TODO: 2.2 implement PropertyManager related functionality
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedPortalCallbackServices implements PortalCallbackService
{
    public static final String PER_REQUEST_EVENT_PROVIDER = "org.apache.jetspeed.container.EventProvider";

    PropertyManager propertyManager;   
    FilterManager filterManager;
    PortletURLListener urlListener;
    PortletWindowAccessor windowAccessor;
    EventContainer eventContainer; 
    
    public JetspeedPortalCallbackServices(final PropertyManager propertyManager,
            final FilterManager filterManager, final PortletURLListener urlListener, final PortletWindowAccessor windowAccessor)
    {
        this.propertyManager = propertyManager;
        this.filterManager = filterManager;
        this.urlListener = urlListener;
        this.windowAccessor = windowAccessor;
    }

    public void setEventContainer(final EventContainer eventContainer)
    {
        this.eventContainer = eventContainer;
    }
    
    public PortletURLProvider getPortletURLProvider(HttpServletRequest request,
            org.apache.pluto.PortletWindow portletWindow)
    {
        RequestContext rc = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        return new PortletURLProviderImpl(rc, (org.apache.jetspeed.container.PortletWindow)portletWindow);
    }

    public ResourceURLProvider getResourceURLProvider(
            HttpServletRequest request, org.apache.pluto.PortletWindow portletWindow)
    {
        RequestContext rc = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        return new ResourceURLProviderImpl(rc, portletWindow);
    }

    public PropertyManager getPropertyManager()
    {
        return propertyManager;
    }
        
    
    public EventProvider getEventProvider(HttpServletRequest request,
            org.apache.pluto.PortletWindow portletWindow)
    {   
        RequestContext rc = (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        return new EventProviderImpl(rc, portletWindow, this.windowAccessor);
    }

    public EventProvider getEventProvider()
    {
        throw new RuntimeException("TODO: need to deprecate this");
    }

    /**
     * Returns the FilterManager, this is used to process the filter.
     * 
     * @return FilterManager
     */
    public FilterManager getFilterManager(
            PortletApplicationDefinition portletAppDD, String portletName,
            String lifeCycle)
    {
        return filterManager;
    }

    public PortletURLListener getPortletURLListener()
    {
        return urlListener;
    }

    /**
     * Method invoked by the container when the portlet sets its title. This
     * method binds the dynamic portlet title to the servlet request for later
     * use.
     */
    public void setTitle(HttpServletRequest request,
            org.apache.pluto.PortletWindow portletWindow, String titleArg)
    {
        // TODO: 2.2 jetspeed uses a title service        
        String title = null;
        if (titleArg == null || titleArg.length() == 0)
        {
            title = getTitleFromPortletDefinition(portletWindow, request);
        }
        else
        {
            title = titleArg;
        }
        request.setAttribute(
                PortalReservedParameters.OVERRIDE_PORTLET_TITLE_ATTR
                        + "::window.id::" + portletWindow.getId(), title);        
    }

    protected final String getTitleFromPortletDefinition(org.apache.pluto.PortletWindow window,
            HttpServletRequest request)
    {
        String title = null;
        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        
        org.apache.jetspeed.container.PortletWindow  jsWindow = (org.apache.jetspeed.container.PortletWindow)window;
        PortletEntity entity = jsWindow.getPortletEntity();
        if (entity != null && entity.getPortletDefinition() != null)
        {
            title = requestContext.getPreferedLanguage(
                    entity.getPortletDefinition()).getTitle();
        }

        if (title == null && entity.getPortletDefinition() != null)
        {
            title = entity.getPortletDefinition().getPortletName();
        }
        else if (title == null)
        {
            title = "Invalid portlet entity " + entity.getId();
        }
        
        return title;
    }

    
}
