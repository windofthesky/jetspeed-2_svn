/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.serializer.objects;

/**
 * Serialized Profiler Criterion
 * <criterion>
 *   <order>0</order>
 *   <type>user</type>
 *   <name>user</name>
 *   <value>*</value>
 *   <fallback>continue</fallback>
 * </criterion>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSCriterion
{
    private int order;
    private String type;
    private String name;
    private String value;
    private String fallback;
    
    public JSCriterion()
    {        
    }

    
    public String getFallback()
    {
        return fallback;
    }

    
    public void setFallback(String fallback)
    {
        this.fallback = fallback;
    }

    
    public String getName()
    {
        return name;
    }

    
    public void setName(String name)
    {
        this.name = name;
    }

    
    public int getOrder()
    {
        return order;
    }

    
    public void setOrder(int order)
    {
        this.order = order;
    }

    
    public String getType()
    {
        return type;
    }

    
    public void setType(String type)
    {
        this.type = type;
    }

    
    public String getValue()
    {
        return value;
    }

    
    public void setValue(String value)
    {
        this.value = value;
    }

}