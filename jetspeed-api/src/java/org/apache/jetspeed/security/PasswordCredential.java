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
public interface PasswordCredential
{
    String PASSWORD_CREDENTIAL_DAYS_VALID_REQUEST_ATTR_KEY = PasswordCredential.class.getName() + ".check";

    /**
     * @return The username.
     */
    String getUserName();

    /**
     * @return The password.
     */
    char[] getPassword();

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
     * @return when the password is (going to be) expired.
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
}
