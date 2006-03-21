/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.portalsite;

import java.io.Serializable;
import java.util.Map;

import org.apache.jetspeed.page.PageManager;

/**
 * This describes the session context for the portal-site component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface PortalSiteSessionContext extends Serializable
{
    /**
     * newRequestContext - create a new request context instance with fallback
     *
     * @param requestProfileLocators request profile locators
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(Map requestProfileLocators);

    /**
     * newRequestContext - create a new request context instance
     *
     * @param requestProfileLocators request profile locators
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @return new request context instance
     */
    PortalSiteRequestContext newRequestContext(Map requestProfileLocators, boolean requestFallback);

    /**
     * getPageManager - return PageManager component instance
     *
     * @return PageManager instance
     */
    PageManager getPageManager();
}
