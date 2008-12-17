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
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
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
     * Creates a new empty Page instance
     *
     * @return a newly created Page object
     */
    public Page newPage(String path);

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
     * ContentPage
     * </p>
     *
     * Returns a PSML document suitable for use in content
     * rendering, for the given key
     *
     * @see ContentPage
     * @see Fragment
     * @param locator The locator descriptor of the document to be retrieved.
     * @throws PageNotFoundException if the page cannot be found
     * @throws NodeException
     */
    public ContentPage getContentPage(String path) throws PageNotFoundException, NodeException;
    
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

    /** Update a page in persistent storage
     *
     * @param page The page to be updated.
     */
    public void updatePage(Page page) throws NodeException, PageNotUpdatedException;

    /** Remove a document.
     *
     * @param page The page to be removed.
     */
    public void removePage(Page page) throws NodeException, PageNotRemovedException;

    /** Update a folder and all child folders
     *  and documents in persistent storage
     *
     * @param folder The folder to be updated.
     */
    public void updateFolder(Folder folder) throws NodeException, FolderNotUpdatedException;

    /** Update a folder in persistent storage
     *
     * @param folder The folder to be updated.
     * @param deep Flag to control recursive deep updates.
     */
    public void updateFolder(Folder folder, boolean deep) throws NodeException, FolderNotUpdatedException;

    /** Remove a folder.
     *
     * @param page The folder to be removed.
     */
    public void removeFolder(Folder folder) throws NodeException, FolderNotRemovedException;

    /** Update a link in persistent storage
     *
     * @param link The link to be updated.
     */
    public void updateLink(Link link) throws NodeException, LinkNotUpdatedException;

    /** Remove a link.
     *
     * @param link The link to be removed.
     */
    public void removeLink(Link link) throws NodeException, LinkNotRemovedException;

    /** Update a page security document in persistent storage
     *
     * @param pageSecurity The document to be updated.
     */
    public void updatePageSecurity(PageSecurity pageSecurity) throws NodeException, FailedToUpdateDocumentException;

    /** Remove a page security document.
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
     * with the same portlet and fragment collection as the source
     * All fragments are created with new fragment ids
     * 
     * @param source The source Page object to be copied 
     * @param path a PSML normalized path to the new page to be created
     * @return a new Page object copied from the source, with new fragment ids
     */
    public Page copyPage(Page source, String path) 
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
     * with the same content as the source
     * All subobjects are created with new ids
     * 
     * @param source The source Folder object to be copied 
     * @param path a PSML normalized path to the new folder to be created
     * @return a new Folder object copied from the source, with new subobject ids
     */
    public Folder copyFolder(Folder source, String path) 
        throws NodeException;

    /** 
     * Copy the source fragment creating and returning a new copy of the fragment  
     * with the parameter collection as the source
     * The fragment is created with a new fragment id
     * 
     * @param source The source Fragment object to be copied 
     * @param the new fragment name, can be the same as source fragment name
     * @return a new Fragment object copied from the source
     */
    public Fragment copyFragment(Fragment source, String name) 
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
}
