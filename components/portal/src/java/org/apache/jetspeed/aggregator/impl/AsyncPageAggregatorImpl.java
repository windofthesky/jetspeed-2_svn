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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.CurrentWorkerContext;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.PageAggregator;
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
 * @author <a>Woonsan Ko</a>
 * @version $Id: $
 */
public class AsyncPageAggregatorImpl extends BaseAggregatorImpl implements PageAggregator
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
            aggregateAndRender(root, context, page, true, null, null, null);
        }        
        //dispatcher.include(root);
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
    }

    protected void aggregateAndRender(ContentFragment f, RequestContext context, ContentPage page, boolean isRoot,
                                      List sequentialJobs, List parallelJobs, List layoutFragments)
            throws FailedToRenderFragmentException
    {
        // First Pass, kick off async render threads for all portlets on page 
        // Store portlet rendering jobs in the list to wait later.
        // Store layout fragment in the list to render later.
        if (sequentialJobs == null) 
        {
            sequentialJobs = new ArrayList();
        }
        if (parallelJobs == null) 
        {
            parallelJobs = new ArrayList();
        }        
        if (layoutFragments == null)
        {
            layoutFragments = new ArrayList();
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
                        // create and store the portlet rendering job into the jobs lists.
                        RenderingJob job = renderer.createRenderingJob(child, context);

                        // The returned job can be null for some reason, such as invalid portlet entity.
                        if (job != null) 
                        {
                            if (job.getTimeout() > 0)
                                parallelJobs.add(job);
                            else
                                sequentialJobs.add(job);
                        }
                    }
                    else
                    {
                        // walk thru layout 
                        // and store the layout rendering job into the layout jobs list.
                        aggregateAndRender(child, context, page, false, sequentialJobs, parallelJobs, layoutFragments);
                        layoutFragments.add(child);
                    }
                }
            }
        }

        // If the fragment is not root, skip the following.
        if (!isRoot)
            return;
        
        int parallelJobCount = parallelJobs.size();
        int sequentialJobCount = sequentialJobs.size();
        
        if (log.isInfoEnabled())
        {
            log.info("Aggregating " + page.getPath() + ". Parallel: " + parallelJobCount + ", Sequential: " + sequentialJobCount);
        }
        
        CurrentWorkerContext.setParallelRenderingMode(parallelJobCount > 0);

        // kick off the parallel rendering jobs
        Iterator iter = parallelJobs.iterator();
        while (iter.hasNext())
        {
            RenderingJob job = (RenderingJob) iter.next();
            renderer.processRenderingJob(job);
        }

        // kick off the sequential rendering jobs
        iter = sequentialJobs.iterator();
        while (iter.hasNext())
        {
            RenderingJob job = (RenderingJob) iter.next();
            renderer.processRenderingJob(job);
        }

        // synchronize on completion of all jobs
        renderer.waitForRenderingJobs(parallelJobs);
        
        // Now, restore it to non parallel mode for rendering layout portlets.
        CurrentWorkerContext.setParallelRenderingMode(false);
        
        // render layout fragments.
        iter = layoutFragments.iterator();
        while (iter.hasNext()) 
        {
            ContentFragment child = (ContentFragment) iter.next();
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
