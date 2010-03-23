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
import org.apache.jetspeed.security.UserCredential;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * <p>
 * Default Password credential implementation
 * </p>
 * 
 * @version $Id$
 */
public class PasswordCredentialImpl implements PasswordCredential, PersistenceBrokerAware
{
    private static final long serialVersionUID = -4975305752376365096L;
    
    private boolean persistent;
    
    @SuppressWarnings("unused")
    private Long principalId;
    
    private User user;
    
    private String userName;
    
    /**
     * The saved current "raw" password value
     */
    private String currentPassword;

    /** The "raw" password value */
    private String password;
    
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
     * flag indicating if the password is encoded
     */
    private boolean encoded;
    
    /**
     * flag indicating if the current password is encoded
     */
    private boolean currentEncoded;
    
    /**
     * flag (default true) indicating if the credential password is updatable (e.g. by the user itself).
     */
    private boolean updateAllowed = true;
    
    /**
     * flag (default false) indicating if the credential password state is read only.
     */
    private boolean stateReadOnly = false;

    /** The update required state */
    private boolean updateRequired;
    
    /** The enabled state. */
    private boolean enabled = true;
    
    /** The expired state. */
    private boolean expired;
    
    /** The creation date. */
    private Timestamp creationDate;
    
    /** The modified date. */
    private Timestamp modifiedDate;
    
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
    private Short type = TYPE_CURRENT;

    public PasswordCredentialImpl()
    {        
    }
    
    public PasswordCredentialImpl(User user, String password)
    {
        setUser(user);
        this.password = password;
    }
    
    public void synchronize(UserCredential pwc)
    {
        throw new UnsupportedOperationException();
    }
    
    public boolean isNew()
    {
        return !persistent;
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
        if (user != null && user.getId() != null)
        {
            this.principalId = user.getId();
        }
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
    
    public Short getType()
    {
        return type;
    }
    
    /**
     * @return The password.
     */
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password, boolean encoded)
    {
        checkUpdatePassword();
        if (password != null && (this.password == null || encoded != this.encoded || !password.equals(this.password)))
        {
            if (!newPasswordSet && currentPassword == null)
            {
                this.currentPassword = this.password;
                this.currentEncoded = this.encoded;
            }
            this.password = password;
            this.encoded = encoded;
            oldPassword = null;
            newPassword = null;
            newPasswordSet = true;
        }
    }
    
    public void setPassword(String oldPassword, String newPassword)
    {
        checkUpdatePassword();
        if (newPassword != null && (oldPassword == null || !newPassword.equals(oldPassword)))
        {
            if (!newPasswordSet && currentPassword == null)
            {
                currentPassword = this.password;
                this.currentEncoded = this.encoded;
            }
            this.newPassword = newPassword;
            this.oldPassword = oldPassword;
            newPasswordSet = true;
        }
    }
    
    public void clearNewPasswordSet()
    {
        currentPassword = null;
        oldPassword = null;
        newPassword = null;
        newPasswordSet = false;
    }
    
    public void revertNewPasswordSet()
    {
        if (newPasswordSet)
        {
            password = currentPassword;
            encoded = currentEncoded;
            currentPassword = null;
            oldPassword = null;
            newPassword = null;
            newPasswordSet = false;
        }
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
    
    public boolean isEncoded()
    {
        return encoded;
    }
    
    public void setEncoded(boolean encoded)
    {
        checkUpdatePassword();
        this.encoded = encoded;
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
        if (enabled)
        {
            setAuthenticationFailures(0);
        }
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

    public Timestamp getCreationDate()
    {
        return creationDate;
    }

    public Timestamp getModifiedDate()
    {
        return modifiedDate;
    }

    public Date getExpirationDate()
    {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate)
    {
        checkUpdateState();
        this.expirationDate = expirationDate;
        if (expirationDate != null && new Date(new java.util.Date().getTime()).after(expirationDate))
        {
            setExpired(true);
        }
        else
        {
            setExpired(false);
        }
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

    //
    /// OJB PersistenceBrokerAware interface implementation
    //
    public void afterDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        persistent = false;
    }

    public void afterInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        persistent = true;
    }

    public void afterLookup(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        persistent = true;
    }

    public void afterUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void beforeDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void beforeInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    public void beforeUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        this.modifiedDate = new Timestamp(System.currentTimeMillis());
    }
}