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

package org.apache.jetspeed.page.psml;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.jetspeed.om.folder.psml.FolderImpl;
import org.apache.jetspeed.om.folder.psml.MenuDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuExcludeDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuIncludeDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuOptionsDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.ContentPageImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsDefImpl;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.LinkImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PageSecurityImpl;
import org.apache.jetspeed.om.page.psml.SecurityConstraintsImpl;
import org.apache.jetspeed.page.AbstractPageManager;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.jetspeed.page.document.psml.NodeSetImpl;

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

    private static Map modelClasses = new HashMap();
    static
    {
        modelClasses.put("FragmentImpl.class", FragmentImpl.class);
        modelClasses.put("PageImpl.class", PageImpl.class);
        modelClasses.put("FolderImpl.class", FolderImpl.class);
        modelClasses.put("LinkImpl.class", LinkImpl.class);
        modelClasses.put("PageSecurityImpl.class", PageSecurityImpl.class);
        modelClasses.put("MenuDefinitionImpl.class", MenuDefinitionImpl.class);
        modelClasses.put("MenuExcludeDefinitionImpl.class", MenuExcludeDefinitionImpl.class);
        modelClasses.put("MenuIncludeDefinitionImpl.class", MenuIncludeDefinitionImpl.class);
        modelClasses.put("MenuOptionsDefinitionImpl.class", MenuOptionsDefinitionImpl.class);
        modelClasses.put("MenuSeparatorDefinitionImpl.class", MenuSeparatorDefinitionImpl.class);
        modelClasses.put("SecurityConstraintsImpl.class", SecurityConstraintsImpl.class);
        modelClasses.put("SecurityConstraintImpl.class", SecurityConstraintImpl.class);
        modelClasses.put("SecurityConstraintsDefImpl.class", SecurityConstraintsDefImpl.class);
    }

    private IdGenerator generator = null;
    private DocumentHandlerFactory handlerFactory;
    private FolderHandler folderHandler;
    private FileCache fileCache;

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory handlerFactory,
                                 FolderHandler folderHandler, FileCache fileCache,
                                 boolean permissionsEnabled, boolean constraintsEnabled ) throws FileNotFoundException
    {
        super(permissionsEnabled, constraintsEnabled, modelClasses);
        this.generator = generator;
        handlerFactory.setPermissionsEnabled(permissionsEnabled);
        handlerFactory.setConstraintsEnabled(constraintsEnabled);
        this.handlerFactory = handlerFactory;
        this.folderHandler = folderHandler;
        this.fileCache = fileCache;
        this.fileCache.addListener(this);
    }

    /**
     * <p>
     * newFragment
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#newFragment()
     * @return fragment
     */
    public Fragment newFragment()
    {
        // FragmentImpl requires generated ids
        FragmentImpl fragment = (FragmentImpl)super.newFragment();
        fragment.setId(generator.getNextPeid());
        return fragment;
    }

    public Fragment newPortletFragment()
    {
        // FragmentImpl requires generated ids
        FragmentImpl fragment = (FragmentImpl)super.newFragment();
        fragment.setType(Fragment.PORTLET);
        fragment.setId(generator.getNextPeid());
        return fragment;
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
     * updatePage
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws JetspeedException
    {
        // unwrap page to be registered
        if (page instanceof ContentPageImpl)
        {
            page = ((ContentPageImpl)page).getPage();
        }

        // make sure path and related members are set
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

        // set parent
        boolean newPage = false;
        FolderImpl parentFolder = getNodeFolder(page.getPath());
        if (page.getParent() == null)
        {
            page.setParent(parentFolder);
            newPage = true;
        }

        // enable permissions/constraints
        PageImpl pageImpl = (PageImpl)page;
        pageImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        pageImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());

        // check for edit access
        page.checkAccess(SecuredResource.EDIT_ACTION);

        // update page
        handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).updateDocument(page);

        // update folder
        if ((parentFolder != null) && !parentFolder.getAllNodes().contains(page))
        {
            parentFolder.getAllNodes().add(page);
            newPage = true;
        }

        // notify page manager listeners
        if (newPage)
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
     * updateLink
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updateLink(org.apache.jetspeed.om.page.Link)
     */
    public void updateLink(Link link) throws JetspeedException
    {
        // make sure path and related members are set
        if (link.getPath() != null)
        {
            if (!link.getPath().equals(link.getId()))
            {
                log.error("Link paths and ids must match!");
                return;
            }
        }
        else
        {
            log.error("Link paths and ids must be set!");
            return;
        }

        // set parent
        boolean newLink = false;
        FolderImpl parentFolder = getNodeFolder(link.getPath());
        if (link.getParent() == null)
        {
            link.setParent(parentFolder);
            newLink = true;
        }

        // enable permissions/constraints
        LinkImpl linkImpl = (LinkImpl)link;
        linkImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        linkImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());

        // check for edit access
        link.checkAccess(SecuredResource.EDIT_ACTION);

        // update link
        handlerFactory.getDocumentHandler(Link.DOCUMENT_TYPE).updateDocument(link);

        // update folder
        if ((parentFolder != null) && !parentFolder.getAllNodes().contains(link))
        {
            parentFolder.getAllNodes().add(link);
            newLink = true;
        }

        // notify page manager listeners
        if (newLink)
        {
            notifyNewNode(link);
        }
        else
        {
            notifyUpdatedNode(link);
        }
    }

    /**
     * <p>
     * removeLink
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#removeLink(org.apache.jetspeed.om.page.Link)
     */
    public void removeLink(Link link) throws JetspeedException
    {
        // check for edit access
        link.checkAccess(SecuredResource.EDIT_ACTION);

        // remove link
        handlerFactory.getDocumentHandler(Link.DOCUMENT_TYPE).removeDocument(link);

        // update folder
        FolderImpl folder = getNodeFolder(link.getPath());
        ((NodeSetImpl)folder.getAllNodes()).remove(link);
        link.setParent(null);

        // notify page manager listeners
        notifyRemovedNode(link);
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updatePageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public void updatePageSecurity(PageSecurity pageSecurity) throws JetspeedException, FailedToUpdateDocumentException
    {
        throw new FailedToUpdateDocumentException("Document " + pageSecurity.getPath() + " not updated, update not implemented.");
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public void removePageSecurity(PageSecurity pageSecurity) throws JetspeedException, FailedToDeleteDocumentException
    {
        throw new FailedToDeleteDocumentException("Document " + pageSecurity.getPath() + " not removed, remove not implemented.");
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
     * updateFolder
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updateFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void updateFolder(Folder folder) throws JetspeedException
    {
        // make sure path and related members are set
        if (folder.getPath() != null)
        {
            if (!folder.getPath().equals(folder.getId()))
            {
                log.error("Folder paths and ids must match!");
                return;
            }
        }
        else
        {
            log.error("Folder paths and ids must be set!");
            return;
        }

        // set parent
        boolean newFolder = false;
        FolderImpl parentFolder = getNodeFolder(folder.getPath());
        if (folder.getParent() == null)
        {
            folder.setParent(parentFolder);
            newFolder = true;
        }

        // enable permissions/constraints
        FolderImpl folderImpl = (FolderImpl)folder;
        folderImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        folderImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());

        // check for edit access
        folder.checkAccess(SecuredResource.EDIT_ACTION);

        // update folder
        folderHandler.updateFolder(folder);

        // update parent folder
        if ((parentFolder != null) && !parentFolder.getAllNodes().contains(folder))
        {
            parentFolder.getAllNodes().add(folder);
            newFolder = true;
        }

        // notify page manager listeners
        if (newFolder)
        {
            notifyNewNode(folder);
        }
        else
        {
            notifyUpdatedNode(folder);
        }
    }

    /**
     * <p>
     * removeFolder
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#removeFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void removeFolder(Folder folder) throws JetspeedException
    {
        // check for edit access
        folder.checkAccess(SecuredResource.EDIT_ACTION);

        // remove folder
        folderHandler.removeFolder(folder);

        // update parent folder
        FolderImpl parentFolder = getNodeFolder(folder.getPath());
        ((NodeSetImpl)parentFolder.getAllNodes()).remove(folder);
        folder.setParent(null);

        // notify page manager listeners
        notifyRemovedNode(folder);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#reset()
     */
    public void reset()
    {
        // propagate to super
        super.reset();

        // evict all from file cache to force subsequent
        // refreshs from persistent store
        fileCache.evictAll();
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

    public Page copy(Page source)
    {
        return null;
    }
}
