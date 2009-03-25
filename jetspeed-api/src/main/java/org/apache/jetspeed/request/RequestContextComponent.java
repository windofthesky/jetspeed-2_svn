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

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
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
     * Creates a request context for the given servlet request and stores it on the current thread
     * 
     * @param req
     * @param resp
     * @param config
     * @return
     */
    RequestContext create(HttpServletRequest req, HttpServletResponse resp, ServletConfig config);

    /**
     * Get the request context on the current thread
     * @return
     */
    RequestContext getRequestContext();
    
    /**
     * Set a new request context on the current (possibly spawned) thread
     * Note: providing a null value effectively clears the request context from the current thread.
     * 
     * @param requestContext
     */
    void setRequestContext(RequestContext requestContext);
    
    UserInfoManager getUserInfoManager();
    PortletRegistry getPortletRegistry();
    
}
