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
package org.apache.jetspeed.security.spi.impl;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Enforces a {@link #MaxPasswordAuthenticationFailuresInterceptor(int) maximum number of times} a user may provide an invalid password.
 * Once the maximum number of invalid authentications is reached, the credential is disabled.</p>
 * <p>
 * Note: the current count is <em>not</em> reset on valid authentication by this interceptor.
 * But, this interceptor <em>does</em> (re)sets the count on creation and on change of the password.</p>
 * <p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class MaxPasswordAuthenticationFailuresInterceptor extends AbstractPasswordCredentialInterceptorImpl
{
    private int maxNumberOfAuthenticationFailures;
    
    /**
     * <p>
     * Configure the maximum number of invalid authentications allowed in a row.</p>
     * <p>
     * A value of zero (0) disables the check</p>
     */
    public MaxPasswordAuthenticationFailuresInterceptor(int maxNumberOfAuthenticationFailures)
    {
        this.maxNumberOfAuthenticationFailures = maxNumberOfAuthenticationFailures;
    }
    
    /**
     * Checks the current count of authentication failures when the credential is not expired and authentication failed.
     * @return true if the maximum number of invalid authentications is reached and the credential is disabled.
     */
    public boolean afterAuthenticated(PasswordCredential credential, boolean authenticated) throws SecurityException
    {
        boolean update = false;
        if ( !credential.isExpired() && !authenticated && maxNumberOfAuthenticationFailures > 0 )
        {
            int authenticationFailures = credential.getAuthenticationFailures()+1;
            credential.setAuthenticationFailures(authenticationFailures);
            if (authenticationFailures >= maxNumberOfAuthenticationFailures)
            {
                credential.setEnabled(false);
            }
            update = true;
        }
        return update;
    }
    
    /**
     * Sets the count of invalid authentications to zero (0).
     */
    public void beforeCreate(PasswordCredential credential) throws SecurityException
    {
        credential.setAuthenticationFailures(0);
    }
    
    /**
     * Resets the count of invalid authentications to zero (0).
     */
    public void beforeSetPassword(PasswordCredential credential, String password) throws SecurityException
    {
        credential.setAuthenticationFailures(0);
    }
}
