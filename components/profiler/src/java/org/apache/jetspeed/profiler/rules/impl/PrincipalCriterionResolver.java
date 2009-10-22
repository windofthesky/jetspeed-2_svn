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

import java.security.Principal;
import java.util.Iterator;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.profiler.rules.FallbackCriterionResolver;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;

/**
 * Fallback by Principal control resolver. Can be used with any principal type including role and group
 * It first looks at the value in the criterion record.
 * If it is null, it then falls back to a request parameter.
 * If it is null it gives up and returns null allowing subclasses
 * to continue processing.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PrincipalCriterionResolver.java 767444 2009-04-22 10:08:53Z ate $
 */
public class PrincipalCriterionResolver extends StandardResolver implements
        FallbackCriterionResolver
{
    private static final long serialVersionUID = 1L;
    protected final static Log log = LogFactory.getLog(PrincipalCriterionResolver.class);
    
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
        return resolvePrincipals(context, criterion, subject, criterion.getName());
     }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#isControl()
     */
    public boolean isControl(RuleCriterion criterion)
    {
        return true;
    }
    
    protected String resolvePrincipals(RequestContext context, RuleCriterion criterion, Subject subject, String jptName)
    {
        StringBuffer result = new StringBuffer();        
        Iterator principals = SecurityHelper.getPrincipals(subject, jptName).iterator();
        int count = 0;
        while (principals.hasNext())
        {
            Principal principal = (Principal)principals.next();
            if (count > 0)
            {
                result.append(VALUE_DELIMITER);
            }
            result.append(principal.getName());
            count++;
        }
        if (count == 0)
        {
            return null;
        }
        return result.toString();        
    }    
    
        
    protected String combinePrincipals(RequestContext context, RuleCriterion criterion, Subject subject, String jptName)
    {
        StringBuffer result = new StringBuffer();
        Iterator principals = SecurityHelper.getPrincipals(subject, jptName).iterator();
        int count = 0;
        while (principals.hasNext())
        {
            Principal principal = (Principal)principals.next();
            if (count > 0)
            {
                result.append(COMBO_DELIMITER);
            }
            result.append(principal.getName());
            count++;
        }
        if (count == 0)
        {
            return null;
        }
        return result.toString();        
    }
  
}
