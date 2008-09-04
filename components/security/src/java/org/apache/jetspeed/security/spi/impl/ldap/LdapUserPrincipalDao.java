/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.impl.ldap;

import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * The ldap user principal DAO.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a
 *         href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface LdapUserPrincipalDao extends LdapPrincipalDao
{
    /**
     * <p>
     * Add a user to a group.
     * </p>
     * 
     * @param userPrincipalUid The user principal.
     * @param groupPrincipalUid The group principal.
     * @throws SecurityException A {@link SecurityException}.
     */
    void addGroup(String userPrincipalUid, String groupPrincipalUid) throws SecurityException;

    /**
     * <p>
     * Remove a user from a group.
     * </p>
     * 
     * @param userPrincipalUid The user principal.
     * @param groupPrincipalUid The group principal.
     * @throws SecurityException A {@link SecurityException}.
     */
    void removeGroup(String userPrincipalUid, String groupPrincipalUid) throws SecurityException;

    /**
     * <p>
     * Add a user to a group.
     * </p>
     * 
     * @param userPrincipalUid The user principal.
     * @param rolePrincipalUid The role principal.
     * @throws SecurityException A {@link SecurityException}.
     */
    void addRole(String userPrincipalUid, String rolePrincipalUid) throws SecurityException;

    /**
     * <p>
     * Remove a user from a group.
     * </p>
     * 
     * @param userPrincipalUid The user principal.
     * @param rolePrincipalUid The role principal.
     * @throws SecurityException A {@link SecurityException}.
     */
    void removeRole(String userPrincipalUid, String rolePrincipalUid) throws SecurityException;
    
    
    /**
     * <p>
     * Return an array of the group principal UIDS that belong to a specific user.
     * </p>
     * 
     * @param userPrincipalUid The user principal uid.
     * @return The array of group uids asociated with this user
     * @throws SecurityException A {@link SecurityException}.
     */
    String[] getGroupUidsForUser(String userPrincipalUid) throws SecurityException;
    
    /**
     * <p>
     * Return an array of the role principal UIDS that belong to a specific user.
     * </p>
     * 
     * @param userPrincipalUid The user principal uid.
     * @return The array of group uids asociated with this user
     * @throws SecurityException A {@link SecurityException}.
     */
    String[] getRoleUidsForUser(String userPrincipalUid) throws SecurityException;    

    /**
     * <p>
     * Return an array of the user principal uids that belong to a group.
     * </p>
     * 
     * @param groupPrincipalUid The group uid.
     * @return The array of user uids asociated with this group
     * @throws SecurityException A {@link SecurityException}.
     */
    String[] getUserUidsForGroup(String groupPrincipalUid) throws SecurityException;

    /**
     * <p>
     * Return an array of the user principal uids that belong to a role.
     * </p>
     * 
     * @param rolePrincipalUid The role uid.
     * @return The array of user uids asociated with this group
     * @throws SecurityException A {@link SecurityException}.
     */
    String[] getUserUidsForRole(String rolePrincipalUid) throws SecurityException;

    /**
     * <p>
     * Return an array of the role principal UIDS that belong to a specific group.
     * </p>
     * 
     * @param groupPrincipalUid The group principal uid.
     * @return The array of role uids asociated with this user
     * @throws SecurityException A {@link SecurityException}.
     */
    String[] getRolesForGroup(String groupPrincipalUid) throws SecurityException;   
    
    /**
     * <p>
     * Add a role to a group.
     * </p>
     * 
     * @param groupPrincipalUid The group principal.
     * @param rolePrincipalUid The role principal.
     * @throws SecurityException A {@link SecurityException}.
     */        
    void addRoleToGroup(String groupPrincipalUid, String rolePrincipalUid) throws SecurityException;
    
    /**
     * <p>
     * Remove a role from a group.
     * </p>
     * 
     * @param groupPrincipalUid The group principal.
     * @param rolePrincipalUid The role principal.
     * @throws SecurityException A {@link SecurityException}.
     */        
    void removeRoleFromGroup(String groupPrincipalUid, String rolePrincipalUid) throws SecurityException;
    
    
}