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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.SecurityException;

/**
 * @$
 *
 */
public abstract class TransientJetspeedPrincipal implements JetspeedPrincipal, Serializable
{
    protected static JetspeedPrincipalManagerProvider jpmp;
    
    protected String name;
    @SuppressWarnings("unused")
    private String type;
    
    protected transient JetspeedPrincipalType jpt;
    protected transient SecurityAttributes sa;
    private Long domainId;
    
    public static void setJetspeedPrincipalManagerProvider(JetspeedPrincipalManagerProvider jpmp)
    {
        TransientJetspeedPrincipal.jpmp = jpmp;
    }
    
    protected TransientJetspeedPrincipal()
    {
        type = jpmp.getPrincipalTypeByClassName(getClass().getName()).getName();
    }
    
    protected TransientJetspeedPrincipal(String type, String name)
    {
        this.name = name;
        this.type = type;
    }
    
    public TransientJetspeedPrincipal(String name)
    {
        this();
    	this.name = name;
    }
    
    public Long getId()
    {
        return null;
    }

    public String getName()
    {
        return name;
    }   

    public synchronized JetspeedPrincipalType getType()
    {
        if (jpt == null)
        {
            jpt = jpmp.getPrincipalType(type);
        }
        return jpt;
    }

    public Timestamp getCreationDate()
    {
        return null;
    }
    
    public Timestamp getModifiedDate()
    {
        return null;
    }
    
    public boolean isTransient()
    {
        return true;
    }
    
    public boolean isEnabled()
    {
        return true;
    }

    public void setEnabled(boolean enabled) throws SecurityException
    {
        throw new UnsupportedOperationException();
    }
    
    public boolean isMapped()
    {
        return false;
    }
    
    public void setMapped(boolean mapped)
    {
        throw new UnsupportedOperationException();
    }

    public boolean isReadOnly()
    {
        return false;
    }
    
    public void setReadOnly(boolean readOnly)
    {
        throw new UnsupportedOperationException();
    }

    public boolean isRemovable()
    {
        return false;
    }
    
    public void setRemovable(boolean removable)
    {
        throw new UnsupportedOperationException();
    }

    public boolean isExtendable()
    {
        return true;
    }
    
    public void setExtendable(boolean extendable)
    {
        throw new UnsupportedOperationException();
    }
    
    public synchronized SecurityAttributes getSecurityAttributes()
    {
        if (sa == null)
        {
            sa = new SecurityAttributesImpl(this);
        }
        return sa;
    }
    
    public Map<String, String> getInfoMap()
    {
        return getSecurityAttributes().getInfoMap();
    }

    public String toString()
    {
        return getType().getName()+": "+getName()+" (id: "+(getId())+", transient: "+isTransient()+", mapped: "+isMapped()+")";
    }
    
    public Long getDomainId()
    {
        return domainId;
    }

    public void setDomainId(Long domainId){
        this.domainId=domainId;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (o instanceof JetspeedPrincipal)
        {
            JetspeedPrincipal other = (JetspeedPrincipal)o;
            return (getName().equals(other.getName()) &&
                    getType().equals(other.getType()) &&
                    (((getDomainId() != null) ? getDomainId().longValue() : 0L) == ((other.getDomainId() != null) ? other.getDomainId().longValue() : 0L)));
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getName().hashCode()^getType().hashCode()^((getDomainId() != null) ? getDomainId().intValue() : 0);
    }
}
