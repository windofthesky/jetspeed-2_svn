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
