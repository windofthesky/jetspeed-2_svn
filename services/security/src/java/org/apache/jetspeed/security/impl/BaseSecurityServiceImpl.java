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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.om.JetspeedGroupPrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedGroupPrincipalImpl;
import org.apache.jetspeed.security.om.JetspeedRolePrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedRolePrincipalImpl;
import org.apache.jetspeed.security.om.JetspeedUserPrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedUserPrincipalImpl;

/**
 * <p>Base class for the security services.</p> 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class BaseSecurityServiceImpl extends BaseCommonService
{

    /** <p>The persistence plugin.</p> */
    PersistencePlugin plugin;

    /**
     * <p>Default Constructor.</p>
     */
    public BaseSecurityServiceImpl()
    {
    }

    /**
     * <p>Implementation is left to the derived class which must
     * set the plugin.</p>
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws CPSInitializationException
    {
    }

    /**
     * <p>Returns the {@link JetspeedGroupPrincipal} from the group full path name.</p>
     * @param groupFullPathName The group full path name.
     * @return The {@link JetspeedGroupPrincipal}.
     */
    JetspeedGroupPrincipal getJetspeedGroupPrincipal(String groupFullPathName)
    {
        GroupPrincipal groupPrincipal = new GroupPrincipalImpl(groupFullPathName);
        String fullPath = groupPrincipal.getFullPath();
        // Get group.
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("fullPath", fullPath);
        Object query = plugin.generateQuery(JetspeedGroupPrincipalImpl.class, c);
        JetspeedGroupPrincipal omGroup = (JetspeedGroupPrincipal) plugin.getObjectByQuery(JetspeedGroupPrincipalImpl.class, query);
        return omGroup;
    }

    /**
     * <p>Returns the {@link JetspeedUserPrincipal} from the user name.</p>
     * @param username The user name.
     * @return The {@link JetspeedUserPrincipal}.
     */
    JetspeedUserPrincipal getJetspeedUserPrincipal(String username)
    {
        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        // Get user.
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("fullPath", fullPath);
        Object query = plugin.generateQuery(JetspeedUserPrincipalImpl.class, c);
        JetspeedUserPrincipal omUser = (JetspeedUserPrincipal) plugin.getObjectByQuery(JetspeedUserPrincipalImpl.class, query);
        return omUser;
    }

    /**
     * <p>Returns the {@link JetspeedRolePrincipal} from the role full path name.</p>
     * @param username The role full path name.
     * @return The {@link JetspeedRolePrincipal}.
     */
    JetspeedRolePrincipal getJetspeedRolePrincipal(String roleFullPathName)
    {
        RolePrincipal rolePrincipal = new RolePrincipalImpl(roleFullPathName);
        String fullPath = rolePrincipal.getFullPath();

        // Remove security role
        LookupCriteria c1 = plugin.newLookupCriteria();
        c1.addEqualTo("fullPath", fullPath);
        Object query1 = plugin.generateQuery(JetspeedRolePrincipalImpl.class, c1);
        JetspeedRolePrincipal omRole = (JetspeedRolePrincipal) plugin.getObjectByQuery(JetspeedRolePrincipalImpl.class, query1);
        return omRole;
    }

    /**
     * <p>Returns a {@link User} object provided a {@link JetspeedUserPrincipal}
     * @param omUser The {@link JetspeedUserPrincipal}
     * @return The {@link User}
     */
    User getUser(JetspeedUserPrincipal omUser)
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
     * {@link JetspeedUserPrincipal}.</p>
     * @param omUsers The collection of {@link JetspeedUserPrincipal}.
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
                JetspeedUserPrincipal omUser = (JetspeedUserPrincipal) omUsersIterator.next();
                users.add(getUser(omUser));
            }
        }
        return users;
    }

    /**
     * <p>Returns a {@link Role} object provided a {@link JetspeedRolePrincipal}.</p>
     * @param omRole The {@link JetspeedRolePrincipal}
     * @return The {@link Role}
     */
    Role getRole(JetspeedRolePrincipal omRole)
    {
        RolePrincipal rolePrincipal = new RolePrincipalImpl(RolePrincipalImpl.getPrincipalNameFromFullPath(omRole.getFullPath()));
        Preferences preferences = Preferences.userRoot().node(omRole.getFullPath());
        Role role = new RoleImpl(rolePrincipal, preferences);
        return role;
    }

    /**
     * <p>Returns a collection of {@link Role} from a collection of
     * {@link JetspeedRolePrincipal}.</p>
     * @param omRoles The collection of {@link JetspeedRolePrincipal}.
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
                JetspeedRolePrincipal omRole = (JetspeedRolePrincipal) omRolesIterator.next();
                roles.add(getRole(omRole));
            }
        }
        return roles;
    }

    /**
     * <p>Given a role full path, removes the matching role from a given
     * {@JetspeedRolePrincipal} collection.</p>
     * @param omRoles The collection of {@JetspeedRolePrincipal}.
     * @param roleFullPathName The full path of the role to remove.
     * @return The new collection of {@JetspeedRolePrincipal}.
     */
    Collection removeRole(Collection omRoles, String roleFullPathName)
    {
        ArrayList newOmRoles = new ArrayList();
        Iterator omRolesIterator = omRoles.iterator();
        while (omRolesIterator.hasNext())
        {
            JetspeedRolePrincipal omRole = (JetspeedRolePrincipal) omRolesIterator.next();
            if (!(omRole.getFullPath().equals(RolePrincipalImpl.getFullPathFromPrincipalName(roleFullPathName))))
            {
                newOmRoles.add(omRole);
            }
        }
        return newOmRoles;
    }

    /**
     * <p>Returns a {@link Group} object provided a {@link JetspeedGroupPrincipal}
     * @param omGroup The {@link JetspeedGroupPrincipal}
     * @return The {@link Group}
     */
    Group getGroup(JetspeedGroupPrincipal omGroup)
    {
        GroupPrincipal groupPrincipal =
            new GroupPrincipalImpl(GroupPrincipalImpl.getPrincipalNameFromFullPath(omGroup.getFullPath()));
        Preferences preferences = Preferences.userRoot().node(omGroup.getFullPath());
        Group group = new GroupImpl(groupPrincipal, preferences);
        return group;
    }

    /**
     * <p>Returns a collection of {@link Group} from a collection of
     * {@link JetspeedGroupPrincipal}.</p>
     * @param omGroups The collection of {@link Group}.
     * @return The collection of {@link JetspeedGroupPrincipal}.
     */
    Collection getGroups(Collection omGroups)
    {
        Collection groups = new ArrayList();
        if ((null != omGroups) && (omGroups.size() > 0))
        {
            Iterator omGroupsIterator = omGroups.iterator();
            while (omGroupsIterator.hasNext())
            {
                JetspeedGroupPrincipal omGroup = (JetspeedGroupPrincipal) omGroupsIterator.next();
                groups.add(getGroup(omGroup));
            }
        }
        return groups;
    }

    /**
     * <p>Given a group full path, removes the matching group from a given
     * {@JetspeedGroupPrincipal} collection.</p>
     * @param omGroups The collection of {@JetspeedGroupPrincipal}.
     * @param groupFullPathName The full path of the group to remove.
     * @return The new collection of {@JetspeedGroupPrincipal}.
     */
    Collection removeGroup(Collection omGroups, String groupFullPathName)
    {
        ArrayList newOmGroups = new ArrayList();
        Iterator omGroupsIterator = omGroups.iterator();
        while (omGroupsIterator.hasNext())
        {
            JetspeedGroupPrincipal omGroup = (JetspeedGroupPrincipal) omGroupsIterator.next();
            if (!(omGroup.getFullPath().equals(GroupPrincipalImpl.getFullPathFromPrincipalName(groupFullPathName))))
            {
                newOmGroups.add(omGroup);
            }
        }
        return newOmGroups;
    }

    /**
     * <p>Utility method to get a set of {@link RolePrincipal} from 
     * a collection of {@link JetspeedRolePrincipal} object model.</p>
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
                JetspeedRolePrincipal omRole = (JetspeedRolePrincipal) omRolesIter.next();
                rolePrincipals.add(new RolePrincipalImpl(RolePrincipalImpl.getPrincipalNameFromFullPath(omRole.getFullPath())));
            }
        }
        return rolePrincipals;
    }

    /**
     * <p>Utility method to get a set of {@link GroupPrincipal} from 
     * a collection of {@link JetspeedGroupPrincipal} object model.</p>
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
                JetspeedGroupPrincipal omGroup = (JetspeedGroupPrincipal) omGroupsIter.next();
                groupPrincipals.add(new GroupPrincipalImpl(GroupPrincipalImpl.getPrincipalNameFromFullPath(omGroup.getFullPath())));
            }
        }
        return groupPrincipals;
    }
}
