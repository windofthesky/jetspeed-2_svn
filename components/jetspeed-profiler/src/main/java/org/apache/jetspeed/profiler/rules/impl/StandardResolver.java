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
 * Standard Jetspeed-1 style resolver for criterion.
 * It first looks at the value in the request parameters.
 * If it is null, it then falls back to the criterion record..
 * If it is null it gives up and returns null allowing subclasses
 * to continue processing.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class StandardResolver implements RuleCriterionResolver
{
    private static final long serialVersionUID = 1L;
    public static final String VALUE_DELIMITER = ",";
    public static final String COMBO_DELIMITER = "-";
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#resolve(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.rules.RuleCriterion)
     */
    public String resolve(RequestContext context, RuleCriterion criterion)
    {
        String value = context.getRequestParameter(criterion.getName());
        if (value == null)
        {
            value = criterion.getValue();
        }
        return value;            
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#isControl()
     */
    public boolean isControl(RuleCriterion criterion)
    {
        if (criterion.getName().equals(RuleCriterionResolver.PATH) ||
            criterion.getName().equals(RuleCriterionResolver.PAGE))
        {
            return false;
        }
        return true;
    }

    public boolean isNavigation(RuleCriterion criterion)
    {
        return false;
    }    
}
