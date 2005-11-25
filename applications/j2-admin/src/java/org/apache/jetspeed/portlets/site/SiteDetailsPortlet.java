/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
import java.io.NotSerializableException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.state.MutableNavigationalState;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.portals.bridges.frameworks.VelocityFrameworkPortlet;
import org.apache.portals.messaging.PortletMessaging;

/**
 * SiteDetailsPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: SiteDetailsPortlet.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class SiteDetailsPortlet extends VelocityFrameworkPortlet
{

    private PageManager pageManager;

    // private LinkedHashMap tabMap = new LinkedHashMap();

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);        
        PortletContext context = getPortletContext();
        pageManager = (PageManager) context.getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager) { throw new PortletException(
                "Failed to find the Page Manager on portlet initialization"); }

        Map externalSupportMap = new HashMap();
        externalSupportMap.put("folderBean", pageManager);
        externalSupportMap.put("pageBean", pageManager);
        setExternalSupport(externalSupportMap);
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        // Get the messages from the browser
        String currentFolder = (String) PortletMessaging.consume(request,
                PortletApplicationResources.SITE_PORTLET, PortletApplicationResources.CURRENT_FOLDER);
        String currentPage = (String) PortletMessaging.consume(request,
                PortletApplicationResources.SITE_PORTLET, PortletApplicationResources.CURRENT_PAGE);
        
        if (currentFolder != null)
        {
            request.setAttribute("site.folder.key", currentFolder);
            changePortletView(request, response, "folder-view");
        }
        if (currentPage != null)
        {
            request.setAttribute("site.page.key", currentPage);
            changePortletView(request, response, "page-view");
        }
        
        String parent = request.getParameter("parent");
        if (parent != null)
        {
            System.out.println("parent = " + parent);            
            PortletMessaging.publish(request,
                    PortletApplicationResources.SITE_PORTLET, "parent", parent);                                
        }
        super.doView(request, response);

    }

    private void changePortletView(PortletRequest request, PortletResponse response, String view)
    {
        //this.setDefaultViewPage("page-view");
        PortletWindow window = (PortletWindow)request.getAttribute(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE);
        RequestContext context = (RequestContext)request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        MutableNavigationalState state = (MutableNavigationalState)context.getPortalURL().getNavigationalState();
        if (window != null)
            state.clearParameters(window);            
        this.setLogicalView(request, response, view, PortletMode.VIEW);        
    }
    
    public String processSaveFolderAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        System.out.println("Processing SAVE action.");
        FolderProxyBean proxy = (FolderProxyBean)bean;
        String key = proxy.getKey();
        try
        {
            Folder folder = pageManager.getFolder(key);
            proxy.update(folder);
            pageManager.updateFolder(folder);
            
            notifyUpdate(request, response, key);            
            
        }
        catch (JetspeedException e)
        {
            e.printStackTrace();            
        }
        return "folder-view:success";
    }

    public String processAddFolderAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        FolderProxyBean proxy = (FolderProxyBean)bean;
        String key = proxy.getKey();
        try
        {
            String fullKey = getFullKey(request, key);
            System.out.println("Saving . " + fullKey);
            Folder folder = pageManager.newFolder(fullKey);
            
            
            proxy.update(folder);
            pageManager.updateFolder(folder);
            
            notifyUpdate(request, response, fullKey);            
        }
        catch (JetspeedException e)
        {
            e.printStackTrace();
        }
        return "folder-view:success";
    }

    public String processAddPageAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        PageProxyBean proxy = (PageProxyBean)bean;
        String key = proxy.getKey();
        try
        {
            String fullKey = getFullKey(request, key);
            System.out.println("Saving . " + fullKey);
            Page page = pageManager.newPage(fullKey);
            // TODO: Get System Wide defaults for decorators
            page.getRootFragment().setName("jetspeed-layouts::VelocityTwoColumns");
            page.setDefaultDecorator("tigris", Fragment.LAYOUT);
            page.setDefaultDecorator("tigris", Fragment.PORTLET);
            // or:
            //String templateFolder = actionRequest.getPreferences().getValue("newUserTemplateDirectory", "/_user/template/");
            // TODO: copy the entire dir tree, not just the default-page.psml                 
            //Page template = pageManager.getPage(templateFolder + "default-page.psml");                
            //Page copy = pageManager.copyPage(template, Folder.USER_FOLDER + userName + "/default-page.psml");
            
            
            proxy.update(page);
            pageManager.updatePage(page);
            
            notifyUpdate(request, response, fullKey);            
        }
        catch (JetspeedException e)
        {
            e.printStackTrace();
        }
        return "folder-view:success";
    }
   
    public String processDeleteFolderAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        FolderProxyBean proxy = (FolderProxyBean)bean;
        String key = proxy.getKey();
        try
        {
            String fullKey = getFullKey(request, key);
            
            Folder folder = pageManager.getFolder(fullKey);
            pageManager.removeFolder(folder);
            
            notifyUpdate(request, response, fullKey);            
        }
        catch (JetspeedException e)
        {
            e.printStackTrace();
        }
        return "folder-view:success";
    }
    
    private String getFullKey(ActionRequest request, String key)
    {
        String parent = (String)PortletMessaging.consume(request, PortletApplicationResources.SITE_PORTLET, "parent");
        if (parent == null)
        {
            return "/" + key;
        }
        if (!parent.endsWith("/"))
            parent += "/";

        return parent + key;        
    }
    
    public String processSavePageAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        System.out.println("Processing SAVE Page action.");
        PageProxyBean proxy = (PageProxyBean)bean;
        String key = proxy.getKey();
        try
        {            
            Page page = pageManager.getPage(key);
            proxy.update(page);
            pageManager.updatePage(page);

            notifyUpdate(request, response, proxy.getKey());
            
        }
        catch (JetspeedException e)
        {
            e.printStackTrace();            
        }
        return "page-view:success";
    }

    private void notifyUpdate(ActionRequest request, ActionResponse response, String selected)
    throws NotSerializableException
    {
        PortletMessaging.publish(request,
                PortletApplicationResources.SITE_PORTLET,
                PortletApplicationResources.MESSAGE_REFRESH, "true");
//        PortletMessaging.publish(request,
//                PortletApplicationResources.SITE_PORTLET,
//                PortletApplicationResources.MESSAGE_SELECTED, proxy
//                        .getLookupKey());
        changePortletView(request, response, "folder-view");
        
    }
}