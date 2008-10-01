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

import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.springframework.ldap.filter.Filter;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */
public class StubEntityDAO implements EntityDAO
{

    private Map<String,Entity> entities = new HashMap<String,Entity>();
    
    public void addEntity(Entity entity)
    {
        entities.put(entity.getId(),entity);
    }

    public Collection<Entity> getAllEntities()
    {
        return entities.values();
    }

    public Collection<Entity> getEntities(Filter filter)
    {
        return entities.values();
    }

    public Collection<Entity> getEntitiesById(Collection<String> entityIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<Entity> getEntitiesByInternalId(Collection<String> entityIds)
    {
        // TODO Auto-generated method stub
        return null;
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

}
