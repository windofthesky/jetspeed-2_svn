/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.aggregator.impl;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.cache.ContentCacheKey;


public class PortletContentImpl implements PortletContent
{
    private CharArrayWriter cw;
    private PrintWriter writer;
    private boolean complete = false;
    private ContentCacheKey cacheKey;
    private int expiration = 0;
    private String title;
    private String contentType;
    private PortletRenderer renderer = null;
    
    PortletContentImpl()
    {
        init();
    }
    
    PortletContentImpl(PortletRenderer renderer, ContentCacheKey cacheKey, int expiration, String title)
    {
        this.renderer = renderer;
        this.cacheKey = cacheKey;
        this.expiration = expiration;
        this.title = title;
        init();
    }

    PortletContentImpl(PortletRenderer renderer, ContentCacheKey cacheKey, int expiration)
    {
        this(renderer, cacheKey, expiration,"no title");
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
        if (writer != null)
        {
            writer.close();
            cw.reset();
        }
        cw = null;
        writer = null;
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

    void setComplete(boolean state, boolean notify)
    {
        if (renderer != null && notify)
            renderer.notifyContentComplete(this);
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
       setComplete(true, true);
    }
    
    // error case, don't notify 
    public void completeWithError()
    {
        setComplete(true, false);
    }
    
    public ContentCacheKey getCacheKey()
    {
        return cacheKey;
    }
   
    public int getExpiration()
    {
        return expiration;
    }
    
    public void setExpiration(int expiration)
    {
        this.expiration = expiration;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getContentType()
    {
        return contentType;
    }
    
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
     
    public void reset()
    {
        if (!complete)
        {
            resetBuffer();
            // TODO: clear headers
        }
    }
    
    public void resetBuffer()
    {
        if (!complete)
        {
            writer.flush();
            cw.reset();
        }
    }
}