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

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;

/**
 * Hostname Resolver
 * 
 * @author <a href="mailto:stalherm@goodgulf.net">Frank Stalherm</a>
 * @version $Id$
 */
public class HostnameCriterionResolver extends StandardResolver implements
        RuleCriterionResolver
{
    private static final long serialVersionUID = 1L;
    
    private boolean useDotPrefix;
    private List<Rule> hostnameMappingRules;
    
    public HostnameCriterionResolver(boolean usePrefix)
    {
        super();
        this.useDotPrefix = usePrefix;
    }

    public HostnameCriterionResolver(List<Rule> hostnameMappingRules)
    {
        super();
        this.hostnameMappingRules = hostnameMappingRules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.rules.impl.StandardResolver#isControl(org.apache.jetspeed.profiler.rules.RuleCriterion)
     */
    public boolean isControl(RuleCriterion criterion)
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.rules.impl.StandardResolver#isNavigation(org.apache.jetspeed.profiler.rules.RuleCriterion)
     */
    public boolean isNavigation(RuleCriterion criterion)
    {
        return false;
    }

    public String resolve(RequestContext context, RuleCriterion criterion)
    {
        String serverName = context.getRequest().getServerName();        
        if (useDotPrefix)
        {
            int idx = serverName.indexOf(".");
            if (idx != -1)
            {
                // SUFFIX: hostname = servername.substring(idx + 1, servername.length());
                serverName = serverName.substring(0, idx);
            }
        }
        else if (hostnameMappingRules != null)
        {
            for (Rule rule : hostnameMappingRules)
            {
                serverName = rule.map(serverName);
            }
        }
        return serverName;
    }

    public static class Rule implements Serializable
    {
        private static final long serialVersionUID = 1L;
        
        private String replacement;
        private Pattern compiledPattern;        
        
        public Rule(String pattern, String replacement)
        {
            this.replacement = replacement;
            this.compiledPattern = Pattern.compile(pattern);
        }
        
        public String map(String hostname)
        {
            Matcher patternMatcher = compiledPattern.matcher(hostname);
            
            if (patternMatcher.find())
            {
                return patternMatcher.replaceAll(replacement);
            }
            
            return hostname;
        }
    }
}
