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
 * Enforces a maximum lifespan for a password credential.</p>
 * When {@link #afterAuthenticated(InternalUserPrincipal, String, InternalCredential, boolean) on authentication}
 * a password its expiration date is reached, its expired flag is set.
 * The {@link DefaultCredentialHandler} then will fail the authentication and subsequent authentications
 * will fail immediately.</p>
 * <p>
 * To ensure proper expiration handling, an empty (null) expiration date will be automatically
 * filled in when the credential is loaded from the persistent store using the {@link #PasswordExpirationInterceptor(int) configured} 
 * max lifespan in days.</p>
 * <p>
 * When a password credential is {@link #beforeCreate(InternalUserPrincipal, Collection, String, InternalCredential, String) created}
 * or a password is {@link #beforeSetPassword(InternalUserPrincipal, Collection, String, InternalCredential, String, boolean) updated}
 * a new future expiration date is calculated.</p>
 * <p>
 * An existing or already provided higher expiration date will be preserved though. 
 * This allows to (pre)set a (very) high expiration date, like with {@link InternalCredential#MAX_DATE},
 * for credentials which shouldn't expire.</p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PasswordExpirationInterceptor extends AbstractInternalPasswordCredentialInterceptorImpl
{
    private long maxLifeSpanInMillis;
    
    /**
     * @param maxLifeSpanInDays default lifespan of password credentials in days
     */
    public PasswordExpirationInterceptor(int maxLifeSpanInDays)
    {
        this.maxLifeSpanInMillis = (long)(maxLifeSpanInDays) * 1000*60*60*24;
    }
    
    /**
     * @return true when the password credential is now expired
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterAuthenticated(org.apache.jetspeed.security.om.InternalUserPrincipal, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, boolean)
     */
    public boolean afterAuthenticated(InternalUserPrincipal internalUser, String userName,
            InternalCredential credential, boolean authenticated) throws SecurityException
    {
        boolean update = false;
        if ( !credential.isExpired() )
        {
            long expirationTime = credential.getExpirationDate().getTime();
            long currentTime     = new java.util.Date().getTime();
            if (expirationTime <= currentTime)
            {
                credential.setExpired(true);
                update = true;
            }
        }
        return update;
    }
    
    /**
     * @return true when a new default expiration date is set
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterLoad(org.apache.jetspeed.security.spi.PasswordCredentialProvider, java.lang.String, org.apache.jetspeed.security.om.InternalCredential)
     */
    public boolean afterLoad(PasswordCredentialProvider pcProvider, String userName, InternalCredential credential)
            throws SecurityException
    {
        boolean update = false;
        if ( credential.getExpirationDate() == null )
        {
            credential.setExpirationDate(new Date(new java.util.Date().getTime()+maxLifeSpanInMillis));
            update = true;
        }
        return update;
    }
    
    /**
     * Calculates and sets the default expiration date and the expired flag to false 
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeCreate(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, InternalCredential, java.lang.String)
     */
    public void beforeCreate(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password) throws SecurityException
    {
        setExpiration(credential);
    }
    
    /**
     * Sets a new expiration date if a higher expiration date isn't set already and resets the expired flag
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeSetPassword(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, java.lang.String, boolean)
     */
    public void beforeSetPassword(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password, boolean authenticated) throws SecurityException
    {
        setExpiration(credential);
    }
    
    protected void setExpiration(InternalCredential credential)
    {
        Date nextExpirationDate = new Date(new java.util.Date().getTime()+maxLifeSpanInMillis);
        if ( credential.getExpirationDate() == null || credential.getExpirationDate().before(nextExpirationDate))
        {
            credential.setExpirationDate(nextExpirationDate);
        }
        credential.setExpired(false);
    }
}
