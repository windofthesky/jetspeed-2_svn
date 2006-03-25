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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.decoration.PageActionAccess;
import org.apache.jetspeed.om.page.ContentPageImpl;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.PageProfilerValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.portalsite.PortalSite;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.PortalSiteSessionContext;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * ProfilerValveImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class ProfilerValveImpl extends AbstractValve implements PageProfilerValve
{
    protected Log log = LogFactory.getLog(ProfilerValveImpl.class);   

    /**
     * PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY - session portal site context attribute key
     */
    public static final String PORTAL_SITE_SESSION_CONTEXT_ATTR_KEY = "org.apache.jetspeed.portalsite.PortalSiteSessionContext";

    /**
     * PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY - request portal site context attribute key
     */
    public static final String PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY = "org.apache.jetspeed.portalsite.PortalSiteRequestContext";

    /**
     * PROFILED_PAGE_CONTEXT_ATTR_KEY - legacy request portal site context attribute key
     */
    public static final String PROFILED_PAGE_CONTEXT_ATTR_KEY = "org.apache.jetspeed.profiledPageContext";

    /**
     * session key for storing map of PageActionAccess instances
     */
    private static final String PAGE_ACTION_ACCESS_MAP_SESSION_ATTR_KEY = "org.apache.jetspeed.profiler.pageActionAccessMap";
    
    /**
     * profiler - profiler component
     */
    private Profiler profiler;

    /**
     * portalSite - portal site component
     */
    private PortalSite portalSite;

    /**
     * pageManager - page manager component
     */
    private PageManager pageManager;
   
    /**
     * requestFallback - flag indicating whether request should fallback to root folder
     *                   if locators do not select a page or access is forbidden
     */
    private boolean requestFallback;

    /**
     * ProfilerValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param pageManager page manager component reference
     * @param requestFallback flag to enable root folder fallback
     */
    public ProfilerValveImpl( Profiler profiler, PortalSite portalSite, PageManager pageManager, boolean requestFallback )
    {
        this.profiler = profiler;
        this.portalSite = portalSite;
        this.pageManager = pageManager;
        this.requestFallback = requestFallback;
    }

    public ProfilerValveImpl( Profiler profiler, PortalSite portalSite, PageManager pageManager)
    {
        this(profiler, portalSite, pageManager, true);
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
            
            // get request specific profile locators if required
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

            // get specified or default locators for the current user,
            // falling back to global defaults and, if necessary, explicity
            // fallback to 'page' profile locators
            if ( locators == null )
            {
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
            
            // get profiled page using the profiler, page manager,
            // and portal site components
            if (locators != null)
            {
                // get or create portalsite session context; the session
                // context maintains the user view of the site and is
                // searched against to locate the requested page and
                // used to build site menus from its extent; this is
                // cached in the session because locators seldom change
                // during the session so the session view of the site can
                // be cached unless locators do change; if the context
                // is invalid, (perhaps because the session was persisted
                // and is now being reloaded in a new server), it must be
                // replaced with a newly created session context
                PortalSiteSessionContext sessionContext = (PortalSiteSessionContext)request.getSessionAttribute(PORTAL_SITE_SESSION_CONTEXT_ATTR_KEY);
                if ((sessionContext == null) || !sessionContext.isValid())
                {
                    sessionContext = portalSite.newSessionContext();
                    request.setSessionAttribute(PORTAL_SITE_SESSION_CONTEXT_ATTR_KEY, sessionContext);
                }

                // construct and save a new portalsite request context
                // using session context, locators map, and fallback; the
                // request context uses the locators to initialize or resets
                // the session context if locators have changed for this
                // request; the request context also acts as a short term
                // request cache for the selected page and built menus;
                // however, creating the request context here does not
                // select the page or build menus: that is done when the
                // request context is accessed subsequently
                PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, requestFallback);
                request.setAttribute(PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY, requestContext);

                // additionally save request context under legacy key
                // to support existing decorator access
                request.setAttribute(PROFILED_PAGE_CONTEXT_ATTR_KEY, requestContext);

                // get profiled page from portalsite request context
                // and save profile locators map; accessing the request
                // context here and in subsequent valves/decorators
                // latently selects the page and builds menus from the
                // user site view using the request context locators;
                // the managed page accesed here is the raw selected page
                // as returned by the PageManager component; accessing
                // the managed page here selects the current page for the
                // request
                request.setPage(new ContentPageImpl(requestContext.getManagedPage()));
                request.setProfileLocators(requestContext.getLocators());
                
                request.setAttribute(PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE,getPageActionAccess(request));                
            }

            // continue
            context.invokeNext(request);
        }
        catch (SecurityException se)
        {
            // fallback to portal root folder/default page if
            // no user is available and request path is not
            // already attempting to access the root folder;
            // this is rarely the case since the anonymous
            // user is normally defined unless the default
            // security system has been replaced/overridden
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

            // return standard HTTP 403 - FORBIDDEN status
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
            // return standard HTTP 404 - NOT FOUND status
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

    /**
     * Returns the <code>PageActionAccess</code> for the current user request.
     * @see PageActionAccess
     * @param requestContext RequestContext of the current portal request.
     * @return PageActionAccess for the current user request.
     */
    protected PageActionAccess getPageActionAccess(RequestContext requestContext)
    { 
        Page page = requestContext.getPage();
        String key = page.getId();
        boolean loggedOn = requestContext.getRequest().getUserPrincipal() != null;
        boolean anonymous = !loggedOn;
        PageActionAccess pageActionAccess = null;

        Map sessionActions = null;
        synchronized (this)
        {
            sessionActions = (Map) requestContext.getSessionAttribute(PAGE_ACTION_ACCESS_MAP_SESSION_ATTR_KEY);
            if (sessionActions == null)
            {
                sessionActions = new HashMap();
                requestContext.setSessionAttribute(PAGE_ACTION_ACCESS_MAP_SESSION_ATTR_KEY, sessionActions);
            }
            else
            {
                pageActionAccess = (PageActionAccess) sessionActions.get(key);
            }
        }
        synchronized (sessionActions)
        {
            if (pageActionAccess == null)
            {
                pageActionAccess = new PageActionAccess(anonymous, page);
                sessionActions.put(key, pageActionAccess);
            }
            else
            {
                pageActionAccess.checkReset(anonymous, page);
            }        
        }
        
        return pageActionAccess;
    }

    public String toString()
    {
        return "ProfilerValve";
    }

}
