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
package org.apache.jetspeed.profiler;

import java.security.Principal;
import java.util.Collection;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.om.desktop.Desktop;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.request.RequestContext;

/**
 * Profiler
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class Profiler
{
    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    public static ProfilerService getService()
    {
        return (ProfilerService)CommonPortletServices
            .getInstance().getService(ProfilerService.SERVICE_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getProfile(org.apache.jetspeed.request.RequestContext)
     */
    public static ProfileLocator getProfile(RequestContext context)
        throws ProfilerException
    {
        return getService().getProfile(context);
    }    

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getDefaultRule()
     */
    public static ProfilingRule getDefaultRule()
    {
        return getService().getDefaultRule();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getRuleForPrincipal(java.security.Principal)
     */
    public static ProfilingRule getRuleForPrincipal(Principal principal)
    {
        return getService().getRuleForPrincipal(principal);
    }    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getDesktop(org.apache.jetspeed.profiler.ProfileLocator)
     */
    public static Desktop getDesktop(ProfileLocator locator)
    {
        return getService().getDesktop(locator);    
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getPage(org.apache.jetspeed.profiler.ProfileLocator)
     */
    public static Page getPage(ProfileLocator locator)
    {
        return getService().getPage(locator);
    }    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getFragment(org.apache.jetspeed.profiler.ProfileLocator)
     */
    public static Fragment getFragment(ProfileLocator locator)
    {
        return getService().getFragment(locator);        
    }    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#createLocator()
     */
     public static ProfileLocator createLocator()
     {
         return getService().createLocator();
     }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getRules()
     */
    public static Collection getRules()
    {
        return getService().getRules();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.profiler.ProfilerService#getAnonymousUser()
     */
    public static String getAnonymousUser()
    {
        return getService().getAnonymousUser();
    }
    
}
