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
package org.apache.jetspeed.security.spi;

import java.util.Set;

import org.apache.jetspeed.security.HierarchyResolver;

/**
 * <p>
 * This interface encapsulates the persistence of security roles.
 * </p>
 * <p>
 * This provides a central placeholder for changing the persistence of roles
 * security information.
 * </p>
 * <p>
 * A security implementation wanting to store role security implementation in
 * LDAP for instance would need to provide an LDAP implementation of this
 * interface.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface RoleSecurityHandler
{
    
    /**
     * <p>
     * Sets the {@link HierarchyResolver} to be used for resolving role hierachy.
     * </p>
     * 
     * @param roleHierarchyResolver The role {@link HierarchyResolver}.
     */
    void setRoleHierarchyResolver(HierarchyResolver roleHierarchyResolver);
    
    /**
     * <p>
     * Gets the role principals for the given user.
     * </p>
     * 
     * @param username The user name.
     * @return A set of <code>RolePrincipal</p>
     */
    Set getRolePrincipals(String username);

}
