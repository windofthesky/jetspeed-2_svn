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

import java.security.Principal;
import java.util.Collection;

import org.apache.jetspeed.cps.CommonService;
import org.apache.jetspeed.om.desktop.Desktop;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.request.RequestContext;

/**
 * ProfilerService
 * Jetspeed-2 Profiler service. 
 * Locates portal resources given a set of request parameters, properties, and attributes
 * The Profiler is invoked during the request processing pipeline.
 * It requires that the request context is already populated with the portal request and response,
 * and capability and user information. The request context parameters, properties and attributes
 * make up the profile criterion which the profiler uses to locate portal resources:
 *   1. desktop
 *   2. page
 *   3. fragment
 * 
 * In all cases (desktop, page, fragment), a fallback algorithm should be applied to fallback
 * to default portal resources.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface ProfilerService extends CommonService
{
    /** The name of this service */
    public String SERVICE_NAME = "profiler";
   
    /**
     *  Get the Profile object using the request parameters.
     *
     * @param context The request context
     * @return a new Profile Locator object or null if failed to find a appropriate locator.
     */
    ProfileLocator getProfile(RequestContext context)
        throws ProfilerException;
    
    /**
     * @param locator
     * @return
     */
    Desktop getDesktop(ProfileLocator locator);
    
    /**
     * @param locator
     * @return
     */
    Page getPage(ProfileLocator locator);
    
    /**
     * @param locator
     * @return
     */
    Fragment getFragment(ProfileLocator locator);
      
    /**
      * Creates a new ProfileLocator object that can be successfully managed by
      * the current Profiler implementation
      *
      * @return A new ProfileLocator object
      */
    ProfileLocator createLocator();
    
    /**
     * For a given principal, lookup the associated profiling rule to that principal name.
     * 
     * @param principal Lookup the profiling rule based on this principal. 
     * @return The rule found or null if not found
     */
    ProfilingRule getRuleForPrincipal(Principal principal);
      
    /**
     * Lookup the portal's default profiling rule.
     * 
     * @return The portal's default profiling rule.
     */
    ProfilingRule getDefaultRule();
              
    /**
     * @return
     */
    Collection getRules();
                  
}
