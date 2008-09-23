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

import java.sql.Timestamp;
import java.util.Date;

import org.apache.jetspeed.security.AlgorithmUpgradePasswordEncodingService;
import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.CredentialPasswordValidator;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Encodes (encrypts) an {@link PasswordCredential} password using the provided {@link PasswordCredentialEncoder encoder}
 * if it is loaded unencoded from the persistent store.</p>
 * <p>
 * This interceptor is useful when credentials need to be preset in the persistent store (like through scripts) or
 * migrated unencoded from a different storage.</p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class EncodePasswordOnFirstLoadInterceptor extends AbstractPasswordCredentialInterceptorImpl
{
    /**
     * @return true if now encoded
     */
    public boolean afterLoad(String userName, PasswordCredential credential, CredentialPasswordEncoder encoder, CredentialPasswordValidator validator) throws SecurityException
    {
        boolean updated = false;
        if (credential.getPassword() != null && !credential.isEncoded() && encoder != null )
        {
            credential.setPassword(encoder.encode(userName,credential.getPassword()), true);
            credential.clearNewPasswordSet();
            
            if ( encoder instanceof AlgorithmUpgradePasswordEncodingService)
            {
                // For the AlgorithmUpgradePBEPasswordService to be able to distinguise between
                // old and new encoded passwords, it evaluates the last and previous authentication timestamps.
                // With an automatic encoding (using the new encoding schema) the last authentication must be
                // set to null (as the user hasn't been authenticated yet again, which leaves the previous
                // authentication timestamp for indicating when the (new) encoding took place.
                credential.setPreviousAuthenticationDate(new Timestamp(new Date().getTime()));
                credential.setLastAuthenticationDate(null);
            }
            updated = true;
        }
        return updated;
    }
}
