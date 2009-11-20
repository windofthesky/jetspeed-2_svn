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
package org.apache.jetspeed.portalsite.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
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
     * requestFallback - flag indicating whether request should fallback to root folder
     *                   if locators do not select a page or access is forbidden
     */
    private boolean requestFallback;

    /**
     * useHistory - flag indicating whether to use visited page
     *              history to select default page per site folder
     */
    private boolean useHistory;

    /**
     * forceReservedVisible - force reserved/hidden folders visible in site view
     */
    private boolean forceReservedVisible;

    /**
     * requestPage - cached request profiled page proxy
     */
    private Page requestPage;

    /**
     * requestPageTemplate - cached request page template proxy
     */
    private PageTemplate requestPageTemplate;

    /**
     * requestPageTemplateCached - cached flag for request page template proxy
     */
    private boolean requestPageTemplateCached;

    /**
     * requestFragmentDefinitions - cached request request fragment definition proxies map
     */
    private Map requestFragmentDefinitions;

    /**
     * requestFragmentDefinitionsCached - cached flag for request fragment definition proxies map
     */
    private boolean requestFragmentDefinitionsCached;

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
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceReservedVisible force reserved/hidden folders visible in site view
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, Map requestProfileLocators,
                                        boolean requestFallback, boolean useHistory, boolean forceReservedVisible)
    {
        this.sessionContext = sessionContext;
        this.requestProfileLocators = requestProfileLocators;
        this.requestFallback = requestFallback;
        this.useHistory = useHistory;
        this.forceReservedVisible = forceReservedVisible;
    }

    /**
     * PortalSiteRequestContextImpl - constructor
     *
     * @param sessionContext session context
     * @param requestProfileLocators request profile locators
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, Map requestProfileLocators,
                                        boolean requestFallback, boolean useHistory)
    {
        this(sessionContext, requestProfileLocators, requestFallback, useHistory, false);
    }

    /**
     * PortalSiteRequestContextImpl - constructor
     *
     * @param sessionContext session context
     * @param requestProfileLocators request profile locators
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, Map requestProfileLocators,
                                        boolean requestFallback)
    {
        this(sessionContext, requestProfileLocators, requestFallback, true, false);
    }

    /**
     * PortalSiteRequestContextImpl - constructor
     *
     * @param sessionContext session context
     * @param requestProfileLocators request profile locators
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, Map requestProfileLocators)
    {
        this(sessionContext, requestProfileLocators, true, true, false);
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
     * getManagedPageTemplate - get request profiled concrete page 
     *                          template instance as managed by the
     *                          page manager
     *  
     * @return page template
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public PageTemplate getManagedPageTemplate() throws NodeNotFoundException
    {
        return sessionContext.getManagedPageTemplate(getPageTemplate());            
    }

    /**
     * getManagedFragmentDefinitions - get map of request profiled concrete
     *                                 fragment definition instances as
     *                                 managed by the page manager
     *  
     * @return map of fragment definitions by id
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Map getManagedFragmentDefinitions() throws NodeNotFoundException
    {
        // convert map of proxies to map of managed fragment definitions
        Map fragmentDefinitions = getFragmentDefinitions();
        if (fragmentDefinitions != null)
        {
            Map managedFragmentDefinitions = new HashMap(4);
            Iterator fragmentDefinitionsIter = fragmentDefinitions.entrySet().iterator();
            while (fragmentDefinitionsIter.hasNext())
            {
                Map.Entry fragmentDefinitionEntry = (Map.Entry)fragmentDefinitionsIter.next();
                String id = (String)fragmentDefinitionEntry.getKey();
                FragmentDefinition fragmentDefinition = (FragmentDefinition)fragmentDefinitionEntry.getValue();
                FragmentDefinition managedFragmentDefinition = sessionContext.getManagedFragmentDefinition(fragmentDefinition);
                if (managedFragmentDefinition != null)
                {
                    managedFragmentDefinitions.put(id, managedFragmentDefinition);
                }
            }
            return managedFragmentDefinitions;
        }
        return null;
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
            requestPage = sessionContext.selectRequestPage(requestProfileLocators, requestFallback, useHistory, forceReservedVisible);            
        }
        return requestPage;
    }

    /**
     * getPageTemplate - get page template proxy for request profiled page
     *  
     * @return page template proxy if found or null
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public PageTemplate getPageTemplate() throws NodeNotFoundException
    {
        if (!requestPageTemplateCached)
        {
            // get requested page
            Page page = getPage();
            if (page != null)
            {
                // scan through site looking for first page template
                // up the folder hierarchy from the requested page
                try
                {
                    Folder folder = (Folder)page.getParent();
                    while ((folder != null) && (requestPageTemplate == null))
                    {
                        NodeSet pageTemplates = folder.getPageTemplates();
                        if ((pageTemplates != null) && !pageTemplates.isEmpty())
                        {
                            // return first page template found
                            requestPageTemplate = (PageTemplate)pageTemplates.iterator().next();
                        }
                        else
                        {
                            // continue scan
                            folder = (Folder)folder.getParent();
                        }
                    }
                }
                catch (NodeException ne)
                {
                }
            }
            requestPageTemplateCached = true;
        }
        return requestPageTemplate;
    }

    /**
     * getFragmentDefinitions - get fragment definition proxy map for request
     *                          profiled page and page template
     *  
     * @return map of fragment definition proxies by fragment id
     * @throws NodeNotFoundException if page or fragment definition not found
     * @throws SecurityException if page view access not granted
     */
    public Map getFragmentDefinitions() throws NodeNotFoundException
    {
        if (!requestFragmentDefinitionsCached)
        {
            // get requested page and optional page template
            Page page = getPage();
            PageTemplate pageTemplate = getPageTemplate();
            if (page != null)
            {
                // merge fragment reference ids from requested page and page template
                Set refIds = new HashSet(4);
                List requestPageFragmentReferences = page.getFragmentsByInterface(FragmentReference.class);
                mergeFragmentDefinitionRefIds(requestPageFragmentReferences, refIds);
                List requestPageTemplateFragmentReferences = ((pageTemplate != null) ? pageTemplate.getFragmentsByInterface(FragmentReference.class) : null);
                mergeFragmentDefinitionRefIds(requestPageTemplateFragmentReferences, refIds);
                
                // scan through site looking for each first matching fragment
                // definition by reference/definition id up the folder hierarchy
                // from the requested page
                Iterator refIdsIter = refIds.iterator();
                while (refIdsIter.hasNext())
                {
                    String refId = (String)refIdsIter.next();
                    FragmentDefinition requestFragmentDefinition = null;
                    try
                    {
                        // scan for fragment definition
                        Folder folder = (Folder)page.getParent();
                        while ((folder != null) && (requestFragmentDefinition == null))
                        {
                            NodeSet fragmentDefinitions = folder.getFragmentDefinitions();
                            if ((fragmentDefinitions != null) && !fragmentDefinitions.isEmpty())
                            {
                                // find fragment definition by matching reference/definition id
                                Iterator fragmentDefinitionsIter = fragmentDefinitions.iterator();
                                while (fragmentDefinitionsIter.hasNext() && (requestFragmentDefinition == null))
                                {
                                    FragmentDefinition fragmentDefinition = (FragmentDefinition)fragmentDefinitionsIter.next();
                                    if (fragmentDefinition.getDefId().equals(refId))
                                    {
                                        requestFragmentDefinition = fragmentDefinition;
                                    }
                                }
                            }
                            else
                            {
                                // continue scan
                                folder = (Folder)folder.getParent();
                            }
                        }
                        
                        // match fragment definition
                        if (requestFragmentDefinition != null)
                        {
                            if (requestFragmentDefinitions == null)
                            {
                                requestFragmentDefinitions = Collections.synchronizedMap(new HashMap(4));
                            }
                            requestFragmentDefinitions.put(refId, requestFragmentDefinition);
                        }
                        else
                        {
                            throw new NodeNotFoundException("Fragment definition for "+refId+" not found.");                        
                        }
                    }
                    catch (NodeException ne)
                    {
                        NodeNotFoundException nnfe = new NodeNotFoundException("Fragment definition for "+refId+" not found.");
                        nnfe.initCause(ne);
                        throw nnfe;
                    }
                }
            }
            requestFragmentDefinitionsCached = true;
        }
        return requestFragmentDefinitions;
    }
    
    /**
     * mergeFragmentDefinitionRefIds - utility method to merge reference ids
     * 
     * @param fragmentReferences list of fragment references
     * @param refIds merged set of unique reference ids
     */
    private void mergeFragmentDefinitionRefIds(List fragmentReferences, Set refIds)
    {
        // merge list of fragment reference reference ids 
        if ((fragmentReferences != null) && !fragmentReferences.isEmpty())
        {
            Iterator fragmentReferencesIter = fragmentReferences.iterator();
            while (fragmentReferencesIter.hasNext())
            {
                FragmentReference fragmentReference = (FragmentReference)fragmentReferencesIter.next();
                if (fragmentReference.getRefId() != null)
                {
                    refIds.add(fragmentReference.getRefId());
                }
            }
        }        
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
            requestRootFolder = sessionContext.getRequestRootFolder(requestProfileLocators, forceReservedVisible);
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
     *                      defined for the request profiled page, page
     *                      template, and folders
     *  
     * @return menu names set
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Set getCustomMenuNames() throws NodeNotFoundException
    {
        // access page and page templates to force request
        // page resolution
        Page page = getPage();
        PageTemplate pageTemplate = getPageTemplate();

        // return available menu definition names from
        // current request page and page template if not
        // previously cached in this context
        Set standardMenuNames = sessionContext.getStandardMenuNames();
        if ((page != null) && (standardMenuNames != null) && (pageMenuDefinitionNames == null))
        {
            List pageLocators = sessionContext.getMenuDefinitionLocators(page);
            List pageTemplateLocators = ((pageTemplate != null) ? sessionContext.getMenuDefinitionLocators(pageTemplate) : null);
            if ((pageLocators != null) || (pageTemplateLocators != null))
            {
                // get custom definition names
                pageMenuDefinitionNames = Collections.synchronizedSet(new HashSet(8));
                mergeMenuDefinitionLocatorNames(pageLocators, standardMenuNames, pageMenuDefinitionNames);
                mergeMenuDefinitionLocatorNames(pageTemplateLocators, standardMenuNames, pageMenuDefinitionNames);
            }
            else
            {
                pageMenuDefinitionNames = Collections.synchronizedSet(new HashSet(0));
            }
        }
        return pageMenuDefinitionNames;
    }
    
    /**
     * mergeMenuDefinitionLocatorNames - merge menu locator names
     * 
     * @param locators menu definition locators
     * @param excludeNames excluded names set
     * @param names merged names set
     */
    private void mergeMenuDefinitionLocatorNames(List locators, Set excludeNames, Set names)
    {
        // merge menu definition locator names
        if (locators != null)
        {
            Iterator locatorsIter = locators.iterator();
            while (locatorsIter.hasNext())
            {
                String definitionName = ((SiteViewMenuDefinitionLocator)locatorsIter.next()).getName();
                if (!excludeNames.contains(definitionName))
                {
                    names.add(definitionName);
                }
            }
        }        
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
        // access page and page template to force request
        // page resolution
        Page page = getPage();
        PageTemplate pageTemplate = getPageTemplate();
        if ((page != null) && (name != null))
        {
            // get menu definition locator from page or page template
            SiteViewMenuDefinitionLocator locator = sessionContext.getMenuDefinitionLocator(page, name);
            if ((pageTemplate != null) && ((locator == null) || !locator.isOverride()))
            {
                SiteViewMenuDefinitionLocator pageTemplateLocator = sessionContext.getMenuDefinitionLocator(pageTemplate, name);
                if (pageTemplateLocator != null)
                {
                    locator = pageTemplateLocator;
                }
            }
            // get menu implementation for menu definition locator
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
                // if current page is not hidden; hidden pages generate
                // menus that should be considered relative since
                // explicitly addressed hidden pages are added to
                // menus for display purposes
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
                // relative/request, based on hidden page, or
                // absolute/session cachable and cache accordingly
                if (page.isHidden() || menu.isElementRelative())
                {
                    // cache relative menu for request
                    if (menuDefinitionLocatorCache == null)
                    {
                        menuDefinitionLocatorCache = Collections.synchronizedMap(new HashMap(8));
                    }
                    menuDefinitionLocatorCache.put(locator, menu);
                }
                else
                {
                    // cache absolute menu for session
                    if (sessionContext.getMenuDefinitionLocatorCache() == null)
                    {
                        sessionContext.setMenuDefinitionLocatorCache(Collections.synchronizedMap(new HashMap(8)));
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
