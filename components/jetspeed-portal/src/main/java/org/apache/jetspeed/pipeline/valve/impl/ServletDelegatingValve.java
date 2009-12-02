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
package org.apache.jetspeed.pipeline.valve.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * ServletDelegatingValve
 * 
 * @version $Id$
 */
public class ServletDelegatingValve extends AbstractValve
{
    protected HttpServlet servlet;
    protected ServletConfig config;
    protected boolean servletInitialized;

    public ServletDelegatingValve(HttpServlet servlet, ServletConfig config)
    {
        this.servlet = servlet;
        this.config = config;
    }
    
    @Override
    public void initialize() throws PipelineException
    {
    }
    
    public void destroy()
    {
        if (servlet != null && servletInitialized)
        {
            try
            {
                servlet.destroy();
            }
            finally
            {
                servletInitialized = false;
            }
        }
    }

    @Override
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try
        {
            if (!servletInitialized)
            {
                initServlet();
            }
            
            servlet.service(request.getRequest(), request.getResponse());
        }
        catch (Exception e)
        {
            throw new PipelineException(e);
        }
        
        // continue
        context.invokeNext(request);
    }
    
    private synchronized void initServlet() throws PipelineException
    {
        if (!servletInitialized)
        {
            try
            {
                servlet.init(config);
                servletInitialized = true;
            }
            catch (Exception e)
            {
                throw new PipelineException(e);
            }
        }
    }

    public static class ServletConfigImpl implements ServletConfig
    {
        protected String servletName;
        protected Map<String, String> initParams;
        protected ServletContext servletContext;

        public ServletConfigImpl(String servletName, Map<String, String> initParams)
        {
            this(servletName, initParams, null);
        }

        public ServletConfigImpl(String servletName, Map<String, String> initParams, ServletContext servletContext)
        {
            this.servletName = servletName;
            this.initParams = initParams;
            this.servletContext = servletContext;
        }

        public String getInitParameter(String paramName)
        {
            return initParams.get(paramName);
        }

        public Enumeration getInitParameterNames()
        {
            return Collections.enumeration(initParams.keySet());
        }

        public ServletContext getServletContext()
        {
            return servletContext;
        }

        public void setServletContext(ServletContext servletContext)
        {
            this.servletContext = servletContext;
        }

        public String getServletName()
        {
            return servletName;
        }
    }
}
