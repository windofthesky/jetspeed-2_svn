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
package org.apache.jetspeed.security.om.impl;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.jetspeed.security.om.InternalCredential;

/**
 * <p>{@link InternalCredential} interface implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 * @version $Id$
 */
public class InternalCredentialImpl implements InternalCredential
{
    /**
     * <p>InternalCredential implementation default constructor.</p>
     */
    public InternalCredentialImpl()
    {
    }

    /**
     * <p>InternalCredentialImpl constructor given a value, type and classname.</p>
     * @param principalId The principal id.
     * @param value The value.
     * @param type The type.
     * @param classname The classname.
     */
    public InternalCredentialImpl(long principalId, String value, int type, String classname)
    {
        this.principalId = principalId;
        this.value = value;
        this.type = type;
        this.classname = classname;
        this.creationDate = new Timestamp(new java.util.Date().getTime());
        this.modifiedDate = this.creationDate;
    }
    
    /**
     * <p>InternalCredentialImpl copy constructor given another InternalCredential and overriding classname</p>
     * @param credential The credential to copy from
     * @param classname The classname for the new credential
     */
    public InternalCredentialImpl(InternalCredential credential, String classname)
    {
        this.authenticationFailures = credential.getAuthenticationFailures();
        this.classname = classname;
        this.creationDate = credential.getCreationDate();
        this.enabled = credential.isEnabled();
        this.encoded = credential.isEncoded();
        this.expirationDate = credential.getExpirationDate();
        this.expired = credential.isExpired();
        this.previousAuthenticationDate = credential.getPreviousAuthenticationDate();
        this.lastAuthenticationDate = credential.getLastAuthenticationDate();
        this.modifiedDate = credential.getModifiedDate();
        this.principalId = credential.getPrincipalId();
        this.type = credential.getType();
        this.updateRequired = credential.isUpdateRequired();
        this.value = credential.getValue();
    }

    private long credentialId;

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getCredentialId()
     */
    public long getCredentialId()
    {
        return this.credentialId;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setCredentialId(long)
     */
    public void setCredentialId(long credentialId)
    {
        this.credentialId = credentialId;
    }

    private long principalId;

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getPrincipalId()
     */
    public long getPrincipalId()
    {
        return this.principalId;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setPrincipalId(long)
     */
    public void setPrincipalId(long principalId)
    {
        this.principalId = principalId;
    }

    private String value;

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getValue()
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    
    private boolean updateRequired;
    
    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#isUpdateRequired()
     */
    public boolean isUpdateRequired()
    {
        return updateRequired;
    }
    
    /*
     * @see org.apache.jetspeed.security.om.InternalCredential#setUpdateRequired(boolean)
     */
    public void setUpdateRequired(boolean updateRequired)
    {
        this.updateRequired = updateRequired;
    }
    
    private boolean encoded;
    
    /** 
     * @see org.apache.jetspeed.security.om.InternalCredential#isEncoded()
     */
    public boolean isEncoded()
    {
        return encoded;
    }
    
    /** 
     * @see org.apache.jetspeed.security.om.InternalCredential#setEncoded(boolean)
     */
    public void setEncoded(boolean encoded)
    {
        this.encoded = encoded;
    }
    
    private boolean enabled = true;
    
    /** 
     * @see org.apache.jetspeed.security.om.InternalCredential#isEnabled()
     */
    public boolean isEnabled()
    {
        return enabled;
    }
    
    /** 
     * @see org.apache.jetspeed.security.om.InternalCredential#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    private int authenticationFailures;
    
    /** 
     * @see org.apache.jetspeed.security.om.InternalCredential#getAuthenticationFailures()
     */
    public int getAuthenticationFailures()
    {
        return authenticationFailures;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setAuthenticationFailures(int)
     */
    public void setAuthenticationFailures(int authenticationFailures)
    {
        this.authenticationFailures = authenticationFailures;
    }

    private boolean expired;
    
    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#isExpired()
     */
    public boolean isExpired()
    {
        return expired;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setExpired(boolean)
     */
    public void setExpired(boolean expired)
    {
        this.expired = expired;
    }
    
    private Date expirationDate;
    
    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getExpirationDate()
     */
    public Date getExpirationDate()
    {
        return expirationDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setExpirationDate(java.sql.Timestamp)
     */
    public void setExpirationDate(Date expirationDate)
    {
        this.expirationDate = expirationDate;
    }
    
    private int type;

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getType()
     */
    public int getType()
    {
        return this.type;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setType(int)
     */
    public void setType(int type)
    {
        this.type = type;
    }

    private String classname;

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getClassname()
     */
    public String getClassname()
    {
        return this.classname;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setClassname(java.lang.String)
     */
    public void setClassname(String classname)
    {
        this.classname = classname;
    }

    private Timestamp creationDate;

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    private Timestamp previousAuthenticationDate;

    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getPreviousAuthenticationDate()
     */
    public Timestamp getPreviousAuthenticationDate()
    {
        return previousAuthenticationDate;
    }
    
    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setPreviousAuthenticationDate(java.sql.Timestamp)
     */
    public void setPreviousAuthenticationDate(Timestamp previousAuthenticationDate)
    {
        this.previousAuthenticationDate = previousAuthenticationDate;
    }

    private Timestamp lastAuthenticationDate;
    
    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#getLastAuthenticationDate()
     */
    public Timestamp getLastAuthenticationDate()
    {
        return lastAuthenticationDate;
    }
    
    /**
     * @see org.apache.jetspeed.security.om.InternalCredential#setLastAuthenticationDate(java.sql.Timestamp)
     */
    public void setLastAuthenticationDate(Timestamp lastAuthenticationDate)
    {
        this.lastAuthenticationDate = lastAuthenticationDate;
    }
    
    /**
     * <p>Compares this {@link InternalCredential} to the provided credential
     * and check if they are equal.</p>
     * return Whether the {@link InternalCredential} are equal.
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof InternalCredential))
            return false;

        InternalCredential c = (InternalCredential) object;
        boolean isEqual = (((null == c.getClassname()) || (c.getClassname().equals(this.getClassname()))) &&
                            (c.getValue().equals(this.getValue())) && 
                            (c.getType() == this.getType()));
        return isEqual;
    }

    /**
     * <p>Convert <code>Node</code> to string.</p>
     * @return The Node string value.
     */
    public String toString()
    {
        String toStringCredential = "[[principalId, " + this.principalId + "], "
            + "[value, " + this.value + "], "
            + "[updateRequired, " + this.updateRequired + "], "
            + "[encoded, " + this.encoded + "], "
            + "[enabled, " + this.enabled + "], "
            + "[authenticationFailures, "+ this.authenticationFailures + "], "
            + "[expired, "+ this.expired + "], "
            + "[type, " + this.type + "], "
            + "[classname, " + this.classname + "], "
            + "[creationDate, " + this.creationDate + "], "
            + "[modifiedDate, " + this.modifiedDate + "], "
            + "[previousAuthenticationDate, " + this.previousAuthenticationDate + "]"
            + "[lastAuthenticationDate, " + this.lastAuthenticationDate + "]"
            + (expirationDate != null ? (", [expirationDate, "+ this.expirationDate + "]]") : "]");
        return toStringCredential;
    }    
}
