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

import java.io.Serializable;
import java.util.Map;

import org.apache.jetspeed.profiler.rules.ProfileResolvers;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;

/**
 * Profile Resolvers 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class ProfileResolversImpl implements ProfileResolvers, Serializable
{
    private Map resolvers;
    
    public ProfileResolversImpl(Map resolvers)
    {
        this.resolvers = resolvers;
    }
    
    public RuleCriterionResolver get(String resolverName)
    {
        return (RuleCriterionResolver)resolvers.get(resolverName);
    }
    
    /**
     * return the map of resolver
     */
    public Map getResolvers()
    {
    	return resolvers;
    }
}
