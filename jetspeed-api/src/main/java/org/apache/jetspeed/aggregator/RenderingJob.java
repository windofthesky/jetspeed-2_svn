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
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.AccessControlContext;

/**
 * A rendering worker, running on its own thread if using parallel rendering. This worker thread
 * processes rendering jobs of portlet windows and notifies the WorkerMonitor when the job is completed.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface RenderingJob extends Runnable
{
    /**
     * Worker attribute name of AccessControlContext object in the current request processing context.
     */
    String ACCESS_CONTROL_CONTEXT_WORKER_ATTR = AccessControlContext.class.getName();

    /**
     * Execute the rendering of a portlet window, delegating to the portlet container. Exceptions are never thrown
     * from this method since it can be run in a separate thread. All exceptions are kept internal and returned in the
     * job's state and content. Portlet tracking and timeout processing should be handled by implementations.
     */
    void execute();

    /**
     * Returns the portlet renderer service managing this job
     *
     * @return the renderer service
     * @deprecated
     */
    PortletRenderer getRenderer();

    /**
     * Returns the portlet window associated with this job
     *
     * @return the portlet window
     */
    PortletWindow getWindow();

    /**
     * Returns the portlet content buffer associated with this job
     *
     * @return the portlet content buffer
     */
    PortletContent getPortletContent();

    /**
     * The timeout for rendering this job in milliseconds. Depending on the {@link PortletTrackingManager} policy,
     * this portlet can be taken out of service after timing out repeatedly.
     *
     * @param portletTimeout the rendering timeout in milliseconds
     */
    void setTimeout(long portletTimeout);

    /**
     * The timeout for rendering this job in milliseconds. Depending on the {@link PortletTrackingManager} policy,
     * this portlet can be taken out of service after timing out repeatedly.
     *
     * @return  the rendering timeout in milliseconds
     */
    long getTimeout();

    /**
     * Determines if this job has exceeded the timeout for rendering this job in milliseconds.
     * Depending on the {@link PortletTrackingManager} policy this portlet can be taken out of
     * service after timing out repeatedly.
     *
     * @return <tt>true</tt> if this job has exceeded the timeout in rendering
     */
    boolean isTimeout();

    /**
     * Returns the portlet definition associated with this portlet window and job
     *
     * @return the portlet definition from the portlet registry
     */
    PortletDefinition getPortletDefinition();

    /**
     * The underlying HttpServletRequest used to render this job
     *
     * @return the underlying HttpServletRequest
     */
    HttpServletRequest getRequest();

    /**
     * The underlying HttpServletResponse used to render this job
     *
     * @return the underlying HttpServletResponse
     */
    HttpServletResponse getResponse();

    /**
     * The content fragment to hold the rendered output of this job
     *
     * @return the content fragment
     */
    ContentFragment getFragment();

    /**
     * The request context associated with this job
     *
     * @return the request context
     */
    RequestContext getRequestContext();

    /**
     *  The expiration cache timeout value in seconds for the content of this job
     *
     * @return the expiration cache timeout in seconds
     */
    int getExpirationCache();

    /**
     * Set a generic worker attribute and associate it with this job.
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    void setWorkerAttribute(String name, Object value);

    /**
     * Retrieve a generic worker attribute associated it with this job.
     *
     * @param name the name of the attribute
     * @return the value of the attribute
     */
    Object getWorkerAttribute(String name);

    /**
     * Remove a generic worker attribute associated with this job.
     * @param name the name oof the attribute
     */
    void removeWorkerAttribute(String name);
}

