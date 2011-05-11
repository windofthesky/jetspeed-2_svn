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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.EntitySearchResultHandler;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityRelationDAO;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.SecurityEntityRelationType;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */
public class StubEntityRelationDAO implements EntityRelationDAO
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
    
    private Map<Entity,Collection<Entity>> fromTo = new HashMap<Entity,Collection<Entity>>();
    private Map<Entity,Collection<Entity>> toFrom = new HashMap<Entity,Collection<Entity>>();
    private SecurityEntityRelationType relationType;
    
    
    public StubEntityRelationDAO(SecurityEntityRelationType relationType)
    {
        super();
        this.relationType = relationType;
    }

    public void getRelatedEntitiesFrom(EntityDAO fromDao, EntityDAO toDao, Entity fromEntity, EntitySearchResultHandler handler)
    {
        handler.setEntityFactory(copyingEntityFactory);
        int index = 0;
        for (Entity e : fromTo.get(fromEntity) )
        {
            handler.handleSearchResult(e, 0, index, index);
            index++;
        }
        handler.setEntityFactory(null);
    }

    public void getRelatedEntitiesTo(EntityDAO fromDao, EntityDAO toDao, Entity toEntity, EntitySearchResultHandler handler)
    {
        handler.setEntityFactory(copyingEntityFactory);
        int index = 0;
        for (Entity e : toFrom.get(toEntity) )
        {
            handler.handleSearchResult(e, 0, index, index);
            index++;
        }
        handler.setEntityFactory(null);
    }
    
    public SecurityEntityRelationType getRelationType()
    {
        return relationType;
    }

    private void internalRelate(Map<Entity,Collection<Entity>> mapping, Entity startEntity, Entity endEntity){
        Collection<Entity> mappedEntities = mapping.get(startEntity);
        if (mappedEntities == null){
            mappedEntities=new ArrayList<Entity>();
            mapping.put(startEntity,mappedEntities);
        }
        if (!mappedEntities.contains(endEntity)){
            mappedEntities.add(endEntity);
        }
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.mapping.ldap.dao.EntityRelationDAO#relate(org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO, org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO, org.apache.jetspeed.security.mapping.model.Entity, org.apache.jetspeed.security.mapping.model.Entity)
     */
    public void relate(EntityDAO sourceDao, EntityDAO targetDao, Entity sourceEntity, Entity targetEntity)
    {
        internalRelate(fromTo,sourceEntity,targetEntity);
        internalRelate(toFrom,targetEntity,sourceEntity);
    }

    public void addRelation(EntityDAO sourceDao, EntityDAO targetDao, Entity sourceEntity, Entity targetEntity)
    {
        // TODO Auto-generated method stub
        
    }

    public void removeRelation(EntityDAO sourceDao, EntityDAO targetDao, Entity sourceEntity, Entity targetEntity)
    {
        // TODO Auto-generated method stub
        
    }

    public void addRelation(EntityDAO sourceDao, EntityDAO targetDao, String sourceEntityId, String targetEntityId) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }

    public void removeRelation(EntityDAO sourceDao, EntityDAO targetDao, String sourceEntityId, String targetEntityId) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }
}
