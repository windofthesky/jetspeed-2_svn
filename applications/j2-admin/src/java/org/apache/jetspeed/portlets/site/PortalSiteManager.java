/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.gems.dojo.AbstractDojoVelocityPortlet;
import org.apache.velocity.context.Context;

/**
 * Manage the Portal Site
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortalSiteManager extends AbstractDojoVelocityPortlet 
{
    protected final Log log = LogFactory.getLog(this.getClass());
    
    // components
    protected PageManager pageManager;
    protected PortletRegistry registry;
    protected DecorationFactory decorationFactory;
    
    // session
    protected final static String SESSION_FOLDERS = "jetspeed.site.manager.folders";
    protected final static String SESSION_ROOT = "jetspeed.site.manager.root";
    
    // context
    public final static String FOLDERS = "folders";
    public final static String JSROOT = "jsroot";   
    public static final String ALL_SECURITY_REFS = "allSecurityRefs";
    
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        pageManager = (PageManager) getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);        
        if (null == pageManager) 
        { 
            PortletException pe = new PortletException("Failed to find the Page Manager on SiteViewController initialization");
            throw new RuntimeException(pe); 
        }
        registry = (PortletRegistry) getPortletContext().getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        if (null == registry)
        {
            PortletException pe = new PortletException("Failed to find the Portlet Registry on SiteViewController initialization");
            throw new RuntimeException(pe);             
        }
        decorationFactory = (DecorationFactory) getPortletContext().getAttribute(CommonPortletServices.CPS_DECORATION_FACTORY);
        if (null == decorationFactory)
        {
            PortletException pe = new PortletException("Failed to find the Decoration Factory on SiteViewController initialization");
            throw new RuntimeException(pe);             
        }                
        
    }
    
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        try
        {
            String jsroot = determineRootFolder(request);
            RequestContext requestContext = 
                (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
            this.getContext(request).put("page-decorations", decorationFactory.getPageDecorations(requestContext));
            this.getContext(request).put("portlet-decorations", decorationFactory.getPortletDecorations(requestContext));
            this.getContext(request).put("themes", decorationFactory.getDesktopPageDecorations(requestContext));
            this.getContext(request).put("userTree", determineuserTree(request));
            this.getContext(request).put("defaultLayout", request.getPreferences().getValue("defaultLayout", "jetspeed-layouts::VelocityTwoColumns"));            
            this.getContext(request).put(FOLDERS, retrieveFolders(request, jsroot));
            this.getContext(request).put(ALL_SECURITY_REFS, pageManager.getPageSecurity().getSecurityConstraintsDefs());                    
        }
        catch (Exception e)
        {
            throw new PortletException("Failed to get root folder");
        }
        
        super.doView(request, response);
    }
    
    protected String determineRootFolder(RenderRequest request)
    {
        String jsroot = request.getParameter(JSROOT);
        if (jsroot == null || jsroot.equals(""))
        {
            jsroot = request.getPreferences().getValue("root", "/_user/" + request.getRemoteUser() + "/");
        }
        this.getContext(request).put(JSROOT, jsroot);
        return jsroot;
    }
    protected String determineuserTree(RenderRequest request)
    {
    	String userTree;
    	userTree = request.getPreferences().getValue("displayUserTree","false");
    	return userTree;
    }
    public Folder retrieveFolders(RenderRequest request, String root)
    throws PortletException
    {
        try
        {
            Folder folder = pageManager.getFolder(root);
            return folder;
        }
        catch (Exception e)
        {
            log.error("Failed to retrieve folders ", e);
            throw new PortletException("Failed to get root folder");
        }
    }
    
    protected void includeHeaderContent(HeaderResource headerResource)
    {
        headerResource.dojoAddCoreLibraryRequire("dojo.lang.*");
        //headerResource.dojoAddCoreLibraryRequire("dojo.dnd.*");
        headerResource.dojoAddCoreLibraryRequire("dojo.dnd.HtmlDragManager");
        headerResource.dojoAddCoreLibraryRequire("dojo.dnd.DragAndDrop");
        headerResource.dojoAddCoreLibraryRequire("dojo.dnd.HtmlDragAndDrop");
        
        headerResource.dojoAddCoreLibraryRequire("dojo.event.*");
        headerResource.dojoAddCoreLibraryRequire("dojo.io");
                
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.ContentPane");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.LayoutContainer");
        
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Tree");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeRPCController");        
        // headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeLoadingControllerV3");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeSelector");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeNode");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeContextMenu");
        
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.ValidationTextbox");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.ComboBox");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Checkbox");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Dialog");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Button");
        
        headerResource.dojoAddModuleLibraryRequire( "jetspeed.desktop.core" );
        headerResource.dojoAddModuleLibraryRequire( "jetspeed.widget.EditorTable" );        
    }

    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        doPreferencesEdit(request, response);
    }
    
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, java.io.IOException
    {
        String add = request.getParameter("Save");
        if (add != null)
        { 
            processPreferencesAction(request, actionResponse);
        }
    }    
}
