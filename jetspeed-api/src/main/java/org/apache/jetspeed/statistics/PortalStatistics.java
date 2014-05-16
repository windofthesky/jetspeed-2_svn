/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.statistics;

import org.apache.jetspeed.request.RequestContext;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * The PortletStatistics interface provides an API for logging portlet
 * statistics. Each log entry is formatted in the <A
 * HREF="http://httpd.apache.org/docs/logs.html"> Apache Common Log Format (CLF)
 * </A>. Each CLF log entry has the following form:
 * <P>
 * "%h %l %u %t \"%r\" %>s %b"
 * <P>
 * where:
 * <UL>
 * <LI><B>%h </B>- remote host</LI>
 * <LI><B>%l </B>- remote log name</LI>
 * <LI><B>%u </B>- remote user</LI>
 * <LI><B>%t </B>- time in common log time format</LI>
 * <LI><B>%r </B>- first line of HTTP request</LI>
 * <LI><B>%s </B>- HTTP status code</LI>
 * <LI><B>%b </B>- number of bytes sent ("-" if no bytes sent).
 * </UL>
 * <P>
 * Here's an example of a CLF log entry:
 * <P>
 * 
 * <PRE>
 * 
 * 192.168.2.3 - johndoe [25/Oct/2005:11:44:40 PDT] "GET
 * /jetspeed/DatabaseBrowserTest HTTP/1.1" 200 -
 * 
 * </PRE>
 * 
 * <P>
 * The PortletStatistics interface overloads the %r field of the CLF format,
 * depending on the type of information being logged:
 * <P>
 * 
 * <PRE>
 * 
 * LOG TYPE FORMAT OF %r FIELD -------------- ----------------------------
 * Portlet access "PORTLET <page-path><portlet-name>" Page access "PAGE
 * <page-path>" User logout "LOGOUT"
 * 
 * </PRE>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch </a>
 * @author <a href="mailto:rklein@bluesunrise.com">Richard Klein </a>
 * @version $Id: $
 */
public interface PortalStatistics
{
    public static final String QUERY_TYPE_PORTLET = "portlet";
    public static final String QUERY_TYPE_USER = "user";
    public static final String QUERY_TYPE_PAGE = "page";
    
    public static final String HTTP_OK = "200";

    public static final String HTTP_UNAUTHORIZED = "401";

    public static final String HTTP_NOT_FOUND = "404";

    public static final String HTTP_INTERNAL_ERROR = "500";

    // Codes for logging actions and events
    public static final String HTTP_ACTION = "900";
    public static final String HTTP_EVENT = "901";
    
    /**
     * Logs an access to a portlet.
     * 
     * @param request
     *            current request info object
     * @param portlet
     *            portlet being logged
     * @param statusCode
     *            HTTP status code.
     * @param msElapsedTime
     *            elapsed time the portlet took to render
     */
    public void logPortletAccess(RequestContext request, String portlet,
            String statusCode, long msElapsedTime);

    /**
     * Logs an access to a page.
     * 
     * @param request
     *            current request info object
     * @param statusCode
     *            HTTP status code
     * @param msElapsedTime
     *            elapsed time the page took to render
     */
    public void logPageAccess(RequestContext request, String statusCode,
            long msElapsedTime);

    /**
     * Logs a user logout event. The %s (HTTP status code) field of the log
     * entry will be set to 200 (OK).
     * 
     * @param ipAddress the ip address of the request
     * @param userName the name of the user being tracked
     * @param  msSessionLength time that the user was logged in
     */
    public void logUserLogout(String ipAddress, String userName,
            long msSessionLength);

    /**
     * Logs a user logout event. The %s (HTTP status code) field of the log
     * entry will be set to 200 (OK).
     * 
     * @param request
     *            current request info object
     * @param msElapsedLoginTime
     *            time it took the user to login
     */
    public void logUserLogin(RequestContext request, long msElapsedLoginTime);

    /**
     * force the database loggers to flush out
     */
    public void forceFlush();

    /**
     * @return DataSource in use by the logger useful for writing decent tests
     */
    public DataSource getDataSource();

    public AggregateStatistics queryStatistics(StatisticsQueryCriteria criteria)
            throws InvalidCriteriaException;

    /**
     * @return returns the current number of logged in users
     */
    public int getNumberOfLoggedInUsers();

    public List<Map<String,UserStats>> getListOfLoggedInUsers();
    
    /**
     * Factory to create new statistics query criteria
     * 
     * @return a newly create statistics empty criteria
     */
    public StatisticsQueryCriteria createStatisticsQueryCriteria();
    
    /**
     * Factory to create new, empty, aggregate statistics object.
     * 
     * @return unpopulated AggregateStatistics object 
     */
    public AggregateStatistics getDefaultEmptyAggregateStatistics();
}
