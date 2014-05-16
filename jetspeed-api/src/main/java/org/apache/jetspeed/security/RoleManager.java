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

/**
 * <p>Describes the service interface for managing roles.</p>
 * @version $Id$
 */
public interface RoleManager extends PrincipalTypeManager
{
    Role newRole(String name, boolean mapped);
    Role newTransientRole(String name);
    
    /**
     * <p>
     * Add a new role
     * </p>
     * <p>
     * If an external security storage manager is used, the role will be mapped/replicated to it as well.
     * </p>
     * @param roleName The role name
     * @return the new {@link Role}
     * @throws throws a security exception if the role already exists.
     */
    Role addRole(String roleName) throws SecurityException;

    /**
     * <p>
     * Add a new role and optionally map/replicate it to an external storage manager (if configured).
     * </p>
     * 
     * @param roleName The role name.
     * @param mapped if the new Role should be mapped/replicated to an external security storage manager (if used) or not.
     * @return the new {@link Role}
     * @throws throws a security exception if the role already exists.
     */
    Role addRole(String roleName, boolean mapped) throws SecurityException;

    /**
     * <p>Remove a given role</p>
     * @param roleName
     * @throws throws a security exception.
     */
    void removeRole(String roleName) throws SecurityException;

    /**
     * <p>Whether or not a role exists.</p>
     * @param roleName 
     * @return Whether or not a role exists.
     */
    boolean roleExists(String roleName);

    /**
     * <p>Get a role {@link Role} for a given role name.
     * @param roleName The role name
     * @return The {@link Role}.
     * @throws throws a security exception if the role does not exist.
     */
    Role getRole(String roleName) throws SecurityException;

    /**
     * <p>Retrieves a detached and modifiable {@link Role} list of all the roles
     * associated to a specific user.</p>
     * @param username The user name.
     * @return A List of {@link Role}.
     * @throws throws a security exception if the user does not exist.
     */
    List<Role> getRolesForUser(String username) throws SecurityException;

    /**
     * <p>Retrieves a detached and modifiable {@link Role} list of all the roles
     * associated to a specific group.</p>
     * @param groupName The group name
     * @return A Collection of {@link Role}.
     * @throws throws a security exception if the group does not exist.
     */
    List<Role> getRolesInGroup(String groupName) throws SecurityException;
    
    /**
     * <p>Add a role to a user.</p>
     * @param username The user name
     * @param roleName The role name
     * @throws throws a security exception if the role or the user do not exist.
     */
    void addRoleToUser(String username, String roleName) throws SecurityException;

    /**
     * <p>Remove a user from a role.</p>
     * @param username The user name.
     * @param roleName The role name
     * @throws throws a security exception.
     */
    void removeRoleFromUser(String username, String roleName) throws SecurityException;

    /**
     * <p>Whether or not a user is in a role.</p>
     * @param username The user name.
     * @param roleName The role name 
     * @return Whether or not a user is in a role.
     * @throws throws a security exception if the role or the user does not exist.
     */
    boolean isUserInRole(String username, String roleName) throws SecurityException;

    /**
     * <p>Add a role to a group.</p>
     * @param roleName The role name
     * @param groupName The group name 
     * @throws throws a security exception.
     */
    void addRoleToGroup(String roleName, String groupName) throws SecurityException;

    /**
     * <p>Remove a role from a group.</p>
     * @param roleName The role name
     * @param groupName The group name
     * @throws throws a security exception.
     */
    void removeRoleFromGroup(String roleName, String groupName) throws SecurityException;

    /**
     * <p>Whether or not a role is in a group.</p>
     * @param groupName The group name
     * @param roleName The role name
     * @return Whether or not a role is in a group.
     * @throws throws a security exception if the role or the group does not exist.
     */
    boolean isGroupInRole(String groupName, String roleName) throws SecurityException;

    /**
     * Retrieves a detached and modifiable {@link Role} list matching the corresponding
     * role name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching roles.
     * @return a list of {@link Role} 
     */
    List<Role> getRoles(String nameFilter) throws SecurityException;
    
    /**
     * <p>
     * Retrieves a detached and modifiable List of role names, finding roles matching the corresponding
     * role name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching roles.
     * @return A list of role names
     */
    List<String> getRoleNames(String nameFilter) throws SecurityException;

    /**
     * Updates a role and all its attributes
     * @param role
     * @throws SecurityException
     */
    void updateRole(Role role) throws SecurityException;
    
    /**
     * Add a hierarchical association between two roles.
     * <p>
     * Default supported hierarchical associations are {@link JetspeedPrincipalAssociationType#IS_A} and
     * {@link JetspeedPrincipalAssociationType#IS_PART_OF}, but it will depend on the actual runtime configuration
     * if the required {@link JetspeedPrincipalAssociationType} is available.
     * </p>
     * @param from The role for the from side of the association 
     * @param to The role for the to side of the association
     * @param associationName The name of the {@link JetspeedPrincipalAssociationType} to create
     * @throws SecurityException
     */
    void addRoleToRole(Role from, Role to, String associationName) throws SecurityException;

    /**
     * Remove a hierarchical association between two roles.
     * <p>
     * Default supported hierarchical associations are {@link JetspeedPrincipalAssociationType#IS_A} and
     * {@link JetspeedPrincipalAssociationType#IS_PART_OF}, but it will depend on the actual runtime configuration
     * if the required {@link JetspeedPrincipalAssociationType} is available.
     * </p>
     * @param from The role for the from side of the association 
     * @param to The role for the to side of the association
     * @param associationName The name of the {@link JetspeedPrincipalAssociationType} to create
     * @throws SecurityException
     */
    void removeRoleFromRole(Role from, Role to, String associationName) throws SecurityException;
    
    /**
     * Retrieve all the roles which are associated <em>to</em> the provided role. 
     * <p>
     * Default supported hierarchical associations are {@link JetspeedPrincipalAssociationType#IS_A} and
     * {@link JetspeedPrincipalAssociationType#IS_PART_OF}, but it will depend on the actual runtime configuration
     * if the required {@link JetspeedPrincipalAssociationType} is available.
     * </p>
     * <p>
     * If the corresponding {@link JetspeedPrincipalAssociationType} is not available, this method will simply
     * return a empty list.
     * </p>
     * <p>
     * For a {@link JetspeedPrincipalAssociationType#IS_PART_OF} association, this will return all
     * the nested roles which together <em>represent</em> the provided role.
     * </p>
     * <p>
     * For a {@link JetspeedPrincipalAssociationType#IS_A} association, this will return all
     * the roles which <em>extend</em> the provided role.
     * </p>
     * <p>
     * Note: this method will only return the directly associated roles, not further derived associations.
     * </p>
     * @param to The role for the to side of the association
     * @param associationName The name of the {@link JetspeedPrincipalAssociationType} to create
     */
    List<Role> getRolesAssociatedTo(Role to, String associationName);

    /**
     * Retrieve all the roles which are associated <em>from</em> the provided role. 
     * <p>
     * Default supported hierarchical associations are {@link JetspeedPrincipalAssociationType#IS_A} and
     * {@link JetspeedPrincipalAssociationType#IS_PART_OF}, but it will depend on the actual runtime configuration
     * if the required {@link JetspeedPrincipalAssociationType} is available.
     * </p>
     * <p>
     * If the corresponding {@link JetspeedPrincipalAssociationType} is not available, this method will simply
     * return a empty list.
     * </p>
     * <p>
     * For a {@link JetspeedPrincipalAssociationType#IS_PART_OF} association, this will return (at most)
     * the single role where the provided role is part of.
     * </p>
     * <p>
     * For a {@link JetspeedPrincipalAssociationType#IS_A} association, this will return all
     * the roles which the provided role <em>extends</em>.
     * </p>
     * <p>
     * Note: this method will only return the directly associated role(s), not further derived associations.
     * </p>
     * @param from The role for the from side of the association 
     * @param associationName The name of the {@link JetspeedPrincipalAssociationType} to create
     */
    List<Role> getRolesAssociatedFrom(Role from, String associationName);
}
