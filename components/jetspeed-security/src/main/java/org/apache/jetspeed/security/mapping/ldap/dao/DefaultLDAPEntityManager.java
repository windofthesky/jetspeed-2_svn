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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.mapping.impl.SecurityEntityRelationTypeImpl;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.SecurityEntityRelationType;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class DefaultLDAPEntityManager implements SecurityEntityManager
{
    // entity type DAOs
    private Map<String, EntityDAO>                             entityDAOs = new HashMap<String, EntityDAO>();
    private Map<SecurityEntityRelationType, EntityRelationDAO> entityRelationDAOs = new HashMap<SecurityEntityRelationType, EntityRelationDAO>();
    private Map<String, Set<SecurityEntityRelationType>> entityRelationTypes = new HashMap<String, Set<SecurityEntityRelationType>>();
    
    public DefaultLDAPEntityManager(List<EntityDAO> entityDAOs, List<EntityRelationDAO> entityRelationDAOs)
    {
        for (EntityDAO entityDAO: entityDAOs)
        {
            this.entityDAOs.put(entityDAO.getEntityType(), entityDAO);
        }
        if (entityRelationDAOs != null)
        {
            for (EntityRelationDAO entityRelationDAO: entityRelationDAOs)
            {
                SecurityEntityRelationType relationType = entityRelationDAO.getRelationType();
                if (this.entityDAOs.get(relationType.getFromEntityType()) == null || this.entityDAOs.get(relationType.getToEntityType()) == null)
                {
                    throw new IllegalArgumentException("Missing EntityDAO for EntityRelationDAO fromEntityType: "+relationType.getFromEntityType() +
                                                       " and/or toEntityType: "+relationType.getToEntityType());
                }
                this.entityRelationDAOs.put(new SecurityEntityRelationTypeImpl(relationType.getRelationType(), relationType.getFromEntityType(), relationType.getToEntityType()), 
                                            entityRelationDAO);
            }
        }
        for (SecurityEntityRelationType relationType : this.entityRelationDAOs.keySet())
        {
            Set<SecurityEntityRelationType> relationTypes = entityRelationTypes.get(relationType.getFromEntityType());
            if (relationTypes == null)
            {
                relationTypes = new HashSet<SecurityEntityRelationType>();
                entityRelationTypes.put(relationType.getFromEntityType(), relationTypes);
            }
            relationTypes.add(relationType);
            relationTypes = entityRelationTypes.get(relationType.getToEntityType());
            if (relationTypes == null)
            {
                relationTypes = new HashSet<SecurityEntityRelationType>();
                entityRelationTypes.put(relationType.getFromEntityType(), relationTypes);
            }
            relationTypes.add(relationType);
        }
    }
    
    public SecurityEntityRelationType getSupportedEntityRelationType(String relationType, String fromEntityType, String toEntityType)
    {
        SecurityEntityRelationType key = new SecurityEntityRelationTypeImpl(relationType, fromEntityType, toEntityType);
        return entityRelationDAOs.containsKey(key) ? key : null;
    }

    public Set<SecurityEntityRelationType> getSupportedEntityRelationTypes()
    {
        return entityRelationDAOs.keySet();
    }

    public Set<String> getSupportedEntityTypes()
    {
        return entityRelationTypes.keySet();
    }

    public Set<SecurityEntityRelationType> getSupportedEntityRelationTypes(String entityType)
    {
        return entityRelationTypes.get(entityType);
    }

    private EntityDAO getDAOForEntity(Entity entity)
    {
        return entityDAOs.get(entity.getType());
    }

    public void addRelation(String fromEntityId, String toEntityId, SecurityEntityRelationType relationType) throws SecurityException
    {
        EntityRelationDAO dao = entityRelationDAOs.get(relationType instanceof SecurityEntityRelationTypeImpl ? relationType : new SecurityEntityRelationTypeImpl(relationType));
        if (dao != null)
        {
            dao.addRelation(entityDAOs.get(relationType.getFromEntityType()), entityDAOs.get(relationType.getToEntityType()), fromEntityId, toEntityId);
        }
    }
    
    public void removeRelation(String fromEntityId, String toEntityId, SecurityEntityRelationType relationType) throws SecurityException
    {
        EntityRelationDAO dao = entityRelationDAOs.get(relationType instanceof SecurityEntityRelationTypeImpl ? relationType : new SecurityEntityRelationTypeImpl(relationType));
        if (dao != null)
        {
            dao.removeRelation(entityDAOs.get(relationType.getFromEntityType()), entityDAOs.get(relationType.getToEntityType()), fromEntityId, toEntityId);
        }
    }
    
    public Collection<Entity> getAllEntities(String entityType) throws SecurityException
    {
        EntityDAO dao = entityDAOs.get(entityType);
        return dao != null ? dao.getAllEntities() : null;
    }

    public Entity getEntity(String entityType, String entityId) throws SecurityException
    {
        EntityDAO dao = entityDAOs.get(entityType);
        return dao != null ? dao.getEntity(entityId) : null;
    }

    public Collection<Entity> getRelatedEntitiesTo(Entity toEntity, SecurityEntityRelationType relationType) throws SecurityException
    {
        EntityRelationDAO relationDAO = entityRelationDAOs.get(relationType instanceof SecurityEntityRelationTypeImpl ? relationType : new SecurityEntityRelationTypeImpl(relationType));
        if (relationDAO != null)
        {
            EntityDAO fromDAO = entityDAOs.get(relationType.getFromEntityType());
            EntityDAO toDAO = entityDAOs.get(relationType.getToEntityType());
            if (fromDAO != null && toDAO != null && toDAO.getEntityType().equals(toEntity.getType()))
            {
                return relationDAO.getRelatedEntitiesTo(fromDAO, toDAO, toEntity);
            }
        }
        return null; // todo : throw exception, since combination of entity
        // types and relation type is not configured.
    }

    public Collection<Entity> getRelatedEntitiesFrom(Entity fromEntity, SecurityEntityRelationType relationType) throws SecurityException
    {
        EntityRelationDAO relationDAO = entityRelationDAOs.get(relationType instanceof SecurityEntityRelationTypeImpl ? relationType : new SecurityEntityRelationTypeImpl(relationType));
        if (relationDAO != null)
        {
            EntityDAO fromDAO = entityDAOs.get(relationType.getFromEntityType());
            EntityDAO toDAO = entityDAOs.get(relationType.getToEntityType());
            if (fromDAO != null && toDAO != null && fromDAO.getEntityType().equals(fromEntity.getType()))
            {
                return relationDAO.getRelatedEntitiesFrom(fromDAO, toDAO, fromEntity);
            }
        }
        return null; // todo : throw exception, since combination of entity
        // types and relation type is not configured.
    }

    public void updateEntity(Entity entity) throws SecurityException
    {
        EntityDAO dao = getDAOForEntity(entity);
        if (dao != null)
        {
            dao.update(entity);
        }
    }

    public void removeEntity(Entity entity) throws SecurityException
    {
        EntityDAO dao = getDAOForEntity(entity);
        if (dao != null)
        {
            dao.remove(entity);
        }
    }

    public void addEntity(Entity entity) throws SecurityException
    {
        EntityDAO dao = getDAOForEntity(entity);
        if (dao != null)
        {
            dao.add(entity);
        }
    }

    public void addEntity(Entity entity, Entity parentEntity) throws SecurityException
    {
        EntityDAO parentEntityDao = getDAOForEntity(parentEntity);
        EntityDAO dao = getDAOForEntity(entity);
        Entity liveParentEntity = null;
        if (parentEntityDao != null && dao != null)
        {
            // fetch "live" entity from LDAP to
            // 1) check whether entity exists and
            // 2) fetch all LDAP attributes (mapped and not mapped) + fill the internal ID
            liveParentEntity = parentEntityDao.getEntity(parentEntity.getId());
            if (liveParentEntity == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(parentEntity.getType(), parentEntity.getId()));
            }
            dao.add(entity, liveParentEntity);
        }
    }

    public EntityFactory getEntityFactory(String entityType)
    {
        EntityDAO dao = entityDAOs.get(entityType);
        return dao != null ? dao.getEntityFactory() : null;
    }
}
