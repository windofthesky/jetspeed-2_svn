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

package org.apache.jetspeed.page;

import javax.security.auth.Subject;

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageFragment;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;

import java.util.List;


/**
 * This service is responsible for loading and saving Pages into
 * the selected persistent store.
 *
 * @version $Id$
 */
public interface PageManager 
{
    /** The name of the service */
    public String SERVICE_NAME = "PageManager";
    
    /**
     * user standard property scope
     */
    String USER_PROPERTY_SCOPE = FragmentProperty.USER_PROPERTY_SCOPE;

    /**
     * group standard property scope
     */
    String GROUP_PROPERTY_SCOPE = FragmentProperty.GROUP_PROPERTY_SCOPE;

    /**
     * role standard property scope
     */
    String ROLE_PROPERTY_SCOPE = FragmentProperty.ROLE_PROPERTY_SCOPE;

    /**
     * global standard property scope
     */
    String GLOBAL_PROPERTY_SCOPE = FragmentProperty.GLOBAL_PROPERTY_SCOPE;

    /**
     * all standard property scopes
     */
    String ALL_PROPERTY_SCOPE = "all";

    /**
     * group and role standard property scopes enabled flag
     */
    boolean GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED = FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED;

    /**
     * <p>
     * getConstraintsEnabled
     * </p>
     *
     * @return enabled indicator
     */
    public boolean getConstraintsEnabled();

    /**
     * <p>
     * getPermissionsEnabled
     * </p>
     *
     * @return enabled indicator
     */
    public boolean getPermissionsEnabled();

    /**
     * Get node reaping interval.
     *
     * @return reaping interval
     */
    public long getNodeReapingInterval();

    /**
     * Creates a new empty Page instance
     *
     * @return a newly created Page object
     */
    public Page newPage(String path);

    /**
     * Creates a new empty PageTemplate instance
     *
     * @return a newly created PageTemplate object
     */
    public PageTemplate newPageTemplate(String path);

    /**
     * Creates a new empty DynamicPage instance
     *
     * @return a newly created DynamicPage object
     */
    public DynamicPage newDynamicPage(String path);

    /**
     * Creates a new empty FragmentDefinition instance
     *
     * @return a newly created FragmentDefinition object
     */
    public FragmentDefinition newFragmentDefinition(String path);

    /**
     * Create a new empty Folder instance
     *
     * @return a newly created Folder object
     */
    public Folder newFolder(String path);

    /**
     * Creates a new empty Link instance
     *
     * @return a newly created Link object
     */
    public Link newLink(String path);

    /**
     * Creates a new empty PageSecurity instance
     *
     * @return a newly created PageSecurity object
     */
    public PageSecurity newPageSecurity();

    /**
     * Creates a new empty Layout Fragment instance
     *
     * @return a newly created Fragment object
     */
    public Fragment newFragment();

    /**
     * Creates a new empty Portlet Fragment instance
     *
     * @return a newly created Fragment object
     */    
    public Fragment newPortletFragment();
    
    /**
     * Creates a new FragmentReference instance
     *
     * @return a newly created Fragment object
     */    
    public FragmentReference newFragmentReference();
    
    /**
     * Creates a new PageFragment instance
     *
     * @return a newly created Fragment object
     */    
    public PageFragment newPageFragment();
    
    /**
     * newFolderMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object to be used in Folder
     */
    public MenuDefinition newFolderMenuDefinition();

    /**
     * newFolderMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object to be used in Folder
     */
    public MenuExcludeDefinition newFolderMenuExcludeDefinition();

    /**
     * newFolderMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object to be used in Folder
     */
    public MenuIncludeDefinition newFolderMenuIncludeDefinition();

    /**
     * newFolderMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object to be used in Folder
     */
    public MenuOptionsDefinition newFolderMenuOptionsDefinition();

    /**
     * newFolderMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object to be used in Folder
     */
    public MenuSeparatorDefinition newFolderMenuSeparatorDefinition();

    /**
     * newPageMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object to be used in Page
     */
    public MenuDefinition newPageMenuDefinition();

    /**
     * newPageMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object to be used in Page
     */
    public MenuExcludeDefinition newPageMenuExcludeDefinition();

    /**
     * newPageMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object to be used in Page
     */
    public MenuIncludeDefinition newPageMenuIncludeDefinition();

    /**
     * newPageMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object to be used in Page
     */
    public MenuOptionsDefinition newPageMenuOptionsDefinition();

    /**
     * newPageMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object to be used in Page
     */
    public MenuSeparatorDefinition newPageMenuSeparatorDefinition();

    /**
     * newSecurityConstraints - creates a new empty security constraints definition
     *
     * @return a newly created SecurityConstraints object
     */
    public SecurityConstraints newSecurityConstraints();

    /**
     * newFolderSecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object to be used in Folder
     */
    public SecurityConstraint newFolderSecurityConstraint();

    /**
     * newPageSecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object to be used in Page
     */
    public SecurityConstraint newPageSecurityConstraint();

    /**
     * newFragmentSecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object to be used in Fragment
     */
    public SecurityConstraint newFragmentSecurityConstraint();

    /**
     * newLinkSecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object to be used in Link
     */
    public SecurityConstraint newLinkSecurityConstraint();

    /**
     * newPageSecuritySecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object to be used in PageSecurity
     */
    public SecurityConstraint newPageSecuritySecurityConstraint();

    /**
     * newSecurityConstraintsDef - creates a new security constraints definition
     *
     * @return a newly created SecurityConstraintsDef object
     */
    public SecurityConstraintsDef newSecurityConstraintsDef();

    /**
     * newFragmentPreference - creates a new fragment preference
     *
     * @return a newly created FragmentPreference
     */
    public FragmentPreference newFragmentPreference();

    /**
     * newFragmentProperty - creates a new fragment property
     *
     * @return a newly created FragmentProperty
     */
    public FragmentProperty newFragmentProperty();

    /**
     * <p>
     * getPage
     * </p>
     *
     * Returns a Page based on its path
     *
     * @param path
     * @throws PageNotFoundException if the page cannot be found
     * @throws NodeException
     */
    public Page getPage(String path) throws PageNotFoundException, NodeException;
    
    /**
     * <p>
     * getPageTemplate
     * </p>
     *
     * Returns a PageTemplate based on its path
     *
     * @param path
     * @throws PageNotFoundException if the page cannot be found
     * @throws NodeException
     */
    public PageTemplate getPageTemplate(String path) throws PageNotFoundException, NodeException;

    /**
     * <p>
     * getDynamicPage
     * </p>
     *
     * Returns a DynamicPage based on its path
     *
     * @param path
     * @throws PageNotFoundException if the page cannot be found
     * @throws NodeException
     */
    public DynamicPage getDynamicPage(String path) throws PageNotFoundException, NodeException;

    /**
     * <p>
     * getFragmentDefinition
     * </p>
     *
     * Returns a FragmentDefinition based on its path
     *
     * @param path
     * @throws PageNotFoundException if the page cannot be found
     * @throws NodeException
     */
    public FragmentDefinition getFragmentDefinition(String path) throws PageNotFoundException, NodeException;

    /**
     * <p>
     * getLink
     * </p>
     *
     * Returns a Link document for the given path
     *
     * @param name The path of the document to be retrieved.
     * @throws PageNotFoundException if the page cannot be found
     * @throws NodeException
     */
    public Link getLink(String name) throws DocumentNotFoundException, UnsupportedDocumentTypeException, NodeException;

    /**
     * <p>
     * getPageSecurity
     * </p>
     *
     * Returns the PageSecurity document
     *
     * @throws DocumentNotFoundException if the document cannot be found
     * @throws UnsupportedDocumentTypeException
     * @throws NodeException
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException, UnsupportedDocumentTypeException, NodeException;
    
    /**
     * <p>
     * getFolder
     * </p>
     *
     * Locates a folder for the given path.
     *
     * @param folderPath
     * @return <code>Folder</code> object represented by the <code>folderPath</code>
     * @throws FolderNotFoundException
     * @throws NodeException
     * @throws InvalidFolderException
     */
    public Folder getFolder(String folderPath) throws FolderNotFoundException, InvalidFolderException, NodeException;

    /**
     * <p>
     * getFolders
     * </p>
     *
     * Locates folders within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getFolders(org.apache.jetspeed.om.folder.Folder)
     *
     * @param folder The parent folder.
     * @return A <code>NodeSet</code> containing all sub-folders
     *         directly under this folder.
     * @throws DocumentException
     */
    public NodeSet getFolders(Folder folder) throws DocumentException;

    /**
     * <p>
     * getFolder
     * </p>
     *
     * Locates folders within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getFolder(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     *
     * @param folder The parent folder.
     * @param name The name of folder to retrieve.
     * @return A Folder referenced by this folder.
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    public Folder getFolder(Folder folder, String name) throws FolderNotFoundException, DocumentException;

    /**
     * <p>
     * getPages
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getPages(org.apache.jetspeed.om.folder.Folder)
     *
     * @param folder The parent folder.
     * @return A <code>NodeSet</code> of all the Pages referenced
     *         by this Folder.
     * @throws NodeException
     */
    public NodeSet getPages(Folder folder) throws NodeException;
    
    /**
     * <p>
     * getPageTemplates
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getPageTemplates(org.apache.jetspeed.om.folder.Folder)
     *
     * @param folder The parent folder.
     * @return A <code>NodeSet</code> of all the PageTemplates referenced
     *         by this Folder.
     * @throws NodeException
     */
    public NodeSet getPageTemplates(Folder folder) throws NodeException;
    
    /**
     * <p>
     * getDynamicPages
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getDynamicPages(org.apache.jetspeed.om.folder.Folder)
     *
     * @param folder The parent folder.
     * @return A <code>NodeSet</code> of all the DynamicPages referenced
     *         by this Folder.
     * @throws NodeException
     */
    public NodeSet getDynamicPages(Folder folder) throws NodeException;
    
    /**
     * <p>
     * getFragmentDefinition
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getFragmentDefinition(org.apache.jetspeed.om.folder.Folder)
     *
     * @param folder The parent folder.
     * @return A <code>NodeSet</code> of all the FragmentDefinitions referenced
     *         by this Folder.
     * @throws NodeException
     */
    public NodeSet getFragmentDefinitions(Folder folder) throws NodeException;
    
    /**
     * <p>
     * getPage
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getPage(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     *
     * @param folder The parent folder.
     * @param name The name of page to retrieve.
     * @return A Page referenced by this folder.
     * @throws PageNotFoundException if the Page requested could not be found.
     * @throws NodeException
     */
    public Page getPage(Folder folder, String name) throws PageNotFoundException, NodeException;
    
    /**
     * <p>
     * getPageTemplate
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getPageTemplate(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     *
     * @param folder The parent folder.
     * @param name The name of page template to retrieve.
     * @return A PageTemplate referenced by this folder.
     * @throws PageNotFoundException if the PageTemplate requested could not be found.
     * @throws NodeException
     */
    public PageTemplate getPageTemplate(Folder folder, String name) throws PageNotFoundException, NodeException;
    
    /**
     * <p>
     * getDynamicPage
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getDynamicPage(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     *
     * @param folder The parent folder.
     * @param name The name of dynamic page to retrieve.
     * @return A DynamicPage referenced by this folder.
     * @throws PageNotFoundException if the DynamicPage requested could not be found.
     * @throws NodeException
     */
    public DynamicPage getDynamicPage(Folder folder, String name) throws PageNotFoundException, NodeException;
    
    /**
     * <p>
     * getFragmentDefinition
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getFragmentDefinition(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     *
     * @param folder The parent folder.
     * @param name The name of fragment definition to retrieve.
     * @return A DynamicPage referenced by this folder.
     * @throws PageNotFoundException if the FragmentDefinition requested could not be found.
     * @throws NodeException
     */
    public FragmentDefinition getFragmentDefinition(Folder folder, String name) throws PageNotFoundException, NodeException;
    
    /**
     * <p>
     * getLinks
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.Folder#getLinks(org.apache.jetspeed.om.folder.Folder)
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @param folder The parent folder.
     * @return NodeSet of all the Links referenced by this Folder.
     * @throws NodeException
     */    
    public NodeSet getLinks(Folder folder) throws NodeException;
    
    /**
     * <p>
     * getLink
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getLink(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     *
     * @param folder The parent folder.
     * @param name The name of page to retrieve.
     * @return A Link referenced by this folder.
     * @throws DocumentNotFoundException if the document requested could not be found.
     * @throws NodeException
     */    
    public Link getLink(Folder folder, String name) throws DocumentNotFoundException, NodeException;
    
    /**
     * <p>
     * getPageSecurity
     * </p>
     *
     * Locates documents within a specified parent folder.
     * Returned documents are filtered according to security
     * constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getPageSecurity(org.apache.jetspeed.om.folder.Folder)
     *
     * @param folder The parent folder.
     * @return A PageSecurity referenced by this folder.
     * @throws DocumentNotFoundException if the document requested could not be found.
     * @throws NodeException
     */    
    public PageSecurity getPageSecurity(Folder folder) throws DocumentNotFoundException, NodeException;

    /**
     * <p>
     * getAll
     * </p>
     *
     * Locates folders and documents within a specified parent folder.
     * Returned folders and documents are filtered according to
     * security constraints and/or permissions.
     *
     * @see org.apache.jetspeed.om.folder.Folder#getAll(org.apache.jetspeed.om.folder.Folder)
     *
     * @param folder The parent folder.
     * @return A <code>NodeSet</code> containing all sub-folders
     *         and documents directly under this folder.
     * @throws DocumentException
     */
    public NodeSet getAll(Folder folder) throws DocumentException;

    /**
     * Update a page in persistent storage
     *
     * @param page The page to be updated.
     */
    public void updatePage(Page page) throws NodeException, PageNotUpdatedException;

    /**
     * Remove a page.
     *
     * @param page The page to be removed.
     */
    public void removePage(Page page) throws NodeException, PageNotRemovedException;

    /**
     * Update a page template in persistent storage
     *
     * @param pageTemplate The page template to be updated.
     */
    public void updatePageTemplate(PageTemplate pageTemplate) throws NodeException, PageNotUpdatedException;

    /**
     * Remove a page template.
     *
     * @param pageTemplate The page template to be removed.
     */
    public void removePageTemplate(PageTemplate pageTemplate) throws NodeException, PageNotRemovedException;

    /**
     * Update a dynamic page in persistent storage
     *
     * @param dynamicPage The dynamic page to be updated.
     */
    public void updateDynamicPage(DynamicPage dynamicPage) throws NodeException, PageNotUpdatedException;

    /**
     * Remove a dynamic page.
     *
     * @param dynamicPage The dynamic page to be removed.
     */
    public void removeDynamicPage(DynamicPage dynamicPage) throws NodeException, PageNotRemovedException;

    /**
     * Update a fragment definition in persistent storage
     *
     * @param fragmentDefinition The fragment definition to be updated.
     */
    public void updateFragmentDefinition(FragmentDefinition fragmentDefinition) throws NodeException, PageNotUpdatedException;

    /**
     * Remove a fragment definition.
     *
     * @param fragmentDefinition The fragment definition to be removed.
     */
    public void removeFragmentDefinition(FragmentDefinition fragmentDefinition) throws NodeException, PageNotRemovedException;
    
    /**
     * Update fragment properties for specified scope. Implementation
     * may not be able to update fragment properties without updating
     * entire page. Edit page security constraints will not be checked
     * in these cases if the specified scope is user; this effectively
     * circumvents security checks, so this method should only be used
     * in places where the edits will be restricted to user fragment
     * properties.
     *
     * @param fragment owner of fragment properties to update.
     * @param scope fragment property scope to update, (user, group, role, global, or all).
     */
    public void updateFragmentProperties(BaseFragmentElement fragment, String scope) throws NodeException, PageNotUpdatedException;
    
    /**
     * Update a folder and all child folders
     * and documents in persistent storage
     *
     * @param folder The folder to be updated.
     */
    public void updateFolder(Folder folder) throws NodeException, FolderNotUpdatedException;

    /**
     * Update a folder in persistent storage
     *
     * @param folder The folder to be updated.
     * @param deep Flag to control recursive deep updates.
     */
    public void updateFolder(Folder folder, boolean deep) throws NodeException, FolderNotUpdatedException;

    /**
     * Remove a folder.
     *
     * @param page The folder to be removed.
     */
    public void removeFolder(Folder folder) throws NodeException, FolderNotRemovedException;

    /**
     * Update a link in persistent storage
     *
     * @param link The link to be updated.
     */
    public void updateLink(Link link) throws NodeException, LinkNotUpdatedException;

    /**
     * Remove a link.
     *
     * @param link The link to be removed.
     */
    public void removeLink(Link link) throws NodeException, LinkNotRemovedException;

    /**
     * Update a page security document in persistent storage
     *
     * @param pageSecurity The document to be updated.
     */
    public void updatePageSecurity(PageSecurity pageSecurity) throws NodeException, FailedToUpdateDocumentException;

    /**
     * Remove a page security document.
     *
     * @param pageSecurity The document to be removed.
     */
    public void removePageSecurity(PageSecurity pageSecurity) throws NodeException, FailedToDeleteDocumentException;

    /**
     * addListener - add page manager event listener
     *
     * @param listener page manager event listener
     */
    public void addListener(PageManagerEventListener listener);

    /**
     * removeListener - remove page manager event listener
     *
     * @param listener page manager event listener
     */
    public void removeListener(PageManagerEventListener listener);
    
    /**
     * reset - force subsequent refresh from persistent store 
     */
    public void reset();

    /**
     * shutdown - gracefully shutdown page manager and disconnect
     * from other singleton components, (e.g. shared caches) 
     */
    public void shutdown();

    /** 
     * Copy the source page creating and returning a new copy of the page  
     * with the same portlet and fragment collection as the source.
     * All fragments are created with new fragment ids.
     * 
     * @param source The source Page object to be copied 
     * @param path a PSML normalized path to the new page to be created
     * @return a new Page object copied from the source, with new fragment ids
     */
    public Page copyPage(Page source, String path) 
        throws NodeException;

    /** 
     * Copy the source page creating and returning a new copy of the page  
     * with the same portlet and fragment collection as the source.
     * 
     * @param source The source Page object to be copied 
     * @param path a PSML normalized path to the new page to be created
     * @param copyIds flag indicating whether to use new or copied ids
     * @return a new Page object copied from the source
     */
    public Page copyPage(Page source, String path, boolean copyIds)
        throws NodeException;

    /** 
     * Copy the source page template creating and returning a new copy of the page  
     * template with the same portlet and fragment collection as the source.
     * All fragments are created with new fragment ids.
     * 
     * @param source The source PageTemplate object to be copied 
     * @param path a PSML normalized path to the new page template to be created
     * @return a new PageTemplate object copied from the source, with new fragment ids
     */
    public PageTemplate copyPageTemplate(PageTemplate source, String path) 
        throws NodeException;

    /** 
     * Copy the source page template creating and returning a new copy of the page  
     * template with the same portlet and fragment collection as the source.
     * 
     * @param source The source PageTemplate object to be copied 
     * @param path a PSML normalized path to the new page template to be created
     * @param copyIds flag indicating whether to use new or copied ids
     * @return a new PageTemplate object copied from the source
     */
    public PageTemplate copyPageTemplate(PageTemplate source, String path, boolean copyIds)
        throws NodeException;

    /** 
     * Copy the source dynamic page creating and returning a new copy of the dynamic  
     * page with the same portlet and fragment collection as the source.
     * All fragments are created with new fragment ids.
     * 
     * @param source The source DynamicPage object to be copied 
     * @param path a PSML normalized path to the new dynamic page to be created
     * @return a new DynamicPage object copied from the source, with new fragment ids
     */
    public DynamicPage copyDynamicPage(DynamicPage source, String path) 
        throws NodeException;

    /** 
     * Copy the source dynamic page creating and returning a new copy of the dynamic  
     * page with the same portlet and fragment collection as the source.
     * 
     * @param source The source DynamicPage object to be copied 
     * @param path a PSML normalized path to the new dynamic page to be created
     * @param copyIds flag indicating whether to use new or copied ids
     * @return a new DynamicPage object copied from the source
     */
    public DynamicPage copyDynamicPage(DynamicPage source, String path, boolean copyIds)
        throws NodeException;

    /** 
     * Copy the source fragment definition creating and returning a new copy of  
     * the fragment definition with the same portlet and fragment collection as the
     * source. All fragments are created with new fragment ids.
     * 
     * @param source The source FragmentDefinition object to be copied 
     * @param path a PSML normalized path to the new fragment definition to be created
     * @return a new FragmentDefinition object copied from the source, with new fragment ids
     */
    public FragmentDefinition copyFragmentDefinition(FragmentDefinition source, String path) 
        throws NodeException;

    /** 
     * Copy the source fragment definition creating and returning a new copy of
     * the fragment definition with the same portlet and fragment collection as the source.
     * 
     * @param source The source FragmentDefinition object to be copied 
     * @param path a PSML normalized path to the new fragment definition to be created
     * @param copyIds flag indicating whether to use new or copied ids
     * @return a new FragmentDefinition object copied from the source
     */
    public FragmentDefinition copyFragmentDefinition(FragmentDefinition source, String path, boolean copyIds)
        throws NodeException;

    /** 
     * Copy the source link creating and returning a new copy of the link  
     * 
     * @param source The source Link object to be copied 
     * @param path a PSML normalized path to the new link to be created
     * @return a new Link object copied from the source
     */
    public Link copyLink(Link source, String path) 
        throws NodeException;

    /** 
     * Copy the source folder creating and returning a new copy of the folder
     * without copying any content from within the folder
     * 
     * @param source The source Folder object to be copied 
     * @param path a PSML normalized path to the new folder to be created
     * @return a new empty Folder object copied from the source
     */
    public Folder copyFolder(Folder source, String path) 
        throws NodeException;

    /** 
     * Copy the source fragment creating and returning a new copy of the fragment  
     * with the parameter collection as the source
     * The fragment is created with a new fragment id
     * 
     * @param source The source Fragment object to be copied 
     * @param name the new fragment name, can be the same as source fragment name
     *             or null to copy existing name
     * @return a new Fragment object copied from the source
     */
    public BaseFragmentElement copyFragment(BaseFragmentElement source, String name) 
        throws NodeException;

    /** 
     * Copy the source fragment creating and returning a new copy of the fragment  
     * with the parameter collection as the source
     * 
     * @param source The source Fragment object to be copied 
     * @param name the new fragment name, can be the same as source fragment name
     *             or null to copy existing name
     * @param copyIds flag indicating whether to use new or copied ids
     * @return a new Fragment object copied from the source
     */
    public BaseFragmentElement copyFragment(BaseFragmentElement source, String name, boolean copyIds) 
        throws NodeException;

    /**
     * Copy the source page security (both global constraints and constraint references)
     * creating and returning a new copy of the page security definition.
     *  
     * @param source The source PageSecurity definitions
     * @return the new page security object
     * @throws NodeException
     */
    public PageSecurity copyPageSecurity(PageSecurity source) 
        throws NodeException;
        
    /**
     * Deep copy a folder. Copies a folder and all subcontents including
     * other folders, subpages, links, menus, security, fragments. 
     *  
     * @param source source folder
     * @param dest destination folder
     * @param owner set owner of the new folder(s), or null for no owner
     * @throws NodeException if the root folder already exists
     */
    public void deepCopyFolder(Folder srcFolder, String destinationPath, String owner)
        throws NodeException;

    /**
     * Deep copy a folder. Copies a folder and all subcontents including
     * other folders, subpages, links, menus, security, fragments. 
     *  
     * @param source source folder
     * @param dest destination folder
     * @param owner set owner of the new folder(s), or null for no owner
     * @param copyIds flag indicating whether to use new or copied ids
     * @throws NodeException if the root folder already exists
     */
    public void deepCopyFolder(Folder srcFolder, String destinationPath, String owner, boolean copyIds)
        throws NodeException;

    /**
     * Deep merges a source folder into a destination folder. Copies a folder and all subcontents including
     * other folders, subpages, links, menus, security, fragments. If a destination resource already exists,
     * it is skipped over without error.
     *  
     * @param source source folder
     * @param dest destination folder
     * @param owner set owner of the new folder(s), or null for no owner
     */
    public void deepMergeFolder(Folder srcFolder, String destinationPath, String owner)
        throws NodeException;
    
    /**
     * Deep merges a source folder into a destination folder. Copies a folder and all subcontents including
     * other folders, subpages, links, menus, security, fragments. If a destination resource already exists,
     * it is skipped over without error.
     *  
     * @param source source folder
     * @param dest destination folder
     * @param owner set owner of the new folder(s), or null for no owner
     * @param copyIds flag indicating whether to use new or copied ids
     */
    public void deepMergeFolder(Folder srcFolder, String destinationPath, String owner, boolean copyIds)
        throws NodeException;
    
    /**
     * Retrieve a page for the given user name and page name
     * 
     * @param userName
     * @param pageName
     * @return
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public Page getUserPage(String userName, String pageName) 
        throws PageNotFoundException, NodeException;
    
    /**
     * Retrieve a user's folder
     * 
     * @param userName
     * @return
     * @throws FolderNotFoundException
     * @throws InvalidFolderException
     * @throws NodeException
     */
    public Folder getUserFolder(String userName) 
        throws FolderNotFoundException, InvalidFolderException, NodeException;
    
    /**
     * Check if a folder exists for the given folder name
     * 
     * @param folderName
     * @return
     */
    public boolean folderExists(String folderName);
    
    /**
     * Check if a page exists for the given page name
     * 
     * @param pageName
     * @return
     */
    public boolean pageExists(String pageName);
    
    /**
     * Check if a page template exists for the given page name
     * 
     * @param pageName
     * @return
     */
    public boolean pageTemplateExists(String pageName);
    
    /**
     * Check if a dynamic page exists for the given page name
     * 
     * @param pageName
     * @return
     */
    public boolean dynamicPageExists(String pageName);
    
    /**
     * Check if a fragment definition exists for the given name
     * 
     * @param name
     * @return
     */
    public boolean fragmentDefinitionExists(String name);
    
    /**
     * Check if a link exists for the given link name
     * 
     * @param linkName
     * @return
     */
    public boolean linkExists(String linkName);
    
    /**
     * Check if the root folder exists for a given user
     * 
     * @param userName
     * @return
     */
    public boolean userFolderExists(String userName);
    
    /**
     * Check if a page exists for the given user
     * 
     * @param userName
     * @param pageName
     * @return
     */
    public boolean userPageExists(String userName, String pageName);

    /**
     * Creates a user's home page from the roles of the current user.
     * The use case: when a portal is setup to use shared pages, but then
     * the user attempts to customize. At this point, we create the new page(s) for the user.
     * 
     * @param subject The full user Java Security subject.
     */
    public void createUserHomePagesFromRoles(Subject subject)
    throws NodeException;
    
    /**
     * 
     * @param pages
     * @return
     * @throws NodeException
     */
    public int addPages(Page[] pages)
    throws NodeException;
    
    /**
     * For a given security constraint definition name, and the given action(s),
     * make a constraint check for the current user subject
     * 
     * @param securityConstraintName the name of the security constraint definition
     * @param actions one or more portlet actions (view,edit,help,..)
     * @return
     */
    public boolean checkConstraint(String securityConstraintName, String actions);
    
    /**
     * Returns whether the page manager cache is currently part of a distributed
     * cache cluster.
     * 
     * @return distributed flag
     */
    public boolean isDistributed();

    /**
     * Notify page manager listeners that node modification was externally detected.
     *
     * @param node updated managed node if known
     */
    public void notifyUpdatedNode(Node node);
    
    /**
     * Cleanup request level cache for current thread.
     */
    public void cleanupRequestCache();
    
    /**
     * Supplemental and optional service supported by Page Managers. 
     * Fragment Property lists can be manipulated with this interface 
     */
    public FragmentPropertyManagement getFragmentPropertyManager();

    /**
     * Create list suitable for list model members.
     *
     * @param <T> list element type
     * @return list
     */
    public <T> List<T> createList();
}
