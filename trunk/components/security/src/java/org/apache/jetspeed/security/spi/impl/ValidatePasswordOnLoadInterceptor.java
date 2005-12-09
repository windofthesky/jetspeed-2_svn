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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;

/**
 * <p>
 * Checks if a (pre)set password in the persitent store is valid according to the configured
 * {@link PasswordCredentialProvider#getValidator() validator} when loaded from the persistent store.</p>
 * <p>
 * If the password checks out to be invalid, an error is logged and the credential is flagged to be 
 * {@link InternalCredential#isUpdateRequired() updateRequired}.</p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class ValidatePasswordOnLoadInterceptor extends AbstractInternalPasswordCredentialInterceptorImpl
{
    private static final Log log = LogFactory.getLog(InternalPasswordCredentialInterceptor.class);
    
    /**
     * @return true is the password was invalid and update is required
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterLoad(org.apache.jetspeed.security.spi.PasswordCredentialProvider, java.lang.String, org.apache.jetspeed.security.om.InternalCredential)
     */
    public boolean afterLoad(PasswordCredentialProvider pcProvider, String userName, InternalCredential credential)
            throws SecurityException
    {
        boolean updated = false;
        if (!credential.isEncoded() && pcProvider.getValidator() != null )
        {
            try
            {
                pcProvider.getValidator().validate(credential.getValue());
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
