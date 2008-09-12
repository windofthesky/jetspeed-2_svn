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
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;

/**
 * <p>
 * Implements the service interface for managing Jetsped Security Groups.
 * </p>
 * <p>
 * Group hierarchy elements are being returned as a {@link Group}collection.
 * The backing implementation must appropriately map the group hierarchy to a
 * preferences sub-tree.
 * </p>
 * <p>
 * The convention {principal}.{subprincipal} has been chosen to name groups hierarchies. 
 * </p>
 * <p>Modified 2008-08-05 - DST - decoupled java preferences</p> 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class GroupManagerImpl extends BaseJetspeedPrincipalManager implements GroupManager
{

    /** The logger. */
    private static final Log log = LogFactory.getLog(GroupManagerImpl.class);

    private JetspeedPrincipalType userType;
    private JetspeedPrincipalType roleType;
    private UserManager userManager;
    
    public GroupManagerImpl(JetspeedPrincipalType principalType,JetspeedPrincipalType userType,JetspeedPrincipalType roleType, 
                           JetspeedPrincipalAccessManager jpam, JetspeedPrincipalStorageManager jpsm)
    {
        super(principalType, jpam, jpsm);
        this.userType = userType;
        this.roleType = roleType;
    }
    
    public void setUserManager(UserManager manager)
    {
    	this.userManager = manager;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.GroupManager#newGroup(java.lang.String, boolean)
     */
    public Group newGroup(String name, boolean mapped)
    {
        GroupImpl group = new GroupImpl(name);
        group.setMapped(mapped);
        return group;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.GroupManager#newTransientGroup(java.lang.String)
     */
    public Group newTransientGroup(String name)
    {
        TransientGroup group = new TransientGroup(name);
        return group;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#addGroup(java.lang.String)
     */
    public Group addGroup(String groupName) throws SecurityException
    {
        return addGroup(groupName, true);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#addGroup(java.lang.String, boolean)
     */
    public Group addGroup(String groupName, boolean mapped) throws SecurityException
    {
        Group group = newGroup(groupName, mapped);
        
        try
        {
            super.addPrincipal(group, null);
        }
        catch (PrincipalAlreadyExistsException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ALREADY_EXISTS.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName)); 
        }
        catch (PrincipalAssociationRequiredException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_REQUIRED.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        } 
        catch (PrincipalAssociationNotAllowedException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_NOT_ALLOWED.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
        catch (PrincipalAssociationUnsupportedException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_UNSUPPORTED.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
        
        if (log.isDebugEnabled())
            log.debug("Added group: " + groupName);
        
        return group;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws SecurityException
    {
        try
        {
            super.removePrincipal(groupName);
        }
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
        catch (PrincipalNotRemovableException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_NOT_REMOVABLE.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
        catch (DependentPrincipalException e)
        {
            throw new SecurityException(SecurityException.DEPENDENT_PRINCIPAL_EXISTS.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#groupExists(java.lang.String)
     */
    public boolean groupExists(String groupName)
    {
        return super.principalExists(groupName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroup(java.lang.String)
     */
    public Group getGroup(String groupName) throws SecurityException
    {
        Group group = (Group) super.getPrincipal(groupName);
        
        if (null == group) 
        { 
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }

        return group;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsForUser(java.lang.String)
     */
    public List<Group> getGroupsForUser(String username)
            throws SecurityException
    {
        return (List<Group>) super.getAssociatedFrom(username, userType, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsInRole(java.lang.String)
     */
    public List<Group> getGroupsInRole(String roleName)
            throws SecurityException
    {
        return (List<Group>) super.getAssociatedTo(roleName, roleType, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#addUserToGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addUserToGroup(String username, String groupName)
            throws SecurityException
    {
        try
        {
            User user = userManager.getUser(username);
            if (user == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER_TYPE_NAME, username));
            }
            Group group = getGroup(groupName);
            if (group == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
            }
            super.addAssociation(user, group, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
        } 
        catch (PrincipalNotFoundException e)
        {
            // TODO: determine *which* principal does not exist to provide the correct error message...
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST);
        } 
        catch (PrincipalAssociationNotAllowedException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_NOT_ALLOWED.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
        catch (PrincipalAssociationUnsupportedException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_UNSUPPORTED.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#removeUserFromGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeUserFromGroup(String username, String groupName)
            throws SecurityException
    {
        try
        {
            User user = userManager.getUser(username);
            if (user == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER_TYPE_NAME, username));
            }
            Group group = getGroup(groupName);
            if (group == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
            }
            super.removeAssociation(user, group, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
        } 
        catch (PrincipalAssociationRequiredException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_REQUIRED.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, groupName));
        }
        catch (PrincipalNotFoundException e)
        {
            // TODO: determine *which* principal does not exist to provide the correct error message...
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST);
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#isUserInGroup(java.lang.String,
     *      java.lang.String)
     */
    public boolean isUserInGroup(String username, String groupName)
            throws SecurityException
    {
        return getGroupsForUser(username).contains(getGroup(groupName));
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroups(java.lang.String)
     */
    public List<Group> getGroups(String nameFilter) throws SecurityException
    {
        return (List<Group>) super.getPrincipals(nameFilter);
    }
    
    public List<String> getGroupNames(String nameFilter) throws SecurityException
    {
        return getPrincipalNames(nameFilter);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#updateGroup(org.apache.jetspeed.security.Group)
     */
    public void updateGroup(Group group) throws SecurityException
    {
        try
        {
            super.updatePrincipal(group);
        }
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, group.getName()));
        }
        catch (PrincipalUpdateException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_UPDATE_FAILURE.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, group.getName()), e);
        }
        catch (PrincipalReadOnlyException e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_IS_READ_ONLY.createScoped(JetspeedPrincipalType.GROUP_TYPE_NAME, group.getName()));
        }
    }

    public JetspeedPrincipal newPrincipal(String name, boolean mapped)
    {
        return newGroup(name, mapped);
    }

    public JetspeedPrincipal newTransientPrincipal(String name)
    {
        return newTransientGroup(name);
    }
}