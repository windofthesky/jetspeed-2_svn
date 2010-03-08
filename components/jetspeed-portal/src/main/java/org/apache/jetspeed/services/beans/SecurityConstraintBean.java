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
package org.apache.jetspeed.services.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.common.SecurityConstraint;

/**
 * SecurityConstraintBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="securityConstraint")
public class SecurityConstraintBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private List<String> users;
    private List<String> roles;
    private List<String> groups;
    private List<String> permissions;
    
    public SecurityConstraintBean()
    {
        
    }
    
    public SecurityConstraintBean(SecurityConstraint securityConstraint)
    {
        List<String> temp = securityConstraint.getUsers();
        
        if (temp != null)
        {
            users = new ArrayList<String>(temp);
        }
        
        temp = securityConstraint.getRoles();
        
        if (temp != null)
        {
            roles = new ArrayList<String>(temp);
        }
        
        temp = securityConstraint.getGroups();
        
        if (temp != null)
        {
            groups = new ArrayList<String>(temp);
        }

        temp = securityConstraint.getPermissions();
        
        if (temp != null)
        {
            permissions = new ArrayList<String>(temp);
        }
    }

    @XmlElementWrapper(name="users")
    @XmlElements(@XmlElement(name="user"))
    public List<String> getUsers()
    {
        return users;
    }

    public void setUsers(List<String> users)
    {
        this.users = users;
    }

    @XmlElementWrapper(name="roles")
    @XmlElements(@XmlElement(name="role"))
    public List<String> getRoles()
    {
        return roles;
    }

    public void setRoles(List<String> roles)
    {
        this.roles = roles;
    }

    @XmlElementWrapper(name="groups")
    @XmlElements(@XmlElement(name="group"))
    public List<String> getGroups()
    {
        return groups;
    }

    public void setGroups(List<String> groups)
    {
        this.groups = groups;
    }

    @XmlElementWrapper(name="permissions")
    @XmlElements(@XmlElement(name="permission"))
    public List<String> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(List<String> permissions)
    {
        this.permissions = permissions;
    }
    
}
