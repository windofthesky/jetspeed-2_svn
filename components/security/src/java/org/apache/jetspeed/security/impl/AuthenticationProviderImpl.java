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

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.components.util.system.SystemResourceUtil;
import org.apache.jetspeed.components.util.system.ClassLoaderSystemResourceUtilImpl;
import org.apache.jetspeed.security.AuthenticationProvider;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.UserManager;

/**
 * @author <a href="">David Le Strat </a>
 *  
 */
public class AuthenticationProviderImpl implements AuthenticationProvider
{

    private static final Log log = LogFactory.getLog(AuthenticationProviderImpl.class);

    /** The {@link SecurityProvider}instance. */
    static AuthenticationProvider authenticationProvider;

    /** The {@link UserManager}. */
    private UserManager userMgr;

    /**
     * <p>
     * Constructor configuring the security service with the correct
     * <code>java.security.auth.login.config</code> and providing a bridge
     * between the login module and the security components.
     * </p>
     * 
     * @param loginConfig The login module config.
     * @param userMgr The user manager.

     */
    public AuthenticationProviderImpl(String loginConfig, UserManager userMgr)
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        SystemResourceUtil resourceUtil = new ClassLoaderSystemResourceUtilImpl(cl);
        URL loginConfigUrl = null;
        // The login module config.
        try
        {
            loginConfigUrl = resourceUtil.getURL(loginConfig);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Could not locate the login config.  Bad URL. " + e.toString());
        }
        if (null != loginConfigUrl)
        {
            if (log.isDebugEnabled())
                log.debug("java.security.auth.login.config = " + loginConfigUrl.toString());
            System.setProperty("java.security.auth.login.config", loginConfigUrl.toString());
        }
        // The user manager.
        this.userMgr = userMgr;
        
        // Hack providing access to the UserManager in the LoginModule.
        // TODO Can we fix this?
        AuthenticationProviderImpl.authenticationProvider = this;
    }

    /**
     * @see org.apache.jetspeed.security.SecurityProvider#getUserManager()
     */
    public UserManager getUserManager()
    {
        return this.userMgr;
    }

}