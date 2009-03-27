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
import javax.servlet.http.HttpServletResponse;

/**
 * ContainerRequestResponseUnwrapper finds the web container servlet request
 * or servlet response suitable for cross-context dispatching
 * from the current container request or response by unwrapping.
 * Third-party module can provide an implementation to decorate the real request
 * or response object of a servlet container for instance to ensure reliable
 * behavior when executing multiple portlet invocations in parallel on top of
 * the same web container request and response.
 *
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id$
 */
public interface ContainerRequestResponseUnwrapper
{
    /**
     * Unwraps the container request to find the web container servlet request
     * suitable for cross-context request dispatching.
     * 
     * @param containerRequest The container request to be unwrapped.
     * @return servletRequest The servlet request found by unwrapping.
     */
    ServletRequest unwrapContainerRequest(HttpServletRequest containerRequest);

    /**
     * Unwraps the container response to find the web container servlet response
     * suitable for cross-context request dispatching.
     * 
     * @param containerResponse The container response to be unwrapped.
     * @return servletResponse The servlet response found by unwrapping.
     */
    ServletResponse unwrapContainerResponse(HttpServletResponse containerResponse);
}
