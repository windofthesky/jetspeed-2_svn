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
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;

/**
 * This portlet is a tree browser user interface for viewing site resoures:
 * pages and folders.
 * 
 * @author <a href="mailto:jford@apache.com">Jeremy Ford </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SiteBrowserPortlet.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class SiteBrowserPortlet extends AbstractPSMLTreePortlet
{
    private PortletContext context;
    private static final String SITE_TREE_ATTRIBUTE = "site-tree";

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        context = getPortletContext();
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");

        TreeControl control = prepareSiteTree(request);
        request.setAttribute(SITE_TREE_ATTRIBUTE, control);

        super.doView(request, response);

    }

    private TreeControl prepareSiteTree(RenderRequest request)
    {
        String refresh = (String)PortletMessaging.consume(request, 
                PortletApplicationResources.SITE_PORTLET, PortletApplicationResources.MESSAGE_REFRESH);
        
        TreeControl control = (TreeControl) request.getPortletSession().getAttribute(SITE_TREE_ATTRIBUTE);
        if (refresh != null || control == null)
        {
            Folder root = null;
            try
            {
                root = pageManager.getFolder(psmlRoot);
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
                request.getPortletSession().setAttribute(SITE_TREE_ATTRIBUTE, control);
            }
        }
        return control;
    }
    
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
            IOException
    {
        TreeControl control = (TreeControl) actionRequest.getPortletSession().getAttribute(SITE_TREE_ATTRIBUTE);
        //assert control != null
        if (control != null)
        {
            String node = actionRequest.getParameter("node");
            if (node != null)
            {
                TreeControlNode controlNode = control.findNode(node);
                if (controlNode != null && controlNode.isLazy() && !controlNode.isLoaded()) 
                {
                    //loader.loadChildren(actionRequest, controlNode, refToURIMap);
                    String domain = controlNode.getDomain();
                    //if (domain.equals(PSMLTreeLoader.FOLDER_DOMAIN))
                    {
                        try
                        {
                            Folder folder = pageManager.getFolder(controlNode.getName());
                            loader.loadChildren(folder, controlNode, actionRequest.getLocale());
                        }
                        catch (JetspeedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                controlNode.setExpanded(!controlNode.isExpanded());
                //actionRequest.getPortletSession().setAttribute("tree_anchor", node);
                
                if(controlNode.isExpanded() && controlNode != control.getRoot())
                {
                    TreeControlNode[] siblings = controlNode.getParent().findChildren();
                    for(int i=0; i<siblings.length; i++)
                    {
                        TreeControlNode sibling = siblings[i];
                        if(sibling != controlNode)
                        {
                            sibling.setExpanded(false);
                        }
                    }
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

        TreeControlNode root = loader.createRootNode(folder, locale);
        
        TreeControl control = new TreeControl(root);
        loader.loadChildren(folder, root, locale);

        return control;
    }


}