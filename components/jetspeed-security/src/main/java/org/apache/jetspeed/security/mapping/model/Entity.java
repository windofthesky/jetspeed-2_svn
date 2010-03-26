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
package org.apache.jetspeed.security.mapping.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface Entity
{
    String getType();

    String getId();
    void setId(String id);

    String getInternalId();
    void setInternalId(String internalId);

    Attribute getAttribute(String name);

    Attribute getAttribute(String name, boolean create);

    /**
     * Returns a read-only map of attributes (name to attribute). To add attributes, call one of the setAttribute() methods
     * 
     * @return collection of all attributes of the entity
     */
    Map<String, Attribute> getAttributes();

    /**
     * Returns a read-only map of attributes (mapped name to attribute). Each attribute is mapped, i.e. is synchronized with a related Jetspeed principal
     * attribute.
     * 
     * @return collection of all attributes of the entity
     */
    Map<String, Attribute> getMappedAttributes();

    void setAttribute(String name, String value);

    void setAttribute(String name, Collection<String> values);

    void setAttributes(Set<Attribute> attributes);

    Set<AttributeDef> getAllowedAttributes();
}
