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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Asynchronous Page Aggregator builds the content required to render a 
 * page of portlets by rendering the portlets in parallel. Each portlet is
 * rendered on its own thread. A work manager handles the thread pooling
 * and synchronization of worker threads.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class AsyncPageAggregatorImpl implements PageAggregator
{
    protected final static Log log = LogFactory.getLog(AsyncPageAggregatorImpl.class);

    protected PortletRenderer renderer;

    protected List fallBackContentPathes;

    public AsyncPageAggregatorImpl(PortletRenderer renderer)
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
            aggregateAndRender(root, context, page, true, null, null);
        }        
        //dispatcher.include(root);
        context.getResponse().getWriter().write(root.getRenderedContent());
        if (null != window)
        {
            context.getRequest().removeAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE);
            context.getRequest().removeAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE);
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
    }

    protected void aggregateAndRender(ContentFragment f, RequestContext context, ContentPage page, boolean isRoot,
                                      List portletJobs, List layoutFragments)
            throws FailedToRenderFragmentException
    {
        // First Pass, kick off async render threads for all portlets on page 
        // Store portlet rendering jobs in the list to wait later.
        // Store layout fragment in the list to render later.
        if (portletJobs == null) 
        {
            portletJobs = new ArrayList(16);
        }
        if (layoutFragments == null)
        {
            layoutFragments = new ArrayList(4);
        }

        if (f.getContentFragments() != null && f.getContentFragments().size() > 0)
        {
            Iterator children = f.getContentFragments().iterator();
            while (children.hasNext())
            {
                ContentFragment child = (ContentFragment) children.next();
                if (!"hidden".equals(f.getState()))
                {
                    if (child.getType().equals(ContentFragment.PORTLET))
                    {
                        // kick off render thread
                        // and store the portlet rendering job into the portlet jobs list.
                        RenderingJob job = renderer.render(child, context);

                        // The returned job can be null for some reason, such as invalid portlet entity.
                        if ((job != null) && (job.getTimeout() > 0)) 
                        {
                            portletJobs.add(job);
                        }
                    }
                    else
                    {
                        // walk thru layout 
                        // and store the layout rendering job into the layout jobs list.
                        aggregateAndRender(child, context, page, false, portletJobs, layoutFragments);
                        layoutFragments.add(child);
                    }
                }
            }
        }

        // If the fragment is not root, skip the following.
        if (!isRoot)
            return;


        // synchronize on completion of all jobs
        Iterator it = portletJobs.iterator();
        
        try 
        {
            while (it.hasNext()) 
            {
                RenderingJob job = (RenderingJob) it.next();
                PortletContent portletContent = job.getPortletContent();
                
                if (!portletContent.isComplete()) 
                {
                    synchronized (portletContent) 
                    {
                        portletContent.wait();
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception during synchronizing all portlet rendering jobs.", e);
        }
        
        // render layout fragments.
        it = layoutFragments.iterator();
        while (it.hasNext()) 
        {
            ContentFragment child = (ContentFragment) it.next();
            renderer.renderNow(child, context);
        }
        
        // Start the actual rendering process
        String defaultPortletDecorator = page.getEffectiveDefaultDecorator(ContentFragment.PORTLET);
        if (log.isDebugEnabled())
        {
            log.debug("Rendering portlet fragment: [[name, " + f.getName() + "], [id, " + f.getId() + "]]");
        }        
        renderer.renderNow(f, context);
    }
    

}
