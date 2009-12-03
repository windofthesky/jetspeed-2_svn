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
import java.util.List;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;

/**
 * ContentPageAggregator builds the content required to render a page of portlets.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class PageAggregatorImpl extends BaseAggregatorImpl implements PageAggregator 
{
    public PageAggregatorImpl( PortletRenderer renderer)
    {
        super(renderer);
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
        
        ContentFragment root = page.getRootFragment();
        
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
        
        // write all rendered content
        renderContent(context, root);
        
        if (null != window)
        {
            window.removeAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE);
            window.removeAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE);
        }
        
        releaseBuffers(root, context);        
    }
    
    @SuppressWarnings("unchecked")
    protected void aggregateAndRender( ContentFragment f, RequestContext context, ContentPage page )
    {
        List<ContentFragment> contentFragments = f.getFragments();
        
        if (contentFragments != null && !contentFragments.isEmpty())
        {
            for (ContentFragment child : contentFragments)
            {
                if (!"hidden".equals(f.getState()))
                {
                    aggregateAndRender(child, context, page);
                }
            }
        }        
        renderer.renderNow(f, context);
    }
    
}
