/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.profiler;

import java.security.Principal;
import java.util.Collection;

import org.apache.jetspeed.om.desktop.Desktop;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
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
public interface Profiler 
{
    /**
     *  Get the Profile object using the request parameters.
     *
     * @param context The request context
     * @return a new Profile Locator object or null if failed to find a appropriate locator.
     */
    ProfileLocator getProfile(RequestContext context)
        throws ProfilerException;

    /**
     *  Get the Profile object using the request parameters and the rule.
     *
     * @param context The request context
     * @return a new Profile Locator object or null if failed to find a appropriate locator.
     */        
    ProfileLocator getProfile(RequestContext context, ProfilingRule rule)
        throws ProfilerException;        
    
    /**
     * @param locator
     * @return
     */
    Desktop getDesktop(ProfileLocator locator);
    
    /**
     * 
     * <p>
     * getPage
     * </p>
     *
     * @param locator
     * @return
     * @throws PageNotFoundException
     * @throws NodeException
     */
    Page getPage(ProfileLocator locator) throws PageNotFoundException, NodeException;
    
    /**
     * @param locator
     * @return
     */
    Fragment getFragment(ProfileLocator locator);
      
    /**
      * Creates a new ProfileLocator object that can be managed by
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

    /**
     * Given a rule id, get the rule
     * 
     * @param id
     * @return the rule
     */
    ProfilingRule getRule(String id);
    
    /**
     * @return
     */
    String getAnonymousUser();
	
}
