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
package org.apache.jetspeed.security;

import java.util.Set;

/**
 * <p>
 * Resolves hierarchies of roles or groups. Given a path, the resolver will determine all permutations of a role or group
 * and return them as a List.
 * </p>
 * 
 * @author <a href="mailto:Artem.Grinshtein@t-systems.com">Artem Grinshtein </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * 
 * @version $Id: HierarchyResolver.java 187640 2004-09-30 04:01:42Z dlestrat $
 */
public interface HierarchyResolver 
{    
    static final String DEFAULT_HIERARCHY_SEPARATOR = ".";
    
    /**
     * Usually hierarchies are separated by ".", as in a role named "user.admin". The separator is configurable
     * and queried from this service.
     * @return The hierarchy separator such as a "."
     */
    String getHierarchySeparator();
    
    /**
     * <p>
     * Returns absolute path names of a given role, depending on the algorithm.
     * Path names are decomposed based on the algorithm. There are two kinds of algorithms available:
     * </p>
     * <p>
     * Generalization: given a role "engineering.software.developer", 
     *          will return ["engineering.software.developer", "engineering.software", "engineering"] 
     * </p>
     * <p>
     * Aggregation: given a role "a", all existing subroles are aggregated: 
     *          will return ["a", "a.b", "a.c", "a.b.b1", a.b.b2", "a.c.c1", etc] 
     * </p>
     * 
     * @param the role path where we can extract out an array of super roles
     * @return Returns absolute path names of the dependency roles.
     */
    Set<RolePrincipal> resolveRoles(String rolePath);

    /**
     * <p>
     * Returns absolute path names of a given group, depending on the algorithm.
     * Path names are decomposed based on the algorithm. There are two kinds of algorithms available:
     * </p>
     * <p>
     * Generalization: given a group "engineering.software.developer", 
     *          will return ["engineering.software.developer", "engineering.software", "engineering"] 
     * </p>
     * <p>
     * Aggregation: given a group "a", all existing subgroups are aggregated: 
     *          will return ["a", "a.b", "a.c", "a.b.b1", a.b.b2", "a.c.c1", etc] 
     * </p>
     * 
     * @param the group path where we can extract out an array of super groups
     * @return Returns absolute path names of the dependency groups.
     */    
    Set<GroupPrincipal> resolveGroups(String groupPath);
}
