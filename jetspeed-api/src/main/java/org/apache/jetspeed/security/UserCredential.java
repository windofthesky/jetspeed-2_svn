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

package org.apache.jetspeed.security;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * The (readonly) UserCredential accessible through the Subject (private) credentials
 * @version $Id$
 *
 */
public interface UserCredential extends Credential
{
    /**
     * @return The user name.
     */
    String getUserName();
    
    /**
     * Synchronize the internal <em>read only</em> UserCredential state when its underlying PasswordCredential is changed (by a user).
     * <p>
     * If no update is allowed this method should not be called.
     * </p>
     * <p>
     * Note: A PasswordCredential implementation (extending UserCredential) probably will throw an UnsupportedOperationException
     * </p>
     * @param pwc the underlying PasswordCredential for this UserCredential
     */
    void synchronize(UserCredential pwc);
    
    boolean isUpdateAllowed();
    
    /**
     * @return true if update required.
     */
    boolean isUpdateRequired();
    
    /**
     * @return true if enabled.
     */
    boolean isEnabled();
    
    /**
     * @return true if expired.
     */
    boolean isExpired();
    
    /**
     * @return when the credential is created.
     */
    Timestamp getCreationDate();
    
    /**
     * @return when the credential was last modified.
     */
    Timestamp getModifiedDate();
    
    /**
     * @return when the credential is (going to be) expired.
     */
    Date getExpirationDate();
    
    /**
     * @return the previous time the user logged in 
     */
    Timestamp getPreviousAuthenticationDate();

    /**
     * @return the last time the user logged in 
     */
    Timestamp getLastAuthenticationDate();

    /**
     * <p>Getter for the current number of authentication failures in a row.</p>
     * <ul>
     *   <li>-1: never tried yet</li>
     *   <li> 0: none, or last attempt was successful</li>
     *   <li>>0: number of failures</li>
     * </ul>
     * @return The number of authentication failures
     */
    int getAuthenticationFailures();
}
