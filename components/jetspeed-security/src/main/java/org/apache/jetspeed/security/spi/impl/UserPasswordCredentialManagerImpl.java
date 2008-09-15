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

import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.spi.UserPasswordCredentialAccessManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialPolicyManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialStorageManager;

/**
 * @version $Id$
 */
public class UserPasswordCredentialManagerImpl implements UserPasswordCredentialManager
{
    private UserPasswordCredentialStorageManager upcsm;
    private UserPasswordCredentialAccessManager upcam;
    private UserPasswordCredentialPolicyManager upcpm;
    
    public UserPasswordCredentialManagerImpl(UserPasswordCredentialStorageManager upcsm, UserPasswordCredentialAccessManager upcam)
    {
        this.upcsm = upcsm;
        this.upcam = upcam;
    }

    public UserPasswordCredentialManagerImpl(UserPasswordCredentialStorageManager upcsm, UserPasswordCredentialAccessManager upcam, UserPasswordCredentialPolicyManager upcpm)
    {
        this(upcsm, upcam);
        this.upcpm = upcpm;
    }

    public PasswordCredential getPasswordCredential(User user) throws SecurityException
    {
        PasswordCredential credential = upcsm.getPasswordCredential(user);
        if (upcpm != null)
        {
            upcpm.onLoad(credential, user.getName());
        }
        return credential;
    }

    public void storePasswordCredential(PasswordCredential credential) throws SecurityException
    {
        if (upcpm != null)
        {
            upcpm.onStore(credential);
        }
        upcsm.storePasswordCredential(credential);
    }

    public PasswordCredential getAuthenticatedPasswordCredential(String userName, String password) throws SecurityException
    {
        PasswordCredential credential = upcam.getPasswordCredential(userName);
        if (upcpm != null)
        {
            upcpm.onLoad(credential, userName);
            upcpm.authenticate(credential, userName, password);
        }
        else if (password == null)
        {
            throw new SecurityException(SecurityException.PASSWORD_REQUIRED);
        }
        else if (credential.getPassword() == null || !password.equals(new String(credential.getPassword())))
        {
            throw new SecurityException(SecurityException.INVALID_PASSWORD);
        }
        if (!credential.isEnabled() || credential.isExpired())
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER_TYPE_NAME, userName));
        }
        try
        {
            upcam.loadPasswordCredentialUser(credential);
        }
        catch (Exception e)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER_TYPE_NAME, userName), e);
        }
        if (credential.getUser() == null || !credential.getUser().isEnabled())
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER_TYPE_NAME, userName));
        }
        return credential;
    }
}
