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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.InternalRolePrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalGroupPrincipalImpl;
import org.apache.jetspeed.security.om.impl.InternalUserPrincipalImpl;
import org.apache.jetspeed.security.spi.SecurityAccess;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;

/**
 * @see org.apache.jetspeed.security.spi.SecurityMappingHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 */
public class DefaultSecurityMappingHandler implements SecurityMappingHandler
{

    /** The hierarchy resolver. */
    HierarchyResolver hierarchyResolver = null;

    /** Common queries. */
    private SecurityAccess commonQueries = null;

    /**
     * <p>
     * Constructor providing access to the common queries.
     * </p>
     */
    public DefaultSecurityMappingHandler(SecurityAccess commonQueries)
    {
        this.commonQueries = commonQueries;
    }

    /**
     * <p>
     * Constructor providing access to the common queries and hierarchy
     * resolvers.
     * </p>
     */
    public DefaultSecurityMappingHandler(SecurityAccess commonQueries, HierarchyResolver hierarchyResolver)
    {
        this.commonQueries = commonQueries;
        this.hierarchyResolver = hierarchyResolver;
    }

    /**
     * @return Returns the HierarchyResolver.
     */
    public HierarchyResolver getHierarchyResolver()
    {
        return hierarchyResolver;
    }

    public void setHierarchyResolver(HierarchyResolver hierarchyResolver)
    {
        this.hierarchyResolver = hierarchyResolver;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getRolePrincipals(java.lang.String)
     */
    public Set<RolePrincipal> getRolePrincipals(String username)
    {
        Set<RolePrincipal> rolePrincipals = new HashSet<RolePrincipal>();
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username);
        if (null != internalUser)
        {
            Collection<InternalRolePrincipal> internalRoles = internalUser.getRolePrincipals();
            if (null != internalRoles)
            {
                for (InternalRolePrincipal internalRole : internalRoles)    
                {
                    if (this.hierarchyResolver != null)
                    {
                        Set<RolePrincipal> subset = hierarchyResolver.resolveRoles(internalRole.getName());
                        for (RolePrincipal rp : subset)
                        {
                            if (!rolePrincipals.contains(rp))
                                rolePrincipals.add(rp);
                        }
                    }
                    else
                    {
                        rolePrincipals.add(new RolePrincipalImpl(internalRole.getPrincipalId(), internalRole.getName(),
                                internalRole.isEnabled(), internalRole.isMappingOnly()));
                    }
                }
            }
        }
        return rolePrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#setUserPrincipalInRole(java.lang.String,
     *      java.lang.String)
     */
    public void setUserPrincipalInRole(String username, String roleName) throws SecurityException
    {
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username);
        boolean isMappingOnly = false;
        if (null == internalUser)
        {
            // This is a record for mapping only.
            isMappingOnly = true;
            internalUser = new InternalUserPrincipalImpl(username);
        }
        Collection<InternalRolePrincipal> internalRoles = internalUser.getRolePrincipals();
        // This should not be null. Check for null should be made by the caller.
        InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(roleName);
        // Check anyway.
        if (null == internalRole)
        {
            throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST.create(roleName));
        }
        internalRoles.add(internalRole);
        internalUser.setRolePrincipals(internalRoles);
        commonQueries.storeInternalUserPrincipal(internalUser, isMappingOnly);
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#removeUserPrincipalInRole(java.lang.String,
     *      java.lang.String)
     */
    public void removeUserPrincipalInRole(String username, String roleName) throws SecurityException
    {
        boolean isMappingOnly = false;
        // Check is the record is used for mapping only.
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username, false);
        if (null == internalUser)
        {
            internalUser = commonQueries.getInternalUserPrincipal(username, true);
            isMappingOnly = true;
        }
        if (null != internalUser)
        {
            Collection<InternalRolePrincipal> internalRoles = internalUser.getRolePrincipals();
            // This should not be null. Check for null should be made by the caller.
            InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(roleName);
            if (null == internalRole)
            {
                throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST.create(roleName));
            }
            internalRoles.remove(internalRole);
            // Remove dead mapping records. I.e. No mapping is associated with the specific record.
            if (isMappingOnly && internalRoles.isEmpty() && internalUser.getGroupPrincipals().isEmpty()
                    && internalUser.getPermissions().isEmpty())
            {
                commonQueries.removeInternalUserPrincipal(internalUser);
            }
            else
            {
                internalUser.setRolePrincipals(internalRoles);
                commonQueries.storeInternalUserPrincipal(internalUser, isMappingOnly);
            }
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(username));
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getRolePrincipalsInGroup(java.lang.String)
     */
    public Set<RolePrincipal> getRolePrincipalsInGroup(String groupName)
    {
        Set<RolePrincipal> rolePrincipals = new HashSet<RolePrincipal>();
        InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(groupName);
        if (internalGroup != null)
        {
            if (this.hierarchyResolver != null)
            {
                Collection<InternalRolePrincipal> internalRoles = internalGroup.getRolePrincipals();
                for (InternalRolePrincipal internalRole: internalRoles)
                {
                    Set<RolePrincipal> resolvedRoles = hierarchyResolver.resolveRoles(internalRole.getName());            
                    for (RolePrincipal rp : resolvedRoles)
                    {    
                        if (!rolePrincipals.contains(rp))
                        {
                            rolePrincipals.add(new RolePrincipalImpl(internalRole.getPrincipalId(), internalRole.getName(),
                                    internalRole.isEnabled(), internalRole.isMappingOnly()));                
                        }
                    }
                }
            }
            else
            {
                Collection<InternalRolePrincipal> internalRoles = internalGroup.getRolePrincipals();
                for (InternalRolePrincipal internalRole: internalRoles)
                {
                    rolePrincipals.add(new RolePrincipalImpl(internalRole.getPrincipalId(), internalRole.getName(),
                            internalRole.isEnabled(), internalRole.isMappingOnly()));                
                }
            }
        }
        return rolePrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#setRolePrincipalInGroup(java.lang.String,
     *      java.lang.String)
     */
    public void setRolePrincipalInGroup(String groupName, String roleName) throws SecurityException
    {
        InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(groupName);
        boolean isMappingOnly = false;
        if (null == internalGroup)
        {
            // This is a record for mapping only.
            isMappingOnly = true;
            internalGroup = new InternalGroupPrincipalImpl(groupName);
        }        
        Collection<InternalRolePrincipal> internalRoles = internalGroup.getRolePrincipals();
        InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(roleName);
        internalRoles.add(internalRole);
        internalGroup.setRolePrincipals(internalRoles);
        commonQueries.storeInternalGroupPrincipal(internalGroup, isMappingOnly);
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#removeRolePrincipalInGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeRolePrincipalInGroup(String groupName, String roleName) throws SecurityException
    {
        InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(groupName);
        boolean isMappingOnly = false;
        if (null == internalGroup)
        {
            // This is a record for mapping only.
            isMappingOnly = true;
            internalGroup = new InternalGroupPrincipalImpl(groupName);
        }                
        if (null == internalGroup)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST.create(internalGroup));
        }
        Collection<InternalRolePrincipal> internalRoles = internalGroup.getRolePrincipals();
        InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(roleName);
        internalRoles.remove(internalRole);
        internalGroup.setRolePrincipals(internalRoles);
        commonQueries.storeInternalGroupPrincipal(internalGroup, isMappingOnly);
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getGroupPrincipals(java.lang.String)
     */
    public Set<GroupPrincipal> getGroupPrincipals(String username)
    {
        Set<GroupPrincipal> groupPrincipals = new HashSet<GroupPrincipal>();
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username);
        if (null != internalUser)
        {
            Collection<InternalGroupPrincipal> internalGroups = internalUser.getGroupPrincipals();
            if (null != internalGroups)
            {
                for (InternalGroupPrincipal internalGroup : internalGroups)
                {
                    if (hierarchyResolver != null)
                    {
                        Set<GroupPrincipal> resolvedGroups = hierarchyResolver.resolveGroups(internalGroup.getName());                    
                        for (GroupPrincipal gp : resolvedGroups)
                        {
                            if (!groupPrincipals.contains(gp))
                                groupPrincipals.add(gp);
                        }
                    }
                    else
                    {
                        groupPrincipals.add(new GroupPrincipalImpl(internalGroup.getPrincipalId(), internalGroup.getName(),
                                internalGroup.isEnabled(), internalGroup.isMappingOnly()));                                        
                    }
                }
            }
        }
        return groupPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getGroupPrincipalsInRole(java.lang.String)
     */
    public Set<GroupPrincipal> getGroupPrincipalsInRole(String roleName)
    {
        Set<GroupPrincipal> groupPrincipals = new HashSet<GroupPrincipal>();
        InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(roleName);
        if (internalRole != null)
        {
            if (this.hierarchyResolver != null)
            {
                Collection<InternalGroupPrincipal> internalGroups = internalRole.getGroupPrincipals();
                for (InternalGroupPrincipal internalGroup: internalGroups)
                {
                    Set<GroupPrincipal> resolvedGroups = hierarchyResolver.resolveGroups(internalGroup.getName());            
                    for (GroupPrincipal gp : resolvedGroups)
                    {    
                        if (!groupPrincipals.contains(gp))
                        {
                            groupPrincipals.add(new GroupPrincipalImpl(internalGroup.getPrincipalId(), internalGroup.getName(),
                                    internalGroup.isEnabled(), internalGroup.isMappingOnly()));                
                        }
                    }
                }
            }
            else
            {
                Collection<InternalGroupPrincipal> internalGroups = internalRole.getGroupPrincipals();
                for (InternalGroupPrincipal internalGroup: internalGroups)
                {
                    groupPrincipals.add(new GroupPrincipalImpl(internalGroup.getPrincipalId(), internalGroup.getName(),
                            internalGroup.isEnabled(), internalGroup.isMappingOnly()));                
                }
            }
        }
        return groupPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getUserPrincipalsInRole(java.lang.String)
     */
    public Set<UserPrincipal> getUserPrincipalsInRole(String roleName)
    {
        Set<UserPrincipal> userPrincipals = new HashSet<UserPrincipal>();
        InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(roleName);
        if (internalRole != null)
        {
            Collection<InternalUserPrincipal> internalUsers = internalRole.getUserPrincipals();
            for (InternalUserPrincipal internalUser: internalUsers)
            {
                userPrincipals.add(new UserPrincipalImpl(internalUser.getPrincipalId(), internalUser.getName(),
                        internalUser.isEnabled(), internalUser.isMappingOnly()));                
            }
        }
        return userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#getUserPrincipalsInGroup(java.lang.String)
     */
    public Set<UserPrincipal> getUserPrincipalsInGroup(String groupName)
    {
        Set<UserPrincipal> userPrincipals = new HashSet<UserPrincipal>();
        InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(groupName);
        if (internalGroup != null)
        {
            Collection<InternalUserPrincipal> internalUsers = internalGroup.getUserPrincipals();
            for (InternalUserPrincipal internalUser: internalUsers)
            {
                userPrincipals.add(new UserPrincipalImpl(internalUser.getPrincipalId(), internalUser.getName(),
                        internalUser.isEnabled(), internalUser.isMappingOnly()));                
            }
        }
        return userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#setUserPrincipalInGroup(java.lang.String,
     *      java.lang.String)
     */
    public void setUserPrincipalInGroup(String username, String groupName) throws SecurityException
    {
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username);
        boolean isMappingOnly = false;
        if (null == internalUser)
        {
            // This is a record for mapping only.
            isMappingOnly = true;
            internalUser = new InternalUserPrincipalImpl(username);
        }
        Collection<InternalGroupPrincipal> internalGroups = internalUser.getGroupPrincipals();
        // This should not be null. Check for null should be made by the caller.
        InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(groupName);
        // Check anyway.
        if (null == internalGroup)
        {
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST.create(groupName));
        }
        internalGroups.add(internalGroup);
        internalUser.setGroupPrincipals(internalGroups);
        commonQueries.storeInternalUserPrincipal(internalUser, isMappingOnly);
    }

    /**
     * @see org.apache.jetspeed.security.spi.SecurityMappingHandler#removeUserPrincipalInGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeUserPrincipalInGroup(String username, String groupName) throws SecurityException
    {
        boolean isMappingOnly = false;
        // Check is the record is used for mapping only.
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username, false);
        if (null == internalUser)
        {
            internalUser = commonQueries.getInternalUserPrincipal(username, true);
            isMappingOnly = true;
        }
        if (null != internalUser)
        {
            Collection<InternalGroupPrincipal> internalGroups = internalUser.getGroupPrincipals();
            // This should not be null. Check for null should be made by the caller.
            InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(groupName);
            if (null == internalGroup)
            {
                throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST.create(groupName));
            }
            internalGroups.remove(internalGroup);
            // Remove dead mapping records. I.e. No mapping is associated with the specific record.
            if (isMappingOnly && internalGroups.isEmpty() && internalUser.getRolePrincipals().isEmpty()
                    && internalUser.getPermissions().isEmpty())
            {
                commonQueries.removeInternalUserPrincipal(internalUser);
            }
            else
            {
                internalUser.setGroupPrincipals(internalGroups);
                commonQueries.storeInternalUserPrincipal(internalUser, isMappingOnly);
            }
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(username));
        }
    }
    
}
