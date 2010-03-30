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
package org.apache.jetspeed.pipeline.valve.impl;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.decoration.PageActionAccess;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.BaseConcretePageElement;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.PageProfilerValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.portalsite.PortalSite;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract page valve common implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class AbstractPageValveImpl extends AbstractValve implements PageProfilerValve
{
    protected Logger log = LoggerFactory.getLogger(AbstractPageValveImpl.class);   

    /**
     * portalSite - portal site component
     */
    protected PortalSite portalSite;

    /**
     * requestFallback - flag indicating whether request should fallback to closest
     *                   intermediate or root folder if locators do not select a page
     *                   or access is forbidden; if set, forbidden and not found
     *                   response status codes are avoided if at all possible: do not
     *                   set if 403s and 404s are expected to be returned by the portal
     */
    protected boolean requestFallback;

    /**
     * useHistory - flag indicating whether to use visited page
     *              history to select default page per site folder
     */
    protected boolean useHistory;
    
    /**
     * pageLayoutComponent - component used to construct and maintain ContentPage from
     *                       profiled PSML Pages and Fragments.
     */
    protected PageLayoutComponent pageLayoutComponent;

    /**
     * AbstractPageValveImpl - constructor
     *
     * @param portalSite portal site component reference
     * @param pageLayoutComponent page layout component reference
     * @param requestFallback flag to enable root folder fallback
     * @param useHistory flag to enable selection of last visited folder page
     */
    public AbstractPageValveImpl(PortalSite portalSite, PageLayoutComponent pageLayoutComponent,
                                 boolean requestFallback, boolean useHistory)
    {
        this.portalSite = portalSite;
        this.pageLayoutComponent = pageLayoutComponent;
        this.requestFallback = requestFallback;
        this.useHistory = useHistory;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.pipeline.valve.AbstractValve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try
        {
            // get request path: first check "override" or custom "mapped" path through PATH_ATTRIBUTE request attribute
            String requestPath = (String)request.getAttribute(PortalReservedParameters.PATH_ATTRIBUTE);
            if (requestPath == null)
            {
                requestPath = request.getPath();
            }
            if ((requestPath != null) && (requestPath.length() > 0))
            {
                if (!requestPath.startsWith(Folder.PATH_SEPARATOR))
                {
                    requestPath = Folder.PATH_SEPARATOR+requestPath;
                }
            }
            else
            {
                requestPath = Folder.PATH_SEPARATOR;
            }
            if (log.isDebugEnabled())
            {
                log.debug("Request path: "+requestPath);
            }

            // get request subject/principal
            Subject requestSubject = request.getSubject();
            if (requestSubject == null)
            {
                throw new ProfilerException("Missing subject for request: " + requestPath);
            }            
            Principal requestUserPrincipal = SubjectHelper.getBestPrincipal(requestSubject, User.class);
            if (requestUserPrincipal == null)
            {
                throw new ProfilerException("Missing principal for request: " + requestPath);
            }

            // get request page and set page action access
            setRequestPage(request, requestPath, requestUserPrincipal);
            ContentPage requestPage = request.getPage();
            if (requestPage != null)
            {
                request.setAttribute(PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE, getPageActionAccess(request));
                
                if (log.isDebugEnabled())
                {
                    log.debug("Page path: "+requestPage.getPath());
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("No page found for request path: "+requestPath);
                }
            }

            // continue
            if (context != null)
            {
                context.invokeNext(request);
            }
        }
        catch (SecurityException se)
        {
            // fallback to root folder/default page
            if (requestFallback)
            {
                // fallback to portal root folder/default page if
                // no user is available and request path is not
                // already attempting to access the root folder;
                // this is rarely the case since the anonymous
                // user is normally defined unless the default
                // security system has been replaced/overridden
                if ((request.getRequest().getUserPrincipal() == null) && (request.getPath() != null) && !request.getPath().equals("/"))
                {
                    try 
                    {
                        request.getResponse().sendRedirect(request.getRequest().getContextPath());
                    }
                    catch (IOException ioe)
                    {
                    }
                    return;
                }
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
     * Set request page and associated session and request attributes
     * based on request and derived valve implementations.
     * 
     * @param request invoked request
     * @param requestPath invoked request path
     * @param requestUserPrincipal invoked request user principal
     */
    protected abstract void setRequestPage(RequestContext request, String requestPath, Principal requestUserPrincipal) throws NodeNotFoundException, ProfilerException;

    /**
     * Set request page and associated session and request attributes
     * based on portal site request context constructed by derived valve
     * implementations.
     * 
     * @param request invoked request
     * @param requestPath invoked request path
     * @param requestContext portal site request context
     */
    protected void setRequestPage(RequestContext request, String requestPath, PortalSiteRequestContext requestContext) throws NodeNotFoundException
    {
        // save request context
        request.setAttribute(PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY, requestContext);

        // additionally save request context under legacy key
        // to support existing decorator access
        request.setAttribute(PROFILED_PAGE_CONTEXT_ATTR_KEY, requestContext);

        // get request page from portal site request context;
        // accessing the request context here and in subsequent
        // valves/decorators latently selects the page and builds
        // menus from the user site view; the managed page accessed
        // here is the raw selected page as returned by the
        // page manager component; accessing the managed page here
        // selects the current page for the request
        BaseFragmentsElement managedPageOrTemplate = requestContext.getManagedPageOrTemplate();
        PageTemplate managedPageTemplate = requestContext.getManagedPageTemplate();
        Map managedFragmentDefinitions = requestContext.getManagedFragmentDefinitions();
        ContentPage contentPage = pageLayoutComponent.newContentPage(managedPageOrTemplate, managedPageTemplate, managedFragmentDefinitions);
        request.setPage(contentPage);
        request.setProfileLocators(requestContext.getLocators());
        
        // save original request for down stream content portlets
        if (requestContext.isContentPage())
        {
            request.setAttribute(PortalReservedParameters.PATH_ATTRIBUTE, requestPath);
            request.setAttribute(PortalReservedParameters.CONTENT_PATH_ATTRIBUTE, requestContext.getPageContentPath());
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
        ContentPage page = requestContext.getPage();
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
}
