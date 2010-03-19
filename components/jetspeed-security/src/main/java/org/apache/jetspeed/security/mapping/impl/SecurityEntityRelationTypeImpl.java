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

import org.apache.jetspeed.security.mapping.SecurityEntityRelationType;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class SecurityEntityRelationTypeImpl implements SecurityEntityRelationType
{
    private String sourceEntityType, targetEntityType, relationType;

    public SecurityEntityRelationTypeImpl(String relationType, String sourceEntityType, String targetEntityType)
    {
        super();
        this.relationType = relationType;
        this.sourceEntityType = sourceEntityType;
        this.targetEntityType = targetEntityType;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((relationType == null) ? 0 : relationType.hashCode());
        result = prime * result + ((sourceEntityType == null) ? 0 : sourceEntityType.hashCode());
        result = prime * result + ((targetEntityType == null) ? 0 : targetEntityType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        SecurityEntityRelationType other = (SecurityEntityRelationType) obj;
        if (relationType == null)
        {
            if (other.getRelationType() != null)
            {
                return false;
            }
        }
        else if (!relationType.equals(other.getRelationType()))
        {
            return false;
        }
        if (sourceEntityType == null)
        {
            if (other.getFromEntityType() != null)
            {
                return false;
            }
        }
        else if (!sourceEntityType.equals(other.getFromEntityType()))
        {
            return false;
        }
        if (targetEntityType == null)
        {
            if (other.getToEntityType() != null)
            {
                return false;
            }
        }
        else if (!targetEntityType.equals(other.getToEntityType()))
        {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.jetspeed.security.mapping.ldap.dao.Temp#getSourceEntityType()
     */
    public String getFromEntityType()
    {
        return sourceEntityType;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.jetspeed.security.mapping.ldap.dao.Temp#getTargetEntityType()
     */
    public String getToEntityType()
    {
        return targetEntityType;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.jetspeed.security.mapping.ldap.dao.Temp#getRelationType()
     */
    public String getRelationType()
    {
        return relationType;
    }
}