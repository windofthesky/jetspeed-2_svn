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
package org.apache.jetspeed.security.impl;

import org.apache.jetspeed.security.AuthenticationProvider;
import org.apache.jetspeed.security.LoginModuleProxy;
import org.apache.jetspeed.security.UserManager;

/**
 * @see org.apache.jetspeed.security.LoginModuleProxy
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class LoginModuleProxyImpl implements LoginModuleProxy
{
    /** The {@link LoginModuleProxy}instance. */
    static LoginModuleProxy loginModuleProxy;

    /** The {@link AuthenticationProvider}. */
    protected AuthenticationProvider authProvider;

    /** The {@link UserManager}. */
    private UserManager userMgr;
    
    /** The portal user role. */
    private String portalUserRole;

    /**
     * <p>
     * Constructor providing a bridge between the login module and the user
     * manager.
     * </p>
     * 
     * @param authProvider The authentication provider
     * @param userMgr The user manager.
     * @param portalUserRole The portal user role shared by all portal users: used
     *                       in web.xml authorization to detect authenticated portal
     *                       users.
     *  
     */
    public LoginModuleProxyImpl(AuthenticationProvider authProvider, UserManager userMgr, String portalUserRole)
    {
        // The authentication provider
        this.authProvider = authProvider;

        // The user manager.
        this.userMgr = userMgr;
        
        // The portal user role
        this.portalUserRole = (portalUserRole != null ? portalUserRole : DEFAULT_PORTAL_USER_ROLE_NAME);

        // Hack providing access to the UserManager in the LoginModule.
        // TODO Can we fix this?
        LoginModuleProxyImpl.loginModuleProxy = this;
    }
    public LoginModuleProxyImpl(AuthenticationProvider authProvider, UserManager userMgr)
    {
        this(authProvider, userMgr, DEFAULT_PORTAL_USER_ROLE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.LoginModuleProxy#getUserManager()
     */
    public UserManager getUserManager()
    {
        return this.userMgr;
    }

    /**
     * @see org.apache.jetspeed.security.LoginModuleProxy#getAuthenticationProvider()
     */
    public AuthenticationProvider getAuthenticationProvider()
    {
        return this.authProvider;
    }
    
    /**
     * @see org.apache.jetspeed.security.LoginModuleProxy#getPortalUserRole()
     */
    public String getPortalUserRole()
    {
        return this.portalUserRole;
    }
}
