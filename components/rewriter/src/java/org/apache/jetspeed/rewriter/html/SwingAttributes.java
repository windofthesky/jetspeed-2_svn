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
package org.apache.jetspeed.rewriter.html;

import java.util.Enumeration;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;

import org.apache.jetspeed.rewriter.MutableAttributes;


/**
 * SwingAttributes
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SwingAttributes implements MutableAttributes
{
    MutableAttributeSet swingset;
    
    public SwingAttributes(MutableAttributeSet swingset)
    {
        this.swingset = swingset;
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getLength()
     */
    public int getLength()
    {
        return swingset.getAttributeCount();
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getURI(int)
     */
    public String getURI(int index)
    {
        return "";
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getLocalName(int)
     */
    public String getLocalName(int index)
    {
        Enumeration e = swingset.getAttributeNames();
        int ix = 0;
        while (e.hasMoreElements())
        {
            Object object = e.nextElement();
            if (ix == index)
            {
                return object.toString();
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getQName(int)
     */
    public String getQName(int index)
    {
        return getLocalName(index);
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getType(int)
     */
    public String getType(int index)
    {
        return "CDATA";
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getValue(int)
     */
    public String getValue(int index)
    {
        Enumeration e = swingset.getAttributeNames();
        int ix = 0;
        while (e.hasMoreElements())
        {
            Object object = e.nextElement();
            if (ix == index)
            {
                return (String)swingset.getAttribute(object);
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getIndex(java.lang.String, java.lang.String)
     */
    public int getIndex(String uri, String localPart)
    {
        return getIndex(localPart);
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getIndex(java.lang.String)
     */
    public int getIndex(String qName)
    {
        Enumeration e = swingset.getAttributeNames();
        int ix = 0;
        while (e.hasMoreElements())
        {
            String name = (String)e.nextElement();
            if (name.equalsIgnoreCase(qName))
            {
                return ix;
            }
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getType(java.lang.String, java.lang.String)
     */
    public String getType(String uri, String localName)
    {
        return "CDATA";
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getType(java.lang.String)
     */
    public String getType(String qName)
    {
        return "CDATA";
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getValue(java.lang.String, java.lang.String)
     */
    public String getValue(String uri, String localName)
    {
        return getValue(localName);
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.Attributes#getValue(java.lang.String)
     */
    public String getValue(String qName)
    {
        Attribute att = HTML.getAttributeKey(qName.toLowerCase());        
        return (String)swingset.getAttribute(att);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.MutableAttributes#addAttribute(java.lang.String, java.lang.Object)
     */
    public void addAttribute(String name, Object value)
    {
        Attribute att = HTML.getAttributeKey(name.toLowerCase());
        swingset.addAttribute(att, value);
    }

}
