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

import java.util.Collection;

/**
 * <p>
 * Describes the service interface for managing groups.
 * </p>
 * <p>
 * Group hierarchy elements are being returned as a {@link Group}collection.
 * The backing implementation must appropriately map the group hierarchy to a
 * preferences sub-tree.
 * </p>
 * <p>
 * The convention {principal}.{subprincipal} has been chosen to name groups
 * hierachies. Implementation follow the conventions enforced by the Preferences
 * API.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface GroupManager
{

    /**
     * <p>
     * Add a new group.
     * </p>
     * <p>
     * Group principal names are expressed as {principal}.{subprincipal} where
     * "." is the separator expressing the hierarchical nature of a group.
     * </p>
     * <p>
     * Group principal path names are stored leveraging the {@link Preferences}
     * api. Groups will be stored under /group/theGroupName/theGroupNameChild
     * when given the full path name theGroupName.theGroupNameChild.
     * 
     * @param groupFullPathName The group name full path (e.g.
     *            theGroupName.theGroupNameChild).
     * @throws Throws a security exception.
     */
    void addGroup(String groupFullPathName) throws SecurityException;

    /**
     * <p>
     * Remove a group.
     * </p>
     * <p>
     * Group principal names are expressed as {principal}.{subprincipal} where
     * "." is the separator expressing the hierarchical nature of a group.
     * </p>
     * <p>
     * Group principal path names are stored leveraging the {@link Preferences}
     * api. Groups will be stored under /group/theGroupName/theGroupNameChild
     * when given the full path name theGroupName.theGroupNameChild.
     * 
     * @param groupFullPathName The group name full path (e.g.
     *            theGroupName.theGroupNameChild)
     * @throws Throws a security exception.
     */
    void removeGroup(String groupFullPathName) throws SecurityException;

    /**
     * <p>
     * Whether or not a group exists.
     * </p>
     * 
     * @param groupFullPathName The group name full path (e.g.
     *            theGroupName.theGroupNameChild)
     * @return Whether or not a group exists.
     */
    boolean groupExists(String groupFullPathName);

    /**
     * <p>
     * Get a group {@link Group}for a given group full path name.
     * 
     * @param groupFullPathName The group name full path (e.g.
     *            theGroupName.theGroupChildName).
     * @return The {@link Preferences}node.
     * @throws Throws security exception if the group does not exist.
     */
    Group getGroup(String groupFullPathName) throws SecurityException;

    /**
     * <p>
     * A collection of {@link Group}for all the groups associated to a specific
     * user.
     * 
     * @param username The user name.
     * @return A collection of {@link Group}.
     * @throws Throws security exception if the user does not exist.
     */
    Collection<Group> getGroupsForUser(String username) throws SecurityException;

    /**
     * <p>
     * A collection of {@link Group}for all the groups in a specific role.
     * </p>
     * 
     * @param roleFullPathName The role full path (e.g.
     *            theRoleName.theRoleChildName)..
     * @return A Collection of {@link Group}.
     * @throws Throws a security exception if the role does not exist.
     */
    Collection<Group> getGroupsInRole(String roleFullPathName) throws SecurityException;

    /**
     * <p>
     * Add a user to a group.
     * </p>
     * 
     * @param username The user name.
     * @param groupFullPathName The group name full path (e.g.
     *            theGroupName.theGroupChildName).
     * @throws Throws a security exception.
     */
    void addUserToGroup(String username, String groupFullPathName) throws SecurityException;

    /**
     * <p>
     * Remove a user from a group.
     * </p>
     * 
     * @param username The user name.
     * @param groupFullPathName The group name full path (e.g.
     *            theGroupName.theGroupChildName).
     * @throws Throws a security exception.
     */
    void removeUserFromGroup(String username, String groupFullPathName) throws SecurityException;

    /**
     * <p>
     * Whether or not a user is in a group.
     * </p>
     * 
     * @param username The user name.
     * @param groupFullPathName The group name full path (e.g.
     *            theGroupName.theGroupChildName).
     * @return Whether or not a user is in a group.
     * @throws Throws security exception if the user or group does not exist.
     */
    boolean isUserInGroup(String username, String groupFullPathName) throws SecurityException;

    /**
     * Get all groups available from all group handlers
     * 
     * @param filter The filter used to retrieve matching groups.
     * @return all groups available as Group 
     */
   Collection<Group> getGroups(String filter) throws SecurityException;
    
   /**
    * Enable or disable a group.
    * @param groupName The group name full path 
     *            theGroupName.theGroupChildName).
    * @param enabled enabled flag for the group
    */
   void setGroupEnabled(String groupName, boolean enabled) throws SecurityException;
}