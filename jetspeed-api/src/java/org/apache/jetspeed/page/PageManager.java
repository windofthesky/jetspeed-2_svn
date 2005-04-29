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

package org.apache.jetspeed.page;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.DocumentSet;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.jetspeed.profiler.ProfiledPageContext;

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
    public Page newPage();

    /**
     * Creates a new empty Fragment instance
     *
     * @return a newly created Fragment object
     */
    public Fragment newFragment();

    /**
     * Creates a new empty Property instance
     *
     * @return a newly created Property object
     */
    public Property newProperty();

   /**
    * 
    * <p>
    * getPage
    * </p>
    *
    * Returns a PSML document for the given key
    *
    * @param locator The locator descriptor of the document to be retrieved.
    * @throws PageNotFoundException if the page cannot be found
    * @throws NodeException
    */
    public Page getPage(String id) throws PageNotFoundException, NodeException;
    
    /**
     * 
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
    * 
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
    public Link getLink(String name) throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException;

   /**
    * 
    * <p>
    * getDocumentSet
    * </p>
    *
    * Returns a DocumentSet document for the given path
    *
    * @param name The path of the document to be retrieved.
    * @throws PageNotFoundException if the page cannot be found
    * @throws NodeException
    */
    public DocumentSet getDocumentSet(String name) throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException;
    
   /**
    * 
    * <p>
    * getPageSecurity
    * </p>
    *
    * Returns the PageSecurity document
    *
    * @throws PageNotFoundException if the page cannot be found
    * @throws NodeException
    */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException;
    
    /**
     * 
     * <p>
     * getFolder
     * </p>
     * Locates a folder for the given path.
     * @param folderPath
     * @return <code>Folder</code> object represented by the <code>folderPath</code> or
     * <code>null</code> if no such folder exists.
     * @throws DocumentException
     * @throws FolderNotFoundException
     * @throws NodeException
     * @throws InvalidFolderException
     */
    Folder getFolder(String folderPath) throws FolderNotFoundException, InvalidFolderException, NodeException;

    /**
     * <p>
     * Compute profiled page context elements based on named profile
     * locators associated with a session/principal in supplied
     * context instance.
     * </p>
     *
     * @param page
     * @throws PageNotFoundException if the page cannot be found.
     * @throws DocumentException
     * @throws NodeException
     */
    public void computeProfiledPageContext(ProfiledPageContext pageContext) throws PageNotFoundException, DocumentException, NodeException;

    /** Store the page on disk
     *
     * @param page The page to be stored.
     */
    public void registerPage(Page page) throws JetspeedException;

    /** Update a page in persistent storage
     *
     * @param page The page to be updated.
     */
    public void updatePage(Page page) throws JetspeedException, PageNotUpdatedException;

    /** Remove a document.
     *
     * @param page The page to be removed.
     */
    public void removePage(Page page) throws JetspeedException, PageNotRemovedException;

}