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
    private static final Logger log = LoggerFactory.getLogger(GroupManagerImpl.class);

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
    public void checkInitialized()
    {    	
    	if (userManager == null)
    	{
        	userManager = (UserManager)getPrincipalManagerProvider().getManager(userType);
    	}
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
        
        super.addPrincipal(group, null);
            
        if (log.isDebugEnabled())
            log.debug("Added group: " + groupName);
        
        return group;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws SecurityException
    {
       super.removePrincipal(groupName);
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
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP, groupName));
        }

        return group;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsForUser(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Group> getGroupsForUser(String username)
            throws SecurityException
    {
        return (List<Group>) super.getAssociatedFrom(username, userType, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsInRole(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Group> getGroupsInRole(String roleName)
            throws SecurityException
    {
        return (List<Group>) super.getAssociatedTo(roleName, roleType, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#addUserToGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addUserToGroup(String username, String groupName)
            throws SecurityException
    {
       	checkInitialized();
       	User user = userManager.getUser(username);
        if (user == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, username));
        }
        Group group = getGroup(groupName);
        if (group == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP, groupName));
        }
        super.addAssociation(user, group, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#removeUserFromGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeUserFromGroup(String username, String groupName)
            throws SecurityException
    {
    	checkInitialized();
        User user = userManager.getUser(username);
        if (user == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, username));
        }
        Group group = getGroup(groupName);
        if (group == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.GROUP, groupName));
        }
        super.removeAssociation(user, group, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#isUserInGroup(java.lang.String,
     *      java.lang.String)
     */
    public boolean isUserInGroup(String username, String groupName)
            throws SecurityException
    {
        return getAssociatedNamesFrom(username, userType, JetspeedPrincipalAssociationType.IS_MEMBER_OF).contains(groupName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroups(java.lang.String)
     */
    @SuppressWarnings("unchecked")
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
        super.updatePrincipal(group);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.GroupManager#addGroupToGroup(org.apache.jetspeed.security.Group, org.apache.jetspeed.security.Group, java.lang.String)
     */
    public void addGroupToGroup(Group from, Group to, String associationName) throws SecurityException
    {
        this.addAssociation(from, to, associationName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.GroupManager#removeGroupFromGroup(org.apache.jetspeed.security.Group, org.apache.jetspeed.security.Group, java.lang.String)
     */
    public void removeGroupFromGroup(Group from, Group to, String associationName) throws SecurityException
    {
        removeAssociation(from, to, associationName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.GroupManager#getGroupsAssociatedFrom(org.apache.jetspeed.security.Group, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Group> getGroupsAssociatedFrom(Group from, String associationName)
    {
        return (List<Group>)getAssociatedFrom(from.getName(), from.getType(), associationName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.GroupManager#getGroupsAssociatedTo(org.apache.jetspeed.security.Group, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Group> getGroupsAssociatedTo(Group to, String associationName)
    {
        return (List<Group>)getAssociatedTo(to.getName(), to.getType(), associationName);
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