/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.page.impl;

import java.util.List;

import org.apache.jetspeed.om.common.SecurityConstraint;

/**
 * Content security constraint implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class ContentSecurityConstraint implements SecurityConstraint
{
    private boolean mutable;
    private List groups;
    private List permissions;
    private List roles;
    private List users;

    /**
     * Construct new security constraint implementation.
     * 
     * @param mutable mutable flag
     * @param groups constraint groups
     * @param permissions constraint permissions
     * @param roles constraint roles
     * @param users constraint users
     */
    public ContentSecurityConstraint(boolean mutable, List groups, List permissions, List roles, List users)
    {
        this.mutable = mutable;
        this.groups = groups;
        this.permissions = permissions;
        this.roles = roles;
        this.users = users;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getGroups()
     */
    public List getGroups()
    {
        return groups;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getPermissions()
     */
    public List getPermissions()
    {
        return permissions;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getRoles()
     */
    public List getRoles()
    {
        return roles;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraint#getUsers()
     */
    public List getUsers()
    {
        return users;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setGroups(java.util.List)
     */
    public void setGroups(List groups)
    {
        if (!mutable)
        {
            throw new UnsupportedOperationException("ContentSecurityConstraint.setGroups()");
        }
        this.groups = groups;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setPermissions(java.util.List)
     */
    public void setPermissions(List permissions)
    {
        if (!mutable)
        {
            throw new UnsupportedOperationException("ContentSecurityConstraint.setPermissions()");
        }
        this.permissions = permissions;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setRoles(java.util.List)
     */
    public void setRoles(List roles)
    {
        if (!mutable)
        {
            throw new UnsupportedOperationException("ContentSecurityConstraint.setRoles()");
        }
        this.roles = roles;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraint#setUsers(java.util.List)
     */
    public void setUsers(List users)
    {
        if (!mutable)
        {
            throw new UnsupportedOperationException("ContentSecurityConstraint.setUsers()");
        }
        this.users = users;
    }
}
