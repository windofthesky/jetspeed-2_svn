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
