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
import java.util.Collection;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalReadOnlyException;
import org.apache.jetspeed.security.SecurityAttributes;

/**
 * @version $Id$
 *
 */
public class BaseJetspeedPrincipal implements JetspeedPrincipal, Serializable
{
    private static final long serialVersionUID = 5484179899807809619L;
    
    private static JetspeedPrincipalManagerProvider jpmp;
    
    private Long id;
    private String name;    
    private Timestamp creationDate;
    private Timestamp modifiedDate;
    private boolean enabled;
    private boolean mapped;
    private boolean readOnly;
    private boolean removable;
    private boolean extendable;
    @SuppressWarnings("unchecked")
    private Collection avColl;
    
    private transient JetspeedPrincipalType jpt;
    private transient SecurityAttributes attributes;
    
    public static void setJetspeedPrincipalManagerProvider(JetspeedPrincipalManagerProvider jpmp)
    {
        BaseJetspeedPrincipal.jpmp = jpmp;
    }
    
    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public synchronized JetspeedPrincipalType getType()
    {
        if (jpt == null)
        {
            jpt = jpmp.getPrincipalTypeByClassName(getClass().getName());
        }
        return jpt;
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

    public void setEnabled(boolean enabled) throws PrincipalReadOnlyException
    {
        if (isReadOnly())
        {
            throw new PrincipalReadOnlyException();
        }
        this.enabled = enabled;
    }
    
    public boolean isMapped()
    {
        return mapped;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public boolean isRemovable()
    {
        return removable;
    }

    public boolean isExtendable()
    {
        return extendable;
    }
    
    public synchronized SecurityAttributes getSecurityAttributes()
    {
        if (attributes == null)
        {
            attributes = new SecurityAttributesImpl(this, avColl, isReadOnly(), isExtendable());
        }
        return attributes;
    }
}
