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
package org.apache.jetspeed.ajax;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * Simple ServletFilter for invoking AJAX services.
 * 
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class AJAXFilter implements Filter
{
    private ApplicationContext ctx;
    private AJAXService ajaxService;
    private FilterConfig config;
    
    public void init(FilterConfig config) throws ServletException
    {
        this.config = config;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain arg2) throws IOException, ServletException
    {        
        try
        {
            response.setContentType("text/xml");
            if(ctx == null)
            {
                ctx = (ApplicationContext)config.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
                ajaxService = (AJAXService) ctx.getBean("AJAXService");
            }
            
            AJAXRequest ajaxRequest = new AJAXRequestImpl((HttpServletRequest) request, (HttpServletResponse) response, config.getServletContext());
            AJAXResponse ajaxReponse = ajaxService.processRequest(ajaxRequest);
            ajaxReponse.complete();
        }
        catch (AJAXException e)
        {
           ((HttpServletResponse) response).sendError(500, e.getMessage());
        }
        catch(Exception e)
        {
            throw new ServletException(e.getMessage(), e);
        }
    }

    public void destroy()
    {
        // do nothing

    }

}
