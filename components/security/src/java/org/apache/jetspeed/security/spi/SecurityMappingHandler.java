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
 * This interface encapsulates the mapping between principals.
 * </p>
 * <p>
 * This provides a central placeholder for changing the implementation
 * of the mapping association between principals.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface SecurityMappingHandler
{
    
    /**
     * <p>
     * Gets the {@link HierarchyResolver} to be used for resolving role hierarchy.
     * </p>
     * 
     * @return The role {@link HierarchyResolver}.
     */
    HierarchyResolver getRoleHierarchyResolver();
    
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
     * Gets the {@link HierarchyResolver} to be used for resolving group hierarchy.
     * </p>
     * 
     * @return The role {@link HierarchyResolver}.
     */
    HierarchyResolver getGroupHierarchyResolver();
    
    /**
     * <p>
     * Sets the {@link HierarchyResolver} used for resolving group hierarchy.
     * </p>
     * 
     * @param groupHierarchyResolver The group {@link HierarchyResolver}.
     */
    void setGroupHierarchyResolver(HierarchyResolver groupHierarchyResolver);
    
    /**
     * <p>
     * Gets the role principals for the given user according to the relevant hierarchy
     * resolution rules.
     * </p>
     * 
     * @param username The user name.
     * @return A set of <code>Principal</p>
     */
    Set getRolePrincipals(String username);
    
    /**
     * <p>
     * Sets the roles principals on a given user.  The provided set replaces
     * any set that may already have been set on the user.
     * </p>
     * 
     * @param username The user to add the roles principals to.
     * @param rolePrincipals The roles principals to add.
     */
    void setRolePrincipals(String username, Set rolePrincipals);
    
    /**
     * <p>
     * Gets the role principals for the given group according to the relevant hierarchy
     * resolution rules.
     * </p>
     * 
     * @param groupFullPathName The group full path name.
     * @return A set of <code>Principal</p>
     */
    Set getRolePrincipalsInGroup(String groupFullPathName);
    
    /**
     * <p>
     * Gets the group principals for the given user according to the relevant hierarchy
     * resolution rules.
     * </p>
     * 
     * @param username The user name.
     * @return A set of <code>GroupPrincipal</p>
     */
    Set getGroupPrincipals(String username);
    
    /**
     * <p>
     * Gets the group principals for the given role according to the relevant hierarchy
     * resolution rules.
     * </p>
     * 
     * @param roleFullPathName The role full path name.
     * @return A set of <code>Principal</p>
     */
    Set getGroupPrincipalsInRole(String roleFullPathName);
    
    /**
     * <p>
     * Gets the user principals for the given role according to the relevant hierarchy
     * resolution rules.
     * 
     * @param roleFullPathName The role full path name.
     * @return A set of <code>Principal</p>
     */   
    Set getUserPrincipalsInRole(String roleFullPathName);
    
    /**
     * <p>
     * Gets the user principals for the given group according to the relevant hierarchy
     * resolution rules.
     * 
     * @param groupFullPathName The group full path name.
     * @return A set of <code>Principal</p>
     */   
    Set getUserPrincipalsInGroup(String groupFullPathName);

}
