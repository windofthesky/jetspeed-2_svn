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
package org.apache.jetspeed.profiler;

import java.io.Serializable;

/**
 * ProfileLocator
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface ProfileLocator extends Serializable, Cloneable
{
    /*
     * populates this profile locator from a given path in the format:
     *
     *   user/<name>/media-type/<mediaType>/language/<language>
     *               /country/<country>/<page>/page
     *
     *   group/ ""
     *   role/  ""
     *
     * @param path The formatted profiler path string.
     */
    public void createFromPath(String path);

    /*
     * Gets the unique profile locator id, which is a combination of the params
     * This ID must follow the one of the 4 sequences below:
     *
     *   <username>/<mediaType>/<language>/<country>/<page>
     *   <group>/<mediaType>/<language>/<country>/<page>
     *   <role>/<mediaType>/<language>/<country>/<page>
     *
     * @return The profile locator id
     */
    public String getId();

    /*
     * Gets the unique profile locator path, which is a combination of the params
     * This Path must follow the one of the 4 sequences below:
     *
    *   user/<name>/media-type/<mediaType>/language/<language>
    *               /country/<country>/<page>/page
    *
    *   group/ ""
    *   role/  ""
     *
     * @return The profile locator path
     */
    public String getPath();

    String getParameter(String name);
    
    String setParameter(String name, String value);
    
    /*
     * Gets the resource name parameter for this profile.
     *
     * @return The resource name parameter for this profile.
     */
    String getName();

    /*
     * Sets the resource name parameter for this profile.
     *
     * @param name The resource name parameter for this profile.
     */
    void setName(String name);

    /*
     * Gets the media type parameter for this profile.
     * Media types are values such as html, wml, xml ...
     *
     * @return The media type parameter for this profile.
     */
    public String getMediaType();

    /*
     * Sets the media type parameter for this profile.
     * Media types are values such as html, wml, xml ...
     *
     * @param mediaType The media type parameter for this profile.
     */
    public void setMediaType(String mediaType);

    /*
     * Gets the language parameter for this profile.
     * Language values are ISO-639 standard language abbreviations
     * en, fr, de, ...
     *
     * @return The language parameter for this profile.
     */
    public String getLanguage();

    /*
     * Sets the language parameter for this profile.
     * Language values are ISO-639 standard language abbreviations
     * en, fr, de, ...
     *
     * @param language The language parameter for this profile.
     */
    public void setLanguage(String language);

    /*
     * Gets the country code parameter for this profile.
     * Country code values are ISO-3166 standard country code abbreviations.
     * GB, US, FR, CA, DE, ...
     *
     * @return The country code parameter for this profile.
     */
    public String getCountry();

    /*
     * Sets the country code parameter for this profile.
     * Country code values are ISO-3166 standard country code abbreviations.
     * GB, US, FR, CA, DE, ...
     *
     * @param country The country code parameter for this profile.
     */
    public void setCountry(String country);

    /*
     * Gets the user parameter for this profile.
     *
     * @return The user parameter for this profile.
     */
    public String getUser();

    /*
     * Sets the user parameter for this profile.
     *
     * @param user The user parameter for this profile.
     */
    public void setUser(String user);

    /*
     * Gets the anonymous user flag for this profile.
     *
     * @return True if this profile is anonymous.
     */
    public boolean getAnonymous();

    /*
     * Sets the user parameter as the anonymous user
     *
     * @param anonymous True indicates this is an anonymous user.
     */
    public void setAnonymous(boolean anonymous);

    /*
     * Gets the role parameter for this profile.
     *
     * @return The role parameter for this profile.
     */
    public String getRole();

    /*
     * Sets the role parameter for this profile.
     *
     * @param role The role parameter for this profile.
     */
    public void setRole( String role );

    /*
     * Gets the group parameter for this profile.
     *
     * @return The group parameter for this profile.
     */
    public String getGroup();

    /*
     * Sets the group parameter for this profile.
     *
     * @param group The group parameter for this profile.
     */
    public void setGroup( String group );

 
    /**
     * @see Object#clone
     * @return an instance copy of this object
     */
    public Object clone() throws java.lang.CloneNotSupportedException;
    
}
