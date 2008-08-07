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
package org.apache.jetspeed.security.spi.impl;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
import org.apache.jetspeed.security.spi.impl.ldap.LdapGroupDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapRoleDaoImpl;
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
    
    private LdapPrincipalDao roleDao;

    /** The logger. */
    private static final Log LOG = LogFactory.getLog(LdapSecurityMappingHandler.class);

    /** The role hierarchy resolver. */
    private HierarchyResolver hierarchyResolver = null;

    /**
     * @param userDao
     * @param groupDao
     */
    public LdapSecurityMappingHandler(LdapUserPrincipalDao userDao, LdapPrincipalDao groupDao,LdapPrincipalDao roleDao)
    {
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.roleDao = roleDao;
    }

    /**
     * @throws NamingException A {@link NamingException}.
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapSecurityMappingHandler() throws SecurityException, NamingException
    {
        this.userDao = new LdapUserPrincipalDaoImpl();
        this.groupDao = new LdapGroupDaoImpl();
        this.roleDao = new LdapRoleDaoImpl();
    }

    public HierarchyResolver getHierarchyResolver()
    {
        return hierarchyResolver;
    }

    public void setHierarchyResolver(HierarchyResolver hierarchyResolver)
    {
        this.hierarchyResolver = hierarchyResolver;
    }

    public Set<RolePrincipal> getRolePrincipals(String username)
    {
        Set<RolePrincipal> rolePrincipals = new HashSet<RolePrincipal>();
        String[] roles;
        try
        {
            roles = userDao.getRoleUidsForUser(username);
            for (int i = 0; i < roles.length; i++)
            {
                createResolvedRolePrincipalSet(username, rolePrincipals, roles, i);
            }
        }
        catch (SecurityException e)
        {
            LOG.error(e);
        }
        return rolePrincipals;
        
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#setUserPrincipalInRole(java.lang.String,
     *      java.lang.String)
     */
    public void setUserPrincipalInRole(String username, String roleFullPathName) throws SecurityException
    {
        verifyUserAndRoleExist(username, roleFullPathName);
        addRoleToUser(username, roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#removeUserPrincipalInRole(java.lang.String,
     *      java.lang.String)
     */
    public void removeUserPrincipalInRole(String username, String roleFullPathName) throws SecurityException
    {
        verifyUserAndRoleExist(username, roleFullPathName);
        removeUserFromRole(username, roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getRolePrincipalsInGroup(java.lang.String)
     */
    public Set<RolePrincipal> getRolePrincipalsInGroup(String groupFullPathName)
    {
        Set<RolePrincipal> rolePrincipalsInGroup = new HashSet<RolePrincipal>();
        String[] roles;
        try
        {
        	//TODO: see if we can't use the groupDao here
            roles = userDao.getRolesForGroup(groupFullPathName);
            for (int i = 0; i < roles.length; i++)
            {
                createResolvedRolePrincipalSet(groupFullPathName, rolePrincipalsInGroup, roles, i);
            }
        }
        catch (SecurityException e)
        {
            LOG.error(e);
        }
        return rolePrincipalsInGroup;        
    }

    public void setRolePrincipalInGroup(String groupFullPathName, String roleFullPathName) throws SecurityException
    {
        verifyGroupAndRoleExist(groupFullPathName, roleFullPathName);
        addRoleToGroup(groupFullPathName, roleFullPathName);    	
    }

    public void removeRolePrincipalInGroup(String groupFullPathName, String roleFullPathName) throws SecurityException
    {
        verifyGroupAndRoleExist(groupFullPathName, roleFullPathName);
        removeRoleFromGroup(groupFullPathName, roleFullPathName);    	
    }

	/**
     * This method returns the set of group principals associated with a user.
     * 
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getGroupPrincipals(java.lang.String)
     */
    public Set<GroupPrincipal> getGroupPrincipals(String userPrincipalUid)
    {
    	Set<GroupPrincipal> groupPrincipals = new HashSet<GroupPrincipal>();
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

    public Set<GroupPrincipal> getGroupPrincipalsInRole(String roleFullPathName)
    {
        Set<GroupPrincipal> groupPrincipals = new HashSet<GroupPrincipal>();
        return groupPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getUserPrincipalsInRole(java.lang.String)
     */
    public Set<UserPrincipal> getUserPrincipalsInRole(String roleFullPathName)
    {
    	//TODO: Check that this is correct
    	Set<UserPrincipal> userPrincipals = new HashSet<UserPrincipal>();
        String[] fullPaths = {roleFullPathName};
        try
        {
            getUserPrincipalsInRole(userPrincipals, fullPaths);
        }
        catch (SecurityException e)
        {
            LOG.error(e);
        }
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
    public Set<UserPrincipal> getUserPrincipalsInGroup(String groupFullPathName)
    {
    	Set<UserPrincipal> userPrincipals = new HashSet<UserPrincipal>();
    	//TODO: Check that this is correct
    	String[] fullPaths = {groupFullPathName};
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
    private void getUserPrincipalsInGroup(Set<UserPrincipal> userPrincipals, String[] fullPaths) throws SecurityException
    {
        for (int i = 0; i < fullPaths.length; i++)
        {
            String[] usersInGroup = userDao.getUserUidsForGroup(fullPaths[i]);
            for (int y = 0; y < usersInGroup.length; y++)
            {
                UserPrincipal userPrincipal = new UserPrincipalImpl(usersInGroup[y]);
                userPrincipals.add(userPrincipal);
            }
        }
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
    private void getUserPrincipalsInRole(Set<UserPrincipal> userPrincipals, String[] fullPaths) throws SecurityException
    {
        for (int i = 0; i < fullPaths.length; i++)
        {
            String[] usersInRole = userDao.getUserUidsForRole(fullPaths[i]);
            for (int y = 0; y < usersInRole.length; y++)
            {
                UserPrincipal userPrincipal = new UserPrincipalImpl(usersInRole[y]);
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
    private void verifyGroupAndRoleExist(String groupFullPathName, String roleFullPathName) throws SecurityException
    {
        GroupPrincipal group = getGroup(groupFullPathName);
        RolePrincipal role = getRole(roleFullPathName);
        if ((null == group) && (null == role))
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST);
        }
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
     * @param groupFullPathName
     * @throws SecurityException
     */
    private void verifyUserAndRoleExist(String username, String roleFullPathName) throws SecurityException
    {
        UserPrincipal user = getUser(username);
        RolePrincipal role = getRole(roleFullPathName);
        if ((null == user) && (null == role))
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
    private void createResolvedGroupPrincipalSet(String username, Set<GroupPrincipal> groupPrincipals, String[] groups, int i)
    {
        LOG.debug("Group [" + i + "] for user[" + username + "] is [" + groups[i] + "]");
        GroupPrincipal group = new GroupPrincipalImpl(groups[i]);
        Set<GroupPrincipal> x = hierarchyResolver.resolveGroups(group.getName());
        for (GroupPrincipal groupPrincipal : x)
        {
            LOG.debug("Group [" + i + "] for user[" + username + "] is [" + groupPrincipal.getName() + "]");
            groupPrincipals.add(groupPrincipal);
        }
    }

    /**
     * @param username
     * @param groupPrincipals
     * @param groups
     * @param i
     */
    private void createResolvedRolePrincipalSet(String username, Set<RolePrincipal> rolePrincipals, String[] roles, int i)
    {
        LOG.debug("Role [" + i + "] for user[" + username + "] is [" + roles[i] + "]");
        RolePrincipal role = new RolePrincipalImpl(roles[i]);
        Set<RolePrincipal> x = hierarchyResolver.resolveRoles(role.getName());
        for (RolePrincipal rolePrincipal : x)
        {
            LOG.debug("Role [" + i + "] for user[" + username + "] is [" + rolePrincipal.getName() + "]");
            rolePrincipals.add(rolePrincipal);
        }
    }

    
    /**
     * @param username
     * @param groupName
     * @throws SecurityException
     */
    private void removeUserFromGroup(String username, String groupName) throws SecurityException
    {
        userDao.removeGroup(username, groupName);
    }
    
    /**
     * @param username
     * @param groupFullPathName
     * @throws SecurityException
     */
    private void removeUserFromRole(String username, String roleFullPathName) throws SecurityException
    {
        userDao.removeRole(username, roleFullPathName);
    }    
    
    private void removeRoleFromGroup(String groupFullPathName, String roleFullPathName)throws SecurityException
    {
    	userDao.removeRoleFromGroup(groupFullPathName,roleFullPathName);
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
     * @param uid
     * @return
     * @throws SecurityException A {@link SecurityException}.
     */
    private RolePrincipal getRole(String uid) throws SecurityException
    {
        Principal[] role = roleDao.find(uid, RolePrincipal.PREFS_ROLE_ROOT);
        
        if (role.length == 1)
        
        {
            return (RolePrincipal) role[0];
        }
        else
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST.create(uid));
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

    /**
     * @param username
     * @param groupFullPathName
     * @throws SecurityException A {@link SecurityException}.
     */
    private void addRoleToUser(String username, String roleFullPathName) throws SecurityException
    {
        userDao.addRole(username, roleFullPathName);
    }
    
    /**
     * @param username
     * @param groupFullPathName
     * @throws SecurityException A {@link SecurityException}.
     */
    private void addRoleToGroup(String groupFullPathName, String roleFullPathName) throws SecurityException
    {
        userDao.addRoleToGroup(groupFullPathName, roleFullPathName);
    }

}