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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;

/**
 * <p>
 * Implementation for "is a" hierarchy. For Example: if a user has the role
 * [roleA.roleB.roleC] than
 * </p>
 * <code>user.getSubject().getPrincipals()</code> returns:
 * <ul>
 * <li>/role/roleA</li>
 * <li>/role/roleA/roleB</li>
 * <li>/role/roleA/roleB/roleC</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:Artem.Grinshtein@t-systems.com">Artem Grinshtein </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class GeneralizationHierarchyResolver extends BaseHierarchyResolver implements HierarchyResolver
{    
    public GeneralizationHierarchyResolver(RoleSecurityHandler roleHandler, GroupSecurityHandler groupHandler)
    {
        super(roleHandler, groupHandler);
    }

    /**
     * Resolve roles by aggregation of children of the given role path
     */
    public Set<RolePrincipal> resolveRoles(String rolePath)
    {
        List<RolePrincipal> query = this.roleHandler.getRolePrincipals(rolePath);
        Set<RolePrincipal> resultSet = new HashSet<RolePrincipal>();
        for (RolePrincipal rp : query)
        {
            resultSet.add(rp);
        }
        return resultSet;
    }
    
    /**
     * Resolve groups by aggregation of children of the given group path
     */
    public Set<GroupPrincipal> resolveGroups(String groupPath)
    {
        List<GroupPrincipal> query = this.groupHandler.getGroupPrincipals(groupPath);
        Set<GroupPrincipal> resultSet = new HashSet<GroupPrincipal>();
        for (GroupPrincipal gp : query)
        {
            resultSet.add(gp);
        }
        return resultSet;
    }
    
}