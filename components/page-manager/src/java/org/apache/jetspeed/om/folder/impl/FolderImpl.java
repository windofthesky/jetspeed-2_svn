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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
    private Collection pageSecurity;

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
     * Sets the page security singleton in the persistent collection and resets cached node sets.
     *
     * @param pageSecurity new page security impl
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
     * @see org.apache.jetspeed.om.folder.Folder#isRootFolder()
     */
    public boolean isRootFolder()
    {
        // test using Node implementation
        return super.isRootNode();
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
        if (foldersNodeSet != null)
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
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getFolder(java.lang.String)
     */
    public Folder getFolder(String name) throws FolderNotFoundException, DocumentException
    {
        Folder folder = (Folder)getFolders().get(name);
        if (folder == null)
        {
            throw new FolderNotFoundException("Folder not found: " + name);
        }
        return folder;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPages()
     */
    public NodeSet getPages() throws NodeException
    {
        if (pagesNodeSet != null)
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
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPage(java.lang.String)
     */
    public Page getPage(String name) throws PageNotFoundException, NodeException
    {
        Page page = (Page)getPages().get(name);
        if (page == null)
        {
            throw new PageNotFoundException("Page not found: " + name);
        }
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
        // get singleton page security
        if ((pageSecurity != null) && !pageSecurity.isEmpty())
        {
            return (PageSecurity)pageSecurity.iterator().next();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getAll()
     */
    public NodeSet getAll() throws FolderNotFoundException, DocumentException
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
}
