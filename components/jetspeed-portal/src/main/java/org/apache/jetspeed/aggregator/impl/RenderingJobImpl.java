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

import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.UnavailableException;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.aggregator.PortletTrackingManager;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.jetspeed.util.ServletRequestCleanupService;
import org.apache.pluto.container.PortletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RenderingJob is responsible for storing all necessary objets for
 * asynchronous portlet rendering as well as implementing the rendering logic
 * in its Runnable method.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a>Woonsan Ko</a>
 * @version $Id$
 */
public class RenderingJobImpl implements RenderingJob
{
    /** Commons logging */
    protected final static Logger log = LoggerFactory.getLogger(RenderingJobImpl.class);

    /** WorkerMonitor used to flush the queue */
    protected PortletWindow window = null;
    protected HttpServletRequest request = null;
    protected HttpServletResponse response = null;

    protected PortletContainer container = null;
    protected PortletRenderer renderer = null;
    protected RequestContext requestContext = null;
    protected PortletTrackingManager portletTracking = null;

    protected PortletDefinition portletDefinition;
    protected PortalStatistics statistics;

    protected int expirationCache = 0;

    protected Map<String, Object> workerAttributes;

    protected boolean parallel;

    protected long startTimeMillis = 0;
    protected long timeout;

    public RenderingJobImpl(PortletContainer container,
                            PortletRenderer renderer,
                            PortletDefinition portletDefinition,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            RequestContext requestContext,
                            PortletWindow window,
                            PortalStatistics statistics,
                            int expirationCache)
    {
        this.container = container;
        this.renderer = renderer;
        this.portletTracking = renderer.getPortletTrackingManager();
        this.statistics = statistics;
        this.portletDefinition = portletDefinition;
        this.request = request;
        this.response = response;
        this.requestContext = requestContext;
        this.window = window;
        this.expirationCache = expirationCache;
    }

    public PortletRenderer getRenderer()
    {
        return renderer;
    }

    /**
     * Sets portlet timout in milliseconds.
     */
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    /**
     * Gets portlet timout in milliseconds.
     */
    public long getTimeout()
    {
        return this.timeout;
    }

    /**
     * Checks if the portlet rendering is timeout
     */
    public boolean isTimeout()
    {
        if ((this.timeout > 0) && (this.startTimeMillis > 0))
        {
            return (System.currentTimeMillis() - this.startTimeMillis > this.timeout);
        }
        return false;
    }

    /**
     * Job execution entry point method.
     */
    public void run()
    {
        parallel = true;
        boolean clearContext = requestContext.ensureThreadContext();

        try
        {
            if (this.timeout > 0)
            {
                this.startTimeMillis = System.currentTimeMillis();
            }

            ServletRequestCleanupService.executeNestedRenderJob(this);
        }
        finally
        {
            if (clearContext)
            {
                requestContext.clearThreadContext();
            }

            parallel = false;

            synchronized (window.getFragment().getPortletContent())
            {
               if (log.isDebugEnabled())
               {
                   log.debug("Notifying completion of rendering job for portlet window " + this.window.getId());
               }

               window.getFragment().getPortletContent().notifyAll();
            }
        }
    }

    /**
     * The rendering job execution method.
     * This method tries to find the underlying access control context, and execute it as a privileged action if found.
     * This method is invoked back by {@link ServletRequestCleanupService} which is called in the {@link #run()} call.
     */
    public void execute()
    {
        // We should try to retrieve the subject in context when this job is executed in a worker thread.
        // If it is being executed in the normal http request processing thread, not in a separate worker thread,
        // then we don't have to find the subject in context and run it with the context.
        Subject subject = null;

        // The ACCESS_CONTROL_CONTEXT_WORKER_ATTR attribute is available only when this job is executed in a worker thread.
        AccessControlContext context = (AccessControlContext) getWorkerAttribute(ACCESS_CONTROL_CONTEXT_WORKER_ATTR);

        if (context != null)
        {
            subject = JSSubject.getSubject(context);
        }

        // If a subject found from the the ACCESS_CONTROL_CONTEXT_WORKER_ATTR attribute from the worker executing job.
        if (subject != null)
        {
            JSSubject.doAsPrivileged(subject, new PrivilegedAction<Object>()
                {
                    public Object run()
                    {
                        try
                        {
                            executeInternal();
                        }
                        catch (Throwable t)
                        {
                            log.error("Job execution error", t);
                        }
                        return null;
                    }
                }, context);
        }
        // Otherwise, just execute it without doing a privileged action.
        else
        {
            try
            {
                executeInternal();
            }
            catch (Throwable t)
            {
                log.error("Job execution error", t);
            }
        }

    }

    /**
     * The internal rendering job execution method called by {@link #execute()}.
     */
    private void executeInternal()
    {
        long start = System.currentTimeMillis();
        ContentFragment fragment = this.window.getFragment();

        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Rendering OID "+this.window.getId()+" "+ this.request +" "+this.response);
            }
            container.doRender(this.window, this.request, this.response);
        }
        catch (Throwable t)
        {
            if (t instanceof UnavailableException)
            {
                // no need to dump a full stack trace to the log
                log.error("Error rendering portlet OID " + this.window.getId() + ": " + t.toString());
            }
            else
            {
                log.error("Error rendering portlet OID " + this.window.getId(), t);
            }
            fragment.overrideRenderedContent(t.getMessage());
        }
        finally
        {
            try
            {
                if (fragment.getType().equals(Fragment.PORTLET))
                {
                    long end = System.currentTimeMillis();
                    boolean exceededTimeout = portletTracking.exceededTimeout(end - start, window);

                    if (statistics != null)
                    {
                        statistics.logPortletAccess(requestContext, fragment.getName(), PortalStatistics.HTTP_OK, end - start);
                    }
                    if (exceededTimeout)
                    {
                        // took too long to render
                        log.info("Portlet Exceeded timeout: " + this.window.getPortletDefinition().getPortletName() + " for window " + this.window.getId());
                        portletTracking.incrementRenderTimeoutCount(this.window);
                    }
                    else
                    {
                        portletTracking.success(this.window);
                    }
                }
            }
            finally
            {
                synchronized (fragment.getPortletContent())
                {
                    fragment.getPortletContent().complete();
                    if (fragment.getOverriddenContent() == null)
                    {
                        renderer.notifyContentComplete(requestContext, window);
                    }
                }
            }
        }
    }

    /**
     *
     * <p>
     * getWindow
     * </p>
     *
     * @return The window this job is in charge of rendering
     */
    public PortletWindow getWindow()
    {
        return window;
    }

    /**
     *
     * <p>
     * getPortletContent
     * </p>
     *
     * @return The portlet content this job is in charge of rendering
     */
    public PortletContent getPortletContent()
    {
        return window.getFragment().getPortletContent();
    }

    public PortletDefinition getPortletDefinition()
    {
        return this.portletDefinition;
    }

    public HttpServletRequest getRequest()
    {
        return this.request;
    }

    public HttpServletResponse getResponse()
    {
        return this.response;
    }

    public ContentFragment getFragment()
    {
        return this.window.getFragment();
    }

    public RequestContext getRequestContext()
    {
        return this.requestContext;
    }

    public int getExpirationCache()
    {
        return this.expirationCache;
    }

    public void setWorkerAttribute(String name, Object value)
    {
        if (this.workerAttributes == null)
        {
            this.workerAttributes = Collections.synchronizedMap(new HashMap<String, Object>());
        }

        if (value != null)
        {
            this.workerAttributes.put(name, value);
        }
        else
        {
            this.workerAttributes.remove(name);
        }
    }

    public Object getWorkerAttribute(String name)
    {
        Object value = null;

        if (this.workerAttributes != null)
        {
            value = this.workerAttributes.get(name);
        }

        return value;
    }

    public void removeWorkerAttribute(String name)
    {
        if (this.workerAttributes != null)
        {
            this.workerAttributes.remove(name);
        }
    }
}
