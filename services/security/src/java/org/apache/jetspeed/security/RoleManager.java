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
import java.util.prefs.Preferences;

import org.apache.jetspeed.cps.CommonPortletServices;

/**
 * Convenience static wrapper around {@link RoleManagerService}.
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class RoleManager
{

    /**
     * <p>Default Constructor.  This class contains only static
     * methods, hence users should not be allowed to instantiate it.</p>
     */
    public RoleManager()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>Returns the {@link RoleManagerService}.</p>
     * @return The RoleManagerService.
     */
    private static final RoleManagerService getService()
    {
        return (RoleManagerService) CommonPortletServices.getPortalService(RoleManagerService.SERVICE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#addRole(java.lang.String)
     */
    public static void addRole(String roleFullPathName) throws SecurityException
    {
        getService().addRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#removeRole(java.lang.String)
     */
    public static void removeRole(String roleFullPathName)
    {
        getService().removeRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#roleExists(java.lang.String)
     */
    public static boolean roleExists(String roleFullPathName)
    {
       return getService().roleExists(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getRole(java.lang.String)
     */
    public static Role getRole(String roleFullPathName) throws SecurityException
    {
        return getService().getRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getRolesForUser(java.lang.String)
     */
    public static Collection getRolesForUser(String username) throws SecurityException
    {
        return getService().getRolesForUser(username);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getUsersInRole(java.lang.String)
     */
    public static Collection getUsersInRole(String roleFullPathName) throws SecurityException
    {
        return getService().getUsersInRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getRolesForGroup(java.lang.String)
     */
    public static Collection getRolesForGroup(String groupFullPathName) throws SecurityException
    {
        return getService().getRolesForGroup(groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getGroupsInRole(java.lang.String)
     */
    public static Collection getGroupsInRole(String roleFullPathName) throws SecurityException
    {
        return getService().getGroupsInRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#addRoleToUser(java.lang.String, java.lang.String)
     */
    public static void addRoleToUser(String username, String roleFullPathName) throws SecurityException
    {
        getService().addRoleToUser(username, roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#removeRoleFromUser(java.lang.String, java.lang.String)
     */
    public static void removeRoleFromUser(String username, String roleFullPathName)
    {
        getService().removeRoleFromUser(username, roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#isUserInRole(java.lang.String, java.lang.String)
     */
    public static boolean isUserInRole(String username, String roleFullPathName) throws SecurityException
    {
        return isUserInRole(username, roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#addRoleToGroup(java.lang.String, java.lang.String)
     */
    public static void addRoleToGroup(String roleFullPathName, String groupFullPathName) throws SecurityException
    {
        getService().addRoleToGroup(roleFullPathName, groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#removeRoleFromGroup(java.lang.String, java.lang.String)
     */
    public static void removeRoleFromGroup(String roleFullPathName, String groupFullPathName)
    {
        getService().removeRoleFromGroup(roleFullPathName, groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#isGroupInRole(java.lang.String, java.lang.String)
     */
    public static boolean isGroupInRole(String roleFullPathName, String groupFullPathName) throws SecurityException
    {
        return getService().isGroupInRole(roleFullPathName, groupFullPathName);
    }

}
