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

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.EntitySearchResultHandler;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.springframework.ldap.filter.Filter;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface EntityDAO
{
    /**
     * @return the Entity type
     */
    String getEntityType();
    
    /**
     * Fetch entities by providing a list of specific entity IDs.
     * 
     * @param entityIds entity IDs
     * @param handler the Entity callback handler called for each entity retrieved
     */
    void getEntitiesById(Collection<String> entityIds, EntitySearchResultHandler handler) throws SecurityException;

    /**
     * Fetch entity by providing an *internal* entity ID.
     * 
     * @param internalId internal entity ID
     * @return found entity
     */
    Entity getEntityByInternalId(String internalId) throws SecurityException;

    /**
     * Fetch entities by providing a list of *internal* entity IDs.
     * 
     * @param entityIds internal entity IDs
     * @param handler the Entity callback handler called for each entity retrieved
     */
    void getEntitiesByInternalId(Collection<String> entityIds, EntitySearchResultHandler handler) throws SecurityException;

    /**
     * Method for applying a specific filter on the complete entity set retrievable by the DAO.
     * 
     * @param filter a specific filter to narrow the returned entity set
     * @param handler the Entity callback handler called for each entity retrieved
     */
    void getEntities(Filter filter, EntitySearchResultHandler handler) throws SecurityException;

    /**
     * Same as getEntities(Filter filter), except that this method only returns entities which are children of the given parent entity.
     * 
     * @param parentEntity
     * @param filter
     * @param handler the Entity callback handler called for each entity retrieved
     */
    void getEntities(Entity parentEntity, Filter filter, EntitySearchResultHandler handler) throws SecurityException;

    /**
     * Fetch a single entity by ID.
     * 
     * @param entityId
     * @return the entity
     */
    Entity getEntity(String entityId) throws SecurityException;

    /**
     * Fetch a entity internalId by ID
     * 
     * @param entityId
     * @param required if true and entity not found SecurityException.PRINCIPAL_DOES_NOT_EXIST will be thrown
     * @return the entity internalId
     */
    String getInternalId(String entityId, boolean required) throws SecurityException;
    
    /**
     * Returns the parent entity of the given entity, if there is any.
     * 
     * @param filter a specific filter to narrow the returned entity set
     * @return parent entity
     */
    Entity getParentEntity(Entity childEntity) throws SecurityException;

    void getAllEntities(EntitySearchResultHandler cbh) throws SecurityException;
    
    void update(Entity entity) throws SecurityException;

    void add(Entity entity) throws SecurityException;

    void remove(Entity entity) throws SecurityException;

    void add(Entity entity, Entity parentEntity) throws SecurityException;

    void addRelation(String entityId, String relatedEntityInternalId, String attributeName) throws SecurityException;

    void removeRelation(String EntityId, String relatedEntityInternalId, String attributeName) throws SecurityException;
    
    EntityFactory getEntityFactory();
}
