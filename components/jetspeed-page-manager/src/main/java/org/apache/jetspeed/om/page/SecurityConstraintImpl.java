/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.page;

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;


/**
 * <p>
 * SecurityConstraintImpl
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 *
 */
public class SecurityConstraintImpl implements SecurityConstraint
{
    private int id;
    private int applyOrder;
    private List<String> usersList;
    private List<String> rolesList;
    private List<String> groupsList;
    private List<String> permissionsList;

    private String users;
    private String roles;
    private String groups;
    private String permissions;

    /**
     * getApplyOrder
     *
     * @return apply order for constraints
     */
    public int getApplyOrder()
    {
        return applyOrder;
    }

    /**
     * setApplyOrder
     *
     * @param order apply order for constraints
     */
    public void setApplyOrder(int order)
    {
        applyOrder = order;
    }

    /**
     * getUsersAsString
     *
     * @return users CSV list
     */
    public String getUsersAsString()
    {
        // get from users list if not immediately available
        if ((users == null) && (usersList != null) && !usersList.isEmpty())
        {
            users = formatCSVList(usersList);
        }
        return users;
    }

    /**
     * setUsersAsString
     *
     * @param users users CSV list
     */
    public void setUsersAsString(String users)
    {
        // set and propagate to users list setting
        this.users = users;
        usersList = parseCSVList(users);
    }

    /**
     * getRolesAsString
     *
     * @return roles CSV list
     */
    public String getRolesAsString()
    {
        // get from roles list if not immediately available
        if ((roles == null) && (rolesList != null) && !rolesList.isEmpty())
        {
            roles = formatCSVList(rolesList);
        }
        return roles;
    }
    
    /**
     * setRolesAsString
     *
     * @param roles roles CSV list
     */
    public void setRolesAsString(String roles)
    {
        // set and propagate to roles list setting
        this.roles = roles;
        rolesList = parseCSVList(roles);
    }

    /**
     * getGroupsAsString
     *
     * @return groups CSV list
     */
    public String getGroupsAsString()
    {
        // get from groups list if not immediately available
        if ((groups == null) && (groupsList != null) && !groupsList.isEmpty())
        {
            groups = formatCSVList(groupsList);
        }
        return groups;
    }
    
    /**
     * setGroupsAsString
     *
     * @param groups groups CSV list
     */
    public void setGroupsAsString(String groups)
    {
        // set and propagate to groups list setting
        this.groups = groups;
        groupsList = parseCSVList(groups);
    }

    /**
     * getPermissionsAsString
     *
     * @return permissions CSV list
     */
    public String getPermissionsAsString()
    {
        // get from permissions list if not immediately available
        if ((permissions == null) && (permissionsList != null) && !permissionsList.isEmpty())
        {
            permissions = formatCSVList(permissionsList);
        }
        return permissions;
    }
    
    /**
     * setPermissionsAsString
     *
     * @param permissions permissions CSV list
     */
    public void setPermissionsAsString(String permissions)
    {
        // set and propagate to permissions list setting
        this.permissions = permissions;
        permissionsList = parseCSVList(permissions);
    }
    
    /**
     * <p>
     * getUsers
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getUsers()
     * @return users list
     */
    public List<String> getUsers()
    {
        return usersList;
    }
    
    /**
     * <p>
     * setUsers
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setUsers(java.util.List)
     * @param users users list
     */
    public void setUsers(List<String> users)
    {
        // set and clear potentially stale string representation
        usersList = users;
        this.users = null;
    }

    /**
     * <p>
     * getRoles
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getRoles()
     * @return roles list
     */
    public List<String> getRoles()
    {
        return rolesList;
    }
    
    /**
     * <p>
     * setRoles
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setRoles(java.util.List)
     * @param roles roles list
     */
    public void setRoles(List<String> roles)
    {
        // set and clear potentially stale string representation
        rolesList = roles;
        this.roles = null;
    }

    /**
     * <p>
     * getGroups
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getGroups()
     * @return groups list
     */
    public List<String> getGroups()
    {
        return groupsList;
    }
    
    /**
     * <p>
     * setGroups
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setGroups(java.util.List)
     * @param groups groups list
     */
    public void setGroups(List<String> groups)
    {
        // set and clear potentially stale string representation
        groupsList = groups;
        this.groups = null;
    }

    /**
     * <p>
     * getPermissions
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getPermissions()
     * @return permissions list
     */
    public List<String> getPermissions()
    {
        return permissionsList;
    }
    
    /**
     * <p>
     * setPermissions
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setPermissions(java.util.List)
     * @param permissions permissions list
     */
    public void setPermissions(List<String> permissions)
    {
        // set and clear potentially stale string representation
        permissionsList = permissions;
        this.permissions = null;
    }

    /**
     * <p>
     * principalsMatch
     * </p>
     * <p>
     * Test user/role/group names against principal names.
     * </p>
     *
     * @param userPrincipals
     * @param rolePrincipals
     * @param groupPrincipals
     * @param allowDefault
     * @return match result
     */
    public boolean principalsMatch(List<String> userPrincipals, List<String> rolePrincipals, List<String> groupPrincipals, boolean allowDefault)
    {
        // test match using users, roles, and groups list members
        // since these are the master representation in this impl
        return ((allowDefault && (usersList == null) && (rolesList == null) && (groupsList == null)) ||
                ((usersList != null) && (userPrincipals != null) &&
                 (containsAny(usersList, userPrincipals) || usersList.contains(WILD_CHAR))) ||
                ((rolesList != null) && (rolePrincipals != null) &&
                 (containsAny(rolesList, rolePrincipals) || rolesList.contains(WILD_CHAR))) ||
                ((groupsList != null) && (groupPrincipals != null) &&
                 (containsAny(groupsList, groupPrincipals) || groupsList.contains(WILD_CHAR))));
    }

    /**
     * <p>
     * actionMatch
     * </p>
     * <p>
     * Test permission names against action name.
     * </p>
     *
     * @param action
     * @return match result
     */
    public boolean actionMatch(String action)
    {
        // test match using permissions list member since
        // this is the master representation in this impl
        return ((permissionsList != null) &&
                (permissionsList.contains(action) || permissionsList.contains(WILD_CHAR)));
    }

    /**
     * <p>
     * parseCSVList
     * </p>
     * <p>
     * Utility to parse CSV string values into Lists.
     * </p>
     *
     * @param csv
     * @return parsed values list.
     */
    public static List<String> parseCSVList(String csv)
    {
        if (csv != null)
        {
            List<String> csvList = DatabasePageManagerUtils.createList();;
            if (csv.indexOf(',') != -1)
            {
                StringTokenizer csvTokens = new StringTokenizer(csv, ",");
                while (csvTokens.hasMoreTokens())
                {
                    csvList.add(csvTokens.nextToken().trim());
                }
            }
            else
            {
                csvList.add(csv);
            }
            return csvList;
        }
        return null;
    }

    /**
     * <p>
     * formatCSVList
     * </p>
     * <p>
     * Utility to format CSV List values into strings.
     * </p>
     *
     * @param list
     * @return formatted string value.
     */
    public static String formatCSVList(List<String> list)
    {
        if ((list != null) && !list.isEmpty())
        {
            StringBuffer csv = new StringBuffer();
            for (String item : list)
            {
                if (csv.length() > 0)
                {
                    csv.append(",");
                }
                csv.append(item);
            }
            return csv.toString();
        }
        return null;
    }

    /**
     * <p>
     * containsAny
     * </p>
     * <p>
     * Utility implementation for contains any test against two collections.
     * </p>
     *
     * @param collection0
     * @param collection1
     * @return contains any result.
     */
    public static boolean containsAny(Collection<String> collection0, Collection<String> collection1)
    {
        if ((collection0 != null) && (collection1 != null))
        {
            for (String item1 : collection1)
            {
                if (collection0.contains(item1))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
