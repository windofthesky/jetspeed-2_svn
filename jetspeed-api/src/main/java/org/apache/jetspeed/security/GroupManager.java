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
 * <p>
 * Describes the service interface for managing groups.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 */
public interface GroupManager
{
    Group newGroup(String name, boolean mapped);
    Group newTransientGroup(String name);

    /**
     * <p>
     * Add a new group.
     * </p>
     * <p>
     * If an external security storage manager is used, the group will be mapped/replicated to it as well.
     * </p>
     * @param groupName The group name
     * @return the new {@link Group}
     * @throws Throws a security exception.
     */
    Group addGroup(String groupName) throws SecurityException;

    /**
     * <p>
     * Add a new group and optionally map/replicate it to an external storage manager (if configured).
     * </p>
     * @param groupName The group name
     * @param mapped if the new Group should be mapped/replicated to an external security storage manager (if used) or not.
     * @return the new {@link Group}
     * @throws Throws a security exception.
     */
    Group addGroup(String groupName, boolean mapped) throws SecurityException;

    /**
     * <p>
     * Remove a group.
     * </p>
     * @param groupName The group name
     * @throws Throws a security exception.
     */
    void removeGroup(String groupName) throws SecurityException;

    /**
     * <p>
     * Whether or not a group exists.
     * </p>
     * 
     * @param groupName The group name
     * @return Whether or not a group exists.
     */
    boolean groupExists(String groupName);

    /**
     * <p>
     * Get a group {@link Group}for a given group name.
     * 
     * @param groupName
     * @return The {@link Group}
     * @throws Throws security exception if the group does not exist.
     */
    Group getGroup(String groupName) throws SecurityException;

    /**
     * <p>
     * Retrieves a detached and modifiable {@link Group} list of all the groups associated to a specific
     * user.
     * 
     * @param username The user name.
     * @return A list of {@link Group}.
     * @throws Throws security exception if the user does not exist.
     */
    List<Group> getGroupsForUser(String username) throws SecurityException;

    /**
     * <p>
     * Retrieves a detached and modifiable {@link Group} list of all the groups in a specific role.
     * </p>
     * 
     * @param roleName The role name
     * @return A list of {@link Group}.
     * @throws Throws a security exception if the role does not exist.
     */
    List<Group> getGroupsInRole(String roleName) throws SecurityException;

    /**
     * <p>
     * Add a user to a group.
     * </p>
     * 
     * @param username The user name.
     * @param groupName The group name
     * @throws Throws a security exception.
     */
    void addUserToGroup(String username, String groupName) throws SecurityException;

    /**
     * <p>
     * Remove a user from a group.
     * </p>
     * 
     * @param username The user name.
     * @param groupName The group name
     * @throws Throws a security exception.
     */
    void removeUserFromGroup(String username, String groupName) throws SecurityException;

    /**
     * <p>
     * Whether or not a user is in a group.
     * </p>
     * 
     * @param username The user name.
     * @param groupName The group name
     * @return Whether or not a user is in a group.
     * @throws Throws security exception if the user or group does not exist.
     */
    boolean isUserInGroup(String username, String groupName) throws SecurityException;

    /**
     * Retrieves a detached and modifiable {@link Group} list matching the corresponding
     * group name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching groups.
     * @return a list of {@link Group} 
     */
   List<Group> getGroups(String nameFilter) throws SecurityException;
    
   /**
    * <p>
    * Retrieves a detached and modifiable List of group names, finding groups matching the corresponding
    * group name filter.
    * </p>
    * 
    * @param nameFilter The filter used to retrieve matching groups.
    * @return A list of group names
    */
   List<String> getGroupNames(String nameFilter) throws SecurityException;

   /**
    * Updates a group and all its attributes
    * @param group
    * @throws SecurityException
    */
   void updateGroup(Group group) throws SecurityException;
}