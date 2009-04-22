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
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventPortlet;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.container.FilterManager;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.factory.PortletFilterInstance;
import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.FilterMapping;
import org.apache.jetspeed.om.portlet.PortletApplication;

public class JetspeedFilterManager implements FilterManager
{
    private static final Logger log = LoggerFactory.getLogger(JetspeedFilterManager.class);
    
    private PortletFactory portletFactory;
    private JetspeedFilterChain filterchain;
    private PortletApplication portletApp;
    private String portletName;
    private String lifeCycle;

    public JetspeedFilterManager(PortletFactory portletFactory, PortletApplication portletApp, String portletName, String lifeCycle)
    {
        this.portletFactory = portletFactory;
        this.portletApp = portletApp;
        this.portletName = portletName;
        this.lifeCycle = lifeCycle;
        filterchain = new JetspeedFilterChain(lifeCycle);
        initFilterChain();
    }

    private void initFilterChain()
    {
        List<? extends FilterMapping> filterMappingList = portletApp.getFilterMappings();
        
        if (filterMappingList != null)
        {
            for (FilterMapping filterMapping : filterMappingList)
            {
                if (isFilter(filterMapping, portletName))
                {
                    // the filter is specified for the portlet, check the filter for the lifecycle
                    List<? extends Filter> filterList = portletApp.getFilters();
                    
                    for (Filter filter : filterList)
                    {
                        // search for the filter in the filter
                        if (filter.getFilterName().equals(filterMapping.getFilterName()))
                        {
                            // check the lifecycle
                            if (isLifeCycle(filter, lifeCycle))
                            {
                                // the filter match to the portlet and has the specified lifecycle -> add to chain
                                try
                                {
                                    PortletFilterInstance filterInstance = this.portletFactory.getPortletFilterInstance(portletApp, filter.getFilterName());
                                    filterchain.addFilterInstance(filterInstance);
                                }
                                catch (PortletException e)
                                {
                                    String message = "The portlet filter is not available: " + filter.getFilterClass();
                                    log.error(message, e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void processFilter(ActionRequest req, ActionResponse res, Portlet portlet, PortletContext portletContext) throws PortletException, IOException
    {
        filterchain.processFilter(req, res, portlet, portletContext);
    }
    
    public void processFilter(RenderRequest req, RenderResponse res, Portlet portlet, PortletContext portletContext) throws PortletException, IOException
    {
        filterchain.processFilter(req, res, portlet, portletContext);
    }
    
    public void processFilter(ResourceRequest req, ResourceResponse res, ResourceServingPortlet resourceServingPortlet, PortletContext portletContext) throws PortletException, IOException
    {
        filterchain.processFilter(req, res, resourceServingPortlet, portletContext);
    }
    
    public void processFilter(EventRequest req, EventResponse res, EventPortlet eventPortlet, PortletContext portletContext)throws PortletException, IOException
    {
        filterchain.processFilter(req, res, eventPortlet, portletContext);
    }

    private boolean isLifeCycle(Filter filter, String lifeCycle)
    {
        List<String> lifeCyclesList = filter.getLifecycles();
        return lifeCyclesList.contains(lifeCycle);
    }

    private boolean isFilter(FilterMapping filterMapping, String portletName)
    {
        boolean isFilter = false;
        
        for (String filterPortletName : filterMapping.getPortletNames())
        {
            if (filterPortletName.endsWith("*"))
            {
                if (filterPortletName.length() == 1)
                {
                    isFilter = true;
                    break;
                }
                else if (portletName.startsWith(filterPortletName.substring(0, filterPortletName.length() - 1)))
                {
                    isFilter = true;
                    break;
                }
            }
            else if (filterPortletName.equals(portletName))
            {
                isFilter = true;
                break;
            }
        }
        
        return isFilter;
    }
    
}
