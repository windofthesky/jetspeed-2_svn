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
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.SecurityEntityRelationType;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface EntityRelationDAO
{
    SecurityEntityRelationType getRelationType();

    Collection<Entity> getRelatedEntitiesFrom(EntityDAO fromDao, EntityDAO toDao, Entity fromEntity);

    Collection<Entity> getRelatedEntitiesTo(EntityDAO fromDao, EntityDAO toDao, Entity toEntity);

    void addRelation(EntityDAO sourceDao, EntityDAO targetDao, Entity sourceEntity, Entity targetEntity) throws SecurityException;

    void removeRelation(EntityDAO sourceDao, EntityDAO targetDao, Entity sourceEntity, Entity targetEntity) throws SecurityException;
}
