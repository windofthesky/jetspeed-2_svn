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

package org.apache.jetspeed.util;


/**
 *
 * <p>Utility class for declaring MIME types to use for various requests and provide
 * utility manipulation methods.</p>
 * <p>Added Content-Encoding capability, with defaults
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @version $Id$
 */
public class MimeType
{
    
    public static final MimeType HTML  = new MimeType("text/html", "UTF-8"); //FIXME: test
    public static final MimeType XHTML = new MimeType("text/xhtml");
    public static final MimeType WML   = new MimeType("text/vnd.wap.wml");
    public static final MimeType XML   = new MimeType("text/xml");
    public static final MimeType VXML  = new MimeType("text/vxml");
    
    /**
     * Standard ContentType String, with no encoding appended.
     */
    private String mimeType = "";
    /**
     * null value means default encoding.
     * Otherwise, charset to be used.
     */
    private String charSet = null;
    
    public MimeType(String mimeType)
    {
        if (mimeType == null)
        {
            throw new NullPointerException();
        }
        this.mimeType = mimeType;
    }
    
    /**
     *
     */
    public MimeType(String mimeType, String charSet)
    {
        if (mimeType == null)
        {
            throw new NullPointerException();
        }
        this.mimeType = mimeType;
        this.charSet = charSet;
    }
    
    /** Extracts from this MimeType a user-friendly identifying code
     * ie "html" for "text/html" or "wml" for "text/vnd.wap.wml"
     *
     * @return the simplified type
     */
    public String getCode()
    {
        String type = this.mimeType;
        // get everything after "/"
        type = type.substring(type.indexOf("/") + 1);
        // remove any dot in the name
        int idx = type.lastIndexOf(".");
        if (idx >= 0)
        {
            type = type.substring(idx + 1);
        }
        //remove anything before a "-"
        idx = type.lastIndexOf("-");
        if (idx >= 0)
        {
            type = type.substring(idx + 1);
        }
        
        return type.toLowerCase();
    }
    
    /**
     * Return the media type associated
     */
    public String getContentType()
    {
        return this.mimeType;
    }
    
    /**
     * Return the character encoding associated, if any
     */
    public String getCharSet()
    {
        return this.charSet;
    }
    
    /**
     * Convert this MimeType to its external String representation
     */
    public String toString()
    {
        if (null == this.charSet)
        {
            return this.mimeType;
        }
        return this.mimeType + "; charset=" + this.charSet;
    }
    
    /**
     * Compare one MimeType to another
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (obj instanceof MimeType)
        {
            MimeType comp = (MimeType) obj;
            return this.toString().equals(comp.toString());
        }
        else
        {
            return false;
        }
    }
    
}
