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

/**
 * <p>Describe the interface for managing {@link Permission} and permission
 * association to {@link Principal}.  Permissions are used to manage Principals
 * access entitlement on specified resources.</p>
 * <p>For instance:</p>
 * <pre><code>
 * grant principal o.a.j.security.UserPrincipal "theUserPrincipal"
 * {
 *     permission o.a.j.security.PortletPermission "myportlet", "view,edit,minimize,maximize";
 * };
 * </code><pre>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface PermissionManagerService
{
    /** <p>The name of the service.</p> */
    String SERVICE_NAME = "PermissionManager";

    /**
     * <p>Gets the {@link Permissions} given a {@link Principal}.
     * @param principal The principal.
     * @return The permissions.
     */
    Permissions getPermissions(Principal principal);

    /**
     * <p>Gets the {@link Permissions} given a collection
     * of {@link Principal}.
     * @param principals A collection of principal.
     * @return The permissions.
     */
    Permissions getPermissions(Collection principals);

    /**
     * <p>Remove all instances of a given permission.</p>
     * @param permission The permission to remove.
     */
    void removePermission(Permission permission);

    /**
     * <p>Remove all permissions for a given principal.</p>
     * @param principal The principal.
     */
    void removePermissions(Principal principal);

    /**
     * <p>Grant a {@link Permission} to a given {@link Principal}. 
     * @param principal The principal.
     * @param permission The permission.
     * @throws Throws a security exception if the principal does not exist.
     */
    void grantPermission(Principal principal, Permission permission) throws SecurityException;

    /**
     * <p>Revoke a {@link Permission} from a given {@link Principal}.
     * @param principal The principal.
     * @param permission The permission.
     */
    void revokePermission(Principal principal, Permission permission);

}
