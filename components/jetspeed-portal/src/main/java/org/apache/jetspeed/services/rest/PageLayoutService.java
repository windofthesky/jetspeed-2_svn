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
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.layout.Coordinate;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.layout.PortletPlacementContext;
import org.apache.jetspeed.layout.PortletPlacementException;
import org.apache.jetspeed.layout.impl.CoordinateImpl;
import org.apache.jetspeed.layout.impl.PortletPlacementContextImpl;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.beans.ContentFragmentBean;
import org.apache.jetspeed.services.beans.ContentPageBean;
import org.apache.jetspeed.services.beans.DecorationBean;
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
    
    protected PageLayoutComponent pageLayoutComponent;
    
    protected PortletRegistry portletRegistry;
    
    protected PortletActionSecurityBehavior securityBehavior;
    
    private ContentFragmentRowComparator contentFragmentRowComparator = new ContentFragmentRowComparator();
    
    @Context
    private ServletConfig servletConfig;
    
    @Context
    private ServletContext servletContext;
    
    public PageLayoutService(PageLayoutComponent pageLayoutComponent,
                             PortletRegistry portletRegistry,
                             PortletActionSecurityBehavior securityBehavior)
    {
        this.pageLayoutComponent = pageLayoutComponent;
        this.portletRegistry = portletRegistry;
        this.securityBehavior = securityBehavior;
    }
    
    public PageLayoutService(PageLayoutComponent pageLayoutComponent,
                             PortletRegistry portletRegistry)
    {
        this(pageLayoutComponent, portletRegistry, null);
    }
    
    @GET
    @Path("/page/")
    public ContentPageBean getContentPage(@Context HttpServletRequest servletRequest,
                                          @Context UriInfo uriInfo)
    {
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = null;
        try
        {
            contentPage = getContentPage(requestContext, JetspeedActions.VIEW);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
        
        return new ContentPageBean(contentPage);
    }
    
    @GET
    @Path("/fragment/{id}/")
    public ContentFragmentBean getContentFragment(@Context HttpServletRequest servletRequest,
                                                  @Context UriInfo uriInfo,
                                                  @PathParam("id") String fragmentId)
    {
        if (StringUtils.isBlank(fragmentId))
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment id not specified"));
        }
        
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = null;
        try
        {
            contentPage = getContentPage(requestContext, JetspeedActions.VIEW);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
        ContentFragment contentFragment = contentPage.getFragmentById(fragmentId);
        
        if (contentFragment == null)
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment not found with the specified id: " + fragmentId));
        }
        
        return new ContentFragmentBean(contentFragment);
    }
    
    @POST
    @Path("/fragment/{type}/{name}/")
    public ContentFragmentBean addContentFragment(@Context HttpServletRequest servletRequest,
                                                  @Context UriInfo uriInfo,
                                                  @PathParam("type") String fragmentType,
                                                  @PathParam("name") String fragmentName,
                                                  @FormParam("row") String rowParam,
                                                  @FormParam("col") String colParam,
                                                  @FormParam("minrowscol") String minRowsColumnParam)
    {
        if (StringUtils.isBlank(fragmentType) || StringUtils.isBlank(fragmentName))
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment type and name not specified"));
        }
        
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = null;
        try
        {
            contentPage = getContentPage(requestContext, JetspeedActions.EDIT);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
        
        int row = NumberUtils.toInt(rowParam, -1);
        int col = NumberUtils.toInt(colParam, -1);
        boolean minRowsColumn = BooleanUtils.toBoolean(minRowsColumnParam);
        
        try
        {
            ContentFragment contentFragment = pageLayoutComponent.addPortlet(contentPage, fragmentType, fragmentName);
            String addedContentFragmentId = contentFragment.getId();
            
            ContentFragment layoutFragment = null;
            
            if (col == -1 && minRowsColumn)
            {
                layoutFragment = getParentFragment(contentPage.getNonTemplateRootFragment(), addedContentFragmentId);
                col = getMinRowsColumnIndex(layoutFragment);
            }
            
            if (row != -1 || col != -1) 
            {
                pageLayoutComponent.updateRowColumn(contentFragment, row, col);
            } 
            
            if (layoutFragment == null)
            {
                layoutFragment = getParentFragment(contentPage.getNonTemplateRootFragment(), addedContentFragmentId);
            }
            
            PortletPlacementContext ppc = new PortletPlacementContextImpl(contentPage, portletRegistry, layoutFragment);
            // synchronize back to the page layout root fragment
            contentPage = ppc.syncPageFragments();
            
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
        ContentPage contentPage = null;
        try
        {
            contentPage = getContentPage(requestContext, JetspeedActions.EDIT);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
        ContentFragment contentFragment = contentPage.getFragmentById(fragmentId);
        
        if (contentFragment == null)
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment not found with the specified id: " + fragmentId));
        }
        
        ContentFragment layoutFragment = getParentFragment(contentPage.getNonTemplateRootFragment(), fragmentId);
        
        if (layoutFragment == null)
        {
            throw new WebApplicationException(new IllegalArgumentException("Layout fragment not found for the fragment: " + fragmentId));
        }
        
        try
        {
            pageLayoutComponent.removeFragment(contentPage, fragmentId);
            
            PortletPlacementContext ppc = new PortletPlacementContextImpl(contentPage, portletRegistry, layoutFragment);
            // synchronize back to the page layout root fragment
            contentPage = ppc.syncPageFragments();
        }
        catch (Exception e)
        {
            throw new WebApplicationException(e);
        }
        
        return new ContentFragmentBean(contentFragment);
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
        ContentPage contentPage = null;
        try
        {
            contentPage = getContentPage(requestContext, JetspeedActions.EDIT);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
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
                layoutFragment = getParentFragment(contentPage.getNonTemplateRootFragment(), fragmentId );
                
                if (layoutFragment == null)
                {
                    throw new WebApplicationException(new IllegalArgumentException("Layout fragment not found for the fragment: " + fragmentId));
                }
            }
            
            PortletPlacementContext ppc = null;
            
            try
            {
                ppc = new PortletPlacementContextImpl(contentPage, portletRegistry, layoutFragment);
                
                if ("left".equals(direction))
                {
                    ppc.moveLeft(contentFragment);
                }
                else if ("right".equals(direction))
                {
                    ppc.moveRight(contentFragment);
                }
                else if ("up".equals(direction))
                {
                    ppc.moveUp(contentFragment);
                }
                else if ("down".equals(direction))
                {
                    ppc.moveDown(contentFragment);
                }
                else
                {
                    throw new WebApplicationException(new IllegalArgumentException("Invalid direction: " + direction));
                }
                
                // synchronize back to the page layout root fragment
                contentPage = ppc.syncPageFragments(PageLayoutComponent.USER_PROPERTY_SCOPE, null);
            }
            catch (PortletPlacementException e)
            {
                throw new WebApplicationException(e);
            }
        }
        else if (!StringUtils.isBlank(rowParam) && !StringUtils.isBlank(colParam))
        {
            int row = NumberUtils.toInt(rowParam, -1);
            int col = NumberUtils.toInt(colParam, -1);
            float posHeight = NumberUtils.toFloat(posHeightParam, -1.0f);
            
            if (row != -1 && col != -1 && (contentFragment.getLayoutRow() != row || contentFragment.getLayoutColumn() != col))
            {
                try
                {
                    ContentFragment layoutFragment = null;
                    boolean attach = false;
                    if (!StringUtils.isBlank(layoutFragmentId) && layoutFragmentId.equals("attach"))
                    {
                    	layoutFragmentId = null;
                    	attach = true;
                    }
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
                        layoutFragment = getParentFragment(contentPage.getNonTemplateRootFragment(), fragmentId);
                        
                        if (layoutFragment == null)
                        {
                            throw new WebApplicationException(new IllegalArgumentException("Layout fragment not found for the fragment: " + fragmentId));
                        }
                    }
                    
                    PortletPlacementContext ppc = new PortletPlacementContextImpl(contentPage, portletRegistry, layoutFragment);
                    Coordinate coordinate = new CoordinateImpl(0, 0, col, row);
                    ppc.moveAbsolute(contentFragment, coordinate);
                    
                    if (posHeight != -1.0f)
                    {
                        pageLayoutComponent.updatePosition(contentFragment, -1.0f, -1.0f, -1.0f, -1.0f, posHeight, PageLayoutComponent.USER_PROPERTY_SCOPE, null);
                    }
                    
                    // synchronize back to the page layout root fragment
                    contentPage = ppc.syncPageFragments(PageLayoutComponent.USER_PROPERTY_SCOPE, null);
                    if (attach)
                    	pageLayoutComponent.updateStateMode(contentFragment, JetspeedActions.NORMAL, null, PageLayoutComponent.USER_PROPERTY_SCOPE, null);                    
                }
                catch (Exception e)
                {
                    throw new WebApplicationException(e);
                }
            }
            else if (!StringUtils.isBlank(layoutFragmentId) && layoutFragmentId.equals("attach"))
            {
              	pageLayoutComponent.updateStateMode(contentFragment, JetspeedActions.NORMAL, null, PageLayoutComponent.USER_PROPERTY_SCOPE, null);                    
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
                pageLayoutComponent.updatePosition(contentFragment, posX, posY, posZ, posWidth, posHeight, PageLayoutComponent.USER_PROPERTY_SCOPE, null);
                pageLayoutComponent.updateStateMode(contentFragment, JetspeedActions.DETACH, null, PageLayoutComponent.USER_PROPERTY_SCOPE, null);
                if (layoutFragmentId != null && layoutFragmentId.equals("detach"))
                {
                    // first time detach, reallocate column and move it to the back
                    ContentFragment layoutFragment = getParentFragment(contentPage.getNonTemplateRootFragment(), fragmentId);                    
                    if (layoutFragment == null)
                    {
                        throw new WebApplicationException(new IllegalArgumentException("Layout fragment not found for the fragment: " + fragmentId));
                    }                    
                    PortletPlacementContext ppc = new PortletPlacementContextImpl(contentPage, portletRegistry, layoutFragment);
                    int col = contentFragment.getLayoutColumn();
                    int row = ppc.getNumberRows((col <= 0) ? 0 : col - 1);
                    Coordinate coordinate = new CoordinateImpl(col, contentFragment.getLayoutRow(), col, row);
                    ppc.moveAbsolute(contentFragment, coordinate);
                    contentPage = ppc.syncPageFragments(PageLayoutComponent.USER_PROPERTY_SCOPE, null);
                }
            }
            catch (Exception e)
            {
                throw new WebApplicationException(e);
            }
        }        
        return new ContentFragmentBean(contentFragment);
    }

    @PUT
    @Path("/fragment/{id}/mod/")
    public ContentFragmentBean modifyContentFragment(@Context HttpServletRequest servletRequest,
                                                   @Context UriInfo uriInfo,
                                                   @PathParam("id") String fragmentId,
                                                   @QueryParam("state") String state,
                                                   @QueryParam("mode") String mode)
    {
        if (StringUtils.isBlank(fragmentId))
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment id not specified"));
        }
        
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = null;
        try
        {
            contentPage = getContentPage(requestContext, JetspeedActions.EDIT);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
        ContentFragment contentFragment = contentPage.getFragmentById(fragmentId);
        
        if (contentFragment == null)
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment not found with the specified id: " + fragmentId));
        }
        
        if (!StringUtils.isBlank(state) || !StringUtils.isBlank(state))
        {
            pageLayoutComponent.updateStateMode(contentFragment, state, mode, PageLayoutComponent.USER_PROPERTY_SCOPE, null);            
        }
        
        return new ContentFragmentBean(contentFragment);
    }
    
    @GET
    @Path("/decoration/fragment/{id}/")
    public DecorationBean getDecorationOfContentFragment(@Context HttpServletRequest servletRequest,
                                                         @Context UriInfo uriInfo,
                                                         @PathParam("id") String fragmentId)
    {
        if (StringUtils.isBlank(fragmentId))
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment id not specified"));
        }
        
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        ContentPage contentPage = null;
        try
        {
            contentPage = getContentPage(requestContext, JetspeedActions.VIEW);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
        ContentFragment contentFragment = contentPage.getFragmentById(fragmentId);
        
        if (contentFragment == null)
        {
            throw new WebApplicationException(new IllegalArgumentException("Fragment not found with the specified id: " + fragmentId));
        }
        
        Decoration decoration = contentFragment.getDecoration();
        
        if (decoration == null)
        {
            throw new WebApplicationException(new IllegalArgumentException("Decoration not found with the specified id: " + fragmentId));
        }
        
        return new DecorationBean(decoration);
    }
    
    /**
     * Returns the content page of the current portal request context with security check.
     * 
     * @param requestContext the portal request context
     * @param action the action to check the security against.
     * @return
     * @throws SecurityException
     */
    private ContentPage getContentPage(RequestContext requestContext, String action) throws SecurityException
    {
        if (securityBehavior != null && !securityBehavior.checkAccess(requestContext, action))
        {
            throw new SecurityException("Insufficient access to view page");
        }
        
        return requestContext.getPage();
    }
    
    /**
     * Returns the parent layout content fragment of the content fragment specified by the fragmentId.
     * @param contentFragment the seed content fragment where searching starts from.
     * @param fragmentId the fragment id of the content fragment, of which the parent fragment is looked for.
     * @return
     */
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
    
    /**
     * Returns the column count of the layout content fragment based on the init parameters of the layout portlet.
     * @param layoutFragment
     * @return
     */
    private int getColumnCountOfLayoutFragment(ContentFragment layoutFragment)
    {
        int columnCount = 1;
        
        String sizes = layoutFragment.getLayoutSizes();
        
        if (StringUtils.isBlank(sizes))
        {
            if (portletRegistry != null)
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
        }
        
        if (!StringUtils.isBlank(sizes))
        {
            columnCount = StringUtils.splitPreserveAllTokens(sizes, ",").length;
        }
        
        return columnCount;
    }
    
    /**
     * Returns child content fragment set array ordered by the column index from the layout content fragment.
     * @param layoutFragment
     * @param columnCount
     * @return
     */
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
    
    private int getMinRowsColumnIndex(ContentFragment layoutFragment)
    {
        int columnCount = getColumnCountOfLayoutFragment(layoutFragment);
        SortedSet<ContentFragment> [] fragmentSetArray = getSortedChildFragmentSetArray(layoutFragment, columnCount);
        int col = fragmentSetArray.length - 1;
        
        int rowCount = fragmentSetArray[col].size();
        
        for (int i = fragmentSetArray.length - 2; i >= 0; i--)
        {
            if (fragmentSetArray[i].size() < rowCount)
            {
                col = i;
                rowCount = fragmentSetArray[i].size();
            }
        }
        
        return col;
    }
    
    /**
     * ContentFragmentRowComparator
     * <P>
     * Comparator to compare content fragments by the row index in a column.
     * </P>
     */
    private static class ContentFragmentRowComparator implements Comparator<ContentFragment>
    {
        public int compare(ContentFragment f1, ContentFragment f2)
        {
            int r1 = f1.getLayoutRow();
            int r2 = f2.getLayoutRow();            
            String s1 = f1.getState(); 
            String s2 = f2.getState();
            
            if (!StringUtils.isEmpty(s1) && s1.equals(JetspeedActions.DETACH))
            {
                if (StringUtils.isEmpty(s2) || !s2.equals(JetspeedActions.DETACH))
                    return -1;                
            }
            else if (!StringUtils.isEmpty(s2) && s2.equals(JetspeedActions.DETACH))
            {
                if (StringUtils.isEmpty(s1) || !s1.equals(JetspeedActions.DETACH))
                    return 1;                                
            }
            
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
    
    private class ExtendedContentFragmentRowComparator implements Comparator<ContentFragment>
    {
        private boolean movingDown;
        private ContentFragment movingFragment;
        public ExtendedContentFragmentRowComparator(boolean movingDown, ContentFragment movingFragment)
        {
            this.movingDown = movingDown;
            this.movingFragment = movingFragment;
        }
        public int compare(ContentFragment f1, ContentFragment f2)
        {
            int r1 = f1.getLayoutRow();
            int r2 = f2.getLayoutRow();
            String s1 = f1.getState(); 
            String s2 = f2.getState();
            if (!StringUtils.isEmpty(s1) && s1.equals(JetspeedActions.DETACH))
            {
                if (StringUtils.isEmpty(s2) || !s2.equals(JetspeedActions.DETACH))
                    return -1;                
            }
            else if (!StringUtils.isEmpty(s2) && s2.equals(JetspeedActions.DETACH))
            {
                if (StringUtils.isEmpty(s1) || !s1.equals(JetspeedActions.DETACH))
                    return 1;                                
            }            
            if (r1 == r2)
            {
                if (f1 == movingFragment)
                {
                    return (movingDown) ? 1: -1;
                }
                else if (f2 == movingFragment)
                {
                    return (movingDown) ? -1: 1;
                }
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
