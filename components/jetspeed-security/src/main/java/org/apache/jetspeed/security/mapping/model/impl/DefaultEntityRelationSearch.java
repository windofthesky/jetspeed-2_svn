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
package org.apache.jetspeed.security.mapping.model.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.EntityRelationDAO;
import org.apache.jetspeed.security.mapping.model.EntityDAO;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class DefaultEntityRelationSearch implements EntityRelationDAO
{

    private EntityDAO toEntitySearch;

    private String relationAttribute;

    @SuppressWarnings("unchecked")
    public Collection<Entity> getRelatedEntities(Entity entity)
    {
        Set<String> relatedEntityIds = getRelatedEntityIds(entity);
        if (relatedEntityIds.size() > 0)
        {
            return toEntitySearch.getEntities(relatedEntityIds);
        } else
        {
            return CollectionUtils.EMPTY_COLLECTION;
        }
    }

    public Entity getRelatedEntity(Entity entity)
    {
        Collection<Entity> entities = getRelatedEntities(entity);
        if (entities != null && entities.size() == 1)
        {
            return entities.iterator().next();
        } else
        {
            return null;
        }
    }

    protected Set<String> getRelatedEntityIds(Entity entity)
    {
        Attribute relatedAttr = entity.getAttribute(relationAttribute);
        Set<String> foundIds = new HashSet();

        if (relatedAttr != null)
        {
            if (relatedAttr.getDefinition().isMultiValue())
            {
                foundIds.addAll(relatedAttr.getValues());
            } else
            {
                // TODO: if single value, parse value as CSV string
            }
        }
        return foundIds;
    }

    public EntityDAO getToEntitySearch()
    {
        return toEntitySearch;
    }

    public void setToEntitySearch(EntityDAO toEntitySearch)
    {
        this.toEntitySearch = toEntitySearch;
    }

    public String getRelationAttribute()
    {
        return relationAttribute;
    }

    public void setRelationAttribute(String relationAttribute)
    {
        this.relationAttribute = relationAttribute;
    }

}
