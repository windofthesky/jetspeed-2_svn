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
package org.apache.jetspeed.rewriter.rules.impl;

import org.apache.jetspeed.rewriter.rules.Attribute;
import org.apache.jetspeed.rewriter.rules.Rule;

/**
 * Attribute
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class AttributeImpl extends IdentifiedImpl implements Attribute
{
    private Rule rule;
    private String ruleId;
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Ruleset#setId(java.lang.String)
     */
    public void setId(String id)
    {
        if (id != null)
        {
            this.id = id.toUpperCase();
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Attribute#getRule()
     */
    public Rule getRule()
    {
        return this.rule;
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Attribute#setRule(org.apache.jetspeed.cps.rewriter.rules.Rule)
     */
    public void setRule(Rule rule)
    {        
        this.rule = rule;
    }
    
    /**
     * Castor setter to set the rule id.
     * 
     * @param ruleId The rule identifier.
     */
    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }
    
    /**
     * Castor accessor to get the rule id.
     * 
     * @return The rule identifier.
     */
    public String getRuleId()
    {
        return this.ruleId;
    }
        
}
