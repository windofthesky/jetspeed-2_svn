/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security;

import java.util.List;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.spi.AuthenticatedUser;

/**
 * <p>
 * Describes the interface for managing users and provides access to the
 * {@link User}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 */
public interface UserManager
{
    /**
     * @return the name of the anonymous user
     */
    String getAnonymousUser();
    
    User newUser(String name);
    
    User newUser(String name, boolean mapped);
    
    User newTransientUser(String name);
    
    /**
     * <p>
     * Add a new user provided a username and password.
     * </p>
     * <p>
     * If an external security storage manager is used, the user will be mapped/replicated to it as well.
     * </p>
     * @param username The user name.
     * @param password The password.
     * @throws Throws a security exception.
     */
    void addUser(String username, String password) throws SecurityException;

    /**
     * <p>
     * Add a new user provided a username and password and optionally map/replicate it to an external storage manager (if configured).
     * </p>
     * 
     * @param username The user name.
     * @param password The password.
     * @param mapped if the new User should be mapped/replicated to an external security storage manager (if used) or not.
     * @throws Throws a security exception.
     */
    void addUser(String username, String password, boolean mapped) throws SecurityException;

    
    /**
     * <p>
     * Import a new user with username and password and allow to bypass the enconding algorithm
     * </p>
     * 
     * @param username The user name.
     * @param password The password.
     * @param mapped if the new User should be mapped/replicated to an external security storage manager (if used) or not.
     * @param passThrough If true the provided password will not be validated/encoded
     * @throws Throws a security exception.
     */
    void addUser(String username, String password, boolean mapped, boolean passThrough) throws SecurityException;

    /**
     * <p>
     * Remove a user. If there user attributes associated with this user, they will be removed as well.
     * </p>
     * <p>
     * {@link java.security.Permission}for this user will be removed as well.
     * </p>
     * 
     * @param username The user name.
     * @throws Throws a security exception.
     */
    void removeUser(String username) throws SecurityException;

    /**
     * <p>
     * Whether or not a user exists.
     * </p>
     * 
     * @param username The user name.
     * @return Whether or not a user exists.
     */
    boolean userExists(String username);

    /**
     * <p>
     * Get a {@link User}for a given username.
     * </p>
     * 
     * @param username The username.
     * @return The {@link User}.
     * @throws Throws a security exception if the user cannot be found.
     */
    User getUser(String username) throws SecurityException;

    /**
     * <p>
     * Get a Subject for a given username.
     * </p>
     * 
     * @param username The username.
     * @return The Subject.
     * @throws Throws a security exception if the user cannot be found
     */
    Subject getSubject(String username) throws SecurityException;

    /**
     * <p>
     * Get a Subject for an (externally) authenticated user with (optionally) already provided credentials.
     * </p>
     * 
     * @param user The authenticated user.
     * @param mergeCredentials indicate if provided credentials should be merged with the Jetspeed Credentials for the user (if available).
     * @return The Subject.
     * @throws Throws a security exception if the user cannot be found
     */
    Subject getSubject(AuthenticatedUser user, boolean mergeCredentials) throws SecurityException;

    /**
     * <p>
     * Retrieves a {@link User} list matching the corresponding
     * user name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching users.
     * @return a list of {@link User}.
     */
    List<User> getUsers(String nameFilter) throws SecurityException;

    /**
     * <p>
     * Retrieves a List user names, finding users matching the corresponding
     * user name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching users.
     * @return A list of user names
     */
    List<String> getUserNames(String nameFilter) throws SecurityException;

    /**
     * <p>
     * Retrieves a {@link User} list of all the users in a specific role.
     * </p>
     * 
     * @param roleName The role name
     * @return A List of {@link User}.
     * @throws Throws a security exception if the role does not exist.
     */
    List<User> getUsersInRole(String roleName) throws SecurityException;
    
    /**
     * <p>Retrieves a {@link User} list of all the users in a specific group.</p>
     * @param groupName The group name
     * @return A list of {@link User}.
     * @throws Throws security exception if the group does not exist.
     */
    List<User> getUsersInGroup(String groupName) throws SecurityException;
    
    /**
     * Enable or disable a user.
     * @param userName The user name
     * @param enabled enabled flag for the user
     */
    void setUserEnabled(String userName, boolean enabled) throws SecurityException;

    /**
     * Updates a user and all attributes and associations
     * @param user
     * @throws SecurityException
     */
    void updateUser(User user) throws SecurityException;
    
    /**
     * <p>Retrieves a {@link User} list of all the users having a specific value for a specific attribute
     * @param attributeName
     * @param attributeValue
     * @return a List of users
     * @throws SecurityException
     */
    List<User> lookupUsers(String attributeName, String attributeValue) throws SecurityException;
    
    /**
     * Returns the current PasswordCredential for a User or a new one if the doesn't have one yet
     * @param user the user
     * @return null if the UserManager doesn't support PasswordCredentials
     */
    PasswordCredential getPasswordCredential(User user);
    
    void setPassword(User user, String oldPassword, String newPassword) throws SecurityException;
    
    void storePasswordCredential(PasswordCredential credential) throws SecurityException;
}