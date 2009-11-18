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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.aggregator.PortletAccessDeniedException;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;

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
    protected final static Logger log = LoggerFactory.getLogger(AsyncPageAggregatorImpl.class);

    public AsyncPageAggregatorImpl(PortletRenderer renderer)
    {
        super(renderer);
    }

    /**
     * Builds the portlet set defined in the context into a portlet tree.
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
            aggregateAndRender(root, context, page, true, null, null, null);
        }
        
        renderContent(context, root);
        
        if (null != window)
        {
            window.removeAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE);
            window.removeAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE);
        }
        
        releaseBuffers(root, context);                
    }

    protected void aggregateAndRender(ContentFragment f, RequestContext context, ContentPage page, boolean isRoot,
                                      List<RenderingJob> sequentialJobs, List<RenderingJob> parallelJobs, List<ContentFragment> layoutFragments)
    {
        // First Pass, kick off async render threads for all portlets on page 
        // Store portlet rendering jobs in the list to wait later.
        // Store layout fragment in the list to render later.
        if (sequentialJobs == null) 
        {
            sequentialJobs = new ArrayList<RenderingJob>();
        }
        if (parallelJobs == null) 
        {
            parallelJobs = new ArrayList<RenderingJob>();
        }        
        if (layoutFragments == null)
        {
            layoutFragments = new ArrayList<ContentFragment>();
        }

        if (f.getFragments() != null)
        {
            for (ContentFragment child : (List<ContentFragment>)f.getFragments())
            {
                if (!"hidden".equals(f.getState()))
                {
                    if (child.getType().equals(ContentFragment.PORTLET))
                    {
                        try
                        {
                            // create and store the portlet rendering job into the jobs lists.
                            RenderingJob job = renderer.createRenderingJob(child, context);

                            // The returned job can be null for some reason, such as invalid portlet entity.
                            if (job != null) 
                            {
                                if (job.getTimeout() > 0)
                                {
                                    parallelJobs.add(job);
                                }
                                else
                                {
                                    sequentialJobs.add(job);
                                }
                            }
                        }
                        catch (PortletAccessDeniedException e)
                        {
                            child.overrideRenderedContent(e.getLocalizedMessage());                        
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
        
        // kick off the parallel rendering jobs
        for (RenderingJob job : parallelJobs)
        {
            renderer.processRenderingJob(job);
        }

        // kick off the sequential rendering jobs
        for (RenderingJob job : sequentialJobs)
        {
            renderer.processRenderingJob(job);
        }

        // synchronize on completion of all jobs
        renderer.waitForRenderingJobs(parallelJobs);
        
        // render layout fragments.
        for (ContentFragment child : layoutFragments)
        {
            renderer.renderNow(child, context);
        }
        
        // Start the actual rendering process
        if (log.isDebugEnabled())
        {
            log.debug("Rendering portlet fragment: [[name, " + f.getName() + "], [id, " + f.getId() + "]]");
        }
        
        renderer.renderNow(f, context);
    }
    
}
