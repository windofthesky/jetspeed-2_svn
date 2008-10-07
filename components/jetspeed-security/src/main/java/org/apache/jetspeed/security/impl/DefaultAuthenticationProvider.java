/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.security.impl;

import org.apache.jetspeed.security.AuthenticatedUser;
import org.apache.jetspeed.security.AuthenticatedUserImpl;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.UserPasswordCredentialManager;

/**
 * @version $Id$
 *
 */
public class DefaultAuthenticationProvider extends BaseAuthenticationProvider
{
    private UserPasswordCredentialManager upcm;

    public DefaultAuthenticationProvider(String providerName, String providerDescription, UserPasswordCredentialManager upcm)
    {
        super(providerName, providerDescription);
        this.upcm = upcm;
    }

    public DefaultAuthenticationProvider(String providerName, String providerDescription, String loginConfig,
                                         UserPasswordCredentialManager upcm)
    {
        super(providerName, providerDescription, loginConfig);
        this.upcm = upcm;
    }

    public AuthenticatedUser authenticate(String userName, String password) throws SecurityException
    {
        PasswordCredential credential = upcm.getAuthenticatedPasswordCredential(userName, password);
        return new AuthenticatedUserImpl(credential.getUser(), new UserCredentialImpl(credential));
    }
}
