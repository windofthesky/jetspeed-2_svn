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

import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.ldap.EntityFactoryImpl;
import org.apache.jetspeed.security.mapping.ldap.dao.DefaultEntityContextMapper;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.LDAPEntityDAOConfiguration;
import org.apache.jetspeed.security.mapping.ldap.dao.SearchUtil;
import org.apache.jetspeed.security.mapping.ldap.filter.SimpleFilter;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
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
        if (configuration.getSearchFilter() != null)
        {
            combinedFilter = SearchUtil.andFilters(idFilter, configuration.getSearchFilter());
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
            DistinguishedName principalDN = getRelativeDN(internalId);
            internalId = principalDN.toString(); 
            Entity resultEntity = (Entity) ldapTemplate.lookup(internalId, getContextMapper());
            if (resultEntity != null)
            {
                resultSet.add(resultEntity);
            }
        }
        return resultSet;
    }
    
    protected DistinguishedName getRelativeDN(String fullDN){
        DistinguishedName principalDN = new DistinguishedName(fullDN);
        if (configuration.getBaseDN() != null && configuration.getBaseDN().length() > 0){
            principalDN.removeFirst(new DistinguishedName(configuration.getBaseDN()));
        }
        return principalDN;
    }

    protected String createSearchFilter(Filter filter){
        if (configuration.getSearchFilter() != null)
        {
            if (filter == null)
            {
                filter = configuration.getSearchFilter();
            }
            else
            {
                filter = SearchUtil.andFilters(configuration.getSearchFilter(), filter);
            }
        }
        String filterStr = filter.toString();
        if (StringUtils.isEmpty(filterStr))
        {
            filterStr = "(objectClass=*)"; // trivial search query
        }
        return filterStr;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<Entity> getEntities(Filter filter)
    {
        String filterStr = createSearchFilter(filter);
        return (Collection<Entity>) ldapTemplate.search(configuration.getSearchDN(), filterStr, SearchControls.SUBTREE_SCOPE, getContextMapper());
    }

    public Collection<Entity> getAllEntities()
    {
        final String finalFilter = configuration.getSearchFilter() != null ? configuration.getSearchFilter().encode() : "(objectClass=*)";
        return getEntities(new SimpleFilter(finalFilter));
    }

    public void update(Entity entity)
    {
        update(entity,true);
    }
    
    public void update(Entity entity, boolean updateMappedAttributes)
    {
        String internalIdStr = entity.getInternalId();
        if (internalIdStr == null){
            Entity ldapEntity = getEntity(entity.getId());
            if (ldapEntity == null || ldapEntity.getInternalId() == null){
                // TODO throw exception
                return;
            }   
            internalIdStr = entity.getInternalId();
        }
        Name dn=getRelativeDN(internalIdStr);
        DirContextOperations dirCtxOps = ldapTemplate.lookupContext(dn);
        if (dirCtxOps == null){
            // TODO throw exception
            return;
        }
        Collection<ModificationItem> modItems = getModItems(entity,dirCtxOps,updateMappedAttributes);
        ldapTemplate.modifyAttributes(dn, modItems.toArray(new ModificationItem[]{}));
    }

    public void addEntity(Entity entity)
    {
    }

    public void removeEntity(Entity entity)
    {
    }

    
    public void addEntity(Entity entity, Entity parentEntity)
    {
        
    }

    public void removeEntity(Entity entity, Entity parentEntity)
    {
        
    }

    public void update(Entity entity, Entity parentEntity)
    {
        
    }

    public LDAPEntityDAOConfiguration getConfiguration()
    {
        return configuration;
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

    protected boolean setNamingAttribute(Attribute entityAttr, DirContextOperations dirCtxOps){
        boolean attrAdded = false;
        if (entityAttr != null){
            AttributeDef attrDef = entityAttr.getDefinition();
            if (attrDef.isMultiValue()){
                Collection<String> values = entityAttr.getValues();
                if (values != null){
                    dirCtxOps.setAttributeValues(attrDef.getName(),values.toArray());
                    attrAdded = true;
                }
            } else {
                String value = entityAttr.getValue();
                if (value != null){
                    dirCtxOps.setAttributeValue(attrDef.getName(),value);
                    attrAdded = true;
                }
            }                    
        }    
        return attrAdded;
    }
    
    protected Collection<ModificationItem> getModItems(Entity entity, DirContextOperations dirCtxOps, boolean useMappedAttributes){
        Collection<ModificationItem> modItems = new ArrayList<ModificationItem>();
        
        for(AttributeDef attrDef : configuration.getAttributeDefinitions()){
            
            if (!attrDef.getName().equals(configuration.getLdapIdAttribute()) && ( (useMappedAttributes && attrDef.isMapped()) || (!useMappedAttributes && !attrDef.isMapped()))){
                Attribute entityAttr = entity.getAttribute(attrDef.getName());
                boolean attrAdded = false;
                if (entityAttr != null){
                    if (attrDef.isMultiValue()){
                        Collection<String> values = entityAttr.getValues();
                        if (values != null){
                            javax.naming.directory.Attribute namingAttr = new BasicAttribute(entityAttr.getName());
                            if (values.size() > 0){
                                for (String val : values)
                                {   
                                    namingAttr.add(val);
                                }
                                modItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,namingAttr));
                                attrAdded = true;
                            }
                        }
                    } else {
                        String value = entityAttr.getValue();
                        if (value != null){
                            javax.naming.directory.Attribute namingAttr = new BasicAttribute(entityAttr.getName(), entityAttr.getValue());
                            modItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,namingAttr));
                            attrAdded = true;
                        }
                    }                    
                }    
                if (!attrAdded){
                    // entity attribute not added, so remove it if present in ldap.
                    Object namingAttrValue = dirCtxOps.getObjectAttribute(attrDef.getName());
                    if (namingAttrValue != null){
                        BasicAttribute basicAttr = new BasicAttribute(attrDef.getName());
                        if (attrDef.isRequired()){
                            if (attrDef.getRequiredDefaultValue() != null){
                                basicAttr.add(attrDef.getRequiredDefaultValue());
                                modItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,basicAttr));
                            } else {
                                // TODO throw exception
                                break;
                            }
                        } else {
                            modItems.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE,basicAttr));
                        }
                    }
                }
            }
        }
        return modItems;
    }
    
    protected Filter createFilterForIdSearch(String entityId)
    {
        return SearchUtil.constructMatchingFieldsFilter(configuration.getSearchFilter(), new String[] { configuration.getLdapIdAttribute(), entityId });
    }


}
