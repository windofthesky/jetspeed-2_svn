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

package org.apache.jetspeed.security.spi.impl;

import java.util.List;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.CredentialPasswordEncoder;
import org.apache.jetspeed.security.spi.CredentialPasswordValidator;
import org.apache.jetspeed.security.spi.PasswordCredentialInterceptor;
import org.apache.jetspeed.security.spi.UserPasswordCredentialPolicyManager;

/**
 * @version $Id$
 *
 */
public class UserPasswordCredentialPolicyManagerImpl implements UserPasswordCredentialPolicyManager
{
    private CredentialPasswordEncoder encoder;
    private CredentialPasswordValidator validator;
    private PasswordCredentialInterceptor[] interceptors;
            
    public UserPasswordCredentialPolicyManagerImpl()
    {
        this.interceptors = new PasswordCredentialInterceptor[0];
    }

    public UserPasswordCredentialPolicyManagerImpl(CredentialPasswordEncoder encoder, CredentialPasswordValidator validator, List<?> interceptors)
    {
        this.encoder = encoder;
        this.validator = validator;
        this.interceptors = (PasswordCredentialInterceptor[]) interceptors.toArray(new PasswordCredentialInterceptor[interceptors.size()]);
    }

    public CredentialPasswordEncoder getCredentialPasswordEncoder()
    {
        return encoder;
    }

    public CredentialPasswordValidator getCredentialPasswordValidator()
    {
        return validator;
    }

    public void onLoad(PasswordCredential credential, String userName)
    {
        // TODO Auto-generated method stub
    }

    public boolean authenticate(PasswordCredential credential, String userName, String password) throws SecurityException
    {
        // TODO Auto-generated method stub
        return true;
    }

    public void onStore(PasswordCredential credential)
    {
        // TODO Auto-generated method stub
    }
}
