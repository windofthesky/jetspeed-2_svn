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

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.spi.UserPasswordCredentialManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialPolicyManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialStorageManager;

/**
 * @version $Id$
 */
public class UserPasswordCredentialManagerImpl implements UserPasswordCredentialManager
{
    private UserPasswordCredentialStorageManager upcsm;
    private UserPasswordCredentialPolicyManager upcpm;
    
    public UserPasswordCredentialManagerImpl(UserPasswordCredentialStorageManager upcsm)
    {
        this.upcsm = upcsm;
    }

    public UserPasswordCredentialManagerImpl(UserPasswordCredentialStorageManager upcsm, UserPasswordCredentialPolicyManager upcpm)
    {
        this(upcsm);
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
}
