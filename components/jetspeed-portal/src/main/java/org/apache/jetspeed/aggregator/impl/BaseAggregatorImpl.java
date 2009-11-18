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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;

/**
 * Share common code for all aggregators 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a>Woonsan Ko</a>
 * @version $Id: $
 */
public abstract class BaseAggregatorImpl 
{
    private final static Logger log = LoggerFactory.getLogger(BaseAggregatorImpl.class);
    
    protected PortletRenderer renderer;
    
    public BaseAggregatorImpl(PortletRenderer renderer)
    {
        this.renderer = renderer;
    }

    protected void releaseBuffers(ContentFragment f, RequestContext context)
    {
        
        if (f.getFragments() != null)
        {
            for (ContentFragment child : (List<ContentFragment>)f.getFragments())
            {
                if (!"hidden".equals(child.getState()))
                {
                    releaseBuffers(child, context);
                }
            }
        }
        PortletContent content = f.getPortletContent();
        if (content != null &&  content.getExpiration() == 0)
        {
            content.release();
        }
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
     */
    protected void renderMaximizedWindow( RequestContext context, ContentPage page, ContentFragment layoutContentFragment, 
                                          PortletWindow window )
    {
        boolean maxedLayout = false;
        PortletWindow layoutWindow;
        if (window.getFragment().getId().equals(layoutContentFragment.getId()))
        {
            layoutWindow = window;
            maxedLayout = true;
        }
        else
        {
            layoutWindow = context.getPortletWindow(layoutContentFragment);
        }
        
        layoutWindow.setAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE, window.getFragment());
        layoutWindow.setAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE, page.getRootFragment());

        try
        {
            renderer.renderNow(window.getFragment(), context);
            if (!maxedLayout)
            {
                renderer.renderNow(layoutContentFragment, context);
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            window.getFragment().overrideRenderedContent("Sorry, but we were unable access the requested portlet. Send the following message to your portal admin:  "+  e.getMessage());
        }
    }

    public void renderContent(RequestContext context, ContentFragment root) throws JetspeedException, IOException
    {
        context.getResponse().getWriter().write(root.getRenderedContent());        
    }
    
}
