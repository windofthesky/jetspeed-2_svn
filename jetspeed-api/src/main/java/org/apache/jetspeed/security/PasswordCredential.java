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
import java.sql.Timestamp;

/**
 * <p>
 * PasswordCredential
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public interface PasswordCredential extends UserCredential
{
    String PASSWORD_CREDENTIAL_DAYS_VALID_REQUEST_ATTR_KEY = PasswordCredential.class.getName() + ".check";
    public static final Short TYPE_CURRENT = new Short((short) 0);
    public static final Short TYPE_HISTORICAL = new Short((short) 1);

    boolean isNew();
    
    /**
     * @return The user the PasswordCredential belongs to
     */
    User getUser();
    
    Short getType();
    
    /**
     * @return raw (possibly encoded) password.
     */
    String getPassword();
    
    /**
     * @return the temporary old (plain text) password provided when a new password is set
     */
    String getOldPassword();
    
    /**
     * @return the temporary new (plain text) password provided when a new password is set
     */
    String getNewPassword();
    
    /**
     * Set a new raw (possibly encoded) password
     * @param password
     * @param encoded
     */
    void setPassword(String password, boolean encoded);
    
    /**
     * Set a new unencoded password and providing the old (unencoded) password for validation
     * <p>
     * Depending on the implementation one or more pluggable validation and processing handlers
     * might be invoked when the PasswordCredential is stored.
     * </p>
     * <p>
     * A User changing its own password should be required to provide an oldPassword to validate against
     * and might trigger different processing than when a null value is provided for the oldPassword
     * </p>
     * @param oldPassword
     * @param newPassword
     */
    void setPassword(String oldPassword, String newPassword);
    
    boolean isNewPasswordSet();
    
    void clearNewPasswordSet();
    
    void revertNewPasswordSet();
    
    boolean isEncoded();
    
    void setEncoded(boolean encoded);
    
    void setUpdateRequired(boolean updateRequired);
    
    boolean isStateReadOnly();

    void setEnabled(boolean enabled);
    
    void setExpired(boolean expired);
    
    void setExpirationDate(Date expirationDate);
    
    void setPreviousAuthenticationDate(Timestamp date);
    
    void setLastAuthenticationDate(Timestamp date);
    
    void setAuthenticationFailures(int authenticationFailures);
}
