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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.JetspeedGroupPrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedGroupPrincipalImpl;
import org.apache.jetspeed.security.om.JetspeedRolePrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedRolePrincipalImpl;
import org.apache.jetspeed.security.om.JetspeedUserPrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedUserPrincipalImpl;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>Implementation for managing roles.</p>
 * <p>Role hierarchy elements are being returned as a {@link Role}
 * collection.  The backing implementation must appropriately map 
 * the role hierarchy to a preferences sub-tree.</p> 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class RoleManagerImpl extends BaseSecurityImpl implements RoleManager
{
    private static final Log log = LogFactory.getLog(RoleManagerImpl.class);

    /**
     * <p>Constructor providing access to the persistence component.</p>
     */
    public RoleManagerImpl(PersistenceStoreContainer storeContainer, String keyStoreName)
    {
        super(storeContainer, keyStoreName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRole(java.lang.String)
     */
    public void addRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" }, "addRole(java.lang.String)");

        RolePrincipal rolePrincipal = new RolePrincipalImpl(roleFullPathName);
        String fullPath = rolePrincipal.getFullPath();
        // Check if role already exists.
        if (roleExists(roleFullPathName))
        {
            throw new SecurityException(SecurityException.ROLE_ALREADY_EXISTS + " " + roleFullPathName);
        }

        // If does not exist, create.
        JetspeedRolePrincipal omRole = new JetspeedRolePrincipalImpl(fullPath);
        Preferences preferences = Preferences.userRoot().node(fullPath);
        PersistenceStore store = getPersistenceStore();
        try
        {
            if ((null != preferences) && preferences.absolutePath().equals(fullPath))
            {
                store.lockForWrite(omRole);
                store.getTransaction().checkpoint();
            }
        }
        catch (Exception e)
        {
            String msg = "Unable to lock Role for update.";
            log.error(msg, e);
            store.getTransaction().rollback();
            try
            {
                preferences.removeNode();
            }
            catch (BackingStoreException bse)
            {
                bse.printStackTrace();
            }
            throw new SecurityException(msg, e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRole(java.lang.String)
     */
    public void removeRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" }, "removeRole(java.lang.String)");

        JetspeedRolePrincipal omParentRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null != omParentRole)
        {
            PersistenceStore store = getPersistenceStore();
            Filter filter = store.newFilter();
            filter.addLike((Object) new String("fullPath"), (Object) (omParentRole.getFullPath() + "/*"));
            Object query = store.newQuery(JetspeedRolePrincipalImpl.class, filter);
            Collection omRoles = store.getCollectionByQuery(query);
            if (null == omRoles)
            {
                omRoles = new ArrayList();
            }
            omRoles.add(omParentRole);
            // Remove each role in the collection.
            Iterator omRolesIterator = omRoles.iterator();
            while (omRolesIterator.hasNext())
            {
                JetspeedRolePrincipal omRole = (JetspeedRolePrincipal) omRolesIterator.next();
                // TODO This should be managed in a transaction.
                Collection omUsers = omRole.getUserPrincipals();
                if (null != omUsers)
                {
                    omUsers.clear();
                }
                Collection omGroups = omRole.getGroupPrincipals();
                if (null != omGroups)
                {
                    omGroups.clear();
                }
                Collection omPermissions = omRole.getPermissions();
                if (null != omPermissions)
                {
                    omPermissions.clear();
                }

                try
                {
                    // TODO Can this be done in one shot?
                    // Remove dependencies.
                    store.lockForWrite(omRole);
                    omRole.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                    omRole.setUserPrincipals(omUsers);
                    omRole.setGroupPrincipals(omGroups);
                    omRole.setPermissions(omPermissions);
                    store.getTransaction().checkpoint();

                    // Remove role.
                    store.deletePersistent(omRole);
                    store.getTransaction().checkpoint();
                }
                catch (Exception e)
                {
                    String msg = "Unable to lock Role for update.";
                    log.error(msg, e);
                    store.getTransaction().rollback();
                    throw new SecurityException(msg, e);
                }
                // Remove preferences
                Preferences preferences = Preferences.userRoot().node(omRole.getFullPath());
                try
                {
                    preferences.removeNode();
                }
                catch (BackingStoreException bse)
                {
                    bse.printStackTrace();
                }
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#roleExists(java.lang.String)
     */
    public boolean roleExists(String roleFullPathName)
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" }, "roleExists(java.lang.String)");

        JetspeedRolePrincipal omRole = super.getJetspeedRolePrincipal(roleFullPathName);
        boolean roleExists = (null != omRole);
        return roleExists;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRole(java.lang.String)
     */
    public Role getRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" }, "getRole(java.lang.String)");

        JetspeedRolePrincipal omRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null == omRole)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }
        return super.getRole(omRole);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesForUser(java.lang.String)
     */
    public Collection getRolesForUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "getRolesForUser(java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }

        Collection omUserRoles = omUser.getRolePrincipals();
        return super.getRoles(omUserRoles);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getUsersInRole(java.lang.String)
     */
    public Collection getUsersInRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" }, "getUsersInRole(java.lang.String)");

        JetspeedRolePrincipal omRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null == omRole)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }
        Collection omRoleUsers = omRole.getUserPrincipals();
        return super.getUsers(omRoleUsers);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesForGroup(java.lang.String)
     */
    public Collection getRolesForGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { groupFullPathName },
            new String[] { "groupFullPathName" },
            "getRolesForGroup(java.lang.String)");

        JetspeedGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        if (null == omGroup)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST + " " + groupFullPathName);
        }
        Collection omGroupRoles = omGroup.getRolePrincipals();
        return super.getRoles(omGroupRoles);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getGroupsInRole(java.lang.String)
     */
    public Collection getGroupsInRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { roleFullPathName },
            new String[] { "roleFullPathName" },
            "getGroupsInRole(java.lang.String)");

        JetspeedRolePrincipal omRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null == omRole)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }
        Collection omRoleGroups = omRole.getGroupPrincipals();
        return super.getGroups(omRoleGroups);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRoleToUser(java.lang.String, java.lang.String)
     */
    public void addRoleToUser(String username, String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { username, roleFullPathName },
            new String[] { "username", "roleFullPathName" },
            "addUserToRole(java.lang.String, java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        JetspeedRolePrincipal omRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null == omRole)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }

        Collection omUserRoles = omUser.getRolePrincipals();
        if (null == omUserRoles)
        {
            omUserRoles = new ArrayList();
        }
        if (!omUserRoles.contains(omRole))
        {
            omUserRoles.add(omRole);
            PersistenceStore store = getPersistenceStore();
            try
            {
                store.lockForWrite(omUser);
                omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                omUser.setRolePrincipals(omUserRoles);
                store.getTransaction().checkpoint();
            }
            catch (Exception e)
            {
                String msg = "Unable to lock User for update.";
                log.error(msg, e);
                store.getTransaction().rollback();
                throw new SecurityException(msg, e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRoleFromUser(java.lang.String, java.lang.String)
     */
    public void removeRoleFromUser(String username, String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { username, roleFullPathName },
            new String[] { "username", "roleFullPathName" },
            "removeRoleFromUser(java.lang.String, java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        // TODO This should be managed in a transaction.
        if (null != omUser)
        {
            Collection omRoles = omUser.getRolePrincipals();
            if (null != omRoles)
            {
                Collection newOmRoles = super.removeRole(omRoles, roleFullPathName);
                if (newOmRoles.size() < omRoles.size())
                {
                    PersistenceStore store = getPersistenceStore();
                    try
                    {
                        store.lockForWrite(omUser);
                        omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                        omUser.setRolePrincipals(newOmRoles);
                        store.getTransaction().checkpoint();
                    }
                    catch (Exception e)
                    {
                        String msg = "Unable to lock User for update.";
                        log.error(msg, e);
                        store.getTransaction().rollback();
                        throw new SecurityException(msg, e);
                    }
                }
            }
        }
    }

    /**
    * @see org.apache.jetspeed.security.RoleManager#isUserInRole(java.lang.String, java.lang.String)
    */
    public boolean isUserInRole(String username, String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { username, roleFullPathName },
            new String[] { "username", "roleFullPathName" },
            "isUserInRole(java.lang.String, java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        JetspeedRolePrincipal omRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null == omRole)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }
        boolean isUserInRole = false;
        Collection omRoles = omUser.getRolePrincipals();
        if ((null != omRoles) && (omRoles.contains(omRole)))
        {
            isUserInRole = true;
        }
        return isUserInRole;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRoleToGroup(java.lang.String, java.lang.String)
     */
    public void addRoleToGroup(String roleFullPathName, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { roleFullPathName, groupFullPathName },
            new String[] { "roleFullPathName", "groupFullPathName" },
            "addRoleToGroup(java.lang.String, java.lang.String)");

        JetspeedRolePrincipal omRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null == omRole)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }

        JetspeedGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        if (null == omGroup)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST + " " + groupFullPathName);
        }

        Collection omGroupRoles = omGroup.getRolePrincipals();
        if (null == omGroupRoles)
        {
            omGroupRoles = new ArrayList();
        }
        if (!omGroupRoles.contains(omRole))
        {
            omGroupRoles.add(omRole);
            PersistenceStore store = getPersistenceStore();
            try
            {
                store.lockForWrite(omGroup);
                omGroup.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                omGroup.setRolePrincipals(omGroupRoles);
                store.getTransaction().checkpoint();
            }
            catch (Exception e)
            {
                String msg = "Unable to lock Group for update.";
                log.error(msg, e);
                store.getTransaction().rollback();
                throw new SecurityException(msg, e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRoleFromGroup(java.lang.String, java.lang.String)
     */
    public void removeRoleFromGroup(String roleFullPathName, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { roleFullPathName, groupFullPathName },
            new String[] { "roleFullPathName", "groupFullPathName" },
            "removeRoleFromGroup(java.lang.String, java.lang.String)");

        JetspeedGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        // TODO This should be managed in a transaction.
        if (null != omGroup)
        {
            Collection omRoles = omGroup.getRolePrincipals();
            if (null != omRoles)
            {
                Collection newOmRoles = super.removeRole(omRoles, roleFullPathName);
                if (newOmRoles.size() < omRoles.size())
                {
                    PersistenceStore store = getPersistenceStore();
                    try
                    {
                        store.lockForWrite(omGroup);
                        omGroup.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                        omGroup.setRolePrincipals(newOmRoles);
                        store.getTransaction().checkpoint();
                    }
                    catch (Exception e)
                    {
                        String msg = "Unable to lock Group for update.";
                        log.error(msg, e);
                        store.getTransaction().rollback();
                        throw new SecurityException(msg, e);
                    }
                }
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#isGroupInRole(java.lang.String, java.lang.String)
     */
    public boolean isGroupInRole(String groupFullPathName, String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { roleFullPathName, groupFullPathName },
            new String[] { "roleFullPathName", "groupFullPathName" },
            "isGroupInRole(java.lang.String, java.lang.String)");

        JetspeedGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        if (null == omGroup)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST + " " + groupFullPathName);
        }
        JetspeedRolePrincipal omRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null == omRole)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }
        boolean isGroupInRole = false;
        Collection omRoles = omGroup.getRolePrincipals();
        if ((null != omRoles) && (omRoles.contains(omRole)))
        {
            isGroupInRole = true;
        }
        return isGroupInRole;
    }

}
