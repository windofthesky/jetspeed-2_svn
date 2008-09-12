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

import java.sql.Date;

import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.CredentialPasswordValidator;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Enforces a maximum lifespan for a password credential.</p>
 * When {@link #afterAuthenticated(PasswordCredential, boolean) on authentication}
 * a password its expiration date is reached, its expired flag is set.
 * <p>
 * To ensure proper expiration handling, an empty (null) expiration date will be automatically
 * filled in when the credential is loaded from the persistent store using the {@link #PasswordExpirationInterceptor(int) configured} 
 * max lifespan in days.</p>
 * <p>
 * When a password credential is {@link #beforeCreate(PasswordCredential) created}
 * or a password is {@link #beforeSetPassword(PasswordCredential, String) updated}
 * a new future expiration date is calculated.</p>
 * <p>
 * An existing or already provided higher expiration date will be preserved though. 
 * This allows to (pre)set a (very) high expiration date for credentials which shouldn't expire.</p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PasswordExpirationInterceptor extends AbstractPasswordCredentialInterceptorImpl
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
     */
    public boolean afterAuthenticated(PasswordCredential credential, boolean authenticated) throws SecurityException
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
     */
    public boolean afterLoad(String userName, PasswordCredential credential, CredentialPasswordEncoder encoder, CredentialPasswordValidator validator) throws SecurityException
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
     */
    public void beforeCreate(PasswordCredential credential) throws SecurityException
    {
        setExpiration(credential);
    }
    
    /**
     * Sets a new expiration date if a higher expiration date isn't set already and resets the expired flag
     */
    public void beforeSetPassword(PasswordCredential credential, String password) throws SecurityException
    {
        setExpiration(credential);
    }
    
    protected void setExpiration(PasswordCredential credential)
    {
        Date nextExpirationDate = new Date(new java.util.Date().getTime()+maxLifeSpanInMillis);
        if ( credential.getExpirationDate() == null || credential.getExpirationDate().before(nextExpirationDate))
        {
            credential.setExpirationDate(nextExpirationDate);
        }
        credential.setExpired(false);
    }
}
