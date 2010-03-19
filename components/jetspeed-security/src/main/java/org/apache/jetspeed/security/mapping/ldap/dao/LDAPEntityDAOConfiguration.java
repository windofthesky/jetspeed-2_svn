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
package org.apache.jetspeed.security.mapping.ldap.dao;

import java.util.Set;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.springframework.ldap.filter.Filter;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class LDAPEntityDAOConfiguration
{
    private String            baseDN;
    private String            searchDN;
    private Filter            baseFilter;
    private String            ldapIdAttribute;
    private Set<AttributeDef> attributeDefinitions;
    private String            entityType;
    private String[]          objectClassesArr;

    public void initialize() throws JetspeedException
    {
        checkNotEmpty("entityType", entityType);
        checkNotNull("baseDN", baseDN);
        checkNotEmpty("ldapIdAttribute", ldapIdAttribute);
        checkNotNull("attributeDefinitions", attributeDefinitions);
    }

    private void checkNotNull(String fieldName, Object fieldValue) throws JetspeedException
    {
        if (fieldValue == null)
        {
            throw new JetspeedException(getClass().getName() + ": property '" + fieldName + "' cannot be null.");
        }
    }

    private void checkNotEmpty(String fieldName, String fieldValue) throws JetspeedException
    {
        if (fieldValue == null)
        {
            throw new JetspeedException(getClass().getName() + ": property '" + fieldName + "' cannot be null or empty.");
        }
    }

    public String getBaseDN()
    {
        return baseDN;
    }

    public void setBaseDN(String baseDN)
    {
        this.baseDN = baseDN;
    }

    public String getSearchDN()
    {
        return searchDN;
    }

    public void setSearchDN(String searchDN)
    {
        this.searchDN = searchDN;
    }

    public Filter getSearchFilter()
    {
        return baseFilter;
    }

    public void setSearchFilter(Filter baseFilter)
    {
        this.baseFilter = baseFilter;
    }

    public Set<AttributeDef> getAttributeDefinitions()
    {
        return attributeDefinitions;
    }

    public void setAttributeDefinitions(Set<AttributeDef> attributeDefinitions)
    {
        this.attributeDefinitions = attributeDefinitions;
    }

    public String getLdapIdAttribute()
    {
        return ldapIdAttribute;
    }

    public void setLdapIdAttribute(String ldapIdAttribute)
    {
        this.ldapIdAttribute = ldapIdAttribute;
    }

    public String getEntityType()
    {
        return entityType;
    }

    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }

    public String[] getObjectClassesArray()
    {
        return objectClassesArr;
    }

    public void setObjectClasses(String objectClasses)
    {
        if (objectClasses != null)
        {
            this.objectClassesArr = objectClasses.split(",");
        }
    }
}
