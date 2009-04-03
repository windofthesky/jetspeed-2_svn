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
import java.io.NotSerializableException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.list.TreeList;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.util.DOMUtils;
import org.apache.jetspeed.util.DefaultKeyValue;
import org.apache.jetspeed.util.KeyValue;
import org.w3c.dom.Element;


public class PortletContentImpl implements PortletContent
{
    private CharArrayWriter cw;
    private PrintWriter writer;
    private boolean complete;
    private ContentCacheKey cacheKey;
    private int expiration;
    private String title;
    private String contentType;
    private PortletRenderer renderer;
    
    /**
     * The list container for all contributed head elements from this portlet content.
     * Because the insertion order might be important for web development, this container should be list instead of map.
     */
    private List<KeyValue<String, Element>> headElements;
    
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

    @SuppressWarnings("unchecked")
    public void addHeadElement(Element element, String keyHint) throws NotSerializableException
    {
        if (this.headElements == null)
        {
            // org.apache.commons.collections.list.TreeList is well-optimized for
            // fast insertions at any index in the list.
            // Refer to description in the javadoc for details.
            this.headElements = new TreeList();
        }

        if (element == null)
        {
            if (keyHint != null)
            {
                KeyValue<String, Element> kvPair = new DefaultKeyValue(keyHint, null, true);
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
        
        if (!(element instanceof Serializable))
        {
            throw new NotSerializableException("The element is not serializable.");
        }
        
        if (keyHint == null)
        {
            // If element is dom4j's element, then we can use dom4j's serializer.
            // Meanwhile, the element can be a serializable one which is not from dom4j.
            // In the case, serialize the element to a string as a keyHint.
            
            if (element instanceof org.dom4j.Element) 
            {
                keyHint = DOMUtils.stringifyElement((org.dom4j.Element) element);
            }
            else
            {
                keyHint = DOMUtils.stringifyElement(element);
            }
        }

        KeyValue<String, Element> kvPair = new DefaultKeyValue(keyHint, element, true);
        
        if (!this.headElements.contains(kvPair))
        {
            this.headElements.add(kvPair);
        }
    }

    public List<KeyValue<String, Element>> getHeadElements()
    {
        List<KeyValue<String, Element>> headElems = null;
        
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