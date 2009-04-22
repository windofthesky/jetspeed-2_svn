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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.profiler.rules.FallbackCriterionResolver;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.request.RequestContext;

/**
 * Role combo resolver 
 * Combines all roles into one string
 * Example: roles = a,b,c
 * RoleCombo = a-b-c
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: RoleCriterionResolver.java 187756 2004-10-15 22:58:43Z ate $
 */
public class RoleComboCriterionResolver
    extends PrincipalCriterionResolver
    implements FallbackCriterionResolver
{
    private static final long serialVersionUID = 1L;
    protected final static Logger log = LoggerFactory.getLogger(RoleComboCriterionResolver.class);
    
    public String resolve(RequestContext context, RuleCriterion criterion)
    {
        Subject subject = context.getSubject();
        if (subject == null)
        {
            String msg = "Invalid (null) Subject in request pipeline";
            log.error(msg);
            return null;
        }
        return combinePrincipals(context, criterion, subject, criterion.getName());
     }
    
    public boolean isControl(RuleCriterion criterion)
    {
        return true;
    }
}
