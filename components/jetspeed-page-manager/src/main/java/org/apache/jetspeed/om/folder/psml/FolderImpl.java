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
package org.apache.jetspeed.om.folder.psml;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.folder.Reset;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.page.PageManagerUtils;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.jetspeed.page.document.psml.AbstractNode;
import org.apache.jetspeed.page.document.psml.NodeOrderComparator;
import org.apache.jetspeed.page.document.psml.NodeSetImpl;
import org.apache.jetspeed.security.PermissionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.Permission;
import java.util.List;
import java.util.Locale;

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
    
    private NodeSet allNodes;
    private FolderMetaDataImpl metadata;
    private FolderHandler folderHandler;
    private int reservedType = RESERVED_FOLDER_NONE;
    
    private static final Logger log = LoggerFactory.getLogger(FolderImpl.class);

    protected static PermissionFactory pf;
    
    public static void setPermissionsFactory(PermissionFactory pf)
    {
        FolderImpl.pf = pf;
    }
    
    public FolderImpl( String path, FolderMetaDataImpl metadata, DocumentHandlerFactory handlerFactory,
                       FolderHandler folderHandler )
    {
        this.metadata = metadata;
        this.metadata.setParent(this);
        this.folderHandler = folderHandler;
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
        setPath(path);
        setReservedType();
        setHandlerFactory(handlerFactory);
        setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
    }

    public FolderImpl()
    {
        this.metadata = new FolderMetaDataImpl();
        this.metadata.setParent(this);
        setReservedType();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getSkin()
     */
    public String getSkin()
    {
        return metadata.getSkin();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setSkin(java.lang.String)
     */
    public void setSkin( String skinName )
    {
        metadata.setSkin(skinName);
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
            Folder parentFolder = (Folder)getParent();
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
    public String getDefaultDecorator( String fragmentType )
    {
        return metadata.getDefaultDecorator(fragmentType);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultDecorator(java.lang.String, java.lang.String)
     */
    public void setDefaultDecorator( String decoratorName, String fragmentType )
    {
        metadata.setDefaultDecorator(decoratorName, fragmentType);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDocumentOrder()
     */
    public List<String> getDocumentOrder()
    {
        return metadata.getDocumentOrder();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setDocumentOrder(java.util.List)
     */
    public void setDocumentOrder(List<String> docIndexes)
    {
        metadata.setDocumentOrder(docIndexes);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultPage()
     */
    public String getDefaultPage()
    {
        return metadata.getDefaultPage();
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

    /**
     * <p>
     * getFolders
     * </p>
     * 
     * @param checkAccess flag
     * @return folders node set
     * @throws DocumentException
     */
    public NodeSet getFolders(boolean checkAccess) throws DocumentException
    {
        // get list of all folders
        NodeSet folders = getAllNodes().subset(FOLDER_TYPE);

        // filter node set by access
        if (checkAccess)
        {
            folders = checkAccess(folders, JetspeedActions.VIEW);
        }
        return folders;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getFolders()
     */
    public NodeSet getFolders() throws DocumentException
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
        Folder folder = (Folder) getAllNodes().subset(FOLDER_TYPE).get(name);
        if (folder == null)
        {
            throw new FolderNotFoundException("Jetspeed PSML folder not found: " + name);
        }

        // check access
        if (checkAccess)
        {
            folder.checkAccess(JetspeedActions.VIEW);
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
            pages = checkAccess(pages, JetspeedActions.VIEW);
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
            page.checkAccess(JetspeedActions.VIEW);
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
     * getPageTemplates
     * </p>
     * 
     * @param checkAccess flag
     * @return pages node set
     * @throws NodeException
     */
    public NodeSet getPageTemplates(boolean checkAccess) throws NodeException
    {
        // get list of all template pages
        NodeSet pageTemplates = getAllNodes().subset(PageTemplate.DOCUMENT_TYPE);

        // filter node set by access
        if (checkAccess)
        {
            pageTemplates = checkAccess(pageTemplates, JetspeedActions.VIEW);
        }
        return pageTemplates;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPageTemplates()
     */
    public NodeSet getPageTemplates() throws NodeException
    {
        // by default enable access checks
        return getPageTemplates(true);
    }

    /**
     * <p>
     * getPageTemplate
     * </p>
     * 
     * @param name
     * @param checkAccess flag
     * @return page
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public PageTemplate getPageTemplate(String name, boolean checkAccess) throws PageNotFoundException, NodeException
    {
        // get page template
        PageTemplate pageTemplate = (PageTemplate) getAllNodes().subset(PageTemplate.DOCUMENT_TYPE).get(name);
        if (pageTemplate == null)
        {
            throw new PageNotFoundException("Jetspeed PSML page template not found: " + name);
        }

        // check access
        if (checkAccess)
        {
            pageTemplate.checkAccess(JetspeedActions.VIEW);
        }
        return pageTemplate;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPageTemplate(java.lang.String)
     */
    public PageTemplate getPageTemplate(String name) throws PageNotFoundException, NodeException
    {
        // by default enable access checks
        return getPageTemplate(name, true);
    }

    /**
     * <p>
     * getDynamicPages
     * </p>
     * 
     * @param checkAccess flag
     * @return pages node set
     * @throws NodeException
     */
    public NodeSet getDynamicPages(boolean checkAccess) throws NodeException
    {
        // get list of all dynamic pages
        NodeSet dynamicPages = getAllNodes().subset(DynamicPage.DOCUMENT_TYPE);

        // filter node set by access
        if (checkAccess)
        {
            dynamicPages = checkAccess(dynamicPages, JetspeedActions.VIEW);
        }
        return dynamicPages;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDynamicPages()
     */
    public NodeSet getDynamicPages() throws NodeException
    {
        // by default enable access checks
        return getDynamicPages(true);
    }

    /**
     * <p>
     * getDynamicPage
     * </p>
     * 
     * @param name
     * @param checkAccess flag
     * @return page
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public DynamicPage getDynamicPage(String name, boolean checkAccess) throws PageNotFoundException, NodeException
    {
        // get dynamic page
        DynamicPage dynamicPage = (DynamicPage) getAllNodes().subset(DynamicPage.DOCUMENT_TYPE).get(name);
        if (dynamicPage == null)
        {
            throw new PageNotFoundException("Jetspeed PSML dynamic page not found: " + name);
        }

        // check access
        if (checkAccess)
        {
            dynamicPage.checkAccess(JetspeedActions.VIEW);
        }
        return dynamicPage;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDynamicPage(java.lang.String)
     */
    public DynamicPage getDynamicPage(String name) throws PageNotFoundException, NodeException
    {
        // by default enable access checks
        return getDynamicPage(name, true);
    }
    
    /**
     * <p>
     * getFragmentDefinitions
     * </p>
     * 
     * @param checkAccess flag
     * @return pages node set
     * @throws NodeException
     */
    public NodeSet getFragmentDefinitions(boolean checkAccess) throws NodeException
    {
        // get list of all fragment definitions
        NodeSet fragmentDefinitions = getAllNodes().subset(FragmentDefinition.DOCUMENT_TYPE);

        // filter node set by access
        if (checkAccess)
        {
            fragmentDefinitions = checkAccess(fragmentDefinitions, JetspeedActions.VIEW);
        }
        return fragmentDefinitions;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getFragmentDefinitions()
     */
    public NodeSet getFragmentDefinitions() throws NodeException
    {
        // by default enable access checks
        return getFragmentDefinitions(true);
    }

    /**
     * <p>
     * getFragmentDefinition
     * </p>
     * 
     * @param name
     * @param checkAccess flag
     * @return page
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public FragmentDefinition getFragmentDefinition(String name, boolean checkAccess) throws PageNotFoundException, NodeException
    {
        // get fragment definition
        FragmentDefinition fragmentDefinition = (FragmentDefinition) getAllNodes().subset(FragmentDefinition.DOCUMENT_TYPE).get(name);
        if (fragmentDefinition == null)
        {
            throw new PageNotFoundException("Jetspeed PSML fragment definition not found: " + name);
        }

        // check access
        if (checkAccess)
        {
            fragmentDefinition.checkAccess(JetspeedActions.VIEW);
        }
        return fragmentDefinition;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getFragmentDefinition(java.lang.String)
     */
    public FragmentDefinition getFragmentDefinition(String name) throws PageNotFoundException, NodeException
    {
        // by default enable access checks
        return getFragmentDefinition(name, true);
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
            links = checkAccess(links, JetspeedActions.VIEW);
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
            link.checkAccess(JetspeedActions.VIEW);
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
            checkAccess(JetspeedActions.VIEW);
        }

        // get pageSecurity
        PageSecurity pageSecurity = (PageSecurity) getAllNodes( false ).subset(PageSecurity.DOCUMENT_TYPE).get(PageSecurity.DOCUMENT_TYPE);
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
    public NodeSet getAll() throws DocumentException
    {
        // return secure set of all nodes: enforce access checks
        // on folders and documents while creating filtered nodes
        NodeSet nodes = getAllNodes();
        NodeSet filteredNodes = null;
        for (Node node : nodes)
        {
            try
            {
                node.checkAccess(JetspeedActions.VIEW);
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
     */
    public NodeSet getAllNodes() throws DocumentException
    {
    	return getAllNodes( true );
    }
    
    protected synchronized NodeSet getAllNodes( boolean folderExistenceRequired ) throws DocumentException
    {
        if((allNodes == null) && (folderHandler != null))
        {            
            if(metadata.getDocumentOrder() != null)
            {
                if (getPath().endsWith(PATH_SEPARATOR))
                {
                    allNodes = new NodeSetImpl(getPath(), new NodeOrderComparator(metadata.getDocumentOrder(), getPath()));
                }
                else
                {
                    allNodes = new NodeSetImpl(getPath(), new NodeOrderComparator(metadata.getDocumentOrder(), getPath() + PATH_SEPARATOR));
                }
            }
            else
            {
                allNodes = new NodeSetImpl(getPath());
            }

            try
            {
                String[] nodeNames = folderHandler.listAll(getPath());
                for (int i = 0; i < nodeNames.length; i++)
                {
                    if (!nodeNames[i].equals(FolderMetaDataImpl.DOCUMENT_TYPE))
                    {
                        Node node = null;
                        try
                        {
                            if (getPath().endsWith(PATH_SEPARATOR))
                            {
                                String full = PageManagerUtils.concatenatePaths(getPath(), nodeNames[i]); 
                                if (!folderHandler.isFolder(full))
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
                                String full = PageManagerUtils.concatenatePaths(getPath(), nodeNames[i]); 
                                if (!folderHandler.isFolder(full))                                
                                //if(nodeNames[i].indexOf(".") > -1)
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
            }
            catch (FolderNotFoundException fnfe)
            {
            	if ( folderExistenceRequired )
            	{
            		log.error( "getAllNodes() unexpected missing folder: " + getPath(), fnfe );
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
     * @return implementation specific folder metadata
     */
    public FolderMetaDataImpl getFolderMetaData()
    {
        return metadata;
    }

    /**
     * <p>
     * setFolderHandler
     * </p>
     *
     * @param handler folder handler
     */
    public void setFolderHandler(FolderHandler handler)
    {
        this.folderHandler = handler;
    }

    /**
     * <p>
     * getMetadata
     * </p>
     *
     * @see org.apache.jetspeed.page.document.psml.AbstractNode#getMetadata()
     * @return metadata
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
     * @see org.apache.jetspeed.om.common.SecuredResource#getSecurityConstraints()
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
     * @see org.apache.jetspeed.om.common.SecuredResource#setSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
     * @param constraints
     */
    public void setSecurityConstraints(SecurityConstraints constraints)
    {
        metadata.setSecurityConstraints(constraints);
    }

    /**
     * getEffectivePageSecurity
     *
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseElement#getEffectivePageSecurity()
     */
    public PageSecurity getEffectivePageSecurity()
    {
        // return single page security if available
        PageSecurity pageSecurity = null;
        try
        {
            pageSecurity = getPageSecurity(false);
            if (pageSecurity != null)
            {
                return pageSecurity;
            }
        }
        catch (NodeException ne)
        {
        }
        catch (NodeNotFoundException nnfe)
        {
        }

        // delegate to parent folder implementation
        FolderImpl parentFolderImpl = (FolderImpl)getParent();
        if (parentFolderImpl != null)
        {
            return parentFolderImpl.getEffectivePageSecurity();
        }
        return null;
    }

    /**
     * <p>
     * checkPermissions
     * </p>
     *
     * @param path
     * @param mask
     * @param checkNodeOnly
     * @param checkParentsOnly
     * @throws SecurityException
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
        if (!checkNodeOnly && (getParent() != null))
        {
            ((AbstractNode)getParent()).checkPermissions(mask, false, false);
        }
    }

    /**
     * <p>
     * getTitle
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#getTitle(java.util.Locale)
     * @param locale
     * @return title in specified locale
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
     * @return title
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
     * @return short title in supplied locate
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
     * @return short title
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
     * @return type string
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
     * @return whether folder is hidden
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
     * @see org.apache.jetspeed.page.document.Node#setHidden(boolean)
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
    public List<MenuDefinition> getMenuDefinitions()
    {
        return metadata.getMenuDefinitions();
    }

    /**
     * newMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object for use in Folder
     */
    public MenuDefinition newMenuDefinition()
    {
        return new MenuDefinitionImpl();
    }

    /**
     * newMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object for use in Folder
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        return new MenuExcludeDefinitionImpl();
    }

    /**
     * newMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object for use in Folder
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        return new MenuIncludeDefinitionImpl();
    }

    /**
     * newMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object for use in Folder
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        return new MenuOptionsDefinitionImpl();
    }

    /**
     * newMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object for use in Folder
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        return new MenuSeparatorDefinitionImpl();
    }

    /**
     * setMenuDefinitions - set list of menu definitions
     *
     * @param definitions definition list
     */
    public void setMenuDefinitions(List<MenuDefinition> definitions)
    {
        metadata.setMenuDefinitions(definitions);
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     * @param generator id generator
     * @return dirty flag
     */
    public boolean unmarshalled(IdGenerator generator)
    {
        // notify super class implementation
        boolean dirty = super.unmarshalled(generator);

        // default title of pages to name
        if (getTitle() == null)
        {
            setTitle(getTitleName());
        }
        
        return dirty;
    }
    
    public boolean isReserved()
    {
        return (reservedType > RESERVED_FOLDER_NONE);
    }
    
    public int getReservedType()
    {
        return reservedType;
    }
    
    private void setReservedType()
    {
        String name = getName();
        if (name != null)
        {
            if (name.startsWith(RESERVED_SUBSITE_FOLDER_PREFIX))
            {
                reservedType = RESERVED_FOLDER_SUBSITES;
            }
            else if (name.startsWith(RESERVED_FOLDER_PREFIX))            
            {
                if (name.equals(RESERVED_USER_FOLDER_NAME))
                    reservedType = RESERVED_FOLDER_USERS;
                else if (name.equals(RESERVED_ROLE_FOLDER_NAME))
                    reservedType = RESERVED_FOLDER_ROLES;
                else if (name.equals(RESERVED_GROUP_FOLDER_NAME))
                    reservedType = RESERVED_FOLDER_GROUPS;
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
    
}
