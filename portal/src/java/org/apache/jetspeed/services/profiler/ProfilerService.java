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

import org.apache.jetspeed.cps.CommonService;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.request.RequestContext;

/**
 * <P>This interface is a facade for all profile related operations</P>
 *
 * @see org.apache.jetspeed.om.profile.Profile
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id$
 */

public interface ProfilerService extends CommonService
{

    /** The name of this service */
    public String SERVICE_NAME = "Profiler";

    /**
     *  Get the Profile object using a profile locator.
     *
     * @param locator The locator containing criteria describing the profile.
     * @return a new Profile object
     */
    public Profile getProfile(ProfileLocator locator)
        throws ProfileException;

    /**
     *  Get the Profile object using the request parameters.
     *
     * @param context The request context
     * @return a new Profile object
     */
    public Profile getProfile(RequestContext context)
        throws ProfileException;

    /**
     * Creates a dynamic URI
     *
     * @param rundata the rundata object for the current request
     * @param locator The description of the profile.
     * @return A new dynamic URI representing all profile parameters from the locator.
     */
//    public DynamicURI makeDynamicURI( RunData data, ProfileLocator locator )
  //      throws ProfileException;

    /**
     * Creates a new Profile object that can be successfully managed by
     * the current Profiler implementation
     *
     * @return A new Profile object
     */
    public Profile createProfile();

    /**
     * Creates a new Profile object for a specific locator.
     *
     * @param locator The description of the profile.
     * @return A new Profile object
     */
    public Profile createProfile(ProfileLocator locator);

    /**
     * Create a new profile given a profile locator
     *
     * @param locator The description of the new profile to be created.
     * @param portlets The PSML tree
     */
    public Profile createProfile(ProfileLocator locator, Portlets portlets)
            throws ProfileException;

    /**
     * Creates a new ProfileLocator object that can be successfully managed by
     * the current Profiler implementation
     *
     * @return A new ProfileLocator object
     */
    public ProfileLocator createLocator();

   /**
     *  Removes a profile.
     *
     * @param locator The profile locator criteria.
     */
    public void removeProfile( ProfileLocator locator )
        throws ProfileException;

    /** Query for a collection of profiles given a profile locator criteria.
     *
     * @param locator The profile locator criteria.
     * @return The list of profiles matching the locator criteria.
     */
    public Iterator query( QueryLocator locator );

    /**
     * Returns status of role profile merging feature
     *
     * @return True if role profile merging is active
     */
    public boolean useRoleProfileMerging();

}