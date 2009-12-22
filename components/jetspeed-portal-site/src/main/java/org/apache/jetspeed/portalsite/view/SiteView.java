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
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.proxy.FolderProxy;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.proxy.DynamicPageProxy;
import org.apache.jetspeed.om.page.proxy.FragmentDefinitionProxy;
import org.apache.jetspeed.om.page.proxy.PageProxy;
import org.apache.jetspeed.om.page.proxy.PageTemplateProxy;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.portalsite.menu.StandardBackMenuDefinition;
import org.apache.jetspeed.portalsite.menu.StandardBreadcrumbsMenuDefinition;
import org.apache.jetspeed.portalsite.menu.StandardNavigationsMenuDefinition;
import org.apache.jetspeed.portalsite.menu.StandardPagesMenuDefinition;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfileLocatorProperty;

/**
 * This class defines the logical view of site content.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SiteView
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
     * searchPaths - validated list of ordered search path objects
     *               where paths have no trailing folder separator
     */
    private List searchPaths;

    /**
     * searchPathsString - search paths as string
     */
    private String searchPathsString;
    
    /**
     * forceReservedVisible - force visibility of hidden/reserved folders
     */
    private boolean forceReservedVisible;

    /**
     * rootFolderProxy - root folder proxy instance
     */
    private Folder rootFolderProxy;

    /**
     * SiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param searchPaths list of search paths in string or search path
     *                    object form
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SiteView(PageManager pageManager, List searchPaths, boolean forceReservedVisible)
    {
        this.pageManager = pageManager;
        if ((searchPaths != null) && !searchPaths.isEmpty())
        {
            // validate search path format and existence
            this.searchPaths = new ArrayList(searchPaths.size());
            StringBuffer searchPathsStringBuffer = new StringBuffer();
            Iterator pathsIter = searchPaths.iterator();
            while (pathsIter.hasNext())
            {
                Object pathObject = pathsIter.next();
                if (!(pathObject instanceof SiteViewSearchPath))
                {
                    String path = pathObject.toString().trim();
                    if (path.length() > 0)
                    {
                        pathObject = new SiteViewSearchPath(ProfileLocator.PAGE_LOCATOR, path);
                    }
                }
                SiteViewSearchPath searchPath = (SiteViewSearchPath)pathObject;
                if (this.searchPaths.indexOf(searchPath) == -1)
                {
                    try
                    {
                        if (this.pageManager.getFolder(searchPath.toString()) != null)
                        {
                            this.searchPaths.add(searchPath);
                            
                            // construct search paths as string
                            if (searchPathsStringBuffer.length() == 0)
                            {
                                searchPathsStringBuffer.append(searchPath);
                            }
                            else
                            {
                                searchPathsStringBuffer.append(',');
                                searchPathsStringBuffer.append(searchPath);
                            }
                        }
                    }
                    catch (NodeException ne)
                    {
                    }
                    catch (NodeNotFoundException nnfe)
                    {
                    }
                    catch (SecurityException se)
                    {
                    }
                }
            }

            // if no valid paths found, assume root search path
            // with no aggregation is to be used as default; otherwise,
            // save search paths as string
            if (this.searchPaths.isEmpty())
            {
                this.searchPaths.add(new SiteViewSearchPath(ProfileLocator.PAGE_LOCATOR, Folder.PATH_SEPARATOR));
                this.searchPathsString = Folder.PATH_SEPARATOR;
            }
            else
            {
                this.searchPathsString = searchPathsStringBuffer.toString();
            }
        }
        else
        {
            // root search path with no aggregation
            this.searchPaths = new ArrayList(1);
            this.searchPaths.add(new SiteViewSearchPath(ProfileLocator.PAGE_LOCATOR, Folder.PATH_SEPARATOR));
            this.searchPathsString = Folder.PATH_SEPARATOR;
        }
        this.forceReservedVisible = forceReservedVisible;
    }

    /**
     * SiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param searchPaths array of search paths
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SiteView(PageManager pageManager, String [] searchPaths, boolean forceReservedVisible)
    {
        this(pageManager, makeSearchPathList(searchPaths), forceReservedVisible);
    }

    /**
     * makeSearchPathList - construct from array
     *
     * @param searchPaths array of search paths
     * @return search path list
     */
    private static List makeSearchPathList(String [] searchPaths)
    {
        if ((searchPaths != null) && (searchPaths.length > 0))
        {
            List searchPathsList = new ArrayList(searchPaths.length);
            for (int i = 0; (i < searchPaths.length); i++)
            {
                searchPathsList.add(searchPaths[i]);
            }
            return searchPathsList;
        }
        return null;
    }

    /**
     * SiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param searchPaths string of comma separated search paths
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SiteView(PageManager pageManager, String searchPaths, boolean forceReservedVisible)
    {
        this(pageManager, makeSearchPathList(searchPaths), forceReservedVisible);
    }

    /**
     * makeSearchPathList - construct from string
     *
     * @param searchPaths string of comma separated search paths
     * @return search path list
     */
    private static List makeSearchPathList(String searchPaths)
    {
        return ((searchPaths != null) ? makeSearchPathList(searchPaths.split(",")) : null);
    }

    /**
     * SiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param locator profile locator search specification
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SiteView(PageManager pageManager, ProfileLocator locator, boolean forceReservedVisible)
    {
        this(pageManager, makeSearchPathList(locator), forceReservedVisible);
    }
    
    /**
     * makeSearchPathList - construct from profile locator
     *
     * @param locator profile locator search specification
     * @return search path list
     */
    private static List makeSearchPathList(ProfileLocator locator)
    {
        if (locator != null)
        {
            // generate and return locator search paths
            return mergeSearchPathList(ProfileLocator.PAGE_LOCATOR, locator, new ArrayList(8));
        }
        return null;
    }

    /**
     * SiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param locators map of named profile locator search specifications
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SiteView(PageManager pageManager, Map locators, boolean forceReservedVisible)
    {
        this(pageManager, makeSearchPathList(locators), forceReservedVisible);
    }
    
    /**
     * makeSearchPathList - construct from profile locators
     *
     * @param locators map of named profile locator search specifications
     * @return search path list
     */
    private static List makeSearchPathList(Map locators)
    {
        if ((locators != null) && !locators.isEmpty())
        {
            // generate locators search paths; aggregate individual
            // profile locator search paths with the 'page' locator
            // having priority, (all other named locators are processed
            // subsequent to 'page' in unspecified order).
            List searchPaths = new ArrayList(8 * locators.size());
            ProfileLocator pageLocator = (ProfileLocator)locators.get(ProfileLocator.PAGE_LOCATOR);
            if (pageLocator != null)
            {
                // add priority 'page' locator search paths
                mergeSearchPathList(ProfileLocator.PAGE_LOCATOR, pageLocator, searchPaths);
            }
            if ((pageLocator == null) || (locators.size() > 1))
            {
                Iterator locatorNameIter = locators.keySet().iterator();
                while (locatorNameIter.hasNext())
                {
                    String locatorName = (String)locatorNameIter.next();
                    if (!locatorName.equals(ProfileLocator.PAGE_LOCATOR))
                    {
                        // add alternate locator search paths
                        mergeSearchPathList(locatorName, (ProfileLocator)locators.get(locatorName), searchPaths);
                    }
                }
            }
            return searchPaths;
        }
        return null;
    }
    
    /**
     * mergeSearchPathList - append search paths from profile locator
     *
     * @param locatorName name of profile locator
     * @param locator profile locator search specification
     * @param searchPaths list of search paths to merge into
     * @return search path list
     */
    private static List mergeSearchPathList(String locatorName, ProfileLocator locator, List searchPaths)
    {
        // generate profile locator search paths with locator
        // names to be used later for node identification and
        // grouping; note that the profile locator iterator
        // starts returning a full set of properties and returns
        // a shorter properties array with each iteration to
        // create the next search criteria... depending on the
        // validity of the property values and their cardinality,
        // (multiple property values are returned sequentially),
        // profiler locator iterations may be skipped to
        // generate the proper search paths
        List locatorSearchPaths = new ArrayList(8);
        int addLocatorSearchPathsAt = 0;
        Iterator locatorIter = locator.iterator();
        while (locatorIter.hasNext())
        {
            // initialize path construction variables
            String pathRoot = Folder.PATH_SEPARATOR;
            List paths = new ArrayList(8);
            paths.add(new StringBuffer(pathRoot));
            int pathDepth = 0;
            int lastPathsCount = 0;
            String lastPropertyName = null;
            int lastPropertyValueLength = 0;
            boolean navigatedPathRoot = false;

            // reset advance of the profile locator offset by one
            // to accomodate automatic iteration within locator loop
            int skipProfileLocatorIterations = -1;

            // form locator properties into a complete path
            ProfileLocatorProperty [] properties = (ProfileLocatorProperty []) locatorIter.next();
            for (int i = 0; (i < properties.length); i++)
            {
                if (properties[i].isNavigation())
                {
                    // reset search paths to navigation root path, (reset
                    // only navigation supported), skip null navigation values
                    if (properties[i].getValue() != null)
                    {
                        // TODO: support relative navigation values

                        // assume navigation value must be a root prefix
                        // and contains proper path prefix for each subsite
                        // path folder name
                        pathRoot = properties[i].getValue();
                        if (!pathRoot.startsWith(Folder.PATH_SEPARATOR))
                        {
                            pathRoot = Folder.PATH_SEPARATOR + pathRoot; 
                        }
                        if (!pathRoot.endsWith(Folder.PATH_SEPARATOR))
                        {
                            pathRoot += Folder.PATH_SEPARATOR; 
                        }
                        if (!pathRoot.equals(Folder.PATH_SEPARATOR))
                        {
                            int folderIndex = 1;
                            do
                            {
                                if (!pathRoot.regionMatches(folderIndex, Folder.RESERVED_SUBSITE_FOLDER_PREFIX, 0, Folder.RESERVED_SUBSITE_FOLDER_PREFIX.length()))
                                {
                                    pathRoot = pathRoot.substring(0, folderIndex) + Folder.RESERVED_SUBSITE_FOLDER_PREFIX + pathRoot.substring(folderIndex);
                                }
                                folderIndex = pathRoot.indexOf(Folder.PATH_SEPARATOR, folderIndex) + 1;
                            }
                            while ((folderIndex != -1) && (folderIndex != pathRoot.length()));
                        }
                        
                        // reset locator paths using new prefix
                        pathDepth = 0;
                        paths.clear();
                        paths.add(new StringBuffer(pathRoot));
                        lastPathsCount = 0;
                        lastPropertyName = null;
                        lastPropertyValueLength = 0;
                        navigatedPathRoot = true;

                        // reset advance of the the profile locator iterator
                        skipProfileLocatorIterations = 0;
                    }
                    else
                    {
                        // make sure multiple trailing null valued properties are
                        // ignored if more than one is present by advancing
                        // the profile locator iterator
                        skipProfileLocatorIterations++;
                    }
                }
                else if (properties[i].isControl())
                {
                    // skip null control values
                    if (properties[i].getValue() != null)
                    {
                        // fold control names to lower case; preserve
                        // value case as provided by profiler
                        String propertyName = properties[i].getName().toLowerCase();
                        String propertyValue = properties[i].getValue();
                        // detect duplicate control names which indicates multiple
                        // values: must duplicate locator paths for each value; different
                        // control values are simply appended to all locator paths
                        if (propertyName.equals(lastPropertyName))
                        {
                            // duplicate last locator paths set, stripping last matching
                            // control value from each, appending new value, and adding new
                            // valued set to collection of paths
                            ArrayList multipleValuePaths = new ArrayList(lastPathsCount);
                            Iterator pathsIter = paths.iterator();
                            for (int count = 0; (pathsIter.hasNext() && (count < lastPathsCount)); count++)
                            {
                                StringBuffer path = (StringBuffer) pathsIter.next();
                                StringBuffer multipleValuePath = new StringBuffer(path.toString());
                                multipleValuePath.setLength(multipleValuePath.length() - lastPropertyValueLength - 1);
                                multipleValuePath.append(propertyValue);
                                multipleValuePath.append(Folder.PATH_SEPARATOR_CHAR);
                                multipleValuePaths.add(multipleValuePath);
                            }
                            paths.addAll(multipleValuePaths);

                            // make sure trailing multiple valued properties are
                            // ignored by advancing the profile locator iterator
                            // which is reset for each unique property value sets
                            skipProfileLocatorIterations++;
                        }
                        else
                        {
                            // construct locator path folders with control properties
                            Iterator pathsIter = paths.iterator();
                            while (pathsIter.hasNext())
                            {
                                StringBuffer path = (StringBuffer) pathsIter.next();
                                path.append(Folder.RESERVED_FOLDER_PREFIX);
                                path.append(propertyName);
                                path.append(Folder.PATH_SEPARATOR_CHAR);
                                path.append(propertyValue);
                                path.append(Folder.PATH_SEPARATOR_CHAR);
                            }
                            
                            // reset last locator property vars
                            pathDepth++;
                            lastPathsCount = paths.size();
                            lastPropertyValueLength = propertyValue.length();
                            lastPropertyName = propertyName;

                            // reset advance of the the profile locator iterator
                            skipProfileLocatorIterations = 0;
                        }
                    }
                    else
                    {
                        // make sure multiple trailing null valued properties are
                        // ignored along with the last property values if more
                        // than one is present by advancing the profile locator
                        // iterator
                        skipProfileLocatorIterations++;
                    }
                }
                else
                {
                    // make sure multiple trailing request path properties are
                    // ignored if more than one is present by advancing the
                    // profile locator iterator
                    skipProfileLocatorIterations++;
                }
            }
            
            // if required, advance profile locator iterations
            for (int skip = skipProfileLocatorIterations; ((skip > 0) && (locatorIter.hasNext())); skip--)
            {
                locatorIter.next();
            }

            // append any generated paths to locator search paths and
            // append root path if at end of locator path group, (locator
            // path roots are not returned by profiler iterator if not
            // explicitly navigated)
            if ((pathDepth > 0) || navigatedPathRoot)
            {
                locatorSearchPaths.addAll(addLocatorSearchPathsAt, paths);
                addLocatorSearchPathsAt += paths.size();
            }
            if ((pathDepth == 1) && !navigatedPathRoot)
            {
                locatorSearchPaths.add(addLocatorSearchPathsAt++, new StringBuffer(pathRoot));
            }

            // reset locator search path ordering since navigated root
            // paths are generated by the iterator based algorithm above
            // right to left instead of left to right as expected
            if ((pathDepth == 0) && navigatedPathRoot)
            {
                addLocatorSearchPathsAt = 0;
            }
            
            // if end of locator path group and have not navigated to
            // a new root path or there are no more locator iterations,
            // insert the paths into the search path results
            if (((pathDepth <= 1) && !navigatedPathRoot) || !locatorIter.hasNext())
            {
                // add locator paths to unique search paths preserving
                // search order; move non-unique paths to end of search
                // path list to favor more specific paths over common
                // root paths, (i.e. '/' should be last)
                Iterator locatorSearchPathsIter = locatorSearchPaths.iterator();
                while (locatorSearchPathsIter.hasNext())
                {
                    SiteViewSearchPath searchPath = new SiteViewSearchPath(locatorName, locatorSearchPathsIter.next().toString());
                    // test search path uniqueness
                    int existsAt = searchPaths.indexOf(searchPath);
                    if (existsAt != -1)
                    {
                        if (existsAt < (searchPaths.size()-1))
                        {
                            // push existing search path to end of paths
                            searchPaths.add(searchPaths.remove(existsAt));
                        }
                    }
                    else
                    {
                        // add new unique search path to end of paths
                        searchPaths.add(searchPath);
                    }
                }

                // clear merged locator search paths
                locatorSearchPaths.clear();
                addLocatorSearchPathsAt = 0;
            }
        }
        return searchPaths;
    }

    /**
     * SiteView - basic constructor
     *
     * @param pageManager PageManager component instance
     */
    public SiteView(PageManager pageManager)
    {
        this(pageManager, (List)null, false);
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
     * getSearchPaths - return ordered search paths list that
     *                  defines this view
     *
     * @return search paths list
     */
    public List getSearchPaths()
    {
        return searchPaths;
    }

    /**
     * getSearchPathsString - return search paths as string
     *
     * @return search paths list as comma separated string
     */
    public String getSearchPathsString()
    {
        return searchPathsString;
    }

    /**
     * getRootFolderProxy - create and return root folder proxy instance
     *
     * @return root folder proxy
     * @throws FolderNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public Folder getRootFolderProxy() throws FolderNotFoundException
    {
        // latently construct and return root folder proxy
        if (rootFolderProxy == null)
        {
            try
            {
                // the folder and profile locator name of the root
                // folder proxy in the view is the locator name of the
                // first search path since search paths are valid
                SiteViewSearchPath searchPath = (SiteViewSearchPath)searchPaths.get(0);
                String path = searchPath.toString();
                String locatorName = searchPath.getLocatorName();

                // get concrete root folder from page manager
                // and construct proxy
                Folder rootFolder = pageManager.getFolder(path);
                rootFolderProxy = FolderProxy.newInstance(this, locatorName, null, rootFolder, forceReservedVisible);
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
        return rootFolderProxy;
    }

    /**
     * getNodeProxy - get single folder, page, or link proxy
     *                at relative or absolute path
     *
     * @param path single node path
     * @param currentNode current folder or page for relative paths or null
     * @param onlyViewable node required to be viewable
     * @param onlyVisible node required to be visible, (or current)
     * @return folder, page, or link node proxy
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public Node getNodeProxy(String path, Node currentNode, boolean onlyViewable, boolean onlyVisible) throws NodeNotFoundException
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
            List proxies = new ArrayList(1);
            proxies.add(currentNode);
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
            currentFolder = getRootFolderProxy();
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
                        FolderProxy.getFolderProxy(currentFolder).checkAccessToFolderNotFound(subfolder);
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
                // proxy; return null if not found or not viewable/visible
                // and visibility is required
                try
                {
                    NodeSet children = currentFolder.getAll();
                    if (children != null)
                    {
                        Node node = children.get(currentPath);
                        if (((node instanceof Folder) | (node instanceof Page) | (node instanceof Link)) &&
                            (!onlyVisible || !node.isHidden() || (node == currentPage)) &&
                            (!onlyViewable || isProxyViewable(node, onlyVisible)))
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
                FolderProxy.getFolderProxy(currentFolder).checkAccessToNodeNotFound(currentPath);
                // folder node not found in site view
                throw new NodeNotFoundException("Specified path " + path + " not found or viewable/visible.");
            }
        }

        // path maps to current folder; return if viewable/visible
        // or visibility not required
        if ((!onlyVisible || !currentFolder.isHidden()) &&
            (!onlyViewable || isProxyViewable(currentFolder, onlyVisible)))
        {
            return currentFolder;
        }
        throw new NodeNotFoundException("Specified path " + path + " not found or viewable/visible.");
    }

    /**
     * getNodeProxies - get folder, page, or link proxies at
     *                  relative or absolute path using simple path
     *                  wildcards and character classes
     *
     * @param regexpPath regular expression node path
     * @param currentNode current folder or page for relative paths or null
     * @param onlyViewable nodes required to be viewable flag
     * @param onlyVisible node required to be visible, (or current)
     * @return list of folder, page, or link node proxies
     */
    public List getNodeProxies(String regexpPath, Node currentNode, boolean onlyViewable, boolean onlyVisible)
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
                List proxies = new ArrayList(1);
                proxies.add(currentPage);
                return proxies;
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
                List proxies = new ArrayList(1);
                proxies.add(currentNode);
                return proxies;
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
                currentFolder = getRootFolderProxy();
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
                                        List proxies = null;
                                        Iterator subfoldersIter = subfolders.iterator();
                                        while (subfoldersIter.hasNext())
                                        {
                                            currentFolder = (Folder)subfoldersIter.next();
                                            List subfolderProxies = getNodeProxies(currentRegexpPath, currentFolder, onlyViewable, onlyVisible);
                                            if ((subfolderProxies != null) && !subfolderProxies.isEmpty())
                                            {
                                                if (proxies == null)
                                                {
                                                    proxies = new ArrayList();
                                                }
                                                proxies.addAll(subfolderProxies);
                                            }
                                        }
                                        return proxies;
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
                            // page, folder, or link proxies if viewable/visible or
                            // visibility not required
                            children = children.inclusiveSubset(pathPattern);
                            if ((children != null) && !children.isEmpty())
                            {
                                List proxies = null;
                                Iterator childrenIter = children.iterator();
                                while (childrenIter.hasNext())
                                {
                                    Node child = (Node)childrenIter.next(); 
                                    if (((child instanceof Folder) | (child instanceof Page) | (child instanceof Link)) &&
                                        (!onlyVisible || !child.isHidden() || (child == currentPage)) &&
                                        (!onlyViewable || isProxyViewable(child, onlyVisible)))
                                    {
                                        if (proxies == null)
                                        {
                                            proxies = new ArrayList(children.size());
                                        }
                                        proxies.add(child);
                                    }
                                }
                                return proxies;
                            }
                        }
                        else
                        {
                            // access remaining path as page, folder, or link
                            // node proxy; return null if not found or not
                            // viewable and visibility is required
                            Node child = children.get(currentRegexpPath);
                            if (((child instanceof Folder) | (child instanceof Page) | (child instanceof Link)) &&
                                (!onlyVisible || !child.isHidden() || (child == currentPage)) &&
                                (!onlyViewable || isProxyViewable(child, onlyVisible)))
                            {
                                List proxies = new ArrayList(1);
                                proxies.add(currentFolder);
                                return proxies;
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
            (!onlyViewable || isProxyViewable(currentFolder, onlyVisible)))
        {
            List proxies = new ArrayList(1);
            proxies.add(currentFolder);
            return proxies;
        }
        return null;
    }

    /**
     * pathRegexpPattern - tests for and converts simple path wildcard
     *                     and character class regular exressions to
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
     * isProxyViewable - tests for node proxy visibility in view
     *
     * @param nodeProxy test node proxy
     * @param onlyVisible nodes required to be visible
     * @return - viewable flag
     */
    private static boolean isProxyViewable(Node nodeProxy, boolean onlyVisible)
    {
        // pages and links are always considered viewable
        if ((nodeProxy instanceof Page) || (nodeProxy instanceof Link))
        {
            return true;
        }
        // folders must be tested for viewable and visible
        // child nodes
        if (nodeProxy instanceof Folder)
        {
            try
            {
                NodeSet children = ((Folder) nodeProxy).getAll();
                if (children != null)
                {
                    Iterator childrenIter = children.iterator();
                    while (childrenIter.hasNext())
                    {
                        Node child = (Node)childrenIter.next();
                        if ((!onlyVisible || !child.isHidden()) && isProxyViewable(child, onlyVisible))
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
     * getMenuDefinitionLocators - get list of view node proxy menu
     *                             definition locators; implemented here
     *                             to hide view proxy manipulation from
     *                             more general portal site implementation
     *
     * @param node node proxy
     * @return definition locator list
     */
    public List getMenuDefinitionLocators(Node node)
    {
        // access node proxy from specified node and
        // return associated definition locators
        NodeProxy nodeProxy = NodeProxy.getNodeProxy(node);
        if (nodeProxy != null)
        {
            return nodeProxy.getMenuDefinitionLocators();
        }
        return null;
    }

    /**
     * getMenuDefinitionLocator - get named view node proxy menu
     *                            definition locator; implemented here
     *                            to hide view proxy manipulation from
     *                            more general portal site implementation
     *
     * @param node node proxy
     * @param name menu definition name
     * @return menu definition locator
     */
    public SiteViewMenuDefinitionLocator getMenuDefinitionLocator(Node node, String name)
    {
        // access node proxy from specified node and
        // return associated definition locators
        NodeProxy nodeProxy = NodeProxy.getNodeProxy(node);
        if (nodeProxy != null)
        {
            return nodeProxy.getMenuDefinitionLocator(name);
        }
        return null;
    }

    /**
     * getProfileLocatorName - get view node proxy profile locator name;
     *                         implemented here to hide view proxy manipulation
     *                         from more general portal site implementation
     *
     * @param node node proxy
     * @return profile locator name
     */
    public String getProfileLocatorName(Node node)
    {
        SiteViewProxy siteViewProxy = SiteViewProxy.getSiteViewProxy(node);
        if (siteViewProxy != null)
        {
            return siteViewProxy.getLocatorName();
        }
        return null;
    }

    /**
     * getManagedPage - get concrete page instance from page proxy;
     *                  implemented here to hide view proxy manipulation
     *                  from more general portal site implementation
     *  
     * @param page page proxy
     * @return managed page
     */
    public Page getManagedPage(Page page)
    {
        // access page proxy from specified page and
        // return associated delegate managed page
        PageProxy pageProxy = (PageProxy)NodeProxy.getNodeProxy(page);
        return ((pageProxy != null) ? pageProxy.getPage() : null);
    }

    /**
     * getManagedPageTemplate - get concrete page template instance from
     *                          page template proxy; implemented here to
     *                          hide view proxy manipulation from more
     *                          general portal site implementation
     *  
     * @param pageTemplate page template proxy
     * @return managed page template
     */
    public PageTemplate getManagedPageTemplate(PageTemplate pageTemplate)
    {
        // access page template proxy from specified page template
        // and return associated delegate managed page template
        PageTemplateProxy pageTemplateProxy = (PageTemplateProxy)NodeProxy.getNodeProxy(pageTemplate);
        return ((pageTemplateProxy != null) ? pageTemplateProxy.getPageTemplate() : null);
    }

    /**
     * getManagedDynamicPage - get concrete dynamic page instance from
     *                         dynamic page proxy; implemented here to
     *                         hide view proxy manipulation from more
     *                         general portal site implementation
     *  
     * @param dynamicPage dynamic page proxy
     * @return managed dynamic page
     */
    public DynamicPage getManagedDynamicPage(DynamicPage dynamicPage)
    {
        // access dynamic page proxy from specified dynamic page
        // and return associated delegate managed dynamic page
        DynamicPageProxy dynamicPageProxy = (DynamicPageProxy)NodeProxy.getNodeProxy(dynamicPage);
        return ((dynamicPageProxy != null) ? dynamicPageProxy.getDynamicPage() : null);
    }

    /**
     * getManagedFragmentDefinition - get concrete fragment definition
     *                                instance from fragment definition
     *                                proxy; implemented here to hide
     *                                view proxy manipulation from more
     *                                general portal site implementation
     *  
     * @param fragmentDefinition fragment definition proxy
     * @return managed dynamic page
     */
    public FragmentDefinition getManagedFragmentDefinition(FragmentDefinition fragmentDefinition)
    {
        // access fragment definition proxy from specified fragment
        // definition and return associated delegate managed
        // fragment definition
        FragmentDefinitionProxy fragmentDefinitionProxy = (FragmentDefinitionProxy)NodeProxy.getNodeProxy(fragmentDefinition);
        return ((fragmentDefinitionProxy != null) ? fragmentDefinitionProxy.getFragmentDefinition() : null);
    }
}
