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
public class SecurityAttributeTypeImpl implements SecurityAttributeType
{
    private String name;
    private String category;
    private DataType dataType;
    private boolean readOnly;
    private boolean required;
    private boolean registered;
    
    public SecurityAttributeTypeImpl(String name)
    {
        this.name = name;
        this.category = SecurityAttributeType.INFO_CATEGORY;
        this.dataType = SecurityAttributeType.DataType.STRING;
        this.readOnly = false;
        this.required = false;
        this.registered = false;
    }

    public SecurityAttributeTypeImpl(String name, String category)
    {
        this.name = name;
        this.category = category;
        this.dataType = SecurityAttributeType.DataType.STRING;
        this.readOnly = false;
        this.required = false;
        this.registered = true;
    }

    public SecurityAttributeTypeImpl(String name, String category, DataType dataType, boolean readOnly, boolean required)
    {
        this.name = name;
        this.category = category;
        this.dataType = dataType;
        this.readOnly = readOnly;
        this.required = required;
        this.registered = true;
    }

    public String getCategory()
    {
        return category;
    }

    public String getName()
    {
        return name;
    }

    public DataType getDataType()
    {
        return dataType;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isRegistered()
    {
        return registered;
    }
}
