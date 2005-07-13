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
package org.apache.jetspeed.om.folder;

import java.util.List;

import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;

/**
 * Folder
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public interface Folder extends Node
{
    String FOLDER_TYPE = "folder";

    String FALLBACK_DEFAULT_PAGE = "default-page.psml";
    String PAGE_NOT_FOUND_PAGE = "page_not_found.psml";
    
    /**
     * 
     * <p>
     * getDefaultPage
     * </p>
     *
     * @param allowDefaulting flag to enable defaulting logic when not explicitly set
     * @return A String representing the default psml page for this folder
     */
    String getDefaultPage(boolean allowDefaulting);
    
    /**
     * 
     * <p>
     * setDefaultPage
     * </p>
     *
     * @param defaultPage
     */
    void setDefaultPage(String defaultPage);
    
    /**
     * 
     * <p>
     * getDefaultTheme
     * </p>
     *
     * @return A String representing the default theme for this Folder
     */
    String getDefaultTheme();
    
    /**
     * 
     * <p>
     * setDefaultTheme
     * </p>
     *
     * @param defaultTheme
     */
    void setDefaultTheme(String defaultTheme);
    
    /**
     * 
     * <p>
     * getFolders
     * </p>
     *
     * @return A <code>NodeSet</code> containing all sub-folders directly under
     * this folder.
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    NodeSet getFolders() throws FolderNotFoundException, DocumentException;
    
    /**
     * 
     * <p>
     * getFolder
     * </p>
     *
     * @param name
     * @return A Folder referenced by this folder.
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    Folder getFolder(String name) throws FolderNotFoundException, DocumentException;

    /**
     * 
     * <p>
     * getPages
     * </p>
     *
     * @return NodeSet of all the Pages referenced by this Folder.
     * @throws NodeException
     * @throws PageNotFoundException if any of the Pages referenced by this Folder
     * could not be found.
     */
    NodeSet getPages() throws NodeException;
    
    /**
     * 
     * <p>
     * getPage
     * </p>
     *
     * @param name
     * @return A Page referenced by this folder.
     * @throws PageNotFoundException if the Page requested could not be found.
     * @throws DocumentException
     * @throws NodeException
     */
    Page getPage(String name) throws PageNotFoundException, NodeException;
    
    /**
     * 
     * <p>
     * getLinks
     * </p>
     *
     * @return NodeSet of all the Links referenced by this Folder.
     * @throws DocumentException
     * @throws NodeException
     */    
    NodeSet getLinks() throws NodeException;
    
    /**
     * 
     * <p>
     * getLink
     * </p>
     *
     * @param name
     * @return A Link referenced by this folder.
     * @throws DocumentNotFoundException if the document requested could not be found.
     * @throws NodeException
     */    
    Link getLink(String name) throws DocumentNotFoundException, NodeException;
    
    /**
     * 
     * <p>
     * getPageSecurity
     * </p>
     *
     * @param name
     * @return A PageSecurity referenced by this folder.
     * @throws DocumentNotFoundException if the document requested could not be found.
     * @throws NodeException
     */    
    PageSecurity getPageSecurity() throws DocumentNotFoundException, NodeException;

    /**
     * 
     * <p>
     * getAll
     * </p>
     *
     * @return A <code>NodeSet</code> containing all sub-folders and documents directly under
     * this folder.
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    NodeSet getAll() throws FolderNotFoundException, DocumentException;

    /**
     * 
     * <p>
     * getFolderMetaData
     * </p>
     *
     * @return folder metadata
     */
    FolderMetaData getFolderMetaData();

    /**
     * getMenuDefinitions - get list of menu definitions
     *
     * @return definition list
     */
    List getMenuDefinitions();

    /**
     * setMenuDefinitions - set list of menu definitions
     *
     * @param definitions definition list
     */
    void setMenuDefinitions(List definitions);
    
    /**
     * Determines if a folder is a reserved folder.
     * Reserved folders are special folders that can
     * hold subsites, the root of user folders, and the
     * root of role folders.
     * @return
     */
    boolean isReserved();
    
    /**
     * Returns a valid reserved folder type:
     *  RESERVED_FOLDER_SUBSITES
     *  RESERVED_FOLDER_USERS
     *  RESERVED_FOLDER_ROLES
     *  RESERVED_FOLDER_GROUPS
     *  RESERVED_FOLDER_MEDIATYPE
     *  RESERVED_FOLDER_LANGUAGE
     *  RESERVED_FOLDER_COUNTRY
     *  
     * @return one of the valid reserved folder types
     */
    int getReservedType();

    int RESERVED_FOLDER_NONE = 0;    
    int RESERVED_FOLDER_SUBSITES = 1;
    int RESERVED_FOLDER_USERS = 2;
    int RESERVED_FOLDER_ROLES = 3;
    int RESERVED_FOLDER_GROUPS = 4;
    int RESERVED_FOLDER_MEDIATYPE = 5;
    int RESERVED_FOLDER_LANGUAGE = 6;
    int RESERVED_FOLDER_COUNTRY = 7;
    int RESERVED_FOLDER_OTHER = 9999;
    
}
