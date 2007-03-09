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
package org.apache.jetspeed.security.spi;

import java.security.Principal;
import java.util.List;

import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;

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
     * Gets the role principal for the role full path name {principal}.{subprincipal}.
     * </p>
     * 
     * @param roleFullPathName The role full path name.
     * @return The <code>Principal</p>
     */
    RolePrincipal getRolePrincipal(String roleFullPathName);
    
    /**
     * <p>
     * Sets the role principal in the backing store.
     * </p>
     * 
     * @param rolePrincipal The <code>RolePrincipal</code>.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void setRolePrincipal(RolePrincipal rolePrincipal) throws SecurityException;
    
    /**
     * <p>
     * Removes the role principal.
     * </p>
     * 
     * @param rolePrincipal The <code>RolePrincipal</code>.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void removeRolePrincipal(RolePrincipal rolePrincipal) throws SecurityException;

    /**
     * <p>
     * Gets the an iterator of role principals for a given filter.
     * </p>
     * 
     * @param filter The filter.
     * @return The list of <code>Principal</code>
     */
    List getRolePrincipals(String filter);
   
}  
