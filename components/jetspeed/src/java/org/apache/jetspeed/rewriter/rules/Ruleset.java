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
package org.apache.jetspeed.rewriter.rules;

import java.util.Collection;

/**
 * Ruleset
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Ruleset extends Identified
{                
    /**
     * Get the remove comments flag for removing comments from the markup source.
     * 
     * @return true True if comments should be removed.
     */
    public boolean getRemoveComments();

    /**
     * Set the remove comments flag for removing comments from the markup source.
     * 
     * @param flag True if comments should be removed.
     */    
    public void setRemoveComments(boolean flag);

    /**
     * Given a tag identifier, lookup and return a tag object.
     * 
     * @param tagId the unique tag identifier
     * @return the tag object for the given identifier
     */
    Tag getTag(String tagId);
        
    /**
     * Given a rule identifier, lookup and return a rule object.
     * 
     * @param ruleId the unique rule identifier
     * @return the rule object for the given identifier
     */        
    Rule getRule(String ruleId);        


    /**
     * Get a collection of rules for this rule set.
     * 
     * @return A collection of rules.
     */
    Collection getRules();    

    /**
     * Get a collection of markup tags for this rule set.
     * 
     * @return A collection of markup tags.
     */
    public Collection getTags();

    /**
     * Synchronize the Ruleset
     * 
     */
    void sync();
    
}
