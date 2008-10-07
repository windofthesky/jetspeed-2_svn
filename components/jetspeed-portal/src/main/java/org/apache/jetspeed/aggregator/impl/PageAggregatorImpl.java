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
package org.apache.jetspeed.aggregator.impl;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.impl.BaseAggregatorImpl;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * ContentPageAggregator builds the content required to render a page of portlets.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class PageAggregatorImpl extends BaseAggregatorImpl implements PageAggregator 
{
    private final static Log log = LogFactory.getLog(PageAggregatorImpl.class);
    private PortletRenderer renderer;

    public PageAggregatorImpl( PortletRenderer renderer)
    {
        this.renderer = renderer;
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
            renderMaximizedWindow(context, page, root, window);
        }
        else
        {
            aggregateAndRender(root, context, page);
        }        
        context.getResponse().getWriter().write(root.getRenderedContent());
        if (null != window)
        {
            context.getRequest().removeAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE);
            context.getRequest().removeAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE);
        }
        releaseBuffers(root, context);        
    }

    /**
     * <p>
     * renderMaximizedWindow
     * </p>
     * 
     * @param context
     * @param page
     * @param layoutContentFragment
     * @param defaultPortletDecorator
     * @param dispatcher
     * @param window
     * @throws FailedToRenderContentFragmentException
     */
    protected void renderMaximizedWindow( RequestContext context, ContentPage page, ContentFragment layoutContentFragment,
            PortletWindow window ) throws FailedToRenderFragmentException
    {
        ContentFragment maxedContentFragment = page.getContentFragmentById(window.getId().toString());
        if (maxedContentFragment != null)
        {
            context.getRequest().setAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE, maxedContentFragment);
            context.getRequest().setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, maxedContentFragment);
            context.getRequest().setAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE, page.getRootContentFragment());

            try
            {
                renderer.renderNow(maxedContentFragment, context);
                renderer.renderNow(layoutContentFragment, context);              
                
            }
            catch (Exception e)
            {
                log.error(e.getMessage(), e);
                maxedContentFragment.overrideRenderedContent("Sorry, but we were unable access the requested portlet.  Send the following message to your portal admin:  "+  e.getMessage());
            }
        }
        else
        {
            String message = "Maximized fragment not found."; 
            log.error(message);
            if (maxedContentFragment != null)
                maxedContentFragment.overrideRenderedContent("Sorry, but we were unable access the requested portlet.  Send the following message to your portal admin:  "+  message);            
        }
    }

    protected void aggregateAndRender( ContentFragment f, RequestContext context, ContentPage page )
            throws FailedToRenderFragmentException
    {
        if (f.getContentFragments() != null && f.getContentFragments().size() > 0)
        {
            Iterator children = f.getContentFragments().iterator();
            while (children.hasNext())
            {
                ContentFragment child = (ContentFragment) children.next();
                if (!"hidden".equals(f.getState()))
                {
                    aggregateAndRender(child, context, page);
                }
            }
        }
        renderer.renderNow(f, context);
    }
}
