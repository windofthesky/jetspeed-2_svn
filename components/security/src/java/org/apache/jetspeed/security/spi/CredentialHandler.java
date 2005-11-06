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

import java.sql.Date;
import java.util.Set;

import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * This interface encapsulates the handling of security credentials.
 * </p>
 * <p>
 * This provides a central placeholder for changing the mapping of user
 * credentials.  The default implementation only supports <code>PasswordCredential</code>
 * </p>
 * <p>
 * A security implementation wanting to map additional credentials should do so
 * here.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface CredentialHandler
{
    /**
     * <p>
     * Gets the public credentials for the user.
     * </p>
     * 
     * @param username The username.
     * @return The set of public credentials.
     */
    Set getPublicCredentials(String username);
    
    /**
     * <p>
     * Gets the private credentials for the user.
     * </p>
     * 
     * @param username The username.
     * @return The set of private credentials.
     */
    Set getPrivateCredentials(String username);
    
    /**
     * <p>
     * Adds or updates a private password credential.<br>
     * If <code>oldPassword</code> is not null, the oldPassword will first be checked (authenticated).<br>
     * </p>
     * 
     * @param oldPassword The old password.
     * @param newPassword The new password.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void setPassword(String userName, String oldPassword, String newPassword) throws SecurityException;

    /**
     * <p>
     * Set the update required state of the user password credential.
     * </p>
     * 
     * @param userName The user name.
     * @param updateRequired The update required state.
     * @throws Throws a security exception.
     */
    void setPasswordUpdateRequired(String userName, boolean updateRequired) throws SecurityException;

    /**
     * <p>
     * Set the enabled state of the user password credential.
     * </p>
     * 
     * @param userName The user name.
     * @param enabled The enabled state.
     * @throws Throws a security exception.
     */
    void setPasswordEnabled(String userName, boolean enabled) throws SecurityException;

    /**
     * <p>
     * Set the expiration date and the expired flag of the password credential.</p>
     * <p>
     * If a date equal or before the current date is provided, the expired flag will be set to true,
     * otherwise to false.</p>
     * 
     * @param userName The user name.
     * @param expirationDate The expiration date to set.
     * @throws Throws a security exception.
     */
    void setPasswordExpiration(String userName, Date expirationDate) throws SecurityException;

    /**
     * <p>
     * Authenticate a user.
     * </p>
     * 
     * @param userName The user name.
     * @param password The user password.
     * @return Whether or not a user is authenticated.
     */
    boolean authenticate(String userName, String password) throws SecurityException;
}