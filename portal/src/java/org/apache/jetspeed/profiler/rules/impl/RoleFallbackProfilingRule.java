/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.profiler.rules.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfilerService;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;

/**
 * RoleFallbackProfilingRule
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RoleFallbackProfilingRule
    extends AbstractProfilingRule
    implements ProfilingRule
{
    protected final static Log log = LogFactory.getLog(RoleFallbackProfilingRule.class);
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#apply(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.ProfilerService)
     */
    public ProfileLocator apply(RequestContext context, ProfilerService service)
    {
        StringBuffer key = new StringBuffer();
        int count = 0;
        
        // first pass, build the key
        Iterator criteria = this.getRuleCriteria().iterator();
        while (criteria.hasNext())
        {
            RuleCriterion criterion = (RuleCriterion)criteria.next();
            if (criterion.getType() == null)
            {
                log.warn("Invalid criterion provided - type null on rule " + this);
            }
            RuleCriterionResolver resolver = getResolver(criterion.getType());
            if (resolver == null)
            {
                resolver = getDefaultResolver();
            }
            String value = resolver.resolve(context, criterion);
            key.append(criterion.getName());
            key.append(ProfileLocator.PATH_SEPARATOR);
            key.append(value);
            if (criteria.hasNext())
            {
                key.append(ProfileLocator.PATH_SEPARATOR);
            }
            count++;                                                                                                    
        }
        // try to get the profile locator from the cache        
        String locatorKey = key.toString();
        ProfileLocator locator = getLocatorFromCache(locatorKey); 
        if (locator != null)
        {
            return locator;
        }
        
        // second pass, build the locator object         
        locator = service.createLocator();
        criteria = this.getRuleCriteria().iterator();
        while (criteria.hasNext())
        {
            RuleCriterion criterion = (RuleCriterion)criteria.next();
            if (criterion.getType() == null)
            {
                log.warn("Invalid criterion provided - name or type null on rule " + this);
            }
            RuleCriterionResolver resolver = getResolver(criterion.getType());
            if (resolver != null)
            {
                String value = resolver.resolve(context, criterion);
                locator.add(criterion, value);
            }                
        }               
             
        addLocatorToCache(locatorKey, locator);
        return locator; 
        
    }
}
