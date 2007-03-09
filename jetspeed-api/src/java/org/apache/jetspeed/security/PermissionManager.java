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
package org.apache.jetspeed.security;

import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.util.Collection;
import javax.security.auth.Subject;

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

    /**
     * <p>
     * Check permission for the given subject's access to the resource protected by the permission
     * This is an abstraction introduced in M4 for Permission Manager implementations NOT
     * founded upon the a Java security policy.</p>
     * 
     * @param subject The Java subject.
     * @param permission The permission, usually a portlet, page or folder type permission.
     * @return true if the subject has access to the permission protected resource, false
     *         if the subject does not have access.
     */
    boolean checkPermission(Subject subject, Permission permission);
       
    /**
     * Retrieve a collection of all Permissions in the system ordered by Permission Type, resource
     * Note that we return a collection of <code>InternalPrincipal</code>
     * 
     * @return A Java Security collection of <code>InternalPrincipal</code>
     */
    Collection getPermissions();    
    
    /**
     * Retrieve a list of all Permissions in the system for a given resource
     * The resource can be a prefix, for example "j2-admin" will retrieve all 
     * portlet permissions starting with j2-admin
     * 
     * @return A Java Security collection of Permissions
     */
    Permissions getPermissions(String classname, String resource);

    /**
     * Update the collection of principals on the given principal, 
     * appropriately granting or revoking principals to the given permission.
     * 
     * @param permission Permission to be updated
     * @param principals The new collection of principals based on BasePrincipal 
     *        to be associated with this permission 
     * @return
     * @throws SecurityException
     */
    int updatePermission(Permission permission, Collection principals)
    throws SecurityException;
    
    /**
     * Given a permission, return all principals granted to that permission
     * 
     * @param permission 
     * @return A collection of Java Security Permission objects
     */
    public Collection getPrincipals(Permission permission);
}