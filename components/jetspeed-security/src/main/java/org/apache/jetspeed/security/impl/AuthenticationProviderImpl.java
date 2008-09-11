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

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.components.util.system.SystemResourceUtil;
import org.apache.jetspeed.components.util.system.ClassLoaderSystemResourceUtilImpl;
import org.apache.jetspeed.security.AuthenticatedUser;
import org.apache.jetspeed.security.AuthenticationProvider;
import org.apache.jetspeed.security.SecurityException;

/**
 * @see org.apache.jetspeed.security.AuthenticationProvider
 * @version $Id$
 */
public class AuthenticationProviderImpl implements AuthenticationProvider
{

    /** The logger. */
    private static final Log log = LogFactory.getLog(AuthenticationProviderImpl.class);

    /** The provider name. */
    private String providerName;

    /** The provider description. */
    private String providerDescription;

    /**
     * <p>
     * Constructor to configure authenticatino user security and credential
     * handlers.
     * </p>
     * 
     * @param providerName The provider name.
     * @param providerDescription The provider description.
     * @param credHandler The credential handler.
     * @param userSecurityHandler The user security handler.
     */
    public AuthenticationProviderImpl(String providerName, String providerDescription)
    {
        // The provider name.
        this.providerName = providerName;
        // The provider description.
        this.providerDescription = providerDescription;
    }
    
    /**
     * <p>
     * Constructor configuring the security service with the correct
     * <code>java.security.auth.login.config</code>.
     * </p>
     * 
     * @param providerName The provider name.
     * @param providerDescription The provider description.
     * @param loginConfig The login module config.
     */
    public AuthenticationProviderImpl(String providerName, String providerDescription, String loginConfig)
    {
        this(providerName, providerDescription);
        
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
    }

    /**
     * @return Returns the providerDescription.
     */
    public String getProviderDescription()
    {
        return providerDescription;
    }

    /**
     * @param providerDescription The providerDescription to set.
     */
    public void setProviderDescription(String providerDescription)
    {
        this.providerDescription = providerDescription;
    }

    /**
     * @return Returns the providerName.
     */
    public String getProviderName()
    {
        return providerName;
    }

    /**
     * @param providerName The providerName to set.
     */
    public void setProviderName(String providerName)
    {
        this.providerName = providerName;
    }

    public AuthenticatedUser authenticate(String userName, String password) throws SecurityException
    {
        // TODO Auto-generated method stub
        return null;
    }
}