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
package org.apache.jetspeed.rewriter;

import java.net.URL;
import java.util.Iterator;

import org.apache.jetspeed.rewriter.rules.Attribute;
import org.apache.jetspeed.rewriter.rules.Rule;
import org.apache.jetspeed.rewriter.rules.Ruleset;
import org.apache.jetspeed.rewriter.rules.Tag;


/**
 * RuleBasedRewriter
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RulesetRewriterImpl extends BasicRewriter implements RulesetRewriter
{
    private Ruleset ruleset = null;
    private boolean removeComments = false;

    public boolean shouldStripTag(String tagid)
    {        
        if (null == ruleset)
        {
            return false;
        }
        
        Tag tag = ruleset.getTag(tagid.toUpperCase());
        if (null == tag)
        {
            return false;
        }
        return tag.getStrip();        
    }
            
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.Rewriter#shouldRemoveTag(java.lang.String)
     */
    public boolean shouldRemoveTag(String tagid)
    {        
        if (null == ruleset)
        {
            return false;
        }
        
        Tag tag = ruleset.getTag(tagid.toUpperCase());
        if (null == tag)
        {
            return false;
        }
        return tag.getRemove();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.RulesetRewriter#setRuleset(org.apache.jetspeed.cps.rewriter.rules.Ruleset)
     */
    public void setRuleset(Ruleset ruleset)
    {
        this.ruleset = ruleset;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.RulesetRewriter#getRuleset()
     */
    public Ruleset getRuleset()
    {
        return this.ruleset;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.Rewriter#shouldRemoveComments()
     */
    public boolean shouldRemoveComments()
    {
        if (null == ruleset)
        {
            return false;
        }
        
        return ruleset.getRemoveComments();                
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#convertTagEvent(java.lang.String, org.xml.sax.Attributes)
     */
    public void enterConvertTagEvent(String tagid, MutableAttributes attributes)
    {
        // System.out.println("tagid = " + tagid);
        
        if (null == ruleset)
        {
            return;
        }
        
        Tag tag = ruleset.getTag(tagid.toUpperCase());
        if (null == tag)
        {
            return;
        }

        Iterator attribRules = tag.getAttributes().iterator();
        while (attribRules.hasNext())
        {
            Attribute attribute = (Attribute)attribRules.next();
            String name = attribute.getId();
            String value = (String)attributes.getValue(name);
            //String id = (String)attributes.getValue("name");
            //System.out.println("id = " + id);
            //System.out.println("value = " + value);
            //System.out.println("name = " + name);
                
            if (value != null) // && name.equalsIgnoreCase(attribute.getId()))
            {
                Rule rule = attribute.getRule();
                if (null == rule)
                {
                    continue;
                }
                
                if (!rule.shouldRewrite(value))
                {
                    continue;
                }                                        
                
                String rewritten = rewriteUrl(value, tag.getId(), name);
                if (null != rewritten) // return null indicates "don't rewrite" 
                {
                    if (rule.getSuffix() != null)
                    {
                        rewritten = rewritten.concat(rule.getSuffix());
                    }
                    
                    attributes.addAttribute(name, rewritten);
                                        
                    if (rule.getPopup())
                    {
                        attributes.addAttribute("TARGET", "_BLANK");                        
                    }
                }
            }            
        }
        
    }

    /*    
     * This callback is called by the ParserAdaptor implementation to write
     * back all rewritten URLs to point to the proxy server.
     * Given the targetURL, rewrites the link as a link back to the proxy server.
     *
     * @return the rewritten URL to the proxy server.
     *
     */
    public String rewriteUrl(
        String url,
        String tag,
        String attribute)
    {
        String fullPath = "";
        try
        {
            String baseUrl = super.getBaseUrl();
            if (baseUrl != null)
            {
                URL full = new URL(new URL(baseUrl), url);
                fullPath = full.toString();
            }
            else
            {
                return url; // leave as is
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
        return fullPath;
    }
    
}
