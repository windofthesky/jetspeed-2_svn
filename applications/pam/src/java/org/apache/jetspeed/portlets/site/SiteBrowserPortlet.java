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
import java.util.Iterator;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;

/**
 * This portlet is a tree browser user interface for viewing site resoures:
 * pages and folders.
 * 
 * @author <a href="mailto:jford@apache.com">Jeremy Ford </a>
 * @version $Id$
 */
public class SiteBrowserPortlet extends GenericServletPortlet
{

    private PortletContext context;

    private PageManager pageManager;

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        context = getPortletContext();
        pageManager = (PageManager) context.getAttribute(PortletApplicationResources.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager) { throw new PortletException(
                "Failed to find the Page Manager on portlet initialization"); }
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");

        TreeControl control = (TreeControl) request.getPortletSession().getAttribute("j2_tree");
        if (control == null)
        {
            Folder root = null;
            try
            {
                root = pageManager.getFolder("/");
            }
            catch (FolderNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (InvalidFolderException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (NodeException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (root != null)
            {
                control = buildTree(root, request.getLocale());
                request.getPortletSession().setAttribute("j2_tree", control);
            }
        }
        request.setAttribute("j2_tree", control);

        super.doView(request, response);

    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
            IOException
    {
        TreeControl control = (TreeControl) actionRequest.getPortletSession().getAttribute("j2_tree");
        //assert control != null
        if (control != null)
        {
            String node = actionRequest.getParameter("node");
            if (node != null)
            {
                TreeControlNode controlNode = control.findNode(node);
                if (controlNode != null)
                {
                    controlNode.setExpanded(!controlNode.isExpanded());
                }
            }

            String selectedNode = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_NODE);
            if (selectedNode != null)
            {
                control.selectNode(selectedNode);
                TreeControlNode child = control.findNode(selectedNode);
                if (child != null)
                {
                    String domain = child.getDomain();
                    String name = child.getName();

                    PortletMessaging.cancel(actionRequest, PortletApplicationResources.SITE_PORTLET,
                            PortletApplicationResources.CURRENT_FOLDER);
                    PortletMessaging.cancel(actionRequest, PortletApplicationResources.SITE_PORTLET,
                            PortletApplicationResources.CURRENT_PAGE);
                    
                    String attrName = PortletApplicationResources.CURRENT_FOLDER;
                    if (domain.equals("PAGE_DOMAIN"))
                    {
                        attrName = PortletApplicationResources.CURRENT_PAGE;
                    }

                    PortletMessaging.publish(actionRequest, PortletApplicationResources.SITE_PORTLET, attrName,
                            name);

                    /*
                     * if(domain.equals("FOLDER_DOMAIN")) { try { Folder folder =
                     * pageManager.getFolder(name);
                     * actionRequest.getPortletSession().setAttribute(PortletApplicationResources.CURRENT_FOLDER,
                     * folder, PortletSession.APPLICATION_SCOPE); } catch
                     * (FolderNotFoundException e) { // TODO Auto-generated
                     * catch block e.printStackTrace(); } catch
                     * (InvalidFolderException e) { // TODO Auto-generated catch
                     * block e.printStackTrace(); } catch (NodeException e) { //
                     * TODO Auto-generated catch block e.printStackTrace(); } }
                     * else if(domain.equals("PAGE_DOMAIN")) { try { Page page =
                     * pageManager.getPage(name);
                     * actionRequest.getPortletSession().setAttribute(PortletApplicationResources.CURRENT_PAGE,
                     * page, PortletSession.APPLICATION_SCOPE); } catch
                     * (PageNotFoundException e) { // TODO Auto-generated catch
                     * block e.printStackTrace(); } catch (NodeException e) { //
                     * TODO Auto-generated catch block e.printStackTrace(); } }
                     */
                }
            }
        }
    }

    private TreeControl buildTree(Folder folder, Locale locale)
    {

        TreeControlNode root = new TreeControlNode(folder.getName(), null, folder.getTitle(locale),
                PortletApplicationResources.PORTLET_URL, null, true, "FOLDER_DOMAIN");

        TreeControl control = new TreeControl(root);

        buildFolderNodes(folder, root, locale);

        return control;
    }

    private void buildFolderNodes(Folder folder, TreeControlNode parent, Locale locale)
    {
        NodeSet childFolders = null;
        try
        {
            childFolders = folder.getFolders();
        }
        catch (FolderNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (DocumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (childFolders != null)
        {
            Iterator folderIter = childFolders.iterator();
            while (folderIter.hasNext())
            {
                Folder childFolder = (Folder) folderIter.next();                
                TreeControlNode childNode = new TreeControlNode(childFolder.getPath(), null, childFolder
                        .getTitle(locale), PortletApplicationResources.PORTLET_URL, null, false, "FOLDER_DOMAIN");
                parent.addChild(childNode);
                buildFolderNodes(childFolder, childNode, locale);
            }

            buildPageNodes(folder, parent, locale);
        }
    }

    private void buildPageNodes(Folder folder, TreeControlNode node, Locale locale)
    {
        NodeSet pages = null;
        try
        {
            pages = folder.getPages();
        }
        catch (NodeException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (pages != null)
        {
            Iterator pageIter = pages.iterator();

            while (pageIter.hasNext())
            {
                Page page = (Page) pageIter.next();
                TreeControlNode child = new TreeControlNode(page.getPath(), null, page.getTitle(locale),
                        PortletApplicationResources.PORTLET_URL, null, false, "PAGE_DOMAIN");
                node.addChild(child);
            }
        }
    }
}