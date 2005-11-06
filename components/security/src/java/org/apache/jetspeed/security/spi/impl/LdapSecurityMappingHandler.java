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
package org.apache.jetspeed.security.spi.impl;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.GeneralizationHierarchyResolver;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
import org.apache.jetspeed.security.spi.impl.ldap.LdapGroupDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDaoImpl;

/**
 * @see org.apache.jetspeed.security.spi.SecurityMappingHandler
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a><br/>
 *         <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class LdapSecurityMappingHandler implements SecurityMappingHandler
{

    private LdapUserPrincipalDao userDao;

    private LdapPrincipalDao groupDao;

    /** The logger. */
    private static final Log LOG = LogFactory.getLog(LdapSecurityMappingHandler.class);

    /** The role hierarchy resolver. */
    private HierarchyResolver roleHierarchyResolver = new GeneralizationHierarchyResolver();

    /** The group hierarchy resolver. */
    private HierarchyResolver groupHierarchyResolver = new GeneralizationHierarchyResolver();

    /**
     * @param userDao
     * @param groupDao
     */
    public LdapSecurityMappingHandler(LdapUserPrincipalDao userDao, LdapPrincipalDao groupDao)
    {
        this.userDao = userDao;
        this.groupDao = groupDao;
    }

    /**
     * @throws NamingException A {@link NamingException}.
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapSecurityMappingHandler() throws SecurityException, NamingException
    {
        this.userDao = new LdapUserPrincipalDaoImpl();
        this.groupDao = new LdapGroupDaoImpl();
    }

    /** 
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getRoleHierarchyResolver()
     */
    public HierarchyResolver getRoleHierarchyResolver()
    {
        return roleHierarchyResolver;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#setRoleHierarchyResolver(org.apache.jetspeed.security.HierarchyResolver)
     */
    public void setRoleHierarchyResolver(HierarchyResolver roleHierarchyResolver)
    {
        this.roleHierarchyResolver = roleHierarchyResolver;
    }

    /**
     * @return Returns the groupHierarchyResolver.
     */
    public HierarchyResolver getGroupHierarchyResolver()
    {
        return groupHierarchyResolver;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#setGroupHierarchyResolver(org.apache.jetspeed.security.HierarchyResolver)
     */
    public void setGroupHierarchyResolver(HierarchyResolver groupHierarchyResolver)
    {
        this.groupHierarchyResolver = groupHierarchyResolver;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getRolePrincipals(java.lang.String)
     */
    public Set getRolePrincipals(String username)
    {
        Set rolePrincipals = new HashSet();
        return rolePrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#setRolePrincipal(java.lang.String,
     *      java.lang.String)
     */
    public void setRolePrincipal(String username, String roleFullPathName) throws SecurityException
    {
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#removeRolePrincipal(java.lang.String,
     *      java.lang.String)
     */
    public void removeRolePrincipal(String username, String roleFullPathName) throws SecurityException
    {
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getRolePrincipalsInGroup(java.lang.String)
     */
    public Set getRolePrincipalsInGroup(String groupFullPathName)
    {
        Set rolePrincipals = new HashSet();
        return rolePrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#setRolePrincipalInGroup(java.lang.String,
     *      java.lang.String)
     */
    public void setRolePrincipalInGroup(String groupFullPathName, String roleFullPathName) throws SecurityException
    {
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#removeRolePrincipalInGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeRolePrincipalInGroup(String groupFullPathName, String roleFullPathName) throws SecurityException
    {
    }

    /**
     * This method returns the set of group principals associated with a user.
     * 
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getGroupPrincipals(java.lang.String)
     */
    public Set getGroupPrincipals(String userPrincipalUid)
    {
        Set groupPrincipals = new HashSet();

        String[] groups;
        try
        {
            groups = userDao.getGroupUidsForUser(userPrincipalUid);
            for (int i = 0; i < groups.length; i++)
            {
                createResolvedGroupPrincipalSet(userPrincipalUid, groupPrincipals, groups, i);
            }
        }
        catch (SecurityException e)
        {
            LOG.error(e);
        }
        return groupPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getGroupPrincipalsInRole(java.lang.String)
     */
    public Set getGroupPrincipalsInRole(String roleFullPathName)
    {
        Set groupPrincipals = new HashSet();
        return groupPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getUserPrincipalsInRole(java.lang.String)
     */
    public Set getUserPrincipalsInRole(String roleFullPathName)
    {
        Set userPrincipals = new HashSet();
        return userPrincipals;
    }

    /**
     * <p>
     * This method is the analog of the getGroupPrincipals except it returns the
     * set of user principals in a group.
     * </p>
     * 
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getUserPrincipalsInGroup(java.lang.String)
     */
    public Set getUserPrincipalsInGroup(String groupFullPathName)
    {
        Set userPrincipals = new HashSet();
        Preferences preferences = Preferences.userRoot().node(groupFullPathName);
        String[] fullPaths = groupHierarchyResolver.resolve(preferences);
        try
        {
            getUserPrincipalsInGroup(userPrincipals, fullPaths);
        }
        catch (SecurityException e)
        {
            LOG.error(e);
        }
        return userPrincipals;
    }

    /**
     * <p>
     * Gets the user principals in groups.
     * </p>
     * 
     * @param userPrincipals
     * @param fullPaths
     * @throws SecurityException A {@link SecurityException}.
     */
    private void getUserPrincipalsInGroup(Set userPrincipals, String[] fullPaths) throws SecurityException
    {
        for (int i = 0; i < fullPaths.length; i++)
        {
            String[] usersInGroup = userDao.getUserUidsForGroup(fullPaths[i]);
            for (int y = 0; y < usersInGroup.length; y++)
            {
                Principal userPrincipal = new UserPrincipalImpl(usersInGroup[y]);
                userPrincipals.add(userPrincipal);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#setUserPrincipalInGroup(java.lang.String,
     *      java.lang.String)
     */
    public void setUserPrincipalInGroup(String username, String groupFullPathName) throws SecurityException
    {
        verifyUserAndGroupExist(username, groupFullPathName);
        addGroupToUser(username, groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#removeUserPrincipalInGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeUserPrincipalInGroup(String username, String groupFullPathName) throws SecurityException
    {
        verifyUserAndGroupExist(username, groupFullPathName);
        removeUserFromGroup(username, groupFullPathName);
    }

    /**
     * @param username
     * @param groupFullPathName
     * @throws SecurityException
     */
    private void verifyUserAndGroupExist(String username, String groupFullPathName) throws SecurityException
    {
        UserPrincipal user = getUser(username);
        GroupPrincipal group = getGroup(groupFullPathName);
        if ((null == user) && (null == group))
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST);
        }
    }

    /**
     * @param username
     * @param groupPrincipals
     * @param groups
     * @param i
     */
    private void createResolvedGroupPrincipalSet(String username, Set groupPrincipals, String[] groups, int i)
    {
        LOG.debug("Group [" + i + "] for user[" + username + "] is [" + groups[i] + "]");

        GroupPrincipal group = new GroupPrincipalImpl(groups[i]);
        Preferences preferences = Preferences.userRoot().node(group.getName());
        LOG.debug("Group name:" + group.getName());
        String[] fullPaths = groupHierarchyResolver.resolve(preferences);
        for (int n = 0; n < fullPaths.length; n++)
        {
            LOG.debug("Group [" + i + "] for user[" + username + "] is ["
                    + GroupPrincipalImpl.getPrincipalNameFromFullPath(fullPaths[n]) + "]");
            groupPrincipals.add(new GroupPrincipalImpl(GroupPrincipalImpl.getPrincipalNameFromFullPath(fullPaths[n])));
        }
    }

    /**
     * @param username
     * @param groupFullPathName
     * @throws SecurityException
     */
    private void removeUserFromGroup(String username, String groupFullPathName) throws SecurityException
    {
        userDao.removeGroup(username, groupFullPathName);
    }

    /**
     * @param uid
     * @return
     * @throws SecurityException A {@link SecurityException}.
     */
    private UserPrincipal getUser(String uid) throws SecurityException
    {
        Principal[] user = userDao.find(uid, UserPrincipal.PREFS_USER_ROOT);
        if (user.length == 1)
        {
            return (UserPrincipal) user[0];
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(uid));
        }
    }

    /**
     * @param uid
     * @return
     * @throws SecurityException A {@link SecurityException}.
     */
    private GroupPrincipal getGroup(String uid) throws SecurityException
    {
        Principal[] group = groupDao.find(uid, GroupPrincipal.PREFS_GROUP_ROOT);
        if (group.length == 1)
        {
            return (GroupPrincipal) group[0];
        }
        else
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST.create(uid));
        }
    }

    /**
     * @param username
     * @param groupFullPathName
     * @throws SecurityException A {@link SecurityException}.
     */
    private void addGroupToUser(String username, String groupFullPathName) throws SecurityException
    {
        userDao.addGroup(username, groupFullPathName);
    }

}