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

import java.security.Policy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;

/**
 * @author <a href="">David Le Strat </a>
 *  
 */
public class SecurityProviderImpl implements SecurityProvider
{

    private static final Log log = LogFactory.getLog(SecurityProviderImpl.class);

    /** The {@link CredentialHandler}. */
    private CredentialHandler credHandler;

    /** The {@link UserSecurityHandler}. */
    private UserSecurityHandler userSecurityHandler;

    /** The {@link RoleSecurityHandler}. */
    private RoleSecurityHandler roleSecurityHandler;

    /** The {@link GroupSecurityHandler}. */
    private GroupSecurityHandler groupSecurityHandler;

    /**
     * <p>
     * Constructor configuring the security services with the correct
     * security handlers.
     * </p>
     * 
     * @param credHandler The credential handler.
     * @param userSecurityHandler The user security handler.
     * @param roleSecurityHandler The role security handler.
     * @param groupSecurityHandler The group security handler.
     */
    public SecurityProviderImpl(CredentialHandler credHandler, UserSecurityHandler userSecurityHandler,
            RoleSecurityHandler roleSecurityHandler, GroupSecurityHandler groupSecurityHandler)
    {
        // The credential handler.
        this.credHandler = credHandler;
        // The user security handler.
        this.userSecurityHandler = userSecurityHandler;
        // The role security handler.
        this.roleSecurityHandler = roleSecurityHandler;
        // The group security handler.
        this.groupSecurityHandler = groupSecurityHandler;
    }
    
    /**
     * <p>
     * Constructor configuring the security services with the correct
     * {@link Policy}and security handlers.
     * </p>
     * 
     * @param policy The policy.
     * @param credHandler The credential handler.
     * @param userSecurityHandler The user security handler.
     * @param roleSecurityHandler The role security handler.
     * @param groupSecurityHandler The group security handler.
     */
    public SecurityProviderImpl(Policy policy, CredentialHandler credHandler, UserSecurityHandler userSecurityHandler,
            RoleSecurityHandler roleSecurityHandler, GroupSecurityHandler groupSecurityHandler)
    {
        // The policy.
        Policy.setPolicy(policy);
        Policy.getPolicy().refresh();
        // The credential handler.
        this.credHandler = credHandler;
        // The user security handler.
        this.userSecurityHandler = userSecurityHandler;
        // The role security handler.
        this.roleSecurityHandler = roleSecurityHandler;
        // The group security handler.
        this.groupSecurityHandler = groupSecurityHandler;
    }

    /**
     * @see org.apache.jetspeed.security.SecurityProvider#getCredentialHandler()
     */
    public CredentialHandler getCredentialHandler()
    {
        return this.credHandler;
    }

    /**
     * @see org.apache.jetspeed.security.SecurityProvider#getUserSecurityHandler()
     */
    public UserSecurityHandler getUserSecurityHandler()
    {
        return this.userSecurityHandler;
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
}