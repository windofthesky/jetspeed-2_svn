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
package org.apache.jetspeed.profiler.rules;

import java.util.Map;

/**
 * Holds the mapping of resolver names to criterion resolvers for building profiling rules. This component is configured
 * in the Jetspeed configuration
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PrincipalRule.java 188415 2005-03-23 22:15:25Z ate $
 */
public interface ProfileResolvers 
{
    /**
     * Lookup a resolver for a given resolver name
     *
     * @param resolverName the name of the resolver to lookup
     * @return the found resolver or if not found, null
     */
    RuleCriterionResolver get(String resolverName);

    /**
     * Returns a representation of all resolvers and their associated names in a map
     *
     * @return the map of resolver names mapped to resolvers
     */
    Map<String,RuleCriterionResolver> getResolvers();
}
