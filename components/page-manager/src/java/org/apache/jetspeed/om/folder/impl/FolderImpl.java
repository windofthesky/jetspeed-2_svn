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
package org.apache.jetspeed.om.folder.impl;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.impl.LinkImpl;
import org.apache.jetspeed.om.page.impl.PageImpl;
import org.apache.jetspeed.om.page.impl.PageSecurityImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.impl.NodeImpl;
import org.apache.jetspeed.page.document.impl.NodeSetImpl;
import org.apache.jetspeed.security.FolderPermission;
import org.apache.ojb.broker.core.proxy.ProxyHelper;

/**
 * FolderImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FolderImpl extends NodeImpl implements Folder
{
    private String defaultPage;
    private String skin;
    private String defaultLayoutDecorator;
    private String defaultPortletDecorator;
    private List orders;
    private List menus;

    private PageManager pageManager;
    private List folders;
    private boolean foldersCached;
    private List pages;
    private boolean pagesCached;
    private List links;
    private boolean linksCached;
    private PageSecurityImpl pageSecurity;
    private boolean pageSecurityCached;
    private List all;
    private boolean allCached;
    private FolderOrderList documentOrder;
    private boolean documentOrderComparatorValid;
    private Comparator documentOrderComparator;
    private NodeSet foldersNodeSet;
    private NodeSet pagesNodeSet;
    private NodeSet linksNodeSet;
    private NodeSet allNodeSet;
    private FolderMenuDefinitionList menuDefinitions;

    public FolderImpl()
    {
        super(new FolderSecurityConstraintsImpl());
    }

    /**
     * accessFolderOrders
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessFolderOrders()
    {
        // create initial collection if necessary
        if (orders == null)
        {
            orders = new ArrayList();
        }
        return orders;
    }

    /**
     * accessMenus
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessMenus()
    {
        // create initial collection if necessary
        if (menus == null)
        {
            menus = new ArrayList(2);
        }
        return menus;
    }

    /**
     * setPageManager
     *
     * Infuses PageManager for use by this folder instance.
     *
     * @param pageManager page manager that manages this folder instance
     */
    public void setPageManager(PageManager pageManager)
    {
        this.pageManager = pageManager;
    }

    /**
     * accessFolders
     *
     * Access folders transient cache collection for use by PageManager.
     *
     * @return folders collection
     */
    public List accessFolders()
    {
        // create initial collection if necessary
        if (folders == null)
        {
            folders = new ArrayList();
        }
        return folders;
    }

    /**
     * resetFolders
     *
     * Reset folders transient caches for use by PageManager.
     *
     * @param cached set cached state for folders
     */
    public void resetFolders(boolean cached)
    {
        // save cached state
        foldersCached = cached;
        allCached = false;

        // update node caches
        if (!cached)
        {
            accessFolders().clear();
        }
        accessAll().clear();

        // reset cached node sets
        foldersNodeSet = null;
        allNodeSet = null;
    }

    /**
     * accessPages
     *
     * Access pages transient cache collection for use by PageManager.
     *
     * @return pages collection
     */
    public List accessPages()
    {
        // create initial collection if necessary
        if (pages == null)
        {
            pages = new ArrayList();
        }
        return pages;
    }

    /**
     * resetPages
     *
     * Reset pages transient caches for use by PageManager.
     *
     * @param cached set cached state for pages
     */
    public void resetPages(boolean cached)
    {
        // save cached state
        pagesCached = cached;
        allCached = false;

        // update node caches
        if (!cached)
        {
            accessPages().clear();
        }
        accessAll().clear();

        // reset cached node sets
        pagesNodeSet = null;
        allNodeSet = null;
    }

    /**
     * accessLinks
     *
     * Access links transient cache collection for use by PageManager.
     *
     * @return links collection
     */
    public List accessLinks()
    {
        // create initial collection if necessary
        if (links == null)
        {
            links = new ArrayList();
        }
        return links;
    }

    /**
     * resetLinks
     *
     * Reset links transient caches for use by PageManager.
     *
     * @param cached set cached state for links
     */
    public void resetLinks(boolean cached)
    {
        // save cached state
        linksCached = cached;
        allCached = false;

        // update node caches
        if (!cached)
        {
            accessLinks().clear();
        }
        accessAll().clear();

        // reset cached node sets
        linksNodeSet = null;
        allNodeSet = null;
    }

    /**
     * accessPageSecurity
     *
     * Access pageSecurity cached instance for use by PageManager.
     *
     * @return pageSecurity instance
     */
    public PageSecurityImpl accessPageSecurity()
    {
        return pageSecurity;
    }

    /**
     * resetPageSecurity
     *
     * Reset pageSecurity transient cache instance for use by PageManager.
     *
     * @param newPageSecurty cached page security instance.
     * @param cached set cached state for page security
     */
    public void resetPageSecurity(PageSecurityImpl newPageSecurity, boolean cached)
    {
        // save cached state
        pageSecurity = newPageSecurity;
        pageSecurityCached = cached;
        allCached = false;

        // update node caches
        accessAll().clear();

        // reset cached node sets
        allNodeSet = null;
    }

    /**
     * accessAll
     *
     * Access all transient cache collection for use by PageManager.
     *
     * @return all collection
     */
    public List accessAll()
    {
        // create initial collection if necessary
        if (all == null)
        {
            all = new ArrayList();
        }
        return all;
    }

    /**
     * resetAll
     *
     * Reset all transient caches for use by PageManager.
     *
     * @param cached set cached state for all
     */
    public void resetAll(boolean cached)
    {
        // save cached state
        allCached = cached;
        foldersCached = cached;
        pagesCached = cached;
        linksCached = cached;
        pageSecurityCached = cached;

        // update node caches
        accessFolders().clear();
        accessPages().clear();
        accessLinks().clear();
        pageSecurity = null;
        if (cached)
        {
            // populate node caches
            Iterator nodeIter = accessAll().iterator();
            while (nodeIter.hasNext())
            {
                Node node = (Node)nodeIter.next();
                if (node instanceof PageImpl)
                {
                    pages.add(node);
                }
                else if (node instanceof FolderImpl)
                {
                    folders.add(node);
                }
                else if (node instanceof LinkImpl)
                {
                    links.add(node);
                }
                else if (node instanceof PageSecurityImpl)
                {
                    pageSecurity = (PageSecurityImpl)node;
                }
            }
        }
        else
        {
            accessAll().clear();
        }

        // reset cached node sets
        allNodeSet = null;
        foldersNodeSet = null;
        pagesNodeSet = null;
        linksNodeSet = null;
    }

    /**
     * createDocumentOrderComparator
     *
     * @return document order comparator
     */
    private Comparator createDocumentOrderComparator()
    {
        if (!documentOrderComparatorValid)
        {
            documentOrderComparatorValid = true;
            // return null if no document order exists;
            // (null implies natural ordering by name)
            final List documentOrder = getDocumentOrder();
            if ((documentOrder == null) || documentOrder.isEmpty())
            {
                return null;
            }
            // create new document order comparator
            documentOrderComparator = new Comparator()
                {
                    /* (non-Javadoc)
                     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                     */
                    public int compare(Object o1, Object o2)
                    {
                        // Compare node names using document order;
                        // use indicies as names if found in document
                        // order to force explicitly ordered items
                        // ahead of unordered items
                        String name1 = (String)o1;
                        int index1 = documentOrder.indexOf(name1);
                        if (index1 >= 0)
                        {
                            // use order index as name1
                            name1 = String.valueOf(index1);
                        }
                        String name2 = (String)o2;
                        int index2 = documentOrder.indexOf(name2);
                        if (index2 >= 0)
                        {
                            // use order index as name2
                            name2 = String.valueOf(index2);
                            if (index1 >= 0)
                            {
                                // pad order indicies for numeric string compare
                                while (name1.length() != name2.length())
                                {
                                    if (name1.length() < name2.length())
                                    {
                                        name1 = "0" + name1;
                                    }
                                    else
                                    {
                                        name2 = "0" + name2;
                                    }
                                }
                            }
                        }
                        // compare names and/or indicies
                        return name1.compareTo(name2);                        
                    }
                };
        }
        return documentOrderComparator;
    }

    /**
     * clearDocumentOrderComparator
     */
    void clearDocumentOrderComparator()
    {
        // clear node set ordering
        documentOrderComparatorValid = false;
        documentOrderComparator = null;
        // clear previously cached node sets
        allNodeSet = null;
        foldersNodeSet = null;
        pagesNodeSet = null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.impl.NodeImpl#newPageMetadata(java.util.Collection)
     */
    public PageMetadataImpl newPageMetadata(Collection fields)
    {
        PageMetadataImpl pageMetadata = new PageMetadataImpl(FolderMetadataLocalizedFieldImpl.class);
        pageMetadata.setFields(fields);
        return pageMetadata;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getEffectivePageSecurity()
     */
    public PageSecurity getEffectivePageSecurity()
    {
        // return page security instance if available
        if (!pageSecurityCached)
        {
            // use PageManager to get and cache page security
            // instance for this folder
            try
            {
                return pageManager.getPageSecurity(this);
            }
            catch (NodeException ne)
            {
            }
        }
        else if (pageSecurity != null)
        {
            return pageSecurity;
        }

        // delegate to real parent folder implementation
        FolderImpl parentFolderImpl = (FolderImpl)ProxyHelper.getRealObject(getParent());
        if (parentFolderImpl != null)
        {
            return parentFolderImpl.getEffectivePageSecurity();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#checkPermissions(java.lang.String, int, boolean, boolean)
     */
    public void checkPermissions(String path, int mask, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check granted folder permissions unless the check is
        // to be skipped due to explicity granted access
        if (!checkParentsOnly)
        {
            FolderPermission permission = new FolderPermission(path, mask);
            AccessController.checkPermission(permission);
        }

        // if not checking node only, recursively check
        // all parent permissions in hierarchy
        if (!checkNodeOnly)
        {
            FolderImpl parentFolderImpl = (FolderImpl)ProxyHelper.getRealObject(getParent());
            if (parentFolderImpl != null)
            {
                parentFolderImpl.checkPermissions(mask, false, false);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getTitle()
     */
    public String getTitle()
    {
        // default title to folder name
        String title = super.getTitle();
        if (title == null)
        {
            title = defaultTitleFromName();
            setTitle(title);
        }
        return title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        this.skin = skinName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getEffectiveDefaultDecorator(java.lang.String)
     */
    public String getEffectiveDefaultDecorator(String fragmentType)
    {
        // get locally defined decorator
        String decorator = getDefaultDecorator(fragmentType);
        if (decorator == null)
        {
            // delegate to parent folder
            Folder parentFolder = (Folder)ProxyHelper.getRealObject(getParent());
            if (parentFolder != null)
            {
                return parentFolder.getEffectiveDefaultDecorator(fragmentType);
            }
        }
        return decorator;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultDecorator(java.lang.String)
     */
    public String getDefaultDecorator(String fragmentType)
    {
        // retrieve supported decorator types
        if (fragmentType != null)
        {
            if (fragmentType.equals(Fragment.LAYOUT))
            {
                return defaultLayoutDecorator; 
            }
            if (fragmentType.equals(Fragment.PORTLET))
            {
                return defaultPortletDecorator; 
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultDecorator(java.lang.String,java.lang.String)
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType)
    {
        // save supported decorator types
        if (fragmentType != null)
        {
            if (fragmentType.equals(Fragment.LAYOUT))
            {
                defaultLayoutDecorator = decoratorName; 
            }
            if (fragmentType.equals(Fragment.PORTLET))
            {
                defaultPortletDecorator = decoratorName; 
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDocumentOrder()
     */
    public List getDocumentOrder()
    {
        // return mutable document order list
        // by using list wrapper to manage sort
        // order and element uniqueness
        if (documentOrder == null)
        {
            documentOrder = new FolderOrderList(this);
        }
        return documentOrder;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setDocumentOrder(java.util.List)
     */
    public void setDocumentOrder(List docNames)
    {
        // set document order using ordered document
        // names by replacing existing entries with
        // new elements if new collection is specified
        List documentOrder = getDocumentOrder();
        if (docNames != documentOrder)
        {
            // replace all document order names
            documentOrder.clear();
            if (docNames != null)
            {
                documentOrder.addAll(docNames);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultPage()
     */
    public String getDefaultPage()
    {
        return defaultPage;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultPage(java.lang.String)
     */
    public void setDefaultPage(String defaultPage)
    {
        this.defaultPage = defaultPage;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getFolders()
     */
    public NodeSet getFolders() throws FolderNotFoundException, DocumentException
    {
        // get folders collection
        if (!foldersCached)
        {
            // use PageManager to get and cache folders
            // collection for this folder
            return pageManager.getFolders(this);
        }

        // return nodes with view access
        return filterNodeSetByAccess(getFoldersNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getFolder(java.lang.String)
     */
    public Folder getFolder(String name) throws FolderNotFoundException, DocumentException
    {
        // get folder instance if folders collection not available
        if (!foldersCached)
        {
            // use PageManager to get folder instance without
            // caching the folders collection for this folder
            return pageManager.getFolder(this, name);
        }

        // select folder by name from cached folders collection
        Folder folder = (Folder)getFoldersNodeSet().get(name);
        if (folder == null)
        {
            throw new FolderNotFoundException("Folder not found: " + name);
        }

        // check for view access on folder
        folder.checkAccess(JetspeedActions.VIEW);

        return folder;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPages()
     */
    public NodeSet getPages() throws NodeException
    {
        // get pages collection
        if (!pagesCached)
        {
            // use PageManager to get and cache pages
            // collection for this folder
            return pageManager.getPages(this);
        }

        // return nodes with view access
        return filterNodeSetByAccess(getPagesNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPage(java.lang.String)
     */
    public Page getPage(String name) throws PageNotFoundException, NodeException
    {
        // get page instance if pages collection not available
        if (!pagesCached)
        {
            // use PageManager to get page instance without
            // caching the pages collection for this folder
            return pageManager.getPage(this, name);
        }

        // select page by name from cached pages collection
        Page page = (Page)getPagesNodeSet().get(name);
        if (page == null)
        {
            throw new PageNotFoundException("Page not found: " + name);
        }

        // check for view access on page
        page.checkAccess(JetspeedActions.VIEW);

        return page;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getLinks()
     */
    public NodeSet getLinks() throws NodeException
    {
        // get links collection
        if (!linksCached)
        {
            // use PageManager to get and cache links
            // collection for this folder
            return pageManager.getLinks(this);
        }

        // return nodes with view access
        return filterNodeSetByAccess(getLinksNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getLink(java.lang.String)
     */
    public Link getLink(String name) throws DocumentNotFoundException, NodeException
    {
        // get link instance if links collection not available
        if (!linksCached)
        {
            // use PageManager to get link instance without
            // caching the links collection for this folder
            return pageManager.getLink(this, name);
        }

        // select link by name from cached links collection
        Link link = (Link)getLinksNodeSet().get(name);
        if (link == null)
        {
            throw new DocumentNotFoundException("Link not found: " + name);
        }

        // check for view access on link
        link.checkAccess(JetspeedActions.VIEW);

        return link;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPageSecurity()
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException, NodeException
    {
        // get page security instance
        if (!pageSecurityCached)
        {
            // use PageManager to get and cache page security
            // instance for this folder
            return pageManager.getPageSecurity(this);
        }
        if (pageSecurity == null)
        {
            throw new DocumentNotFoundException("Page security document not found");
        }

        // check for view access on document
        pageSecurity.checkAccess(JetspeedActions.VIEW);

        return pageSecurity;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getAll()
     */
    public NodeSet getAll() throws FolderNotFoundException, DocumentException
    {
        // get all nodes collection
        if (!allCached)
        {
            // use PageManager to get and cache all nodes
            // collection for this folder
            return pageManager.getAll(this);
        }

        // return nodes with view access
        return filterNodeSetByAccess(getAllNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getMenuDefinitions()
     */
    public List getMenuDefinitions()
    {
        // return mutable menu definition list
        // by using list wrapper to manage
        // element uniqueness
        if (menuDefinitions == null)
        {
            menuDefinitions = new FolderMenuDefinitionList(this);
        }
        return menuDefinitions;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#newMenuDefinition()
     */
    public MenuDefinition newMenuDefinition()
    {
        return new FolderMenuDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#newMenuExcludeDefinition()
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        return new FolderMenuExcludeDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#newMenuIncludeDefinition()
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        return new FolderMenuIncludeDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#newMenuOptionsDefinition()
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        return new FolderMenuOptionsDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#newMenuSeparatorDefinition()
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        return new FolderMenuSeparatorDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setMenuDefinitions(java.util.List)
     */
    public void setMenuDefinitions(List definitions)
    {
        // set menu definitions by replacing
        // existing entries with new elements if
        // new collection is specified
        List menuDefinitions = getMenuDefinitions();
        if (definitions != menuDefinitions)
        {
            // replace all menu definitions
            menuDefinitions.clear();
            if (definitions != null)
            {
                menuDefinitions.addAll(definitions);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#isReserved()
     */
    public boolean isReserved()
    {
        // folders are always concrete in this implementation
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getReservedType()
     */
    public int getReservedType()
    {
        // folders are always concrete in this implementation
        return RESERVED_FOLDER_NONE;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        return FOLDER_TYPE;
    }

    /**
     * getFoldersNodeSet
     *
     * Latently create and access folders node set.
     *
     * @return folders node set
     */
    private NodeSet getFoldersNodeSet()
    {
        if (foldersNodeSet == null)
        {
            if ((folders != null) && !folders.isEmpty())
            {
                foldersNodeSet = new NodeSetImpl(folders, createDocumentOrderComparator());
            }
            else
            {
                foldersNodeSet = NodeSetImpl.EMPTY_NODE_SET;
            }
        }
        return foldersNodeSet;
    }
    
    /**
     * getPagesNodeSet
     *
     * Latently create and access pages node set.
     *
     * @return folders node set
     */
    private NodeSet getPagesNodeSet() throws NodeException
    {
        if (pagesNodeSet == null)
        {
            if ((pages != null) && !pages.isEmpty())
            {
                pagesNodeSet = new NodeSetImpl(pages, createDocumentOrderComparator());
            }
            else
            {
                pagesNodeSet = NodeSetImpl.EMPTY_NODE_SET;
            }
        }
        return pagesNodeSet;
    }
    
    /**
     * getLinksNodeSet
     *
     * Latently create and access links node set.
     *
     * @return folders node set
     */
    private NodeSet getLinksNodeSet() throws NodeException
    {
        if (linksNodeSet == null)
        {
            if ((links != null) && !links.isEmpty())
            {
                linksNodeSet = new NodeSetImpl(links, createDocumentOrderComparator());
            }
            else
            {
                linksNodeSet = NodeSetImpl.EMPTY_NODE_SET;
            }
        }
        return linksNodeSet;
    }
    
    /**
     * getAllNodeSet
     *
     * Latently create and access all nodes node set.
     *
     * @return all nodes node set
     */
    private NodeSet getAllNodeSet()
    {
        if (allNodeSet == null)
        {
            if ((all != null) && !all.isEmpty())
            {
                allNodeSet = new NodeSetImpl(all, createDocumentOrderComparator());
            }
            else
            {
                allNodeSet = NodeSetImpl.EMPTY_NODE_SET;
            }
        }
        return allNodeSet;
    }

    /**
     * filterNodeSetByAccess
     *
     * Filter node set elements for view access.
     *
     * @param nodes node set containing nodes to check
     * @return checked subset of nodes
     */
    static NodeSet filterNodeSetByAccess(NodeSet nodes)
    {
        if ((nodes != null) && !nodes.isEmpty())
        {
            // check permissions and constraints, filter nodes as required
            NodeSetImpl filteredNodes = null;
            Iterator checkAccessIter = nodes.iterator();
            while (checkAccessIter.hasNext())
            {
                Node node = (Node)checkAccessIter.next();
                try
                {
                    // check access
                    node.checkAccess(JetspeedActions.VIEW);

                    // add to filteredNodes nodes if copying
                    if (filteredNodes != null)
                    {
                        // permitted, add to filteredNodes nodes
                        filteredNodes.add(node);
                    }
                }
                catch (SecurityException se)
                {
                    // create filteredNodes nodes if not already copying
                    if (filteredNodes == null)
                    {
                        // not permitted, copy previously permitted nodes
                        // to new filteredNodes node set with same comparator
                        filteredNodes = new NodeSetImpl(nodes);
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

            // return filteredNodes nodes if generated
            if (filteredNodes != null)
            {
                return filteredNodes;
            }
        }
        return nodes;
    }
}
