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

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.EntitySearchResultHandler;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;
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
    private String  relationAttribute;
    private boolean useFromEntityAttribute;
    private boolean attributeContainsInternalId; // if internal ID ( = DN) is

    // not used, then the attribute
    // contains the ID(s).
    public void getRelatedEntitiesFrom(EntityDAO fromDAO, EntityDAO toDAO, Entity fromEntity, EntitySearchResultHandler handler) throws SecurityException
    {
        internalGetRelatedEntities(fromDAO, toDAO, useFromEntityAttribute, fromEntity, handler);
    }

    public void getRelatedEntitiesTo(EntityDAO fromDAO, EntityDAO toDAO, Entity toEntity, EntitySearchResultHandler handler) throws SecurityException
    {
        internalGetRelatedEntities(toDAO, fromDAO, !useFromEntityAttribute, toEntity, handler);
    }

    private void internalGetRelatedEntities(EntityDAO fromDAO, EntityDAO toDAO, boolean useFromEntityAttribute, Entity entity, 
                                           EntitySearchResultHandler handler) throws SecurityException
    {
        if (useFromEntityAttribute)
        {
            Attribute relationAttrValue = entity.getAttribute(relationAttribute);
            if (relationAttrValue != null)
            {
                Collection<String> values = relationAttrValue.getValues();
                AttributeDef attrDef = relationAttrValue.getDefinition();
                if (attrDef.isMultiValue() && attrDef.isRequired())
                {
                    String defaultValue = attrDef.requiresDnDefaultValue() ? entity.getInternalId() : attrDef.getRequiredDefaultValue();
                    values.remove(defaultValue);
                }
                if (attributeContainsInternalId)
                {
                    toDAO.getEntitiesByInternalId(values, handler);
                }
                else
                {
                    toDAO.getEntitiesById(values, handler);
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
                Filter memberAttrFilter = new EqualsFilter(relationAttribute, fromEntityUsedIdValue);
                toDAO.getEntities(memberAttrFilter, handler);
            }
        }
    }

    private String getInternalId(Entity entity, EntityDAO entityDao) throws SecurityException
    {
        if (StringUtils.isEmpty(entity.getInternalId()))
        {
            // apparently internalId is not stored in the DB => fetch it from
            // LDAP store
            entity = entityDao.getEntity(entity.getId());
        }
        return entity.getInternalId();
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

    public void addRelation(EntityDAO sourceDao, EntityDAO targetDao, String sourceEntityId, String targetEntityId) throws SecurityException
    {
        if (useFromEntityAttribute)
        {            
            sourceDao.addRelation(sourceEntityId, targetDao.getInternalId(targetEntityId, true), relationAttribute);
        }
        else
        {
            targetDao.addRelation(targetEntityId, sourceDao.getInternalId(sourceEntityId, true), relationAttribute);
        }
    }

    public void removeRelation(EntityDAO sourceDao, EntityDAO targetDao, String sourceEntityId, String targetEntityId) throws SecurityException
    {
        if (useFromEntityAttribute)
        {
            String internalEntityId = targetDao.getInternalId(targetEntityId, false);
            if (internalEntityId != null)
            {
                sourceDao.removeRelation(sourceEntityId, internalEntityId, relationAttribute);
            }
        }
        else
        {
            String internalEntityId = sourceDao.getInternalId(sourceEntityId, false);
            if (internalEntityId != null)
            {
                targetDao.removeRelation(targetEntityId, internalEntityId, relationAttribute);
            }
        }
    }

}
