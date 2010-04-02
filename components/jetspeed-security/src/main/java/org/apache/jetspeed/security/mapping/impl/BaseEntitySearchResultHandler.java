/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.mapping.impl;

import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.EntitySearchResultHandler;
import org.apache.jetspeed.security.mapping.model.Entity;

/**
 * @version $Id$
 *
 */
public class BaseEntitySearchResultHandler extends BaseSearchResultHandler<Entity,Object> implements EntitySearchResultHandler
{
    private EntityFactory entityFactory;
    
    public BaseEntitySearchResultHandler()
    {
    }

    public BaseEntitySearchResultHandler(int maxCount)
    {
        super(maxCount);
    }
    
    public void setEntityFactory(EntityFactory entityFactory)
    {
        this.entityFactory = entityFactory;
    }

    protected Entity mapResult(Object result, int pageSize, int pageIndex, int index)
    {
        return entityFactory.loadEntity(result);
    }
}
