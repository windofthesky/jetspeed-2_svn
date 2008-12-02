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
 * @version $Id$
 *
 */
public class MockRequestContextComponent implements RequestContextComponent
{

    public RequestContext create(HttpServletRequest req, HttpServletResponse resp, ServletConfig config)
    {
        return null;
    }

    public RequestContext getRequestContext(HttpServletRequest request)
    {
        return null;
    }

    public RequestContext getRequestContext()
    {
        return null;
    }

    public void release(RequestContext context)
    {
    }

    public ServletRequestFactory getServletRequestFactory()
    {
        return null;
    }

    public ServletResponseFactory getServletResponseFactory()
    {
        return null;
    }

    public UserInfoManager getUserInfoManager()
    {
        return null;
    }
    
}
