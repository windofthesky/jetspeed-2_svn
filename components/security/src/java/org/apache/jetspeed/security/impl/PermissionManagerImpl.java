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

import java.lang.reflect.Constructor;
import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.om.InternalPermission;
import org.apache.jetspeed.security.om.InternalPrincipal;
import org.apache.jetspeed.security.om.impl.InternalPermissionImpl;
import org.apache.jetspeed.security.om.impl.InternalPrincipalImpl;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>Implementation for managing {@link Permission} and permission
 * association to {@link Principal}.  Permissions are used to manage Principals
 * access entitlement on specified resources.</p>
 * <p>For instance:</p>
 * <pre><code>
 * grant principal o.a.j.security.UserPrincipal "theUserPrincipal"
 * {
 *     permission o.a.j.security.PortletPermission "myportlet", "view,edit,minimize,maximize";
 * };
 * </code><pre>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PermissionManagerImpl implements PermissionManager
{
    private static final Log log = LogFactory.getLog(PermissionManagerImpl.class);

    PersistenceStore persistenceStore;

    /**
     * <p>Constructor providing access to the persistence component.</p>
     */
    public PermissionManagerImpl(PersistenceStore persistenceStore)
    {
        if (persistenceStore == null)
        {
            throw new IllegalArgumentException("persistenceStore cannot be null for BaseSecurityImpl");
        }
        
        this.persistenceStore = persistenceStore;
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#getPermissions(java.security.Principal)
     */
    public Permissions getPermissions(Principal principal)
    {
        String fullPath = SecurityHelper.getPreferencesFullPath(principal);
        ArgUtil.notNull(new Object[] { fullPath }, new String[] { "fullPath" }, "removePermission(java.security.Principal)");

        // Remove permissions on principal.
        InternalPrincipal omPrincipal = getJetspeedPrincipal(fullPath);
        Collection omPermissions = new ArrayList();
        if (null != omPrincipal)
        {
            omPermissions = omPrincipal.getPermissions();
        }
        return getSecurityPermissions(omPermissions);
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#getPermissions(java.util.Collection)
     */
    public Permissions getPermissions(Collection principals)
    {
        ArgUtil.notNull(new Object[] { principals }, new String[] { "principals" }, "getPermissions(java.util.Collection)");

        Permissions permissions = new Permissions();
        Collection principalsFullPath = getPrincipalsFullPath(principals);
        if ((null != principalsFullPath) && principalsFullPath.size() > 0)
        {
            PersistenceStore store = getPersistenceStore();
            Filter filter = store.newFilter();
            filter.addIn("fullPath", principalsFullPath);
            Object query = store.newQuery(InternalPrincipalImpl.class, filter);
            Collection omPrincipals = store.getCollectionByQuery(query);
            Iterator omPrincipalsIterator = omPrincipals.iterator();
            while (omPrincipalsIterator.hasNext())
            {
                InternalPrincipal omPrincipal = (InternalPrincipal) omPrincipalsIterator.next();
                Collection omPermissions = omPrincipal.getPermissions();
                if (null != omPermissions)
                {
                    permissions = getSecurityPermissions(omPermissions);
                }
            }
        }
        return permissions;
    }

    /**
     * <p>Get the full path for the {@link Principal} in the collection.</p>
     * @param principals The collection of principals.
     * @return The collection of principals names.
     */
    private Collection getPrincipalsFullPath(Collection principals)
    {
        Collection principalsFullPath = new ArrayList();
        Iterator principalsIterator = principals.iterator();
        while (principalsIterator.hasNext())
        {
            Principal principal = (Principal) principalsIterator.next();
            String fullPath = SecurityHelper.getPreferencesFullPath(principal);
            if (null != fullPath)
            {
                principalsFullPath.add(fullPath);
            }
        }
        return principalsFullPath;
    }

    /**
     * <p>Iterate through a collection of {@link InternalPermission}
     * and build a collection of {@link java.security.Permission}.</p>
     * @param omPermissions The collection of {@link InternalPermission}.
     * @return The collection of {@link java.security.Permission}.
     */
    private Permissions getSecurityPermissions(Collection omPermissions)
    {
        Permissions permissions = new Permissions();
        Iterator omPermissionsIterator = omPermissions.iterator();
        while (omPermissionsIterator.hasNext())
        {
            InternalPermission omPermission = (InternalPermission) omPermissionsIterator.next();
            Permission permission = null;
            try
            {
                Class permissionClass = Class.forName(omPermission.getClassname());
                Class[] parameterTypes = { String.class, String.class };
                Constructor permissionConstructor = permissionClass.getConstructor(parameterTypes);
                Object[] initArgs = { omPermission.getName(), omPermission.getActions()};
                permission = (Permission) permissionConstructor.newInstance(initArgs);
                permissions.add(permission);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return permissions;
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#removePermission(java.security.Permission)
     */
    public void removePermission(Permission permission) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { permission }, new String[] { "permission" }, "removePermission(java.security.Permission)");

        InternalPermission omPermission = getJetspeedPermission(permission);
        if (null != omPermission)
        {
            Collection omPrincipals = omPermission.getPrincipals();
            if (null != omPrincipals)
            {
                omPrincipals.clear();
            }
            PersistenceStore store = getPersistenceStore();
            try
            {
                // TODO Can this be done in one shot?
                // Remove principals.
                store.lockForWrite(omPermission);
                omPermission.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                omPermission.setPrincipals(omPrincipals);
                store.getTransaction().checkpoint();

                // Remove permission.
                store.deletePersistent(omPermission);
                store.getTransaction().checkpoint();
            }
            catch (Exception e)
            {
                String msg = "Unable to lock Permission for update.";
                log.error(msg, e);
                store.getTransaction().rollback();
                throw new SecurityException(msg, e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#removePermissions(java.security.Principal)
     */
    public void removePermissions(Principal principal) throws SecurityException
    {
        String fullPath = SecurityHelper.getPreferencesFullPath(principal);
        ArgUtil.notNull(new Object[] { fullPath }, new String[] { "fullPath" }, "removePermission(java.security.Principal)");

        // Remove permissions on principal.
        InternalPrincipal omPrincipal = getJetspeedPrincipal(fullPath);
        if (null != omPrincipal)
        {
            Collection omPermissions = omPrincipal.getPermissions();
            if (null != omPermissions)
            {
                omPermissions.clear();
            }
            PersistenceStore store = getPersistenceStore();
            try
            {
                store.lockForWrite(omPrincipal);
                omPrincipal.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                omPrincipal.setPermissions(omPermissions);
                store.getTransaction().checkpoint();
            }
            catch (Exception e)
            {
                String msg = "Unable to lock Principal for update.";
                log.error(msg, e);
                store.getTransaction().rollback();
                throw new SecurityException(msg, e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#grantPermission(java.security.Principal, java.security.Permission)
     */
    public void grantPermission(Principal principal, Permission permission) throws SecurityException
    {
        String fullPath = SecurityHelper.getPreferencesFullPath(principal);
        ArgUtil.notNull(
            new Object[] { fullPath, permission },
            new String[] { "fullPath", "permission" },
            "grantPermission(java.security.Principal, java.security.Permission)");

        boolean createPermission = true;
        Collection omPermissions = new ArrayList();

        InternalPrincipal omPrincipal = getJetspeedPrincipal(fullPath);
        if (null == omPrincipal)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST + ": " + principal.getName());
        }
        InternalPermission omPermission = getJetspeedPermission(permission);
        if (null == omPermission)
        {
            omPermission =
                new InternalPermissionImpl(permission.getClass().getName(), permission.getName(), permission.getActions());
        }

        if (null != omPrincipal.getPermissions())
        {
            omPermissions.addAll(omPrincipal.getPermissions());
        }
        if (!omPermissions.contains(omPermission))
        {
            omPermissions.add(omPermission);
        }
        PersistenceStore store = getPersistenceStore();
        try
        {
            store.lockForWrite(omPrincipal);
            omPrincipal.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            omPrincipal.setPermissions(omPermissions);
            store.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to lock Principal for update.";
            log.error(msg, e);
            store.getTransaction().rollback();
            throw new SecurityException(msg, e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#revokePermission(java.security.Principal, java.security.Permission)
     */
    public void revokePermission(Principal principal, Permission permission) throws SecurityException
    {
        String fullPath = SecurityHelper.getPreferencesFullPath(principal);
        ArgUtil.notNull(
            new Object[] { fullPath, permission },
            new String[] { "fullPath", "permission" },
            "revokePermission(java.security.Principal, java.security.Permission)");

        // Remove permissions on principal.
        InternalPrincipal omPrincipal = getJetspeedPrincipal(fullPath);
        if (null != omPrincipal)
        {
            Collection omPermissions = omPrincipal.getPermissions();
            if (null != omPermissions)
            {
                boolean revokePermission = false;
                ArrayList newOmPermissions = new ArrayList();
                Iterator omPermissionsIterator = omPermissions.iterator();
                while (omPermissionsIterator.hasNext())
                {
                    InternalPermission omPermission = (InternalPermission) omPermissionsIterator.next();
                    if (!((omPermission.getClassname().equals(permission.getClass().getName()))
                        && (omPermission.getName().equals(permission.getName()))
                        && (omPermission.getActions().equals(permission.getActions()))))
                    {
                        newOmPermissions.add(omPermission);
                    }
                    else
                    {
                        revokePermission = true;
                    }
                }
                if (revokePermission)
                {
                    PersistenceStore store = getPersistenceStore();
                    try
                    {
                        store.lockForWrite(omPrincipal);
                        omPrincipal.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                        omPrincipal.setPermissions(newOmPermissions);
                        store.getTransaction().checkpoint();
                    }
                    catch (Exception e)
                    {
                        String msg = "Unable to lock Principal for update.";
                        log.error(msg, e);
                        store.getTransaction().rollback();
                        throw new SecurityException(msg, e);
                    }
                }
            }
        }
    }

    /**
     * <p>Returns the {@link InternalPrincipal} from the full path.</p>
     * @param fullPath The full path.
     * @return The {@link InternalPrincipal}.
     */
    InternalPrincipal getJetspeedPrincipal(String fullPath)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("fullPath", fullPath);
        Object query = store.newQuery(InternalPrincipalImpl.class, filter);
        InternalPrincipal omPrincipal = (InternalPrincipal) store.getObjectByQuery(query);
        return omPrincipal;
    }

    /**
     * <p>Returns the {@link InternalPermission} from the full path.</p>
     * @param fullPath The full path.
     * @return The {@link InternalPermission}.
     */
    InternalPermission getJetspeedPermission(Permission permission)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("classname", permission.getClass().getName());
        filter.addEqualTo("name", permission.getName());
        filter.addEqualTo("actions", permission.getActions());
        Object query = store.newQuery(InternalPermissionImpl.class, filter);
        InternalPermission omPermission = (InternalPermission) store.getObjectByQuery(query);
        return omPermission;
    }

    /**
     * <p>Utility method to get the persistence store and initiate
     * the transaction if not open.</p>
     * @return The persistence store.
     */
    PersistenceStore getPersistenceStore()
    {
    
        return persistenceStore;
    }

}