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

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.LockFailedException;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfiledPageContext;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.impl.AbstractProfilingRule;
import org.apache.jetspeed.profiler.rules.impl.PrincipalRuleImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * JetspeedProfiler
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class JetspeedProfiler implements Profiler
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(JetspeedProfiler.class);

    /** The default locator class implementation */
    private Class locatorClass = JetspeedProfileLocator.class;
    /** The default profiled page context class implementation */
    private Class profiledPageContextClass = JetspeedProfiledPageContext.class;
    /** The default principalRule association class implementation */
    private Class principalRuleClass = PrincipalRuleImpl.class;
    /** The base (abstract) profilingRule class implementation */
    private Class profilingRuleClass = AbstractProfilingRule.class;

    /** The configured default rule for this portal */
    private String defaultRule = "j1";

    private String anonymousUser = "anon";

    PersistenceStore persistentStore;

    private Map principalRules = new HashMap();

    public JetspeedProfiler( PersistenceStore persistentStore )
    {
        this.persistentStore = persistentStore;
    }

    /**
     * Create a JetspeedProfiler with properties. Expected properties are:
     * 
     * defaultRule = the default profiling rule anonymousUser = the name of the
     * anonymous user storeName = The name of the persistence store component to
     * connect to services.profiler.locator.impl = the pluggable Profile Locator
     * impl services.profiler.principalRule.impl = the pluggable Principal Rule
     * impl services.profiler.profilingRule.impl = the pluggable Profiling Rule
     * impl
     * 
     * @param pContainer
     *            The persistence store container
     * @param properties
     *            Properties for this component described above
     * @throws ClassNotFoundException
     *             if any the implementation classes defined within the
     *             <code>properties</code> argument could not be found.
     */
    public JetspeedProfiler( PersistenceStore persistentStore, Properties properties )
            throws ClassNotFoundException
    {
        this(persistentStore);
        this.defaultRule = properties.getProperty("defaultRule", "j1");
        this.anonymousUser = properties.getProperty("anonymousUser", "anon");
        initModelClasses(properties); // TODO: move this to start()
    }

    private void initModelClasses( Properties properties ) throws ClassNotFoundException
    {
        String modelName = "";

        if ((modelName = properties.getProperty("locator.impl")) != null)
        {
            locatorClass = Class.forName(modelName);
        }
        if ((modelName = properties.getProperty("profiledPageContext.impl")) != null)
        {
            profiledPageContextClass = Class.forName(modelName);
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

    public ProfileLocator getProfile(RequestContext context, String locatorName) 
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
        ProfilingRule rule = getRuleForPrincipal(principal, locatorName);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.ProfilerService#getProfile(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.profiler.rules.ProfilingRule)
     */
    public ProfileLocator getProfile( RequestContext context, ProfilingRule rule )
    {
        // create a profile locator for given rule
        return rule.apply(context, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.ProfilerService#getDefaultRule()
     */
    public ProfilingRule getDefaultRule()
    {
        return lookupProfilingRule(this.defaultRule);
    }

    public ProfilingRule getRuleForPrincipal(Principal principal, String locatorName)
    {
        // lookup the rule for the given principal in our user/rule table
        PrincipalRule pr = lookupPrincipalRule(principal.getName(), locatorName);

        // if not found, fallback to the system wide rule
        if (pr == null)
        {
            return getDefaultRule();
        }

        // Now get the associated rule
        return pr.getProfilingRule();
    }
    
    
    public void setRuleForPrincipal(Principal principal, ProfilingRule rule, String locatorName)
    {
        Transaction tx = persistentStore.getTransaction();
        tx.begin();
  
        Filter filter = persistentStore.newFilter();
        filter.addEqualTo("principalName", principal);
        filter.addEqualTo("locatorName", locatorName);
        Object query = persistentStore.newQuery(principalRuleClass, filter);
        PrincipalRule pr = (PrincipalRule) persistentStore.getObjectByQuery(query);
        if (pr == null)
        {
            pr = new PrincipalRuleImpl(); // TODO: factory
            pr.setPrincipalName(principal.getName());
            pr.setLocatorName(locatorName);
            pr.setProfilingRule(rule);
        }
        try
        {
            pr.setProfilingRule(rule);
            persistentStore.lockForWrite(pr);
        }
        catch (LockFailedException e)
        {
            tx.rollback();
            e.printStackTrace();
            // TODO: throw appropriate exception
        }
        persistentStore.getTransaction().commit();
        principalRules.put(makePrincipalRuleKey(principal.getName(), locatorName), pr);
    }

    private String makePrincipalRuleKey(String principal, String locator)
    {
        return principal + ":" + locator;
    }
    
    /**
     * Helper function to lookup principal rule associations by principal
     * 
     * @param principal
     *            The string representation of the principal name.
     * @return The found PrincipalRule associated with the principal key or null
     *         if not found.
     */
    private PrincipalRule lookupPrincipalRule(String principal, String locatorName)
    {
        PrincipalRule pr = (PrincipalRule) principalRules.get(makePrincipalRuleKey(principal, locatorName));
        if (pr != null)
        {
            return pr;
        }
        Filter filter = persistentStore.newFilter();        
        filter.addEqualTo("principalName", principal);
        filter.addEqualTo("locatorName", locatorName);        
        Object query = persistentStore.newQuery(principalRuleClass, filter);
        pr = (PrincipalRule) persistentStore.getObjectByQuery(query);
        principalRules.put(makePrincipalRuleKey(principal, locatorName), pr);
        return pr;
    }

    /**
     * Helper function to lookup a profiling rule by rule id
     * 
     * @param ruleid
     *            The unique identifier for a rule.
     * @return The found ProfilingRule associated with the rule id or null if
     *         not found.
     */
    private ProfilingRule lookupProfilingRule( String ruleid )
    {
        // TODO: implement caching
        Filter filter = persistentStore.newFilter();
        Object query = persistentStore.newQuery(profilingRuleClass, filter);
        ProfilingRule rule = (ProfilingRule) persistentStore.getObjectByQuery(query);
        return rule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#createLocator(org.apache.jetspeed.request.RequestContext)
     */
    public ProfileLocator createLocator( RequestContext context )
    {
        try
        {
            ProfileLocator locator = (ProfileLocator) locatorClass.newInstance();
            locator.init(this, context.getPath());
            return locator;
        }
        catch (Exception e)
        {
            log.error("Failed to create locator for " + locatorClass);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#createProfiledPageContext(java.util.Map)
     */
    public ProfiledPageContext createProfiledPageContext(Map locators)
    {
        try
        {
            ProfiledPageContext pageContext = (ProfiledPageContext) profiledPageContextClass.newInstance();
            pageContext.init(this, locators);
            return pageContext;
        }
        catch (Exception e)
        {
            log.error("Failed to create profiled page context for " + profiledPageContextClass);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.ProfilerService#getRules()
     */
    public Collection getRules()
    {

        return persistentStore.getExtent(profilingRuleClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.ProfilerService#getRule(java.lang.String)
     */
    public ProfilingRule getRule( String id )
    {
        Filter filter = persistentStore.newFilter();
        filter.addEqualTo("id", id);
        Object query = persistentStore.newQuery(profilingRuleClass, filter);
        return (ProfilingRule) persistentStore.getObjectByQuery(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.ProfilerService#getAnonymousUser()
     */
    public String getAnonymousUser()
    {
        return this.anonymousUser;
    }

    public String[] getLocatorNamesForPrincipal(Principal principal)
    {
        Filter filter = persistentStore.newFilter();        
        filter.addEqualTo("principalName", principal.getName());
        Object query = persistentStore.newQuery(principalRuleClass, filter);
        Collection result = persistentStore.getCollectionByQuery(query);
        if (result.size() == 0)
        {
            return new String[]{};
        }
        String [] names = new String[result.size()];
        Iterator it = result.iterator();
        int ix = 0;
        while (it.hasNext())
        {
            PrincipalRule pr = (PrincipalRule)it.next();
            names[ix] = pr.getLocatorName();
            ix++;
        }
        return names;
    }
    
    public Map getProfileLocators(RequestContext context, Principal principal)
    throws ProfilerException
    {
        Map locators = new HashMap();
        String locatorNames[] = getLocatorNamesForPrincipal(principal);
        
        for (int ix = 0; (ix < locatorNames.length); ix++)
        {
            locators.put(locatorNames[ix], getProfile(context, locatorNames[ix]));   
        }
        return locators;
    }
    
    
    public void storeProfilingRule(ProfilingRule rule)
    throws ProfilerException
    {
        try
        {
            System.out.println("making pers : " + rule.getId());
            Transaction tx = persistentStore.getTransaction();
            tx.begin();
            persistentStore.makePersistent(rule);
            persistentStore.lockForWrite(rule);
            tx.commit();
            System.out.println("done making pers : " + rule.getId());
            
        }
        catch (Exception e)
        {
            throw new ProfilerException("failed to store: " + rule.getId(), e);
        }
    }
    
    public void deleteProfilingRule(ProfilingRule rule)
    throws ProfilerException    
    {
        try
        {
            Transaction tx = persistentStore.getTransaction();
            tx.begin();
            persistentStore.deletePersistent(rule);
            tx.commit();
        }
        catch (Exception e)
        {
            throw new ProfilerException("failed to delete: " + rule.getId(), e);
        }
        
    }
    
}
