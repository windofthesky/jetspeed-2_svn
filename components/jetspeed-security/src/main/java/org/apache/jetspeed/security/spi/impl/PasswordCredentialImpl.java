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
import java.sql.Timestamp;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.User;

/**
 * <p>
 * Default Password credential implementation
 * </p>
 * 
 * @version $Id$
 */
public class PasswordCredentialImpl implements PasswordCredential
{
    private static final long serialVersionUID = -4975305752376365096L;
    
    private User user;
    
    private String userName;

    /** The "raw" password value */
    private char[] password;
    
    /**
     * the "old" password for authenticating password change (if set)
     */
    private String oldPassword;
    /**
     * the "new" password to be used for changing the real password
     */
    private String newPassword;
    
    /**
     * flag indicating a new password value is set to be processed
     */
    private boolean newPasswordSet;
    
    /**
     * flag indicating if the current password is encoded
     */
    private boolean passwordEncoded;
    
    private boolean updateAllowed = true;
    
    private boolean stateReadOnly = false;

    /** The update required state */
    private boolean updateRequired;
    
    /** The enabled state. */
    private boolean enabled = true;
    
    /** The expired state. */
    private boolean expired;
    
    /** The creation date. */
    private Date creationDate;
    
    /** The expiration date. */
    private Date expirationDate;
    
    /** The previous authentication in date */
    private Timestamp previousAuthenticationDate;
    
    /** The last authentication in date */
    private Timestamp lastAuthenticationDate;
    
    /** The number of authentication failures */
    private int authenticationFailures;
    
    /**
     * The type mapping field
     */
    @SuppressWarnings("unused")
    private Integer type = TYPE_CURRENT;

    public PasswordCredentialImpl()
    {        
    }
    
    public PasswordCredentialImpl(User user, char[] password)
    {
        this.user = user;
        this.password = password;
    }
    
    private void checkUpdatePassword()
    {
        if (!updateAllowed)
        {
            throw new IllegalStateException();
        }
    }
    
    private void checkUpdateState()
    {
        if (stateReadOnly)
        {
            throw new IllegalStateException();
        }
    }
    
    public void setUser(User user)
    {
        this.user = user;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    /**
     * @return The username.
     */
    public String getUserName()
    {
        return user != null ? user.getName() : userName;
    }
    
    public User getUser()
    {
        return user;
    }
    
    public Integer getType()
    {
        return type;
    }

    /**
     * @return The password.
     */
    public char[] getPassword()
    {
        return password != null ? (char[]) password.clone() : null;
    }
    
    public void setPassword(char[] password, boolean encoded)
    {
        checkUpdatePassword();
        char[] value = password.clone();
        this.passwordEncoded = encoded;
        if (!value.equals(password))
        {
            this.password = value;
            oldPassword = null;
            newPassword = null;
            newPasswordSet = true;
        }
    }
    
    public void setPassword(String oldPassword, String newPassword)
    {
        checkUpdatePassword();
        if (!newPassword.equals(oldPassword))
        {
            this.newPassword = newPassword;
            this.oldPassword = oldPassword;
            password = null;
            passwordEncoded = false;
            newPasswordSet = true;
        }
    }
    
    public void clearNewPasswordSet()
    {
        oldPassword = null;
        newPassword = null;
        newPasswordSet = false;
    }
    
    public String getOldPassword()
    {
        return oldPassword;
    }
    
    public String getNewPassword()
    {
        return newPassword;
    }
    
    public boolean isNewPasswordSet()
    {
        return newPasswordSet;
    }
    
    public boolean isPasswordEncoded()
    {
        return passwordEncoded;
    }
    
    public void setPasswordEncoded(boolean passwordEncoded)
    {
        checkUpdatePassword();
        this.passwordEncoded = passwordEncoded;
    }
    
    public boolean isUpdateAllowed()
    {
        return updateAllowed;
    }
    
    public void setUpdateAllowed(boolean updateAllowed)
    {
        this.updateAllowed = true;
    }
    
    public boolean isStateReadOnly()
    {
        return stateReadOnly;
    }
    
    public void setStateReadOnly(boolean stateReadOnly)
    {
        this.stateReadOnly = stateReadOnly; 
    }
    
    public boolean isUpdateRequired()
    {
        return updateRequired;
    }
    
    public void setUpdateRequired(boolean updateRequired)
    {
        checkUpdateState();
        this.updateRequired = updateRequired;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
    
    public void setEnabled(boolean enabled)
    {
        checkUpdateState();
        this.enabled = enabled;
    }

    public boolean isExpired()
    {
        return expired;
    }
    
    public void setExpired(boolean expired)
    {
        checkUpdateState();
        this.expired = expired;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate()
    {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate)
    {
        checkUpdateState();
        this.expirationDate = expirationDate;
    }
    
    public Timestamp getPreviousAuthenticationDate()
    {
        return previousAuthenticationDate;
    }
    
    public void setPreviousAuthenticationDate(Timestamp previousAuthenticationDate)
    {
        checkUpdateState();
        this.previousAuthenticationDate = previousAuthenticationDate;
    }

    public Timestamp getLastAuthenticationDate()
    {
        return lastAuthenticationDate;
    }

    public void setLastAuthenticationDate(Timestamp lastAuthenticationDate)
    {
        checkUpdateState();
        this.lastAuthenticationDate = lastAuthenticationDate;
    }

    public int getAuthenticationFailures()
    {
        return authenticationFailures;
    }

    public void setAuthenticationFailures(int authenticationFailures)
    {
        checkUpdateState();
        this.authenticationFailures = authenticationFailures;
    }
}