/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
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

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;

/**
 * <p>
 * Encodes (encrypts) an {@link InternalCredential} password using the configured {@link PasswordCredentialProvider#getEncoder() encoder}
 * if it is loaded unencoded from the persistent store.</p>
 * <p>
 * This interceptor is useful when credentials need to be preset in the persistent store (like through scripts) or
 * migrated unencoded from a different storage.</p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class EncodePasswordOnFirstLoadInterceptor extends AbstractInternalPasswordCredentialInterceptorImpl
{
    /**
     * @return true if now encoded
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterLoad(org.apache.jetspeed.security.spi.PasswordCredentialProvider, java.lang.String, org.apache.jetspeed.security.om.InternalCredential)
     */
    public boolean afterLoad(PasswordCredentialProvider pcProvider, String userName, InternalCredential credential)
            throws SecurityException
    {
        boolean updated = false;
        if (!credential.isEncoded() && pcProvider.getEncoder() != null )
        {
            credential.setValue(pcProvider.getEncoder().encode(userName,credential.getValue()));
            credential.setEncoded(true);
            updated = true;
        }
        return updated;
    }
}
