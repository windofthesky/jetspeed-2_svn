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

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.pluto.core.CoreUtils;
import org.apache.pluto.core.InternalPortletRequest;
import org.apache.pluto.core.InternalPortletResponse;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.factory.FactoryManager;

/**
 * Provides access to servlet request and response wrappers
 * 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ServletObjectAccess.java 185962 2004-03-08 01:03:33Z jford $
 */
public abstract class ServletObjectAccess
{
    public static HttpServletRequest getServletRequest(HttpServletRequest request, PortletWindow window)
    {
    	System.out.println("n");
        return requestFactory.getServletRequest(request, window);
    }

    public static HttpServletResponse getServletResponse(HttpServletResponse response, PortletWindow window)
    {
        return responseFactory.getServletResponse(response);
    }

    public static HttpServletRequest getServletRequest(PortletRequest request)
    {
        InternalPortletRequest internalPortletRequest = CoreUtils.getInternalRequest(request);

        return  (HttpServletRequest) ((javax.servlet.http.HttpServletRequestWrapper) internalPortletRequest).getRequest();
            
    }

    public static HttpServletResponse getServletResponse(PortletResponse response)
    {
        InternalPortletResponse internalPortletResponse = CoreUtils.getInternalResponse(response);
        return (HttpServletResponse) ((HttpServletResponseWrapper) internalPortletResponse).getResponse();
            
    }

    private static ServletRequestFactory requestFactory =
        (ServletRequestFactory) FactoryManager.getFactory(javax.servlet.http.HttpServletRequest.class);
    private static ServletResponseFactory responseFactory =
        (ServletResponseFactory) FactoryManager.getFactory(javax.servlet.http.HttpServletResponse.class);

}
