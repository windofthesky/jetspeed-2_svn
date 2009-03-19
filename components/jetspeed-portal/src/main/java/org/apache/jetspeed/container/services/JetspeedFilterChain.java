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
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.portlet.Filter;

public class JetspeedFilterChain implements FilterChain
{
    private static final Log LOG = LogFactory.getLog(JetspeedFilterChain.class);
    
    private List<Filter> filterList = new ArrayList<Filter>();
    private String lifeCycle;
    Portlet portlet;
    EventPortlet eventPortlet;
    ResourceServingPortlet resourceServingPortlet;
    ClassLoader loader;
    PortletContext portletContext;
    int filterListIndex = 0;

    public JetspeedFilterChain(String lifeCycle){
        this.lifeCycle = lifeCycle;
    }
    public void processFilter(PortletRequest req, PortletResponse res, ClassLoader loader, EventPortlet eventPortlet, PortletContext portletContext) throws IOException, PortletException{
        this.eventPortlet = eventPortlet;
        this.loader = loader;
        this.portletContext = portletContext;
        doFilter((EventRequest)req,(EventResponse) res);
    }
    public void processFilter(PortletRequest req, PortletResponse res, ClassLoader loader, ResourceServingPortlet resourceServingPortlet, PortletContext portletContext) throws IOException, PortletException{
        this.resourceServingPortlet = resourceServingPortlet;
        this.loader = loader;
        this.portletContext = portletContext;
        doFilter((ResourceRequest)req,(ResourceResponse) res);
    }
    
    public void processFilter(PortletRequest req, PortletResponse res, ClassLoader loader, Portlet portlet, PortletContext portletContext) throws IOException, PortletException{
        this.portlet = portlet;
        this.loader = loader;
        this.portletContext = portletContext;
        if (lifeCycle.equals(PortletRequest.ACTION_PHASE)){
            doFilter((ActionRequest)req,(ActionResponse) res);
        }
        else if (lifeCycle.equals(PortletRequest.RENDER_PHASE)){
            doFilter((RenderRequest)req, (RenderResponse)res);
        }
    }
    
    public void addFilter(Filter filter){
        filterList.add(filter);
    }
    
    public void doFilter(ActionRequest request, ActionResponse response) throws IOException, PortletException {
        if (filterListIndex <filterList.size()){
            Filter filter = filterList.get(filterListIndex);
            filterListIndex++;
            try {
                ActionFilter actionFilter = (ActionFilter) filter.getFilterInstance(loader);
                JetspeedFilterConfig filterConfig = new JetspeedFilterConfig(filter.getFilterName(),filter.getInitParams(),portletContext);
                actionFilter.init(filterConfig);
                actionFilter.doFilter(request, response, this);
                actionFilter.destroy();
            } catch (InstantiationException e) {
                LOG.error("Failed to instantiate the filter: " + filter.getFilterClass());
            } catch (IllegalAccessException e) {
                LOG.error("Illegal access to the filter: " + filter.getFilterClass());
            } catch (ClassNotFoundException e) {
                LOG.error("Cannot find the filter class: " + filter.getFilterClass());
            }
        }
        else{
            portlet.processAction(request, response);
        }
    }
    
    public void doFilter(EventRequest request, EventResponse response) throws IOException, PortletException {
        if (filterListIndex <filterList.size()){
            Filter filter = filterList.get(filterListIndex);
            filterListIndex++;
            try {
                EventFilter eventFilter = (EventFilter) filter.getFilterInstance(loader);
                JetspeedFilterConfig filterConfig = new JetspeedFilterConfig(filter.getFilterName(),filter.getInitParams(),portletContext);
                eventFilter.init(filterConfig);
                eventFilter.doFilter(request, response, this);
                eventFilter.destroy();
            } catch (InstantiationException e) {
                LOG.error("Failed to instantiate the filter: " + filter.getFilterClass());
            } catch (IllegalAccessException e) {
                LOG.error("Illegal access to the filter: " + filter.getFilterClass());
            } catch (ClassNotFoundException e) {
                LOG.error("Cannot find the filter class: " + filter.getFilterClass());
            }
        }
        else{
            eventPortlet.processEvent(request, response);
        }
    }

    public void doFilter(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        if (filterListIndex <filterList.size()){
            Filter filter = filterList.get(filterListIndex);
            filterListIndex++;
            try {
                RenderFilter renderFilter = (RenderFilter) filter.getFilterInstance(loader);
                JetspeedFilterConfig filterConfig = new JetspeedFilterConfig(filter.getFilterName(),filter.getInitParams(),portletContext);
                renderFilter.init(filterConfig);
                renderFilter.doFilter(request, response, this);
                renderFilter.destroy();
            } catch (InstantiationException e) {
                LOG.error("Failed to instantiate the filter: " + filter.getFilterClass());
            } catch (IllegalAccessException e) {
                LOG.error("Illegal access to the filter: " + filter.getFilterClass());
            } catch (ClassNotFoundException e) {
                LOG.error("Cannot find the filter class: " + filter.getFilterClass());
            }
        }
        else{
            portlet.render(request, response);
        }
    }

    public void doFilter(ResourceRequest request, ResourceResponse response) throws IOException, PortletException {
        if (filterListIndex <filterList.size()){
            Filter filter = filterList.get(filterListIndex);
            filterListIndex++;
            try {
                ResourceFilter resourceFilter = (ResourceFilter) filter.getFilterInstance(loader);
                JetspeedFilterConfig filterConfig = new JetspeedFilterConfig(filter.getFilterName(),filter.getInitParams(),portletContext);
                resourceFilter.init(filterConfig);
                resourceFilter.doFilter(request, response, this);
                resourceFilter.destroy();
            } catch (InstantiationException e) {
                LOG.error("Failed to instantiate the filter: " + filter.getFilterClass());
            } catch (IllegalAccessException e) {
                LOG.error("Illegal access to the filter: " + filter.getFilterClass());
            } catch (ClassNotFoundException e) {
                LOG.error("Cannot find the filter class: " + filter.getFilterClass());
            }
        }
        else{
            resourceServingPortlet.serveResource(request, response);
        }
    }
}
