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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.DocumentSet;
import org.apache.jetspeed.om.folder.DocumentSetPath;
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

    private Map pageContextCache;

    private Map perl5PathRegexpCache;

    // default configuration values

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory hanlderFactory,
                                 FolderHandler folderHandler, int cacheSize,
                                 boolean profilingEnabled ) throws FileNotFoundException
    {
        super(generator);
        this.handlerFactory = hanlderFactory;
        this.folderHandler = folderHandler;
        this.profilingEnabled = profilingEnabled;
        initCaches(cacheSize);
    }

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory hanlderFactory,
                                 FolderHandler folderHandler, int cacheSize,
                                 boolean profilingEnabled, List modelClasses ) throws FileNotFoundException
    {
        super(generator, modelClasses);
        this.handlerFactory = hanlderFactory;
        this.folderHandler = folderHandler;
        this.profilingEnabled = profilingEnabled;
        initCaches(cacheSize);
    }

    private void initCaches( int cacheSize )
    {
        if (cacheSize > 0)
        {
            // use LRU maps to limit cache size
            this.pageContextCache = new LRUMap(cacheSize);
            this.perl5PathRegexpCache = new LRUMap(cacheSize*2);
        }
        else
        {
            // use unlimited cache size
            this.pageContextCache = new HashMap();
            this.perl5PathRegexpCache = new HashMap();
        }
    }

    /**
     * <p>
     * Compute profiled page context elements based on named profile
     * locators associated with a session/principal in supplied
     * context instance.
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getProfiledPageContext(org.apache.jetspeed.page.ProfiledPageContext)
     * @param pageContext
     * @throws PageNotFoundException
     * @throws DocmentException
     * @throws NodeException
     */
    public void computeProfiledPageContext(ProfiledPageContext pageContext)
        throws PageNotFoundException, DocumentException, NodeException
    {
        // construct key and test for page context cache hit
        String pageContextCacheKey = pageContextCacheKey(pageContext);
        log.debug("computeProfiledPageContext() invoked, cache key = " + pageContextCacheKey + ", (profilingEnabled = " + profilingEnabled + ")");
        ProfiledPageContext cachedPageContext = null;
        synchronized ( pageContextCache )
        {
            cachedPageContext = (ProfiledPageContext) pageContextCache.get(pageContextCacheKey);
        }
        if (cachedPageContext != null)
        {
            // copy profiled page context from cached page context and return
            copyProfiledPageContext(cachedPageContext, pageContext);
            log.debug("computeProfiledPageContext() cache hit, cache key = " + pageContextCacheKey);
            return ;
        }

        // determine profiled page context using profile locator; get
        // page profile locator from session/principal profile locators
        ProfileLocator locator = selectPageProfileLocator(pageContext.getLocators());

        // get request path
        String requestPath = locator.getRequestPath();

        // get profiled page context initialization parameters
        Folder folder = null;
        Page page = null;
        NodeSet siblingPages = null;
        Folder parentFolder = null;
        NodeSet siblingFolders = null;
        NodeSet rootLinks = null;
        NodeSet documentSets = null;
        Map documentSetNodeSets = null;
        if (profilingEnabled)
        {
            // profile page request using profile locator

            Folder [] profiledFolder = new Folder[1];
            Page [] profiledPage = new Page[1];
            List profiledFolders = new ArrayList();
            List searchProfiledFolders = new ArrayList();

            // generate profile locator folder/page search paths
            List searchPaths = generateProfilingSearchPaths(requestPath, locator, false);

            // find page in page manager content using search paths
            boolean profiled = findProfiledPageAndFolders(searchPaths, profiledFolder, profiledPage, profiledFolders, searchProfiledFolders);

            // profile fallback to default root folder to locate folder/page
            boolean rootFallback = false;
            if (rootFallback = (! profiled && ! requestPath.equals("/")))
            {
                log.warn("computeProfiledPageContext(): Falling back to profiled root default page for " + requestPath);
                searchPaths = generateProfilingSearchPaths("/", locator, true);
                profiled = findProfiledPageAndFolders(searchPaths, profiledFolder, profiledPage, profiledFolders, searchProfiledFolders);
            }

            // profiled folder and page
            if (profiled)
            {
                folder = (Folder) setProfiledNodeUrl(profiledFolder[0]);
                page = (Page) setProfiledNodeUrl(profiledPage[0]);
            }

            // profile page context
            if (page != null)
            {
                // profile general document/folder order
                List documentOrder = null;
                Iterator foldersIter = searchProfiledFolders.iterator();
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

                // profile document sets by aggregating all document set documents by document
                // set name in all profiled folders for page
                Map aggregateDocumentSets = new HashMap(12);
                foldersIter = searchProfiledFolders.iterator();
                while (foldersIter.hasNext())
                {
                    Folder aggregateFolder = (Folder) foldersIter.next();
                    NodeSet aggregateFolderDocumentSets = aggregateFolder.getDocumentSets();
                    Iterator aggregateFolderDocumentSetsIter = aggregateFolderDocumentSets.iterator();
                    while (aggregateFolderDocumentSetsIter.hasNext())
                    {
                        DocumentSet documentSet = (DocumentSet) aggregateFolderDocumentSetsIter.next();
                        if (! aggregateDocumentSets.containsKey(documentSet.getDocumentSetName()))
                        {
                            aggregateDocumentSets.put(documentSet.getDocumentSetName(), documentSet);
                        }
                    }
                }
                
                // generate profiled document sets from aggregated document set documents
                if (! aggregateDocumentSets.isEmpty())
                {
                    // profiled document sets to be returned
                    documentSets = new NodeSetImpl(null, documentComparator);
                    documentSetNodeSets = new HashMap(aggregateDocumentSets.size() * 2);
                    
                    // profile each aggregated document set
                    Iterator documentSetsIter = aggregateDocumentSets.values().iterator();
                    while (documentSetsIter.hasNext())
                    {
                        // expand and profile each document set
                        DocumentSet documentSet = (DocumentSet) documentSetsIter.next();
                        NodeSetImpl documentSetNodes = null;
                        documentSetNodes = expandAndProfileDocumentSet(pageContext.getLocators(), documentSet, documentSetNodes);
                        if ((documentSetNodes != null) && (documentSetNodes.size() > 0))
                        {
                            documentSets.add(setProfiledNodeUrl(documentSet));
                            documentSetNodeSets.put(documentSet, documentSetNodes);
                        }
                    }
                }

                // profile root links by aggregating all links in profiled root folders
                if (! rootFallback && ! requestPath.equals("/"))
                {
                    // profile root folders if required
                    searchPaths = generateProfilingSearchPaths("/", locator, true);
                    profiled = findProfiledPageAndFolders(searchPaths, profiledFolder, profiledPage, profiledFolders, searchProfiledFolders);
                }
                if (profiled)
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
                                {
                                    rootLinks = addUniqueOrDescribedUrlNode((NodeSetImpl) rootLinks, rootLink);
                                }
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
                log.error("computeProfiledPageContext(): Failed to find profiled page for " + requestPath + " at " + locator);
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
                    {
                        folderPath = folderPath.substring(0, lastSlashIndex);
                    }
                    else
                    {
                        folderPath = "/";
                    }
                }
                folder = getFolder(folderPath);
                String pagePath = requestPath;
                if (! pagePath.endsWith(Page.DOCUMENT_TYPE))
                {
                    pagePath = folder.getDefaultPage();
                }
                page = folder.getPage(pagePath);
            }
            catch (NodeException ne)
            {
            }
            if (page == null)
            {
                // fallback to default page for root folder
                log.warn("computeProfiledPageContext(): Falling back to managed root default page for " + requestPath);
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
                // return folders and pages relative to requested page
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
                try
                {
                    // get default document set order from folder
                    Comparator documentComparator = ((NodeSetImpl) folder.getAllNodes()).getComparator();

                    // aggregate and expand document sets from page to root folder;
                    documentSets = new NodeSetImpl(null, documentComparator);
                    documentSetNodeSets = new HashMap(8);
                    Set documentSetNames = new HashSet(8);
                    Folder aggregateFolder = folder;
                    do
                    {
                        // aggregate uniquely named and expand folder document sets
                        Iterator documentSetsIter = aggregateFolder.getDocumentSets().iterator();
                        while (documentSetsIter.hasNext())
                        {
                            DocumentSet documentSet = (DocumentSet) documentSetsIter.next();

                            // aggregate document sets
                            if (! documentSetNames.contains(documentSet.getDocumentSetName()))
                            {
                                documentSetNames.add(documentSet.getDocumentSetName());

                                // expand document set using default document set order
                                NodeSetImpl documentSetNodes = new NodeSetImpl(null, documentComparator);
                                documentSetNodes = expandDocumentSet(documentSet, documentSetNodes);
                                if ((documentSetNodes != null) && (documentSetNodes.size() > 0))
                                {
                                    documentSets.add(setProfiledNodeUrl(documentSet));
                                    documentSetNodeSets.put(documentSet, documentSetNodes);
                                }
                            }
                        }

                        // aggregate document sets in parent
                        aggregateFolder = (Folder) aggregateFolder.getParent();
                    }
                    while (aggregateFolder != null);
                }
                catch (NodeException ne)
                {
                }
            }
            else
            {
                log.error("computeProfiledPageContext(): Failed to find managed page for " + requestPath);
                throw new PageNotFoundException(requestPath);
            }
        }

        // populate ProfiledPageContext instance
        populateProfiledPageContext(pageContext, folder, page, siblingPages, parentFolder, siblingFolders, rootLinks, documentSets, documentSetNodeSets);

        // cache ProfiledPageContext instance
        synchronized ( pageContextCache )
        {
            pageContextCache.put(pageContextCacheKey, pageContext);
        }
        log.debug("computeProfiledPageContext() cached, cache key = " + pageContextCacheKey);
    }

    private String pageContextCacheKey(ProfiledPageContext pageContext)
    {
        // compute key from sorted profile locator strings
        StringBuffer cacheKeyBuffer = new StringBuffer();
        if (pageContext.getLocators() != null)
        {
            // get page context locators extent and sort by locator name
            List locators = new ArrayList(pageContext.getLocators().entrySet());
            Comparator locatorComparator = new Comparator()
                {
                    public int compare(Object locator1, Object locator2)
                    {
                        // compare locator names
                        return ((String) ((Map.Entry) locator1).getKey()).compareTo((String) ((Map.Entry) locator1).getKey());
                    }
                } ;
            Collections.sort(locators, locatorComparator);

            // construct key using locator names and locators
            Iterator locatorIter = locators.iterator();
            while (locatorIter.hasNext())
            {
                Map.Entry locator = (Map.Entry) locatorIter.next();
                if (cacheKeyBuffer.length() > 0)
                {
                    cacheKeyBuffer.append(',');
                }
                cacheKeyBuffer.append(locator.getKey());
                cacheKeyBuffer.append(ProfileLocator.PATH_SEPARATOR);
                cacheKeyBuffer.append(locator.getValue());
            }
        }
        return cacheKeyBuffer.toString();
    } 

    private NodeSetImpl expandAndProfileDocumentSet(Map profileLocators, DocumentSet documentSet, NodeSetImpl expandedNodes)
    {
        // expand and profile document set using document set or default
        // navigation profile locator
        ProfileLocator navigationLocator = selectNavigationProfileLocator(documentSet.getProfileLocatorName(), profileLocators);
        if (navigationLocator == null)
        {
            log.error("expandAndProfileDocumentSet(): Navigation profile locator " + documentSet.getProfileLocatorName() + " unavailable for document set " + documentSet.getPath() + ", ignored." );
            return null;
        }

        // generate search paths for profile locator from root
        List searchPaths = generateProfilingSearchPaths("/", navigationLocator, true);
        if (log.isDebugEnabled())
        {
            Iterator pathsIter = searchPaths.iterator();
            while (pathsIter.hasNext())
            {
                log.debug("expandAndProfileDocumentSet(), searchPath = " + pathsIter.next());
            }
        }

        // initialized expanded nodes collection with profiled document/folder ordering
        if (expandedNodes == null)
        {
            // get document/folder ordering
            List documentOrder = null;
            Iterator pathsIter = searchPaths.iterator();
            while ((documentOrder == null) && pathsIter.hasNext())
            {
                String folderPath = (String) pathsIter.next();
                if (folderPath.endsWith("/") && (folderPath.length() > 1))
                {
                    folderPath = folderPath.substring(0, folderPath.length()-1);
                }
                try
                {
                    FolderImpl folder = (FolderImpl) setProfiledNodeUrl((Node) getFolder(folderPath));
                    if ((folder.getMetaData() != null) && (folder.getMetaData().getDocumentOrder() != null) &&
                        ! folder.getMetaData().getDocumentOrder().isEmpty())
                    {
                        documentOrder = folder.getMetaData().getDocumentOrder();
                    }
                }
                catch (NodeException ne)
                {
                }
            }
            Comparator documentComparator = new DocumentOrderComparator(documentOrder);

            // create ordered node set
            expandedNodes = new NodeSetImpl(null, documentComparator);
        }

        // profile each document path using profile locator search paths
        Iterator documentSetPathsIter = documentSet.getDefaultedDocumentPaths().iterator();
        while (documentSetPathsIter.hasNext())
        {
            DocumentSetPath documentSetPath = (DocumentSetPath) documentSetPathsIter.next();

            // enforce assumption that document set paths are absolute
            String path = forceAbsoluteDocumentSetPath(documentSetPath.getPath());
            log.debug("expandAndProfileDocumentSet(), document set path = " + path);

            // convert regexp paths to java/perl5 form
            boolean regexp = documentSetPath.isRegexp();
            if (regexp)
            {
                path = pathToPerl5Regexp(path);
            }

            // get matching document/folder node or nodes along search paths
            // and add to expanded set if unique
            Iterator pathsIter = searchPaths.iterator();
            while (pathsIter.hasNext())
            {
                String searchPath = (String) pathsIter.next();

                // prefix document set path with search path
                if (searchPath.endsWith("/"))
                {
                    searchPath += path.substring(1);
                }
                else
                {
                    searchPath += path;
                }

                // get matching document set path nodes and add to unique
                // document set nodes
                try
                {
                    Iterator pathNodesIter = filterDocumentSet(getNodes(searchPath, regexp, null)).iterator();
                    while (pathNodesIter.hasNext())
                    {
                        expandedNodes = addUniqueOrDescribedUrlNode(expandedNodes, (Node) pathNodesIter.next());
                    }
                }
                catch (NodeException ne)
                {
                }
            }

        }
        return expandedNodes;
    }

    private List generateProfilingSearchPaths(String requestPath, ProfileLocator locator, boolean forceRequestPath)
    {
        // generate profile locator folder/page paths
        List paths = new ArrayList();
        String pagePath = requestPath;
        Iterator locatorIter = locator.iterator();
        while (locatorIter.hasNext())
        {
            // get fallback locator properties
            ProfileLocatorProperty [] locatorProperties = (ProfileLocatorProperty []) locatorIter.next();
            log.debug("generateProfilingSearchPaths(), locatorPath = " + locator.getLocatorPath(locatorProperties));
            
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
                    // set locator page path with page/path properties relative
                    // to the request path
                    if (locatorProperties[i].getValue() != null)
                    {
                        pagePath = constructRootPagePath(requestPath, locatorProperties[i].getValue());
                    }
                    else
                    {
                        pagePath = requestPath;
                    }
                }
            }

            // append page path to locator path folders and add to paths
            Iterator locatorPathsIter = locatorPaths.iterator();
            while (locatorPathsIter.hasNext())
            {
                StringBuffer locatorPath = (StringBuffer) locatorPathsIter.next();
                if (pagePath != null)
                {
                    if (pagePath.startsWith("/"))
                    {
                        locatorPath.append(pagePath.substring(1));
                    }
                    else
                    {
                        locatorPath.append(pagePath);
                    }
                }
                paths.add(locatorPath.toString());
            }
        }

        // add default page path with no locator path to paths
        if (pagePath != null)
        {
            if (! pagePath.startsWith("/"))
            {
                paths.add("/" + pagePath);
            }
            else
            {
                paths.add(pagePath);
            }
        }
        return paths;
    }

    private String constructRootPagePath(String requestPath, String pagePath)
    {
        // page names and relative paths are assumed relative to
        // request path and that any page paths with no url
        // separator should have the page extension appended

        // get default page if page path null
        if (pagePath == null)
        {
            pagePath = "";
        }

        // append page extension if required
        if ((pagePath.indexOf("/") == -1) && ! pagePath.endsWith(Page.DOCUMENT_TYPE))
        {
            pagePath = pagePath + Page.DOCUMENT_TYPE;
        }

        // remove default page and let folder perform defaulting
        // if request path is probably referencing a folder, (i.e.
        // not a page)
        if (pagePath.equals(FolderImpl.FALLBACK_DEFAULT_PAGE))
        {
            pagePath = "";
        }

        // relative path: append to request path if page path is specified
        // or if request path is probably referencing a folder, (i.e.
        // not a page); the empty page path here forces a folder path
        // to be created with a trailing slash... the folder then will
        // choose its default page name according to its own rules.
        if (! pagePath.startsWith("/"))
        {
            if ((pagePath.length() > 0) || ! requestPath.endsWith(Page.DOCUMENT_TYPE))
            {
                // append page path to request path
                int lastSlashIndex = requestPath.lastIndexOf('/');
                if (lastSlashIndex > 0)
                {
                    pagePath = requestPath.substring(0, lastSlashIndex) + "/" + pagePath;
                }
                else if (requestPath.length() > 1)
                {
                    pagePath = requestPath + "/" + pagePath;
                }
                else
                {
                    pagePath = "/" + pagePath;
                }
            }
            else
            {
                // default page path to page request path
                pagePath = requestPath;
            }
        }

        return pagePath;
    }

    private boolean findProfiledPageAndFolders(List pageSearchPaths, Folder [] folder, Page [] page, List folders, List searchFolders)
    {
        folder[0] = null;
        page[0] = null;
        folders.clear();
        searchFolders.clear();

        // iterate through search paths looking for page in page manager content
        Iterator pathsIter = pageSearchPaths.iterator();
        while (pathsIter.hasNext())
        {
            String searchRequestPath = (String) pathsIter.next();
            
            log.debug("findProfiledPageAndFolders(), searchPath = " + searchRequestPath);
            
            // search for matching folder and/or page in search path
            String folderPath = searchRequestPath;
            Folder searchFolder = null;
            Page searchPage = null;
            try
            {
                if (folderPath.endsWith(Page.DOCUMENT_TYPE) || folderPath.endsWith("/"))
                {
                    int lastSlashIndex = folderPath.lastIndexOf('/');
                    if (lastSlashIndex > 0)
                    {
                        folderPath = folderPath.substring(0, lastSlashIndex);
                    }
                    else
                    {
                        folderPath = "/";
                    }
                }
                searchFolder = getFolder(folderPath);
                String pagePath = searchRequestPath;
                if (! pagePath.endsWith(Page.DOCUMENT_TYPE))
                {
                    pagePath = searchFolder.getDefaultPage();
                }
                searchPage = searchFolder.getPage(pagePath);
            }
            catch (NodeException ne)
            {
            }
            if (searchFolder != null)
            {
                log.debug("findProfiledPageAndFolders(), matched searchFolder = " + searchFolder);
            }
            if (searchPage != null)
            {
                log.debug("findProfiledPageAndFolders(), matched searchPage = " + searchPage);
            }
            
            // return matching page and related folders
            if ((page[0] == null) && (searchPage != null))
            {
                // matched profiled folder/page
                folder[0] = searchFolder;
                page[0] = searchPage;
                
                log.debug("findProfiledPageAndFolders(), using matched searchFolder = " + searchFolder);
                log.debug("findProfiledPageAndFolders(), using matched searchPage = " + searchPage);
            }

            // return profiled folders and search profiled folders; the search
            // profiled folders are used to find other profiled documents, (i.e
            // document sets).
            if (searchFolder != null)
            {
                // profiled folder
                folders.add(searchFolder);

                // parent search profiled folders, (excluding profile property folders)
                do
                {
                    searchFolders.add(searchFolder);
                    searchFolder = (Folder) searchFolder.getParent();
                }
                while ((searchFolder != null) && ! searchFolder.getName().startsWith(PROFILE_PROPERTY_FOLDER_PREFIX));
            }
            else
            {
                // add parents of missing profiled folders to search profiled
                // folders if they exist
                String searchFolderName = null;
                do
                {
                    // find parent path or folder
                    if (searchFolder == null)
                    {
                        // get parent folder path
                        int separatorIndex = folderPath.lastIndexOf("/");
                        if (separatorIndex > 0)
                        {
                            folderPath = folderPath.substring(0, separatorIndex);
                        }
                        else
                        {
                            folderPath = "/";
                        }

                        // get folder if it exists and folder name
                        try
                        {
                            searchFolder = getFolder(folderPath);
                            searchFolderName = searchFolder.getName();
                        }
                        catch (NodeException ne)
                        {
                            separatorIndex = folderPath.lastIndexOf("/");
                            if (separatorIndex > 0)
                            {
                                searchFolderName = folderPath.substring(separatorIndex+1);
                            }
                            else
                            {
                                searchFolderName = "/";
                            }
                        }
                    }
                    else
                    {
                        // get folder as parent of search folder
                        searchFolder = (Folder) searchFolder.getParent();
                        if (searchFolder != null)
                        {
                            searchFolderName = searchFolder.getName();
                        }
                    }

                    // add to search profiled folders if it exists, (excluding
                    // profile property folders)
                    if ((searchFolder != null) && ! searchFolderName.startsWith(PROFILE_PROPERTY_FOLDER_PREFIX))
                    {
                        searchFolders.add(searchFolder);
                    }
                }
                while (! searchFolderName.equals("/") && ! searchFolderName.startsWith(PROFILE_PROPERTY_FOLDER_PREFIX));
            }
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
                    {
                        profiledAbstractNode.setTitle(url);
                    }
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
                    {
                        path = path.substring(contentPathIndex);
                    }
                    else
                    {
                        path = "/";
                    }
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
                        {
                            newSet.add(copyNode);
                        }
                        else
                        {
                            newSet.add(node);
                        }
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
            {
                name1 = name1.substring(nameIndex1 + 1);
            }
            String name2 = rootLink2.toString();
            int nameIndex2 = name2.lastIndexOf('/');
            if (nameIndex2 != -1)
            {
                name2 = name2.substring(nameIndex2 + 1);
            }
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
                {
                    name = name.substring(nameIndex + 1);
                }
                if (order.indexOf(name) == -1)
                {
                    return true;
                }
            }
            return false;
        }
    }

    private NodeSetImpl expandDocumentSet(DocumentSet documentSet, NodeSetImpl expandedNodes)
    {
        // ignore document sets with profiling locator specified
        if (documentSet.getProfileLocatorName() != null)
        {
            log.warn("expandDocumentSet(), profiling locator " + documentSet.getProfileLocatorName() + " ignored, ignoring document set path = " + documentSet.getPath());
            return null;
        }

        // expand document set against managed repository only without
        // profiling; ignores document set profiling rules as well
        if (expandedNodes == null)
        {
            expandedNodes = new NodeSetImpl(null);        
        }
        Iterator documentSetPathsIter = documentSet.getDefaultedDocumentPaths().iterator();
        while (documentSetPathsIter.hasNext())
        {
            DocumentSetPath documentSetPath = (DocumentSetPath) documentSetPathsIter.next();

            // enforce assumption that document set paths are absolute
            String path = forceAbsoluteDocumentSetPath(documentSetPath.getPath());
            log.debug("expandDocumentSet(), document set path = " + path);

            // convert regexp paths to java/perl5 form
            boolean regexp = documentSetPath.isRegexp();
            if (regexp)
            {
                path = pathToPerl5Regexp(path);
            }

            // get filtered document/folder node or nodes and add to expanded set
            try
            {
                Iterator pathNodesIter = filterDocumentSet(getNodes(path, regexp, null)).iterator();
                while (pathNodesIter.hasNext())
                    expandedNodes.add((Node) pathNodesIter.next());
            }
            catch (NodeException ne)
            {
            }
        }
        return expandedNodes;
    }

    private String forceAbsoluteDocumentSetPath(String path)
    {
        // force relative paths to be root absolute
        if (path == null)
        {
            path = "/";
        }
        else if (! path.startsWith("/"))
        {
            path = "/" + path;
        }
        return path;
    }

    private String pathToPerl5Regexp(String path)
    {
        // convert conventional path expressions to java/perl5 form and cache
        synchronized ( perl5PathRegexpCache )
        {
            String perl5Path = (String) perl5PathRegexpCache.get(path);
            if (perl5Path == null)
            {
                perl5Path = path.replaceAll("\\.", "\\\\.");
                perl5Path = perl5Path.replaceAll("\\?", ".");
                perl5Path = perl5Path.replaceAll("\\*", ".*");
                perl5PathRegexpCache.put(path, perl5Path);
            }
            return perl5Path;
        }
    }

    private NodeSet filterDocumentSet(NodeSet set)
    {
        // return empty node set
        if (set.size() == 0)
        {
            return set;
        }

        // determine if filtering required before creating new node set
        boolean filterRequired = false;
        Iterator setIter = set.iterator();
        while (!filterRequired && setIter.hasNext())
        {
            Node node = (Node) setIter.next();
            filterRequired = (! (node instanceof Page) && ! (node instanceof Folder) && ! (node instanceof Link));
        }
        if (! filterRequired)
        {
            return set;
        }

        // filter expanded document set for pages, folders, and links
        NodeSet filteredSet = new NodeSetImpl(null);        
        setIter = set.iterator();
        while (setIter.hasNext())
        {
            Node node = (Node) setIter.next();
            if ((node instanceof Page) || (node instanceof Folder) || (node instanceof Link))
            {
                filteredSet.add(node);
            }
        }
        return filteredSet;
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
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage( Page page ) throws JetspeedException
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
     * getDocumentSet
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getDocumentSet(java.lang.String)
     * @param name
     * @return @throws
     *         DocumentNotFoundException
     * @throws DocumentNotFoundException
     * @throws UnsupportedDocumentTypeException
     * @throws 
     * @throws NodeException
     * @throws FolderNotFoundException
     */
    public DocumentSet getDocumentSet( String name ) throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException
    {
        return (DocumentSet) addParent(handlerFactory.getDocumentHandler(DocumentSet.DOCUMENT_TYPE).getDocument(name), name);
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

    /**
     * <p>
     * getNodes
     * </p>
     * <p>
     * Returns a set of nodes relative to the <code>folder</code> argument of the type
     * indicated by the <code>documentType</code> argument. The <code>folder</code> argument
     * may include regular expressions if indicated by the <code>regex</code> argument. The
     * returned set is unordered.
     * </p>
     *
     * @param path Path from which to locate documents
     * @param regex Flag indicating whether regex should be expanded in path
     * @param documentType document type to filter on.
     * @return NodeSet of documents and folders located under the <code>path</code> argument.
     * @throws FolderNotFoundException if folder under the <code>path</code> does not actually exist.
     * @throws DocumentException if an error is encountered reading the folders.
     * @throws InvalidFolderException
     * @throws NodeException
     */
    public NodeSet getNodes(String path, boolean regex, String documentType) throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        return folderHandler.getNodes(path,regex,documentType);
    }
}
