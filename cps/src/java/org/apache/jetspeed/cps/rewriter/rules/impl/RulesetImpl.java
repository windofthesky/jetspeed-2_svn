/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.cps.rewriter.rules.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jetspeed.cps.rewriter.rules.Attribute;
import org.apache.jetspeed.cps.rewriter.rules.Rule;
import org.apache.jetspeed.cps.rewriter.rules.Ruleset;
import org.apache.jetspeed.cps.rewriter.rules.Tag;

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
