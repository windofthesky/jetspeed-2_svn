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
package org.apache.jetspeed.profiler.impl;

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.om.desktop.Desktop;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.profiler.ProfilerService;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.services.page.PageManager;

/**
 * JetspeedProfilerService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedProfilerService
    extends BaseCommonService
    implements ProfilerService
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(JetspeedProfilerService.class);
    /** The persistence plugin service */
    private PersistencePlugin plugin;
    /** The default locator class implementation */
    private Class locatorClass = null;
    /** The base (abstract) profilingRule class implementation */
    private Class profilingRuleClass = null;    
    /** The default principalRule association class implementation */
    private Class principalRuleClass = null;
    /** The configured default rule for this portal */
    private String defaultRule = null;
    /** anonymous user */
    private String anonymousUser = null;
    
    /* (non-Javadoc)
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws InitializationException
    {
        if (!isInitialized())
        {
            this.defaultRule = getConfiguration().getString("default.rule");
            this.anonymousUser = getConfiguration().getString("anonymous.user", "anon");
            locatorClass = this.loadModelClass("locator.impl");
            principalRuleClass = this.loadModelClass("principalRule.impl");
            profilingRuleClass = this.loadModelClass("profilingRule.impl");
            
            PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
            String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");
            plugin = ps.getPersistencePlugin(pluginName);
                                               
            setInit(true);
        }        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getProfile(org.apache.jetspeed.request.RequestContext)
     */
    public ProfileLocator getProfile(RequestContext context)
        throws ProfilerException
    {
        // get the principal representing the currently logged on user 
        Subject subject = context.getSubject();
        if (subject == null)
        {
            String msg = "Invalid (null) Subject in request pipeline";
            log.error(msg);
            throw new ProfilerException(msg);
        }
        // get the UserPrincipal, finding the first UserPrincipal, or
        // find the first principal if no UserPrincipal isn't available
        Principal principal = SecurityHelper.getBestPrincipal(subject, UserPrincipal.class);      
        if (principal == null)
        {
            String msg = "Could not find a principle for subject in request pipeline";
            log.error(msg);
            throw new ProfilerException(msg);            
        }
        
        // find a profiling rule for this principal
        ProfilingRule rule = getRuleForPrincipal(principal);
        if (null == rule)
        {
            log.warn("Could not find profiling rule for principal: " + principal);
            rule = this.getDefaultRule();
        }
    
        if (null == rule)
        {
            String msg = "Couldn't find any profiling rules including default rule for principal " + principal;
            log.error(msg);
            throw new ProfilerException(msg);                
        }
        // create a profile locator for given rule
        return rule.apply(context, this);
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getDefaultRule()
     */
    public ProfilingRule getDefaultRule()
    {
        return lookupProfilingRule(this.defaultRule);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getRuleForPrincipal(java.security.Principal)
     */
    public ProfilingRule getRuleForPrincipal(Principal principal)
    {
        // lookup the rule for the given principal in our user/rule table
        PrincipalRule pr = lookupPrincipalRule(principal.getName());
                
        // if not found, fallback to the system wide rule         
        if (pr == null)
        {
            return getDefaultRule();
        }
                
        // Now get the associated rule 
        return pr.getProfilingRule();
    }

    /**
     * Helper function to lookup principal rule associations by principal
     * 
     * @param principal The string representation of the principal name.
     * @return The found PrincipalRule associated with the principal key or null if not found.
     */
    private PrincipalRule lookupPrincipalRule(String principal)
    {
        // TODO: implement caching        
        LookupCriteria criteria = plugin.newLookupCriteria();
        criteria.addEqualTo("principalName", principal);
        Object query = plugin.generateQuery(principalRuleClass, criteria);
        PrincipalRule pr = (PrincipalRule) plugin.getObjectByQuery(principalRuleClass, query);
        return pr;            
    }

    /**
     * Helper function to lookup a profiling rule by rule id 
     * 
     * @param ruleid The unique identifier for a rule.
     * @return The found ProfilingRule associated with the rule id or null if not found.
     */
    private ProfilingRule lookupProfilingRule(String ruleid)
    {
        // TODO: implement caching
        LookupCriteria criteria = plugin.newLookupCriteria();
        criteria.addEqualTo("id", ruleid);
        Object query = plugin.generateQuery(profilingRuleClass, criteria);
        ProfilingRule rule = (ProfilingRule) plugin.getObjectByQuery(profilingRuleClass, query);
        return rule;            
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getDesktop(org.apache.jetspeed.profiler.ProfileLocator)
     */
    public Desktop getDesktop(ProfileLocator locator)
    {
        Desktop desktop = null;
        Iterator fallback = locator.iterator();
        while (fallback.hasNext())
        {
            // desktop = PageManager.getDesktop((String)locator.next());
            if (desktop != null)
            {
                break;
            }            
        }        
        return desktop;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getPage(org.apache.jetspeed.profiler.ProfileLocator)
     */
    public Page getPage(ProfileLocator locator)
    {
        // TODO: under construction, for now use the name
        return PageManager.getPage(locator);
        
        /*
        Page page = null;
        Iterator fallback = locator.iterator();
        while (fallback.hasNext())
        {
            page = PageManager.getPage((String)fallback.next());
            if (page != null)
            {
                break;
            }            
        }               
        return page;
        */
    }   
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getFragment(org.apache.jetspeed.profiler.ProfileLocator)
     */
    public Fragment getFragment(ProfileLocator locator)
    {
        Fragment fragment = null;
        Iterator fallback = locator.iterator();        
        while (fallback.hasNext())
        {
            // fragment = PageManager.getFragment((String)fallback.next());
            if (fragment != null)
            {
                break;
            }
        }
        return fragment;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#createLocator()
     */
     public ProfileLocator createLocator()
     {
         return (ProfileLocator)this.createObject(locatorClass);
     }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getRules()
     */
    public Collection getRules()
    {
        return plugin.getExtent(profilingRuleClass);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getAnonymousUser()
     */
    public String getAnonymousUser()
    {
         return this.anonymousUser;
    }
    
}
