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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.decoration.PageEditAccess;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.FolderNotUpdatedException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 */
public class MultiColumnPortlet extends LayoutPortlet
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(MultiColumnPortlet.class);

    protected final static String PARAM_NUM_COLUMN = "columns";
    protected final static int DEFAULT_NUM_COLUMN = 2;
    protected final static String PARAM_COLUMN_SIZES = "sizes";
    protected final static String DEFAULT_ONE_COLUMN_SIZES = "100%";
    protected final static String DEFAULT_TWO_COLUMN_SIZES = "50%,50%";
    protected final static String DEFAULT_THREE_COLUMN_SIZES = "34%,33%,33%";

    private int numColumns = 0;
    private String columnSizes = null;
    private String portletName = null;
    private String layoutType;
    private String editorType = null;
    protected DecorationFactory decorators;
    protected JetspeedDesktop desktop;
    protected JetspeedContentCache decoratorCache;

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
    }

    public void doView( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        RequestContext context = getRequestContext(request);

        ContentPage requestPage = context.getPage();       
        PageEditAccess pageEditAccess = (PageEditAccess)context.getAttribute(PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE);
        if ( requestPage == null)
        {
            // Targetting this portlet REQUIRES that the ProfilerValve has been invoked!
            throw new PortletException("Current request page not available.");
        }
        if (pageEditAccess == null)
        {
            // Targetting this portlet REQUIRES that the ProfilerValve has been invoked!
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
        Fragment f = getFragment(request, false);
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
        ColumnLayout layout;
        try
        {
            layout = new ColumnLayout(numColumns, layoutType, f.getFragments(), fragmentColumnSizesArray);
            layout.addLayoutEventListener(new PageManagerLayoutEventListener(pageManager, context.getPage(), layoutType));
        }
        catch (LayoutEventException e1)
        {
            throw new PortletException("Failed to build ColumnLayout "+e1.getMessage(), e1);
        }

        // invoke the JSP associated with this portlet
        request.setAttribute("columnLayout", layout);
        request.setAttribute("numberOfColumns", new Integer(numColumns));
        request.setAttribute("decorationFactory", this.decorators);
        request.setAttribute("columnSizes", fragmentColumnSizesList);
        request.setAttribute("editing",editing);
        request.setAttribute("fragmentNestingLevel",new Integer(getFragmentNestingLevel(requestPage,f.getId())));
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
        RequestContext requestContext = (RequestContext)request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        
        ContentPage requestPage = requestContext.getPage();       
        PageEditAccess pageEditAccess = (PageEditAccess)requestContext.getAttribute(PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE);
        if ( requestPage == null || pageEditAccess == null )
        {
            // Targetting this portlet with an ActionRequest REQUIRES that the ProfilerValve has been invoked!
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
                    ContentFragment targetFragment = requestPage.getContentFragmentById(window.getId().toString());
                    
                    if ( targetFragment == null )
                    {
                        // ignore no longer consistent page definition
                        return;
                    }
                    
                    if ( addLayout )
                    {
                        try
                        {
                            Fragment fragment = pageManager.newFragment();
                            fragment.setType(Fragment.LAYOUT);
                            fragment.setName(layout);                            
                            targetFragment.getFragments().add(fragment);
                            pageManager.updatePage(requestPage);
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
                            targetFragment.setName(layout);
                            pageManager.updatePage(requestPage);
                            entityAccess.updatePortletEntity(window.getPortletEntity(), targetFragment);
                            entityAccess.storePortletEntity(window.getPortletEntity());
                            windowAccess.createPortletWindow(window.getPortletEntity(), targetFragment.getId());
                            clearLayoutAttributes(request);
                            return;
                        }
                        catch (Exception e)
                        {
                            throw new PortletException("Unable to update page: "+e.getMessage(), e);
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
                if ( jsPageName != null && jsPageName.length() > 0 && jsPageName.indexOf(Folder.PATH_SEPARATOR) == -1 )
                {
                    try
                    {                
                        Folder parent = (Folder)requestPage.getParent();
                        if (parent != null)
                        {
                            String path = parent.getPath();
                            if (path.endsWith(Folder.PATH_SEPARATOR))
                            {
                                path = path + getEscapedName(jsPageName);
                            }
                            else
                            {
                                path = path + Folder.PATH_SEPARATOR
                                        + getEscapedName(jsPageName);
                            }
                            Page page = pageManager.newPage(path);
                            if ( layout == null || layout.length() == 0 )
                            {
                                layout = requestPage.getRootFragment().getName();
                            }
                            page.getRootFragment().setName(layout);
                            page.setDefaultDecorator(requestPage.getDefaultDecorator(Fragment.LAYOUT), Fragment.LAYOUT);
                            page.setDefaultDecorator(requestPage.getDefaultDecorator(Fragment.PORTLET), Fragment.PORTLET);
                            page.setTitle(jsPageTitle != null && !jsPageTitle.equals("") ? jsPageTitle : jsPageName);
                            page.setShortTitle(jsPageShortTitle != null
                                            && !jsPageShortTitle.equals("") ? jsPageShortTitle
                                            : jsPageName);
                            pageManager.updatePage(page);
                            clearLayoutAttributes(request);                            
                            List orderList = parent.getDocumentOrder();
                            if (orderList != null)
                            {
                                String name = page.getName();
                                if (orderList.indexOf(name) < 0)
                                {
                                    orderList.add(name);
                                    parent.setDocumentOrder(orderList);
                                    pageManager.updateFolder(parent);
                                }
                            }
                        }
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
                    if (jsPageTitle != null && !jsPageTitle.equals(""))
                    {
                        requestPage.setTitle(jsPageTitle);
                    }
                    if (jsPageShortTitle != null
                            && !jsPageShortTitle.equals(""))
                    {
                        requestPage.setShortTitle(jsPageShortTitle);
                    }
                    pageManager.updatePage(requestPage);
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
                    Folder parent = (Folder)requestPage.getParent();
                    if (parent != null)
                    {
                        List orderList = parent.getDocumentOrder();
                        if (orderList != null)
                        {
                            String name = requestPage.getName();
                            if (orderList.indexOf(name) > -1)
                            {
                                orderList.remove(name);
                                parent.setDocumentOrder(orderList);
                                pageManager.updateFolder(parent);
                            }
                        }
                    }

                    pageManager.removePage(requestPage);
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
                    Folder parent = (Folder) requestPage.getParent();
                    if (parent != null)
                    {
                        List orderList = parent.getDocumentOrder();
                        String name = requestPage.getName();
                        if (orderList != null)
                        {
                            int index = orderList.indexOf(name);
                            if (index > -1)
                            {
                                String type = requestPage.getType();
                                int i = index - 1;
                                while (i >= 0)
                                {
                                    String value = (String) orderList.get(i);
                                    if (value.endsWith(type))
                                    {
                                        orderList.remove(index);
                                        orderList.add(i, name);
                                        parent.setDocumentOrder(orderList);
                                        pageManager.updateFolder(parent);
                                        break;
                                    }
                                    i--;
                                }
                            }
                            else
                            {
                                orderList.add(name);
                                parent.setDocumentOrder(orderList);
                                pageManager.updateFolder(parent);
                            }
                        }
                        else
                        {
                            orderList = new ArrayList(4);
                            orderList.add(name);
                            parent.setDocumentOrder(orderList);
                            pageManager.updateFolder(parent);
                        }
                    }
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
                    Folder parent = (Folder) requestPage.getParent();
                    if (parent != null)
                    {
                        List orderList = parent.getDocumentOrder();
                        String name = requestPage.getName();
                        if (orderList != null)
                        {
                            int index = orderList.indexOf(name);
                            if (index > -1)
                            {
                                String type = requestPage.getType();
                                int i = index + 1;
                                while (i < orderList.size())
                                {
                                    String value = (String) orderList.get(i);
                                    if (value.endsWith(type))
                                    {
                                        orderList.remove(index);
                                        orderList.add(i, name);
                                        parent.setDocumentOrder(orderList);
                                        pageManager.updateFolder(parent);
                                        break;
                                    }
                                    i++;
                                }
                            }
                            else
                            {
                                orderList.add(name);
                                parent.setDocumentOrder(orderList);
                                pageManager.updateFolder(parent);
                            }
                        }
                        else
                        {
                            orderList = new ArrayList(4);
                            orderList.add(name);
                            parent.setDocumentOrder(orderList);
                            pageManager.updateFolder(parent);
                        }
                    }
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
                if (jsFolderName != null && jsFolderName.length() > 0
                        && jsFolderName.indexOf(Folder.PATH_SEPARATOR) == -1)
                {
                    try
                    {
                        Folder parent = (Folder) requestPage.getParent();
                        if (parent != null)
                        {
                            String path = parent.getPath();
                            if (path.endsWith(Folder.PATH_SEPARATOR))
                            {
                                path = path + getEscapedName(jsFolderName);
                            }
                            else
                            {
                                path = path + Folder.PATH_SEPARATOR
                                        + getEscapedName(jsFolderName);
                            }
                            Folder folder = pageManager.newFolder(path);
                            if (layout == null || layout.length() == 0)
                            {
                                layout = requestPage.getRootFragment()
                                        .getName();
                            }
                            folder.setDefaultDecorator(requestPage
                                    .getDefaultDecorator(Fragment.LAYOUT),
                                    Fragment.LAYOUT);
                            folder.setDefaultDecorator(requestPage
                                    .getDefaultDecorator(Fragment.PORTLET),
                                    Fragment.PORTLET);
                            folder
                                    .setTitle(jsFolderTitle != null
                                            && !jsFolderTitle.equals("") ? jsFolderTitle
                                            : jsFolderName);
                            folder
                                    .setShortTitle(jsFolderShortTitle != null
                                            && !jsFolderShortTitle.equals("") ? jsFolderShortTitle
                                            : jsFolderName);
                            pageManager.updateFolder(folder);

                            List orderList = parent.getDocumentOrder();
                            if (orderList != null)
                            {
                                String name = folder.getName();
                                if (orderList.indexOf(name) < 0)
                                {
                                    orderList.add(name);
                                    parent.setDocumentOrder(orderList);
                                    pageManager.updateFolder(parent);
                                }
                            }

                            // add default page
                            path = folder.getPath();
                            if (path.endsWith(Folder.PATH_SEPARATOR))
                            {
                                path = path + getEscapedName("default-page");
                            }
                            else
                            {
                                path = path + Folder.PATH_SEPARATOR
                                        + getEscapedName("default-page");
                            }
                            Page page = pageManager.newPage(path);
                            if (layout == null || layout.length() == 0)
                            {
                                layout = requestPage.getRootFragment()
                                        .getName();
                            }
                            page.getRootFragment().setName(layout);
                            page.setDefaultDecorator(requestPage
                                    .getDefaultDecorator(Fragment.LAYOUT),
                                    Fragment.LAYOUT);
                            page.setDefaultDecorator(requestPage
                                    .getDefaultDecorator(Fragment.PORTLET),
                                    Fragment.PORTLET);
                            page.setTitle(jsFolderName);
                            pageManager.updatePage(page);

                            orderList = folder.getDocumentOrder();
                            if (orderList != null)
                            {
                                String name = page.getName();
                                if (orderList.indexOf(name) < 0)
                                {
                                    orderList.add(name);
                                    folder.setDocumentOrder(orderList);
                                    pageManager.updateFolder(folder);
                                }
                            }
                        }
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
                    Folder parent = (Folder) requestPage.getParent();
                    if (parent != null)
                    {
                        if (jsFolderTitle != null && !jsFolderTitle.equals(""))
                        {
                            parent.setTitle(jsFolderTitle);
                        }
                        if (jsFolderShortTitle != null
                                && !jsFolderShortTitle.equals(""))
                        {
                            parent.setShortTitle(jsFolderShortTitle);
                        }
                        pageManager.updateFolder(parent);
                    }

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
                    Folder targetFolder = (Folder) requestPage.getParent();
                    Folder parent = (Folder) targetFolder.getParent();
                    if (parent != null)
                    {
                        List orderList = parent.getDocumentOrder();
                        if (orderList != null)
                        {
                            String name = targetFolder.getName();
                            if (orderList.indexOf(name) > -1)
                            {
                                orderList.remove(name);
                                parent.setDocumentOrder(orderList);
                                pageManager.updateFolder(parent);
                            }
                        }

                        // do not remove if the folder is root.
                        pageManager.removeFolder(targetFolder);
                    }
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
                    Folder targetFolder = (Folder) requestPage.getParent();
                    Folder parent = (Folder) targetFolder.getParent();
                    if (parent != null)
                    {
                        List orderList = parent.getDocumentOrder();
                        String name = targetFolder.getName();
                        if (orderList != null)
                        {
                            int index = orderList.indexOf(name);
                            if (index > -1)
                            {
                                int i = index - 1;
                                while (i >= 0)
                                {
                                    String value = (String) orderList.get(i);
                                    if (!value.endsWith(".psml")
                                            && !value.endsWith(".link"))
                                    {
                                        orderList.remove(index);
                                        orderList.add(i, name);
                                        parent.setDocumentOrder(orderList);
                                        pageManager.updateFolder(parent);
                                        break;
                                    }
                                    i--;
                                }
                            }
                            else
                            {
                                orderList.add(name);
                                parent.setDocumentOrder(orderList);
                                pageManager.updateFolder(parent);
                            }
                        }
                        else
                        {
                            orderList = new ArrayList(4);
                            orderList.add(name);
                            parent.setDocumentOrder(orderList);
                            pageManager.updateFolder(parent);
                        }
                    }
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
                    Folder targetFolder = (Folder) requestPage.getParent();
                    Folder parent = (Folder) targetFolder.getParent();
                    if (parent != null)
                    {
                        List orderList = parent.getDocumentOrder();
                        String name = targetFolder.getName();
                        if (orderList != null)
                        {
                            int index = orderList.indexOf(name);
                            if (index > -1)
                            {
                                int i = index + 1;
                                while (i < orderList.size())
                                {
                                    String value = (String) orderList.get(i);
                                    if (!value.endsWith(".psml")
                                            && !value.endsWith(".link"))
                                    {
                                        orderList.remove(index);
                                        orderList.add(i, name);
                                        parent.setDocumentOrder(orderList);
                                        pageManager.updateFolder(parent);
                                        break;
                                    }
                                    i++;
                                }
                            }
                            else
                            {
                                orderList.add(name);
                                parent.setDocumentOrder(orderList);
                                pageManager.updateFolder(parent);
                            }
                        }
                        else
                        {
                            orderList = new ArrayList(4);
                            orderList.add(name);
                            parent.setDocumentOrder(orderList);
                            pageManager.updateFolder(parent);
                        }
                    }
                }
                catch (Exception e)
                {
                    throw new PortletException(
                            "Unable to access folder for changing the document order: "
                                    + e.getMessage(), e);
                }
                return;
            }

            String theme = request.getParameter("theme");
            if ( theme != null && theme.length() > 0 && !theme.equals(requestPage.getDefaultDecorator(Fragment.LAYOUT)) )
            {
                requestPage.setDefaultDecorator(theme, Fragment.LAYOUT);
                try
                {
                    pageManager.updatePage(requestPage);
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
                    Fragment currentFragment = requestPage.getFragmentById(window.getId().toString());
                    Fragment fragmentToMove = requestPage.getFragmentById(fragmentId);
                    
                    if ( currentFragment == null || fragmentToMove == null )
                    {
                        // ignore no longer consistent page definition
                        return;
                    }
                    
                    ColumnLayout columnLayout;
                    try
                    {
                        columnLayout = new ColumnLayout(numColumns, layoutType, currentFragment.getFragments(), null);
                        columnLayout.addLayoutEventListener(new PageManagerLayoutEventListener(pageManager, requestPage, layoutType));
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
                    return;
                }
                
                String remove = request.getParameter("remove");
                if ( remove != null && remove.length() > 0 )
                {
                    Page page = null;
                    try
                    {
                        // TODO: for now retrieve the real Page instead of ContentPage
                        //       because removing fragments isn't working through the ContentFragment wrapping
                        page = pageManager.getPage(requestPage.getPath());
                    }
                    catch (Exception e)
                    {
                        throw new PortletException("Unable to retrieve page "+requestPage.getId(),e);
                    }

                    PortletWindow window = requestContext.getActionWindow();
                    Fragment currentFragment = page.getFragmentById(window.getId().toString());

                    if ( currentFragment == null )
                    {
                        // ignore no longer consistent page definition
                        return;
                    }
                    
                    removeFragment(page, currentFragment, fragmentId);
                    return;
                }
                
                String decorator = request.getParameter("decorator");
                if ( decorator != null )
                {
                    Fragment fragment = requestPage.getFragmentById(fragmentId);

                    if ( fragment == null )
                    {
                        // ignore no longer consistent page definition
                        return;
                    }
                    
                    if (decorator.trim().length() == 0)
                        fragment.setDecorator(null);
                    else
                        fragment.setDecorator(decorator);
                    try
                    {
                        pageManager.updatePage(requestPage);
                    }
                    catch (Exception e)
                    {
                        throw new PortletException("Unable to update page for fragment decorator: "+e.getMessage(), e);
                    }
                    return;
                }
                // evlach
                String decorators = request.getParameter("decorators");                
                // change style for all pages in user folder 
                String jsChangeUserPagesTheme = request.getParameter("jsChangeUserPagesTheme");
                if ( jsChangeUserPagesTheme != null )
                {
                   String user_pages_theme = request.getParameter("user_pages_theme");
                   try
                    {
                       Folder f = pageManager.getUserFolder(request.getRemoteUser());
                       applyStyle(f,user_pages_theme,Fragment.LAYOUT);
                       decorators=null;
                    }
                    catch (Exception e)
                    {
                       throw new PortletException("Unable to update folder for defUserLayoutDeco decorator: "+e.getMessage(), e);
                    }
                }                
                String jsChangeUserPortletsDeco = request.getParameter("jsChangeUserPortletsDeco");
                if ( jsChangeUserPortletsDeco != null )
                {                  
                   String user_portlets_deco = request.getParameter("user_portlets_deco");
                   try
                    {
                       Folder f = pageManager.getUserFolder(request.getRemoteUser());
                       applyStyle(f,user_portlets_deco,Fragment.PORTLET);
                        decorators = null; //do insert next if
                    }
                   catch (Exception e)
                    {
                        throw new PortletException("Unable to update folder for defUserPortletDeco decorator: "+e.getMessage(), e);
                    }
                }                                
                
                if ( decorators != null && decorators.length() > 1)
                {
                    Iterator fragmentsIter = requestPage.getRootFragment().getFragments().iterator();
                    while(fragmentsIter.hasNext())
                    {
                        Fragment fragment = (Fragment) fragmentsIter.next();
                        if ( fragment == null )
                        {
                            // ignore no longer consistent page definition
                            return;
                        }
                        
                        if (decorators.trim().length() == 0)
                            fragment.setDecorator(null);
                        else
                            fragment.setDecorator(decorators);
                    }
                    try
                    {
                        pageManager.updatePage(requestPage);
                    }
                    catch (Exception e)
                    {
                        throw new PortletException("Unable to update page for fragment decorator: "+e.getMessage(), e);
                    }
                    return;
                }                
            }
            
            String portlets = request.getParameter("portlets");
            if ( portlets != null && portlets.length() > 0 )
            {
                PortletWindow window = requestContext.getActionWindow();
                Fragment targetFragment = requestPage.getFragmentById(window.getId().toString());

                if ( targetFragment == null )
                {
                    // ignore no longer consistent page definition
                    return;
                }
                
                StringTokenizer tokenizer = new StringTokenizer(portlets, ",");            
                while (tokenizer.hasMoreTokens())
                {
                    String portlet = tokenizer.nextToken();
                    if (portlet.startsWith("box_"))
                    {
                        portlet = portlet.substring("box_".length());                        
                        addPortletToPage(requestPage, targetFragment, portlet);
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

    protected int getFragmentNestingLevel(Page page, String fragmentId)
    {
        Fragment root = page.getRootFragment();
        if ( root.getId().equals(fragmentId) )
        {
            return 0;
        }
        else
        {
            return getFragmentNestingLevel(root, 1, fragmentId);
        }
    }
    
    protected int getFragmentNestingLevel(Fragment parent, int level, String fragmentId)
    {
        Iterator iter = parent.getFragments().iterator();
        Fragment child;
        int childLevel;
        while ( iter.hasNext() )
        {
            child = (Fragment)iter.next();
            if (child.getId().equals(fragmentId))
            {
                return level;
            }
            else
            {
                childLevel = getFragmentNestingLevel(child, level+1, fragmentId);
                if ( childLevel != -1 )
                {
                    return childLevel;
                }
            }
        }
        return -1;
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
}
