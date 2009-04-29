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
package org.apache.jetspeed.container.invoker;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * DefaultContainerRequestResponseUnwrapper implements ContainerRequestResponseUnwrapper
 * and finds servlet request or servlet response
 *
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id$
 */
public class DefaultContainerRequestResponseUnwrapper implements ContainerRequestResponseUnwrapper
{
    public ServletRequest unwrapContainerRequest(HttpServletRequest containerRequest)
    {
        ServletRequest request = containerRequest;
        while (request instanceof HttpServletRequestWrapper && !(request instanceof ContainerRequiredRequestResponseWrapper))
        {
            request = ((HttpServletRequestWrapper)request).getRequest();
        }
        return request;
    }
    
    public ServletResponse unwrapContainerResponse(HttpServletResponse containerResponse)
    {
        return containerResponse;
    }
}
