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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.InternalRolePrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalGroupPrincipalImpl;
import org.apache.jetspeed.security.om.impl.InternalRolePrincipalImpl;
import org.apache.jetspeed.security.om.impl.InternalUserPrincipalImpl;

/**
 * <p>Base class for the security services.</p> 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 * @version $Id$
 */
public class BaseSecurityImpl
{

    PersistenceStore persistenceStore;
    
    HierarchyResolver roleHierarchyResolver=new GeneralizationHierarchyResolver();
    HierarchyResolver groupHierarchyResolver=new GeneralizationHierarchyResolver();

    /**
     * <p>Constructor providing access to the persistence component.</p>
     */
    public BaseSecurityImpl(PersistenceStore persistenceStore)
    {
        if (persistenceStore == null)
        {
            throw new IllegalArgumentException("persistenceStore cannot be null for BaseSecurityImpl");
        }
        
       this.persistenceStore = persistenceStore;
    }
    
    /**
     * <p>Constructor providing access to the persistence component and role/group hierarchy resolvers</p>
     */
    public BaseSecurityImpl(PersistenceStore persistenceStore, HierarchyResolver roleHierarchyResolver,HierarchyResolver groupHierarchyResolver)
    {
        this(persistenceStore);
        this.roleHierarchyResolver=roleHierarchyResolver;
        this.groupHierarchyResolver=groupHierarchyResolver;
    }

    /**
     * <p>Returns the {@link InternalGroupPrincipal} from the group full path name.</p>
     * @param groupFullPathName The group full path name.
     * @return The {@link InternalGroupPrincipal}.
     */
    InternalGroupPrincipal getJetspeedGroupPrincipal(String groupFullPathName)
    {
        GroupPrincipal groupPrincipal = new GroupPrincipalImpl(groupFullPathName);
        String fullPath = groupPrincipal.getFullPath();
        // Get group.
        PersistenceStore store = getPersistenceStore();       
        Filter filter = store.newFilter();
        filter.addEqualTo("fullPath", fullPath);
        Object query = store.newQuery(InternalGroupPrincipalImpl.class, filter);
        InternalGroupPrincipal omGroup = (InternalGroupPrincipal) store.getObjectByQuery(query);
        return omGroup;
    }

    /**
     * <p>Returns the {@link InternalUserPrincipal} from the user name.</p>
     * @param username The user name.
     * @return The {@link InternalUserPrincipal}.
     */
    InternalUserPrincipal getJetspeedUserPrincipal(String username)
    {
        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        // Get user.
        PersistenceStore store = getPersistenceStore();       
        Filter filter = store.newFilter();
        filter.addEqualTo("fullPath", fullPath);
        Object query = store.newQuery(InternalUserPrincipalImpl.class, filter);
        InternalUserPrincipal omUser = (InternalUserPrincipal) store.getObjectByQuery(query);
        return omUser;
    }

    /**
     * <p>Returns the {@link InternalRolePrincipal} from the role full path name.</p>
     * @param username The role full path name.
     * @return The {@link InternalRolePrincipal}.
     */
    InternalRolePrincipal getJetspeedRolePrincipal(String roleFullPathName)
    {
        RolePrincipal rolePrincipal = new RolePrincipalImpl(roleFullPathName);
        String fullPath = rolePrincipal.getFullPath();
        // Remove security role
        PersistenceStore store = getPersistenceStore();       
        Filter filter = store.newFilter();
        filter.addEqualTo("fullPath", fullPath);
        Object query = store.newQuery(InternalRolePrincipalImpl.class, filter);
        InternalRolePrincipal omRole = (InternalRolePrincipal) store.getObjectByQuery(query);
        return omRole;
    }

    /**
     * <p>Returns a {@link User} object provided a {@link InternalUserPrincipal}
     * @param omUser The {@link InternalUserPrincipal}
     * @return The {@link User}
     */
    User getUser(InternalUserPrincipal omUser)
    {
        UserPrincipal userPrincipal = new UserPrincipalImpl(UserPrincipalImpl.getPrincipalNameFromFullPath(omUser.getFullPath()));
        String fullPath = userPrincipal.getFullPath();
        Set principals = new HashSet();
        // TODO For now, we do not add the credentials to the Subject.
        Set publicCredentials = new HashSet();
        Set privateCredentials = new HashSet();
        principals.add(userPrincipal);
        Collection roles = omUser.getRolePrincipals();
        if ((null != roles) && (roles.size() > 0))
        {
            principals.addAll(getRolePrincipals(roles));
        }
        Collection groups = omUser.getGroupPrincipals();
        if ((null != groups) && (groups.size() > 0))
        {
            principals.addAll(getGroupPrincipals(groups));
        }
        Subject subject = new Subject(true, principals, publicCredentials, privateCredentials);
        Preferences preferences = Preferences.userRoot().node(fullPath);
        User user = new UserImpl(subject, preferences);
        return user;
    }

    /**
     * <p>Returns a collection of {@link User} from a collection of
     * {@link InternalUserPrincipal}.</p>
     * @param omUsers The collection of {@link InternalUserPrincipal}.
     * @return The collection of {@link User}.
     */
    Collection getUsers(Collection omUsers)
    {
        Collection users = new ArrayList();
        if ((null != omUsers) && (omUsers.size() > 0))
        {
            Iterator omUsersIterator = omUsers.iterator();
            while (omUsersIterator.hasNext())
            {
                InternalUserPrincipal omUser = (InternalUserPrincipal) omUsersIterator.next();
                users.add(getUser(omUser));
            }
        }
        return users;
    }

    /**
     * <p>Returns a {@link Role} object provided a {@link InternalRolePrincipal}.</p>
     * @param omRole The {@link InternalRolePrincipal}
     * @return The {@link Role}
     */
    Role getRole(InternalRolePrincipal omRole)
    {
        RolePrincipal rolePrincipal = new RolePrincipalImpl(RolePrincipalImpl.getPrincipalNameFromFullPath(omRole.getFullPath()));
        Preferences preferences = Preferences.userRoot().node(omRole.getFullPath());
        Role role = new RoleImpl(rolePrincipal, preferences);
        return role;
    }

    /**
     * <p>Returns a collection of {@link Role} from a collection of
     * {@link InternalRolePrincipal}.</p>
     * @param omRoles The collection of {@link InternalRolePrincipal}.
     * @return The collection of {@link Role}.
     */
    Collection getRoles(Collection omRoles)
    {
        Collection roles = new ArrayList();
        if ((null != omRoles) && (omRoles.size() > 0))
        {
            Iterator omRolesIterator = omRoles.iterator();
            while (omRolesIterator.hasNext())
            {
                InternalRolePrincipal omRole = (InternalRolePrincipal) omRolesIterator.next();
                roles.add(getRole(omRole));
            }
        }
        return roles;
    }

    /**
     * <p>Given a role full path, removes the matching role from a given
     * {@InternalRolePrincipal} collection.</p>
     * @param omRoles The collection of {@InternalRolePrincipal}.
     * @param roleFullPathName The full path of the role to remove.
     * @return The new collection of {@InternalRolePrincipal}.
     */
    Collection removeRole(Collection omRoles, String roleFullPathName)
    {
        ArrayList newOmRoles = new ArrayList();
        Iterator omRolesIterator = omRoles.iterator();
        while (omRolesIterator.hasNext())
        {
            InternalRolePrincipal omRole = (InternalRolePrincipal) omRolesIterator.next();
            if (!(omRole.getFullPath().equals(RolePrincipalImpl.getFullPathFromPrincipalName(roleFullPathName))))
            {
                newOmRoles.add(omRole);
            }
        }
        return newOmRoles;
    }

    /**
     * <p>Returns a {@link Group} object provided a {@link InternalGroupPrincipal}
     * @param omGroup The {@link InternalGroupPrincipal}
     * @return The {@link Group}
     */
    Group getGroup(InternalGroupPrincipal omGroup)
    {
        GroupPrincipal groupPrincipal =
            new GroupPrincipalImpl(GroupPrincipalImpl.getPrincipalNameFromFullPath(omGroup.getFullPath()));
        Preferences preferences = Preferences.userRoot().node(omGroup.getFullPath());
        Group group = new GroupImpl(groupPrincipal, preferences);
        return group;
    }

    /**
     * <p>Returns a collection of {@link Group} from a collection of
     * {@link InternalGroupPrincipal}.</p>
     * @param omGroups The collection of {@link Group}.
     * @return The collection of {@link InternalGroupPrincipal}.
     */
    Collection getGroups(Collection omGroups)
    {
        Collection groups = new ArrayList();
        if ((null != omGroups) && (omGroups.size() > 0))
        {
            Iterator omGroupsIterator = omGroups.iterator();
            while (omGroupsIterator.hasNext())
            {
                InternalGroupPrincipal omGroup = (InternalGroupPrincipal) omGroupsIterator.next();
                groups.add(getGroup(omGroup));
            }
        }
        return groups;
    }

    /**
     * <p>Given a group full path, removes the matching group from a given
     * {@InternalGroupPrincipal} collection.</p>
     * @param omGroups The collection of {@InternalGroupPrincipal}.
     * @param groupFullPathName The full path of the group to remove.
     * @return The new collection of {@InternalGroupPrincipal}.
     */
    Collection removeGroup(Collection omGroups, String groupFullPathName)
    {
        ArrayList newOmGroups = new ArrayList();
        Iterator omGroupsIterator = omGroups.iterator();
        while (omGroupsIterator.hasNext())
        {
            InternalGroupPrincipal omGroup = (InternalGroupPrincipal) omGroupsIterator.next();
            if (!(omGroup.getFullPath().equals(GroupPrincipalImpl.getFullPathFromPrincipalName(groupFullPathName))))
            {
                newOmGroups.add(omGroup);
            }
        }
        return newOmGroups;
    }

    /**
     * <p>Utility method to get a set of {@link RolePrincipal} from 
     * a collection of {@link InternalRolePrincipal} object model.</p>
     * @param omRoles The roles.
     * @return The role principals.
     */
    Set getRolePrincipals(Collection omRoles)
    {
        Set rolePrincipals = new HashSet();

        if (null != omRoles)
        {
            Iterator omRolesIter = omRoles.iterator();
            while (omRolesIter.hasNext())
            {
                InternalRolePrincipal omRole = (InternalRolePrincipal) omRolesIter.next();
                Preferences preferences = Preferences.userRoot().node(omRole.getFullPath());
                String [] fullPaths=roleHierarchyResolver.resolve(preferences);
                for (int i = 0; i < fullPaths.length; i++)
                {
                    rolePrincipals.add(new RolePrincipalImpl(RolePrincipalImpl.getPrincipalNameFromFullPath(fullPaths[i])));    
                }
            }
        }
        return rolePrincipals;
    }

    /**
     * <p>Utility method to get a set of {@link GroupPrincipal} from 
     * a collection of {@link InternalGroupPrincipal} object model.</p>
     * @param groups The groups.
     * @return The group principals.
     */
    Set getGroupPrincipals(Collection omGroups)
    {
        Set groupPrincipals = new HashSet();

        if (null != omGroups)
        {
            Iterator omGroupsIter = omGroups.iterator();
            while (omGroupsIter.hasNext())
            {
                InternalGroupPrincipal omGroup = (InternalGroupPrincipal) omGroupsIter.next();
                
                Preferences preferences = Preferences.userRoot().node(omGroup.getFullPath());
                String [] fullPaths=groupHierarchyResolver.resolve(preferences);
                for (int i = 0; i < fullPaths.length; i++)
                {
                    groupPrincipals.add(new GroupPrincipalImpl(GroupPrincipalImpl.getPrincipalNameFromFullPath(fullPaths[i])));   
                }
            }
        }
        return groupPrincipals;
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
