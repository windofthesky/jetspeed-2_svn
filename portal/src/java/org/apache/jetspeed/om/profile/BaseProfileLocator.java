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
package org.apache.jetspeed.om.profile;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.services.profiler.Profiler;

/**
 * Interface definition for a Profile Locator.
 * Locators are used by the profiler to describe the parameters used to locate
 * a resource in the persistent configuration store.
 *
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:adambalk@cisco.com">Atul Dambalkar</a>
 * @version $Id$
*/

public class BaseProfileLocator implements ProfileLocator
{
    // instance state
    private String name = null;
    private String mediaType = null;
    private String language = null;
    private String country = null;
    private String user = null;
    private String   role = null;
    private String  group = null;
    private boolean anonymous = false;

    private static final String DELIM = "/";

    private final static Log log = LogFactory.getLog(BaseProfileLocator.class);

    /*
     * Gets the unique profile locator id, which is a combination of the params
     * This ID must follow the one of the 4 sequences below:
     *
     *   <username>/<mediaType>/<language>/<country>/<page>
     *   <group>/<mediaType>/<language>/<country>/<page>
     *   <role>/<mediaType>/<language>/<country>/<page>
     *
     *
     * @return The profile locator id
     */

    public String getId()
    {
        StringBuffer id = new StringBuffer(128);

        if (!anonymous && user != null)
        {
            id.append(Profiler.PARAM_USER).append(DELIM);
            id.append(user);
        }
        else if (group != null)
        {
            id.append(Profiler.PARAM_GROUP).append(DELIM);
            id.append(group);
        }
        else if (role != null)
        {
            id.append(Profiler.PARAM_ROLE).append(DELIM);
            id.append(role);
        }
        else
        {
            id.append(Profiler.PARAM_ANON);
        }
        if (language != null)
        {
            id.append(DELIM);
            id.append(language);
        }
        if (country != null)
        {
            id.append(DELIM);
            id.append(country);
        }
        if (mediaType != null)
        {
            id.append(DELIM);
            id.append(mediaType);
        }
        if (name != null)
        {
            id.append(DELIM);
            id.append(name);
        }

        return id.toString();
    }


    /*
     * Gets the unique profile locator path, which is a combination of the name
     * value pairs. This ID must follow the one of the 4 sequences below:
     *
     *   user/<name>/media-type/<mediaType>/language/<language>
     *               /country/<country>/<page>/page
     *
     *   group/ ""
     *   role/  ""
     *
     *
     * @return The profile locator path
     */

    public String getPath()
    {
        StringBuffer id = new StringBuffer(128);

        if (!anonymous && user != null)
        {
            id.append(Profiler.PARAM_USER).append(DELIM);
            id.append(user).append(DELIM);
        }
        else if (group != null)
        {
            id.append(Profiler.PARAM_GROUP).append(DELIM);
            id.append(group).append(DELIM);
        }
        else if (role != null)
        {
            id.append(Profiler.PARAM_ROLE).append(DELIM);
            id.append(role).append(DELIM);
        }
        else
        {
            id.append(Profiler.PARAM_USER).append(DELIM);
            id.append(Profiler.PARAM_ANON).append(DELIM);
        }

        if (language != null)
        {
            id.append(Profiler.PARAM_LANGUAGE).append(DELIM);
            id.append(language).append(DELIM);
        }
        if (country != null)
        {
            id.append(Profiler.PARAM_COUNTRY).append(DELIM);
            id.append(country).append(DELIM);
        }
        if (mediaType != null)
        {
            id.append(Profiler.PARAM_MEDIA_TYPE).append(DELIM);
            id.append(mediaType).append(DELIM);
        }
        if (name != null)
        {
            id.append(Profiler.PARAM_PAGE).append(DELIM);
            id.append(name).append(DELIM);
        }
        id.deleteCharAt(id.length()-1);
        return id.toString();
    }


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
    public void createFromPath(String path)
    {
        StringTokenizer tok = new StringTokenizer(path, "/");
        while (tok.hasMoreTokens())
        {
            String name = (String)tok.nextToken();
            if (name.equals(Profiler.PARAM_USER) && tok.hasMoreTokens())
            {
                try
                {
                    this.setUser( tok.nextToken() );
                }
                catch (Exception e)
                {
                    log.error("ProfileLocator: Failed to set User: " + e);
                }
            }
            else if (name.equals(Profiler.PARAM_GROUP) && tok.hasMoreTokens())
            {
                try
                {
                    this.setGroup( tok.nextToken() );
                }
                catch (Exception e)
                {
                    log.error("ProfileLocator: Failed to set Group: " + e);
                }
            }
            else if (name.equals(Profiler.PARAM_ROLE) && tok.hasMoreTokens())
            {
                try
                {
                    this.setRole( tok.nextToken() );
                }
                catch (Exception e)
                {
                    log.error("ProfileLocator: Failed to set Role: " + e);
                }
            }
            else if (name.equals(Profiler.PARAM_PAGE) && tok.hasMoreTokens())
            {
                this.setName(tok.nextToken());
            }
            else if (name.equals(Profiler.PARAM_MEDIA_TYPE) && tok.hasMoreTokens())
            {
                this.setMediaType(tok.nextToken());
            }
            else if (name.equals(Profiler.PARAM_LANGUAGE) && tok.hasMoreTokens())
            {
                this.setLanguage(tok.nextToken());
            }
            else if (name.equals(Profiler.PARAM_COUNTRY) && tok.hasMoreTokens())
            {
                this.setCountry(tok.nextToken());
            }

        }
    }

    /**
     * @see Object#clone
     * @return an instance copy of this object
     */
    public Object clone() throws java.lang.CloneNotSupportedException
    {
        return super.clone();
    }

    /*
     * Gets the resource name parameter for this profile.
     *
     * @return The resource name parameter for this profile.
     */
    public String getName()
    {
        return name;
    }

    /*
     * Sets the resource name parameter for this profile.
     *
     * @param The resource name parameter for this profile.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /*
     * Gets the anonymous user flag for this profile.
     *
     * @param The user parameter for this profile.
     */
    public boolean getAnonymous()
    {
        return this.anonymous;
    }


    /*
     * Sets the user parameter as the anonymous user
     *
     * @param anonymous True indicates this is an anonymous user.
     */
    public void setAnonymous(boolean anonymous)
    {
        try
        {
            // JetspeedUser user = JetspeedUserFactory.getInstance();
            // user.setUserName(JetspeedSecurity.getAnonymousUserName());
            // this.setUser(user);
        }
        catch (Exception e)
        {
            log.error("Could not get Anonymous user");
        }
        finally
        {
            this.anonymous = anonymous;
        }
    }

    /*
     * Gets the media type parameter for this profile.
     * Media types are values such as html, wml, xml ...
     *
     * @return The media type parameter for this profile.
     */
    public String getMediaType()
    {
        return mediaType;
    }

    /*
     * Sets the media type parameter for this profile.
     * Media types are values such as html, wml, xml ...
     *
     * @param The media type parameter for this profile.
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    /*
     * Gets the language parameter for this profile.
     * Language values are ISO-639 standard language abbreviations
     * en, fr, de, ...
     *
     * @return The language parameter for this profile.
     */
    public String getLanguage()
    {
        return language;
    }

    /*
     * Sets the language parameter for this profile.
     * Language values are ISO-639 standard language abbreviations
     * en, fr, de, ...
     *
     * @param The language parameter for this profile.
     */
    public void setLanguage(String language)
    {
        this.language = language;
    }

    /*
     * Gets the country code parameter for this profile.
     * Country code values are ISO-3166 standard country code abbreviations.
     * GB, US, FR, CA, DE, ...
     *
     * @return The country code parameter for this profile.
     */
    public String getCountry()
    {
        return country;
    }

    /*
     * Sets the country code parameter for this profile.
     * Country code values are ISO-3166 standard country code abbreviations.
     * GB, US, FR, CA, DE, ...
     *
     * @param The country code parameter for this profile.
     */
    public void setCountry(String country)
    {
        this.country = country;
    }

    /*
     * Gets the user parameter for this profile.
     *
     * @return The user parameter for this profile.
     */
    public String getUser()
    {
        return user;
    }

    /*
     * Sets the user parameter for this profile.
     *
     * @param The user parameter for this profile.
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /*
     * Gets the role parameter for this profile.
     *
     * @return The role parameter for this profile.
     */
    public String getRole()
    {
        return role;
    }

    /*
     * Sets the role parameter for this profile.
     *
     * @param The role parameter for this profile.
     */
    public void setRole( String role )
    {
        this.role = role;
    }

    /*
     * Gets the group parameter for this profile.
     *
     * @return The group parameter for this profile.
     */
    public String getGroup()
    {
        return group;
    }

    /*
     * Sets the group parameter for this profile.
     *
     * @param The group parameter for this profile.
     */
    public void setGroup( String group )
    {
        this.group = group;
    }

    /*
     * Comparision Functions. Contributed by Atul Dambalkar
     */

   /**
     * Define equality criteria for ProfileLocator objects.
     * @param obj ProfileLocator object to be compared with.
     */
    public boolean equals(Object obj)
    {
        if( obj == null )
        {
            return false;
        }
        synchronized (obj)
        {
            if ( ! ( obj instanceof ProfileLocator ) )
            {
                return false;
            }

            ProfileLocator locator = (ProfileLocator)obj;

            String name = locator.getName();
            String mediaType = locator.getMediaType();
            String language = locator.getLanguage();
            String country = locator.getCountry();
            String group = locator.getGroup();
            String role = locator.getRole();
            String user = locator.getUser();

            return stringEquals(name, this.getName())
                   && stringEquals(mediaType, this.getMediaType())
                   && stringEquals(language, this.getLanguage())
                   && stringEquals(country, this.getCountry())
                   && stringEquals(user, this.getUser())
                   && stringEquals(group, this.getGroup())
                   && stringEquals(role, this.getRole());
        }
    }

    /**
     * AssertNotNull the two String objects and then check the equality.
     */
    private boolean stringEquals(String str1, String str2)
    {
        if (exclusiveOr(str1, str2))
        {
            return false;
        }
        if (assertNotNull(str1) && assertNotNull(str2))
        {
            return str1.equals(str2);
        }
        // both are null
        return true;
    }

    /**
     * AssertNotNull the given object.
     */
    private boolean assertNotNull(Object object)
    {
        return object != null;
    }

    /**
     * Exclusive or the two objects fro their references null-ability.
     */
    private boolean exclusiveOr(Object obj1, Object obj2)
    {
        return (assertNotNull(obj1) && !assertNotNull(obj2))
                || (!assertNotNull(obj1) && assertNotNull(obj2));
    }
}
