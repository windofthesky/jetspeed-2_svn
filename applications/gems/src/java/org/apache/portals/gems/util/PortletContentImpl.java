/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.portals.gems.util;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import org.apache.jetspeed.aggregator.PortletContent;

public class PortletContentImpl implements PortletContent
{
    private CharArrayWriter cw;
    private PrintWriter writer;
    private boolean complete = false;
    private String cacheKey;
    private int expiration = 0;
    private String title;
 
    public PortletContentImpl()
    {
        init();
    }

    PortletContentImpl(String cacheKey, int expiration, String title, boolean complete)
    {
        this.cacheKey = cacheKey;
        this.expiration = expiration;
        this.title = title;
        this.complete = complete;
        init();
    }

    PortletContentImpl(String cacheKey, int expiration)
    {
        this(cacheKey, expiration, "no title", false);
    }
   
    public PrintWriter getWriter()
    {
        return writer;
    }

    public void init()
    {
        cw = new CharArrayWriter();
        writer = new PrintWriter(cw);
    }

    public void release()
    {
        writer.close();
    }

    public String toString()
    {
        writer.flush();
        return cw.toString();
    }

    public void writeTo( java.io.Writer out ) throws java.io.IOException
    {
        writer.flush();
        cw.writeTo(out);
    }

    public char[] toCharArray()
    {
        writer.flush();
        return cw.toCharArray();
    }

    public boolean isComplete()
    {
        return complete;
    }

    void setComplete( boolean state )
    {
        this.complete = state;
    }
    
    public String getContent()
    {
        return toString();
    }
    /**
     * <p>
     * complete
     * </p>
     *
     * @see org.apache.jetspeed.aggregator.PortletContent#complete()
     * 
     */
    public void complete()
    {
       setComplete(true);
    }
        
    public String getCacheKey()
    {
        return cacheKey;
    }
    
    public int getExpiration()
    {
        return expiration;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
   
}