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
package org.apache.jetspeed.security.spi.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.InternalRolePrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalGroupPrincipalImpl;
import org.apache.jetspeed.security.om.impl.InternalRolePrincipalImpl;
import org.apache.jetspeed.security.om.impl.InternalUserPrincipalImpl;

/**
 * <p>
 * Provides a utility class for common SPI queries.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class CommonQueries
{
    private static final Log log = LogFactory.getLog(CommonQueries.class);

    /** The persistence store. * */
    private PersistenceStore persistenceStore;

    /**
     * @param persistenceStore The persistence store.
     */
    public CommonQueries(PersistenceStore persistenceStore)
    {
        if (persistenceStore == null)
        {
            throw new IllegalArgumentException("persistenceStore cannot be null for BaseSecurityImpl");
        }

        this.persistenceStore = persistenceStore;
    }

    /**
     * <p>
     * Returns the {@link InternalUserPrincipal}from the user name.
     * </p>
     * 
     * @param username The user name.
     * @return The {@link InternalUserPrincipal}.
     */
    public InternalUserPrincipal getInternalUserPrincipal(String username)
    {
        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        // Get user.
        Filter filter = persistenceStore.newFilter();
        filter.addEqualTo("fullPath", fullPath);
        Object query = persistenceStore.newQuery(InternalUserPrincipalImpl.class, filter);
        InternalUserPrincipal internalUser = (InternalUserPrincipal) persistenceStore.getObjectByQuery(query);
        return internalUser;
    }

    /**
     * <p>
     * Returns a collection of {@link Principal}given the filter.
     * </p>
     * 
     * @param filter The filter.
     * @return Collection of {@link InternalUserPrincipal}.
     */
    public Iterator getInternalUserPrincipals(String filter)
    {
        Iterator result = persistenceStore.getExtent(InternalUserPrincipalImpl.class).iterator();
        return result;
    }

    /**
     * <p>
     * Sets the given {@link InternalUserPrincipal}.
     * </p>
     * 
     * @param internalUser The {@link InternalUserPrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void setInternalUserPrincipal(InternalUserPrincipal internalUser) throws SecurityException
    {
        try
        {
            persistenceStore.lockForWrite(internalUser);
            persistenceStore.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to lock user for update.";
            log.error(msg, e);
            persistenceStore.getTransaction().rollback();
            throw new SecurityException(msg, e);
        }
    }

    /**
     * <p>
     * Remove the given {@link InternalUserPrincipal}.
     * </p>
     * 
     * @param internalUser The {@link InternalUserPrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void removeInternalUserPrincipal(InternalUserPrincipal internalUser) throws SecurityException
    {
        try
        {
            // Remove user.
            persistenceStore.deletePersistent(internalUser);
            persistenceStore.getTransaction().checkpoint();
            if (log.isDebugEnabled())
            {
                log.debug("Deleted user: " + internalUser.getFullPath());
            }

        }
        catch (Exception e)
        {
            String msg = "Unable to lock User for update.";
            log.error(msg, e);
            persistenceStore.getTransaction().rollback();
            throw new SecurityException(msg, e);
        }
    }

    /**
     * <p>
     * Returns the {@link InternalRolePrincipal}from the role full path name.
     * </p>
     * 
     * @param username The role full path name.
     * @return The {@link InternalRolePrincipal}.
     */
    public InternalRolePrincipal getInternalRolePrincipal(String roleFullPathName)
    {
        Filter filter = persistenceStore.newFilter();
        filter.addEqualTo("fullPath", roleFullPathName);
        Object query = persistenceStore.newQuery(InternalRolePrincipalImpl.class, filter);
        InternalRolePrincipal internalRole = (InternalRolePrincipal) persistenceStore.getObjectByQuery(query);
        return internalRole;
    }
    
    /**
     * <p>
     * Sets the given {@link InternalRolePrincipal}.
     * </p>
     * 
     * @param internalRole The {@link InternalRolePrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void setInternalRolePrincipal(InternalRolePrincipal internalRole) throws SecurityException
    {
        try
        {
            persistenceStore.lockForWrite(internalRole);
            persistenceStore.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to lock role for update.";
            log.error(msg, e);
            persistenceStore.getTransaction().rollback();
            throw new SecurityException(msg, e);
        }
    }
    
    /**
     * <p>
     * Remove the given {@link InternalRolePrincipal}.
     * </p>
     * 
     * @param internalRole The {@link InternalRolePrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void removeInternalRolePrincipal(InternalRolePrincipal internalRole) throws SecurityException
    {
        try
        {
            // Remove role.
            persistenceStore.deletePersistent(internalRole);
            persistenceStore.getTransaction().checkpoint();
            if (log.isDebugEnabled())
            {
                log.debug("Deleted role: " + internalRole.getFullPath());
            }

        }
        catch (Exception e)
        {
            String msg = "Unable to lock Role for update.";
            log.error(msg, e);
            persistenceStore.getTransaction().rollback();
            throw new SecurityException(msg, e);
        }
        
        /*InternalRolePrincipal omParentRole = super.getJetspeedRolePrincipal(roleFullPathName);
        if (null != omParentRole)
        {
            PersistenceStore store = getPersistenceStore();
            Filter filter = store.newFilter();
            filter.addLike((Object) new String("fullPath"), (Object) (omParentRole.getFullPath() + "/*"));
            Object query = store.newQuery(InternalRolePrincipalImpl.class, filter);
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
                InternalRolePrincipal omRole = (InternalRolePrincipal) omRolesIterator.next();
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
        }*/
    }

    /**
     * <p>
     * Returns the {@link InternalGroupPrincipal}from the group full path name.
     * </p>
     * 
     * @param username The group full path name.
     * @return The {@link InternalGroupPrincipal}.
     */
    public InternalGroupPrincipal getInternalGroupPrincipal(String groupFullPathName)
    {
        Filter filter = persistenceStore.newFilter();
        filter.addEqualTo("fullPath", groupFullPathName);
        Object query = persistenceStore.newQuery(InternalGroupPrincipalImpl.class, filter);
        InternalGroupPrincipal internalGroup = (InternalGroupPrincipal) persistenceStore.getObjectByQuery(query);
        return internalGroup;
    }

}