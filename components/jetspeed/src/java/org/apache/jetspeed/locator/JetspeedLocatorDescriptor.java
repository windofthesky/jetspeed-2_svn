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
package org.apache.jetspeed.locator;

/**
 * Jetspeed default Locator Descriptor implementation
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedLocatorDescriptor implements LocatorDescriptor
{
    public JetspeedLocatorDescriptor()
    {
    }
            
    private String type;
    private String name;
    private String mediaType;
    private String language;
    private String country;   
    private static final String DELIM = "/";
        

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {   
        StringBuffer value = new StringBuffer();

        // type
        if (type != null)
        {
            value.append(LocatorDescriptor.PARAM_TYPE).append(DELIM);            
            value.append(type).append(DELIM);
        }
        
        // media type
        if (mediaType != null)
        {
            value.append(LocatorDescriptor.PARAM_MEDIA_TYPE).append(DELIM);
            value.append(mediaType).append(DELIM);
        }

        // language
        if (language != null)
        {
            value.append(LocatorDescriptor.PARAM_LANGUAGE).append(DELIM);
            value.append(language).append(DELIM);
        }
        
        // country
        if (country != null)
        {
            value.append(LocatorDescriptor.PARAM_COUNTRY).append(DELIM);
            value.append(country).append(DELIM);
        }
        
        // template name
        if (name != null)
        {
            value.append(LocatorDescriptor.PARAM_NAME).append(DELIM);                    
            value.append(name).append(DELIM);
        }
        
        value.deleteCharAt(value.length()-1);
        return value.toString();
         
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#toPath()
     */
    public String toPath()
    {
        StringBuffer value = new StringBuffer("/");

        // type
        if (type != null)
        {
            value.append(type).append(DELIM);
        }
        
        // media type
        if (mediaType != null)
        {
            value.append(mediaType).append(DELIM);
        }

        // language
        if (language != null)
        {
            value.append(language).append(DELIM);
        }
        
        // country
        if (country != null)
        {
            value.append(country).append(DELIM);
        }
        
        // template name
        if (name != null)
        {
            value.append(name).append(DELIM);
        }
        
        value.deleteCharAt(value.length()-1);
        return value.toString();
        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getType()
     */
    public String getType()
    {
        return type;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setType(java.lang.String)
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getName()
     */
    public String getName()
    {
        return name;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getMediaType()
     */
    public String getMediaType()
    {
        return mediaType;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setMediaType(java.lang.String)
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getLanguage()
     */
    public String getLanguage()
    {
        return language;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setLanguage(java.lang.String)
     */
    public void setLanguage(String language)
    {
        this.language = language;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#getCountry()
     */
    public String getCountry()
    {
        return country;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocator#setCountry(java.lang.String)
     */
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    /**
     * @see Object#clone
     * @return an instance copy of this object
     */
    public Object clone() throws java.lang.CloneNotSupportedException
    {
        return super.clone();
    }
    
}
