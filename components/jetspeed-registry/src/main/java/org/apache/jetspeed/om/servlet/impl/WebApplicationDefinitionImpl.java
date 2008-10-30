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
package org.apache.jetspeed.om.servlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.om.servlet.WebApplicationDefinition;

/**
 * 
 * WebApplicationDefinitionImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class WebApplicationDefinitionImpl implements WebApplicationDefinition, Serializable
{
    private static final long serialVersionUID = 257065602152274557L;
    
    private String displayName;
    private String description;
    private List<String> roles = new ArrayList<String>();
    
    private String contextRoot;
    
    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getContextRoot()
    {
        return contextRoot;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public void setContextRoot(String contextRoot)
    {
        this.contextRoot = contextRoot;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<String> getRoles()
    {
        return roles;
    }
    
    public void addRole(String role)
    {
        if (roles.contains(role))
        {
            throw new IllegalArgumentException("Role "+role+" already defined");
        }
        roles.add(role);
    }
}
