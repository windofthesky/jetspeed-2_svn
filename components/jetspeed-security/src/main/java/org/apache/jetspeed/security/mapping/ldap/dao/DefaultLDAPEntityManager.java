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

import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.mapping.model.Entity;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class DefaultLDAPEntityManager implements SecurityEntityManager
{

    // entity type DAOs
    protected Map<String, EntityDAO> entityDAOs;

    protected Map<RelationDefinitionKey, EntityRelationDAO> entityRelationDAOs = new HashMap<RelationDefinitionKey, EntityRelationDAO>();

    private EntityDAO getDAOForEntity(Entity entity)
    {
        return entity.getType() != null ? entityDAOs.get(entity.getType())
                : null;
    }

    private EntityRelationDAO getRelationDAO(String relationType,
            String fromEntityType, String targetEntityType)
    {
        return entityRelationDAOs.get(new RelationDefinitionKey(relationType,
                fromEntityType, targetEntityType));
    }

    public void addRelatedEntity(Entity sourceEntity, Entity targetEntity,
            String relationType)
    {
        EntityDAO sourceDao = getDAOForEntity(sourceEntity);
        EntityDAO targetDao = getDAOForEntity(targetEntity);
        if (sourceDao != null && targetDao != null)
        {
            EntityRelationDAO relationDAO = getRelationDAO(relationType,
                    sourceEntity.getType(), targetEntity.getType());
            if (relationDAO != null)
            {
                relationDAO.relate(sourceDao, targetDao, sourceEntity,
                        targetEntity);
            }
        }
    }

    public Collection<Entity> getAllEntities(String entityType)
    {
        EntityDAO dao = entityDAOs.get(entityType);
        return dao != null ? dao.getAllEntities() : null;
    }

    public Entity getEntity(String entityType, String entityId)
    {
        EntityDAO dao = entityDAOs.get(entityType);
        return dao != null ? dao.getEntity(entityId) : null;
    }

    public Collection<Entity> getRelatedEntities(Entity sourceEntity,
            String targetEntityType, String relationType)
    {
        EntityDAO sourceDAO = getDAOForEntity(sourceEntity);
        EntityDAO targetDAO = entityDAOs.get(targetEntityType);
        if (sourceDAO != null && targetDAO != null)
        {
            EntityRelationDAO relationDAO = getRelationDAO(relationType,
                    sourceEntity.getType(), targetEntityType);
            if (relationDAO != null) { return relationDAO.getRelatedEntities(
                    sourceDAO, targetDAO, sourceEntity); }
        }
        return null; // todo : throw exception, since combination of entity
                     // types and relation type is not configured.
    }

    public void update(Entity entity)
    {
        EntityDAO dao = getDAOForEntity(entity);
        if (dao != null)
        {
            dao.update(entity);
        }
    }

    public void setEntityDAOs(Map<String, EntityDAO> entityDAOs)
    {
        this.entityDAOs = entityDAOs;
    }

    public void setEntityRelationDAOs(
            Collection<EntityRelationDAO> entityRelationDAOs)
    {
        this.entityRelationDAOs.clear();
        for (EntityRelationDAO dao : entityRelationDAOs)
        {
            this.entityRelationDAOs.put(new RelationDefinitionKey(dao
                    .getRelationType(), dao.getFromEntityType(), dao
                    .getToEntityType()), dao);
        }
    }

    private class RelationDefinitionKey
    {

        private String sourceEntityType, targetEntityType, relationType;

        public RelationDefinitionKey(String relationType,
                String sourceEntityType, String targetEntityType)
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
            result = prime * result + getOuterType().hashCode();
            result = prime * result
                    + ((relationType == null) ? 0 : relationType.hashCode());
            result = prime
                    * result
                    + ((sourceEntityType == null) ? 0 : sourceEntityType
                            .hashCode());
            result = prime
                    * result
                    + ((targetEntityType == null) ? 0 : targetEntityType
                            .hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RelationDefinitionKey other = (RelationDefinitionKey) obj;
            if (!getOuterType().equals(other.getOuterType())) return false;
            if (relationType == null)
            {
                if (other.relationType != null) return false;
            } else if (!relationType.equals(other.relationType)) return false;
            if (sourceEntityType == null)
            {
                if (other.sourceEntityType != null) return false;
            } else if (!sourceEntityType.equals(other.sourceEntityType))
                return false;
            if (targetEntityType == null)
            {
                if (other.targetEntityType != null) return false;
            } else if (!targetEntityType.equals(other.targetEntityType))
                return false;
            return true;
        }

        public String getSourceEntityType()
        {
            return sourceEntityType;
        }

        public String getTargetEntityType()
        {
            return targetEntityType;
        }

        public String getRelationType()
        {
            return relationType;
        }

        private DefaultLDAPEntityManager getOuterType()
        {
            return DefaultLDAPEntityManager.this;
        }

    }
}
