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

package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.ContainerRuntimeOptionValue;
import org.apache.jetspeed.util.ojb.CollectionUtils;

/**
 * @version $Id$
 *
 */
public class ContainerRuntimeOptionImpl implements ContainerRuntimeOption, Serializable
{
    private static final long serialVersionUID = 1L;
    protected String name;
    protected List<ContainerRuntimeOptionValue> values = new ArrayList<ContainerRuntimeOptionValue>();    
    protected String owner;    

    public ContainerRuntimeOptionImpl()
    {}
    
    public ContainerRuntimeOptionImpl(Object owner, String name)
    {
        this.owner = owner.getClass().getName();        
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    @SuppressWarnings("unchecked")
    public void addValue(String value)
    {
        if (values == null)
        {
            values = CollectionUtils.createList();
        }
        for (ContainerRuntimeOptionValue param : this.values)
        {
            if (param.equals(value))
            {
                throw new IllegalArgumentException("Support for container runtime parameter with identifier: "+value+" already defined");
            }
        }
        values.add(new ContainerRuntimeOptionValueImpl(value));                
    }

    @SuppressWarnings("unchecked")
    public List<String> getValues()
    {
        if (values == null)
        {
            values = CollectionUtils.createList();
        }
        List<String> vals = new ArrayList<String>();
        for (ContainerRuntimeOptionValue v : this.values)
        {
            vals.add(v.toString());
        }
        return vals;
    }
}
