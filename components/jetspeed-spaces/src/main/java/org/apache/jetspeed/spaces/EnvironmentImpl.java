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

import java.util.ArrayList;
import java.util.List;

/**
 * Environment object 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class EnvironmentImpl implements Environment
{
    private String name;
    private String description;
    private String title;
    private String owner;
    private List<Space> spaces = new ArrayList<Space>();
    private String path;
    
    public EnvironmentImpl(String name, String path, String owner)
    {
        this.name = name;        
        this.path = path;
        this.owner = owner;
    }
    

    public String getDescription()
    {
        return description;
    }

    public String getName()
    {
        return name;
    }


    public String getTitle()
    {
        return title;
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

    public void addSpace(Space space)
    {
        spaces.add(space);
    }

    public Dashboard getDashboard()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Space> getSpaces()
    {
        return spaces;
    }

    public void removeSpace(Space space)
    {
        // TODO Auto-generated method stub
        
    }
}
