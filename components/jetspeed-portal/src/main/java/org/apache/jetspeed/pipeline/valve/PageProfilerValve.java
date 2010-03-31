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
package org.apache.jetspeed.pipeline.valve;

import org.apache.jetspeed.PortalReservedParameters;

/**
 * Determine the page to display and add it to the RequestContext
 *
 * <br/>
 * Read from the ValveContext:
 * <ul>
 * </ul>
 *
 * <br/>
 * Written into the ValveContext:
 * <ul>
 * </ul>
 *
 * <br>
 * Note: The primary purpose of this interface is primary for documentation.
 * 
 * @author <a href="mailto:paul@apache.org">Paul Spencer</a>
 * @version $Id$
 *
 * @see ValveContext
 */
public interface PageProfilerValve extends Valve
{
    /**
     * PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY - session portal site context attribute key
     */
    static String PORTAL_SITE_SESSION_CONTEXT_ATTR_KEY = PortalReservedParameters.PORTAL_SITE_SESSION_CONTEXT_ATTR_KEY;

    /**
     * PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY - request portal site context attribute key
     */
    static String PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY = PortalReservedParameters.PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY;

    /**
     * PROFILED_PAGE_CONTEXT_ATTR_KEY - legacy request portal site context attribute key
     */
    static String PROFILED_PAGE_CONTEXT_ATTR_KEY = "org.apache.jetspeed.profiledPageContext";

    /**
     * PAGE_ACTION_ACCESS_MAP_SESSION_ATTR_KEY - PageActionAccess instances map attribute key
     */
    static String PAGE_ACTION_ACCESS_MAP_SESSION_ATTR_KEY = "org.apache.jetspeed.profiler.pageActionAccessMap";
    
    /**
     * PROFILE_LOCATOR_REQUEST_ATTR_KEY - request override for profile locator attribute key
     */
    static String PROFILE_LOCATOR_REQUEST_ATTR_KEY = "org.apache.jetspeed.profiler.ProfileLocator";
}
