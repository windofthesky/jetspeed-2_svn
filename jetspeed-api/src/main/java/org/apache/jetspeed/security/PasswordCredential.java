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
    public static final Integer TYPE_CURRENT = new Integer(0);
    public static final Integer TYPE_HISTORICAL = new Integer(1);

    /**
     * @return The user the PasswordCredential belongs to
     */
    User getUser();
    
    Integer getType();
    
    /**
     * @return raw (possibly encoded) password.
     */
    char[] getPassword();
    
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
    void setPassword(char[] password, boolean encoded);
    
    /**
     * Set a new (plain text) password also (optionally) providing the old (plain text) password
     */
    void setPassword(String oldPassword, String newPassword);
    
    boolean isNewPasswordSet();
    
    void clearNewPasswordSet();
    
    boolean isPasswordEncoded();
    
    void setPasswordEncoded(boolean passwordEncoded);
    
    void setUpdateRequired(boolean updateRequired);
    
    boolean isStateReadOnly();

    void setEnabled(boolean enabled);
    
    void setExpired(boolean expired);
    
    void setExpirationDate(Date expirationDate);
    
    void resetAuthenticationFailures();
}
