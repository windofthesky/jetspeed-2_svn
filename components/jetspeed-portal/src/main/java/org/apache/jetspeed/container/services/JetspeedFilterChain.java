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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventPortlet;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

import org.apache.jetspeed.factory.PortletFilterInstance;

public class JetspeedFilterChain implements FilterChain
{
    
    private List<PortletFilterInstance> filterList = new ArrayList<PortletFilterInstance>();
    private String lifeCycle;
    Portlet portlet;
    EventPortlet eventPortlet;
    ResourceServingPortlet resourceServingPortlet;
    PortletContext portletContext;
    int filterListIndex = 0;
    boolean filtersInitialized;

    public JetspeedFilterChain(String lifeCycle)
    {
        this.lifeCycle = lifeCycle;
    }

    public void processFilter(PortletRequest req, PortletResponse res, EventPortlet eventPortlet, PortletContext portletContext) throws IOException, PortletException
    {
        initFilters(portletContext);
        this.eventPortlet = eventPortlet;
        this.portletContext = portletContext;
        doFilter((EventRequest) req, (EventResponse) res);
    }

    public void processFilter(PortletRequest req, PortletResponse res, ResourceServingPortlet resourceServingPortlet,
                              PortletContext portletContext) throws IOException, PortletException
    {
        initFilters(portletContext);
        this.resourceServingPortlet = resourceServingPortlet;
        this.portletContext = portletContext;
        doFilter((ResourceRequest) req, (ResourceResponse) res);
    }

    public void processFilter(PortletRequest req, PortletResponse res, Portlet portlet, PortletContext portletContext) throws IOException, PortletException
    {
        initFilters(portletContext);
        this.portlet = portlet;
        this.portletContext = portletContext;
        if (lifeCycle.equals(PortletRequest.ACTION_PHASE))
        {
            doFilter((ActionRequest) req, (ActionResponse) res);
        }
        else if (lifeCycle.equals(PortletRequest.RENDER_PHASE))
        {
            doFilter((RenderRequest) req, (RenderResponse) res);
        }
    }

    public void addFilterInstance(PortletFilterInstance filter)
    {
        filterList.add(filter);
    }

    public void doFilter(ActionRequest request, ActionResponse response) throws IOException, PortletException
    {
        if (filterListIndex < filterList.size())
        {
            PortletFilterInstance filter = filterList.get(filterListIndex);
            filterListIndex++;
            ActionFilter actionFilter = (ActionFilter) filter.getRealPortletFilter();
            actionFilter.doFilter(request, response, this);
        }
        else
        {
            portlet.processAction(request, response);
        }
    }

    public void doFilter(EventRequest request, EventResponse response) throws IOException, PortletException
    {
        if (filterListIndex < filterList.size())
        {
            PortletFilterInstance filter = filterList.get(filterListIndex);
            filterListIndex++;
            EventFilter eventFilter = (EventFilter) filter.getRealPortletFilter();
            eventFilter.doFilter(request, response, this);
        }
        else
        {
            eventPortlet.processEvent(request, response);
        }
    }

    public void doFilter(RenderRequest request, RenderResponse response) throws IOException, PortletException
    {
        if (filterListIndex < filterList.size())
        {
            PortletFilterInstance filter = filterList.get(filterListIndex);
            filterListIndex++;
            RenderFilter renderFilter = (RenderFilter) filter.getRealPortletFilter();
            renderFilter.doFilter(request, response, this);
        }
        else
        {
            portlet.render(request, response);
        }
    }

    public void doFilter(ResourceRequest request, ResourceResponse response) throws IOException, PortletException
    {
        if (filterListIndex < filterList.size())
        {
            PortletFilterInstance filter = filterList.get(filterListIndex);
            filterListIndex++;
            ResourceFilter resourceFilter = (ResourceFilter) filter.getRealPortletFilter();
            resourceFilter.doFilter(request, response, this);
        }
        else
        {
            resourceServingPortlet.serveResource(request, response);
        }
    }
    
    private void initFilters(PortletContext portletContext) throws PortletException
    {
        if (!this.filtersInitialized)
        {
            for (PortletFilterInstance filterInstance : this.filterList)
            {
                if (!filterInstance.isInitialized())
                {
                    FilterConfig filterConfig = new JetspeedFilterConfigImpl(filterInstance.getFilter(), portletContext);
                    filterInstance.init(filterConfig);
                }
            }
            
            this.filtersInitialized = true;
        }
    }
}
