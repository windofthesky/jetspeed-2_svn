/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.security;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.apache.jetspeed.cps.CommonService;

/**
 * <p>Describes the service interface for managing roles.</p>
 * <p>Role hierarchy elements are being returned as a {@link Role}
 * collection.  The backing implementation must appropriately map 
 * the role hierarchy to a preferences sub-tree.</p> 
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 */
public interface RoleManagerService extends CommonService
{
    /** <p>The name of the service.</p> */
    String SERVICE_NAME = "RoleManager";

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
     */
    void removeRole(String roleFullPathName);

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
     */
    void removeRoleFromUser(String username, String roleFullPathName);

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
     */
    void removeRoleFromGroup(String roleFullPathName, String groupFullPathName);

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
