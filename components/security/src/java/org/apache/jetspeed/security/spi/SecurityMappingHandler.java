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
import org.apache.jetspeed.security.SecurityException;

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
     * Sets the role principal on a given user.
     * Existence of the role or the user must be checked prior to invoking this method.
     * If a principal does not exist in the security mapping store, it will be created for the purpose of
     * security mapping only.
     * </p>
     * 
     * @param username The user to add the role principal to.
     * @param roleFullPathName The full path of the role principal to add.
     * @throws SecurityException Throws a {@link SecurityException}.  An exeption needs to be
     * 							 thrown if the user does not exist.
     */
    void setRolePrincipal(String username, String roleFullPathName) throws SecurityException;
    
    /**
     * <p>
     * Removes the role principal on a given user.
     * </p>
     * <p>
     * If a mapping only record does not have any mapping, this method will
     * remove the record as well.
     * </p>
     * 
     * @param username The user to remove the role principal from.
     * @param roleFullPathName The full path of the role principal to remove.
     * @throws SecurityException Throws a {@link SecurityException}.  An exeption needs to be
     * 							 thrown if the user does not exist.
     */
    void removeRolePrincipal(String username, String roleFullPathName) throws SecurityException;

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
     * Sets the role principal on a given user.
     * </p>
     * 
     * @param groupFullPathName The group to add the role principal to.
     * @param roleFullPathName The full path of the role principal to add.
     * @throws SecurityException Throws a {@link SecurityException}.  An exeption needs to be
     * 							 thrown if the group does not exist.
     */
    void setRolePrincipalInGroup(String groupFullPathName, String roleFullPathName) throws SecurityException;
    
    /**
     * <p>
     * Removes the role principal on a given user.
     * </p>
     * 
     * @param groupFullPathName The group to remove the role principal from.
     * @param roleFullPathName The full path of the role principal to remove.
     * @throws SecurityException Throws a {@link SecurityException}.  An exeption needs to be
     * 							 thrown if the group does not exist.
     */
    void removeRolePrincipalInGroup(String groupFullPathName, String roleFullPathName) throws SecurityException;
    
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
     * </p>
     * 
     * @param roleFullPathName The role full path name.
     * @return A set of <code>Principal</p>
     */   
    Set getUserPrincipalsInRole(String roleFullPathName);
    
    /**
     * <p>
     * Gets the user principals for the given group according to the relevant hierarchy
     * resolution rules.
     * </p>
     * 
     * @param groupFullPathName The group full path name.
     * @return A set of <code>Principal</p>
     */   
    Set getUserPrincipalsInGroup(String groupFullPathName);
    
    /**
     * <p>
     * Sets the user principal in the given group.
     * </p>
     * <p>
     * Existence of the group or the user must be checked prior to invoking this method.
     * If a principal does not exist in the security mapping store, it will be created for the purpose of
     * security mapping only.
     * </p>
     * 
     * @param username The user to add to the group principal.
     * @param groupFullPathName The full path of the group principal.
     * @throws SecurityException Throws a {@link SecurityException}.  An exeption needs to be
     * 							 thrown if the user does not exist.
     */
    void setUserPrincipalInGroup(String username, String groupFullPathName) throws SecurityException;
    
    /**
     * <p>
     * Removes the user principal from the given group.
     * </p>
     * 
     * @param username The user to remove from the group principal.
     * @param groupFullPathName The full path of the group principal.
     * @throws SecurityException Throws a {@link SecurityException}.  An exeption needs to be
     * 							 thrown if the user does not exist.
     */
    void removeUserPrincipalInGroup(String username, String groupFullPathName) throws SecurityException;

}
