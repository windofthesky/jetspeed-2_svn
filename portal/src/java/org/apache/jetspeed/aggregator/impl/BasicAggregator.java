/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.aggregator.impl;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.Aggregator;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Basic Aggregator, nothing complicated. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class BasicAggregator implements Aggregator
{
    private final static Log log = LogFactory.getLog(BasicAggregator.class);

    public final static int STRATEGY_SEQUENTIAL = 0;
    public final static int STRATEGY_PARALLEL = 1;
    private int strategy = STRATEGY_SEQUENTIAL;
    
    private PortletRegistry registry;
    private PortletWindowAccessor windowAccessor;
    private PortletContainer portletContainer;

    public BasicAggregator(PortletRegistry registry, 
                           PortletWindowAccessor windowAccessor,
                           PortletContainer portletContainer,
                           int strategy)
    {
        this.registry = registry;
        this.windowAccessor = windowAccessor;
        this.strategy = strategy;
        this.portletContainer = portletContainer;
    }
    
    public BasicAggregator(PortletRegistry registry, 
                           PortletWindowAccessor windowAccessor,
                           PortletContainer portletContainer)            
    {
        this(registry, windowAccessor, portletContainer, STRATEGY_SEQUENTIAL);
    }
    
    public void start()
    {
    }
    
    public void stop()
    {
        
    }
    
    /**
     * Builds the portlet set defined in the context into a portlet tree.
     *
     * @return Unique Portlet Entity ID
     */
    public void build(RequestContext request) throws JetspeedException
    {
        Page page = request.getPage();
        if (null == page)
        {
            throw new JetspeedException("Failed to find PSML Pin BasicAggregator.build");
        }

        Fragment root = page.getRootFragment();
        render(portletContainer, root, request);
        
        for (Iterator fit = root.getFragments().iterator(); fit.hasNext();)
        {
            Fragment fragment = (Fragment)fit.next();
            
            if (fragment.getType().equals(Fragment.LAYOUT))
            {
                // skip layouts for now
                // continue;
            }
            render(portletContainer, fragment, request);
        }
    }

    /**
     * Render a portlet by calling the container's renderPortlet.
     * 
     * @param container
     * @param fragment
     * @param request
     */
    private void render(PortletContainer container, Fragment fragment, RequestContext request)
    {

        //
        // create the portlet window and render the portlet
        //
        try
        {
            // 
            // Load Portlet from registry
            // 
            System.out.println("*** Getting portlet from registry: " + fragment.getName());
            PortletDefinition portletDefinition = registry.getPortletDefinitionByUniqueName(fragment.getName());
            if (portletDefinition == null)
            {
                throw new JetspeedException("Failed to load: " + fragment.getName() + " from registry");
            }
                        
            PortletWindow portletWindow = windowAccessor.getPortletWindow(fragment);

            HttpServletRequest servletRequest = request.getRequestForWindow(portletWindow);
            HttpServletResponse servletResponse = request.getResponseForWindow(portletWindow);

            container.renderPortlet(portletWindow, servletRequest, servletResponse);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            log.error("Failed to service portlet, portlet exception: " + t);
        }
        
    }
}
