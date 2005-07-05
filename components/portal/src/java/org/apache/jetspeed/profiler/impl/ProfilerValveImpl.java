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
package org.apache.jetspeed.profiler.impl;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.page.psml.ContentPageImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.PageProfilerValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfiledPageContext;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * ProfilerValveImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: ProfilerValveImpl.java 202324 2005-06-29 07:12:27Z taylor $
 */
public class ProfilerValveImpl extends AbstractValve implements PageProfilerValve
{
    protected Log log = LogFactory.getLog(ProfilerValveImpl.class);   

    public static final String PROFILED_PAGE_CONTEXT_ATTR_KEY = "org.apache.jetspeed.profiledPageContext";

    private Profiler profiler;
    private PageManager pageManager;
   

    public ProfilerValveImpl( Profiler profiler, PageManager pageManager )
    {
        this.profiler = profiler;
        this.pageManager = pageManager;
    }
    
 
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {
        try
        {
            // get profiler locators for request subject/principal using the profiler
            Subject subject = request.getSubject();
            if (subject == null)
            {
                throw new ProfilerException("Missing subject for request: " + request.getPath());
            }            
            Principal principal = SecurityHelper.getBestPrincipal(subject, UserPrincipal.class);
            if (principal == null)
            {
                throw new ProfilerException("Missing principal for request: " + request.getPath());
            }
            
            Map locators = null;
            String locatorName = (String)request.getAttribute(PROFILE_LOCATOR_REQUEST_ATTR_KEY);
            if ( locatorName != null )
            {
                ProfileLocator locator = profiler.getProfile(request,locatorName);
                if ( locator != null )
                {
                    locators = new HashMap();
                    locators.put(ProfileLocator.PAGE_LOCATOR, locator);
                }
            }
            
            if ( locators ==  null )
            {
                // get all locators for the current user
                locators = profiler.getProfileLocators(request, principal);
            }

            if (locators.size() == 0)
            {
                locators = profiler.getDefaultProfileLocators(request);                
            }
            
            if (locators.size() == 0)
            {
                locators.put(ProfileLocator.PAGE_LOCATOR, profiler.getProfile(request, ProfileLocator.PAGE_LOCATOR));
            }
            
            // get profiled page context using the profiler and page manager
            ProfiledPageContext profiledPageContext = profiler.createProfiledPageContext(locators);
            pageManager.computeProfiledPageContext(profiledPageContext);
            if (profiledPageContext.getPage() == null)
            {
                throw new NodeNotFoundException("Unable to profile request: " + request.getPath());
            }
            
            // set request page and profile locator
            request.setPage(new ContentPageImpl(profiledPageContext.getPage()));
            request.setProfileLocators(profiledPageContext.getLocators());

            // return profiled page context in request attribute
            HttpServletRequest httpRequest = request.getRequest();
            httpRequest.setAttribute(PROFILED_PAGE_CONTEXT_ATTR_KEY, profiledPageContext);

            // continue
            context.invokeNext(request);
        }
        catch (SecurityException se)
        {
            if (request.getRequest().getUserPrincipal() == null &&
                request.getPath() != null &&
                !request.getPath().equals("/"))
            {
                try 
                {
                    request.getResponse().sendRedirect(request.getRequest().getContextPath());
                }
                catch (IOException ioe){}
                return;
            }
            log.error(se.getMessage(), se);
            try
            {                
                request.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN, se.getMessage());
            }
            catch (IOException ioe)
            {
                log.error("Failed to invoke HttpServletReponse.sendError: " + ioe.getMessage(), ioe);
            }
        }
        catch (NodeNotFoundException nnfe)
        {
            log.error(nnfe.getMessage(), nnfe);
            try
            {
                request.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND, nnfe.getMessage());
            }
            catch (IOException ioe)
            {
                log.error("Failed to invoke HttpServletReponse.sendError: " + ioe.getMessage(), ioe);
            }
        }
        catch (Exception e)
        {
            log.error("Exception in request pipeline: " + e.getMessage(), e);
            throw new PipelineException(e.toString(), e);
        }
    }

    public String toString()
    {
        return "ProfilerValve";
    }

}
