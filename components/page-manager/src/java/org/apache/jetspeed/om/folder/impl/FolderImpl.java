/*
 * Copyright 2004 The Apache Software Foundation.
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
import java.io.File;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.DocumentSet;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderMetaData;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.AbstractNode;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeOrderCompartaor;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.NodeSetImpl;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.jetspeed.security.FolderPermission;

/**
 * FolderImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:jford@apache.org">Jeremy Ford </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class FolderImpl extends AbstractNode implements Folder
{
    public static final String FALLBACK_DEFAULT_PAGE = "default-page.psml";

    private static final String PAGE_NOT_FOUND_PAGE = "page_not_found.psml";
    private final static String FOLDER_PERMISSION_WILD_CHAR = new String(new char[]{FolderPermission.WILD_CHAR});
    
    private String defaultTheme;
    private NodeSet allNodes;
    private File directory;
    private FolderMetaData metadata;
    private FolderHandler folderHandler;
    
    private static final Log log = LogFactory.getLog(FolderImpl.class);

    public FolderImpl( String path, FolderMetaData metadata, DocumentHandlerFactory handlerFactory,
                       FolderHandler folderHandler )
    {
        this.metadata = metadata;
        this.metadata.setParent(this);
        this.folderHandler = folderHandler;
        setId(path);
        setHandlerFactory(handlerFactory);
        setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
        setPath(path);
    }

    public FolderImpl( String path, DocumentHandlerFactory handlerFactory, FolderHandler folderHandler )
    {
        this.metadata = new FolderMetaDataImpl();
        this.metadata.setTitle(path);
        this.metadata.setParent(this);
        this.folderHandler = folderHandler;
        setId(path);
        setHandlerFactory(handlerFactory);
        setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
        setPath(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultPage(boolean)
     */
    public String getDefaultPage(boolean allowDefaulting)
    {
        try
        {   
            String defaultPage = metadata.getDefaultPage();
            if(defaultPage == null)
            {
                defaultPage = FALLBACK_DEFAULT_PAGE;
            }
            return getPage(defaultPage, false).getName();
        }
        catch (NodeException e)
        {
            if (allowDefaulting)
            {
                try
                {
                    Iterator pagesIter = getPages(false).iterator();
                    if (pagesIter.hasNext())
                    {
                        return ((Page) pagesIter.next()).getName();
                    }
                    else
                    {
                        return PAGE_NOT_FOUND_PAGE;
                    }
                }
                catch (NodeException e1)
                {
                    return PAGE_NOT_FOUND_PAGE;
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultPage()
     */
    public void setDefaultPage( String defaultPage )
    {
        metadata.setDefaultPage(defaultPage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultTheme()
     */
    public String getDefaultTheme()
    {
        return defaultTheme;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultTheme()
     */
    public void setDefaultTheme( String defaultTheme )
    {
        this.defaultTheme = defaultTheme;
    }

    /**
     * <p>
     * getFolders
     * </p>
     * 
     * @param checkAccess flag
     * @return folders node set
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    public NodeSet getFolders(boolean checkAccess) throws FolderNotFoundException, DocumentException
    {
        // get list of all folders
        NodeSet folders = getAllNodes().subset(Folder.FOLDER_TYPE);

        // filter node set by access
        if (checkAccess)
        {
            folders = checkAccess(folders, SecuredResource.VIEW_ACTION);
        }
        return folders;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getFolders()
     */
    public NodeSet getFolders() throws FolderNotFoundException, DocumentException
    {
        // by default disable access checks to facilitate navigation
        return getFolders(false);
    }

    /**
     * <p>
     * getFolder
     * </p>
     * 
     * @param name
     * @param checkAccess flag
     * @return folder
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    public Folder getFolder(String name, boolean checkAccess) throws FolderNotFoundException, DocumentException
    {
        // get folder
        Folder folder = (Folder) getAllNodes().subset(Folder.FOLDER_TYPE).get(name);
        if (folder == null)
        {
            throw new FolderNotFoundException("Jetspeed PSML folder not found: " + name);
        }

        // check access
        if (checkAccess)
        {
            folder.checkAccess(SecuredResource.VIEW_ACTION);
        }
        return folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getFolder(java.lang.String)
     */
    public Folder getFolder(String name) throws FolderNotFoundException, DocumentException
    {
        // by default disable access checks to facilitate navigation
        return getFolder(name, false);
    }

    /**
     * <p>
     * getPages
     * </p>
     * 
     * @param checkAccess flag
     * @return pages node set
     * @throws NodeException
     */
    public NodeSet getPages(boolean checkAccess) throws NodeException
    {
        // get list of all pages
        NodeSet pages = getAllNodes().subset(Page.DOCUMENT_TYPE);

        // filter node set by access
        if (checkAccess)
        {
            pages = checkAccess(pages, SecuredResource.VIEW_ACTION);
        }
        return pages;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getPages()
     */
    public NodeSet getPages() throws NodeException
    {
        // by default enable access checks
        return getPages(true);
    }

    /**
     * <p>
     * getPage
     * </p>
     * 
     * @param name
     * @param checkAccess flag
     * @return page
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public Page getPage(String name, boolean checkAccess) throws PageNotFoundException, NodeException
    {
        // get page
        Page page = (Page) getAllNodes().subset(Page.DOCUMENT_TYPE).get(name);
        if (page == null)
        {
            throw new PageNotFoundException("Jetspeed PSML page not found: " + name);
        }

        // check access
        if (checkAccess)
        {
            page.checkAccess(SecuredResource.VIEW_ACTION);
        }
        return page;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getPage(java.lang.String)
     */
    public Page getPage(String name) throws PageNotFoundException, NodeException
    {
        // by default enable access checks
        return getPage(name, true);
    }

    /**
     * <p>
     * getLinks
     * </p>
     * 
     * @param checkAccess flag
     * @return links node set
     * @throws NodeException
     */
    public NodeSet getLinks(boolean checkAccess) throws NodeException
    {
        // get list of all links
        NodeSet links = getAllNodes().subset(Link.DOCUMENT_TYPE);

        // filter node set by access
        if (checkAccess)
        {
            links = checkAccess(links, SecuredResource.VIEW_ACTION);
        }
        return links;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getLinks()
     */
    public NodeSet getLinks() throws NodeException
    {
        // by default enable access checks
        return getLinks(true);
    }

    /**
     * <p>
     * getLink
     * </p>
     * 
     * @param name
     * @param checkAccess flag
     * @return link
     * @throws DocumentNotFoundException
     * @throws NodeException
     */
    public Link getLink(String name, boolean checkAccess) throws DocumentNotFoundException, NodeException
    {
        // get link
        Link link = (Link) getAllNodes().subset(Link.DOCUMENT_TYPE).get(name);
        if (link == null)
        {
            throw new DocumentNotFoundException("Jetspeed PSML link not found: " + name);
        }

        // check access
        if (checkAccess)
        {
            link.checkAccess(SecuredResource.VIEW_ACTION);
        }
        return link;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getLink(java.lang.String)
     */
    public Link getLink(String name) throws DocumentNotFoundException, NodeException
    {
        // by default enable access checks
        return getLink(name, true);
    }

    /**
     * <p>
     * getDocumentSets
     * </p>
     * 
     * @param checkAccess flag
     * @return documentSets node set
     * @throws NodeException
     */
    public NodeSet getDocumentSets(boolean checkAccess) throws NodeException
    {
        // get list of all documentSets
        NodeSet documentSets = getAllNodes().subset(DocumentSet.DOCUMENT_TYPE);

        // filter node set by access
        if (checkAccess)
        {
            documentSets = checkAccess(documentSets, SecuredResource.VIEW_ACTION);
        }
        return documentSets;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getDocumentSets()
     */
    public NodeSet getDocumentSets() throws NodeException
    {
        // by default enable access checks
        return getDocumentSets(true);
    }

    /**
     * <p>
     * getDocumentSet
     * </p>
     * 
     * @param name
     * @param checkAccess flag
     * @return document set
     * @throws DocumentNotFoundException
     * @throws NodeException
     */
    public DocumentSet getDocumentSet(String name, boolean checkAccess) throws DocumentNotFoundException, NodeException
    {
        // get documentSet
        DocumentSet documentSet = (DocumentSet) getAllNodes().subset(DocumentSet.DOCUMENT_TYPE).get(name);
        if (documentSet == null)
        {
            throw new DocumentNotFoundException("Jetspeed PSML document set not found: " + name);
        }

        // check access
        if (checkAccess)
        {
            documentSet.checkAccess(SecuredResource.VIEW_ACTION);
        }
        return documentSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getDocumentSet(java.lang.String)
     */
    public DocumentSet getDocumentSet(String name) throws DocumentNotFoundException, NodeException
    {
        // by default enable access checks
        return getDocumentSet(name, true);
    }

    /**
     * <p>
     * getPageSecurity
     * </p>
     * 
     * @param checkAccess flag
     * @return page security
     * @throws DocumentNotFoundException
     * @throws NodeException
     */
    public PageSecurity getPageSecurity(boolean checkAccess) throws DocumentNotFoundException, NodeException
    {
        // check access to this folder in place
        // of access to page security document
        if (checkAccess)
        {
            checkAccess(SecuredResource.VIEW_ACTION);
        }

        // get pageSecurity
        PageSecurity pageSecurity = (PageSecurity) getAllNodes().subset(PageSecurity.DOCUMENT_TYPE).get(PageSecurity.DOCUMENT_TYPE);
        if (pageSecurity == null)
        {
            throw new DocumentNotFoundException("Jetspeed PSML page security not found: " + PageSecurity.DOCUMENT_TYPE);
        }
        return pageSecurity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getPageSecurity()
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException, NodeException
    {
        // by default disable access checks
        return getPageSecurity(false);
    }

    /**
     * <p>
     * getMetaData
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getMetaData()
     * @return
     */
    public FolderMetaData getMetaData()
    {
        return metadata;
    }

    /**
     * <p>
     * getAllNodes
     * </p>
     *
     * @return all nodes immediatley under this
     * @throws DocumentException
     * @throws FolderNotFoundException
     */
    public NodeSet getAllNodes() throws FolderNotFoundException, DocumentException
    {
        if(allNodes == null)
        {            
            if(metadata.getDocumentOrder() != null)
            {
                if (getPath().endsWith(PATH_SEPARATOR))
                {
                    allNodes = new NodeSetImpl(getPath(), new NodeOrderCompartaor(metadata.getDocumentOrder(), getPath()));
                }
                else
                {
                    allNodes = new NodeSetImpl(getPath(), new NodeOrderCompartaor(metadata.getDocumentOrder(), getPath() + PATH_SEPARATOR));
                }
            }
            else
            {
                allNodes = new NodeSetImpl(getPath());
            }
            
            //DocumentHandler docHandler = getHandlerFactory().getDocumentHandler(documentType);

            String[] nodeNames = folderHandler.listAll(getPath());
            for (int i = 0; i < nodeNames.length; i++)
            {
                Node node = null;
                try
                {
                    if (getPath().endsWith(PATH_SEPARATOR))
                    {
                        if(nodeNames[i].indexOf(".") > -1)
                        {    
                            node = getHandlerFactory().getDocumentHandlerForPath(nodeNames[i]).getDocument(getPath() + nodeNames[i]);
                        }
                        else
                        {
                            node = folderHandler.getFolder(getPath() + nodeNames[i]);
                        }
                    }
                    else
                    {
                        
                        if(nodeNames[i].indexOf(".") > -1)
                        {    
                            node = getHandlerFactory().getDocumentHandlerForPath(nodeNames[i]).getDocument(getPath() + PATH_SEPARATOR + nodeNames[i]);
                        }
                        else
                        {
                            node = folderHandler.getFolder(getPath() + PATH_SEPARATOR + nodeNames[i]);
                        }
                    }
                    
                    node.setParent(this);
                    allNodes.add(node);
                }               
                catch (UnsupportedDocumentTypeException e)
                {
                    // Skip unsupported documents
                    log.info("getAllNodes() Skipping unsupported document: "+nodeNames[i]);
                }
                catch (Exception e)
                {
                    log.warn("getAllNodes() failed to create Node: "+nodeNames[i]+":"+e.toString(), e);
                }               
            }            
        }
        
        return allNodes;
    }
    /**
     * <p>
     * getMetadata
     * </p>
     *
     * @see org.apache.jetspeed.page.document.AbstractNode#getMetadata()
     * @return
     */
    public GenericMetadata getMetadata()
    {        
        return metadata.getMetadata();
    }

    /**
     * <p>
     * getSecurityConstraints
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#getSecurityConstraints()
     * @return
     */
    public SecurityConstraints getSecurityConstraints()
    {
        return metadata.getSecurityConstraints();
    }
    /**
     * <p>
     * setSecurityConstraints
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#setSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
     * @param constraints
     */
    public void setSecurityConstraints(SecurityConstraints constraints)
    {
        metadata.setSecurityConstraints(constraints);
    }
    /**
     * <p>
     * checkPermissions
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#checkPermissions(java.lang.String)
     * @param actions
     * @throws SecurityException
     */
    public void checkPermissions(String actions) throws SecurityException
    {
        // skip checks if not enabled
        if (!getPermissionsEnabled())
        {
            return;
        }

        // check folder permission, (test requires appended wild character -
        // i.e. "/some-folder/*"),  if permission paths available for this
        // element
        String physicalPermissionPath = getPhysicalPermissionPath();
        if (physicalPermissionPath != null)
        {
            // check permission using physical path
            String permissionPath = physicalPermissionPath;
            if (permissionPath.endsWith(PATH_SEPARATOR))
            {
                permissionPath += FOLDER_PERMISSION_WILD_CHAR;
            }
            else
            {
                permissionPath += PATH_SEPARATOR + FOLDER_PERMISSION_WILD_CHAR;
            }
            try
            {
                FolderPermission permission = new FolderPermission(permissionPath, actions);
                AccessController.checkPermission(permission);
            }
            catch (SecurityException physicalSE)
            {
                // fallback check using logical path if available and different
                String logicalPermissionPath = getLogicalPermissionPath();
                if ((logicalPermissionPath != null) && !logicalPermissionPath.equals(physicalPermissionPath))
                {
                    permissionPath = logicalPermissionPath;
                    if (permissionPath.endsWith(PATH_SEPARATOR))
                    {
                        permissionPath += FOLDER_PERMISSION_WILD_CHAR;
                    }
                    else
                    {
                        permissionPath += PATH_SEPARATOR + FOLDER_PERMISSION_WILD_CHAR;
                    }
                    FolderPermission permission = new FolderPermission(permissionPath, actions);
                    AccessController.checkPermission(permission);
                }
                else
                {
                    throw physicalSE;
                }
            }
        }
    }

    /**
     * <p>
     * getTitle
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#getTitle(java.util.Locale)
     * @param locale
     * @return
     */
    public String getTitle( Locale locale )
    {
        return metadata.getTitle(locale);
    }
    /**
     * <p>
     * getTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.BaseElement#getTitle()
     * @return
     */
    public String getTitle()
    {
        return metadata.getTitle();
    }
    /**
     * <p>
     * setTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.BaseElement#setTitle(java.lang.String)
     * @param title
     */
    public void setTitle( String title )
    {
        metadata.setTitle(title);
    }
    /**
     * <p>
     * getShortTitle
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#getShortTitle(java.util.Locale)
     * @param locale
     * @return
     */
    public String getShortTitle( Locale locale )
    {
        return metadata.getShortTitle(locale);
    }
    /**
     * <p>
     * getShortTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.BaseElement#getShortTitle()
     * @return
     */
    public String getShortTitle()
    {
        return metadata.getShortTitle();
    }
    /**
     * <p>
     * setShortTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.BaseElement#setShortTitle(java.lang.String)
     * @param title
     */
    public void setShortTitle( String title )
    {
        metadata.setShortTitle(title);
    }
    /**
     * <p>
     * getType
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#getType()
     * @return
     */
    public String getType()
    {
        return FOLDER_TYPE;
    }
    /**
     * <p>
     * isHidden
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#isHidden()
     * @return
     */
    public boolean isHidden()
    {
        return metadata.isHidden();
    }
    /**
     * <p>
     * setHidden
     * </p>
     *
     * @see org.apache.jetspeed.page.document.AbstractNode#setHidden(boolean)
     * @param hidden
     */
    public void setHidden( boolean hidden )
    {        
        ((AbstractNode)metadata).setHidden(hidden);
    }
}
