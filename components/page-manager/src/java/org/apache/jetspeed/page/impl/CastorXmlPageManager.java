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

package org.apache.jetspeed.page.impl;

//standard java stuff
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderMetaData;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.AbstractNode;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.NodeSetImpl;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.jetspeed.profiler.ProfiledPageContext;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfileLocatorProperty;

/**
 * This service is responsible for loading and saving PSML pages serialized to
 * disk
 * 
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta </a>
 * @author <a href="mailto:weaver@apache.org">Scott T Weaver </a>
 * @version $Id$
 */
public class CastorXmlPageManager extends AbstractPageManager implements PageManager
{
    private final static Log log = LogFactory.getLog(CastorXmlPageManager.class);

    protected final static String PROFILE_PROPERTY_FOLDER_PREFIX = "_";

    private DocumentHandlerFactory handlerFactory;

    private FolderHandler folderHandler;

    private boolean profilingEnabled;

    // default configuration values

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory hanlderFactory,
                                 FolderHandler folderHandler, boolean profilingEnabled ) throws FileNotFoundException
    {
        super(generator);
        this.handlerFactory = hanlderFactory;
        this.folderHandler = folderHandler;
        this.profilingEnabled = profilingEnabled;
    }

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory hanlderFactory,
                                 FolderHandler folderHandler, boolean profilingEnabled, List modelClasses ) throws FileNotFoundException
    {
        super(generator, modelClasses);
        this.handlerFactory = hanlderFactory;
        this.folderHandler = folderHandler;
        this.profilingEnabled = profilingEnabled;
    }

    /**
     * 
     * <p>
     * getProfiledPageContext
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getProfiledPageContext(org.apache.jetspeed.profiler.ProfileLocator)
     * @param locator
     * @return profiled page context
     * @throws PageNotFoundException
     * @throws DocmentException
     * @throws NodeException
     */
    public ProfiledPageContext getProfiledPageContext( ProfileLocator locator ) throws PageNotFoundException, DocumentException, NodeException
    {
        // determine profiled page context using profile locator.
        // TODO: implement caching of profiled page contexts if practical.

        log.debug("getProfiledPageContext() invoked, locator = " + locator + ", (profilingEnabled = " + profilingEnabled + ")");

        // get request path
        String requestPath = locator.getRequestPath();

        // get profiled page context initialization parameters
        Folder folder = null;
        Page page = null;
        NodeSet siblingPages = null;
        Folder parentFolder = null;
        NodeSet siblingFolders = null;
        NodeSet rootLinks = null;
        if (profilingEnabled)
        {
            // profile page request using profile locator

            Folder [] profiledFolder = new Folder[1];
            Page [] profiledPage = new Page[1];
            List profiledFolders = new ArrayList();

            // generate profile locator folder/page search paths
            List searchPaths = generateProfilingPageSearchPaths(requestPath, locator, false);

            // find page in page manager content using search paths
            boolean profiled = findProfiledPageAndFolders(searchPaths, profiledFolder, profiledPage, profiledFolders);

            // profile fallback to default root folder to locate folder/page
            boolean rootPathProfiled = false;
            if (rootPathProfiled = (! profiled && ! requestPath.equals("/")))
            {
                log.warn("getProfiledPageContext(): Falling back to profiled root default page for " + requestPath);
                searchPaths = generateProfilingPageSearchPaths("/", locator, true);
                profiled = findProfiledPageAndFolders(searchPaths, profiledFolder, profiledPage, profiledFolders);
            }

            // profiled folder and page
            if (profiled)
            {
                folder = (Folder) setProfiledNodeUrl(profiledFolder[0]);
                page = (Page) setProfiledNodeUrl(profiledPage[0]);
            }

            // profiled page context
            if (page != null)
            {
                // profile general document/folder order
                List documentOrder = null;
                Iterator foldersIter = profiledFolders.iterator();
                while ((documentOrder == null) && foldersIter.hasNext())
                {
                    FolderImpl profiledPageFolder = (FolderImpl) setProfiledNodeUrl((Node) foldersIter.next());
                    if ((profiledPageFolder.getMetaData() != null) && (profiledPageFolder.getMetaData().getDocumentOrder() != null) &&
                        ! profiledPageFolder.getMetaData().getDocumentOrder().isEmpty())
                    {
                        documentOrder = profiledPageFolder.getMetaData().getDocumentOrder();
                    }
                }
                Comparator documentComparator = new DocumentOrderComparator(documentOrder);

                // profile sibling pages by aggregating all siblings in profiled folders
                // using profiled general document order, (do not filter unordered siblings)
                siblingPages = new NodeSetImpl(null, documentComparator);
                foldersIter = profiledFolders.iterator();
                while (foldersIter.hasNext())
                {
                    Folder aggregatePagesFolder = (Folder) foldersIter.next();
                    NodeSet aggregatePages = aggregatePagesFolder.getPages();
                    Iterator aggregatePagesIter = aggregatePages.iterator();
                    while (aggregatePagesIter.hasNext())
                    {
                        siblingPages = addUniqueOrDescribedUrlNode((NodeSetImpl) siblingPages, setProfiledNodeUrl((Node) aggregatePagesIter.next()));
                    }
                }

                // profile parent folder using profiled parent
                if ((folder.getParent() != null) && ! folder.getUrl().equals("/"))
                {
                    parentFolder = (Folder) setProfiledNodeUrl(folder.getParent());
                }

                // profile sibling folders by aggregating all siblings in profiled folders
                // using profiled general document order, (do not filter unordered siblings)
                siblingFolders = new NodeSetImpl(null, documentComparator);
                foldersIter = profiledFolders.iterator();
                while (foldersIter.hasNext())
                {
                    Folder aggregateFoldersFolder = (Folder) foldersIter.next();
                    NodeSet aggregateFolders = aggregateFoldersFolder.getFolders().exclusiveSubset("^.*/" + PROFILE_PROPERTY_FOLDER_PREFIX + "[^/]*$");
                    Iterator aggregateFoldersIter = aggregateFolders.iterator();
                    while (aggregateFoldersIter.hasNext())
                    {
                        siblingFolders = addUniqueOrDescribedUrlNode((NodeSetImpl) siblingFolders, setProfiledNodeUrl((Node) aggregateFoldersIter.next()));
                    }
                }

                // profile root links by aggregating all links in profiled root folders
                if (! rootPathProfiled)
                {
                    searchPaths = generateProfilingPageSearchPaths("/", locator, true);
                    rootPathProfiled = findProfiledPageAndFolders(searchPaths, profiledFolder, profiledPage, profiledFolders);
                }
                if (rootPathProfiled)
                {
                    // profile root link document order folder meta data
                    List linkDocumentOrder = null;
                    foldersIter = profiledFolders.iterator();
                    while ((linkDocumentOrder == null) && foldersIter.hasNext())
                    {
                        FolderImpl profiledRootFolder = (FolderImpl) setProfiledNodeUrl((Node) foldersIter.next());
                        if (profiledRootFolder.getUrl().equals("/") && 
                            (profiledRootFolder.getMetaData() != null) && (profiledRootFolder.getMetaData().getDocumentOrder() != null) &&
                            ! profiledRootFolder.getMetaData().getDocumentOrder().isEmpty())
                        {
                            linkDocumentOrder = profiledRootFolder.getMetaData().getDocumentOrder();
                        }
                    }
                    Comparator linkDocumentComparator = new DocumentOrderComparator(linkDocumentOrder);
                    DocumentOrderFilter linkDocumentFilter = new DocumentOrderFilter(linkDocumentOrder);

                    // profile root links using profiled document order, filtering
                    // links not explicitly ordered if ordering is specified
                    rootLinks = new NodeSetImpl(null, linkDocumentComparator);
                    foldersIter = profiledFolders.iterator();
                    while (foldersIter.hasNext())
                    {
                        Folder aggregateLinksFolder = (Folder) setProfiledNodeUrl((Node) foldersIter.next());
                        if (aggregateLinksFolder.getUrl().equals("/"))
                        {
                            NodeSet aggregateLinks = aggregateLinksFolder.getLinks();
                            Iterator aggregateLinksIter = aggregateLinks.iterator();
                            while (aggregateLinksIter.hasNext())
                            {
                                Node rootLink = (Node) aggregateLinksIter.next();
                                if (! linkDocumentFilter.filter(rootLink))
                                    rootLinks = addUniqueOrDescribedUrlNode((NodeSetImpl) rootLinks, rootLink);
                            }
                        }
                    }
                }
                else
                {
                    // return empty root links
                    rootLinks = new NodeSetImpl(null);
                }
            }
            else
            {
                log.error("getProfiledPageContext(): Failed to find profiled page for " + requestPath + " at " + locator);
                throw new PageNotFoundException(requestPath + " at " + locator);
            }
        }
        else
        {
            // return request folder and page

            // managed folder and page
            try
            {
                // retrieve managed folder and page from request
                String folderPath = requestPath;
                if (folderPath.endsWith(Page.DOCUMENT_TYPE) || folderPath.endsWith("/"))
                {
                    int lastSlashIndex = folderPath.lastIndexOf('/');
                    if (lastSlashIndex > 0)
                        folderPath = folderPath.substring(0, lastSlashIndex);
                    else
                        folderPath = "/";
                }
                folder = getFolder(folderPath);
                String pagePath = requestPath;
                if (! pagePath.endsWith(Page.DOCUMENT_TYPE))
                    pagePath = folder.getDefaultPage();
                page = folder.getPage(pagePath);
            }
            catch (NodeException ne)
            {
            }
            if (page == null)
            {
                // fallback to default page for root folder
                log.warn("getProfiledPageContext(): Falling back to managed root default page for " + requestPath);
                try
                {
                    folder = getFolder("/");
                    String pagePath = folder.getDefaultPage();
                    page = folder.getPage(pagePath);
                }
                catch (NodeException ne)
                {
                }
            }

            // managed page context
            if (page != null)
            {
                siblingPages = folder.getPages();
                parentFolder = (Folder) folder.getParent();
                siblingFolders = folder.getFolders();
                try
                {
                    Folder rootFolder = getFolder("/");
                    rootLinks = rootFolder.getLinks();
                }
                catch (NodeException ne)
                {
                }
            }
            else
            {
                log.error("getProfiledPageContext(): Failed to find managed page for " + requestPath);
                throw new PageNotFoundException(requestPath);
            }
        }

        // debug profiled page context initialization parameters
        if (log.isDebugEnabled())
        {
            log.debug("getProfiledPageContext(), folder = " + folder + ", url = " + folder.getUrl());
            log.debug("getProfiledPageContext(), page = " + page + ", url = " + page.getUrl());
            if ((siblingPages != null) && (siblingPages.size() > 0))
            {
                Iterator debugIter = siblingPages.iterator();
                while (debugIter.hasNext())
                {
                    Page debug = (Page) debugIter.next();
                    log.debug("getProfiledPageContext(), siblingPage = " + debug + ", url = " + debug.getUrl());
                }
            }
            else
                log.debug("getProfiledPageContext(), siblingPages = null/empty");
            log.debug("getProfiledPageContext(), parentFolder = " + parentFolder + ", url = " + ((parentFolder != null) ? parentFolder.getUrl() : "null"));
            if ((siblingFolders != null) && (siblingFolders.size() > 0))
            {
                Iterator debugIter = siblingFolders.iterator();
                while (debugIter.hasNext())
                {
                    Folder debug = (Folder) debugIter.next();
                    log.debug("getProfiledPageContext(), siblingFolder = " + debug + ", url = " + debug.getUrl());
                }
            }
            else
                log.debug("getProfiledPageContext(), siblingFolders = null/empty");
            if ((rootLinks != null) && (rootLinks.size() > 0))
            {
                Iterator debugIter = rootLinks.iterator();
                while (debugIter.hasNext())
                {
                    Link debug = (Link) debugIter.next();
                    log.debug("getProfiledPageContext(), rootLink = " + debug + ", url = " + debug.getUrl());
                }
            }
            else
                log.debug("getProfiledPageContext(), rootLinks = null/empty");
        }

        // construct, initialize, and return new ProfiledPageContext instance
        ProfiledPageContext pageContext = locator.createProfiledPageContext();
        if (pageContext != null)
        {
            pageContext.setPage(page);
            pageContext.setFolder(folder);
            pageContext.setSiblingPages(siblingPages);
            pageContext.setParentFolder(parentFolder);
            pageContext.setSiblingFolders(siblingFolders);
            pageContext.setRootLinks(rootLinks);

            log.debug("getProfiledPageContext() returning profiled page context");

            return pageContext;
        }
        else
            log.error("getProfiledPageContext(): Failed to create profiled page context.");
        return null;
    }

    private List generateProfilingPageSearchPaths(String requestPath, ProfileLocator locator, boolean forceRequestPath)
    {
        // generate profile locator folder/page paths
        List paths = new ArrayList();
        String pagePath = requestPath;
        Iterator locatorIter = locator.iterator();
        while (locatorIter.hasNext())
        {
            // get fallback locator properties
            ProfileLocatorProperty [] locatorProperties = (ProfileLocatorProperty []) locatorIter.next();
            if (log.isDebugEnabled())
                log.debug("generateProfilingPageSearchPaths(), locatorPath = " + locator.getLocatorPath(locatorProperties));
            
            // get folder and page locator path elements
            List locatorPaths = new ArrayList();
            locatorPaths.add(new StringBuffer("/"));
            int lastLocatorPathsCount = 0;
            String lastLocatorPropertyName = null;
            int lastLocatorPropertyValueLength = 0;
            for (int i = 0; (i < locatorProperties.length); i++)
            {
                if (locatorProperties[i].isControl())
                {
                    // skip null control values
                    if (locatorProperties[i].getValue() != null)
                    {
                        // fold control names and values to lower case
                        String locatorPropertyName = locatorProperties[i].getName().toLowerCase();
                        String locatorPropertyValue = locatorProperties[i].getValue().toLowerCase();

                        // detect duplicate control names which indicates multiple
                        // values: must duplicate locator paths for each value; different
                        // control values are simply appended to all locator paths
                        if (locatorPropertyName.equals(lastLocatorPropertyName))
                        {
                            // duplicate last locator paths set, stripping last matching
                            // control value from each, appending nevw value, and adding new
                            // valued set to collection of locatorPaths
                            ArrayList multipleValueLocatorPaths = new ArrayList(lastLocatorPathsCount);
                            Iterator locatorPathsIter = locatorPaths.iterator();
                            for (int count = 0; (locatorPathsIter.hasNext() && (count < lastLocatorPathsCount)); count++)
                            {
                                StringBuffer locatorPath = (StringBuffer) locatorPathsIter.next();
                                StringBuffer multipleValueLocatorPath = new StringBuffer(locatorPath.toString());
                                multipleValueLocatorPath.setLength(multipleValueLocatorPath.length() - lastLocatorPropertyValueLength - 1);
                                multipleValueLocatorPath.append(locatorPropertyValue);
                                multipleValueLocatorPath.append('/');
                                multipleValueLocatorPaths.add(multipleValueLocatorPath);
                            }
                            locatorPaths.addAll(multipleValueLocatorPaths);
                        }
                        else
                        {
                            // construct locator path folders with control properties
                            Iterator locatorPathsIter = locatorPaths.iterator();
                            while (locatorPathsIter.hasNext())
                            {
                                StringBuffer locatorPath = (StringBuffer) locatorPathsIter.next();
                                locatorPath.append(PROFILE_PROPERTY_FOLDER_PREFIX);
                                locatorPath.append(locatorPropertyName);
                                locatorPath.append('/');
                                locatorPath.append(locatorPropertyValue);
                                locatorPath.append('/');
                            }

                            // reset last locator property vars
                            lastLocatorPathsCount = locatorPaths.size();
                            lastLocatorPropertyName = locatorPropertyName;
                            lastLocatorPropertyValueLength = locatorPropertyValue.length();
                        }
                    }
                }
                else if (! forceRequestPath)
                {
                    // set locator page path with page/path properties, assumes
                    // page names and relative paths are relative to request path
                    // and that any page paths with no url separator should have
                    // the page extension appended
                    if (locatorProperties[i].getValue() != null)
                    {
                        // get locator path property
                        pagePath = locatorProperties[i].getValue();
                        if (pagePath == null)
                            pagePath = "";
                        // append page extension if required
                        if ((pagePath.indexOf("/") == -1) && ! pagePath.endsWith(Page.DOCUMENT_TYPE))
                            pagePath = pagePath + Page.DOCUMENT_TYPE;
                        // remove default page and let folder perform defaulting
                        // if request path is probably referencing a folder, (i.e.
                        // not a page)
                        if (pagePath.equals(FolderImpl.FALLBACK_DEFAULT_PAGE))
                            pagePath = "";
                        // relative path: append to request path if page path is specified
                        // or if request path is probably referencing a folder, (i.e.
                        // not a page); the empty page path here forces a folder path
                        // to be created with a trailing slash... the folder then will
                        // choose its default page name according to its own rules.
                        if (! pagePath.startsWith("/"))
                            if ((pagePath.length() > 0) || ! requestPath.endsWith(Page.DOCUMENT_TYPE))
                            {
                                // append page path to request path
                                int lastSlashIndex = requestPath.lastIndexOf('/');
                                if (lastSlashIndex > 0)
                                    pagePath = requestPath.substring(0, lastSlashIndex) + "/" + pagePath;
                                else if (requestPath.length() > 1)
                                    pagePath = requestPath + "/" + pagePath;
                                else
                                    pagePath = "/" + pagePath;
                            }
                            else
                            {
                                // default page path to page request path
                                pagePath = requestPath;
                            }
                    }
                    else
                        pagePath = requestPath;
                }
            }
            
            // append page path to locator path folders and record
            Iterator locatorPathsIter = locatorPaths.iterator();
            while (locatorPathsIter.hasNext())
            {
                StringBuffer locatorPath = (StringBuffer) locatorPathsIter.next();
                if (pagePath != null)
                    if (pagePath.startsWith("/"))
                        locatorPath.append(pagePath.substring(1));
                    else
                        locatorPath.append(pagePath);
                paths.add(locatorPath.toString());
            }
        }

        // append default page path with no locator path
        if (pagePath != null)
            if (! pagePath.startsWith("/"))
                paths.add("/" + pagePath);
            else
                paths.add(pagePath);
        return paths;
    }

    private boolean findProfiledPageAndFolders(List pageSearchPaths, Folder [] folder, Page [] page, List folders)
    {
        folder[0] = null;
        page[0] = null;
        folders.clear();

        // iterate through search paths looking for page in page manager content
        Iterator pathsIter = pageSearchPaths.iterator();
        while (pathsIter.hasNext())
        {
            String searchRequestPath = (String) pathsIter.next();
            
            log.debug("findProfiledPageAndFolders(), searchPath = " + searchRequestPath);
            
            // search for matching folder and/or page in search path
            Folder searchFolder = null;
            Page searchPage = null;
            try
            {
                String folderPath = searchRequestPath;
                if (folderPath.endsWith(Page.DOCUMENT_TYPE) || folderPath.endsWith("/"))
                {
                    int lastSlashIndex = folderPath.lastIndexOf('/');
                    if (lastSlashIndex > 0)
                        folderPath = folderPath.substring(0, lastSlashIndex);
                    else
                        folderPath = "/";
                }
                searchFolder = getFolder(folderPath);
                String pagePath = searchRequestPath;
                if (! pagePath.endsWith(Page.DOCUMENT_TYPE))
                    pagePath = searchFolder.getDefaultPage();
                searchPage = searchFolder.getPage(pagePath);
            }
            catch (NodeException ne)
            {
            }
            if (log.isDebugEnabled())
            {
                if (searchFolder != null)
                    log.debug("findProfiledPageAndFolders(), matched searchFolder = " + searchFolder);
                if (searchPage != null)
                    log.debug("findProfiledPageAndFolders(), matched searchPage = " + searchPage);
            }
            
            // return matching page and related folders
            if ((page[0] == null) && (searchPage != null))
            {
                folder[0] = searchFolder;
                page[0] = searchPage;
                
                log.debug("findProfiledPageAndFolders(), using matched searchFolder = " + searchFolder);
                log.debug("findProfiledPageAndFolders(), using matched searchPage = " + searchPage);
            }
            if (searchFolder != null)
                folders.add(searchFolder);
        }

        return ((page[0] != null) && (folder[0] != null));
    }

    private Node setProfiledNodeUrl(Node profiledNode)
    {
        // explicitly override profiled node urls to hide real ids and paths
        // that are artifacts of profiled content in file system
        if (profiledNode instanceof AbstractNode)
        {
            AbstractNode profiledAbstractNode = (AbstractNode) profiledNode;
            if (! profiledAbstractNode.isUrlSet())
            {
                String url = stripProfiledPath(profiledAbstractNode.getUrl());
                if (url.startsWith("/") && (url.length() > 0))
                {
                    profiledAbstractNode.setUrl(url);
                    if (profiledAbstractNode.getPath().equals(profiledAbstractNode.getTitle()))
                        profiledAbstractNode.setTitle(url);
                }
            }
        }
        return profiledNode;
    }

    private String stripProfiledPath(String path)
    {
        // strip profiled property pairs folder path from profiled path
        if (path != null)
        {
            // find last property pair folders in path
            int contentPathIndex = path.lastIndexOf("/" + PROFILE_PROPERTY_FOLDER_PREFIX);
            // advance past last property pair folders to base path
            if (contentPathIndex != -1)
            {
                contentPathIndex = path.indexOf("/", contentPathIndex+1);
                if (contentPathIndex != -1)
                {
                    contentPathIndex = path.indexOf("/", contentPathIndex+1);
                    // strip property pairs from base path
                    if (contentPathIndex != -1)
                        path = path.substring(contentPathIndex);
                    else
                        path = "/";
                }
            }
        }
        return path;
    }

    private NodeSetImpl addUniqueOrDescribedUrlNode(NodeSetImpl set, Node node)
    {
        // add node to node set only if url set and unique
        // or has metadata and entry in set does not; returns
        // new set if replace required
        if (node.getUrl() == null)
            return set;
        Iterator setIter = set.iterator();
        while (setIter.hasNext())
        {
            Node setNode = (Node) setIter.next();
            if (node.getUrl().equals(setNode.getUrl()))
            {
                // replace placeholder with described node
                if ((node.getMetadata() != null) && (setNode.getMetadata() == null))
                {
                    // cannot remove from NodeSet: copy to replace setNode and return new set
                    NodeSetImpl newSet = new NodeSetImpl(null, set.getComparator());
                    Iterator copyIter = set.iterator();
                    while (copyIter.hasNext())
                    {
                        Node copyNode = (Node) copyIter.next();
                        if (copyNode != setNode)
                            newSet.add(copyNode);
                        else
                            newSet.add(node);
                    }
                    return newSet;
                }
                
                // skip duplicate node
                return set;
            }
        }

        // add unique node
        set.add(node);
        return set;
    }

    private static class DocumentOrderComparator implements Comparator
    {
        private List order;

        public DocumentOrderComparator(List documentOrderList)
        {
            this.order = documentOrderList;
        }

        public int compare(Object rootLink1, Object rootLink2)
        {
            // compare names of links against order or each other by default
            String name1 = rootLink1.toString();
            int nameIndex1 = name1.lastIndexOf('/');
            if (nameIndex1 != -1)
                name1 = name1.substring(nameIndex1 + 1);
            String name2 = rootLink2.toString();
            int nameIndex2 = name2.lastIndexOf('/');
            if (nameIndex2 != -1)
                name2 = name2.substring(nameIndex2 + 1);
            if (order != null)
            {
                // compare names against order
                int index1 = order.indexOf(name1);
                int index2 = order.indexOf(name2);
                if ((index1 != -1) || (index2 != -1))
                {
                    if ((index1 == -1) && (index2 != -1))
                        return 1;
                    if ((index1 != -1) && (index2 == -1))
                        return -1;
                    return index1-index2;
                }
            }
            // compare names against each other
            return name1.compareTo(name2);
        }
    }

    private static class DocumentOrderFilter
    {
        private List order;

        public DocumentOrderFilter(List documentOrderList)
        {
            this.order = documentOrderList;
        }

        public boolean filter(Object rootLink)
        {
            if (order != null)
            {
                // filter names of links against order
                String name = rootLink.toString();
                int nameIndex = name.lastIndexOf('/');
                if (nameIndex != -1)
                    name = name.substring(nameIndex + 1);
                if (order.indexOf(name) == -1)
                    return true;
            }
            return false;
        }
    }



    /**
     * 
     * <p>
     * getPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPage(org.apache.jetspeed.profiler.ProfileLocator)
     * @param locator
     * @return located Page instance
     * @throws PageNotFoundException
     * @throws DocmentException
     * @throws NodeException
     */
    public Page getPage( ProfileLocator locator ) throws PageNotFoundException, DocumentException, NodeException
    {
        log.debug("getPage() invoked, locator = " + locator);

        ProfiledPageContext pageContext = getProfiledPageContext(locator);
        if (pageContext != null)
            return pageContext.getPage();
        return null;
    }

    /**
     * 
     * <p>
     * getPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPage(java.lang.String)
     * @param id
     * @return @throws
     *         PageNotFoundException
     * @throws IllegalStateException
     *             if the page could be inserted into the FileCache.
     */
    public Page getPage( String id ) throws PageNotFoundException, NodeException
    {
        return (Page) addParent(handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).getDocument(id), id);
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#registerPage(org.apache.jetspeed.om.page.Page)
     */
    public void registerPage( Page page ) throws JetspeedException
    {
        // sanity checks
        if (page == null)
        {
            log.warn("Recieved null page to register");
            return;
        }

        String id = page.getId();

        if (id == null)
        {
            page.setId(generator.getNextPeid());
            id = page.getId();
            log.warn("Page with no Id, created new Id : " + id);
        }

        handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).updateDocument(page);
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage( Page page ) throws JetspeedException
    {
        registerPage(page);
    }

    /**
     * @throws UnsupportedDocumentTypeException
     * @throws FailedToDeleteDocumentException
     * @throws DocumentNotFoundException
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage( Page page ) throws DocumentNotFoundException, FailedToDeleteDocumentException,
            UnsupportedDocumentTypeException
    {
        String id = page.getId();

        if (id == null)
        {
            log.warn("Unable to remove page with null Id from disk");
            return;
        }

        handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).removeDocument(page);

    }

    /**
     * <p>
     * getLink
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getLink(java.lang.String)
     * @param name
     * @return @throws
     *         DocumentNotFoundException
     * @throws DocumentNotFoundException
     * @throws UnsupportedDocumentTypeException
     * @throws 
     * @throws NodeException
     * @throws FolderNotFoundException
     */
    public Link getLink( String name ) throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException
    {
        return (Link) addParent(handlerFactory.getDocumentHandler(Link.DOCUMENT_TYPE).getDocument(name), name);
    }

    /**
     * <p>
     * getFolder
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getFolder(java.lang.String)
     * @param folderPath
     * @return @throws
     *         DocumentException
     * @throws FolderNotFoundException
     * @throws NodeException
     * @throws InvalidFolderException
     * @throws IOException
     */
    public Folder getFolder( String folderPath ) throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        return folderHandler.getFolder(folderPath);
    }

    protected Node addParent( Node childNode, String nodePath ) throws NodeException, InvalidFolderException
    {
        int lastSlash = nodePath.indexOf("/");
        if (lastSlash > -1)
        {
            childNode.setParent(folderHandler.getFolder(nodePath.substring(0, lastSlash)));
        }
        else
        {
            childNode.setParent(folderHandler.getFolder("/"));
        }

        return childNode;

    }
}
