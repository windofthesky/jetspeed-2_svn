/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.portalsite.view;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class defines a physical view of site content.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PhysicalSiteView extends AbstractSiteView
{
    private static final List<SiteViewMenuDefinitionLocator> NULL_LOCATORS = new ArrayList<SiteViewMenuDefinitionLocator>(0);
    
    /**
     * userPrincipal - user principal for view
     */
    private String userPrincipal;
    
    /**
     * menuDefinitionLocatorsCache - cached menu definition locators
     */
    private ConcurrentHashMap<String,List<SiteViewMenuDefinitionLocator>> menuDefinitionLocatorsCache = new ConcurrentHashMap<String,List<SiteViewMenuDefinitionLocator>>();
    
    /**
     * PhysicalSiteView - basic constructor
     *
     * @param pageManager PageManager component instance
     */
    public PhysicalSiteView(PageManager pageManager, String userPrincipal)
    {
        super(pageManager);
        this.userPrincipal = userPrincipal;
    }

    /**
     * createRootFolderView - create and return root folder view instance
     *
     * @return root folder view
     * @throws FolderNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    protected Folder createRootFolderView() throws FolderNotFoundException
    {
        try
        {
            // get concrete root folder from page manager
            return getPageManager().getFolder(Folder.PATH_SEPARATOR);
        }
        catch (NodeException ne)
        {
            FolderNotFoundException fnfe = new FolderNotFoundException("Root folder not found");
            fnfe.initCause(ne);
            throw fnfe;
        }
        catch (NodeNotFoundException nnfe)
        {
            FolderNotFoundException fnfe = new FolderNotFoundException("Root folder not found");
            fnfe.initCause(nnfe);
            throw fnfe;
        }        
    }

    /**
     * checkAccessToFolderNotFound - checks security access to child folder
     *                               nodes not found in site view when accessed
     *                               directly
     *
     * @param folder parent view folder
     * @param folderName name of child folder in view to check
     * @throws SecurityException if view access to folder not granted
     */
    protected void checkAccessToNodeNotFound(Folder folder, String folderName)
    {
        // additional security checks not required for physical view access
    }

    /**
     * getMenuDefinitionLocators - get list of view node menu definition locators
     *
     * @param node node view
     * @return definition locator list
     */
    public List<SiteViewMenuDefinitionLocator> getMenuDefinitionLocators(Node node)
    {
        // access cached menu definition locators
        String path = node.getPath();
        List<SiteViewMenuDefinitionLocator> locators = menuDefinitionLocatorsCache.get(path);
        if (locators == null)
        {
            if (node instanceof Folder)
            {
                // merge folder menu definition locators from most to least
                // specific along inheritance folder graph by name
                Folder folder = (Folder)node;
                while (folder != null)
                {
                    // get menu definitions from inheritance folders and
                    // merge into menu definition locators
                    locators = SiteViewUtils.mergeMenuDefinitionLocators(folder.getMenuDefinitions(), folder, folder.getPath(), false, locators);
                    folder = (Folder)folder.getParent();
                }
                // merge standard menu definition locator defaults
                locators = SiteViewUtils.mergeMenuDefinitionLocators(getStandardMenuDefinitionLocators(), locators);        
            }
            else if (node instanceof Page)
            {
                // merge page and parent folder menu definition locators
                // by name, (most specific page definitions are merged first
                // since they override any folder definitions); note parent
                // folder menu definitions include standard menu definition
                // locator defaults
                Page page = (Page)node;
                Folder folder = (Folder)node.getParent();
                locators = SiteViewUtils.mergeMenuDefinitionLocators(page.getMenuDefinitions(), page, folder.getPath(), true, null);
                locators = SiteViewUtils.mergeMenuDefinitionLocators(getMenuDefinitionLocators(folder), locators);
            }
            else if (node instanceof DynamicPage)
            {
                // merge page and parent folder menu definition locators
                // by name, (most specific page definitions are merged first
                // since they override any folder definitions); note parent
                // folder menu definitions include standard menu definition
                // locator defaults
                DynamicPage dynamicPage = (DynamicPage)node;
                Folder folder = (Folder)node.getParent();
                locators = SiteViewUtils.mergeMenuDefinitionLocators(dynamicPage.getMenuDefinitions(), dynamicPage, folder.getPath(), true, null);
                locators = SiteViewUtils.mergeMenuDefinitionLocators(getMenuDefinitionLocators(folder), locators);
            }
            else if (node instanceof PageTemplate)
            {
                // merge only page template menu definition locators by name
                PageTemplate pageTemplate = (PageTemplate)node;
                Folder folder = (Folder)node.getParent();
                locators = SiteViewUtils.mergeMenuDefinitionLocators(pageTemplate.getMenuDefinitions(), pageTemplate, folder.getPath(), false, null);
            }
            locators = ((locators != null) ? locators : NULL_LOCATORS);
            List<SiteViewMenuDefinitionLocator> cachedLocators = menuDefinitionLocatorsCache.putIfAbsent(path, locators);
            locators = ((cachedLocators != null) ? cachedLocators : locators);
        }
        return ((locators != NULL_LOCATORS) ? locators : null);
    }

    /**
     * getMenuDefinitionLocators - get list of view node menu definition locators
     *
     * @param node node view
     * @return definition locator list
     */
    public SiteViewMenuDefinitionLocator getMenuDefinitionLocator(Node node, String name)
    {
        // get menu definition locators and find by name
        List<SiteViewMenuDefinitionLocator> locators = getMenuDefinitionLocators(node);
        if (locators != null)
        {
            return SiteViewUtils.findMenuDefinitionLocator(locators, name);
        }
        return null;
    }

    /**
     * getProfileLocatorName - get profile locator name from view node
     *
     * @param node node view
     * @return profile locator name or null
     */
    public String getProfileLocatorName(Node node)
    {
        // profile locators not applicable in physical view
        return null;
    }

    /**
     * getManagedPage - get concrete page instance from page view
     *  
     * @param page page view
     * @return managed page
     */
    public Page getManagedPage(Page page)
    {
        // physical view is directly managed
        return page;
    }

    /**
     * getManagedLink - get concrete link instance from link view
     *  
     * @param link link view
     * @return managed link
     */
    public Link getManagedLink(Link link)
    {
        // physical view is directly managed
        return link;
    }

    /**
     * getManagedFolder - get concrete folder instance from folder view
     *  
     * @param folder folder view
     * @return managed folder
     */
    public Folder getManagedFolder(Folder folder)
    {
        // physical view is directly managed
        return folder;
    }

    /**
     * getManagedPageTemplate - get concrete page template instance from
     *                          page template view
     *  
     * @param pageTemplate page template view
     * @return managed page template
     */
    public PageTemplate getManagedPageTemplate(PageTemplate pageTemplate)
    {
        // physical view is directly managed
        return pageTemplate;
    }

    /**
     * getManagedDynamicPage - get concrete dynamic page instance from
     *                         dynamic page view
     *  
     * @param dynamicPage dynamic page view
     * @return managed dynamic page
     */
    public DynamicPage getManagedDynamicPage(DynamicPage dynamicPage)
    {
        // physical view is directly managed
        return dynamicPage;
    }

    /**
     * getManagedFragmentDefinition - get concrete fragment definition
     *                                instance from fragment definition
     *                                view
     *  
     * @param fragmentDefinition fragment definition view
     * @return managed dynamic page
     */
    public FragmentDefinition getManagedFragmentDefinition(FragmentDefinition fragmentDefinition)
    {
        // physical view is directly managed
        return fragmentDefinition;
    }

    /**
     * getUserFolderPath - return primary concrete root user folder path
     *
     * @return user folder path or null
     */
    public String getUserFolderPath()
    {
        return ((userPrincipal != null) ? Folder.USER_FOLDER+userPrincipal : null);
    }

    /**
     * getBaseFolderPath - return primary concrete root base folder path
     *
     * @return base folder path or null
     */
    public String getBaseFolderPath()
    {
        return Folder.PATH_SEPARATOR;        
    }
}
