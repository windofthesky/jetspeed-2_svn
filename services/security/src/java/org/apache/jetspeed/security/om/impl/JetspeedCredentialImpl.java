/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.om.impl;

import java.sql.Timestamp;

import org.apache.jetspeed.security.om.JetspeedCredential;

/**
 * <p>{@link JetspeedCredential} interface implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class JetspeedCredentialImpl implements JetspeedCredential
{

    /**
     * <p>JetspeedCredential implementation default constructor.</p>
     */
    public JetspeedCredentialImpl()
    {
    }

    /**
     * <p>JetspeedPrincipal constructor given a value, type and classname.</p>
     * @param principalId The principal id.
     * @param value The value.
     * @param type The type.
     * @param classname The classname.
     */
    public JetspeedCredentialImpl(int principalId, String value, short type, String classname)
    {
        this.principalId = principalId;
        this.value = value;
        this.type = type;
        this.classname = classname;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    private int credentialId;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#getCredentialId()
     */
    public int getCredentialId()
    {
        return this.credentialId;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#setCredentialId(int)
     */
    public void setCredentialId(int credentialId)
    {
        this.credentialId = credentialId;
    }

    private int principalId;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#getPrincipalId()
     */
    public int getPrincipalId()
    {
        return this.principalId;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#setPrincipalId(int)
     */
    public void setPrincipalId(int principalId)
    {
        this.principalId = principalId;
    }

    private String value;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#getValue()
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    private short type;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#getType()
     */
    public short getType()
    {
        return this.type;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#setType(short)
     */
    public void setType(short type)
    {
        this.type = type;
    }

    private String classname;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#getClassname()
     */
    public String getClassname()
    {
        return this.classname;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#setClassname(java.lang.String)
     */
    public void setClassname(String classname)
    {
        this.classname = classname;
    }

    private Timestamp creationDate;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedCredential#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    /**
     * <p>Compares this {@link JetspeedCredential} to the provided credential
     * and check if they are equal.</p>
     * return Whether the {@link JetspeedCredential} are equal.
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof JetspeedCredential))
            return false;

        JetspeedCredential c = (JetspeedCredential) object;
        boolean isEqual = ((c.getValue().equals(this.getValue())) && (c.getType() == this.getType()) && ((null == c.getClassname()) || (c.getClassname().equals(this.getClassname()))));
        return isEqual;
    }

}
