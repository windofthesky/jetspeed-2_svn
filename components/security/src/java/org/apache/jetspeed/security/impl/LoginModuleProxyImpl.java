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

    /** The {@link UserManager}. */
    private UserManager userMgr;

    /**
     * <p>
     * Constructor providing a bridge between the login module and the user
     * manager.
     * </p>
     * 
     * @param loginConfig The login module config.
     * @param userMgr The user manager.
     *  
     */
    public LoginModuleProxyImpl(UserManager userMgr)
    {
        // The user manager.
        this.userMgr = userMgr;

        // Hack providing access to the UserManager in the LoginModule.
        // TODO Can we fix this?
        LoginModuleProxyImpl.loginModuleProxy = this;
    }

    /**
     * @see org.apache.jetspeed.security.LoginModuleProxy#getUserManager()
     */
    public UserManager getUserManager()
    {
        return this.userMgr;
    }

}