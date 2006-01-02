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
 * Serialized Permission
 *      <permission type='folder' resource='/' actions='view, edit'>
 *           <roles>admin, user</roles>
 *           <groups>dev</groups>
 *           <users>joe</users>
 *       </permission>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSPermission
{
    private String type;
    private String resource;
    private String actions;
    private String roles;
    private String groups;
    private String users;
    
    public JSPermission()
    {        
    }

    
    public String getActions()
    {
        return actions;
    }

    
    public void setActions(String actions)
    {
        this.actions = actions;
    }

    
    public String getGroups()
    {
        return groups;
    }

    
    public void setGroups(String groups)
    {
        this.groups = groups;
    }

    
    public String getResource()
    {
        return resource;
    }

    
    public void setResource(String resource)
    {
        this.resource = resource;
    }

    
    public String getRoles()
    {
        return roles;
    }

    
    public void setRoles(String roles)
    {
        this.roles = roles;
    }

    
    public String getType()
    {
        return type;
    }

    
    public void setType(String type)
    {
        this.type = type;
    }

    
    public String getUsers()
    {
        return users;
    }

    
    public void setUsers(String users)
    {
        this.users = users;
    }
    
}