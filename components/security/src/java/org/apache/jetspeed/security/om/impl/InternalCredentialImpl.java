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

import java.sql.Timestamp;

import org.apache.jetspeed.security.om.InternalCredential;

/**
 * <p>{@link InternalCredential} interface implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
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
     * <p>InternalPrincipal constructor given a value, type and classname.</p>
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
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
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
        boolean isEqual = ((c.getValue().equals(this.getValue())) && (c.getType() == this.getType()) && ((null == c.getClassname()) || (c.getClassname().equals(this.getClassname()))));
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
            + "[type, " + this.type + "], "
            + "[classname, " + this.classname + "], "
            + "[creationDate, " + this.creationDate + "], "
            + "[modifiedDate, " + this.modifiedDate + "]]";
        return toStringCredential;
    }
}
