/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security;

import java.util.Collection;

import org.apache.jetspeed.cps.CommonPortletServices;

/**
 * Convenience static wrapper around {@link RoleManagerService}.
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class GroupManager
{

    /**
     * <p>Default Constructor.  This class contains only static
     * methods, hence users should not be allowed to instantiate it.</p>
     */
    public GroupManager()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>Returns the {@link GroupManagerService}.</p>
     * @return The GroupManagerService.
     */
    private static final GroupManagerService getService()
    {
        return (GroupManagerService) CommonPortletServices.getPortalService(GroupManagerService.SERVICE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#addGroup(java.lang.String)
     */
    public static void addGroup(String groupFullPathName) throws SecurityException
    {
        getService().addGroup(groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#removeGroup(java.lang.String)
     */
    public static void removeGroup(String groupFullPathName)
    {
        getService().removeGroup(groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#groupExists(java.lang.String)
     */
    public static boolean groupExists(String groupFullPathName)
    {
        return getService().groupExists(groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#getGroup(java.lang.String)
     */
    public static Group getGroup(String groupFullPathName) throws SecurityException
    {
        return getService().getGroup(groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#getGroupsForUser(java.lang.String)
     */
    public static Collection getGroupsForUser(String username) throws SecurityException
    {
        return getService().getGroupsForUser(username);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#getUsersInGroup(java.lang.String)
     */
    public static Collection getUsersInGroup(String groupFullPathName) throws SecurityException
    {
        return getService().getUsersInGroup(groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#addUserToGroup(java.lang.String, java.lang.String)
     */
    public static void addUserToGroup(String username, String groupFullPathName) throws SecurityException
    {
        getService().addUserToGroup(username, groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#removeUserFromGroup(java.lang.String, java.lang.String)
     */
    public static void removeUserFromGroup(String username, String groupFullPathName)
    {
        getService().removeUserFromGroup(username, groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#isUserInGroup(java.lang.String, java.lang.String)
     */
    public static boolean isUserInGroup(String username, String groupFullPathName) throws SecurityException
    {
        return getService().isUserInGroup(username, groupFullPathName);
    }

}
