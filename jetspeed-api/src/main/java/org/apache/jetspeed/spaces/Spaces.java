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
package org.apache.jetspeed.spaces;

import java.util.List;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;

/**
 * Spaces Services
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Spaces
{
	public static final String ENVIRONMENTS_LOCATION = "/_environments/";	
	public static final String DEFAULT_ENVIRONMENT = "default-environment";
	public static final String DEFAULT_SPACE = "default-space";
	
	/**
	 * List all environments in the portal
	 * 
	 * @return a list of zero or more environment objects
	 */
    List<Environment> listEnvironments();
    
    /**
     * Creates an environment object and persists it
     * 
     * @param envName the unique name of the environment to create
     * @return a new environment object, not yet persisted
     * @throws a SpacesException in case the object could not be persisted
     */
    Environment createEnvironment(String envName, String owner) throws SpacesException;
    
    /**
     * Store an environment to the portal
     * 
     * @param env the environment object to be persisted
     * @throws a SpacesException in case the object could not be persisted
     */
    void storeEnvironment(Environment env) throws SpacesException;
    
    /**
     * Deletes an environment object given the unique key envName from the persistence store
     * 
     * @param env the environment object to delete
     * @throws a SpacesException in case the object could not be deleted
     */
    void deleteEnvironment(Environment env) throws SpacesException;

    /**
     * Looks up an environment object given the unique key envName from the persistence store
     * 
     * @param envName the unique name of the environment object to lookup
     * @return the found environment object from the persistent store, or null if not found
     */
    Environment lookupEnvironment(String envName);
    
    /**
     * Lists all unique spaces in the portal, regardless of environment
     *  
     * @return the list of all spaces in the portal 
     */
    List<Space> listSpaces();
    
    /**
     * Lists all spaces for a given environment
     * 
     * @param envName filter the space list result by the envName 
     * @return a list of spaces for the given environment name
     */
    List<Space> listSpaces(String envName);

    /**
     * Setup and create a new space folder area cloning entire contents of template folder
     * 
     * @param space the space not yet persisted, should be created with newSpace
     * @param templateFolder the template to use to create the space folder from
     * @return a newly created space representing the space folder
     * @throws a SpacesException in case the object could not be persisted
     */
    Space storeSpaceFromTemplate(Space space, Folder templateFolder) throws SpacesException;

    /**
     * Creates a space in memory object, not persisted 
     * 
     * @param spaceName the name of the space to create (not the path to the space)
     * @param owner the owner (user) of the space
     * @return a newly created space 
     */
    Space newSpace(String spaceName, String owner) throws SpacesException;
    
    /**
     * Stores a space to the portal
     * 
     * @param space the space object to be persisted
     * @return the space object with any updated persistence state
     * @throws a SpacesException in case the object could not be persisted
     */
    void storeSpace(Space space) throws SpacesException;
        
    /**
     * Deletes a space object given the unique key spaceName from the persistence store
     * 
     * @param space the space object to delete
     * @throws a SpacesException in case the object could not be deleted
     */
    void deleteSpace(Space space) throws SpacesException;

    /**
     * Looks up a space object given the unique key spaceName from the persistence store
     * 
     * @param spaceName the unique name of the space object to lookup
     * @return the found space object from the persistent store, or null if not found
     */
    Space lookupSpace(String spaceName);
    
    /**
     * Retrieve the user space for a user
     * @param username
     * @return the user space for a user
     */
    Space lookupUserSpace(String username);
    
    /**
     * Adds a space to the list of spaces for the given environment
     * A space can exist in zero or more environments
     * 
     * @param space
     * @param env
     * @throws a SpacesException in case the object could not be added
     */
    void addSpaceToEnvironment(Space space, Environment env) throws SpacesException;

    /**
     * Removes a space from the list of spaces for the given environment
     * 
     * @param space
     * @param env
     * @throws a SpacesException in case the object could not be added
     */
    void removeSpaceFromEnvironment(Space space, Environment env) throws SpacesException;

    /**
     * Determines if a space is a member of the set of spaces for a given environment
     * @param space
     * @param env
     * @return true if the space is a member of the environment, false if it is not
     */
    boolean isSpaceInEnvironment(Space space, Environment env);
        
    /**
     * Deletes a page 
     * 
     * @param page the page to be deleted
     * @throws a SpacesException in case the object could not be deleted
     */
    void deletePage(Page page) throws SpacesException;
    
    /**
     * List all pages in the given space
     *  
     * @param space the space to filter the set of pages by
     * @return a list of zero or more page objects found in the given space
     */
    List<Page> listPages(Space space);
    
    /**
     * List all links in the given space
     *  
     * @param space the space to filter the set of links by
     * @return a list of zero or more links objects found in the given space
     */
    List<Link> listLinks(Space space);

    /**
     * List all folders in the given space
     *  
     * @param space the space to filter the set of folders by
     * @return a list of zero or more folder objects found in the given space
     */
    List<Folder> listFolders(Space space);

    
}
