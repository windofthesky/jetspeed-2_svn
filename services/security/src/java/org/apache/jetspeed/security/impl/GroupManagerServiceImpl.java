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
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManagerService;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.JetspeedGroupPrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedGroupPrincipalImpl;
import org.apache.jetspeed.security.om.JetspeedRolePrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedRolePrincipalImpl;
import org.apache.jetspeed.security.om.JetspeedUserPrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedUserPrincipalImpl;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>Describes the service interface for managing groups.</p>
 * <p>Group hierarchy elements are being returned as a {@link Group}
 * collection.  The backing implementation must appropriately map 
 * the group hierarchy to a preferences sub-tree.</p>
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 */
public class GroupManagerServiceImpl extends BaseSecurityServiceImpl implements GroupManagerService
{

    /** <p>The persistence plugin.</p> */
    private PersistencePlugin plugin;

    /** <p>The persistence plugin.</p> */
    private PermissionManagerService pms;

    /**
     * <p>Default Constructor.</p>
     */
    public GroupManagerServiceImpl()
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
     * @see org.apache.jetspeed.security.GroupManagerService#addGroup(java.lang.String)
     */
    public void addGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" }, "addGroup(java.lang.String)");

        GroupPrincipal groupPrincipal = new GroupPrincipalImpl(groupFullPathName);
        String fullPath = groupPrincipal.getFullPath();
        // Check if group already exists.
        if (groupExists(groupFullPathName))
        {
            throw new SecurityException(SecurityException.GROUP_ALREADY_EXISTS + " " + groupFullPathName);
        }

        // If does not exist, create.
        JetspeedGroupPrincipal omGroup = new JetspeedGroupPrincipalImpl(fullPath);
        Preferences preferences = Preferences.userRoot().node(fullPath);
        try
        {
            if ((null != preferences) && preferences.absolutePath().equals(fullPath))
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(omGroup);
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
     * @see org.apache.jetspeed.security.GroupManagerService#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupFullPathName)
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" }, "removeGroup(java.lang.String)");

        JetspeedGroupPrincipal omParentGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        if (null != omParentGroup)
        {
            LookupCriteria c2 = plugin.newLookupCriteria();
            c2.addLike((Object) new String("fullPath"), (Object) (omParentGroup.getFullPath() + "/*"));
            Object query2 = plugin.generateQuery(JetspeedGroupPrincipalImpl.class, c2);
            Collection omGroups = plugin.getCollectionByQuery(JetspeedGroupPrincipalImpl.class, query2);
            if (null == omGroups)
            {
                omGroups = new ArrayList();
            }
            omGroups.add(omParentGroup);
            // Remove each group in the collection.
            Iterator omGroupsIterator = omGroups.iterator();
            while (omGroupsIterator.hasNext())
            {
                JetspeedGroupPrincipal omGroup = (JetspeedGroupPrincipal) omGroupsIterator.next();
                // TODO This should be managed in a transaction.
                Collection omUsers = omGroup.getUserPrincipals();
                if (null != omUsers)
                {
                    omUsers.clear();
                }
                Collection omRoles = omGroup.getRolePrincipals();
                if (null != omRoles)
                {
                    omRoles.clear();
                }
                Collection omPermissions = omGroup.getPermissions();
                if (null != omPermissions)
                {
                    omPermissions.clear();
                }
                try
                {
                    // Remove dependencies.
                    plugin.beginTransaction();
                    plugin.prepareForUpdate(omGroup);
                    omGroup.setUserPrincipals(omUsers);
                    omGroup.setRolePrincipals(omRoles);
                    omGroup.setPermissions(omPermissions);
                    plugin.commitTransaction();

                    // Remove group.
                    plugin.beginTransaction();
                    plugin.prepareForDelete(omGroup);
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
                Preferences preferences = Preferences.userRoot().node(omGroup.getFullPath());
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
     * @see org.apache.jetspeed.security.GroupManagerService#groupExists(java.lang.String)
     */
    public boolean groupExists(String groupFullPathName)
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" }, "groupExists(java.lang.String)");

        JetspeedGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        boolean groupExists = (null != omGroup);
        return groupExists;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#getGroup(java.lang.String)
     */
    public Group getGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" }, "getGroup(java.lang.String)");

        JetspeedGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        if (null == omGroup)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST + " " + groupFullPathName);
        }
        return super.getGroup(omGroup);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#getGroupsForUser(java.lang.String)
     */
    public Collection getGroupsForUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "getGroupsForUser(java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        Collection omUserGroups = omUser.getGroupPrincipals();
        return super.getGroups(omUserGroups);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#getUsersInGroup(java.lang.String)
     */
    public Collection getUsersInGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { groupFullPathName },
            new String[] { "groupFullPathName" },
            "getUsersInGroup(java.lang.String)");

        JetspeedGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        if (null == omGroup)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST + " " + groupFullPathName);
        }
        Collection users = new ArrayList();
        Collection omGroupUsers = omGroup.getUserPrincipals();
        return super.getUsers(omGroupUsers);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManagerService#addUserToGroup(java.lang.String, java.lang.String)
     */
    public void addUserToGroup(String username, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { username, groupFullPathName },
            new String[] { "username", "groupFullPathName" },
            "addUserToGroup(java.lang.String, java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }

        JetspeedGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        if (null == omGroup)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST + " " + groupFullPathName);
        }

        Collection omUserGroups = omUser.getGroupPrincipals();
        if (null == omUserGroups)
        {
            omUserGroups = new ArrayList();
        }
        if (!omUserGroups.contains(omGroup))
        {
            omUserGroups.add(omGroup);
            try
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(omUser);
                omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                omUser.setGroupPrincipals(omUserGroups);
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
     * @see org.apache.jetspeed.security.GroupManagerService#removeUserFromGroup(java.lang.String, java.lang.String)
     */
    public void removeUserFromGroup(String username, String groupFullPathName)
    {
        ArgUtil.notNull(
            new Object[] { username, groupFullPathName },
            new String[] { "username", "groupFullPathName" },
            "removeUserFromGroup(java.lang.String, java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        // TODO This should be managed in a transaction.
        if (null != omUser)
        {
            Collection omGroups = omUser.getGroupPrincipals();
            if (null != omGroups)
            {
                Collection newOmGroups = super.removeGroup(omGroups, groupFullPathName);
                if (newOmGroups.size() < omGroups.size())
                {
                    try
                    {
                        plugin.beginTransaction();
                        plugin.prepareForUpdate(omUser);
                        omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                        omUser.setGroupPrincipals(newOmGroups);
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
     * @see org.apache.jetspeed.security.GroupManagerService#isUserInGroup(java.lang.String, java.lang.String)
     */
    public boolean isUserInGroup(String username, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { username, groupFullPathName },
            new String[] { "username", "groupFullPathName" },
            "isUserInGroup(java.lang.String, java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        JetspeedGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        if (null == omGroup)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST + " " + groupFullPathName);
        }
        boolean isUserInGroup = false;
        Collection omGroups = omUser.getGroupPrincipals();
        if ((null != omGroups) && (omGroups.contains(omGroup)))
        {
            isUserInGroup = true;
        }
        return isUserInGroup;
    }

}
