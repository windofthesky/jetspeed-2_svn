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

/**
 * This interface defines the content type mapper component
 * for use with the portal-site component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public interface PortalSiteContentTypeMapper
{
    public static final String PAGE_SYSTEM_TYPE = "portal-page";
    public static final String TEMPLATE_SYSTEM_TYPE = "portal-template";
    public static final String FOLDER_SYSTEM_TYPE = "portal-folder";
    
    /**
     * mapSystemType - map request to system type; implementation should
     *                 return null if type is not known.
     *
     * @param requestPath raw portal request path
     * @return mapped system type string or null
     */
    String mapSystemType(String requestPath);

    /**
     * isContentTypeFallbackEnabled - enable content type fallback for missing
     *                                system type page, folder, etc. requests
     *
     * @return content type fallback enabled flag
     */
    boolean isContentTypeFallbackEnabled();

    /**
     * mapContentType - map request to content type used to select dynamic
     *                  pages; implementation should return null to handle
     *                  request as page and folder lookup.
     *
     * @param requestPath raw portal request path
     * @return mapped content type string or null
     */
    String mapContentType(String requestPath);

    /**
     * mapSystemRequestPath - map content request path given previously mapped
     *                        content type to existing system page or folder;
     *                        implementation should return null to indicate no
     *                        mapping is available.
     *
     * @param serverName request server name
     * @param contentType mapped content type
     * @param requestPath raw portal request path
     * @return mapped system page or folder path or null
     */
    String mapSystemRequestPath(String serverName, String contentType, String requestPath);

    /**
     * mapDynamicRequestPath - map content request path given previously mapped
     *                         content type to dynamic page; implementation should
     *                         return null to indicate no mapping is available.
     *
     * @param serverName request server name
     * @param contentType mapped content type
     * @param requestPath raw portal request path
     * @return mapped dynamic page path or null
     */
    String mapDynamicRequestPath(String serverName, String contentType, String requestPath);

    /**
     * mapContentRequestPath - map content request path given previously mapped
     *                         content type to external content path; implementation
     *                         should return null to indicate no mapping is available.
     *
     * @param serverName request server name
     * @param contentType mapped content type
     * @param requestPath raw portal request path
     * @return mapped dynamic page path or null
     */
    String mapContentRequestPath(String serverName, String contentType, String requestPath);
}
