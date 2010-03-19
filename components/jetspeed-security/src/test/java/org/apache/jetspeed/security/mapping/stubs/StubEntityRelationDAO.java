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

import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityRelationDAO;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.SecurityEntityRelationType;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */
public class StubEntityRelationDAO implements EntityRelationDAO
{

    private Map<Entity,Collection<Entity>> fromTo = new HashMap<Entity,Collection<Entity>>();
    private Map<Entity,Collection<Entity>> toFrom = new HashMap<Entity,Collection<Entity>>();
    private SecurityEntityRelationType relationType;
    
    
    public StubEntityRelationDAO(SecurityEntityRelationType relationType)
    {
        super();
        this.relationType = relationType;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.mapping.ldap.dao.EntityRelationDAO#getRelatedEntitiesFrom(org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO, org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO, org.apache.jetspeed.security.mapping.model.Entity)
     */
    public Collection<Entity> getRelatedEntitiesFrom(EntityDAO fromDao, EntityDAO toDao, Entity fromEntity)
    {
        return fromTo.get(fromEntity);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.mapping.ldap.dao.EntityRelationDAO#getRelatedEntitiesTo(org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO, org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO, org.apache.jetspeed.security.mapping.model.Entity)
     */
    public Collection<Entity> getRelatedEntitiesTo(EntityDAO fromDao, EntityDAO toDao, Entity toEntity)
    {
        return toFrom.get(toEntity);
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

}
