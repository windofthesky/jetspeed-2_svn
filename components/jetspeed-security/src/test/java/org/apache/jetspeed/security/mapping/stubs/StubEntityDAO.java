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
package org.apache.jetspeed.security.mapping.stubs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.EntitySearchResultHandler;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.springframework.ldap.filter.Filter;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */
public class StubEntityDAO implements EntityDAO
{
    private static EntityFactory copyingEntityFactory = new EntityFactory()
    {
        public Entity createEntity(JetspeedPrincipal principal) {return null; }
        public String getEntityType() { return null; }
        public Entity loadEntity(Object entity) { return (Entity)entity; }
        public boolean isCreateAllowed() { return true; }
        public boolean isRemoveAllowed() { return true; }
        public boolean isUpdateAllowed() { return true; }
    };
    
    private Map<String,Entity> entities = new HashMap<String,Entity>();
    
    private void copyEntities(EntitySearchResultHandler handler)
    {
        handler.setEntityFactory(copyingEntityFactory);
        int index = 0;
        for (Entity e : entities.values() )
        {
            handler.handleSearchResult(e, 0, index, index);
            index++;
        }
        handler.setEntityFactory(null);
    }
    
    public String getEntityType()
    {
        return null;
    }
    
    public void getEntities(Entity parentEntity, Filter filter, EntitySearchResultHandler handler)
    {
        // TODO Auto-generated method stub
    }

    public Entity getEntityByInternalId(String internalId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Entity getParentEntity(Entity childEntity)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void addEntity(Entity entity)
    {
        entities.put(entity.getId(),entity);
    }

    public void getAllEntities(EntitySearchResultHandler handler)
    {
        copyEntities(handler);
    }

    public void getEntities(Filter filter, EntitySearchResultHandler handler)
    {
        copyEntities(handler);
    }

    public void getEntitiesById(Collection<String> entityIds, EntitySearchResultHandler handler)
    {
        // TODO Auto-generated method stub
    }

    public void getEntitiesByInternalId(Collection<String> entityIds, EntitySearchResultHandler handler)
    {
        // TODO Auto-generated method stub
    }

    public Entity getEntity(String entityId)
    {
        return entities.get(entityId);
    }

    public void removeEntity(Entity entity)
    {
        entities.remove(entity.getId());
    }

    public void update(Entity entity)
    {
        entities.put(entity.getId(),entity);
    }

    public EntityFactory getEntityFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void add(Entity entity)
    {
        // TODO Auto-generated method stub
        
    }

    public void add(Entity entity, Entity parentEntity)
    {
        // TODO Auto-generated method stub
        
    }

    public void remove(Entity entity)
    {
        // TODO Auto-generated method stub
        
    }

    public void update(Entity entity, boolean updateMappedAttributes)
    {
        // TODO Auto-generated method stub
        
    }

    public void updateInternalAttributes(Entity entity)
    {
        // TODO Auto-generated method stub
        
    }

    public void addRelation(String entityId, String relatedEntityId, String attributeName)
    {
        // TODO Auto-generated method stub
        
    }

    public void removeRelation(String EntityId, String relatedEntityId, String attributeName)
    {
        // TODO Auto-generated method stub
        
    }

    public String getInternalId(String entityId, boolean required) throws SecurityException
    {
        // TODO Auto-generated method stub
        return null;
    }
}