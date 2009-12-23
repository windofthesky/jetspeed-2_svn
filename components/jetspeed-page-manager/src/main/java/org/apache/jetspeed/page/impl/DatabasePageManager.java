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
package org.apache.jetspeed.page.impl;

import java.security.AccessController;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.idgenerator.IdGenerator;
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
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageFragment;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.page.impl.BaseFragmentElementImpl;
import org.apache.jetspeed.om.page.impl.BaseFragmentsElementImpl;
import org.apache.jetspeed.om.page.impl.DynamicPageImpl;
import org.apache.jetspeed.om.page.impl.FragmentDefinitionImpl;
import org.apache.jetspeed.om.page.impl.FragmentImpl;
import org.apache.jetspeed.om.page.impl.FragmentPreferenceImpl;
import org.apache.jetspeed.om.page.impl.FragmentPropertyImpl;
import org.apache.jetspeed.om.page.impl.FragmentPropertyList;
import org.apache.jetspeed.om.page.impl.FragmentReferenceImpl;
import org.apache.jetspeed.om.page.impl.FragmentSecurityConstraintImpl;
import org.apache.jetspeed.om.page.impl.LinkImpl;
import org.apache.jetspeed.om.page.impl.LinkSecurityConstraintImpl;
import org.apache.jetspeed.om.page.impl.PageFragmentImpl;
import org.apache.jetspeed.om.page.impl.PageImpl;
import org.apache.jetspeed.om.page.impl.PageMenuDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageMenuExcludeDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageMenuIncludeDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageMenuOptionsDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageMenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.page.impl.PageSecurityConstraintImpl;
import org.apache.jetspeed.om.page.impl.PageSecurityImpl;
import org.apache.jetspeed.om.page.impl.PageSecuritySecurityConstraintImpl;
import org.apache.jetspeed.om.page.impl.PageTemplateImpl;
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
import org.apache.jetspeed.page.PageManagerSecurityUtils;
import org.apache.jetspeed.page.PageManagerUtils;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.PageNotRemovedException;
import org.apache.jetspeed.page.PageNotUpdatedException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.impl.NodeImpl;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
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
    private static ThreadLocal fragmentPropertyListsCache = new ThreadLocal();
    
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
        modelClasses.put("FragmentReferenceImpl", FragmentReferenceImpl.class);
        modelClasses.put("PageFragmentImpl", PageFragmentImpl.class);
        modelClasses.put("PageTemplateImpl", PageTemplateImpl.class);
        modelClasses.put("DynamicPageImpl", DynamicPageImpl.class);
        modelClasses.put("FragmentDefinitionImpl", FragmentDefinitionImpl.class);
        modelClasses.put("FragmentPropertyImpl", FragmentPropertyImpl.class);
    }

    private DelegatingPageManager delegator;
    
    private PageManager pageManagerProxy;

    public DatabasePageManager(String repositoryPath, IdGenerator generator, boolean isPermissionsSecurity, boolean isConstraintsSecurity, JetspeedCache oidCache, JetspeedCache pathCache)
    {
        super(repositoryPath);
        delegator = new DelegatingPageManager(generator, isPermissionsSecurity, isConstraintsSecurity, modelClasses);
        DatabasePageManagerCache.cacheInit(oidCache, pathCache, this);
    }

    /**
     * getPageManagerProxy
     *
     * @return proxied page manager interface used to
     *         inject into Folder instances to provide
     *         transaction/interception
     */
    public PageManager getPageManagerProxy()
    {
        return pageManagerProxy;
    }

    /**
     * setPageManagerProxy
     *
     * @param proxy proxied page manager interface used to
     *              inject into Folder instances to provide
     *              transaction/interception
     */
    public void setPageManagerProxy(PageManager proxy)
    {
        // set/reset page manager proxy and propagate to cache
        if (pageManagerProxy != proxy)
        {
            pageManagerProxy = proxy;
            DatabasePageManagerCache.setPageManagerProxy(proxy);
        }
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
     * @see org.apache.jetspeed.page.PageManager#getNodeReapingInterval()
     */
    public long getNodeReapingInterval()
    {
        return delegator.getNodeReapingInterval();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPage(java.lang.String)
     */
    public Page newPage(String path)
    {
        return delegator.newPage(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageTemplate(java.lang.String)
     */
    public PageTemplate newPageTemplate(String path)
    {
        return delegator.newPageTemplate(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newDynamicPage(java.lang.String)
     */
    public DynamicPage newDynamicPage(String path)
    {
        return delegator.newDynamicPage(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFragmentDefinition(java.lang.String)
     */
    public FragmentDefinition newFragmentDefinition(String path)
    {
        return delegator.newFragmentDefinition(path);
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
     * @see org.apache.jetspeed.page.PageManager#newFragmentReference()
     */
    public FragmentReference newFragmentReference()
    {
        return delegator.newFragmentReference();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageFragment()
     */
    public PageFragment newPageFragment()
    {
        return delegator.newPageFragment();
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
     * @see org.apache.jetspeed.page.PageManager#newFragmentPreference()
     */
    public FragmentProperty newFragmentProperty()
    {
        return delegator.newFragmentProperty();
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
     * @see org.apache.jetspeed.page.PageManager#shutdown()
     */
    public void shutdown()
    {
        // delegate
        delegator.shutdown();
    }

     /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPage(java.lang.String)
     */
    public Page getPage(String path) throws PageNotFoundException, NodeException
    {
        return (Page)getFragmentsElement(Page.class, PageImpl.class, path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageTemplate(java.lang.String)
     */
    public PageTemplate getPageTemplate(String path) throws PageNotFoundException, NodeException
    {
        return (PageTemplate)getFragmentsElement(PageTemplate.class, PageTemplateImpl.class, path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getDynamicPage(java.lang.String)
     */
    public DynamicPage getDynamicPage(String path) throws PageNotFoundException, NodeException
    {
        return (DynamicPage)getFragmentsElement(DynamicPage.class, DynamicPageImpl.class, path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFragmentDefinition(java.lang.String)
     */
    public FragmentDefinition getFragmentDefinition(String path) throws PageNotFoundException, NodeException
    {
        return (FragmentDefinition)getFragmentsElement(FragmentDefinition.class, FragmentDefinitionImpl.class, path);
    }

    /**
     * Get generic fragments/page element from cache or query from database by class.
     * 
     * @param fragmentsElementType fragments/page type
     * @param fragmentsElementImplType fragments/page implementation type
     * @param path page path
     * @return page element
     * @throws PageNotFoundException
     * @throws NodeException
     */
    protected BaseFragmentsElement getFragmentsElement(Class fragmentsElementType, Class fragmentsElementImplType, String path) throws PageNotFoundException, NodeException
    {
        // construct page element attributes from path
        path = NodeImpl.getCanonicalNodePath(path);

        // optimized retrieval from cache by path if available
        NodeImpl cachedNode = DatabasePageManagerCache.cacheLookup(path);
        if (fragmentsElementType.isInstance(cachedNode))
        {
            // check for view access on page element
            cachedNode.checkAccess(JetspeedActions.VIEW);

            return (BaseFragmentsElement)cachedNode;
        }

        // retrieve page element from database
        try
        {
            Criteria filter = new Criteria();
            filter.addEqualTo("path", path);
            QueryByCriteria query = QueryFactory.newQuery(fragmentsElementImplType, filter);
            BaseFragmentsElement fragmentsElement = (BaseFragmentsElement)getPersistenceBrokerTemplate().getObjectByQuery(query);
            
            // return page element or throw exception
            if (fragmentsElement == null)
            {
                throw new PageNotFoundException("Fragments/page element " + path + " not found.");
            }

            // check for view access on page element
            fragmentsElement.checkAccess(JetspeedActions.VIEW);

            return fragmentsElement;
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
            throw new PageNotFoundException("Fragments/page element " + path + " not found.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLink(java.lang.String)
     */
    public Link getLink(String path) throws DocumentNotFoundException, NodeException
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
            Link link = (Link)getPersistenceBrokerTemplate().getObjectByQuery(query);
            
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

    /**
     * Given a securityConstraintName definition and a set of actions,
     * run a security constraint checks
     */
    public boolean checkConstraint(String securityConstraintName, String actions)
    {
        try
        {
            PageSecurity security = this.getPageSecurity();
            SecurityConstraintsDef def = security.getSecurityConstraintsDef(securityConstraintName);
            if (def != null)
            {
                return PageManagerSecurityUtils.checkConstraint(def, actions);
            }            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }           
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageSecurity()
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException, NodeException
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
            PageSecurity document = (PageSecurity)getPersistenceBrokerTemplate().getObjectByQuery(query);
            
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
            Folder folder = (Folder)getPersistenceBrokerTemplate().getObjectByQuery(query);
            
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
     * @see org.apache.jetspeed.page.PageManager#getFolders(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getFolders(Folder folder) throws DocumentException
    {
        FolderImpl folderImpl = (FolderImpl)folder;

        // perform lookup of folder folders collection and cache in folder
        try
        {
            // query for folders
            Criteria filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            QueryByCriteria query = QueryFactory.newQuery(FolderImpl.class, filter);
            Collection folders = getPersistenceBrokerTemplate().getCollectionByQuery(query);

            // cache folders in folder
            folderImpl.accessFolders().clear();
            if (folders != null)
            {
                folderImpl.accessFolders().addAll(folders);
            }
            folderImpl.resetFolders(true);
        }
        catch (Exception e)
        {
            // reset cache in folder
            folderImpl.resetFolders(false);
            throw new DocumentException("Unable to access folders for folder " + folder.getPath() + ".");
        }

        // folder folders cache populated, get folders from folder
        // to provide packaging as filtered node set
        return folder.getFolders();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFolder(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */
    public Folder getFolder(Folder folder, String name) throws FolderNotFoundException, DocumentException
    {
        // perform lookup by path so that cache can be used
        String folderPath = folder.getPath() + Folder.PATH_SEPARATOR + name;
        try
        {
            return getFolder(folderPath);
        }
        catch (FolderNotFoundException fnfe)
        {
            throw fnfe;
        }
        catch (Exception e)
        {
            throw new FolderNotFoundException("Folder " + folderPath + " not found.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPages(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getPages(Folder folder) throws NodeException
    {
        FolderImpl folderImpl = (FolderImpl)folder;

        // perform lookup of folder pages collection and cache in folder
        try
        {
            // query for pages
            Criteria filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            QueryByCriteria query = QueryFactory.newQuery(PageImpl.class, filter);
            Collection pages = getPersistenceBrokerTemplate().getCollectionByQuery(query);

            // cache pages in folder
            folderImpl.accessPages().clear();
            if (pages != null)
            {
                folderImpl.accessPages().addAll(pages);
            }
            folderImpl.resetPages(true);
        }
        catch (Exception e)
        {
            // reset cache in folder
            folderImpl.resetPages(false);
            throw new NodeException("Unable to access pages for folder " + folder.getPath() + ".");
        }

        // folder pages cache populated, get pages from folder
        // to provide packaging as filtered node set
        return folder.getPages();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPage(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */
    public Page getPage(Folder folder, String name) throws PageNotFoundException, NodeException
    {
        // perform lookup by path so that cache can be used
        String pagePath = folder.getPath() + Folder.PATH_SEPARATOR + name;
        try
        {
            return getPage(pagePath);
        }
        catch (PageNotFoundException pnfe)
        {
            throw pnfe;
        }
        catch (Exception e)
        {
            throw new PageNotFoundException("Page " + pagePath + " not found.", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageTemplates(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getPageTemplates(Folder folder) throws NodeException
    {
        FolderImpl folderImpl = (FolderImpl)folder;

        // perform lookup of folder page templates collection and cache in folder
        try
        {
            // query for page templates
            Criteria filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            QueryByCriteria query = QueryFactory.newQuery(PageTemplateImpl.class, filter);
            Collection pageTemplates = getPersistenceBrokerTemplate().getCollectionByQuery(query);

            // cache page templates in folder
            folderImpl.accessPageTemplates().clear();
            if (pageTemplates != null)
            {
                folderImpl.accessPageTemplates().addAll(pageTemplates);
            }
            folderImpl.resetPageTemplates(true);
        }
        catch (Exception e)
        {
            // reset cache in folder
            folderImpl.resetPageTemplates(false);
            throw new NodeException("Unable to access page templates for folder " + folder.getPath() + ".");
        }

        // folder page templates cache populated, get page templates
        // from folder to provide packaging as filtered node set
        return folder.getPageTemplates();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageTemplate(org.apache.jetspeed.om.folder.Folder, java.lang.String)
     */
    public PageTemplate getPageTemplate(Folder folder, String name) throws PageNotFoundException, NodeException
    {
        // perform lookup by path so that cache can be used
        String pageTemplatePath = folder.getPath() + Folder.PATH_SEPARATOR + name;
        try
        {
            return getPageTemplate(pageTemplatePath);
        }
        catch (PageNotFoundException pnfe)
        {
            throw pnfe;
        }
        catch (Exception e)
        {
            throw new PageNotFoundException("Page template " + pageTemplatePath + " not found.", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getDynamicPages(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getDynamicPages(Folder folder) throws NodeException
    {
        FolderImpl folderImpl = (FolderImpl)folder;

        // perform lookup of folder dynamic pages collection and cache in folder
        try
        {
            // query for dynamic pages
            Criteria filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            QueryByCriteria query = QueryFactory.newQuery(DynamicPageImpl.class, filter);
            Collection dynamicPages = getPersistenceBrokerTemplate().getCollectionByQuery(query);

            // cache dynamic pages in folder
            folderImpl.accessDynamicPages().clear();
            if (dynamicPages != null)
            {
                folderImpl.accessDynamicPages().addAll(dynamicPages);
            }
            folderImpl.resetDynamicPages(true);
        }
        catch (Exception e)
        {
            // reset cache in folder
            folderImpl.resetDynamicPages(false);
            throw new NodeException("Unable to access dynamic pages for folder " + folder.getPath() + ".");
        }

        // folder dynamic pages cache populated, get dynamic pages
        // from folder to provide packaging as filtered node set
        return folder.getDynamicPages();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getDynamicPage(org.apache.jetspeed.om.folder.Folder, java.lang.String)
     */
    public DynamicPage getDynamicPage(Folder folder, String name) throws PageNotFoundException, NodeException
    {
        // perform lookup by path so that cache can be used
        String dynamicPagePath = folder.getPath() + Folder.PATH_SEPARATOR + name;
        try
        {
            return getDynamicPage(dynamicPagePath);
        }
        catch (PageNotFoundException pnfe)
        {
            throw pnfe;
        }
        catch (Exception e)
        {
            throw new PageNotFoundException("Dynamic page " + dynamicPagePath + " not found.", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFragmentDefinitions(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getFragmentDefinitions(Folder folder) throws NodeException
    {
        FolderImpl folderImpl = (FolderImpl)folder;

        // perform lookup of folder fragment definition collection and cache in folder
        try
        {
            // query for fragment definition
            Criteria filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            QueryByCriteria query = QueryFactory.newQuery(FragmentDefinitionImpl.class, filter);
            Collection fragmentDefinitions = getPersistenceBrokerTemplate().getCollectionByQuery(query);

            // cache fragment definition in folder
            folderImpl.accessFragmentDefinitions().clear();
            if (fragmentDefinitions != null)
            {
                folderImpl.accessFragmentDefinitions().addAll(fragmentDefinitions);
            }
            folderImpl.resetFragmentDefinitions(true);
        }
        catch (Exception e)
        {
            // reset cache in folder
            folderImpl.resetFragmentDefinitions(false);
            throw new NodeException("Unable to access fragment definition for folder " + folder.getPath() + ".");
        }

        // folder fragment definition cache populated, get fragment definition
        // from folder to provide packaging as filtered node set
        return folder.getFragmentDefinitions();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFragmentDefinition(org.apache.jetspeed.om.folder.Folder, java.lang.String)
     */
    public FragmentDefinition getFragmentDefinition(Folder folder, String name) throws PageNotFoundException, NodeException
    {
        // perform lookup by path so that cache can be used
        String fragmentDefinitionPath = folder.getPath() + Folder.PATH_SEPARATOR + name;
        try
        {
            return getFragmentDefinition(fragmentDefinitionPath);
        }
        catch (PageNotFoundException pnfe)
        {
            throw pnfe;
        }
        catch (Exception e)
        {
            throw new PageNotFoundException("Fragment definition " + fragmentDefinitionPath + " not found.", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLinks(org.apache.jetspeed.om.folder.Folder)
     */    
    public NodeSet getLinks(Folder folder) throws NodeException
    {
        FolderImpl folderImpl = (FolderImpl)folder;

        // perform lookup of folder links collection and cache in folder
        try
        {
            // query for links
            Criteria filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            QueryByCriteria query = QueryFactory.newQuery(LinkImpl.class, filter);
            Collection links = getPersistenceBrokerTemplate().getCollectionByQuery(query);

            // cache links in folder
            folderImpl.accessLinks().clear();
            if (links != null)
            {
                folderImpl.accessLinks().addAll(links);
            }
            folderImpl.resetLinks(true);
        }
        catch (Exception e)
        {
            // reset cache in folder
            folderImpl.resetLinks(false);
            throw new NodeException("Unable to access links for folder " + folder.getPath() + ".");
        }

        // folder links cache populated, get links from folder
        // to provide packaging as filtered node set
        return folder.getLinks();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLink(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */    
    public Link getLink(Folder folder, String name) throws DocumentNotFoundException, NodeException
    {
        // perform lookup by path so that cache can be used
        String linkPath = folder.getPath() + Folder.PATH_SEPARATOR + name;
        try
        {
            return getLink(linkPath);
        }
        catch (DocumentNotFoundException dnfe)
        {
            throw dnfe;
        }
        catch (Exception e)
        {
            throw new DocumentNotFoundException("Link " + linkPath + " not found.", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageSecurity(org.apache.jetspeed.om.folder.Folder)
     */    
    public PageSecurity getPageSecurity(Folder folder) throws DocumentNotFoundException, NodeException
    {
        FolderImpl folderImpl = (FolderImpl)folder;

        // perform lookup of page security document and cache
        // in folder; limit lookup to root folder since page
        // security document is currently supported only as a
        // root folder singleton
        if (folder.getPath().equals(Folder.PATH_SEPARATOR))
        {
            try
            {
                // query for page security
                Criteria filter = new Criteria();
                filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
                QueryByCriteria query = QueryFactory.newQuery(PageSecurityImpl.class, filter);
                PageSecurity document = (PageSecurity)getPersistenceBrokerTemplate().getObjectByQuery(query);

                // cache page security in folder
                folderImpl.resetPageSecurity((PageSecurityImpl)document, true);
            }
            catch (Exception e)
            {
                // reset page security in folder
                folderImpl.resetPageSecurity(null, true);
                throw new NodeException("Unable to access page security for folder " + folder.getPath() + ".");
            }
        }
        else
        {
            // cache page security in folder
            folderImpl.resetPageSecurity(null, true);
        }

        // folder page security instance cache populated, get
        // instance from folder to provide security checks
        return folder.getPageSecurity();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getAll(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getAll(Folder folder) throws DocumentException
    {
        FolderImpl folderImpl = (FolderImpl)folder;

        // perform lookup of folder nodes collection and cache in folder
        try
        {
            // query for all nodes
            List all = DatabasePageManagerUtils.createList();
            
            // query for subfolders
            Criteria filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            QueryByCriteria query = QueryFactory.newQuery(FolderImpl.class, filter);
            Collection folders = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (folders != null)
            {
                all.addAll(folders);
            }

            // polymorphic query for pages, page templates, dynamic pages,
            // and fragment definitions
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(BaseFragmentsElementImpl.class, filter);
            Collection baseFragments = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (baseFragments != null)
            {
                all.addAll(baseFragments);
            }

            // query for links
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(LinkImpl.class, filter);
            Collection links = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (links != null)
            {
                all.addAll(links);
            }

            // query for page security
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(PageSecurityImpl.class, filter);
            PageSecurity document = (PageSecurity)getPersistenceBrokerTemplate().getObjectByQuery(query);
            if (document != null)
            {
                all.add(document);
            }

            // cache links in folder
            folderImpl.accessAll().clear();
            folderImpl.accessAll().addAll(all);
            folderImpl.resetAll(true);
        }
        catch (Exception e)
        {
            // reset cache in folder
            folderImpl.resetAll(false);
            throw new DocumentException("Unable to access all nodes for folder " + folder.getPath() + ".");
        }

        // folder all nodes cache populated, get all from folder
        // to provide packaging as filtered node set
        return folder.getAll();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws NodeException, PageNotUpdatedException
    {
        // dereference page in case proxy is supplied
        page = (Page)ProxyHelper.getRealObject(page);

        // update page
        boolean newPage[] = new boolean[]{false};
        FolderImpl parentFolder = updateFragmentsElement(page, newPage);

        // reset parent folder in case page is new or
        // parent is holding an out of date copy of
        // this page that was removed from the cache
        // before this one was accessed
        if (parentFolder != null)
        {
            parentFolder.resetPages(false);            
        }

        // notify page manager listeners
        if (newPage[0])
        {
            delegator.notifyNewNode(page);
        }
        else
        {
            delegator.notifyUpdatedNode(page);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updatePageTemplate(org.apache.jetspeed.om.page.PageTemplate)
     */
    public void updatePageTemplate(PageTemplate pageTemplate) throws NodeException, PageNotUpdatedException
    {
        // dereference page template in case proxy is supplied
        pageTemplate = (PageTemplate)ProxyHelper.getRealObject(pageTemplate);

        // update page template
        boolean newPageTemplate[] = new boolean[]{false};
        FolderImpl parentFolder = updateFragmentsElement(pageTemplate, newPageTemplate);

        // reset parent folder in case page is new or
        // parent is holding an out of date copy of
        // this page that was removed from the cache
        // before this one was accessed
        if (parentFolder != null)
        {
            parentFolder.resetPageTemplates(false);
        }

        // notify page manager listeners
        if (newPageTemplate[0])
        {
            delegator.notifyNewNode(pageTemplate);
        }
        else
        {
            delegator.notifyUpdatedNode(pageTemplate);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateDynamicPage(org.apache.jetspeed.om.page.DynamicPage)
     */
    public void updateDynamicPage(DynamicPage dynamicPage) throws NodeException, PageNotUpdatedException
    {
        // dereference dynamic page in case proxy is supplied
        dynamicPage = (DynamicPage)ProxyHelper.getRealObject(dynamicPage);

        // update dynamic page
        boolean newDynamicPage[] = new boolean[]{false};
        FolderImpl parentFolder = updateFragmentsElement(dynamicPage, newDynamicPage);

        // reset parent folder in case page is new or
        // parent is holding an out of date copy of
        // this page that was removed from the cache
        // before this one was accessed
        if (parentFolder != null)
        {
            parentFolder.resetDynamicPages(false);
        }

        // notify page manager listeners
        if (newDynamicPage[0])
        {
            delegator.notifyNewNode(dynamicPage);            
        }
        else
        {
            delegator.notifyUpdatedNode(dynamicPage);            
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateFragmentDefinition(org.apache.jetspeed.om.page.FragmentDefinition)
     */
    public void updateFragmentDefinition(FragmentDefinition fragmentsDefinition) throws NodeException, PageNotUpdatedException
    {
        // dereference fragment definition in case proxy is supplied
        fragmentsDefinition = (FragmentDefinition)ProxyHelper.getRealObject(fragmentsDefinition);

        // update fragment definition
        boolean newFragmentDefinition[] = new boolean[]{false};
        FolderImpl parentFolder = updateFragmentsElement(fragmentsDefinition, newFragmentDefinition);

        // reset parent folder in case page is new or
        // parent is holding an out of date copy of
        // this page that was removed from the cache
        // before this one was accessed
        if (parentFolder != null)
        {
            parentFolder.resetFragmentDefinitions(false);
        }

        // notify page manager listeners
        if (newFragmentDefinition[0])
        {
            delegator.notifyNewNode(fragmentsDefinition);            
        }
        else
        {
            delegator.notifyUpdatedNode(fragmentsDefinition);            
        }
    }
    
    /**
     * Update fragments/page element.
     * 
     * @param fragmentsElement fragments/page element to update
     * @param newFragmentsElement new fragments/page element flag
     * @return parent folder
     * @throws NodeException
     * @throws PageNotUpdatedException
     */
    protected FolderImpl updateFragmentsElement(BaseFragmentsElement fragmentsElement, boolean [] newFragmentsElement) throws NodeException, PageNotUpdatedException
    {
        try
        {
            // validate fragments element
            BaseFragmentsElementImpl fragmentsElementImpl = (BaseFragmentsElementImpl)fragmentsElement;
            if (!fragmentsElementImpl.validateFragments())
            {
                throw new PageNotUpdatedException("Fragments hierarchy invalid for fragments/page: " + fragmentsElement.getPath() + ", not updated.");
            }
            
            // look up and set parent folder if necessary
            FolderImpl parent = (FolderImpl)fragmentsElement.getParent();
            if (parent == null)
            {
                // access folder by path
                String pageElementPath = fragmentsElement.getPath();
                String parentPath = pageElementPath.substring(0, pageElementPath.lastIndexOf(Folder.PATH_SEPARATOR));
                if (parentPath.length() == 0)
                {
                    parentPath = Folder.PATH_SEPARATOR;
                }
                try
                {
                    parent = (FolderImpl)getFolder(parentPath);                    
                }
                catch (FolderNotFoundException fnfe)
                {
                    throw new PageNotUpdatedException("Missing parent folder: " + parentPath);
                }
                
                // check for edit access on parent folder; fragments/page
                // access not checked on create
                parent.checkAccess(JetspeedActions.EDIT);

                // update fragments/page and mark cache transaction
                fragmentsElement.setParent(parent);
                storeEntity(fragmentsElement, pageElementPath, true);
                
                // new fragments/page
                newFragmentsElement[0] = true;
            }
            else
            {
                // check for edit access on fragments/page and parent folder
                fragmentsElement.checkAccess(JetspeedActions.EDIT);

                // update fragments/page and mark cache transaction
                storeEntity(fragmentsElement, fragmentsElement.getPath(), false);

                // updated fragments/page
                newFragmentsElement[0] = false;
            }
            
            // return parent folder to update is caches after update
            return parent;
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
            throw new PageNotUpdatedException("Fragments/page element " + fragmentsElement.getPath() + " not updated.", e);
        }        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page) throws NodeException, PageNotRemovedException
    {
        // dereference page in case proxy is supplied
        page = (Page)ProxyHelper.getRealObject(page);

        // remove page
        FolderImpl parentFolder = removeFragmentsElement(page);

        // reset parent folder holding removed page
        parentFolder.resetPages(false);            

        // notify page manager listeners
        delegator.notifyRemovedNode(page);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePageTemplate(org.apache.jetspeed.om.page.PageTemplate)
     */
    public void removePageTemplate(PageTemplate pageTemplate) throws NodeException, PageNotRemovedException
    {
        // dereference page template in case proxy is supplied
        pageTemplate = (PageTemplate)ProxyHelper.getRealObject(pageTemplate);

        // remove page template
        FolderImpl parentFolder = removeFragmentsElement(pageTemplate);

        // reset parent folder holding removed page
        parentFolder.resetPages(false);            

        // notify page manager listeners
        delegator.notifyRemovedNode(pageTemplate);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeDynamicPage(org.apache.jetspeed.om.page.DynamicPage)
     */
    public void removeDynamicPage(DynamicPage dynamicPage) throws NodeException, PageNotRemovedException
    {
        // dereference dynamic page in case proxy is supplied
        dynamicPage = (DynamicPage)ProxyHelper.getRealObject(dynamicPage);

        // remove dynamic page
        FolderImpl parentFolder = removeFragmentsElement(dynamicPage);

        // reset parent folder holding removed page
        parentFolder.resetPages(false);            

        // notify page manager listeners
        delegator.notifyRemovedNode(dynamicPage);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeFragmentDefinition(org.apache.jetspeed.om.page.FragmentDefinition)
     */
    public void removeFragmentDefinition(FragmentDefinition fragmentDefinition) throws NodeException, PageNotRemovedException
    {
        // dereference fragment definition in case proxy is supplied
        fragmentDefinition = (FragmentDefinition)ProxyHelper.getRealObject(fragmentDefinition);

        // remove fragment definition
        FolderImpl parentFolder = removeFragmentsElement(fragmentDefinition);

        // reset parent folder holding removed page
        parentFolder.resetPages(false);            

        // notify page manager listeners
        delegator.notifyRemovedNode(fragmentDefinition);
    }

    /**
     * Remove fragments/page element.
     * 
     * @param fragmentsElement fragments/page element to remove
     * @return parent folder
     * @throws NodeException
     * @throws PageNotUpdatedException
     */
    protected FolderImpl removeFragmentsElement(BaseFragmentsElement fragmentsElement) throws NodeException, PageNotRemovedException
    {
        try
        {
            // check for edit access on fragments/page and parent folder
            fragmentsElement.checkAccess(JetspeedActions.EDIT);

            // look up and update parent folder if necessary
            FolderImpl parent = (FolderImpl)ProxyHelper.getRealObject(fragmentsElement.getParent());
                
            // delete fragments/page
            getPersistenceBrokerTemplate().delete(fragmentsElement);

            // return parent folder to update is caches after remove
            return parent;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new PageNotRemovedException("Fragments/page element " + fragmentsElement.getPath() + " not removed.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void updateFolder(Folder folder) throws NodeException, FolderNotUpdatedException
    {
        // shallow update by default
        updateFolder(folder, false);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateFolder(org.apache.jetspeed.om.folder.Folder,boolean)
     */
    public void updateFolder(Folder folder, boolean deep) throws NodeException, FolderNotUpdatedException
    {
        try
        {
            // dereference folder in case proxy is supplied
            folder = (Folder)ProxyHelper.getRealObject(folder);
            FolderImpl folderImpl = (FolderImpl)folder;

            // look up and set parent folder if required
            FolderImpl parent = (FolderImpl)folder.getParent();
            if ((parent == null) && !folder.getPath().equals(Folder.PATH_SEPARATOR))
            {
                // access folder by path
                String folderPath = folder.getPath();
                String parentPath = folderPath.substring(0, folderPath.lastIndexOf(Folder.PATH_SEPARATOR));
                if (parentPath.length() == 0)
                {
                    parentPath = Folder.PATH_SEPARATOR;
                }
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

                // update folder and mark cache transaction
                folder.setParent(parent);
                storeEntity(folder, folderPath, true);

                // reset parent folder folders cache
                parent.resetFolders(false);

                // notify page manager listeners
                delegator.notifyNewNode(folder);
            }
            else
            {
                // determine if folder is new by checking autoincrement id
                boolean newFolder = (folderImpl.getIdentity() == 0);

                // check for edit access on folder and parent folder
                // if not being initially created; access is not
                // checked on create
                String folderPath = folder.getPath();
                if (!newFolder || !folderPath.equals(Folder.PATH_SEPARATOR))
                {
                    folder.checkAccess(JetspeedActions.EDIT);
                }

                // create root folder or update folder and mark cache transaction
                storeEntity(folder, folderPath);
                if (newFolder && (folderImpl.getIdentity() != 0))
                {
                    DatabasePageManagerCache.addTransaction(new TransactionedOperation(folderPath, TransactionedOperation.ADD_OPERATION));
                }
                else
                {
                    DatabasePageManagerCache.addTransaction(new TransactionedOperation(folderPath, TransactionedOperation.UPDATE_OPERATION));
                }

                // reset parent folder folders cache in case
                // parent is holding an out of date copy of
                // this folder that was removed from the cache
                // before this one was accessed
                if (parent != null)
                {
                    parent.resetFolders(false);
                }

                // notify page manager listeners
                if (newFolder && (folderImpl.getIdentity() != 0))
                {
                    delegator.notifyNewNode(folder);
                }
                else
                {
                    delegator.notifyUpdatedNode(folder);
                }
            }

            // update deep recursively if specified
            if (deep)
            {
                // update recursively, (breadth first)
                updateFolderNodes(folderImpl);
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

    /**
     * updateFolderNodes - recursively update all folder nodes
     *
     * @param folderImpl folder whose nodes are to be updated
     * @param throws FolderNotUpdatedException
     */
    private void updateFolderNodes(FolderImpl folderImpl) throws FolderNotUpdatedException
    {
        try
        {
            // update pages
            Criteria filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            QueryByCriteria query = QueryFactory.newQuery(PageImpl.class, filter);
            Collection pages = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (pages != null)
            {
                Iterator pagesIter = pages.iterator();
                while (pagesIter.hasNext())
                {
                    updatePage((Page)pagesIter.next());
                }
            }

            // update page templates
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(PageTemplateImpl.class, filter);
            Collection pageTemplates = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (pageTemplates != null)
            {
                Iterator pageTemplatesIter = pageTemplates.iterator();
                while (pageTemplatesIter.hasNext())
                {
                    updatePageTemplate((PageTemplate)pageTemplatesIter.next());
                }
            }

            // update dynamic pages
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(DynamicPageImpl.class, filter);
            Collection dynamicPages = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (dynamicPages != null)
            {
                Iterator dynamicPagesIter = dynamicPages.iterator();
                while (dynamicPagesIter.hasNext())
                {
                    updateDynamicPage((DynamicPage)dynamicPagesIter.next());
                }
            }

            // update fragment definitions
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(FragmentDefinitionImpl.class, filter);
            Collection fragmentDefinitions = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (fragmentDefinitions != null)
            {
                Iterator fragmentDefinitionsIter = fragmentDefinitions.iterator();
                while (fragmentDefinitionsIter.hasNext())
                {
                    updateFragmentDefinition((FragmentDefinition)fragmentDefinitionsIter.next());
                }
            }

            // update links
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(LinkImpl.class, filter);
            Collection links = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (links != null)
            {
                Iterator linksIter = links.iterator();
                while (linksIter.hasNext())
                {
                    updateLink((Link)linksIter.next());
                }
            }

            // update page security
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(PageSecurityImpl.class, filter);
            PageSecurity document = (PageSecurity)getPersistenceBrokerTemplate().getObjectByQuery(query);
            if (document != null)
            {
                updatePageSecurity(document);
            }

            // update folders last: breadth first recursion
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(FolderImpl.class, filter);
            Collection folders = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (folders != null)
            {
                Iterator foldersIter = folders.iterator();
                while (foldersIter.hasNext())
                {
                    updateFolder((Folder)foldersIter.next(), true);
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
            throw new FolderNotUpdatedException("Folder " + folderImpl.getPath() + " not updated.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void removeFolder(Folder folder) throws NodeException, FolderNotRemovedException
    {
        try
        {
            // dereference folder in case proxy is supplied
            folder = (Folder)ProxyHelper.getRealObject(folder);

            // check for edit access on folder and parent folder
            folder.checkAccess(JetspeedActions.EDIT);

            // reset folder nodes cache
            ((FolderImpl)folder).resetAll(false);

            // remove recursively, (depth first)
            removeFolderNodes((FolderImpl)folder);

            // look up and update parent folder if necessary
            if (folder.getParent() != null)
            {
                FolderImpl parent = (FolderImpl)ProxyHelper.getRealObject(folder.getParent());

                // delete folder
                getPersistenceBrokerTemplate().delete(folder);

                // reset parent folder folders cache
                parent.resetFolders(false);
            }
            else
            {
                // delete folder: depth recursion
                getPersistenceBrokerTemplate().delete(folder);
            }

            // notify page manager listeners
            delegator.notifyRemovedNode((FolderImpl)folder);
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

    /**
     * removeFolderNodes - recursively remove all folder nodes
     *
     * @param folderImpl folder whose nodes are to be removed
     * @param throws FolderNotRemovedException
     */
    private void removeFolderNodes(FolderImpl folderImpl) throws FolderNotRemovedException
    {
        try
        {
            // remove folders first: depth first recursion
            Criteria filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            QueryByCriteria query = QueryFactory.newQuery(FolderImpl.class, filter);
            Collection folders = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (folders != null)
            {
                Iterator foldersIter = folders.iterator();
                while (foldersIter.hasNext())
                {
                    removeFolder((Folder)foldersIter.next());
                }
            }

            // remove pages
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(PageImpl.class, filter);
            Collection pages = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (pages != null)
            {
                Iterator pagesIter = pages.iterator();
                while (pagesIter.hasNext())
                {
                    removePage((Page)pagesIter.next());
                }
            }

            // remove page templates
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(PageTemplateImpl.class, filter);
            Collection pageTemplates = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (pageTemplates != null)
            {
                Iterator pageTemplatesIter = pageTemplates.iterator();
                while (pageTemplatesIter.hasNext())
                {
                    removePageTemplate((PageTemplate)pageTemplatesIter.next());
                }
            }

            // remove dynamic pages
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(DynamicPageImpl.class, filter);
            Collection dynamicPages = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (dynamicPages != null)
            {
                Iterator dynamicPagesIter = dynamicPages.iterator();
                while (dynamicPagesIter.hasNext())
                {
                    removeDynamicPage((DynamicPage)dynamicPagesIter.next());
                }
            }

            // remove fragment definitions
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(FragmentDefinitionImpl.class, filter);
            Collection fragmentDefinitions = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (fragmentDefinitions != null)
            {
                Iterator fragmentDefinitionsIter = fragmentDefinitions.iterator();
                while (fragmentDefinitionsIter.hasNext())
                {
                    removeFragmentDefinition((FragmentDefinition)fragmentDefinitionsIter.next());
                }
            }

            // remove links
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(LinkImpl.class, filter);
            Collection links = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            if (links != null)
            {
                Iterator linksIter = links.iterator();
                while (linksIter.hasNext())
                {
                    removeLink((Link)linksIter.next());
                }
            }

            // remove page security
            filter = new Criteria();
            filter.addEqualTo("parent", new Integer(folderImpl.getIdentity()));
            query = QueryFactory.newQuery(PageSecurityImpl.class, filter);
            PageSecurity document = (PageSecurity)getPersistenceBrokerTemplate().getObjectByQuery(query);
            if (document != null)
            {
                removePageSecurity(document);
            }
        }
        catch (FolderNotRemovedException fnre)
        {
            throw fnre;
        }
        catch (SecurityException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new FolderNotRemovedException("Folder " + folderImpl.getPath() + " not removed.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateLink(org.apache.jetspeed.om.page.Link)
     */
    public void updateLink(Link link) throws NodeException, LinkNotUpdatedException
    {
        try
        {
            // dereference link in case proxy is supplied
            link = (Link)ProxyHelper.getRealObject(link);

            // look up and set parent folder if necessary
            boolean newLink = false;
            FolderImpl parent = (FolderImpl)link.getParent();
            if (parent == null)
            {
                // access folder by path
                String linkPath = link.getPath();
                String parentPath = linkPath.substring(0, linkPath.lastIndexOf(Folder.PATH_SEPARATOR));
                if (parentPath.length() == 0)
                {
                    parentPath = Folder.PATH_SEPARATOR;
                }
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

                // update link and mark cache transaction
                link.setParent(parent);
                storeEntity(link, linkPath, true);

                // new link
                newLink = true;
            }
            else
            {
                // check for edit access on link and parent folder
                link.checkAccess(JetspeedActions.EDIT);

                // update link and mark cache transaction
                storeEntity(link, link.getPath(), false);
                
                // update link
                newLink = false;
            }

            // reset parent folder links cache in case
            // new or parent is holding an out of date copy
            // of this link that was removed from the cache
            // before this one was accessed
            parent.resetLinks(false);

            // notify page manager listeners
            if (newLink)
            {
                delegator.notifyNewNode(link);
            }
            else
            {
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
    public void removeLink(Link link) throws NodeException, LinkNotRemovedException
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
                FolderImpl parent = (FolderImpl)ProxyHelper.getRealObject(link.getParent());

                // delete link
                getPersistenceBrokerTemplate().delete(link);

                // reset parent folder links cache
                parent.resetLinks(false);
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
    public void updatePageSecurity(PageSecurity pageSecurity) throws NodeException, FailedToUpdateDocumentException
    {
        try
        {
            // dereference document in case proxy is supplied
            pageSecurity = (PageSecurity)ProxyHelper.getRealObject(pageSecurity);

            // look up and set parent folder if necessary
            boolean newPageSecurity = false;
            FolderImpl parent = (FolderImpl)pageSecurity.getParent();
            if (parent == null)
            {
                // access folder by path
                String pageSecurityPath = pageSecurity.getPath();
                String parentPath = pageSecurityPath.substring(0, pageSecurityPath.lastIndexOf(Folder.PATH_SEPARATOR));
                if (parentPath.length() == 0)
                {
                    parentPath = Folder.PATH_SEPARATOR;
                }
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
                    parent.getPageSecurity();
                    throw new FailedToUpdateDocumentException("Parent folder page security exists: " + parentPath);
                }
                catch (DocumentNotFoundException dnfe)
                {
                    // check for edit access on parent folder; document
                    // access not checked on create
                    parent.checkAccess(JetspeedActions.EDIT);
                    
                    // update document and mark cache transaction
                    pageSecurity.setParent(parent);
                    storeEntity(pageSecurity, pageSecurityPath, true);

                    // new page security
                    newPageSecurity = true;
                }
                catch (Exception e)
                {
                    throw new FailedToUpdateDocumentException("Parent folder page security exists: " + parentPath);
                }
            }
            else
            {
                // check for edit access on document and parent folder
                pageSecurity.checkAccess(JetspeedActions.EDIT);

                // update document and mark cache transaction
                storeEntity(pageSecurity, pageSecurity.getPath(), false);

                // update page security
                newPageSecurity = false;
            }

            // reset parent folder page security cache if new or
            // in case parent is holding an out of date copy of
            // this page security that was removed from the cache
            // before this one was accessed
            parent.resetPageSecurity((PageSecurityImpl)pageSecurity, true);

            // reset all cached security constraints
            DatabasePageManagerCache.resetCachedSecurityConstraints();

            // notify page manager listeners
            if (newPageSecurity)
            {
                delegator.notifyNewNode(pageSecurity);                
            }
            else
            {
                delegator.notifyUpdatedNode(pageSecurity);
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
            throw new FailedToUpdateDocumentException("Document " + pageSecurity.getPath() + " not updated.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public void removePageSecurity(PageSecurity pageSecurity) throws NodeException, FailedToDeleteDocumentException
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
                FolderImpl parent = (FolderImpl)ProxyHelper.getRealObject(pageSecurity.getParent());

                // delete document
                getPersistenceBrokerTemplate().delete(pageSecurity);

                // reset parent folder page security cache
                parent.resetPageSecurity(null, true);
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
    
    /**
     * Add or update persistence object by storing to persistence broker.
     *  
     * @param node node to store
     * @param path node path
     */
    private void storeEntity(Object node, String path)
    {
        // store object for add/update and interoperate with cache
        // to signal update operations
        DatabasePageManagerCache.addUpdatePath(path);
        try
        {
            getPersistenceBrokerTemplate().store(node);
        }
        finally
        {
            DatabasePageManagerCache.removeUpdatePath(path);
        }
    }

    /**
     * Add or update persistence object by storing to persistence broker
     * with thread cache transaction management.
     *  
     * @param node node to store
     * @param path node path
     * @param add whether transaction operation is add or update
     */
    private void storeEntity(Object node, String path, boolean add)
    {
        // store object for add/update and interoperate with cache
        // to signal update operations and record thread transactions
        storeEntity(node, path);
        DatabasePageManagerCache.addTransaction(new TransactionedOperation(path, (add ? TransactionedOperation.ADD_OPERATION : TransactionedOperation.UPDATE_OPERATION)));
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPage(org.apache.jetspeed.om.page.Page,java.lang.String)
     */
    public Page copyPage(Page source, String path)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyPage(source, path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPage(org.apache.jetspeed.om.page.Page, java.lang.String, boolean)
     */
    public Page copyPage(Page source, String path, boolean copyIds)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyPage(source, path, copyIds);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPageTemplate(org.apache.jetspeed.om.page.PageTemplate, java.lang.String)
     */
    public PageTemplate copyPageTemplate(PageTemplate source, String path)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyPageTemplate(source, path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPageTemplate(org.apache.jetspeed.om.page.PageTemplate, java.lang.String, boolean)
     */
    public PageTemplate copyPageTemplate(PageTemplate source, String path, boolean copyIds)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyPageTemplate(source, path, copyIds);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyDynamicPage(org.apache.jetspeed.om.page.DynamicPage, java.lang.String)
     */
    public DynamicPage copyDynamicPage(DynamicPage source, String path)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyDynamicPage(source, path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyDynamicPage(org.apache.jetspeed.om.page.DynamicPage, java.lang.String, boolean)
     */
    public DynamicPage copyDynamicPage(DynamicPage source, String path, boolean copyIds)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyDynamicPage(source, path, copyIds);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyFragmentDefinition(org.apache.jetspeed.om.page.FragmentDefinition, java.lang.String)
     */
    public FragmentDefinition copyFragmentDefinition(FragmentDefinition source, String path)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyFragmentDefinition(source, path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyFragmentDefinition(org.apache.jetspeed.om.page.FragmentDefinition, java.lang.String, boolean)
     */
    public FragmentDefinition copyFragmentDefinition(FragmentDefinition source, String path, boolean copyIds)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyFragmentDefinition(source, path, copyIds);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyLink(org.apache.jetspeed.om.page.Link,java.lang.String)
     */
    public Link copyLink(Link source, String path)
    throws NodeException, LinkNotUpdatedException
    {
        return this.delegator.copyLink(source, path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyFolder(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */
    public Folder copyFolder(Folder source, String path)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyFolder(source, path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyFragment(org.apache.jetspeed.om.page.BaseFragmentElement,java.lang.String)
     */
    public BaseFragmentElement copyFragment(BaseFragmentElement source, String name)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyFragment(source, name);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyFragment(org.apache.jetspeed.om.page.BaseFragmentElement, java.lang.String, boolean)
     */
    public BaseFragmentElement copyFragment(BaseFragmentElement source, String name, boolean copyIds)
    throws NodeException, PageNotUpdatedException
    {
        return this.delegator.copyFragment(source, name, copyIds);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public PageSecurity copyPageSecurity(PageSecurity source) 
    throws NodeException
    {
        return this.delegator.copyPageSecurity(source);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getUserPage(java.lang.String,java.lang.String)
     */
    public Page getUserPage(String userName, String pageName)
    throws PageNotFoundException, NodeException
    {
        return this.getPage(Folder.USER_FOLDER + userName + Folder.PATH_SEPARATOR + pageName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getUserFolder(java.lang.String)
     */
    public Folder getUserFolder(String userName) 
        throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        return this.getFolder(Folder.USER_FOLDER + userName);        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#folderExists(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#pageExists(java.lang.String)
     */
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
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#pageTemplateExists(java.lang.String)
     */
    public boolean pageTemplateExists(String pageName)
    {
        try
        {
            getPageTemplate(pageName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#dynamicPageExists(java.lang.String)
     */
    public boolean dynamicPageExists(String pageName)
    {
        try
        {
            getDynamicPage(pageName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#fragmentDefinitionExists(java.lang.String)
     */
    public boolean fragmentDefinitionExists(String name)
    {
        try
        {
            getFragmentDefinition(name);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#linkExists(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#userFolderExists(java.lang.String)
     */
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
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#userPageExists(java.lang.String)
     */
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
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#createUserHomePagesFromRoles(java.security.auth.Subject)
     */
    public void createUserHomePagesFromRoles(Subject subject)
    throws NodeException
    {
        PageManagerUtils.createUserHomePagesFromRoles(this, subject);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#deepCopyFolder(org.apache.jetspeed.om.folder.Folder, java.lang.String, java.lang.String)
     */
    public void deepCopyFolder(Folder srcFolder, String destinationPath, String owner)
    throws NodeException, PageNotUpdatedException
    {
        deepCopyFolder(srcFolder, destinationPath, owner, false);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#deepCopyFolder(org.apache.jetspeed.om.folder.Folder, java.lang.String, java.lang.String, boolean)
     */
    public void deepCopyFolder(Folder srcFolder, String destinationPath, String owner, boolean copyIds)
    throws NodeException, PageNotUpdatedException
    {
        PageManagerUtils.deepCopyFolder(this, srcFolder, destinationPath, owner, copyIds);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#deepMergeFolder(org.apache.jetspeed.om.folder.Folder, java.lang.String, java.lang.String)
     */
    public void deepMergeFolder(Folder srcFolder, String destinationPath, String owner)
    throws NodeException, PageNotUpdatedException
    {
        deepMergeFolder(srcFolder, destinationPath, owner, false);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#deepMergeFolder(org.apache.jetspeed.om.folder.Folder, java.lang.String, java.lang.String, boolean)
     */
    public void deepMergeFolder(Folder srcFolder, String destinationPath, String owner, boolean copyIds)
    throws NodeException, PageNotUpdatedException
    {
        PageManagerUtils.deepMergeFolder(this, srcFolder, destinationPath, owner, copyIds);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#addPages(org.apache.jetspeed.om.page.Page[])
     */
    public int addPages(Page[] pages)
    throws NodeException
    {   
        if (pages.length > 0 && pages[0].getPath().equals("/tx__test1.psml"))
        {
            // for tx testing
            System.out.println("Adding first page");
            this.updatePage(pages[0]);
            System.out.println("Adding second page");
            this.updatePage(pages[1]);
            System.out.println("About to throw ex");
            throw new NodeException("Its gonna blow captain!");
        }
        for (int ix = 0; ix < pages.length; ix++)
        {
            this.updatePage(pages[ix]);
        }
        return pages.length;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#isDistributed()
     */
    public boolean isDistributed()
    {
        return DatabasePageManagerCache.isDistributed();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#notifyUpdatedNode(org.apache.jetspeed.page.document.Node)
     */
    public void notifyUpdatedNode(Node node)
    {
        // notify page manager listeners
        delegator.notifyUpdatedNode(node);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#cleanupRequestCache()
     */
    public void cleanupRequestCache()
    {
        // clear thread local cache on request completion
        fragmentPropertyListsCache.remove();
    }
    
    /**
     * Get and cache fragment property list for specified fragment.
     * 
     * @param baseFragmentElement owning fragment of fragment property list
     * @return new or cached fragment property list
     */
    public FragmentPropertyList getFragmentPropertiesList(BaseFragmentElementImpl baseFragmentElement, FragmentPropertyList transientList)
    {
        // access thread local fragment property lists cache
        String threadLocalCacheKey = getFragmentPropertiesListThreadLocalCacheKey(baseFragmentElement);
        Map threadLocalCache = (Map)fragmentPropertyListsCache.get();

        // get cached persistent list
        FragmentPropertyList list = ((threadLocalCache != null) ? (FragmentPropertyList)threadLocalCache.get(threadLocalCacheKey) : null);
        if (list == null)
        {
            // use transient list or create new fragment property list
            list = ((transientList != null) ? transientList : new FragmentPropertyList(baseFragmentElement));
            
            // build fragment properties database query
            Criteria filter = new Criteria();
            filter.addEqualTo("fragment", new Integer(baseFragmentElement.getIdentity()));
            Criteria scopesFilter = new Criteria();
            Criteria globalScopeFilter = new Criteria();
            globalScopeFilter.addIsNull("scope");
            scopesFilter.addOrCriteria(globalScopeFilter);
            // add scopes for current user, groups, and roles
            Subject subject = JSSubject.getSubject(AccessController.getContext());
            if (subject != null)
            {
                if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                {
                    Set principals = subject.getPrincipals();
                    Iterator principalsIter = principals.iterator();
                    while (principalsIter.hasNext())
                    {
                        Principal principal = (Principal)principalsIter.next();
                        if (principal instanceof User)
                        {
                            Criteria userScopeFilter = new Criteria();
                            userScopeFilter.addEqualTo("scope", FragmentProperty.USER_PROPERTY_SCOPE);
                            userScopeFilter.addEqualTo("scopeValue", principal.getName());
                            scopesFilter.addOrCriteria(userScopeFilter);
                        }
                        else if (principal instanceof Group)
                        {
                            Criteria groupScopeFilter = new Criteria();
                            groupScopeFilter.addEqualTo("scope", FragmentProperty.GROUP_PROPERTY_SCOPE);
                            groupScopeFilter.addEqualTo("scopeValue", principal.getName());
                            scopesFilter.addOrCriteria(groupScopeFilter);
                        }
                        else if (principal instanceof Role)
                        {
                            Criteria roleScopeFilter = new Criteria();
                            roleScopeFilter.addEqualTo("scope", FragmentProperty.ROLE_PROPERTY_SCOPE);
                            roleScopeFilter.addEqualTo("scopeValue", principal.getName());
                            scopesFilter.addOrCriteria(roleScopeFilter);
                        }
                    }
                }
                else
                {
                    Principal userPrincipal = SubjectHelper.getBestPrincipal(subject, User.class);
                    if (userPrincipal != null)
                    {
                        Criteria userScopeFilter = new Criteria();
                        userScopeFilter.addEqualTo("scope", FragmentProperty.USER_PROPERTY_SCOPE);
                        userScopeFilter.addEqualTo("scopeValue", userPrincipal.getName());
                        scopesFilter.addOrCriteria(userScopeFilter);
                    }
                }
            }
            filter.addAndCriteria(scopesFilter);
            // query for fragment properties for list using database query
            QueryByCriteria query = QueryFactory.newQuery(FragmentPropertyImpl.class, filter);
            Collection fragmentProperties = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            list.getProperties().addAll(fragmentProperties);
        
            // save fragment property list in thread local cache
            if (threadLocalCache == null)
            {
                threadLocalCache = new HashMap();
                fragmentPropertyListsCache.set(threadLocalCache);
            }
            threadLocalCache.put(threadLocalCacheKey, list);
        }
        else if (transientList != null)
        {
            synchronized (list)
            {
                synchronized (transientList)
                {
                    // merge into persistent list; this is unsafe due to the
                    // lack of transactional isolation in shared objects, but
                    // this should only happen before new objects are committed
                    // and here we are assuming that only the current user has
                    // access to the new objects
                    Iterator sourceIter = transientList.iterator();
                    while (sourceIter.hasNext())
                    {
                        FragmentProperty sourceProperty = (FragmentProperty)sourceIter.next();
                        FragmentProperty targetProperty = list.getMatchingProperty(sourceProperty);
                        if (targetProperty != null)
                        {
                            targetProperty.setValue(sourceProperty.getValue());
                        }
                        else
                        {
                            list.add(sourceProperty);
                        }
                    }
                    
                    // clear transient list
                    transientList.getProperties().clear();
                    List removedProperties = transientList.getRemovedProperties();
                    if (removedProperties != null)
                    {
                        removedProperties.clear();
                    }
                }
            }
        }
        return list;
    }

    /**
     * Update fragment property list.
     * 
     * @param list fragment property list
     */
    public void updateFragmentPropertiesList(BaseFragmentElementImpl baseFragmentElement, FragmentPropertyList transientList)
    {
        // update persistent list
        FragmentPropertyList list = getFragmentPropertiesList(baseFragmentElement, transientList);
        if (list != null)
        {
            // update fragment properties in list in database
            synchronized (list)
            {
                Iterator propertiesIter = list.getProperties().iterator();
                while (propertiesIter.hasNext())
                {
                    FragmentPropertyImpl storeProperty = (FragmentPropertyImpl)propertiesIter.next();
                    storeProperty.setFragment(baseFragmentElement);
                    getPersistenceBrokerTemplate().store(storeProperty);
                }
                List removedProperties = list.getRemovedProperties();
                if (removedProperties != null)
                {
                    Iterator removedPropertiesIter = removedProperties.iterator();
                    while (removedPropertiesIter.hasNext())
                    {
                        FragmentPropertyImpl deleteProperty = (FragmentPropertyImpl)removedPropertiesIter.next();
                        deleteProperty.setFragment(baseFragmentElement);
                        getPersistenceBrokerTemplate().delete(deleteProperty);
                    }
                }
            }
        }
    }

    /**
     * Remove fragment property list.
     * 
     * @param list fragment property list
     */
    public void removeFragmentPropertiesList(BaseFragmentElementImpl baseFragmentElement, FragmentPropertyList transientList)
    {
        // access thread local fragment property lists cache
        String threadLocalCacheKey = getFragmentPropertiesListThreadLocalCacheKey(baseFragmentElement);
        Map threadLocalCache = (Map)fragmentPropertyListsCache.get();

        // remove cached persistent list
        FragmentPropertyList list = ((threadLocalCache != null) ? (FragmentPropertyList)threadLocalCache.get(threadLocalCacheKey) : null);
        if (list != null)
        {
            // remove list from cache
            threadLocalCache.remove(threadLocalCacheKey);
            // cleanup list
            synchronized (list)
            {
                list.getProperties().clear();
                List removedProperties = list.getRemovedProperties();
                if (removedProperties != null)
                {
                    removedProperties.clear();
                }
            }
        }
        
        // cleanup transient list
        if (transientList != null)
        {
            synchronized (transientList)
            {
                transientList.getProperties().clear();
                List removedProperties = transientList.getRemovedProperties();
                if (removedProperties != null)
                {
                    removedProperties.clear();
                }
            }
        }

        // remove all fragment properties in list from database
        Criteria filter = new Criteria();
        filter.addEqualTo("fragment", new Integer(baseFragmentElement.getIdentity()));
        QueryByCriteria query = QueryFactory.newQuery(FragmentPropertyImpl.class, filter);
        getPersistenceBrokerTemplate().deleteByQuery(query);
    }
    
    /**
     * Compute thread local cache key for fragment properties.
     * 
     * @param baseFragmentElement owner of fragment properties
     * @return key string
     */
    private static String getFragmentPropertiesListThreadLocalCacheKey(BaseFragmentElementImpl baseFragmentElement)
    {
        // base key
        String key = baseFragmentElement.getBaseFragmentsElement().getPath()+"/"+baseFragmentElement.getId();
        // append current user if available
        Subject subject = JSSubject.getSubject(AccessController.getContext());
        if (subject != null)
        {
            Principal userPrincipal = SubjectHelper.getBestPrincipal(subject, User.class);
            if (userPrincipal != null)
            {
                key = key+"/"+userPrincipal.getName();
            }
        }
        return key;
    }
    
    /**
     * Rollback transactions registered with current thread.
     */
    public static void rollbackTransactions()
    {
        // clear thread local cache on rollback to ensure clean reset
        fragmentPropertyListsCache.remove();
    }

    /**
     * Clear transactions registered with current thread.
     */
    public static void clearTransactions()
    {
        // do not clear thread local cache: cache across transactions
    }
}
