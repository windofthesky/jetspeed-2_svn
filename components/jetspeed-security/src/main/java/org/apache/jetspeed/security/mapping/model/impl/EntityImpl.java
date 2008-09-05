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
package org.apache.jetspeed.security.mapping.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class EntityImpl implements Entity
{

    private Map<String, Attribute> nameToAttributeMap = new HashMap<String, Attribute>();

    private final Set<AttributeDef> allowedAttributes;

    private String id;

    private String internalId;

    private String type;

    public EntityImpl(String type, String id,
            Set<AttributeDef> allowedAttributes)
    {
        this.type = type;
        this.id = id;
        this.allowedAttributes = Collections.unmodifiableSet(allowedAttributes);
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Attribute getAttribute(String name)
    {
        return nameToAttributeMap.get(name);
    }

    public Set<AttributeDef> getAllowedAttributes()
    {
        return allowedAttributes;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    protected AttributeDef getAttributeDefinition(String name)
    {
        for (AttributeDef def : allowedAttributes)
        {
            if (def.getName().equals(name)) { return def; }
        }
        return null;
    }

    public void setAttribute(String name, String value)
    {
        Attribute attr = nameToAttributeMap.get(name);
        if (attr == null)
        {
            AttributeDef def = getAttributeDefinition(name);
            if (def == null) { return; // TODO: throw proper exception
            }
            if (def.isMultiValue()) { return; // TODO: throw proper exception
            }
            attr = new AttributeImpl(def);
            nameToAttributeMap.put(name, attr);
        }
        attr.setValue(value);
    }

    public void setAttribute(String name, Collection<String> values)
    {
        Attribute attr = nameToAttributeMap.get(name);
        if (attr == null)
        {
            AttributeDef def = getAttributeDefinition(name);
            if (def == null) { return; // TODO: throw proper exception
            }
            if (!def.isMultiValue()) { return; // TODO: throw proper exception
            }
            attr = new AttributeImpl(def);
            nameToAttributeMap.put(name, attr);
        }
        attr.setValues(values);
    }

    public void setAttributes(Set<Attribute> attributes)
    {
        for (Attribute attribute : attributes)
        {
            nameToAttributeMap.put(attribute.getName(), attribute);
        }
    }

    public String getInternalId()
    {
        return internalId;
    }

    public void setInternalId(String internalId)
    {
        this.internalId = internalId;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((internalId == null) ? 0 : internalId.hashCode());
        result = prime
                * result
                + ((nameToAttributeMap == null) ? 0 : nameToAttributeMap
                        .hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        EntityImpl other = (EntityImpl) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (internalId == null)
        {
            if (other.internalId != null) return false;
        } else if (!internalId.equals(other.internalId)) return false;
        if (nameToAttributeMap == null)
        {
            if (other.nameToAttributeMap != null) return false;
        } else if (!nameToAttributeMap.equals(other.nameToAttributeMap))
            return false;
        if (type == null)
        {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        return true;
    }

}
