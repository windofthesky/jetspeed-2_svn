/*
 * Copyright 2004 The Apache Software Foundation.
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
import java.util.LinkedHashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.portlets.pam.beans.TabBean;
import org.apache.portals.bridges.common.GenericServletPortlet;

/**
 * This portlet is a tabbed editor user interface for editing site resoures: pages and folders.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @version $Id$
 */
public class SiteDetailPortlet extends GenericServletPortlet
{
    private PortletContext context;
    private PageManager pageManager;
    
    private LinkedHashMap tabMap = new LinkedHashMap();

    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        context = getPortletContext();                
        pageManager = (PageManager)context.getAttribute(PortletApplicationResources.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager)
        {
            throw new PortletException("Failed to find the Page Manager on portlet initialization");
        }
        
        TabBean tb1 = new TabBean("site_details");
        TabBean tb2 = new TabBean("site_security");
        
        tabMap.put(tb1.getId(), tb1);
        tabMap.put(tb2.getId(), tb2);
    }

    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        String currentFolder = (String)request.getPortletSession().getAttribute(PortletApplicationResources.CURRENT_FOLDER, PortletSession.APPLICATION_SCOPE);
        String currentPage = (String)request.getPortletSession().getAttribute(PortletApplicationResources.CURRENT_PAGE, PortletSession.APPLICATION_SCOPE);
        
        if(currentFolder != null)
        {
            try
            {
                Folder folder = pageManager.getFolder(currentFolder);
                request.setAttribute("folder", folder);
            } catch (FolderNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvalidFolderException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NodeException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        else if(currentPage != null)
        {
            try
            {
                Page page = pageManager.getPage(currentPage);
                request.setAttribute("page", page);
            } catch (PageNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NodeException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        request.setAttribute("tabs", tabMap.values());
        
        TabBean selectedTab = (TabBean) request.getPortletSession().getAttribute(PortletApplicationResources.REQUEST_SELECT_SITE_TAB, PortletSession.APPLICATION_SCOPE);
        if(selectedTab == null)
        {
            selectedTab = (TabBean) tabMap.values().iterator().next();
        }
        
        request.setAttribute(PortletApplicationResources.REQUEST_SELECT_TAB, selectedTab);
        
        super.doView(request, response);
    }
    
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
	{
        
        String selectedTab = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_SITE_TAB);
        if(selectedTab != null)
        {
            TabBean tab = (TabBean) tabMap.get(selectedTab);
            actionRequest.getPortletSession().setAttribute(PortletApplicationResources.REQUEST_SELECT_SITE_TAB, tab, PortletSession.APPLICATION_SCOPE);
        }
        
        String actionType = actionRequest.getParameter("action_type");
        if(actionType == null)
        {
            actionType = "folder";
        }
        
        String acl = actionRequest.getParameter("acl");
        String nodeName = actionRequest.getParameter("node_name");
        
        if(nodeName != null)
        {
            try
            {
                Node node = null;
                if(actionType.equals("folder"))
                {
                    node = pageManager.getFolder(nodeName);
                }
                else
                {
                    node = pageManager.getPage(nodeName);
                }
                
                if(node != null)
                {
                    // acls depricated: node.setAcl(acl);
                }
                
                //how to store ??
            }
            catch (NodeNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (NodeException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	}
}
