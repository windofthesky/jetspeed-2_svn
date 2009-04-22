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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.CredentialPasswordValidator;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.PasswordCredentialInterceptor;

/**
 * <p>
 * Checks if a (pre)set password in the persitent store is valid according to the provided
 * {@link PasswordCredentialValidator validator} when loaded from the persistent store.</p>
 * <p>
 * If the password checks out to be invalid, an error is logged and the credential is flagged to be 
 * {@link PasswordCredential#isUpdateRequired() updateRequired}.</p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class ValidatePasswordOnLoadInterceptor extends AbstractPasswordCredentialInterceptorImpl
{
    private static final Logger log = LoggerFactory.getLogger(PasswordCredentialInterceptor.class);
    
    /**
     * @return true is the password was invalid and update is required
     */
    public boolean afterLoad(String userName, PasswordCredential credential, CredentialPasswordEncoder encoder, CredentialPasswordValidator validator) throws SecurityException
    {
        boolean updated = false;
        if (credential.getPassword() != null && !credential.isEncoded() && validator != null )
        {
            try
            {
                validator.validate(credential.getPassword());
            }
            catch (SecurityException e)
            {
                log.error("Loaded password for user "+userName+" is invalid. The user will be required to change it.");
                // persitent store contains an invalid password
                // allow login (assuming the user knows the invalid value) but enforce an update
                credential.setUpdateRequired(true);
                updated = true;
            }
        }
        return updated;
    }
}
