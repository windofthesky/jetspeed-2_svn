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
package org.apache.jetspeed.page.impl;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.FolderMenuExcludeDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.FolderMenuIncludeDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.FolderSecurityConstraintImpl;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.ContentPageImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.page.impl.FragmentImpl;
import org.apache.jetspeed.om.page.impl.FragmentPreferenceImpl;
import org.apache.jetspeed.om.page.impl.FragmentSecurityConstraintImpl;
import org.apache.jetspeed.om.page.impl.LinkImpl;
import org.apache.jetspeed.om.page.impl.LinkSecurityConstraintImpl;
import org.apache.jetspeed.om.page.impl.PageImpl;
import org.apache.jetspeed.om.page.impl.PageMenuDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageMenuExcludeDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageMenuIncludeDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageMenuOptionsDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageMenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageSecurityConstraintImpl;
import org.apache.jetspeed.om.page.impl.PageSecurityImpl;
import org.apache.jetspeed.om.page.impl.PageSecuritySecurityConstraintImpl;
import org.apache.jetspeed.om.page.impl.SecurityConstraintsDefImpl;
import org.apache.jetspeed.om.page.impl.SecurityConstraintsImpl;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.DelegatingPageManager;
import org.apache.jetspeed.page.FolderNotRemovedException;
import org.apache.jetspeed.page.FolderNotUpdatedException;
import org.apache.jetspeed.page.LinkNotRemovedException;
import org.apache.jetspeed.page.LinkNotUpdatedException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageManagerEventListener;
import org.apache.jetspeed.page.PageManagerUtils;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.PageNotRemovedException;
import org.apache.jetspeed.page.PageNotUpdatedException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.jetspeed.page.document.impl.NodeImpl;
import org.apache.ojb.broker.core.proxy.ProxyHelper;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;

/**
 * DatabasePageManager
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class DatabasePageManager extends InitablePersistenceBrokerDaoSupport implements PageManager
{
    private static final int DEFAULT_CACHE_SIZE = 128;
    private static final int MIN_CACHE_EXPIRES_SECONDS = 30;
    private static final int DEFAULT_CACHE_EXPIRES_SECONDS = 150;

    private static Map modelClasses = new HashMap();
    static
    {
        modelClasses.put("FragmentImpl", FragmentImpl.class);
        modelClasses.put("PageImpl", PageImpl.class);
        modelClasses.put("FolderImpl", FolderImpl.class);
        modelClasses.put("LinkImpl", LinkImpl.class);
        modelClasses.put("PageSecurityImpl", PageSecurityImpl.class);
        modelClasses.put("FolderMenuDefinitionImpl", FolderMenuDefinitionImpl.class);
        modelClasses.put("FolderMenuExcludeDefinitionImpl", FolderMenuExcludeDefinitionImpl.class);
        modelClasses.put("FolderMenuIncludeDefinitionImpl", FolderMenuIncludeDefinitionImpl.class);
        modelClasses.put("FolderMenuOptionsDefinitionImpl", FolderMenuOptionsDefinitionImpl.class);
        modelClasses.put("FolderMenuSeparatorDefinitionImpl", FolderMenuSeparatorDefinitionImpl.class);
        modelClasses.put("PageMenuDefinitionImpl", PageMenuDefinitionImpl.class);
        modelClasses.put("PageMenuExcludeDefinitionImpl", PageMenuExcludeDefinitionImpl.class);
        modelClasses.put("PageMenuIncludeDefinitionImpl", PageMenuIncludeDefinitionImpl.class);
        modelClasses.put("PageMenuOptionsDefinitionImpl", PageMenuOptionsDefinitionImpl.class);
        modelClasses.put("PageMenuSeparatorDefinitionImpl", PageMenuSeparatorDefinitionImpl.class);
        modelClasses.put("SecurityConstraintsImpl", SecurityConstraintsImpl.class);
        modelClasses.put("FolderSecurityConstraintImpl", FolderSecurityConstraintImpl.class);
        modelClasses.put("PageSecurityConstraintImpl", PageSecurityConstraintImpl.class);
        modelClasses.put("FragmentSecurityConstraintImpl", FragmentSecurityConstraintImpl.class);
        modelClasses.put("LinkSecurityConstraintImpl", LinkSecurityConstraintImpl.class);
        modelClasses.put("PageSecuritySecurityConstraintImpl", PageSecuritySecurityConstraintImpl.class);
        modelClasses.put("SecurityConstraintsDefImpl", SecurityConstraintsDefImpl.class);
        modelClasses.put("FragmentPreferenceImpl", FragmentPreferenceImpl.class);
    }

    private DelegatingPageManager delegator;
    
    private int cacheSize;

    private int cacheExpiresSeconds;

    private DatabasePageManagerCache cache;

    public DatabasePageManager(String repositoryPath, int cacheSize, int cacheExpiresSeconds, boolean isPermissionsSecurity, boolean isConstraintsSecurity)
    {
        super(repositoryPath);
        delegator = new DelegatingPageManager(isPermissionsSecurity, isConstraintsSecurity, modelClasses);
        this.cacheSize = Math.max(cacheSize, DEFAULT_CACHE_SIZE);
        if (cacheExpiresSeconds < 0)
        {
            this.cacheExpiresSeconds = DEFAULT_CACHE_EXPIRES_SECONDS;
        }
        else if (cacheExpiresSeconds == 0)
        {
            this.cacheExpiresSeconds = 0;
        }
        else
        {
            this.cacheExpiresSeconds = Math.max(cacheExpiresSeconds, MIN_CACHE_EXPIRES_SECONDS);
        }
        DatabasePageManagerCache.cacheInit(this);
    }

    /**
     * getCacheSize
     *
     * @return configured cache size
     */
    public int getCacheSize()
    {
        return cacheSize;
    }

    /**
     * getCacheExpiresSeconds
     *
     * @return configured cache expiration in seconds
     */
    public int getCacheExpiresSeconds()
    {
        return cacheExpiresSeconds;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getConstraintsEnabled()
     */
    public boolean getConstraintsEnabled()
    {
        return delegator.getConstraintsEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        return delegator.getPermissionsEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPage(java.lang.String)
     */
    public Page newPage(String path)
    {
        return delegator.newPage(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFolder(java.lang.String)
     */
    public Folder newFolder(String path)
    {
        return delegator.newFolder(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newLink(java.lang.String)
     */
    public Link newLink(String path)
    {
        return delegator.newLink(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageSecurity()
     */
    public PageSecurity newPageSecurity()
    {
        return delegator.newPageSecurity();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFragment()
     */
    public Fragment newFragment()
    {
        return delegator.newFragment();    
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPortletFragment()
     */
    public Fragment newPortletFragment()
    {
        return delegator.newPortletFragment();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFolderMenuDefinition()
     */
    public MenuDefinition newFolderMenuDefinition()
    {
        return delegator.newFolderMenuDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFolderMenuExcludeDefinition()
     */
    public MenuExcludeDefinition newFolderMenuExcludeDefinition()
    {
        return delegator.newFolderMenuExcludeDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFolderMenuIncludeDefinition()
     */
    public MenuIncludeDefinition newFolderMenuIncludeDefinition()
    {
        return delegator.newFolderMenuIncludeDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFolderMenuOptionsDefinition()
     */
    public MenuOptionsDefinition newFolderMenuOptionsDefinition()
    {
        return delegator.newFolderMenuOptionsDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFolderMenuSeparatorDefinition()
     */
    public MenuSeparatorDefinition newFolderMenuSeparatorDefinition()
    {
        return delegator.newFolderMenuSeparatorDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageMenuDefinition()
     */
    public MenuDefinition newPageMenuDefinition()
    {
        return delegator.newPageMenuDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageMenuExcludeDefinition()
     */
    public MenuExcludeDefinition newPageMenuExcludeDefinition()
    {
        return delegator.newPageMenuExcludeDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageMenuIncludeDefinition()
     */
    public MenuIncludeDefinition newPageMenuIncludeDefinition()
    {
        return delegator.newPageMenuIncludeDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageMenuOptionsDefinition()
     */
    public MenuOptionsDefinition newPageMenuOptionsDefinition()
    {
        return delegator.newPageMenuOptionsDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageMenuSeparatorDefinition()
     */
    public MenuSeparatorDefinition newPageMenuSeparatorDefinition()
    {
        return delegator.newPageMenuSeparatorDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newSecurityConstraints()
     */
    public SecurityConstraints newSecurityConstraints()
    {
        return delegator.newSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFolderSecurityConstraint()
     */
    public SecurityConstraint newFolderSecurityConstraint()
    {
        return delegator.newFolderSecurityConstraint();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageSecurityConstraint()
     */
    public SecurityConstraint newPageSecurityConstraint()
    {
        return delegator.newPageSecurityConstraint();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFragmentSecurityConstraint()
     */
    public SecurityConstraint newFragmentSecurityConstraint()
    {
        return delegator.newFragmentSecurityConstraint();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newLinkSecurityConstraint()
     */
    public SecurityConstraint newLinkSecurityConstraint()
    {
        return delegator.newLinkSecurityConstraint();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageSecuritySecurityConstraint()
     */
    public SecurityConstraint newPageSecuritySecurityConstraint()
    {
        return delegator.newPageSecuritySecurityConstraint();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newSecurityConstraintsDef()
     */
    public SecurityConstraintsDef newSecurityConstraintsDef()
    {
        return delegator.newSecurityConstraintsDef();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFragmentPreference()
     */
    public FragmentPreference newFragmentPreference()
    {
        return delegator.newFragmentPreference();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#addListener(org.apache.jetspeed.page.PageManagerEventListener)
     */
    public void addListener(PageManagerEventListener listener)
    {
        delegator.addListener(listener);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeListener(org.apache.jetspeed.page.PageManagerEventListener)
     */
    public void removeListener(PageManagerEventListener listener)
    {
        delegator.removeListener(listener);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#reset()
     */
    public void reset()
    {
        // propagate to delegator
        delegator.reset();

        // clear cache to force subsequent refreshs from persistent store
        DatabasePageManagerCache.cacheClear();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPage(java.lang.String)
     */
    public Page getPage(String path) throws PageNotFoundException, NodeException
    {
        // construct page attributes from path
        path = NodeImpl.getCanonicalNodePath(path);

        // optimized retrieval from cache by path if available
        NodeImpl cachedNode = DatabasePageManagerCache.cacheLookup(path);
        if (cachedNode instanceof Page)
        {
            // check for view access on page
            cachedNode.checkAccess(JetspeedActions.VIEW);

            return (Page)cachedNode;
        }

        // retrieve page from database
        try
        {
            Criteria filter = new Criteria();
            filter.addEqualTo("path", path);
            QueryByCriteria query = QueryFactory.newQuery(PageImpl.class, filter);
            Page page = (Page) getPersistenceBrokerTemplate().getObjectByQuery(query);
            
            // return page or throw exception
            if (page == null)
            {
                throw new PageNotFoundException("Page " + path + " not found.");
            }

            // check for view access on page
            page.checkAccess(JetspeedActions.VIEW);

            return page;
        }
        catch (PageNotFoundException pnfe)
        {
            throw pnfe;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new PageNotFoundException("Page " + path + " not found.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getContentPage(java.lang.String)
     */
    public ContentPage getContentPage(String path) throws PageNotFoundException, NodeException
    {
        // return proxied page
        return new ContentPageImpl(getPage(path));
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLink(java.lang.String)
     */
    public Link getLink(String path) throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException
    {
        // construct link attributes from path
        path = NodeImpl.getCanonicalNodePath(path);

        // optimized retrieval from cache by path if available
        NodeImpl cachedNode = DatabasePageManagerCache.cacheLookup(path);
        if (cachedNode instanceof Link)
        {
            // check for view access on link
            cachedNode.checkAccess(JetspeedActions.VIEW);

            return (Link)cachedNode;
        }

        // retrieve link from database
        try
        {
            Criteria filter = new Criteria();
            filter.addEqualTo("path", path);
            QueryByCriteria query = QueryFactory.newQuery(LinkImpl.class, filter);
            Link link = (Link) getPersistenceBrokerTemplate().getObjectByQuery(query);
            
            // return link or throw exception
            if (link == null)
            {
                throw new DocumentNotFoundException("Link " + path + " not found.");
            }

            // check for view access on link
            link.checkAccess(JetspeedActions.VIEW);

            return link;
        }
        catch (DocumentNotFoundException dnfe)
        {
            throw dnfe;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new DocumentNotFoundException("Link " + path + " not found.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageSecurity()
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException
    {
        // construct document attributes from path
        String path = Folder.PATH_SEPARATOR + PageSecurity.DOCUMENT_TYPE;

        // optimized retrieval from cache by path if available
        NodeImpl cachedNode = DatabasePageManagerCache.cacheLookup(path);
        if (cachedNode instanceof PageSecurity)
        {
            // check for view access on document
            cachedNode.checkAccess(JetspeedActions.VIEW);

            return (PageSecurity)cachedNode;
        }

        // retrieve document from database
        try
        {
            Criteria filter = new Criteria();
            filter.addEqualTo("path", path);
            QueryByCriteria query = QueryFactory.newQuery(PageSecurityImpl.class, filter);
            PageSecurity document = (PageSecurity) getPersistenceBrokerTemplate().getObjectByQuery(query);
            
            // return page or throw exception
            if (document == null)
            {
                throw new DocumentNotFoundException("Document " + path + " not found.");
            }

            // check for view access on document
            document.checkAccess(JetspeedActions.VIEW);

            return document;
        }
        catch (DocumentNotFoundException dnfe)
        {
            throw dnfe;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new DocumentNotFoundException("Document " + path + " not found.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFolder(java.lang.String)
     */
    public Folder getFolder(String folderPath) throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        // construct folder attributes from path
        folderPath = NodeImpl.getCanonicalNodePath(folderPath);

        // optimized retrieval from cache by path if available
        NodeImpl cachedNode = DatabasePageManagerCache.cacheLookup(folderPath);
        if (cachedNode instanceof Folder)
        {
            // check for view access on folder
            cachedNode.checkAccess(JetspeedActions.VIEW);

            return (Folder)cachedNode;
        }

        // retrieve folder from database
        try
        {
            Criteria filter = new Criteria();
            filter.addEqualTo("path", folderPath);
            QueryByCriteria query = QueryFactory.newQuery(FolderImpl.class, filter);
            Folder folder = (Folder) getPersistenceBrokerTemplate().getObjectByQuery(query);
            
            // return folder or throw exception
            if (folder == null)
            {
                throw new FolderNotFoundException("Folder " + folderPath + " not found.");
            }

            // check for view access on folder
            folder.checkAccess(JetspeedActions.VIEW);

            return folder;
        }
        catch (FolderNotFoundException fnfe)
        {
            throw fnfe;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new FolderNotFoundException("Folder " + folderPath + " not found.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws JetspeedException, PageNotUpdatedException
    {
        try
        {
            // dereference page in case proxy is supplied
            if (page instanceof ContentPageImpl)
            {
                page = ((ContentPageImpl)page).getPage();
            }
            page = (Page)ProxyHelper.getRealObject(page);

            // look up and set parent folder if necessary
            if (page.getParent() == null)
            {
                // access folder by path
                String pagePath = page.getPath();
                String parentPath = pagePath.substring(0, pagePath.lastIndexOf(Folder.PATH_SEPARATOR));
                if (parentPath.length() == 0)
                {
                    parentPath = Folder.PATH_SEPARATOR;
                }
                FolderImpl parent = null;
                try
                {
                    parent = (FolderImpl)getFolder(parentPath);
                }
                catch (FolderNotFoundException fnfe)
                {
                    throw new PageNotUpdatedException("Missing parent folder: " + parentPath);
                }
                
                // check for edit access on parent folder; page
                // access not checked on create
                parent.checkAccess(JetspeedActions.EDIT);

                try
                {
                    // update parent folder with added page
                    parent.addPage((PageImpl)page);
                    page.setParent(parent);
                    getPersistenceBrokerTemplate().store(parent);
                }
                catch (Exception e)
                {
                    // cleanup parent folder on error
                    parent.removePage((PageImpl)page);
                    throw e;
                }

                // notify page manager listeners
                delegator.notifyNewNode(page);
            }
            else
            {
                // check for edit access on page and parent folder
                page.checkAccess(JetspeedActions.EDIT);

                // update page
                getPersistenceBrokerTemplate().store(page);

                // notify page manager listeners
                delegator.notifyUpdatedNode(page);
            }
        }
        catch (PageNotUpdatedException pnue)
        {
            throw pnue;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new PageNotUpdatedException("Page " + page.getPath() + " not updated.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page) throws JetspeedException, PageNotRemovedException
    {
        try
        {
            // dereference page in case proxy is supplied
            if (page instanceof ContentPageImpl)
            {
                page = ((ContentPageImpl)page).getPage();
            }
            page = (Page)ProxyHelper.getRealObject(page);

            // check for edit access on page and parent folder
            page.checkAccess(JetspeedActions.EDIT);

            // look up and update parent folder if necessary
            if (page.getParent() != null)
            {
                // update parent folder with removed page; deletes page
                FolderImpl parent = (FolderImpl)ProxyHelper.getRealObject(page.getParent());
                parent.removePage((PageImpl)page);
                getPersistenceBrokerTemplate().store(parent);
            }
            else
            {
                // delete page
                getPersistenceBrokerTemplate().delete(page);
            }

            // notify page manager listeners
            delegator.notifyRemovedNode(page);
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new PageNotRemovedException("Page " + page.getPath() + " not removed.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void updateFolder(Folder folder) throws JetspeedException, FolderNotUpdatedException
    {
        try
        {
            // dereference folder in case proxy is supplied
            folder = (Folder)ProxyHelper.getRealObject(folder);

            // look up and set parent folder if required
            if ((folder.getParent() == null) && !folder.getPath().equals(Folder.PATH_SEPARATOR))
            {
                // access folder by path
                String folderPath = folder.getPath();
                String parentPath = folderPath.substring(0, folderPath.lastIndexOf(Folder.PATH_SEPARATOR));
                if (parentPath.length() == 0)
                {
                    parentPath = Folder.PATH_SEPARATOR;
                }
                FolderImpl parent = null;
                try
                {
                    parent = (FolderImpl)getFolder(parentPath);
                }
                catch (FolderNotFoundException fnfe)
                {
                    throw new FolderNotUpdatedException("Missing parent folder: " + parentPath);
                }
                
                // check for edit access on parent folder; folder
                // access not checked on create
                parent.checkAccess(JetspeedActions.EDIT);

                try
                {
                    // update parent folder with added folder
                    parent.addFolder((FolderImpl)folder);
                    folder.setParent(parent);
                    getPersistenceBrokerTemplate().store(parent);
                }
                catch (Exception e)
                {
                    // cleanup parent folder on error
                    parent.removeFolder((FolderImpl)folder);
                    throw e;
                }

                // notify page manager listeners
                delegator.notifyNewNode(folder);
            }
            else
            {
                // determine if folder is new by checking autoincrement id
                boolean newFolder = folder.getId().equals("0");

                // check for edit access on folder and parent folder
                // if not being initially created; access is not
                // checked on create
                if (!newFolder || !folder.getPath().equals(Folder.PATH_SEPARATOR))
                {
                    folder.checkAccess(JetspeedActions.EDIT);
                }

                // create root folder or update folder
                getPersistenceBrokerTemplate().store(folder);

                // notify page manager listeners
                if (newFolder && !folder.getId().equals("0"))
                {
                    delegator.notifyNewNode(folder);
                }
                else
                {
                    delegator.notifyUpdatedNode(folder);
                }
            }
        }
        catch (FolderNotUpdatedException fnue)
        {
            throw fnue;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new FolderNotUpdatedException("Folder " + folder.getPath() + " not updated.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void removeFolder(Folder folder) throws JetspeedException, FolderNotRemovedException
    {
        try
        {
            // dereference folder in case proxy is supplied
            folder = (Folder)ProxyHelper.getRealObject(folder);

            // check for edit access on folder and parent folder
            folder.checkAccess(JetspeedActions.EDIT);

            // look up and update parent folder if necessary
            if (folder.getParent() != null)
            {
                // update parent folder with removed folder; deep deletes folder
                FolderImpl parent = (FolderImpl)ProxyHelper.getRealObject(folder.getParent());
                parent.removeFolder((FolderImpl)folder);
                getPersistenceBrokerTemplate().store(parent);
            }
            else
            {
                // deep delete folder
                getPersistenceBrokerTemplate().delete(folder);
            }

            // notify page manager listeners
            delegator.notifyRemovedNode(folder);
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new FolderNotRemovedException("Folder " + folder.getPath() + " not removed.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateLink(org.apache.jetspeed.om.page.Link)
     */
    public void updateLink(Link link) throws JetspeedException, LinkNotUpdatedException
    {
        try
        {
            // dereference link in case proxy is supplied
            link = (Link)ProxyHelper.getRealObject(link);

            // look up and set parent folder if necessary
            if (link.getParent() == null)
            {
                // access folder by path
                String linkPath = link.getPath();
                String parentPath = linkPath.substring(0, linkPath.lastIndexOf(Folder.PATH_SEPARATOR));
                if (parentPath.length() == 0)
                {
                    parentPath = Folder.PATH_SEPARATOR;
                }
                FolderImpl parent = null;
                try
                {
                    parent = (FolderImpl)getFolder(parentPath);
                }
                catch (FolderNotFoundException fnfe)
                {
                    throw new FailedToUpdateDocumentException("Missing parent folder: " + parentPath);
                }
                
                // check for edit access on parent folder; link
                // access not checked on create
                parent.checkAccess(JetspeedActions.EDIT);

                try
                {
                    // update parent folder with added link
                    parent.addLink((LinkImpl)link);
                    link.setParent(parent);
                    getPersistenceBrokerTemplate().store(parent);
                }
                catch (Exception e)
                {
                    // cleanup parent folder on error
                    parent.removeLink((LinkImpl)link);
                    throw e;
                }

                // notify page manager listeners
                delegator.notifyNewNode(link);
            }
            else
            {
                // check for edit access on link and parent folder
                link.checkAccess(JetspeedActions.EDIT);

                // update link
                getPersistenceBrokerTemplate().store(link);

                // notify page manager listeners
                delegator.notifyUpdatedNode(link);
            }
        }
        catch (FailedToUpdateDocumentException fude)
        {
            throw fude;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new FailedToUpdateDocumentException("Link " + link.getPath() + " not updated.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeLink(org.apache.jetspeed.om.page.Link)
     */
    public void removeLink(Link link) throws JetspeedException, LinkNotRemovedException
    {
        try
        {
            // dereference link in case proxy is supplied
            link = (Link)ProxyHelper.getRealObject(link);

            // check for edit access on link and parent folder
            link.checkAccess(JetspeedActions.EDIT);

            // look up and update parent folder if necessary
            if (link.getParent() != null)
            {
                // update parent folder with removed link; deletes link
                FolderImpl parent = (FolderImpl)ProxyHelper.getRealObject(link.getParent());
                parent.removeLink((LinkImpl)link);
                getPersistenceBrokerTemplate().store(parent);
            }
            else
            {
                // delete link
                getPersistenceBrokerTemplate().delete(link);
            }

            // notify page manager listeners
            delegator.notifyRemovedNode(link);
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new FailedToDeleteDocumentException("Link " + link.getPath() + " not removed.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updatePageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public void updatePageSecurity(PageSecurity pageSecurity) throws JetspeedException, FailedToUpdateDocumentException
    {
        try
        {
            // dereference document in case proxy is supplied
            pageSecurity = (PageSecurity)ProxyHelper.getRealObject(pageSecurity);

            // look up and set parent folder if necessary
            if (pageSecurity.getParent() == null)
            {
                // access folder by path
                String pageSecurityPath = pageSecurity.getPath();
                String parentPath = pageSecurityPath.substring(0, pageSecurityPath.lastIndexOf(Folder.PATH_SEPARATOR));
                if (parentPath.length() == 0)
                {
                    parentPath = Folder.PATH_SEPARATOR;
                }
                FolderImpl parent = null;
                try
                {
                    parent = (FolderImpl)getFolder(parentPath);
                }
                catch (FolderNotFoundException fnfe)
                {
                    throw new FailedToUpdateDocumentException("Missing parent folder: " + parentPath);
                }

                // do not replace existing page security documents
                try
                {
                    // test for page security document
                    parent.getPageSecurity();
                    throw new FailedToUpdateDocumentException("Parent folder page security exists: " + parentPath);
                }
                catch (DocumentNotFoundException dnfe)
                {
                    // check for edit access on parent folder; document
                    // access not checked on create
                    parent.checkAccess(JetspeedActions.EDIT);
                    
                    try
                    {
                        // update parent folder with added document
                        parent.setPageSecurity((PageSecurityImpl)pageSecurity);
                        pageSecurity.setParent(parent);
                        getPersistenceBrokerTemplate().store(parent);
                    }
                    catch (Exception e)
                    {
                        // clear document on error
                        parent.setPageSecurity(null);
                        throw e;
                    }
                }
                catch (Exception e)
                {
                    throw new FailedToUpdateDocumentException("Parent folder page security exists: " + parentPath);
                }

                // notify page manager listeners
                delegator.notifyNewNode(pageSecurity);
            }
            else
            {
                // check for edit access on document and parent folder
                pageSecurity.checkAccess(JetspeedActions.EDIT);

                // update document
                getPersistenceBrokerTemplate().store(pageSecurity);

                // notify page manager listeners
                delegator.notifyUpdatedNode(pageSecurity);
            }

            // reset all cached security constraints
            DatabasePageManagerCache.resetCachedSecurityConstraints();
        }
        catch (FailedToUpdateDocumentException fude)
        {
            throw fude;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new FailedToUpdateDocumentException("Document " + pageSecurity.getPath() + " not updated.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public void removePageSecurity(PageSecurity pageSecurity) throws JetspeedException, FailedToDeleteDocumentException
    {
        try
        {
            // dereference document in case proxy is supplied
            pageSecurity = (PageSecurity)ProxyHelper.getRealObject(pageSecurity);

            // check for edit access on document and parent folder
            pageSecurity.checkAccess(JetspeedActions.EDIT);

            // look up and update parent folder if necessary
            if (pageSecurity.getParent() != null)
            {
                // update parent folder with removed document; deletes document
                FolderImpl parent = (FolderImpl)ProxyHelper.getRealObject(pageSecurity.getParent());
                parent.setPageSecurity(null);
                getPersistenceBrokerTemplate().store(parent);
            }
            else
            {
                // delete document
                getPersistenceBrokerTemplate().delete(pageSecurity);
            }

            // reset all cached security constraints
            DatabasePageManagerCache.resetCachedSecurityConstraints();

            // notify page manager listeners
            delegator.notifyRemovedNode(pageSecurity);
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new FailedToDeleteDocumentException("Document " + pageSecurity.getPath() + " not removed.", e);
        }
    }

    public Page copyPage(Page source, String path)
    throws JetspeedException, PageNotUpdatedException
    {
        return this.delegator.copyPage(source, path);
    }

    public Link copyLink(Link source, String path)
    throws JetspeedException, LinkNotUpdatedException
    {
        return this.delegator.copyLink(source, path);
    }

    public Folder copyFolder(Folder source, String path)
    throws JetspeedException, PageNotUpdatedException
    {
        return this.delegator.copyFolder(source, path);
    }

    public Fragment copyFragment(Fragment source, String name)
    throws JetspeedException, PageNotUpdatedException
    {
        return this.delegator.copyFragment(source, name);
    }
    
    public PageSecurity copyPageSecurity(PageSecurity source) 
    throws JetspeedException
    {
        return this.delegator.copyPageSecurity(source);
    }
    
    public Page getUserPage(String userName, String pageName)
    throws PageNotFoundException, NodeException
    {
        return this.getPage(Folder.USER_FOLDER + userName + Folder.PATH_SEPARATOR + pageName);
    }
    
    public Folder getUserFolder(String userName) 
        throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        return this.getFolder(Folder.USER_FOLDER + userName);        
    }

    public boolean folderExists(String folderName)
    {
        try
        {
            getFolder(folderName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    public boolean pageExists(String pageName)
    {
        try
        {
            getPage(pageName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    public boolean linkExists(String linkName)
    {
        try
        {
            getLink(linkName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public boolean userFolderExists(String userName)
    {
        try
        {
            getFolder(Folder.USER_FOLDER + userName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    public boolean userPageExists(String userName, String pageName)
    {
        try
        {
            getPage(Folder.USER_FOLDER + userName + Folder.PATH_SEPARATOR + pageName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    public void createUserHomePagesFromRoles(Subject subject)
    throws JetspeedException
    {
        PageManagerUtils.createUserHomePagesFromRoles(this, subject);
    }
    
    public void deepCopyFolder(Folder srcFolder, String destinationPath, String owner)
    throws JetspeedException, PageNotUpdatedException
    {
        PageManagerUtils.deepCopyFolder(this, srcFolder, destinationPath, owner);
    }
    
    public int addPages(Page[] pages)
    throws JetspeedException
    {
        System.out.println("Adding first page");
        this.updatePage(pages[0]);
        System.out.println("Adding second page");
        this.updatePage(pages[1]);
        System.out.println("About to throw ex");
        throw new JetspeedException("Its gonna blow captain!");
    }
    
}
