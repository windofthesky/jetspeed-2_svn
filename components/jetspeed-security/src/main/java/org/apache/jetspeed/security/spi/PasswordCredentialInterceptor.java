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
package org.apache.jetspeed.security.spi;

import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.CredentialPasswordValidator;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Callback component interface to be used by the {@link UserPasswordCredentialPasswordPolicyManager} 
 * allowing injecting custom logic on certain events of the {@link PasswordCredential}.
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id: InternalPasswordCredentialInterceptor.java 291016 2005-09-22 21:19:36Z ate $
 */
public interface PasswordCredentialInterceptor
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
     * @param userName the name of the principal to which the credential belongs
     * @param credential the credential just loaded from the persistent store
     * @param encoder
     * @param validator
     * @return true if the credential is updated
     * @throws SecurityException
     */
    boolean afterLoad(String userName, PasswordCredential credential, CredentialPasswordEncoder encoder, CredentialPasswordValidator validator) throws SecurityException;

    /**
     * <p>
     * Invoked during authentication after the provided password is compared against the one retrieved from
     * the PasswordCredential.</p>
     * <p>
     * If true is returned, the credential is expected to be updated and its {@link PasswordCredential#isEnabled() enabled}
     * and {@link PasswordCredential#isExpired() expired} flags will checked if the credential is (still) valid.</p>
     * <p>
     * Note: the enabled and expired flags are <em>only</em> checked if this method returns true.</p>
     * <p>
     * A thrown SecurityException will be passed on to the authentication requestor.</p>
     *  
     * @param credential the credential of the user
     * @param authenticated true if the provided password matches the value of the credential
     * @return true if the credential is updated
     * @throws SecurityException
     */
    boolean afterAuthenticated(PasswordCredential credential, boolean authenticated) throws SecurityException;

    /**
     * <p>
     * Invoked when the first password credential is to be saved for a user.</p>
     * <p>
     * This callback method can be used to set default values like the {@link PasswordCredential#getExpirationDate() expiration date}.</p>
     * <p>
     * A thrown SecurityException is passed on to the new password requestor.</p>
     * 
     * @param credential the credential of the user
     * @throws SecurityException
     */
    void beforeCreate(PasswordCredential credential) throws SecurityException;

    /**
     * <p>
     * Invoked when a new password value is to be set for a user.</p>
     * <p>
     * The new raw, possibly encoded, password value is <em>not</em> yet set on the provided credential when this callback is invoked but provided as parameter.
     * This allows custom history maintenance and/or auditing to be performed.</p>
     * After this callback is invoked, the password raw value will be set, as well as a reset of the
     * {@link PasswordCredential#isUpdateRequired() updateRequired} flag, before the credential is saved.</p>
     * <p>
     * A thrown SecurityException is passed on to the set password requestor.</p>
     * 
     * @param credential the credential of the user
     * @param password
     * @param authenticated
     * @throws SecurityException
     */
    void beforeSetPassword(PasswordCredential credential, String password, boolean authenticated) throws SecurityException;
}
