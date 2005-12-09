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

import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;

/**
 * Standard Jetspeed-1 Group/Role/User resolver.
 * First looking for a group request parameter, then a role request parameter,
 * then a user request parameter. If none are found, then it uses the 
 * current user's principal.
 * 
 * If it is null, it then falls back to a request parameter.
 * If it is null it gives up and returns null allowing subclasses
 * to continue processing.
 * 
 * Since there is no 1:1 value for a combination rule of group, the criterion's
 * value is ignored.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class GroupRoleUserCriterionResolver
    extends UserCriterionResolver
    implements RuleCriterionResolver
{    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#resolve(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.rules.RuleCriterion)
     */    
    public String resolve(RequestContext context, RuleCriterion criterion)
    {
        String value = context.getRequestParameter(ProfilingRule.STANDARD_GROUP);
        if (value != null)
        {
            criterion.setName(ProfilingRule.STANDARD_GROUP);
            return value;
        }
        value = context.getRequestParameter(ProfilingRule.STANDARD_ROLE);
        if (value != null)
        {
            criterion.setName(ProfilingRule.STANDARD_ROLE);            
            return value;
        }

        // use the User Criterion to resolve it        
        criterion.setName(ProfilingRule.STANDARD_USER);
        return super.resolve(context, criterion);
     }
    
    
    
}
