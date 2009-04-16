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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;

/**
 * <h4>PortletRendererService<br />
 * Jetspeed-2 Rendering service.</h4>
 * <p>This service process all portlet rendering requests and interfaces with the portlet
 * container to generate the resulting markup</p>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a>Woonsan Ko</a>
 * @version $Id$
 */
public interface PortletRenderer 
{
    /**
        Render the specified Page fragment.
        Result is returned in the PortletResponse.
     */
    public void renderNow(ContentFragment fragment, RequestContext request);

    /**
     * Render the specified Page fragment in a separate Thread from the current (rendering) Thread
     * Result is returned in the PortletResponse.
    */
    public void renderNow(ContentFragment fragment, RequestContext request, boolean spawned);

    /**
        Render the specified Page fragment.
        Result is returned in the PortletResponse.
     */
    public void renderNow(ContentFragment fragment, HttpServletRequest request, HttpServletResponse response);

    /** 
     * 
     * Create a rendering job for the specified Page fragment.
     * The method returns a rendering job which should be passed to 'processRenderingJob(RenderingJob job)' method.
     * @return portlet rendering job to pass to render(RenderingJob job) method
     * @throws PortletAccessDeniedException
     */
    public RenderingJob createRenderingJob(ContentFragment fragment, RequestContext request)
    throws PortletAccessDeniedException;

    /** 
     * 
     * Render the specified rendering job.
     * The method returns before rendering is complete when the job is processed in parallel mode.
     * When the job is not parallel mode, it returns after rendering is complete.
     */
    public void processRenderingJob(RenderingJob job);
        
    /**
     * Wait for all rendering jobs in the collection to finish successfully or otherwise. 
     * @param renderingJobs the Collection of rendering job objects to wait for.
     */
    public void waitForRenderingJobs(List<RenderingJob> renderingJobs);
    
    /**
     * Notify that content completed by worker jobs 
     * So that renderer can update its state
     * @param context
     * @param window
     */
    public void notifyContentComplete(RequestContext context, PortletWindow window);

    PortletTrackingManager getPortletTrackingManager();
    
}
