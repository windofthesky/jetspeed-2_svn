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

import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;

/**
 * <p>
 * Utility component used to configure the security component and provide access
 * to the various security handlers.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface SecurityProvider
{
    /**
     * <p>
     * Getter for the {@link AuthenticationProviderProxy}
     * </p>
     * 
     * @return The {@link AuthenticationProviderProxy}.
     */
    AuthenticationProviderProxy getAuthenticationProviderProxy();

    /**
     * <p>
     * Getter for the {@link RoleSecurityHandler}
     * </p>
     * 
     * @return The RoleSecurityHandler.
     */
    RoleSecurityHandler getRoleSecurityHandler();

    /**
     * <p>
     * Getter for the {@link GroupSecurityHandler}
     * </p>
     * 
     * @return The GroupSecurityHandler.
     */
    GroupSecurityHandler getGroupSecurityHandler();

    /**
     * <p>
     * Gettter for the {@link SecurityMappingHandler}
     * </p>
     * 
     * @return The SecurityMappingHandler.
     */
    SecurityMappingHandler getSecurityMappingHandler();

}
