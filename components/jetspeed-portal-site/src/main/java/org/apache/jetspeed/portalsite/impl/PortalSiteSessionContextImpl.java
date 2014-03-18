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
package org.apache.jetspeed.portalsite.impl;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageManagerEventListener;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.portalsite.PortalSiteContentTypeMapper;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.PortalSiteSessionContext;
import org.apache.jetspeed.portalsite.view.AbstractSiteView;
import org.apache.jetspeed.portalsite.view.PhysicalSiteView;
import org.apache.jetspeed.portalsite.view.SearchPathsSiteView;
import org.apache.jetspeed.portalsite.view.SiteViewMenuDefinitionLocator;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfileLocatorProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class encapsulates managed session state for and
 * interface to the portal-site component and subscribes
 * to page manager and session events to flush stale state.
 *
 * Note that is object is Serializable since it is designed
 * to be cached in the session. However, because this object
 * is cached only for these two reasons:
 *
 * 1. a performance optimization to reuse SiteViews, and 
 * 2. to hold optional folder page history,
 *
 * this object need not be relocatable between J2 instances.
 * Consequently, all data members are marked transient and
 * the isValid() method is used to test whether this object
 * is a valid context for the session or if it was
 * transferred from another server or the persistent session
 * store and needs to be discarded.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PortalSiteSessionContextImpl implements PortalSiteSessionContext, PageManagerEventListener, HttpSessionActivationListener, HttpSessionBindingListener, Serializable
{
    /**
     * log - logging instance
     */
    private final static Logger log = LoggerFactory.getLogger(PortalSiteSessionContextImpl.class);

    /**
     * pageManager - PageManager component
     */
    private transient PageManager pageManager;

    /**
     * contentTypeMapper - PortalSiteContentTypeMapper component
     */
    private transient PortalSiteContentTypeMapper contentTypeMapper;

    /**
     * profileLocators - map of session profile locators by locator names
     */
    private transient Map<String,ProfileLocator> profileLocators;

    /**
     * forceReservedVisible - force reserved/hidden folders visible in site view
     */
    private transient boolean forceReservedVisible;

    /**
     * userPrincipal - session user principal
     */
    private transient String userPrincipal;

    /**
     * siteView - session site view
     */
    private transient AbstractSiteView siteView;

    /**
     * folderPageHistory - map of last page visited by folder 
     */
    private transient Map<String,String> folderPageHistory;

    /**
     * menuDefinitionLocatorCache - cached menu definition locators for
     *                              absolute menus valid for session
     */
    private transient Map<SiteViewMenuDefinitionLocator,MenuImpl> menuDefinitionLocatorCache;

    /**
     * subscribed - flag that indicates whether this context
     *              is subscribed as event listeners
     */
    private transient boolean subscribed;

    /**
     * stale - flag that indicates whether the state
     *         managed by this context is stale
     */
    private transient boolean stale;

    /**
     * locatorsLastUpdateCheck - time stamp of last locators update check.
     */
    private transient long locatorsLastUpdateCheck;

    /**
     * PortalSiteSessionContextImpl - constructor
     *
     * @param pageManager PageManager component instance
     * @param contentTypeMapper PortalSiteContentTypeMapper component instance
     */
    public PortalSiteSessionContextImpl(PageManager pageManager, PortalSiteContentTypeMapper contentTypeMapper)
    {
        this.pageManager = pageManager;
        this.contentTypeMapper = contentTypeMapper;
    }

    /**
     * newRequestContext - create a new request context instance with fallback and history
     *
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @return new request context instance
     */
    public PortalSiteRequestContext newRequestContext(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal)
    {
        return new PortalSiteRequestContextImpl(this, requestProfileLocators, requestUserPrincipal, true, true, false, false);
    }

    /**
     * newRequestContext - create a new request context instance with history
     *
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @return new request context instance
     */
    public PortalSiteRequestContext newRequestContext(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback)
    {
        return new PortalSiteRequestContextImpl(this, requestProfileLocators, requestUserPrincipal, requestFallback, true, false, false);
    }

    /**
     * newRequestContext - create a new request context instance
     *
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @return new request context instance
     */
    public PortalSiteRequestContext newRequestContext(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback, boolean useHistory)
    {
        return new PortalSiteRequestContextImpl(this, requestProfileLocators, requestUserPrincipal, requestFallback, useHistory, false, false);
    }

    /**
     * newRequestContext - create a new request context instance
     *
     * @param requestProfileLocators request profile locators
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceReservedVisible force reserved/hidden folders visible in site view
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     * @return new request context instance
     */
    public PortalSiteRequestContext newRequestContext(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback, boolean useHistory, boolean forceReservedVisible, boolean forceTemplatesAccessible)
    {
        return new PortalSiteRequestContextImpl(this, requestProfileLocators, requestUserPrincipal, requestFallback, useHistory, forceReservedVisible, forceTemplatesAccessible);
    }

    /**
     * newRequestContext - create a new request context instance without profiling
     *                     support with fallback and history
     *
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @return new request context instance
     */
    public PortalSiteRequestContext newRequestContext(String requestPath, String requestServerName, String requestUserPrincipal)
    {
        return new PortalSiteRequestContextImpl(this, requestPath, requestServerName, requestUserPrincipal, true, true, false);
    }

    /**
     * newRequestContext - create a new request context instance without profiling
     *                     support with history
     *
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @return new request context instance
     */
    public PortalSiteRequestContext newRequestContext(String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback)
    {
        return new PortalSiteRequestContextImpl(this, requestPath, requestServerName, requestUserPrincipal, requestFallback, true, false);
    }

    /**
     * newRequestContext - create a new request context instance without profiling
     *                     support
     *
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @return new request context instance
     */
    public PortalSiteRequestContext newRequestContext(String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback, boolean useHistory)
    {
        return new PortalSiteRequestContextImpl(this, requestPath, requestServerName, requestUserPrincipal, requestFallback, useHistory, false);
    }

    /**
     * newRequestContext - create a new request context instance without profiling
     *                     support
     *
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     * @return new request context instance
     */
    public PortalSiteRequestContext newRequestContext(String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback, boolean useHistory, boolean forceTemplatesAccessible)
    {
        return new PortalSiteRequestContextImpl(this, requestPath, requestServerName, requestUserPrincipal, requestFallback, useHistory, forceTemplatesAccessible);
    }

    /**
     * selectRequestPageOrTemplate - select page or template view for request for
     *                               path and server
     *
     * @param requestPath request path
     * @param requestServerName request server name
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     * @param requestPageContentPath returned content path associated with selected page
     * @return selected page view for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public BaseFragmentsElement selectRequestPageOrTemplate(String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback, boolean useHistory, boolean forceTemplatesAccessible, String [] requestPageContentPath) throws NodeNotFoundException
    {
        return selectRequestPageOrTemplate(null, requestPath, requestServerName, requestUserPrincipal, requestFallback, useHistory, false, forceTemplatesAccessible, requestPageContentPath);
    }

    /**
     * selectRequestPageOrTemplate - select page or template view for request given
     *                               profile locators
     *
     * @param requestProfileLocators map of profile locators for request
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceReservedVisible force reserved/hidden folders visible for request
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     * @param requestPageContentPath returned content path associated with selected page
     * @return selected page view for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public BaseFragmentsElement selectRequestPageOrTemplate(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestFallback, boolean useHistory, boolean forceReservedVisible, boolean forceTemplatesAccessible, String [] requestPageContentPath) throws NodeNotFoundException
    {
        return selectRequestPageOrTemplate(requestProfileLocators, null, null, requestUserPrincipal, requestFallback, useHistory, forceReservedVisible, forceTemplatesAccessible, requestPageContentPath);
    }
    
    /**
     * selectRequestPageOrTemplate - select page or template view for request
     *
     * @param requestProfileLocators map of profile locators for request
     * @param requestPath request path if no locators specified
     * @param requestServerName request server name if no locators specified
     * @param requestUserPrincipal request user principal
     * @param requestFallback flag specifying whether to fallback to root folder
     *                        if locators do not select a page or access is forbidden
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceReservedVisible force reserved/hidden folders visible for request
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     * @param requestPageContentPath returned content path associated with selected page
     * @return selected page view for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    private BaseFragmentsElement selectRequestPageOrTemplate(Map<String,ProfileLocator> requestProfileLocators, String requestPath, String requestServerName, String requestUserPrincipal, boolean requestFallback, boolean useHistory, boolean forceReservedVisible, boolean forceTemplatesAccessible, String [] requestPageContentPath) throws NodeNotFoundException
    {
        // validate and update session profile locators if modified
        if (updateSessionProfileLocators(requestProfileLocators, requestUserPrincipal, forceReservedVisible))
        {
            // extract page request path and server from the locators
            // if specified, otherwise use specified parameters
            ProfileLocator locator = null;
            if (requestProfileLocators != null)
            {
                requestPath = Folder.PATH_SEPARATOR;
                requestServerName = null;
                locator = (ProfileLocator)requestProfileLocators.get(ProfileLocator.PAGE_LOCATOR);
                if (locator != null)
                {
                    // use 'page' locator to determine request page by executing
                    // profile locator to determine path
                    requestPath = getRequestPathFromLocator(locator);
                }
                else
                {
                    // 'page' locator unavailable, use first locator since
                    // all locators should have identical request paths, (do
                    // not execute profile locator though to determine path:
                    // simply use the request path)
                    locator = (ProfileLocator)requestProfileLocators.values().iterator().next();
                    requestPath = locator.getRequestPath();
                }
                requestServerName = locator.getRequestServerName();
            }
            if (log.isDebugEnabled())
            {
                log.debug("Select request page: requestPath="+requestPath+", requestServerName: "+requestServerName);
            }

            // determine content mapping for request path if content type
            // mapper configured for context
            if (contentTypeMapper != null)
            {
                // test for system page or folder request mappings; if system
                // types matched, continue below with native portal resolution
                // of request
                String systemType = contentTypeMapper.mapSystemType(requestPath);
                if ((systemType != null) && contentTypeMapper.isContentTypeFallbackEnabled())
                {
                    // test for exact system match if content fallback enabled;
                    // clear system type if not found
                    AbstractSiteView view = getSiteView();
                    if (view != null)
                    {
                        try
                        {
                            view.getNodeView(requestPath, null, null, !forceTemplatesAccessible, false, false);
                        }
                        catch (NodeNotFoundException nnfe)
                        {
                            // log fallback mapping
                            if (log.isDebugEnabled())
                            {
                                log.debug("System request: requestPath="+requestPath+" does not exist: fallback to content type");
                            }                    
                            systemType = null;
                        }
                    }
                }
                // if not system type, test for content type
                if (systemType == null)
                {
                    // test for content type mappings; if no content type matched
                    // request, continue with native portal resolution of request
                    String contentType = contentTypeMapper.mapContentType(requestPath);
                    if (contentType != null)
                    {
                        // log mapping
                        if (log.isDebugEnabled())
                        {
                            log.debug("Content request: requestPath="+requestPath+", mapped to content type: "+contentType);
                        }
                        
                        // generate external content path if mapping defined
                        String contentPageRequestPath = contentTypeMapper.mapContentRequestPath(requestServerName, contentType, requestPath);
                        if (contentPageRequestPath != null)
                        {
                            // log mapping
                            if (log.isDebugEnabled())
                            {
                                log.debug("Mapped content request to content path: serverName="+requestServerName+", contentType="+contentType+", requestPath="+requestPath+", mapped to: "+contentPageRequestPath);
                            }
                            
                            if (requestPageContentPath != null)
                            {
                                requestPageContentPath[0] = contentPageRequestPath;
                            }
                        }

                        // support request path mapping of system page requests
                        String systemPageRequestPath = contentTypeMapper.mapSystemRequestPath(requestServerName, contentType, requestPath);
                        if (systemPageRequestPath != null)
                        {
                            // verify system page existence
                            AbstractSiteView view = getSiteView();
                            if (view != null)
                            {
                                try
                                {
                                    if (view.getNodeView(systemPageRequestPath, null, null, true, false, false) instanceof Page)
                                    {
                                        systemType = PortalSiteContentTypeMapper.PAGE_SYSTEM_TYPE;
                                    }
                                }
                                catch (NodeNotFoundException nnfe)
                                {
                                }
                            }

                            // log mapping
                            if (systemType != null)
                            {
                                if (log.isDebugEnabled())
                                {
                                    log.debug("Mapped content request to existing system page: serverName="+requestServerName+", contentType="+contentType+", requestPath="+requestPath+", mapped to: "+systemPageRequestPath);
                                }
 
                                requestPath = systemPageRequestPath;
                            }
                        }

                        // if not mapped to a system type, continue with content
                        // dynamic page selection 
                        if (systemType == null)
                        {
                            // support request path mapping of dynamic page requests
                            String dynamicPageRequestPath = contentTypeMapper.mapDynamicRequestPath(requestServerName, contentType, requestPath);
                            if (dynamicPageRequestPath != null)
                            {
                                // log mapping
                                if (log.isDebugEnabled())
                                {
                                    log.debug("Mapped content request to dynamic page: serverName="+requestServerName+", contentType="+contentType+", requestPath="+requestPath+", mapped to: "+dynamicPageRequestPath);
                                }
                                
                                requestPath = dynamicPageRequestPath;
                            }

                            // attempt to match content request against dynamic pages
                            // using profile locators and site view; start at root
                            // folder until path no longer matches and search from
                            // there back up toward the root for dynamic pages by
                            // content type; fallback to wildcard content type
                            return selectContentRequestPage(requestPath, contentType);
                        }
                    }
                }
                else
                {
                    // log mapping
                    if (log.isDebugEnabled())
                    {
                        log.debug("System request: requestPath="+requestPath+", mapped to system type: "+systemType);
                    }                    
                }
            }
            
            // attempt to select request page, folder or template using
            // profile locators and site view; if fallback enabled,
            // fallback on missing node or access exceptions to the parent
            // folders until the root folder access has been attempted
            do
            {
                // attempt to access requested path
                Exception fallbackException = null;
                try
                {
                    return selectRequestPageOrTemplate(requestPath, useHistory, forceTemplatesAccessible);
                }
                catch (NodeNotFoundException nnfe)
                {
                    if (!requestFallback || requestPath.equals(Folder.PATH_SEPARATOR))
                    {
                        throw nnfe;
                    }
                    fallbackException = nnfe;
                }
                catch (SecurityException se)
                {
                    if (!requestFallback || requestPath.equals(Folder.PATH_SEPARATOR))
                    {
                        throw se;
                    }
                    fallbackException = se;
                }

                // compute fallback request path
                if (requestFallback && !requestPath.equals(Folder.PATH_SEPARATOR))
                {
                    // compute parent folder fallback request path
                    String fallbackRequestPath = requestPath;
                    while (fallbackRequestPath.endsWith(Folder.PATH_SEPARATOR))
                    {
                        fallbackRequestPath = fallbackRequestPath.substring(0, fallbackRequestPath.length()-1);
                    }
                    int folderIndex = fallbackRequestPath.lastIndexOf(Folder.PATH_SEPARATOR);
                    if (folderIndex >= 2)
                    {
                        // fallback to parent folder
                        fallbackRequestPath = fallbackRequestPath.substring(0, folderIndex);
                    }
                    else
                    {
                        // fallback to root folder
                        fallbackRequestPath = Folder.PATH_SEPARATOR;
                    }

                    // check fallback path and log fallback operation
                    if (!fallbackRequestPath.equals(requestPath))
                    {
                        // log fallback
                        if (log.isDebugEnabled())
                        {
                            log.debug("Missing/forbidden page selection fallback: request path=" + requestPath + ", attempting fallback request path=" + fallbackRequestPath, fallbackException);
                        }
                        
                        // clear all history entries for fallback
                        // request path in advance to make fallback
                        // page selection more predictable
                        Iterator folderPathIter = getFolderPageHistory().keySet().iterator();
                        while (folderPathIter.hasNext())
                        {
                            String folderPath = (String)folderPathIter.next();
                            if (folderPath.equals(fallbackRequestPath))
                            {
                                folderPathIter.remove();
                                break;
                            }
                        }

                        // retry requested page access
                        requestPath = fallbackRequestPath;
                    }
                }
                else
                {
                    // fallback attempts complete: no page found for user
                    break;
                }
            }
            while (true);
        }

        // no request page available
        throw new NodeNotFoundException("No request page available in site view.");
    }

    /**
     * getRequestPathFromLocator - execute profile locator to extract
     *                             request path using locator rules; this
     *                             is request specific and is not part of
     *                             the site view
     *
     * @param locator profile locator to execute
     * @return request path from profile locator
     */
    private String getRequestPathFromLocator(ProfileLocator locator)
    {
        // use profile iterator to process the initial full
        // set of profile locator properties searching for
        // the first non control/navigation, (i.e. page/path),
        // property that will force the request path if
        // non-null; otherwise default to locator request path
        String requestPath = locator.getRequestPath();
        Iterator<ProfileLocatorProperty []> locatorIter = locator.iterator();
        if (locatorIter.hasNext())
        {
            ProfileLocatorProperty [] properties = locatorIter.next();
            for (int i = 0; (i < properties.length); i++)
            {
                if (!properties[i].isControl() && !properties[i].isNavigation())
                {
                    // request page/path property; append to or replace
                    // using locator specified path
                    String path = properties[i].getValue();
                    if (path != null)
                    {
                        // specified page/path to be appended to request path if
                        // relative; otherwise specified page/path to replace
                        // request path
                        if (!path.startsWith(Folder.PATH_SEPARATOR))
                        {
                            // strip page from request path if required
                            // and append page/path to base request path
                            String basePath = requestPath;
                            if (basePath == null)
                            {
                                basePath = Folder.PATH_SEPARATOR;
                            }
                            else if (basePath.endsWith(Page.DOCUMENT_TYPE))
                            {
                                basePath = basePath.substring(0, basePath.lastIndexOf(Folder.PATH_SEPARATOR)+1);
                            }
                            else if (!basePath.endsWith(Folder.PATH_SEPARATOR))
                            {
                                basePath += Folder.PATH_SEPARATOR;
                            }
                            path = basePath + path;

                            // make sure path ends in page extension
                            // if folder not explicitly specified
                            if (!path.endsWith(Folder.PATH_SEPARATOR) && !path.endsWith(Page.DOCUMENT_TYPE))
                            {
                                path += Page.DOCUMENT_TYPE;
                            }
                        }

                        // detect profile locator request path modification
                        if (!path.equals(requestPath))
                        {
                            // if modified request path ends with default page,
                            // strip default page from path to allow folder level
                            // defaulting to take place: locator should not force
                            // selection of default page when selection of the
                            // folder is implied by use in locator page/path
                            if (path.endsWith(Folder.PATH_SEPARATOR + Folder.FALLBACK_DEFAULT_PAGE))
                            {
                                path = path.substring(0, path.length() - Folder.FALLBACK_DEFAULT_PAGE.length());
                            }
                            
                            // log modified page request
                            if (log.isDebugEnabled() && !path.equals(requestPath))
                            {
                                log.debug("Request path modified by profile locator: request path=" + path + ", original request path=" + requestPath);
                            }
                            return path;
                        }
                    }
                }
            }
        }

        // return locator request path
        return requestPath;
    }

    /**
     * selectRequestPageOrTemplate - select page or template view for request for
     *                               specified path against site view associated
     *                               with this context
     *
     * @param requestPath request path
     * @param useHistory flag indicating whether to use visited page
     *                   history to select default page per site folder
     * @param forceTemplatesAccessible force templates accessible to requests in site view
     * @return selected page or template view for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    private BaseFragmentsElement selectRequestPageOrTemplate(String requestPath, boolean useHistory, boolean forceTemplatesAccessible) throws NodeNotFoundException
    {
        // save access exceptions
        SecurityException accessException = null;

        // valid SiteView required from session profile locators
        AbstractSiteView view = getSiteView();
        if (view != null)
        {
            // default request to root folder if not specified
            if (requestPath == null)
            {
                requestPath = Folder.PATH_SEPARATOR;
            }
            
            // log page request
            if (log.isDebugEnabled())
            {
                log.debug("Request page: request path=" + requestPath);
            }

            // lookup request path in view for viewable page or folder
            // nodes; note: directly requested pages/folders may be hidden
            // or not viewable
            Node requestNode = null;
            try
            {
                // try page or folder request url
                requestNode = view.getNodeView(requestPath, null, null, !forceTemplatesAccessible, false, false);
            }
            catch (NodeNotFoundException nnfe)
            {
                // if request path ends with default page, strip from
                // request url to retry for folder default
                if (requestPath.endsWith(Folder.PATH_SEPARATOR + Folder.FALLBACK_DEFAULT_PAGE))
                {
                    // retry folder request url
                    requestPath = requestPath.substring(0, requestPath.length() - Folder.FALLBACK_DEFAULT_PAGE.length());
                    requestNode = view.getNodeView(requestPath, null, null, !forceTemplatesAccessible, true, false);
                }
                else
                {
                    // rethrow original exception
                    throw nnfe;
                }
            }
            
            // invoke default page logic to determine folder page
            if (requestNode instanceof Folder)
            {
                Folder requestFolder = (Folder)requestNode;
                
                // support subfolders specified as default pages;
                // find highest subfolder with a default page that
                // specifies a default folder, (not a default page).
                try
                {
                    String defaultFolderName = requestFolder.getDefaultPage();
                    if (defaultFolderName != null)
                    {
                        // do not follow broken default folders
                        Folder defaultRequestFolder = requestFolder;
                        // follow default folders to parent folders
                        while ((defaultRequestFolder != null) && (defaultFolderName != null) &&
                               defaultFolderName.equals(".."))
                        {
                            defaultRequestFolder = (Folder)defaultRequestFolder.getParent();
                            if (defaultRequestFolder != null)
                            {
                                defaultFolderName = defaultRequestFolder.getDefaultPage();
                            }
                            else
                            {
                                defaultFolderName = null;
                            }
                        }
                        // follow default folders to subfolders
                        while ((defaultRequestFolder != null) && (defaultFolderName != null) &&
                               !defaultFolderName.endsWith(Page.DOCUMENT_TYPE) && !defaultFolderName.equals(".."))
                        {
                            defaultRequestFolder = defaultRequestFolder.getFolder(defaultFolderName);
                            defaultFolderName = defaultRequestFolder.getDefaultPage();
                        }
                        // use default request folder
                        if (defaultRequestFolder != null)
                        {
                            requestFolder = defaultRequestFolder;
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
                    requestFolder = null;
                    accessException = se;
                }

                // only request folders with pages can be
                // selected by request; otherwise, fall back to
                // parent folders assuming that immediate parents
                // will have the most appropriate default page
                NodeSet requestFolderPages = null;
                if (requestFolder != null)
                {
                    try
                    {
                        requestFolderPages = requestFolder.getPages();
                        while (((requestFolderPages == null) || requestFolderPages.isEmpty()) && (requestFolder.getParent() != null))
                        {
                            requestFolder = (Folder)requestFolder.getParent();
                            requestFolderPages = requestFolder.getPages();
                        }
                    }
                    catch (NodeException ne)
                    {
                        requestFolderPages = null;
                    }
                    catch (SecurityException se)
                    {
                        requestFolderPages = null;
                        accessException = se;
                    }
                }
                if ((requestFolder != null) && (requestFolderPages != null) && !requestFolderPages.isEmpty())
                {
                    Page requestPage = null;

                    // attempt to lookup last visited page by folder path;
                    // page id test must be performed since identical paths
                    // may occur in multiple site views
                    if (useHistory)
                    {
                        String requestPageId = (String)getFolderPageHistory().get(requestFolder.getPath());
                        if (requestPageId != null)
                        {
                            // find page by id in request folder pages
                            for (Node requestFolderPageNode : requestFolderPages)
                            {
                                Page requestFolderPage = (Page)requestFolderPageNode;
                                if (requestPageId.equals(requestFolderPage.getId()))
                                {
                                    requestPage = requestFolderPage;
                                    break;
                                }
                            }
                            
                            // log selected request page
                            if (requestPage != null)
                            {
                                if (log.isDebugEnabled())
                                {
                                    log.debug("Selected folder historical page: path=" + view.getManagedPage(requestPage).getPath());
                                }
                                return requestPage;
                            }
                        }
                    }
                    
                    // get default page for folder view if more than one
                    // page is available to choose from
                    if (requestFolderPages.size() > 1)
                    {
                        String defaultPageName = requestFolder.getDefaultPage();
                        if (defaultPageName == null)
                        {
                            // use fallback default if default page
                            // not explicitly specified
                            defaultPageName = Folder.FALLBACK_DEFAULT_PAGE;
                        }
                        try
                        {
                            // save last visited non-hidden page for folder path
                            // and return default page
                            requestPage = requestFolder.getPage(defaultPageName);
                            if (!requestPage.isHidden())
                            {
                                getFolderPageHistory().put(requestFolder.getPath(), requestPage.getId());
                            }
                            
                            // log selected request page
                            if (log.isDebugEnabled())
                            {
                                log.debug("Selected folder default page: path=" + view.getManagedPage(requestPage).getPath());
                            }
                            return requestPage;
                        }
                        catch (NodeException ne)
                        {
                        }
                        catch (NodeNotFoundException nnfe)
                        {
                        }
                        catch (SecurityException se)
                        {
                            accessException = se;
                        }
                    }
                    
                    // default page not available, select first page
                    // view in request folder; save last visited
                    // non-hidden page for folder path and return default page
                    requestPage = (Page)requestFolderPages.iterator().next();
                    if (!requestPage.isHidden())
                    {
                        getFolderPageHistory().put(requestFolder.getPath(), requestPage.getId());
                    }

                    // log selected request page
                    if (log.isDebugEnabled())
                    {
                        log.debug("Selected first folder page, path=" + view.getManagedPage(requestPage).getPath());
                    }
                    return requestPage;
                }
            }
            else if (requestNode instanceof Page)
            {
                Page requestPage = (Page)requestNode;
                
                // save last visited non-hidden page for folder path
                // and return matched page
                Folder requestFolder = (Folder)requestPage.getParent();
                if (!requestPage.isHidden())
                {
                	getFolderPageHistory().put(requestFolder.getPath(), requestPage.getId());
                }

                // log selected request page
                if (log.isDebugEnabled())
                {
                    log.debug("Selected page, path=" + view.getManagedPage(requestPage).getPath());
                }
                return requestPage;
            }
            else if (forceTemplatesAccessible)
            {
                if (requestNode instanceof PageTemplate)
                {
                    PageTemplate requestPageTemplate = (PageTemplate)requestNode;
                    
                    // log selected request page template
                    if (log.isDebugEnabled())
                    {
                        log.debug("Selected page template, path=" + view.getManagedPageTemplate(requestPageTemplate).getPath());
                    }
                    return requestPageTemplate;
                }
                else if (requestNode instanceof DynamicPage)
                {
                    DynamicPage requestDynamicPage = (DynamicPage)requestNode;
                    
                    // log selected request dynamic page
                    if (log.isDebugEnabled())
                    {
                        log.debug("Selected dynamic page, path=" + view.getManagedDynamicPage(requestDynamicPage).getPath());
                    }
                    return requestDynamicPage;
                }
                else if (requestNode instanceof FragmentDefinition)
                {
                    FragmentDefinition requestFragmentDefinition = (FragmentDefinition)requestNode;
                    
                    // log selected request fragment definition
                    if (log.isDebugEnabled())
                    {
                        log.debug("Selected fragment definition, path=" + view.getManagedFragmentDefinition(requestFragmentDefinition).getPath());
                    }
                    return requestFragmentDefinition;
                }
            }
        }
            
        // no page matched or accessible
        if (accessException != null)
        {
            throw accessException;
        }
        throw new NodeNotFoundException("No page matched " + requestPath + " request in site view.");
    }
    
    /**
     * selectContentRequestPage - select dynamic page view for request for
     *                            specified content request path and content
     *                            type given profile locators and site view
     *                            associated with this context
     *
     * @param requestPath request path
     * @param contentType content type
     * @return selected dynamic page view for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    private DynamicPage selectContentRequestPage(String requestPath, String contentType) throws NodeNotFoundException
    {
        // save access exceptions
        SecurityException accessException = null;

        // valid SiteView required from session profile locators
        AbstractSiteView view = getSiteView();
        if (view != null)
        {
            // default request to root folder if not specified
            if (requestPath == null)
            {
                requestPath = Folder.PATH_SEPARATOR;
            }
            
            // log content page request
            if (log.isDebugEnabled())
            {
                log.debug("Request content page: request path=" + requestPath);
            }
            
            // search for deepest matching content request path
            // that matches request path; start with root folder in view
            Folder contentRequestFolder = (Folder)view.getNodeView(Folder.PATH_SEPARATOR, null, null, true, false, false);
            String contentRequestPath = contentRequestFolder.getPath();
            for (;;)
            {
                // find next path name
                int startOfPathNameIndex = (contentRequestPath.equals(Folder.PATH_SEPARATOR) ? 1 : contentRequestPath.length()+1);
                int endOfPathNameIndex = requestPath.indexOf(Folder.PATH_SEPARATOR, startOfPathNameIndex);
                if ((endOfPathNameIndex == -1) || (endOfPathNameIndex == startOfPathNameIndex))
                {
                    break;
                }
                // find folder in view
                try
                {
                    String pathFolderName = requestPath.substring(startOfPathNameIndex, endOfPathNameIndex);
                    contentRequestFolder = contentRequestFolder.getFolder(pathFolderName);
                    contentRequestPath = contentRequestFolder.getPath();
                }
                catch (Exception e)
                {
                    break;
                }
            }
            
            // determine if content request folder matches request
            // folder assuming request path may include a content
            // document name, but no deeper folders
            boolean matchingContentRequestFolder = true;
            if (contentRequestPath.length() < requestPath.length())
            {
                String requestPathTail = requestPath.substring(contentRequestPath.length());
                matchingContentRequestFolder = (requestPathTail.indexOf(Folder.PATH_SEPARATOR_CHAR, (requestPathTail.startsWith(Folder.PATH_SEPARATOR) ? 1 : 0)) == -1);
            }
            
            // select matching dynamic pages in folders from deepest
            // to root folders in content request path
            while (contentRequestFolder != null)
            {
                // select dynamic page by content type or wildcard match
                try
                {
                    NodeSet dynamicPages = contentRequestFolder.getDynamicPages();
                    if ((dynamicPages != null) && !dynamicPages.isEmpty())
                    {
                        // select matching page
                        DynamicPage matchingPage = null;
                        DynamicPage inheritableMatchingPage = null;
                        DynamicPage wildcardMatchingPage = null;
                        for (Node dynamicPageNode : dynamicPages)
                        {
                            DynamicPage dynamicPage = (DynamicPage)dynamicPageNode;
                            if ((dynamicPage.getContentType() == null) || dynamicPage.getContentType().equals(DynamicPage.WILDCARD_CONTENT_TYPE))
                            {
                                wildcardMatchingPage = dynamicPage;
                            }
                            else if (dynamicPage.getContentType().equals(contentType))
                            {
                                boolean inheritableDynamicPage = dynamicPage.isInheritable();
                                if (matchingContentRequestFolder && !inheritableDynamicPage)
                                {
                                    matchingPage = dynamicPage;
                                }
                                else if (inheritableDynamicPage)
                                {
                                    inheritableMatchingPage = dynamicPage;
                                }
                            }
                        }
                        // select matching page
                        if (matchingPage != null)
                        {
                            // log selected dynamic page
                            if (log.isDebugEnabled())
                            {
                                log.debug("Selected "+contentType+" content dynamic page, path=" + view.getManagedDynamicPage(matchingPage).getPath());
                            }
                            return matchingPage;
                        }
                        // select inheritable matching page
                        if (inheritableMatchingPage != null)
                        {
                            // log selected dynamic page
                            if (log.isDebugEnabled())
                            {
                                log.debug("Selected "+contentType+" inheritable content dynamic page, path=" + view.getManagedDynamicPage(inheritableMatchingPage).getPath());
                            }
                            return inheritableMatchingPage;
                        }
                        // select wildcard matching page
                        if (wildcardMatchingPage != null)
                        {
                            // log selected dynamic page
                            if (log.isDebugEnabled())
                            {
                                log.debug("Selected "+contentType+" wildcard content dynamic page, path=" + view.getManagedDynamicPage(wildcardMatchingPage).getPath());
                            }
                            return wildcardMatchingPage;
                        }
                    }
                }
                catch (NodeException ne)
                {
                    break;
                }
                catch (SecurityException se)
                {
                    accessException = se;
                }

                // continue search with parent folder
                contentRequestFolder = (Folder)contentRequestFolder.getParent();
                matchingContentRequestFolder = false;
            }
        }
            
        // no dynamic page matched or accessible
        if (accessException != null)
        {
            throw accessException;
        }
        throw new NodeNotFoundException("No dynamic page matched " + requestPath + " request in site view.");
    }

    /**
     * getRequestRootFolder - select root folder view for given profile locators
     *
     * @param requestUserPrincipal request user principal
     * @return root folder view for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public Folder getRequestRootFolder(String requestUserPrincipal) throws NodeNotFoundException
    {
        return getRequestRootFolder(null, requestUserPrincipal, false);
    }

    /**
     * getRequestRootFolder - select root folder view for given profile locators
     *
     * @param requestProfileLocators map of profile locators for request
     * @param requestUserPrincipal request user principal
     * @param requestForceReservedVisible force reserved/hidden folders visible for request
     * @return root folder view for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public Folder getRequestRootFolder(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestForceReservedVisible) throws NodeNotFoundException
    {
        // validate and update session profile locators if modified
        if (updateSessionProfileLocators(requestProfileLocators, requestUserPrincipal, requestForceReservedVisible))
        {
            // valid site view required from session profile locators
            AbstractSiteView view = getSiteView();
            if (view != null)
            {
                // return root folder view from session site view
                return view.getRootFolderView();
            }
        }

        // no root folder available
        throw new NodeNotFoundException("No root folder available in site view.");
    }

    /**
     * updateSessionProfileLocators - detect modification of and update cached
     *                                session profile locators
     *
     * @param requestProfileLocators map of profile locators for request
     * @param requestUserPrincipal request user principal
     * @param requestForceReservedVisible force reserved/hidden folders visible for request
     * @return profile locators validation flag
     */
    private boolean updateSessionProfileLocators(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestForceReservedVisible)
    {
        // valid request profile locators are required
        if ((requestProfileLocators == null) || !requestProfileLocators.isEmpty())
        {
            // detect stale session, modification of user
            // principal, or changed profile locators for
            // this session context
            boolean userUpdate = false;
            boolean locatorsUpdate = false;
            boolean forceReservedVisibleUpdate = false;
            boolean updated = false;
            synchronized (this)
            {
                userUpdate = (((userPrincipal == null) && (requestUserPrincipal != null)) ||
                              ((userPrincipal != null) && !userPrincipal.equals(requestUserPrincipal)));
                locatorsUpdate = !locatorsEquals(profileLocators, requestProfileLocators);
                forceReservedVisibleUpdate = (forceReservedVisible != requestForceReservedVisible);
                if (stale || userUpdate || locatorsUpdate || forceReservedVisibleUpdate)
                {
                    // reset cached session profile locators, view,
                    // folder page history, menu definition locators,
                    // and stale flag
                    clearSessionProfileLocators();
                    profileLocators = requestProfileLocators;
                    forceReservedVisible = requestForceReservedVisible;
                    userPrincipal = requestUserPrincipal;
                    updated = true;
                }
                locatorsLastUpdateCheck = System.currentTimeMillis();
            }

            // log session context setup and update
            if (updated && log.isDebugEnabled())
            {
                StringBuffer debug = new StringBuffer();
                if (userUpdate)
                {
                    debug.append("Updated user");
                    if (locatorsUpdate)
                    {
                        debug.append("/locators");
                    }
                    if (forceReservedVisibleUpdate)
                    {
                        debug.append("/force reserved visible");
                    }
                    if (stale)
                    {
                        debug.append("/stale");
                    }
                }
                else if (locatorsUpdate)
                {
                    debug.append("Updated locators");
                    if (forceReservedVisibleUpdate)
                    {
                        debug.append("/force reserved visible");
                    }
                    if (stale)
                    {
                        debug.append("/stale");
                    }
                }
                else if (forceReservedVisibleUpdate)
                {
                    debug.append("Updated force reserved visible");
                    if (stale)
                    {
                        debug.append("/stale");
                    }
                }
                else
                {
                    debug.append("Updated stale");
                }
                debug.append(" context: user=" + requestUserPrincipal + ", profileLocators=(");
                if (requestProfileLocators != null)
                {
                    boolean firstEntry = true;
                    for (Map.Entry<String,ProfileLocator> entry : requestProfileLocators.entrySet())
                    {
                        String locatorName = entry.getKey();
                        ProfileLocator locator = entry.getValue();
                        if (!firstEntry)
                        {
                            debug.append(",");
                        }
                        else
                        {
                            firstEntry = false;
                        }
                        debug.append(locatorName);
                        debug.append("=");
                        debug.append(locator.toString());
                    }
                }
                else
                {
                    debug.append("null");
                }
                debug.append(")");
                log.debug(debug.toString());
            }

            // return valid
            return true;
        }

        // return invalid
        return false;
    }

    /**
     * clearSessionProfileLocators - clear cache session profile locators
     */
    private synchronized void clearSessionProfileLocators()
    {
        // clear cached session profile locators, view,
        // folder page history, menu definition locators,
        // and stale flag
        profileLocators = null;
        userPrincipal = null;
        siteView = null;
        folderPageHistory = null;
        if (menuDefinitionLocatorCache != null)
        {
            menuDefinitionLocatorCache.clear();
        }
        stale = false;
    }

    /**
     * getSiteView - lookup and/or create site view for
     *               profile locators of this context
     *
     * @return site view instance
     */
    public AbstractSiteView getSiteView()
    {
        // get or create site view
        AbstractSiteView view = siteView;
        while (view == null)
        {
            // access site view and test for creation
            Map<String,ProfileLocator> createViewProfileLocators = null;
            String createViewUserPrincipal = null;
            boolean createView = false;
            synchronized (this)
            {
                view = siteView;
                createView = ((view == null) && (pageManager != null));
                createViewProfileLocators = profileLocators;
                createViewUserPrincipal = userPrincipal;
            }

            // create new site view if necessary
            boolean viewCreated = false;
            if (createView)
            {
                // create site view outside of synchronized state; this is
                // required since construction of site view requires access
                // to the page manager and page manager event notifications
                // may arrive during construction of the site view which
                // might then result in synchronized deadlock with page
                // manager or page manager cache internals
                if (createViewProfileLocators != null)
                {
                    view = new SearchPathsSiteView(pageManager, createViewProfileLocators, forceReservedVisible);
                }
                else
                {
                    view = new PhysicalSiteView(pageManager, userPrincipal);                    
                }

                // update site view if not already made available by another
                // request thread
                synchronized (this)
                {
                    if ((siteView == null) && (pageManager != null) && (profileLocators == createViewProfileLocators) && (userPrincipal == createViewUserPrincipal))
                    {
                        siteView = view;
                        viewCreated = true;
                    }
                    view = siteView;
                }
            }
        
            // log site view creation
            if (viewCreated && log.isDebugEnabled())
            {
                if (view instanceof SearchPathsSiteView)
                {
                    log.debug("Created search path site view: search paths=" + ((SearchPathsSiteView)view).getSearchPathsString());
                }
                else if (view instanceof PhysicalSiteView)
                {
                    log.debug("Created physical site view");
                }
            }
        }
        
        return view;
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
     * getContentTypeMapper - return PortalSiteContentTypeMapper component instance
     *
     * @return PortalSiteContentTypeMapper instance
     */
    public PortalSiteContentTypeMapper getContentTypeMapper()
    {
        return contentTypeMapper;
    }
    
    /**
     * isValid - return flag indicating whether this context instance
     *           is valid or if it is stale after being persisted and
     *           reloaded as session state
     *
     * @return valid context status
     */
    public boolean isValid()
    {
        // existent transient page manager implies valid context 
        return (pageManager != null);
    }

    /**
     * getProfileLocators - get session profile locators
     */
    public Map<String,ProfileLocator> getProfileLocators()
    {
        return profileLocators;
    }

    /**
     * getStandardMenuNames - get set of available standard menu names
     *  
     * @return menu names set
     */
    public Set<String> getStandardMenuNames()
    {
        // return standard menu names defined for site view
        AbstractSiteView view = getSiteView();
        return ((view != null) ? view.getStandardMenuNames() : null);            
    }

    /**
     * getMenuDefinitionLocators - get list of node view menu definition
     *                             locators from site view
     *
     * @param node site view node view
     * @return definition locator list
     */
    public List<SiteViewMenuDefinitionLocator> getMenuDefinitionLocators(Node node)
    {
        // return menu definition locators for node in site view
        AbstractSiteView view = getSiteView();
        return ((view != null) ? view.getMenuDefinitionLocators(node) : null);            
    }

    /**
     * getMenuDefinitionLocator - get named node view menu definition
     *                            locator from site view
     *
     * @param node site view node view
     * @param name menu definition name
     * @return menu definition locator
     */
    public SiteViewMenuDefinitionLocator getMenuDefinitionLocator(Node node, String name)
    {
        // return named menu definition locator for node in site view
        AbstractSiteView view = getSiteView();
        return ((view != null) ? view.getMenuDefinitionLocator(node, name) : null);            
    }

    /**
     * getManagedPageOrTemplate - get managed page, page template, dynamic page, or
     *                            fragment definition instance from page view
     *  
     * @param pageOrTemplate page view
     * @return managed page
     */
    public BaseFragmentsElement getManagedPageOrTemplate(BaseFragmentsElement pageOrTemplate)
    {
        // return managed page or template in site view
        AbstractSiteView view = getSiteView();
        if (pageOrTemplate instanceof Page)
        {
            return ((view != null) ? view.getManagedPage((Page)pageOrTemplate) : null);
        }
        else if (pageOrTemplate instanceof PageTemplate)
        {
            return ((view != null) ? view.getManagedPageTemplate((PageTemplate)pageOrTemplate) : null);
        }
        else if (pageOrTemplate instanceof DynamicPage)
        {
            return ((view != null) ? view.getManagedDynamicPage((DynamicPage)pageOrTemplate) : null);
        }
        else if (pageOrTemplate instanceof FragmentDefinition)
        {
            return ((view != null) ? view.getManagedFragmentDefinition((FragmentDefinition)pageOrTemplate) : null);
        }
        return null;
    }

    /**
     * getManagedPageTemplate - get concrete page template instance from page
     *                          template view
     *  
     * @param pageTemplate page template view
     * @return managed page template
     */
    public PageTemplate getManagedPageTemplate(PageTemplate pageTemplate)
    {
        // return managed page template in site view
        AbstractSiteView view = getSiteView();
        return ((view != null) ? view.getManagedPageTemplate(pageTemplate) : null);            
    }

    /**
     * getManagedDynamicPage - get concrete dynamic page instance from dynamic
     *                         page view
     *  
     * @param dynamicPage dynamic page view
     * @return managed dynamic page
     */
    public DynamicPage getManagedDynamicPage(DynamicPage dynamicPage)
    {
        // return managed dynamic page in site view
        AbstractSiteView view = getSiteView();
        return ((view != null) ? view.getManagedDynamicPage(dynamicPage) : null);            
    }

    /**
     * getManagedFragmentDefinition - get concrete dynamic page instance from
     *                                fragment definition view
     *  
     * @param fragmentDefinition fragment definition view
     * @return managed fragment definition
     */
    public FragmentDefinition getManagedFragmentDefinition(FragmentDefinition fragmentDefinition)
    {
        // return managed fragment definition in site view
        AbstractSiteView view = getSiteView();
        return ((view != null) ? view.getManagedFragmentDefinition(fragmentDefinition) : null);            
    }

    /**
     * getUserFolderPath - return primary concrete root user folder path
     *
     * @param requestProfileLocators map of profile locators for request
     * @param requestUserPrincipal request user principal
     * @param requestForceReservedVisible force reserved/hidden folders visible for request
     * @return user folder path or null
     */
    public String getUserFolderPath(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestForceReservedVisible)
    {
        // validate and update session profile locators if modified
        if (updateSessionProfileLocators(requestProfileLocators, requestUserPrincipal, forceReservedVisible))
        {
            // return user folder path in site view
            AbstractSiteView view = getSiteView();
            return ((view != null) ? view.getUserFolderPath() : null);
        }
        return null;
    }

    /**
     * getBaseFolderPath - return primary concrete root base folder path
     *
     * @param requestProfileLocators map of profile locators for request
     * @param requestUserPrincipal request user principal
     * @param requestForceReservedVisible force reserved/hidden folders visible for request
     * @return base folder path or null
     */
    public String getBaseFolderPath(Map<String,ProfileLocator> requestProfileLocators, String requestUserPrincipal, boolean requestForceReservedVisible)
    {
        // validate and update session profile locators if modified
        if (updateSessionProfileLocators(requestProfileLocators, requestUserPrincipal, forceReservedVisible))
        {
            // return base folder path in site view
            AbstractSiteView view = getSiteView();
            return ((view != null) ? view.getBaseFolderPath() : null);
        }
        return null;
    }

    /**
     * getMenuDefinitionLocatorCache - get menu definition locators cache
     *                                 for absolute menus
     *
     * @return menu definition locators cache
     */
    public Map<SiteViewMenuDefinitionLocator,MenuImpl> getMenuDefinitionLocatorCache()
    {
        return menuDefinitionLocatorCache;
    }

    /**
     * setMenuDefinitionLocatorCache - set menu definition locators cache
     *                                 for absolute menus
     *
     * @return menu definition locators cache
     */
    public void setMenuDefinitionLocatorCache(Map<SiteViewMenuDefinitionLocator,MenuImpl> cache)
    {
        menuDefinitionLocatorCache = cache;
    }
    
    /**
     * locatorsEquals - test profile locator maps for equivalence
     *                  ignoring request specifics
     *
     * @param locators0 request profile locator map
     * @param locators1 request profile locator map
     * @return boolean flag indicating equivalence
     */
    private static boolean locatorsEquals(Map<String,ProfileLocator> locators0, Map<String,ProfileLocator> locators1)
    {
        // trivial comparisons
        if (locators0 == locators1)
        {
            return true;
        }
        if ((locators0 == null) || (locators1 == null))
        {
            return false;
        }

        // compare locator map sizes
        if (locators0.size() != locators1.size())
        {
            return false;
        }

        // compare locator map entries
        for (Map.Entry<String,ProfileLocator> entry : locators0.entrySet())
        {
            ProfileLocator locator0 = entry.getValue();
            ProfileLocator locator1 = locators1.get(entry.getKey());
            if (locator1 == null)
            {
                return false;
            }

            // compare locators using the most specific,
            // (i.e. first), locator properties array
            // returned by the locator iterator
            ProfileLocatorProperty [] properties0 = locator0.iterator().next();
            ProfileLocatorProperty [] properties1 = locator1.iterator().next();
            if ((properties0 != null) || (properties1 != null))
            {
                if ((properties0 == null) || (properties1 == null) || (properties0.length != properties1.length))
                {
                    return false;
                }
                
                // compare ordered locator properties
                for (int i = 0, limit = properties0.length; (i < limit); i++)
                {
                    // compare property names, control flags, navigation flags,
                    // and values. note: properties values are compared only for
                    // control or navigation properties; otherwise they are
                    // assumed to contain variable request paths that should
                    // be treated as equivalent
                    if (!properties0[i].getName().equals(properties1[i].getName()) ||
                        (properties0[i].isControl() && !properties1[i].isControl()) ||
                        (properties0[i].isNavigation() && !properties1[i].isNavigation()) ||
                        ((properties0[i].isControl() || properties0[i].isNavigation()) && 
                         (((properties0[i].getValue() == null) && (properties1[i].getValue() != null)) ||
                          ((properties0[i].getValue() != null) && !properties0[i].getValue().equals(properties1[i].getValue())))))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * locatorRequestPath - extract request specific path from profile locator
     *                      using request path from locator
     *
     * @param locator request profile locator
     * @return request path
     */     
    private static String locatorRequestPath(ProfileLocator locator)
    {
        // use request path in locator as default
        return locatorRequestPath(locator, locator.getRequestPath());
    }

    /**
     * locatorRequestPath - extract request specific path from profile locator
     *
     * @param locator request profile locator
     * @param requestPath request path
     * @return request path
     */
    private static String locatorRequestPath(ProfileLocator locator, String requestPath)
    {
        // search locator using the most specific,
        // (i.e. first), locator properties array
        // returned by the locator iterator and return
        // first valued property that is not a control
        // or navigation type
        ProfileLocatorProperty [] properties = locator.iterator().next();
        for (int i = 0, limit = properties.length; (i < limit); i++)
        {
            if (!properties[i].isControl() && !properties[i].isNavigation() && (properties[i].getValue() != null))
            {
                // use specified locator path
                String locatorPath = properties[i].getValue();

                // return specified locatorPath if absolute
                if (locatorPath.startsWith(Folder.PATH_SEPARATOR))
                {
                    return locatorPath;
                }

                // page names and relative paths are assumed relative to
                // request path and that any locator paths with no url
                // separator should have the page extension appended
                // get default page if page path null
                if ((locatorPath.indexOf(Folder.PATH_SEPARATOR) == -1) && !locatorPath.endsWith(Page.DOCUMENT_TYPE))
                {
                    locatorPath += Page.DOCUMENT_TYPE;
                }
            
                // append locator path to request path, replacing
                // requested pages and preserving requested folders
                boolean rootFolderRequest = requestPath.equals(Folder.PATH_SEPARATOR);
                boolean folderRequest = (!requestPath.endsWith(Page.DOCUMENT_TYPE));
                int lastSeparatorIndex = requestPath.lastIndexOf(Folder.PATH_SEPARATOR_CHAR);
                if ((lastSeparatorIndex > 0) && (!folderRequest || requestPath.endsWith(Folder.PATH_SEPARATOR)))
                {
                    // append locator to request path base path
                    return requestPath.substring(0, lastSeparatorIndex) + Folder.PATH_SEPARATOR + locatorPath;
                }
                else if (!rootFolderRequest && folderRequest)
                {
                    // append locator to request path root folder
                    return requestPath + Folder.PATH_SEPARATOR + locatorPath;
                }
                else
                {
                    // use root folder locator
                    return Folder.PATH_SEPARATOR + locatorPath;
                }
            }
        }
        return requestPath;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManagerEventListener#newNode(org.apache.jetspeed.page.document.Node)
     */
    public void newNode(Node node)
    {
        // equivalent to node updated event
        updatedNode(node);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManagerEventListener#updatedNode(org.apache.jetspeed.page.document.Node)
     */
    public void updatedNode(Node node)
    {
        // set stale flag to force session context state reset
        synchronized (this)
        {
            stale = true;
        }

        // log updated node event
        if (log.isDebugEnabled())
        {
            if (node != null)
            {
                log.debug("Page manager update event, (node=" + node.getPath() + "): set session context state stale");
            }
            else
            {
                log.debug("Page manager update event: set session context state stale");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManagerEventListener#removedNode(org.apache.jetspeed.page.document.Node)
     */
    public void removedNode(Node node)
    {
        // equivalent to node updated event
        updatedNode(node);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManagerEventListener#reapNodes(long)
     */
    public synchronized void reapNodes(long interval)
    {
        // reap page manager nodes for idle sessions to free
        // system resources; the site view will be lazily
        // recalculated if the session is resumed
        if ((locatorsLastUpdateCheck > 0) && (System.currentTimeMillis()-locatorsLastUpdateCheck > interval))
        {
            siteView = null;
        }
    }

    /**
     * sessionDidActivate - notification that the session has just
     *                      been activated
     *
     * @param event session activation event
     */
    public void sessionDidActivate(HttpSessionEvent event)
    {
        // set stale flag to force session context state reset
        synchronized (this)
        {
            stale = true;
        }

        // log activation event
        if (log.isDebugEnabled())
        {
            log.debug("Session activation event: set session context state stale");
        }
    }
    
    /**
     * sessionWillPassivate - notification that the session is about
     *                        to be passivated
     *
     * @param event session activation event
     */
    public void sessionWillPassivate(HttpSessionEvent event)
    {
        // clear session context state
        clearSessionProfileLocators();

        // log activation event
        if (log.isDebugEnabled())
        {
            log.debug("Session deactivation event: clear session context state");
        }
    }

    /**
     * valueBound - notifies this context that it is being bound to
     *              a session and identifies the session
     *
     * @param event session binding event
     */
    public void valueBound(HttpSessionBindingEvent event)
    {
        // subscribe this session context to page manager events
        synchronized (this)
        {
            if (!subscribed && (pageManager != null))
            {
                pageManager.addListener(this);
                subscribed = true;
            }
        }

        // log binding event
        if (log.isDebugEnabled())
        {
            log.debug("Session bound event: setup page manager listener");
        }
    }
    
    /**
     * valueUnbound - notifies this context that it is being unbound
     *                from a session and identifies the session
     *
     * @param event session binding event
     */
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        synchronized (this)
        {
            // unsubscribe this session context to page manager events
            if (subscribed && (pageManager != null))
            {
                pageManager.removeListener(this);
                subscribed = false;
            }

            // clear session context state
            clearSessionProfileLocators();
        }

        // log binding event
        if (log.isDebugEnabled())
        {
            log.debug("Session unbound event: clear page manager listener and session context state");
        }
    }

	private synchronized Map<String,String> getFolderPageHistory()
    {
		if (folderPageHistory == null)
        {
			folderPageHistory = new HashMap<String,String>();
		}
		return folderPageHistory;
	}
}
