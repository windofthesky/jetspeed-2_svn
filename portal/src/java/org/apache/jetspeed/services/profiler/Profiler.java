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
package org.apache.jetspeed.services.profiler;

import java.util.Iterator;

import org.apache.jetspeed.services.JetspeedServices;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.request.RequestContext;

import org.apache.jetspeed.services.profiler.ProfilerService;

public class Profiler
{
    public final static String PARAM_MEDIA_TYPE         = "media-type";
    public final static String PARAM_ROLE               = "role";
    public final static String PARAM_GROUP              = "group";
    public final static String PARAM_PAGE               = "page";
    public final static String PARAM_USER               = "user";
    public final static String PARAM_ANON               = "anon";
    public final static String PARAM_LANGUAGE           = "language";
    public final static String PARAM_COUNTRY            = "country";
    public final static String DEFAULT_PROFILE          = "default";
    public final static String FULL_DEFAULT_PROFILE          = "default.psml";
    public final static String DEFAULT_EXTENSION        = ".psml";


    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    public static ProfilerService getService()
    {
        return (ProfilerService)JetspeedServices
            .getInstance().getService(ProfilerService.SERVICE_NAME);
    }

    /**
     *  get the Profile object using a profile locator
     *
     * @param locator The locator containing criteria describing the profile.
     * @return a new Profile object
     */
    public static Profile getProfile(ProfileLocator locator)
        throws ProfileException
    {
        return getService().getProfile( locator );
    }

    /**
     *  Get the Profile object using the request parameters.
     *
     * @param context The request context
     * @return a new Profile object
     */
    public static Profile getProfile(RequestContext context)
        throws ProfileException
    {
        return getService().getProfile(context);
    }

    /**
     * @see ProfilerService#makeDynamicURI
     TODO: write me
     public static DynamicURI makeDynamicURI( RunData data, ProfileLocator locator )
        throws ProfileException
     {
        return getService().makeDynamicURI( data, locator );
     }
*/

    /**
     * @see ProfilerService#createProfile
     */
    public static Profile createProfile()
    {
        return getService().createProfile();
    }

    /**
     * @see ProfilerService#createProfile
     */
    public static Profile createProfile(ProfileLocator locator)
    {
        return getService().createProfile(locator);
    }

    /**
     * @see ProfilerService#createProfile
     */
    public static Profile createProfile(ProfileLocator locator, Portlets portlets)
            throws ProfileException
    {
        return getService().createProfile(locator, portlets);
    }

    /**
     * @see ProfilerService#createProfile
     */
    public static ProfileLocator createLocator()
    {
        return getService().createLocator();
    }

    /**
     * @see ProfilerService#removeProfile
     */
    public static void removeProfile(ProfileLocator locator)
        throws ProfileException
    {
        getService().removeProfile( locator );
    }

    /**
     * @see ProfilerService#query
     */
    public static Iterator query( QueryLocator locator )
    {
        return getService().query( locator );
    }

    /**
     * @see ProfilerService#useRoleProfileMerging
     */
    public static boolean useRoleProfileMerging()
    {
        return getService().useRoleProfileMerging();
    }


}