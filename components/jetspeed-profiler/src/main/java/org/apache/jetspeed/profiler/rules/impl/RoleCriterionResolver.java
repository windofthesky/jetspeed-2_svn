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

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.Role;

/**
 * Standard Jetspeed-1 Role resolver (not role-based fallback).
 * It first looks at the value in the criterion record.
 * If it is null, it then falls back to a request parameter.
 * If it is null it gives up and returns null allowing subclasses
 * to continue processing.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RoleCriterionResolver
    extends StandardResolver
    implements RuleCriterionResolver
{
    protected final static Log log = LogFactory.getLog(UserCriterionResolver.class);
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#resolve(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.rules.RuleCriterion)
     */    
    public String resolve(RequestContext context, RuleCriterion criterion)
    {
        String value = super.resolve(context, criterion);
        if (value != null)
        {
            return value;
        }
            
        Subject subject = context.getSubject();
        if (subject == null)
        {
            String msg = "Invalid (null) Subject in request pipeline";
            log.error(msg);
            return null;
        }
            
        return resolvePrincipals(context, criterion, subject, Role.class);
     }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#isControl()
     */
    public boolean isControl(RuleCriterion criterion)
    {
        return true;
    }
    
    
}
