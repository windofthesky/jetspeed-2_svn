/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
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
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;

/**
 * JetspeedTransactionalProfiler
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class JetspeedProfilerImpl extends InitablePersistenceBrokerDaoSupport implements Profiler
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(JetspeedProfilerImpl.class);

    /**
     * This is the princapl that is used if there are no principal to rule
     * associations for the current principal
     */
    public final static Principal DEFAULT_RULE_PRINCIPAL = new UserPrincipalImpl("*");

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

    private Map principalRules = new HashMap();

    public JetspeedProfilerImpl(String repositoryPath)
    {
        super(repositoryPath);
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
     * @param properties
     *                     Properties for this component described above
     * @throws ClassNotFoundException
     *                    if any the implementation classes defined within the
     *                    <code>properties</code> argument could not be found.
     */
    public JetspeedProfilerImpl(String repositoryPath, String defaultRule) 
    throws ClassNotFoundException
    {
        this(repositoryPath);
        this.defaultRule = defaultRule; 
        // start()
    }

    public JetspeedProfilerImpl( String repositoryPath, String defaultRule, Properties properties) 
    throws ClassNotFoundException
    {
        this(repositoryPath, defaultRule);
        initModelClasses(properties); // TODO: move this to
        // start()
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

    public ProfileLocator getProfile( RequestContext context, String locatorName ) throws ProfilerException
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
    
    public ProfileLocator getDefaultProfile( RequestContext context, String locatorName ) throws ProfilerException
    {
       
        ProfilingRule rule = getRuleForPrincipal(DEFAULT_RULE_PRINCIPAL, locatorName);
        if (null == rule)
        {
            log.warn("Could not find profiling rule for principal: " + DEFAULT_RULE_PRINCIPAL);
            rule = this.getDefaultRule();
        }

        if (null == rule)
        {
            String msg = "Couldn't find any profiling rules including default rule for principal " + DEFAULT_RULE_PRINCIPAL;
            log.error(msg);
            throw new ProfilerException(msg);
        }
        // create a profile locator for given rule
        return rule.apply(context, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getProfile(org.apache.jetspeed.request.RequestContext,
     *          org.apache.jetspeed.profiler.rules.ProfilingRule)
     */
    public ProfileLocator getProfile( RequestContext context, ProfilingRule rule ) throws ProfilerException
    {
        // create a profile locator for given rule
        return rule.apply(context, this);
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
    public ProfiledPageContext createProfiledPageContext( Map locators )
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
     * @see org.apache.jetspeed.profiler.Profiler#getRuleForPrincipal(java.security.Principal,
     *          java.lang.String)
     */
    public ProfilingRule getRuleForPrincipal( Principal principal, String locatorName )
    {
        ProfilingRule rule = null;
        // lookup the rule for the given principal in our user/rule table
        PrincipalRule pr = lookupPrincipalRule(principal.getName(), locatorName);

        // if not found, fallback to the locator named rule or system wide rule
        if (pr == null)
        {
            // find rule on locator name
            rule = getRule(locatorName);
            
            if ( rule == null )
            {
                // if not found, fallback to the system wide rule
                rule = getDefaultRule();
            }
        }
        else
        {
            // Get the associated rule
            rule = pr.getProfilingRule();
        }

        return rule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#setRuleForPrincipal(java.security.Principal,
     *          org.apache.jetspeed.profiler.rules.ProfilingRule, java.lang.String)
     */
    public void setRuleForPrincipal( Principal principal, ProfilingRule rule, String locatorName )
    {
        Criteria c = new Criteria();
        c.addEqualTo("principalName", principal.getName());
        c.addEqualTo("locatorName", locatorName);

        PrincipalRule pr = (PrincipalRule) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(principalRuleClass, c));

        if (pr == null)
        {
            pr = new PrincipalRuleImpl(); // TODO: factory
            pr.setPrincipalName(principal.getName());
            pr.setLocatorName(locatorName);
            pr.setProfilingRule(rule);
        }
        pr.setProfilingRule(rule);
        getPersistenceBrokerTemplate().store(pr);
        principalRules.put(makePrincipalRuleKey(principal.getName(), locatorName), pr);
    }

    private String makePrincipalRuleKey( String principal, String locator )
    {
        return principal + ":" + locator;
    }

    /**
     * Helper function to lookup principal rule associations by principal
     * 
     * @param principal
     *                     The string representation of the principal name.
     * @return The found PrincipalRule associated with the principal key or null
     *                if not found.
     */
    private PrincipalRule lookupPrincipalRule( String principal, String locatorName )
    {
        PrincipalRule pr = (PrincipalRule) principalRules.get(makePrincipalRuleKey(principal, locatorName));
        if (pr != null)
        {
            return pr;
        }
        Criteria c = new Criteria();
        c.addEqualTo("principalName", principal);
        c.addEqualTo("locatorName", locatorName);

        pr = (PrincipalRule) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(principalRuleClass, c));

        principalRules.put(makePrincipalRuleKey(principal, locatorName), pr);
        return pr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getDefaultRule()
     */
    public ProfilingRule getDefaultRule()
    {
        return getRule(this.defaultRule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getRules()
     */
    public Collection getRules()
    {
        return getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(profilingRuleClass, new Criteria()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getRule(java.lang.String)
     */
    public ProfilingRule getRule( String id )
    {
        // TODO: implement caching
        Criteria c = new Criteria();
        c.addEqualTo("id", id);

        return (ProfilingRule) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(profilingRuleClass, c));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getLocatorNamesForPrincipal(java.security.Principal)
     */
    public String[] getLocatorNamesForPrincipal( Principal principal )
    {
        Criteria c = new Criteria();
        c.addEqualTo("principalName", principal.getName());

        Collection result = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(principalRuleClass, c));
        if (result.size() == 0)
        {
            return new String[]{};
        }
        String[] names = new String[result.size()];
        Iterator it = result.iterator();
        int ix = 0;
        while (it.hasNext())
        {
            PrincipalRule pr = (PrincipalRule) it.next();
            names[ix] = pr.getLocatorName();
            ix++;
        }
        return names;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getRulesForPrincipal(java.security.Principal)
     */
    public Collection getRulesForPrincipal( Principal principal )
    {
        Criteria c = new Criteria();
        c.addEqualTo("principalName", principal.getName());
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(principalRuleClass, c));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getProfileLocators(org.apache.jetspeed.request.RequestContext,
     *          java.security.Principal)
     */
    public Map getProfileLocators( RequestContext context, Principal principal ) throws ProfilerException
    {
        Map locators = new HashMap();
        Collection rules = getRulesForPrincipal(principal);
    
        Iterator it = rules.iterator();
        while (it.hasNext())
        {
            PrincipalRule pr = (PrincipalRule) it.next();
            locators.put(pr.getLocatorName(), getProfile(context, pr.getLocatorName()));
        }
        return locators;
    }

    public Map getDefaultProfileLocators( RequestContext context) throws ProfilerException
    {
        Map locators = new HashMap();

        Collection rules = getRulesForPrincipal(DEFAULT_RULE_PRINCIPAL);

        Iterator it = rules.iterator();
        while (it.hasNext())
        {
            PrincipalRule pr = (PrincipalRule) it.next();
            locators.put(pr.getLocatorName(), getDefaultProfile(context, pr.getLocatorName()));
        }
        return locators;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#storeProfilingRule(org.apache.jetspeed.profiler.rules.ProfilingRule)
     */
    public void storeProfilingRule( ProfilingRule rule ) throws ProfilerException
    {
        getPersistenceBrokerTemplate().store(rule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#deleteProfilingRule(org.apache.jetspeed.profiler.rules.ProfilingRule)
     */
    public void deleteProfilingRule( ProfilingRule rule ) throws ProfilerException
    {
        getPersistenceBrokerTemplate().delete(rule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#storePrincipalRule(org.apache.jetspeed.profiler.rules.PrincipalRule)
     */
    public void storePrincipalRule( PrincipalRule rule ) throws ProfilerException
    {
        getPersistenceBrokerTemplate().store(rule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#deletePrincipalRule(org.apache.jetspeed.profiler.rules.PrincipalRule)
     */
    public void deletePrincipalRule( PrincipalRule rule ) throws ProfilerException
    {
        getPersistenceBrokerTemplate().delete(rule);
    }

}