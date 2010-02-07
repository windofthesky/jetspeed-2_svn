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
package org.apache.jetspeed.openid.filter;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.login.filter.PortalRequestWrapper;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;

/**
 * Propagates OpenID portal subject from session to request.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class OpenIDPortalFilter implements Filter
{
    protected String guest = "guest";
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        PortalConfiguration config = Jetspeed.getConfiguration();
        if (config != null)
        {
            guest = config.getString("default.user.principal");
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        // portal request filter
        if (servletRequest instanceof HttpServletRequest)
        {
            // retrieve current subject from session and wrap portal
            // request to support principal access
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            Subject subject = (Subject)request.getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
            if (subject != null)
            {
                Principal principal = SubjectHelper.getPrincipal(subject, User.class);
                if ((principal == null) || !principal.getName().equals(guest))
                {                        
                    servletRequest = new PortalRequestWrapper(request, subject, principal);
                }
            }              
            
            // tag request as filtered
            servletRequest.setAttribute(PortalReservedParameters.PORTAL_FILTER_ATTRIBUTE, "true");
        }
        
        // continue request filter processing
        if (filterChain != null)
        {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
