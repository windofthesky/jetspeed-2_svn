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
package org.apache.jetspeed.security.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
import org.apache.jetspeed.util.ArgUtil;

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
 * hierachies. Implementation follow the conventions enforced by the
 * {@link Preferences}API.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class GroupManagerImpl implements GroupManager
{
    /** The logger. */
    private static final Log log = LogFactory.getLog(GroupManagerImpl.class);

    /** The group security handler. */
    private GroupSecurityHandler groupSecurityHandler = null;

    /** The security mapping handler. */
    private SecurityMappingHandler securityMappingHandler = null;

    /**
     * @param securityProvider The security provider.
     */
    public GroupManagerImpl(SecurityProvider securityProvider)
    {
        this.groupSecurityHandler = securityProvider.getGroupSecurityHandler();
        this.securityMappingHandler = securityProvider.getSecurityMappingHandler();
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#addGroup(java.lang.String)
     */
    public void addGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" },
                "addGroup(java.lang.String)");

        // Check if group already exists.
        if (groupExists(groupFullPathName))
        {
            throw new SecurityException(SecurityException.GROUP_ALREADY_EXISTS + " " + groupFullPathName);
        }

        GroupPrincipal groupPrincipal = new GroupPrincipalImpl(groupFullPathName);
        String fullPath = groupPrincipal.getFullPath();
        // Add the preferences.
        Preferences preferences = Preferences.userRoot().node(fullPath);
        if (log.isDebugEnabled())
        {
            log.debug("Added group preferences node: " + fullPath);
        }
        try
        {
            if ((null != preferences) && preferences.absolutePath().equals(fullPath))
            {
                // Add role principal.
                groupSecurityHandler.setGroupPrincipal(groupPrincipal);
                if (log.isDebugEnabled())
                {
                    log.debug("Added group: " + fullPath);
                }
            }
        }
        catch (SecurityException se)
        {
            String msg = "Unable to create the role.";
            log.error(msg, se);

            // Remove the preferences node.
            try
            {
                preferences.removeNode();
            }
            catch (BackingStoreException bse)
            {
                bse.printStackTrace();
            }
            throw new SecurityException(msg, se);
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" },
                "removeGroup(java.lang.String)");

        // Resolve the group hierarchy.
        Preferences prefs = Preferences.userRoot().node(
                GroupPrincipalImpl.getFullPathFromPrincipalName(groupFullPathName));
        String[] groups = securityMappingHandler.getGroupHierarchyResolver().resolveChildren(prefs);
        for (int i = 0; i < groups.length; i++)
        {
            try
            {
                groupSecurityHandler.removeGroupPrincipal(new GroupPrincipalImpl(GroupPrincipalImpl
                        .getPrincipalNameFromFullPath((String) groups[i])));
            }
            catch (Exception e)
            {
                String msg = "Unable to remove group: "
                        + GroupPrincipalImpl.getPrincipalNameFromFullPath((String) groups[i]);
                log.error(msg, e);
                throw new SecurityException(msg, e);
            }
            // Remove preferences
            Preferences groupPref = Preferences.userRoot().node((String) groups[i]);
            try
            {
                groupPref.removeNode();
            }
            catch (BackingStoreException bse)
            {
                String msg = "Unable to remove group preferences: " + groups[i];
                log.error(msg, bse);
                throw new SecurityException(msg, bse);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#groupExists(java.lang.String)
     */
    public boolean groupExists(String groupFullPathName)
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" },
                "groupExists(java.lang.String)");

        Principal principal = groupSecurityHandler.getGroupPrincipal(groupFullPathName);
        boolean groupExists = (null != principal);
        if (log.isDebugEnabled())
        {
            log.debug("Role exists: " + groupExists);
            log.debug("Role: " + groupFullPathName);
        }
        return groupExists;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroup(java.lang.String)
     */
    public Group getGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" },
                "getGroup(java.lang.String)");

        String fullPath = GroupPrincipalImpl.getFullPathFromPrincipalName(groupFullPathName);

        Principal groupPrincipal = groupSecurityHandler.getGroupPrincipal(groupFullPathName);
        if (null == groupPrincipal)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST + " " + groupFullPathName);
        }
        Preferences preferences = Preferences.userRoot().node(fullPath);
        Group group = new GroupImpl(groupPrincipal, preferences);
        return group;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsForUser(java.lang.String)
     */
    public Collection getGroupsForUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "getGroupsForUser(java.lang.String)");

        Collection groups = new ArrayList();

        Set groupPrincipals = securityMappingHandler.getGroupPrincipals(username);
        Iterator groupPrincipalsIter = groupPrincipals.iterator();
        while (groupPrincipalsIter.hasNext())
        {
            Principal groupPrincipal = (Principal) groupPrincipalsIter.next();
            Preferences preferences = Preferences.userRoot().node(
                    GroupPrincipalImpl.getFullPathFromPrincipalName(groupPrincipal.getName()));
            groups.add(new GroupImpl(groupPrincipal, preferences));
        }
        return groups;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsInRole(java.lang.String)
     */
    public Collection getGroupsInRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" },
                "getGroupsInRole(java.lang.String)");

        Collection groups = new ArrayList();

        Set groupPrincipals = securityMappingHandler.getGroupPrincipalsInRole(roleFullPathName);
        Iterator groupPrincipalsIter = groupPrincipals.iterator();
        while (groupPrincipalsIter.hasNext())
        {
            Principal groupPrincipal = (Principal) groupPrincipalsIter.next();
            Preferences preferences = Preferences.userRoot().node(
                    GroupPrincipalImpl.getFullPathFromPrincipalName(groupPrincipal.getName()));
            groups.add(new GroupImpl(groupPrincipal, preferences));
        }
        return groups;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#addUserToGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addUserToGroup(String username, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, groupFullPathName }, new String[] { "username", "groupFullPathName" },
                "addUserToGroup(java.lang.String, java.lang.String)");

        // Get the group principal to add to user.
        Principal groupPrincipal = groupSecurityHandler.getGroupPrincipal(groupFullPathName);
        if (null == groupPrincipal)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST + " " + groupFullPathName);
        }
        // Get the user groups.
        Set groupPrincipals = securityMappingHandler.getGroupPrincipals(username);
        // Add group to user.
        if (!groupPrincipals.contains(groupPrincipal))
        {
            securityMappingHandler.setUserPrincipalInGroup(username, groupFullPathName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#removeUserFromGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeUserFromGroup(String username, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, groupFullPathName }, new String[] { "username", "groupFullPathName" },
                "removeUserFromGroup(java.lang.String, java.lang.String)");

        // Get the group principal to remove.
        Principal groupPrincipal = groupSecurityHandler.getGroupPrincipal(groupFullPathName);
        if (null != groupPrincipal)
        {
            securityMappingHandler.removeUserPrincipalInGroup(username, groupFullPathName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#isUserInGroup(java.lang.String,
     *      java.lang.String)
     */
    public boolean isUserInGroup(String username, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, groupFullPathName }, new String[] { "username", "groupFullPathName" },
                "isUserInGroup(java.lang.String, java.lang.String)");

        boolean isUserInGroup = false;
        
        Set groupPrincipals = securityMappingHandler.getGroupPrincipals(username);
        Principal groupPrincipal = new GroupPrincipalImpl(groupFullPathName);       
        if (groupPrincipals.contains(groupPrincipal))
        {
            isUserInGroup = true;
        }
        return isUserInGroup;
    }

}