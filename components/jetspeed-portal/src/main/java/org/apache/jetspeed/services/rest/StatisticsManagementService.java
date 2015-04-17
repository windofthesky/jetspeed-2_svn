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

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * UserManagerService. This REST service provides statistics for Jetspeed charting portlets
 *
 * @version $Id: $
 */
@Path("/statistics/")
public class StatisticsManagementService {

    private static Logger log = LoggerFactory.getLogger(ProfilerManagementService.class);

    private PortalStatistics statistics;
    private PortletActionSecurityBehavior securityBehavior;

    public StatisticsManagementService(PortalStatistics statistics,
                                       PortletActionSecurityBehavior securityBehavior) {
        this.statistics = statistics;
        this.securityBehavior = securityBehavior;
    }

    /**
     * Retrieve JVM memory info
     *
     * @param servletRequest
     * @param uriInfo
     * @return
     */
    @GET
    @Path("/runtime")
    public Map<String,Map<String,Long>> runtimeInfo(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo) {
        checkPrivilege(servletRequest, JetspeedActions.VIEW);
        Runtime runtime = Runtime.getRuntime();
        Map<String,Long> memory = new HashMap<>();
        memory.put("total", runtime.totalMemory());
        memory.put("free", runtime.freeMemory());
        //memory.put("max", runtime.maxMemory());
        Map<String,Map<String,Long>> result = new HashMap<>();
        result.put("memory", memory);
        return result;
    }

    protected void checkPrivilege(HttpServletRequest servletRequest, String action) {
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        if (securityBehavior != null && !securityBehavior.checkAccess(requestContext, action)) {
            throw new WebApplicationException(new JetspeedException("Insufficient privilege to access this REST service."));
        }
    }
}

