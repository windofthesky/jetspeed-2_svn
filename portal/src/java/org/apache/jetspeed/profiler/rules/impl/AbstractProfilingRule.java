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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfilerService;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;

/**
 * ProfilingRuleImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractProfilingRule implements ProfilingRule
{
    protected Collection criteria = new Vector();
    protected String id;
    protected String title;
    protected String ojbConcreteClass;

    protected static final String DEFAULT_RESOLVER = "AbstractProfilingRule.default";
    
    /** Map of profile locators kept around for reuse TODO: evict entries after max size reached */    
    static Map locators = new HashMap();
    
    /** Map of resolver rules for criteria. The map goes from criterion name to resolver class */
    static Map resolvers = new HashMap();
    
    static 
    {
        RuleCriterionResolver standardResolver = new StandardResolver();
        resolvers.put(DEFAULT_RESOLVER, standardResolver);
        resolvers.put(ProfilingRule.STANDARD_DESKTOP, standardResolver);        
        resolvers.put(ProfilingRule.STANDARD_PAGE, standardResolver);
        resolvers.put(ProfilingRule.STANDARD_USER, new UserCriterionResolver());
        resolvers.put(ProfilingRule.STANDARD_ROLE, new RoleCriterionResolver()); 
        resolvers.put(ProfilingRule.STANDARD_GROUP, new GroupCriterionResolver());          
        resolvers.put(ProfilingRule.STANDARD_MEDIATYPE, new MediatypeCriterionResolver());
        resolvers.put(ProfilingRule.STANDARD_LANGUAGE, new LanguageCriterionResolver());
        resolvers.put(ProfilingRule.STANDARD_COUNTRY, new CountryCriterionResolver());
        resolvers.put(ProfilingRule.STANDARD_GROUP_ROLE_USER, new GroupRoleUserCriterionResolver());                         
    }
    
    protected ProfileLocator getLocatorFromCache(String key)
    {
        return (ProfileLocator)locators.get(key);
    }
    
    
    protected void addLocatorToCache(String key, ProfileLocator locator)
    {
        locators.put(key, locator);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getResolver(java.lang.String)
     */
    public RuleCriterionResolver getResolver(String name)
    {
        return (RuleCriterionResolver)resolvers.get(name);
    }

    public RuleCriterionResolver getDefaultResolver()
    {
        return (RuleCriterionResolver)resolvers.get(DEFAULT_RESOLVER);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#apply(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.profiler.ProfilerService)
     */
    public abstract ProfileLocator apply(RequestContext context, ProfilerService service);
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getRuleCriterion()
     */
    public Collection getRuleCriteria()
    {
        return criteria;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getId()
     */
    public String getId()
    {
        return this.id;    
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#setId(java.lang.String)
     */
    public void setId(String id)
    {
        this.id = id;
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getTitle()
     */
    public String getTitle()
    {
        return this.title;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        this.title = title;                        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#getClassname()
     */
    public String getClassname()
    {
        return this.ojbConcreteClass;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.rules.ProfilingRule#setClassname(java.lang.String)
     */
    public void setClassname(String classname)
    {
        this.ojbConcreteClass = classname;
    }
    
    public String toString()
    {
        if (id != null)
        {
            return id;
        }
        else if (title != null)
        {
            return title;
        }
        return this.getClass().toString();
    }
    
}
