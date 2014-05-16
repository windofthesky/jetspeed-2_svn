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

import java.security.Policy;
import java.util.List;

/**
 * <p>
 * Configures the policies.  Instantiates the <code>SecurityPolicies</code> with the security policies
 * that need to be enforced.  It will add the default policy already configured as well as the engine policies
 * used to enforce permission checks.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface AuthorizationProvider
{
    /**
     * <p>
     * The list of configured policies.
     * </p>
     * 
     * @return The list of policies.
     */
    List<Policy> getPolicies();
    
    
    /**
     * <p>
     * Whether to use the default policy or not in addition to the Policies configured for the AuthorizationProvider.
     * </p>
     * 
     * @param whetherToUseDefaultPolicy Boolean false: does not use the default policy, true: does.
     */
    void useDefaultPolicy(boolean whetherToUseDefaultPolicy);
}
