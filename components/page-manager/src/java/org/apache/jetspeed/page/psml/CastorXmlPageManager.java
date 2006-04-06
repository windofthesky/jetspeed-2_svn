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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
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
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.LinkImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PageSecurityImpl;
import org.apache.jetspeed.om.page.psml.SecurityConstraintsDefImpl;
import org.apache.jetspeed.om.page.psml.SecurityConstraintsImpl;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.om.preference.impl.FragmentPreferenceImpl;
import org.apache.jetspeed.page.AbstractPageManager;
import org.apache.jetspeed.page.FolderNotUpdatedException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
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
        modelClasses.put("FragmentImpl", FragmentImpl.class);
        modelClasses.put("PageImpl", PageImpl.class);
        modelClasses.put("FolderImpl", FolderImpl.class);
        modelClasses.put("LinkImpl", LinkImpl.class);
        modelClasses.put("PageSecurityImpl", PageSecurityImpl.class);
        modelClasses.put("FolderMenuDefinitionImpl", MenuDefinitionImpl.class);
        modelClasses.put("FolderMenuExcludeDefinitionImpl", MenuExcludeDefinitionImpl.class);
        modelClasses.put("FolderMenuIncludeDefinitionImpl", MenuIncludeDefinitionImpl.class);
        modelClasses.put("FolderMenuOptionsDefinitionImpl", MenuOptionsDefinitionImpl.class);
        modelClasses.put("FolderMenuSeparatorDefinitionImpl", MenuSeparatorDefinitionImpl.class);
        modelClasses.put("PageMenuDefinitionImpl", MenuDefinitionImpl.class);
        modelClasses.put("PageMenuExcludeDefinitionImpl", MenuExcludeDefinitionImpl.class);
        modelClasses.put("PageMenuIncludeDefinitionImpl", MenuIncludeDefinitionImpl.class);
        modelClasses.put("PageMenuOptionsDefinitionImpl", MenuOptionsDefinitionImpl.class);
        modelClasses.put("PageMenuSeparatorDefinitionImpl", MenuSeparatorDefinitionImpl.class);
        modelClasses.put("SecurityConstraintsImpl", SecurityConstraintsImpl.class);
        modelClasses.put("FolderSecurityConstraintImpl", SecurityConstraintImpl.class);
        modelClasses.put("PageSecurityConstraintImpl", SecurityConstraintImpl.class);
        modelClasses.put("FragmentSecurityConstraintImpl", SecurityConstraintImpl.class);
        modelClasses.put("LinkSecurityConstraintImpl", SecurityConstraintImpl.class);
        modelClasses.put("PageSecuritySecurityConstraintImpl", SecurityConstraintImpl.class);
        modelClasses.put("SecurityConstraintsDefImpl", SecurityConstraintsDefImpl.class);
        modelClasses.put("FragmentPreferenceImpl", FragmentPreferenceImpl.class);
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
    public Page getPage(String path) throws PageNotFoundException, NodeException
    {
        // get page via folder, (access checked in Folder.getPage())
        try
        {
            FolderImpl folder = getNodeFolder(path);
            return folder.getPage(getNodeName(path));
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new PageNotFoundException(fnfe.getMessage());
        }
    }

    /**
     * <p>
     * updatePage
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws NodeException
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

        try
        {
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
            page.checkAccess(JetspeedActions.EDIT);
            
            // update page
            handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).updateDocument(page);
            
            // update parent folder
            if (parentFolder != null)
            {
                NodeSetImpl parentAllNodes = (NodeSetImpl)parentFolder.getAllNodes();
                if (!parentAllNodes.contains(page))
                {
                    // add new page
                    parentAllNodes.add(page);
                    newPage = true;
                }
                else if (parentAllNodes.get(page.getPath()) != page)
                {
                    // remove stale page and add updated page
                    parentAllNodes.remove(page);                
                    parentAllNodes.add(page);
                }
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
        catch (FolderNotFoundException fnfe)
        {
            throw new NodeException(fnfe.getMessage());
        }
    }

    /**
     * <p>
     * removePage
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page) throws NodeException
    {
        // unwrap page to be removed
        if (page instanceof ContentPageImpl)
        {
            page = ((ContentPageImpl)page).getPage();
        }

        // check for edit access
        page.checkAccess(JetspeedActions.EDIT);

        try
        {
            FolderImpl folder = getNodeFolder(page.getPath());

            // remove page
            handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).removeDocument(page);
            
            // update folder
            ((NodeSetImpl)folder.getAllNodes()).remove(page);
            
            // notify page manager listeners
            notifyRemovedNode(page);
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new NodeException(fnfe.getMessage());
        }
        catch (DocumentNotFoundException dnfe)
        {
            throw new NodeException(dnfe.getMessage());
        }
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
     */
    public Link getLink(String path) throws DocumentNotFoundException, UnsupportedDocumentTypeException, NodeException
    {
        // get link via folder, (access checked in Folder.getLink())
        try
        {
            FolderImpl folder = getNodeFolder(path);
            return folder.getLink(getNodeName(path));
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new DocumentNotFoundException(fnfe.getMessage());
        }
    }

    /**
     * <p>
     * updateLink
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updateLink(org.apache.jetspeed.om.page.Link)
     */
    public void updateLink(Link link) throws NodeException
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

        try
        {
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
            link.checkAccess(JetspeedActions.EDIT);
            
            // update link
            handlerFactory.getDocumentHandler(Link.DOCUMENT_TYPE).updateDocument(link);
            
            // update parent folder
            if (parentFolder != null)
            {
                NodeSetImpl parentAllNodes = (NodeSetImpl)parentFolder.getAllNodes();
                if (!parentAllNodes.contains(link))
                {
                    // add new link
                    parentAllNodes.add(link);
                    newLink = true;
                }
                else if (parentAllNodes.get(link.getPath()) != link)
                {
                    // remove stale link and add updated link
                    parentAllNodes.remove(link);
                    parentAllNodes.add(link);
                }
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
        catch (FolderNotFoundException fnfe)
        {
            throw new NodeException(fnfe.getMessage());
        }
    }

    /**
     * <p>
     * removeLink
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#removeLink(org.apache.jetspeed.om.page.Link)
     */
    public void removeLink(Link link) throws NodeException
    {
        // check for edit access
        link.checkAccess(JetspeedActions.EDIT);

        try
        {
            FolderImpl folder = getNodeFolder(link.getPath());

            // remove link
            handlerFactory.getDocumentHandler(Link.DOCUMENT_TYPE).removeDocument(link);
            
            // update folder
            ((NodeSetImpl)folder.getAllNodes()).remove(link);
            
            // notify page manager listeners
            notifyRemovedNode(link);
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new NodeException(fnfe.getMessage());
        }
        catch (DocumentNotFoundException dnfe)
        {
            throw new NodeException(dnfe.getMessage());
        }
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
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException, UnsupportedDocumentTypeException, NodeException
    {
        // get page security via folder, (always allow access)
        try
        {
            FolderImpl folder = getNodeFolder(Folder.PATH_SEPARATOR);
            return folder.getPageSecurity();
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new DocumentNotFoundException(fnfe.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updatePageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public void updatePageSecurity(PageSecurity pageSecurity) throws NodeException, FailedToUpdateDocumentException
    {
        // validate path... must exist in root folder and
        // make sure path and related members are set
        if (pageSecurity.getPath() != null)
        {
            if (!pageSecurity.getPath().equals(Folder.PATH_SEPARATOR + PageSecurity.DOCUMENT_TYPE))
            {
                log.error("PageSecurity path must be: " + Folder.PATH_SEPARATOR + PageSecurity.DOCUMENT_TYPE);
                return;
            }
            if (!pageSecurity.getPath().equals(pageSecurity.getId()))
            {
                log.error("PageSecurity paths and ids must match!");
                return;
            }
        }
        else
        {
            log.error("PageSecurity paths and ids must be set!");
            return;
        }

        try
        {
            // set parent
            boolean newPageSecurity = false;
            FolderImpl parentFolder = getNodeFolder(Folder.PATH_SEPARATOR);
            if (pageSecurity.getParent() == null)
            {
                pageSecurity.setParent(parentFolder);
                newPageSecurity = true;
            }
            
            // enable permissions/constraints
            PageSecurityImpl pageSecurityImpl = (PageSecurityImpl)pageSecurity;
            pageSecurityImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
            pageSecurityImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
            
            // check for edit access
            pageSecurity.checkAccess(JetspeedActions.EDIT);
            
            // update pageSecurity
            handlerFactory.getDocumentHandler(PageSecurity.DOCUMENT_TYPE).updateDocument(pageSecurity);
            
            // update parent folder
            if (parentFolder != null)
            {
                NodeSetImpl parentAllNodes = (NodeSetImpl)parentFolder.getAllNodes();
                if (!parentAllNodes.contains(pageSecurity))
                {
                    // add new page security
                    parentAllNodes.add(pageSecurity);
                    newPageSecurity = true;
                }
                else if (parentAllNodes.get(pageSecurity.getPath()) != pageSecurity)
                {
                    // remove stale page security and add updated page security
                    parentAllNodes.remove(pageSecurity);
                    parentAllNodes.add(pageSecurity);
                }
            }
            
            // notify page manager listeners
            if (newPageSecurity)
            {
                notifyNewNode(pageSecurity);
            }
            else
            {
                notifyUpdatedNode(pageSecurity);
            }
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new NodeException(fnfe.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public void removePageSecurity(PageSecurity pageSecurity) throws NodeException, FailedToDeleteDocumentException
    {
        // check for edit access
        pageSecurity.checkAccess(JetspeedActions.EDIT);

        try
        {
            FolderImpl folder = getNodeFolder(Folder.PATH_SEPARATOR);

            // remove page security
            handlerFactory.getDocumentHandler(PageSecurity.DOCUMENT_TYPE).removeDocument(pageSecurity);
            
            // update folder
            ((NodeSetImpl)folder.getAllNodes()).remove(pageSecurity);
            
            // notify page manager listeners
            notifyRemovedNode(pageSecurity);
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new NodeException(fnfe.getMessage());
        }
        catch (DocumentNotFoundException dnfe)
        {
            throw new NodeException(dnfe.getMessage());
        }
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
        folder.checkAccess(JetspeedActions.VIEW);
        return folder;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFolders(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getFolders(Folder folder) throws DocumentException
    {
        // delegate back to folder instance
        return folder.getFolders();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFolder(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */
    public Folder getFolder(Folder folder, String name) throws FolderNotFoundException, DocumentException
    {
        // delegate back to folder instance
        return folder.getFolder(name);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPages(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getPages(Folder folder) throws NodeException
    {
        // delegate back to folder instance
        return folder.getPages();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPage(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */
    public Page getPage(Folder folder, String name) throws PageNotFoundException, NodeException
    {
        // delegate back to folder instance
        return folder.getPage(name);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLinks(org.apache.jetspeed.om.folder.Folder)
     */    
    public NodeSet getLinks(Folder folder) throws NodeException
    {
        // delegate back to folder instance
        return folder.getLinks();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLink(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */    
    public Link getLink(Folder folder, String name) throws DocumentNotFoundException, NodeException
    {
        // delegate back to folder instance
        return folder.getLink(name);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageSecurity(org.apache.jetspeed.om.folder.Folder)
     */    
    public PageSecurity getPageSecurity(Folder folder) throws DocumentNotFoundException, NodeException
    {
        // delegate back to folder instance
        return folder.getPageSecurity();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getAll(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getAll(Folder folder) throws DocumentException
    {
        // delegate back to folder instance
        return folder.getAll();
    }

    /**
     * <p>
     * updateFolder
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updateFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void updateFolder(Folder folder) throws NodeException, FolderNotUpdatedException
    {
        // shallow update by default
        updateFolder(folder, false);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateFolder(org.apache.jetspeed.om.folder.Folder,boolean)
     */
    public void updateFolder(Folder folder, boolean deep) throws NodeException, FolderNotUpdatedException
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

        try
        {
            // set parent
            boolean newFolder = false;
            FolderImpl parentFolder = null;
            if (!folder.getPath().equals(Folder.PATH_SEPARATOR))
            {
                parentFolder = getNodeFolder(folder.getPath());
                if (folder.getParent() == null)
                {
                    folder.setParent(parentFolder);
                    newFolder = true;
                }
            }
            else
            {
                folder.setParent(null);            
            }
            
            // enable permissions/constraints and configure
            // folder handler before access is checked
            FolderImpl folderImpl = (FolderImpl)folder;
            folderImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
            folderImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
            folderImpl.setFolderHandler(folderHandler);
            
            // check for edit access
            folder.checkAccess(JetspeedActions.EDIT);
            
            // update folder
            folderHandler.updateFolder(folder);
            
            // update parent folder
            if (parentFolder != null)
            {
                NodeSetImpl parentAllNodes = (NodeSetImpl)parentFolder.getAllNodes();
                if (!parentAllNodes.contains(folder))
                {
                    // add new folder
                    parentAllNodes.add(folder);
                    newFolder = true;
                }
                else if (parentAllNodes.get(folder.getPath()) != folder)
                {
                    // remove stale folder and add updated folder
                    parentAllNodes.remove(folder);
                    parentAllNodes.add(folder);
                }
            }
            
            // update deep recursively if specified
            if (deep)
            {
                // update recursively, (breadth first)
                updateFolderNodes(folderImpl);
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
        catch (FolderNotFoundException fnfe)
        {
            throw new NodeException(fnfe.getMessage());
        }
    }

    /**
     * updateFolderNodes - recusively update all folder nodes
     *
     * @param folderImpl folder whose nodes are to be updated
     * @param throws FolderNotUpdatedException
     */
    private void updateFolderNodes(FolderImpl folderImpl) throws FolderNotUpdatedException
    {
        try
        {
            // update folder documents
            NodeSet nodes = folderImpl.getAllNodes();
            Iterator nodesIter = nodes.iterator();
            while (nodesIter.hasNext())
            {
                Node node = (Node) nodesIter.next();
                if (node instanceof Page)
                {
                    updatePage((Page)node);
                }
                else if (node instanceof Link)
                {
                    updateLink((Link)node);
                }
                else if (node instanceof PageSecurity)
                {
                    updatePageSecurity((PageSecurity)node);
                }
            }

            // update folders last: breadth first recursion
            nodesIter = nodes.iterator();
            while (nodesIter.hasNext())
            {
                Node node = (Node) nodesIter.next();
                if (node instanceof Folder)
                {
                    updateFolder((Folder)node, true);
                }
            }
        }
        catch (FolderNotUpdatedException fnue)
        {
            throw fnue;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new FolderNotUpdatedException("Folder " + folderImpl.getPath() + " not updated.", e);
        }
    }

    /**
     * <p>
     * removeFolder
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#removeFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void removeFolder(Folder folder) throws NodeException
    {
        // check for edit access
        folder.checkAccess(JetspeedActions.EDIT);

        try
        {
            FolderImpl parentFolder = null;
            if (!folder.getPath().equals(Folder.PATH_SEPARATOR))
            {
                parentFolder = getNodeFolder(folder.getPath());
            }

            // remove folder
            folderHandler.removeFolder(folder);

            // update parent folder
            if (parentFolder != null)
            {
                ((NodeSetImpl)parentFolder.getAllNodes()).remove(folder);
            }

            // notify page manager listeners
            notifyRemovedNode(folder);
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new NodeException(fnfe.getMessage());
        }
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
     * @throws FolderNotFoundException
     */
    private FolderImpl getNodeFolder(String nodePath) throws NodeException, InvalidFolderException, FolderNotFoundException
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
    
    public int addPages(Page[] pages)
    throws NodeException
    {
        this.updatePage(pages[0]);
        this.updatePage(pages[1]);
        throw new NodeException("Its gonna blow captain!");
    }
    
}
