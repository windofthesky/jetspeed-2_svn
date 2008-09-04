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
public interface PasswordCredential extends Credential
{
    String PASSWORD_CREDENTIAL_DAYS_VALID_REQUEST_ATTR_KEY = PasswordCredential.class.getName() + ".check";

    /**
     * @return The user this PasswordCredential belongs to
     */
    User getUser();
    /**
     * @return The username.
     */
    String getName();
    
    void setUserName(String name);
    
    boolean isReadOnly();
   
    /**
     * @return The password.
     */
    char[] getPassword();
    
    void setPassword(char[] password);

    /**
     * @return true if update required.
     */
    boolean isUpdateRequired();
    
    void setUpdateRequired(boolean updateRequired);
    
    /**
     * @return true if enabled.
     */
    boolean isEnabled();
    
    void setEnabled(boolean enabled);
    
    /**
     * @return true if expired.
     */
    boolean isExpired();
    
    void setExpired(boolean expired);
    
    /**
     * @return when the password is (going to be) expired.
     */
    Date getExpirationDate();
    
    void setExpirationDate(Date expirationDate);
    
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
    
    void resetAuthenticationFailured();
}
