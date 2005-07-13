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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.proxy.NodeSetImpl;
import org.apache.jetspeed.portalsite.Menu;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.PortalSiteSessionContext;
import org.apache.jetspeed.portalsite.view.SiteViewMenuDefinitionLocator;

/**
 * This class encapsulates managed request state for and
 * interface to the portal-site component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PortalSiteRequestContextImpl implements PortalSiteRequestContext
{
    /**
     * sessionContext - component session state/interface
     */
    private PortalSiteSessionContextImpl sessionContext;

    /**
     * requestProfileLocators - map of request profile locators by locator names
     */
    private Map requestProfileLocators;

    /**
     * page - cached request profiled page proxy
     */
    private Page requestPage;

    /**
     * siblingPages - cached node set of visible sibling page proxies
     */
    private NodeSet siblingPages;

    /**
     * siblingPagesCached - cached flag for sibling page proxies
     */
    private boolean siblingPagesCached;

    /**
     * siblingFolders - cached node set of visible sibling folder proxies
     */
    private NodeSet siblingFolders;

    /**
     * siblingFoldersCached - cached flag for sibling folder proxies
     */
    private boolean siblingFoldersCached;

    /**
     * rootFolder - cached request profiled root folder proxy
     */
    private Folder requestRootFolder;

    /**
     * rootLinks - cached node set of visible link proxies
     */
    private NodeSet rootLinks;

    /**
     * rootLinksCached - cached flag for link proxies
     */
    private boolean rootLinksCached;

    /**
     * pageMenuDefinitionNames - cached menu definition names for request page
     */
    private Set pageMenuDefinitionNames;

    /**
     * menuDefinitionLocatorCache - cached menu definition locators for
     *                              relative menus valid for request
     */
    private Map menuDefinitionLocatorCache;

    /**
     * PortalSiteRequestContextImpl - constructor
     *
     * @param sessionContext session context
     * @param requestProfileLocators request profile locators
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, Map requestProfileLocators)
    {
        this.sessionContext = sessionContext;
        this.requestProfileLocators = requestProfileLocators;
    }

    /**
     * getSessionContext - get component session context
     *
     * @return component session context
     */
    public PortalSiteSessionContext getSessionContext()
    {
        return sessionContext;
    }

    /**
     * getLocators - get profile locators by locator names
     *  
     * @return request profile locators
     */
    public Map getLocators()
    {
        return requestProfileLocators;
    }

    /**
     * getManagedPage - get request profiled concrete page instance
     *                  as managed by the page manager
     *  
     * @return managed page
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Page getManagedPage() throws NodeNotFoundException
    {
        return sessionContext.getManagedPage(getPage());            
    }

    /**
     * getPage - get request profiled page proxy
     *  
     * @return page proxy
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Page getPage() throws NodeNotFoundException
    {
        // select request page from session context using
        // request profile locators if not previously
        // cached in this context
        if (requestPage == null)
        {
            requestPage = sessionContext.selectRequestPage(requestProfileLocators);            
        }
        return requestPage;
    }

    /**
     * getFolder - get folder proxy relative to request profiled page
     *  
     * @return page folder proxy
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Folder getFolder() throws NodeNotFoundException
    {
        // return parent folder of request page
        Page page = getPage();
        if (page != null)
        {
            return (Folder)page.getParent();
        }
        return null;
    }

    /**
     * getSiblingPages - get node set of sibling page proxies relative
     *                   to request profiled page, (includes profiled
     *                   page proxy)
     *  
     * @return sibling page proxies
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public NodeSet getSiblingPages() throws NodeNotFoundException
    {
        // cache filtered return value
        if (!siblingPagesCached)
        {
            // return pages from parent folder of request page
            Folder folder = getFolder();
            if (folder != null)
            {
                try
                {
                    // access, filter hidden, and cache
                    siblingPages = filterHiddenNodes(folder.getPages());
                    siblingPagesCached = true;
                }
                catch (NodeException ne)
                {
                    NodeNotFoundException nnfe = new NodeNotFoundException("Sibling pages not found.");
                    nnfe.initCause(ne);
                    throw nnfe;
                }
            }
        }
        return siblingPages;
    }

    /**
     * getParentFolder - get parent folder proxy relative to request
     *                   profiled page
     *  
     * @return parent folder proxy or null
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Folder getParentFolder() throws NodeNotFoundException
    {
        // return parent folder of parent folder of request page
        Folder folder = getFolder();
        if (folder != null)
        {
            // access, filter hidden, and return
            Folder parent = (Folder)folder.getParent();
            if ((parent != null) && !parent.isHidden())
            {
                return parent;
            }
        }
        return null;
    }

    /**
     * getSiblingFolders - get node set of sibling folder proxies relative
     *                     to request profiled page, (includes profiled
     *                     page folder proxy)
     *  
     * @return sibling folder proxies
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public NodeSet getSiblingFolders() throws NodeNotFoundException
    {
        // cache filtered return value
        if (!siblingFoldersCached)
        {
            // return folders from parent folder of request page
            Folder folder = getFolder();
            if (folder != null)
            {
                try
                {
                    // access, filter hidden, and cache
                    siblingFolders = filterHiddenNodes(folder.getFolders());
                    siblingFoldersCached = true;
                }
                catch (NodeException ne)
                {
                    NodeNotFoundException nnfe = new NodeNotFoundException("Sibling folders not found.");
                    nnfe.initCause(ne);
                    throw nnfe;
                }
            }
        }
        return siblingFolders;
    }

    /**
     * getRootFolder - get root profiled folder proxy
     *  
     * @return parent folder proxy
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Folder getRootFolder() throws NodeNotFoundException
    {
        // get request root folder from session context
        // using request profile locators if not previously
        // cached in this context
        if (requestRootFolder == null)
        {
            requestRootFolder = sessionContext.getRequestRootFolder(requestProfileLocators);
        }
        return requestRootFolder;
    }

    /**
     * getRootLinks - get node set of link proxies relative to
     *                profiled root folder
     *  
     * @return root link proxies
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public NodeSet getRootLinks() throws NodeNotFoundException
    {
        // cache filtered return value
        if (!rootLinksCached)
        {
            // return links from request root folder
            Folder rootFolder = getRootFolder();
            if (rootFolder != null)
            {
                try
                {
                    // access, filter hidden, and cache
                    rootLinks = filterHiddenNodes(rootFolder.getLinks());
                    rootLinksCached = true;
                }
                catch (NodeException ne)
                {
                    NodeNotFoundException nnfe = new NodeNotFoundException("Root links not found.");
                    nnfe.initCause(ne);
                    throw nnfe;
                }
            }
        }
        return rootLinks;
    }

    /**
     * getStandardMenuNames - get set of available standard menu names
     *  
     * @return menu names set
     */
    public Set getStandardMenuNames()
    {
        // return standard menu names defined for session
        return sessionContext.getStandardMenuNames();
    }

    /**
     * getCustomMenuNames - get set of custom menu names available as
     *                      defined for the request profiled page and folder
     *  
     * @return menu names set
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Set getCustomMenuNames() throws NodeNotFoundException
    {
        // access page to force request page resolution
        Page page = getPage();

        // return available menu definition names from
        // current request page if not previously cached
        // in this context
        Set standardMenuNames = sessionContext.getStandardMenuNames();
        if ((page != null) && (standardMenuNames != null) && (pageMenuDefinitionNames == null))
        {
            List locators = sessionContext.getMenuDefinitionLocators(page);
            if (locators != null)
            {
                // get custom definition names
                pageMenuDefinitionNames = new HashSet(locators.size());
                Iterator locatorsIter = locators.iterator();
                while (locatorsIter.hasNext())
                {
                    // get definition name; filter standard menu names
                    String definitionName = ((SiteViewMenuDefinitionLocator)locatorsIter.next()).getName();
                    if (!standardMenuNames.contains(definitionName))
                    {
                        pageMenuDefinitionNames.add(definitionName);
                    }
                }
            }
            else
            {
                pageMenuDefinitionNames = new HashSet(0);
            }
        }
        return pageMenuDefinitionNames;
    }

    /**
     * getMenu - get instantiated menu available for the request
     *           profiled page and folder
     *  
     * @param name menu definition name
     * @return menu instance
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Menu getMenu(String name) throws NodeNotFoundException
    {
        // get menu initiating at menu definition root
        // with no related menu definition names
        return getMenu(name, null);
    }

    /**
     * getMenu - get instantiated menu available for the request
     *           profiled page and folder, avoiding cyclic
     *           menu definition loops by propagating related menu
     *           names set from menu construction
     *
     * @param name menu definition name
     * @param names set of related menu definition names
     * @return menu instance
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Menu getMenu(String name, Set names) throws NodeNotFoundException
    {
        // access page to force request page resolution
        Page page = getPage();
        if ((page != null) && (name != null))
        {
            // get menu definition locator
            SiteViewMenuDefinitionLocator locator = sessionContext.getMenuDefinitionLocator(page, name);
            if (locator != null)
            {
                // lookup and return cached relative/request menus
                if (menuDefinitionLocatorCache != null)
                {
                    MenuImpl menu = (MenuImpl)menuDefinitionLocatorCache.get(locator);
                    if (menu != null)
                    {
                        return menu;
                    }
                }

                // lookup and return cached absolute/session menus
                if (sessionContext.getMenuDefinitionLocatorCache() != null)
                {
                    MenuImpl menu = (MenuImpl)sessionContext.getMenuDefinitionLocatorCache().get(locator);
                    if (menu != null)
                    {
                        return menu;
                    }
                }

                // construct new menu from menu definition in locator
                // using current request context and propagating related
                // names set to detect cyclic menu definitions
                MenuImpl menu = new MenuImpl(locator.getMenuDefinition(), this, names);
 
                // determine whether menu definition locator is
                // relative/request or absolute/session cachable
                // and cache accordingly
                if (menu.isElementRelative())
                {
                    // cache relative menu for request
                    if (menuDefinitionLocatorCache == null)
                    {
                        menuDefinitionLocatorCache = new HashMap(8);
                    }
                    menuDefinitionLocatorCache.put(locator, menu);
                }
                else
                {
                    // cache absolute menu for session
                    if (sessionContext.getMenuDefinitionLocatorCache() == null)
                    {
                        sessionContext.setMenuDefinitionLocatorCache(new HashMap(8));
                    }
                    sessionContext.getMenuDefinitionLocatorCache().put(locator, menu);
                }

                // return new cached menu
                return menu;
            }
        }
        return null;
    }

    /**
     * filterHiddenNodes - utility to filter hidden node proxies out of node sets
     *
     * @param nodes proxy node set to filter
     * @return input or filtered proxy node set
     */
    private static NodeSet filterHiddenNodes(NodeSet nodes)
    {
        if ((nodes != null) && !nodes.isEmpty())
        {
            // filter node proxies in node set
            List filteredNodes = null;
            Iterator nodesIter = nodes.iterator();
            while (nodesIter.hasNext())
            {
                // test hidden status of individual node proxies
                Node node = (Node)nodesIter.next();
                if (node.isHidden())
                {
                    // if not copying, create new node set
                    // and copy preceding node proxies
                    if (filteredNodes == null)
                    {
                        filteredNodes = new ArrayList(nodes.size());
                        Iterator copyIter = nodes.iterator();
                        while (copyIter.hasNext())
                        {
                            Node copyNode = (Node)copyIter.next();
                            if (copyNode != node)
                            {
                                filteredNodes.add(copyNode);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                else if (filteredNodes != null)
                {
                    // if copying, copy node proxy to filtered set
                    filteredNodes.add(node);
                }
            }

            // return filteredNodes node proxies if generated
            // in new immutable proxy node set
            if (filteredNodes != null)
            {
                return new NodeSetImpl(filteredNodes);
            }
        }
        return nodes;
    }
}
