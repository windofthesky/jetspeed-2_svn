/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.impl;

import java.security.Principal;

import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.om.InternalRolePrincipal;
import org.apache.jetspeed.security.om.impl.InternalRolePrincipalImpl;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;

/**
 * @see org.apache.jetspeed.security.spi.RoleSecurityHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class DefaultRoleSecurityHandler implements RoleSecurityHandler
{
    /** Common queries. */
    private CommonQueries commonQueries = null;

    /**
     * <p>
     * Constructor providing access to the common queries.
     * </p>
     */
    public DefaultRoleSecurityHandler(CommonQueries commonQueries)
    {
        this.commonQueries = commonQueries;
    }

    /**
     * @see org.apache.jetspeed.security.spi.RoleSecurityHandler#getRolePrincipal(java.lang.String)
     */
    public Principal getRolePrincipal(String roleFullPathName)
    {
        RolePrincipal rolePrincipal = null;
        InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(RolePrincipalImpl
                .getFullPathFromPrincipalName(roleFullPathName));
        if (null != internalRole)
        {
            rolePrincipal = new RolePrincipalImpl(RolePrincipalImpl.getPrincipalNameFromFullPath(internalRole
                    .getFullPath()));
        }
        return rolePrincipal;
    }

    /**
     * @see org.apache.jetspeed.security.spi.RoleSecurityHandler#setRolePrincipal(org.apache.jetspeed.security.RolePrincipal)
     */
    public void setRolePrincipal(RolePrincipal rolePrincipal) throws SecurityException
    {
        String fullPath = rolePrincipal.getFullPath();
        InternalRolePrincipal internalRole = new InternalRolePrincipalImpl(fullPath);
        commonQueries.setInternalRolePrincipal(internalRole);   
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.RoleSecurityHandler#removeRolePrincipal(org.apache.jetspeed.security.RolePrincipal)
     */
    public void removeRolePrincipal(RolePrincipal rolePrincipal) throws SecurityException
    {
        InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(rolePrincipal.getFullPath());
        if (null != internalRole)
        {
            commonQueries.removeInternalRolePrincipal(internalRole);
        }        
    }
}