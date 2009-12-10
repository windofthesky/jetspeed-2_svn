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
package org.apache.jetspeed.portlets.layout;

import java.util.HashMap;
import java.util.Map;


public class NameValueProperty
{
    public static final String NAME_VALUE_PROPERTY = "jsdesktop";
    public static final String DETACHED = "detached";
    public static final String DECORATOR_RENDERED = "decRendered";
    
    private String property;
    private Map<String,String> nvp = null;
    
    public NameValueProperty(String property)
    {        
        this.property = property;
        if (this.property != null)
        {
            String[] pairs = property.split("\\,");
            for (String pair : pairs)
            {
                if (pair.indexOf('=') > -1)
                {
                    if (nvp == null)
                    {
                        nvp = new HashMap<String,String>();
                    }
                    String[]nameValue = pair.split("\\=");
                    nvp.put(nameValue[0], nameValue[1]);
                }                
            }
        }
    }
    
    public boolean isDetached()
    {
        return getValue(DETACHED);
    }
    
    public boolean isDecoratorRendered()
    {
        return getValue(DECORATOR_RENDERED);
    }
    
    protected boolean getValue(String name)
    {
        if (nvp == null)
            return false;
        String result = nvp.get(name);
        if (result == null || result.equalsIgnoreCase("false"))
            return false;
        return true;        
    }
    
}