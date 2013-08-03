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
package org.apache.jetspeed.aggregator;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * The Portlet Renderer Service is the interface to processing portlet rendering requests and interfaces with the portlet
 * container to generate the resulting markup. The renderer is called by the aggregator {@link PortletAggregator} to
 * render the content of one portlet. The render works directly with {@link ContentFragment}s, which are wrappers
 * around portlet instances.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a>Woonsan Ko</a>
 * @version $Id$
 */
public interface PortletRenderer 
{
    /**
     * Renders the specified fragment immediately.
     *
     * @param fragment represents the content and runtime preferences about the portlet instance to be rendered
     * @param request the request context holding runtime request parameters to be normalized
     */
    public void renderNow(ContentFragment fragment, RequestContext request);

    /**
     * Render the specified fragment immediately. If spawned is set to true, the rendering can occur in a separate
     * thread from the current (rendering) thread.
     *
     * @param fragment represents the content and runtime preferences about the portlet instance to be rendered
     * @param request the request context holding runtime request parameters to be normalized
     * @param spawned <tt>true</tt> if this rendering to occur in a separate thread
    */
    public void renderNow(ContentFragment fragment, RequestContext request, boolean spawned);

    /**
     * Render the specified fragment immediately. If spawned is set to true, the rendering can occur in a separate
     * thread from the current (rendering) thread.
     *
     * @param fragment represents the content and runtime preferences about the portlet instance to be rendered
     * @param request the servlet request
     * @param response the servlet response
     * @deprecated
     */
    public void renderNow(ContentFragment fragment, HttpServletRequest request, HttpServletResponse response);

    /** 
     * Create a rendering job for the specified content fragment. All rendering is done under the context of a
     * rendering job, whether is scheduled in parallel, or sequential. The returned job is passed
     * to the {@link #processRenderingJob(RenderingJob job)} method.
     *
     * @param fragment represents the content and runtime preferences about the portlet instance to be rendered
     * @param request the request context holding runtime request parameters to be normalized
     * @return a portlet rendering job to pass to {@link #processRenderingJob(RenderingJob job)} method
     * @throws PortletAccessDeniedException
     */
    public RenderingJob createRenderingJob(ContentFragment fragment, RequestContext request)
    throws PortletAccessDeniedException;

    /** 
     * Processes the actual rendering of the specified rendering job, dispatching to the portlet container to process
     * the rendering of a portlet instance. The implementation can return before rendering is complete when the job
     * is processed in parallel mode. When the job is not parallel mode, it returns after rendering is complete.
     *
     * @param job the rendering job to process
     */
    public void processRenderingJob(RenderingJob job);
        
    /**
     * Waits for all rendering jobs in the list of jobs to finish successfully or otherwise.
     *
     * @param renderingJobs the list of rendering job objects to wait for.
     */
    public void waitForRenderingJobs(List<RenderingJob> renderingJobs);
    
    /**
     * Notification callback from job to this renderer informing that the content rendering has completed.
     *
     * @param context the request context holding runtime request parameters to be normalized
     * @param window the portlet window associated with the job
     */
    public void notifyContentComplete(RequestContext context, PortletWindow window);

    /**
     * Returns an instance of the PortletTrackingManager which is monitoring this rendering request.
     *
     * @return the PortletTrackingManager
     */
    PortletTrackingManager getPortletTrackingManager();
    
}
