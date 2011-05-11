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
package org.apache.jetspeed.security.mapping;

import java.util.Set;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.SecurityEntityRelationType;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface SecurityEntityManager
{
    boolean isReadOnly();
    
    Set<String> getSupportedEntityTypes();

    Set<SecurityEntityRelationType> getSupportedEntityRelationTypes();

    Set<SecurityEntityRelationType> getSupportedEntityRelationTypes(String entityType);

    SecurityEntityRelationType getSupportedEntityRelationType(String relationType, String fromEntityType, String toEntityType);
    
    Entity getEntity(String entityType, String entityId) throws SecurityException;

    void getAllEntities(String entityType, EntitySearchResultHandler handler) throws SecurityException;

    void addEntity(Entity entity) throws SecurityException;

    void addEntity(Entity entity, Entity parentEntity) throws SecurityException;

    void removeEntity(Entity entity) throws SecurityException;

    void updateEntity(Entity entity) throws SecurityException;

    void addRelation(String fromEntityId, String toEntityId, SecurityEntityRelationType relationType) throws SecurityException;
    
    void removeRelation(String fromEntityId, String toEntityId, SecurityEntityRelationType relationType) throws SecurityException;
    
    void getRelatedEntitiesFrom(Entity fromEntity, SecurityEntityRelationType relationType, EntitySearchResultHandler handler) throws SecurityException;

    void getRelatedEntitiesTo(Entity toEntity, SecurityEntityRelationType relationType, EntitySearchResultHandler handler) throws SecurityException;

    EntityFactory getEntityFactory(String entityType);
}
