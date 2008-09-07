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
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalAlreadyExistsException;
import org.apache.jetspeed.security.PrincipalAssociationNotAllowedException;
import org.apache.jetspeed.security.PrincipalAssociationRequiredException;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalReadOnlyException;
import org.apache.jetspeed.security.PrincipalUpdateException;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalPermissionStorageManager;
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
    
    public GroupManagerImpl(JetspeedPrincipalType principalType, 
                           JetspeedPrincipalAccessManager jpam, JetspeedPrincipalStorageManager jpsm,
                           JetspeedPrincipalPermissionStorageManager jppsm,
                           UserManager userManager, RoleManager roleManager)
    {
        super(principalType, jpam, jpsm, jppsm);
        this.userType = ((JetspeedPrincipalManager) userManager).getPrincipalType();
        this.roleType = ((JetspeedPrincipalManager) roleManager).getPrincipalType();
        this.userManager = userManager;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.GroupManager#newGroup(java.lang.String, boolean)
     */
    public Group newGroup(String name, boolean mapped)
    {
        GroupImpl group = new GroupImpl();
        group.setName(name);
        group.setMapped(mapped);
        return group;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.GroupManager#newTransientGroup(java.lang.String)
     */
    public Group newTransientGroup(String name)
    {
        TransientGroup group = new TransientGroup();
        group.setName(name);
        return group;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#addGroup(java.lang.String)
     */
    public void addGroup(String groupName) throws SecurityException
    {
        Group group = newGroup(groupName, true);
        
        try
        {
            super.addPrincipal(group, null);
        }
        catch (PrincipalAlreadyExistsException e)
        {
            throw new SecurityException(SecurityException.GROUP_ALREADY_EXISTS.create(groupName)); 
        }
        catch (PrincipalAssociationRequiredException e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create("GroupManager.addGroup", "add", e.getMessage()));
        } 
        catch (PrincipalAssociationNotAllowedException e)
        {
            throw new SecurityException(e);
        }
        
        if (log.isDebugEnabled())
            log.debug("Added group: " + groupName);
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
        catch (Exception e)
        {
            throw new SecurityException(e);
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
            throw new SecurityException(
                SecurityException.GROUP_DOES_NOT_EXIST.create(groupName)); 
        }

        return group;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsForUser(java.lang.String)
     */
    public List<Group> getGroupsForUser(String username)
            throws SecurityException
    {
        return (List<Group>) super.getAssociatedFrom(username, userType, JetspeedPrincipalAssociationType.IS_PART_OF);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsInRole(java.lang.String)
     */
    public List<Group> getGroupsInRole(String roleName)
            throws SecurityException
    {
        return (List<Group>) super.getAssociatedTo(roleName, roleType, JetspeedPrincipalAssociationType.IS_PART_OF);
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
            Group group = getGroup(groupName);
            super.addAssociation(JetspeedPrincipalAssociationType.IS_PART_OF, user, group);
        } 
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(e);
        } 
        catch (PrincipalAssociationNotAllowedException e)
        {
            throw new SecurityException(e);
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
            Group group = getGroup(groupName);
            super.removeAssociation(JetspeedPrincipalAssociationType.IS_PART_OF, user, group);
        } 
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(e);
        } 
        catch (PrincipalAssociationRequiredException e)
        {
            throw new SecurityException(e);
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
    
    /**
     * @see org.apache.jetspeed.security.GroupManager#setGroupEnabled(java.lang.String, boolean)
     */
    public void setGroupEnabled(String groupName, boolean enabled) throws SecurityException
    {
        Group group = (Group) super.getPrincipal(groupName);
        
        if (null == group)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST.create(groupName));
        }
        
        try
        {
            if (enabled != group.isEnabled())
            {
                group.setEnabled(enabled);
                super.updatePrincipal(group);
            }
            
            group.setEnabled(enabled);
        }
        catch (PrincipalReadOnlyException e)
        {
            throw new SecurityException(e);
        }
        catch (PrincipalUpdateException e)
        {
            throw new SecurityException(e);
        } 
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(e);
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