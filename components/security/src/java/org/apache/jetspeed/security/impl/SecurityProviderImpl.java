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

import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.UserSecurityProvider;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;

/**
 * @author <a href="">David Le Strat </a>
 *  
 */
public class SecurityProviderImpl implements SecurityProvider
{

    /** The {@link CredentialHandler}. */
    private CredentialHandler credHandler;

    /** The {@link UserSecurityProvider}. */
    private UserSecurityProvider userSecurityProvider;

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
     * @param credHandler The credential handler.
     * @param userSecurityProvider The user security provider.
     * @param roleSecurityHandler The role security handler.
     * @param groupSecurityHandler The group security handler.
     * @param securityMappingHandler The security mapping handler.
     */
    public SecurityProviderImpl(CredentialHandler credHandler, UserSecurityProvider userSecurityProvider,
            RoleSecurityHandler roleSecurityHandler, GroupSecurityHandler groupSecurityHandler,
            SecurityMappingHandler securityMappingHandler)
    {
        // The credential handler.
        this.credHandler = credHandler;
        // The user security handler.
        this.userSecurityProvider = userSecurityProvider;
        // The role security handler.
        this.roleSecurityHandler = roleSecurityHandler;
        // The group security handler.
        this.groupSecurityHandler = groupSecurityHandler;
        // The security mapping handler.
        this.securityMappingHandler = securityMappingHandler;
    }

    /**
     * @see org.apache.jetspeed.security.SecurityProvider#getCredentialHandler()
     */
    public CredentialHandler getCredentialHandler()
    {
        return this.credHandler;
    }

    /**
     * @see org.apache.jetspeed.security.SecurityProvider#getUserSecurityProvider()
     */
    public UserSecurityProvider getUserSecurityProvider()
    {
        return this.userSecurityProvider;
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