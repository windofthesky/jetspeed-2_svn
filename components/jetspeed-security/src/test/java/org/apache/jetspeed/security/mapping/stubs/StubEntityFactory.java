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
import java.util.Map;

import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.impl.EntityImpl;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */
public class StubEntityFactory
{
    private Map<String,AttributeDef> attributeDefs;

    public void setAttributeDefs(Collection<AttributeDef> attributeDefs){
        for (AttributeDef attributeDef : attributeDefs)
        {
            this.attributeDefs.put(attributeDef.getName(), attributeDef);
        }
    }
    
    public Entity createEntity(String id, String entityType, String[]... attrKeyValuePairs){
        
        EntityImpl stubEntity = new EntityImpl("user", id, attributeDefs);
        for (int i = 0; i < attrKeyValuePairs.length; i++)
        {
            AttributeDef def = attributeDefs.get(attrKeyValuePairs[i][0]);
            if (def!=null){
                stubEntity.setAttribute(def.getName(), attrKeyValuePairs[i][1]);
            }
        }
        stubEntity.setInternalId(id); // simply use the id
        return stubEntity;
    }
    
}
