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
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
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
        TreeControl control = (TreeControl) request.getPortletSession().getAttribute(SITE_TREE_ATTRIBUTE);
        
        String refresh = (String)PortletMessaging.consume(request, 
                PortletApplicationResources.SITE_PORTLET, PortletApplicationResources.MESSAGE_REFRESH);
        
        NodeInfo nodeUpdated = (NodeInfo)PortletMessaging.consume(request,
                PortletApplicationResources.SITE_PORTLET, PortletApplicationResources.NODE_UPDATED);
        
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
        else if(nodeUpdated != null) 
        {
            try 
            {
                String action = nodeUpdated.getAction();
                
                if(/* node.isLoaded() && */ action.equals("delete")) 
                {
                    TreeControlNode node = control.findNode(nodeUpdated.getName());
                    control.removeNode(node);
                }
                else if (action.equals("update"))
                {
                    TreeControlNode node = control.findNode(nodeUpdated.getName());
                    String domain = node.getDomain();
                    if (domain.equals(PSMLTreeLoader.FOLDER_DOMAIN))
                    {
                        Folder folder = pageManager.getFolder(node.getName());
                        if (folder != null)
                        {
                            String title = folder.getTitle();
                            node.setLabel(title);
                        }
                    }
                    else if (domain.equals(PSMLTreeLoader.PAGE_DOMAIN))
                    {
                        Page page = pageManager.getPage(node.getName());
                        if (page != null)
                        {
                            String title = page.getTitle();
                            node.setLabel(title);
                        }                        
                    }
                    else if (domain.equals(PSMLTreeLoader.LINK_DOMAIN))
                    {
                        Link link = pageManager.getLink(node.getName());
                        if (link!= null)
                        {
                            String title = link.getTitle();
                            node.setLabel(title);
                        }                        
                    }                    
                }
                else if(/* node.isLoaded() && */ action.equals("insert"))
                {                    
                    if (nodeUpdated.getDomain().equals(PSMLTreeLoader.FOLDER_DOMAIN))
                    {
                        Folder folder = pageManager.getFolder(nodeUpdated.getName());
                        if (folder != null)
                        {
                            Folder parentFolder = (Folder)folder.getParent();                            
                            TreeControlNode parent = control.findNode(parentFolder.getPath());
                            if (parent != null)
                            {
                                if (parent.isLoaded() == false)
                                {
                                    loader.loadChildren(parentFolder, parent, request.getLocale());
                                }
                                else
                                {
                                    TreeControlNode childNode = loader.createFolderNode(folder, request.getLocale(), "");
                                    parent.addChild(childNode);                                    
                                }
                            }
                        }
                    }
                    else if (nodeUpdated.getDomain().equals(PSMLTreeLoader.PAGE_DOMAIN))
                    {                        
                        Page page = pageManager.getPage(nodeUpdated.getName());
                        if (page != null)
                        {                 
                            Folder parentFolder = (Folder)page.getParent();
                            TreeControlNode parent = control.findNode(parentFolder.getPath());
                            if (parent != null)
                            {
                                if (parent.isLoaded() == false)
                                {
                                    loader.loadChildren(parentFolder, parent, request.getLocale());
                                }
                                else
                                {
                                    TreeControlNode childNode = loader.createPageNode(page, request.getLocale(), "");
                                    parent.addChild(childNode);
                                }
                            }
                        }                    
                    }
                    else if (nodeUpdated.getDomain().equals(PSMLTreeLoader.LINK_DOMAIN))
                    {
                        Link link = pageManager.getLink(nodeUpdated.getName());
                        if (link != null)
                        {
                            Folder parentFolder = (Folder)link.getParent();                                                            
                            TreeControlNode parent = control.findNode(parentFolder.getPath());
                            if (parent != null)
                            {
                                if (parent.isLoaded() == false)
                                {
                                    loader.loadChildren(parentFolder, parent, request.getLocale());
                                }                                
                                else
                                {
                                    TreeControlNode childNode = loader.createLinkNode(link, request.getLocale(), "");
                                    parent.addChild(childNode);                                    
                                }
                            }
                        }                    
                    }                    
                }
            } 
            catch(Exception e) 
            {
                e.printStackTrace();
            }
        }
        return control;
    }
        
    private int getIndex(TreeControlNode parent, TreeControlNode child) {
        int myindex = -1;
        TreeControlNode[] children = parent.findChildren();
        for (int i = 0; i < children.length; i++) {
            TreeControlNode node = children[i];
            if(child == node) {
                myindex = i;
                break;
            }
        }
        return myindex;
    }
    
    public void txTest()
    {
        Page[] pages = new Page[3];
        pages[0] = pageManager.newPage("/tx__test1.psml");
        pages[1] = pageManager.newPage("/tx__test2.psml");
        pages[2] = pageManager.newPage("/tx__test3.psml");
        try
        {
            pageManager.addPages(pages);
        }
        catch (Exception e)
        {
            System.out.println("Exception adding pages" + e);
            e.printStackTrace();
            
        }        
    }
    
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
            IOException
    {
        // DST: FOR TX ROLLBACK TESTING: txTest();
        
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
                    PortletMessaging.cancel(actionRequest, PortletApplicationResources.SITE_PORTLET,
                            PortletApplicationResources.CURRENT_LINK);
                    
                    String attrName = PortletApplicationResources.CURRENT_FOLDER;
                    if (domain.equals("PAGE_DOMAIN"))
                    {
                        attrName = PortletApplicationResources.CURRENT_PAGE;
                    }
                    else if (domain.equals("LINK_DOMAIN"))
                    {
                        attrName = PortletApplicationResources.CURRENT_LINK;
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