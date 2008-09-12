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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class AttributeImpl implements Attribute
{

    private Collection<String> values = new ArrayList<String>();

    private String value;

    private AttributeDef definition;

    public AttributeImpl(AttributeDef definition)
    {
        super();
        this.definition = definition;
    }

    public String getValue()
    {
        return getDefinition().isMultiValue() ? null : value;
    }

    public void setValue(String value)
    {
        this.values = null;
        this.value = value;
    }

    public String getName()
    {
        return definition.getName();
    }

    public String getMappedName()
    {
        return definition.getMappedName();
    }
    
    public Collection<String> getValues()
    {
        return getDefinition().isMultiValue() ? values : null;
    }

    public void setValues(Collection<String> values)
    {
        if (getDefinition().isMultiValue())
        {
            this.values = values;
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((definition == null) ? 0 : definition.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AttributeImpl other = (AttributeImpl) obj;
        if (definition == null)
        {
            if (other.definition != null) return false;
        } else if (!definition.equals(other.definition)) return false;
        if (value == null)
        {
            if (other.value != null) return false;
        } else if (!value.equals(other.value)) return false;
        if (values == null)
        {
            if (other.values != null) return false;
        } else if (!values.equals(other.values)) return false;
        return true;
    }

    public AttributeDef getDefinition()
    {
        return definition;
    }

}
