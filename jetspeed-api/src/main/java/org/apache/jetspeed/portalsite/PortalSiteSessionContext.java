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
package org.apache.jetspeed.portalsite;

import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.ProfileLocator;

import java.io.Serializable;
import java.util.Map;

/**
 * This describes the session context for the portal-site component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface PortalSiteSessionContext extends Serializable
{
    /**
     * newRequestContext - create a new request context instance with fallback and history
     *
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal);

    /**
     * newRequestContext - create a new request context instance with history
     *
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback);

    /**
     * newRequestContext - create a new request context instance
     *
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback, boolean useHistory);

    /**
     * newRequestContext - create a new request context instance
     *
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceReservedVisible force reserved/hidden folders visible in site view
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback, boolean useHistory, boolean forceReservedVisible, boolean forceTemplatesAccessible);

    /**
     * newRequestContext - create a new request context instance without profiling
     *                     support with fallback and history
     *
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(String requestPath, String requestServerName, String requestUserPrincipal);

    /**
     * newRequestContext - create a new request context instance without profiling
     *                     support with history
     *
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback);

    /**
     * newRequestContext - create a new request context instance without profiling
     *                     support
     *
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback, boolean useHistory);

    /**
     * newRequestContext - create a new request context instance without profiling
     *                     support
     *
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback, boolean useHistory, boolean forceTemplatesAccessible);

    /**
     * getPageManager - return PageManager component instance
     *
     * @return PageManager instance
     */
    PageManager getPageManager();

    /**
     * getContentTypeMapper - return PortalSiteContentTypeMapper component instance
     *
     * @return PortalSiteContentTypeMapper instance
     */
    PortalSiteContentTypeMapper getContentTypeMapper();

    /**
     * isValid - return flag indicating whether this context instance
     *           is valid or if it is stale after being persisted and
     *           reloaded as session state
     *
     * @return valid context status
     */
    boolean isValid();
}

