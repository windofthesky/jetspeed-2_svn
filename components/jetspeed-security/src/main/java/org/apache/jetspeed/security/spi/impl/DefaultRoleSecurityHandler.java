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
package org.apache.jetspeed.security.spi.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.om.InternalRolePrincipal;
import org.apache.jetspeed.security.om.impl.InternalRolePrincipalImpl;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityAccess;

/**
 * @see org.apache.jetspeed.security.spi.RoleSecurityHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 */
public class DefaultRoleSecurityHandler implements RoleSecurityHandler
{

    /** Common queries. */
    private SecurityAccess commonQueries = null;

    /**
     * <p>
     * Constructor providing access to the common queries.
     * </p>
     */
    public DefaultRoleSecurityHandler(SecurityAccess commonQueries)
    {
        this.commonQueries = commonQueries;
    }

    /**
     * @see org.apache.jetspeed.security.spi.RoleSecurityHandler#getRolePrincipal(java.lang.String)
     */
    public RolePrincipal getRolePrincipal(String roleName)
    {
        RolePrincipal rolePrincipal = null;
        InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(roleName);        
        if (null != internalRole)
        {
            rolePrincipal = new RolePrincipalImpl(internalRole.getPrincipalId(), internalRole.getName(),
                                            internalRole.isEnabled(), internalRole.isMappingOnly());
        }
        return rolePrincipal;
    }

    /**
     * @see org.apache.jetspeed.security.spi.RoleSecurityHandler#setRolePrincipal(org.apache.jetspeed.security.RolePrincipal)
     */
    public void storeRolePrincipal(RolePrincipal rolePrincipal)
            throws SecurityException
    {        
        InternalRolePrincipal internalRole = commonQueries.getInternalRolePrincipal(rolePrincipal.getName());
        if ( null == internalRole )
        {
            internalRole = new InternalRolePrincipalImpl(rolePrincipal.getName());
            internalRole.setEnabled(rolePrincipal.isEnabled());
            commonQueries.storeInternalRolePrincipal(internalRole, false);
        }
        else if ( !internalRole.isMappingOnly() )
        {
            if ( internalRole.isEnabled() != rolePrincipal.isEnabled() )
            {
                internalRole.setEnabled(rolePrincipal.isEnabled());
                commonQueries.storeInternalRolePrincipal(internalRole, false);
            }
        }
        else
        {
            // TODO: should we throw an exception here?
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.RoleSecurityHandler#removeRolePrincipal(org.apache.jetspeed.security.RolePrincipal)
     */
    public void removeRolePrincipal(RolePrincipal rolePrincipal)
            throws SecurityException
    {
        InternalRolePrincipal internalRole = commonQueries
                .getInternalRolePrincipal(rolePrincipal.getName());
        if (null != internalRole)
        {
            commonQueries.removeInternalRolePrincipal(internalRole);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.RoleSecurityHandler#getRolePrincipals(java.lang.String)
     */
    public List<RolePrincipal> getRolePrincipals(String filter)
    {
        List<RolePrincipal> rolePrincipals = new LinkedList<RolePrincipal>();
        Collection<InternalRolePrincipal> internalRolePrincipals = commonQueries.getInternalRolePrincipals(filter);
        for (InternalRolePrincipal internalRole : internalRolePrincipals)
        {
            rolePrincipals.add(new RolePrincipalImpl(internalRole.getPrincipalId(), internalRole.getName(), 
                                internalRole.isEnabled(), internalRole.isMappingOnly()));
        }
        return rolePrincipals;
    }        
}