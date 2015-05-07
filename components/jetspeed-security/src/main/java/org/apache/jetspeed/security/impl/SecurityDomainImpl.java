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
package org.apache.jetspeed.security.impl;

import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.security.SecurityDomain;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class SecurityDomainImpl implements SecurityDomain, DistributedCacheObject
{

    private Long domainId;
    private String name;
    private Long ownerDomainId;
    private boolean remote;
    private boolean enabled=true;
    
    
    public SecurityDomainImpl(){
        
    }
    
    public SecurityDomainImpl(SecurityDomain anotherDomain){
        this();
        this.domainId=anotherDomain.getDomainId();
        this.name=anotherDomain.getName();
        this.ownerDomainId=anotherDomain.getOwnerDomainId();
        this.remote=anotherDomain.isRemote();
        this.enabled=anotherDomain.isEnabled();
    }
    
    public Long getDomainId()
    {
        return domainId;
    }
    
    public void setDomainId(Long domainId)
    {
        this.domainId = domainId;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Long getOwnerDomainId()
    {
        return ownerDomainId;
    }
    
    public void setOwnerDomainId(Long ownerDomainId)
    {
        this.ownerDomainId = ownerDomainId;
    }
    
    public boolean isRemote()
    {
        return remote;
    }
    
    public void setRemote(boolean remote)
    {
        this.remote = remote;
    }

    
    public boolean isEnabled()
    {
        return enabled;
    }

    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void notifyChange(int action)
    {
    }
}
