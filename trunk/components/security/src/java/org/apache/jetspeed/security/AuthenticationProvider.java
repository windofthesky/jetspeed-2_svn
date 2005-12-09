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
 * Configures an authentication provider.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface AuthenticationProvider
{
    
    /**
     * <p>
     * Gets the authentication provider name.
     * </p>
     * 
     * @return The authentication provider name.
     */
    String getProviderName();
    
    /**
     * <p>
     * Gets the authentication provider description.
     * </p>
     * 
     * @return The authentication provider description.
     */
    String getProviderDescription();
    
    /**
     * <p>
     * Gets the {@link UserSecurityHandler}.
     * </p>
     * 
     * @return The {@link UserSecurityHandler}.
     */
    UserSecurityHandler getUserSecurityHandler();
    
    
    /**
     * <p>
     * Sets the {@link UserSecurityHandler}.
     * </p>
     * 
     * @param userSecurityHandler The {@link UserSecurityHandler}.
     */
    void setUserSecurityHandler(UserSecurityHandler userSecurityHandler);
    
    /**
     * <p>
     * Gets the {@link CredentialHandler}.
     * </p>
     * 
     * @return The {@link CredentialHandler}.
     */
    CredentialHandler getCredentialHandler();
    
    /**
     * <p>
     * Sets the {@link CredentialHandler}.
     * </p>
     * 
     * @param credHandler The {@link CredentialHandler}.
     */
    void setCredentialHandler(CredentialHandler credHandler);

}