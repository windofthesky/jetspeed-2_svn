/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
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
package org.apache.jetspeed.security;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.apache.jetspeed.cps.CommonPortletServices;

/**
 * Convenience static wrapper around {@link RoleManagerService}.
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class RoleManager
{

    /**
     * <p>Default Constructor.  This class contains only static
     * methods, hence users should not be allowed to instantiate it.</p>
     */
    public RoleManager()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>Returns the {@link RoleManagerService}.</p>
     * @return The RoleManagerService.
     */
    private static final RoleManagerService getService()
    {
        return (RoleManagerService) CommonPortletServices.getPortalService(RoleManagerService.SERVICE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#addRole(java.lang.String)
     */
    public static void addRole(String roleFullPathName) throws SecurityException
    {
        getService().addRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#removeRole(java.lang.String)
     */
    public static void removeRole(String roleFullPathName)
    {
        getService().removeRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#roleExists(java.lang.String)
     */
    public static boolean roleExists(String roleFullPathName)
    {
       return getService().roleExists(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getRole(java.lang.String)
     */
    public static Role getRole(String roleFullPathName) throws SecurityException
    {
        return getService().getRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getRolesForUser(java.lang.String)
     */
    public static Collection getRolesForUser(String username) throws SecurityException
    {
        return getService().getRolesForUser(username);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getUsersInRole(java.lang.String)
     */
    public static Collection getUsersInRole(String roleFullPathName) throws SecurityException
    {
        return getService().getUsersInRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getRolesForGroup(java.lang.String)
     */
    public static Collection getRolesForGroup(String groupFullPathName) throws SecurityException
    {
        return getService().getRolesForGroup(groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#getGroupsInRole(java.lang.String)
     */
    public static Collection getGroupsInRole(String roleFullPathName) throws SecurityException
    {
        return getService().getGroupsInRole(roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#addRoleToUser(java.lang.String, java.lang.String)
     */
    public static void addRoleToUser(String username, String roleFullPathName) throws SecurityException
    {
        getService().addRoleToUser(username, roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#removeRoleFromUser(java.lang.String, java.lang.String)
     */
    public static void removeRoleFromUser(String username, String roleFullPathName)
    {
        getService().removeRoleFromUser(username, roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#isUserInRole(java.lang.String, java.lang.String)
     */
    public static boolean isUserInRole(String username, String roleFullPathName) throws SecurityException
    {
        return isUserInRole(username, roleFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#addRoleToGroup(java.lang.String, java.lang.String)
     */
    public static void addRoleToGroup(String roleFullPathName, String groupFullPathName) throws SecurityException
    {
        getService().addRoleToGroup(roleFullPathName, groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#removeRoleFromGroup(java.lang.String, java.lang.String)
     */
    public static void removeRoleFromGroup(String roleFullPathName, String groupFullPathName)
    {
        getService().removeRoleFromGroup(roleFullPathName, groupFullPathName);
    }

    /**
     * @see org.apache.jetspeed.security.RoleManagerService#isGroupInRole(java.lang.String, java.lang.String)
     */
    public static boolean isGroupInRole(String roleFullPathName, String groupFullPathName) throws SecurityException
    {
        return getService().isGroupInRole(roleFullPathName, groupFullPathName);
    }

}
