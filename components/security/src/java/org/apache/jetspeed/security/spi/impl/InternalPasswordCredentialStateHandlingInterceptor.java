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

import java.sql.Date;
import java.util.Collection;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;

/**
 * <p>
 * InternalPasswordCredentialStateHandlingInterceptor
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class InternalPasswordCredentialStateHandlingInterceptor extends DefaultInternalPasswordCredentialInterceptor
{
    private int maxNumberOfAuthenticationFailures;
    private long maxLifeSpanInMillis;
    
    public InternalPasswordCredentialStateHandlingInterceptor(int maxNumberOfAuthenticationFailures, int maxLifeSpanInDays)
    {
        super();
        this.maxNumberOfAuthenticationFailures = maxNumberOfAuthenticationFailures;
        this.maxLifeSpanInMillis = (long)(maxLifeSpanInDays) * 1000*60*60*24;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterAuthenticated(org.apache.jetspeed.security.om.InternalUserPrincipal, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, boolean)
     */
    public boolean afterAuthenticated(InternalUserPrincipal internalUser, String userName,
            InternalCredential credential, boolean authenticated) throws SecurityException
    {
        boolean update = super.afterAuthenticated(internalUser, userName, credential, authenticated);
        if ( !credential.isExpired() )
        {
            if (credential.getExpirationDate().getTime() < System.currentTimeMillis())
            {
                credential.setExpired(true);
                update = true;
            }
            else if (!authenticated && maxNumberOfAuthenticationFailures > 0)
            {
                int authenticationFailures = credential.getAuthenticationFailures()+1;
                credential.setAuthenticationFailures(authenticationFailures);
                if (authenticationFailures >= maxNumberOfAuthenticationFailures)
                {
                    credential.setEnabled(false);
                }
                update = true;
            }
        }
        return update;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterLoad(org.apache.jetspeed.security.spi.PasswordCredentialProvider, java.lang.String, org.apache.jetspeed.security.om.InternalCredential)
     */
    public boolean afterLoad(PasswordCredentialProvider pcProvider, String userName, InternalCredential credential)
            throws SecurityException
    {
        boolean update = super.afterLoad(pcProvider, userName, credential);
        if ( credential.getExpirationDate() == null )
        {
            credential.setExpirationDate(new Date(System.currentTimeMillis()+maxLifeSpanInMillis));
            update = true;
        }
        if (credential.getLastLogonDate() == null && !credential.isUpdateRequired())
        {
            credential.setUpdateRequired(true);
            update = true;
        }
        return update;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeCreate(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, InternalCredential, java.lang.String)
     */
    public void beforeCreate(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password) throws SecurityException
    {
        super.beforeCreate(internalUser, credentials, userName, credential, password);
        credential.setExpirationDate(new Date(System.currentTimeMillis()+maxLifeSpanInMillis));
        credential.setExpired(false);
        credential.setAuthenticationFailures(0);
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeSetPassword(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, java.lang.String, boolean)
     */
    public void beforeSetPassword(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password, boolean authenticated) throws SecurityException
    {
        super.beforeSetPassword(internalUser, credentials, userName, credential, password, authenticated);
        credential.setExpirationDate(new Date(System.currentTimeMillis()+maxLifeSpanInMillis));
        credential.setExpired(false);
        credential.setAuthenticationFailures(0);
    }
}
