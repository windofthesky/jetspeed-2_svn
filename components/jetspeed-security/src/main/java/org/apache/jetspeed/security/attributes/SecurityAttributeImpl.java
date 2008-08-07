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
package org.apache.jetspeed.security.attributes;

import java.io.Serializable;
import java.security.Principal;

import org.apache.jetspeed.security.BasePrincipal;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.RemotePrincipal;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.UserPrincipal;

public class SecurityAttributeImpl implements SecurityAttribute, Serializable
{        
    private static final long serialVersionUID = 4351915790962874112L;
    private long attributeId;
    private long principalId;
    private String name;
    private String type;
    private String principalType;
    private String value;
    private boolean nullValue;
    
    public SecurityAttributeImpl()
    {}
    
    public SecurityAttributeImpl(Principal p, String name, String type, String value)
    {
        if (!(p instanceof BasePrincipal))
            throw new RuntimeException("Invalid Principal Type: " + p.getClass());
        BasePrincipal principal = (BasePrincipal)p;
        this.principalId = principal.getId();
        this.name = name;
        this.type = type;
        this.value = value;
        if (principal instanceof UserPrincipal)
        {
            this.principalType = UserPrincipal.PRINCIPAL_TYPE;            
        }
        else if (principal instanceof RolePrincipal)
        {
            this.principalType = RolePrincipal.PRINCIPAL_TYPE;            
        }
        else if (principal instanceof GroupPrincipal)
        {
            this.principalType = GroupPrincipal.PRINCIPAL_TYPE;            
        }
        else if (principal instanceof RemotePrincipal)
        {
            this.principalType = RemotePrincipal.PRINCIPAL_TYPE;            
        }
        else
            throw new RuntimeException("Invalid Principal Type: " + principal.getClass());
    }
    
    public String getName()
    {
        return this.name;
    }

    public String getType()
    {
        return this.type;
    }

    public String getValue()
    {
        return this.value;
    }

    public boolean getNullValue()
    {
        return this.nullValue;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }

    public long getAttributeId()
    {
        return attributeId;
    }
    
    public long getPrincipalId()
    {
        return principalId;
    }
    
    public String getPrincipalType()
    {
        return principalType;
    }

    
}
