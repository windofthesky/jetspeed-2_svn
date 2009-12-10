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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.beans.ContentFragmentBean;
import org.apache.jetspeed.services.beans.ContentPageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PageLayoutService
 * 
 * @version $Id$
 */
@Path("/pagelayout/")
public class PageLayoutService
{
    
    private static Logger log = LoggerFactory.getLogger(PageLayoutService.class);
    
    @Context
    private ServletConfig servletConfig;
    
    @Context
    private ServletContext servletContext;
    
    private PageLayoutComponent pageLayoutComponent;
    
    private PortletRegistry portletRegistry;
    
    private PortletActionSecurityBehavior securityBehavior;
    
    public PageLayoutService(PageLayoutComponent pageLayoutComponent,
                             PortletRegistry portletRegistry,
                             PortletActionSecurityBehavior securityBehavior)
    {
        this.pageLayoutComponent = pageLayoutComponent;
        this.portletRegistry = portletRegistry;
        this.securityBehavior = securityBehavior;
    }
    
    @GET
    @Path("/page/")
    public ContentPageBean getContentPage(@Context HttpServletRequest servletRequest,
                                          @Context UriInfo uriInfo)
    {
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = getContentPage(requestContext, JetspeedActions.VIEW);
        return new ContentPageBean(contentPage);
    }
    
    @POST
    @Path("/fragment/{type}/{name}/")
    public ContentFragmentBean addContentFragment(@Context HttpServletRequest servletRequest,
                                                  @Context UriInfo uriInfo,
                                                  @PathParam("type") String fragmentType,
                                                  @PathParam("name") String fragmentName)
    {
        if (StringUtils.isBlank(fragmentType) || StringUtils.isBlank(fragmentName))
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment type and name not specified"));
        }
        
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = getContentPage(requestContext, JetspeedActions.EDIT);
        
        try
        {
            ContentFragment contentFragment = pageLayoutComponent.addPortlet(contentPage, fragmentType, fragmentName);
            return new ContentFragmentBean(contentFragment);
        }
        catch (Exception e)
        {
            throw new WebApplicationException(e);
        }
    }
    
    @DELETE
    @Path("/fragment/{id}/")
    public Response deleteContentFragment(@Context HttpServletRequest servletRequest,
                                          @Context UriInfo uriInfo,
                                          @PathParam("id") String fragmentId)
    {
        if (StringUtils.isBlank(fragmentId))
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment id not specified"));
        }
        
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = getContentPage(requestContext, JetspeedActions.EDIT);
        
        try
        {
            pageLayoutComponent.removeFragment(contentPage, fragmentId);
        }
        catch (Exception e)
        {
            throw new WebApplicationException(e);
        }
        
        return Response.ok().build();
    }
    
    @PUT
    @Path("/fragment/{id}/pos/")
    public ContentFragmentBean moveContentFragment(@Context HttpServletRequest servletRequest,
                                                   @Context UriInfo uriInfo,
                                                   @PathParam("id") String fragmentId,
                                                   @QueryParam("layout") String layoutFragmentId,
                                                   @QueryParam("dir") String direction,
                                                   @QueryParam("row") String rowParam,
                                                   @QueryParam("col") String colParam,
                                                   @QueryParam("x") String posXParam,
                                                   @QueryParam("y") String posYParam,
                                                   @QueryParam("z") String posZParam,
                                                   @QueryParam("w") String posWidthParam,
                                                   @QueryParam("h") String posHeightParam)
    {
        if (StringUtils.isBlank(fragmentId))
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment id not specified"));
        }
        
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = getContentPage(requestContext, JetspeedActions.EDIT);
        ContentFragment contentFragment = contentPage.getFragmentById(fragmentId);
        
        if (contentFragment == null)
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment not found with the specified id: " + fragmentId));
        }
        
        ContentFragment layoutFragment = null;
        
        if (!StringUtils.isBlank(layoutFragmentId))
        {
            layoutFragment = contentPage.getFragmentByFragmentId(layoutFragmentId);
            
            if (layoutFragment == null)
            {
                throw new WebApplicationException(new IllegalArgumentException("Layout fragment not found with the specified id: " + layoutFragmentId));
            }
        }
        else
        {
            layoutFragment = getParentFragment(pageLayoutComponent.getUnlockedRootFragment(contentPage), fragmentId);
            
            if (layoutFragment == null)
            {
                throw new WebApplicationException(new IllegalArgumentException("Layout fragment not found for the fragment: " + fragmentId));
            }
        }
        
        int layoutColumnCount = getColumnCountOfLayoutFragment(layoutFragment);
        
        if (!StringUtils.isBlank(direction))
        {
            int row = contentFragment.getLayoutRow();
            int col = contentFragment.getLayoutColumn();
            
            direction = direction.trim();
            
            if ("left".equals(direction))
            {
                if (col > 0)
                {
                    --col;
                }
            }
            else if ("right".equals(direction))
            {
                if (col < layoutColumnCount - 1)
                {
                    ++col;
                }
            }
            else if ("up".equals(direction))
            {
                // TODO: retrieve all portlet fragments to calculate the real row number?
                if (row > 0)
                {
                    --row;
                }
            }
            else if ("down".equals(direction))
            {
                // TODO: retrieve all portlet fragments to calculate the real row number?
                ++row;
            }
            else
            {
                throw new WebApplicationException(new IllegalArgumentException("Invalid direction: " + direction));
            }
            
            rowParam = Integer.toString(row);
            colParam = Integer.toString(col);
        }
        
        if (!StringUtils.isBlank(rowParam) && !StringUtils.isBlank(colParam))
        {
            int row = NumberUtils.toInt(rowParam, -1);
            int col = NumberUtils.toInt(colParam, -1);

            try
            {
                pageLayoutComponent.updateRowColumn(contentFragment, row, col);
                return new ContentFragmentBean(contentPage.getFragmentById(fragmentId));
            }
            catch (Exception e)
            {
                throw new WebApplicationException(e);
            }
        }
        else
        {
            float posX = NumberUtils.toFloat(posXParam, -1.0f);
            float posY = NumberUtils.toFloat(posYParam, -1.0f);
            float posZ = NumberUtils.toFloat(posZParam, -1.0f);
            float posWidth = NumberUtils.toFloat(posWidthParam, -1.0f);
            float posHeight = NumberUtils.toFloat(posHeightParam, -1.0f);
            
            try
            {
                pageLayoutComponent.updatePosition(contentFragment, posX, posY, posZ, posWidth, posHeight);
                return new ContentFragmentBean(contentPage.getFragmentById(fragmentId));
            }
            catch (Exception e)
            {
                throw new WebApplicationException(e);
            }
        }
    }
    
    private ContentPage getContentPage(RequestContext requestContext, String action) throws WebApplicationException
    {
        try
        {
            if (securityBehavior != null)
            {
                if (!securityBehavior.checkAccess(requestContext, action))
                {
                    throw new SecurityException("Insufficient access to view page");
                }
            }
            
            return requestContext.getPage();
        }
        catch (Exception e)
        {
            throw new WebApplicationException(e);
        }
    }
    
    private ContentFragment getParentFragment(ContentFragment contentFragment, String fragmentId)
    {
        for (ContentFragment child : (List<ContentFragment>) contentFragment.getFragments())
        {
            if (fragmentId.equals(child.getId()))
            {
                return contentFragment;
            }
            else if (ContentFragment.LAYOUT.equals(child.getType()))
            {
                ContentFragment parent = getParentFragment(child, fragmentId);
                
                if (parent != null)
                {
                    return parent;
                }
            }
        }
        
        return null;
    }
    
    private int getColumnCountOfLayoutFragment(ContentFragment layoutFragment)
    {
        int columnCount = 0;
        
        String sizes = layoutFragment.getLayoutSizes();
        
        if (StringUtils.isBlank(sizes))
        {
            PortletDefinition layoutPortletDef = portletRegistry.getPortletDefinitionByUniqueName(layoutFragment.getName(), true);
            InitParam initParam = layoutPortletDef.getInitParam("sizes");
            
            if (initParam != null)
            {
                sizes = initParam.getParamValue();
            }
            else
            {
                initParam = layoutPortletDef.getInitParam("columns");
                
                if (initParam != null)
                {
                    return Integer.parseInt(initParam.getParamValue());
                }
            }
        }
        
        if (!StringUtils.isBlank(sizes))
        {
            columnCount = StringUtils.splitPreserveAllTokens(sizes, ",").length;
        }
        
        return columnCount;
    }
}
