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
package org.apache.jetspeed.container.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServletFilter to check if a HttpSession is still valid and if not invalidate it.
 * 
 * This code was in part copied from Pluto PortletRequestImpl.getSession(boolean)
 * 
 * @version $Id$
 *
 */
public class PortalSessionValidationFilter implements Filter
{
	public static final String SESSION_VALIDATED_ATTRIBUTE_NAME = PortalSessionValidationFilter.class.getName()+".validated";
	
    private static Logger log = LoggerFactory.getLogger(PortalSessionValidationFilter.class);

    public static HttpSession getValidSession(HttpServletRequest request)
    {
        HttpSession httpSession = request.getSession(false);
        // only (should) need to do this once per servlet request
    	if (request.getAttribute(SESSION_VALIDATED_ATTRIBUTE_NAME) == null)
    	{
    		request.setAttribute(SESSION_VALIDATED_ATTRIBUTE_NAME, Boolean.TRUE);
            if (httpSession != null)
            {
                // HttpSession is not null does NOT mean that it is valid.
                int maxInactiveInterval = httpSession.getMaxInactiveInterval();
                long lastAccesstime = httpSession.getLastAccessedTime();
                if (maxInactiveInterval >= 0 && lastAccesstime > 0)
                {    // < 0 => Never expires.
                    long maxInactiveTime = httpSession.getMaxInactiveInterval() * 1000L;
                    long currentInactiveTime = System.currentTimeMillis() - lastAccesstime;
                    if (currentInactiveTime > maxInactiveTime)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("The current HttpSession with ID {} is expired and will be invalidated.", httpSession.getId());
                        }
                        httpSession.invalidate();
                        httpSession = null;
                    }                
                }
            }
    	}
    	
        return httpSession;
    }
    
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
	{
		if (request instanceof HttpServletRequest)
		{
			getValidSession((HttpServletRequest)request);
		}
		
        if (filterChain != null)
        {
            filterChain.doFilter(request, response);
        }
	}

	public void destroy()
	{
	}
}
