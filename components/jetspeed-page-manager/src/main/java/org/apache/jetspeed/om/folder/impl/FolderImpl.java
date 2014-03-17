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
package org.apache.jetspeed.om.folder.impl;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.impl.DynamicPageImpl;
import org.apache.jetspeed.om.page.impl.FragmentDefinitionImpl;
import org.apache.jetspeed.om.page.impl.LinkImpl;
import org.apache.jetspeed.om.page.impl.PageImpl;
import org.apache.jetspeed.om.page.impl.PageSecurityImpl;
import org.apache.jetspeed.om.page.impl.PageTemplateImpl;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.impl.NodeImpl;
import org.apache.jetspeed.page.document.impl.NodeSetImpl;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;
import org.apache.jetspeed.security.PermissionFactory;
import org.apache.ojb.broker.core.proxy.ProxyHelper;

import java.security.AccessController;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private List<FolderOrder> orders;
    private List<FolderMenuDefinitionImpl> menus;

    private List<FolderImpl> folders;
    private boolean foldersCached;
    private List<PageImpl> pages;
    private boolean pagesCached;
    private List<PageTemplateImpl> pageTemplates;
    private boolean pageTemplatesCached;
    private List<DynamicPageImpl> dynamicPages;
    private boolean dynamicPagesCached;
    private List<FragmentDefinitionImpl> fragmentDefinitions;
    private boolean fragmentDefinitionsCached;
    private List<LinkImpl> links;
    private boolean linksCached;
    private PageSecurityImpl pageSecurity;
    private boolean pageSecurityCached;
    private List<Node> all;
    private boolean allCached;
    private FolderOrderList documentOrder;
    private boolean documentOrderComparatorValid;
    private Comparator<String> documentOrderComparator;
    private NodeSet foldersNodeSet;
    private NodeSet pagesNodeSet;
    private NodeSet pageTemplatesNodeSet;
    private NodeSet dynamicPagesNodeSet;
    private NodeSet fragmentDefinitionsNodeSet;
    private NodeSet linksNodeSet;
    private NodeSet allNodeSet;
    private FolderMenuDefinitionList menuDefinitions;

    protected static PermissionFactory pf;
    
    public static void setPermissionsFactory(PermissionFactory pf)
    {
        FolderImpl.pf = pf;
    }
    
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
    List<FolderOrder> accessFolderOrders()
    {
        // create initial collection if necessary
        if (orders == null)
        {
            orders = DatabasePageManagerUtils.createList();
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
    List<FolderMenuDefinitionImpl> accessMenus()
    {
        // create initial collection if necessary
        if (menus == null)
        {
            menus = DatabasePageManagerUtils.createList();
        }
        return menus;
    }

    /**
     * accessFolders
     *
     * Access folders transient cache collection for use by PageManager.
     *
     * @return folders collection
     */
    public List<FolderImpl> accessFolders()
    {
        // create initial collection if necessary
        if (folders == null)
        {
            folders = Collections.synchronizedList(new ArrayList<FolderImpl>());
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
    public List<PageImpl> accessPages()
    {
        // create initial collection if necessary
        if (pages == null)
        {
            pages = Collections.synchronizedList(new ArrayList<PageImpl>());
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
     * accessPageTemplates
     *
     * Access page templates transient cache collection for use by PageManager.
     *
     * @return page templates collection
     */
    public List<PageTemplateImpl> accessPageTemplates()
    {
        // create initial collection if necessary
        if (pageTemplates == null)
        {
            pageTemplates = Collections.synchronizedList(new ArrayList<PageTemplateImpl>());
        }
        return pageTemplates;
    }

    /**
     * resetPageTemplates
     *
     * Reset page templates transient caches for use by PageManager.
     *
     * @param cached set cached state for page templates
     */
    public void resetPageTemplates(boolean cached)
    {
        // save cached state
        pageTemplatesCached = cached;
        allCached = false;

        // update node caches
        if (!cached)
        {
            accessPageTemplates().clear();
        }
        accessAll().clear();

        // reset cached node sets
        pageTemplatesNodeSet = null;
        allNodeSet = null;
    }

    /**
     * accessDynamicPages
     *
     * Access dynamic pages transient cache collection for use by PageManager.
     *
     * @return dynamic pages collection
     */
    public List<DynamicPageImpl> accessDynamicPages()
    {
        // create initial collection if necessary
        if (dynamicPages == null)
        {
            dynamicPages = Collections.synchronizedList(new ArrayList<DynamicPageImpl>());
        }
        return dynamicPages;
    }

    /**
     * resetDynamicPages
     *
     * Reset dynamic pages transient caches for use by PageManager.
     *
     * @param cached set cached state for dynamic pages
     */
    public void resetDynamicPages(boolean cached)
    {
        // save cached state
        dynamicPagesCached = cached;
        allCached = false;

        // update node caches
        if (!cached)
        {
            accessDynamicPages().clear();
        }
        accessAll().clear();

        // reset cached node sets
        dynamicPagesNodeSet = null;
        allNodeSet = null;
    }

    /**
     * accessFragmentDefinitions
     *
     * Access fragment definitions transient cache collection for use by PageManager.
     *
     * @return fragment definitions collection
     */
    public List<FragmentDefinitionImpl> accessFragmentDefinitions()
    {
        // create initial collection if necessary
        if (fragmentDefinitions == null)
        {
            fragmentDefinitions = Collections.synchronizedList(new ArrayList<FragmentDefinitionImpl>());
        }
        return fragmentDefinitions;
    }

    /**
     * resetFragmentDefinitions
     *
     * Reset fragment definitions transient caches for use by PageManager.
     *
     * @param cached set cached state for fragment definitions
     */
    public void resetFragmentDefinitions(boolean cached)
    {
        // save cached state
        fragmentDefinitionsCached = cached;
        allCached = false;

        // update node caches
        if (!cached)
        {
            accessFragmentDefinitions().clear();
        }
        accessAll().clear();

        // reset cached node sets
        fragmentDefinitionsNodeSet = null;
        allNodeSet = null;
    }

    /**
     * accessLinks
     *
     * Access links transient cache collection for use by PageManager.
     *
     * @return links collection
     */
    public List<LinkImpl> accessLinks()
    {
        // create initial collection if necessary
        if (links == null)
        {
            links = Collections.synchronizedList(new ArrayList<LinkImpl>());
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
     * @param newPageSecurity cached page security instance.
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
    public List<Node> accessAll()
    {
        // create initial collection if necessary
        if (all == null)
        {
            all = Collections.synchronizedList(new ArrayList<Node>());
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
        pageTemplatesCached = cached;
        dynamicPagesCached = cached;
        fragmentDefinitionsCached = cached;
        linksCached = cached;
        pageSecurityCached = cached;

        // update node caches
        accessFolders().clear();
        accessPages().clear();
        accessPageTemplates().clear();
        accessDynamicPages().clear();
        accessFragmentDefinitions().clear();
        accessLinks().clear();
        pageSecurity = null;
        if (cached)
        {
            // populate node caches
            synchronized(all)
            {
                for (Node node: accessAll())
                {
                    if (node instanceof PageImpl)
                    {
                        pages.add((PageImpl)node);
                    }
                    else if (node instanceof PageTemplateImpl)
                    {
                        pageTemplates.add((PageTemplateImpl)node);
                    }
                    else if (node instanceof DynamicPageImpl)
                    {
                        dynamicPages.add((DynamicPageImpl)node);
                    }
                    else if (node instanceof FragmentDefinitionImpl)
                    {
                        fragmentDefinitions.add((FragmentDefinitionImpl)node);
                    }
                    else if (node instanceof FolderImpl)
                    {
                        folders.add((FolderImpl)node);
                    }
                    else if (node instanceof LinkImpl)
                    {
                        links.add((LinkImpl)node);
                    }
                    else if (node instanceof PageSecurityImpl)
                    {
                        pageSecurity = (PageSecurityImpl)node;
                    }
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
        pageTemplatesNodeSet = null;
        dynamicPagesNodeSet = null;
        fragmentDefinitionsNodeSet = null;
        linksNodeSet = null;
    }

    /**
     * createDocumentOrderComparator
     *
     * @return document order comparator
     */
    private Comparator<String> createDocumentOrderComparator()
    {
        if (!documentOrderComparatorValid)
        {
            documentOrderComparatorValid = true;
            // return null if no document order exists;
            // (null implies natural ordering by name)
            final List<String> documentOrder = getDocumentOrder();
            if ((documentOrder == null) || documentOrder.isEmpty())
            {
                return null;
            }
            // create new document order comparator
            documentOrderComparator = new Comparator<String>()
                {
                    /* (non-Javadoc)
                     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                     */
                    public int compare(String s1, String s2)
                    {
                        // Compare node names using document order;
                        // use indices as names if found in document
                        // order to force explicitly ordered items
                        // ahead of unordered items
                        String name1 = (String)s1;
                        int index1 = documentOrder.indexOf(name1);
                        if (index1 >= 0)
                        {
                            // use order index as name1
                            name1 = String.valueOf(index1);
                        }
                        String name2 = (String)s2;
                        int index2 = documentOrder.indexOf(name2);
                        if (index2 >= 0)
                        {
                            // use order index as name2
                            name2 = String.valueOf(index2);
                            if (index1 >= 0)
                            {
                                // pad order indices for numeric string compare
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
                        // compare names and/or indices
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
        pageTemplatesNodeSet = null;
        dynamicPagesNodeSet = null;
        fragmentDefinitionsNodeSet = null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.impl.NodeImpl#newPageMetadata(java.util.Collection)
     */
    public PageMetadataImpl newPageMetadata(Collection<LocalizedField> fields)
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
                return getPageManager().getPageSecurity(this);
            }
            catch (NodeException ne)
            {
            }
            catch (NodeNotFoundException nnfe)
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
        // to be skipped due to explicitly granted access
        if (!checkParentsOnly)
        {
            AccessController.checkPermission((Permission)pf.newPermission(pf.FOLDER_PERMISSION, path, mask));
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
    public List<String> getDocumentOrder()
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
    public void setDocumentOrder(List<String> docNames)
    {
        // set document order using ordered document
        // names by replacing existing entries with
        // new elements if new collection is specified
        List<String> documentOrder = getDocumentOrder();
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
    public NodeSet getFolders() throws DocumentException
    {
        // get folders collection
        if (!foldersCached)
        {
            // use PageManager to get and cache folders
            // collection for this folder
            return getPageManager().getFolders(this);
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
            return getPageManager().getFolder(this, name);
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
            return getPageManager().getPages(this);
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
            return getPageManager().getPage(this, name);
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
     * @see org.apache.jetspeed.om.folder.Folder#getPageTemplates()
     */
    public NodeSet getPageTemplates() throws NodeException
    {
        // get page templates collection
        if (!pageTemplatesCached)
        {
            // use PageManager to get and cache page templates
            // collection for this folder
            return getPageManager().getPageTemplates(this);
        }

        // return nodes with view access
        return filterNodeSetByAccess(getPageTemplatesNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPageTemplate(java.lang.String)
     */
    public PageTemplate getPageTemplate(String name) throws PageNotFoundException, NodeException
    {
        // get page template instance if page templates collection not available
        if (!pageTemplatesCached)
        {
            // use PageManager to get page template instance without
            // caching the page templates collection for this folder
            return getPageManager().getPageTemplate(this, name);
        }

        // select page template by name from cached page templates collection
        PageTemplate pageTemplate = (PageTemplate)getPageTemplatesNodeSet().get(name);
        if (pageTemplate == null)
        {
            throw new PageNotFoundException("PageTemplate not found: " + name);
        }

        // check for view access on page template
        pageTemplate.checkAccess(JetspeedActions.VIEW);

        return pageTemplate;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDynamicPages()
     */
    public NodeSet getDynamicPages() throws NodeException
    {
        // get dynamic pages collection
        if (!dynamicPagesCached)
        {
            // use PageManager to get and cache dynamic pages
            // collection for this folder
            return getPageManager().getDynamicPages(this);
        }

        // return nodes with view access
        return filterNodeSetByAccess(getDynamicPagesNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDynamicPage(java.lang.String)
     */
    public DynamicPage getDynamicPage(String name) throws PageNotFoundException, NodeException
    {
        // get dynamic page instance if dynamic pages collection not available
        if (!dynamicPagesCached)
        {
            // use PageManager to get dynamic page instance without
            // caching the dynamic pages collection for this folder
            return getPageManager().getDynamicPage(this, name);
        }

        // select dynamic page by name from cached dynamic pages collection
        DynamicPage dynamicPage = (DynamicPage)getDynamicPagesNodeSet().get(name);
        if (dynamicPage == null)
        {
            throw new PageNotFoundException("DynamicPage not found: " + name);
        }

        // check for view access on page
        dynamicPage.checkAccess(JetspeedActions.VIEW);

        return dynamicPage;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getFragmentDefinitions()
     */
    public NodeSet getFragmentDefinitions() throws NodeException
    {
        // get fragment definitions collection
        if (!fragmentDefinitionsCached)
        {
            // use PageManager to get and cache fragment definitions
            // collection for this folder
            return getPageManager().getFragmentDefinitions(this);
        }

        // return nodes with view access
        return filterNodeSetByAccess(getFragmentDefinitionsNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getFragmentDefinition(java.lang.String)
     */
    public FragmentDefinition getFragmentDefinition(String name) throws PageNotFoundException, NodeException
    {
        // get fragment definitions instance if fragment definitions collection not available
        if (!fragmentDefinitionsCached)
        {
            // use PageManager to get fragment definition instance without
            // caching the fragment definitions collection for this folder
            return getPageManager().getFragmentDefinition(this, name);
        }

        // select fragment definition by name from cached fragment definitions collection
        FragmentDefinition fragmentDefinition = (FragmentDefinition)getFragmentDefinitionsNodeSet().get(name);
        if (fragmentDefinition == null)
        {
            throw new PageNotFoundException("FragmentDefinition not found: " + name);
        }

        // check for view access on page
        fragmentDefinition.checkAccess(JetspeedActions.VIEW);

        return fragmentDefinition;
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
            return getPageManager().getLinks(this);
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
            return getPageManager().getLink(this, name);
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
            return getPageManager().getPageSecurity(this);
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
    public NodeSet getAll() throws DocumentException
    {
        // get all nodes collection
        if (!allCached)
        {
            // use PageManager to get and cache all nodes
            // collection for this folder
            return getPageManager().getAll(this);
        }

        // return nodes with view access
        return filterNodeSetByAccess(getAllNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getMenuDefinitions()
     */
    public List<MenuDefinition> getMenuDefinitions()
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
    public void setMenuDefinitions(List<MenuDefinition> definitions)
    {
        // set menu definitions by replacing
        // existing entries with new elements if
        // new collection is specified
        List<MenuDefinition> menuDefinitions = getMenuDefinitions();
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
     * getPageTemplatesNodeSet
     *
     * Latently create and access page templates node set.
     *
     * @return folders node set
     */
    private NodeSet getPageTemplatesNodeSet() throws NodeException
    {
        if (pageTemplatesNodeSet == null)
        {
            if ((pageTemplates != null) && !pageTemplates.isEmpty())
            {
                pageTemplatesNodeSet = new NodeSetImpl(pageTemplates, createDocumentOrderComparator());
            }
            else
            {
                pageTemplatesNodeSet = NodeSetImpl.EMPTY_NODE_SET;
            }
        }
        return pageTemplatesNodeSet;
    }
    
    /**
     * getDynamicPagesNodeSet
     *
     * Latently create and access dynamic pages node set.
     *
     * @return folders node set
     */
    private NodeSet getDynamicPagesNodeSet() throws NodeException
    {
        if (dynamicPagesNodeSet == null)
        {
            if ((dynamicPages != null) && !dynamicPages.isEmpty())
            {
                dynamicPagesNodeSet = new NodeSetImpl(dynamicPages, createDocumentOrderComparator());
            }
            else
            {
                dynamicPagesNodeSet = NodeSetImpl.EMPTY_NODE_SET;
            }
        }
        return dynamicPagesNodeSet;
    }
    
    /**
     * getFragmentDefinitionsNodeSet
     *
     * Latently create and access fragment definitions node set.
     *
     * @return folders node set
     */
    private NodeSet getFragmentDefinitionsNodeSet() throws NodeException
    {
        if (fragmentDefinitionsNodeSet == null)
        {
            if ((fragmentDefinitions != null) && !fragmentDefinitions.isEmpty())
            {
                fragmentDefinitionsNodeSet = new NodeSetImpl(fragmentDefinitions, createDocumentOrderComparator());
            }
            else
            {
                fragmentDefinitionsNodeSet = NodeSetImpl.EMPTY_NODE_SET;
            }
        }
        return fragmentDefinitionsNodeSet;
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
                synchronized(all)
                {
                    allNodeSet = new NodeSetImpl(new ArrayList<Node>(all), createDocumentOrderComparator());
                }
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
            for (Node node : nodes)
            {
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
