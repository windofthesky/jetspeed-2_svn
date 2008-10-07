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

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * NtlmHttpServletRequestWrapper should be used in combination with an Ntml authentication filter (jCIFS).
 * This filter wraps the original request, setting the principal and remoteUser retrieved by Ntml 
 * authentication with the client. The wrapper Request sets the principal and remoteUser, <i>regardless</i> 
 * of the principal already present in the original request. This HttpServletRequestWrapper returns the principal 
 * from the original request when it's there, and otherwise returns the Ntml principal. When the
 * the Ntml principal is actually returned can be influenced by a comma-separated list of servlet urls: 
 *  only for these urls the Ntlm principal / remoteUser is ignored. 
 * @see NtlmHttpServletRequestFilter
 * @author <a href="mailto:d.dam@hippo.nl">Dennis Dam</a>
 * @version $Id$
 */
public class NtlmHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private Principal principal;
    private String remoteUser;
    
    public NtlmHttpServletRequestWrapper(HttpServletRequest req, String ignoreNtmlUrls) {
        super(req);    
        if (req instanceof HttpServletRequestWrapper){
            String[] urls = ignoreNtmlUrls != null ? StringUtils.split(ignoreNtmlUrls, ',') : new String[]{};
            String servletUrl = req.getServletPath();
            Principal reqPrincipal = req.getUserPrincipal();
            HttpServletRequest originalRequest = (HttpServletRequest)((HttpServletRequestWrapper) req).getRequest();
            /*
             *  Original request principal has precedence over Ntml authenticated principal. This is needed
             *  in the case that the Ntlm authenticated principal is not authorized by Jetspeed: a fallback login 
             *  method can then be used. If Ntml authentication succeeds, then the principal from the
             *  original request will be null.
             */ 
            if (originalRequest.getUserPrincipal() != null){
                principal = originalRequest.getUserPrincipal();
            } else 
            /*
             *   If no principal in the original request, take principal from Ntlm authentication, but
             *   only if the current servlet url is not in the ignore list. The last
             *   requirement is necessary when falling back to another authentication method, e.g. container-based
             *   form authentication: these authentication methods might only work if there is no 
             *   principal in the request.    
             */
            if (!ArrayUtils.contains(urls,servletUrl) && reqPrincipal != null && req.getRemoteUser() != null){
                principal = reqPrincipal;
                remoteUser = req.getRemoteUser();
            }             
        } else {            
            principal = super.getUserPrincipal();
        }
    }
    
    public Principal getUserPrincipal() {        
        return principal;
    }
    
    public String getRemoteUser() {   
        return remoteUser;
    }
    
}
