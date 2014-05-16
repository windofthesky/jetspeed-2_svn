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

import javax.security.auth.Subject;
import java.util.List;


/**
 * <p>
 * Describes the interface for managing users and provides access to the
 * {@link User}.
 * </p>
 * 
 * @version $Id$
 */
public interface UserManager extends PrincipalTypeManager
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
     * Add a new user
     * </p>
     * <p>
     * If an external security storage manager is used, the user will be mapped/replicated to it as well.
     * </p>
     * @param userName The user name.
     * @return the new {@link User}
     * @throws throws a security exception.
     */
    User addUser(String userName) throws SecurityException;

    /**
     * <p>
     * Add a new user and optionally map/replicate it to an external storage manager (if configured).
     * </p>
     * 
     * @param userName The user name.
     * @param mapped if the new User should be mapped/replicated to an external security storage manager (if used) or not.
     * @return the new {@link User}
     * @throws throws a security exception.
     */
    User addUser(String userName, boolean mapped) throws SecurityException;

    
    /**
     * <p>
     * Remove a user. If there user attributes associated with this user, they will be removed as well.
     * </p>
     * <p>
     * {@link java.security.Permission}for this user will be removed as well.
     * </p>
     * 
     * @param userName The user name.
     * @throws throws a security exception.
     */
    void removeUser(String userName) throws SecurityException;

    /**
     * <p>
     * Whether or not a user exists.
     * </p>
     * 
     * @param userName The user name.
     * @return Whether or not a user exists.
     */
    boolean userExists(String userName);

    /**
     * <p>
     * Get a {@link User}for a given user name.
     * </p>
     * 
     * @param userName The user name.
     * @return The {@link User}.
     * @throws throws a security exception if the user cannot be found.
     */
    User getUser(String userName) throws SecurityException;

    /**
     * <p>
     * Get a Subject for an user.
     * </p>
     * <p>
     * The Subject credentials (.e.g. PasswordCredential) will be retrieved
     * by the Jetspeed security provider itself (if configured).
     * </p>
     * <p>
     * If an external authentication provider is used supplying custom credentials
     * the method {@link #getSubject(AuthenticatedUser)} should be used instead. 
     * </p>
     * 
     * @param user The user.
     * @return The Subject.
     * @throws throws a security exception if the user cannot be found
     */
    Subject getSubject(User user) throws SecurityException;

    /**
     * <p>
     * Get a Subject for an (possibly externally) authenticated user with (optionally) provided credentials.
     * </p>
     * 
     * @param user The authenticated user.
     * @return The Subject.
     * @throws throws a security exception if the user cannot be found
     */
    Subject getSubject(AuthenticatedUser user) throws SecurityException;

    /**
     * <p>
     * Retrieves a detached and modifiable {@link User} list matching the corresponding
     * user name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching users.
     * @return a list of {@link User}.
     */
    List<User> getUsers(String nameFilter) throws SecurityException;

    
    /**
     * <p>
     * Retrieves a detached and modifiable {@link User} list matching the corresponding
     * query context. It returns a {@link UserResultList}, containing
     * the actual result list an the total number of results from the query.
     * 
     * </p>
     * 
     * @param queryContext The (@see JetspeedPrincipalQueryContext) for this query.
     * @return
     * @throws SecurityException
     */
    UserResultList getUsersExtended(JetspeedPrincipalQueryContext queryContext) throws SecurityException;
    
    /**
     * <p>
     * Retrieves a a detached and modifiable List user names, finding users matching the corresponding
     * user name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching users.
     * @return A list of user names
     */
    List<String> getUserNames(String nameFilter) throws SecurityException;

    /**
     * <p>
     * Retrieves a a detached and modifiable {@link User} list of all the users in a specific role.
     * </p>
     * 
     * @param roleName The role name
     * @return A List of {@link User}.
     * @throws throws a security exception if the role does not exist.
     */
    List<User> getUsersInRole(String roleName) throws SecurityException;
    
    /**
     * <p>Retrieves a a detached and modifiable {@link User} list of all the users in a specific group.</p>
     * @param groupName The group name
     * @return A list of {@link User}.
     * @throws throws security exception if the group does not exist.
     */
    List<User> getUsersInGroup(String groupName) throws SecurityException;
    
    /**
     * Updates a user and all its attributes
     * @param user
     * @throws SecurityException
     */
    void updateUser(User user) throws SecurityException;
    
    /**
     * <p>Retrieves a a detached and modifiable {@link User} list of all the users having a specific value for a specific attribute
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
    PasswordCredential getPasswordCredential(User user) throws SecurityException;
    
    void storePasswordCredential(PasswordCredential credential) throws SecurityException;
}