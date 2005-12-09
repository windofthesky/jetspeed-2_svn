/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.jetspeed.profiler.rules.RuleCriterion;

/**
 * RuleCriterionImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RuleCriterionImpl implements RuleCriterion
{
    private String id;
    private String ruleId;
    private String type;
    private String name = null;
    private String value;
    private int fallbackType = RuleCriterion.FALLBACK_CONTINUE;
    private int fallbackOrder;
    
    public RuleCriterionImpl()
    {
    }

    public RuleCriterionImpl(RuleCriterion master)
    {
        this.name = master.getName();
        this.ruleId = master.getRuleId();
        this.type = master.getType();
        this.value = master.getValue();
        this.fallbackOrder = master.getFallbackOrder();
        this.fallbackType = master.getFallbackType();
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#getType()
     */
    public String getType()
    {
        return this.type;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#setType(java.lang.String)
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#getName()
     */
    public String getName()
    {
        return this.name;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#getRuleId()
     */
    public String getRuleId()
    {
        return ruleId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#setRuleId(java.lang.String)
     */
    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#getFallbackOrder()
     */
    public int getFallbackOrder()
    {
        return fallbackOrder;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#getValue()
     */
    public String getValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#setFallbackOrder(int)
     */
    public void setFallbackOrder(int i)
    {
        fallbackOrder = i;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#getFallbackType()
     */
    public int getFallbackType()
    {
        return fallbackType;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#setFallbackType(int)
     */
    public void setFallbackType(int i)
    {
        fallbackType = i;
    }

    
}
