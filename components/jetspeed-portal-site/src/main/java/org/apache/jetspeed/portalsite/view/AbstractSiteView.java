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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.portalsite.menu.StandardBackMenuDefinition;
import org.apache.jetspeed.portalsite.menu.StandardBreadcrumbsMenuDefinition;
import org.apache.jetspeed.portalsite.menu.StandardNavigationsMenuDefinition;
import org.apache.jetspeed.portalsite.menu.StandardPagesMenuDefinition;

/**
 * This abstract class defines the base implementation for
 * views of site content.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class AbstractSiteView
{
    /**
     * CURRENT_PAGE_PATH - expression used to match the current page
     */
    public final static String CURRENT_PAGE_PATH = "~";

    /**
     * ALT_CURRENT_PAGE_PATH - alternate expression used to match the current page
     */
    public final static String ALT_CURRENT_PAGE_PATH = "@";
    public final static char ALT_CURRENT_PAGE_PATH_CHAR = '@';
    public final static String ALT_CURRENT_PAGE_PATH_0 = "@0";

    /**
     * STANDARD_*_MENU_NAME - standard menu names
     */
    public final static String STANDARD_BACK_MENU_NAME = "back";
    public final static String STANDARD_BREADCRUMBS_MENU_NAME = "breadcrumbs";
    public final static String STANDARD_PAGES_MENU_NAME = "pages";
    public final static String STANDARD_NAVIGATIONS_MENU_NAME = "navigations";

    /**
     * CUSTOM_*_MENU_NAME - custom menu names
     */
    public final static String CUSTOM_PAGE_NAVIGATIONS_MENU_NAME = "page-navigations";

    /**
     * STANDARD_MENU_NAMES - set of supported standard menu names
     */
    private final static Set STANDARD_MENU_NAMES = new HashSet(3);
    static
    {
        STANDARD_MENU_NAMES.add(STANDARD_BACK_MENU_NAME);
        STANDARD_MENU_NAMES.add(STANDARD_BREADCRUMBS_MENU_NAME);
        STANDARD_MENU_NAMES.add(STANDARD_PAGES_MENU_NAME);
        STANDARD_MENU_NAMES.add(STANDARD_NAVIGATIONS_MENU_NAME);
    }

    /**
     * STANDARD_MENU_DEFINITION_LOCATORS - list of standard menu definition locators
     */
    private final static List STANDARD_MENU_DEFINITION_LOCATORS = new ArrayList(4);
    static
    {
        STANDARD_MENU_DEFINITION_LOCATORS.add(new SiteViewMenuDefinitionLocator(new StandardBackMenuDefinition()));
        STANDARD_MENU_DEFINITION_LOCATORS.add(new SiteViewMenuDefinitionLocator(new StandardBreadcrumbsMenuDefinition()));
        STANDARD_MENU_DEFINITION_LOCATORS.add(new SiteViewMenuDefinitionLocator(new StandardPagesMenuDefinition()));
        STANDARD_MENU_DEFINITION_LOCATORS.add(new SiteViewMenuDefinitionLocator(new StandardNavigationsMenuDefinition()));
    }

    /**
     * pageManager - PageManager component
     */
    private PageManager pageManager;

    /**
     * rootFolderView - root folder view instance
     */
    private Folder rootFolderView;

    /**
     * AbstractSiteView - constructor
     *
     * @param pageManager PageManager component instance
     */
    public AbstractSiteView(PageManager pageManager)
    {
        this.pageManager = pageManager;
    }

    /**
     * getPageManager - return PageManager component instance
     *
     * @return PageManager instance
     */
    public PageManager getPageManager()
    {
        return pageManager;
    }

    /**
     * getRootFolderView - access root folder view instance
     *
     * @return root folder view
     * @throws FolderNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public Folder getRootFolderView() throws FolderNotFoundException
    {
        // latently construct and return root folder view
        if (rootFolderView == null)
        {
            rootFolderView = createRootFolderView();
        }
        return rootFolderView;
    }

    /**
     * createRootFolderView - create and return root folder view instance
     *
     * @return root folder view
     * @throws FolderNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    protected abstract Folder createRootFolderView() throws FolderNotFoundException;

    /**
     * getNodeView - get single folder, page, or link view
     *               at relative or absolute path
     *
     * @param path single node path
     * @param currentNode current folder or page for relative paths or null
     * @param onlyViewable node required to be viewable
     * @param onlyVisible node required to be visible, (or current)
     * @return folder, page, or link node view
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public Node getNodeView(String path, Node currentNode, boolean onlyViewable, boolean onlyVisible) throws NodeNotFoundException
    {
        // determine current folder and page
        String currentPath = path;
        Folder currentFolder = null;
        Page currentPage = null;
        if (currentNode instanceof Page)
        {
            currentPage = (Page)currentNode;
            currentFolder = (Folder)currentPage.getParent();
        }
        else if (currentNode instanceof Folder)
        {
            currentFolder = (Folder)currentNode;
        }

        // match current page path
        if (currentPath.equals(CURRENT_PAGE_PATH) || currentPath.equals(ALT_CURRENT_PAGE_PATH))
        {
            // return current page if specified, (assume viewable)
            return currentPage;
        }

        // match current node path
        if (currentPath.equals(ALT_CURRENT_PAGE_PATH_0))
        {
            // return current node, (assume viewable)
            return currentNode;
        }

        // convert absolute path to a root relative search
        // and default current folder
        if (currentPath.startsWith(Folder.PATH_SEPARATOR))
        {
            currentPath = currentPath.substring(1);
            currentFolder = null;
        }
        if (currentFolder == null)
        {
            currentFolder = getRootFolderView();
        }

        // search for path based on current folder 
        while ((currentPath.length() > 0) && !currentPath.equals(Folder.PATH_SEPARATOR))
        {
            // parse relative sub-folder from path
            int separatorIndex = currentPath.indexOf(Folder.PATH_SEPARATOR);
            if (separatorIndex != -1)
            {
                // isolate sub-folder and continue search
                // using remaining paths
                String subfolder = currentPath.substring(0, separatorIndex);
                currentPath = currentPath.substring(separatorIndex+1);
                if (subfolder.equals(".."))
                {
                    // adjust current folder if parent exists
                    if (currentFolder.getParent() != null)
                    {
                        currentFolder = (Folder)currentFolder.getParent();
                    }
                    else
                    {
                        throw new NodeNotFoundException("Specified path " + path + " not found.");
                    }
                }
                else if (!subfolder.equals("."))
                {
                    // access sub-folder or return null if nonexistent
                    // or access forbidden
                    try
                    {
                        currentFolder = currentFolder.getFolder(subfolder);
                    }
                    catch (NodeException ne)
                    {
                        NodeNotFoundException nnfe = new NodeNotFoundException("Specified path " + path + " not found.");
                        nnfe.initCause(ne);
                        throw nnfe;
                    }
                    catch (NodeNotFoundException nnfe)
                    {
                        // check security access to folder not found in site view
                        checkAccessToNodeNotFound(currentFolder, subfolder);
                        // folder not found in site view
                        NodeNotFoundException nnfeWrapper = new NodeNotFoundException("Specified path " + path + " not found.");
                        nnfeWrapper.initCause(nnfe);
                        throw nnfeWrapper;
                    }
                }
            }
            else
            {
                // access remaining path as page, folder, or link node
                // view; return null if not found or not viewable/visible
                // and visibility is required
                try
                {
                    NodeSet children = currentFolder.getAll();
                    if (children != null)
                    {
                        Node node = children.get(currentPath);
                        if (((node instanceof Folder) || (node instanceof Page) || (node instanceof Link)) &&
                            (!onlyVisible || !node.isHidden() || (node == currentPage)) &&
                            (!onlyViewable || isViewable(node, onlyVisible)))
                        {
                            return node;
                        }
                    }
                }
                catch (NodeException ne)
                {
                    NodeNotFoundException nnfe = new NodeNotFoundException("Specified path " + path + " not found.");
                    nnfe.initCause(ne);
                    throw nnfe;
                }
                // check security access to folder node not found in site view
                checkAccessToNodeNotFound(currentFolder, currentPath);
                // folder node not found in site view
                throw new NodeNotFoundException("Specified path " + path + " not found or viewable/visible.");
            }
        }

        // path maps to current folder; return if viewable/visible
        // or visibility not required
        if ((!onlyVisible || !currentFolder.isHidden()) &&
            (!onlyViewable || isViewable(currentFolder, onlyVisible)))
        {
            return currentFolder;
        }
        throw new NodeNotFoundException("Specified path " + path + " not found or viewable/visible.");
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
    protected abstract void checkAccessToNodeNotFound(Folder folder, String folderName);

    /**
     * getNodeViews - get folder, page, or link views at
     *                relative or absolute path using simple path
     *                wildcards and character classes
     *
     * @param regexpPath regular expression node path
     * @param currentNode current folder or page for relative paths or null
     * @param onlyViewable nodes required to be viewable flag
     * @param onlyVisible node required to be visible, (or current)
     * @return list of folder, page, or link node views
     */
    public List getNodeViews(String regexpPath, Node currentNode, boolean onlyViewable, boolean onlyVisible)
    {
        // determine current folder and page
        String currentRegexpPath = regexpPath;
        Folder currentFolder = null;
        Page currentPage = null;
        if (currentNode instanceof Page)
        {
            currentPage = (Page)currentNode;
            currentFolder = (Folder)currentPage.getParent();
        }
        else if (currentNode instanceof Folder)
        {
            currentFolder = (Folder)currentNode;
        }

        // match current page path
        if (currentRegexpPath.equals(CURRENT_PAGE_PATH) || currentRegexpPath.equals(ALT_CURRENT_PAGE_PATH))
        {
            if (currentPage != null)
            {
                // return current page, (assume viewable)
                List views = new ArrayList(1);
                views.add(currentPage);
                return views;
            }
            else
            {
                // current page not specified
                return null;
            }
        }
        
        // convert pattern with indexed current node path expressions
        if (currentNode != null)
        {
            // match current node path
            if (currentRegexpPath.equals(ALT_CURRENT_PAGE_PATH_0))
            {
                // return current node, (assume viewable)
                List views = new ArrayList(1);
                views.add(currentNode);
                return views;
            }

            // match current node path expression
            int currentNodePathIndex = currentRegexpPath.indexOf(ALT_CURRENT_PAGE_PATH_CHAR);
            String [] currentNodePathElements = null;
            while (currentNodePathIndex != -1)
            {
                if (currentNodePathIndex+1 < currentRegexpPath.length())
                {
                    String currentNodePathElement = null;
                    char currentNodePathElementIndexChar = currentRegexpPath.charAt(currentNodePathIndex+1);
                    if ((currentNodePathElementIndexChar >= '0') && (currentNodePathElementIndexChar <= '9'))
                    {
                        // valid current node path pattern
                        int currentNodePathElementIndex = (int)(currentNodePathElementIndexChar-'0');
                        if (currentNodePathElementIndex > 0)
                        {
                            // use indexed current node path element
                            if (currentNodePathElements == null)
                            {
                                // note: leading '/' in path makes index one based
                                currentNodePathElements = currentNode.getPath().split(Folder.PATH_SEPARATOR);
                            }
                            if (currentNodePathElementIndex < currentNodePathElements.length)
                            {
                                currentNodePathElement = currentNodePathElements[currentNodePathElementIndex];
                            }
                        }
                        else
                        {
                            // use full current node path trimmed of separators
                            currentNodePathElement = currentNode.getPath();
                            if (currentNodePathElement.endsWith(Folder.PATH_SEPARATOR))
                            {
                                currentNodePathElement = currentNodePathElement.substring(0, currentNodePathElement.length()-1);
                            }
                            if (currentNodePathElement.startsWith(Folder.PATH_SEPARATOR))
                            {
                                currentNodePathElement = currentNodePathElement.substring(1);
                            }
                        }
                    }
                    else if (currentNodePathElementIndexChar == '$')
                    {
                        // use last current node path element
                        if (currentNodePathElements == null)
                        {
                            // note: leading '/' in path makes index one based
                            currentNodePathElements = currentNode.getPath().split(Folder.PATH_SEPARATOR);
                        }
                        currentNodePathElement = currentNodePathElements[currentNodePathElements.length-1];
                    }
                    // replace current node path expression
                    if (currentNodePathElement != null)
                    {
                        currentRegexpPath = currentRegexpPath.substring(0, currentNodePathIndex)+currentNodePathElement+currentRegexpPath.substring(currentNodePathIndex+2);
                        currentNodePathIndex += currentNodePathElement.length()-1;
                    }
                }
                currentNodePathIndex = currentRegexpPath.indexOf(ALT_CURRENT_PAGE_PATH_CHAR, currentNodePathIndex+1);
            }
        }

        // convert absolute path to a root relative search
        // and default current folder
        if (currentRegexpPath.startsWith(Folder.PATH_SEPARATOR))
        {
            currentRegexpPath = currentRegexpPath.substring(1);
            currentFolder = null;
        }
        if (currentFolder == null)
        {
            try
            {
                currentFolder = getRootFolderView();
            }
            catch (FolderNotFoundException fnfe)
            {
                return null;
            }
            catch (SecurityException se)
            {
                return null;
            }
        }

        // search for path based on current folder 
        while ((currentRegexpPath.length() > 0) && !currentRegexpPath.equals(Folder.PATH_SEPARATOR))
        {
            // parse relative sub-folder from path
            int separatorIndex = currentRegexpPath.indexOf(Folder.PATH_SEPARATOR);
            if (separatorIndex != -1)
            {
                // isolate sub-folder and continue search
                // using remaining paths
                String subfolder = currentRegexpPath.substring(0, separatorIndex);
                currentRegexpPath = currentRegexpPath.substring(separatorIndex+1);
                if (subfolder.equals(".."))
                {
                    // adjust current folder if parent exists
                    if (currentFolder.getParent() != null)
                    {
                        currentFolder = (Folder)currentFolder.getParent();
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (!subfolder.equals("."))
                {
                    try
                    {
                        // check for regular expression pattern
                        String subfolderPattern = pathRegexpPattern(subfolder);
                        if (subfolderPattern != null)
                        {
                            // follow all matching sub-folders
                            NodeSet subfolders = currentFolder.getFolders();
                            if (subfolders != null)
                            {
                                subfolders = subfolders.inclusiveSubset(subfolderPattern);
                                if (subfolders != null)
                                {
                                    // recursively process sub-folders if more than
                                    // one match, access single sub-folder, or return
                                    // null if nonexistent
                                    if (subfolders.size() > 1)
                                    {
                                        // recursively process matching sub-folders
                                        List views = null;
                                        Iterator subfoldersIter = subfolders.iterator();
                                        while (subfoldersIter.hasNext())
                                        {
                                            currentFolder = (Folder)subfoldersIter.next();
                                            List subfolderProxies = getNodeViews(currentRegexpPath, currentFolder, onlyViewable, onlyVisible);
                                            if ((subfolderProxies != null) && !subfolderProxies.isEmpty())
                                            {
                                                if (views == null)
                                                {
                                                    views = new ArrayList();
                                                }
                                                views.addAll(subfolderProxies);
                                            }
                                        }
                                        return views;
                                    }
                                    else if (subfolders.size() == 1)
                                    {
                                        // access single sub-folder
                                        currentFolder = (Folder)subfolders.iterator().next();
                                    }
                                    else
                                    {
                                        // no matching sub-folders
                                        return null;
                                    }
                                }
                                else
                                {
                                    // no matching sub-folders
                                    return null;
                                }
                            }
                            else
                            {
                                // no sub-folders
                                return null;
                            }
                        }
                        else
                        {
                            // access single sub-folder or return null if
                            // nonexistent by throwing exception
                            currentFolder = currentFolder.getFolder(subfolder);
                        }
                    }
                    catch (NodeException ne)
                    {
                        // could not access sub-folders
                        return null;
                    }
                    catch (NodeNotFoundException nnfe)
                    {
                        // could not access sub-folders
                        return null;
                    }
                    catch (SecurityException se)
                    {
                        // could not access sub-folders
                        return null;
                    }
                }
            }
            else
            {
                try
                {
                    // get all children of current folder
                    NodeSet children = currentFolder.getAll();
                    if (children != null)
                    {
                        // check for regular expression pattern
                        String pathPattern = pathRegexpPattern(currentRegexpPath);
                        if (pathPattern != null)
                        {
                            // copy children matching remaining path pattern as
                            // page, folder, or link views if viewable/visible or
                            // visibility not required
                            children = children.inclusiveSubset(pathPattern);
                            if ((children != null) && !children.isEmpty())
                            {
                                List views = null;
                                Iterator childrenIter = children.iterator();
                                while (childrenIter.hasNext())
                                {
                                    Node child = (Node)childrenIter.next(); 
                                    if (((child instanceof Folder) || (child instanceof Page) || (child instanceof Link)) &&
                                        (!onlyVisible || !child.isHidden() || (child == currentPage)) &&
                                        (!onlyViewable || isViewable(child, onlyVisible)))
                                    {
                                        if (views == null)
                                        {
                                            views = new ArrayList(children.size());
                                        }
                                        views.add(child);
                                    }
                                }
                                return views;
                            }
                        }
                        else
                        {
                            // access remaining path as page, folder, or link
                            // node view; return null if not found or not
                            // viewable and visibility is required
                            Node child = children.get(currentRegexpPath);
                            if (((child instanceof Folder) || (child instanceof Page) || (child instanceof Link)) &&
                                (!onlyVisible || !child.isHidden() || (child == currentPage)) &&
                                (!onlyViewable || isViewable(child, onlyVisible)))
                            {
                                List views = new ArrayList(1);
                                views.add(currentFolder);
                                return views;
                            }
                        }
                    }
                    
                }
                catch (NodeException ne)
                {
                }
                catch (SecurityException se)
                {
                }

                // no children match or available
                return null;
            }
        }

        // path maps to current folder; return if viewable/visible
        // or visibility not required
        if ((!onlyVisible || !currentFolder.isHidden()) &&
            (!onlyViewable || isViewable(currentFolder, onlyVisible)))
        {
            List views = new ArrayList(1);
            views.add(currentFolder);
            return views;
        }
        return null;
    }

    /**
     * pathRegexpPattern - tests for and converts simple path wildcard
     *                     and character class regular expressions to
     *                     perl5/standard java pattern syntax
     *
     * @param regexp - candidate path regular expression
     * @return - converted pattern or null if no regular expression
     */
    private static String pathRegexpPattern(String regexp)
    {
        // convert expression to pattern
        StringBuffer pattern = null;
        for (int i = 0, limit = regexp.length(); (i < limit); i++)
        {
            char regexpChar = regexp.charAt(i);
            switch (regexpChar)
            {
                case '*':
                case '.':
                case '?':
                case '[':
                    if (pattern == null)
                    {
                        pattern = new StringBuffer(regexp.length()*2);
                        pattern.append(regexp.substring(0, i));
                    }
                    switch (regexpChar)
                    {
                        case '*':
                            pattern.append(".*");
                            break;
                        case '.':
                            pattern.append("\\.");
                            break;
                        case '?':
                            pattern.append('.');
                            break;
                        case '[':
                            pattern.append('[');
                            break;
                    }
                    break;
                default:
                    if (pattern != null)
                    {
                        pattern.append(regexpChar);
                    }
                    break;
            }
        }

        // return converted pattern or null if not a regular expression
        if (pattern != null)
            return pattern.toString();
        return null;
    }

    /**
     * isViewable - tests for node visibility in view
     *
     * @param node test node view
     * @param onlyVisible nodes required to be visible
     * @return - viewable flag
     */
    private static boolean isViewable(Node node, boolean onlyVisible)
    {
        // pages and links are always considered viewable
        if ((node instanceof Page) || (node instanceof Link))
        {
            return true;
        }
        // folders must be tested for viewable and visible
        // child nodes
        if (node instanceof Folder)
        {
            try
            {
                NodeSet children = ((Folder) node).getAll();
                if (children != null)
                {
                    Iterator childrenIter = children.iterator();
                    while (childrenIter.hasNext())
                    {
                        Node child = (Node)childrenIter.next();
                        if ((!onlyVisible || !child.isHidden()) && isViewable(child, onlyVisible))
                        {
                            return true;
                        }
                    }
                }
            }
            catch (NodeException ne)
            {
            }
            catch (SecurityException se)
            {
            }
            return false;
        }
        // templates, fragments, and dynamic page are not visible
        return false;
    }

    /**
     * getStandardMenuNames - get set of available standard menu names
     *  
     * @return menu names set
     */
    public Set getStandardMenuNames()
    {
        // return constant standard menu names
        return STANDARD_MENU_NAMES;
    }

    /**
     * getStandardMenuDefinitionLocators - get list of available standard
     *                                     menu definition locators
     *  
     * @return menu definition locators list
     */
    public List getStandardMenuDefinitionLocators()
    {
        // return constant standard menu definition locators
        return STANDARD_MENU_DEFINITION_LOCATORS;
    }

    /**
     * getMenuDefinitionLocators - get list of view node menu definition locators
     *
     * @param node node view
     * @return definition locator list
     */
    public abstract List getMenuDefinitionLocators(Node node);

    /**
     * getMenuDefinitionLocator - get named view node menu definition locator
     *
     * @param node node view
     * @param name menu definition name
     * @return menu definition locator
     */
    public abstract SiteViewMenuDefinitionLocator getMenuDefinitionLocator(Node node, String name);

    /**
     * getProfileLocatorName - get profile locator name from view node
     *
     * @param node node view
     * @return profile locator name or null
     */
    public abstract String getProfileLocatorName(Node node);

    /**
     * getManagedPage - get concrete page instance from page view
     *  
     * @param page page view
     * @return managed page
     */
    public abstract Page getManagedPage(Page page);

    /**
     * getManagedLink - get concrete link instance from link view
     *  
     * @param link link view
     * @return managed link
     */
    public abstract Link getManagedLink(Link link);

    /**
     * getManagedFolder - get concrete folder instance from folder view
     *  
     * @param folder folder view
     * @return managed folder
     */
    public abstract Folder getManagedFolder(Folder folder);

    /**
     * getManagedPageTemplate - get concrete page template instance from
     *                          page template view
     *  
     * @param pageTemplate page template view
     * @return managed page template
     */
    public abstract PageTemplate getManagedPageTemplate(PageTemplate pageTemplate);

    /**
     * getManagedDynamicPage - get concrete dynamic page instance from
     *                         dynamic page view
     *  
     * @param dynamicPage dynamic page view
     * @return managed dynamic page
     */
    public abstract DynamicPage getManagedDynamicPage(DynamicPage dynamicPage);

    /**
     * getManagedFragmentDefinition - get concrete fragment definition
     *                                instance from fragment definition
     *                                view
     *  
     * @param fragmentDefinition fragment definition view
     * @return managed dynamic page
     */
    public abstract FragmentDefinition getManagedFragmentDefinition(FragmentDefinition fragmentDefinition);
}