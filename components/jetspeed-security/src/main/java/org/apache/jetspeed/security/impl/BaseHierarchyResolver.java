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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>
 * Base implementation for the hierarchy resolver.
 * <p>
 * <p>Modified 2008-08-05 - DST - decoupled java preferences</p> 
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public abstract class BaseHierarchyResolver implements HierarchyResolver
{
    protected RoleSecurityHandler roleHandler;
    protected GroupSecurityHandler groupHandler; 
    
    public BaseHierarchyResolver(RoleSecurityHandler roleHandler, GroupSecurityHandler groupHandler)
    {
        this.roleHandler = roleHandler;
        this.groupHandler = groupHandler;
    }
    
    public String getHierarchySeparator()
    {
        return HierarchyResolver.DEFAULT_HIERARCHY_SEPARATOR;
    }
}
