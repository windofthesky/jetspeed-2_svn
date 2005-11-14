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
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.impl.PageImpl;
import org.apache.jetspeed.om.page.impl.PageSecurityImpl;
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
    private List folders;
    private List pages;
    private List pageSecurity;

    private NodeSet allNodeSet;
    private NodeSet foldersNodeSet;
    private NodeSet pagesNodeSet;

    public FolderImpl()
    {
        super(new FolderSecurityConstraintsImpl());
    }

    /**
     * addFolder
     *
     * Adds a folder to the persistent collection and resets cached node sets.
     *
     * @param folder new folder impl
     */
    public void addFolder(FolderImpl newFolder)
    {
        // add to folders collection
        if (folders == null)
        {
            folders = new ArrayList();
        }
        folders.add(newFolder);

        // reset cached node sets
        allNodeSet = null;
        foldersNodeSet = null;
    }
    
    /**
     * removeFolder
     *
     * Removes a folder to the persistent collection and resets cached node sets.
     *
     * @param folder remove folder impl
     */
    public void removeFolder(FolderImpl removeFolder)
    {
        // remove from folders collection
        if (folders != null)
        {
            folders.remove(removeFolder);
        }

        // reset cached node sets
        allNodeSet = null;
        foldersNodeSet = null;
    }

    /**
     * addPage
     *
     * Adds a page to the persistent collection and resets cached node sets.
     *
     * @param page new page impl
     */
    public void addPage(PageImpl newPage)
    {
        // add to pages collection
        if (pages == null)
        {
            pages = new ArrayList();
        }
        pages.add(newPage);

        // reset cached node sets
        allNodeSet = null;
        pagesNodeSet = null;
    }
    
    /**
     * removePage
     *
     * Removes a page to the persistent collection and resets cached node sets.
     *
     * @param page remove page impl
     */
    public void removePage(PageImpl removePage)
    {
        // remove from pages collection
        if (pages != null)
        {
            pages.remove(removePage);
        }

        // reset cached node sets
        allNodeSet = null;
        pagesNodeSet = null;
    }

    /**
     * setPageSecurity
     *
     * Sets the single page security in the persistent collection and resets cached node sets.
     *
     * @param newPageSecurity new page security impl
     */
    public void setPageSecurity(PageSecurityImpl newPageSecurity)
    {
        if (newPageSecurity != null)
        {
            // add to page security collection
            if (pageSecurity == null)
            {
                pageSecurity = new ArrayList(1);
            }
            pageSecurity.add(newPageSecurity);            
        }
        else
        {
            // clear page security collection
            if ((pageSecurity != null) && !pageSecurity.isEmpty())
            {
                pageSecurity.clear();
            }
        }

        // reset cached node sets
        allNodeSet = null;
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
        // return single page security if available
        PageSecurity pageSecurity = getSinglePageSecurity();
        if (pageSecurity != null)
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
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#checkPermissions(java.lang.String, java.lang.String, boolean, boolean)
     */
    public void checkPermissions(String path, String actions, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check granted folder permissions unless the check is
        // to be skipped due to explicity granted access
        if (!checkParentsOnly)
        {
            FolderPermission permission = new FolderPermission(path, actions);
            AccessController.checkPermission(permission);
        }

        // if not checking node only, recursively check
        // all parent permissions in hierarchy
        if (!checkNodeOnly)
        {
            FolderImpl parentFolderImpl = (FolderImpl)ProxyHelper.getRealObject(getParent());
            if (parentFolderImpl != null)
            {
                parentFolderImpl.checkPermissions(actions, false, false);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDocumentOrder()
     */
    public List getDocumentOrder()
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setDocumentOrder(java.util.List)
     */
    public void setDocumentOrder(List docIndexes)
    {
        // NYI
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
        // return nodes with view access
        return filterNodeSetByAccess(getFoldersNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getFolder(java.lang.String)
     */
    public Folder getFolder(String name) throws FolderNotFoundException, DocumentException
    {
        // select folder by name
        Folder folder = (Folder)getFoldersNodeSet().get(name);
        if (folder == null)
        {
            throw new FolderNotFoundException("Folder not found: " + name);
        }

        // check for view access on folder
        folder.checkAccess(SecuredResource.VIEW_ACTION);

        return folder;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPages()
     */
    public NodeSet getPages() throws NodeException
    {
        // return nodes with view access
        return filterNodeSetByAccess(getPagesNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPage(java.lang.String)
     */
    public Page getPage(String name) throws PageNotFoundException, NodeException
    {
        // select page by name
        Page page = (Page)getPagesNodeSet().get(name);
        if (page == null)
        {
            throw new PageNotFoundException("Page not found: " + name);
        }

        // check for view access on page
        page.checkAccess(SecuredResource.VIEW_ACTION);

        return page;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getLinks()
     */
    public NodeSet getLinks() throws NodeException
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getLink(java.lang.String)
     */
    public Link getLink(String name) throws DocumentNotFoundException, NodeException
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPageSecurity()
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException, NodeException
    {
        // get single page security
        PageSecurity pageSecurity = getSinglePageSecurity();
        if (pageSecurity == null)
        {
            throw new DocumentNotFoundException("Page security document not found");
        }

        // check for view access on document
        pageSecurity.checkAccess(SecuredResource.VIEW_ACTION);

        return pageSecurity;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getAll()
     */
    public NodeSet getAll() throws FolderNotFoundException, DocumentException
    {
        // return nodes with view access
        return filterNodeSetByAccess(getAllNodeSet());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getMenuDefinitions()
     */
    public List getMenuDefinitions()
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setMenuDefinitions(java.util.List)
     */
    public void setMenuDefinitions(List definitions)
    {
        // NYI
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
            if (folders != null)
            {
                foldersNodeSet = new NodeSetImpl(folders);
            }
            else
            {
                foldersNodeSet = new NodeSetImpl();
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
            if (pages != null)
            {
                pagesNodeSet = new NodeSetImpl(pages);
            }
            else
            {
                pagesNodeSet = new NodeSetImpl();
            }
        }
        return pagesNodeSet;
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
            List all = new ArrayList();
            if (folders != null)
            {
                all.addAll(folders);
            }
            if (pages != null)
            {
                all.addAll(pages);
            }
            if (pageSecurity != null)
            {
                all.addAll(pageSecurity);
            }
            if (!all.isEmpty())
            {
                allNodeSet = new NodeSetImpl(all);
            }
            else
            {
                allNodeSet = new NodeSetImpl();
            }
        }
        return allNodeSet;
    }

    /**
     * getSinglePageSecurity
     *
     * Extract single page security from persistent list.
     *
     * @return single page security
     */
    private PageSecurity getSinglePageSecurity()
    {
        if ((pageSecurity != null) && !pageSecurity.isEmpty())
        {
            return (PageSecurity)pageSecurity.iterator().next();
        }
        return null;
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
                    node.checkAccess(SecuredResource.VIEW_ACTION);

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
                        filteredNodes = new NodeSetImpl();
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
