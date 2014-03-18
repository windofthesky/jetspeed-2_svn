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
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.portalsite.menu.StandardBackMenuDefinition;
import org.apache.jetspeed.portalsite.menu.StandardBreadcrumbsMenuDefinition;
import org.apache.jetspeed.portalsite.menu.StandardNavigationsMenuDefinition;
import org.apache.jetspeed.portalsite.menu.StandardPagesMenuDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * APPEND_PATH - expression used to match the menu definition path
     */
    public final static String MENU_DEFINITION_PATH = "+";
    public final static char MENU_DEFINITION_PATH_CHAR = '+';

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
    private final static Set<String> STANDARD_MENU_NAMES = new HashSet<String>(3);
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
    private final static List<SiteViewMenuDefinitionLocator> STANDARD_MENU_DEFINITION_LOCATORS = new ArrayList<SiteViewMenuDefinitionLocator>(4);
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
     * @param menuPath menu definition path or null
     * @param onlyConcrete node required to be concrete folder, page, or link
     * @param onlyViewable node required to be viewable
     * @param onlyVisible node required to be visible, (or current)
     * @return folder, page, or link node view
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public Node getNodeView(String path, Node currentNode, String menuPath, boolean onlyConcrete, boolean onlyViewable, boolean onlyVisible) throws NodeNotFoundException
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

        // convert path with indexed current node path expressions
        if (currentNode != null)
        {
            currentPath = substituteCurrentPathExpressions(currentNode, currentPath);
        }

        // convert path with menu path prefix or expressions
        if (menuPath != null)
        {
            currentPath = substituteMenuPathExpressions(menuPath, currentPath);
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
                        if ((node != null) && (!onlyConcrete || isConcreteNode(node)) &&
                            (!onlyVisible || isVisible(node, currentPage)) &&
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
        if ((!onlyVisible || isVisible(currentFolder, null)) &&
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
     * @param menuPath menu definition path or null
     * @param onlyConcrete node required to be concrete folder, page, or link
     * @param onlyViewable nodes required to be viewable flag
     * @param onlyVisible node required to be visible, (or current)
     * @return list of folder, page, or link node views
     */
    public List<Node> getNodeViews(String regexpPath, Node currentNode, String menuPath, boolean onlyConcrete, boolean onlyViewable, boolean onlyVisible)
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
                List<Node> views = new ArrayList<Node>(1);
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
                List<Node> views = new ArrayList<Node>(1);
                views.add(currentNode);
                return views;
            }

            // substitute current node path expression
            currentRegexpPath = substituteCurrentPathExpressions(currentNode, currentRegexpPath);
        }

        // convert pattern with menu path prefix or expressions
        if (menuPath != null)
        {
            currentRegexpPath = substituteMenuPathExpressions(menuPath, currentRegexpPath);
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
                                        List<Node> views = null;
                                        for (Node folderNode : subfolders)
                                        {
                                            currentFolder = (Folder)folderNode;
                                            List<Node> subfolderViews = getNodeViews(currentRegexpPath, currentFolder, menuPath, onlyConcrete, onlyViewable, onlyVisible);
                                            if ((subfolderViews != null) && !subfolderViews.isEmpty())
                                            {
                                                if (views == null)
                                                {
                                                    views = new ArrayList<Node>();
                                                }
                                                views.addAll(subfolderViews);
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
                                List<Node> views = null;
                                for (Node child : children)
                                {
                                    if ((!onlyConcrete || isConcreteNode(child)) &&
                                        (!onlyVisible || isVisible(child, currentPage)) &&
                                        (!onlyViewable || isViewable(child, onlyVisible)))
                                    {
                                        if (views == null)
                                        {
                                            views = new ArrayList<Node>(children.size());
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
                            if ((child != null) && (!onlyConcrete || isConcreteNode(child)) &&
                                (!onlyVisible || isVisible(child, currentPage)) &&
                                (!onlyViewable || isViewable(child, onlyVisible)))
                            {
                                List<Node> views = new ArrayList<Node>(1);
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
        if ((!onlyVisible || isVisible(currentFolder, null)) &&
            (!onlyViewable || isViewable(currentFolder, onlyVisible)))
        {
            List<Node> views = new ArrayList<Node>(1);
            views.add(currentFolder);
            return views;
        }
        return null;
    }
    
    /**
     * substituteCurrentPathExpressions - Substitute current path expressions in specified path.
     * 
     * @param currentNode current node
     * @param path path
     * @return substituted path
     */
    private static String substituteCurrentPathExpressions(Node currentNode, String path)
    {
        // match current node path expression
        String [] currentNodePathElements = null;
        int pathIndex = path.indexOf(ALT_CURRENT_PAGE_PATH_CHAR);
        while (pathIndex != -1)
        {
            if (pathIndex+1 < path.length())
            {
                String currentNodePathElement = null;
                char pathElementIndexChar = path.charAt(pathIndex+1);
                if ((pathElementIndexChar >= '0') && (pathElementIndexChar <= '9'))
                {
                    // valid current node path pattern
                    int pathElementIndex = (int)(pathElementIndexChar-'0');
                    if (pathElementIndex > 0)
                    {
                        // use indexed current node path element
                        if (currentNodePathElements == null)
                        {
                            // note: leading '/' in path makes index one based
                            currentNodePathElements = currentNode.getPath().split(Folder.PATH_SEPARATOR);
                        }
                        if (pathElementIndex < currentNodePathElements.length)
                        {
                            currentNodePathElement = currentNodePathElements[pathElementIndex];
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
                else if (pathElementIndexChar == '$')
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
                    path = path.substring(0, pathIndex)+currentNodePathElement+path.substring(pathIndex+2);
                    pathIndex += currentNodePathElement.length()-1;
                }
            }
            pathIndex = path.indexOf(ALT_CURRENT_PAGE_PATH_CHAR, pathIndex+1);
        }
        return path;
    }

    /**
     * substituteMenuPathExpressions - Substitute menu path expressions in specified path.
     * 
     * @param menuPath menu definition path
     * @param path path
     * @return substituted path
     */
    private static String substituteMenuPathExpressions(String menuPath, String path)
    {
        // match menu path expression
        String strippedMenuPath = null;
        int pathIndex = path.indexOf(MENU_DEFINITION_PATH_CHAR);
        while (pathIndex != -1)
        {
            if (strippedMenuPath == null)
            {
                strippedMenuPath = menuPath.substring(1);
                if (strippedMenuPath.endsWith(Folder.PATH_SEPARATOR))
                {
                    strippedMenuPath = menuPath.substring(0, strippedMenuPath.length()-1);
                }
            }
            if (strippedMenuPath.length() > 0)
            {
                if (pathIndex == 0)
                {
                    path = Folder.PATH_SEPARATOR+strippedMenuPath+path.substring(1);
                    pathIndex = path.indexOf(MENU_DEFINITION_PATH_CHAR, strippedMenuPath.length()+1);                    
                }
                else
                {
                    path = path.substring(0, pathIndex)+strippedMenuPath+path.substring(pathIndex+1);
                    pathIndex = path.indexOf(MENU_DEFINITION_PATH_CHAR, pathIndex+strippedMenuPath.length());                    
                }
            }
            else
            {
                if ((pathIndex > 0) && (pathIndex+1 < path.length()) && (path.charAt(pathIndex-1) == Folder.PATH_SEPARATOR_CHAR) && (path.charAt(pathIndex+1) == Folder.PATH_SEPARATOR_CHAR))
                {
                    path = path.substring(0, pathIndex)+path.substring(pathIndex+2);
                }
                else
                {
                    path = path.substring(0, pathIndex)+path.substring(pathIndex+1);
                }
                pathIndex = path.indexOf(MENU_DEFINITION_PATH_CHAR, pathIndex);
            }
        }
        return path;
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
     * isConcreteNode - tests for concrete view node type
     *
     * @param node test node view
     * @return view node type flag
     */
    private static boolean isConcreteNode(Node node)
    {
        // concrete nodes include folder, pages, and links
        return ((node instanceof Folder) || (node instanceof Page) || (node instanceof Link));
    }

    /**
     * isVisible - tests for node visibility in view
     *
     * @param node test node view
     * @param currentPage current page view
     * @return visible flag
     */
    private static boolean isVisible(Node node, Page currentPage)
    {
        // pages are considered visible if not hidden or match current page
        if (node instanceof Page)
        {
            return (!node.isHidden() || (node == currentPage));
        }
        // folders are considered visible if not hidden and not reserved
        if (node instanceof Folder)
        {
            return !node.isHidden() && !((Folder)node).isReserved();            
        }
        // links are considered visible if not hidden
        if (node instanceof Link)
        {
            return !node.isHidden();
        }
        // templates, fragments, and dynamic page are not visible
        return false;
    }

    /**
     * isViewable - tests for node belonging to view
     *
     * @param node test node view
     * @param onlyVisible nodes required to be visible
     * @return viewable flag
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
                    for (Node child : children)
                    {
                        if ((!onlyVisible || isVisible(child, null)) && isViewable(child, onlyVisible))
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
    public Set<String> getStandardMenuNames()
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
    public List<SiteViewMenuDefinitionLocator> getStandardMenuDefinitionLocators()
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
    public abstract List<SiteViewMenuDefinitionLocator> getMenuDefinitionLocators(Node node);

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

    /**
     * getUserFolderPath - return primary concrete root user folder path
     *
     * @return user folder path or null
     */
    public abstract String getUserFolderPath();

    /**
     * getBaseFolderPath - return primary concrete root base folder path
     *
     * @return base folder path or null
     */
    public abstract String getBaseFolderPath();
}
