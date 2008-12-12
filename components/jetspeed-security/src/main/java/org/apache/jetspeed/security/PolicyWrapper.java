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

import java.io.Serializable;
import java.security.Policy;

/**
 * <p>
 * Simple wrapper for security policy providing the ability to add attribute on Policy and how they
 * should be used by the application.
 * </p>
 * 
 * @author <a href="mailto:LeStrat_David@emc.com">David Le Strat</a>
 */
public class PolicyWrapper implements Serializable
{
    /** The serial version uid.  */
    private static final long serialVersionUID = 3386468724328997598L;

    /** The policy. */
    private transient Policy policy;

    /** Whether to use as a policy. */
    private boolean useAsPolicy = false;

    /** Whether to use as a default policy. */
    private boolean defaultPolicy = false;

    /**
     * @param policy
     * @param useAsPolicy
     * @param defaultPolicy
     */
    public PolicyWrapper(Policy policy, boolean useAsPolicy, boolean defaultPolicy)
    {
        this.policy = policy;
        this.useAsPolicy = useAsPolicy;
        this.defaultPolicy = defaultPolicy;
    }

    /**
     * @return Returns the policy.
     */
    public Policy getPolicy()
    {
        return this.policy;
    }

    /**
     * @param policy The policy to set.
     */
    public void setPolicy(Policy policy)
    {
        this.policy = policy;
    }

    /**
     * @return Returns the defaultPolicy.
     */
    public boolean isDefaultPolicy()
    {
        return defaultPolicy;
    }

    /**
     * @param defaultPolicy The defaultPolicy to set.
     */
    public void setDefaultPolicy(boolean defaultPolicy)
    {
        this.defaultPolicy = defaultPolicy;
    }

    /**
     * @return Returns the useAsPolicy.
     */
    public boolean isUseAsPolicy()
    {
        return useAsPolicy;
    }

    /**
     * @param useAsPolicy The useAsPolicy to set.
     */
    public void setUseAsPolicy(boolean useAsPolicy)
    {
        this.useAsPolicy = useAsPolicy;
    }

}
