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

package org.apache.jetspeed.om.preference.impl;

import java.io.Serializable;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * @version $Id$
 *
 */
public class PreferenceValueImpl implements Serializable, PersistenceBrokerAware
{
    private long prefId;
    private short index;
    private String userName;
    private Long entityOid;
    private String entityId;
    private boolean readOnly;
    private boolean nullValue;
    private String value;
    
    public long getPrefId()
    {
        return prefId;
    }
    public void setPrefId(long prefId)
    {
        this.prefId = prefId;
    }
    public short getIndex()
    {
        return index;
    }
    public void setIndex(short index)
    {
        this.index = index;
    }
    public String getUserName()
    {
        return userName;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    public Long getEntityOid()
    {
        return entityOid;
    }
    public void setEntityOid(Long entityOid)
    {
        this.entityOid = entityOid;
    }
    public String getEntityId()
    {
        return entityId;
    }
    public void setEntityId(String entityId)
    {
        this.entityId = entityId;
    }
    public boolean isReadOnly()
    {
        return readOnly;
    }
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
        nullValue = value == null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterLookup(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        if (!nullValue && value == null)
        {
            value = "";
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }
}
