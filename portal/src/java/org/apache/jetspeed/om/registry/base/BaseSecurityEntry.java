/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.om.registry.base;

// Java imports
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.jetspeed.om.registry.SecurityAccess;
import org.apache.jetspeed.om.registry.SecurityAllow;
import org.apache.jetspeed.om.registry.SecurityEntry;

/**
 * Interface for manipulatin the Security Entry on the registry entries
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class BaseSecurityEntry extends BaseRegistryEntry implements SecurityEntry, java.io.Serializable
{

    /** Holds value of property accesses. */
    private Vector accesses = new Vector();

    private transient Map accessMap = null;

    public static final String ALL_ACTIONS = "*";

    public static final String ALL_ROLES = "*";

    public static final String ALL_USERS = "*";

    private static final String OWNER_MAP = "owner";

    private static final String ROLE_MAP = "role";

    private static final String USER_MAP = "user";

    private static transient Object accessMapSync = new Object();

    public BaseSecurityEntry()
    { }

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object == null)
        {
            return false;
        }

        BaseSecurityEntry obj = (BaseSecurityEntry) object;

        Iterator i = accesses.iterator();
        Iterator i2 = obj.accesses.iterator();
        while (i.hasNext())
        {
            BaseSecurityAccess c1 = (BaseSecurityAccess) i.next();
            BaseSecurityAccess c2 = null;

            if (i2.hasNext())
            {
                c2 = (BaseSecurityAccess) i2.next();
            }
            else
            {
                return false;
            }

            if (!c1.equals(c2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        return super.equals(object);
    }

    /** Getter for property accesses.
     * @return Value of property accesses.
     */
    public Vector getAccesses()
    {
        return accesses;
    }

    /** Setter for property accesses.
     * @param accesses New value of property accesses.
     */
    public void setAccesses(Vector accesses)
    {
        this.accesses = accesses;
    }

    /**
     * Aututhorizes action for a role.
     *
     * o If the requested action and the action ALL_ACTIONS
     *   do not exist, then return false.
     *
     * o If the requesting role and ALL_ROLES does not exist for the
     *   the action, then return false.
     *
     * @param role requesting action
     * @param action being requested
     * @return <CODE>true</CODE> if action is allowed for role
     */
    public boolean allowsRole(String role, String action)
    {
        Map allowMap = null;
        boolean allow = false;

        if (accessMap == null)
        {
            buildAccessMap();
        }

        // Checked action
        allowMap = (Map) accessMap.get(action);
        allow = isInAllowMap(allowMap, ROLE_MAP, role, ALL_ROLES);
        if (allow == true)
        {
            return allow;
        }

        // Checked all actions
        allowMap = (Map) accessMap.get(ALL_ACTIONS);
        allow = isInAllowMap(allowMap, ROLE_MAP, role, ALL_ROLES);

        // Not allowed
        return allow;
    }

    /**
     * Aututhorizes action for a named user
     *
     * @param userName requesting action
     * @param action being requested
     * @return <CODE>true</CODE> if action is allowed for named user
     */
    public boolean allowsUser(String userName, String action)
    {
        return allowsUser(userName, action, null);
    }
    /**
     * Aututhorizes action for a named user
     *
     * @param userName requesting action
     * @param action being requested
     * @param owner User
     * @return <CODE>true</CODE> if action is allowed for named user
     */
    public boolean allowsUser(String userName, String action, String owner)
    {
        Map allowMap = null;
        boolean allow = false;

        if (accessMap == null)
        {
            buildAccessMap();
        }
        if ((owner != null) && (owner.equals(userName)))
        {
            // Checked action
            allowMap = (Map) accessMap.get(action);
            allow = isInAllowMap(allowMap, OWNER_MAP, null, null);
            if (allow == true)
            {
                return allow;
            }

            // Checked action
            allowMap = (Map) accessMap.get(ALL_ACTIONS);
            allow = isInAllowMap(allowMap, OWNER_MAP, null, null);
            if (allow == true)
            {
                return allow;
            }
        }

        // Checked action
        allowMap = (Map) accessMap.get(action);
        allow = isInAllowMap(allowMap, USER_MAP, userName, ALL_USERS);
        if (allow == true)
        {
            return allow;
        }
        // Checked action
        allowMap = (Map) accessMap.get(action);
        allow = isInAllowMap(allowMap, USER_MAP, userName, ALL_USERS);
        if (allow == true)
        {
            return allow;
        }

        // Checked all actions
        allowMap = (Map) accessMap.get(ALL_ACTIONS);
        allow = isInAllowMap(allowMap, USER_MAP, userName, ALL_USERS);

        // Not allowed
        return allow;

    }

    private void buildAccessMap()
    {
        Map actionMap = null;
        SecurityAccess accessElement = null;

        synchronized (accessMapSync)
        {
            if (accessMap == null)
            {
                accessMap = new HashMap();
            }

            accessMap.clear();
        }
        // Build allow map
        for (Iterator accessIterator = getAccesses().iterator(); accessIterator.hasNext();)
        {
            accessElement = (SecurityAccess) accessIterator.next();

            // Get action map of the action.  Create one if none exists
            String action = accessElement.getAction();

            if (action == null)
            {
                action = ALL_ACTIONS;
            }

            actionMap = (Map) accessMap.get(action);
            if (actionMap == null)
            {
                actionMap = new HashMap();
                accessMap.put(action, actionMap);
            }
            addAllows(actionMap, accessElement);
        }
    }

    /**
     * Add access elements to the access map.  The elements will be
     * appened to the appropiate map.
     *
     * @param accessMap to receive accessElements
     * @param accessElement to copy to access map
     */
    private void addAllows(Map accessMap, SecurityAccess accessElement)
    {
        SecurityAllow allowElement = null;
        String role = null;
        Map ownerMap = null; // Map of owner allowed
        Map roleMap = null;  // Map of roles allowed
        Map userMap = null;  // Map of users allowed
        String userName = null;

        if (accessElement.getAllAllows() == null)
        {
            return;
        }

        // Add allows to the action Map
        for (Iterator allowIterator = accessElement.getAllAllows().iterator(); allowIterator.hasNext();)
        {
            allowElement = (SecurityAllow) allowIterator.next();
            role = null;
            userName = null;


            // Add Owner
            if (allowElement.isOwner() == true)
            {
                ownerMap = (Map) accessMap.get(OWNER_MAP);
                if (ownerMap == null)
                {
                    ownerMap = new HashMap();
                    accessMap.put(OWNER_MAP, ownerMap);
                }
                ownerMap.put(null, null);
            }

            // Add Role
            role = allowElement.getRole();
            if (role != null)
            {
                roleMap = (Map) accessMap.get(ROLE_MAP);
                if (roleMap == null)
                {
                    roleMap = new HashMap();
                    accessMap.put(ROLE_MAP, roleMap);
                }
                roleMap.put(role, null);
            }

            // Add User
            userName = allowElement.getUser();
            if (userName != null)
            {
                userMap = (Map) accessMap.get(USER_MAP);
                if (userMap == null)
                {
                    userMap = new HashMap();
                    accessMap.put(USER_MAP, userMap);
                }
                userMap.put(userName, null);
            }
        }
    }

    /**
     * Search allow map of user/role or "all user/role"
     *
     * @param allowMap Map of allow-if
     * @param mapType ROLE_MAP or USER_MAP
     * @param mapKey role or user to test
     * @param allKey ALL_ROLE or ALL_USER
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    private boolean isInAllowMap(Map allowMap, String mapType, String mapKey, String allKey)
    {
        boolean allow = false;
        if (allowMap != null)
        {
            Map allowRoleMap = (Map) allowMap.get(mapType);
            if (allowRoleMap == null)
            {
                return allowMap.isEmpty(); // If acction exist and no allows, then grant permission
            }
            allow = allowRoleMap.containsKey(mapKey);
            if (allow == false)
            {
              allow = allowRoleMap.containsKey(allKey);
            }
            return allow;
        }

        // Not allowed
        return allow;
    }
 }

