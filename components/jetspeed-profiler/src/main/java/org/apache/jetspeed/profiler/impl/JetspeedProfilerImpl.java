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
package org.apache.jetspeed.profiler.impl;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfileResolvers;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.impl.AbstractProfilingRule;
import org.apache.jetspeed.profiler.rules.impl.PrincipalRuleImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.UserSubjectPrincipal;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import java.io.Serializable;
import java.security.Principal;
import java.util.*;

/**
 * JetspeedTransactionalProfiler
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class JetspeedProfilerImpl extends InitablePersistenceBrokerDaoSupport
        implements Profiler, BeanFactoryAware {
    /**
     * Profiler context session attribute name
     */
    public final static String PROFILER_CONTEXT_ATTRIBUTE_NAME = "org.apache.jetspeed.profiler.ProfilerContext";

    /**
     * Default guest principal name
     */
    public final static String DEFAULT_GUEST_PRINCIPAL_NAME = "guest";

    /**
     * Default rule principal name
     */
    public final static String DEFAULT_RULE_PRINCIPAL_NAME = "*";

    /**
     * Default and guest rule cache reaping interval
     */
    public final static long DEFAULT_AND_GUEST_RULE_REAPING_INTERVAL = 30000;

    /**
     * The default rule.
     */
    public final static String DEFAULT_RULE = "j1";

    /**
     * Commons logging
     */
    protected final static Logger log = LoggerFactory.getLogger(JetspeedProfilerImpl.class);

    /**
     * This is the principal that is used if there are no principal to rule associations for the current principal
     */
    public final static Principal DEFAULT_RULE_PRINCIPAL = new Principal() {
        public String getName() {
            return DEFAULT_RULE_PRINCIPAL_NAME;
        }
    };

    /**
     * The default locator class implementation
     */
    private String locatorBean = "ProfileLocator";

    /**
     * The default principalRule association class implementation
     */
    private Class prRuleClass;

    private String principalRuleBean = "PrincipalRule";

    /**
     * The base (abstract) profilingRule class implementation
     */
    private String profilingRuleStandardBean = "StandardProfilingRule";

    /**
     * The base (abstract) profilingRule class implementation
     */
    private String profilingRuleFallbackBean = "RoleFallbackProfilingRule";

    /**
     * The base (abstract) profilingRule class implementation
     */
    private Class profilingRuleClass = AbstractProfilingRule.class;

    /**
     * The configured default rule for this portal
     */
    private String defaultRule = DEFAULT_RULE;

    /**
     * list of transient and persistent rules cached by principal and locator name
     */
    private Map<String, PrincipalRule> principalRules =
            Collections.synchronizedMap(new HashMap<String, PrincipalRule>());

    /**
     * list of persistent rules cached by principal
     */
    private Map<String, Collection<PrincipalRule>> rulesPerPrincipal =
            Collections.synchronizedMap(new HashMap<String, Collection<PrincipalRule>>());

    /**
     * list of all transient and persistent rules cached by principal
     */
    private Map<String, Collection<PrincipalRule>> allRulesPerPrincipal =
            Collections.synchronizedMap(new HashMap<String, Collection<PrincipalRule>>());

    private ProfileResolvers resolvers;

    /**
     * the default criterion bean name
     */
    private String ruleCriterionBean = "RuleCriterion";

    /**
     * added support for bean factory to create profile rules
     */
    private BeanFactory beanFactory;

    /**
     * last default and guest reap time
     */
    private volatile long lastDefaultGuestReapTime = System.currentTimeMillis();

    /**
     * configured guest principal name
     */
    private String guestPrincipalName;

    public JetspeedProfilerImpl(String repositoryPath,
                                ProfileResolvers resolvers) {
        super(repositoryPath);
        this.resolvers = resolvers;
    }

    /**
     * Create a JetspeedProfiler with properties. Expected properties are:
     * defaultRule = the default profiling rule anonymousUser = the name of the
     * anonymous user storeName = The name of the persistence store component to
     * connect to services.profiler.locator.impl = the pluggable Profile Locator
     * impl services.profiler.principalRule.impl = the pluggable Principal Rule
     * impl services.profiler.profilingRule.impl = the pluggable Profiling Rule
     * impl
     *
     * @throws ClassNotFoundException if any the implementation classes defined within the
     *                                <code>properties</code> argument could not be found.
     */
    public JetspeedProfilerImpl(String repositoryPath, String defaultRule,
                                ProfileResolvers resolvers) throws ClassNotFoundException {
        this(repositoryPath, resolvers);
        this.defaultRule = defaultRule;
    }

    /**
     * @deprecated As of release 2.1, property-based class references replaced
     *             by container managed bean factory
     */
    public JetspeedProfilerImpl(String repositoryPath, String defaultRule,
                                Properties properties, ProfileResolvers resolvers)
            throws ClassNotFoundException {
        this(repositoryPath, defaultRule, resolvers);
        initModelClasses(properties); // TODO: move this to
        // start()
    }

    /**
     * support passing of rule creation classes
     */
    public JetspeedProfilerImpl(String repositoryPath, String defaultRule,
                                ProfileResolvers resolvers, Map<String,String> ruleConstructors,
                                String ruleCriterionBean) throws ClassNotFoundException {
        this(repositoryPath, defaultRule, resolvers);
        this.ruleCriterionBean = ruleCriterionBean;
        initRuleClasses(ruleConstructors);
    }

    /**
     * @param defaultRule The default rule to set.
     */
    public void setDefaultRule(String defaultRule) {
        this.defaultRule = defaultRule;
    }

    /**
     * @deprecated As of release 2.1, property-based class references replaced
     *             by container managed bean factory
     */
    private void initModelClasses(Properties properties)
            throws ClassNotFoundException {

        /**
         * String modelName = "";
         *
         * if ((modelName = properties.getProperty("locator.impl")) != null) { //
         * locatorClassName = modelName; } if ((modelName =
         * properties.getProperty("principalRule.impl")) != null) {
         * principalRuleClass = Class.forName(modelName); } if ((modelName =
         * properties.getProperty("profilingRule.impl")) != null) {
         * profilingRuleClass = Class.forName(modelName); }
         */
    }

    public ProfileLocator getProfile(RequestContext context, String locatorName)
            throws ProfilerException {
        // get the principal representing the currently logged on user
        Subject subject = context.getSubject();
        if (subject == null) {
            String msg = "Invalid (null) Subject in request pipeline";
            log.error(msg);
            throw new ProfilerException(msg);
        }
        // get the UserPrincipal, finding the first UserPrincipal, or
        // find the first principal if no UserPrincipal isn't available
        Principal principal = SubjectHelper.getBestPrincipal(subject,
                UserSubjectPrincipal.class);
        if (principal == null) {
            String msg = "Could not find a principle for subject in request pipeline";
            log.error(msg);
            throw new ProfilerException(msg);
        }

        // setup/maintain profiler context for session end notification
        setupProfilerContext(context);

        // find a profiling rule for this principal
        ProfilingRule rule = getRuleForPrincipal(principal, locatorName);
        if (null == rule) {
            log.warn("Could not find profiling rule for principal: "
                    + principal);
            rule = this.getDefaultRule();
        }

        if (null == rule) {
            String msg = "Couldn't find any profiling rules including default rule for principal "
                    + principal;
            log.error(msg);
            throw new ProfilerException(msg);
        }
        // create a profile locator for given rule
        return rule.apply(context, this);
    }

    public ProfileLocator getDefaultProfile(RequestContext context,
                                            String locatorName) throws ProfilerException {
        // find a profiling rule for the default principal
        ProfilingRule rule = getRuleForPrincipal(DEFAULT_RULE_PRINCIPAL,
                locatorName);
        if (null == rule) {
            log.warn("Could not find profiling rule for principal: "
                    + DEFAULT_RULE_PRINCIPAL);
            rule = this.getDefaultRule();
        }

        if (null == rule) {
            String msg = "Couldn't find any profiling rules including default rule for principal "
                    + DEFAULT_RULE_PRINCIPAL;
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
     *      org.apache.jetspeed.profiler.rules.ProfilingRule)
     */
    public ProfileLocator getProfile(RequestContext context, ProfilingRule rule)
            throws ProfilerException {
        // create a profile locator for given rule
        return rule.apply(context, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getRuleForPrincipal(java.security.Principal,
     *      java.lang.String)
     */
    public ProfilingRule getRuleForPrincipal(Principal principal,
                                             String locatorName) {
        ProfilingRule rule = null;
        // lookup the rule for the given principal in our user/rule table
        PrincipalRule pr = lookupPrincipalRule(principal.getName(), locatorName);

        // if not found, fallback to the locator named rule or system wide rule
        if (pr == null) {
            // find rule on locator name
            rule = getRule(locatorName);

            if (rule == null) {
                // if not found, fallback to the system wide rule
                rule = getDefaultRule();
            }
            pr = new PrincipalRuleImpl();
            pr.setLocatorName(locatorName);
            pr.setPrincipalName(principal.getName());
            pr.setProfilingRule(rule);
            principalRules.put(makePrincipalRuleKey(principal.getName(),
                    locatorName), pr);
            // track cached principal rules
            trackCachedPrincipalRulesPut(principal.getName(), pr);
        } else {
            // Get the associated rule
            rule = pr.getProfilingRule();
        }

        return rule;
    }

    /**
     * Internal method to ensure we have a valid principalRule class for queries
     *
     * @return the class object for the principalRule
     */
    private Class getPrincipalRuleClass() {
        if (this.prRuleClass == null) {
            try {
                PrincipalRule tempRule = createPrincipalRule();
                this.prRuleClass = tempRule.getClass();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.prRuleClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#setRuleForPrincipal(java.security.Principal,
     *      org.apache.jetspeed.profiler.rules.ProfilingRule, java.lang.String)
     */
    public void setRuleForPrincipal(Principal principal, ProfilingRule rule,
                                    String locatorName) {
        Criteria c = new Criteria();
        c.addEqualTo("principalName", principal.getName());
        c.addEqualTo("locatorName", locatorName);
        PrincipalRule pr = (PrincipalRule) getPersistenceBrokerTemplate()
                .getObjectByQuery(
                        QueryFactory.newQuery(getPrincipalRuleClass(), c));

        if (pr == null) {
            pr = new PrincipalRuleImpl(); // TODO: factory
            pr.setPrincipalName(principal.getName());
            pr.setLocatorName(locatorName);
            pr.setProfilingRule(rule);
        }
        rule.setResolvers(resolvers);
        pr.setProfilingRule(rule);
        getPersistenceBrokerTemplate().store(pr);
        principalRules.put(makePrincipalRuleKey(principal.getName(),
                locatorName), pr);
        // track cached principal rules
        trackCachedPrincipalRulesPut(principal.getName(), pr);
        // reset persistent rules per principal
        rulesPerPrincipal.remove(principal.getName());
    }

    private String makePrincipalRuleKey(String principal, String locator) {
        return principal + ":" + locator;
    }

    /**
     * Helper function to lookup principal rule associations by principal
     *
     * @param principal The string representation of the principal name.
     * @return The found PrincipalRule associated with the principal key or null
     *         if not found.
     */
    private PrincipalRule lookupPrincipalRule(String principal,
                                              String locatorName) {
        PrincipalRule pr = principalRules.get(makePrincipalRuleKey(principal, locatorName));
        if (pr != null) {
            return pr;
        }
        Criteria c = new Criteria();
        c.addEqualTo("principalName", principal);
        c.addEqualTo("locatorName", locatorName);

        pr = (PrincipalRule) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(getPrincipalRuleClass(), c));
        if (pr != null) pr.getProfilingRule().setResolvers(resolvers);

        principalRules.put(makePrincipalRuleKey(principal, locatorName), pr);
        // track cached principal rules
        trackCachedPrincipalRulesPut(principal, pr);
        return pr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getDefaultRule()
     */
    public ProfilingRule getDefaultRule() {
        return getRule(this.defaultRule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getRules()
     */
    public Collection<ProfilingRule> getRules() {
        Collection<ProfilingRule> rules = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(profilingRuleClass, new Criteria()));
        for (ProfilingRule rule : rules) {
            rule.setResolvers(resolvers);
        }
        return rules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getRule(java.lang.String)
     */
    public ProfilingRule getRule(String id) {
        Criteria c = new Criteria();
        c.addEqualTo("id", id);
        ProfilingRule rule = (ProfilingRule) getPersistenceBrokerTemplate()
                .getObjectByQuery(QueryFactory.newQuery(profilingRuleClass, c));
        if (rule != null) {
            rule.setResolvers(resolvers);
        }
        return rule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getLocatorNamesForPrincipal(java.security.Principal)
     */
    public String[] getLocatorNamesForPrincipal(Principal principal) {
        Criteria c = new Criteria();
        c.addEqualTo("principalName", principal.getName());

        Collection<PrincipalRule> result = getPersistenceBrokerTemplate()
                .getCollectionByQuery(
                        QueryFactory.newQuery(getPrincipalRuleClass(), c));
        if (result.size() == 0) {
            return new String[]{};
        }
        String[] names = new String[result.size()];
        int ix = 0;
        for (PrincipalRule pr : result) {
            names[ix] = pr.getLocatorName();
            pr.getProfilingRule().setResolvers(resolvers);
            ix++;
        }
        return names;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getRulesForPrincipal(java.security.Principal)
     */
    public Collection<PrincipalRule> getRulesForPrincipal(Principal principal) {
        Collection<PrincipalRule> rules = this.rulesPerPrincipal.get(principal.getName());
        if (rules != null) return rules;
        Criteria c = new Criteria();
        c.addEqualTo("principalName", principal.getName());
        rules = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(getPrincipalRuleClass(), c));
        for (PrincipalRule pr : rules) {
            ProfilingRule rule = pr.getProfilingRule();
            if (rule != null) rule.setResolvers(resolvers);
        }
        this.rulesPerPrincipal.put(principal.getName(), rules);
        return rules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#getProfileLocators(org.apache.jetspeed.request.RequestContext,
     *      java.security.Principal)
     */
    public Map<String, ProfileLocator> getProfileLocators(RequestContext context, Principal principal)
            throws ProfilerException {
        // setup/maintain profiler context for session end notification
        setupProfilerContext(context);

        // get profile locators
        Map<String, ProfileLocator> locators = new HashMap<String, ProfileLocator>();
        Collection<PrincipalRule> rules = getRulesForPrincipal(principal);

        for (PrincipalRule pr : rules) {
            locators.put(pr.getLocatorName(), getProfile(context, pr.getLocatorName()));
        }
        return locators;
    }

    public Map<String, ProfileLocator> getDefaultProfileLocators(RequestContext context)
            throws ProfilerException {
        // get default profile locators
        Map<String, ProfileLocator> locators = new HashMap<String, ProfileLocator>();
        Collection<PrincipalRule> rules = getRulesForPrincipal(DEFAULT_RULE_PRINCIPAL);
        for (PrincipalRule pr : rules) {
            locators.put(pr.getLocatorName(), getDefaultProfile(context, pr.getLocatorName()));
        }
        return locators;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#storeProfilingRule(org.apache.jetspeed.profiler.rules.ProfilingRule)
     */
    public void storeProfilingRule(ProfilingRule rule) throws ProfilerException {
        getPersistenceBrokerTemplate().store(rule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#deleteProfilingRule(org.apache.jetspeed.profiler.rules.ProfilingRule)
     */
    public void deleteProfilingRule(ProfilingRule rule)
            throws ProfilerException {
        getPersistenceBrokerTemplate().delete(rule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#storePrincipalRule(org.apache.jetspeed.profiler.rules.PrincipalRule)
     */
    public void storePrincipalRule(PrincipalRule rule) throws ProfilerException {
        getPersistenceBrokerTemplate().store(rule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#deletePrincipalRule(org.apache.jetspeed.profiler.rules.PrincipalRule)
     */
    public void deletePrincipalRule(PrincipalRule rule)
            throws ProfilerException {
        getPersistenceBrokerTemplate().delete(rule);
        // reset persistent rules per principal
        rulesPerPrincipal.remove(rule.getPrincipalName());
        String key = this.makePrincipalRuleKey(rule.getPrincipalName(), rule.getLocatorName());
        // remove individual rule
        principalRules.remove(key);
        // track cached principal rules
        trackCachedPrincipalRulesRemoved(rule.getPrincipalName(), rule);
    }

    /*
     * Method called automatically by Spring container upon initialization
     * 
     * @param beanFactory automatically provided by framework @throws
     * BeansException
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * added logic to get the settings for creating the various rule related
     * instances
     *
     * @param beans
     * @throws ClassNotFoundException
     */
    private void initRuleClasses(Map<String,String> beans) throws ClassNotFoundException {

        String beanName = "";
        try {
            if ((beanName = (String) beans.get("locator")) != null) {
                this.locatorBean = beanName;
            }
        } catch (Exception e) {
            String msg = "Exception in setting locatorbeanName : "
                    + e.getLocalizedMessage();
            log.error(msg);
        }
        try {
            if ((beanName = (String) beans.get("principal")) != null) {
                this.principalRuleBean = beanName;
            }
        } catch (Exception e) {
            String msg = "Exception in setting principalRulebeanName : "
                    + e.getLocalizedMessage();
            log.error(msg);
        }
        try {
            if ((beanName = (String) beans.get("standard")) != null) {
                this.profilingRuleStandardBean = beanName;
            }
        } catch (Exception e) {
            String msg = "Exception in setting profilingRuleStandardbeanName : "
                    + e.getLocalizedMessage();
            log.error(msg);
        }
        try {
            if ((beanName = (String) beans.get("fallback")) != null) {
                this.profilingRuleFallbackBean = beanName;
            }
        } catch (Exception e) {
            String msg = "Exception in setting profilingRuleFallback : "
                    + e.getLocalizedMessage();
            log.error(msg);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#createProfilingRule(boolean)
     */
    public ProfilingRule createProfilingRule(boolean standard)
            throws ClassNotFoundException {
        try {
            if (standard)
                return (ProfilingRule) beanFactory.getBean(
                        this.profilingRuleStandardBean, ProfilingRule.class);
            else
                return (ProfilingRule) beanFactory.getBean(
                        this.profilingRuleFallbackBean, ProfilingRule.class);

        } catch (BeansException e) {
            throw new ClassNotFoundException("Spring failed to create the "
                    + (standard ? "standard" : "fallback")
                    + " profiling rule bean.", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#createLocator(RequestContext)
     */
    public ProfileLocator createLocator(RequestContext context) {
        try {
            ProfileLocator locator = (ProfileLocator) beanFactory.getBean(
                    this.locatorBean, ProfileLocator.class);
            locator.init(this, context.getPath(), ((context.getRequest() != null) ? context.getRequest().getServerName() : null));
            return locator;
        } catch (Exception e) {
            log.error("Failed to create locator for " + this.locatorBean
                    + " error : " + e.getLocalizedMessage());
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#createPrincipalRule()
     */
    public PrincipalRule createPrincipalRule() throws ClassNotFoundException {
        try {
            PrincipalRule principalRule = (PrincipalRule) beanFactory.getBean(
                    this.principalRuleBean, PrincipalRule.class);
            return principalRule;
        } catch (Exception e) {
            log.error("Failed to create principalRule for " + principalRuleBean
                    + " error : " + e.getLocalizedMessage());
            throw new ClassNotFoundException("Spring failed to create the "
                    + " principal rule bean.", e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.Profiler#createRuleCriterion()
     */
    public RuleCriterion createRuleCriterion() throws ClassNotFoundException {
        try {
            RuleCriterion ruleCriterion = (RuleCriterion) beanFactory.getBean(
                    this.ruleCriterionBean, RuleCriterion.class);
            return ruleCriterion;
        } catch (Exception e) {
            log.error("Failed to create principalRule for " + ruleCriterionBean
                    + " error : " + e.getLocalizedMessage());
            throw new ClassNotFoundException("Spring failed to create the "
                    + " rule criterion bean.", e);
        }

    }

    /**
     * Setup and maintain profiler context to be used to reap rule caches
     * for principals on session end.
     *
     * @param context request context
     * @throws ProfilerException when subject or principal not available
     */
    private void setupProfilerContext(RequestContext context) throws ProfilerException {
        // validate profiler context
        ProfilerContext profilerContext = (ProfilerContext) context.getSessionAttribute(PROFILER_CONTEXT_ATTRIBUTE_NAME);
        try {
            // access session principal and test for change
            Principal principal = SubjectHelper.getBestPrincipal(context.getSubject(), UserSubjectPrincipal.class);
            if (principal == null) {
                throw new NullPointerException("Principal not found");
            }
            if ((profilerContext == null) || (profilerContext.getPrincipal() != principal)) {
                // setup/reset profiler context
                context.setSessionAttribute(PROFILER_CONTEXT_ATTRIBUTE_NAME, new ProfilerContext(this, principal));
            }
        } catch (Exception e) {
            String message = "Unable to access principal in pipeline: " + e;
            log.error(message, e);
            throw new ProfilerException(message, e);
        }
    }

    /**
     * Track all put rules, (persistent and transient), cached for a principal.
     *
     * @param principalName name of principal rules is cached under
     * @param rule          rule cached
     */
    private void trackCachedPrincipalRulesPut(String principalName, PrincipalRule rule) {
        if (rule != null) {
            // maintain list of all rules cached per principal
            synchronized (allRulesPerPrincipal) {
                Collection<PrincipalRule> allRules = allRulesPerPrincipal.get(principalName);
                if (allRules == null) {
                    allRules = new ArrayList(4);
                    allRulesPerPrincipal.put(principalName, allRules);
                }
                allRules.add(rule);
            }
        }
    }

    /**
     * Track all removed rules, (persistent and transient), cached for a principal.
     *
     * @param principalName name of principal rules is cached under
     * @param rule          rule cached
     */
    private void trackCachedPrincipalRulesRemoved(String principalName, PrincipalRule rule) {
        if (rule != null) {
            // maintain list of all rules cached per principal
            synchronized (allRulesPerPrincipal) {
                Collection<PrincipalRule> allRules = allRulesPerPrincipal.get(principalName);
                if (allRules != null) {
                    allRules.remove(rule);
                }
            }
        }
    }

    /**
     * Evict cached profiler rule information for principal.
     *
     * @param principalName cached profiler rule key
     * @param force         force eviction of guest and default rules
     */
    private void evictCachedPrincipalRules(String principalName, boolean force) {
        if (principalName != null) {
            // do not evict guest and default principal profiler rules by
            // default since they would be evicted all too frequently
            if (force || (!principalName.equals(getGuestPrincipalName()) && !principalName.equals(DEFAULT_RULE_PRINCIPAL_NAME))) {
                // evict cached profiler rules
                Collection<PrincipalRule> rules = allRulesPerPrincipal.remove(principalName);
                if (rules != null) {
                    rulesPerPrincipal.remove(principalName);
                    for (PrincipalRule rule : rules) {
                        String key = this.makePrincipalRuleKey(rule.getPrincipalName(), rule.getLocatorName());
                        principalRules.remove(key);
                    }
                }
            }
        }
    }

    /**
     * ProfilerContext
     * <p/>
     * Class used to track session lifetime within profiler implementation
     * so that cached profiler rule information per principal can be evicted
     * from cache on session end. Note that this serializable class must be
     * static to prevent owning JetspeedProfilerImpl from being persisted
     * in the session.
     */
    public static class ProfilerContext implements HttpSessionActivationListener, HttpSessionBindingListener, Serializable {
        private static final long serialVersionUID = 1L;

        private transient JetspeedProfilerImpl profiler;
        private transient Principal principal;
        private transient boolean guestPrincipal;

        /**
         * Construct new profiler context with specified principal.
         *
         * @param profiler  profiler implementation
         * @param principal profiler context principal
         */
        private ProfilerContext(JetspeedProfilerImpl profiler, Principal principal) {
            this.profiler = profiler;
            this.principal = principal;
            this.guestPrincipal = ((principal != null) && principal.getName().equals(profiler.getGuestPrincipalName()));
        }

        /**
         * Notification that the session has just been activated.
         *
         * @param event session activation event
         */
        public void sessionDidActivate(HttpSessionEvent event) {
        }

        /**
         * Notification that the session is about to be passivated.
         *
         * @param event session activation event
         */
        public void sessionWillPassivate(HttpSessionEvent event) {
            evictPrincipal();
        }

        /**
         * Notifies this context that it is being bound to
         * a session and identifies the session.
         *
         * @param event session binding event
         */
        public void valueBound(HttpSessionBindingEvent event) {
        }

        /**
         * Notifies this context that it is being unbound
         * from a session and identifies the session.
         *
         * @param event session binding event
         */
        public void valueUnbound(HttpSessionBindingEvent event) {
            evictPrincipal();
        }

        /**
         * Evict cached principal rules.
         */
        private void evictPrincipal() {
            // profiler can be null for reactivated sessions
            if (profiler != null) {
                // evict cached principal rules on session end
                if ((principal != null) && !guestPrincipal) {
                    profiler.evictCachedPrincipalRules(principal.getName(), false);
                }
                principal = null;

                // evict default and guest principal rules periodically
                long now = System.currentTimeMillis();
                if (now - profiler.lastDefaultGuestReapTime > DEFAULT_AND_GUEST_RULE_REAPING_INTERVAL) {
                    profiler.lastDefaultGuestReapTime = now;
                    profiler.evictCachedPrincipalRules(profiler.getGuestPrincipalName(), true);
                    profiler.evictCachedPrincipalRules(DEFAULT_RULE_PRINCIPAL_NAME, true);
                }
            }
        }

        /**
         * Get context principal.
         *
         * @return context principal
         */
        private Principal getPrincipal() {
            return principal;
        }
    }

    /**
     * Get configured guest principal name.
     *
     * @return guest principal name
     */
    private String getGuestPrincipalName() {
        // lazily access configured guest principal name 
        if (guestPrincipalName == null) {
            guestPrincipalName = DEFAULT_GUEST_PRINCIPAL_NAME;
            PortalConfiguration config = Jetspeed.getConfiguration();
            if (config != null) {
                String configGuestPrincipalName = config.getString("default.user.principal");
                if (configGuestPrincipalName != null) {
                    guestPrincipalName = configGuestPrincipalName;
                }
            }
        }
        return guestPrincipalName;
    }
}
