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
package org.apache.jetspeed.profiler.rules.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfileResolvers;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;

/**
 * ProfilingRuleImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractProfilingRule implements ProfilingRule
{
    private static final long serialVersionUID = 1;    
    protected Collection<RuleCriterion> criteria = new RemovalAwareCollection();
    protected String id;
    protected String title;
    protected String ojbConcreteClass;
    
    /** Map of profile locators kept around for reuse TODO: evict entries after max size reached */    
    protected Map<String, ProfileLocator> locators = Collections.synchronizedMap(new HashMap<String, ProfileLocator>());
    
    /** Map of resolver rules for criteria. The map goes from criterion name to resolver class */
    protected ProfileResolvers resolvers;

    public AbstractProfilingRule()
    {        
    }
    
    public AbstractProfilingRule(ProfileResolvers resolvers) 
    {
        this.resolvers = resolvers;
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
        return resolvers.get(name);
    }

    public RuleCriterionResolver getDefaultResolver()
    {
        return resolvers.get(RuleCriterionResolver.REQUEST);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#apply(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.Profiler)
     */
    public abstract ProfileLocator apply(RequestContext context, Profiler service);
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getRuleCriterion()
     */
    public Collection<RuleCriterion> getRuleCriteria()
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
    
    /**
     * @return Returns the resolvers.
     */
    public ProfileResolvers getResolvers()
    {
        return resolvers;
    }
    /**
     * @param resolvers The resolvers to set.
     */
    public void setResolvers(ProfileResolvers resolvers)
    {
        this.resolvers = resolvers;
    }
    
    
    
    
}
