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
import org.apache.jetspeed.administration.AdminUtil;
import org.apache.jetspeed.decoration.PageActionAccess;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.BaseConcretePageElement;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;
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
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Valve locates the page from the portal request without profiling operations using a 1:1 URL:path location algorithm
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class PageValveImpl extends AbstractValve implements PageProfilerValve
{
    protected Logger log = LoggerFactory.getLogger(PageValveImpl.class);   

   
    /**
     * pageLayoutComponent - component used to construct and maintain ContentPage from
     *                       profiled PSML Pages and Fragments.
     */
    private PageLayoutComponent pageLayoutComponent;

    /**
     * 
     */
    private PageManager pageManager;
    
    public PageValveImpl(PageManager pageManager, PageLayoutComponent pageLayoutComponent)                            
    {
        this.pageManager = pageManager;
    	this.pageLayoutComponent = pageLayoutComponent;
    }

    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {
        try
        { 
            String requestPath = request.getPath();
            if (log.isDebugEnabled())
            {
                log.debug("Request path: "+requestPath);
            }
            if (requestPath == null)
            {
            	requestPath = Folder.PATH_SEPARATOR;
            }
            if (!requestPath.endsWith(Page.DOCUMENT_TYPE)) // FIXME: handle dynamic pages, pages not ending with .psml
            {
            		Folder folder = pageManager.getFolder(requestPath);
                	String defaultPage = folder.getDefaultPage();
                	if (defaultPage == null)
                	{
                		List<String> docs = folder.getDocumentOrder();
                		if (docs != null || docs.size() > 0)
                		{
                			for (String doc: docs)
                			{
                				if (doc.endsWith(Page.DOCUMENT_TYPE))
                				{
                					defaultPage = doc;
                					break;
                				}
                			}
                		}
                		if (defaultPage == null)
                			defaultPage = Folder.FALLBACK_DEFAULT_PAGE;
                	}
                	requestPath = AdminUtil.concatenatePaths(requestPath, defaultPage); 
            }
            Page page = pageManager.getPage(requestPath);
            
            // get profiler locators for request subject/principal using the profiler
            Subject subject = request.getSubject();
            if (subject == null)
            {
                throw new ProfilerException("Missing subject for request: " + requestPath);
            }            
            Principal principal = SubjectHelper.getBestPrincipal(subject, User.class);
            if (principal == null)
            {
                throw new ProfilerException("Missing principal for request: " + requestPath);
            }

            BaseConcretePageElement managedPage = page; //requestContext.getManagedPage();
            PageTemplate managedPageTemplate = this.getPageTemplate(page); //requestContext.getManagedPageTemplate();
            Map managedFragmentDefinitions = null;  //requestContext.getManagedFragmentDefinitions();
            ContentPage contentPage = pageLayoutComponent.newContentPage(managedPage, managedPageTemplate, managedFragmentDefinitions);
            request.setPage(contentPage);

            request.setAttribute(PortalReservedParameters.PATH_ATTRIBUTE, requestPath);
            request.setAttribute(PortalReservedParameters.CONTENT_PATH_ATTRIBUTE, requestPath); //requestContext.getPageContentPath());
//            request.setAttribute(PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE,getPageActionAccess(request));

                if (log.isDebugEnabled())
                {
                    log.debug("Page path: "+contentPage.getPath());
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
            if (true) //requestFallback)
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
//    protected PageActionAccess getPageActionAccess(RequestContext requestContext)
//    { 
//        ContentPage page = requestContext.getPage();
//        String key = page.getId();
//        boolean loggedOn = requestContext.getRequest().getUserPrincipal() != null;
//        boolean anonymous = !loggedOn;
//        PageActionAccess pageActionAccess = null;
//
//        Map sessionActions = null;
//        synchronized (this)
//        {
//            sessionActions = (Map) requestContext.getSessionAttribute(PAGE_ACTION_ACCESS_MAP_SESSION_ATTR_KEY);
//            if (sessionActions == null)
//            {
//                sessionActions = new HashMap();
//                requestContext.setSessionAttribute(PAGE_ACTION_ACCESS_MAP_SESSION_ATTR_KEY, sessionActions);
//            }
//            else
//            {
//                pageActionAccess = (PageActionAccess) sessionActions.get(key);
//            }
//        }
//        synchronized (sessionActions)
//        {
//            if (pageActionAccess == null)
//            {
//                pageActionAccess = new PageActionAccess(anonymous, page);
//                sessionActions.put(key, pageActionAccess);
//            }
//            else
//            {
//                pageActionAccess.checkReset(anonymous, page);
//            }        
//        }
//        
//        return pageActionAccess;
//    }

    public String toString()
    {
        return "PageValve";
    }

    public PageTemplate getPageTemplate(Page page2) throws NodeNotFoundException
    {
    	PageTemplate requestPageTemplate = null;
        BaseConcretePageElement page = page2;
        if (page != null)
        {
            // scan through site looking for first page template
            // up the folder hierarchy from the requested page
            try
            {
                Folder folder = (Folder)page.getParent();
                while ((folder != null) && (requestPageTemplate == null))
                {
                    NodeSet pageTemplates = folder.getPageTemplates();
                    if ((pageTemplates != null) && !pageTemplates.isEmpty())
                    {
                        // return first page template found
                        requestPageTemplate = (PageTemplate)pageTemplates.iterator().next();
                    }
                    else
                    {
                        // continue scan
                        folder = (Folder)folder.getParent();
                    }
                }
            }
            catch (NodeException ne)
            {
            }
        }
        return requestPageTemplate;
    }
    
    
}
