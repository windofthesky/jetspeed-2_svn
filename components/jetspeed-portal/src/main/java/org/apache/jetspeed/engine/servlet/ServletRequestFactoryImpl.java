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
package org.apache.jetspeed.engine.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.container.PortletWindow;

/**
 * Factory implementation for creating HTTP Request Wrappers
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletRequestFactoryImpl
    implements ServletRequestFactory
{    
    
    public void init(javax.servlet.ServletConfig config, Map properties) 
    throws Exception
    {        
    }
    
    public void destroy()
    throws Exception
    {
    }

    protected HttpServletRequest createRequest(HttpServletRequest request, PortletWindow window)
    {
        return new ServletRequestImpl(request, window);        
    }
    
    public HttpServletRequest getServletRequest(HttpServletRequest request, PortletWindow window)
    {
        // May have already been wrapped, no need to re-wrap.
        if (!(request instanceof ServletRequestImpl))
        {
            HttpServletRequest servletRequest = createRequest(request, window);

            return servletRequest;
        }
        else
        {
            return request;
        }        
    }
    
}
