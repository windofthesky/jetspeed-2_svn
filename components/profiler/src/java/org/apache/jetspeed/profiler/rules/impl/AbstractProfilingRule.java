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
package org.apache.jetspeed.profiler.rules.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;

/**
 * ProfilingRuleImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractProfilingRule implements ProfilingRule
{
    protected Collection criteria = new Vector();
    protected String id;
    protected String title;
    protected String ojbConcreteClass;
    
    /** Map of profile locators kept around for reuse TODO: evict entries after max size reached */    
    static Map locators = new HashMap();
    
    /** Map of resolver rules for criteria. The map goes from criterion name to resolver class */
    static Map resolvers = new HashMap();
    
    static 
    {
        resolvers.put(RuleCriterionResolver.REQUEST, new StandardResolver());
        resolvers.put(RuleCriterionResolver.REQUEST_SESSION, new RequestSessionResolver());
        resolvers.put(RuleCriterionResolver.PATH, new PathResolver());        
        resolvers.put(RuleCriterionResolver.PATH_SESSION, new PathSessionResolver());                
        resolvers.put(RuleCriterionResolver.HARD_CODED, new HardCodedResolver());
        resolvers.put(RuleCriterionResolver.USER, new UserCriterionResolver());
        resolvers.put(RuleCriterionResolver.ROLE, new RoleCriterionResolver()); 
        resolvers.put(RuleCriterionResolver.GROUP, new GroupCriterionResolver());          
        resolvers.put(RuleCriterionResolver.MEDIATYPE, new MediatypeCriterionResolver());
        resolvers.put(RuleCriterionResolver.LANGUAGE, new LanguageCriterionResolver());
        resolvers.put(RuleCriterionResolver.COUNTRY, new CountryCriterionResolver());
        resolvers.put(RuleCriterionResolver.GROUP_ROLE_USER, new GroupRoleUserCriterionResolver());
        resolvers.put(RuleCriterionResolver.USER_ATTRIBUTE, new UserAttributeResolver());
    }
    
    protected ProfileLocator getLocatorFromCache(String key)
    {
        return (ProfileLocator)locators.get(key);
    }
    
    
    protected void addLocatorToCache(String key, ProfileLocator locator)
    {
        locators.put(key, locator);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getResolver(java.lang.String)
     */
    public RuleCriterionResolver getResolver(String name)
    {
        return (RuleCriterionResolver)resolvers.get(name);
    }

    public RuleCriterionResolver getDefaultResolver()
    {
        return (RuleCriterionResolver)resolvers.get(RuleCriterionResolver.REQUEST);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#apply(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.Profiler)
     */
    public abstract ProfileLocator apply(RequestContext context, Profiler service);
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getRuleCriterion()
     */
    public Collection getRuleCriteria()
    {
        return criteria;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getId()
     */
    public String getId()
    {
        return this.id;    
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#setId(java.lang.String)
     */
    public void setId(String id)
    {
        this.id = id;
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getTitle()
     */
    public String getTitle()
    {
        return this.title;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        this.title = title;                        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getClassname()
     */
    public String getClassname()
    {
        return this.ojbConcreteClass;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#setClassname(java.lang.String)
     */
    public void setClassname(String classname)
    {
        this.ojbConcreteClass = classname;
    }
    
    public String toString()
    {
        if (id != null)
        {
            return id;
        }
        else if (title != null)
        {
            return title;
        }
        return this.getClass().toString();
    }
    
}
