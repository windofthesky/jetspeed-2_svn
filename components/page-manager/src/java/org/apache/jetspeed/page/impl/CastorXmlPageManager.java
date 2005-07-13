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

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.psml.ContentPageImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.NodeSetImpl;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;

/**
 * This service is responsible for loading and saving PSML pages serialized to
 * disk
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:weaver@apache.org">Scott T Weaver </a>
 * @version $Id$
 */
public class CastorXmlPageManager extends AbstractPageManager implements PageManager, FileCacheEventListener
{
    private final static Log log = LogFactory.getLog(CastorXmlPageManager.class);

    protected final static String PROFILE_PROPERTY_FOLDER_PREFIX = "_";
    protected final static String PROFILE_NAVIGATION_PROPERTY_FOLDER_PREFIX = "__";

    private DocumentHandlerFactory handlerFactory;

    private FolderHandler folderHandler;

    private FileCache fileCache;

    // default configuration values

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory handlerFactory,
                                 FolderHandler folderHandler, FileCache fileCache,
                                 boolean permissionsEnabled, boolean constraintsEnabled ) throws FileNotFoundException
    {
        super(generator, permissionsEnabled, constraintsEnabled);
        handlerFactory.setPermissionsEnabled(permissionsEnabled);
        handlerFactory.setConstraintsEnabled(constraintsEnabled);
        this.handlerFactory = handlerFactory;
        this.folderHandler = folderHandler;
        this.fileCache = fileCache;
        this.fileCache.addListener(this);
    }

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory handlerFactory,
                                 FolderHandler folderHandler, FileCache fileCache,
                                 boolean permissionsEnabled, boolean constraintsEnabled,
                                 List modelClasses ) throws FileNotFoundException
    {
        super(generator, permissionsEnabled, constraintsEnabled, modelClasses);
        handlerFactory.setPermissionsEnabled(permissionsEnabled);
        handlerFactory.setConstraintsEnabled(constraintsEnabled);
        this.handlerFactory = handlerFactory;
        this.folderHandler = folderHandler;
        this.fileCache = fileCache;
        this.fileCache.addListener(this);
    }

    /**
     * <p>
     * getPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPage(java.lang.String)
     * @param path
     * @return page
     * @throws PageNotFoundException
     * @throws NodeException
     * @throws FolderNotFoundException
     */
    public Page getPage(String path) throws PageNotFoundException, FolderNotFoundException, NodeException
    {
        // get page via folder, (access checked in Folder.getPage())
        FolderImpl folder = getNodeFolder(path);
        return folder.getPage(getNodeName(path));
    }

    /**
     * <p>
     * registerPage
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#registerPage(org.apache.jetspeed.om.page.Page)
     */
    public void registerPage(Page page) throws JetspeedException
    {
        // unwrap page to be registered
        if (page instanceof ContentPageImpl)
        {
            page = ((ContentPageImpl)page).getPage();
        }

        // make sure path and related members are set
        boolean newPageRegistered = false;
        if ((page.getPath() == null) && (page.getId() != null))
        {
            String path = page.getId();
            if (!path.startsWith(Folder.PATH_SEPARATOR))
            {
                path = Folder.PATH_SEPARATOR + path;
            }
            if (!path.endsWith(Page.DOCUMENT_TYPE))
            {
                path += Page.DOCUMENT_TYPE;
            }
            page.setId(path);
            page.setPath(path);
            newPageRegistered = true;
        }
        if (page.getPath() != null)
        {
            if (!page.getPath().equals(page.getId()))
            {
                log.error("Page paths and ids must match!");
                return;
            }
        }
        else
        {
            log.error("Page paths and ids must be set!");
            return;
        }

        // check for edit access
        page.checkAccess(SecuredResource.EDIT_ACTION);

        // register page
        handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).updateDocument(page);

        // update folder
        FolderImpl folder = getNodeFolder(page.getPath());
        if (!folder.getAllNodes().contains(page))
        {
            folder.getAllNodes().add(page);
        }
        page.setParent(folder);

        // notify page manager listeners
        if (newPageRegistered)
        {
            notifyNewNode(page);
        }
        else
        {
            notifyUpdatedNode(page);
        }
    }

    /**
     * <p>
     * updatePage
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws JetspeedException
    {
        // unwrap page to be updated
        if (page instanceof ContentPageImpl)
        {
            page = ((ContentPageImpl)page).getPage();
        }

        registerPage(page);
    }

    /**
     * <p>
     * removePage
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page) throws JetspeedException
    {
        // unwrap page to be removed
        if (page instanceof ContentPageImpl)
        {
            page = ((ContentPageImpl)page).getPage();
        }

        // check for edit access
        page.checkAccess(SecuredResource.EDIT_ACTION);

        // remove page
        handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).removeDocument(page);

        // update folder
        FolderImpl folder = getNodeFolder(page.getPath());
        ((NodeSetImpl)folder.getAllNodes()).remove(page);
        page.setParent(null);

        // notify page manager listeners
        notifyRemovedNode(page);
    }

    /**
     * <p>
     * getLink
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getLink(java.lang.String)
     * @param path
     * @return link
     * @throws DocumentNotFoundException
     * @throws UnsupportedDocumentTypeException
     * @throws NodeException
     * @throws FolderNotFoundException
     */
    public Link getLink(String path) throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException
    {
        // get link via folder, (access checked in Folder.getLink())
        FolderImpl folder = getNodeFolder(path);
        return folder.getLink(getNodeName(path));
    }

    /**
     * <p>
     * getPageSecurity
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPageSecurity()
     * @return page security instance
     * @throws DocumentNotFoundException
     * @throws UnsupportedDocumentTypeException
     * @throws NodeException
     * @throws FolderNotFoundException
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException
    {
        // get page security via folder, (always allow access)
        FolderImpl folder = getNodeFolder(Folder.PATH_SEPARATOR);
        return folder.getPageSecurity();
    }

    /**
     * <p>
     * getFolder
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getFolder(java.lang.String)
     * @param folderPath
     * @return folder instance
     * @throws FolderNotFoundException
     * @throws NodeException
     * @throws InvalidFolderException
     */
    public Folder getFolder(String folderPath) throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        // get folder and check access before returning
        Folder folder = folderHandler.getFolder(folderPath);
        folder.checkAccess(SecuredResource.VIEW_ACTION);
        return folder;
    }

    /**
     * <p>
     * getNodeFolder - get folder implementation associated with specifed path
     * </p>
     * 
     * @param nodePath
     * @return folder impl instance
     * @throws NodeException
     * @throws InvalidFolderException
     */
    private FolderImpl getNodeFolder(String nodePath) throws NodeException, InvalidFolderException
    {
        int folderIndex = nodePath.lastIndexOf(Folder.PATH_SEPARATOR);
        if (folderIndex > 0)
        {
            return (FolderImpl) folderHandler.getFolder(nodePath.substring(0, folderIndex));
        }
        return (FolderImpl) folderHandler.getFolder(Folder.PATH_SEPARATOR);
    }

    /**
     * <p>
     * getNodeFolder - get name of node from specified path
     * </p>
     * 
     * @param nodePath
     * @return name of node
     */
    private String getNodeName(String nodePath)
    {
        int folderIndex = nodePath.lastIndexOf(Folder.PATH_SEPARATOR);
        if (folderIndex > -1)
        {
            return nodePath.substring(folderIndex+1);
        }
        return nodePath;
    }

    /**
     * <p>
     * refresh file cache entry
     * </p>
     * 
     * @see org.apache.jetspeed.cache.file.FileCacheEventListener#refresh(org.apache.jetspeed.cache.file.FileCacheEntry)
     * @param entry
     * @throws Exception
     */
    public void refresh( FileCacheEntry entry ) throws Exception
    {
        // file cache managed component refreshed:
        // notify page manager listeners
        Node refreshedNode = null;
        if (entry.getDocument() instanceof Node)
        {
            refreshedNode = (Node)entry.getDocument();
        }
        if (entry.getFile().exists())
        {
            notifyUpdatedNode(refreshedNode);
        }
        else
        {
            notifyRemovedNode(refreshedNode);
        }
    }

    /**
     * <p>
     * evict file cache entry
     * </p>
     * 
     * @see org.apache.jetspeed.cache.file.FileCacheEventListener#evict(org.apache.jetspeed.cache.file.FileCacheEntry)
     * @param entry
     * @throws Exception
     */
    public void evict( FileCacheEntry entry ) throws Exception
    {
        // file cache managed component evicted:
        // no notifications required since eviction
        // is normal cache operation and does not
        // indicate a change in the nodes managed by
        // this page manager
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getContentPage(java.lang.String)
     */
    public ContentPage getContentPage(String path) throws PageNotFoundException, NodeException
    {        
        return new ContentPageImpl(getPage(path));
    } 

}
