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
import java.util.LinkedList;
import java.util.List;
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
import org.apache.jetspeed.om.page.Link;
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
        externalSupportMap.put("linkBean", pageManager);
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
        String currentLink = (String) PortletMessaging.consume(request,
                PortletApplicationResources.SITE_PORTLET, PortletApplicationResources.CURRENT_LINK);
        List errors = (List) PortletMessaging.consume(request,
                PortletApplicationResources.SITE_PORTLET, "ERRORS");
        
        if (errors != null)
            this.getContext(request).put("ERRORS", errors);
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
        if (currentLink != null)
        {
            request.setAttribute("site.link.key", currentLink);
            changePortletView(request, response, "link-view");
        }
        
        String newRecordView = request.getParameter("new");
        if (newRecordView != null)
        {
            this.startNewRecord(request, newRecordView);
        }
        
        String parent = request.getParameter("parent");
        if (parent != null)
        {
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
        String delete = request.getParameter("Delete");
        if (delete != null)
        {
            return this.processDeleteFolderAction(request, response, bean);
        }
        
        FolderProxyBean proxy = (FolderProxyBean)bean;
        String key = proxy.getKey();
        try
        {
            Folder folder = pageManager.getFolder(key);
            proxy.update(folder);
            pageManager.updateFolder(folder);
            
            notifyUpdate(request, response, key, new NodeInfo(key, "update", PSMLTreeLoader.FOLDER_DOMAIN));            
            
        }
        catch (JetspeedException e)
        {
            this.publishStatusMessage(request, e, "save folder: ");
            throw new PortletException(e);
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
            Folder folder = pageManager.newFolder(fullKey);
                        
            proxy.update(folder);
            pageManager.updateFolder(folder);
            notifyUpdate(request, response, fullKey, new NodeInfo(folder.getPath(), "insert", PSMLTreeLoader.FOLDER_DOMAIN));            
        }
        catch (JetspeedException e)
        {
            this.publishStatusMessage(request, e, "add folder: ");
            throw new PortletException(e);
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
            
            notifyUpdate(request, response, fullKey, new NodeInfo(page.getPath(), "insert", PSMLTreeLoader.PAGE_DOMAIN));            
        }
        catch (JetspeedException e)
        {
            this.publishStatusMessage(request, e, "add page: ");
            throw new PortletException(e);            
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
            //String fullKey = getFullKey(request, key);
            
            Folder folder = pageManager.getFolder(key);
            pageManager.removeFolder(folder);
            
            notifyUpdate(request, response, key, new NodeInfo(key, "delete", PSMLTreeLoader.FOLDER_DOMAIN));            
            
        }
        catch (JetspeedException e)
        {
            this.publishStatusMessage(request, e, "delete folder: ");
            throw new PortletException(e);
        }        
        return "folder-view:success";
    }
    
    private String getFullKey(ActionRequest request, String key)
    {
        String parent = (String)PortletMessaging.consume(request, PortletApplicationResources.SITE_PORTLET, "parent");
        if (parent == null)
        {
            if (key.startsWith("/"))
                return key;
            return "/" + key;
        }
        if (!parent.endsWith("/") && !key.startsWith("/"))
            parent += "/";

        return parent + key;        
    }
    
    public String processSavePageAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        String delete = request.getParameter("Delete");
        if (delete != null)
        {
            return this.processDeletePageAction(request, response, bean);
        }
        
        PageProxyBean proxy = (PageProxyBean)bean;
        String key = proxy.getKey();
        try
        {            
            Page page = pageManager.getPage(key);
            proxy.update(page);
            pageManager.updatePage(page);

            notifyUpdate(request, response, proxy.getKey(), new NodeInfo(key, "update", PSMLTreeLoader.PAGE_DOMAIN));
            
        }
        catch (JetspeedException e)
        {
            this.publishStatusMessage(request, e, "save page: ");
            throw new PortletException(e);
        }
        return "page-view:success";
    }

    public String processSaveLinkAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        String delete = request.getParameter("Delete");
        if (delete != null)
        {
            return this.processDeleteLinkAction(request, response, bean);
        }
        
        LinkProxyBean proxy = (LinkProxyBean)bean;
        String key = proxy.getKey();
        try
        {            
            Link link = pageManager.getLink(key);
            proxy.update(link);
            pageManager.updateLink(link);

            notifyUpdate(request, response, proxy.getKey(), new NodeInfo(key, "update", PSMLTreeLoader.LINK_DOMAIN));
            
        }
        catch (JetspeedException e)
        {
            this.publishStatusMessage(request, e, "save link: ");
            throw new PortletException(e);
        }
        return "link-view:success";
    }

    public String processAddLinkAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        LinkProxyBean proxy = (LinkProxyBean)bean;
        String key = proxy.getKey();
        try
        {
            String fullKey = getFullKey(request, key);
            Link link = pageManager.newLink(fullKey);
            
            
            proxy.update(link);
            pageManager.updateLink(link);
            
            notifyUpdate(request, response, fullKey, new NodeInfo(link.getPath(), "insert", PSMLTreeLoader.LINK_DOMAIN));            
        }
        catch (JetspeedException e)
        {
            this.publishStatusMessage(request, e, "add link: ");
            throw new PortletException(e);
        }
        return "link-view:success";
    }
    
    private void notifyUpdate(ActionRequest request, ActionResponse response, String selected, NodeInfo nodeInfo)
    throws NotSerializableException
    {
        PortletMessaging.publish(request,
                PortletApplicationResources.SITE_PORTLET,
                PortletApplicationResources.NODE_UPDATED, nodeInfo);
        
        
//        PortletMessaging.publish(request,
//                PortletApplicationResources.SITE_PORTLET,
//                PortletApplicationResources.MESSAGE_REFRESH, "true");
        changePortletView(request, response, "folder-view");
        
    }
    
    public String processDeletePageAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        PageProxyBean proxy = (PageProxyBean)bean;
        String key = proxy.getKey();
        try
        {
            String fullKey = getFullKey(request, key);
            
            Page page = pageManager.getPage(fullKey);
            pageManager.removePage(page);
            
            notifyUpdate(request, response, fullKey, new NodeInfo(fullKey, "delete", PSMLTreeLoader.PAGE_DOMAIN));            
        }
        catch (JetspeedException e)
        {
            this.publishStatusMessage(request, e, "delete page: ");
            throw new PortletException(e);
        }
        return "folder-view:success";
    }

    public String processDeleteLinkAction(ActionRequest request, ActionResponse response, Object bean) 
    throws PortletException,
           IOException
    {
        LinkProxyBean proxy = (LinkProxyBean)bean;
        String key = proxy.getKey();
        try
        {
            //String fullKey = getFullKey(request, key);
            
            Link link = pageManager.getLink(key);
            pageManager.removeLink(link);
            
            notifyUpdate(request, response, key, new NodeInfo(key, "delete", PSMLTreeLoader.LINK_DOMAIN));            
        }
        catch (JetspeedException e)
        {
            this.publishStatusMessage(request, e, "delete link: ");
            throw new PortletException(e);
        }
        return "folder-view:success";
    }
    
    public void publishStatusMessage(PortletRequest request, Throwable e, String message)
    {
        String msg = message + ": " + e.toString();
        Throwable cause = e.getCause();
        if (cause != null)
        {
            msg = msg + ", " + cause.getMessage();
        }
        List errors = new LinkedList();
        try
        {
            errors.add(msg);
            PortletMessaging.publish(request, PortletApplicationResources.SITE_PORTLET, "ERRORS", errors);
        }
        catch (Exception ee)
        {
            System.err.println("Failed to publish message: " + e);
        }        
    }
 
    
}