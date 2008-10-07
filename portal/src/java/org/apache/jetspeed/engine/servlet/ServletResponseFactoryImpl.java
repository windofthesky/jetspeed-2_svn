/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import javax.servlet.ServletConfig;

import javax.servlet.http.HttpServletResponse;

/**
 * Factory implementation for creating HTTP Response Wrappers
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletResponseFactoryImpl
    implements ServletResponseFactory
{
    private ServletConfig servletConfig;
    
    public void init(ServletConfig config, Map properties) 
    throws Exception
    {
        servletConfig = config;
    }
    
    public void destroy()
    throws Exception
    {

    }

    public javax.servlet.http.HttpServletResponse getServletResponse(HttpServletResponse response)
    {
        HttpServletResponse servletResponse = new ServletResponseImpl(response);
        return servletResponse;
    }
    
}
