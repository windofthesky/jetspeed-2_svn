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
 * <p>Describes the service interface for managing groups.</p>
 * <p>Group hierarchy elements are being returned as a {@link Group}
 * collection.  The backing implementation must appropriately map 
 * the group hierarchy to a preferences sub-tree.</p> 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface GroupManagerService extends CommonService
{
    /** <p>The name of the service.</p> */
    String SERVICE_NAME = "GroupManager";

    /**
     * <p>Add a new group.</p>
     * <p>Group principal names are relative to the /group node.</p>
     * <p>Group principal path names are stored leveraging the {@link Preferences}
     * api.  Groups will be stored under /group/theGroupName/theGroupNameChild
     * when given the full path name /theGroupName/theGroupNameChild.
     * @param groupFullPathName The group name full path relative to the
     *                          /group node (e.g. /theGroupName/theGroupNameChild).
     * @throws Throws a security exception.
     */
    void addGroup(String groupFullPathName) throws SecurityException;

    /**
     * <p>Remove a group.</p>
     * <p>Group principal names are relative to the {@link Preferences}
     * /group node.</p>
     * <p>Group principal path names are stored leveraging the {@link Preferences}
     * api.  Groups will be stored under /group/theGroupName/theGroupNameChild
     * when given the full path name /theGroupName/theGroupNameChild.
     * @param groupFullPathName The group name full path relative to the
     *                          /group node. (e.g. /theGroupName/theGroupNameChild)
     */
    void removeGroup(String groupFullPathName);

    /**
     * <p>Whether or not a group exists.</p>
     * @param groupFullPathName The group name full path relative to the
     *                          /group node. (e.g. /theGroupName/theGroupNameChild)
     * @return Whether or not a group exists.
     */
    boolean groupExists(String groupFullPathName);

    /**
     * <p>Get a group {@link Group} for a given group full path name.
     * @param groupFullPathName The group name full path relative to the
     *                          /group node (e.g. /theGroupName/theGroupChildName).
     * @return The {@link Preferences} node.
     * @throws Throws security exception if the group does not exist.
     */
    Group getGroup(String groupFullPathName) throws SecurityException;

    /**
     * <p>A collection of {@link Group} for all the groups
     * associated to a specific user.
     * @param username The user name.
     * @return A collection of {@link Group}.
     * @throws Throws security exception if the user does not exist.
     */
    Collection getGroupsForUser(String username) throws SecurityException;

    /**
     * <p>A collection of {@link User} for a specific group.</p>
     * @param groupFullPathName The group name full path relative to the
     *                          /group node (e.g. /theGroupName/theGroupChildName).
     * @return A collection of {@link User}.
     * @throws Throws security exception if the group does not exist.
     */
    Collection getUsersInGroup(String groupFullPathName) throws SecurityException;

    /**
     * <p>Add a user to a group.</p>
     * @param username The user name.
     * @param groupFullPathName The group name full path relative to the
     *                          /group node (e.g. /theGroupName/theGroupChildName).
     * @throws Throws a security exception.
     */
    void addUserToGroup(String username, String groupFullPathName) throws SecurityException;

    /**
     * <p>Remove a user from a group.</p>
     * @param username The user name.
     * @param groupFullPathName The group name full path relative to the
     *                          /group node (e.g. /theGroupName/theGroupChildName).
     * @throws Throws a security exception.
     */
    void removeUserFromGroup(String username, String groupFullPathName);

    /**
     * <p>Whether or not a user is in a group.</p>
     * @param username The user name.
     * @param groupFullPathName The group name full path relative to the
     *                          /group node (e.g. /theGroupName/theGroupChildName).
     * @return Whether or not a user is in a group.
     * @throws Throws security exception if the user or group does not exist.
     */
    boolean isUserInGroup(String username, String groupFullPathName) throws SecurityException;

}
