/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.Collection;

/**
 * <p>Describes the service interface for managing roles.</p>
 * <p>Role hierarchy elements are being returned as a {@link Role}
 * collection.  The backing implementation must appropriately map 
 * the role hierarchy to a preferences sub-tree.</p> 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface RoleManager
{

    /**
     * <p>Add a new role.</p>
     * <p>Role principal names are relative to the /role node.</p>
     * <p>Role principal path names are stored leveraging the {@link Preferences}
     * api.  Roles will be stored under /role/theGroupName/theGroupNameChild
     * when given the full path name /theRoleName/theRoleNameChild.
     * @param roleFullPathName The role name full path relative to the
     *                         /role node (e.g. /theRoleName/theRoleNameChild).
     * @throws Throws a security exception if the role already exists.
     */
    void addRole(String roleFullPathName) throws SecurityException;

    /**
     * <p>Remove a given role and all the children of that role.</p>
     * <p>Role principal names are relative to the /role node.</p>
     * <p>Role principal path names are stored leveraging the {@link Preferences}
     * api.  Roles will be stored under /role/theGroupName/theGroupNameChild
     * when given the full path name /theRoleName/theRoleNameChild.
     * @param roleFullPathName The role name full path relative to the
     *                         /role node (e.g. /theRoleName/theRoleNameChild).
     * @throws Throws a security exception.
     */
    void removeRole(String roleFullPathName) throws SecurityException;

    /**
     * <p>Whether or not a role exists.</p>
     * @param roleFullPathName The role name full path relative to the
     *                         /role node. (e.g. /theRoleName/theRoleNameChild)
     * @return Whether or not a role exists.
     */
    boolean roleExists(String roleFullPathName);

    /**
     * <p>Get a role {@link Role} for a given role full path name.
     * @param roleFullPathName The role name full path relative to the
     *                         /role node (e.g. /theRoleName/theRoleChildName).
     * @return The {@link Preferences} node.
     * @throws Throws a security exception if the role does not exist.
     */
    Role getRole(String roleFullPathName) throws SecurityException;

    /**
     * <p>A collection of {@link Role} for all the roles
     * associated to a specific user.</p>
     * @param username The user name.
     * @return A Collection of {@link Role}.
     * @throws Throws a security exception if the user does not exist.
     */
    Collection getRolesForUser(String username) throws SecurityException;

    /**
     * <p>A collection of {@link User} for all the users
     * in a specific role.</p>
     * @param roleFullPathName The role full path relative to
     *                         the /role node (e.g. /theRoleName/theRoleChildName)..
     * @return A Collection of {@link User}.
     * @throws Throws a security exception if the role does not exist.
     */
    Collection getUsersInRole(String roleFullPathName) throws SecurityException;

    /**
     * <p>A collection of {@link Role} for all the roles
     * associated to a specific group.
     * @param groupFullPathName The group full path relative to the 
     *                         /group node (e.g. /theGroupName/theGroupChildName).
     * @return A Collection of {@link Role}.
     * @throws Throws a security exception if the group does not exist.
     */
    Collection getRolesForGroup(String groupFullPathName) throws SecurityException;

    /**
     * <p>A collection of {@link Group} for all the groups
     * in a specific role.
     * @param roleFullPathName The role full path relative to
     *                         the /role node (e.g. /theRoleName/theRoleChildName)..
     * @return A Collection of {@link Group}.
     * @throws Throws a security exception if the role does not exist.
     */
    Collection getGroupsInRole(String roleFullPathName) throws SecurityException;

    /**
     * <p>Add a role to a user.</p>
     * @param username The user name.
     * @param roleFullPathName The role name full path relative to the
     *                         /role node (e.g. /theRoleName/theRoleChildName).
     * @throws Throws a security exception if the role or the user do not exist.
     */
    void addRoleToUser(String username, String roleFullPathName) throws SecurityException;

    /**
     * <p>Remove a user from a role.</p>
     * @param username The user name.
     * @param roleFullPathName The role name full path relative to the
     *                         /role node (e.g. /theRoleName/theRoleChildName).
     * @throws Throws a security exception.
     */
    void removeRoleFromUser(String username, String roleFullPathName) throws SecurityException;

    /**
     * <p>Whether or not a user is in a role.</p>
     * @param username The user name.
     * @param roleFullPathName The role name full path relative to the
     *                         /role node (e.g. /theRoleName/theRoleChildName).
     * @return Whether or not a user is in a role.
     * @throws Throws a security exception if the role or the user does not exist.
     */
    boolean isUserInRole(String username, String roleFullPathName) throws SecurityException;

    /**
     * <p>Add a role to a group.</p>
     * @param roleFullPathName The role full path relative to the 
     *                         /role node (e.g. /theRoleName/theRoleChildName).
     * @param groupFullPathName The group name full path relative to the
     *                          /group node (e.g. /theGroupName/theGroupChildName).
     * @throws Throws a security exception.
     */
    void addRoleToGroup(String roleFullPathName, String groupFullPathName) throws SecurityException;

    /**
     * <p>Remove a role from a group.</p>
     * @param roleFullPathName The role full path relative to the 
     *                         /role node (e.g. /theRoleName/theRoleChildName).
     * @param groupFullPathName The group name full path relative to the
     *                          /group node (e.g. /theGroupName/theGroupChildName).
     * @throws Throws a security exception.
     */
    void removeRoleFromGroup(String roleFullPathName, String groupFullPathName) throws SecurityException;

    /**
     * <p>Whether or not a role is in a group.</p>
     * @param groupFullPathName The group name full path relative to the
     *                          /group node (e.g. /theGroupName/theGroupChildName).
     * @param roleFullPathName The role full path relative to the 
     *                         /role node (e.g. /theRoleName/theRoleChildName).
     * @return Whether or not a role is in a group.
     * @throws Throws a security exception if the role or the group does not exist.
     */
    boolean isGroupInRole(String groupFullPathName, String roleFullPathName) throws SecurityException;

}
