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
import org.apache.jetspeed.cache.CacheMonitorState;
import org.apache.jetspeed.cache.JetspeedCacheMonitor;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.services.beans.UpdateResultBean;
import org.apache.jetspeed.statistics.AggregateStatistics;
import org.apache.jetspeed.statistics.InvalidCriteriaException;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.jetspeed.statistics.StatisticsQueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserManagerService. This REST service provides statistics for Jetspeed charting portlets
 *
 * @version $Id: $
 */
@Path("/statistics/")
public class StatisticsManagementService extends AbstractRestService {

    protected static final String COULD_NOT_PROCESS_REQUEST_FOR_STATISTICS = "Could not process request for statistics";
    private static Logger log = LoggerFactory.getLogger(StatisticsManagementService.class);

    private PortalStatistics statistics;
    private JetspeedCacheMonitor cacheMonitor;

    public StatisticsManagementService(PortalStatistics statistics,
                                       PortletActionSecurityBehavior securityBehavior,
                                       JetspeedCacheMonitor cache) {
        super(securityBehavior);
        this.statistics = statistics;
        this.cacheMonitor = cache;
    }

    /**
     * Retrieve JVM runtime memory usage info
     *
     * @param servletRequest
     * @param uriInfo
     * @return
     */
    @GET
    @Path("/memory")
    public Map<String,Map<String,Long>> memoryInfo(@Context HttpServletRequest servletRequest,
                                                   @Context final HttpServletResponse response,
                                                   @Context UriInfo uriInfo) {
        checkPrivilege(servletRequest, JetspeedActions.VIEW);

        Runtime runtime = Runtime.getRuntime();

        Map<String,Long> memory = new HashMap<>();
        memory.put("total", runtime.totalMemory());
        memory.put("free", runtime.freeMemory());
        Map<String,Map<String,Long>> result = new HashMap<>();
        result.put("memory", memory);

        return result;
    }

    /**
     * Retrieve Portal top page usage info
     *
     * @param servletRequest
     * @param uriInfo
     * @return
     */
    @GET
    @Path("/pages")
    public Map<String,Map<String,Long>> pagesInfo(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo) {

        checkPrivilege(servletRequest, JetspeedActions.VIEW);

        Map<String,Long> pages = new HashMap<>();

        StatisticsQueryCriteria criteria = statistics.createStatisticsQueryCriteria();
        criteria.setQueryType("page");
        try {
            AggregateStatistics pageStats = statistics.queryStatistics(criteria);
            List statList = pageStats.getStatlist();
            int size = statList.size();

            for (int i = 0; i < Math.max(5, size); i++){
                HashMap<String,String> stats = (HashMap)statList.get(i);
                String pageName = stats.get("groupColumn");
                String pageCount = stats.get("count");
                if (pageName != null) {
                    pages.put(pageName, Long.valueOf(pageCount));
                }
            }
        }
        catch (InvalidCriteriaException e) {

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(
                    new UpdateResultBean(Response.Status.BAD_REQUEST.getStatusCode(),
                            COULD_NOT_PROCESS_REQUEST_FOR_STATISTICS)).build());
        }

        Map<String,Map<String,Long>> result = new HashMap<>();
        result.put("pages", pages);

        return result;
    }

    /**
     * Retrieve Portal top user session usage info
     *
     * @param servletRequest
     * @param uriInfo
     * @return
     */
    @GET
    @Path("/users")
    public Map<String,Map<String,Long>> usersInfo(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo) {

        checkPrivilege(servletRequest, JetspeedActions.VIEW);

        Map<String,Long> users = new HashMap<>();

        StatisticsQueryCriteria criteria = statistics.createStatisticsQueryCriteria();
        criteria.setQueryType("user");
        try {
            AggregateStatistics pageStats = statistics.queryStatistics(criteria);
            List statList = pageStats.getStatlist();
            int size = statList.size();

            for (int i=0; i<size; i++){
                HashMap<String,String> stats = (HashMap)statList.get(i);
                String pageName = stats.get("groupColumn");
                if (!pageName.equals("guest")) {
                    String pageCount = stats.get("count");
                    users.put(pageName, Long.valueOf(pageCount));
                }
            }
        }
        catch (InvalidCriteriaException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(
                    new UpdateResultBean(Response.Status.BAD_REQUEST.getStatusCode(),
                            COULD_NOT_PROCESS_REQUEST_FOR_STATISTICS)).build());
        }
        Map<String,Map<String,Long>> result = new HashMap<>();
        result.put("users", users);
        return result;
    }

    /**
     * Retrieve Portal top caches usage info
     *
     * @param servletRequest
     * @param uriInfo
     * @return
     */
    @GET
    @Path("/caches")
    public Map<String,Map<String,Map<String,Long>>> cachesInfo(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo) {

        checkPrivilege(servletRequest, JetspeedActions.VIEW);

        final String PREFERENCES = "preferencesCache";
        final String PORTLET_DEFINITION_NAME = "portletDefinitionNameCache";
        final String PORTLET_APPLICATION_NAME = "portletApplicationNameCache";
        final String PAGE_FILE = "pageFileCache";
        final String PAGE_MANAGER_PATH = "pageManagerPathCache";

        Map<String,Map<String,Long>> caches = new HashMap<>();

        /* Loads all of the caches counts except those with all zeros counts
        List<CacheMonitorState> stateList = cacheMonitor.snapshotStatistics();
        int size = stateList.size();
        for (int i=0; i<size; i++) {
            CacheMonitorState state = stateList.get(i);
            Map<String, Long> counts = new HashMap<>();
            Long hits = state.getCacheHits();
            Long misses = state.getCacheMisses();
            Long evictions = state.getEvictionCount();
            if (hits > 0 || misses > 0 || evictions != 0) {
                counts.put("hits", hits);
                counts.put("misses", misses);
                counts.put("evictions", evictions);
                caches.put(state.getCacheName(), counts);
            }
        }*/

        CacheMonitorState cacheState = cacheMonitor.snapshotStatistics(PREFERENCES);
        Map<String,Long> preferences = new HashMap<>();
        preferences.put("hits", cacheState.getCacheHits());
        preferences.put("misses", cacheState.getCacheMisses());
        preferences.put("evictions", cacheState.getEvictionCount());
        caches.put("Preferences", preferences);

        cacheState = cacheMonitor.snapshotStatistics(PORTLET_DEFINITION_NAME);
        Map<String,Long> portlet = new HashMap<>();
        portlet.put("hits", cacheState.getCacheHits());
        portlet.put("misses", cacheState.getCacheMisses());
        portlet.put("evictions", cacheState.getEvictionCount());
        caches.put("Portlets", portlet);

        cacheState = cacheMonitor.snapshotStatistics(PORTLET_APPLICATION_NAME);
        Map<String,Long> portletApp = new HashMap<>();
        portletApp.put("hits", cacheState.getCacheHits());
        portletApp.put("misses", cacheState.getCacheMisses());
        portletApp.put("evictions", cacheState.getEvictionCount());
        caches.put("PortletApps", portletApp);

        CacheMonitorState fileState = cacheMonitor.snapshotStatistics(PAGE_FILE);
        Map<String,Long> pageFile = new HashMap<>();
        if (fileState.getMemoryStoreSize() > 0) {
            pageFile.put("hits", fileState.getCacheHits());
            pageFile.put("misses", fileState.getCacheMisses());
            pageFile.put("evictions", fileState.getEvictionCount());
            caches.put("PSML-Files", pageFile);
        }
        else {
            CacheMonitorState pathState = cacheMonitor.snapshotStatistics(PAGE_MANAGER_PATH);
            pageFile.put("hits", pathState.getCacheHits());
            pageFile.put("misses", pathState.getCacheMisses());
            pageFile.put("evictions", pathState.getEvictionCount());
            caches.put("DBPSML", pageFile);
        }

        Map<String,Map<String,Map<String,Long>>> result;
        result = new HashMap<>();
        result.put("caches", caches);

        return result;
    }

}

