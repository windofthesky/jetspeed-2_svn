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
package org.apache.jetspeed.om.folder;

import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;

import java.util.List;

/**
 * Folder
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public interface Folder extends Node, SecuredResource
{
    String FOLDER_TYPE = "folder";

    String FALLBACK_DEFAULT_PAGE = "default-page.psml";
    String PAGE_NOT_FOUND_PAGE = "page_not_found.psml";
    
    String RESERVED_SUBSITE_FOLDER_PREFIX = "__";
    String RESERVED_FOLDER_PREFIX = "_";
    String RESERVED_USER_FOLDER_NAME = RESERVED_FOLDER_PREFIX + "user";
    String RESERVED_ROLE_FOLDER_NAME = RESERVED_FOLDER_PREFIX + "role";
    String RESERVED_GROUP_FOLDER_NAME = RESERVED_FOLDER_PREFIX + "group";
    String RESERVED_MEDIATYPE_FOLDER_NAME = RESERVED_FOLDER_PREFIX + "mediatype";
    String RESERVED_LANGUAGE_FOLDER_NAME = RESERVED_FOLDER_PREFIX + "language";
    String RESERVED_COUNTRY_FOLDER_NAME = RESERVED_FOLDER_PREFIX + "country";
    String RESERVED_USER_TEMPLATE_FOLDER_NAME = RESERVED_FOLDER_PREFIX + "template";

    String USER_FOLDER = PATH_SEPARATOR + RESERVED_USER_FOLDER_NAME + PATH_SEPARATOR;
    String ROLE_FOLDER = PATH_SEPARATOR + RESERVED_ROLE_FOLDER_NAME + PATH_SEPARATOR;
    String GROUP_FOLDER = PATH_SEPARATOR + RESERVED_GROUP_FOLDER_NAME + PATH_SEPARATOR;
    String MEDIATYPE_FOLDER = PATH_SEPARATOR + RESERVED_MEDIATYPE_FOLDER_NAME + PATH_SEPARATOR;
    String LANGUAGE_FOLDER = PATH_SEPARATOR + RESERVED_LANGUAGE_FOLDER_NAME + PATH_SEPARATOR;
    String COUNTRY_FOLDER = PATH_SEPARATOR + RESERVED_COUNTRY_FOLDER_NAME + PATH_SEPARATOR;
    String SUBSITE_ROOT_FOLDER = RESERVED_SUBSITE_FOLDER_PREFIX + "subsite-root" + PATH_SEPARATOR;
    String USER_TEMPLATE_FOLDER =  PATH_SEPARATOR + RESERVED_USER_TEMPLATE_FOLDER_NAME + PATH_SEPARATOR;

    int RESERVED_FOLDER_NONE = 0;    
    int RESERVED_FOLDER_SUBSITES = 1;
    int RESERVED_FOLDER_USERS = 2;
    int RESERVED_FOLDER_ROLES = 3;
    int RESERVED_FOLDER_GROUPS = 4;
    int RESERVED_FOLDER_MEDIATYPE = 5;
    int RESERVED_FOLDER_LANGUAGE = 6;
    int RESERVED_FOLDER_COUNTRY = 7;
    int RESERVED_FOLDER_OTHER = 9999;
   
    /**
     * Returns the name of the skin that applies to this
     * folder.
     *
     * @return the page default skin name
     */
    String getSkin();

    /**
     * Modifies the skin for this folder.
     *
     * @param skinName the name of the new skin for the folder
     */
    void setSkin(String skinName);

    /**
     * Returns the name of the default decorator as set here or
     * in parent folders that applies to page fragments
     * in this folder or subfolders.
     *
     * @param fragmentType the type of fragment considered
     * @return the decorator name for the selected type
     */
    String getEffectiveDefaultDecorator(String fragmentType);

    /**
     * Returns the name of the default decorator that applies to page
     * fragments in this folder or subfolders.
     *
     * @param fragmentType the type of fragment considered
     * @return the decorator name for the selected type
     */
    String getDefaultDecorator(String fragmentType);

    /**
     * Modifies the default decorator for the specified fragment type.
     *
     * @param decoratorName the name of the new decorator for the type
     * @param fragmentType the type of fragment considered
     */
    void setDefaultDecorator(String decoratorName, String fragmentType);

    /**
     * getDocumentOrder
     *
     * @return list of ordered document names in folder
     */
    List<String> getDocumentOrder();
    
    /**
     * setDocumentOrder
     *
     * @param docIndexes list of ordered document names in folder
     */
    void setDocumentOrder(List<String> docIndexes);

    /**
     * 
     * <p>
     * getDefaultPage
     * </p>
     *
     * @return A String representing the default psml page for this folder
     */
    String getDefaultPage();
    
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
     * getFolders
     * </p>
     *
     * @return A <code>NodeSet</code> containing all sub-folders directly under
     * this folder.
     * @throws DocumentException
     */
    NodeSet getFolders() throws DocumentException;
    
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
     * getPageTemplates
     * </p>
     *
     * @return NodeSet of all the PageTemplates referenced by this Folder.
     * @throws NodeException
     * @throws PageNotFoundException if any of the PageTemplates referenced by this Folder
     * could not be found.
     */
    NodeSet getPageTemplates() throws NodeException;
    
    /**
     * 
     * <p>
     * getDynamicPages
     * </p>
     *
     * @return NodeSet of all the DynamicPages referenced by this Folder.
     * @throws NodeException
     * @throws PageNotFoundException if any of the DynamicPages referenced by this Folder
     * could not be found.
     */
    NodeSet getDynamicPages() throws NodeException;
    
    /**
     * 
     * <p>
     * getFragmentDefinitions
     * </p>
     *
     * @return NodeSet of all the FragmentDefinitions referenced by this Folder.
     * @throws NodeException
     * @throws PageNotFoundException if any of the FragmentDefinitions referenced by this Folder
     * could not be found.
     */
    NodeSet getFragmentDefinitions() throws NodeException;
    
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
     * getPageTemplate
     * </p>
     *
     * @param name
     * @return A PageTemplate referenced by this folder.
     * @throws PageNotFoundException if the PageTemplate requested could not be found.
     * @throws DocumentException
     * @throws NodeException
     */
    PageTemplate getPageTemplate(String name) throws PageNotFoundException, NodeException;
    
    /**
     * 
     * <p>
     * getDynamicPage
     * </p>
     *
     * @param name
     * @return A DynamicPage referenced by this folder.
     * @throws PageNotFoundException if the DynamicPage requested could not be found.
     * @throws DocumentException
     * @throws NodeException
     */
    DynamicPage getDynamicPage(String name) throws PageNotFoundException, NodeException;
    
    /**
     * 
     * <p>
     * getFragmentDefinition
     * </p>
     *
     * @param name
     * @return A FragmentDefinition referenced by this folder.
     * @throws PageNotFoundException if the FragmentDefinition requested could not be found.
     * @throws DocumentException
     * @throws NodeException
     */
    FragmentDefinition getFragmentDefinition(String name) throws PageNotFoundException, NodeException;
    
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
     * @throws DocumentException
     */
    NodeSet getAll() throws DocumentException;

    /**
     * getMenuDefinitions - get list of menu definitions
     *
     * @return definition list
     */
    List<MenuDefinition> getMenuDefinitions();

    /**
     * newMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object for use in Folder
     */
    MenuDefinition newMenuDefinition();

    /**
     * newMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object for use in Folder
     */
    MenuExcludeDefinition newMenuExcludeDefinition();

    /**
     * newMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object for use in Folder
     */
    MenuIncludeDefinition newMenuIncludeDefinition();

    /**
     * newMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object for use in Folder
     */
    MenuOptionsDefinition newMenuOptionsDefinition();

    /**
     * newMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object for use in Folder
     */
    MenuSeparatorDefinition newMenuSeparatorDefinition();

    /**
     * setMenuDefinitions - set list of menu definitions
     *
     * @param definitions definition list
     */
    void setMenuDefinitions(List<MenuDefinition> definitions);
    
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
}
