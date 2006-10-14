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

import java.io.IOException;
import java.util.Iterator;

import javax.portlet.Portlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;

/**
 * HeaderAggregator builds the content required to render a page of portlets.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: PageAggregatorImpl.java 359125 2005-12-26 23:16:39Z rwatler $
 */
public class HeaderAggregatorImpl implements PageAggregator
{
    private final static Log log = LogFactory.getLog(HeaderAggregatorImpl.class);

    private PortletFactory factory;
    private PortletWindowAccessor windowAccessor;
    private HeaderResourceFactory headerResourceFactory;
    
    public HeaderAggregatorImpl(PortletFactory factory, PortletWindowAccessor windowAccessor, HeaderResourceFactory headerResourceFactory)
    {
        this.factory = factory;
        this.windowAccessor = windowAccessor;
        this.headerResourceFactory = headerResourceFactory;
    }

    /**
     * Builds the portlet set defined in the context into a portlet tree.
     * 
     * @return Unique Portlet Entity ID
     */
    public void build( RequestContext context ) throws JetspeedException, IOException
    {
        ContentPage page = context.getPage();
        if (null == page)
        {
            throw new JetspeedException("Failed to find PSML Pin ContentPageAggregator.build");
        }

        ContentFragment root = page.getRootContentFragment();

        if (root == null)
        {
            throw new JetspeedException("No root ContentFragment found in ContentPage");
        }

        // handle maximized state
        NavigationalState nav = context.getPortalURL().getNavigationalState();
        PortletWindow window = nav.getMaximizedWindow();
        if (null != window)
        {
            ContentFragment maxedContentFragment = page.getContentFragmentById(window.getId().toString());
            if (maxedContentFragment != null)
            {
                renderHeaderFragment(context, maxedContentFragment);
            }
        }
        else
        {
            aggregateAndRender(root, context, page);
        }
        
    }

    protected void aggregateAndRender( ContentFragment fragment, RequestContext context, ContentPage page )
            throws FailedToRenderFragmentException
    {

        if (fragment.getContentFragments() != null && fragment.getContentFragments().size() > 0)
        {
            Iterator children = fragment.getContentFragments().iterator();
            while (children.hasNext())
            {
                ContentFragment child = (ContentFragment) children.next();
                if (!"hidden".equals(fragment.getState()))
                {
                    aggregateAndRender(child, context, page);
                }
            }
        }
        renderHeaderFragment(context, fragment);
    }
    
    protected void renderHeaderFragment(RequestContext context, ContentFragment fragment)
    {
        try
        {
            if (fragment.getType().equals(ContentFragment.LAYOUT))
                return;
            
            PortletWindow portletWindow = windowAccessor.getPortletWindow(fragment);
            PortletDefinition pd = portletWindow.getPortletEntity().getPortletDefinition();
            //portletWindow.getPortletEntity().getPreferenceSet().get
            String portletApplicationContextPath = pd.getPortletApplicationDefinition().getWebApplicationDefinition().getContextRoot();
            Portlet portlet = factory.getPortletInstance(context.getConfig().getServletContext().getContext(portletApplicationContextPath), pd).getRealPortlet();            
            if (portlet instanceof SupportsHeaderPhase)
            {
                System.out.println("HEADER AGGREGATOR: supports header phase: " + pd.getName());
                
                HeaderResource hr = headerResourceFactory.getHeaderResouce(context);
                PortletHeaderRequest headerRequest = new PortletHeaderRequestImpl(context, portletWindow, portletApplicationContextPath);                
                PortletHeaderResponse headerResponse = new PortletHeaderResponseImpl(context, hr);
                ((SupportsHeaderPhase)portlet).doHeader(headerRequest, headerResponse);                
            }
        }
        catch (Exception e)
        {
            log.equals(e);
        }
        
    }
    
    
}
