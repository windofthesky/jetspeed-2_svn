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
    private String users;
    private List usersList;

    private String roles;
    private List rolesList;

    private String groups;
    private List groupsList;

    private String permissions;
    private List permissionsList;

    /**
     * <p>
     * getUsers
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getUsers()
     * @return
     */
    public String getUsers()
    {
        return users;
    }
    
    /**
     * <p>
     * getUsersList
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getUsersList()
     * @return
     */
    public List getUsersList()
    {
        return usersList;
    }
    
    /**
     * <p>
     * setUsers
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setUsers(java.lang.String)
     * @param users
     */
    public void setUsers(String users)
    {
        this.users = users;
        usersList = parseCSVList(users);
    }

    /**
     * <p>
     * setUsers
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setUsers(java.util.List)
     * @param users
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
     * @return
     */
    public String getRoles()
    {
        return roles;
    }
    
    /**
     * <p>
     * getRolesList
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getRolesList()
     * @return
     */
    public List getRolesList()
    {
        return rolesList;
    }
    
    /**
     * <p>
     * setRoles
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setRoles(java.lang.String)
     * @param roles
     */
    public void setRoles(String roles)
    {
        this.roles = roles;
        rolesList = parseCSVList(roles);
    }

    /**
     * <p>
     * setRoles
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setRoles(java.util.List)
     * @param roles
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
     * @return
     */
    public String getGroups()
    {
        return groups;
    }
    
    /**
     * <p>
     * getGroupsList
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getGroupsList()
     * @return
     */
    public List getGroupsList()
    {
        return groupsList;
    }
    
    /**
     * <p>
     * setGroups
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setGroups(java.lang.String)
     * @param groups
     */
    public void setGroups(String groups)
    {
        this.groups = groups;
        groupsList = parseCSVList(groups);
    }

    /**
     * <p>
     * setGroups
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setGroups(java.util.List)
     * @param groups
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
     * @return
     */
    public String getPermissions()
    {
        return permissions;
    }
    
    /**
     * <p>
     * getPermissionsList
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getPermissionsList()
     * @return
     */
    public List getPermissionsList()
    {
        return permissionsList;
    }
    
    /**
     * <p>
     * setPermissions
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setPermissions(java.lang.String)
     * @param permissions
     */
    public void setPermissions(String permissions)
    {
        this.permissions = permissions;
        permissionsList = parseCSVList(permissions);
    }

    /**
     * <p>
     * setPermissions
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setPermissions(java.util.List)
     * @param permissions
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
