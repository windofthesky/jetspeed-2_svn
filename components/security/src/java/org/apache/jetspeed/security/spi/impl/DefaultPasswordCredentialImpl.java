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

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;

import org.apache.jetspeed.security.PasswordCredential;

/**
 * <p>
 * Default Password credential implementation. Provides the same mechanism as J2EE
 * <code>javax.resource.spi.security.PasswordCredential</code>.
 * </p>
 * 
 * <p>
 * Code borrowed from the Geronimo project.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class DefaultPasswordCredentialImpl implements PasswordCredential, Serializable
{

    /** The user name. */
    private String userName;

    /** The password. */
    private char[] password;

    /** The update required state */
    private boolean updateRequired;
    
    /** The enabled state. */
    private boolean enabled = true;
    
    /** The expired state. */
    private boolean expired;
    
    /** The expiration date. */
    private Date expirationDate;
    
    /** The last logged in date */
    private Timestamp lastLogonDate;
    
    /**
     * @param userName
     * @param password
     */
    public DefaultPasswordCredentialImpl(String userName, char[] password)
    {
        this.userName = userName;
        this.password = (char[]) password.clone();
    }
    
    public DefaultPasswordCredentialImpl(String userName, char[] password, boolean updateRequired, boolean enabled, 
            boolean expired, Date expirationDate, Timestamp lastLogonDate)
    {
        this(userName, password);
        this.updateRequired = updateRequired;
        this.enabled = enabled;
        this.expired = expired;
        this.expirationDate = expirationDate;
        this.lastLogonDate = lastLogonDate;
    }
    
    /**
     * @return The username.
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @return The password.
     */
    public char[] getPassword()
    {
        return (char[]) password.clone();
    }
    
    
    /**
     * @see org.apache.jetspeed.security.PasswordCredential#isUpdateRequired()
     */
    public boolean isUpdateRequired()
    {
        return updateRequired;
    }

    /**
     * @see org.apache.jetspeed.security.PasswordCredential#isEnabled()
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * @see org.apache.jetspeed.security.PasswordCredential#isExpired()
     */
    public boolean isExpired()
    {
        return expired;
    }

    /**
     * @see org.apache.jetspeed.security.PasswordCredential#getExpirationDate()
     */
    public Date getExpirationDate()
    {
        return expirationDate;
    }
    
    /**
     * @see org.apache.jetspeed.security.PasswordCredential#getLastLogonDate()
     */
    public Timestamp getLastLogonDate()
    {
        return lastLogonDate;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof DefaultPasswordCredentialImpl))
            return false;

        final DefaultPasswordCredentialImpl credential = (DefaultPasswordCredentialImpl) o;

        if (!Arrays.equals(password, credential.password))
            return false;
        if (!userName.equals(credential.userName))
            return false;

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int result = userName.hashCode();
        for (int i = 0; i < password.length; i++)
        {
            result *= password[i];
        }
        return result;
    }
}