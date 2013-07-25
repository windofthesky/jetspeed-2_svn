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
package org.apache.jetspeed.profiler.impl;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.pipeline.valve.impl.AbstractPageValveImpl;
import org.apache.jetspeed.portalsite.PortalSite;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.PortalSiteSessionContext;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.request.RequestContext;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * ProfilerValveImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class ProfilerValveImpl extends AbstractPageValveImpl
{
    /**
     * profiler - profiler component
     */
    private Profiler profiler;

    /**
     * ProfilerValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param pageLayoutComponent page layout component reference
     * @param requestFallback flag to enable root folder fallback
     * @param useHistory flag to enable selection of last visited folder page
     */
    public ProfilerValveImpl(Profiler profiler, PortalSite portalSite, PageLayoutComponent pageLayoutComponent, boolean requestFallback, boolean useHistory)
    {
        super(portalSite, pageLayoutComponent, requestFallback, useHistory);
        this.profiler = profiler;
    }

    /**
     * ProfilerValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param pageLayoutComponent page layout component reference
     * @param requestFallback flag to enable root folder fallback
     */
    public ProfilerValveImpl(Profiler profiler, PortalSite portalSite, PageLayoutComponent pageLayoutComponent, boolean requestFallback)
    {
        this(profiler, portalSite, pageLayoutComponent, requestFallback, true);
    }

    /**
     * ProfilerValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param pageLayoutComponent page layout component reference
     */
    public ProfilerValveImpl(Profiler profiler, PortalSite portalSite, PageLayoutComponent pageLayoutComponent)
    {
        this(profiler, portalSite, pageLayoutComponent, true, true);
    }

    /**
     * Set request page and associated session and request attributes
     * based on request and derived valve implementations.
     * 
     * @param request invoked request
     * @param requestPath invoked request path
     * @param requestUserPrincipal invoked request user principal
     */
    protected void setRequestPage(RequestContext request, String requestPath, Principal requestUserPrincipal) throws NodeNotFoundException, ProfilerException
    {
        // get request specific profile locators if required
        Map<String,ProfileLocator> locators = null;
        String locatorName = (String)request.getAttribute(PROFILE_LOCATOR_REQUEST_ATTR_KEY);
        if ( locatorName != null )
        {
            ProfileLocator locator = profiler.getProfile(request,locatorName);
            if ( locator != null )
            {
                locators = new HashMap<String,ProfileLocator>();
                locators.put(ProfileLocator.PAGE_LOCATOR, locator);
            }
        }

        // get specified or default locators for the current user,
        // falling back to global defaults and, if necessary, explicitly
        // fallback to 'page' profile locators
        if ( locators == null )
        {
            locators = profiler.getProfileLocators(request, requestUserPrincipal);
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
            // get or create portal site session context; the session
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

            // construct and save a new portal site request context
            // using session context, locators map, fallback, and
            // folder page histories; the request context uses the
            // locators to initialize or resets the session context if
            // locators have changed for this request; the request
            // context also acts as a short term request cache for the
            // selected page and built menus; however, creating the
            // request context here does not select the page or build
            // menus: that is done when the request context is
            // accessed subsequently
            String pipeline = request.getPipeline().getName();
            boolean forceReservedFoldersVisible = (pipeline.equals(PortalReservedParameters.CONFIG_PIPELINE_NAME) ||
                                                   pipeline.equals(PortalReservedParameters.DESKTOP_CONFIG_PIPELINE_NAME));
            PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, requestUserPrincipal.getName(), requestFallback, useHistory, forceReservedFoldersVisible, true);

            // save request context and set request page from portal
            // site request context
            setRequestPage(request, requestPath, requestContext);
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "ProfilerValve";
    }
}
