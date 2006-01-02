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
 * Jetspeed Serialized (JS) User
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSUser
{
    private String name;
    private String password;
    private String template;
    private String roles;
    private String groups;
    private List userInfo = new ArrayList();
    private List profileRules = new ArrayList();
    
    public JSUser()
    {        
    }

    
    public String getGroups()
    {
        return groups;
    }

    
    public void setGroups(String groups)
    {
        this.groups = groups;
    }

    
    public String getPassword()
    {
        return password;
    }

    
    public void setPassword(String password)
    {
        this.password = password;
    }

    
    public String getRoles()
    {
        return roles;
    }

    
    public void setRoles(String roles)
    {
        this.roles = roles;
    }

    
    public String getTemplate()
    {
        return template;
    }

    
    public void setTemplate(String template)
    {
        this.template = template;
    }

    
    public String getName()
    {
        return name;
    }

    
    public void setName(String name)
    {
        this.name = name;
    }

    
    public List getUserInfo()
    {
        return userInfo;
    }

    
    public void setUserInfo(List userInfo)
    {
        this.userInfo = userInfo;
    }


    
    public List getProfileRules()
    {
        return profileRules;
    }


    
    public void setProfileRules(List profileRules)
    {
        this.profileRules = profileRules;
    }


    
    
    
}