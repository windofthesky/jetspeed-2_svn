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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.components.util.system.SystemResourceUtil;
import org.apache.jetspeed.components.util.system.ClassLoaderSystemResourceUtilImpl;
import org.apache.jetspeed.security.AuthenticationProvider;

/**
 * @see org.apache.jetspeed.security.AuthenticationProvider
 * @author <a href="mailto:LeStrat_David@emc.com">David Le Strat </a> 
 */
public class AuthenticationProviderImpl implements AuthenticationProvider
{

    /** The logger. */
    private static final Log log = LogFactory.getLog(AuthenticationProviderImpl.class);

    /** The list of login modules. */
    private List loginModules = new ArrayList();
    
    /**
     * <p>
     * Constructor configuring the security service with the correct
     * <code>java.security.auth.login.config</code>.
     * </p>
     * 
     * @param loginConfig The login module config.
     */
    public AuthenticationProviderImpl(String loginConfig)
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
            // TODO This is incorect but will do for now.
            loginModules.add(loginConfigUrl.toString());
        }
    }
   
    /**
     * @see org.apache.jetspeed.security.AuthenticationProvider#getLoginModules()
     */
    public List getLoginModules()
    {
        return this.loginModules;
    }
}