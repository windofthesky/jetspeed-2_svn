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
 * @version $Id$
 */
public interface GroupManager extends PrincipalTypeManager
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
     * @throws throws a security exception.
     */
    Group addGroup(String groupName) throws SecurityException;

    /**
     * <p>
     * Add a new group and optionally map/replicate it to an external storage manager (if configured).
     * </p>
     * @param groupName The group name
     * @param mapped if the new Group should be mapped/replicated to an external security storage manager (if used) or not.
     * @return the new {@link Group}
     * @throws throws a security exception.
     */
    Group addGroup(String groupName, boolean mapped) throws SecurityException;

    /**
     * <p>
     * Remove a group.
     * </p>
     * @param groupName The group name
     * @throws throws a security exception.
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
     * @throws throws security exception if the group does not exist.
     */
    Group getGroup(String groupName) throws SecurityException;

    /**
     * <p>
     * Retrieves a detached and modifiable {@link Group} list of all the groups associated to a specific
     * user.
     * 
     * @param username The user name.
     * @return A list of {@link Group}.
     * @throws throws security exception if the user does not exist.
     */
    List<Group> getGroupsForUser(String username) throws SecurityException;

    /**
     * <p>
     * Retrieves a detached and modifiable {@link Group} list of all the groups in a specific role.
     * </p>
     * 
     * @param roleName The role name
     * @return A list of {@link Group}.
     * @throws throws a security exception if the role does not exist.
     */
    List<Group> getGroupsInRole(String roleName) throws SecurityException;

    /**
     * <p>
     * Add a user to a group.
     * </p>
     * 
     * @param username The user name.
     * @param groupName The group name
     * @throws throws a security exception.
     */
    void addUserToGroup(String username, String groupName) throws SecurityException;

    /**
     * <p>
     * Remove a user from a group.
     * </p>
     * 
     * @param username The user name.
     * @param groupName The group name
     * @throws throws a security exception.
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
     * @throws throws security exception if the user or group does not exist.
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

   /**
    * Add a hierarchical association between two groups.
    * <p>
    * Default supported hierarchical associations are {@link JetspeedPrincipalAssociationType#IS_A} and
    * {@link JetspeedPrincipalAssociationType#IS_PART_OF}, but it will depend on the actual runtime configuration
    * if the required {@link JetspeedPrincipalAssociationType} is available.
    * </p>
    * @param from The group for the from side of the association 
    * @param to The group for the to side of the association
    * @param associationName The name of the {@link JetspeedPrincipalAssociationType} to create
    * @throws SecurityException
    */
   void addGroupToGroup(Group from, Group to, String associationName) throws SecurityException;

   /**
    * Remove a hierarchical association between two groups.
    * <p>
    * Default supported hierarchical associations are {@link JetspeedPrincipalAssociationType#IS_A} and
    * {@link JetspeedPrincipalAssociationType#IS_PART_OF}, but it will depend on the actual runtime configuration
    * if the required {@link JetspeedPrincipalAssociationType} is available.
    * </p>
    * @param from The group for the from side of the association 
    * @param to The group for the to side of the association
    * @param associationName The name of the {@link JetspeedPrincipalAssociationType} to create
    * @throws SecurityException
    */
   void removeGroupFromGroup(Group from, Group to, String associationName) throws SecurityException;
   
   /**
    * Retrieve all the groups which are associated <em>to</em> the provided group. 
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
    * the nested groups which together <em>represent</em> the provided group.
    * </p>
    * <p>
    * For a {@link JetspeedPrincipalAssociationType#IS_A} association, this will return all
    * the groups which <em>extend</em> the provided group.
    * </p>
    * <p>
    * Note: this method will only return the directly associated groups, not further derived associations.
    * </p>
    * @param to The group for the to side of the association
    * @param associationName The name of the {@link JetspeedPrincipalAssociationType} to create
    */
   List<Group> getGroupsAssociatedTo(Group to, String associationName);

   /**
    * Retrieve all the groups which are associated <em>from</em> the provided group. 
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
    * the single group where the provided group is part of.
    * </p>
    * <p>
    * For a {@link JetspeedPrincipalAssociationType#IS_A} association, this will return all
    * the groups which the provided group <em>extends</em>.
    * </p>
    * <p>
    * Note: this method will only return the directly associated group(s), not further derived associations.
    * </p>
    * @param from The group for the from side of the association 
    * @param associationName The name of the {@link org.apache.jetspeed.security.JetspeedPrincipalAssociationType} to create
    */
   List<Group> getGroupsAssociatedFrom(Group from, String associationName);
}