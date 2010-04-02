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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.filter.Filter;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class LDAPEntityDAOConfiguration
{
    private static final int          DEFAULT_SEARCH_PAGE_SIZE = 256;
    
    private DistinguishedName         baseDN = new DistinguishedName().immutableDistinguishedName();
    private DistinguishedName         searchDN = new DistinguishedName().immutableDistinguishedName();
    private Filter                    baseFilter;
    private String                    ldapIdAttribute;
    private Map<String, AttributeDef> attributeDefinitions;
    private Map<String, AttributeDef> entityAttributeDefinitions;
    private String                    entityType;
    private String[]                  objectClassesArr;
    private String[]                  entityAttributeNames;
    private int                       searchPageSize = DEFAULT_SEARCH_PAGE_SIZE;

    public void initialize() throws JetspeedException
    {
        checkNotEmpty("entityType", entityType);
        checkNotEmpty("ldapIdAttribute", ldapIdAttribute);
        checkNotNull("attributeDefinitions", attributeDefinitions);
        boolean idAttributeNameFound = false;
        for (AttributeDef def : attributeDefinitions.values())
        {
            if (ldapIdAttribute.equals(def.getName()))
            {
                if (def.isMultiValue() || !def.isRequired() || def.isRelationOnly())
                {
                    throw new RuntimeException("Unsupported ldapIdAttribute Attribute definition: multi-value, optional and/or relationOnly attribute");
                }
                def.setEntityIdAttribute(true);
                idAttributeNameFound = true;
                break;
            }
        }
        if (!idAttributeNameFound)
        {
            throw new RuntimeException("No ldapIdAttribute Attribute definition provided");
        }
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
    
    public int getSearchPageSize()
    {
        return searchPageSize;
    }

    public void setSearchPageSize(int searchPageSize)
    {
        this.searchPageSize = searchPageSize;
    }

    public DistinguishedName getBaseDN()
    {
        return baseDN;
    }

    public void setLdapBase(String ldapBase)
    {
        this.baseDN = new DistinguishedName(ldapBase).immutableDistinguishedName();
    }

    public DistinguishedName getSearchDN()
    {
        return searchDN;
    }

    public void setSearchBase(String searchBase)
    {
        this.searchDN = new DistinguishedName(searchBase).immutableDistinguishedName();
    }

    public Filter getSearchFilter()
    {
        return baseFilter;
    }

    public void setSearchFilter(Filter baseFilter)
    {
        this.baseFilter = baseFilter;
    }

    public Map<String, AttributeDef> getAttributeDefinitionsMap()
    {
        return attributeDefinitions;
    }

    public Map<String, AttributeDef> getEntityAttributeDefinitionsMap()
    {
        return attributeDefinitions;
    }

    public AttributeDef getAttributeDef(String name)
    {
        return attributeDefinitions.get(name);
    }

    public void setAttributeDefinitions(Collection<AttributeDef> attributeDefinitions)
    {
        this.attributeDefinitions = new HashMap<String, AttributeDef>();
        this.entityAttributeDefinitions = new HashMap<String, AttributeDef>();
        for (AttributeDef def : attributeDefinitions)
        {
            if (!def.isRelationOnly())
            {
                this.entityAttributeDefinitions.put(def.getName(), def);
            }
            this.attributeDefinitions.put(def.getName(), def);
        }
        entityAttributeNames = entityAttributeDefinitions.keySet().toArray(new String[0]);
    }

    public String[] getEntityAttributeNames()
    {
        return entityAttributeNames;
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
