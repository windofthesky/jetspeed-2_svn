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
package org.apache.jetspeed.security.mapping.impl;

import org.apache.jetspeed.security.mapping.model.SecurityEntityRelationType;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class SecurityEntityRelationTypeImpl implements SecurityEntityRelationType
{
    private final String fromEntityType;
    private final String toEntityType;
    private final String relationType;
    private final boolean createAllowed;
    private final boolean removeAllowed;
    private final int hashCode;

    public SecurityEntityRelationTypeImpl(SecurityEntityRelationType src)
    {
        this(src.getFromEntityType(), src.getToEntityType(), src.getFromEntityType());
    }
    
    public SecurityEntityRelationTypeImpl(String relationType, String sourceEntityType, String targetEntityType)
    {
        this(relationType, sourceEntityType, targetEntityType, true, true);
    }

    public SecurityEntityRelationTypeImpl(String relationType, String sourceEntityType, String targetEntityType, boolean createAllowed, boolean removeAllowed)
    {
        this.relationType = relationType;
        this.fromEntityType = sourceEntityType;
        this.toEntityType = targetEntityType;
        this.createAllowed = createAllowed;
        this.removeAllowed = removeAllowed;
        this.hashCode = relationType.hashCode() + sourceEntityType.hashCode() + targetEntityType.hashCode();
    }
    
    @Override
    public int hashCode()
    {
        return hashCode;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o != null && o instanceof SecurityEntityRelationTypeImpl)
        {
            SecurityEntityRelationTypeImpl other = (SecurityEntityRelationTypeImpl)o;
            return other.relationType.equals(relationType) && other.fromEntityType.equals(fromEntityType) && other.toEntityType.equals(toEntityType);
        }
        return false;
    }

    public String getFromEntityType()
    {
        return fromEntityType;
    }

    public String getToEntityType()
    {
        return toEntityType;
    }

    public String getRelationType()
    {
        return relationType;
    }

    public boolean isCreateAllowed()
    {
        return createAllowed;
    }
    
    public boolean isRemoveAllowed()
    {
        return removeAllowed;
    }
}