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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;

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
 * @version $Id$
 */
public class RoleManagerImpl extends BaseJetspeedPrincipalManager implements RoleManager
{
    /** The logger. */
    private static final Logger log = LoggerFactory.getLogger(RoleManagerImpl.class);
    
    private JetspeedPrincipalType userType;
    private JetspeedPrincipalType groupType;
    private UserManager userManager;
    private GroupManager groupManager;
    
    public RoleManagerImpl(JetspeedPrincipalType principalType,JetspeedPrincipalType userType,JetspeedPrincipalType groupType,
                           JetspeedPrincipalAccessManager jpam, JetspeedPrincipalStorageManager jpsm)
    {
        super(principalType, jpam, jpsm);
        this.userType = userType;
        this.groupType = groupType;
    }
    public void checkInitialized()
    {    	
    	if (userManager == null)
    	{
    		userManager = (UserManager)getPrincipalManagerProvider().getManager(userType);
    	}
    	if (groupManager == null)
    	{
    		groupManager = (GroupManager)getPrincipalManagerProvider().getManager(groupType);
    	}
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.RoleManager#newRole(java.lang.String, boolean)
     */
    public Role newRole(String name, boolean mapped)
    {
        RoleImpl role = new RoleImpl(name);
        role.setMapped(mapped);
        return role;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.RoleManager#newTransientRole(java.lang.String)
     */
    public Role newTransientRole(String name)
    {
        TransientRole role = new TransientRole(name);
        return role;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRole(java.lang.String)
     */
    public Role addRole(String roleName) throws SecurityException
    {
        return addRole(roleName, true);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRole(java.lang.String, boolean)
     */
    public Role addRole(String roleName, boolean mapped) throws SecurityException
    {
        Role role = newRole(roleName, mapped);

        super.addPrincipal(role, null);        
        
        if (log.isDebugEnabled())
            log.debug("Added role: " + roleName);
        
        return role;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRole(java.lang.String)
     */
    public void removeRole(String roleName) throws SecurityException
    {
        super.removePrincipal(roleName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#roleExists(java.lang.String)
     */
    public boolean roleExists(String roleName)
    {
        return super.principalExists(roleName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRole(java.lang.String)
     */
    public Role getRole(String roleName) throws SecurityException
    {
        Role role = (Role) super.getPrincipal(roleName);
        
        if (null == role) 
        { 
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE, roleName)); 
        }

        return role;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesForUser(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Role> getRolesForUser(String username) throws SecurityException
    {        
        return (List<Role>)super.getAssociatedFrom(username, userType, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesInGroup(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Role> getRolesInGroup(String groupName) throws SecurityException
    {
        return (List<Role>)super.getAssociatedFrom(groupName, groupType, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRoleToUser(java.lang.String,
     *      java.lang.String)
     */
    public void addRoleToUser(String username, String roleName) throws SecurityException
    {
        checkInitialized();
    	User user = userManager.getUser(username);
        if (user == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, username));
        }
        Role role = getRole(roleName);
        if (role == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE, roleName));
        }
        super.addAssociation(user, role, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRoleFromUser(java.lang.String,
     *      java.lang.String)
     */
    public void removeRoleFromUser(String username, String roleName) throws SecurityException
    {
    	checkInitialized();
    	User user = userManager.getUser(username);
        if (user == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, username));
        }
        Role role = getRole(roleName);
        if (role == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE, roleName));
        }
        super.removeAssociation(user, role, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#isUserInRole(java.lang.String,
     *      java.lang.String)
     */
    public boolean isUserInRole(String username, String roleName) throws SecurityException
    {
        return getAssociatedNamesFrom(username, userType, JetspeedPrincipalAssociationType.IS_MEMBER_OF).contains(roleName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#addRoleToGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addRoleToGroup(String roleName, String groupName) throws SecurityException
    {
        checkInitialized();
    	Group group = groupManager.getGroup(groupName);
        if (group == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP, groupName));
        }
        Role role = getRole(roleName);
        if (role == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE, roleName));
        }
        super.addAssociation(group, role, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRoleFromGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeRoleFromGroup(String roleName, String groupName) throws SecurityException
    {
    	checkInitialized();
    	Group group = groupManager.getGroup(groupName);
        if (group == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP, groupName));
        }
        Role role = getRole(roleName);
        if (role == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE, roleName));
        }
        super.removeAssociation(group, role, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#isGroupInRole(java.lang.String,
     *      java.lang.String)
     */
    public boolean isGroupInRole(String groupName, String roleName) throws SecurityException
    {
        return getAssociatedNamesFrom(groupName, groupType, JetspeedPrincipalAssociationType.IS_MEMBER_OF).contains(roleName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRoles(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Role> getRoles(String nameFilter) throws SecurityException
    {
        return (List<Role>)super.getPrincipals(nameFilter);
    }

    public List<String> getRoleNames(String nameFilter) throws SecurityException
    {
        return getPrincipalNames(nameFilter);
    }

    /** 
     * @see org.apache.jetspeed.security.RoleManager#updateRole(org.apache.jetspeed.security.Role)
     */
    public void updateRole(Role role) throws SecurityException
    {
         super.updatePrincipal(role);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.RoleManager#addRoleToRole(org.apache.jetspeed.security.Role, org.apache.jetspeed.security.Role, java.lang.String)
     */
    public void addRoleToRole(Role from, Role to, String associationName) throws SecurityException
    {
        this.addAssociation(from, to, associationName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.RoleManager#removeRoleFromRole(org.apache.jetspeed.security.Role, org.apache.jetspeed.security.Role, java.lang.String)
     */
    public void removeRoleFromRole(Role from, Role to, String associationName) throws SecurityException
    {
        removeAssociation(from, to, associationName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.RoleManager#getRolesAssociatedFrom(org.apache.jetspeed.security.Role, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Role> getRolesAssociatedFrom(Role from, String associationName)
    {
        return (List<Role>)getAssociatedFrom(from.getName(), from.getType(), associationName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.RoleManager#getRolesAssociatedTo(org.apache.jetspeed.security.Role, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Role> getRolesAssociatedTo(Role to, String associationName)
    {
        return (List<Role>)getAssociatedTo(to.getName(), to.getType(), associationName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipalManager#newPrincipal(java.lang.String, boolean)
     */
    public JetspeedPrincipal newPrincipal(String name, boolean mapped)
    {
        return newRole(name, mapped);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipalManager#newTransientPrincipal(java.lang.String)
     */
    public JetspeedPrincipal newTransientPrincipal(String name)
    {
        return newTransientRole(name);
    }
}