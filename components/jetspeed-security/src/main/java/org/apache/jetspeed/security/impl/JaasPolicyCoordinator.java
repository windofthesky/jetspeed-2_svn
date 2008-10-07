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

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;

/**
 * <p>
 * Provide coordination between the default policy and Jetspeed custom policy.
 * </p>
 */
public class JaasPolicyCoordinator extends Policy
{
    private final Policy defaultPolicy;

    private final Policy j2Policy;

    /**
     * <p>
     * Constructor for coordinating the policies.
     * </p>
     * 
     * @param defaultPolicy The default policy.
     * @param j2Policy Jetspeed policy.
     */
    public JaasPolicyCoordinator(Policy defaultPolicy, Policy j2Policy)
    {
        this.defaultPolicy = defaultPolicy;
        this.j2Policy = j2Policy;
    }

    /**
     * @see java.security.Policy#getPermissions(java.security.CodeSource)
     */
    public PermissionCollection getPermissions(CodeSource codeSource)
    {
        return defaultPolicy.getPermissions(codeSource);
    }

    /**
     * @see java.security.Policy#refresh()
     */
    public void refresh()
    {
        defaultPolicy.refresh();
        j2Policy.refresh();
    }

    /**
     * @see java.security.Policy#implies(java.security.ProtectionDomain, java.security.Permission)
     */
    public boolean implies(ProtectionDomain domain, Permission permission)
    {
        if (permission.getClass().getName().startsWith("java"))
        {
            return defaultPolicy.implies(domain, permission);
        }
        else
        {
            return j2Policy.implies(domain, permission);
        }
    }
}
