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
package org.apache.jetspeed.security.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.jetspeed.security.PermissionManagerService;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManagerService;
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
public class RoleManagerServiceImpl extends BaseSecurityServiceImpl implements RoleManagerService
{

    /** <p>The persistence plugin.</p> */
    private PersistencePlugin plugin;

    /** <p>The persistence plugin.</p> */
    private PermissionManagerService pms;

    /**
     * <p>Default constructor.</p>
     */
    public RoleManagerServiceImpl()
    {
    }

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws CPSInitializationException
    {
        if (!isInitialized())
        {
            PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
            String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");
            plugin = ps.getPersistencePlugin(pluginName);
            // Initialize the parent plugin.
            super.plugin = plugin;
            // Get persistence manager service
            pms = (PermissionManagerService) CommonPortletServices.getPortalService(PermissionManagerService.SERVICE_NAME);
            setInit(true);
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#addRole(java.lang.String)
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
        try
        {
            if ((null != preferences) && preferences.absolutePath().equals(fullPath))
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(omRole);
                plugin.commitTransaction();
            }
        }
        catch (TransactionStateException e)
        {
            try
            {
                plugin.rollbackTransaction();
                try
                {
                    preferences.removeNode();
                }
                catch (BackingStoreException bse)
                {
                    bse.printStackTrace();
                }
            }
            catch (TransactionStateException e1)
            {
                log.error("Failed to rollback transaction.", e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#removeRole(java.lang.String)
     */
    public void removeRole(String roleFullPathName)
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" }, "removeRole(java.lang.String)");

        JetspeedRolePrincipal omParentRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null != omParentRole)
        {
            LookupCriteria c2 = plugin.newLookupCriteria();
            c2.addLike((Object) new String("fullPath"), (Object) (omParentRole.getFullPath() + "/*"));
            Object query2 = plugin.generateQuery(JetspeedRolePrincipalImpl.class, c2);
            Collection omRoles = plugin.getCollectionByQuery(JetspeedRolePrincipalImpl.class, query2);
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
                    // Remove dependencies.
                    plugin.beginTransaction();
                    plugin.prepareForUpdate(omRole);
                    omRole.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                    omRole.setUserPrincipals(omUsers);
                    omRole.setGroupPrincipals(omGroups);
                    omRole.setPermissions(omPermissions);
                    plugin.commitTransaction();

                    // Remove role.
                    plugin.beginTransaction();
                    plugin.prepareForDelete(omRole);
                    plugin.commitTransaction();
                }
                catch (TransactionStateException e)
                {
                    try
                    {
                        plugin.rollbackTransaction();
                    }
                    catch (TransactionStateException e1)
                    {
                        log.error("Failed to rollback transaction.", e);
                    }
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
     * @see org.apache.jetspeed.security.RoleManagerService#roleExists(java.lang.String)
     */
    public boolean roleExists(String roleFullPathName)
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" }, "roleExists(java.lang.String)");

        JetspeedRolePrincipal omRole = super.getJetspeedRolePrincipal(roleFullPathName);
        boolean roleExists = (null != omRole);
        return roleExists;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getRole(java.lang.String)
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
     * @see org.apache.jetspeed.security.RoleManagerService#getRolesForUser(java.lang.String)
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
     * @see org.apache.jetspeed.security.RoleManagerService#getUsersInRole(java.lang.String)
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
     * @see org.apache.jetspeed.security.RoleManagerService#getRolesForGroup(java.lang.String)
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
     * @see org.apache.jetspeed.security.RoleManagerService#getGroupsInRole(java.lang.String)
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
     * @see org.apache.jetspeed.security.RoleManagerService#addRoleToUser(java.lang.String, java.lang.String)
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
            try
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(omUser);
                omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                omUser.setRolePrincipals(omUserRoles);
                plugin.commitTransaction();
            }
            catch (TransactionStateException e)
            {
                try
                {
                    plugin.rollbackTransaction();
                }
                catch (TransactionStateException e1)
                {
                    log.error("Failed to rollback transaction.", e);
                }
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#removeRoleFromUser(java.lang.String, java.lang.String)
     */
    public void removeRoleFromUser(String username, String roleFullPathName)
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
                    try
                    {
                        plugin.beginTransaction();
                        plugin.prepareForUpdate(omUser);
                        omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                        omUser.setRolePrincipals(newOmRoles);
                        plugin.commitTransaction();
                    }
                    catch (TransactionStateException e)
                    {
                        try
                        {
                            plugin.rollbackTransaction();
                        }
                        catch (TransactionStateException e1)
                        {
                            log.error("Failed to rollback transaction.", e);
                        }
                    }
                }
            }
        }
    }

    /**
    * @see org.apache.jetspeed.security.RoleManagerService#isUserInRole(java.lang.String, java.lang.String)
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
     * @see org.apache.jetspeed.security.RoleManagerService#addRoleToGroup(java.lang.String, java.lang.String)
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
            try
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(omGroup);
                omGroup.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                omGroup.setRolePrincipals(omGroupRoles);
                plugin.commitTransaction();
            }
            catch (TransactionStateException e)
            {
                try
                {
                    plugin.rollbackTransaction();
                }
                catch (TransactionStateException e1)
                {
                    log.error("Failed to rollback transaction.", e);
                }
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#removeRoleFromGroup(java.lang.String, java.lang.String)
     */
    public void removeRoleFromGroup(String roleFullPathName, String groupFullPathName)
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
                    try
                    {
                        plugin.beginTransaction();
                        plugin.prepareForUpdate(omGroup);
                        omGroup.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                        omGroup.setRolePrincipals(newOmRoles);
                        plugin.commitTransaction();
                    }
                    catch (TransactionStateException e)
                    {
                        try
                        {
                            plugin.rollbackTransaction();
                        }
                        catch (TransactionStateException e1)
                        {
                            log.error("Failed to rollback transaction.", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#isGroupInRole(java.lang.String, java.lang.String)
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
