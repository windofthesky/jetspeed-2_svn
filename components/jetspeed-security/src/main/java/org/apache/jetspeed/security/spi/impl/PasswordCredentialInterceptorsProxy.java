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
package org.apache.jetspeed.security.spi.impl;

import java.util.List;

import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.CredentialPasswordValidator;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.PasswordCredentialInterceptor;

/**
 * <p>
 * Provides a wrapper around a list of interceptors so multiple interceptors can
 * be used with the {@link DefaultCredentialHandler}.
 * Each interceptor will be invoked sequentially.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PasswordCredentialInterceptorsProxy implements PasswordCredentialInterceptor
{
    private PasswordCredentialInterceptor[] interceptors;

    public PasswordCredentialInterceptorsProxy(List<?> interceptors)
    {
        this.interceptors = (PasswordCredentialInterceptor[]) interceptors
                .toArray(new PasswordCredentialInterceptor[interceptors.size()]);
    }

    public boolean afterLoad(String userName, PasswordCredential credential, CredentialPasswordEncoder encoder, CredentialPasswordValidator validator) throws SecurityException
    {
        boolean updated = false;
        for (int i = 0; i < interceptors.length; i++)
        {
            if (interceptors[i] != null && interceptors[i].afterLoad(userName, credential, encoder, validator))
            {
                updated = true;
            }
        }
        return updated;
    }

    public boolean afterAuthenticated(PasswordCredential credential, boolean authenticated) throws SecurityException
    {
        boolean updated = false;
        for (int i = 0; i < interceptors.length; i++)
        {
            if (interceptors[i] != null
                    && interceptors[i].afterAuthenticated(credential, authenticated))
            {
                updated = true;
            }
        }
        return updated;
    }

    public void beforeCreate(PasswordCredential credential) throws SecurityException
    {
        for (int i = 0; i < interceptors.length; i++)
        {
            if (interceptors[i] != null)
            {
                interceptors[i].beforeCreate(credential);
            }
        }
    }

    public void beforeSetPassword(PasswordCredential credential, String password, boolean authenticated) throws SecurityException
    {
        for (int i = 0; i < interceptors.length; i++)
        {
            if (interceptors[i] != null)
            {
                interceptors[i].beforeSetPassword(credential, password, authenticated);
            }
        }
    }
}
