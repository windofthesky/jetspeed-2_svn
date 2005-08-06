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
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.portals.bridges.frameworks.VelocityFrameworkPortlet;
import org.apache.portals.messaging.PortletMessaging;

/**
 * SiteDetailsPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
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
        }
        super.doView(request, response);

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
            
            //PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH, "true");
            //PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED, userName);
            
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
        System.out.println("Processing SAVE action.");
        FolderProxyBean proxy = (FolderProxyBean)bean;
        String key = proxy.getKey();
        try
        {
            Folder folder = pageManager.newFolder(key);
            
            
            //Folder folder = pageManager.getFolder(key);
            proxy.update(folder);
            pageManager.updateFolder(folder);
            
            //PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH, "true");
            //PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED, userName);
            
        }
        catch (JetspeedException e)
        {
            e.printStackTrace();
        }
        return "folder-view:success";
    }
    
}