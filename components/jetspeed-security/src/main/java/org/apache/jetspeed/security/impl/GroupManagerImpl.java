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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.AuthenticationProviderProxy;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.attributes.SecurityAttributes;
import org.apache.jetspeed.security.attributes.SecurityAttributesProvider;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;

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
 */
public class GroupManagerImpl implements GroupManager
{

    /** The logger. */
    private static final Log log = LogFactory.getLog(GroupManagerImpl.class);

    /** The authentication provider proxy. */
    private AuthenticationProviderProxy atnProviderProxy = null;

    /** The group security handler. */
    private GroupSecurityHandler groupSecurityHandler = null;

    /** The security mapping handler. */
    private SecurityMappingHandler securityMappingHandler = null;

    private SecurityAttributesProvider attributesProvider;
    
    /**
     * @param securityProvider
     *            The security provider.
     */
    public GroupManagerImpl(SecurityProvider securityProvider, SecurityAttributesProvider attributesProvider)
    {
        this.atnProviderProxy = securityProvider.getAuthenticationProviderProxy();
        this.groupSecurityHandler = securityProvider.getGroupSecurityHandler();
        this.securityMappingHandler = securityProvider.getSecurityMappingHandler();
        this.attributesProvider = attributesProvider;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#addGroup(java.lang.String)
     */
    public void addGroup(String groupName) throws SecurityException
    {
        if (groupExists(groupName)) 
        {  
            throw new SecurityException(SecurityException.GROUP_ALREADY_EXISTS.create(groupName)); 
        }
        GroupPrincipal groupPrincipal = new GroupPrincipalImpl(groupName);        
        groupSecurityHandler.storeGroupPrincipal(groupPrincipal);
        SecurityAttributes sa = attributesProvider.createSecurityAttributes(groupPrincipal);
        attributesProvider.saveAttributes(sa);
        if (log.isDebugEnabled())
            log.debug("Added group: " + groupName);
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws SecurityException
    {
        if (securityMappingHandler.getHierarchyResolver() != null)
        {
            Set<GroupPrincipal> groups = securityMappingHandler.getHierarchyResolver().resolveGroups(groupName);
            for (GroupPrincipal gp : groups)
            {
                groupSecurityHandler.removeGroupPrincipal(gp);
//                TODO: should we use cascading deletes?
                attributesProvider.deleteAttributes(gp);
            }
        }
        else
        {
            GroupPrincipal gp = groupSecurityHandler.getGroupPrincipal(groupName);
            if (gp != null)
            {
                groupSecurityHandler.removeGroupPrincipal(new GroupPrincipalImpl(groupName));
//              TODO: should we use cascading deletes?
                attributesProvider.deleteAttributes(gp);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#groupExists(java.lang.String)
     */
    public boolean groupExists(String groupName)
    {
        Principal principal = groupSecurityHandler.getGroupPrincipal(groupName);
        boolean groupExists = (null != principal);
        return groupExists;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroup(java.lang.String)
     */
    public Group getGroup(String groupName) throws SecurityException
    {
        Principal groupPrincipal = groupSecurityHandler.getGroupPrincipal(groupName);
        if (null == groupPrincipal) 
        { 
            throw new SecurityException(
                SecurityException.GROUP_DOES_NOT_EXIST.create(groupName)); 
        }
        SecurityAttributes attributes = this.attributesProvider.retrieveAttributes(groupPrincipal);
        Group group = new GroupImpl(groupPrincipal, attributes);
        return group;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsForUser(java.lang.String)
     */
    public Collection<Group> getGroupsForUser(String userName)
            throws SecurityException
    {
        Collection<Group> groups = new ArrayList<Group>();
        Set<GroupPrincipal> groupPrincipals = securityMappingHandler.getGroupPrincipals(userName);
        for (GroupPrincipal groupPrincipal : groupPrincipals)
        {
            SecurityAttributes attributes = this.attributesProvider.retrieveAttributes(groupPrincipal);
            groups.add(new GroupImpl(groupPrincipal, attributes));
        }
        return groups;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroupsInRole(java.lang.String)
     */
    public Collection<Group> getGroupsInRole(String roleName)
            throws SecurityException
    {
        Collection<Group> groups = new ArrayList<Group>();
        Set<GroupPrincipal> groupPrincipals = securityMappingHandler.getGroupPrincipalsInRole(roleName);
        for (GroupPrincipal groupPrincipal : groupPrincipals)
        {
            SecurityAttributes attributes = this.attributesProvider.retrieveAttributes(groupPrincipal);
            groups.add(new GroupImpl(groupPrincipal, attributes));
        }
        return groups;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#addUserToGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addUserToGroup(String username, String groupName)
            throws SecurityException
    {
        GroupPrincipal groupPrincipal = groupSecurityHandler.getGroupPrincipal(groupName);
        if (null == groupPrincipal) 
        { 
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST.create(groupName)); 
        }
        Principal userPrincipal = atnProviderProxy.getUserPrincipal(username);
        if (null == userPrincipal) 
        { 
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(username));
        }
        Set<GroupPrincipal> groupPrincipals = securityMappingHandler.getGroupPrincipals(username);
        if (!groupPrincipals.contains(groupPrincipal))
        {
            securityMappingHandler.setUserPrincipalInGroup(username, groupName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#removeUserFromGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeUserFromGroup(String username, String groupName)
            throws SecurityException
    {
        Principal userPrincipal = atnProviderProxy.getUserPrincipal(username);
        if (null == userPrincipal) 
        { 
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(username));
        }
        Principal groupPrincipal = groupSecurityHandler.getGroupPrincipal(groupName);
        if (null != groupPrincipal)
        {
            securityMappingHandler.removeUserPrincipalInGroup(username, groupName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#isUserInGroup(java.lang.String,
     *      java.lang.String)
     */
    public boolean isUserInGroup(String username, String groupName)
            throws SecurityException
    {
        boolean isUserInGroup = false;
        Set<GroupPrincipal> groupPrincipals = securityMappingHandler.getGroupPrincipals(username);
        Principal groupPrincipal = new GroupPrincipalImpl(groupName);
        if (groupPrincipals.contains(groupPrincipal))
        {
            isUserInGroup = true;
        }
        return isUserInGroup;
    }

    /**
     * @see org.apache.jetspeed.security.GroupManager#getGroups(java.lang.String)
     */
    public Collection<Group> getGroups(String filter) throws SecurityException
    {
        List<Group> groups = new LinkedList<Group>();
        Collection<GroupPrincipal> groupPrincipals = groupSecurityHandler.getGroupPrincipals(filter);
        for (GroupPrincipal principal : groupPrincipals)
        {
            SecurityAttributes attributes = this.attributesProvider.retrieveAttributes(principal);
            Group group = new GroupImpl(principal, attributes);
            groups.add(group);
        }
        return groups;
    }
    
    /**
     * @see org.apache.jetspeed.security.GroupManager#setGroupEnabled(java.lang.String, boolean)
     */
    public void setGroupEnabled(String groupName, boolean enabled) throws SecurityException
    {
        GroupPrincipalImpl groupPrincipal = (GroupPrincipalImpl)groupSecurityHandler.getGroupPrincipal(groupName);
        if (null == groupPrincipal)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST.create(groupName));
        }
        if ( enabled != groupPrincipal.isEnabled() )
        {
            groupPrincipal.setEnabled(enabled);
            groupSecurityHandler.storeGroupPrincipal(groupPrincipal);
        }
    }
}