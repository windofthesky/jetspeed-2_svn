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

package org.apache.jetspeed.security.impl;

import org.apache.jetspeed.security.SecurityAttributeType;

/**
 * @version $Id$
 *
 */
public class SecurityAttributeImpl implements org.apache.jetspeed.security.SecurityAttribute
{
    private SecurityAttributeType type;
    private SecurityAttributeValue value;
    private boolean persistent;
    
    public SecurityAttributeImpl(SecurityAttributeType type, SecurityAttributeValue value, boolean persistent)
    {
        this.type = type;
        this.value = value;
        this.persistent = persistent;
    }
    
    public SecurityAttributeValue getSecurityAttributeValue()
    {
        return value;
    }

    public String getStringValue()
    {
        return value.getStringValue();
    }

    public void setStringValue(String stringValue)
    {
        value.setStringValue(stringValue);
    }

    public String getCategory()
    {
        return type.getCategory();
    }

    public DataType getDataType()
    {
        return type.getDataType();
    }

    public String getName()
    {
        return type.getName();
    }

    public boolean isReadOnly()
    {
        return persistent ? type.isReadOnly() : false;
    }

    public boolean isRequired()
    {
        return persistent ? type.isRequired() : false;
    }

    public boolean isRegistered()
    {
        return type.isRegistered();
    }
}
