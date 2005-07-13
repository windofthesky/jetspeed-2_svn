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
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderMetaData;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.Reset;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.page.PageManager;
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
public class FolderImpl extends AbstractNode implements Folder, Reset
{
    private final static String FOLDER_PERMISSION_WILD_CHAR = new String(new char[]{FolderPermission.WILD_CHAR});
    
    private String defaultTheme;
    private NodeSet allNodes;
    private File directory;
    private FolderMetaData metadata;
    private FolderHandler folderHandler;
    private int reservedType = RESERVED_FOLDER_NONE;
    
    private static final Log log = LogFactory.getLog(FolderImpl.class);

    public FolderImpl( String path, FolderMetaData metadata, DocumentHandlerFactory handlerFactory,
                       FolderHandler folderHandler )
    {
        this.metadata = metadata;
        this.metadata.setParent(this);
        this.folderHandler = folderHandler;
        setId(path);
        setPath(path);
        setReservedType();
        setHandlerFactory(handlerFactory);
        setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
    }

    public FolderImpl( String path, DocumentHandlerFactory handlerFactory, FolderHandler folderHandler )
    {
        this.metadata = new FolderMetaDataImpl();
        this.metadata.setParent(this);
        this.folderHandler = folderHandler;
        setId(path);
        setPath(path);
        setReservedType();
        setHandlerFactory(handlerFactory);
        setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
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
                defaultPage = Folder.FALLBACK_DEFAULT_PAGE;
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
                        return Folder.PAGE_NOT_FOUND_PAGE;
                    }
                }
                catch (NodeException e1)
                {
                    return Folder.PAGE_NOT_FOUND_PAGE;
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
        // by default enable access checks
        return getFolders(true);
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
        // by default enable access checks
        return getFolder(name, true);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getAll()
     */
    public NodeSet getAll() throws FolderNotFoundException, DocumentException
    {
        // return secure set of all nodes: enforce access checks
        // on folders and documents while creating filtered nodes
        NodeSet nodes = getAllNodes();
        NodeSet filteredNodes = null;
        Iterator checkAccessIter = nodes.iterator();
        while (checkAccessIter.hasNext())
        {
            Node node = (Node)checkAccessIter.next();
            try
            {
                ((AbstractNode) node).checkAccess(SecuredResource.VIEW_ACTION);
                if (filteredNodes != null)
                {
                    filteredNodes.add(node);
                }
            }
            catch (SecurityException se)
            {
                if (filteredNodes == null)
                {
                    filteredNodes = new NodeSetImpl(getPath(), ((NodeSetImpl) nodes).getComparator());
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
        if (filteredNodes != null)
        {
            return filteredNodes;
        }
        return nodes;
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
     * getFolderMetaData
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getFolderMetaData()
     * @return
     */
    public FolderMetaData getFolderMetaData()
    {
        return metadata;
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
     * @param path
     * @param actions
     * @param checkNodeOnly
     * @param checkParentsOnly
     * @throws SecurityException
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
        if (!checkNodeOnly && (getParent() != null))
        {
            ((AbstractNode)getParent()).checkPermissions(actions, false, false);
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Reset#reset()
     */
    public void reset()
    {
        allNodes = null;
        
    }

    /**
     * getMenuDefinitions - get list of menu definitions
     *
     * @return definition list
     */
    public List getMenuDefinitions()
    {
        return metadata.getMenuDefinitions();
    }

    /**
     * setMenuDefinitions - set list of menu definitions
     *
     * @param definitions definition list
     */
    public void setMenuDefinitions(List definitions)
    {
        metadata.setMenuDefinitions(definitions);
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     */
    public void unmarshalled()
    {
        // notify super class implementation
        super.unmarshalled();

        // default title of pages to name
        if (getTitle() == null)
        {
            setTitle(getTitleName());
        }
    }
    
    public boolean isReserved()
    {
        return (reservedType > RESERVED_FOLDER_NONE);
    }
    
    public int getReservedType()
    {
        return reservedType;
    }
    
    private static final String RESERVED_FOLDER_PREFIX = "_";
    private static final String RESERVED_USER_FOLDER_NAME = "_user";
    private static final String RESERVED_ROLE_FOLDER_NAME = "_role";
    private static final String RESERVED_GROUP_FOLDER_NAME = "_group";
    private static final String RESERVED_SUBSITES_FOLDER_NAME = "__subsite-root";
    private static final String RESERVED_MEDIATYPE_FOLDER_NAME = "_mediatype";
    private static final String RESERVED_LANGUAGE_FOLDER_NAME = "_language";
    private static final String RESERVED_COUNTRY_FOLDER_NAME = "_country";
    
    private void setReservedType()
    {
        String name = getName();
        if (name != null && name.startsWith(RESERVED_FOLDER_PREFIX))            
        {
            if (name.equals(RESERVED_USER_FOLDER_NAME))
                reservedType = RESERVED_FOLDER_USERS;
            else if (name.equals(RESERVED_ROLE_FOLDER_NAME))
                reservedType = RESERVED_FOLDER_ROLES;
            else if (name.equals(RESERVED_GROUP_FOLDER_NAME))
                reservedType = RESERVED_FOLDER_GROUPS;
            else if (name.equals(RESERVED_SUBSITES_FOLDER_NAME))
                reservedType = RESERVED_FOLDER_SUBSITES;
            else if (name.equals(RESERVED_MEDIATYPE_FOLDER_NAME))
                reservedType = RESERVED_FOLDER_MEDIATYPE;
            else if (name.equals(RESERVED_LANGUAGE_FOLDER_NAME))
                reservedType = RESERVED_FOLDER_LANGUAGE;
            else if (name.equals(RESERVED_COUNTRY_FOLDER_NAME))
                reservedType = RESERVED_FOLDER_COUNTRY;
            else
                reservedType = RESERVED_FOLDER_OTHER;            
        }
    }
    
}
