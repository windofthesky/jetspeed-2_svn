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
import org.apache.jetspeed.om.folder.DocumentSet;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PropertyImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.AbstractNode;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.NodeSetImpl;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfiledPageContext;

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
        public List profiledFolders;
        public NodeSet nestedDocumentSets;

        public CacheablePageContext(Page page, Folder folder, NodeSet siblingPages, Folder parentFolder, NodeSet siblingFolders, NodeSet rootLinks, NodeSet documentSets, Map documentSetNames, Map documentSetNodeSets, List profiledFolders)
        {
            this.page = page;
            this.folder = folder;
            this.siblingPages = siblingPages;
            this.parentFolder = parentFolder;
            this.siblingFolders = siblingFolders;
            this.profiledFolders = profiledFolders;
            this.rootLinks = rootLinks;
            this.documentSets = documentSets;
            this.documentSetNames = documentSetNames;
            this.documentSetNodeSets = documentSetNodeSets;

            // compute nested document sets nodes set: these
            // do not appear in the document set but are mapped
            // in docuemnt set names/node sets
            if ((this.documentSetNames != null) && (this.documentSets != null) && (this.documentSetNames.size() > this.documentSets.size()))
            {
                Iterator mappedIter = this.documentSetNames.entrySet().iterator();
                while (mappedIter.hasNext())
                {
                    Map.Entry mappedEntry = (Map.Entry)mappedIter.next();
                    DocumentSet documentSet = (DocumentSet)mappedEntry.getKey();
                    if (!this.documentSets.contains(documentSet))
                    {
                        if (this.nestedDocumentSets == null)
                        {
                            this.nestedDocumentSets = new NodeSetImpl(null);
                        }
                        this.nestedDocumentSets.add(documentSet);
                    }
                }
            }

            // debug profiled page context elements
            if (log.isDebugEnabled())
            {
                log.debug("CacheablePageContext(), folder = " + this.folder + ", url = " + this.folder.getUrl());
                log.debug("CacheablePageContext(), page = " + this.page + ", url = " + this.page.getUrl());
                if ((this.siblingPages != null) && !this.siblingPages.isEmpty())
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
                if ((this.siblingFolders != null) && !this.siblingFolders.isEmpty())
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
                if ((this.rootLinks != null) && !this.rootLinks.isEmpty())
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
                if ((this.documentSets != null) && !this.documentSets.isEmpty() &&
                    (this.documentSetNames != null) && !this.documentSetNames.isEmpty() &&
                    (this.documentSetNodeSets != null) && !this.documentSetNodeSets.isEmpty())
                {
                    Iterator debugIter = this.documentSets.iterator();
                    while (debugIter.hasNext())
                    {
                        log.debug("CacheablePageContext(), " + debugDocumentSetMessage((DocumentSet)debugIter.next()));
                    }
                    if ((this.nestedDocumentSets != null) && !this.nestedDocumentSets.isEmpty())
                    {
                        debugIter = this.nestedDocumentSets.iterator();
                        while (debugIter.hasNext())
                        {
                            log.debug("CacheablePageContext(), nested " + debugDocumentSetMessage((DocumentSet)debugIter.next()));
                        }
                    }
                }
                else
                    log.debug("CacheablePageContext(), documentSets/documentSetNames/documentSetNodeSets = null/empty");
                if ((this.profiledFolders != null) && !this.profiledFolders.isEmpty())
                {
                    Iterator debugIter = this.profiledFolders.iterator();
                    while (debugIter.hasNext())
                    {
                        Folder debug = (Folder) debugIter.next();
                        log.debug("CacheablePageContext(), profiledFolder = " + debug + ", url = " + debug.getUrl());
                    }
                }
                else
                    log.debug("CacheablePageContext(), profiledFolders = null/empty");
            }
        }

        private String debugDocumentSetMessage(DocumentSet debug)
        {
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
            return debugMessage;
        }
    }

    protected void populateProfiledPageContext(CacheablePageContext cachedPageContext, ProfiledPageContext pageContext)
    {
        // populate supplied page context object while checking
        // page and folder visibility using access permissions
        // and/or constraints; by definition, page visibility implies
        // folder and parent folder visibility
        pageContext.setPage((Page)checkVisibility((AbstractNode)cachedPageContext.page, cachedPageContext.profiledFolders));
        pageContext.setFolder(cachedPageContext.folder);
        pageContext.setSiblingPages(checkVisibility(cachedPageContext.siblingPages, cachedPageContext.profiledFolders));
        pageContext.setParentFolder(cachedPageContext.parentFolder);
        pageContext.setSiblingFolders(checkVisibility(cachedPageContext.siblingFolders, cachedPageContext.profiledFolders));
        pageContext.setRootLinks(checkVisibility(cachedPageContext.rootLinks, cachedPageContext.profiledFolders));
        if (cachedPageContext.documentSets != null)
        {
            Iterator documentSetIter = checkVisibility(cachedPageContext.documentSets, cachedPageContext.profiledFolders).iterator();
            while (documentSetIter.hasNext())
            {
                // populate root document set
                DocumentSet documentSet = (DocumentSet)documentSetIter.next();
                String documentSetName = (String)cachedPageContext.documentSetNames.get(documentSet);
                NodeSet documentSetNodes = checkVisibility((NodeSet)cachedPageContext.documentSetNodeSets.get(documentSet), cachedPageContext.profiledFolders);
                pageContext.setDocumentSet(documentSetName, documentSet, documentSetNodes);
            }
            if (cachedPageContext.nestedDocumentSets != null)
            {
                Iterator nestedDocumentSetIter = checkVisibility(cachedPageContext.nestedDocumentSets, cachedPageContext.profiledFolders).iterator();
                while (nestedDocumentSetIter.hasNext())
                {
                    // populate nested document set
                    DocumentSet documentSet = (DocumentSet)nestedDocumentSetIter.next();
                    String documentSetName = (String)cachedPageContext.documentSetNames.get(documentSet);
                    NodeSet documentSetNodes = checkVisibility((NodeSet)cachedPageContext.documentSetNodeSets.get(documentSet), cachedPageContext.profiledFolders);
                    pageContext.setNestedDocumentSet(documentSetName, documentSet, documentSetNodes);
                }
            }
        }
    }

    protected AbstractNode checkVisibility(AbstractNode node, List profiledFolders) throws SecurityException
    {
        // check access constraints/permissions of node
        // to determine visibility, (hidden status already
        // tested by profiler); throws SecurityExceptiond
        // if view access not granted
        if (node != null)
        {
            if (node instanceof Folder)
            {
                // visibility of folder determined by visibility
                // of pages in folder; accessible pages in subfolders
                // are not checked since navigating into a folder
                // with no visible pages is not permitted; finally,
                // all equivalent profiled folders must be checked
                // if available
                boolean securityException = false;
                try
                {
                    Iterator pagesIter = ((FolderImpl) node).getPages(false).iterator();
                    while (pagesIter.hasNext())
                    {
                        try
                        {
                            AbstractNode page = (AbstractNode)pagesIter.next();
                            if (!page.isHidden())
                            {
                                checkVisibility(page, profiledFolders);
                                return node;
                            }
                        }
                        catch (SecurityException se)
                        {
                            securityException = true;
                        }
                    }
                }
                catch (NodeException ne)
                {
                }
                if (profiledFolders != null)
                {
                    String folderUrl = node.getUrl();
                    Iterator foldersIter = profiledFolders.iterator();
                    while (foldersIter.hasNext())
                    {
                        FolderImpl profiledFolder = (FolderImpl)foldersIter.next();
                        if ((profiledFolder != node) && folderUrl.equals(profiledFolder.getUrl()))
                        {
                            try
                            {
                                Iterator pagesIter = profiledFolder.getPages(false).iterator();
                                while (pagesIter.hasNext())
                                {
                                    try
                                    {
                                        AbstractNode page = (AbstractNode)pagesIter.next();
                                        if (!page.isHidden())
                                        {
                                            checkVisibility(page, profiledFolders);
                                            return node;
                                        }
                                    }
                                    catch (SecurityException se)
                                    {
                                        securityException = true;
                                    }
                                }
                            }
                            catch (NodeException ne)
                            {
                            }
                        }
                    }
                }

                // no visible pages found in folder or equivalent
                // folder: throw security or runtime exception
                if (securityException)
                {
                    throw new SecurityException("AbstractPageManager.checkVisibility(): Access for " + SecuredResource.VIEW_ACTION + " not permitted to any folder page.");
                }
                throw new RuntimeException("AbstractPageManager.checkVisibility(): Empty folders not visible.");
            }
            else
            {
                // simple access test for document
                node.checkAccess(SecuredResource.VIEW_ACTION);
            }
        }
        return node;
    }

    protected NodeSet checkVisibility(NodeSet nodes, List profiledFolders)
    {
        // check access constraints/permissions of nodes
        // to determine visibility; filter nodes accordingly
        if ((nodes != null) && !nodes.isEmpty())
        {
            NodeSetImpl filteredNodes = null;
            Iterator checkAccessIter = nodes.iterator();
            while (checkAccessIter.hasNext())
            {
                AbstractNode node = (AbstractNode)checkAccessIter.next();
                try
                {
                    // check visibility
                    checkVisibility(node, profiledFolders);

                    // add to filteredNodes nodes if copying
                    if (filteredNodes != null)
                    {
                        // permitted, add to filteredNodes nodes
                        filteredNodes.add(node);
                    }
                }
                catch (RuntimeException rte)
                {
                    // create filteredNodes nodes if not already copying
                    if (filteredNodes == null)
                    {
                        // not permitted, copy previously permitted nodes
                        // to new filteredNodes node set with same comparator
                        filteredNodes = new NodeSetImpl(null, ((NodeSetImpl)nodes).getComparator());
                        Iterator copyIter = nodes.iterator();
                        while (copyIter.hasNext())
                        {
                            AbstractNode copyNode = (AbstractNode)copyIter.next();
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

            // return filteredNodes nodes if generated
            if (filteredNodes != null)
            {
                return filteredNodes;
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
