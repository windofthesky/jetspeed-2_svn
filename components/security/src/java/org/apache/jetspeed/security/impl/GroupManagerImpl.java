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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalGroupPrincipalImpl;
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
public class GroupManagerImpl extends BaseSecurityImpl implements GroupManager
{
    /** The logger. */
    private static final Log log = LogFactory.getLog(GroupManagerImpl.class);

    /** The group security handler. */
    private GroupSecurityHandler groupSecurityHandler = null;

    /** The security mapping handler. */
    private SecurityMappingHandler securityMappingHandler = null;

    /**
     * @param persistenceStore
     */
    public GroupManagerImpl(PersistenceStore persistenceStore)
    {
        super(persistenceStore);
    }

    /**
     * @param securityProvider The security provider.
     */
    public GroupManagerImpl(PersistenceStore persistenceStore, SecurityProvider securityProvider)
    {
        super(persistenceStore);
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

        GroupPrincipal groupPrincipal = new GroupPrincipalImpl(groupFullPathName);
        String fullPath = groupPrincipal.getFullPath();
        // Check if group already exists.
        if (groupExists(groupFullPathName))
        {
            throw new SecurityException(SecurityException.GROUP_ALREADY_EXISTS + " " + groupFullPathName);
        }

        // If does not exist, create.
        InternalGroupPrincipal omGroup = new InternalGroupPrincipalImpl(fullPath);
        Preferences preferences = Preferences.userRoot().node(fullPath);
        PersistenceStore store = getPersistenceStore();
        try
        {
            if ((null != preferences) && preferences.absolutePath().equals(fullPath))
            {
                store.lockForWrite(omGroup);
                store.getTransaction().checkpoint();
            }
        }
        catch (Exception e)
        {
            String msg = "Unable to lock Group for update.";
            log.error(msg, e);
            store.getTransaction().rollback();
            throw new SecurityException(msg, e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" },
                "removeGroup(java.lang.String)");

        InternalGroupPrincipal omParentGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
        if (null != omParentGroup)
        {
            PersistenceStore store = getPersistenceStore();
            Filter filter = store.newFilter();
            filter.addLike("fullPath", omParentGroup.getFullPath() + "/*");
            Object query = store.newQuery(InternalGroupPrincipalImpl.class, filter);
            Collection omGroups = store.getCollectionByQuery(query);
            if (null == omGroups)
            {
                omGroups = new ArrayList();
            }
            omGroups.add(omParentGroup);
            // Remove each group in the collection.
            Iterator omGroupsIterator = omGroups.iterator();
            while (omGroupsIterator.hasNext())
            {
                InternalGroupPrincipal omGroup = (InternalGroupPrincipal) omGroupsIterator.next();
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
                    // TODO Can this be done in one shot?
                    // Remove dependencies.
                    store.lockForWrite(omGroup);
                    omGroup.setUserPrincipals(omUsers);
                    omGroup.setRolePrincipals(omRoles);
                    omGroup.setPermissions(omPermissions);
                    store.getTransaction().checkpoint();

                    // Remove group.
                    store.deletePersistent(omGroup);
                    store.getTransaction().checkpoint();
                }
                catch (Exception e)
                {
                    String msg = "Unable to lock Group for update.";
                    log.error(msg, e);
                    store.getTransaction().rollback();
                    throw new SecurityException(msg, e);
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

        InternalUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        Collection omUserGroups = omUser.getGroupPrincipals();
        return super.getGroups(omUserGroups);
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

        InternalUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }

        InternalGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
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
            PersistenceStore store = getPersistenceStore();
            try
            {
                store.lockForWrite(omUser);
                omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                omUser.setGroupPrincipals(omUserGroups);
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
     * @see org.apache.jetspeed.security.GroupManager#removeUserFromGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeUserFromGroup(String username, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, groupFullPathName }, new String[] { "username", "groupFullPathName" },
                "removeUserFromGroup(java.lang.String, java.lang.String)");

        InternalUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        // TODO This should be managed in a transaction.
        if (null != omUser)
        {
            Collection omGroups = omUser.getGroupPrincipals();
            if (null != omGroups)
            {
                Collection newOmGroups = super.removeGroup(omGroups, groupFullPathName);
                if (newOmGroups.size() < omGroups.size())
                {
                    PersistenceStore store = getPersistenceStore();
                    try
                    {
                        store.lockForWrite(omUser);
                        omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                        omUser.setGroupPrincipals(newOmGroups);
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
     * @see org.apache.jetspeed.security.GroupManager#isUserInGroup(java.lang.String,
     *      java.lang.String)
     */
    public boolean isUserInGroup(String username, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, groupFullPathName }, new String[] { "username", "groupFullPathName" },
                "isUserInGroup(java.lang.String, java.lang.String)");

        InternalUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        InternalGroupPrincipal omGroup = super.getJetspeedGroupPrincipal(groupFullPathName);
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