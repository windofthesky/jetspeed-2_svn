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

import java.security.Principal;
import java.util.List;

import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * This interface encapsulates the persistence of security groups.
 * </p>
 * <p>
 * This provides a central placeholder for changing the persistence of groups
 * security information.
 * </p>
 * <p>
 * A security implementation wanting to store group security implementation in
 * LDAP for instance would need to provide an LDAP implementation of this
 * interface.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface GroupSecurityHandler
{
    /**
     * <p>
     * Gets the group principal for the group full path name {principal}.{subprincipal}.
     * </p>
     * 
     * @param groupFullPathName The group full path name.
     * @return The <code>Principal</p>
     */
    Principal getGroupPrincipal(String groupFullPathName);
    
    /**
     * <p>
     * Sets the group principal in the backing store.
     * </p>
     * 
     * @param groupPrincipal The <code>GroupPrincipal</code>.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void setGroupPrincipal(GroupPrincipal groupPrincipal) throws SecurityException;
    
    /**
     * <p>
     * Removes the group principal.
     * </p>
     * 
     * @param groupPrincipal The <code>GroupPrincipal</code>.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void removeGroupPrincipal(GroupPrincipal groupPrincipal) throws SecurityException;

    /**
     * <p>
     * Gets the an iterator of group principals for a given filter.
     * </p>
     * 
     * @param filter The filter.
     * @return The list of <code>Principal</code>
     */
    List getGroupPrincipals(String filter);
   
}
