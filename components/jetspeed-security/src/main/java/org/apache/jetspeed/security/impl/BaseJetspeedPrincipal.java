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

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityAttributes;

/**
 * @version $Id$
 *
 */
public class BaseJetspeedPrincipal implements JetspeedPrincipal, Serializable
{
    private static final long serialVersionUID = 5484179899807809619L;

    public Timestamp getCreationDate()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Timestamp getModifiedDate()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public SecurityAttributes getSecurityAttributes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JetspeedPrincipalType getType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isEnabled()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isExtendable()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isMapped()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isReadOnly()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRemovable()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void setCreationDate(Timestamp creationDate)
    {
        // TODO Auto-generated method stub
    }

    public void setEnable(boolean enabled)
    {
        // TODO Auto-generated method stub
    }

    public void setExtendable(boolean extendable)
    {
        // TODO Auto-generated method stub
    }

    public void setMapped(boolean mapped)
    {
        // TODO Auto-generated method stub
    }

    public void setModifiedDate(Timestamp modifiedDate)
    {
        // TODO Auto-generated method stub
    }

    public void setName(String name)
    {
        // TODO Auto-generated method stub
    }

    public void setReadonly(boolean readonly)
    {
        // TODO Auto-generated method stub
    }

    public void setRemovable(boolean removable)
    {
        // TODO Auto-generated method stub
    }
}
