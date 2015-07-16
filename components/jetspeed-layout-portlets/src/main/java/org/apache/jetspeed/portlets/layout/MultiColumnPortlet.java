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
package org.apache.jetspeed.portlets.layout;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.decoration.PageEditAccess;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.FolderNotUpdatedException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 */
public class MultiColumnPortlet extends LayoutPortlet
{
    /** Commons logging */
    protected final static Logger log = LoggerFactory.getLogger(MultiColumnPortlet.class);

    protected final static String PARAM_NUM_COLUMN = "columns";
    protected final static int DEFAULT_NUM_COLUMN = 2;
    protected final static String PARAM_COLUMN_SIZES = "sizes";
    protected final static String DEFAULT_ONE_COLUMN_SIZES = "100%";
    protected final static String DEFAULT_TWO_COLUMN_SIZES = "50%,50%";
    protected final static String DEFAULT_THREE_COLUMN_SIZES = "34%,33%,33%";

    protected int numColumns = 0;
    protected String columnSizes = null;
    protected String portletName = null;
    protected String layoutType;
    protected String editorType = null;
    protected DecorationFactory decorators;
    protected JetspeedDesktop desktop;
    protected JetspeedContentCache decoratorCache;
    protected PageManager pageManager;

    public void init( PortletConfig config ) throws PortletException
    {
        super.init(config);
        this.portletName = config.getPortletName();
        this.layoutType = config.getInitParameter("layoutType");
        this.editorType = config.getInitParameter("editorType");
        if (this.layoutType == null)
        {
            throw new PortletException("Layout type not specified for " + this.portletName);
        }
        this.numColumns = Integer.parseInt(config.getInitParameter(PARAM_NUM_COLUMN));
        if (this.numColumns < 1)
        {
            this.numColumns = 1;
        }
        this.columnSizes = config.getInitParameter(PARAM_COLUMN_SIZES);
        if ((this.columnSizes == null) || (this.columnSizes.trim().length() == 0))
        {
            switch (this.numColumns)
            {
            case 1: this.columnSizes = DEFAULT_ONE_COLUMN_SIZES; break;
            case 2: this.columnSizes = DEFAULT_TWO_COLUMN_SIZES; break;
            case 3: this.columnSizes = DEFAULT_THREE_COLUMN_SIZES; break;
            default: this.columnSizes = null; break;
            }
        }
        if (this.columnSizes == null)
        {
            throw new PortletException("Column sizes cannot be defaulted for " + this.numColumns + " columns and are not specified for " + this.portletName);
        }
       
        this.decorators = (DecorationFactory)getPortletContext().getAttribute(CommonPortletServices.CPS_DECORATION_FACTORY);
        if (null == this.decorators)
        {
            throw new PortletException("Failed to find the Decoration Factory on portlet initialization");
        }
        
        this.desktop = (JetspeedDesktop)getPortletContext().getAttribute(CommonPortletServices.CPS_DESKTOP);
        
        this.decoratorCache = (JetspeedContentCache)getPortletContext().getAttribute(CommonPortletServices.CPS_DECORATOR_CACHE);
        
        pageManager = (PageManager)getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager)
        {
            throw new PortletException("Failed to find the Page Manager on portlet initialization");
        }        
    }

    public void doView( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        RequestContext context = getRequestContext(request);

        ContentPage requestPage = context.getPage();       
        PageEditAccess pageEditAccess = (PageEditAccess)context.getAttribute(PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE);
        if ( requestPage == null)
        {
            // Targeting this portlet REQUIRES that the ProfilerValve has been invoked!
            throw new PortletException("Current request page not available.");
        }
        if (pageEditAccess == null)
        {
            // Targeting this portlet REQUIRES that the ProfilerValve has been invoked!
            throw new PortletException("Current PageEditAccess not available.");            
        }
        
        Boolean editing = ( pageEditAccess.isEditing() && 
                            PortletMode.VIEW.equals(request.getPortletMode()) && 
                            request.isPortletModeAllowed(PortletMode.EDIT))
                          ? Boolean.TRUE : Boolean.FALSE;
                                         
        PortletWindow window = context.getPortalURL().getNavigationalState().getMaximizedWindow();
        if (window != null)
        {
            super.doView(request, response);
            return;
        }
        
        // get fragment column sizes
        ContentFragment f = getFragment(request, false);
        String fragmentColumnSizes = columnSizes;
        String fragmentColumnSizesProperty = f.getProperty(Fragment.SIZES_PROPERTY_NAME);
        if (fragmentColumnSizesProperty != null)
        {
            fragmentColumnSizes = fragmentColumnSizesProperty;
        }
        String [] fragmentColumnSizesArray = fragmentColumnSizes.split("\\,");
        List fragmentColumnSizesList = new ArrayList(fragmentColumnSizesArray.length);
        for (int i = 0; (i < fragmentColumnSizesArray.length); i++)
        {
            fragmentColumnSizesList.add(fragmentColumnSizesArray[i]);
        }

        // construct layout object
        ColumnLayout layout = constructColumnLayout(f, layoutType, fragmentColumnSizesArray);

        // invoke the JSP associated with this portlet
        request.setAttribute("columnLayout", layout);
        request.setAttribute("numberOfColumns", new Integer(numColumns));
        request.setAttribute("decorationFactory", this.decorators);
        request.setAttribute("columnSizes", fragmentColumnSizesList);
        request.setAttribute("editing",editing);
        request.setAttribute("fragmentNestingLevel",new Integer(requestPage.getFragmentNestingLevel(f.getId())));
        super.doView(request, response);
        request.removeAttribute("decorationFactory");
        request.removeAttribute("columnLayout");
        request.removeAttribute("numberOfColumns");
        request.removeAttribute("columnSizes");
        request.removeAttribute("editing");
        request.removeAttribute(("fragmentNestingLevel"));
    }

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        RequestContext requestContext = getRequestContext(request);
        
        ContentPage requestPage = requestContext.getPage();       
        PageEditAccess pageEditAccess = (PageEditAccess)requestContext.getAttribute(PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE);
        if ( requestPage == null || pageEditAccess == null )
        {
            // Targeting this portlet with an ActionRequest REQUIRES that the ProfilerValve has been invoked!
            throw new PortletException("Current request page or PageEditAccess not available.");
        }

        decoratorCache.invalidate(requestContext);
        
        String pageMode = request.getParameter("pageMode");
        if ( pageMode != null )
        {
            if ( "view".equals(pageMode) )
            {
                pageEditAccess.setEditing(false);
            }
            else if ( "edit".equals(pageMode) && pageEditAccess.isEditAllowed() )
            {
                if ( this.editorType != null && this.editorType.equals( "desktop" ) )
                {
                    String redirectUrl = this.desktop.getPortalUrl( requestContext, requestContext.getPath() );
                    redirectUrl += "?editPage=true&portal=true";
                    response.sendRedirect( redirectUrl );
                }
                else
                {
                    pageEditAccess.setEditing(true);
                }
            }
            return;
        }
        
        if ( pageEditAccess.isEditAllowed() && request.isPortletModeAllowed(PortletMode.EDIT) )
        {
            String layout = null;
            
            boolean addLayout = request.getParameter("jsAddLayout") != null;
            if ( addLayout || request.getParameter("jsChangeLayout") != null )
            {
                layout = request.getParameter("layout");
                if ( layout != null && layout.length() > 0 )
                {
                    PortletWindow window = requestContext.getActionWindow();
                    ContentFragment targetFragment = requestPage.getFragmentById(window.getId().toString());
                    if ( targetFragment != null )
                    {
                        if ( addLayout )
                        {
                            try
                            {
                                targetFragment.addPortlet(Fragment.LAYOUT, layout);
                                clearLayoutAttributes(request);                            
                            }
                            catch (Exception e)
                            {
                                throw new PortletException("failed to add portlet " + layout + " to page: " + requestPage+": "+e.getMessage(), e);
                            }
                        }
                        else if ( !layout.equals(targetFragment.getName()) )
                        {
                            try
                            {
                                // layout portlet change
                                targetFragment.updateName(layout);
                                clearLayoutAttributes(request);
                                return;
                            }
                            catch (Exception e)
                            {
                                throw new PortletException("Unable to update page: "+e.getMessage(), e);
                            }
                        }
                    }
                }
                return;
            }

            if ( request.getParameter("jsSubmitPage" ) != null )
            {
                String jsPageName = request.getParameter("jsPageName");
                String jsPageTitle = request.getParameter("jsPageTitle");
                String jsPageShortTitle = request.getParameter("jsPageShortTitle");
                layout = request.getParameter("layout");
                if ( ( layout == null ) || ( layout.length() == 0 ) )
                {
                    layout = requestPage.getRootFragment().getName();
                }
                if ( jsPageName != null && jsPageName.length() > 0 && jsPageName.indexOf(Folder.PATH_SEPARATOR) == -1 )
                {
                    try
                    {
                        requestPage.newSiblingPage(getEscapedName(jsPageName), layout, jsPageTitle, jsPageShortTitle);
                    }
                    catch (Exception e)
                    {
                        throw new PortletException("Unable to access page for editing: "+e.getMessage(), e);
                    }                        
                }
                return;
            }

            if (request.getParameter("jsChangePageName") != null)
            {
                String jsPageTitle = request.getParameter("jsPageTitle");
                String jsPageShortTitle = request
                        .getParameter("jsPageShortTitle");
                try
                {
                    requestPage.updateTitles(jsPageTitle, jsPageShortTitle);
                }
                catch (Exception e)
                {
                    throw new PortletException(
                            "Unable to access page for editing: "
                                    + e.getMessage(), e);
                }
                return;
            }

            if ( request.getParameter("jsDeletePage" ) != null )
            {
                try
                {
                    requestPage.remove();
                }
                catch (Exception e)
                {
                    throw new PortletException("Unable to access page for removing: "+e.getMessage(), e);
                }
                return;
            }

            if (request.getParameter("jsMovePageLeft") != null)
            {
                try
                {
                    requestPage.decrementInDocumentOrder();
                }
                catch (Exception e)
                {
                    throw new PortletException(
                            "Unable to access page for changing the document order: "
                                    + e.getMessage(), e);
                }
                return;
            }

            if (request.getParameter("jsMovePageRight") != null)
            {
                try
                {
                    requestPage.incrementInDocumentOrder();
                }
                catch (Exception e)
                {
                    throw new PortletException(
                            "Unable to access page for changing the document order: "
                                    + e.getMessage(), e);
                }
                return;
            }            

            if (request.getParameter("jsSubmitFolder") != null)
            {
                String jsFolderName = request.getParameter("jsFolderName");
                String jsFolderTitle = request.getParameter("jsFolderTitle");
                String jsFolderShortTitle = request.getParameter("jsFolderShortTitle");
                layout = request.getParameter("layout");
                if ( ( layout == null ) || ( layout.length() == 0 ) )
                {
                    layout = requestPage.getRootFragment().getName();
                }
                if (jsFolderName != null && jsFolderName.length() > 0
                        && jsFolderName.indexOf(Folder.PATH_SEPARATOR) == -1)
                {
                    try
                    {
                        requestPage.newSiblingFolder(getEscapedName(jsFolderName), jsFolderTitle, jsFolderShortTitle, layout);
                    }
                    catch (Exception e)
                    {
                        throw new PortletException(
                                "Unable to access folder for editing: "
                                        + e.getMessage(), e);
                    }
                }
                return;
            }

            if (request.getParameter("jsChangeFolderName") != null)
            {
                String jsFolderTitle = request.getParameter("jsFolderTitle");
                String jsFolderShortTitle = request
                        .getParameter("jsFolderShortTitle");
                try
                {
                    requestPage.updateFolderTitles(jsFolderTitle, jsFolderShortTitle);
                }
                catch (Exception e)
                {
                    throw new PortletException(
                            "Unable to access folder for editing: "
                                    + e.getMessage(), e);
                }
                return;
            }
            
            if (request.getParameter("jsDeleteFolder") != null)
            {
                try
                {
                    requestPage.removeFolder();
                }
                catch (Exception e)
                {
                    throw new PortletException(
                            "Unable to access folder for removing: "
                                    + e.getMessage(), e);
                }
                return;
            }

            if (request.getParameter("jsMoveFolderLeft") != null)
            {
                try
                {
                    requestPage.decrementFolderInDocumentOrder();
                }
                catch (Exception e)
                {
                    throw new PortletException(
                            "Unable to access folder for changing the document order: "
                                    + e.getMessage(), e);
                }
                return;
            }

            if (request.getParameter("jsMoveFolderRight") != null)
            {
                try
                {
                    requestPage.incrementFolderInDocumentOrder();
                }
                catch (Exception e)
                {
                    throw new PortletException(
                            "Unable to access folder for changing the document order: "
                                    + e.getMessage(), e);
                }
                return;
            }

            // JS2-1331: since 2.3.0
            if ( request.getParameter("jsChangeColumns" ) != null )
            {
                String columns = request.getParameter("jsNumberColumns");
                if ( columns != null && columns.length() > 0 )
                {
                    PortletWindow window = requestContext.getActionWindow();
                    ContentFragment targetFragment = requestPage.getFragmentById(window.getId().toString());
                    if ( targetFragment != null )
                    {
                        try
                        {
                            int max = Integer.parseInt(columns);
                            targetFragment.reorderColumns(max);
                            clearLayoutAttributes(request);
                        }
                        catch (Exception e)
                        {
                            throw new PortletException("failed to change columns " + layout + " to page: " + requestPage+": "+e.getMessage(), e);
                        }
                    }
                }
                return;
            }

            String theme = request.getParameter("theme");
            if ( theme != null && theme.length() > 0 )
            {
                try
                {
                    requestPage.updateDefaultDecorator(theme, Fragment.LAYOUT);
                }
                catch (Exception e)
                {
                    throw new PortletException("Unable to update page: "+e.getMessage(), e);
                }
                return;
            }
            
            String fragmentId = request.getParameter("fragment");
            if ( fragmentId != null && fragmentId.length() > 0 )
            {
                String move = request.getParameter("move");
                if ( move != null && move.length() > 0 )
                {
                    int moveCode = Integer.parseInt(move);                    
                    PortletWindow window = requestContext.getActionWindow();
                    ContentFragment currentFragment = requestPage.getFragmentById(window.getId().toString());
                    ContentFragment fragmentToMove = requestPage.getFragmentById(fragmentId);                    
                    if ( currentFragment != null && fragmentToMove != null )
                    {
                        ColumnLayout columnLayout;
                        try
                        {
                            columnLayout = new ColumnLayout(numColumns, layoutType, currentFragment.getFragments(), null);
                            columnLayout.addLayoutEventListener(new PageLayoutEventListener(layoutType));
                        }
                        catch (LayoutEventException e1)
                        {
                            throw new PortletException("Failed to build ColumnLayout "+e1.getMessage(), e1);
                        }

                        try
                        {                
                            switch (moveCode)
                            {
                            case LayoutEvent.MOVED_UP:
                                columnLayout.moveUp(fragmentToMove);
                                break;
                            case LayoutEvent.MOVED_DOWN:
                                columnLayout.moveDown(fragmentToMove);
                                break;
                            case LayoutEvent.MOVED_RIGHT:
                                columnLayout.moveRight(fragmentToMove);
                                break;
                            case LayoutEvent.MOVED_LEFT:
                                columnLayout.moveLeft(fragmentToMove);
                                break;
                            default:
                                throw new PortletException("Invalid movement code " + moveCode);
                            }
                           
                        }
                        catch (SecurityException se)
                        {
                            // ignore page security constraint violations, only
                            // permitted users can edit managed pages; page
                            // update will remain transient
                            log.info("Unable to update page " + requestPage.getId() + " layout due to security permission/constraint.", se);
                        }
                        catch (Exception e)
                        {
                            if (e instanceof PortletException)
                            {
                                throw (PortletException)e;
                            }
                            else
                            {
                                throw new PortletException("Unable to process layout for page " + requestPage.getId() + " layout: " + e.toString(), e);
                            }
                        }
                    }
                    
                    return;
                }
                
                String remove = request.getParameter("remove");
                if ( remove != null && remove.length() > 0 )
                {
                    try
                    {
                        requestPage.removeFragment(fragmentId);
                    }
                    catch (Exception e)
                    {
                        throw new PortletException("Unable to update page to remove fragment: "+e.getMessage(), e);
                    }
                    return;
                }
                
                String decorator = request.getParameter("decorator");
                if ( decorator != null )
                {
                    try
                    {
                        ContentFragment fragment = requestPage.getFragmentById(fragmentId);
                        if ( fragment != null )
                        {
                            fragment.updateDecorator(decorator);
                        }
                    }
                    catch (Exception e)
                    {
                        throw new PortletException("Unable to update page for fragment decorator: "+e.getMessage(), e);
                    }
                    return;
                }
            }
            
            // change style for all pages in user folder 
            String jsChangeUserPagesTheme = request.getParameter("jsChangeUserPagesTheme");
            if ( jsChangeUserPagesTheme != null )
            {
                String user_pages_theme = request.getParameter("user_pages_theme");
                try
                {
                   Folder f = pageManager.getUserFolder(request.getRemoteUser());
                   applyStyle(f,user_pages_theme,Fragment.LAYOUT);
                }
                catch (Exception e)
                {
                   throw new PortletException("Unable to update folder for defUserLayoutDeco decorator: "+e.getMessage(), e);
                }
                return;
            }
            
            String jsChangeUserPortletsDeco = request.getParameter("jsChangeUserPortletsDeco");
            if ( jsChangeUserPortletsDeco != null )
            {                  
                String user_portlets_deco = request.getParameter("user_portlets_deco");
                try
                {
                   Folder f = pageManager.getUserFolder(request.getRemoteUser());
                   applyStyle(f,user_portlets_deco,Fragment.PORTLET);
                }
                catch (Exception e)
                {
                    throw new PortletException("Unable to update folder for defUserPortletDeco decorator: "+e.getMessage(), e);
                }
               return;
            }
            
            String jsChangeThemeAll = request.getParameter("jsChangeThemeAll");
            if (jsChangeThemeAll != null)
            {
                String decorators = request.getParameter("decorators");                
                String targetFragmentId = request.getParameter("fragment");
                ContentFragment targetFragment = requestPage.getFragmentById(targetFragmentId);
                if (targetFragment != null)
                {
                    Iterator fragmentsIter = targetFragment.getFragments().iterator();
                    while (fragmentsIter.hasNext())
                    {
                        ContentFragment fragment = (ContentFragment) fragmentsIter.next();
                        if (fragment != null )
                        {
                            try
                            {
                                fragment.updateDecorator(decorators);
                            }
                            catch (Exception e)
                            {
                                throw new PortletException("Unable to update page for fragment decorator: "+e.getMessage(), e);
                            }
                        }
                    }
                }
                return;
            }                
            
            String portlets = request.getParameter("portlets");
            if ( portlets != null && portlets.length() > 0 )
            {
                PortletWindow window = requestContext.getActionWindow();
                ContentFragment targetFragment = requestPage.getFragmentById(window.getId().toString());
                if ( targetFragment != null )
                {
                    StringTokenizer tokenizer = new StringTokenizer(portlets, ",");            
                    while (tokenizer.hasMoreTokens())
                    {
                        String portlet = tokenizer.nextToken();
                        if (portlet.startsWith("box_"))
                        {
                            portlet = portlet.substring("box_".length());                        
                            try
                            {
                                targetFragment.addPortlet(ContentFragment.PORTLET, portlet);
                            }
                            catch (Exception e)
                            {
                                throw new PortletException("Unable to add portlet fragment to page: "+e.getMessage(), e);
                            }
                        }
                    }
                }
                return;
            }

        }
    }
        
    protected void clearLayoutAttributes(ActionRequest request)
    {
        request.getPortletSession().removeAttribute(PortalReservedParameters.PAGE_LAYOUT_VIEW);
        request.getPortletSession().removeAttribute(PortalReservedParameters.PAGE_LAYOUT_SOLO);
        request.getPortletSession().removeAttribute(PortalReservedParameters.PAGE_LAYOUT_MAX);        
        request.getPortletSession().removeAttribute(PortalReservedParameters.PAGE_LAYOUT_HELP);
    }

    protected String getEscapedName(String pageName)
    {
        try
        {
            return URLEncoder.encode(pageName, "UTF-8").replace('%', '_');
        }
        catch (UnsupportedEncodingException e)
        {
            log.warn("Unsupported Encoding Exception.", e);
            return pageName;
        }
    }
    
    private void applyStyle(Folder f, String theme, String theme_type) throws FolderNotUpdatedException, NodeException 
    {
       f.setDefaultDecorator(theme, theme_type);
       pageManager.updateFolder(f);
       Iterator pagesIter = f.getPages().iterator();
       while(pagesIter.hasNext())
       {
           Page pp = (Page) pagesIter.next();
           pp.setDefaultDecorator(theme, theme_type);
           pageManager.updatePage(pp);
       }                       
       Iterator userFoldersIter = pageManager.getFolders(f).iterator();
       while(userFoldersIter.hasNext()) 
       {
           Folder ff = (Folder) userFoldersIter.next();
           applyStyle(ff,theme,theme_type);
       }
    }

    public ColumnLayout constructColumnLayout(ContentFragment f, String layout, String[] fragmentColumnSizesArray)
            throws PortletException
    {
        try
        {
            ColumnLayout columnLayout = new ColumnLayout(getNumColumns(f), layoutType, f.getFragments(), fragmentColumnSizesArray);
            columnLayout.addLayoutEventListener(new PageLayoutEventListener(layoutType));
            return columnLayout;
        }
        catch (LayoutEventException e1)
        {
            throw new PortletException("Failed to build ColumnLayout "+e1.getMessage(), e1);
        }
    }

    public int getNumColumns(ContentFragment fragment) {
        return numColumns;
    }

}
