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

import java.util.Set;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * This interface encapsulates the handling of security credentials.
 * </p>
 * <p>
 * This provides a central placeholder for changing the mapping of user
 * credentials.  The default implementation only supports <code>PasswordCredential</code>
 * </p>
 * <p>
 * A security implementation wanting to map additional credentials should do so
 * here.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface CredentialHandler
{
    /**
     * <p>
     * Factory method to create a new, CredentialHandler specific, {@link PasswordCredential}.
     * </p>
     * 
     * @param userName The username
     * @param password The password
     * @return The new PasswordCredential
     * @throws SecurityException if the UserPrincipal doesn't exists or the password isn't valid.
     */
    PasswordCredential createPasswordCredential(String userName, char[] password) throws SecurityException;
    
    /**
     * <p>
     * Gets the public credentials for the user.
     * </p>
     * 
     * @param username The username.
     * @return The set of public credentials.
     */
    Set getPublicCredentials(String username);
    
    /**
     * <p>
     * Sets a public password credential.
     * </p>
     * 
     * @param oldPwdCredential The old {@link PasswordCredential}.
     * @param newPwdCredential The new {@link PasswordCredential}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void setPublicPasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential) throws SecurityException;
    
    /**
     * <p>
     * Gets the private credentials for the user.
     * </p>
     * 
     * @param username The username.
     * @return The set of private credentials.
     */
    Set getPrivateCredentials(String username);
    
    /**
     * <p>
     * Sets a private password credential.  If <code>oldPwdCredential</code> is not null, the new
     * password credential will replace the old one.
     * </p>
     * 
     * @param oldPwdCredential The old {@link PasswordCredential}.
     * @param newPwdCredential The new {@link PasswordCredential}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void setPrivatePasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential) throws SecurityException;
}