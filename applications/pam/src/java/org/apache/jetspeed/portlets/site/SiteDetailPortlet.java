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
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.portlet.ServletPortlet;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;

/**
 * This portlet is a tabbed editor user interface for editing site resoures: pages and folders.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @version $Id$
 */
public class SiteDetailPortlet extends ServletPortlet
{
    private PortletContext context;
    private PageManager pageManager;

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
        
        super.doView(request, response);
    }
}
