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
import java.util.Map;

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
 *   1. page
 *   2. navigations
 *   3. document lists
 * 
 * In all cases, a fallback algorithm should be applied to fallback
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
     * @param locatorName The name of the profile locator to find i.e. "page", "docset", ...
     * @return a new Profile Locator object or null if failed to find a appropriate locator.
     */
    ProfileLocator getProfile(RequestContext context, String locatorName) throws ProfilerException;

    /**
     *  Get the Profile object using the request parameters and the rule.
     *
     * @param context The request context
     * @return a new Profile Locator object or null if failed to find a appropriate locator.
     */        
    ProfileLocator getProfile(RequestContext context, ProfilingRule rule) throws ProfilerException;        
    
    /**
      * Creates a new ProfileLocator object that can be managed by
      * the current Profiler implementation
      *
      * @param context The request context
      * @return A new ProfileLocator object
      */
    ProfileLocator createLocator(RequestContext context);
        
    /**
      * Creates a new ProfiledPageContext object that references
      * the specified locator and can be managed by the current
      * Profiler implementation
      *
      * @param locators Map of ProfileLocator objects used to select page
      * @return A new ProfiledPageContext object
      */
    ProfiledPageContext createProfiledPageContext(Map locators);
        
    /**
     * For a given principal, lookup the associated profiling rule to that principal name.
     * 
     * @param principal Lookup the profiling rule based on this principal. 
     * @param locatorName the unique name of a locator for this principal/rule/locator 
     * @return The rule found or null if not found
     */
    ProfilingRule getRuleForPrincipal(Principal principal, String locatorName);

    /**
     * For a given principal, associate a profiling rule to that principal name.
     * TODO: this API should be secured and require admin role
     * 
     * @param principal Lookup the profiling rule based on this principal.
     * @param locatorName the unique name of a locator for this principal/rule/locator 
     * @param The rule used to find profiles for this user
     */
    void setRuleForPrincipal(Principal principal, ProfilingRule rule, String locatorName);
    
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
    
    /**
     * For a given principal, find all supported locators and return a string array of 
     * locator names.
     * 
     * @param principal The given principal.
     * @return array of String locator names
     */
    String[] getLocatorNamesForPrincipal(Principal principal);

    /**
     * Gets all supported locators for a principal.
     *  
     * @param context
     * @param principal
     * @return
     * @throws ProfilerException
     */
    public Map getProfileLocators(RequestContext context, Principal principal)
    throws ProfilerException;
    
}
