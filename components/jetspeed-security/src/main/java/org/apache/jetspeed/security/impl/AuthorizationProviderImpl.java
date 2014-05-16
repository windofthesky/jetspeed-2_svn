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

import org.apache.jetspeed.security.AuthorizationProvider;

import java.security.Policy;
import java.util.Collections;
import java.util.List;

/**
 * @see org.apache.jetspeed.security.AuthorizationProvider
 * @author <a href="mailto:LeStrat_David@emc.com">David Le Strat </a>
 */
public class AuthorizationProviderImpl implements AuthorizationProvider
{

    /**
     * <p>
     * Constructor for adding another policy to be enforced. This constructor makes the assumption
     * that the input policy should be used as the primary policy.
     * </p>
     * 
     * @param policy The policy to configure.
     * @param useDefaultPolicy Whether to also use the default policy.
     */
    public AuthorizationProviderImpl(Policy policy, boolean useDefaultPolicy)
    {
        Policy defaultPolicy = Policy.getPolicy();
        Policy.setPolicy(new JaasPolicyCoordinator(defaultPolicy, policy));
        Policy.getPolicy().refresh();
    }

    /**
     * @see org.apache.jetspeed.security.AuthorizationProvider#getPolicies()
     */
    public List<Policy> getPolicies()
    {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.apache.jetspeed.security.AuthorizationProvider#useDefaultPolicy(boolean)
     */
    public void useDefaultPolicy(boolean whetherToUseDefaultPolicy)
    {
    }

}