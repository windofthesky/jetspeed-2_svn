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
package org.apache.jetspeed.portalsite.impl;

import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.portalsite.PortalSite;
import org.apache.jetspeed.portalsite.PortalSiteSessionContext;

/**
 * This class implements the portal-site component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PortalSiteImpl implements PortalSite
{
    /**
     * pageManager - PageManager component
     */
    private PageManager pageManager;

    /**
     * PortalSiteImpl - component constructor
     *
     * @param pageManager PageManager component instance
     */
    public PortalSiteImpl(PageManager pageManager)
    {
        this.pageManager = pageManager;
    }

    /**
     * newSessionContext - create a new session context instance
     *
     * @return new session context instance
     */
    public PortalSiteSessionContext newSessionContext()
    {
        return new PortalSiteSessionContextImpl(pageManager);
    }

    /**
     * getPageManager - return PageManager component instance
     *
     * @return PageManager instance
     */
    public PageManager getPageManager()
    {
        return pageManager;
    }
}
