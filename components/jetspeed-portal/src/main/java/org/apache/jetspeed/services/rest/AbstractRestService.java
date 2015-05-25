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
package org.apache.jetspeed.services.rest;

import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.beans.UpdateResultBean;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by dtaylor on 5/2/15.
 */
public class AbstractRestService {

    private PortletActionSecurityBehavior securityBehavior;

    protected AbstractRestService(PortletActionSecurityBehavior securityBehavior) {
        this.securityBehavior = securityBehavior;
    }

    protected void checkPrivilege(HttpServletRequest servletRequest, String action)
    {
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        if (securityBehavior != null && !securityBehavior.checkAccess(requestContext, action))
        {
            throw new WebApplicationException(
                    Response.status(Response.Status.UNAUTHORIZED).entity(
                            new UpdateResultBean(Response.Status.UNAUTHORIZED.getStatusCode(),
                                    "Insufficient privilege to access this REST service")).build());
        }
    }

}
