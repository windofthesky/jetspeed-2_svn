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
package org.apache.jetspeed.cps.template;

/**
 * TemplateLocator
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface TemplateLocator
{
    public final static String PARAM_TYPE               = "type";    
    public final static String PARAM_MEDIA_TYPE         = "media-type";
    public final static String PARAM_NAME               = "name";
    public final static String PARAM_LANGUAGE           = "language";
    public final static String PARAM_COUNTRY            = "country";
    
    public final static String TYPE_EMAIL               = "email";
    public final static String TYPE_PORTLET             = "portlet";
    
  
    /*
     * Gets the unique template locator string, which is a combination of the params
     * The string must follow the one the sequences below, where bracketed items are optional:
     *
     *   template/<templateType>/[media-type/<mediaType>]/[language/<language>]/[country/<country>]]/name/<templateName
     *
     * @return The template locator as a string
     */
    String toString();

    /*
     * Gets the path locator string, which is a combination of the params without the name tags.
     * The string must follow the one the sequences below, where bracketed items are optional:
     *
     *   <templateType>/[<mediaType>]/[<language>]/[<country>]]/<templateName>
     *
     * @return The template locator path
     */
    String toPath();
    
    /*
     * Gets the template type parameter for this template.
     * Any value is valid if there the service supports it.
     * Known values are email, portlet.
     *
     * @return The template type parameter for this template.
     */
    String getType();

    /*
     * Sets the template type parameter for this template.
     * Any value is valid if there the service supports it.
     * Known values are email, portlet.
     *
     * @param The template type parameter for this template.
     */
    void setType(String type);

    /*
     * Gets the resource name parameter for this template.
     *
     * @return The resource name parameter for this template.
     */
    String getName();

    /*
     * Sets the resource name parameter for this template.
     *
     * @param name The resource name parameter for this template.
     */
    void setName(String name);

    /*
     * Gets the media type parameter for this template.
     * Media types are values such as html, wml, xml ...
     * TODO: support for 2 or more media types
     * 
     * @return The media type parameter for this template.
     */
     String getMediaType();

    /*
     * Sets the media type parameter for this template.
     * Media types are values such as html, wml, xml ...
     * TODO: support for 2 or more media types
     *
     * @param mediaType The media type parameter for this template.
     */
     void setMediaType(String mediaType);

    /*
     * Gets the language parameter for this template.
     * Language values are ISO-639 standard language abbreviations
     * en, fr, de, ...
     *
     * @return The language parameter for this template.
     */
     String getLanguage();

    /*
     * Sets the language parameter for this template.
     * Language values are ISO-639 standard language abbreviations
     * en, fr, de, ...
     *
     * @param language The language parameter for this template.
     */
     void setLanguage(String language);

    /*
     * Gets the country code parameter for this template.
     * Country code values are ISO-3166 standard country code abbreviations.
     * GB, US, FR, CA, DE, ...
     *
     * @return The country code parameter for this template.
     */
     String getCountry();

    /*
     * Sets the country code parameter for this template.
     * Country code values are ISO-3166 standard country code abbreviations.
     * GB, US, FR, CA, DE, ...
     *
     * @param country The country code parameter for this template.
     */
     void setCountry(String country);

 
    /**
     * @see Object#clone
     * @return an instance copy of this object
     */
     Object clone() throws java.lang.CloneNotSupportedException;
    
}
