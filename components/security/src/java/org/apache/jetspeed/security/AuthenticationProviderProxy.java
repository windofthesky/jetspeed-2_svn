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

import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;

/**
 * <p>
 * Proxy allowing to handle multiple authentication providers.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface AuthenticationProviderProxy extends UserSecurityHandler, CredentialHandler
{
    /**
     * <p>
     * Sets user principal in a given authentication provider.
     * </p>
     * 
     * @param userPrincipal The user principal.
     * @param authenticationProvider The authentication provider name.
     * @throws SecurityException Throws a security exception.
     */
    void setUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Remove user principal in a given authentication provider.
     * </p>
     * 
     * @param userPrincipal The user principal.
     * @param authenticationProvider The authentication provider name.
     * @throws SecurityException Throws a security exception.
     */
    void removeUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Sets public password credential in a given authentication provider.
     * </p>
     * 
     * @param oldPwdCredential The old password credential.
     * @param newPwdCredential The new password credential.
     * @param authenticationProvider The authentication provider name.
     * @throws SecurityException Throws a security exception.
     */
    void setPublicPasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential,
            String authenticationProvider) throws SecurityException;

    /**
     * <p>
     * Sets private password credential in a given authentication provider.
     * </p>
     * 
     * @param oldPwdCredential The old password credential.
     * @param newPwdCredential The new password credential.
     * @param authenticationProvider The authentication provider name.
     * @throws SecurityException Throws a security exception.
     */
    void setPrivatePasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential,
            String authenticationProvider) throws SecurityException;
}