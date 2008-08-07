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
package org.apache.jetspeed.security.impl;

import java.security.Principal;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.attributes.SecurityAttributes;

/**
 * <p>Represents a security 'role' made of a {@link org.apache.jetspeed.security.RolePrincipal} and security attributes.</p>
 * <p>Modified 2008-08-05 - DST - decoupled java preferences</p> 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class RoleImpl implements Role
{
    private Principal rolePrincipal;
    private SecurityAttributes attributes;
    
    /**
     * <p>Default constructor.</p>
     */
    public RoleImpl()
    {
    }

    /**
     * <p>{@link Role} constructor given a role principal and its security attributes.</p>
     * @param rolePrincipal The role principal.
     * @param attributes The security attributes.
     */
    public RoleImpl(Principal rolePrincipal, SecurityAttributes attributes)
    {
        this.rolePrincipal = rolePrincipal;
        this.attributes = attributes;
    }

    
    /**
     * @see org.apache.jetspeed.security.Role#getPrincipal()
     */
    public Principal getPrincipal()
    {
        return this.rolePrincipal;
    }

    /**
     * @see org.apache.jetspeed.security.Role#setPrincipal(java.security.Principal)
     */
    public void setPrincipal(Principal rolePrincipal)
    {
        this.rolePrincipal = rolePrincipal;
    }

    public SecurityAttributes getAttributes()
    {
        return this.attributes;
    }

    public void setAttributes(SecurityAttributes attributes)
    {
        this.attributes = attributes;
    }

}
