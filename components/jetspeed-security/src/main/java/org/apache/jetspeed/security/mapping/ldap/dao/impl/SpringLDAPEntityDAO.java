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
package org.apache.jetspeed.security.mapping.ldap.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.directory.SearchControls;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.impl.EntityFactoryImpl;
import org.apache.jetspeed.security.mapping.ldap.dao.DefaultEntityContextMapper;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.LDAPEntityDAOConfiguration;
import org.apache.jetspeed.security.mapping.ldap.dao.SearchUtil;
import org.apache.jetspeed.security.mapping.ldap.filter.SimpleFilter;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class SpringLDAPEntityDAO implements EntityDAO
{
    protected LdapTemplate ldapTemplate;
    protected LDAPEntityDAOConfiguration configuration;
    private ContextMapper contextMapper;
    private EntityFactory entityFactory;

    
    public SpringLDAPEntityDAO(LDAPEntityDAOConfiguration configuration)
    {
        super();
        this.configuration = configuration;
        this.entityFactory = new EntityFactoryImpl(configuration);
        this.contextMapper = new DefaultEntityContextMapper(entityFactory);
    }

    public void initialize(LdapTemplate ldapTemplate)
    {
        this.ldapTemplate = ldapTemplate;
    }

    public Entity getEntity(String entityId)
    {
        Filter idFilter = createFilterForIdSearch(entityId);
        Collection<Entity> entities = getEntities(idFilter);
        if (entities != null && entities.size() == 1)
        {
            return entities.iterator().next();
        }
        else
        {
            return null;
        }
    }

    public Collection<Entity> getEntitiesById(Collection<String> entityIds)
    {
        OrFilter idFilter = new OrFilter();
        String idAttr = configuration.getLdapIdAttribute();
        for (String id : entityIds)
        {
            idFilter.or(new EqualsFilter(idAttr, id));
        }
        Filter combinedFilter = null;
        if (configuration.getBaseFilter() != null)
        {
            combinedFilter = SearchUtil.andFilters(idFilter, configuration.getBaseFilter());
        }
        else
        {
            combinedFilter = idFilter;
        }
        return getEntities(combinedFilter);
    }

    public Collection<Entity> getEntitiesByInternalId(Collection<String> internalIds)
    {
        final Collection<Entity> resultSet = new ArrayList<Entity>();
        for (Iterator<String> iterator = internalIds.iterator(); iterator.hasNext();)
        {
            String internalId = (String) iterator.next();
            DistinguishedName principalDN = new DistinguishedName(internalId);
            principalDN.removeFirst();
            internalId =principalDN.toString();            
            Entity resultEntity = (Entity) ldapTemplate.lookup(internalId, getContextMapper());
            if (resultEntity != null)
            {
                resultSet.add(resultEntity);
            }
        }
        return resultSet;
    }

    @SuppressWarnings("unchecked")
    public Collection<Entity> getEntities(Filter filter)
    {
        if (configuration.getBaseFilter() != null)
        {
            if (filter == null)
            {
                filter = configuration.getBaseFilter();
            }
            else
            {
                filter = SearchUtil.andFilters(configuration.getBaseFilter(), filter);
            }
        }
        String filterStr = filter.toString();
        if (StringUtils.isEmpty(filterStr))
        {
            filterStr = "(objectClass=*)"; // trivial search query
        }
        return (Collection<Entity>) ldapTemplate.search(configuration.getBaseDN(), filterStr, SearchControls.SUBTREE_SCOPE, getContextMapper());
    }

    public Collection<Entity> getAllEntities()
    {
        final String finalFilter = configuration.getBaseFilter() != null ? configuration.getBaseFilter().encode() : "(objectClass=*)";
        return getEntities(new SimpleFilter(finalFilter));
    }

    public void update(Entity entity)
    {
    }

    public void addEntity(Entity entity)
    {
    }

    public void removeEntity(Entity entity)
    {
    }

    public LDAPEntityDAOConfiguration getConfiguration()
    {
        return configuration;
    }

    protected Filter createFilterForIdSearch(String entityId)
    {
        return SearchUtil.constructMatchingFieldsFilter(configuration.getBaseFilter(), new String[] { configuration.getLdapIdAttribute(), entityId });
    }

    public ContextMapper getContextMapper()
    {
        return contextMapper;
    }

    public EntityFactory getEntityFactory()
    {
        return entityFactory;
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate)
    {
        this.ldapTemplate = ldapTemplate;
    }

    public void setContextMapper(ContextMapper contextMapper)
    {
        this.contextMapper = contextMapper;
    }
}
