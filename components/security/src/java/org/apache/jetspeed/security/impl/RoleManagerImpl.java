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
import org.apache.jetspeed.security.AuthenticationProviderProxy;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>
 * Implementation for managing roles.
 * </p>
 * <p>
 * Role hierarchy elements are being returned as a {@link Role}collection. The
 * backing implementation must appropriately map the role hierarchy to a
 * preferences sub-tree.
 * </p>
 * <p>
 * The convention {principal}.{subprincipal} has been chosen to name roles
 * hierachies in order to support declarative security. Implementation follow
 * the conventions enforced by the {@link Preferences}API.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class RoleManagerImpl implements RoleManager
{
    /** The logger. */
    private static final Log log = LogFactory.getLog(RoleManagerImpl.class);

    /** The authentication provider proxy. */
    private AuthenticationProviderProxy atnProviderProxy = null;
    
    /** The role security handler. */
    private RoleSecurityHandler roleSecurityHandler = null;

    /** The security mapping handler. */
    private SecurityMappingHandler securityMappingHandler = null;

    /**
     * @param securityProvider The security provider.
     */
    public RoleManagerImpl(SecurityProvider securityProvider)
    {
        this.atnProviderProxy = securityProvider.getAuthenticationProviderProxy();
        this.roleSecurityHandler = securityProvider.getRoleSecurityHandler();
        this.securityMappingHandler = securityProvider.getSecurityMappingHandler();
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRole(java.lang.String)
     */
    public void addRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" },
                "addRole(java.lang.String)");

        // Check if role already exists.
        if (roleExists(roleFullPathName))
        {
            throw new SecurityException(SecurityException.ROLE_ALREADY_EXISTS + " " + roleFullPathName);
        }

        RolePrincipal rolePrincipal = new RolePrincipalImpl(roleFullPathName);
        String fullPath = rolePrincipal.getFullPath();
        // Add the preferences.
        Preferences preferences = Preferences.userRoot().node(fullPath);
        if (log.isDebugEnabled())
        {
            log.debug("Added role preferences node: " + fullPath);
        }
        try
        {
            if ((null != preferences) && preferences.absolutePath().equals(fullPath))
            {
                // Add role principal.
                roleSecurityHandler.setRolePrincipal(rolePrincipal);
                if (log.isDebugEnabled())
                {
                    log.debug("Added role: " + fullPath);
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
     * @see org.apache.jetspeed.security.RoleManager#removeRole(java.lang.String)
     */
    public void removeRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" },
                "removeRole(java.lang.String)");

        // Resolve the role hierarchy.
        Preferences prefs = Preferences.userRoot().node(
                RolePrincipalImpl.getFullPathFromPrincipalName(roleFullPathName));
        String[] roles = securityMappingHandler.getRoleHierarchyResolver().resolveChildren(prefs);
        for (int i = 0; i < roles.length; i++)
        {
            try
            {
                roleSecurityHandler.removeRolePrincipal(new RolePrincipalImpl(RolePrincipalImpl
                        .getPrincipalNameFromFullPath((String) roles[i])));
            }
            catch (Exception e)
            {
                String msg = "Unable to remove role: "
                        + RolePrincipalImpl.getPrincipalNameFromFullPath((String) roles[i]);
                log.error(msg, e);
                throw new SecurityException(msg, e);
            }
            // Remove preferences
            Preferences rolePref = Preferences.userRoot().node((String) roles[i]);
            try
            {
                rolePref.removeNode();
            }
            catch (BackingStoreException bse)
            {
                String msg = "Unable to remove role preferences: " + roles[i];
                log.error(msg, bse);
                throw new SecurityException(msg, bse);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#roleExists(java.lang.String)
     */
    public boolean roleExists(String roleFullPathName)
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" },
                "roleExists(java.lang.String)");

        Principal principal = roleSecurityHandler.getRolePrincipal(roleFullPathName);
        boolean roleExists = (null != principal);
        if (log.isDebugEnabled())
        {
            log.debug("Role exists: " + roleExists);
            log.debug("Role: " + roleFullPathName);
        }
        return roleExists;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRole(java.lang.String)
     */
    public Role getRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" },
                "getRole(java.lang.String)");

        String fullPath = RolePrincipalImpl.getFullPathFromPrincipalName(roleFullPathName);

        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleFullPathName);
        if (null == rolePrincipal)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }
        Preferences preferences = Preferences.userRoot().node(fullPath);
        Role role = new RoleImpl(rolePrincipal, preferences);
        return role;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesForUser(java.lang.String)
     */
    public Collection getRolesForUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "getRolesForUser(java.lang.String)");

        Collection roles = new ArrayList();

        Set rolePrincipals = securityMappingHandler.getRolePrincipals(username);
        Iterator rolePrincipalsIter = rolePrincipals.iterator();
        while (rolePrincipalsIter.hasNext())
        {
            Principal rolePrincipal = (Principal) rolePrincipalsIter.next();
            Preferences preferences = Preferences.userRoot().node(
                    RolePrincipalImpl.getFullPathFromPrincipalName(rolePrincipal.getName()));
            roles.add(new RoleImpl(rolePrincipal, preferences));
        }
        return roles;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesInGroup(java.lang.String)
     */
    public Collection getRolesInGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" },
                "getRolesInGroup(java.lang.String)");

        Collection roles = new ArrayList();

        Set rolePrincipals = securityMappingHandler.getRolePrincipalsInGroup(groupFullPathName);
        Iterator rolePrincipalsIter = rolePrincipals.iterator();
        while (rolePrincipalsIter.hasNext())
        {
            Principal rolePrincipal = (Principal) rolePrincipalsIter.next();
            Preferences preferences = Preferences.userRoot().node(
                    RolePrincipalImpl.getFullPathFromPrincipalName(rolePrincipal.getName()));
            roles.add(new RoleImpl(rolePrincipal, preferences));
        }
        return roles;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRoleToUser(java.lang.String,
     *      java.lang.String)
     */
    public void addRoleToUser(String username, String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, roleFullPathName }, new String[] { "username", "roleFullPathName" },
                "addUserToRole(java.lang.String, java.lang.String)");

        // Get the role principal to add to user.
        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleFullPathName);
        if (null == rolePrincipal)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }
        // Check that user exists.
        Principal userPrincipal = atnProviderProxy.getUserPrincipal(username);
        if (null == userPrincipal)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        // Get the user roles.
        Set rolePrincipals = securityMappingHandler.getRolePrincipals(username);
        // Add role to user.
        if (!rolePrincipals.contains(rolePrincipal))
        {
            securityMappingHandler.setRolePrincipal(username, roleFullPathName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRoleFromUser(java.lang.String,
     *      java.lang.String)
     */
    public void removeRoleFromUser(String username, String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, roleFullPathName }, new String[] { "username", "roleFullPathName" },
                "removeRoleFromUser(java.lang.String, java.lang.String)");

        // Check that user exists.
        Principal userPrincipal = atnProviderProxy.getUserPrincipal(username);
        if (null == userPrincipal)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        // Get the role principal to remove.
        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleFullPathName);
        if (null != rolePrincipal)
        {
            securityMappingHandler.removeRolePrincipal(username, roleFullPathName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#isUserInRole(java.lang.String,
     *      java.lang.String)
     */
    public boolean isUserInRole(String username, String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, roleFullPathName }, new String[] { "username", "roleFullPathName" },
                "isUserInRole(java.lang.String, java.lang.String)");

        boolean isUserInRole = false;

        Set rolePrincipals = securityMappingHandler.getRolePrincipals(username);
        Principal rolePrincipal = new RolePrincipalImpl(roleFullPathName);
        if (rolePrincipals.contains(rolePrincipal))
        {
            isUserInRole = true;
        }
        return isUserInRole;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRoleToGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addRoleToGroup(String roleFullPathName, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName, groupFullPathName }, new String[] { "roleFullPathName",
                "groupFullPathName" }, "addRoleToGroup(java.lang.String, java.lang.String)");

        // Get the role principal to add to group.
        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleFullPathName);
        if (null == rolePrincipal)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST + " " + roleFullPathName);
        }
        securityMappingHandler.setRolePrincipalInGroup(groupFullPathName, roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRoleFromGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeRoleFromGroup(String roleFullPathName, String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName, groupFullPathName }, new String[] { "roleFullPathName",
                "groupFullPathName" }, "removeRoleFromGroup(java.lang.String, java.lang.String)");
        
        // Get the role principal to remove.
        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleFullPathName);
        if (null != rolePrincipal)
        {
            securityMappingHandler.removeRolePrincipalInGroup(groupFullPathName, roleFullPathName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#isGroupInRole(java.lang.String,
     *      java.lang.String)
     */
    public boolean isGroupInRole(String groupFullPathName, String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName, groupFullPathName }, new String[] { "roleFullPathName",
                "groupFullPathName" }, "isGroupInRole(java.lang.String, java.lang.String)");

        boolean isGroupInRole = false;

        Set rolePrincipals = securityMappingHandler.getRolePrincipalsInGroup(groupFullPathName);
        Principal rolePrincipal = new RolePrincipalImpl(roleFullPathName);
        if (rolePrincipals.contains(rolePrincipal))
        {
            isGroupInRole = true;
        }

        return isGroupInRole;
    }

}