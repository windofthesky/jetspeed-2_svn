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

import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfileResolvers;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;

/**
 * RoleFallbackProfilingRule
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RoleFallbackProfilingRule
    extends AbstractProfilingRule
    implements ProfilingRule
{
    protected final static Log log = LogFactory.getLog(RoleFallbackProfilingRule.class);
    private final static long serialVersionUID = 1L;
    
    public RoleFallbackProfilingRule()
    {     
        this.setClassname(this.getClass().getName());

    }
    
    public RoleFallbackProfilingRule(ProfileResolvers resolvers) 
    {
        super(resolvers);
        this.setClassname(this.getClass().getName());
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#apply(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.Profiler)
     */
    public ProfileLocator apply(RequestContext context, Profiler service)
    {
        StringBuffer key = new StringBuffer();
        int count = 0;
        
        // first pass, build the key
        Iterator criteria = this.getRuleCriteria().iterator();
        while (criteria.hasNext())
        {
            RuleCriterion criterion = (RuleCriterion)criteria.next();
            if (criterion.getType() == null)
            {
                log.warn("Invalid criterion provided - type null on rule " + this);
            }
            RuleCriterionResolver resolver = getResolver(criterion.getType());
            if (resolver == null)
            {
                resolver = getDefaultResolver();
            }
            String value = resolver.resolve(context, criterion);
            if (value != null && (resolver instanceof RoleCriterionResolver ||
                resolver instanceof GroupCriterionResolver))
            {
                StringTokenizer tokenizer = new StringTokenizer(value, StandardResolver.VALUE_DELIMITER);
                while (tokenizer.hasMoreTokens())
                {
                    String token = (String)tokenizer.nextToken();
                    key.append(criterion.getName());
                    key.append(ProfileLocator.PATH_SEPARATOR);
                    key.append(token);
                    if (tokenizer.hasMoreTokens())
                    {
                        key.append(ProfileLocator.PATH_SEPARATOR);
                    }
                }
            }
            else
            {
                key.append(criterion.getName());
                key.append(ProfileLocator.PATH_SEPARATOR);
                key.append(value);
            }
            if (criteria.hasNext())
            {
                key.append(ProfileLocator.PATH_SEPARATOR);
            }
            count++;                                                                                                    
        }
        // try to get the profile locator from the cache,
        // request path and key sufficient to generate unique key
        String requestPath = context.getPath();
        String locatorKey = ((requestPath != null) ? requestPath : "/") + ProfileLocator.PATH_SEPARATOR + key.toString();
        ProfileLocator locator = getLocatorFromCache(locatorKey); 
        if (locator != null)
        {
            return locator;
        }
        
        // second pass, build the locator object         
        locator = service.createLocator(context);
        criteria = this.getRuleCriteria().iterator();
        while (criteria.hasNext())
        {
            RuleCriterion criterion = (RuleCriterion)criteria.next();
            if (criterion.getType() == null)
            {
                log.warn("Invalid criterion provided - name or type null on rule " + this);
            }
            RuleCriterionResolver resolver = getResolver(criterion.getType());
            if (resolver != null)
            {
                String value = resolver.resolve(context, criterion);
                boolean isControl = resolver.isControl(criterion);
                boolean isNavigation = resolver.isNavigation(criterion);
                if (value != null && (resolver instanceof RoleCriterionResolver ||
                        resolver instanceof GroupCriterionResolver))
                    {
                        StringTokenizer tokenizer = new StringTokenizer(value, StandardResolver.VALUE_DELIMITER);
                        while (tokenizer.hasMoreTokens())
                        {
                            String token = (String)tokenizer.nextToken();
                            locator.add(criterion, isControl, isNavigation, token);
                        }
                    }
                    else
                    {
                        locator.add(criterion, isControl, isNavigation, value);
                    }
            }                
        }               
             
        addLocatorToCache(locatorKey, locator);
        return locator; 
        
    }
}
