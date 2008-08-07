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
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;

/**
 * <p>
 * Implementation for "part of" hierarchy. For Example: given roles:
 * <ul>
 * <li>roleA</li>
 * <li>roleA.roleB</li>
 * <li>roleA.roleB.roleC</li>
 * </ul>
 * if a user has the role [roleA] than
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
public class AggregationHierarchyResolver extends BaseHierarchyResolver implements HierarchyResolver
{
    public AggregationHierarchyResolver(RoleSecurityHandler roleHandler, GroupSecurityHandler groupHandler)
    {
        super(roleHandler, groupHandler);
    }
    
    /**
     * Resolve roles by aggregation of children of the given role path
     */
    public Set<RolePrincipal> resolveRoles(String rolePath)
    {
        Set<RolePrincipal> resultSet = new HashSet<RolePrincipal>();
        StringTokenizer tokenizer = new StringTokenizer(this.getHierarchySeparator());
        if (tokenizer.hasMoreTokens())
        {    
            String current = tokenizer.nextToken();
            RolePrincipal rp = this.roleHandler.getRolePrincipal(current);
            if (rp != null)
                resultSet.add(rp);
            while (tokenizer.hasMoreTokens())
            { 
                current = current + this.getHierarchySeparator() + tokenizer.nextToken();
                rp = this.roleHandler.getRolePrincipal(current);
                if (rp != null)
                    resultSet.add(rp);
            }
        }
        else
        {
            RolePrincipal rp = this.roleHandler.getRolePrincipal(rolePath);
            if (rp != null)
                resultSet.add(rp);
        }
        return resultSet;
    }
    
    /**
     * Resolve groups by aggregation of children of the given group path
     */
    public Set<GroupPrincipal> resolveGroups(String groupPath)
    {
        Set<GroupPrincipal> resultSet = new HashSet<GroupPrincipal>();
        StringTokenizer tokenizer = new StringTokenizer(this.getHierarchySeparator());
        if (tokenizer.hasMoreTokens())
        {    
            String current = tokenizer.nextToken();
            GroupPrincipal gp = this.groupHandler.getGroupPrincipal(current);
            if (gp != null)
                resultSet.add(gp);
            while (tokenizer.hasMoreTokens())
            { 
                current = current + this.getHierarchySeparator() + tokenizer.nextToken();
                gp = this.groupHandler.getGroupPrincipal(current);
                if (gp != null)
                    resultSet.add(gp);
            }
        }
        else
        {
            GroupPrincipal gp = this.groupHandler.getGroupPrincipal(groupPath);
            if (gp != null)
                resultSet.add(gp);
        }
        return resultSet;
    }
}