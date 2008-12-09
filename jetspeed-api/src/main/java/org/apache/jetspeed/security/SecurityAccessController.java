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

import org.apache.jetspeed.om.portlet.PortletDefinition;

/**
 * <p>
 * This component abstracts access to security checks.
 * Jetspeed supports two kinds of secured access:
 * <ul>
 * <li>Permissions</li>
 * <li>Constraints</li>
 * </ul>
 * Permissions are checked via Java Security. Jetspeed implements its own security policy.
 * Constrainted are checked via the Page Manager's constraints.
 * Either way, the implicit Jetspeed Security Subject is applied to the security access check.
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface SecurityAccessController
{   
    /**
     * Use the Java Security Policy (Permissions) to make secure access checks
     */
    final int PERMISSIONS = 1;
    /**
     * Use the Jetspeed Security Constraints to make secure access checks
     */
    final int CONSTRAINTS = 2;
    
    /**
     * <p>
     * Checks access for the implicit active subject's access to the resource protected by the portlet permission
     * This is an abstraction introduced in 2.1 for Permission Manager implementations NOT
     * founded upon the a Java security policy. If the Permission Manager is configured to 
     * run with Security Constraints, then a security constraint check is made. Otherwise, 
     * a standard Java Security permission check is made.</p>
     * 
     * @param portlet The portlet to be checked
     * @param mask A mask <code>JetspeedActions</code> such as view, edit
     * @return true if access is granted, false if access denied based on policy or constraints
     */
    boolean checkPortletAccess(PortletDefinition portlet, int mask);
    
    /**
     * Returns the configured security mode for this accessor
     * This component can be configured to make Java Security Policy permission checks
     * or Jetspeed Security Constraint checks
     * @return either PERMISSIONS or CONSTRAINTS
     */
    int getSecurityMode();
}