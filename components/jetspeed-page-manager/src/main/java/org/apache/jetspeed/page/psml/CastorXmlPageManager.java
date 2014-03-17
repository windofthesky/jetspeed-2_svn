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

package org.apache.jetspeed.page.psml;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.folder.psml.FolderImpl;
import org.apache.jetspeed.om.folder.psml.MenuDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuExcludeDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuIncludeDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuOptionsDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.page.psml.AbstractBaseFragmentElement;
import org.apache.jetspeed.om.page.psml.AbstractBaseFragmentsElement;
import org.apache.jetspeed.om.page.psml.DynamicPageImpl;
import org.apache.jetspeed.om.page.psml.FragmentDefinitionImpl;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.FragmentPreferenceImpl;
import org.apache.jetspeed.om.page.psml.FragmentReferenceImpl;
import org.apache.jetspeed.om.page.psml.LinkImpl;
import org.apache.jetspeed.om.page.psml.PageFragmentImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PageSecurityImpl;
import org.apache.jetspeed.om.page.psml.PageTemplateImpl;
import org.apache.jetspeed.om.page.psml.PropertyImpl;
import org.apache.jetspeed.om.page.psml.SecurityConstraintsDefImpl;
import org.apache.jetspeed.om.page.psml.SecurityConstraintsImpl;
import org.apache.jetspeed.page.AbstractPageManager;
import org.apache.jetspeed.page.FolderNotUpdatedException;
import org.apache.jetspeed.page.FragmentPropertyList;
import org.apache.jetspeed.page.FragmentPropertyManagement;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageManagerSecurityUtils;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.PageNotUpdatedException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This service is responsible for loading and saving PSML pages serialized to
 * disk
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:weaver@apache.org">Scott T Weaver </a>
 * @version $Id$
 */
public class CastorXmlPageManager extends AbstractPageManager implements PageManager, FileCacheEventListener, FragmentPropertyManagement
{
    private final static Logger log = LoggerFactory.getLogger(CastorXmlPageManager.class);

    private static Map<String,Class<?>> modelClasses = new HashMap<String,Class<?>>();
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
        modelClasses.put("FragmentReferenceImpl", FragmentReferenceImpl.class);
        modelClasses.put("PageFragmentImpl", PageFragmentImpl.class);
        modelClasses.put("PageTemplateImpl", PageTemplateImpl.class);
        modelClasses.put("DynamicPageImpl", DynamicPageImpl.class);
        modelClasses.put("FragmentDefinitionImpl", FragmentDefinitionImpl.class);
        modelClasses.put("FragmentPropertyImpl", PropertyImpl.class);
    }

    private DocumentHandlerFactory handlerFactory;
    private FolderHandler folderHandler;
    private FileCache fileCache;

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory handlerFactory,
                                 FolderHandler folderHandler, FileCache fileCache,
                                 boolean permissionsEnabled, boolean constraintsEnabled ) throws FileNotFoundException
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
     * getPageTemplate
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPageTemplate(java.lang.String)
     * @param path
     * @return page template
     * @throws PageNotFoundException
     * @throws NodeException
     * @throws FolderNotFoundException
     */
    public PageTemplate getPageTemplate(String path) throws PageNotFoundException, NodeException
    {
        // get page template via folder, (access checked in Folder.getPageTemplate())
        try
        {
            FolderImpl folder = getNodeFolder(path);
            return folder.getPageTemplate(getNodeName(path));
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new PageNotFoundException(fnfe.getMessage());
        }
    }

    /**
     * <p>
     * getDynamicPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getDynamicPage(java.lang.String)
     * @param path
     * @return dynamic page
     * @throws PageNotFoundException
     * @throws NodeException
     * @throws FolderNotFoundException
     */
    public DynamicPage getDynamicPage(String path) throws PageNotFoundException, NodeException
    {
        // get dynamic page via folder, (access checked in Folder.getDynamicPage())
        try
        {
            FolderImpl folder = getNodeFolder(path);
            return folder.getDynamicPage(getNodeName(path));
        }
        catch (FolderNotFoundException fnfe)
        {
            throw new PageNotFoundException(fnfe.getMessage());
        }
    }

    /**
     * <p>
     * getFragmentDefinition
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getFragmentDefinition(java.lang.String)
     * @param path
     * @return dynamic page
     * @throws PageNotFoundException
     * @throws NodeException
     * @throws FolderNotFoundException
     */
    public FragmentDefinition getFragmentDefinition(String path) throws PageNotFoundException, NodeException
    {
        // get fragment definition via folder, (access checked in Folder.getDynamicPage())
        try
        {
            FolderImpl folder = getNodeFolder(path);
            return folder.getFragmentDefinition(getNodeName(path));
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
     * @see org.apache.jetspeed.page.PageManager#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws NodeException
    {
        PageImpl pageImpl = (PageImpl)page;
        updateFragmentsElement(pageImpl, Page.DOCUMENT_TYPE, true);
    }

    /**
     * <p>
     * updatePageTemplate
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#updatePageTemplate(org.apache.jetspeed.om.page.PageTemplate)
     */
    public void updatePageTemplate(PageTemplate pageTemplate) throws NodeException
    {
        PageTemplateImpl pageTemplateImpl = (PageTemplateImpl)pageTemplate;
        updateFragmentsElement(pageTemplateImpl, PageTemplate.DOCUMENT_TYPE, true);
    }

    /**
     * <p>
     * updateDynamicPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#updateDynamicPage(org.apache.jetspeed.om.page.DynamicPage)
     */
    public void updateDynamicPage(DynamicPage dynamicPage) throws NodeException
    {
        DynamicPageImpl dynamicPageImpl = (DynamicPageImpl)dynamicPage;
        updateFragmentsElement(dynamicPageImpl, DynamicPage.DOCUMENT_TYPE, true);
    }

    /**
     * <p>
     * updateFragmentDefinition
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#updateFragmentDefinition(org.apache.jetspeed.om.page.FragmentDefinition)
     */
    public void updateFragmentDefinition(FragmentDefinition fragmentDefinition) throws NodeException
    {
        FragmentDefinitionImpl fragmentDefinitionImpl = (FragmentDefinitionImpl)fragmentDefinition;
        updateFragmentsElement(fragmentDefinitionImpl, FragmentDefinition.DOCUMENT_TYPE, true);
    }

    /**
     * <p>
     * updateFragmentProperty
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#updateFragmentProperties(org.apache.jetspeed.om.page.BaseFragmentElement, java.lang.String)
     */
    public void updateFragmentProperties(BaseFragmentElement fragment, String scope) throws NodeException, PageNotUpdatedException
    {
        // fragment property writes not supported: lookup and
        // update entire page, dynamic page, page template, or
        // fragment definition; security is checked unless user
        // scope is specified
        AbstractBaseFragmentElement fragmentElement = (AbstractBaseFragmentElement)fragment;
        AbstractBaseFragmentsElement fragmentsElement = ((fragmentElement != null) ? fragmentElement.getBaseFragmentsElement() : null);
        boolean checkEditAccess = ((scope == null) || !scope.equals(USER_PROPERTY_SCOPE));
        if (fragmentsElement instanceof Page)
        {
            updateFragmentsElement(fragmentsElement, Page.DOCUMENT_TYPE, checkEditAccess);
        }
        else if (fragmentsElement instanceof DynamicPage)
        {
            updateFragmentsElement(fragmentsElement, DynamicPage.DOCUMENT_TYPE, checkEditAccess);
        }
        else if (fragmentsElement instanceof PageTemplate)
        {
            updateFragmentsElement(fragmentsElement, PageTemplate.DOCUMENT_TYPE, checkEditAccess);
        }
        else if (fragmentsElement instanceof FragmentDefinition)
        {
            updateFragmentsElement(fragmentsElement, FragmentDefinition.DOCUMENT_TYPE, checkEditAccess);
        }
        else
        {
            throw new PageNotUpdatedException("Unable to update fragment properties: no owning page");
        }
    }

    /**
     * <p>
     * updateFragmentsElement
     * </p>
     * 
     * @param fragmentsElement generic fragments/page element implementation
     * @param documentType document type
     * @throws NodeException thrown on update error
     */
    private void updateFragmentsElement(AbstractBaseFragmentsElement fragmentsElement, String documentType, boolean checkEditAccess) throws NodeException
    {
        // make sure path and related members are set
        if (fragmentsElement.getPath() != null)
        {
            if (!fragmentsElement.getPath().equals(fragmentsElement.getId()))
            {
                throw new NodeException("Fragments/page paths and ids must match!");
            }
        }
        else
        {
            throw new NodeException("Fragments/page paths and ids must be set!");
        }
        
        // validate fragments element
        if (!fragmentsElement.validateFragments())
        {
            throw new NodeException("Fragments hierarchy invalid for fragments/page: " + fragmentsElement.getPath() + ", not updated.");
        }

        try
        {
            // set parent
            boolean newPageElement = false;
            FolderImpl parentFolder = getNodeFolder(fragmentsElement.getPath());
            if (fragmentsElement.getParent() == null)
            {
                fragmentsElement.setParent(parentFolder);
                newPageElement = true;
            }

            // enable permissions/constraints
            fragmentsElement.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
            fragmentsElement.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
            
            // check for edit/view access
            fragmentsElement.checkAccess(checkEditAccess ? JetspeedActions.EDIT : JetspeedActions.VIEW);
            
            // update fragments/page
            handlerFactory.getDocumentHandler(documentType).updateDocument(fragmentsElement);
            
            // update parent folder
            if (parentFolder != null)
            {
                NodeSetImpl parentAllNodes = (NodeSetImpl)parentFolder.getAllNodes();
                if (!parentAllNodes.contains(fragmentsElement))
                {
                    // add new fragments/page element
                    parentAllNodes.add(fragmentsElement);
                    newPageElement = true;
                }
                else if (parentAllNodes.get(fragmentsElement.getPath()) != fragmentsElement)
                {
                    // remove stale fragments/page element and add updated element
                    parentAllNodes.remove(fragmentsElement);
                    parentAllNodes.add(fragmentsElement);
                }
            }
            
            // notify page manager listeners
            if (newPageElement)
            {
                notifyNewNode(fragmentsElement);
            }
            else
            {
                notifyUpdatedNode(fragmentsElement);
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
     * @see org.apache.jetspeed.page.PageManager#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page) throws NodeException
    {
        PageImpl pageImpl = (PageImpl)page;
        removeFragmentsElement(pageImpl, Page.DOCUMENT_TYPE);
    }

    /**
     * <p>
     * removePageTemplate
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#removePageTemplate(org.apache.jetspeed.om.page.PageTemplate)
     */
    public void removePageTemplate(PageTemplate pageTemplate) throws NodeException
    {
        PageTemplateImpl pageTemplateImpl = (PageTemplateImpl)pageTemplate;
        removeFragmentsElement(pageTemplateImpl, PageTemplate.DOCUMENT_TYPE);
    }

    /**
     * <p>
     * removeDynamicPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#removeDynamicPage(org.apache.jetspeed.om.page.DynamicPage)
     */
    public void removeDynamicPage(DynamicPage dynamicPage) throws NodeException
    {
        DynamicPageImpl dynamicPageImpl = (DynamicPageImpl)dynamicPage;
        removeFragmentsElement(dynamicPageImpl, DynamicPage.DOCUMENT_TYPE);        
    }

    /**
     * <p>
     * removeFragmentDefinition
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#removeFragmentDefinition(org.apache.jetspeed.om.page.FragmentDefinition)
     */
    public void removeFragmentDefinition(FragmentDefinition fragmentsDefinition) throws NodeException
    {
        FragmentDefinitionImpl fragmentsDefinitionImpl = (FragmentDefinitionImpl)fragmentsDefinition;
        removeFragmentsElement(fragmentsDefinitionImpl, FragmentDefinition.DOCUMENT_TYPE);        
    }

    /**
     * <p>
     * removeFragmentsElement
     * </p>
     * 
     * @param fragmentsElement generic fragments/page element implementation
     * @param documentType document type
     * @throws NodeException thrown on remove error
     */
    private void removeFragmentsElement(AbstractBaseFragmentsElement fragmentsElement, String documentType) throws NodeException
    {
        // check for edit access
        fragmentsElement.checkAccess(JetspeedActions.EDIT);

        try
        {
            FolderImpl folder = getNodeFolder(fragmentsElement.getPath());

            // remove fragments/page
            handlerFactory.getDocumentHandler(documentType).removeDocument(fragmentsElement);
            
            // update folder
            ((NodeSetImpl)folder.getAllNodes()).remove(fragmentsElement);
            
            // notify page manager listeners
            notifyRemovedNode(fragmentsElement);
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
     * @see org.apache.jetspeed.page.PageManager#updateLink(org.apache.jetspeed.om.page.Link)
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
     * @see org.apache.jetspeed.page.PageManager#removeLink(org.apache.jetspeed.om.page.Link)
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

    public boolean checkConstraint(String securityConstraintName, String actions)
    {
        try
        {
            PageSecurity security = this.getPageSecurity();
            SecurityConstraintsDef def = security.getSecurityConstraintsDef(securityConstraintName);
            if (def != null)
            {
                return PageManagerSecurityUtils.checkConstraint(def, actions);
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return false;
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
     * @see org.apache.jetspeed.page.PageManager#getPageTemplates(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getPageTemplates(Folder folder) throws NodeException
    {
        // delegate back to folder instance
        return folder.getPageTemplates();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageTemplate(org.apache.jetspeed.om.folder.Folder, java.lang.String)
     */
    public PageTemplate getPageTemplate(Folder folder, String name) throws PageNotFoundException, NodeException
    {
        // delegate back to folder instance
        return folder.getPageTemplate(name);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getDynamicPages(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getDynamicPages(Folder folder) throws NodeException
    {
        // delegate back to folder instance
        return folder.getDynamicPages();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getDynamicPage(org.apache.jetspeed.om.folder.Folder, java.lang.String)
     */
    public DynamicPage getDynamicPage(Folder folder, String name) throws PageNotFoundException, NodeException
    {
        // delegate back to folder instance
        return folder.getDynamicPage(name);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFragmentDefinitions(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getFragmentDefinitions(Folder folder) throws NodeException
    {
        // delegate back to folder instance
        return folder.getFragmentDefinitions();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFragmentDefinition(org.apache.jetspeed.om.folder.Folder, java.lang.String)
     */
    public FragmentDefinition getFragmentDefinition(Folder folder, String name) throws PageNotFoundException, NodeException
    {
        // delegate back to folder instance
        return folder.getFragmentDefinition(name);
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
     * @see org.apache.jetspeed.page.PageManager#updateFolder(org.apache.jetspeed.om.folder.Folder)
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
     * updateFolderNodes - recursively update all folder nodes
     *
     * @param folderImpl folder whose nodes are to be updated
     * @throws FolderNotUpdatedException
     */
    private void updateFolderNodes(FolderImpl folderImpl) throws FolderNotUpdatedException
    {
        try
        {
            // update folder documents
            NodeSet nodes = folderImpl.getAllNodes();
            for (Node node : nodes)
            {
                if (node instanceof Page)
                {
                    updatePage((Page)node);
                }
                else if (node instanceof PageTemplate)
                {
                    updatePageTemplate((PageTemplate)node);
                }
                else if (node instanceof DynamicPage)
                {
                    updateDynamicPage((DynamicPage)node);
                }
                else if (node instanceof FragmentDefinition)
                {
                    updateFragmentDefinition((FragmentDefinition)node);
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
            for (Node node : nodes)
            {
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
     * @see org.apache.jetspeed.page.PageManager#removeFolder(org.apache.jetspeed.om.folder.Folder)
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
        // refreshes from persistent store
        fileCache.evictAll();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#shutdown()
     */
    public void shutdown()
    {
        // propagate to super
        super.shutdown();
        // disconnect cache listener
        fileCache.removeListener(this);
        // propagate to handlers
        handlerFactory.shutdown();
        folderHandler.shutdown();
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

	public FragmentPropertyManagement getFragmentPropertyManager() 
	{
		return this;
	}

	public FragmentPropertyList getFragmentPropertyList(
			BaseFragmentElement baseFragmentElement,
			FragmentPropertyList transientList) 
	{
		return null;
	}

	public void removeFragmentPropertyList(
			BaseFragmentElement baseFragmentElement,
			FragmentPropertyList transientList) 
	{
	}

	public void updateFragmentPropertyList(
			BaseFragmentElement baseFragmentElement, String scope,
			FragmentPropertyList transientList) 
	{
	}

    public int addPages(Page[] pages)
    throws NodeException
    {   
        if (pages.length > 0 && pages[0].getPath().equals("/tx__test1.psml"))
        {
            // for tx testing
            log.debug("Adding first page");
            this.updatePage(pages[0]);
            log.debug("Adding second page");
            this.updatePage(pages[1]);
            log.debug("About to throw ex");
            throw new NodeException("Its gonna blow captain!");
        }
        for (int ix = 0; ix < pages.length; ix++)
        {
            this.updatePage(pages[ix]);
        }
        return pages.length;
    }

    /**
     * Create list suitable for list model members.
     *
     * @param <T> list element type
     * @return list
     */
    public <T> List<T> createList()
    {
        return Collections.synchronizedList(new ArrayList<T>());
    }
}
