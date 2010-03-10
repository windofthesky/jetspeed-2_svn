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

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

/**
 * DAO for fetching relationships between LDAP entities. A relationship between two entities is created by adding an attribute value to either the entity on the
 * "from" site of the relationship, or the entity on the other side ("to"). An example: a relation "hasRole" from one entity (e.g. of type "user") to another
 * entity (e.g. of type "role"), can be defined by using an attribute "role" on the user, which is a multi-value attribute. The value of that attribute uniquely
 * identifies the role (the id of the role entity). The relationship can also be defined by specifying an attribute on the role which holds the unique id of the
 * user, e.g. through a multi-value "member" attribute.
 * 
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class AttributeBasedRelationDAO extends AbstractRelationDAO
{
    private String relationAttribute;
    private boolean useFromEntityAttribute;
    private boolean attributeContainsInternalId; // if internal ID ( = DN) is

    // not used, then the attribute
    // contains the ID(s).
    public Collection<Entity> getRelatedEntitiesFrom(EntityDAO fromDAO, EntityDAO toDAO, Entity fromEntity)
    {
        return internalGetRelatedEntities(fromDAO, toDAO, useFromEntityAttribute, fromEntity);
    }

    public Collection<Entity> getRelatedEntitiesTo(EntityDAO fromDAO, EntityDAO toDAO, Entity toEntity)
    {
        return internalGetRelatedEntities(toDAO, fromDAO, !useFromEntityAttribute, toEntity);
    }

    private Collection<Entity> internalGetRelatedEntities(EntityDAO fromDAO, EntityDAO toDAO, boolean useFromEntityAttribute, Entity entity)
    {
        if (useFromEntityAttribute)
        {
            Attribute relationAttrValue = entity.getAttribute(relationAttribute);
            if (relationAttrValue != null)
            {
                Collection<String> values = relationAttrValue.getValues();
                if (attributeContainsInternalId)
                {
                    return toDAO.getEntitiesByInternalId(values);
                }
                else
                {
                    return toDAO.getEntitiesById(values);
                }
            }
        }
        else
        {
            // can be either the id or the internalId of the from entity
            String fromEntityUsedIdValue = attributeContainsInternalId ? getInternalId(entity, fromDAO) : entity.getId();
            // TODO : throw exception when no ID / internal ID can be found for
            // the entity
            if (!StringUtils.isEmpty(fromEntityUsedIdValue))
            {
                // fetch entities using target Entity DAO with a specific filter
                // on the member attribute
                Filter roleMemberAttrFilter = new EqualsFilter(relationAttribute, fromEntityUsedIdValue);
                return toDAO.getEntities(roleMemberAttrFilter);
            }
        }
        return null;
    }

    private String getInternalId(Entity entity, EntityDAO entityDao)
    {
        if (StringUtils.isEmpty(entity.getInternalId()))
        {
            // apparently internalId is not stored in the DB => fetch it from
            // LDAP store
            entity = entityDao.getEntity(entity.getId());
            return entity.getInternalId();
        }
        else
        {
            return entity.getInternalId();
        }
    }

    public void setRelationAttribute(String relationAttribute)
    {
        this.relationAttribute = relationAttribute;
    }

    public void setUseFromEntityAttribute(boolean useFromEntityAttribute)
    {
        this.useFromEntityAttribute = useFromEntityAttribute;
    }

    public void setAttributeContainsInternalId(boolean attributeContainsInternalId)
    {
        this.attributeContainsInternalId = attributeContainsInternalId;
    }

    private Entity getLiveEntity(EntityDAO dao, Entity transientEntity) throws SecurityException {
        Entity liveEntity = dao.getEntity(transientEntity.getId());
        if (liveEntity == null){
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(transientEntity.getType(), transientEntity.getId()));
        }
        if (liveEntity.getInternalId() == null){
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(),"getLiveEntity","Internal ID not found"));
        }
        return liveEntity;
    }
    
    private void internalAddRelation(EntityDAO fromEntityDAO, EntityDAO toEntityDAO, Entity fromEntity, Entity toEntity) throws SecurityException
    {
        fromEntity = getLiveEntity(fromEntityDAO, fromEntity);
        
        toEntity = getLiveEntity(toEntityDAO, toEntity);
        
        String attrValue = null;
        if (attributeContainsInternalId)
        {            
            attrValue = toEntity.getInternalId();
        }
        else
        {
            attrValue = toEntity.getId();
        }
        Attribute relationAttribute = fromEntity.getAttribute(this.relationAttribute);
        
        if(relationAttribute == null)
        {
            fromEntity.setAttribute(this.relationAttribute,new ArrayList<String>());    
        }
        else
        {
            if(relationAttribute.getValues().contains(attrValue))
            {
                throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_ALREADY_EXISTS.createScoped(fromEntity.getType(), fromEntity.getId(), relationAttribute, toEntity.getId()));
            }
        }
        
        if (relationAttribute.getDefinition().isMultiValue())
        {
            relationAttribute.getValues().add(attrValue);
        }
        else
        {
            relationAttribute.setValue(attrValue);
        }
        fromEntityDAO.updateInternalAttributes(fromEntity);
    }

    private void internalRemoveRelation(EntityDAO fromEntityDAO, EntityDAO toEntityDAO, Entity fromEntity, Entity toEntity) throws SecurityException
    {
        fromEntity = fromEntityDAO.getEntity(fromEntity.getId());
        toEntity = toEntityDAO.getEntity(toEntity.getId());
        String attrValue = null;
        if (attributeContainsInternalId)
        {
            if (toEntity.getInternalId() == null)
            {
                // internal ID (ldap DN) is not present, refetch the entity from LDAP to get the DN
                toEntity = toEntityDAO.getEntity(toEntity.getId());
            }
            attrValue = toEntity.getInternalId();
        }
        else
        {
            attrValue = toEntity.getId();
        }
        Attribute relationAttribute = fromEntity.getAttribute(this.relationAttribute);
        if (relationAttribute.getDefinition().isMultiValue())
        {
            DistinguishedName attrib = new DistinguishedName(attrValue);
            if (attributeContainsInternalId)
            {
                boolean found = false;
                String attribValue = null;
                Iterator<String> iterator = relationAttribute.getValues().iterator();
                while(iterator.hasNext() && !found)
                {
                    attribValue = iterator.next();
                    DistinguishedName ldapAttr = new DistinguishedName(attribValue);
                    if (ldapAttr.equals(attrib))
                    {
                        relationAttribute.getValues().remove(attribValue);
                        found = true; 
                    }
                }
            }
            else
            {
                relationAttribute.getValues().remove(attrValue);
            }
        }
        else
        {
            relationAttribute.setValue(null);
        }
        fromEntityDAO.updateInternalAttributes(fromEntity);
    }

    public void addRelation(EntityDAO sourceDao, EntityDAO targetDao, Entity sourceEntity, Entity targetEntity) throws SecurityException
    {
        if (useFromEntityAttribute)
        {
            internalAddRelation(sourceDao, targetDao, sourceEntity, targetEntity);
        }
        else
        {
            internalAddRelation(targetDao, sourceDao, targetEntity, sourceEntity);
        }
    }

    public void removeRelation(EntityDAO sourceDao, EntityDAO targetDao, Entity sourceEntity, Entity targetEntity) throws SecurityException
    {
        if (useFromEntityAttribute)
        {
            internalRemoveRelation(sourceDao, targetDao, sourceEntity, targetEntity);
        }
        else
        {
            internalRemoveRelation(targetDao, sourceDao, targetEntity, sourceEntity);
        }
    }
}