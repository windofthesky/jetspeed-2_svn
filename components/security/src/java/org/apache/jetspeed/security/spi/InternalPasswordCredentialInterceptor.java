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

import java.util.Collection;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;

/**
 * <p>
 * Callback component interface used by {@link org.apache.jetspeed.security.spi.impl.DefaultCredentialHandler DefaultCredentialHandler} 
 * allowing injecting custom logic on certain events of the {@link InternalCredential}.
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public interface InternalPasswordCredentialInterceptor
{
    /**
     * <p>
     * Invoked after a password credential is loaded from the persistent store.</p>
     * <p>
     * If true is returned the credential is expected to be updated and its changes will be stored again.</p>
     * <p>
     * A thrown SecurityException will be logged as an error and result in the credential to be ignored 
     * as if not existing (like for authentication).</p>
     * 
     * @param pcProvider provides callback access to for instance the configured {@link CredentialPasswordEncoder} and
     * {@link CredentialPasswordValidator}
     * @param userName the name of the principal to which the credential belongs
     * @param credential the credential just loaded from the persistent store
     * @return true if the credential is updated
     * @throws SecurityException
     * @see org.apache.jetspeed.security.spi.impl.DefaultCredentialHandler#getPasswordCredential(InternalUserPrincipal, String)
     * @see org.apache.jetspeed.security.spi.impl.DefaultCredentialHandler#setPasswordExpiration(String, java.sql.Date)
     */
    boolean afterLoad(PasswordCredentialProvider pcProvider, String userName, InternalCredential credential) throws SecurityException;

    /**
     * <p>
     * Invoked during authentication after the provided password is compared against the one retrieved from
     * the InternalCredential.</p>
     * <p>
     * If true is returned, the credential is expected to be updated and its {@link InternalCredential#isEnabled() enabled}
     * and {@link InternalCredential#isExpired() expired} flags will checked if the credential is (still) valid.</p>
     * <p>
     * Note: the enabled and expired flags are <em>only</em> checked if this method returns true.</p>
     * <p>
     * A thrown SecurityException will be passed on to the authentication requestor.</p>
     *  
     * @param internalUser the user to which the credential belongs
     * @param userName the name of the principal to which the credential belongs
     * @param credential the credential of the user
     * @param authenticated true if the provided password matches the value of the credential
     * @return true if the credential is updated
     * @throws SecurityException
     * @see org.apache.jetspeed.security.spi.impl.DefaultCredentialHandler#authenticate(String, String)
     */
    boolean afterAuthenticated(InternalUserPrincipal internalUser, String userName, InternalCredential credential, boolean authenticated) throws SecurityException;

    /**
     * <p>
     * Invoked when the first password credential is to be saved for a user.</p>
     * <p>
     * This callback method can be used to set default values like the {@link InternalCredential#getExpirationDate() expiration date}.</p>
     * <p>
     * A thrown SecurityException is passed on to the new password requestor.</p>
     * 
     * @param internalUser the user to which the credential belongs
     * @param credentials the collection of credentials which will set on the user after (already contains the new credential)
     * @param userName the name of the principal to which the credential belongs
     * @param credential the credential of the user
     * @param password the new password value (already set on the new credential)
     * @throws SecurityException
     * @see org.apache.jetspeed.security.spi.impl.DefaultCredentialHandler#setPassword(String, String, String)
     */
    void beforeCreate(InternalUserPrincipal internalUser, Collection credentials, String userName, InternalCredential credential, String password) throws SecurityException;

    /**
     * <p>
     * Invoked when a new password value is to be saved for a user.</p>
     * <p>
     * The new password value is <em>not</em> yet set on the provided credential when this callback is invoked. This allows
     * custom history maintenance and/or auditing to be performed.</p>
     * <p>
     * The provided authenticated flag can be used to differentiate between a new password value set directly by a user
     * itself or through an administrative interface.</p>
     * <p>
     * After this callback is invoked, the specified password value will be set, as well as a reset of the
     * {@link InternalCredential#isUpdateRequired() updateRequired} flag, before the credential is saved.</p>
     * <p>
     * A thrown SecurityException is passed on to the set password requestor.</p>
     * 
     * @param internalUser the user to which the credential belongs
     * @param credentials the collection of credentials which will set on the user after (already contains the new credential)
     * @param userName the name of the principal to which the credential belongs
     * @param credential the credential of the user
     * @param password the new password value (already set on the new credential)
     * @param authenticated true if the new password value is provided by the user directly
     * @throws SecurityException
     * @see org.apache.jetspeed.security.spi.impl.DefaultCredentialHandler#setPassword(String, String, String)
     */
    void beforeSetPassword(InternalUserPrincipal internalUser, Collection credentials, String userName, InternalCredential credential, String password, boolean authenticated) throws SecurityException;
}
