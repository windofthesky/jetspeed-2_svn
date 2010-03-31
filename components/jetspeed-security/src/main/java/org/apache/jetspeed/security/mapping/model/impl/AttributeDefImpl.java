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

import org.apache.jetspeed.security.mapping.model.AttributeDef;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class AttributeDefImpl implements AttributeDef
{
    private String  name;
    private String  mappedName;
    private boolean multiValue;          // default is single-valued
    private boolean mapped   = true;
    private boolean required = false;
    private String  requiredDefaultValue;
    private boolean idAttribute;
    private boolean entityIdAttribute;
    private boolean relationOnly;
    private Boolean dnDefaultValue;

    public AttributeDefImpl(String name)
    {
        this.name = name;
        this.mappedName = name; // default mapping
    }

    public AttributeDefImpl(String name, boolean multiValue)
    {
        this(name);
        this.multiValue = multiValue;
    }

    public AttributeDefImpl(String name, boolean multiValue, boolean isMapped)
    {
        this(name, multiValue);
        this.mapped = isMapped;
    }

    public AttributeDefImpl(String name, boolean multiValue, boolean isMapped, String mappedName)
    {
        this(name, multiValue, isMapped);
        this.mappedName = mappedName;
    }

    public String getName()
    {
        return name;
    }

    public String getMappedName()
    {
        return mappedName;
    }

    public void setMappedName(String mappedName)
    {
        this.mappedName = mappedName;
    }

    public boolean isMultiValue()
    {
        return multiValue;
    }

    public boolean isMapped()
    {
        return mapped;
    }

    public void setMapped(boolean mapped)
    {
        this.mapped = mapped;
    }

    public void setMultiValue(boolean multiValue)
    {
        this.multiValue = multiValue;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public String getRequiredDefaultValue()
    {
        return requiredDefaultValue;
    }

    public void setRequiredDefaultValue(String requiredDefaultValue)
    {
        this.requiredDefaultValue = (requiredDefaultValue != null && requiredDefaultValue.length() == 0) ? null : requiredDefaultValue;
    }
    
    public boolean requiresDnDefaultValue()
    {
        if (dnDefaultValue == null)
        {
            dnDefaultValue = isMultiValue() && isRequired() && getRequiredDefaultValue() == null ? Boolean.TRUE : Boolean.FALSE;
        }
        return dnDefaultValue.booleanValue();
    }
    
    public AttributeDefImpl cfgRequired(boolean required)
    {
        setRequired(required);
        return this;
    }

    public boolean isIdAttribute()
    {
        return idAttribute;
    }

    public void setIdAttribute(boolean idAttribute)
    {
        this.idAttribute = idAttribute;
    }
    
    public void setRelationOnly(boolean relationOnly)
    {
        this.relationOnly = relationOnly;
    }
    
    public boolean isRelationOnly()
    {
        return relationOnly;
    }
    
    public boolean isEntityIdAttribute()
    {
        return entityIdAttribute;
    }
    
    public void setEntityIdAttribute(boolean entityIdAttribute)
    {
        this.entityIdAttribute = entityIdAttribute;
    }

    public AttributeDefImpl cfgRequiredDefaultValue(String requiredDefaultValue)
    {
        setRequiredDefaultValue(requiredDefaultValue);
        return this;
    }

    public AttributeDefImpl cfgIdAttribute(boolean isIdAttribute)
    {
        setIdAttribute(isIdAttribute);
        return this;
    }
    
    public AttributeDefImpl cfgRelationOnly(boolean relationOnly)
    {
        setRelationOnly(relationOnly);
        return this;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (multiValue ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        AttributeDefImpl other = (AttributeDefImpl) obj;
        if (multiValue != other.multiValue)
        {
            return false;
        }
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        return true;
    }
}
