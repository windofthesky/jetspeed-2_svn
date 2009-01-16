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
package org.apache.jetspeed.sso.spi;

import java.util.Collection;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PrincipalTypeManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.sso.SSOUser;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface SSOUserManagerSPI extends PrincipalTypeManager {

    SSOUser newUser(String name, Long domainId);

    /**
     * <p>
     * Add a new user
     * </p>
     * <p>
     * If an external security storage manager is used, the user will be mapped/replicated to it as well.
     * </p>
     * @param userName The user name.
     * @param domainId The domain ID of the remote domain
     * @param localPrincipal The portal principal (e.g. user / group) related to this SSO User
     * @return the new {@link SSOUser}
     * @throws Throws a security exception.
     */
    SSOUser addUser(String userName, Long domainId, JetspeedPrincipal ownerPrincipal) throws SecurityException;

    /**
     * <p>
     * Remove a user. If there user attributes associated with this user, they will be removed as well.
     * </p>
     * <p>
     * {@link java.security.Permission}for this user will be removed as well.
     * </p>
     * 
     * @param userName The user name.
     * @throws Throws a security exception.
     */
    void removeUser(String userName, Long domainId) throws SecurityException;

    /**
     * <p>
     * Whether or not a user exists.
     * </p>
     * 
     * @param userName The user name.
     * @return Whether or not a user exists.
     */
    boolean userExists(String userName, Long domainId);

    /**
     * <p>
     * Get a {@link SSOUser}for a given user name.
     * </p>
     * 
     * @param userName The user name.
     * @return The {@link SSOUser}.
     * @throws Throws a security exception idomainPrincipalAccessf the user cannot be found.
     */
    SSOUser getUser(String userName, Long domainId) throws SecurityException;

    /**
     * <p>
     * Retrieves a detached and modifiable {@link SSOUser} list matching the corresponding
     * user name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching users.
     * @return a list of {@link SSOUser}.
     */
    Collection<SSOUser> getUsers(String nameFilter, Long domainId) throws SecurityException;

    Collection<SSOUser> getUsers(JetspeedPrincipal principal) throws SecurityException;
    
    Collection<SSOUser> getUsers(JetspeedPrincipal principal, Long domainId) throws SecurityException;
    
    /**
     * <p>
     * Retrieves a a detached and modifiable List user names, finding users matching the corresponding
     * user name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching users.
     * @return A list of user names
     */
    Collection<String> getUserNames(String nameFilter, Long domainId) throws SecurityException;

    /**
     * Updates a user and all its attributes
     * @param user
     * @throws SecurityException
     */
    void updateUser(SSOUser user) throws SecurityException;

    /**
     * Returns the current PasswordCredential for a User or a new one if the doesn't have one yet
     * @param user the user
     * @return null if the SSOUserManager doesn't support PasswordCredentials
     */
    PasswordCredential getPasswordCredential(SSOUser user) throws SecurityException;
    
    void storePasswordCredential(PasswordCredential credential) throws SecurityException;
    
    void addSSOUserToPrincipal(SSOUser user, JetspeedPrincipal principal) throws SecurityException;
    
    Collection<JetspeedPrincipal> getPortalPrincipals(SSOUser remoteUser, Long portalPrincipalDomain);
    
}