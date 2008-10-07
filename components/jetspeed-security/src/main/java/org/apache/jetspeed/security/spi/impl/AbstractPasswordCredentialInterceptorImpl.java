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

import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.CredentialPasswordValidator;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.PasswordCredentialInterceptor;

/**
 * <p>
 * Base class providing default empty behavior for a {@link PasswordCredentialInterceptor}
 * implementation.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public abstract class AbstractPasswordCredentialInterceptorImpl implements PasswordCredentialInterceptor
{
    public boolean afterLoad(String userName, PasswordCredential credential, CredentialPasswordEncoder encoder, CredentialPasswordValidator validator) throws SecurityException
    {
        return false;
    }

    public boolean afterAuthenticated(PasswordCredential credential, boolean authenticated) throws SecurityException
    {
        return false;
    }

    public void beforeCreate(PasswordCredential credential) throws SecurityException
    {
    }

    public void beforeSetPassword(PasswordCredential credential, String password, boolean authenticated) throws SecurityException
    {
    }
}
