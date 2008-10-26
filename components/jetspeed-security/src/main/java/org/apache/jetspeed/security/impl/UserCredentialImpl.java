/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.security.impl;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.jetspeed.security.UserCredential;

/**
 * @version $Id$
 *
 */
public class UserCredentialImpl implements UserCredential
{
    private static final long serialVersionUID = 8445207990171015069L;
    private int authenticationFailures;
    private Timestamp creationDate;
    private Timestamp modifiedDate;
    private Date expirationDate;
    private Timestamp lastAuthenticationDate;
    private String userName;
    private Timestamp previousAuthenticationDate;
    private boolean enabled;
    private boolean expired;    
    private boolean updateAllowed;
    private boolean updateRequired;
    
    public UserCredentialImpl(UserCredential pwc)
    {
        synchronize(pwc);
    }
    
    public void synchronize(UserCredential pwc)
    {
        this.authenticationFailures = pwc.getAuthenticationFailures();
        this.creationDate = pwc.getCreationDate();
        this.modifiedDate = pwc.getModifiedDate();
        this.expirationDate = pwc.getExpirationDate();
        this.lastAuthenticationDate = pwc.getLastAuthenticationDate();
        this.userName = pwc.getUserName();
        this.previousAuthenticationDate = pwc.getPreviousAuthenticationDate();
        this.enabled = pwc.isEnabled();
        this.expired = pwc.isExpired();
        this.updateAllowed = pwc.isUpdateAllowed();
        this.updateRequired = pwc.isUpdateRequired();
    }
    
    public int getAuthenticationFailures()
    {
        return authenticationFailures;
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

    public Timestamp getLastAuthenticationDate()
    {
        return lastAuthenticationDate;
    }

    public String getUserName()
    {
        return userName;
    }

    public Timestamp getPreviousAuthenticationDate()
    {
        return previousAuthenticationDate;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public boolean isExpired()
    {
        return expired;
    }

    public boolean isUpdateAllowed()
    {
        return updateAllowed;
    }

    public boolean isUpdateRequired()
    {
        return updateRequired;
    }
}
