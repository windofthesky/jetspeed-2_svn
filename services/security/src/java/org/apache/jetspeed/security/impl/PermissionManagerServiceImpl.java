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
package org.apache.jetspeed.security.impl;

import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.jetspeed.security.PermissionManagerService;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.om.JetspeedPermission;
import org.apache.jetspeed.security.om.JetspeedPrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedPermissionImpl;
import org.apache.jetspeed.security.om.impl.JetspeedPrincipalImpl;
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
public class PermissionManagerServiceImpl extends BaseCommonService implements PermissionManagerService
{

    /** <p>The persistence plugin.</p> */
    private PersistencePlugin plugin;

    /**
     * <p>Default Constructor.</p>
     */
    public PermissionManagerServiceImpl()
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
            setInit(true);
        }
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManagerService#getPermissions(java.security.Principal)
     */
    public Permissions getPermissions(Principal principal)
    {
        String fullPath = SecurityHelper.getPrincipalFullPath(principal);
        ArgUtil.notNull(new Object[] { fullPath }, new String[] { "fullPath" }, "removePermission(java.security.Principal)");

        // Remove permissions on principal.
        JetspeedPrincipal omPrincipal = getJetspeedPrincipal(fullPath);
        Collection omPermissions = new ArrayList();
        if (null != omPrincipal)
        {
            omPermissions = omPrincipal.getPermissions();
        }
        return getSecurityPermissions(omPermissions);
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManagerService#getPermissions(java.util.Collection)
     */
    public Permissions getPermissions(Collection principals)
    {
        ArgUtil.notNull(new Object[] { principals }, new String[] { "principals" }, "getPermissions(java.util.Collection)");

        Permissions permissions = new Permissions();
        Collection principalsFullPath = getPrincipalsFullPath(principals);
        if ((null != principalsFullPath) && principalsFullPath.size() > 0)
        {
            LookupCriteria c = plugin.newLookupCriteria();
            c.addIn("fullPath", principalsFullPath);
            Object query = plugin.generateQuery(JetspeedPrincipalImpl.class, c);
            Collection omPrincipals = plugin.getCollectionByQuery(JetspeedPrincipalImpl.class, query);
            Iterator omPrincipalsIterator = omPrincipals.iterator();
            while (omPrincipalsIterator.hasNext())
            {
                JetspeedPrincipal omPrincipal = (JetspeedPrincipal) omPrincipalsIterator.next();
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
            String fullPath = SecurityHelper.getPrincipalFullPath(principal);
            if (null != fullPath)
            {
                principalsFullPath.add(fullPath);
            } 
        }
        return principalsFullPath;
    }

    /**
     * <p>Iterate through a collection of {@link JetspeedPermission}
     * and build a collection of {@link java.security.Permission}.</p>
     * @param omPermissions The collection of {@link JetspeedPermission}.
     * @return The collection of {@link java.security.Permission}.
     */
    private Permissions getSecurityPermissions(Collection omPermissions)
    {
        Permissions permissions = new Permissions();
        Iterator omPermissionsIterator = omPermissions.iterator();
        while (omPermissionsIterator.hasNext())
        {
            JetspeedPermission omPermission = (JetspeedPermission) omPermissionsIterator.next();
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
     * @see org.apache.jetspeed.security.PermissionManagerService#removePermission(java.security.Permission)
     */
    public void removePermission(Permission permission)
    {
        ArgUtil.notNull(new Object[] { permission }, new String[] { "permission" }, "removePermission(java.security.Permission)");

        JetspeedPermission omPermission = getJetspeedPermission(permission);
        if (null != omPermission)
        {
            Collection omPrincipals = omPermission.getPrincipals();
            if (null != omPrincipals)
            {
                omPrincipals.clear();
            }
            try
            {
                // Remove principals.
                plugin.beginTransaction();
                plugin.prepareForUpdate(omPermission);
                omPermission.setModifiedDate(new Timestamp(System.currentTimeMillis()));  
                omPermission.setPrincipals(omPrincipals);
                plugin.commitTransaction();

                // Remove permission.
                plugin.beginTransaction();
                plugin.prepareForDelete(omPermission);
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
     * @see org.apache.jetspeed.security.PermissionManagerService#removePermissions(java.security.Principal)
     */
    public void removePermissions(Principal principal)
    {
        String fullPath = SecurityHelper.getPrincipalFullPath(principal);
        ArgUtil.notNull(new Object[] { fullPath }, new String[] { "fullPath" }, "removePermission(java.security.Principal)");

        // Remove permissions on principal.
        JetspeedPrincipal omPrincipal = getJetspeedPrincipal(fullPath);
        if (null != omPrincipal)
        {
            Collection omPermissions = omPrincipal.getPermissions();
            if (null != omPermissions)
            {
                omPermissions.clear();
            }
            try
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(omPrincipal);
                omPrincipal.setModifiedDate(new Timestamp(System.currentTimeMillis()));  
                omPrincipal.setPermissions(omPermissions);
                plugin.commitTransaction();

                // Remove principal.
                plugin.beginTransaction();
                plugin.prepareForDelete(omPrincipal);
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
     * @see org.apache.jetspeed.security.PermissionManagerService#grantPermission(java.security.Principal, java.security.Permission)
     */
    public void grantPermission(Principal principal, Permission permission) throws SecurityException
    {
        String fullPath = SecurityHelper.getPrincipalFullPath(principal);
        ArgUtil.notNull(
            new Object[] { fullPath, permission },
            new String[] { "fullPath", "permission" },
            "grantPermission(java.security.Principal, java.security.Permission)");

        boolean createPermission = true;
        Collection omPermissions = new ArrayList();

        JetspeedPrincipal omPrincipal = getJetspeedPrincipal(fullPath);
        if (null == omPrincipal)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST + ": " + principal.getName());
        }
        JetspeedPermission omPermission = getJetspeedPermission(permission);
        if (null == omPermission)
        {
            omPermission =
                new JetspeedPermissionImpl(permission.getClass().getName(), permission.getName(), permission.getActions());
        }

        if (null != omPrincipal.getPermissions())
        {
            omPermissions.addAll(omPrincipal.getPermissions());
        }
        if (!omPermissions.contains(omPermission))
        {
            omPermissions.add(omPermission);
        }
        try
        {
            plugin.beginTransaction();
            plugin.prepareForUpdate(omPrincipal);
            omPrincipal.setModifiedDate(new Timestamp(System.currentTimeMillis()));  
            omPrincipal.setPermissions(omPermissions);
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

    /**
     * @see org.apache.jetspeed.security.PermissionManagerService#revokePermission(java.security.Principal, java.security.Permission)
     */
    public void revokePermission(Principal principal, Permission permission)
    {
        String fullPath = SecurityHelper.getPrincipalFullPath(principal);
        ArgUtil.notNull(
            new Object[] { fullPath, permission },
            new String[] { "fullPath", "permission" },
            "revokePermission(java.security.Principal, java.security.Permission)");

        // Remove permissions on principal.
        JetspeedPrincipal omPrincipal = getJetspeedPrincipal(fullPath);
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
                    JetspeedPermission omPermission = (JetspeedPermission) omPermissionsIterator.next();
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
                    try
                    {
                        plugin.beginTransaction();
                        plugin.prepareForUpdate(omPrincipal);
                        omPrincipal.setModifiedDate(new Timestamp(System.currentTimeMillis()));  
                        omPrincipal.setPermissions(newOmPermissions);
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
     * <p>Returns the {@link JetspeedPrincipal} from the full path.</p>
     * @param fullPath The full path.
     * @return The {@link JetspeedPrincipal}.
     */
    JetspeedPrincipal getJetspeedPrincipal(String fullPath)
    {
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("fullPath", fullPath);
        Object query = plugin.generateQuery(JetspeedPrincipalImpl.class, c);
        JetspeedPrincipal omPrincipal = (JetspeedPrincipal) plugin.getObjectByQuery(JetspeedPrincipalImpl.class, query);
        return omPrincipal;
    }

    /**
     * <p>Returns the {@link JetspeedPermission} from the full path.</p>
     * @param fullPath The full path.
     * @return The {@link JetspeedPermission}.
     */
    JetspeedPermission getJetspeedPermission(Permission permission)
    {
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("classname", permission.getClass().getName());
        c.addEqualTo("name", permission.getName());
        c.addEqualTo("actions", permission.getActions());
        Object query = plugin.generateQuery(JetspeedPermissionImpl.class, c);
        JetspeedPermission omPermission = (JetspeedPermission) plugin.getObjectByQuery(JetspeedPermissionImpl.class, query);
        return omPermission;   
    }

}