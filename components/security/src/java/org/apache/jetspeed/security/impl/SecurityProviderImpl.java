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
package org.apache.jetspeed.security.impl;

import org.apache.jetspeed.security.AuthenticationProviderProxy;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;

/**
 * @author <a href="">David Le Strat </a>
 *  
 */
public class SecurityProviderImpl implements SecurityProvider
{

    /** The {@link AuthenticationProviderProxy}. */
    private AuthenticationProviderProxy atnProviderProxy;

    /** The {@link RoleSecurityHandler}. */
    private RoleSecurityHandler roleSecurityHandler;

    /** The {@link GroupSecurityHandler}. */
    private GroupSecurityHandler groupSecurityHandler;

    /** The {@link SecurityMappingHandler}. */
    private SecurityMappingHandler securityMappingHandler;

    /**
     * <p>
     * Constructor configuring the security services with the correct security
     * handlers.
     * </p>
     * 
     * @param atnProviderProxy The authentication provider.
     * @param roleSecurityHandler The role security handler.
     * @param groupSecurityHandler The group security handler.
     * @param securityMappingHandler The security mapping handler.
     */
    public SecurityProviderImpl(AuthenticationProviderProxy atnProviderProxy,
            RoleSecurityHandler roleSecurityHandler, GroupSecurityHandler groupSecurityHandler,
            SecurityMappingHandler securityMappingHandler)
    {
        // The authentication provider proxy.
        this.atnProviderProxy = atnProviderProxy;
        // The role security handler.
        this.roleSecurityHandler = roleSecurityHandler;
        // The group security handler.
        this.groupSecurityHandler = groupSecurityHandler;
        // The security mapping handler.
        this.securityMappingHandler = securityMappingHandler;
    }

    /**
     * @see org.apache.jetspeed.security.SecurityProvider#getAuthenticationProviderProxy()
     */
    public AuthenticationProviderProxy getAuthenticationProviderProxy()
    {
        return this.atnProviderProxy;
    }

    /**
     * @see org.apache.jetspeed.security.SecurityProvider#getRoleSecurityHandler()
     */
    public RoleSecurityHandler getRoleSecurityHandler()
    {
        return this.roleSecurityHandler;
    }

    /**
     * @see org.apache.jetspeed.security.SecurityProvider#getGroupSecurityHandler()
     */
    public GroupSecurityHandler getGroupSecurityHandler()
    {
        return this.groupSecurityHandler;
    }

    /**
     * @see org.apache.jetspeed.security.SecurityProvider#getSecurityMappingHandler()
     */
    public SecurityMappingHandler getSecurityMappingHandler()
    {
        return this.securityMappingHandler;
    }
}