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

import java.util.ArrayList;
import java.util.List;

/**
 * Import ProfilingRule
 * 
 *   <profilingRule>
 *       <name>j2</name>
 *       <className>org.apache.jetspeed.profile.RuleImpl</className>
 *       <description>whatever</description>
 *       <criteria>
 *          ...
 *       </criteria>
 *   </profilingRule>
 *   
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSProfilingRule
{
    private List criteria = new ArrayList();
    private String name;
    private String className;
    private String description;
    
    public JSProfilingRule()
    {        
    }

    
    public String getClassName()
    {
        return className;
    }

    
    public void setClassName(String className)
    {
        this.className = className;
    }

    
    public String getDescription()
    {
        return description;
    }

    
    public void setDescription(String description)
    {
        this.description = description;
    }

    
    public String getName()
    {
        return name;
    }

    
    public void setName(String name)
    {
        this.name = name;
    }

    
    public List getCriteria()
    {
        return criteria;
    }

    
    
}