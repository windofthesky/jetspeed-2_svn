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

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.jetspeed.rewriter.rules.Rule;

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
