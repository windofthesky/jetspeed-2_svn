/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.layout.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.PageNotRemovedException;
import org.apache.jetspeed.page.PageNotUpdatedException;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.BasePageElement;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.page.BaseConcretePageElement;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageFragment;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.impl.ContentFragmentImpl;
import org.apache.jetspeed.om.page.impl.ContentFragmentPreferenceImpl;
import org.apache.jetspeed.om.page.impl.ContentFragmentPropertyImpl;
import org.apache.jetspeed.om.page.impl.ContentLocalizedFieldImpl;
import org.apache.jetspeed.om.page.impl.ContentPageImpl;
import org.apache.jetspeed.om.page.impl.ContentSecurityConstraint;
import org.apache.jetspeed.om.page.impl.ContentSecurityConstraints;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.pluto.container.PortletPreference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page layout component implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class PageLayoutComponentImpl implements PageLayoutComponent, PageLayoutComponentUtils
{
    private static final Logger log = LoggerFactory.getLogger(PageLayoutComponentImpl.class);
    
    private PageManager pageManager;
    
    /**
     * Construct new PageLayoutComponent implementation.
     * 
     * @param pageManager page manager used to access PSML objects
     */
    public PageLayoutComponentImpl(PageManager pageManager)
    {
        this.pageManager = pageManager;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#addFragmentAtRowColumn(org.apache.jetspeed.om.page.ContentPage, org.apache.jetspeed.om.page.ContentFragment, int, int)
     */
    public ContentFragment addFragmentAtRowColumn(ContentPage contentPage, ContentFragment contentFragment, int row, int column)
    {
        log.debug("PageLayoutComponentImpl.addFragmentAtRowColumn() invoked");
        try
        {
            // validate content fragment
            if (Utils.isNull(contentFragment.getType()) || Utils.isNull(contentFragment.getName()))
            {
                throw new IllegalArgumentException("Portlet type and name not specified");
            }
            
            // get page root content fragment
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            ContentFragmentImpl pageRootContentFragmentImpl = contentPageImpl.getPageRootContentFragment();
            if ((pageRootContentFragmentImpl == null) || pageRootContentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Page root content fragment not found or is locked");                
            }

            // retrieve current page and root fragment from page manager
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            BaseFragmentElement rootFragment = page.getRootFragment();
            if (!(rootFragment instanceof Fragment))
            {
                throw new IllegalArgumentException("New Fragment cannot be added to page root fragment");                
            }
            Fragment fragment = (Fragment)rootFragment;
            if (!Fragment.LAYOUT.equals(fragment.getType()))
            {
                throw new IllegalArgumentException("New Fragment cannot be added to non-layout page root fragment");                
            }
            
            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);
            
            // create fragment and add to layout root fragment
            Fragment newFragment = pageManager.newFragment();
            newFragment.setType(contentFragment.getType());
            newFragment.setName(contentFragment.getName());
            if (!Utils.isNull(row))
            {
                newFragment.setLayoutRow(row);
            }
            if (!Utils.isNull(column))
            {
                newFragment.setLayoutColumn(column);
            }
            fragment.getFragments().add(newFragment);

            // update page in page manager
            updatePage(page);
            
            // update content page context
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            contentFragmentImpl.initialize(this, page, page, newFragment, null, null, false);
            if (!Utils.isNull(row))
            {
                contentFragmentImpl.setLayoutRow(null, null, row);
            }
            if (!Utils.isNull(column))
            {
                contentFragmentImpl.setLayoutColumn(null, null, column);
            }
            pageRootContentFragmentImpl.getFragments().add(contentFragmentImpl);
            String newContentFragmentId = pageRootContentFragmentImpl.getId()+"."+contentFragmentImpl.getFragment().getId();
            contentFragmentImpl.setId(newContentFragmentId);            
            return contentFragmentImpl;
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#addPortlet(org.apache.jetspeed.om.page.ContentFragment, java.lang.String, java.lang.String)
     */
    public ContentFragment addPortlet(ContentFragment contentFragment, String type, String name)
    {
        return addPortlet(contentFragment, type, name, -1, -1);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#addPortlet(org.apache.jetspeed.om.page.ContentFragment, java.lang.String, java.lang.String, int, int)
     */
    public ContentFragment addPortlet(ContentFragment contentFragment, String type, String name, int row, int column)
    {
        log.debug("PageLayoutComponentImpl.addPortlet() invoked");
        try
        {
            // validate portlet parameters
            if (Utils.isNull(type) || Utils.isNull(name))
            {
                throw new IllegalArgumentException("Portlet type and name not specified");
            }
            
            // validate content fragment
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            if (contentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Locked content fragment is not mutable");
            }
            boolean contentFragmentDefinitionIsPage = ((contentFragmentImpl.getDefinition() instanceof BaseConcretePageElement) && contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPage().getPath()));
            if (!contentFragmentDefinitionIsPage)
            {
                throw new IllegalArgumentException("Only page fragments can be modified");                
            }

            // retrieve current page and fragment from page manager
            BaseConcretePageElement page = getPage(contentFragmentImpl.getPage().getPath());
            BaseFragmentElement parentFragment = page.getFragmentById(contentFragmentImpl.getFragment().getId());
            if (!(parentFragment instanceof Fragment))
            {
                throw new IllegalArgumentException("New Fragment cannot be added to parent fragment");                
            }
            Fragment fragment = (Fragment)parentFragment;
            if (!Fragment.LAYOUT.equals(fragment.getType()))
            {
                throw new IllegalArgumentException("New Fragment cannot be added to non-layout parent fragment");                
            }
            
            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);

            // create fragment and add to layout fragment
            Fragment newFragment = pageManager.newFragment();
            newFragment.setType(type);
            newFragment.setName(name);
            if (!Utils.isNull(row))
            {
                newFragment.setLayoutRow(row);
            }
            if (!Utils.isNull(column))
            {
                newFragment.setLayoutColumn(column);
            }
            fragment.getFragments().add(newFragment);

            // update page in page manager
            updatePage(page);

            // update content context
            ContentFragmentImpl newContentFragmentImpl = newContentFragment(contentFragmentImpl.getId(), page, null, page, newFragment, false);
            contentFragmentImpl.getFragments().add(newContentFragmentImpl);
            return newContentFragmentImpl;
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#addPortlet(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String)
     */
    public ContentFragment addPortlet(ContentPage contentPage, String type, String name)
    {
        log.debug("PageLayoutComponentImpl.addPortlet() invoked");
        try
        {
            // validate content fragment
            if (Utils.isNull(type) || Utils.isNull(name))
            {
                throw new IllegalArgumentException("Portlet type and name not specified");
            }
            
            // get page root content fragment
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            ContentFragmentImpl pageRootContentFragmentImpl = contentPageImpl.getPageRootContentFragment();
            if ((pageRootContentFragmentImpl == null) || pageRootContentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Page root content fragment not found or is locked");                
            }

            // retrieve current page and root fragment from page manager
            BaseConcretePageElement page = getPage(contentPage.getPage().getPath());
            BaseFragmentElement rootFragment = page.getRootFragment();
            if (!(rootFragment instanceof Fragment))
            {
                throw new IllegalArgumentException("New Fragment cannot be added to page root fragment");                
            }
            Fragment fragment = (Fragment)rootFragment;
            if (!Fragment.LAYOUT.equals(fragment.getType()))
            {
                throw new IllegalArgumentException("New Fragment cannot be added to non-layout page root fragment");                
            }

            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);
            
            // create fragment and add to layout root fragment
            Fragment newFragment = pageManager.newFragment();
            newFragment.setType(type);
            newFragment.setName(name);
            fragment.getFragments().add(newFragment);

            // update page in page manager
            updatePage(page);

            // update content page context
            ContentFragmentImpl newContentFragmentImpl = newContentFragment(pageRootContentFragmentImpl.getId(), page, null, page, newFragment, false);
            pageRootContentFragmentImpl.getFragments().add(newContentFragmentImpl);
            return newContentFragmentImpl;
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#decrementFolderInDocumentOrder(org.apache.jetspeed.om.page.ContentPage)
     */
    public void decrementFolderInDocumentOrder(ContentPage contentPage)
    {
        log.debug("PageLayoutComponentImpl.decrementFolderInDocumentOrder() invoked");
        try
        {
            // retrieve current page and parent folders from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            Folder pageFolder = (Folder)page.getParent();
            Folder documentOrderingFolder = (Folder)pageFolder.getParent();
            if (documentOrderingFolder != null)
            {
                // check for edit permission
                documentOrderingFolder.checkAccess(JetspeedActions.EDIT);

                // shift document order and update document ordering in page manager
                boolean update = shiftDocumentOrder(documentOrderingFolder, pageFolder.getName(), null, true);
                if (update)
                {
                    pageManager.updateFolder(documentOrderingFolder);
                }
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#decrementInDocumentOrder(org.apache.jetspeed.om.page.ContentPage)
     */
    public void decrementInDocumentOrder(ContentPage contentPage)
    {
        log.debug("PageLayoutComponentImpl.decrementInDocumentOrder() invoked");
        try
        {
            // retrieve current page and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            Folder documentOrderingFolder = (Folder)page.getParent();

            // check for edit permission
            documentOrderingFolder.checkAccess(JetspeedActions.EDIT);

            // shift document order and update document ordering in page manager
            boolean update = shiftDocumentOrder(documentOrderingFolder, page.getName(), page.getType(), true);
            if (update)
            {
                pageManager.updateFolder(documentOrderingFolder);
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#incrementFolderInDocumentOrder(org.apache.jetspeed.om.page.ContentPage)
     */
    public void incrementFolderInDocumentOrder(ContentPage contentPage)
    {
        log.debug("PageLayoutComponentImpl.incrementFolderInDocumentOrder() invoked");
        try
        {
            // retrieve current page and parent folders from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            Folder pageFolder = (Folder)page.getParent();
            Folder documentOrderingFolder = (Folder)pageFolder.getParent();
            if (documentOrderingFolder != null)
            {
                // check for edit permission
                documentOrderingFolder.checkAccess(JetspeedActions.EDIT);

                // shift document order and update document ordering in page manager
                boolean update = shiftDocumentOrder(documentOrderingFolder, pageFolder.getName(), null, false);
                if (update)
                {
                    pageManager.updateFolder(documentOrderingFolder);
                }
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#incrementInDocumentOrder(org.apache.jetspeed.om.page.ContentPage)
     */
    public void incrementInDocumentOrder(ContentPage contentPage)
    {
        log.debug("PageLayoutComponentImpl.incrementInDocumentOrder() invoked");
        try
        {
            // retrieve current page and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            Folder documentOrderingFolder = (Folder)page.getParent();

            // check for edit permission
            documentOrderingFolder.checkAccess(JetspeedActions.EDIT);

            // shift document order and update document ordering in page manager
            boolean update = shiftDocumentOrder(documentOrderingFolder, page.getName(), page.getType(), false);
            if (update)
            {
                pageManager.updateFolder(documentOrderingFolder);
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#moveFragment(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String)
     */
    public void moveFragment(ContentPage contentPage, String fragmentId, String toFragmentId)
    {
        moveFragment(contentPage, fragmentId, null, toFragmentId);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#moveFragment(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String, java.lang.String)
     */
    public void moveFragment(ContentPage contentPage, String fragmentId, String fromFragmentId, String toFragmentId)
    {
        log.debug("PageLayoutComponentImpl.moveFragment() invoked");
        try
        {
            // get content fragments
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            ContentFragmentImpl fromContentFragmentImpl = (ContentFragmentImpl)((fromFragmentId != null) ? contentPageImpl.getFragmentById(fromFragmentId) : contentPageImpl.getRootFragment());
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)fromContentFragmentImpl.getFragmentById(fragmentId);
            ContentFragmentImpl toContentFragmentImpl = (ContentFragmentImpl)contentPageImpl.getFragmentById(toFragmentId);
            if ((contentFragmentImpl == null) || (fromContentFragmentImpl == null) || fromContentFragmentImpl.isLocked() ||
                                                 (toContentFragmentImpl == null) || toContentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Fragment ids involved in move and page not consistent or locked");
            }
            if (!fromContentFragmentImpl.getDefinition().getPath().equals(contentPageImpl.getPath()) ||
                !toContentFragmentImpl.getDefinition().getPath().equals(contentPageImpl.getPath()))
            {
                throw new IllegalArgumentException("Cannot use move fragment operation between pages");                
            }
            boolean contentFragmentDefinitionIsPage = ((contentFragmentImpl.getDefinition() instanceof BaseConcretePageElement) && contentFragmentImpl.getDefinition().getPath().equals(contentPageImpl.getPath()));
            if (!contentFragmentDefinitionIsPage && (contentFragmentImpl.getReference() == null))
            {
                throw new IllegalArgumentException("Fragment reference and page not consistent or mutable");
            }
            
            // retrieve current page and fragments from page manager
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            BaseFragmentElement fromFragmentElement = page.getFragmentById(fromContentFragmentImpl.getFragment().getId());
            if (!(fromFragmentElement instanceof Fragment))
            {
                throw new IllegalArgumentException("Move from fragmentId and page not consistent");
            }
            Fragment fromFragment = (Fragment)fromFragmentElement;
            String pageFragmentId = (contentFragmentDefinitionIsPage ? contentFragmentImpl.getFragment().getId() : contentFragmentImpl.getReference().getId());
            BaseFragmentElement fragment = fromFragment.getFragmentById(pageFragmentId);
            if (fragment == null)
            {
                throw new IllegalArgumentException("Move fragmentId and page not consistent");                
            }
            BaseFragmentElement toFragmentElement = page.getFragmentById(toContentFragmentImpl.getFragment().getId());
            if (!(toFragmentElement instanceof Fragment))
            {
                throw new IllegalArgumentException("Move to fragmentId and page not consistent");
            }
            Fragment toFragment = (Fragment)toFragmentElement;

            // check for edit permission
            page.checkAccess(JetspeedActions.EDIT);

            // move page fragment and update page in page manager
            fragment = fromFragment.removeFragmentById(fragment.getId());
            toFragment.getFragments().add(fragment);
            updatePage(page);

            // update content context
            fromContentFragmentImpl.removeFragmentById(fragmentId);
            toContentFragmentImpl.getFragments().add(contentFragmentImpl);
            String newContentFragmentId = toContentFragmentImpl.getId()+"."+contentFragmentImpl.getFragment().getId();
            contentFragmentImpl.setId(newContentFragmentId);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#newContentPage(org.apache.jetspeed.om.page.Page, org.apache.jetspeed.om.page.PageTemplate, java.util.Map)
     */
    public ContentPage newContentPage(BaseConcretePageElement page, PageTemplate pageTemplate, Map fragmentDefinitions)
    {
        // generate content page
        String contentPageId = page.getId();
        ContentPageImpl contentPageImpl = new ContentPageImpl(this, contentPageId, page, pageTemplate, fragmentDefinitions);
        // set/merge page attributes
        mergeContentPageAttributes(contentPageImpl, page);
        contentPageImpl.setName(page.getName());
        contentPageImpl.setPath(page.getPath());
        contentPageImpl.setUrl(page.getUrl());
        contentPageImpl.setHidden(page.isHidden());        
        // merge template attributes
        mergeContentPageAttributes(contentPageImpl, pageTemplate);            
        // set effective default detectors from merged default
        // decorators or inherit page effective default decorators
        Map effectiveDefaultDecorators = null;
        String effectiveLayoutDefaultDecorator = contentPageImpl.getDefaultDecorator(Fragment.LAYOUT);
        if (effectiveLayoutDefaultDecorator == null)
        {
            effectiveLayoutDefaultDecorator = page.getEffectiveDefaultDecorator(Fragment.LAYOUT);
        }
        if (effectiveLayoutDefaultDecorator != null)
        {
            if (effectiveDefaultDecorators == null)
            {
                effectiveDefaultDecorators = new HashMap();
            }
            effectiveDefaultDecorators.put(Fragment.LAYOUT, effectiveLayoutDefaultDecorator);
        }
        String effectivePortletDefaultDecorator = contentPageImpl.getDefaultDecorator(Fragment.PORTLET);
        if (effectivePortletDefaultDecorator == null)
        {
            effectivePortletDefaultDecorator = page.getEffectiveDefaultDecorator(Fragment.PORTLET);
        }
        if (effectivePortletDefaultDecorator != null)
        {
            if (effectiveDefaultDecorators == null)
            {
                effectiveDefaultDecorators = new HashMap();
            }
            effectiveDefaultDecorators.put(Fragment.PORTLET, effectivePortletDefaultDecorator);
        }
        contentPageImpl.setEffectiveDefaultDecorators(effectiveDefaultDecorators);
        
        log.debug("PageLayoutComponentImpl.newContentPage(): construct ContentPage: id="+contentPageImpl.getId()+", path="+contentPageImpl.getPath());
        
        // generate root and nested content fragments
        BaseFragmentsElement definition = null;
        BaseFragmentElement rootFragment = null;
        boolean rootLocked = false;
        if (pageTemplate != null)
        {
            definition = pageTemplate;
            rootFragment = definition.getRootFragment();
            rootLocked = true;
        }
        if (rootFragment == null)
        {
            definition = page;
            rootFragment = definition.getRootFragment();
        }
        if (rootFragment != null)
        {
            ContentFragmentImpl rootContentFragmentImpl = newContentFragment(null, page, fragmentDefinitions, definition, rootFragment, rootLocked);
            if (rootContentFragmentImpl != null)
            {
                contentPageImpl.setRootFragment(rootContentFragmentImpl);
            }
        }

        log.debug("PageLayoutComponentImpl.newContentPage(): constructed ContentPage: id="+contentPageImpl.getId());
        return contentPageImpl;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#newSiblingFolder(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingFolder(ContentPage contentPage, String folderName, String folderTitle, String folderShortTitle, String defaultPageLayoutName)
    {
        log.debug("PageLayoutComponentImpl.newSiblingFolder() invoked");
        try
        {
            // retrieve current page and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            Folder folder = (Folder)page.getParent();
            
            // check for edit permission
            folder.checkAccess(JetspeedActions.EDIT);

            // construct new sibling folder
            String newFolderPath = folder.getPath()+(folder.getPath().endsWith(Folder.PATH_SEPARATOR) ? "" : Folder.PATH_SEPARATOR)+folderName;
            if (pageManager.folderExists(newFolderPath))
            {
                throw new IllegalArgumentException("Folder "+newFolderPath+" exists");
            }
            Folder newFolder = pageManager.newFolder(newFolderPath);
            if (!Utils.isNull(folderTitle))
            {
                newFolder.setTitle(folderTitle);
            }
            if (!Utils.isNull(folderShortTitle))
            {
                newFolder.setShortTitle(folderShortTitle);
            }
            String defaultLayoutDecorator = page.getDefaultDecorator(Fragment.LAYOUT);
            if (defaultLayoutDecorator != null)
            {
                newFolder.setDefaultDecorator(defaultLayoutDecorator, Fragment.LAYOUT);
            }
            String defaultPortletDecorator = page.getDefaultDecorator(Fragment.PORTLET);
            if (defaultPortletDecorator != null)
            {
                newFolder.setDefaultDecorator(defaultPortletDecorator, Fragment.PORTLET);
            }
            
            // update new folder in page manager
            pageManager.updateFolder(newFolder);
            
            // add new folder to parent document order list
            List documentOrder = folder.getDocumentOrder();
            if (documentOrder == null)
            {
                documentOrder = new ArrayList(4);
                folder.setDocumentOrder(documentOrder);
            }
            if (documentOrder.indexOf(newFolder.getName()) == -1)
            {
                documentOrder.add(newFolder.getName());
            }

            // update folder in page manager
            pageManager.updateFolder(folder);
            
            // create default page in new folder
            String newPagePath = newFolder.getPath()+(newFolder.getPath().endsWith(Folder.PATH_SEPARATOR) ? "" : Folder.PATH_SEPARATOR)+"default-page"+Page.DOCUMENT_TYPE;
            Page newPage = pageManager.newPage(newPagePath);
            if (!Utils.isNull(defaultPageLayoutName) && (newPage.getRootFragment() instanceof Fragment))
            {
                ((Fragment)newPage.getRootFragment()).setName(defaultPageLayoutName);
            }
            if (!Utils.isNull(folderTitle))
            {
                newPage.setTitle(folderTitle);
            }
            if (!Utils.isNull(folderShortTitle))
            {
                newPage.setShortTitle(folderShortTitle);
            }
            if (defaultLayoutDecorator != null)
            {
                newPage.setDefaultDecorator(defaultLayoutDecorator, Fragment.LAYOUT);
            }
            if (defaultPortletDecorator != null)
            {
                newPage.setDefaultDecorator(defaultPortletDecorator, Fragment.PORTLET);
            }

            // update default page in page manager
            pageManager.updatePage(newPage);

            // add new page to parent document order list
            documentOrder = newFolder.getDocumentOrder();
            if (documentOrder == null)
            {
                documentOrder = new ArrayList(4);
                newFolder.setDocumentOrder(documentOrder);
            }
            if (documentOrder.indexOf(newPage.getName()) == -1)
            {
                documentOrder.add(newPage.getName());
            }

            // update folder in page manager
            pageManager.updateFolder(newFolder);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#newSiblingPage(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingPage(ContentPage contentPage, String pageName, String layoutName, String pageTitle, String pageShortTitle)
    {
        log.debug("PageLayoutComponentImpl.newSiblingFolder() invoked");
        try
        {
            // retrieve current page and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            Folder folder = (Folder)page.getParent();

            // check for edit permission
            folder.checkAccess(JetspeedActions.EDIT);

            // construct new sibling page
            String newPagePath = folder.getPath()+(folder.getPath().endsWith(Folder.PATH_SEPARATOR) ? "" : Folder.PATH_SEPARATOR)+pageName+Page.DOCUMENT_TYPE;
            if (pageManager.pageExists(newPagePath))
            {
                throw new IllegalArgumentException("Page "+newPagePath+" exists");
            }
            Page newPage = pageManager.newPage(newPagePath);
            if (!Utils.isNull(layoutName) && (newPage.getRootFragment() instanceof Fragment))
            {
                ((Fragment)newPage.getRootFragment()).setName(layoutName);
            }
            if (!Utils.isNull(pageTitle))
            {
                newPage.setTitle(pageTitle);
            }
            if (!Utils.isNull(pageShortTitle))
            {
                newPage.setShortTitle(pageShortTitle);
            }
            String defaultLayoutDecorator = page.getDefaultDecorator(Fragment.LAYOUT);
            if (defaultLayoutDecorator != null)
            {
                newPage.setDefaultDecorator(defaultLayoutDecorator, Fragment.LAYOUT);
            }
            String defaultPortletDecorator = page.getDefaultDecorator(Fragment.PORTLET);
            if (defaultPortletDecorator != null)
            {
                newPage.setDefaultDecorator(defaultPortletDecorator, Fragment.PORTLET);
            }

            // update new page in page manager
            pageManager.updatePage(newPage);
            
            // add new page to parent document order list
            List documentOrder = folder.getDocumentOrder();
            if (documentOrder == null)
            {
                documentOrder = new ArrayList(4);
                folder.setDocumentOrder(documentOrder);
            }
            if (documentOrder.indexOf(newPage.getName()) == -1)
            {
                documentOrder.add(newPage.getName());
            }

            // update folder in page manager
            pageManager.updateFolder(folder);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#remove(org.apache.jetspeed.om.page.ContentPage)
     */
    public void remove(ContentPage contentPage)
    {
        log.debug("PageLayoutComponentImpl.remove() invoked");
        try
        {
            // retrieve current page and document ordering folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            String documentName = page.getName();
            Folder documentOrderingFolder = (Folder)page.getParent();

            // check for edit permission
            page.checkAccess(JetspeedActions.EDIT);

            // remove in page manager
            removePage(page);

            // check for ordering folder edit permission
            documentOrderingFolder.checkAccess(JetspeedActions.EDIT);
            
            // remove document from ordering folder and
            // update document ordering folder in page manager
            boolean update = removeDocumentOrder(documentOrderingFolder, documentName);
            if (update)
            {
                pageManager.updateFolder(documentOrderingFolder);
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#removeFolder(org.apache.jetspeed.om.page.ContentPage)
     */
    public void removeFolder(ContentPage contentPage)
    {
        log.debug("PageLayoutComponentImpl.removeFolder() invoked");
        try
        {
            // retrieve current page and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            Folder folder = (Folder)page.getParent();
            String documentName = folder.getName();
            Folder documentOrderingFolder = (Folder)folder.getParent();

            // check for edit permission
            folder.checkAccess(JetspeedActions.EDIT);

            // remove folder in page manager
            pageManager.removeFolder(folder);

            // check for ordering folder edit permission
            documentOrderingFolder.checkAccess(JetspeedActions.EDIT);
            
            // remove document from ordering folder and
            // update document ordering folder in page manager
            boolean update = removeDocumentOrder(documentOrderingFolder, documentName);
            if (update)
            {
                pageManager.updateFolder(documentOrderingFolder);
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#removeFragment(org.apache.jetspeed.om.page.ContentPage, java.lang.String)
     */
    public void removeFragment(ContentPage contentPage, String fragmentId)
    {
        log.debug("PageLayoutComponentImpl.removeFragment() invoked");
        try
        {
            // lookup page manager fragment and fragment id
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            ContentFragmentImpl [] parentContentFragmentImpl = new ContentFragmentImpl[]{null};
            ContentFragmentImpl contentFragmentImpl = contentPageImpl.getFragmentById(fragmentId, parentContentFragmentImpl);
            if (contentFragmentImpl == null)
            {
                throw new IllegalArgumentException("FragmentId and page not consistent");                
            }
            if ((parentContentFragmentImpl[0] != null) && (!parentContentFragmentImpl[0].getDefinition().getPath().equals(contentPageImpl.getPage().getPath()) ||
                                                           parentContentFragmentImpl[0].isLocked()))
            {
                throw new IllegalArgumentException("Parent content fragment and page not consistent or locked");                
            }
            boolean contentFragmentDefinitionIsPage = ((contentFragmentImpl.getDefinition() instanceof BaseConcretePageElement) && contentFragmentImpl.getDefinition().getPath().equals(contentPageImpl.getPath()));
            if (!contentFragmentDefinitionIsPage && (contentFragmentImpl.getReference() == null))
            {
                throw new IllegalArgumentException("Fragment reference and page not consistent or mutable");
            }
            
            // retrieve current page and fragment from page manager
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            String pageFragmentId = (contentFragmentDefinitionIsPage ? contentFragmentImpl.getFragment().getId() : contentFragmentImpl.getReference().getId());
            BaseFragmentElement fragment = page.getFragmentById(pageFragmentId);
            if (fragment == null)
            {
                throw new IllegalArgumentException("Remove fragmentId and page not consistent");                
            }

            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);
            
            // remove fragment and update in page manager
            boolean update = (page.removeFragmentById(pageFragmentId) != null);
            if (update)
            {
                updatePage(page);
            }
            
            // update content context
            contentPageImpl.removeFragmentById(fragmentId);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateDecorator(org.apache.jetspeed.om.page.ContentFragment, java.lang.String)
     */
    public void updateDecorator(ContentFragment contentFragment, String decoratorName)
    {
        updateDecorator(contentFragment, decoratorName, null, null);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateDecorator(org.apache.jetspeed.om.page.ContentFragment, java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateDecorator(ContentFragment contentFragment, String decoratorName, String scope, String scopeValue)
    {
        log.debug("PageLayoutComponentImpl.updateDecorator() invoked");
        try
        {
            // validate content fragment and lookup current fragment
            // of page or page template from page manager and check
            // edit or view access requirements
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            BaseFragmentElement fragment = lookupPageOrPageTemplateFragment(contentFragmentImpl, scope);
            
            // update fragment decorator and page or page template in
            // page manager
            boolean update = false;
            decoratorName = (!Utils.isNull(decoratorName) ? decoratorName : null);
            if (((decoratorName != null) && !decoratorName.equals(fragment.getDecorator()) ||
                ((decoratorName == null) && (fragment.getDecorator() != null))))
            {
                fragment.setDecorator(scope, scopeValue, decoratorName);
                update = true;
            }
            if (update)
            {
                updatePageOrPageTemplateFragmentProperties(fragment, scope);
            }
            
            // update content context
            contentFragmentImpl.setDecorator(scope, scopeValue, decoratorName);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateDefaultDecorator(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String)
     */
    public void updateDefaultDecorator(ContentPage contentPage, String decoratorName, String fragmentType)
    {
        log.debug("PageLayoutComponentImpl.updateDefaultDecorator() invoked");
        try
        {
            // validate fragment type
            if (Utils.isNull(fragmentType))
            {
                throw new IllegalArgumentException("Fragment type not specified.");
            }
            
            // retrieve current page from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());

            // check for edit permission
            page.checkAccess(JetspeedActions.EDIT);            

            // update default decorator and page in page manager
            boolean update = false;
            decoratorName = (!Utils.isNull(decoratorName) ? decoratorName : null);
            if (((decoratorName != null) && !decoratorName.equals(page.getDefaultDecorator(fragmentType)) ||
                ((decoratorName == null) && (page.getDefaultDecorator(fragmentType) != null))))
            {
                page.setDefaultDecorator(decoratorName, fragmentType);
                update = true;
            }
            if (update)
            {
                updatePage(page);
            }

            // update content context
            Map effectiveDefaultDecorators = new HashMap();
            effectiveDefaultDecorators.put(Fragment.LAYOUT, page.getEffectiveDefaultDecorator(Fragment.LAYOUT));
            effectiveDefaultDecorators.put(Fragment.PORTLET, page.getEffectiveDefaultDecorator(Fragment.PORTLET));
            contentPageImpl.setEffectiveDefaultDecorators(effectiveDefaultDecorators);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateFolderTitles(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String)
     */
    public void updateFolderTitles(ContentPage contentPage, String title, String shortTitle)
    {
        log.debug("PageLayoutComponentImpl.updateFolderTitles() invoked");
        try
        {
            // retrieve current page and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());
            Folder folder = (Folder)page.getParent();

            // check for edit permission
            folder.checkAccess(JetspeedActions.EDIT);            

            // update titles and folder in page manager
            boolean update = false;
            if (!Utils.isNull(title))
            {
                if (!title.equals(folder.getTitle()))
                {
                    folder.setTitle(title);
                    update = true;
                }
            }
            if (!Utils.isNull(shortTitle))
            {
                if (!shortTitle.equals(folder.getShortTitle()))
                {
                    folder.setShortTitle(shortTitle);
                    update = true;
                }
            }
            if (update)
            {
                pageManager.updateFolder(folder);
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateName(org.apache.jetspeed.om.page.ContentFragment, java.lang.String)
     */
    public void updateName(ContentFragment contentFragment, String name)
    {
        log.debug("PageLayoutComponentImpl.updateName() invoked");
        try
        {
            // validate fragment name
            if (Utils.isNull(name))
            {
                throw new IllegalArgumentException("Fragment name not specified.");
            }
            
            // validate content fragment
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            if (contentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Locked content fragment is not mutable");                
            }
            boolean contentFragmentDefinitionIsPage = ((contentFragmentImpl.getDefinition() instanceof BaseConcretePageElement) && contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPage().getPath()));
            if (!contentFragmentDefinitionIsPage)
            {
                throw new IllegalArgumentException("Only page fragments can be modified");                
            }
            
            // retrieve current fragment and page from page manager
            BaseConcretePageElement page = getPage(contentFragmentImpl.getPage().getPath());
            BaseFragmentElement foundFragment = page.getFragmentById(contentFragmentImpl.getFragment().getId());
            if (!(foundFragment instanceof Fragment))
            {
                throw new IllegalArgumentException("Fragment and page not consistent");                
            }
            Fragment fragment = (Fragment)foundFragment;

            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);            

            // update fragment name and page in page manager
            boolean update = false;
            if (!name.equals(fragment.getName()))
            {
                fragment.setName(name);
                update = true;
            }
            if (update)
            {
                updatePage(page);
            }

            // update content context
            contentFragmentImpl.setName(name);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updatePosition(org.apache.jetspeed.om.page.ContentFragment, float, float, float, float, float)
     */
    public void updatePosition(ContentFragment contentFragment, float x, float y, float z, float width, float height)
    {
        updatePosition(contentFragment, x, y, z, width, height, null, null);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updatePosition(org.apache.jetspeed.om.page.ContentFragment, float, float, float, float, float, java.lang.String, java.lang.String)
     */
    public void updatePosition(ContentFragment contentFragment, float x, float y, float z, float width, float height, String scope, String scopeValue)
    {
        log.debug("PageLayoutComponentImpl.updatePosition() invoked");
        try
        {
            // validate content fragment and lookup current fragment
            // of page or page template from page manager and check
            // edit or view access requirements
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            BaseFragmentElement fragment = lookupPageOrPageTemplateFragment(contentFragmentImpl, scope);
            
            // update fragment position and page or page template
            // in page manager
            boolean update = false;
            if (!Utils.isNull(x))
            {
                if (x != fragment.getLayoutX())
                {
                    fragment.setLayoutX(scope, scopeValue, x);
                    update = true;
                }
            }
            if (!Utils.isNull(y))
            {
                if (y != fragment.getLayoutY())
                {
                    fragment.setLayoutY(scope, scopeValue, y);
                    update = true;
                }
            }
            if (!Utils.isNull(z))
            {
                if (z != fragment.getLayoutZ())
                {
                    fragment.setLayoutZ(scope, scopeValue, z);
                    update = true;
                }
            }
            if (!Utils.isNull(width))
            {
                if (width != fragment.getLayoutWidth())
                {
                    fragment.setLayoutWidth(scope, scopeValue, width);
                    update = true;
                }
            }
            if (!Utils.isNull(height))
            {
                if (height != fragment.getLayoutHeight())
                {
                    fragment.setLayoutWidth(scope, scopeValue, height);
                    update = true;
                }
            }
            if (update)
            {
                updatePageOrPageTemplateFragmentProperties(fragment, scope);
            }

            // update content context
            if (!Utils.isNull(x))
            {
                contentFragmentImpl.setLayoutX(scope, scopeValue, x);
            }
            if (!Utils.isNull(y))
            {
                contentFragmentImpl.setLayoutY(scope, scopeValue, y);
            }
            if (!Utils.isNull(z))
            {
                contentFragmentImpl.setLayoutZ(scope, scopeValue, z);
            }
            if (!Utils.isNull(width))
            {
                contentFragmentImpl.setLayoutWidth(scope, scopeValue, width);
            }
            if (!Utils.isNull(height))
            {
                contentFragmentImpl.setLayoutWidth(scope, scopeValue, height);
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updatePreferences(org.apache.jetspeed.om.page.ContentFragment, java.util.Map)
     */
    public void updatePreferences(ContentFragment contentFragment, Map preferences)
    {
        log.debug("PageLayoutComponentImpl.updatePreferences() invoked");
        try
        {
            // validate content fragment
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            boolean contentFragmentDefinitionIsPage = ((contentFragmentImpl.getDefinition() instanceof BaseConcretePageElement) && contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPage().getPath()));
            if (!contentFragmentDefinitionIsPage && (contentFragmentImpl.getReference() == null))
            {
                throw new IllegalArgumentException("Only page fragments and fragment references are mutable");
            }
            
            // retrieve current fragment and page from page manager
            BaseConcretePageElement page = getPage(contentFragmentImpl.getPage().getPath());
            String pageFragmentId = (contentFragmentDefinitionIsPage ? contentFragmentImpl.getFragment().getId() : contentFragmentImpl.getReference().getId());
            BaseFragmentElement fragment = page.getFragmentById(pageFragmentId);
            if (fragment == null)
            {
                throw new IllegalArgumentException("Fragment and page not consistent");                
            }

            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);            

            // update fragment preferences and page in page manager
            fragment.getPreferences().clear();
            if (preferences != null)
            {
                Iterator preferencesIter = preferences.entrySet().iterator();
                while (preferencesIter.hasNext())
                {
                    Map.Entry preferencesEntry = (Map.Entry)preferencesIter.next();
                    FragmentPreference preference = pageManager.newFragmentPreference();
                    preference.setName((String)preferencesEntry.getKey());
                    Object values = preferencesEntry.getValue();
                    if (values instanceof String)
                    {
                        preference.getValueList().add((String)values);
                    }
                    else if (values instanceof String [])
                    {
                        preference.getValueList().addAll(Arrays.asList(((String [])values)));
                    }
                    else if (values instanceof FragmentPreference)
                    {
                        FragmentPreference fragmentPreference = (FragmentPreference)values;
                        preference.setReadOnly(fragmentPreference.isReadOnly());
                        preference.getValueList().addAll(fragmentPreference.getValueList());
                    }
                    else if (values instanceof PortletPreference)
                    {
                        PortletPreference portletPreference = (PortletPreference)values;
                        preference.setReadOnly(portletPreference.isReadOnly());
                        preference.getValueList().addAll(Arrays.asList(portletPreference.getValues()));
                    }
                    else
                    {
                        throw new IllegalArgumentException("Unexpected preference value type");
                    }
                    fragment.getPreferences().add(preference);
                }
            }
            updatePage(page);

            // update content context
            contentFragmentImpl.setPreferences(preferences);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateProperty(org.apache.jetspeed.om.page.ContentFragment, java.lang.String, java.lang.String)
     */
    public void updateProperty(ContentFragment contentFragment, String propName, String propValue)
    {
        updateProperty(contentFragment, propName, propValue, null, null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateProperty(org.apache.jetspeed.om.page.ContentFragment, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateProperty(ContentFragment contentFragment, String propName, String propValue, String scope, String scopeValue)
    {
        log.debug("PageLayoutComponentImpl.updateProperty() invoked");
        try
        {
            // validate content fragment and lookup current fragment
            // of page or page template from page manager and check
            // edit or view access requirements
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            BaseFragmentElement fragment = lookupPageOrPageTemplateFragment(contentFragmentImpl, scope);
            
            // update fragment property and page or page template
            // in page manager
            propValue = (!Utils.isNull(propValue) ? propValue : null);
            String currentPropValue = fragment.getProperty(propName, scope, scopeValue);
            if (((propValue == null) && (currentPropValue != null)) || ((propValue != null) && !propValue.equals(currentPropValue)))
            {
                fragment.setProperty(propName, scope, scopeValue, propValue);
                updatePageOrPageTemplateFragmentProperties(fragment, scope);
            }

            // update content context
            contentFragmentImpl.setProperty(propName, scope, scopeValue, propValue);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateRowColumn(org.apache.jetspeed.om.page.ContentFragment, int, int)
     */
    public void updateRowColumn(ContentFragment contentFragment, int row, int column)
    {
        updateRowColumn(contentFragment, row, column, null, null);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateRowColumn(org.apache.jetspeed.om.page.ContentFragment, int, int, java.lang.String, java.lang.String)
     */
    public void updateRowColumn(ContentFragment contentFragment, int row, int column, String scope, String scopeValue)
    {
        log.debug("PageLayoutComponentImpl.updateRowColumn() invoked");
        try
        {
            // validate content fragment and lookup current fragment
            // of page or page template from page manager and check
            // edit or view access requirements
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            BaseFragmentElement fragment = lookupPageOrPageTemplateFragment(contentFragmentImpl, scope);
            
            // update fragment row and column and page or page
            // template in page manager
            boolean update = false;
            if (!Utils.isNull(row))
            {
                if (row != fragment.getLayoutRow())
                {
                    fragment.setLayoutRow(scope, scopeValue, row);
                    update = true;
                }
            }
            if (!Utils.isNull(column))
            {
                if (column != fragment.getLayoutColumn())
                {
                    fragment.setLayoutColumn(scope, scopeValue, column);
                    update = true;
                }
            }
            if (update)
            {
                updatePageOrPageTemplateFragmentProperties(fragment, scope);
            }

            // update content context
            if (!Utils.isNull(row))
            {
                contentFragmentImpl.setLayoutRow(scope, scopeValue, row);
            }
            if (!Utils.isNull(column))
            {
                contentFragmentImpl.setLayoutColumn(scope, scopeValue, column);
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateSecurityConstraints(org.apache.jetspeed.om.page.ContentFragment, org.apache.jetspeed.om.common.SecurityConstraints)
     */
    public void updateSecurityConstraints(ContentFragment contentFragment, SecurityConstraints constraints)
    {
        log.debug("PageLayoutComponentImpl.updateSecurityConstraints() invoked");
        try
        {
            // validate content fragment
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            boolean contentFragmentDefinitionIsPage = ((contentFragmentImpl.getDefinition() instanceof BaseConcretePageElement) && contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPage().getPath()));
            if (!contentFragmentDefinitionIsPage && (contentFragmentImpl.getReference() == null))
            {
                throw new IllegalArgumentException("Only page fragments and fragment references are mutable");
            }
            
            // retrieve current fragment and page from page manager
            BaseConcretePageElement page = getPage(contentFragmentImpl.getPage().getPath());
            String pageFragmentId = (contentFragmentDefinitionIsPage ? contentFragmentImpl.getFragment().getId() : contentFragmentImpl.getReference().getId());
            BaseFragmentElement fragment = page.getFragmentById(pageFragmentId);
            if (fragment == null)
            {
                throw new IllegalArgumentException("Fragment and page not consistent");                
            }

            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);            

            // update fragment preferences and page in page manager
            fragment.setSecurityConstraints(null);
            if ((constraints != null) && !constraints.isEmpty())
            {
                SecurityConstraints fragmentConstraints = fragment.newSecurityConstraints();
                String constraintsOwner = constraints.getOwner();
                if (constraintsOwner != null)
                {
                    fragmentConstraints.setOwner(constraintsOwner);
                }
                List constraintsConstraints = constraints.getSecurityConstraints();
                if ((constraintsConstraints != null) || !constraintsConstraints.isEmpty())
                {
                    List fragmentConstraintsConstraints = new ArrayList(constraintsConstraints.size());
                    Iterator constraintsIter = constraintsConstraints.iterator();
                    while (constraintsIter.hasNext())
                    {
                        SecurityConstraint constraint = (SecurityConstraint)constraintsIter.next();
                        SecurityConstraint fragmentConstraintsConstraint = fragment.newSecurityConstraint();
                        fragmentConstraintsConstraint.setGroups(constraint.getGroups());
                        fragmentConstraintsConstraint.setPermissions(constraint.getPermissions());
                        fragmentConstraintsConstraint.setRoles(constraint.getRoles());
                        fragmentConstraintsConstraint.setUsers(constraint.getUsers());
                        fragmentConstraintsConstraints.add(fragmentConstraintsConstraint);
                    }
                    fragmentConstraints.setSecurityConstraints(fragmentConstraintsConstraints);
                }
                List constraintsConstraintsRefs = constraints.getSecurityConstraintsRefs();
                if ((constraintsConstraintsRefs != null) || !constraintsConstraintsRefs.isEmpty())
                {
                    List fragmentConstraintsConstraintsRefs = new ArrayList(constraintsConstraintsRefs.size());
                    Iterator constraintsRefsIter = constraintsConstraintsRefs.iterator();
                    while (constraintsRefsIter.hasNext())
                    {
                        fragmentConstraintsConstraintsRefs.add((String)constraintsRefsIter.next());
                    }
                    fragmentConstraints.setSecurityConstraintsRefs(fragmentConstraintsConstraintsRefs);
                }
                fragment.setSecurityConstraints(fragmentConstraints);
            }
            updatePage(page);

            // update content context
            contentFragmentImpl.setSecurityConstraints(constraints);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateStateMode(org.apache.jetspeed.om.page.ContentFragment, java.lang.String, java.lang.String)
     */
    public void updateStateMode(ContentFragment contentFragment, String portletState, String portletMode)
    {
        updateStateMode(contentFragment, portletState, portletMode, null, null);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateStateMode(org.apache.jetspeed.om.page.ContentFragment, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateStateMode(ContentFragment contentFragment, String portletState, String portletMode, String scope, String scopeValue)
    {
        log.debug("PageLayoutComponentImpl.updateStateMode() invoked");
        try
        {
            // validate content fragment and lookup current fragment
            // of page or page template from page manager and check
            // edit or view access requirements
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            BaseFragmentElement fragment = lookupPageOrPageTemplateFragment(contentFragmentImpl, scope);

            // update fragment portlet state and mode and page or page
            // template in page manager
            boolean update = false;
            if (!Utils.isNull(portletState))
            {
                if (!portletState.equals(fragment.getState()))
                {
                    fragment.setState(scope, scopeValue, portletState);
                    update = true;
                }
            }
            if (!Utils.isNull(portletMode))
            {
                if (!portletMode.equals(fragment.getMode()))
                {
                    fragment.setMode(scope, scopeValue, portletMode);
                    update = true;
                }
            }
            if (update)
            {
                updatePageOrPageTemplateFragmentProperties(fragment, scope);
            }

            // update content context
            if (!Utils.isNull(portletState))
            {
                contentFragmentImpl.setState(scope, scopeValue, portletState);
            }
            if (!Utils.isNull(portletMode))
            {
                contentFragmentImpl.setMode(scope, scopeValue, portletMode);
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateTitles(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String)
     */
    public void updateTitles(ContentPage contentPage, String title, String shortTitle)
    {
        log.debug("PageLayoutComponentImpl.updateTitles() invoked");
        try
        {
            // retrieve current page from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseConcretePageElement page = getPage(contentPageImpl.getPage().getPath());

            // check for edit permission
            page.checkAccess(JetspeedActions.EDIT);            

            // update titles and page in page manager
            boolean update = false;
            if (!Utils.isNull(title))
            {
                if (!title.equals(page.getTitle()))
                {
                    page.setTitle(title);
                    update = true;
                }
            }
            if (!Utils.isNull(shortTitle))
            {
                if (!shortTitle.equals(page.getShortTitle()))
                {
                    page.setShortTitle(shortTitle);
                    update = true;
                }
            }
            if (update)
            {
                updatePage(page);
            }
            
            // update content context
            if (!Utils.isNull(title))
            {
                contentPageImpl.setTitle(title);
            }
            if (!Utils.isNull(shortTitle))
            {
                contentPageImpl.setShortTitle(shortTitle);
            }
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#getUnlockedRootFragment(org.apache.jetspeed.om.page.ContentPage)
     */
    public ContentFragment getUnlockedRootFragment(ContentPage contentPage)
    {
        ContentFragment rootFragment = contentPage.getRootFragment();
        
        if (rootFragment.isLocked())
        {
            for (ContentFragment f : (List<ContentFragment>) rootFragment.getFragments())
            {
                if (!f.isLocked() && f.getType().equals(ContentFragment.LAYOUT))
                {
                    return f;
                }
            }
        }
        
        return rootFragment;
    }
    
    /**
     * Merge content page attributes from source PSML page.
     * 
     * @param contentPageImpl target content page
     * @param page source PSML page
     */
    private void mergeContentPageAttributes(ContentPageImpl contentPageImpl, BasePageElement page)
    {
        // merge content page attributes
        if ((contentPageImpl != null) && (page != null))
        {
            if ((page.getMetadata() != null) && (page.getMetadata().getFields() != null))
            {
                Iterator fieldIter = page.getMetadata().getFields().iterator();
                while (fieldIter.hasNext())
                {
                    LocalizedField field = (LocalizedField)fieldIter.next();
                    Locale fieldLocale = field.getLocale();
                    String fieldName = field.getName();
                    if (fieldName != null)
                    {
                        boolean containsField = false;
                        Iterator containsFieldIterator = contentPageImpl.getMetadata().getFields().iterator();
                        while (!containsField && containsFieldIterator.hasNext())
                        {
                            LocalizedField testField = (LocalizedField)containsFieldIterator.next();
                            Locale testFieldLocale = testField.getLocale();
                            String testFieldName = testField.getName();
                            containsField = (fieldName.equals(testFieldName) &&
                                    (((fieldLocale == null) && (testFieldLocale == null)) ||
                                            ((fieldLocale != null) && fieldLocale.equals(testFieldLocale))));
                        }
                        if (!containsField)
                        {
                            contentPageImpl.getMetadata().getFields().add(new ContentLocalizedFieldImpl(fieldLocale, fieldName, field.getValue()));
                        }
                    }
                }
            }
            String layoutDefaultDecorator = contentPageImpl.getDefaultDecorator(Fragment.LAYOUT);
            String portletDefaultDecorator = contentPageImpl.getDefaultDecorator(Fragment.PORTLET);
            Map defaultDecorators = null;
            if (layoutDefaultDecorator == null)
            {
                layoutDefaultDecorator = page.getDefaultDecorator(Fragment.LAYOUT);
                if (layoutDefaultDecorator != null)
                {
                    if (defaultDecorators == null)
                    {
                        defaultDecorators = new HashMap();
                    }
                    defaultDecorators.put(Fragment.LAYOUT, layoutDefaultDecorator);
                    if (portletDefaultDecorator != null)
                    {
                        defaultDecorators.put(Fragment.PORTLET, portletDefaultDecorator);
                    }
                }
            }
            if (portletDefaultDecorator == null)
            {
                portletDefaultDecorator = page.getDefaultDecorator(Fragment.PORTLET);
                if (portletDefaultDecorator != null)
                {
                    if (defaultDecorators == null)
                    {
                        defaultDecorators = new HashMap();
                    }
                    defaultDecorators.put(Fragment.PORTLET, portletDefaultDecorator);
                    if (layoutDefaultDecorator != null)
                    {
                        defaultDecorators.put(Fragment.LAYOUT, layoutDefaultDecorator);
                    }
                }
            }
            if (defaultDecorators != null)
            {
                contentPageImpl.setDefaultDecorators(defaultDecorators);
            }
            if (contentPageImpl.getShortTitle() == null)
            {
                contentPageImpl.setShortTitle(page.getShortTitle());
            }
            if (contentPageImpl.getSkin() == null)
            {
                contentPageImpl.setSkin(page.getSkin());
            }
            if (contentPageImpl.getTitle() == null)
            {
                contentPageImpl.setTitle(page.getTitle());
            }
        }
    }
    
    /**
     * Generate content fragment hierarchy from PSML fragments. Content fragment
     * ids are generated by concatenation of all parent ids to ensure that
     * unique ids are generated per fragment path.
     * 
     * @param parentId content fragment parent id or null 
     * @param page PSML page
     * @param fragmentDefinitions PSML fragment definitions
     * @param definition PSML fragment page, page template, or fragments definition
     * @param fragment PSML fragment
     * @param locked locked fragment flag
     * @return content fragment hierarchy or null if undefined
     */
    private ContentFragmentImpl newContentFragment(String parentId, BaseConcretePageElement page, Map fragmentDefinitions, BaseFragmentsElement definition, BaseFragmentElement fragment, boolean locked)
    {
        // generate content fragment hierarchy for specific fragment;
        // merges fragment hierarchy and attributes from fragments,
        // page fragments, and fragment references in page, page template,
        // and fragment definitions
        ContentFragmentImpl contentFragmentImpl = null;
        String contentFragmentId = null;
        if (fragment instanceof Fragment)
        {
            // construct content fragment to reflect fragment hierarchy
            Fragment fragmentFragment = (Fragment)fragment;
            contentFragmentId = ((parentId != null) ? parentId+"."+fragmentFragment.getId() : fragmentFragment.getId());
            contentFragmentImpl = newContentFragment(contentFragmentId, page, fragmentDefinitions, definition, fragmentFragment, null, null, locked);
            // set content fragment attributes
            mergeContentFragmentAttributes(contentFragmentImpl, fragmentFragment);
            // set content fragment security constraints
            setContentFragmentSecurityConstraints(contentFragmentImpl, fragmentFragment);
        }
        else if (fragment instanceof PageFragment)
        {
            // consume page fragment and build fragment hierarchy from page
            PageFragment pageFragmentFragment = (PageFragment)fragment;
            contentFragmentId = ((parentId != null) ? parentId+"."+pageFragmentFragment.getId() : pageFragmentFragment.getId());
            BaseFragmentElement pageRootFragment = page.getRootFragment();
            if (pageRootFragment instanceof FragmentReference)
            {
                // consume top level page fragment reference and build fragment
                // hierarchy from referenced fragment
                FragmentReference fragmentReferenceFragment = (FragmentReference)pageRootFragment;
                contentFragmentId += "."+fragmentReferenceFragment.getId();
                Fragment [] fragmentFragment = new Fragment[]{null};
                contentFragmentImpl = newContentFragment(contentFragmentId, page, fragmentDefinitions, page, fragmentReferenceFragment, fragmentFragment);
                // inherit page fragment attributes
                mergeContentFragmentAttributes(contentFragmentImpl, pageFragmentFragment);
                // inherit fragment reference attributes
                mergeContentFragmentAttributes(contentFragmentImpl, fragmentReferenceFragment);
                // set content fragment security constraints
                setContentFragmentSecurityConstraints(contentFragmentImpl, fragmentReferenceFragment);
                // merge content fragment attributes
                mergeContentFragmentAttributes(contentFragmentImpl, fragmentFragment[0]);
            }
            else if (pageRootFragment instanceof Fragment)
            {
                // construct content fragment to reflect page fragment hierarchy
                Fragment fragmentFragment = (Fragment)pageRootFragment;
                contentFragmentId += "."+fragmentFragment.getId();
                contentFragmentImpl = newContentFragment(contentFragmentId, page, fragmentDefinitions, page, fragmentFragment, null, null, false);
                // inherit page fragment attributes
                mergeContentFragmentAttributes(contentFragmentImpl, pageFragmentFragment);
                // merge content fragment attributes
                mergeContentFragmentAttributes(contentFragmentImpl, fragmentFragment);
                // set content fragment security constraints
                setContentFragmentSecurityConstraints(contentFragmentImpl, fragmentFragment);
            }
        }
        else if (fragment instanceof FragmentReference)
        {
            // consume fragment reference and build fragment hierarchy from
            // referenced fragment
            FragmentReference fragmentReferenceFragment = (FragmentReference)fragment;
            contentFragmentId = ((parentId != null) ? parentId+"."+fragmentReferenceFragment.getId() : fragmentReferenceFragment.getId());
            Fragment [] fragmentFragment = new Fragment[]{null};
            contentFragmentImpl = newContentFragment(contentFragmentId, page, fragmentDefinitions, definition, fragmentReferenceFragment, fragmentFragment);
            // inherit fragment reference attributes
            mergeContentFragmentAttributes(contentFragmentImpl, fragmentReferenceFragment);
            // set content fragment security constraints
            setContentFragmentSecurityConstraints(contentFragmentImpl, fragmentReferenceFragment);
            // merge content fragment attributes
            mergeContentFragmentAttributes(contentFragmentImpl, fragmentFragment[0]);
        }
        return contentFragmentImpl;
    }

    /**
     * Generate content fragment hierarchy from a PSML fragment reference.
     * 
     * @param parentId content fragment parent id 
     * @param page PSML page
     * @param fragmentDefinitions PSML fragment definitions
     * @param definition PSML fragment page, page template, or fragments definition
     * @param fragmentReference PSML fragment
     * @param fragmentFragment referenced root PSML fragment from fragment definition
     * @return content fragment hierarchy or null if undefined
     */
    private ContentFragmentImpl newContentFragment(String parentId, BaseConcretePageElement page, Map fragmentDefinitions, BaseFragmentsElement definition, FragmentReference fragmentReference, Fragment [] fragmentFragment)
    {
        // generate content fragment hierarchy for reference fragment from
        // fragment definition root fragment if located
        FragmentDefinition fragmentDefinition = (FragmentDefinition)((fragmentDefinitions != null) ? fragmentDefinitions.get(fragmentReference.getRefId()) : null);
        if ((fragmentDefinition != null) && (fragmentDefinition.getRootFragment() instanceof Fragment))
        {
            fragmentFragment[0] = (Fragment)fragmentDefinition.getRootFragment();
            String contentFragmentId = parentId+"."+fragmentFragment[0].getId();                
            return newContentFragment(contentFragmentId, page, fragmentDefinitions, fragmentDefinition, fragmentFragment[0], definition, fragmentReference, true);
        }
        return null;
    }

    /**
     * Generate content fragment hierarchy from a PSML fragment.
     * 
     * @param id content fragment id 
     * @param page PSML page
     * @param fragmentDefinitions PSML fragment definitions
     * @param definition PSML fragment page, page template, or fragments definition
     * @param fragment PSML fragment
     * @param reference PSML fragment reference
     * @param pageReference page fragment reference flag
     * @param locked locked fragment flag
     * @return content fragment hierarchy or null if undefined
     */
    private ContentFragmentImpl newContentFragment(String id, BaseConcretePageElement page, Map fragmentDefinitions, BaseFragmentsElement definition, Fragment fragment, BaseFragmentsElement referenceDefinition, FragmentReference reference, boolean locked)
    {
        // generate content fragment hierarchy for fragment
        ContentFragmentImpl contentFragmentImpl = new ContentFragmentImpl(this, id, page, definition, fragment, referenceDefinition, reference, locked);
        contentFragmentImpl.setName(fragment.getName());
        contentFragmentImpl.setType(fragment.getType());

        // generate nested content fragments
        List fragments = fragment.getFragments();
        if (fragments != null)
        {
            Iterator fragmentIter = fragments.iterator();
            while (fragmentIter.hasNext())
            {
                BaseFragmentElement childFragment = (BaseFragmentElement)fragmentIter.next();
                ContentFragmentImpl newContentFragment = newContentFragment(id, page, fragmentDefinitions, definition, childFragment, locked);
                if (newContentFragment != null)
                {
                    contentFragmentImpl.getFragments().add(newContentFragment);
                }
            }
        }

        log.debug("PageLayoutComponentImpl.newContentFragment(): constructed ContentFragment: id="+contentFragmentImpl.getId()+", name="+contentFragmentImpl.getName()+", locked="+contentFragmentImpl.isLocked());
        return contentFragmentImpl;
    }

    /**
     * Merge content fragment attributes from source PSML fragment.
     * 
     * @param contentFragmentImpl target content fragment
     * @param fragment source PSML fragment
     */
    private void mergeContentFragmentAttributes(ContentFragmentImpl contentFragmentImpl, BaseFragmentElement fragment)
    {
        // merge content fragment attributes
        if ((contentFragmentImpl != null) && (fragment != null))
        {
            if (contentFragmentImpl.getDecorator() == null)
            {
                contentFragmentImpl.setDecorator(fragment.getDecorator());
            }
            if (contentFragmentImpl.getLayoutColumn() <  0)
            {
                contentFragmentImpl.setLayoutColumn(fragment.getLayoutColumn());
            }
            if (contentFragmentImpl.getLayoutHeight() < 0.0F)
            {
                contentFragmentImpl.setLayoutHeight(fragment.getLayoutHeight());
            }
            if (contentFragmentImpl.getLayoutRow() < 0)
            {
                contentFragmentImpl.setLayoutRow(fragment.getLayoutRow());
            }
            if (contentFragmentImpl.getLayoutSizes() == null)
            {
                contentFragmentImpl.setLayoutSizes(fragment.getLayoutSizes());
            }
            if (contentFragmentImpl.getLayoutX() < 0.0F)
            {
                contentFragmentImpl.setLayoutX(fragment.getLayoutX());
            }
            if (contentFragmentImpl.getLayoutY() < 0.0F)
            {
                contentFragmentImpl.setLayoutY(fragment.getLayoutY());
            }
            if (contentFragmentImpl.getLayoutZ() < 0.0F)
            {
                contentFragmentImpl.setLayoutZ(fragment.getLayoutZ());
            }
            if (contentFragmentImpl.getLayoutWidth() < 0.0F)
            {
                contentFragmentImpl.setLayoutWidth(fragment.getLayoutWidth());
            }
            if (fragment.getProperties() != null)
            {
                Iterator propertiesIter = fragment.getProperties().iterator();
                while (propertiesIter.hasNext())
                {
                    boolean foundProperty = false;
                    FragmentProperty fragmentProperty = (FragmentProperty)propertiesIter.next();
                    String name = fragmentProperty.getName();
                    String scope = fragmentProperty.getScope();
                    String scopeValue = fragmentProperty.getScopeValue();
                    Iterator scanPropertiesIter = contentFragmentImpl.getProperties().iterator();
                    while (!foundProperty && scanPropertiesIter.hasNext())
                    {
                        FragmentProperty scanFragmentProperty = (FragmentProperty)scanPropertiesIter.next();
                        String scanName = scanFragmentProperty.getName();
                        if (name.equals(scanName))
                        {
                            String scanScope = scanFragmentProperty.getScope();
                            if ((scope == null) && (scanScope == null))
                            {
                                foundProperty = true;
                            }
                            else if ((scope != null) && scope.equals(scanScope))
                            {
                                String scanScopeValue = scanFragmentProperty.getScopeValue();
                                foundProperty = ((scopeValue != null) && scopeValue.equals(scanScopeValue));
                            }
                        }
                    }
                    if (!foundProperty)
                    {
                        contentFragmentImpl.getProperties().add(new ContentFragmentPropertyImpl(name, scope, scopeValue, fragmentProperty.getValue()));
                    }
                }
            }
            if (contentFragmentImpl.getMode() == null)
            {
                contentFragmentImpl.setMode(fragment.getMode());
            }
            if (fragment.getPreferences() != null)
            {
                Iterator preferencesIter = fragment.getPreferences().iterator();
                while (preferencesIter.hasNext())
                {
                    FragmentPreference preference = (FragmentPreference)preferencesIter.next();
                    String preferenceName = preference.getName();
                    if (preferenceName != null)
                    {
                        boolean containsPreference = false;
                        Iterator containsPreferenceIter = contentFragmentImpl.getPreferences().iterator();
                        while (!containsPreference && containsPreferenceIter.hasNext())
                        {
                            containsPreference = preferenceName.equals(((FragmentPreference)containsPreferenceIter.next()).getName());
                        }
                        if (!containsPreference)
                        {
                            contentFragmentImpl.getPreferences().add(new ContentFragmentPreferenceImpl(preferenceName, preference.isReadOnly(), preference.getValueList()));
                        }
                    }
                }
            }
            if (contentFragmentImpl.getShortTitle() == null)
            {
                contentFragmentImpl.setShortTitle(fragment.getShortTitle());
            }
            if (contentFragmentImpl.getSkin() == null)
            {
                contentFragmentImpl.setSkin(fragment.getSkin());
            }
            if (contentFragmentImpl.getState() == null)
            {
                contentFragmentImpl.setState(fragment.getState());
            }
            if (contentFragmentImpl.getTitle() == null)
            {
                contentFragmentImpl.setTitle(fragment.getTitle());
            }
        }
    }
    
    /**
     * Set content fragment security constraints from source PSML fragment.
     * 
     * @param contentFragmentImpl target content fragment
     * @param fragment source PSML fragment
     */
    private void setContentFragmentSecurityConstraints(ContentFragmentImpl contentFragmentImpl, BaseFragmentElement fragment)
    {
        // set content fragment attributes
        if ((contentFragmentImpl != null) && (fragment != null))
        {
            SecurityConstraints fragmentConstraints = fragment.getSecurityConstraints();
            if ((fragmentConstraints != null) && !fragmentConstraints.isEmpty())
            {
                String contentConstraintsOwner = fragmentConstraints.getOwner();
                List contentConstraintsConstraints = null;
                List fragmentConstraintsConstraints = fragmentConstraints.getSecurityConstraints();
                if ((fragmentConstraintsConstraints != null) && !fragmentConstraintsConstraints.isEmpty())
                {
                    contentConstraintsConstraints = new ArrayList(fragmentConstraintsConstraints.size());
                    Iterator constraintsIter = fragmentConstraintsConstraints.iterator();
                    while (constraintsIter.hasNext())
                    {
                        SecurityConstraint fragmentConstraint = (SecurityConstraint)constraintsIter.next();
                        contentConstraintsConstraints.add(new ContentSecurityConstraint(false, fragmentConstraint.getGroups(), fragmentConstraint.getPermissions(), fragmentConstraint.getRoles(), fragmentConstraint.getUsers()));
                    }
                }
                List contentConstraintsConstraintsRefs = null;
                List fragmentConstraintsConstraintsRefs = fragmentConstraints.getSecurityConstraintsRefs();
                if ((fragmentConstraintsConstraintsRefs != null) && !fragmentConstraintsConstraintsRefs.isEmpty())
                {
                    contentConstraintsConstraintsRefs = new ArrayList(fragmentConstraintsConstraintsRefs.size());
                    Iterator constraintsRefsIter = fragmentConstraintsConstraintsRefs.iterator();
                    while (constraintsRefsIter.hasNext())
                    {
                        contentConstraintsConstraintsRefs.add((String)constraintsRefsIter.next());
                    }
                }
                SecurityConstraints contentConstraints = new ContentSecurityConstraints(false, contentConstraintsOwner, contentConstraintsConstraints, contentConstraintsConstraintsRefs);
                contentFragmentImpl.setSecurityConstraints(contentConstraints);
            }
        }
    }
    
    /**
     * Shift document name by type in folder document order.
     * 
     * @param folder folder with target document order
     * @param documentName document name to add or shift
     * @param documentType document type to add or shift
     * @param decrement decrement or increment flag
     * @return update status
     */
    private boolean shiftDocumentOrder(Folder folder, String documentName, String documentType, boolean decrement)
    {
        // get folder document order
        List documentOrder = folder.getDocumentOrder();
        if (documentOrder == null)
        {
            documentOrder = new ArrayList(4);
            folder.setDocumentOrder(documentOrder);
        }
        // change document position in document order
        ListIterator orderIter = (decrement ? documentOrder.listIterator() : documentOrder.listIterator(documentOrder.size()));
        String lastOrderedDocumentName = null;
        int lastOrderedDocumentIndex = -1;
        while (decrement ? orderIter.hasNext() : orderIter.hasPrevious())
        {
            int orderedDocumentIndex = (decrement ? orderIter.nextIndex() : orderIter.previousIndex());
            String orderedDocumentName = (String)(decrement ? orderIter.next() : orderIter.previous());
            if (orderedDocumentName.equals(documentName))
            {
                boolean update = false; 
                if (lastOrderedDocumentIndex != -1)
                {
                    documentOrder.set(orderedDocumentIndex, lastOrderedDocumentName);
                    documentOrder.set(lastOrderedDocumentIndex, orderedDocumentName);
                    update = true;
                }
                return update;
            }
            else if (((documentType != null) && orderedDocumentName.endsWith(documentType)) ||
                     ((documentType == null) && (orderedDocumentName.indexOf('.') == -1)))
            {
                lastOrderedDocumentIndex = orderedDocumentIndex;
                lastOrderedDocumentName = orderedDocumentName;
            }
        }
        // append to document order
        documentOrder.add(documentName);
        return true;
    }

    /**
     * Remove document name in folder document order.
     * 
     * @param folder folder with target document order
     * @param documentName document name to add or shift
     * @return update status
     */
    private boolean removeDocumentOrder(Folder folder, String documentName)
    {
        List documentOrder = folder.getDocumentOrder();
        if (documentOrder != null)
        {
            Iterator orderIter = documentOrder.iterator();
            while (orderIter.hasNext())
            {
                String orderedDocumentName = (String)orderIter.next();
                if (orderedDocumentName.equals(documentName))
                {
                    orderIter.remove();
                    return true;
                }
            }
        }
        return false;
    }
        
    /**
     * Lookup page or page template fragment to be updated via
     * scoped fragment property based edits.
     * 
     * @param contentFragmentImpl target content fragment
     * @param scope target fragment property scope
     * @return fragment
     * @throws PageNotFoundException
     * @throws NodeException
     */
    private BaseFragmentElement lookupPageOrPageTemplateFragment(ContentFragmentImpl contentFragmentImpl, String scope) throws PageNotFoundException, NodeException
    {
        // validate content fragment
        boolean contentFragmentDefinitionIsPage = ((contentFragmentImpl.getDefinition() instanceof BaseConcretePageElement) && contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPage().getPath()));
        boolean contentFragmentDefinitionIsTemplate = (contentFragmentImpl.getDefinition() instanceof PageTemplate);
        boolean contentFragmentDefinitionIsPageReference = ((contentFragmentImpl.getDefinition() instanceof FragmentDefinition) && (contentFragmentImpl.getReferenceDefinition() instanceof BaseConcretePageElement) && (contentFragmentImpl.getReference() != null));
        boolean contentFragmentDefinitionIsTemplateReference = ((contentFragmentImpl.getDefinition() instanceof FragmentDefinition) && (contentFragmentImpl.getReferenceDefinition() instanceof PageTemplate) && (contentFragmentImpl.getReference() != null));
        boolean userScopedUpdate = ((scope != null) && scope.equals(FragmentProperty.USER_PROPERTY_SCOPE));
        if (!contentFragmentDefinitionIsPage && !contentFragmentDefinitionIsPageReference && (!userScopedUpdate || (!contentFragmentDefinitionIsTemplate && !contentFragmentDefinitionIsTemplateReference)))
        {
            if (userScopedUpdate)
            {
                throw new IllegalArgumentException("Only page fragments, fragment references, and template fragments are user scope mutable");
            }
            else
            {
                throw new IllegalArgumentException("Only page fragments and fragment references are mutable");
            }
        }
        
        // retrieve current fragment using page or page template from page manager
        BaseFragmentElement fragment = null;
        if (contentFragmentDefinitionIsPage || contentFragmentDefinitionIsPageReference)
        {
            BaseConcretePageElement page = getPage(contentFragmentImpl.getPage().getPath());
            String pageFragmentId = (contentFragmentDefinitionIsPage ? contentFragmentImpl.getFragment().getId() : contentFragmentImpl.getReference().getId());
            fragment = page.getFragmentById(pageFragmentId);
        }
        else if (contentFragmentDefinitionIsTemplate || contentFragmentDefinitionIsTemplateReference)
        {
            String pageTemplatePath = (contentFragmentDefinitionIsTemplate ? contentFragmentImpl.getDefinition().getPath() : contentFragmentImpl.getReferenceDefinition().getPath());
            PageTemplate pageTemplate = pageManager.getPageTemplate(pageTemplatePath);
            String pageTemplateFragmentId = (contentFragmentDefinitionIsTemplate ? contentFragmentImpl.getFragment().getId() : contentFragmentImpl.getReference().getId());
            fragment = pageTemplate.getFragmentById(pageTemplateFragmentId);
        }
        if (fragment == null)
        {
            throw new IllegalArgumentException("Fragment and page not consistent");                
        }
        
        // check for edit or view permission
        boolean checkEditAccess = ((scope == null) || !scope.equals(FragmentProperty.USER_PROPERTY_SCOPE));
        fragment.checkAccess(checkEditAccess ? JetspeedActions.EDIT : JetspeedActions.VIEW);
        
        return fragment;
    }

    /**
     * Update page or page template fragment to save scoped
     * fragment property based edits.
     * 
     * @param fragment edited fragment
     * @param scope edited fragment property scope
     * @throws PageNotUpdatedException
     * @throws NodeException
     */
    private void updatePageOrPageTemplateFragmentProperties(BaseFragmentElement fragment, String scope) throws PageNotUpdatedException, NodeException
    {
        // update page or page template fragment properties
        pageManager.updateFragmentProperties(fragment, scope);
    }

    /**
     * Get page or dynamic page from page manager.
     * 
     * @param pagePath path to page
     * @return page or dynamic page
     * @throws PageNotFoundException
     * @throws NodeException
     */
    private BaseConcretePageElement getPage(String pagePath) throws PageNotFoundException, NodeException
    {
        if (pagePath.endsWith(Page.DOCUMENT_TYPE))
        {
            return pageManager.getPage(pagePath);
        }
        if (pagePath.endsWith(DynamicPage.DOCUMENT_TYPE))
        {
            return pageManager.getDynamicPage(pagePath);            
        }
        throw new PageNotFoundException("Unable to classify page path by type: "+pagePath);
    }
    
    /**
     * Update page or dynamic page using page manager.
     * 
     * @param page page or dynamic page to update
     * @throws NodeException
     * @throws PageNotUpdatedException
     */
    private void updatePage(BaseConcretePageElement page) throws NodeException, PageNotUpdatedException
    {
        if (page instanceof Page)
        {
            pageManager.updatePage((Page)page);
            return;
        }
        if (page instanceof DynamicPage)
        {
            pageManager.updateDynamicPage((DynamicPage)page);
            return;
        }
        throw new PageNotUpdatedException("Unable to classify page by type: "+((page != null) ? page.getClass().getName() : "null"));
    }

    /**
     * Remove page or dynamic page using page manager.
     * 
     * @param page page or dynamic page to update
     * @throws NodeException
     * @throws PageNotRemovedException
     */
    private void removePage(BaseConcretePageElement page) throws NodeException, PageNotRemovedException
    {
        if (page instanceof Page)
        {
            pageManager.removePage((Page)page);
            return;
        }
        if (page instanceof DynamicPage)
        {
            pageManager.removeDynamicPage((DynamicPage)page);
            return;
        }
        throw new PageNotRemovedException("Unable to classify page by type: "+((page != null) ? page.getClass().getName() : "null"));
    }
}
