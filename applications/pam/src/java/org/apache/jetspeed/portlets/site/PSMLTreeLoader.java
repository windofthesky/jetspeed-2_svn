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


import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.webapp.admin.TreeControlNode;

/**
 * PSML Tree Loader
 * Encapsulates the handling of PSML content into a tree view
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PSMLTreeLoader 
{
    public static final String ROOT_DOMAIN = "ROOT_DOMAIN";
    public static final String FOLDER_DOMAIN = "FOLDER_DOMAIN";
    public static final String PAGE_DOMAIN = "PAGE_DOMAIN";
	
	public final static String PORTLET_URL = "portlet_url";
		
    private AbstractPSMLTreePortlet portlet;
    private PageManager pm;
        
    public PSMLTreeLoader(PageManager pm, AbstractPSMLTreePortlet portlet)
    {
        this.pm = pm;
        this.portlet = portlet;
    }
    
    public TreeControlNode createRootNode(Folder folder, Locale locale)
    {        
        TreeControlNode root = 
            new TreeControlNode(folder.getPath(), // unique id 
                                portlet.getRootImage(), // image                 
                                folder.getTitle(locale), // label
                                PortletApplicationResources.PORTLET_URL, // action 
                                null, // target 
                                true, // expanded 
                                "0"); // domain
                
        return root;
    }    
    
    public TreeControlNode createFolderNode(Folder folder, Locale locale, String domain) throws JetspeedException
    {        
        TreeControlNode node = 
            new TreeControlNode(
                    folder.getPath(), // unique id
                    portlet.getFolderImage(folder),  // image
                    portlet.getFolderTitle(folder, locale), // label
                    PortletApplicationResources.PORTLET_URL, // action 
                    null, // target
                    false, // expanded
                    domain, // domain
                    "Folder", // CSS
                    true); // lazy load
        
        node.setExpandWhenClicked(true);
        node.setTitle(folder.getName());
        node.setLeaf(folder.getAll().isEmpty());
        
        return node;
    }
    	
	public TreeControlNode createPageNode(Page page, Locale locale, String domain) 
    {
        String title = portlet.getPageTitle(page, locale);
        
    	TreeControlNode issueNode = 
            new TreeControlNode(
                    page.getPath(), // unique id
                    portlet.getImageForContentType("text/html"), // image
                    title,  // label
                    PortletApplicationResources.PORTLET_URL, // action 
                    null, // target
                    false, // expanded
                    domain, // domain
					"Document", // css
					true); // lazy load
    	
        issueNode.setTitle(title);        
    	issueNode.setLeaf(true);
    	
    	return issueNode;
    }
    
    public TreeControlNode createLinkNode(Link link, Locale locale, String domain) 
    {
    	TreeControlNode linkNode = 
            new TreeControlNode(
                    link.getPath(), // unique id
                    portlet.getLinkImage(), // image 
                    link.getTitle(locale), // label
                    PortletApplicationResources.PORTLET_URL, // action
                    null, // target
                    false, // expanded
                    domain, // domain
					null, // css
                    true); // lazy load
        
        linkNode.setTitle(link.getTitle());        
        linkNode.setLeaf(true);
    	
    	return linkNode;
    }
        
    public void loadChildren(Folder folder, TreeControlNode parent, Locale locale)
    {
        NodeSet childFolders = null;
        NodeSet childPages = null;
        NodeSet childLinks = null;
        try
        {
            int ilevel = ((new Integer(parent.getDomain()).intValue()) + 1);        
            String level = Integer.toString(ilevel);
            
            childFolders = folder.getFolders();
            childPages = folder.getPages();
            childLinks = folder.getLinks();

            if (childFolders != null)
            {
                Iterator folderIter = childFolders.iterator();
                while (folderIter.hasNext())
                {
                    Folder childFolder = (Folder) folderIter.next();                
                    TreeControlNode childNode = createFolderNode(childFolder, locale, level);
                    parent.addChild(childNode);
                }
            }
            if (childPages != null)
            {
                Iterator pagesIter = childPages.iterator();
                while (pagesIter.hasNext())
                {
                    Page childPage = (Page) pagesIter.next();                
                    TreeControlNode childNode = createPageNode(childPage, locale, level);
                    parent.addChild(childNode);
                }
            }
            if (childLinks != null)
            {
                Iterator linksIter = childLinks.iterator();
                while (linksIter.hasNext())
                {
                    Link childLink = (Link) linksIter.next();                
                    TreeControlNode childNode = createLinkNode(childLink, locale, level);
                    parent.addChild(childNode);
                }
            }
            parent.setLoaded(true);
        }
        catch (JetspeedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
    }
       	
	public static Comparator psmlComparator = new PSMLObjectComparator();
	
	public static Comparator getComparator() 
    {
		return psmlComparator;
	}
    
    private static class PSMLObjectComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            Node lhs = (Node)o1;
            Node rhs = (Node)o2;
            
            return rhs.getName().compareTo(lhs.getName());
        }
    }
}
