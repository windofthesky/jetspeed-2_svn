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
package org.apache.jetspeed.cps.rewriter;

import java.net.URL;
import java.util.Iterator;

import org.apache.jetspeed.cps.rewriter.rules.Attribute;
import org.apache.jetspeed.cps.rewriter.rules.Rule;
import org.apache.jetspeed.cps.rewriter.rules.Ruleset;
import org.apache.jetspeed.cps.rewriter.rules.Tag;


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
                
                String rewritten = this.rewriteUrl(value, tag.getId(), name);
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
