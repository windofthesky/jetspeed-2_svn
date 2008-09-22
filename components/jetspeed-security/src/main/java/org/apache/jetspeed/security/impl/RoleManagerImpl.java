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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.DependentPrincipalException;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalAlreadyExistsException;
import org.apache.jetspeed.security.PrincipalAssociationNotAllowedException;
import org.apache.jetspeed.security.PrincipalAssociationRequiredException;
import org.apache.jetspeed.security.PrincipalAssociationUnsupportedException;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.PrincipalReadOnlyException;
import org.apache.jetspeed.security.PrincipalUpdateException;
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
    private static final Log log = LogFactory.getLog(RoleManagerImpl.class);
    
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
        
        try
        {
            super.addPrincipal(role, null);
        }
        catch (PrincipalAlreadyExistsException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ALREADY_EXISTS.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        catch (PrincipalAssociationRequiredException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_REQUIRED.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        } 
        catch (PrincipalAssociationNotAllowedException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_NOT_ALLOWED.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        catch (PrincipalAssociationUnsupportedException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_UNSUPPORTED.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        
        if (log.isDebugEnabled())
            log.debug("Added role: " + roleName);
        
        return role;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#removeRole(java.lang.String)
     */
    public void removeRole(String roleName) throws SecurityException
    {
        try
        {
            super.removePrincipal(roleName);
        }
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        catch (PrincipalNotRemovableException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_NOT_REMOVABLE.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        catch (DependentPrincipalException e)
        {
            throw new SecurityException(SecurityException.DEPENDENT_PRINCIPAL_EXISTS.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
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
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName)); 
        }

        return role;
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesForUser(java.lang.String)
     */
    public List<Role> getRolesForUser(String username) throws SecurityException
    {        
        return (List<Role>)super.getAssociatedFrom(username, userType, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRolesInGroup(java.lang.String)
     */
    public List<Role> getRolesInGroup(String groupName) throws SecurityException
    {
        return (List<Role>)super.getAssociatedFrom(groupName, groupType, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
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
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER_TYPE_NAME, username));
        }
        Role role = getRole(roleName);
        if (role == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        super.addAssociation(user, role, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
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
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER_TYPE_NAME, username));
        }
        Role role = getRole(roleName);
        if (role == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        super.removeAssociation(user, role, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#isUserInRole(java.lang.String,
     *      java.lang.String)
     */
    public boolean isUserInRole(String username, String roleName) throws SecurityException
    {
        return getRolesForUser(username).contains(getRole(roleName));
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
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
        Role role = getRole(roleName);
        if (role == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        super.addAssociation(group, role, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
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
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
        Role role = getRole(roleName);
        if (role == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, roleName));
        }
        super.removeAssociation(group, role, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#isGroupInRole(java.lang.String,
     *      java.lang.String)
     */
    public boolean isGroupInRole(String groupName, String roleName) throws SecurityException
    {
        return getRolesInGroup(groupName).contains(getRole(roleName));
    }

    /**
     * @see org.apache.jetspeed.security.RoleManager#getRoles(java.lang.String)
     */
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
        try
        {
            super.updatePrincipal(role);
        }
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, role.getName()));
        }
        catch (PrincipalUpdateException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_UPDATE_FAILURE.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, role.getName()), e);
        } 
        catch (PrincipalReadOnlyException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_IS_READ_ONLY.createScoped(JetspeedPrincipalType.ROLE_TYPE_NAME, role.getName()));
        }
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