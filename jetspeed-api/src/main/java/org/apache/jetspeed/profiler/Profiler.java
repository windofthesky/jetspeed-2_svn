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
package org.apache.jetspeed.profiler;

import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.request.RequestContext;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

/**
 * The Jetspeed Profiler is a portal resource location rule-based engine. The profiler locates the following kinds of portal resources:
 * <ul>
 *    <li>PSML pages</li>
 *    <li>Folders</li>
 *    <li>Menus</li>
 *    <li>Links</li>
 * </ul>
 * When a request is received by the portal, the profiler will compute a normalized instruction set, known as a
 * profile locator {@link ProfileLocator}.
 * The locator is then added to the request context {@link RequestContext}, from which subsequent components
 * on the Jetspeed pipeline, most notably the Page Manager {@link org.apache.jetspeed.page.PageManager} and
 * Portal Site {@link org.apache.jetspeed.portalsite.PortalSite} components, can take the profile locator
 * and use it to find a requested  resource. For example, the Page Manager uses the locator to find a page or folder.
 * The Portal Site component uses the locator build the options on a menu. The profile locator is
 * the output from the profiler. The input is a normalized set of runtime parameters and state. The profiler input is
 * defined in profiling rules {@link ProfilingRule}, and can be made of any Java class
 * available on the pipeline. Jetspeed comes with quite a few predefined rules for taking
 * criteria {@link RuleCriterion} {@link org.apache.jetspeed.profiler.rules.RuleCriterionResolver} from request parameters,
 * HTTP headers, security information, language and session attributes.
 * The profiler is invoked during the request processing pipeline {@link org.apache.jetspeed.pipeline.Pipeline}
 * in the profiler valve {@link org.apache.jetspeed.pipeline.valve.Valve}.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Profiler
{
    /**
     *  Retrieve a profile locator {@link ProfileLocator} for the given runtime parameters represented in the request
     *  context and for the locatorName. Commonly used locator names are:
     *  <ul>
     *      <li>To locate a page - {@link ProfileLocator#PAGE_LOCATOR}</li>
     *      <li>To locate a security redirect page - {@link ProfileLocator#SECURITY_LOCATOR}</li>
     *  </ul>
     *
     * The algorithm for this method looks up a {@link ProfilingRule} for
     * the current user on the request context and for the locator name. That rule is then applied to return a
     * normalized profile. The profile locator holds the normalized set of rules that are used further in the
     * pipeline to locate the resource to be retrieved and rendered.
     * @see ProfilingRule#apply(org.apache.jetspeed.request.RequestContext, Profiler)
     *
     * @param context the request context holding runtime request parameters to be normalized
     * @param locatorName the commonly known name of the profile locator such as {@link ProfileLocator#PAGE_LOCATOR}
     * @return a new ProfileLocator object or null if failed to find a appropriate locator.
     * @throws ProfilerException
     */
    ProfileLocator getProfile(RequestContext context, String locatorName) throws ProfilerException;

    /**
     *  Retrieve the default profile locator {@link ProfileLocator} for the given runtime parameters represented in the request
     *  context and for the locatorName. Differs from {@link #getProfile} in that instead of using the user principal
     *  found within the request context's subject, a default, global principal is used.
     *
     *  Commonly used locator names are:
     *  <ul>
     *      <li>To locate a page - {@link ProfileLocator#PAGE_LOCATOR}</li>
     *      <li>To locate a security redirect page - {@link ProfileLocator#SECURITY_LOCATOR}</li>
     *  </ul>
     *
     * The algorithm for this method looks up a {@link ProfilingRule} for
     * the default principal and for the locator name. That rule is then applied to return a
     * normalized profile. The profile locator holds the normalized set of rules that are used further in the
     * pipeline to locate the resource to be retrieved and rendered.
     * @see ProfilingRule#apply(org.apache.jetspeed.request.RequestContext, Profiler)
     *
     * @param context the request context holding runtime request parameters to be normalized
     * @param locatorName the commonly known name of the profile locator such as {@link ProfileLocator#PAGE_LOCATOR}
     * @return a new  object or null if failed to find a appropriate locator.
     * @throws ProfilerException
     */
    ProfileLocator getDefaultProfile(RequestContext context, String locatorName) throws ProfilerException;

    /**
     *  Retrieve a profile locator {@link ProfileLocator} for the given runtime parameters represented in the request
     *  context and for the provided {@link ProfilingRule}.
     * <p>
     * The algorithm for this method takes the {@link ProfilingRule} directly and applies that rule
     * to return a normalized profile. The profile locator holds the normalized set of rules that are used further in the
     * pipeline to locate the resource to be retrieved and rendered.
     * @see ProfilingRule#apply(org.apache.jetspeed.request.RequestContext, Profiler)
     *
     * @param context The request context  holding runtime request parameters to be normalized
     * @param rule The ProfilingRule to apply and find a {@link ProfileLocator}
     * @return a new ProfileLocator object or null if failed to find a appropriate locator.
     * @throws ProfilerException
     */
    ProfileLocator getProfile(RequestContext context, ProfilingRule rule) throws ProfilerException;        
    
    /**
      * Creates a new ProfileLocator object that can be managed by
      * the current Profiler implementation
      *
      * @param context The request context containing runtime parameters to determine which locator to create
      * @return A new ProfileLocator object
      */
    ProfileLocator createLocator(RequestContext context);
        
    /**
     * For a given principal, lookup the associated profiling rule to that principal name.
     * 
     * @param principal Lookup the profiling rule based on this principal
     * @param locatorName the unique name of a locator for this principal/rule/locator 
     * @return The rule found or null if not found
     */
    ProfilingRule getRuleForPrincipal(Principal principal, String locatorName);

    /**
     * For a given principal, associate a profiling rule to that principal name.
     *
     * @param principal
     *            Lookup the profiling rule based on this principal.
     * @param rule
     *            rule used to find profiles for this user
     * @param locatorName
     *            the unique name of a locator for this principal/rule/locator
     */
    void setRuleForPrincipal(Principal principal, ProfilingRule rule, String locatorName);

    /**
     * Lookup the portal's default profiling rule.
     * 
     * @return The portal's default profiling rule.
     */
    ProfilingRule getDefaultRule();

    /**
     * Retrieves all profiling rules
     *
     * @return a collection of all rules
     */
    Collection<ProfilingRule> getRules();

    /**
     * Given a rule id, get the rule
     * 
     * @param id
     * @return the rule associated the given id
     */
    ProfilingRule getRule(String id);

    /**
     * For a given principal, find all supported locators and return a string
     * array of locator names.
     * 
     * @param principal
     *            The given principal.
     * @return array of String locator names
     */
    String[] getLocatorNamesForPrincipal(Principal principal);

    /**
     * For a given principal, find all supported locators and return a
     * collection of principal rules.
     * 
     * @param principal The given principal such as a user principal
     * @return collection of PrincipalRules
     */
    Collection<PrincipalRule> getRulesForPrincipal(Principal principal);

    /**
     * Retrieves a map of all supported locators for a principal, mapping locator name to profile locator
     * 
     * @param context the request context  holding runtime request parameters to be normalized
     * @param principal the given principal such a User Principal
     * @return a map of locator names mapping to profile locators
     * @throws ProfilerException
     */
    Map<String,ProfileLocator> getProfileLocators(RequestContext context, Principal principal)
            throws ProfilerException;

    /**
     * Retrieves a map of all default locators, mapping locator name to profile locator
     *
     * @param context the request context  holding runtime request parameters to be normalized
     * @return a map of locator names mapping to profile locators
     * @throws ProfilerException
     */
    Map<String,ProfileLocator> getDefaultProfileLocators(RequestContext context)
            throws ProfilerException;

    /*
     * Persist a profiling rule to the persistent store.
     *
     * @param rule the profiling rule to be persisted
     * @throws ProfilerException
     */
    void storeProfilingRule(ProfilingRule rule) throws ProfilerException;

    /*
     * Deletes a profiling rule from the persistent store.
     *
     * @param rule the profiling rule to be deleted
     * @throws ProfilerException
     */
    void deleteProfilingRule(ProfilingRule rule) throws ProfilerException;

    /*
     * Persist a principal rule to the persistent store.
     *
     * @param rule the principal rule to be deleted
     * @throws ProfilerException
     */
    void storePrincipalRule(PrincipalRule rule) throws ProfilerException;

    /*
     * Deletes a principal rule from the persistent store.
     *
     * @param rule the principal rule to be deleted
     * @throws ProfilerException
     */
    void deletePrincipalRule(PrincipalRule rule) throws ProfilerException;

    /**
     * Factory for creating Profiling Rules. The boolean argument specifies whether to
     * obtain a new instance of a standard profiling rule or of a fallback rule.
     * 
     * @param standard
     *            true if standard rule is requested, false if fallback
     * @return New instance of a (standard or fallback) Profiling Rule
     * @throws ClassNotFoundException
     *             if the bean factory couldn't instantiate the bean
     */
    public ProfilingRule createProfilingRule(boolean standard)
            throws ClassNotFoundException;

    /**
     * Factory for PrincipalRule, the container to connect profiling rule and
     * (user) principals
     *
     * Replaces the previous Class.forName and .instantiate logic with the
     * Spring based factory.
     * 
     * @return New instance of a principal rule
     * @throws ClassNotFoundException
     *             if the bean factory couldn't instantiate the bean
     */
    public PrincipalRule createPrincipalRule() throws ClassNotFoundException;

    /**
     * Factory for creating Rule Criterion
     *
     * @return New instance of a rule criterion
     * @throws ClassNotFoundException
     *             if the beanfactory couldn't instantiate the bean
     */
    public RuleCriterion createRuleCriterion() throws ClassNotFoundException;

    
    
    /**
     * Resets the default rule for this portal
     *
     * @param defaultRule
     *            The name of the rule to set as default
     */
    public void setDefaultRule(String defaultRule);

}
