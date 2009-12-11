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
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.commons.collections.list.TreeList;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.util.DefaultKeyValue;
import org.apache.jetspeed.util.HeadElementUtils;
import org.apache.jetspeed.util.KeyValue;


/**
 * PortletContentImpl
 * 
 * @version $Id$
 */
public class PortletContentImpl implements PortletContent
{
    private CharArrayWriter cw;
    private PrintWriter writer;
    private boolean complete;
    private ContentCacheKey cacheKey;
    private int expiration;
    private String title;
    private String contentType;
    private PortletMode portletMode;
    private WindowState windowState;
    
    /**
     * The list container for all contributed head elements from this portlet content.
     * Because the insertion order might be important for web development, this container should be list instead of map.
     */
    private List<KeyValue<String, HeadElement>> headElements;
    
    PortletContentImpl()
    {
        cw = new CharArrayWriter();
        writer = new PrintWriter(cw);
    }
    
    PortletContentImpl(ContentCacheKey cacheKey, int expiration, String title, PortletMode portletMode, WindowState windowState)
    {
        this();
        this.cacheKey = cacheKey;
        this.expiration = expiration;
        this.title = title;
        this.portletMode = portletMode;
        this.windowState = windowState;
    }

    public PrintWriter getWriter()
    {
        return writer;
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
        headElements = null;
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
        this.complete = true;
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
    
    public PortletMode getPortletMode()
    {
        return (portletMode != null ? portletMode : PortletMode.VIEW);
    }
    
    public WindowState getWindowState()
    {
        return (windowState != null ? windowState : WindowState.NORMAL);
    }
    
    public void reset()
    {
        if (!complete)
        {
            resetBuffer();
            headElements = null;
            // TODO: clear other (normal) headers
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

    @SuppressWarnings("unchecked")
    public void addHeadElement(HeadElement headElement, String keyHint)
    {
        if (this.headElements == null)
        {
            // org.apache.commons.collections.list.TreeList is well-optimized for
            // fast insertions at any index in the list.
            // Refer to description in the javadoc for details.
            this.headElements = new TreeList();
        }

        if (headElement == null)
        {
            if (keyHint != null)
            {
                KeyValue<String, HeadElement> kvPair = new DefaultKeyValue(keyHint, null, true);
                this.headElements.remove(kvPair);
            }
            else
            {
                // If element is null and keyHint is null, remove all head elements.
                // This is complying with the portlet spec.
                this.headElements.clear();
            }
            
            return;
        }
        
        if (keyHint == null)
        {
            keyHint = HeadElementUtils.toHtmlString(headElement);
        }

        KeyValue<String, HeadElement> kvPair = new DefaultKeyValue(keyHint, headElement, true);
        
        if (!this.headElements.contains(kvPair))
        {
            this.headElements.add(kvPair);
        }
    }

    public List<KeyValue<String, HeadElement>> getHeadElements()
    {
        List<KeyValue<String, HeadElement>> headElems = null;
        
        if (this.headElements != null) 
        {
            headElems = this.headElements;
        } 
        else 
        {
            headElems = Collections.emptyList();
        }
        
        return headElems;
    }
}