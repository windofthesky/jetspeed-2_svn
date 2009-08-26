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
package org.apache.jetspeed.spaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Space object 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SpaceImpl implements Space, Serializable
{
    private String name;
    private String description;
    private String title;
    private String owner;
    private List<String> templates = new ArrayList<String>();
    private String path;
    
    public SpaceImpl(String name, String path, String owner)
    {
        this.name = name;        
        this.path = path;
        this.owner = owner;
    }
    
    public void addTemplate(String template)
    {
        templates.add(template);
    }

    public String getDescription()
    {
        return description;
    }

    public String getName()
    {
        return name;
    }

    public List<String> getTemplates()
    {
        return templates;
    }

    public String getTitle()
    {
        return title;
    }

    public void removeTemplate(String template)
    {
        templates.remove(template);
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getPath()
    {
        return path;       
    }

    public void setPath(String path)
    {
        this.path = path;
    }
    
    public String getOwner()
    {
        return owner;
    }
}
