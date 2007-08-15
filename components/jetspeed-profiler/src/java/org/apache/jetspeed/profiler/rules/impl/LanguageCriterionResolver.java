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

import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;

/**
 * LanguageCriterionResolver
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class LanguageCriterionResolver
    extends StandardResolver
    implements RuleCriterionResolver
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#resolve(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.rules.RuleCriterion)
     */    
    public String resolve(RequestContext context, RuleCriterion criterion)
    {
        // look for override
        String value = super.resolve(context, criterion);
        if (value != null)
        {
            return value.toLowerCase();
        }
     
        String language = context.getLocale().getLanguage();
        if (language != null)
            language = language.toLowerCase();
        return language;
    }    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#isControl()
     */
    public boolean isControl(RuleCriterion criterion)
    {
        return true;
    }
    
    
}
