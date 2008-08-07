/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.AuthenticationProviderProxy;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.attributes.SecurityAttributes;
import org.apache.jetspeed.security.attributes.SecurityAttributesProvider;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;

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
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
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

    private SecurityAttributesProvider attributesProvider;
    
    /**
     * @param securityProvider The security provider.
     */
    public RoleManagerImpl(SecurityProvider securityProvider, SecurityAttributesProvider attributesProvider)
    {
        this.atnProviderProxy = securityProvider.getAuthenticationProviderProxy();
        this.roleSecurityHandler = securityProvider.getRoleSecurityHandler();
        this.securityMappingHandler = securityProvider.getSecurityMappingHandler();
        this.attributesProvider = attributesProvider;        
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRole(java.lang.String)
     */
    public void addRole(String roleName) throws SecurityException
    {
        if (roleExists(roleName)) 
        {  
            throw new SecurityException(SecurityException.ROLE_ALREADY_EXISTS.create(roleName)); 
        }
        RolePrincipal rolePrincipal = new RolePrincipalImpl(roleName);        
        roleSecurityHandler.storeRolePrincipal(rolePrincipal);
        SecurityAttributes sa = attributesProvider.createSecurityAttributes(rolePrincipal);
        attributesProvider.saveAttributes(sa);
        if (log.isDebugEnabled())
            log.debug("Added role: " + roleName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRole(java.lang.String)
     */
    public void removeRole(String roleName) throws SecurityException
    {
        if (securityMappingHandler.getHierarchyResolver() != null)
        {
            Set<RolePrincipal> roles = securityMappingHandler.getHierarchyResolver().resolveRoles(roleName);
            for (RolePrincipal gp : roles)
            {
                roleSecurityHandler.removeRolePrincipal(gp);
//                TODO: should we use cascading deletes?
                attributesProvider.deleteAttributes(gp);
            }
        }
        else
        {
            RolePrincipal rp = roleSecurityHandler.getRolePrincipal(roleName);
            if (rp != null)
            {
                roleSecurityHandler.removeRolePrincipal(new RolePrincipalImpl(roleName));
//              TODO: should we use cascading deletes?
                attributesProvider.deleteAttributes(rp);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#roleExists(java.lang.String)
     */
    public boolean roleExists(String roleName)
    {
        Principal principal = roleSecurityHandler.getRolePrincipal(roleName);
        boolean roleExists = (null != principal);
        return roleExists;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRole(java.lang.String)
     */
    public Role getRole(String roleName) throws SecurityException
    {
        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleName);
        if (null == rolePrincipal) 
        { 
            throw new SecurityException(
                SecurityException.ROLE_DOES_NOT_EXIST.create(roleName)); 
        }
        SecurityAttributes attributes = this.attributesProvider.retrieveAttributes(rolePrincipal);
        Role role = new RoleImpl(rolePrincipal, attributes);
        return role;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesForUser(java.lang.String)
     */
    public Collection<Role> getRolesForUser(String username) throws SecurityException
    {
        Collection<Role> roles = new ArrayList<Role>();

        Set<RolePrincipal> rolePrincipals = securityMappingHandler.getRolePrincipals(username);
        for (RolePrincipal rolePrincipal : rolePrincipals)
        {
            SecurityAttributes attributes = this.attributesProvider.retrieveAttributes(rolePrincipal);
            roles.add(new RoleImpl(rolePrincipal, attributes));
        }
        return roles;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesInGroup(java.lang.String)
     */
    public Collection<Role> getRolesInGroup(String groupName) throws SecurityException
    {
        Collection<Role> roles = new ArrayList<Role>();
        Set<RolePrincipal> rolePrincipals = securityMappingHandler.getRolePrincipalsInGroup(groupName);
        for (RolePrincipal rolePrincipal : rolePrincipals)
        {
            SecurityAttributes attributes = this.attributesProvider.retrieveAttributes(rolePrincipal);
            roles.add(new RoleImpl(rolePrincipal, attributes));
        }
        return roles;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRoleToUser(java.lang.String,
     *      java.lang.String)
     */
    public void addRoleToUser(String username, String roleName) throws SecurityException
    {
        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleName);
        if (null == rolePrincipal)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST.create(roleName));
        }
        Principal userPrincipal = atnProviderProxy.getUserPrincipal(username);
        if (null == userPrincipal)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(username));
        }
        Set<RolePrincipal> rolePrincipals = securityMappingHandler.getRolePrincipals(username);
        if (!rolePrincipals.contains(rolePrincipal))
        {
            securityMappingHandler.setUserPrincipalInRole(username, roleName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRoleFromUser(java.lang.String,
     *      java.lang.String)
     */
    public void removeRoleFromUser(String username, String roleName) throws SecurityException
    {
        Principal userPrincipal = atnProviderProxy.getUserPrincipal(username);
        if (null == userPrincipal)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(username));
        }
        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleName);
        if (null != rolePrincipal)
        {
            securityMappingHandler.removeUserPrincipalInRole(username, roleName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#isUserInRole(java.lang.String,
     *      java.lang.String)
     */
    public boolean isUserInRole(String username, String roleName) throws SecurityException
    {
        boolean isUserInRole = false;
        Set<RolePrincipal> rolePrincipals = securityMappingHandler.getRolePrincipals(username);
        Principal rolePrincipal = new RolePrincipalImpl(roleName);
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
    public void addRoleToGroup(String roleName, String groupName) throws SecurityException
    {
        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleName);
        if (null == rolePrincipal)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST.create(roleName));
        }
        securityMappingHandler.setRolePrincipalInGroup(groupName, roleName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRoleFromGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeRoleFromGroup(String roleName, String groupName) throws SecurityException
    {
        Principal rolePrincipal = roleSecurityHandler.getRolePrincipal(roleName);
        if (null != rolePrincipal)
        {
            securityMappingHandler.removeRolePrincipalInGroup(groupName, roleName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#isGroupInRole(java.lang.String,
     *      java.lang.String)
     */
    public boolean isGroupInRole(String groupName, String roleName) throws SecurityException
    {
        boolean isGroupInRole = false;
        Set<RolePrincipal> rolePrincipals = securityMappingHandler.getRolePrincipalsInGroup(groupName);
        Principal rolePrincipal = new RolePrincipalImpl(roleName);
        if (rolePrincipals.contains(rolePrincipal))
        {
            isGroupInRole = true;
        }
        return isGroupInRole;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRoles(java.lang.String)
     */
    public Collection<Role> getRoles(String filter) throws SecurityException
    {
        List<Role> roles = new LinkedList<Role>();
        Collection<RolePrincipal> rolePrincipals = roleSecurityHandler.getRolePrincipals(filter);
        for (RolePrincipal principal : rolePrincipals)
        {
            SecurityAttributes attributes = this.attributesProvider.retrieveAttributes(principal);
            Role role = new RoleImpl(principal, attributes);
            roles.add(role);
        }
        return roles;
    }

    /** 
     * @see org.apache.jetspeed.security.RoleManager#setRoleEnabled(java.lang.String, boolean)
     */
    public void setRoleEnabled(String roleName, boolean enabled) throws SecurityException
    {
        RolePrincipalImpl rolePrincipal = (RolePrincipalImpl)roleSecurityHandler.getRolePrincipal(roleName);
        if (null == rolePrincipal)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST.create(roleName));
        }
        if ( enabled != rolePrincipal.isEnabled() )
        {
            rolePrincipal.setEnabled(enabled);
            roleSecurityHandler.storeRolePrincipal(rolePrincipal);
        }
    }
}