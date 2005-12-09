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

import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;

/**
 * This describes the request context for the portal-site component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface PortalSiteRequestContext
{
    /**
     * getSessionContext - get component session context
     *
     * @return component session context
     */
    PortalSiteSessionContext getSessionContext();

    /**
     * getLocators - get profile locators by locator names
     *  
     * @return request profile locators
     */
    Map getLocators();

    /**
     * getManagedPage - get request profiled concrete page instance
     *                  as managed by the page manager
     *  
     * @return page
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    Page getManagedPage() throws NodeNotFoundException;

    /**
     * getPage - get request profiled page proxy
     *  
     * @return page proxy
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    Page getPage() throws NodeNotFoundException;

    /**
     * getFolder - get folder proxy relative to request profiled page
     *  
     * @return page folder proxy
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    Folder getFolder() throws NodeNotFoundException;

    /**
     * getSiblingPages - get node set of sibling page proxies relative
     *                   to request profiled page, (includes profiled
     *                   page proxy)
     *  
     * @return sibling page proxies
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    NodeSet getSiblingPages() throws NodeNotFoundException;

    /**
     * getParentFolder - get parent folder proxy relative to request
     *                   profiled page
     *  
     * @return parent folder proxy or null
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    Folder getParentFolder() throws NodeNotFoundException;

    /**
     * getSiblingFolders - get node set of sibling folder proxies relative
     *                     to request profiled page, (includes profiled
     *                     page folder proxy)
     *  
     * @return sibling folder proxies
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    NodeSet getSiblingFolders() throws NodeNotFoundException;

    /**
     * getRootFolder - get root profiled folder proxy
     *  
     * @return parent folder proxy
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    Folder getRootFolder() throws NodeNotFoundException;

    /**
     * getRootLinks - get node set of link proxies relative to
     *                profiled root folder
     *  
     * @return root link proxies
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    NodeSet getRootLinks() throws NodeNotFoundException;

    /**
     * getStandardMenuNames - get set of available standard menu names
     *  
     * @return menu names set
     */
    Set getStandardMenuNames();

    /**
     * getCustomMenuNames - get set of custom menu names available as
     *                      defined for the request profiled page and folder
     *  
     * @return menu names set
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    Set getCustomMenuNames() throws NodeNotFoundException;

    /**
     * getMenu - get instantiated menu available for the request
     *           profiled page and folder
     *  
     * @param name menu definition name
     * @return menu instance
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    Menu getMenu(String name) throws NodeNotFoundException;
}
