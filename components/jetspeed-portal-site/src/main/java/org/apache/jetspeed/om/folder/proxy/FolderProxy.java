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
package org.apache.jetspeed.om.folder.proxy;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
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
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.page.document.proxy.NodeSetImpl;
import org.apache.jetspeed.portalsite.view.SearchPathsSiteView;
import org.apache.jetspeed.portalsite.view.SiteViewSearchPath;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
    protected static final Method GET_DEFAULT_PAGE_METHOD = reflectMethod(Folder.class, "getDefaultPage", null);
    protected static final Method GET_DYNAMIC_PAGES_METHOD = reflectMethod(Folder.class, "getDynamicPages", null);
    protected static final Method GET_DYNAMIC_PAGE_METHOD = reflectMethod(Folder.class, "getDynamicPage", new Class[]{String.class});
    protected static final Method GET_FOLDERS_METHOD = reflectMethod(Folder.class, "getFolders", null);
    protected static final Method GET_FOLDER_METHOD = reflectMethod(Folder.class, "getFolder", new Class[]{String.class});
    protected static final Method GET_FRAGMENT_DEFINITIONS_METHOD = reflectMethod(Folder.class, "getFragmentDefinitions", null);
    protected static final Method GET_FRAGMENT_DEFINITION_METHOD = reflectMethod(Folder.class, "getFragmentDefinition", new Class[]{String.class});
    protected static final Method GET_LINKS_METHOD = reflectMethod(Folder.class, "getLinks", null);
    protected static final Method GET_LINK_METHOD = reflectMethod(Folder.class, "getLink", new Class[]{String.class});
    protected static final Method GET_MENU_DEFINITIONS_METHOD = reflectMethod(Folder.class, "getMenuDefinitions", null);
    protected static final Method GET_METADATA_METHOD = reflectMethod(Folder.class, "getMetadata", null);
    protected static final Method GET_NAME_METHOD = reflectMethod(Folder.class, "getName", null);
    protected static final Method GET_PAGES_METHOD = reflectMethod(Folder.class, "getPages", null);
    protected static final Method GET_PAGE_METHOD = reflectMethod(Folder.class, "getPage", new Class[]{String.class});
    protected static final Method GET_PAGE_SECURITY_METHOD = reflectMethod(Folder.class, "getPageSecurity", null);
    protected static final Method GET_PAGE_TEMPLATES_METHOD = reflectMethod(Folder.class, "getPageTemplates", null);
    protected static final Method GET_PAGE_TEMPLATE_METHOD = reflectMethod(Folder.class, "getPageTemplate", new Class[]{String.class});
    protected static final Method GET_SHORT_TITLE_LOCALE_METHOD = reflectMethod(Folder.class, "getShortTitle", new Class[]{Locale.class});
    protected static final Method GET_SHORT_TITLE_METHOD = reflectMethod(Folder.class, "getShortTitle", null);
    protected static final Method GET_TITLE_LOCALE_METHOD = reflectMethod(Folder.class, "getTitle", new Class[]{Locale.class});
    protected static final Method GET_TITLE_METHOD = reflectMethod(Folder.class, "getTitle", null);

    /**
     * defaultFolderReference - default proxy delegate folder instance reference
     */
    private FolderWeakReference defaultFolderReference;

    /**
     * titledFolderReference - titled proxy delegate folder instance reference
     */
    private FolderWeakReference titledFolderReference;

    /**
     * forceReservedVisible - flag used to suppress child reserved/hidden folder visibility checks
     */
    private boolean forceReservedVisible;

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
     * pageTemplates - aggregated proxy page template nodes
     */
    private NodeSet pageTemplates;

    /**
     * pageTemplatesAggregated - boolean flag to indicate page templates aggregated
     */
    private boolean pageTemplatesAggregated;

    /**
     * dynamicPages - aggregated proxy dynamic page nodes
     */
    private NodeSet dynamicPages;

    /**
     * dynamicPagesAggregated - boolean flag to indicate dynamic pages aggregated
     */
    private boolean dynamicPagesAggregated;

    /**
     * fragmentDefinitions - aggregated proxy fragment definition nodes
     */
    private NodeSet fragmentDefinitions;

    /**
     * fragmentDefinitionsAggregated - boolean flag to indicate fragment definitions aggregated
     */
    private boolean fragmentDefinitionsAggregated;

    /**
     * links - aggregated proxy link nodes
     */
    private NodeSet links;

    /**
     * linksAggregated - boolean flag to indicate links aggregated
     */
    private boolean linksAggregated;

    /**
     * SearchFolder - data object used hold concrete search folder reference
     *                and related search path profile locator name pairs
     */
    private class SearchFolder
    {
        public FolderWeakReference folderReference;
        public String locatorName;

        public SearchFolder(FolderWeakReference folderReference, String locatorName)
        {
            this.folderReference = folderReference;
            this.locatorName = locatorName;
        }
    }

    /**
     * searchFolders - search folder objects along view search paths
     *                 in most to least specific order
     */
    private List<SearchFolder> searchFolders;

    /**
     * InheritanceFolder - data object used hold aggregated concrete search
     *                     folder references and view path to folder 
     */
    private class InheritanceFolder
    {
        public FolderWeakReference folderReference;
        public String path;

        public InheritanceFolder(FolderWeakReference folderReference, String path)
        {
            this.folderReference = folderReference;
            this.path = path;
        }
    }

    /**
     * inheritanceFolders - inheritance graph folder list in most to
     *                      least specific order
     */
    private List<InheritanceFolder> inheritanceFolders;
        
    /**
     * newInstance - creates a new proxy instance that implements the Folder interface
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param folder proxy delegate
     * @param forceReservedVisible suppress reserved/hidden folder visibility checks
     */
    public static Folder newInstance(SearchPathsSiteView view, String locatorName, Folder parentFolder, Folder folder, boolean forceReservedVisible)
    {
        return (Folder)Proxy.newProxyInstance(folder.getClass().getClassLoader(), new Class[]{Folder.class}, new FolderProxy(view, locatorName, parentFolder, folder, forceReservedVisible));
    }

    /**
     * FolderProxy - private constructor used by newInstance()
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param folder proxy delegate
     * @param forceReservedVisible suppress reserved/hidden folder visibility checks
     */
    private FolderProxy(SearchPathsSiteView view, String locatorName, Folder parentFolder, Folder folder, boolean forceReservedVisible)
    {
        super(view, locatorName, parentFolder, folder.getName(), folder.isHidden());
        this.defaultFolderReference = selectDefaultFromAggregateFolders(folder);
        this.titledFolderReference = selectTitledFromAggregateFolders(this.defaultFolderReference);
        this.forceReservedVisible = forceReservedVisible;
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
    public Object invoke(Object proxy, Method method, Object [] args) throws Throwable
    {
        // proxy implementation method dispatch
        if (method.equals(GET_ALL_METHOD))
        {
            return getAll(proxy);
        }
        else if (method.equals(GET_DEFAULT_PAGE_METHOD))
        {
            return getDefaultPage(proxy);
        }
        else if (method.equals(GET_DYNAMIC_PAGES_METHOD))
        {
            return getDynamicPages(proxy);
        }
        else if (method.equals(GET_DYNAMIC_PAGE_METHOD))
        {
            return getDynamicPage(proxy, (String)args[0]);
        }
        else if (method.equals(GET_FOLDERS_METHOD))
        {
            return getFolders(proxy);
        }
        else if (method.equals(GET_FOLDER_METHOD))
        {
            return getFolder(proxy, (String)args[0]);
        }
        else if (method.equals(GET_FRAGMENT_DEFINITIONS_METHOD))
        {
            return getFragmentDefinitions(proxy);
        }
        else if (method.equals(GET_FRAGMENT_DEFINITION_METHOD))
        {
            return getFragmentDefinition(proxy, (String)args[0]);
        }
        else if (method.equals(GET_LINKS_METHOD))
        {
            return getLinks(proxy);
        }
        else if (method.equals(GET_LINK_METHOD))
        {
            return getLink(proxy, (String)args[0]);
        }
        else if (method.equals(GET_MENU_DEFINITIONS_METHOD))
        {
            return getMenuDefinitions();
        }
        else if (method.equals(GET_METADATA_METHOD))
        {
            return getMetadata();
        }
        else if (method.equals(GET_NAME_METHOD))
        {
            return getName();
        }
        else if (method.equals(GET_PAGES_METHOD))
        {
            return getPages(proxy);
        }
        else if (method.equals(GET_PAGE_METHOD))
        {
            return getPage(proxy, (String)args[0]);
        }
        else if (method.equals(GET_PAGE_TEMPLATES_METHOD))
        {
            return getPageTemplates(proxy);
        }
        else if (method.equals(GET_PAGE_TEMPLATE_METHOD))
        {
            return getPageTemplate(proxy, (String)args[0]);
        }
        else if (method.equals(GET_SHORT_TITLE_LOCALE_METHOD))
        {
            return getShortTitle((Locale)args[0]);
        }
        else if (method.equals(GET_SHORT_TITLE_METHOD))
        {
            return getShortTitle();
        }
        else if (method.equals(GET_TITLE_LOCALE_METHOD))
        {
            return getTitle((Locale)args[0]);
        }
        else if (method.equals(GET_TITLE_METHOD))
        {
            return getTitle();
        }
        else if (method.equals(GET_PARENT_METHOD))
        {
            return getParent();
        }
        else if (method.equals(GET_PATH_METHOD))
        {
            return getPath();
        }
        else if (method.equals(GET_URL_METHOD))
        {
            return getUrl();
        }
        else if (method.equals(EQUALS_METHOD))
        {
            return new Boolean(equals(args[0]));
        }
        else if (method.equals(HASH_CODE_METHOD))
        {
            return new Integer(hashCode());
        }
        else if (method.equals(IS_HIDDEN_METHOD))
        {
            return new Boolean(isHidden());
        }
        else if (method.equals(TO_STRING_METHOD))
        {
            return toString();
        }
    
        // proxy suppression of not implemented or mutable methods
        if (method.equals(GET_PAGE_SECURITY_METHOD) ||
            method.getName().startsWith("set"))
        {
            throw new RuntimeException("Folder instance is immutable from proxy.");
        }

        try
        {
            // attempt to invoke method on delegate Folder instance
            return method.invoke(defaultFolderReference.getFolder(), args);
        }
        catch (InvocationTargetException ite)
        {
            throw ite.getTargetException();
        }
    }

    /**
     * getAll - proxy implementation of Folder.getAll()
     *
     * @param proxy this folder proxy
     * @return list containing sub-folders and documents in folder
     * @throws DocumentException
     */
    public NodeSet getAll(Object proxy) throws DocumentException
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
     * @return default page name
     */
    public String getDefaultPage(Object proxy)
    {
        // attempt to get explicitly specified default page
        return selectDefaultPageFromAggregateFolders(proxy);
    }

    /**
     * getFolders - proxy implementation of Folder.getFolders()
     *
     * @param proxy this folder proxy
     * @return list containing all sub-folders in folder
     * @throws DocumentException
     */
    public NodeSet getFolders(Object proxy) throws DocumentException
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
        return defaultFolderReference.getFolder().getName();
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
     * getDynamicPages - proxy implementation of Folder.getDynamicPages()
     *
     * @param proxy this folder proxy
     * @return list containing all dynamic pages in folder
     * @throws NodeException
     */
    public NodeSet getDynamicPages(Object proxy) throws NodeException
    {
        // latently subset dynamic pages by type from aggregated children
        if (!dynamicPagesAggregated)
        {
            NodeSet allChildren = getAll(proxy);
            if (allChildren != null)
            {
                dynamicPages = allChildren.subset(DynamicPage.DOCUMENT_TYPE);
            }
            dynamicPagesAggregated = true;
        }
        return dynamicPages;
    }
    
    /**
     * getDynamicPage - proxy implementation of Folder.getDynamicPage()
     *
     * @param proxy this folder proxy
     * @param name dynamic page name including extension
     * @return dynamic page
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public DynamicPage getDynamicPage(Object proxy, String name) throws PageNotFoundException, NodeException
    {
        // search for dynamic page by name or absolute path from
        // aggregated dynamic pages
        NodeSet allDynamicPages = getDynamicPages(proxy);
        if (allDynamicPages != null)
        {
            DynamicPage dynamicPage = (DynamicPage)allDynamicPages.get(name);
            if (dynamicPage != null)
            {
                return dynamicPage;
            }
        }
        throw new PageNotFoundException("DynamicPage " + name + " not found at " + getPath());
    }

    /**
     * getPageTemplates - proxy implementation of Folder.getPageTemplates()
     *
     * @param proxy this folder proxy
     * @return list containing all page templates in folder
     * @throws NodeException
     */
    public NodeSet getPageTemplates(Object proxy) throws NodeException
    {
        // latently subset page templates by type from aggregated children
        if (!pageTemplatesAggregated)
        {
            NodeSet allChildren = getAll(proxy);
            if (allChildren != null)
            {
                pageTemplates = allChildren.subset(PageTemplate.DOCUMENT_TYPE);
            }
            pageTemplatesAggregated = true;
        }
        return pageTemplates;
    }
    
    /**
     * getPageTemplate - proxy implementation of Folder.getPageTemplate()
     *
     * @param proxy this folder proxy
     * @param name page template name including extension
     * @return page template
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public PageTemplate getPageTemplate(Object proxy, String name) throws PageNotFoundException, NodeException
    {
        // search for page template by name or absolute path from
        // aggregated page templates
        NodeSet allPageTemplates = getPageTemplates(proxy);
        if (allPageTemplates != null)
        {
            PageTemplate pageTemplate = (PageTemplate)allPageTemplates.get(name);
            if (pageTemplate != null)
            {
                return pageTemplate;
            }
        }
        throw new PageNotFoundException("PageTemplate " + name + " not found at " + getPath());
    }

    /**
     * getFragmentDefinitions - proxy implementation of Folder.getFragmentDefinitions()
     *
     * @param proxy this folder proxy
     * @return list containing all fragment definitions in folder
     * @throws NodeException
     */
    public NodeSet getFragmentDefinitions(Object proxy) throws NodeException
    {
        // latently subset fragment definition by type from aggregated children
        if (!fragmentDefinitionsAggregated)
        {
            NodeSet allChildren = getAll(proxy);
            if (allChildren != null)
            {
                fragmentDefinitions = allChildren.subset(FragmentDefinition.DOCUMENT_TYPE);
            }
            fragmentDefinitionsAggregated = true;
        }
        return fragmentDefinitions;
    }
    
    /**
     * getFragmentDefinition - proxy implementation of Folder.getFragmentDefinition()
     *
     * @param proxy this folder proxy
     * @param name fragment definition name including extension
     * @return fragment definition
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public FragmentDefinition getFragmentDefinition(Object proxy, String name) throws PageNotFoundException, NodeException
    {
        // search for fragment definition by name or absolute path from
        // aggregated fragment definitions
        NodeSet allFragmentDefinitions = getFragmentDefinitions(proxy);
        if (allFragmentDefinitions != null)
        {
            FragmentDefinition fragmentDefinition = (FragmentDefinition)allFragmentDefinitions.get(name);
            if (fragmentDefinition != null)
            {
                return fragmentDefinition;
            }
        }
        throw new PageNotFoundException("FragmentDefinition " + name + " not found at " + getPath());
    }

    /**
     * getMetadata - proxy implementation of Folder.getMetadata()
     *
     * @return metadata
     */
    public GenericMetadata getMetadata()
    {
        // return titled concrete folder metadata
        return titledFolderReference.getFolder().getMetadata();
    }

    /**
     * getTitle - proxy implementation of Folder.getTitle()
     *
     * @return default title
     */
    public String getTitle()
    {
        // return titled concrete folder title
        return titledFolderReference.getFolder().getTitle();
    }

    /**
     * getShortTitle - proxy implementation of Folder.getShortTitle()
     *
     * @return default short title
     */
    public String getShortTitle()
    {
        // return titled concrete folder short title
        return titledFolderReference.getFolder().getShortTitle();
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
        return titledFolderReference.getFolder().getTitle(locale);
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
        return titledFolderReference.getFolder().getShortTitle(locale);
    }

    /**
     * getDefaultFolder - get default proxy delegate folder instance
     *
     * @return default delegate folder
     */
    public Folder getDefaultFolder()
    {
        return defaultFolderReference.getFolder();
    }
    
    /**
     * checkAccessToFolderNotFound - checks security access to child folder
     *                               nodes not found in aggregated children
     *                               site view when accessed directly; folders
     *                               part of the view are by definition
     *                               accessible
     *
     * @param folderName name of child folder to check
     * @throws SecurityException if view access to folder not granted
     */
    public void checkAccessToFolderNotFound(String folderName)
    {
        try
        {
            // check access on concrete child in all search folders
            for (SearchFolder searchFolder : getSearchFolders())
            {
                // test access against child in search folder
                Folder folder = searchFolder.folderReference.getFolder();
                // ignore all folder access exceptions, (throws SecurityException on failed check access)
                try
                {
                    folder.getFolder(folderName);
                }
                catch (DocumentException de)
                {                    
                }
                catch (FolderNotFoundException fnfe)
                {
                }
            }
        }
        catch (FolderNotFoundException fnfe)
        {
        }
    }

    /**
     * checkAccessToNodeNotFound - checks security access to child node
     *                             nodes not found in aggregated children
     *                             site view when accessed directly; pages,
     *                             folders, and links part of the view are
     *                             by definition accessible
     *
     * @throws SecurityException if view access to node not granted
     */
    public void checkAccessToNodeNotFound(String nodeName)
    {
        try
        {
            // check access on concrete child in all search folders
            for (SearchFolder searchFolder : getSearchFolders())
            {
                // test access against child in search folder
                Folder folder = searchFolder.folderReference.getFolder();
                // ignore all folder access exceptions, (throws SecurityException on failed check access)
                try
                {
                    folder.getFolder(nodeName);
                }
                catch (DocumentException de)
                {                    
                }
                catch (FolderNotFoundException fnfe)
                {
                }
                // ignore all page access exceptions, (throws SecurityException on failed check access)
                try
                {
                    folder.getPage(nodeName);
                }
                catch (NodeException ne)
                {                    
                }
                catch (PageNotFoundException ne)
                {                    
                }
                // ignore all link access exceptions, (throws SecurityException on failed check access)
                try
                {
                    folder.getLink(nodeName);
                }
                catch (NodeException ne)
                {                    
                }
                catch (DocumentNotFoundException ne)
                {                    
                }
            }
        }
        catch (FolderNotFoundException fnfe)
        {
        }
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
            for (InheritanceFolder inheritanceFolder : getInheritanceFolders())
            {
                // get menu definitions from inheritance folders and
                // merge into aggregate menu definition locators
                Folder folder = inheritanceFolder.folderReference.getFolder();
                String path = inheritanceFolder.path;
                mergeMenuDefinitionLocators(folder.getMenuDefinitions(), folder, path, false);
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
     * @return selected concrete folder reference
     */
    private FolderWeakReference selectDefaultFromAggregateFolders(Folder defaultFolder)
    {
        // select most specific folder, (i.e. first) along
        // search paths ordered most to least specific
        try
        {
            return ((SearchFolder)getSearchFolders().get(0)).folderReference;
        }
        catch (FolderNotFoundException fnfe)
        {
        }
        return new FolderWeakReference(getView().getPageManager(), defaultFolder);
    }

    /**
     * selectTitledFromAggregateFolders - select most appropriate aggregate concrete
     *                                    folder with a title to use in site view at
     *                                    this proxy folder view path
     *
     * @param defaultFolder default concrete folder reference
     * @return selected concrete folder reference
     */
    private FolderWeakReference selectTitledFromAggregateFolders(FolderWeakReference defaultFolder)
    {
        // select most specific folder along search paths
        // with a specified title, short title, or metadata
        try
        {
            for (SearchFolder searchFolder : getSearchFolders())
            {
                FolderWeakReference folderReference = searchFolder.folderReference;
                Folder folder = folderReference.getFolder();
                String name = folder.getName();
                String title = folder.getTitle();
                String shortTitle = folder.getShortTitle();
                GenericMetadata folderMetadata = folder.getMetadata();
                if (((title != null) && !title.equalsIgnoreCase(name)) ||
                    ((shortTitle != null) && !shortTitle.equalsIgnoreCase(name)) ||
                    ((folderMetadata != null) && (folderMetadata.getFields() != null) && !folderMetadata.getFields().isEmpty()))
                {
                    return folderReference;
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
     * @return selected default page name
     */
    private String selectDefaultPageFromAggregateFolders(Object proxy)
    {
        // select most specific specified default page
        // along search paths
        try
        {
            // only test for fallback default page once
            boolean fallbackDefaultPageNotFound = false;
            for (SearchFolder searchFolder : getSearchFolders())
            {
                // get folder default page name or look for fallback default name
                Folder folder = searchFolder.folderReference.getFolder();
                String defaultPageName = folder.getDefaultPage();
                if (defaultPageName != null)
                {
                    // validate and return default page or folder
                    // if it exists as child in this folder
                    if (defaultPageName.equals(".."))
                    {
                        // default parent folder
                        if (getParent() != null)
                        {
                            return defaultPageName;
                        }
                    }
                    else
                    {
                        // default page
                        try
                        {
                            getPage(proxy, defaultPageName);
                            return defaultPageName;
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
                        // default folder
                        if (!defaultPageName.endsWith(Page.DOCUMENT_TYPE))
                        {
                            try
                            {
                                getFolder(proxy, defaultPageName);
                                return defaultPageName;
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
                }
                else if (!fallbackDefaultPageNotFound)
                {
                    // validate and return fallback default page if
                    // it exists as child in this folder
                    try
                    {
                        getPage(proxy, Folder.FALLBACK_DEFAULT_PAGE);
                        return Folder.FALLBACK_DEFAULT_PAGE;
                    }
                    catch (NodeException ne)
                    {
                        fallbackDefaultPageNotFound = true;
                    }
                    catch (NodeNotFoundException nnfe)
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
            List<Node> allChildren = new ArrayList<Node>();
            List<String> folderDocumentOrder = null;
            for (SearchFolder searchFolder : getSearchFolders())
            {
                // aggregate folders
                Folder folder = searchFolder.folderReference.getFolder();
                String locatorName = searchFolder.locatorName;

                // create and save proxies for concrete children
                NodeSet children = folder.getAll();
                for (Node child : children)
                {
                    String childName = child.getName();

                    // filter profiling property folders unless forced; they are
                    // normally accessed only via SiteView search path aggregation
                    // that directly utilizes the current view page manager
                    boolean visible = (forceReservedVisible || (!(child instanceof Folder) ||
                                                                (!childName.startsWith(Folder.RESERVED_SUBSITE_FOLDER_PREFIX) &&
                                                                 !childName.startsWith(Folder.RESERVED_FOLDER_PREFIX))));
                    if (visible)
                    {
                        // test child name uniqueness
                        boolean childUnique = true ;
                        for (Node testChild : allChildren)
                        {
                            if (childName.equals(testChild.getName()))
                            {
                                childUnique = false;
                                break;
                            }
                        }

                        // add uniquely named children proxies
                        if (childUnique)
                        {
                            if (child instanceof Folder)
                            {
                                allChildren.add(FolderProxy.newInstance(getView(), locatorName, (Folder)proxy, (Folder)child, forceReservedVisible));
                            }
                            else if (child instanceof Page)
                            {
                                allChildren.add(PageProxy.newInstance(getView(), locatorName, (Folder)proxy, (Page)child));
                            }
                            else if (child instanceof PageTemplate)
                            {
                                allChildren.add(PageTemplateProxy.newInstance(getView(), locatorName, (Folder)proxy, (PageTemplate)child));
                            }
                            else if (child instanceof DynamicPage)
                            {
                                allChildren.add(DynamicPageProxy.newInstance(getView(), locatorName, (Folder)proxy, (DynamicPage)child));
                            }
                            else if (child instanceof FragmentDefinition)
                            {
                                allChildren.add(FragmentDefinitionProxy.newInstance(getView(), locatorName, (Folder)proxy, (FragmentDefinition)child));
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
                    List<String> documentOrder = folder.getDocumentOrder();
                    if ((documentOrder != null) && !documentOrder.isEmpty()) 
                    {
                        folderDocumentOrder = documentOrder;
                    }
                }
            }

            // sort children proxies if more than one by folder
            // document order or strict collation order
            if (allChildren.size() > 1)
            {
                final List<String> order = folderDocumentOrder;
                Comparator<Node> comparator = new Comparator<Node>()
                    {
                        public int compare(Node proxyNode1, Node proxyNode2)
                        {
                            // compare names of nodes against order or each other by default
                            String name1 = proxyNode1.getName();
                            String name2 = proxyNode2.getName();
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
    private List<SearchFolder> getSearchFolders() throws FolderNotFoundException
    {
        // latently aggregate search folders
        if (searchFolders == null)
        {
            // search for existing folders along search paths
            List<SiteViewSearchPath> searchPaths = getView().getSearchPaths();
            searchFolders = new ArrayList<SearchFolder>(searchPaths.size());
            for (SiteViewSearchPath searchPath : searchPaths)
            {
                // construct folder paths
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
                    PageManager pageManager = getView().getPageManager();
                    Folder folder = pageManager.getFolder(path);
                    if (folder != null)
                    {
                        searchFolders.add(new SearchFolder(new FolderWeakReference(pageManager, folder), searchPath.getLocatorName()));
                    }
                }
                catch (NodeException ne)
                {
                }
                catch (NodeNotFoundException ne)
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
    private List<InheritanceFolder> getInheritanceFolders() throws FolderNotFoundException
    {
        // latently aggregate inheritance folders
        if (inheritanceFolders == null)
        {
            // inheritance folders are aggregated from super/parent
            // folder search paths for each proxy folder in the view
            // path; concatenate all search paths from this proxy
            // folder to the proxy root to create the inheritance
            // graph folder list
            FolderProxy folder = this;
            List<SearchFolder> searchFolders = folder.getSearchFolders();
            if (getParent() != null)
            {
                inheritanceFolders = new ArrayList<InheritanceFolder>(searchFolders.size() * 2);
            }
            else
            {
                inheritanceFolders = new ArrayList<InheritanceFolder>(searchFolders.size());
            }        
            do
            {
                // copy ordered search path folders into inheritance
                // graph folders list
                for (SearchFolder searchFolder : searchFolders)
                {
                    inheritanceFolders.add(new InheritanceFolder(searchFolder.folderReference, folder.getPath()));
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

    /**
     * getFolderProxy - utility method to access FolderProxy handler
     *                  from Folder proxy instance
     *
     * @param folder folder proxy instance
     * @return folder proxy invocation handler instance
     */
    public static FolderProxy getFolderProxy(Object folder)
    {
        if ((folder != null) && Proxy.isProxyClass(folder.getClass()))
        {
            Object folderProxyHandler = Proxy.getInvocationHandler(folder);
            if (folderProxyHandler instanceof FolderProxy)
            {
                return (FolderProxy)folderProxyHandler;
            }
        }
        return null;
    }
}
