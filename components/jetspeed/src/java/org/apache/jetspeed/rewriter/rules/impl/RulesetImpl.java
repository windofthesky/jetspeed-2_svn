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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jetspeed.rewriter.rules.Attribute;
import org.apache.jetspeed.rewriter.rules.Rule;
import org.apache.jetspeed.rewriter.rules.Ruleset;
import org.apache.jetspeed.rewriter.rules.Tag;

/**
 * RulesetImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
/**
 * Ruleset
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RulesetImpl extends IdentifiedImpl implements Ruleset 
{
    private Collection rules = new ArrayList();
    private Collection tags = new ArrayList();
    private Map ruleMap = new HashMap();
    private Map tagMap = new HashMap();    
    private boolean removeComments = false;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Ruleset#getTag(java.lang.String)
     */
    public Tag getTag(String tagId)
    {
        return (Tag)tagMap.get(tagId);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Ruleset#getRule(java.lang.String)
     */
    public Rule getRule(String ruleId)
    {
        return (Rule)ruleMap.get(ruleId);
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer("Ruleset:" + id);
        if (rules.size() == 0)
        {
            buffer.append(", no rules defined, ");
        }
        else
        {
            buffer.append(", rules: ");
            Iterator it = rules.iterator();
            while (it.hasNext())                    
            {
                RuleImpl rule = (RuleImpl)it.next();
                buffer.append(rule.toString());
                buffer.append(", ");
            }            
        }
        if (tags.size() == 0)
        {
            buffer.append(" no tags defined.");
        }
        else
        {
            buffer.append("tags: ");
            Iterator it = tags.iterator();
            while (it.hasNext())                    
            {
                TagImpl tag = (TagImpl)it.next();
                buffer.append(tag.toString());
                buffer.append(", ");
            }            
        }
        return buffer.toString();        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Ruleset#sync()
     */
    public void sync()
    {
        ruleMap.clear();
        Iterator it = rules.iterator();
        while (it.hasNext())                    
        {
            Rule rule = (Rule)it.next();
            ruleMap.put(rule.getId(), rule);            
        }     
               
        tagMap.clear();        
        it = tags.iterator();
        while (it.hasNext())                    
        {
            Tag tag = (Tag)it.next();
            tagMap.put(tag.getId(), tag);
            Iterator attributes = tag.getAttributes().iterator();
            while (attributes.hasNext())
            {                
                Attribute attribute = (Attribute)attributes.next();                
                if (attribute instanceof AttributeImpl)
                {
                    String ruleId = ((AttributeImpl)attribute).getRuleId();                    
                    Rule rule = (Rule)ruleMap.get(ruleId);                    
                    if (rule != null)
                    {
                        attribute.setRule(rule);
                    }
                }                            
            }
        }                    
    }
    
    /**
     * Castor setter
     * 
     * @param rules
     */
    public void setRules(Collection rules)
    {
        this.rules = rules;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Ruleset#getRules()
     */
    public Collection getRules()
    {
        return this.rules;
    }

    /**
     * Castor setter
     * 
     * @param rules
     */
    public void setTags(Collection tags)
    {
        this.tags = tags;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Ruleset#getTags()
     */
    public Collection getTags()
    {
        return this.tags;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Ruleset#getRemoveComments()
     */
    public boolean getRemoveComments()
    {
        return removeComments;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Ruleset#setRemoveComments(boolean)
     */
    public void setRemoveComments(boolean b)
    {
        removeComments = b;
    }

}
