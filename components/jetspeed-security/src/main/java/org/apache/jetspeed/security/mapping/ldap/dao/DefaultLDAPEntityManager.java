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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.mapping.SecurityEntityRelationType;
import org.apache.jetspeed.security.mapping.impl.SecurityEntityRelationTypeImpl;
import org.apache.jetspeed.security.mapping.model.Entity;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class DefaultLDAPEntityManager implements SecurityEntityManager
{

    // entity type DAOs
    protected Map<String, EntityDAO> entityDAOs;

    protected Map<SecurityEntityRelationType, EntityRelationDAO> entityRelationDAOs = new HashMap<SecurityEntityRelationType, EntityRelationDAO>();

    public Collection<SecurityEntityRelationType> getSupportedEntityRelationTypes()
    {        
        return Collections.unmodifiableCollection(entityRelationDAOs.keySet());
    }

    public Collection<String> getSupportedEntityTypes()
    {
        return Collections.unmodifiableCollection(entityDAOs.keySet());
    }

    public Collection<SecurityEntityRelationType> getSupportedEntityRelationTypes(
            String entityType)
    {
        Collection<SecurityEntityRelationType> supportedRelationTypes=new ArrayList<SecurityEntityRelationType>();
        for (SecurityEntityRelationType type : entityRelationDAOs.keySet())
        {
            if (type.getFromEntityType().equals(entityType) || type.getToEntityType().equals(entityType)){
                supportedRelationTypes.add(type);
            }
            
        }
        return Collections.unmodifiableCollection(supportedRelationTypes);
    }

    private EntityDAO getDAOForEntity(Entity entity)
    {
        return entity.getType() != null ? entityDAOs.get(entity.getType())
                : null;
    }

    private EntityRelationDAO getRelationDAO(String relationType,
            String fromEntityType, String targetEntityType)
    {
        return entityRelationDAOs.get(new SecurityEntityRelationTypeImpl(relationType,
                fromEntityType, targetEntityType));
    }

    public void addRelation(Entity sourceEntity, Entity targetEntity,
            SecurityEntityRelationType relationType)
    {
        EntityRelationDAO relationDAO = entityRelationDAOs.get(relationType);
        if (relationDAO != null)
        {
            EntityDAO sourceDAO;
            EntityDAO targetDAO;
            if (relationType.getFromEntityType().equals(sourceEntity.getType())){
                sourceDAO=entityDAOs.get(sourceEntity.getType());
                targetDAO=entityDAOs.get(relationType.getToEntityType());
            } else {
                targetDAO=entityDAOs.get(sourceEntity.getType());
                sourceDAO=entityDAOs.get(relationType.getToEntityType());
            }         
            if (relationDAO != null)
            {
                relationDAO.addRelation(sourceDAO, targetDAO, sourceEntity,
                        targetEntity);
            }
        }
    }
    
    public void removeRelation(Entity entity, Entity relatedEntity, SecurityEntityRelationType relationType)
    {
        // TODO Auto-generated method stub
        
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

    public Collection<Entity> getRelatedEntitiesTo(Entity toEntity,
            SecurityEntityRelationType relationType)
    {
        EntityDAO fromDAO=entityDAOs.get(relationType.getFromEntityType());
        EntityDAO toDAO=entityDAOs.get(relationType.getToEntityType());
        EntityRelationDAO relationDAO = entityRelationDAOs.get(relationType);
        if (fromDAO != null && toDAO != null && relationDAO != null)
        {
            return relationDAO.getRelatedEntitiesTo(
                    fromDAO, toDAO, toEntity); 
        }
        return null; // todo : throw exception, since combination of entity
                     // types and relation type is not configured.
    }

    public Collection<Entity> getRelatedEntitiesFrom(Entity fromEntity,
            SecurityEntityRelationType relationType)
    {
        EntityDAO fromDAO=entityDAOs.get(relationType.getFromEntityType());
        EntityDAO toDAO=entityDAOs.get(relationType.getToEntityType());
        EntityRelationDAO relationDAO = entityRelationDAOs.get(relationType);
        if (fromDAO != null && toDAO != null && relationDAO != null)
        {
            return relationDAO.getRelatedEntitiesFrom(
                    fromDAO, toDAO, fromEntity); 
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
            this.entityRelationDAOs.put( dao.getRelationType(), dao);
        }
    }

    public EntityFactory getEntityFactory(String entityType)
    {
        EntityDAO dao = entityDAOs.get(entityType);
        
        return dao != null ? dao.getEntityFactory() : null;
    }
}
