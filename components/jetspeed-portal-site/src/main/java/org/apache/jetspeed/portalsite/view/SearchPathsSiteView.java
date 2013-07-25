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
import org.apache.jetspeed.om.folder.proxy.FolderProxy;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.proxy.DynamicPageProxy;
import org.apache.jetspeed.om.page.proxy.FragmentDefinitionProxy;
import org.apache.jetspeed.om.page.proxy.LinkProxy;
import org.apache.jetspeed.om.page.proxy.PageProxy;
import org.apache.jetspeed.om.page.proxy.PageTemplateProxy;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfileLocatorProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * This class defines a logical view of site content using
 * search paths generated by the profiler.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SearchPathsSiteView extends AbstractSiteView
{
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
     * userSearchPath - primary user search path
     */
    private SiteViewSearchPath userSearchPath;

    /**
     * baseSearchPath - base search path
     */
    private SiteViewSearchPath baseSearchPath;

    /**
     * SearchPathsSiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param searchPaths list of search paths in string or search path
     *                    object form
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SearchPathsSiteView(PageManager pageManager, List searchPaths, boolean forceReservedVisible)
    {
        super(pageManager);
        if ((searchPaths != null) && !searchPaths.isEmpty())
        {
            // validate search path format and existence
            this.searchPaths = new ArrayList(searchPaths.size());
            List allSearchPaths = new ArrayList(searchPaths.size());
            StringBuilder searchPathsStringBuilder = new StringBuilder();
            Iterator pathsIter = searchPaths.iterator();
            while (pathsIter.hasNext())
            {
                // construct search paths if necessary
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
                allSearchPaths.add(searchPath);

                // validate and filter final search paths
                if (this.searchPaths.indexOf(searchPath) == -1)
                {
                    try
                    {
                        if (pageManager.getFolder(searchPath.toString()) != null)
                        {
                            this.searchPaths.add(searchPath);
                            
                            // construct search paths as string
                            if (searchPathsStringBuilder.length() == 0)
                            {
                                searchPathsStringBuilder.append(searchPath);
                            }
                            else
                            {
                                searchPathsStringBuilder.append(',');
                                searchPathsStringBuilder.append(searchPath);
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
                this.searchPaths.add(new SiteViewSearchPath(ProfileLocator.PAGE_LOCATOR));
                this.searchPathsString = Folder.PATH_SEPARATOR;
            }
            else
            {
                this.searchPathsString = searchPathsStringBuilder.toString();
            }
            
            // find primary user search path, (may not exist: search against all paths) 
            Iterator searchPathsIter = allSearchPaths.iterator();
            while (searchPathsIter.hasNext())
            {
                SiteViewSearchPath searchPath = (SiteViewSearchPath)searchPathsIter.next();
                if (searchPath.isUserPath())
                {
                    this.userSearchPath = searchPath;
                    break;
                }
            }
            
            // find base search path, (may not exist: search against all paths)
            if (allSearchPaths.size() == 1)
            {
                // single non-principal search path is the base
                // search path
                SiteViewSearchPath searchPath = (SiteViewSearchPath)allSearchPaths.get(0);
                if (!searchPath.isPrincipalPath())
                {
                    this.baseSearchPath = searchPath;
                }
            }
            else
            {
                // scan for the search path that are common to all
                // more specific search paths starting at the least
                // specific search path
                ListIterator baseSearchPathsIter = allSearchPaths.listIterator(allSearchPaths.size());
                while (baseSearchPathsIter.hasPrevious())
                {
                    SiteViewSearchPath searchPath = (SiteViewSearchPath)baseSearchPathsIter.previous();
                    int scanSearchPathsIndex = baseSearchPathsIter.previousIndex();
                    if (scanSearchPathsIndex == -1)
                    {
                        // most specific non-principal search path is the
                        // base path since all less specific search paths are
                        // part of the current search path
                        if (!searchPath.isPrincipalPath())
                        {
                            this.baseSearchPath = searchPath;
                        }
                        break;
                    }
                    else if (!searchPath.isPrincipalPath() && ((this.baseSearchPath == null) || (searchPath.getPathDepth() > this.baseSearchPath.getPathDepth())))
                    {
                        // scan more specific search paths to test whether the
                        // current search path is common to all
                        boolean isCommonSearchPath = true;
                        ListIterator scanBaseSearchPathsIter = allSearchPaths.listIterator(scanSearchPathsIndex+1);
                        while (scanBaseSearchPathsIter.hasPrevious())
                        {
                            SiteViewSearchPath scanSearchPath = (SiteViewSearchPath)scanBaseSearchPathsIter.previous();
                            if (!scanSearchPath.toString().startsWith(searchPath.toString()))
                            {
                                isCommonSearchPath = false;
                                break;
                            }
                        }
                        // if all more specific search paths are start with
                        // the current search path, the current search path is
                        // a base search path candidate: continue scan for a
                        // more specific common search path; otherwise, the
                        // last base search path is the most common possible: done 
                        if (isCommonSearchPath)
                        {
                            this.baseSearchPath = searchPath;
                            continue;
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        // longest non-principal common base search path found: done
                        break;
                    }
                }
            }
        }
        else
        {
            // root search path with no aggregation
            this.searchPaths = new ArrayList(1);
            this.searchPaths.add(new SiteViewSearchPath(ProfileLocator.PAGE_LOCATOR));
            this.searchPathsString = Folder.PATH_SEPARATOR;
            this.baseSearchPath = (SiteViewSearchPath)this.searchPaths.get(0);
        }
        this.forceReservedVisible = forceReservedVisible;
    }

    /**
     * SearchPathsSiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param searchPaths array of search paths
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SearchPathsSiteView(PageManager pageManager, String [] searchPaths, boolean forceReservedVisible)
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
     * SearchPathsSiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param searchPaths string of comma separated search paths
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SearchPathsSiteView(PageManager pageManager, String searchPaths, boolean forceReservedVisible)
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
     * SearchPathsSiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param locator profile locator search specification
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SearchPathsSiteView(PageManager pageManager, ProfileLocator locator, boolean forceReservedVisible)
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
     * SearchPathsSiteView - validating constructor
     *
     * @param pageManager PageManager component instance
     * @param locators map of named profile locator search specifications
     * @param forceReservedVisible force visibility of hidden/reserved folders
     */
    public SearchPathsSiteView(PageManager pageManager, Map locators, boolean forceReservedVisible)
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
     * StringBuilder implementation for constructing search paths.
     */
    private static class PathStringBuilder
    {
        private StringBuilder stringBuilder;
        private boolean userPath;
        private boolean principalPath;
        private int pathDepth;
        
        public PathStringBuilder()
        {
            this.stringBuilder = new StringBuilder();            
        }
        
        public PathStringBuilder(String s)
        {
            this.stringBuilder = new StringBuilder(s);
        }

        public PathStringBuilder append(String s)
        {
            stringBuilder.append(s);
            return this;
        }

        public PathStringBuilder append(char c)
        {
            stringBuilder.append(c);
            return this;
        }

        public int length()
        {
            return stringBuilder.length();
        }

        public void setLength(int length)
        {
            stringBuilder.setLength(length);
        }

        public String toString()
        {
            return stringBuilder.toString();
        }

        public boolean isUserPath()
        {
            return userPath;
        }

        public void setUserPath(boolean userPath)
        {
            this.userPath = userPath;
        }

        public boolean isPrincipalPath()
        {
            return principalPath;
        }

        public void setPrincipalPath(boolean principalPath)
        {
            this.principalPath = principalPath;
        }

        public int getPathDepth()
        {
            return pathDepth;
        }

        public void setPathDepth(int pathDepth)
        {
            this.pathDepth = pathDepth;
        }
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
        Iterator<ProfileLocatorProperty []> locatorIter = locator.iterator();
        while (locatorIter.hasNext())
        {
            // initialize path construction variables
            String pathRoot = Folder.PATH_SEPARATOR;
            List paths = new ArrayList(8);
            paths.add(new PathStringBuilder(pathRoot));
            int pathDepth = 0;
            int lastPathsCount = 0;
            String lastPropertyName = null;
            int lastPropertyValueLength = 0;
            boolean navigatedPathRoot = false;

            // reset advance of the profile locator offset by one
            // to accommodate automatic iteration within locator loop
            int skipProfileLocatorIterations = -1;

            // form locator properties into a complete path
            ProfileLocatorProperty [] properties = locatorIter.next();
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
                        int pathRootDepth = 0;
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
                                pathRootDepth++;
                                folderIndex = pathRoot.indexOf(Folder.PATH_SEPARATOR, folderIndex) + 1;
                            }
                            while ((folderIndex != -1) && (folderIndex != pathRoot.length()));
                        }

                        // construct prefix path root
                        PathStringBuilder path = new PathStringBuilder(pathRoot);
                        path.setPathDepth(pathRootDepth);

                        // reset locator paths using new prefix
                        pathDepth = 0;
                        paths.clear();
                        paths.add(path);
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
                        // classify principal paths
                        String prefixedPropertyName = Folder.RESERVED_FOLDER_PREFIX+propertyName;
                        boolean userPath = prefixedPropertyName.equals(Folder.RESERVED_USER_FOLDER_NAME);
                        boolean rolePath = (!userPath && prefixedPropertyName.equals(Folder.RESERVED_ROLE_FOLDER_NAME));
                        boolean groupPath = (!userPath && !rolePath && prefixedPropertyName.equals(Folder.RESERVED_GROUP_FOLDER_NAME));
                        boolean principalPath = (userPath || rolePath || groupPath);
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
                                PathStringBuilder path = (PathStringBuilder) pathsIter.next();
                                PathStringBuilder multipleValuePath = new PathStringBuilder(path.toString());
                                multipleValuePath.setLength(multipleValuePath.length() - lastPropertyValueLength - 1);
                                multipleValuePath.append(propertyValue);
                                multipleValuePath.append(Folder.PATH_SEPARATOR_CHAR);
                                multipleValuePath.setUserPath(userPath);
                                multipleValuePath.setPrincipalPath(path.isPrincipalPath() || principalPath);
                                multipleValuePath.setPathDepth(path.getPathDepth()+1);
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
                                PathStringBuilder path = (PathStringBuilder) pathsIter.next();
                                path.append(Folder.RESERVED_FOLDER_PREFIX);
                                path.append(propertyName);
                                path.append(Folder.PATH_SEPARATOR_CHAR);
                                path.append(propertyValue);
                                path.append(Folder.PATH_SEPARATOR_CHAR);
                                path.setUserPath(userPath);
                                path.setPrincipalPath(path.isPrincipalPath() || principalPath);
                                path.setPathDepth(path.getPathDepth()+1);
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
                locatorSearchPaths.add(addLocatorSearchPathsAt++, new PathStringBuilder(pathRoot));
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
                    PathStringBuilder searchPathBuilder = (PathStringBuilder)locatorSearchPathsIter.next();
                    SiteViewSearchPath searchPath = new SiteViewSearchPath(locatorName, searchPathBuilder.toString(), searchPathBuilder.isUserPath(), searchPathBuilder.isPrincipalPath(), searchPathBuilder.getPathDepth());
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
     * SearchPathsSiteView - basic constructor
     *
     * @param pageManager PageManager component instance
     */
    public SearchPathsSiteView(PageManager pageManager)
    {
        this(pageManager, (List)null, false);
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
            // the folder and profile locator name of the root
            // folder proxy in the view is the locator name of the
            // first search path since search paths are valid
            SiteViewSearchPath searchPath = (SiteViewSearchPath)searchPaths.get(0);
            String path = searchPath.toString();
            String locatorName = searchPath.getLocatorName();

            // get concrete root folder from page manager
            // and construct proxy
            Folder rootFolder = getPageManager().getFolder(path);
            return FolderProxy.newInstance(this, locatorName, null, rootFolder, forceReservedVisible);
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
        // check security access to folder node not found in site view
        FolderProxy.getFolderProxy(folder).checkAccessToNodeNotFound(folderName);        
    }

    /**
     * getMenuDefinitionLocators - get list of view node menu definition locators
     *
     * @param node node view
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
     * getMenuDefinitionLocators - get list of view node menu definition locators
     *
     * @param node node view
     * @return definition locator list
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
     * getProfileLocatorName - get profile locator name from view node
     *
     * @param node node view
     * @return profile locator name or null
     */
    public String getProfileLocatorName(Node node)
    {
        SearchPathsSiteViewProxy siteViewProxy = SearchPathsSiteViewProxy.getSiteViewProxy(node);
        if (siteViewProxy != null)
        {
            return siteViewProxy.getLocatorName();
        }
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
        // access page proxy from specified page and
        // return associated delegate managed page
        PageProxy pageProxy = (PageProxy)NodeProxy.getNodeProxy(page);
        return ((pageProxy != null) ? pageProxy.getPage() : null);
    }

    /**
     * getManagedLink - get concrete link instance from link view
     *  
     * @param link link view
     * @return managed link
     */
    public Link getManagedLink(Link link)
    {
        // access link proxy from specified page and
        // return associated delegate managed link
        LinkProxy linkProxy = (LinkProxy)NodeProxy.getNodeProxy(link);
        return ((linkProxy != null) ? linkProxy.getLink() : null);
    }

    /**
     * getManagedFolder - get concrete folder instance from folder view
     *  
     * @param folder folder view
     * @return managed folder
     */
    public Folder getManagedFolder(Folder folder)
    {
        // access folder proxy from specified folder and
        // return associated delegate managed folder
        FolderProxy folderProxy = (FolderProxy)NodeProxy.getNodeProxy(folder);
        return ((folderProxy != null) ? folderProxy.getDefaultFolder() : null);
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
        // access page template proxy from specified page template
        // and return associated delegate managed page template
        PageTemplateProxy pageTemplateProxy = (PageTemplateProxy)NodeProxy.getNodeProxy(pageTemplate);
        return ((pageTemplateProxy != null) ? pageTemplateProxy.getPageTemplate() : null);
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
        // access dynamic page proxy from specified dynamic page
        // and return associated delegate managed dynamic page
        DynamicPageProxy dynamicPageProxy = (DynamicPageProxy)NodeProxy.getNodeProxy(dynamicPage);
        return ((dynamicPageProxy != null) ? dynamicPageProxy.getDynamicPage() : null);
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
        // access fragment definition proxy from specified fragment
        // definition and return associated delegate managed
        // fragment definition
        FragmentDefinitionProxy fragmentDefinitionProxy = (FragmentDefinitionProxy)NodeProxy.getNodeProxy(fragmentDefinition);
        return ((fragmentDefinitionProxy != null) ? fragmentDefinitionProxy.getFragmentDefinition() : null);
    }

    /**
     * getUserFolderPath - return primary concrete root user folder path
     *
     * @return user folder path or null
     */
    public String getUserFolderPath()
    {
        return ((userSearchPath != null) ? userSearchPath.toString() : null);
    }

    /**
     * getBaseFolderPath - return primary concrete root base folder path
     *
     * @return base folder path or null
     */
    public String getBaseFolderPath()
    {
        return ((baseSearchPath != null) ? baseSearchPath.toString() : null);        
    }
}
