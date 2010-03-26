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
     * @param entity
     *            IDs
     * @return found entities
     */
    Collection<Entity> getEntitiesById(Collection<String> entityIds);

    /**
     * Fetch entity by providing an *internal* entity ID.
     * 
     * @param internalId
     * @return found entity
     */
    Entity getEntityByInternalId(String internalId);

    /**
     * Fetch entities by providing a list of specific *internal* entity IDs.
     * 
     * @param internal
     *            entity IDs
     * @return found entities
     */
    Collection<Entity> getEntitiesByInternalId(Collection<String> entityIds);

    /**
     * Method for applying a specific filter on the complete entity set returned by the DAO. The result would be the same as applying the specific filter to the
     * result of getAllEntities().
     * 
     * @param filter
     *            a specific filter to narrow the returned entity set
     * @return found entities
     */
    Collection<Entity> getEntities(Filter filter);

    /**
     * Same as getEntities(Filter filter), except that this method only returns entities which are children of the given parent entity.
     * 
     * @param parentEntity
     * @param filter
     * @return
     */
    Collection<Entity> getEntities(Entity parentEntity, Filter filter);

    /**
     * Fetch a single entity by ID.
     * 
     * @param entityId
     * @return the entity
     */
    Entity getEntity(String entityId);

    /**
     * Returns the parent entity of the given entity, if there is any.
     * 
     * @param filter
     *            a specific filter to narrow the returned entity set
     * @return found entities
     */
    Entity getParentEntity(Entity childEntity);

    /**
     * Fetch all entities
     * 
     * @return found entities
     */
    Collection<Entity> getAllEntities();

    void update(Entity entity) throws SecurityException;

    void updateInternalAttributes(Entity entity) throws SecurityException;

    void add(Entity entity) throws SecurityException;

    void remove(Entity entity) throws SecurityException;

    void add(Entity entity, Entity parentEntity) throws SecurityException;

    EntityFactory getEntityFactory();
}
