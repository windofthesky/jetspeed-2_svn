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
package org.apache.jetspeed.security.spi.impl;

import java.util.Collection;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;

/**
 * <p>
 * Enforces a {@link #MaxPasswordAuthenticationFailuresInterceptor(int) maximum number of times} a user may provide an invalid password.
 * Once the maximum number of invalid authentications is reached, the credential is disabled.</p>
 * <p>
 * Note: the current count is <em>not</em> reset on valid authentication by this interceptor.
 * This is done by the {@link DefaultCredentialHandler} which invokes the interceptor(s) after authentication
 * and no interceptor {@link #afterAuthenticated(InternalUserPrincipal, String, InternalCredential, boolean) afterAuthenicated} 
 * method returns true.</p>
 * <p>
 * But, this interceptor <em>does</em> (re)sets the count on creation and on change of the password.</p>
 * <p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class MaxPasswordAuthenticationFailuresInterceptor extends AbstractInternalPasswordCredentialInterceptorImpl
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
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterAuthenticated(org.apache.jetspeed.security.om.InternalUserPrincipal, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, boolean)
     */
    public boolean afterAuthenticated(InternalUserPrincipal internalUser, String userName,
            InternalCredential credential, boolean authenticated) throws SecurityException
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
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeCreate(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, InternalCredential, java.lang.String)
     */
    public void beforeCreate(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password) throws SecurityException
    {
        credential.setAuthenticationFailures(0);
    }
    
    /**
     * Resets the count of invalid authentications to zero (0).
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeSetPassword(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, java.lang.String, boolean)
     */
    public void beforeSetPassword(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password, boolean authenticated) throws SecurityException
    {
        credential.setAuthenticationFailures(0);
    }
}
