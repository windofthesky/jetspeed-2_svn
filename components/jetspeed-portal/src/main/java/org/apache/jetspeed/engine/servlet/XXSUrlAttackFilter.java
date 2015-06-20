/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.apache.jetspeed.engine.servlet;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simple XXS Url attack protection blocking access whenever the request url contains a &lt; or &gt; character.
 * @version $Id$
 * 
 */
public class XXSUrlAttackFilter implements Filter
{
    private final static Logger log = LoggerFactory.getLogger(XXSUrlAttackFilter.class);

    private PortalConfiguration portalConfiguration = null;
    private boolean xssRequestEnabled = true;
    private boolean xssPostEnabled = false;

    public void init(FilterConfig config) throws ServletException
    {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        if (portalConfiguration == null) {
            portalConfiguration = Jetspeed.getConfiguration();
            xssRequestEnabled = portalConfiguration.getBoolean(PortalConfigurationConstants.XSS_FILTER_REQUEST, true);
            xssPostEnabled = portalConfiguration.getBoolean(PortalConfigurationConstants.XSS_FILTER_POST, false);
            if (xssPostEnabled) {
                XSSRequestWrapper.initPatterns(portalConfiguration.getStringArray(PortalConfigurationConstants.XSS_REGEX),
                        portalConfiguration.getStringArray(PortalConfigurationConstants.XSS_FLAGS));
            }
        }
        if (request instanceof HttpServletRequest)
        {
            if (xssRequestEnabled) {
                HttpServletRequest hreq = (HttpServletRequest) request;
                if (isInvalid(hreq.getQueryString())) {
                    log.error("XSS attack query string found: " + hreq.getQueryString());
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
                if (isInvalid(hreq.getRequestURI())) {
                    log.error("XSS attack URI found: " + hreq.getRequestURI());
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }
        if (xssPostEnabled) {
            chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
        }
        else {
            chain.doFilter(request, response);
        }
    }

    private boolean isInvalid(String value)
    {
        return (value != null && (value.indexOf('<') != -1 || value.indexOf('>') != -1 || value.indexOf("%3C") != -1
                || value.indexOf("%3c") != -1 || value.indexOf("%3E") != -1 || value.indexOf("%3e") != -1));
    }

    public void destroy()
    {
    }
}
