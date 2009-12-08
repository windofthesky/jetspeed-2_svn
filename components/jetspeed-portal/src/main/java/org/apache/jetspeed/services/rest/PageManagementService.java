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
package org.apache.jetspeed.services.rest;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.decoration.DecorationValve;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.beans.ContentPageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PageManagementService
 * 
 * @version $Id$
 */

@Path("/")
public class PageManagementService
{
    
    private static Logger log = LoggerFactory.getLogger(PageManagementService.class);
    
    @Context
    private ServletConfig servletConfig;
    
    @Context
    private ServletContext servletContext;
    
    private PageManager pageManager;
    
    private PortletActionSecurityBehavior securityBehavior;
    
    private PortletRegistry portletRegistry;
    
    private DecorationValve decorationValve;
    
    private PageLayoutComponent pageLayoutComponent;
    
    public PageManagementService(PageManager pageManager,
                                       PortletActionSecurityBehavior securityBehavior,
                                       PortletRegistry portletRegistry,
                                       DecorationValve decorationValve,
                                       PageLayoutComponent pageLayoutComponent)
    {
        this.pageManager = pageManager;
        this.securityBehavior = securityBehavior;
        this.portletRegistry = portletRegistry;
        this.decorationValve = decorationValve;
        this.pageLayoutComponent = pageLayoutComponent;
    }
    
    @GET
    @Path("/{path:.*}")
    public ContentPageBean getContentPage(@Context HttpServletRequest servletRequest,
                                          @Context UriInfo uriInfo,
                                          @PathParam("path") List<PathSegment> pathSegments)
    {
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = getContentPage(requestContext);
        return new ContentPageBean(contentPage);
    }
    
    @POST
    @Path("/{path:.*}")
    public Response addContentFragment(@Context HttpServletRequest servletRequest,
                                       @Context UriInfo uriInfo,
                                       @PathParam("path") List<PathSegment> pathSegments,
                                       @FormParam("type") String fragmentType,
                                       @FormParam("name") String fragmentName)
    {
        if (StringUtils.isBlank(fragmentType) || StringUtils.isBlank(fragmentName))
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment type and name not specified"));
        }
        
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = getContentPage(requestContext);
        
        pageLayoutComponent.addPortlet(contentPage, fragmentType, fragmentName);
        
        return Response.ok().build();
    }
    
    @DELETE
    @Path("/{path:.*}")
    public Response deleteContentFragment(@Context HttpServletRequest servletRequest,
                                       @Context UriInfo uriInfo,
                                       @PathParam("path") List<PathSegment> pathSegments,
                                       @QueryParam("id") String fragmentId)
    {
        if (StringUtils.isBlank(fragmentId))
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment id not specified"));
        }
        
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = getContentPage(requestContext);
        
        pageLayoutComponent.removeFragment(contentPage, fragmentId);
        
        return Response.ok().build();
    }
    
    private ContentPage getContentPage(RequestContext requestContext) throws WebApplicationException
    {
        try
        {
            checkPageAccess(requestContext, JetspeedActions.VIEW);
            // Run the Decoration valve to get actions
            decorationValve.invoke(requestContext, null);
            return requestContext.getPage();
        }
        catch (Exception e)
        {
            throw new WebApplicationException(e);
        }
    }
    
    private void checkPageAccess(RequestContext requestContext, String action) throws SecurityException
    {
        if (securityBehavior != null)
        {
            if (!securityBehavior.checkAccess(requestContext, action))
            {
                throw new SecurityException("Insufficient access to view page");
            }
        }
    }

}
