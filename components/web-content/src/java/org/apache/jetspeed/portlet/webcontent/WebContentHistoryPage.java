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

package org.apache.jetspeed.portlet.webcontent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;


/**
 * Information required to re-visit a page in the WebContentPortlet
 *
 * @author <a href="mailto:dyoung@phase2systems.com">David L Young</a>
 * @version $Id: $ 
 */

public class WebContentHistoryPage extends Object
    implements Serializable
{
    private String url;
    private Map params;
    private boolean is_post;

    // Constructors
    
    public WebContentHistoryPage(String url)
    {
        this(url, null, null);
    }
    public WebContentHistoryPage(String url, Map params, String method)
    {
        super();

        // guarantee non-null, so that equals() is well-behaved
        if (url==null)
            throw new IllegalArgumentException("WebContentHistoryPage() - url required");
        
        this.url = url;
        this.params = params != null ? params : new HashMap();
        this.is_post = method != null && method.equalsIgnoreCase("post");
    }
    
    // Base Class Protocol
    
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof WebContentHistoryPage))
            return false ;

        WebContentHistoryPage page = (WebContentHistoryPage)o;
        
        return page.url.equals(this.url) && page.params.equals(this.params) && page.isPost() == this.isPost() ;
    }
    public String toString()
    {
        StringBuffer buff = new StringBuffer();
        buff.append( "[" ).append(isPost() ? "POST: " : "GET: ").append( getUrl() ).append( ", " ).append( getParams().size() ).append(" params: {");
        Iterator iter = getParams().entrySet().iterator();
        while ( iter.hasNext() )
        {
            Map.Entry entry = (Map.Entry)iter.next();
            buff.append("(").append(entry.getKey()).append(" . ").append(ArrayUtils.toString((String[])entry.getKey())).append(")");
        }
        buff.append("}]");
        return buff.toString();
    }
    
    // Data Access
    
    public String getUrl()
    {
        return this.url;
    }
    public Map getParams()
    {
        return this.params;
    }
    public boolean isPost()
    {
        return this.is_post;
    }
}
