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
package org.apache.jetspeed.locator;

/**
 * LocatorDescriptor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface LocatorDescriptor
{
    public final static String PARAM_TYPE               = "type";    
    public final static String PARAM_MEDIA_TYPE         = "media-type";
    public final static String PARAM_NAME               = "name";
    public final static String PARAM_LANGUAGE           = "language";
    public final static String PARAM_COUNTRY            = "country";    
    public final static String TYPE_EMAIL               = "emails";
    public final static String TYPE_PORTLET             = "portlets";
    
  
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
