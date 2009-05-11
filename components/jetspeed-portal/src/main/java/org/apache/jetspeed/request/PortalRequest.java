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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.container.invoker.ContainerRequiredRequestResponseWrapper;

/**
 * PortalRequest wraps the original request to the portal and keeps local
 * references to properties like contextPath, servletPath and the Session
 * when its created.
 * <p>
 * Some web servers like WebSphere don't store these properties inside the
 * request but derive them dynamically based on the web application context
 * in which they are invoked.
 * </p>
 * <p>
 * For cross-context invoked portlet applications, getting access to the
 * portal contextPath using requestContext.getRequest().getContextPath()
 * this clearly is a problem. Also, access to the Portal Session is not
 * possible this way.
 * </p>
 * <p>
 * The requestContext.request is actually wrapped by this class which solves
 * the problem by storing a reference to the actual properties at the time
 * of creation and returning  
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PortalRequest extends HttpServletRequestWrapper
{
    private final String      contextPath;
    private final String      servletPath;
    private final HttpSession session;
    
    public PortalRequest(HttpServletRequest request)
    {
        super(request);
        contextPath = request.getContextPath();
        servletPath = request.getServletPath();
        session = request.getSession(true);
    }

    public String getContextPath()
    {
        return this.contextPath;
    }

    public String getServletPath()
    {
        return this.servletPath;
    }

    public HttpSession getSession()
    {
        return this.session;        
    }

    public HttpSession getSession(boolean create)
    {
        return this.session;
    }    
}
