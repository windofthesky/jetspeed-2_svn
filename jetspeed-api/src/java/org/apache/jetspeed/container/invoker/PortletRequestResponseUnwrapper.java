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

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * PortletRequestResponseUnwrapper finds servlet request or servlet response 
 * from portlet request or portlet response by unwrapping.
 * Third-party module can provide an implementation to decorate the real request
 * or response object of a servlet container.
 * For example, the real request object of a servlet container can be decorated
 * because it is not thread-safe under Jetspeed parallel rendering mode.
 *
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: $
 */
public interface PortletRequestResponseUnwrapper
{
    /**
     * Unwraps portlet request to find the real servlet request.
     * 
     * @param portletRequest The portlet request to be unwrapped.
     * @return servletRequest The servlet request found by unwrapping.
     */
    ServletRequest unwrapPortletRequest(PortletRequest portletRequest);
    
    /**
     * Unwraps portlet response to find the real servlet response.
     * 
     * @param portletResponse The portlet response to be unwrapped.
     * @return servletResponse The servlet response found by unwrapping.
     */
    ServletResponse unwrapPortletResponse(PortletResponse portletResponse);
}
