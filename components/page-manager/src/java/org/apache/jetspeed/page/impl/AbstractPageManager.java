/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.page.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.DocumentSet;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PropertyImpl;
import org.apache.jetspeed.page.document.AbstractNode;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.NodeSetImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.ProfiledPageContext;
import org.apache.jetspeed.profiler.ProfileLocator;

/**
 * AbstractPageManagerService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractPageManager 
    implements PageManager    
{
    private final static Log log = LogFactory.getLog(AbstractPageManager.class);
    
    protected Class fragmentClass = FragmentImpl.class;
    protected Class pageClass = PageImpl.class;
    protected Class propertyClass = PropertyImpl.class;
    protected IdGenerator generator = null;

    private boolean permissionsEnabled;

    private boolean constraintsEnabled;

    public AbstractPageManager(IdGenerator generator, boolean permissionsEnabled, boolean constraintsEnabled)
    {    
        this.generator = generator;
        this.permissionsEnabled = permissionsEnabled;
        this.constraintsEnabled = constraintsEnabled;
    }
    
    public AbstractPageManager(IdGenerator generator, boolean permissionsEnabled, boolean constraintsEnabled, List modelClasses)
    {
        this.generator = generator;     
        if (modelClasses.size() > 0)
        {
            this.fragmentClass = (Class)modelClasses.get(0);
            if (modelClasses.size() > 1)
            {
                this.pageClass  = (Class)modelClasses.get(1);
                if (modelClasses.size() > 2)
                {
                    this.propertyClass  = (Class)modelClasses.get(2);
                }                
            }
        }                                 
        this.permissionsEnabled = permissionsEnabled;
        this.constraintsEnabled = constraintsEnabled;
    }
    
    /**
     * <p>
     * getPermissionsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.page.PageManager#getPermissionsEnabled()
     * @return
     */
    public boolean getPermissionsEnabled()
    {
        return permissionsEnabled;
    }

    /**
     * <p>
     * getConstraintsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.page.PageManager#getConstraintsEnabled()
     * @return
     */
    public boolean getConstraintsEnabled()
    {
        return constraintsEnabled;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPage()
     */
    public Page newPage()
    {
        Page page = null;
        try
        {
            // factory create the page
            page = (Page)createObject(this.pageClass);            
            page.setId(generator.getNextPeid());
            
            // create the default fragment
            Fragment fragment = (Fragment)createObject(this.fragmentClass);
            fragment.setId(generator.getNextPeid());
            fragment.setType(Fragment.LAYOUT);
            page.setRootFragment(fragment);            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page object for " + this.pageClass;
            log.error(message, e);
        }
        return page;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFragment()
     */
    public Fragment newFragment()
    {
        Fragment fragment = null;
        try
        {
            fragment = (Fragment)createObject(this.fragmentClass);
            fragment.setId(generator.getNextPeid());
            fragment.setType(Fragment.LAYOUT);          
            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page object for " + this.pageClass;
            log.error(message, e);
            // throw new JetspeedException(message, e);
        }
        return fragment;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newProperty()
     */
    public Property newProperty()
    {
        Property property = null;
        try
        {
            property = (Property)createObject(this.propertyClass);
            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create fragment-property object for " + this.propertyClass;
            log.error(message, e);
            // throw new JetspeedException(message, e);
        }
        return property;        
    }

    public Object createObject(Class classe)
    {
        Object object = null;
        try
        {
            object = classe.newInstance();
        }
        catch (Exception e)
        {
            log.error("Factory failed to create object: " + classe.getName(), e);            
        }
        
        return object;        
    }
    
    protected class CacheablePageContext
    {
        public Page page;
        public Folder folder;
        public NodeSet siblingPages;
        public Folder parentFolder;
        public NodeSet siblingFolders;
        public NodeSet rootLinks;
        public NodeSet documentSets;
        public Map documentSetNames;
        public Map documentSetNodeSets;

        public CacheablePageContext(Page page, Folder folder, NodeSet siblingPages, Folder parentFolder, NodeSet siblingFolders, NodeSet rootLinks, NodeSet documentSets, Map documentSetNames, Map documentSetNodeSets)
        {
            this.page = page;
            this.folder = folder;
            this.siblingPages = siblingPages;
            this.parentFolder = parentFolder;
            this.siblingFolders = siblingFolders;
            this.rootLinks = rootLinks;
            this.documentSets = documentSets;
            this.documentSetNames = documentSetNames;
            this.documentSetNodeSets = documentSetNodeSets;

            // debug profiled page context elements
            if (log.isDebugEnabled())
            {
                log.debug("CacheablePageContext(), folder = " + this.folder + ", url = " + this.folder.getUrl());
                log.debug("CacheablePageContext(), page = " + this.page + ", url = " + this.page.getUrl());
                if ((this.siblingPages != null) && (this.siblingPages.size() > 0))
                {
                    Iterator debugIter = this.siblingPages.iterator();
                    while (debugIter.hasNext())
                    {
                        Page debug = (Page) debugIter.next();
                        log.debug("CacheablePageContext(), siblingPage = " + debug + ", url = " + debug.getUrl());
                    }
                }
                else
                    log.debug("CacheablePageContext(), siblingPages = null/empty");
                log.debug("CacheablePageContext(), parentFolder = " + this.parentFolder + ", url = " + ((this.parentFolder != null) ? this.parentFolder.getUrl() : "null"));
                if ((this.siblingFolders != null) && (this.siblingFolders.size() > 0))
                {
                    Iterator debugIter = this.siblingFolders.iterator();
                    while (debugIter.hasNext())
                    {
                        Folder debug = (Folder) debugIter.next();
                        log.debug("CacheablePageContext(), siblingFolder = " + debug + ", url = " + debug.getUrl());
                    }
                }
                else
                    log.debug("CacheablePageContext(), siblingFolders = null/empty");
                if ((this.rootLinks != null) && (this.rootLinks.size() > 0))
                {
                    Iterator debugIter = this.rootLinks.iterator();
                    while (debugIter.hasNext())
                    {
                        Link debug = (Link) debugIter.next();
                        log.debug("CacheablePageContext(), rootLink = " + debug + ", url = " + debug.getUrl());
                    }
                }
                else
                    log.debug("CacheablePageContext(), rootLinks = null/empty");
                if ((this.documentSets != null) && (this.documentSets.size() > 0) &&
                    (this.documentSetNames != null) && (this.documentSetNames.size() > 0) &&
                    (this.documentSetNodeSets != null) && (this.documentSetNodeSets.size() > 0))
                {
                    Iterator debugIter = this.documentSets.iterator();
                    while (debugIter.hasNext())
                    {
                        DocumentSet debug = (DocumentSet) debugIter.next();
                        String debugName = (String) this.documentSetNames.get(debug);
                        NodeSet debugNodes = (NodeSet) this.documentSetNodeSets.get(debug);
                        String debugMessage = "document set " + debug + ", name = " + debugName + ", nodes = {";
                        Iterator nodesIter = debugNodes.iterator();
                        if (nodesIter.hasNext())
                        {
                            debugMessage += ((Node) nodesIter.next()).getUrl();
                        }
                        while (nodesIter.hasNext())
                        {
                            debugMessage += ", " + ((Node) nodesIter.next()).getUrl();
                        }
                        debugMessage += "}, url = " + debug.getUrl();
                        log.debug("CacheablePageContext(), " + debugMessage);
                    }
                }
                else
                    log.debug("CacheablePageContext(), documentSets/documentSetNames/documentSetNodeSets = null/empty");
            }
        }
    }

    protected void populateProfiledPageContext(CacheablePageContext cachedPageContext, ProfiledPageContext pageContext)
    {
        // populate supplied page context object while checking
        // page and folder access permissions and/or constraints
        pageContext.setPage((Page)checkAccess((AbstractNode)cachedPageContext.page, SecuredResource.VIEW_ACTION, true));
        pageContext.setFolder((Folder)checkAccess((AbstractNode)cachedPageContext.folder, SecuredResource.VIEW_ACTION, false));
        pageContext.setSiblingPages(checkAccess(cachedPageContext.siblingPages, SecuredResource.VIEW_ACTION));
        pageContext.setParentFolder((Folder)checkAccess((AbstractNode)cachedPageContext.parentFolder, SecuredResource.VIEW_ACTION, false));
        pageContext.setSiblingFolders(checkAccess(cachedPageContext.siblingFolders, SecuredResource.VIEW_ACTION));
        pageContext.setRootLinks(checkAccess(cachedPageContext.rootLinks, SecuredResource.VIEW_ACTION));
        if (cachedPageContext.documentSets != null)
        {
            Iterator documentSetIter = checkAccess(cachedPageContext.documentSets, SecuredResource.VIEW_ACTION).iterator();
            while (documentSetIter.hasNext())
            {
                DocumentSet documentSet = (DocumentSet) documentSetIter.next();
                String documentSetName = (String) cachedPageContext.documentSetNames.get(documentSet);
                NodeSet documentSetNodes = checkAccess((NodeSet) cachedPageContext.documentSetNodeSets.get(documentSet), SecuredResource.VIEW_ACTION);
                pageContext.setDocumentSet(documentSetName, documentSet, documentSetNodes);
            }
        }
    }

    protected AbstractNode checkAccess(AbstractNode node, String actions, boolean throwException)
    {
        if (node != null)
        {
            // propagate thrown SecurityExceptions
            try
            {
                // check access constraints/permissions of node
                node.checkAccess(actions);
            }
            catch (SecurityException se)
            {
                log.debug("checkAccess(): Access denied to folder or document " + node);
                if (!throwException)
                {
                    return null;
                }
                throw se;
            }
        }
        return node;
    }

    protected NodeSet checkAccess(NodeSet nodes, String actions)
    {
        if ((nodes != null) && (nodes.size() > 0))
        {
            // check permissions and constraints, filter nodes as required
            NodeSet filteredNodes = null;
            Iterator checkAccessIter = nodes.iterator();
            while (checkAccessIter.hasNext())
            {
                AbstractNode node = (AbstractNode)checkAccessIter.next();
                try
                {
                    // check access
                    checkAccess(node, actions, true);

                    // add to filtered nodes if copying
                    if (filteredNodes != null)
                    {
                        // permitted, add to filtered nodes
                        filteredNodes.add(node);
                    }
                }
                catch (SecurityException se)
                {
                    // create filtered nodes if not already copying
                    if (filteredNodes == null)
                    {
                        // not permitted, copy previously permitted nodes
                        // to new filtered node set with same comparator
                        filteredNodes = new NodeSetImpl(null, ((NodeSetImpl)nodes).getComparator());;
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
            }

            // return filtered nodes if generated
            if (filteredNodes != null)
            {
                nodes = filteredNodes;
            }
        }
        return nodes;
    }

    protected ProfileLocator selectPageProfileLocator(Map profileLocators)
    {
        // select page profile locator from session/principal profile locators
        return (ProfileLocator) profileLocators.get(ProfileLocator.PAGE_LOCATOR);
    }

    protected List selectAlternatePageProfileLocators(Map profileLocators)
    {
        // select alternate page profile locators from session/principal profile locators
        List locators = new ArrayList(4);
        Iterator locatorsIter = profileLocators.entrySet().iterator();
        while (locatorsIter.hasNext())
        {
            Map.Entry locatorEntry = (Map.Entry) locatorsIter.next();
            if (! ((String) locatorEntry.getKey()).equals(ProfileLocator.PAGE_LOCATOR))
            {
                locators.add(locatorEntry.getValue());
            }
        }
        return locators;
    }

    protected ProfileLocator selectNavigationProfileLocator(String profileLocatorName, Map profileLocators)
    {
        // select navigation profile locator from session/principal profile locators
        ProfileLocator locator = null;
        if (profileLocatorName != null)
        {
            locator = (ProfileLocator) profileLocators.get(profileLocatorName);
        }
        else
        {
            locator = (ProfileLocator) profileLocators.get(ProfileLocator.DOCSET_LOCATOR);
            if (locator == null)
            {
                locator = (ProfileLocator) profileLocators.get(ProfileLocator.PAGE_LOCATOR);
            }
        }
        return locator;
    }
}
