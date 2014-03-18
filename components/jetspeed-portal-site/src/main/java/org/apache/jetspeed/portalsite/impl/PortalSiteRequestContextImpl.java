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

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.BaseConcretePageElement;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.DynamicPage;
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
import org.apache.jetspeed.profiler.ProfileLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Map<String,ProfileLocator> requestProfileLocators;

    /**
     * requestPath - request path if not using profile locators
     */
    private String requestPath;
    
    /**
     * requestServerName - request server name if not using profile locators
     */
    private String requestServerName;
        
    /**
     * requestUserPrincipal - request user principal name
     */
    private String requestUserPrincipal;
        
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
     * forceTemplatesAccessible - force templates, (page templates, dynamic pages, and
     *                            fragment definitions), accessible to requests in site view
     */
    private transient boolean forceTemplatesAccessible;

    /**
     * requestPage - cached request profiled page or template view
     */
    private BaseFragmentsElement requestPageOrTemplate;
    
    /**
     * requestPageContentPath - cached content path mapped for request page
     */
    private String requestPageContentPath;
    
    /**
     * requestPageTemplate - cached request page template view
     */
    private PageTemplate requestPageTemplate;

    /**
     * requestPageTemplateCached - cached flag for request page template view
     */
    private boolean requestPageTemplateCached;

    /**
     * requestFragmentDefinitions - cached request request fragment definition views map
     */
    private Map<String,FragmentDefinition> requestFragmentDefinitions;

    /**
     * requestFragmentDefinitionsCached - cached flag for request fragment definition views map
     */
    private boolean requestFragmentDefinitionsCached;

    /**
     * siblingPages - cached node set of visible sibling page views
     */
    private NodeSet siblingPages;

    /**
     * siblingPagesCached - cached flag for sibling page views
     */
    private boolean siblingPagesCached;

    /**
     * siblingFolders - cached node set of visible sibling folder views
     */
    private NodeSet siblingFolders;

    /**
     * siblingFoldersCached - cached flag for sibling folder views
     */
    private boolean siblingFoldersCached;

    /**
     * rootFolder - cached request profiled root folder view
     */
    private Folder requestRootFolder;

    /**
     * rootLinks - cached node set of visible link views
     */
    private NodeSet rootLinks;

    /**
     * rootLinksCached - cached flag for link views
     */
    private boolean rootLinksCached;

    /**
     * pageMenuDefinitionNames - cached menu definition names for request page
     */
    private Set<String> pageMenuDefinitionNames;

    /**
     * menuDefinitionLocatorCache - cached menu definition locators for
     *                              relative menus valid for request
     */
    private Map<SiteViewMenuDefinitionLocator,MenuImpl> menuDefinitionLocatorCache;

    /**
     * PortalSiteRequestContextImpl - constructor
     *
     * @param sessionContext session context
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceReservedVisible force reserved/hidden folders visible in site view
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback, boolean useHistory, boolean forceReservedVisible, boolean forceTemplatesAccessible)
    {
        this.sessionContext = sessionContext;
        this.requestProfileLocators = requestProfileLocators;
        this.requestUserPrincipal = requestUserPrincipal;
        this.requestFallback = requestFallback;
        this.useHistory = useHistory;
        this.forceReservedVisible = forceReservedVisible;
        this.forceTemplatesAccessible = forceTemplatesAccessible;
    }

    /**
     * PortalSiteRequestContextImpl - constructor
     *
     * @param sessionContext session context
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback, boolean useHistory)
    {
        this(sessionContext, requestProfileLocators, requestUserPrincipal, requestFallback, useHistory, false, false);
    }

    /**
     * PortalSiteRequestContextImpl - constructor
     *
     * @param sessionContext session context
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback)
    {
        this(sessionContext, requestProfileLocators, requestUserPrincipal, requestFallback, true, false, false);
    }

    /**
     * PortalSiteRequestContextImpl - constructor
     *
     * @param sessionContext session context
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal)
    {
        this(sessionContext, requestProfileLocators, requestUserPrincipal, true, true, false, false);
    }

    /**
     * PortalSiteRequestContextImpl - non-profiling constructor
     *
     * @param sessionContext session context
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback, boolean useHistory, boolean forceTemplatesAccessible)
    {
        this.sessionContext = sessionContext;
        this.requestPath = requestPath;
        this.requestServerName = requestServerName;
        this.requestUserPrincipal = requestUserPrincipal;
        this.requestFallback = requestFallback;
        this.useHistory = useHistory;
        this.forceTemplatesAccessible = forceTemplatesAccessible;
    }

    /**
     * PortalSiteRequestContextImpl - non-profiling constructor
     *
     * @param sessionContext session context
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback, boolean useHistory)
    {
        this(sessionContext, requestPath, requestServerName, requestUserPrincipal, requestFallback, useHistory, false);
    }

    /**
     * PortalSiteRequestContextImpl - non-profiling constructor
     *
     * @param sessionContext session context
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback)
    {
        this(sessionContext, requestPath, requestServerName, requestUserPrincipal, requestFallback, true, false);
    }

    /**
     * PortalSiteRequestContextImpl - non-profiling constructor
     *
     * @param sessionContext session context
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     */
    public PortalSiteRequestContextImpl(PortalSiteSessionContextImpl sessionContext, String requestPath, String requestServerName, String requestUserPrincipal)
    {
        this(sessionContext, requestPath, requestServerName, requestUserPrincipal, true, true, false);
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
    public Map<String,ProfileLocator> getLocators()
    {
        return requestProfileLocators;
    }

    /**
     * getManagedPageOrTemplate - get request profiled concrete page or template
     *                            instance as managed by the page manager
     *  
     * @return managed page
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public BaseFragmentsElement getManagedPageOrTemplate() throws NodeNotFoundException
    {
        return sessionContext.getManagedPageOrTemplate(getPageOrTemplate());            
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
    public Map<String,FragmentDefinition> getManagedFragmentDefinitions() throws NodeNotFoundException
    {
        // convert map of views to map of managed fragment definitions
        Map<String,FragmentDefinition> fragmentDefinitions = getFragmentDefinitions();
        if (fragmentDefinitions != null)
        {
            Map<String,FragmentDefinition> managedFragmentDefinitions = new HashMap<String,FragmentDefinition>(4);
            for (Map.Entry<String,FragmentDefinition> fragmentDefinitionEntry : fragmentDefinitions.entrySet())
            {
                String id = fragmentDefinitionEntry.getKey();
                FragmentDefinition fragmentDefinition = fragmentDefinitionEntry.getValue();
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
     * isContentPage - returns flag indicating request page is honoring
     *                 a content request
     *
     * @return content page flag
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public boolean isContentPage() throws NodeNotFoundException
    {
        return ((getPageOrTemplate() instanceof BaseConcretePageElement) && (getPageContentPath() != null));
    }

    /**
     * isConcretePage - returns flag indicating request page is honoring
     *                  a concrete page or content page request
     *
     * @return concrete page flag
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public boolean isConcretePage() throws NodeNotFoundException
    {
        // check current page type and content path
        BaseFragmentsElement pageOrTemplate = getPageOrTemplate();
        String pageContentPath = getPageContentPath();
        return ((pageOrTemplate instanceof Page) || ((pageOrTemplate instanceof DynamicPage) && (pageContentPath != null)));
    }

    /**
     * getPageOrTemplate - get request profiled page or template view
     *  
     * @return page or template view
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public BaseFragmentsElement getPageOrTemplate() throws NodeNotFoundException
    {
        // select request page and associated content path from
        // session context using request profile locators or
        // request path and server name if not previously cached
        // in this context
        if (requestPageOrTemplate == null)
        {
            String [] selectedRequestPageContentPath = new String[]{null};
            if (requestProfileLocators != null)
            {
                requestPageOrTemplate = sessionContext.selectRequestPageOrTemplate(requestProfileLocators, requestUserPrincipal, requestFallback, useHistory, forceReservedVisible, forceTemplatesAccessible, selectedRequestPageContentPath);
            }
            else
            {
                requestPageOrTemplate = sessionContext.selectRequestPageOrTemplate(requestPath, requestServerName, requestUserPrincipal, requestFallback, useHistory, forceTemplatesAccessible, selectedRequestPageContentPath);
            }
            if (requestPageOrTemplate != null)
            {
                requestPageContentPath = selectedRequestPageContentPath[0];
            }
        }
        return requestPageOrTemplate;
    }

    /**
     * getPageContentPath - get content path associated with request page
     *  
     * @return content path
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public String getPageContentPath() throws NodeNotFoundException
    {
        return ((getPageOrTemplate() != null) ? requestPageContentPath : null);
    }

    /**
     * getPageTemplate - get page template view for request profiled page
     *  
     * @return page template view if found or null
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public PageTemplate getPageTemplate() throws NodeNotFoundException
    {
        if (!requestPageTemplateCached)
        {
            // get requested page
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate();
            if (pageOrTemplate != null)
            {
                // scan through site looking for first page template
                // up the folder hierarchy from the requested page
                try
                {
                    Folder folder = (Folder)pageOrTemplate.getParent();
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
     * getFragmentDefinitions - get fragment definition view map for request
     *                          profiled page and page template
     *  
     * @return map of fragment definition views by fragment id
     * @throws NodeNotFoundException if page or fragment definition not found
     * @throws SecurityException if page view access not granted
     */
    public Map<String,FragmentDefinition> getFragmentDefinitions() throws NodeNotFoundException
    {
        if (!requestFragmentDefinitionsCached)
        {
            // get requested page or template and optional page template
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate();
            PageTemplate pageTemplate = getPageTemplate();
            if (pageOrTemplate != null)
            {
                // merge fragment reference ids from requested page and page template
                Set<String> refIds = new HashSet<String>(4);
                List<BaseFragmentElement> requestPageFragmentReferences = pageOrTemplate.getFragmentsByInterface(FragmentReference.class);
                mergeFragmentDefinitionRefIds(requestPageFragmentReferences, refIds);
                List<BaseFragmentElement> requestPageTemplateFragmentReferences = ((pageTemplate != null) ? pageTemplate.getFragmentsByInterface(FragmentReference.class) : null);
                mergeFragmentDefinitionRefIds(requestPageTemplateFragmentReferences, refIds);
                
                // scan through site looking for each first matching fragment
                // definition by reference/definition id up the folder hierarchy
                // from the requested page
                for (String refId : refIds)
                {
                    FragmentDefinition requestFragmentDefinition = null;
                    try
                    {
                        // scan for fragment definition
                        Folder folder = (Folder)pageOrTemplate.getParent();
                        while ((folder != null) && (requestFragmentDefinition == null))
                        {
                            NodeSet fragmentDefinitions = folder.getFragmentDefinitions();
                            if ((fragmentDefinitions != null) && !fragmentDefinitions.isEmpty())
                            {
                                // find fragment definition by matching reference/definition id
                                for (Node fragmentDefinitionNode : fragmentDefinitions)
                                {
                                    FragmentDefinition fragmentDefinition = (FragmentDefinition)fragmentDefinitionNode;
                                    if (fragmentDefinition.getDefId().equals(refId))
                                    {
                                        requestFragmentDefinition = fragmentDefinition;
                                        break;
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
                                requestFragmentDefinitions = Collections.synchronizedMap(new HashMap<String,FragmentDefinition>(4));
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
    private void mergeFragmentDefinitionRefIds(List<BaseFragmentElement> fragmentReferences, Set<String> refIds)
    {
        // merge list of fragment reference reference ids 
        if ((fragmentReferences != null) && !fragmentReferences.isEmpty())
        {
            for (BaseFragmentElement fragmentReference : fragmentReferences)
            {
                String refId = ((FragmentReference)fragmentReference).getRefId();
                if (refId != null)
                {
                    refIds.add(refId);
                }
            }
        }        
    }

    /**
     * getFolder - get folder view relative to request profiled page
     *  
     * @return page folder view
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Folder getFolder() throws NodeNotFoundException
    {
        // return parent folder of request page or template
        BaseFragmentsElement pageOrTemplate = getPageOrTemplate();
        if (pageOrTemplate != null)
        {
            return (Folder)pageOrTemplate.getParent();
        }
        return null;
    }

    /**
     * getSiblingPages - get node set of sibling page views relative
     *                   to request profiled page, (includes profiled
     *                   page view)
     *  
     * @return sibling page views
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
     * getParentFolder - get parent folder view relative to request
     *                   profiled page
     *  
     * @return parent folder view or null
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
     * getSiblingFolders - get node set of sibling folder views relative
     *                     to request profiled page, (includes profiled
     *                     page folder view)
     *  
     * @return sibling folder views
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
     * getRootFolder - get root profiled folder view
     *  
     * @return parent folder view
     * @throws NodeNotFoundException if page not found
     * @throws SecurityException if page view access not granted
     */
    public Folder getRootFolder() throws NodeNotFoundException
    {
        // get request root folder from session context
        // using request profile locators or default if not
        // previously cached in this context
        if (requestRootFolder == null)
        {
            if (requestProfileLocators != null)
            {
                requestRootFolder = sessionContext.getRequestRootFolder(requestProfileLocators, requestUserPrincipal, forceReservedVisible);
            }
            else
            {
                requestRootFolder = sessionContext.getRequestRootFolder(requestUserPrincipal);                
            }
        }
        return requestRootFolder;
    }

    /**
     * getRootLinks - get node set of link views relative to
     *                profiled root folder
     *  
     * @return root link views
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
    public Set<String> getStandardMenuNames()
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
    public Set<String> getCustomMenuNames() throws NodeNotFoundException
    {
        // menus only available for concrete page requests
        if (isConcretePage())
        {
            BaseConcretePageElement page = (BaseConcretePageElement)getPageOrTemplate();            
            PageTemplate pageTemplate = getPageTemplate();
            // return available menu definition names from
            // current request page and page template if not
            // previously cached in this context
            Set<String> standardMenuNames = sessionContext.getStandardMenuNames();
            if ((page != null) && (standardMenuNames != null) && (pageMenuDefinitionNames == null))
            {
                List<SiteViewMenuDefinitionLocator> pageLocators = sessionContext.getMenuDefinitionLocators(page);
                List<SiteViewMenuDefinitionLocator> pageTemplateLocators = ((pageTemplate != null) ? sessionContext.getMenuDefinitionLocators(pageTemplate) : null);
                if ((pageLocators != null) || (pageTemplateLocators != null))
                {
                    // get custom definition names
                    pageMenuDefinitionNames = Collections.synchronizedSet(new HashSet<String>(8));
                    mergeMenuDefinitionLocatorNames(pageLocators, standardMenuNames, pageMenuDefinitionNames);
                    mergeMenuDefinitionLocatorNames(pageTemplateLocators, standardMenuNames, pageMenuDefinitionNames);
                }
                else
                {
                    pageMenuDefinitionNames = Collections.synchronizedSet(new HashSet<String>(0));
                }
            }
            return pageMenuDefinitionNames;
        }
        return null;
    }
    
    /**
     * mergeMenuDefinitionLocatorNames - merge menu locator names
     * 
     * @param locators menu definition locators
     * @param excludeNames excluded names set
     * @param names merged names set
     */
    private void mergeMenuDefinitionLocatorNames(List<SiteViewMenuDefinitionLocator> locators, Set<String> excludeNames, Set<String> names)
    {
        // merge menu definition locator names
        if (locators != null)
        {
            for (SiteViewMenuDefinitionLocator locator : locators)
            {
                String definitionName = locator.getName();
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
    public Menu getMenu(String name, Set<String> names) throws NodeNotFoundException
    {
        if (name != null)
        {
            // menus only available for concrete page requests
            if (isConcretePage())
            {
                BaseConcretePageElement page = (BaseConcretePageElement)getPageOrTemplate();            
                PageTemplate pageTemplate = getPageTemplate();
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
                        MenuImpl menu = menuDefinitionLocatorCache.get(locator);
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
                    MenuImpl menu = new MenuImpl(locator.getMenuDefinition(), locator.getPath(), this, names);

                    // determine whether menu definition locator is
                    // relative/request, based on hidden page, or
                    // absolute/session cachable and cache accordingly
                    if (page.isHidden() || menu.isElementRelative())
                    {
                        // cache relative menu for request
                        if (menuDefinitionLocatorCache == null)
                        {
                            menuDefinitionLocatorCache = Collections.synchronizedMap(new HashMap<SiteViewMenuDefinitionLocator,MenuImpl>(8));
                        }
                        menuDefinitionLocatorCache.put(locator, menu);
                    }
                    else
                    {
                        // cache absolute menu for session
                        if (sessionContext.getMenuDefinitionLocatorCache() == null)
                        {
                            sessionContext.setMenuDefinitionLocatorCache(Collections.synchronizedMap(new HashMap<SiteViewMenuDefinitionLocator,MenuImpl>(8)));
                        }
                        sessionContext.getMenuDefinitionLocatorCache().put(locator, menu);
                    }

                    // return new cached menu
                    return menu;
                }
            }
        }
        return null;
    }
    
    /**
     * getUserFolderPath - return primary concrete root user folder path
     *
     * @return user folder path or null
     */
    public String getUserFolderPath()
    {
        return sessionContext.getUserFolderPath(requestProfileLocators, requestUserPrincipal, forceReservedVisible);
    }

    /**
     * getBaseFolderPath - return primary concrete root base folder path
     *
     * @return base folder path or null
     */
    public String getBaseFolderPath()
    {
        return sessionContext.getBaseFolderPath(requestProfileLocators, requestUserPrincipal, forceReservedVisible);
    }

    /**
     * filterHiddenNodes - utility to filter hidden node views out of node sets
     *
     * @param nodes view node set to filter
     * @return input or filtered view node set
     */
    private static NodeSet filterHiddenNodes(NodeSet nodes)
    {
        if ((nodes != null) && !nodes.isEmpty())
        {
            // filter node views in node set
            List<Node> filteredNodes = null;
            for (Node node : nodes)
            {
                // test hidden status of individual node views
                if (node.isHidden())
                {
                    // if not copying, create new node set
                    // and copy preceding node views
                    if (filteredNodes == null)
                    {
                        filteredNodes = new ArrayList<Node>(nodes.size());
                        for (Node copyNode : nodes)
                        {
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
                    // if copying, copy node view to filtered set
                    filteredNodes.add(node);
                }
            }

            // return filteredNodes node views if generated
            // in new immutable view node set
            if (filteredNodes != null)
            {
                return new NodeSetImpl(filteredNodes);
            }
        }
        return nodes;
    }
}
