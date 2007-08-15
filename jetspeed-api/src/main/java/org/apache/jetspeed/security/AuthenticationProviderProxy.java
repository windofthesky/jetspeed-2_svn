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

import java.sql.Date;

import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;

/**
 * <p>
 * Proxy allowing to handle multiple authentication providers.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface AuthenticationProviderProxy extends UserSecurityHandler, CredentialHandler
{
    /**
     * <p>
     * Returns the default authentication provider.
     * </p>
     * 
     * @return The default authentication provider.
     */
    String getDefaultAuthenticationProvider();
  
    /**
     * <p>
     * Returns the authentication provider of a user principal.
     * @param userName
     * @return The authentication provider or null if user is unknown.
     */
    String getAuthenticationProvider(String userName);
    
    /**
     * <p>
     * Adds a new user principal in a given authentication provider.
     * </p>
     * 
     * @param userPrincipal The new user principal.
     * @param authenticationProvider The authentication provider name.
     * @throws SecurityException Throws a security exception.
     */
    void addUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Updates user principal in a given authentication provider.
     * </p>
     * 
     * @param userPrincipal The user principal.
     * @param authenticationProvider The authentication provider name.
     * @throws SecurityException Throws a security exception.
     */
    void updateUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Remove user principal in a given authentication provider.
     * </p>
     * 
     * @param userPrincipal The user principal.
     * @param authenticationProvider The authentication provider name.
     * @throws SecurityException Throws a security exception.
     */
    void removeUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Adds or updates a private password credentialin a given authentication provider.<br>
     * Note that there is no checking of the <code>oldPassword</code> and the provided password is 
     * assumed to be encoded. Hence no encoding will take place.
     * </p>
     * 
     * @param username The user to be updated.
     * @param newPassword The new password.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void importPassword(String userName, String newPassword) throws SecurityException;

    
    
    /**
     * <p>
     * Adds or updates a private password credentialin a given authentication provider.<br>
     * Note that there is no checking of the <code>oldPassword</code> and the provided password is 
     * assumed to be encoded. Hence no encoding will take place.
     * </p>
     * 
     * @param username The user to be updated.
     * @param newPassword The new password.
     * @param authenticationProvider The authentication provider name.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void importPassword(String userName, String newPassword,String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Adds or updates a private password credential in a given authentication provider.<br>
     * If <code>oldPassword</code> is not null, the oldPassword will first be checked (authenticated).<br>
     * </p>
     * 
     * @param userName The name of the user to be updated.
     * @param oldPassword The old password value.
     * @param newPassword The new password value.
     * @param authenticationProvider The authentication provider name.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void setPassword(String userName, String oldPassword, String newPassword,
            String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Set the update required state of the user password credential in a given authentication provider.
     * </p>
     * 
     * @param userName The user name.
     * @param updateRequired The update required state.
     * @param authenticationProvider The authentication provider name.
     * @throws Throws a security exception.
     */
    void setPasswordUpdateRequired(String userName, boolean updateRequired, 
            String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Set the enabled state of the user password credential in a given authentication provider.
     * </p>
     * 
     * @param userName The user name.
     * @param enabled The enabled state.
     * @param authenticationProvider The authentication provider name.
     * @throws Throws a security exception.
     */
    void setPasswordEnabled(String userName, boolean enabled, 
            String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Set the expiration date and the expired flag of the password credential in a given authentication provider</p>
     * <p>
     * If a date equal or before the current date is provided, the expired flag will be set to true,
     * otherwise to false.</p>
     * 
     * @param userName The user name.
     * @param expirationDate The expiration date to set.
     * @param authenticationProvider The authentication provider name.
     * @throws Throws a security exception.
     */
    void setPasswordExpiration(String userName, Date expirationDate, 
            String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Authenticate a user in a given authentication provider
     * </p>
     * 
     * @param userName The user name.
     * @param password The user password.
     * @param authenticationProvider The authentication provider name.
     * @return Whether or not a user is authenticated.
     */
    boolean authenticate(String userName, String password, String authenticationProvider) throws SecurityException;
}
