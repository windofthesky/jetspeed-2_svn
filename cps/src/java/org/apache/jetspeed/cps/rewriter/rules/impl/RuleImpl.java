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

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.jetspeed.cps.rewriter.rules.Rule;

/**
 * Rule
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RuleImpl extends IdentifiedImpl implements Rule
{
    private boolean useBase = true;
    private boolean popup = false;
    private String suffix = null;
    private String prefixes = null;
    private List ignorePrefixes = null; 
        
    public String toString()
    {
        return id;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Rule#getPopup()
     */
    public boolean getPopup()
    {
        return popup;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Rule#getSuffix()
     */
    public String getSuffix()
    {
        return suffix;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Rule#getUseBase()
     */
    public boolean getUseBase()
    {
        return useBase;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Rule#setPopup(boolean)
     */
    public void setPopup(boolean b)
    {
        popup = b;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Rule#setSuffix(java.lang.String)
     */
    public void setSuffix(String string)
    {
        suffix = string;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Rule#setUseBase(boolean)
     */
    public void setUseBase(boolean b)
    {
        useBase = b;
    }
                    
    public void setIgnorePrefixes(String prefixes)
    {      
        this.prefixes = prefixes;                          
    }

    public String getIgnorePrefixes()
    {
        return this.prefixes;        
    }
    
    public boolean shouldRewrite(String url)
    {
        if (prefixes == null)
        {
            return true;
        }
        if (ignorePrefixes == null)
        {
            ignorePrefixes = new ArrayList();
            StringTokenizer tokenizer = new StringTokenizer(prefixes, ",");
            while (tokenizer.hasMoreTokens())
            {
                String token = (String)tokenizer.nextToken();
                ignorePrefixes.add(token);
            }            
            
        }
        
        Iterator list = ignorePrefixes.iterator();
        while (list.hasNext())
        {
            String prefix = (String)list.next();
            if (url.startsWith(prefix))
            {
                return false;
            }
        }
        return true;         
    }
    
        
}
