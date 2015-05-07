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

import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.impl.SynchronizationStateAccess;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @version $Id$
 *
 */
public abstract class PersistentJetspeedPrincipal extends TransientJetspeedPrincipal implements PersistenceBrokerAware, DistributedCacheObject, Serializable
{
    private Long id;
    private Timestamp creationDate;
    private Timestamp modifiedDate;
    private boolean enabled = true;
    private boolean mapped;
    private boolean readOnly;
    private boolean removable = true;
    private boolean extendable = true;

    @SuppressWarnings("unchecked")
    protected Collection attributes;
       
    public PersistentJetspeedPrincipal()
    {   
    	super();
    }
      
    public PersistentJetspeedPrincipal(String name)
    {   
    	super(name);
    }
    
    public Long getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Timestamp getCreationDate()
    {
        return creationDate;
    }
    
    public Timestamp getModifiedDate()
    {
        return modifiedDate;
    }
    
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled) throws SecurityException
    {
        if (isReadOnly() && !isSynchronizing())
        {
            throw new SecurityException(SecurityException.PRINCIPAL_IS_READ_ONLY.createScoped(getType().getName(), getName()));
        }
        this.enabled = enabled;
    }
    
    public boolean isTransient()
    {
        return false;
    }
    
    public boolean isMapped()
    {
        return mapped;
    }
    
    public void setMapped(boolean mapped)
    {
        this.mapped = mapped;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }
    
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public boolean isRemovable()
    {
        return removable;
    }
    
    public void setRemovable(boolean removable)
    {
        this.removable = removable;
    }

    public boolean isExtendable()
    {
        return extendable;
    }
    
    public void setExtendable(boolean extendable)
    {
        this.extendable = extendable;
    }
    
    public synchronized SecurityAttributes getSecurityAttributes()
    {
        if (sa == null)
        {
            if (attributes == null)
            {
                attributes = new ArrayList<SecurityAttributeValue>();
            }
            sa = new SecurityAttributesImpl(this, attributes, isReadOnly(), isExtendable());
        }
        return sa;
    }
    
    public Map<String, String> getInfoMap()
    {
        return getSecurityAttributes().getInfoMap();
    }
    
    /// OJB PersistenceBrokerAware interface implementation

    public void afterDelete(PersistenceBroker pb) throws PersistenceBrokerException
    {
    }

    public synchronized void afterInsert(PersistenceBroker pb) throws PersistenceBrokerException
    {
        this.sa = null;
    }

    public void afterLookup(PersistenceBroker pb) throws PersistenceBrokerException
    {
    }

    public void afterUpdate(PersistenceBroker pb) throws PersistenceBrokerException
    {
    }

    public void beforeDelete(PersistenceBroker pb) throws PersistenceBrokerException
    {
    }

    public void beforeInsert(PersistenceBroker pb) throws PersistenceBrokerException
    {
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    public void beforeUpdate(PersistenceBroker pb) throws PersistenceBrokerException
    {
        this.modifiedDate = new Timestamp(System.currentTimeMillis());
    }
    
    protected boolean isSynchronizing(){
        return SynchronizationStateAccess.isSynchronizing();
    }

    @Override
    public void notifyChange(int action)
    {
    }
}
