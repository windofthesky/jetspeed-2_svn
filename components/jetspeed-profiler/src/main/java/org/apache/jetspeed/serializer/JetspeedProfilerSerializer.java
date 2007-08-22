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
package org.apache.jetspeed.serializer;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.security.BasePrincipal;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.serializer.objects.JSPrincipalRule;
import org.apache.jetspeed.serializer.objects.JSPrincipalRules;
import org.apache.jetspeed.serializer.objects.JSProfilingRule;
import org.apache.jetspeed.serializer.objects.JSProfilingRules;
import org.apache.jetspeed.serializer.objects.JSRuleCriterion;
import org.apache.jetspeed.serializer.objects.JSRuleCriterions;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.serializer.objects.JSUser;

/**
 * JetspeedProfilerSerializer - Profiler component serializer
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedProfilerSerializer extends AbstractJetspeedComponentSerializer
{
    protected Profiler pm;

    protected UserManager userManager;

    public JetspeedProfilerSerializer(Profiler pm, UserManager userManager)
    {
        this.pm = pm;
        this.userManager = userManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processExport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processExport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_PROFILER))
        {
            log.info("collecting profiling rules and user profiling rules");
            exportProfilingRules(snapshot, settings, log);
            exportUserPrincipalRules(snapshot, settings, log);
        }
    }

    protected void deleteData(Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_PROFILER))
        {
            log.info("deleting profiling rules and user profiling rules");

            try
            {
                String anonymousUser = userManager.getAnonymousUser();
                Iterator _itUsers = userManager.getUsers("");
                while (_itUsers.hasNext())
                {
                    User _user = (User) _itUsers.next();                    
                    Principal principal = SecurityHelper.getPrincipal(_user.getSubject(), UserPrincipal.class);
                    if (principal != null)
                    {
                        Collection col = pm.getRulesForPrincipal(principal);
                        Iterator _itCol = col.iterator();
                        while (_itCol.hasNext())
                        {
                            pm.deletePrincipalRule((PrincipalRule)_itCol.next());
                        }
                    }
                }
                Iterator _itRules = pm.getRules().iterator();
                while ( _itRules.hasNext() )
                {
                    pm.deleteProfilingRule((ProfilingRule)_itRules.next());
                }                
            }
            catch (Exception e)
            {
                throw new SerializerException(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processImport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processImport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_PROFILER))
        {
            log.info("creating profiling rules and user profiling rules");
            recreateProfilingRules(snapshot, settings, log);
            recreateUserPrincipalRules(snapshot, settings, log);
        }
    }

    private void recreateProfilingRules(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        log.debug("recreateProfilingRules - processing");
        JSProfilingRules rules = snapshot.getRules();
        if ((rules != null) && (rules.size() > 0))
        {
            Iterator _it = rules.iterator();
            while (_it.hasNext())
            {
                JSProfilingRule _c = (JSProfilingRule) _it.next();

                try
                {
                    ProfilingRule rule = null;

                    rule = pm.getRule(_c.getId());
                    if ((rule == null) || (isSettingSet(settings, JetspeedSerializer.KEY_OVERWRITE_EXISTING)))
                    {
                        rule = recreateRule(pm, rule, _c);
                        pm.storeProfilingRule(rule);
                    }
                }
                catch (Exception e)
                {
                    throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(
                            "org.apache.jetspeed.capabilities.Capabilities", e.getLocalizedMessage()));
                }
            }
            /** reset the default profiling rule */
            String defaultRuleID = snapshot.getDefaultRule();
            if (defaultRuleID != null)
            {
                ProfilingRule defaultRule = pm.getRule(defaultRuleID);
                if (defaultRule != null)
                    pm.setDefaultRule(defaultRuleID);
            }
        }
        else
            log.debug("NO PROFILING RULES?????");
        log.debug("recreateProfilingRules - done");
    }

    /**
     * called only after users have been established
     * 
     * @throws SerializerException
     */
    private void recreateUserPrincipalRules(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        log.debug("recreateUserPrincipalRules - started");

        // get Rules for each user

        Iterator _itUsers = snapshot.getUsers().iterator();
        while (_itUsers.hasNext())
        {
            JSUser _user = (JSUser) _itUsers.next();
            JSPrincipalRules jsRules = _user.getRules();
            try
            {
                User user = userManager.getUser(_user.getName());
                Principal principal = getUserPrincipal(user);
                if (jsRules != null)
                {
                    Iterator _itRoles = jsRules.iterator();
                    while (_itRoles.hasNext())
                    {
                        JSPrincipalRule pr = (JSPrincipalRule) _itRoles.next();
                        ProfilingRule pRule = pm.getRule(pr.getRule());

                        try
                        {
                            PrincipalRule p1 = pm.createPrincipalRule();
                            p1.setLocatorName(pr.getLocator());
                            p1.setProfilingRule(pRule);
                            p1.setPrincipalName(principal.getName());
                            pm.storePrincipalRule(p1);
                        }
                        catch (Exception eRole)
                        {
                            eRole.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception eUser)
            {
                eUser.printStackTrace();
            }
        }
        log.debug("recreateUserPrincipalRules - done");

    }

    /**
     * recreate a rule criterion object from the deserialized wrapper
     * 
     * @param profiler
     *            established profile manager
     * @param jsr
     *            deserialized object
     * @return new RuleCriterion with content set to deserialized wrapepr
     * @throws SerializerException
     */
    protected RuleCriterion recreateRuleCriterion(Profiler profiler, JSRuleCriterion jsr, ProfilingRule rule)
            throws SerializerException, ClassNotFoundException

    {
        try
        {

            RuleCriterion c = profiler.createRuleCriterion();
            if (c == null)
                throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(
                        "org.apache.jetspeed.profiler.rules.RuleCriterion", "returned null"));
            c.setFallbackOrder(jsr.getFallBackOrder());
            c.setFallbackType(jsr.getFallBackType());
            c.setName(jsr.getName());
            c.setType(jsr.getType());
            c.setValue(jsr.getValue());
            c.setRuleId(rule.getId());
            return c;
        }
        catch (Exception e)
        {
            SerializerException.CREATE_OBJECT_FAILED.create("org.apache.jetspeed.profiler.rules.RuleCriterion", e
                    .getLocalizedMessage());
            return null;
        }
    }

    /**
     * recreate a profiling rule object from the deserialized wrapper and store
     * it
     * 
     * @param profiler
     *            established profile manager
     * @param jsp
     * deserialized object @
     * @throws SerializerException,
     *             ClassNotFoundException, ProfilerException
     */
    protected ProfilingRule recreateRule(Profiler profiler, ProfilingRule existingRule, JSProfilingRule jsp)
            throws SerializerException, ClassNotFoundException, ProfilerException
    {
        ProfilingRule rule = null;
        boolean existing = false;

        if (existingRule == null)
        {
            rule = profiler.getRule(jsp.getId());
            if (jsp.isStandardRule())
                rule = profiler.createProfilingRule(true);
            else
                rule = profiler.createProfilingRule(false);
            rule.setId(jsp.getId());
        }
        else
        {
            rule = existingRule;
            existing = true;
        }

        rule.setTitle(jsp.getDescription());

        JSRuleCriterions col = jsp.getCriterions();

        Iterator _it = col.iterator();
        while (_it.hasNext())
        {
            RuleCriterion c = recreateRuleCriterion(profiler, (JSRuleCriterion) _it.next(), rule);
            if (c != null)
            {
                Collection cHelp = rule.getRuleCriteria();
                if ((existing) && (cHelp.contains(c)))
                    cHelp.remove(c); // remove existing duplicate
                cHelp.add(c); // add the current version back in
            }
        }
        return rule;

    }

    private Principal getUserPrincipal(User user)
    {
        Subject subject = user.getSubject();
        // get the user principal
        Set principals = subject.getPrincipals();
        Iterator list = principals.iterator();
        while (list.hasNext())
        {
            BasePrincipal principal = (BasePrincipal) list.next();
            String path = principal.getFullPath();
            if (path.startsWith("/user/"))
                return principal;
        }
        return null;
    }

    /**
     * read the permissions and then the profiling rules.
     * <p>
     * after that update the cross reference with the users
     * 
     * @throws SerializerException
     */
    private void exportProfilingRules(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        HashMap rulesMap = new HashMap();
        Class standardRuleClass = null;
        try
        {
            ProfilingRule tempStandardRule = pm.createProfilingRule(true);
            standardRuleClass = tempStandardRule.getClass();
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] {
                    "Standard Rule", e.getMessage() }));
        }

        Iterator list = null;
        try
        {
            list = pm.getRules().iterator();
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] {
                    "ProfilingRules", e.getMessage() }));
        }
        while (list.hasNext())
        {
            try
            {
                ProfilingRule p = (ProfilingRule) list.next();
                if (!(rulesMap.containsKey(p.getId())))
                {
                    JSProfilingRule rule = createProfilingRule(p, (standardRuleClass == p.getClass()));
                    rulesMap.put(rule.getId(), rule);
                    snapshot.getRules().add(rule);

                }
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "ProfilingRules", e.getMessage() }));
            }
        }

        // determine the defualt rule
        ProfilingRule defaultRule = pm.getDefaultRule();
        if (defaultRule != null)
            snapshot.setDefaultRule(defaultRule.getId());
    }

    private void exportUserPrincipalRules(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        // get Rules for each user

        Iterator _itUsers = snapshot.getUsers().iterator();
        while (_itUsers.hasNext())
        {
            JSUser _user = (JSUser) _itUsers.next();
            Principal principal = _user.getPrincipal();
            if (principal != null)
            {
                Collection col = pm.getRulesForPrincipal(principal);
                Iterator _itCol = col.iterator();
                while (_itCol.hasNext())
                {
                    PrincipalRule p1 = (PrincipalRule) _itCol.next();
                    JSPrincipalRule pr = new JSPrincipalRule(p1.getLocatorName(), p1.getProfilingRule().getId());
                    _user.getRules().add(pr);
                }
            }
        }
    }

    /**
     * Create the Profiling Rule Wrapper
     * 
     * @param p
     * @return
     */
    private JSProfilingRule createProfilingRule(ProfilingRule p, boolean standard)
    {
        JSProfilingRule rule = new JSProfilingRule();

        rule.setStandardRule(standard);
        rule.setDescription(p.getTitle());
        rule.setId(p.getId());

        Collection col = p.getRuleCriteria();
        Iterator keys = col.iterator();
        while (keys.hasNext())
        {
            RuleCriterion rc = (RuleCriterion) keys.next();
            rule.getCriterions().add(new JSRuleCriterion(rc));
        }
        return rule;

    }
}
