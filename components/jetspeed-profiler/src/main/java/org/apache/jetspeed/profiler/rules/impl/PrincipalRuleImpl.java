/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.profiler.rules.impl;

import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;

/**
 * PrincipalRuleImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PrincipalRuleImpl implements PrincipalRule
{
    private String principalName;
    private String ruleId;
    private ProfilingRule profilingRule;
    private String locatorName;
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.PrincipalRule#getPrincipalName()
     */
    public String getPrincipalName()
    {
        return this.principalName;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.PrincipalRule#setPrincipalName(java.lang.String)
     */
    public void setPrincipalName(String name)
    {
        this.principalName = name;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.PrincipalRule#getProfilingRule()
     */
    public ProfilingRule getProfilingRule()
    {
        return this.profilingRule;    
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.PrincipalRule#setProfilingRule(org.apache.jetspeed.profiler.rules.ProfilingRule)
     */
    public void setProfilingRule(ProfilingRule rule)
    {
        this.profilingRule = rule;    
        this.ruleId = rule.getId();
    }
    
    /**
     * @return Returns the locatorName.
     */
    public String getLocatorName()
    {
        return locatorName;
    }
    /**
     * @param locatorName The locatorName to set.
     */
    public void setLocatorName(String locatorName)
    {
        this.locatorName = locatorName;
    }
}
