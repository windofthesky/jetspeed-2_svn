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
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialManager;

/**
 * @version $Id$
 *
 */
public class DefaultAuthenticationProvider extends BaseAuthenticationProvider
{
    private UserPasswordCredentialManager upcm;
    private UserManager um;

    public DefaultAuthenticationProvider(String providerName, String providerDescription, UserPasswordCredentialManager upcm, UserManager um)
    {
        super(providerName, providerDescription);
        this.upcm = upcm;
        this.um = um;
    }

    public DefaultAuthenticationProvider(String providerName, String providerDescription, String loginConfig,
                                         UserPasswordCredentialManager upcm, UserManager um)
    {
        super(providerName, providerDescription, loginConfig);
        this.upcm = upcm;
        this.um = um;
    }

    public AuthenticatedUser authenticate(String userName, String password) throws SecurityException
    {
        PasswordCredential credential = upcm.getAuthenticatedPasswordCredential(userName, password);
        User user = credential.getUser();
        if (user == null)
        {
            user = um.getUser(credential.getUserName());
        }
        return new AuthenticatedUserImpl(user, new UserCredentialImpl(credential));
    }
}
