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
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class is used to hold the security that will be used when applying security policies. It
 * uses a singleton pattern to maintain state of the policies configured in the consuming engine.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class SecurityPolicies
{
    /** The singleton instance. */
    private static SecurityPolicies instance = null;

    /** The list of wrapped policies. */
    private List wrappedPolicies = new ArrayList();

    /** The list of policies. */
    private List policies = new ArrayList();

    /** The list of used policies. */
    private List usedPolicies = new ArrayList();

    /**
     * Default contructor. Private to force singleton.
     */
    private SecurityPolicies()
    {
    }

    /**
     * <p>
     * Returns the singleton instance for SecurityPolicies.
     * </p>
     * 
     * @return The instance of SecurityPolicies
     */
    public static SecurityPolicies getInstance()
    {
        if (instance == null)
        {
            instance = new SecurityPolicies();
        }
        return instance;
    }

    /**
     * <p>
     * Adds a policy to the list of policies to enforces.
     * </p>
     * 
     * @param wrappedPolicy The {@link PolicyWrapper} to add.
     */
    public void addPolicy(PolicyWrapper wrappedPolicy)
    {
        if (null != wrappedPolicy)
        {
            wrappedPolicies.add(wrappedPolicy);
            policies.add(wrappedPolicy.getPolicy());
            if (wrappedPolicy.isUseAsPolicy())
            {
                usedPolicies.add(wrappedPolicy.getPolicy());
            }
        }

    }

    /**
     * <p>
     * Returns the security policies to enforce as list of {@link Policy}.
     * </p>
     * 
     * @return The policies.
     */
    public List getPolicies()
    {
        return policies;
    }

    /**
     * <p>
     * Returns the security policies to be enforced as list of {@link Policy}.
     * </p>
     * 
     * @return The used policies.
     */
    public List getUsedPolicies()
    {
        return usedPolicies;
    }

    /**
     * <p>
     * Returns the security policies to enforce as list of {@link PolicyWrapper}.
     * </p>
     * 
     * @return The policies.
     */
    public List getWrappedPolicies()
    {
        return wrappedPolicies;
    }

    /**
     * <p>
     * Removes a policy from the list of policies to enforces.
     * </p>
     * 
     * @param policy The {@link Policy} to add.
     */
    public void removePolicy(PolicyWrapper policy)
    {
        wrappedPolicies.remove(policy);
    }
}
