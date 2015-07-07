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

import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.CredentialPasswordValidator;
import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.AlgorithmUpgradeCredentialPasswordEncoder;
import org.apache.jetspeed.security.spi.PasswordCredentialInterceptor;
import org.apache.jetspeed.security.spi.UserPasswordCredentialPolicyManager;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
        if(interceptors !=null)
        {
            this.interceptors = (PasswordCredentialInterceptor[]) interceptors.toArray(new PasswordCredentialInterceptor[interceptors.size()]);
        }
        else
        {
            this.interceptors = new PasswordCredentialInterceptor[0];
        }
    }

    public CredentialPasswordEncoder getCredentialPasswordEncoder()
    {
        return encoder;
    }

    public CredentialPasswordValidator getCredentialPasswordValidator()
    {
        return validator;
    }

    public boolean onLoad(PasswordCredential credential, String userName) throws SecurityException
    {
        boolean update = false;
        for (PasswordCredentialInterceptor pci : interceptors)
        {
            if (pci.afterLoad(userName, credential, encoder, validator))
            {
                update = true;
            }
        }
        return update;
    }

    public boolean authenticate(PasswordCredential credential, String userName, String password) throws SecurityException
    {
        return authenticate(credential, userName, password, false);
    }
    
    public boolean authenticate(PasswordCredential credential, String userName, String password, boolean authenticated) throws SecurityException
    {
        if (!authenticated)
        {
            String encodedPassword = password;
            if (encoder != null && credential.isEncoded())
            {
                if (encoder instanceof AlgorithmUpgradeCredentialPasswordEncoder)
                {
                    encodedPassword = ((AlgorithmUpgradeCredentialPasswordEncoder)encoder).encode(credential, password);
                }
                else
                {
                    encodedPassword = encoder.encode(userName, password);
                }
                authenticated = credential.getPassword().equals(encodedPassword);            
            }
        }
        boolean update = false;
        boolean failuresUpdated = false;

        for (PasswordCredentialInterceptor pci : interceptors)
        {
            if (pci.afterAuthenticated(credential, authenticated))
            {
                update = true;
            }
            if (pci instanceof MaxPasswordAuthenticationFailuresInterceptor) {
                failuresUpdated = true;
            }
        }
        if (update && (!credential.isEnabled() || credential.isExpired()))
        {
            authenticated = false;
        }

        if (authenticated)
        {
            credential.setAuthenticationFailures(0);
            if (encoder != null && encoder instanceof AlgorithmUpgradeCredentialPasswordEncoder)
            {
                ((AlgorithmUpgradeCredentialPasswordEncoder)encoder).recodeIfNeeded(credential, password);
                credential.clearNewPasswordSet();
            }
            credential.setPreviousAuthenticationDate(credential.getLastAuthenticationDate());
            credential.setLastAuthenticationDate(new Timestamp(new Date().getTime()));
            update = true;
        }
        else
        {
            if (!failuresUpdated) {
                credential.setAuthenticationFailures(credential.getAuthenticationFailures() + 1);
            }
        }
        return update;
    }

    public void onStore(PasswordCredential credential) throws SecurityException
    {
        onStore(credential, false);
    }
    
    public void onStore(PasswordCredential credential, boolean authenticated) throws SecurityException
    {
        if (credential.isNewPasswordSet())
        {
            String newPassword = null;
            if (credential.getNewPassword() != null)
            {
                if (credential.getOldPassword() != null && !authenticated)
                {
                    String validatingOldPassword = credential.getOldPassword();
                    if (credential.isEncoded() && encoder != null)
                    {
                        if (encoder instanceof AlgorithmUpgradeCredentialPasswordEncoder)
                        {
                            validatingOldPassword = ((AlgorithmUpgradeCredentialPasswordEncoder)encoder).encode(credential, validatingOldPassword);
                        }
                        else
                        {
                            validatingOldPassword = encoder.encode(credential.getUserName(), validatingOldPassword);
                        }
                    }
                    if (credential.getPassword() == null || !credential.getPassword().equals(validatingOldPassword))
                    {
                        throw new InvalidPasswordException();
                    }
                    authenticated = true;
                }
                if (validator != null)
                {
                    if (!authenticated)
                    {
                        // Note: authenticated is also forced set to true during synchronization like from Ldap
                        // this might means the initial password isn't valid, but needs to be accepted anyway
                        // but will be forced to be changed after first login.
                        validator.validate(credential.getNewPassword());
                    }
                }
                newPassword = credential.getNewPassword();
                if (encoder != null)
                {
                    newPassword = encoder.encode(credential.getUserName(), newPassword);
                }
                
            }
            else
            {
                newPassword = credential.getPassword();
                if (encoder != null && !credential.isEncoded())
                {
                    newPassword = encoder.encode(credential.getUserName(), newPassword);
                }
            }
            
            if (!credential.isNew())
            {
                for (PasswordCredentialInterceptor pci : interceptors)
                {
                    pci.beforeSetPassword(credential, newPassword, authenticated);
                }
                credential.setUpdateRequired(false);
            }
            credential.setPassword(newPassword, encoder != null);
            credential.clearNewPasswordSet();
            if (!authenticated)
            {
                if (encoder != null && encoder instanceof AlgorithmUpgradeCredentialPasswordEncoder)
                {
                    // set current time in previous auth date, and clear last authentication date
                    // !!! While this might be a bit strange logic, it is *required* for the AlgorithmUpgradePBEPasswordEncodingService
                    // to be able to distinguise password changes from other changes
                    credential.setPreviousAuthenticationDate(new Timestamp(new Date().getTime()));
                    credential.setLastAuthenticationDate(null);
                }
            }
            else
            {
                // authenticated password change (by user itself)
                credential.setPreviousAuthenticationDate(credential.getLastAuthenticationDate());
                credential.setLastAuthenticationDate(new Timestamp(new Date().getTime()));
            }
        }
        if (credential.isNew())
        {
            for (PasswordCredentialInterceptor pci : interceptors)
            {
                pci.beforeCreate(credential);
            }
        }
    }
}
