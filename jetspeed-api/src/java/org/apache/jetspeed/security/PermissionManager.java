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
package org.apache.jetspeed.security;

import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.util.Collection;

/**
 * <p>
 * Describe the interface for managing {@link Permission}and permission
 * association to {@link Principal}. Permissions are used to manage Principals
 * access entitlement on specified resources.
 * </p>
 * <p>
 * The permission manager does not enforce any hierarchy resolution, all relevant
 * principals must be passed to the permission manager to assess the proper permissions.
 * </p>
 * <p>
 * For instance:
 * </p>
 * 
 * <pre><code>
 * 
 *  grant principal o.a.j.security.UserPrincipal &quot;theUserPrincipal&quot;
 *  {
 *      permission o.a.j.security.PortletPermission &quot;myportlet&quot;, &quot;view,edit,minimize,maximize&quot;;
 *  };
 *  
 * </code>
 * &lt;pre&gt;
 *  @author &lt;a href=&quot;mailto:dlestrat@apache.org&quot;&gt;David Le Strat&lt;/a&gt;
 * 
 */
public interface PermissionManager
{

    /**
     * <p>
     * Gets the {@link Permissions}given a {@link Principal}.
     * 
     * @param principal The principal.
     * @return The permissions.
     */
    Permissions getPermissions(Principal principal);

    /**
     * <p>
     * Gets the {@link Permissions}given a collection of {@link Principal}.
     * 
     * @param principals A collection of principal.
     * @return The permissions.
     */
    Permissions getPermissions(Collection principals);

    /**
     * <p>
     * Adds a permission definition.
     * </p>
     * 
     * @param permission The permission to add.
     * @throws Throws a security exception.
     */
    void addPermission(Permission permission) throws SecurityException;

    /**
     * <p>
     * Remove all instances of a given permission.
     * </p>
     * 
     * @param permission The permission to remove.
     * @throws Throws a security exception.
     */
    void removePermission(Permission permission) throws SecurityException;

    /**
     * <p>
     * Whether the given permission exists.
     * </p>
     * 
     * @param permission The permission to look for.
     * @return Whether the permission exists.
     */
    boolean permissionExists(Permission permission);

    /**
     * <p>
     * Remove all permissions for a given principal.
     * </p>
     * 
     * @param principal The principal.
     * @throws Throws a security exception.
     */
    void removePermissions(Principal principal) throws SecurityException;

    /**
     * <p>
     * Grant a {@link Permission}to a given {@link Principal}.
     * 
     * @param principal The principal.
     * @param permission The permission.
     * @throws Throws a security exception if the principal does not exist.
     */
    void grantPermission(Principal principal, Permission permission) throws SecurityException;

    /**
     * <p>
     * Revoke a {@link Permission}from a given {@link Principal}.
     * 
     * @param principal The principal.
     * @param permission The permission.
     * @throws Throws a security exception.
     */
    void revokePermission(Principal principal, Permission permission) throws SecurityException;

}