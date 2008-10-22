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
package org.apache.jetspeed.request;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.engine.servlet.ServletRequestFactory;
import org.apache.jetspeed.engine.servlet.ServletResponseFactory;
import org.apache.jetspeed.userinfo.UserInfoManager;


/**
 * RequestContextComponent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface RequestContextComponent 
{
    /**
     * Creates a request context for the given servlet request.
     * 
     * @param req
     * @param resp
     * @param config
     * @return
     */
    RequestContext create(HttpServletRequest req, HttpServletResponse resp, ServletConfig config);

    /**
     * Release a request context back to the context pool.
     * 
     * @param context
     */
    void release(RequestContext context);
    
    /**
     * The servlet request can always get you back to the Request Context if you need it
     * This static accessor provides this capability
     *
     * @param request
     * @return RequestContext
     */
    RequestContext getRequestContext(HttpServletRequest request);    
    RequestContext getRequestContext();    
    
    
    ServletRequestFactory getServletRequestFactory();
    ServletResponseFactory getServletResponseFactory();
    UserInfoManager getUserInfoManager();
    
}
