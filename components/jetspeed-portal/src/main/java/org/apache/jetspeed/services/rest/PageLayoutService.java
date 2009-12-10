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

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
    
    private ContentFragmentRowComparator contentFragmentRowComparator = new ContentFragmentRowComparator();
    
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
            
            if (contentFragment.getLayoutColumn() == -1 || contentFragment.getLayoutRow() == -1)
            {
                String addedContentFragmentId = contentFragment.getId();
                ContentFragment layoutFragment = getParentFragment(pageLayoutComponent.getUnlockedRootFragment(contentPage), addedContentFragmentId);
                int columnCount = getColumnCountOfLayoutFragment(layoutFragment);
                adjustPositionsOfChildFragments(layoutFragment, columnCount);
            }
            
            return new ContentFragmentBean(contentFragment);
        }
        catch (Exception e)
        {
            throw new WebApplicationException(e);
        }
    }
    
    @DELETE
    @Path("/fragment/{id}/")
    public ContentFragmentBean deleteContentFragment(@Context HttpServletRequest servletRequest,
                                          @Context UriInfo uriInfo,
                                          @PathParam("id") String fragmentId)
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
        
        ContentFragment layoutFragment = getParentFragment(pageLayoutComponent.getUnlockedRootFragment(contentPage), fragmentId);
        
        if (layoutFragment == null)
        {
            throw new WebApplicationException(new IllegalArgumentException("Layout fragment not found for the fragment: " + fragmentId));
        }
        
        try
        {
            pageLayoutComponent.removeFragment(contentPage, fragmentId);
            
            int columnCount = getColumnCountOfLayoutFragment(layoutFragment);
            adjustPositionsOfChildFragments(layoutFragment, columnCount);
            
            return new ContentFragmentBean(contentFragment);
        }
        catch (Exception e)
        {
            throw new WebApplicationException(e);
        }
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
        
        if (!StringUtils.isBlank(direction))
        {
            direction = direction.trim();
            
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
            
            if ("left".equals(direction))
            {
                int oldColumn = contentFragment.getLayoutColumn();
                
                if (oldColumn < 0 || oldColumn >= layoutColumnCount)
                {
                    oldColumn = layoutColumnCount - 1;
                }
                
                int newColumn = (oldColumn <= 0 ? 0 : oldColumn - 1);
                
                if (newColumn != oldColumn)
                {
                    pageLayoutComponent.updateRowColumn(contentFragment, contentFragment.getLayoutRow(), newColumn);
                    adjustPositionsOfChildFragments(layoutFragment, layoutColumnCount);
                }
            }
            else if ("right".equals(direction))
            {
                int oldColumn = contentFragment.getLayoutColumn();
                
                if (oldColumn < 0 || oldColumn >= layoutColumnCount)
                {
                    oldColumn = layoutColumnCount - 1;
                }
                
                int newColumn = (oldColumn < layoutColumnCount - 1 ? oldColumn + 1 : layoutColumnCount - 1);
                
                if (newColumn != oldColumn)
                {
                    pageLayoutComponent.updateRowColumn(contentFragment, contentFragment.getLayoutRow(), newColumn);
                    adjustPositionsOfChildFragments(layoutFragment, layoutColumnCount);
                }
            }
            else if ("up".equals(direction))
            {
                adjustPositionsOfChildFragments(layoutFragment, layoutColumnCount);
                SortedSet<ContentFragment> [] fragmentSetArray = getSortedChildFragmentSetArray(layoutFragment, layoutColumnCount);
                for (SortedSet<ContentFragment> set : fragmentSetArray)
                {
                    if (set.contains(contentFragment))
                    {
                        SortedSet<ContentFragment> headSet = set.headSet(contentFragment);
                        
                        if (!headSet.isEmpty())
                        {
                            ContentFragment destFragment = headSet.last();
                            int row = contentFragment.getLayoutRow();
                            int column = contentFragment.getLayoutColumn();
                            int destRow = destFragment.getLayoutRow();
                            int destColumn = destFragment.getLayoutColumn();
                            pageLayoutComponent.updateRowColumn(contentFragment, destRow, destColumn);
                            pageLayoutComponent.updateRowColumn(destFragment, row, column);
                        }
                    }
                    
                    break;
                }
            }
            else if ("down".equals(direction))
            {
                adjustPositionsOfChildFragments(layoutFragment, layoutColumnCount);
                SortedSet<ContentFragment> [] fragmentSetArray = getSortedChildFragmentSetArray(layoutFragment, layoutColumnCount);
                for (SortedSet<ContentFragment> set : fragmentSetArray)
                {
                    if (set.contains(contentFragment))
                    {
                        SortedSet<ContentFragment> tailSet = set.tailSet(contentFragment);
                        
                        if (!tailSet.isEmpty())
                        {
                            ContentFragment destFragment = tailSet.first();
                            int row = contentFragment.getLayoutRow();
                            int column = contentFragment.getLayoutColumn();
                            int destRow = destFragment.getLayoutRow();
                            int destColumn = destFragment.getLayoutColumn();
                            pageLayoutComponent.updateRowColumn(contentFragment, destRow, destColumn);
                            pageLayoutComponent.updateRowColumn(destFragment, row, column);
                        }
                    }
                    
                    break;
                }
            }
            else
            {
                throw new WebApplicationException(new IllegalArgumentException("Invalid direction: " + direction));
            }
        }
        else if (!StringUtils.isBlank(rowParam) && !StringUtils.isBlank(colParam))
        {
            int row = NumberUtils.toInt(rowParam, -1);
            int col = NumberUtils.toInt(colParam, -1);

            try
            {
                pageLayoutComponent.updateRowColumn(contentFragment, row, col);
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
            }
            catch (Exception e)
            {
                throw new WebApplicationException(e);
            }
        }
        
        return new ContentFragmentBean(contentFragment);
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
        int columnCount = 1;
        
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
    
    private SortedSet<ContentFragment> [] getSortedChildFragmentSetArray(ContentFragment layoutFragment, int columnCount)
    {
        if (columnCount <= 0)
        {
            throw new IllegalArgumentException("Invalid columnCount: " + columnCount);
        }
        
        int defaultColumn = columnCount - 1;
        SortedSet<ContentFragment> [] fragmentSetArray = new TreeSet[columnCount];
        
        for (int i = 0; i < columnCount; i++)
        {
            fragmentSetArray[i] = new TreeSet<ContentFragment>(contentFragmentRowComparator);
        }
        
        for (ContentFragment child : (List<ContentFragment>) layoutFragment.getFragments())
        {
            int column = child.getLayoutColumn();
            
            if (column < 0 || column >= columnCount)
            {
                column = defaultColumn;
            }
            
            fragmentSetArray[column].add(child);
        }
        
        return fragmentSetArray;
    }
    
    private void adjustPositionsOfChildFragments(ContentFragment layoutFragment, int columnCount)
    {
        SortedSet<ContentFragment> [] fragmentSetArray = getSortedChildFragmentSetArray(layoutFragment, columnCount);

        for (int column = 0; column < columnCount; column++)
        {
            int row = 0;
            
            for (ContentFragment child : fragmentSetArray[column])
            {
                if (row != child.getLayoutRow() || -1 == child.getLayoutColumn())
                {
                    pageLayoutComponent.updateRowColumn(child, row, column);
                }
                
                ++row;
            }
        }
    }
    
    private static class ContentFragmentRowComparator implements Comparator<ContentFragment>
    {
        public int compare(ContentFragment f1, ContentFragment f2)
        {
            int r1 = f1.getLayoutRow();
            int r2 = f2.getLayoutRow();
            
            if (r1 == r2)
            {
                return 0;
            }
            else if (r1 != -1 && r2 == -1)
            {
                return -1;
            }
            else if (r1 == -1 && r2 != -1)
            {
                return 1;
            }
            else if (r1 > r2)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    }
    
}
