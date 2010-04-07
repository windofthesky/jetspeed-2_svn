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
package org.apache.jetspeed.security.mapping.ldap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.mapping.impl.CollectingEntitySearchResultHandler;
import org.apache.jetspeed.security.mapping.impl.SecurityEntityRelationTypeImpl;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class BasicTestCases
{

    private final boolean debugMode;

    SecurityEntityManager entityManager;

    public BasicTestCases(SecurityEntityManager entityManager, boolean debugMode)
    {
        this.debugMode = debugMode;
        this.entityManager = entityManager;
    }

    public void testFetchSingleEntity(SecurityEntityManager entityManager,
            Entity sampleEntity) throws Exception
    {
        Entity resultUser = entityManager.getEntity(sampleEntity.getType(),
                sampleEntity.getId());
        TestCase.assertNotNull(resultUser);
        printDebug(resultUser);
        TestCase.assertEquals(true, resultUser.equals(sampleEntity));
    }

    public void testFetchAllEntities(int totalNrOfUsers) throws Exception
    {
        CollectingEntitySearchResultHandler handler = new CollectingEntitySearchResultHandler();
        entityManager.getAllEntities("user", handler);
        TestCase.assertNotNull(handler.getResults());
        TestCase.assertEquals(totalNrOfUsers, handler.getSize());
    }

    public void testFetchRelatedEntitiesTo(String fromEntityType,
            String toEntityType, String relationType, String toEntityId,
            Collection<Entity> expectedEntities) throws Exception
    {
        Entity randomEntity = entityManager.getEntity(toEntityType,toEntityId);
        TestCase.assertNotNull(randomEntity);
        CollectingEntitySearchResultHandler handler = new CollectingEntitySearchResultHandler();
        entityManager.getRelatedEntitiesTo(randomEntity, new SecurityEntityRelationTypeImpl(relationType,fromEntityType,toEntityType), handler);
        basicEntityResultSetChecks(expectedEntities, handler.getResults());
    }
    
    public void testFetchRelatedEntitiesFrom(String fromEntityType,
            String toEntityType, String relationType, String fromEntityId,
            Collection<Entity> expectedEntities) throws Exception
    {
        Entity randomEntity = entityManager.getEntity(fromEntityType,fromEntityId);
        TestCase.assertNotNull(randomEntity);
        CollectingEntitySearchResultHandler handler = new CollectingEntitySearchResultHandler();
        entityManager.getRelatedEntitiesFrom(randomEntity, new SecurityEntityRelationTypeImpl(relationType,fromEntityType,toEntityType), handler);
        basicEntityResultSetChecks(expectedEntities, handler.getResults());
    }

    @SuppressWarnings("unchecked")
    private void basicEntityResultSetChecks(
            Collection<Entity> expectedEntities,
            Collection<Entity> resultEntities)
    {
        TestCase.assertNotNull(resultEntities);
        Set<Entity> expectedSet = new HashSet(expectedEntities);
        Set<Entity> resultSet = new HashSet(resultEntities);
        TestCase.assertEquals(true, expectedSet.equals(resultSet));
    }

    private void printDebug(Entity ent)
    {
        if (debugMode)
        {
            for (AttributeDef attributeDef : ent.getAttributeDefinitions())
            {
                Attribute attr = ent.getAttribute(attributeDef.getName());
                if (attr != null)
                {
                    if (attr.getDefinition().isMultiValue())
                    {
                        System.out.println("Values for " + attr.getName()
                                + " :");
                        System.out.println("===");
                        for (String val : attr.getValues())
                        {
                            System.out.println(val);
                        }
                        System.out.println("===");
                    } else
                    {
                        System.out.print("Value for " + attr.getName() + " :");
                        System.out.println(attr.getValue());
                    }

                }
            }
        }

    }

    private void printDebug(Collection<Entity> entities)
    {
        if (debugMode)
        {
            for (Iterator iterator = entities.iterator(); iterator.hasNext();)
            {
                Entity entity = (Entity) iterator.next();
                System.out.println("================================");
                System.out.println("Found " + entities.size() + " entities: ");
                System.out.println("================================");
            }
        }

    }

}
