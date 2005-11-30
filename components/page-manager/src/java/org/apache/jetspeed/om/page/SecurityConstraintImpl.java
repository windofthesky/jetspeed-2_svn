/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jetspeed.om.common.SecurityConstraint;


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
    private List usersList;
    private List rolesList;
    private List groupsList;
    private List permissionsList;

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
        this.groups = groups;
        groupsList = parseCSVList(groups);
    }

    /**
     * getPermissionsAsString
     *
     * @return permmissions CSV list
     */
    public String getPermissionsAsString()
    {
        if ((permissions == null) && (permissionsList != null) && !permissionsList.isEmpty())
        {
            permissions = formatCSVList(permissionsList);
        }
        return permissions;
    }
    
    /**
     * setPermissionsAsString
     *
     * @param permissions permmissions CSV list
     */
    public void setPermissionsAsString(String permissions)
    {
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
    public List getUsers()
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
    public void setUsers(List users)
    {
        this.users = formatCSVList(users);
        usersList = users;
    }

    /**
     * <p>
     * getRoles
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getRoles()
     * @return roles list
     */
    public List getRoles()
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
    public void setRoles(List roles)
    {
        this.roles = formatCSVList(roles);
        rolesList = roles;
    }

    /**
     * <p>
     * getGroups
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getGroups()
     * @return groups list
     */
    public List getGroups()
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
    public void setGroups(List groups)
    {
        this.groups = formatCSVList(groups);
        groupsList = groups;
    }

    /**
     * <p>
     * getPermissions
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getPermissions()
     * @return permissions list
     */
    public List getPermissions()
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
    public void setPermissions(List permissions)
    {
        this.permissions = formatCSVList(permissions);
        permissionsList = permissions;
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
    public boolean principalsMatch(List userPrincipals, List rolePrincipals, List groupPrincipals, boolean allowDefault)
    {
        return ((allowDefault && (users == null) && (roles == null) && (groups == null)) ||
                ((users != null) && (userPrincipals != null) &&
                 (users.equals(WILD_CHAR) || containsAny(usersList, userPrincipals))) ||
                ((roles != null) && (rolePrincipals != null) &&
                 (roles.equals(WILD_CHAR) || containsAny(rolesList, rolePrincipals))) ||
                ((groups != null) && (groupPrincipals != null) &&
                 (groups.equals(WILD_CHAR) || containsAny(groupsList, groupPrincipals))));
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
        return ((permissions != null) &&
                (permissions.equals(WILD_CHAR) || permissionsList.contains(action)));
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
    public static List parseCSVList(String csv)
    {
        if ((csv != null) && !csv.equals(WILD_CHAR))
        {
            List csvList = new ArrayList(4);
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
    public static String formatCSVList(List list)
    {
        if ((list != null) && !list.isEmpty())
        {
            StringBuffer csv = new StringBuffer();
            Iterator listIter = list.iterator();
            while (listIter.hasNext())
            {
                if (csv.length() > 0)
                {
                    csv.append(",");
                }
                csv.append((String)listIter.next());
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
    public static boolean containsAny(Collection collection0, Collection collection1)
    {
        if ((collection0 != null) && (collection1 != null))
        {
            Iterator containsIter = collection1.iterator();
            while (containsIter.hasNext())
            {
                if (collection0.contains(containsIter.next()))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
