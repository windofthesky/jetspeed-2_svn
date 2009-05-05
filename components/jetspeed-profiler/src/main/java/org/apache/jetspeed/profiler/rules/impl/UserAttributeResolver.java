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

import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserSubjectPrincipal;

/**
 * Looks in the Portlet API User Attributes for given named attribute 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class UserAttributeResolver 
    extends 
        StandardResolver 
    implements
        RuleCriterionResolver
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#resolve(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.rules.RuleCriterion)
     */    
    public String resolve(RequestContext context, RuleCriterion criterion)
    {
        if (context.getUserPrincipal() instanceof User)
        {
            User user = (User)context.getUserPrincipal();
            if (user != null)
            {
                Map<String, String> map = user.getInfoMap();
                String attribute = (String)map.get(criterion.getName());
                if (attribute != null)
                {
                    return attribute;
                }
                return criterion.getValue();
            }
        }
        return null;
     }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#isControl()
     */
    public boolean isControl(RuleCriterion criterion)
    {
        return false;
    }

}
