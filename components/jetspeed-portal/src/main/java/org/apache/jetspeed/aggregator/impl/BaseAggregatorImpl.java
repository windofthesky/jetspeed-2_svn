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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.container.PortletWindow;
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
    private final static Log log = LogFactory.getLog(BaseAggregatorImpl.class);
    
    protected PortletRenderer renderer;
    
    public BaseAggregatorImpl(PortletRenderer renderer)
    {
        this.renderer = renderer;
    }

    protected void releaseBuffers(ContentFragment f, RequestContext context)
    {
        
        if (f.getContentFragments() != null)
        {
            for (ContentFragment child : (List<ContentFragment>)f.getContentFragments())
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
        PortletWindow layoutWindow = context.getPortletWindow(layoutContentFragment);
        
        layoutWindow.setAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE, window.getFragment());
        layoutWindow.setAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE, page.getRootContentFragment());

        try
        {
            renderer.renderNow(window.getFragment(), context);
            renderer.renderNow(layoutContentFragment, context);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            window.getFragment().overrideRenderedContent("Sorry, but we were unable access the requested portlet. Send the following message to your portal admin:  "+  e.getMessage());
        }
    }
    
}
