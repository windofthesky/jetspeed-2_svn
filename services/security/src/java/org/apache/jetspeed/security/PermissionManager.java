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
package org.apache.jetspeed.security;

import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.util.Collection;

import org.apache.jetspeed.cps.CommonPortletServices;

/**
 * Convenience static wrapper around {@link PermissionManagerService}.
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PermissionManager
{

    /**
     * <p>Default Constructor.  This class contains only static
     * methods, hence users should not be allowed to instantiate it.</p>
     */
    public PermissionManager()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>Returns the {@link PermissionManagerService}.</p>
     * @return The PermissionManagerService.
     */
    private static final PermissionManagerService getService()
    {
        return (PermissionManagerService) CommonPortletServices.getPortalService(PermissionManagerService.SERVICE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManagerService#getPermissions(java.security.Principal)
     */
    public static Permissions getPermissions(Principal principal)
    {
        return getService().getPermissions(principal);
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManagerService#getPermissions(java.util.Collection)
     */
    public static Permissions getPermissions(Collection principals)
    {
        return getService().getPermissions(principals);
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManagerService#removePermission(java.security.Permission)
     */
    public static void removePermission(Permission permission)
    {
        getService().removePermission(permission);
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManagerService#removePermissions(java.security.Principal)
     */
    public static void removePermissions(Principal principal)
    {
        getService().removePermissions(principal);
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManagerService#grantPermission(java.security.Principal, java.security.Permission)
     */
    public static void grantPermission(Principal principal, Permission permission) throws SecurityException
    {
        getService().grantPermission(principal, permission);
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManagerService#revokePermission(java.security.Principal, java.security.Permission)
     */
    public static void revokePermission(Principal principal, Permission permission)
    {
        getService().revokePermission(principal, permission);
    }

}
