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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.prefs.Preferences;

import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.impl.GeneralizationHierarchyResolver;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.om.InternalRolePrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;

/**
 * @see org.apache.jetspeed.security.spi.RoleSecurityHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class DefaultRoleSecurityHandler implements RoleSecurityHandler
{
    /** Common queries. */
    private CommonQueries commonQueries = null;
    
    /** The role hierarchy resolver. */
    HierarchyResolver roleHierarchyResolver = new GeneralizationHierarchyResolver();
    
    /**
     * <p>Constructor providing access to the common queries.</p>
     */
    public DefaultRoleSecurityHandler(CommonQueries commonQueries)
    {
        this.commonQueries = commonQueries;
    }
    
    /**
     * <p>Constructor providing access to the common queries and specifying the role
     * hierarchy resolution strategy.</p>
     */
    public DefaultRoleSecurityHandler(CommonQueries commonQueries, HierarchyResolver roleHierarchyResolver)
    {
        this.commonQueries = commonQueries;
        this.roleHierarchyResolver = roleHierarchyResolver;
    }
   
    /**
     * @see org.apache.jetspeed.security.spi.RoleSecurityHandler#setRoleHierarchyResolver(org.apache.jetspeed.security.HierarchyResolver)
     */
    public void setRoleHierarchyResolver(HierarchyResolver roleHierarchyResolver)
    {
        this.roleHierarchyResolver = roleHierarchyResolver;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.RoleSecurityHandler#getRolePrincipals(java.lang.String)
     */
    public Set getRolePrincipals(String username)
    {
        Set rolePrincipals = new HashSet();
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username);
        if (null != internalUser)
        {
            Collection internalRoles = internalUser.getRolePrincipals();
            if (null != internalRoles)
            {
                Iterator internalRolesIter = internalRoles.iterator();
                while (internalRolesIter.hasNext())
                {
                    InternalRolePrincipal internalRole = (InternalRolePrincipal) internalRolesIter.next();
                    Preferences preferences = Preferences.userRoot().node(internalRole.getFullPath());
                    String [] fullPaths = roleHierarchyResolver.resolve(preferences);
                    for (int i = 0; i < fullPaths.length; i++)
                    {
                        rolePrincipals.add(new RolePrincipalImpl(RolePrincipalImpl.getPrincipalNameFromFullPath(fullPaths[i])));    
                    }
                }
            }
        }
        return rolePrincipals;
    }
}
