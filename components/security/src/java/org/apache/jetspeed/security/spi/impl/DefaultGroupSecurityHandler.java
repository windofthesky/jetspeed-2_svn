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
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;

/**
 * @see org.apache.jetspeed.security.spi.GroupSecurityHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class DefaultGroupSecurityHandler implements GroupSecurityHandler
{
    /** Common queries. */
    private CommonQueries commonQueries = null;
    
    /** The role hierarchy resolver. */
    HierarchyResolver groupHierarchyResolver = new GeneralizationHierarchyResolver();
    
    /**
     * <p>Constructor providing access to the common queries.</p>
     */
    public DefaultGroupSecurityHandler(CommonQueries commonQueries)
    {
        this.commonQueries = commonQueries;
    }
    
    /**
     * <p>Constructor providing access to the common queries and specifying the group
     * hierarchy resolution strategy.</p>
     */
    public DefaultGroupSecurityHandler(CommonQueries commonQueries, HierarchyResolver groupHierarchyResolver)
    {
        this.commonQueries = commonQueries;
        this.groupHierarchyResolver = groupHierarchyResolver;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#setGroupHierarchyResolver(org.apache.jetspeed.security.HierarchyResolver)
     */
    public void setGroupHierarchyResolver(HierarchyResolver groupHierarchyResolver)
    {
        this.groupHierarchyResolver = groupHierarchyResolver;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#getGroupPrincipals(java.lang.String)
     */
    public Set getGroupPrincipals(String username)
    {
        Set groupPrincipals = new HashSet();
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username);
        if (null != internalUser)
        {
            Collection internalGroups = internalUser.getGroupPrincipals();
            if (null != internalGroups)
            {
                Iterator internalGroupsIter = internalGroups.iterator();
                while (internalGroupsIter.hasNext())
                {
                    InternalGroupPrincipal internalGroup = (InternalGroupPrincipal) internalGroupsIter.next();
                    Preferences preferences = Preferences.userRoot().node(internalGroup.getFullPath());
                    String [] fullPaths = groupHierarchyResolver.resolve(preferences);
                    for (int i = 0; i < fullPaths.length; i++)
                    {
                        groupPrincipals.add(new GroupPrincipalImpl(GroupPrincipalImpl.getPrincipalNameFromFullPath(fullPaths[i])));    
                    }
                }
            }
        }
        return groupPrincipals;
    }
}
