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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version $Id$
 *
 */
public class ServletRequestCleanupFilter implements Filter
{
    private ServletContext context;
    
    public void init(FilterConfig config) throws ServletException
    {
        this.context = config.getServletContext();
    }
    
    public void destroy()
    {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
    {
        ServletRequestCleanupService.setCleaner(this);
        if (filterChain != null)
        {
            filterChain.doFilter(request, response);
        }
        if (request instanceof HttpServletRequest)
        {
            ServletRequestCleanupService.cleanup(this, context, (HttpServletRequest)request, (HttpServletResponse)response);
        }
    }
}
