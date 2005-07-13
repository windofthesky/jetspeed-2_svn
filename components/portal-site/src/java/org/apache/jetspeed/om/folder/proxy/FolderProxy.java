/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.om.folder.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderMetaData;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.proxy.LinkProxy;
import org.apache.jetspeed.om.page.proxy.PageProxy;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.page.document.proxy.NodeSetImpl;
import org.apache.jetspeed.portalsite.view.SiteView;
import org.apache.jetspeed.portalsite.view.SiteViewSearchPath;

/**
 * This class proxies PSML Folder instances to create a logical view
 * of site content using the Dynamic Proxy pattern.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FolderProxy extends NodeProxy implements InvocationHandler
{
    /**
     * *_METHOD - Folder method constants
     */
    protected static final Method GET_ALL_METHOD = reflectMethod(Folder.class, "getAll", null);
    protected static final Method GET_DEFAULT_PAGE_METHOD = reflectMethod(Folder.class, "getDefaultPage", new Class[]{Boolean.TYPE});
    protected static final Method GET_FOLDERS_METHOD = reflectMethod(Folder.class, "getFolders", null);
    protected static final Method GET_FOLDER_METHOD = reflectMethod(Folder.class, "getFolder", new Class[]{String.class});
    protected static final Method GET_LINKS_METHOD = reflectMethod(Folder.class, "getLinks", null);
    protected static final Method GET_LINK_METHOD = reflectMethod(Folder.class, "getLink", new Class[]{String.class});
    protected static final Method GET_MENU_DEFINITIONS_METHOD = reflectMethod(Folder.class, "getMenuDefinitions", null);
    protected static final Method GET_METADATA_METHOD = reflectMethod(Folder.class, "getMetadata", null);
    protected static final Method GET_NAME_METHOD = reflectMethod(Folder.class, "getName", null);
    protected static final Method GET_PAGES_METHOD = reflectMethod(Folder.class, "getPages", null);
    protected static final Method GET_PAGE_METHOD = reflectMethod(Folder.class, "getPage", new Class[]{String.class});
    protected static final Method GET_PAGE_SECURITY_METHOD = reflectMethod(Folder.class, "getPageSecurity", null);
    protected static final Method GET_SHORT_TITLE_LOCALE_METHOD = reflectMethod(Folder.class, "getShortTitle", new Class[]{Locale.class});
    protected static final Method GET_SHORT_TITLE_METHOD = reflectMethod(Folder.class, "getShortTitle", null);
    protected static final Method GET_TITLE_LOCALE_METHOD = reflectMethod(Folder.class, "getTitle", new Class[]{Locale.class});
    protected static final Method GET_TITLE_METHOD = reflectMethod(Folder.class, "getTitle", null);

    /**
     * defaultFolder - default proxy delegate folder instance
     */
    private Folder defaultFolder;

    /**
     * titledFolder - titled proxy delegate folder instance
     */
    private Folder titledFolder;

    /**
     * children - aggregated proxy sub-folder, page, and link nodes
     */
    private NodeSet children;

    /**
     * childrenAggregated - boolean flag to indicate children aggregated
     */
    private boolean childrenAggregated;

    /**
     * folders - aggregated proxy sub-folder nodes
     */
    private NodeSet folders;

    /**
     * foldersAggregated - boolean flag to indicate folders aggregated
     */
    private boolean foldersAggregated;

    /**
     * pages - aggregated proxy page nodes
     */
    private NodeSet pages;

    /**
     * pagesAggregated - boolean flag to indicate pages aggregated
     */
    private boolean pagesAggregated;

    /**
     * links - aggregated proxy link nodes
     */
    private NodeSet links;

    /**
     * linksAggregated - boolean flag to indicate links aggregated
     */
    private boolean linksAggregated;

    /**
     * SearchFolder - data object used hold concrete search folder and
     *                related search path profile locator name pairs
     */
    private class SearchFolder
    {
        public Folder folder;
        public String locatorName;

        public SearchFolder(Folder folder, String locatorName)
        {
            this.folder = folder;
            this.locatorName = locatorName;
        }
    }

    /**
     * searchFolders - search folder objects along view search paths
     *                 in most to least specific order
     */
    private List searchFolders;

    /**
     * inheritanceFolders - inheritance graph folder list in most to
     *                      least specific order
     */
    private List inheritanceFolders;

    /**
     * newInstance - creates a new proxy instance that implements the Folder interface
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param folder proxy delegate
     */
    public static Folder newInstance(SiteView view, String locatorName, Folder parentFolder, Folder folder)
    {
        return (Folder)Proxy.newProxyInstance(folder.getClass().getClassLoader(), new Class[]{Folder.class}, new FolderProxy(view, locatorName, parentFolder, folder));
    }

    /**
     * FolderProxy - private constructor used by newInstance()
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param folder proxy delegate
     */
    private FolderProxy(SiteView view, String locatorName, Folder parentFolder, Folder folder)
    {
        super(view, locatorName, parentFolder, folder.getName());
        this.defaultFolder = selectDefaultFromAggregateFolders(folder);
        this.titledFolder = selectTitledFromAggregateFolders(this.defaultFolder);
    }
    
    /**
     * invoke - method invocation dispatch for this proxy, (defaults to
     *          invocation of delegate unless method is implemented in this
     *          proxy handler or should be hidden/stubbed)
     *
     * @param proxy instance invoked against
     * @param method Folder interface method invoked
     * @param args method arguments
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method m, Object [] args) throws Throwable
    {
        // proxy implementation method dispatch
        if (m.equals(GET_ALL_METHOD))
        {
            return getAll(proxy);
        }
        else if (m.equals(GET_DEFAULT_PAGE_METHOD))
        {
            return getDefaultPage(proxy, ((Boolean)args[0]).booleanValue());
        }
        else if (m.equals(GET_FOLDERS_METHOD))
        {
            return getFolders(proxy);
        }
        else if (m.equals(GET_FOLDER_METHOD))
        {
            return getFolder(proxy, (String)args[0]);
        }
        else if (m.equals(GET_LINKS_METHOD))
        {
            return getLinks(proxy);
        }
        else if (m.equals(GET_LINK_METHOD))
        {
            return getLink(proxy, (String)args[0]);
        }
        else if (m.equals(GET_MENU_DEFINITIONS_METHOD))
        {
            return getMenuDefinitions();
        }
        else if (m.equals(GET_METADATA_METHOD))
        {
            return getMetadata();
        }
        else if (m.equals(GET_NAME_METHOD))
        {
            return getName();
        }
        else if (m.equals(GET_PAGES_METHOD))
        {
            return getPages(proxy);
        }
        else if (m.equals(GET_PAGE_METHOD))
        {
            return getPage(proxy, (String)args[0]);
        }
        else if (m.equals(GET_SHORT_TITLE_LOCALE_METHOD))
        {
            return getShortTitle((Locale)args[0]);
        }
        else if (m.equals(GET_SHORT_TITLE_METHOD))
        {
            return getShortTitle();
        }
        else if (m.equals(GET_TITLE_LOCALE_METHOD))
        {
            return getTitle((Locale)args[0]);
        }
        else if (m.equals(GET_TITLE_METHOD))
        {
            return getTitle();
        }
        else if (m.equals(GET_PARENT_METHOD))
        {
            return getParent();
        }
        else if (m.equals(GET_PATH_METHOD))
        {
            return getPath();
        }
        else if (m.equals(GET_URL_METHOD))
        {
            return getUrl();
        }
        else if (m.equals(EQUALS_METHOD))
        {
            return new Boolean(equals(args[0]));
        }
        else if (m.equals(HASH_CODE_METHOD))
        {
            return new Integer(hashCode());
        }
        else if (m.equals(TO_STRING_METHOD))
        {
            return toString();
        }
    
        // proxy suppression of not implemented or mutable methods
        if (m.equals(GET_PAGE_SECURITY_METHOD) ||
            m.getName().startsWith("set"))
        {
            throw new RuntimeException("Folder instance is immutable from proxy.");
        }

        // attempt to invoke method on delegate Folder instance
        return m.invoke(defaultFolder, args);
    }

    /**
     * getAll - proxy implementation of Folder.getAll()
     *
     * @param proxy this folder proxy
     * @return list containing sub-folders and documents in folder
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    public NodeSet getAll(Object proxy) throws FolderNotFoundException, DocumentException
    {
        // latently aggregate all children
        if (!childrenAggregated)
        {
            children = aggregateChildren(proxy);
            childrenAggregated = true;
        }
        return children;
    }

    /**
     * getDefaultPage - proxy implementation of Folder.getDefaultPage()
     *
     * @param proxy this folder proxy
     * @param allowDefaulting flag to enable defaulting logic
     * @return default page path
     */
    public String getDefaultPage(Object proxy, boolean allowDefaulting)
    {
        // attempt to get explicitly specified default page
        Page defaultPage = selectDefaultPageFromAggregateFolders(proxy);
        if (defaultPage != null)
        {
            return defaultPage.getName();
        }

        // if defaulting allowed, use first page child in folder
        if (allowDefaulting)
        {
            // return first page in folder
            try
            {
                NodeSet pages = getPages(proxy);
                if ((pages != null) && !pages.isEmpty())
                {
                    return ((Page)pages.iterator().next()).getName();
                }
            }
            catch (NodeException ne)
            {
            }
            catch (SecurityException se)
            {
            }

            // no default page fallback default available, return
            // non existing page name
            return Folder.PAGE_NOT_FOUND_PAGE;
        }

        // no default page available
        return null;
    }

    /**
     * getFolders - proxy implementation of Folder.getFolders()
     *
     * @param proxy this folder proxy
     * @return list containing all sub-folders in folder
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    public NodeSet getFolders(Object proxy) throws FolderNotFoundException, DocumentException
    {
        // latently subset folders by type from aggregated children
        if (!foldersAggregated)
        {
            NodeSet allChildren = getAll(proxy);
            if (allChildren != null)
            {
                folders = allChildren.subset(Folder.FOLDER_TYPE);
            }
            foldersAggregated = true;
        }
        return folders;
    }
    
    /**
     * getFolder - proxy implementation of Folder.getFolder()
     *
     * @param proxy this folder proxy
     * @param name sub-folder name
     * @return sub-folder
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    public Folder getFolder(Object proxy, String name) throws FolderNotFoundException, DocumentException
    {
        // search for folder by name or absolute path from
        // aggregated folders
        NodeSet allFolders = getFolders(proxy);
        if (allFolders != null)
        {
            Folder folder = (Folder)allFolders.get(name);
            if (folder != null)
            {
                return folder;
            }
        }
        throw new FolderNotFoundException("Folder " + name + " not found at " + getPath());
    }

    /**
     * getLinks - proxy implementation of Folder.getLinks()
     *
     * @param proxy this folder proxy
     * @return list containing all links in folder
     * @throws NodeException
     */    
    public NodeSet getLinks(Object proxy) throws NodeException
    {
        // latently subset links by type from aggregated children
        if (!linksAggregated)
        {
            NodeSet allChildren = getAll(proxy);
            if (allChildren != null)
            {
                links = allChildren.subset(Link.DOCUMENT_TYPE);
            }
            linksAggregated = true;
        }
        return links;
    }
    
    /**
     * getLink - proxy implementation of Folder.getLink()
     *
     * @param proxy this folder proxy
     * @param name link name including extension
     * @return link
     * @throws DocumentNotFoundException
     * @throws NodeException
     */    
    public Link getLink(Object proxy, String name) throws DocumentNotFoundException, NodeException
    {
        // search for link by name or absolute path from
        // aggregated links
        NodeSet allLinks = getLinks(proxy);
        if (allLinks != null)
        {
            Link link = (Link)allLinks.get(name);
            if (link != null)
            {
                return link;
            }
        }
        throw new DocumentNotFoundException("Link " + name + " not found at " + getPath());
    }

    /**
     * getName - proxy implementation of Node.getName()
     *
     * @return name of folder
     */
    public String getName()
    {
        // force root folder name since the folder is
        // normally aggregated using more specific folders;
        // otherwise, use concrete default folder name
        if (getPath().equals(Folder.PATH_SEPARATOR))
        {
            return Folder.PATH_SEPARATOR;
        }
        return defaultFolder.getName();
    }

    /**
     * getPages - proxy implementation of Folder.getPages()
     *
     * @param proxy this folder proxy
     * @return list containing all pages in folder
     * @throws NodeException
     */
    public NodeSet getPages(Object proxy) throws NodeException
    {
        // latently subset pages by type from aggregated children
        if (!pagesAggregated)
        {
            NodeSet allChildren = getAll(proxy);
            if (allChildren != null)
            {
                pages = allChildren.subset(Page.DOCUMENT_TYPE);
            }
            pagesAggregated = true;
        }
        return pages;
    }
    
    /**
     * getPage - proxy implementation of Folder.getPage()
     *
     * @param proxy this folder proxy
     * @param name page name including extension
     * @return page
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public Page getPage(Object proxy, String name) throws PageNotFoundException, NodeException
    {
        // search for page by name or absolute path from
        // aggregated pages
        NodeSet allPages = getPages(proxy);
        if (allPages != null)
        {
            Page page = (Page)allPages.get(name);
            if (page != null)
            {
                return page;
            }
        }
        throw new PageNotFoundException("Page " + name + " not found at " + getPath());
    }

    /**
     * getMetadata - proxy implementation of Folder.getMetadata()
     *
     * @return metadata
     */
    public GenericMetadata getMetadata()
    {
        // return titled concrete folder metadata
        return titledFolder.getMetadata();
    }

    /**
     * getTitle - proxy implementation of Folder.getTitle()
     *
     * @return default title
     */
    public String getTitle()
    {
        // return titled concrete folder title
        return titledFolder.getTitle();
    }

    /**
     * getShortTitle - proxy implementation of Folder.getShortTitle()
     *
     * @return default short title
     */
    public String getShortTitle()
    {
        // return titled concrete folder short title
        return titledFolder.getShortTitle();
    }

    /**
     * getTitle - proxy implementation of Folder.getTitle()
     *
     * @param locale preferred locale
     * @return title
     */
    public String getTitle(Locale locale)
    {
        // return titled concrete folder title
        return titledFolder.getTitle(locale);
    }

    /**
     * getShortTitle - proxy implementation of Folder.getShortTitle()
     *
     * @param locale preferred locale
     * @return short title
     */
    public String getShortTitle(Locale locale)
    {
        // return titled concrete folder short title
        return titledFolder.getShortTitle(locale);
    }

    /**
     * getDefaultFolder - get default proxy delegate folder instance
     *
     * @return default delegate folder
     */
    public Folder getDefaultFolder()
    {
        return defaultFolder;
    }

    /**
     * aggregateMenuDefinitionLocators - aggregate all menu definition locators
     *                                   in site view for this folder or page
     */
    protected void aggregateMenuDefinitionLocators()
    {
        // aggregate folder menu definition locators from most to least
        // specific along inheritance folder graph by name
        try
        {
            Iterator foldersIter = getInheritanceFolders().iterator();
            while (foldersIter.hasNext())
            {
                // get menu definitions from inheritance folders and
                // merge into aggregate menu definition locators
                Folder folder = (Folder)foldersIter.next();
                mergeMenuDefinitionLocators(folder.getMenuDefinitions(), folder);
            }
        }
        catch (FolderNotFoundException fnfe)
        {
        }

        // aggregate standard menu definition locator defaults
        mergeMenuDefinitionLocators(getView().getStandardMenuDefinitionLocators());        
    }

    /**
     * selectDefaultFromAggregateFolders - select most appropriate aggregate concrete
     *                                     folder to use generally in site view at
     *                                     this proxy folder view path
     *                                     
     *
     * @param defaultFolder default concrete folder
     * @return selected concrete folder
     */
    private Folder selectDefaultFromAggregateFolders(Folder defaultFolder)
    {
        // select most specific folder, (i.e. first) along
        // search paths ordered most to least specific
        try
        {
            return ((SearchFolder)getSearchFolders().get(0)).folder;
        }
        catch (FolderNotFoundException fnfe)
        {
        }
        return defaultFolder;
    }

    /**
     * selectTitledFromAggregateFolders - select most appropriate aggregate concrete
     *                                    folder with a title to use in site view at
     *                                    this proxy folder view path
     *
     * @param defaultFolder default concrete folder
     * @return selected concrete folder
     */
    private Folder selectTitledFromAggregateFolders(Folder defaultFolder)
    {
        // select most specific folder along search paths
        // with a specified short title or metadata
        try
        {
            Iterator foldersIter = getSearchFolders().iterator();
            while (foldersIter.hasNext())
            {
                Folder folder = ((SearchFolder)foldersIter.next()).folder;
                GenericMetadata folderMetadata = folder.getMetadata();
                if ((folder.getTitle() != folder.getShortTitle()) ||
                    ((folderMetadata != null) && (folderMetadata.getFields() != null) && !folderMetadata.getFields().isEmpty()))
                {
                    return folder;
                }
            }
        }
        catch (FolderNotFoundException fnfe)
        {
        }
        return defaultFolder;
    }

    /**
     * selectDefaultPageFromAggregateFolders - select most specific default page
     *                                         proxy to use in site view at this
     *                                         proxy folder view path
     *
     * @param proxy this folder proxy
     * @return selected default page proxy
     */
    private Page selectDefaultPageFromAggregateFolders(Object proxy)
    {
        // select most specific specified default page
        // along search paths
        try
        {
            // only test for fallback default page once
            boolean fallbackDefaultPageNotFound = false;
            Iterator foldersIter = getSearchFolders().iterator();
            while (foldersIter.hasNext())
            {
                // get folder default page name or look for fallback default name
                Folder folder = ((SearchFolder)foldersIter.next()).folder;
                String defaultPageName = folder.getFolderMetaData().getDefaultPage();
                if (defaultPageName != null)
                {
                    // validate and return default page if it exists
                    // as child in this folder
                    try
                    {
                        return getPage(proxy, defaultPageName);
                    }
                    catch (NodeException ne)
                    {
                    }
                    catch (SecurityException se)
                    {
                    }
                }
                else if (!fallbackDefaultPageNotFound)
                {
                    // validate and return fallback default page if
                    // it exists as child in this folder
                    try
                    {
                        return getPage(proxy, Folder.FALLBACK_DEFAULT_PAGE);
                    }
                    catch (NodeException ne)
                    {
                        fallbackDefaultPageNotFound = true;
                    }
                    catch (SecurityException se)
                    {
                        fallbackDefaultPageNotFound = true;
                    }
                }
            }
        }
        catch (FolderNotFoundException fnfe)
        {
        }
        return null;
    }

    /**
     * aggregateChildren - aggregate all children proxies in site view
     *
     * @param proxy this folder proxy
     * @return list containing sub-folders, pages, and links in folder view
     */
    private NodeSet aggregateChildren(Object proxy)
    {
        // extract all children and document ordering information
        // from aggregate folders
        try
        {
            // get children proxies
            List allChildren = new ArrayList();
            List folderDocumentOrder = null;
            Iterator foldersIter = getSearchFolders().iterator();
            while (foldersIter.hasNext())
            {
                // aggregate folders
                SearchFolder searchFolder = (SearchFolder)foldersIter.next();
                Folder folder = searchFolder.folder;
                String locatorName = searchFolder.locatorName;

                // create and save proxies for concrete children
                NodeSet children = folder.getAll();
                Iterator childrenIter = children.iterator();
                while (childrenIter.hasNext())
                {
                    Node child = (Node)childrenIter.next();
                    String childName = child.getName();

                    // filter profiling property folders; they are
                    // accessed only via SiteView search path
                    // aggregation that directly utilizes the
                    // current view page manager
                    if (!(child instanceof Folder) || (!childName.startsWith(SiteView.PROFILING_NAVIGATION_PROPERTY_FOLDER_PREFIX) &&
                                                       !childName.startsWith(SiteView.PROFILING_PROPERTY_FOLDER_PREFIX)))
                    {
                        // test child name uniqueness
                        boolean childUnique = true ;
                        Iterator allChildrenIter = allChildren.iterator();
                        while (childUnique && allChildrenIter.hasNext())
                        {
                            childUnique = !childName.equals(((Node)allChildrenIter.next()).getName());                            
                        }

                        // add uniquely named children proxies
                        if (childUnique)
                        {
                            if (child instanceof Folder)
                            {
                                allChildren.add(FolderProxy.newInstance(getView(), locatorName, (Folder)proxy, (Folder)child));
                            }
                            else if (child instanceof Page)
                            {
                                allChildren.add(PageProxy.newInstance(getView(), locatorName, (Folder)proxy, (Page)child));
                            }
                            else if (child instanceof Link)
                            {
                                allChildren.add(LinkProxy.newInstance(getView(), locatorName, (Folder)proxy, (Link)child));
                            }
                        }
                    }
                }

                // capture most specific document ordering
                if (folderDocumentOrder == null)
                {
                    FolderMetaData metadata = folder.getFolderMetaData();
                    if (metadata != null)
                    {
                        List documentOrder = metadata.getDocumentOrder();
                        if ((documentOrder != null) && !documentOrder.isEmpty()) 
                        {
                            folderDocumentOrder = documentOrder;
                        }
                    }
                }
            }

            // sort children proxies if more than one by folder
            // document order or strict collation order
            if (allChildren.size() > 1)
            {
                final List order = folderDocumentOrder;
                Comparator comparator = new Comparator()
                    {
                        public int compare(Object proxyNode1, Object proxyNode2)
                        {
                            // compare names of nodes against order or each other by default
                            String name1 = ((Node)proxyNode1).getName();
                            String name2 = ((Node)proxyNode2).getName();
                            if (order != null)
                            {
                                // compare names against order
                                int index1 = order.indexOf(name1);
                                int index2 = order.indexOf(name2);
                                if ((index1 != -1) || (index2 != -1))
                                {
                                    if ((index1 == -1) && (index2 != -1))
                                    {
                                        return 1;
                                    }
                                    if ((index1 != -1) && (index2 == -1))
                                    {
                                        return -1;
                                    }
                                    return index1-index2;
                                }
                            }
                            // compare names against each other
                            return name1.compareTo(name2);
                        }
                    } ;
                Collections.sort(allChildren, comparator);
            }

            // wrap ordered children in new NodeSet
            if (!allChildren.isEmpty())
            {
                return new NodeSetImpl(allChildren);
            }
        }
        catch (FolderNotFoundException fnfe)
        {
        }
        catch (DocumentException de)
        {
        }
        return null;
    }

    /**
     * getSearchFolders - aggregate all concrete folders in site view
     *                    at this proxy folder view path
     *
     * @return list containing concrete search folders in folder view
     * @throws FolderNotFoundException
     */
    private List getSearchFolders() throws FolderNotFoundException
    {
        // latently aggregate search folders
        if (searchFolders == null)
        {
            // search for existing folders along search paths
            List searchPaths = getView().getSearchPaths();
            searchFolders = new ArrayList(searchPaths.size());
            Iterator pathsIter = searchPaths.iterator();
            while (pathsIter.hasNext())
            {
                // construct folder paths
                SiteViewSearchPath searchPath = (SiteViewSearchPath)pathsIter.next();
                String path = searchPath.toString();
                if (!path.equals(Folder.PATH_SEPARATOR))
                {
                    path += getPath();
                }
                else
                {
                    path = getPath();
                }
                
                // get existing folders from PageManager, create
                // corresponding search folder objects, and add to
                // search folders list
                try
                {
                    Folder folder = getView().getPageManager().getFolder(path);
                    if (folder != null)
                    {
                        searchFolders.add(new SearchFolder(folder, searchPath.getLocatorName()));
                    }
                }
                catch (NodeException ne)
                {
                }
                catch (SecurityException se)
                {
                }
            }
        }

        // return search folders
        if (!searchFolders.isEmpty())
        {
            return searchFolders;
        }
        throw new FolderNotFoundException("Search folders at " + getPath() + " not found or accessible");
    }

    /**
     * getInheritanceFolders - aggregate all concrete inheritance folders
     *                         in site view at this proxy folder view path
     *
     * @return list containing concrete inheritance folders in folder view
     * @throws FolderNotFoundException
     */
    private List getInheritanceFolders() throws FolderNotFoundException
    {
        // latently aggregate inheritance folders
        if (inheritanceFolders == null)
        {
            // inheritance folders are aggregated from super/parent
            // folder search paths for each proxy folder in the view
            // path; concatinate all search paths from this proxy
            // folder to the proxy root to create the inheritance
            // graph folder list
            FolderProxy folder = this;
            List searchFolders = folder.getSearchFolders();
            if (getParent() != null)
            {
                inheritanceFolders = new ArrayList(searchFolders.size() * 2);
            }
            else
            {
                inheritanceFolders = new ArrayList(searchFolders.size());
            }        
            do
            {
                // copy ordered search path folders into inheritance
                // graph folders list
                Iterator foldersIter = searchFolders.iterator();
                while (foldersIter.hasNext())
                {
                    inheritanceFolders.add(((SearchFolder)foldersIter.next()).folder);
                }

                // get super/parent search paths
                folder = (FolderProxy)getNodeProxy(folder.getParent());
                if (folder != null)
                {
                    searchFolders = folder.getSearchFolders();
                }
            }
            while (folder != null);
        }

        // return inheritance folders
        if (!inheritanceFolders.isEmpty())
        {
            return inheritanceFolders;
        }
        throw new FolderNotFoundException("Inheritance folders at " + getPath() + " not found or accessible");
    }
}
