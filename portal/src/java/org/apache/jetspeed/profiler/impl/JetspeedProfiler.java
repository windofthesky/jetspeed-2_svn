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
package org.apache.jetspeed.profiler.impl;

import org.apache.jetspeed.profiler.Profiler;
import org.picocontainer.Startable;

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.Transaction;

import org.apache.jetspeed.om.desktop.Desktop;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;

import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.impl.AbstractProfilingRule;
import org.apache.jetspeed.profiler.rules.impl.PrincipalRuleImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.page.PageManager;


/**
 * JetspeedProfilerService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedProfiler implements Profiler, Startable 
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(JetspeedProfiler.class);

    private PersistenceStoreContainer pContainer;
    private String storeName = "jetspeed";
    
    /** The default locator class implementation */
    private Class locatorClass = JetspeedProfileLocator.class;
    /** The default principalRule association class implementation */
    private Class principalRuleClass = PrincipalRuleImpl.class;
    /** The base (abstract) profilingRule class implementation */
    private Class profilingRuleClass = AbstractProfilingRule.class; 
        
    /** The configured default rule for this portal */
    private String defaultRule = "j1";

    private String anonymousUser = "anon";
    
    public JetspeedProfiler(PersistenceStoreContainer pContainer, String storeName)
	{
        this.pContainer = pContainer;
        this.storeName = storeName;
    }

    
    /**
     * Create a JetspeedProfiler with properties. Expected properties are:
     * 
     * 	   defaultRule   = the default profiling rule
     *     anonymousUser = the name of the anonymous user
     *     storeName = The name of the persistence store component to connect to  
     *     services.profiler.locator.impl = the pluggable Profile Locator impl
     *     services.profiler.principalRule.impl = the pluggable Principal Rule impl
     *     services.profiler.profilingRule.impl = the pluggable Profiling Rule impl
     *      
     * @param pContainer  The persistence store container
     * @param properties  Properties for this component described above
     */
    public JetspeedProfiler(PersistenceStoreContainer pContainer, Properties properties)
	{
        this.pContainer = pContainer;
        this.storeName = properties.getProperty("storeName", "jetspeed");        
        this.defaultRule = properties.getProperty("defaultRule", "j1");
        this.anonymousUser = properties.getProperty("anonymousUser", "anon");
        initModelClasses(properties);
    }

    public JetspeedProfiler(PersistenceStoreContainer pContainer)
	{
        this.pContainer = pContainer;
	}
    
    private void initModelClasses(Properties properties)
	{
        String modelName = "";
        try
        {
	        if ((modelName = properties.getProperty("locator.impl")) != null)
	        {
	            locatorClass = Class.forName(modelName);
	        }
	        if ((modelName = properties.getProperty("principalRule.impl")) != null)
	        {
	            principalRuleClass = Class.forName(modelName);
	        }
	        if ((modelName = properties.getProperty("profilingRule.impl")) != null)
	        {
	            profilingRuleClass = Class.forName(modelName);
	        }	        	        
        }
        catch (ClassNotFoundException e)
        {
            log.error("Model class not found: " + modelName);
        }
    }
    
    public void start()
	{
	}
    
    public void stop()
	{
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
     * @see org.apache.jetspeed.profiler.ProfilerService#getProfile(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.rules.ProfilingRule)
     */
    public ProfileLocator getProfile(RequestContext context, ProfilingRule rule)
    {        
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
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();        
        filter.addEqualTo("principalName", principal);
        Object query = store.newQuery(principalRuleClass, filter);
        PrincipalRule pr = (PrincipalRule) store.getObjectByQuery(query);
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
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();        
        Object query = store.newQuery(profilingRuleClass, filter);
        ProfilingRule rule = (ProfilingRule) store.getObjectByQuery(query);
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
        
        // TODO: NEXT load the page manager as a dependency when i make the profiler a service
        PageManager pm = (PageManager)Jetspeed.getComponentManager().getComponent("CastorXmlPageManager");
        return pm.getPage(locator);
        
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
         try
         {
             return (ProfileLocator)locatorClass.newInstance();
         }
         catch (Exception e)
         {
             log.error("Failed to create locator for " + locatorClass);
         }
         return null;
     }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getRules()
     */
    public Collection getRules()
    {
        PersistenceStore store = getPersistenceStore();        
        return store.getExtent(profilingRuleClass);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getRule(java.lang.String)
     */
    public ProfilingRule getRule(String id)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();        
        filter.addEqualTo("id", id);
        Object query = store.newQuery(profilingRuleClass, filter);
        return (ProfilingRule) store.getObjectByQuery(query);        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getAnonymousUser()
     */
    public String getAnonymousUser()
    {
         return this.anonymousUser;
    }
    
    protected PersistenceStore getPersistenceStore()
    {
        PersistenceStore store = pContainer.getStoreForThread(storeName);
        Transaction tx = store.getTransaction();
        if (!tx.isOpen())
        {
            tx.begin();
        }
        return store;
    }
	
}
