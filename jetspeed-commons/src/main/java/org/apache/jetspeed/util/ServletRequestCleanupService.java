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
package org.apache.jetspeed.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.aggregator.RenderingJob;

/**
 * @version $Id$
 *
 */
public class ServletRequestCleanupService
{
    private static ThreadLocal<List<ServletRequestCleanupCallback>> callbacks = new ThreadLocal<List<ServletRequestCleanupCallback>>();

    private static List<ServletRequestCleanupCallback> getCallbacks(boolean create)
    {
        List<ServletRequestCleanupCallback> list = callbacks.get();
        if (list == null && create)
        {
            list = new ArrayList<ServletRequestCleanupCallback>();
            callbacks.set(list);
        }
        return list;
    }

    public static void addCleanupCallback(ServletRequestCleanupCallback callback)
    {
        List<ServletRequestCleanupCallback> callbacks = getCallbacks(false);
        if (callbacks == null)
        {
            callbacks = getCallbacks(true);
            try
            {
                throw new RuntimeException();
            }
            catch (RuntimeException jre)
            {
                // log error being called outside filter chain and the stacktrace for this addCleanupCallback call
                JetspeedLoggerUtil.getSharedLogger(ServletRequestCleanupService.class)
                    .error("Registring cleanup callback before ServletRequestCleanupService invoked from filter chain.", jre);
            }
        }
        callbacks.add(callback);
    }

    public static void executeNestedRenderJob(RenderingJob job)
    {
        if (getCallbacks(false) == null)
        {
            List<ServletRequestCleanupCallback> callbacks = getCallbacks(true);
            Throwable jobException = null;

            try
            {
                job.execute();
            }
            catch (Throwable t)
            {
                jobException = t;
                t.fillInStackTrace();
            }

            for (ServletRequestCleanupCallback callback : callbacks)
            {
                try
                {
                    callback.cleanup(job.getWindow().getPortletRequestContext().getServletContext(), job.getRequest(), job.getResponse());
                }
                catch (Throwable tc)
                {
                    try
                    {
                        JetspeedLoggerUtil.getSharedLogger(ServletRequestCleanupService.class).error("Cleanup callback execution failed", tc);
                    }
                    catch (Throwable tl)
                    {
                        // ignore
                    }
                }
            }

            ServletRequestCleanupService.callbacks.remove();

            if (jobException != null)
            {
                if (jobException instanceof RuntimeException)
                {
                    throw (RuntimeException) jobException;
                }

                throw new RuntimeException(jobException);
            }
        }
        else
        {
            job.execute();
        }
    }

    /**
     * Servlet Filter doFilter delegate method which will execute registered ServletRequestCleanupCallbacks
     * after the filterChain, if any.
     * <p>
     * Note: the delegating Servlet Filter(s) MUST <b>only</b> be configured for handling REQUEST dispatching (which is the default),
     * so only a single doFilter call will be executed for a single request.
     * @param context
     * @param request
     * @param response
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    static void doFilter(ServletContext context, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException
    {
        List<ServletRequestCleanupCallback> callbacks = getCallbacks(true);
        Throwable filterException = null;
        try
        {
            if (filterChain != null)
            {
                filterChain.doFilter(request, response);
            }
        }
        catch (Throwable tf)
        {
            filterException = tf;
            tf.fillInStackTrace();
        }
        for (ServletRequestCleanupCallback callback : callbacks)
        {
            try
            {
                callback.cleanup(context, request, response);
            }
            catch (Throwable tc)
            {
                try
                {
                    JetspeedLoggerUtil.getSharedLogger(ServletRequestCleanupService.class)
                        .error("Cleanup callback execution failed", tc);
                }
                catch (Throwable tl)
                {
                    // ignore
                }
            }
        }
        ServletRequestCleanupService.callbacks.remove();
        if (filterException != null)
        {
            if (filterException instanceof ServletException)
            {
                throw (ServletException)filterException;
            }
            if (filterException instanceof IOException)
            {
                throw (IOException)filterException;
            }
            if (filterException instanceof RuntimeException)
            {
                throw (RuntimeException)filterException;
            }
            throw new RuntimeException(filterException);
        }
    }
}
