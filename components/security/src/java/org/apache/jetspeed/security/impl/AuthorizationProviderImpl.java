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
package org.apache.jetspeed.security.impl;

import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.security.AuthorizationProvider;

/**
 * @see org.apache.jetspeed.security.AuthorizationProvider
 * @author <a href="mailto:LeStrat_David@emc.com">David Le Strat </a> 
 */
public class AuthorizationProviderImpl implements AuthorizationProvider
{

    /** The list of {@link Policy}. */
    private List policies = new ArrayList();
    
    public AuthorizationProviderImpl(Policy policy)
    {
        // The policy.
        Policy.setPolicy(policy);
        Policy.getPolicy().refresh();
        
        policies.add(policy);
    }
    
    /**
     * @see org.apache.jetspeed.security.AuthorizationProvider#getPolicies()
     */
    public List getPolicies()
    {
        return policies;
    }

}
