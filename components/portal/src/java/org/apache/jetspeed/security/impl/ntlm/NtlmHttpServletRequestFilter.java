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
package org.apache.jetspeed.security.impl.ntlm;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * <code>NtlmHttpServletRequestFilter</code> can be used in combination with an Ntml authentication filter (jCIFS).
 * The <code>NtlmHttpServletRequestFilter</code> <b>must</b> be configured after the jCIFS filter in web.xml. The 
 * NtlmHttpServletRequestFilter wraps the jCIFS HttpServletRequest  with a <code>NtlmHttpServletRequestWrapper</code>.
 * This is done to control which principal / remoteUser is returned by the request.
 * If a fallback authentication method is used (e.g. container-based form authentication) then you must 
 * use the filter param <code>org.apache.jetspeed.security.ntlm.ignoreUrls</code> in web.xml to specify the urls for
 * which the Ntlm principal / remoteUser should be ignored. 
 * 
 * @see NtlmHttpServletRequestWrapper
 * @author <a href="mailto:d.dam@hippo.nl">Dennis Dam</a>
 * @version $Id$
 */
public class NtlmHttpServletRequestFilter implements Filter {
    
    private String ignoreNtlmUrls;    
    
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        chain.doFilter( new NtlmHttpServletRequestWrapper( req, ignoreNtlmUrls ), resp );
    }

    public void init(FilterConfig config) throws ServletException {
       ignoreNtlmUrls = config.getInitParameter("org.apache.jetspeed.security.ntlm.ignoreUrls");
    }

}
