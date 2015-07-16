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

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.BaseConcretePageElement;
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
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.PageNotRemovedException;
import org.apache.jetspeed.page.PageNotUpdatedException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.pluto.container.PortletPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

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
    private String defaultLayoutPortletName;
    
    /**
     * Construct new PageLayoutComponent implementation.
     * 
     * @param pageManager page manager used to access PSML objects
     * @param defaultLayoutPortletName default layout portlet name used to construct content
     *                                 pages for PSML objects w/o a root layout fragment
     */
    public PageLayoutComponentImpl(PageManager pageManager, String defaultLayoutPortletName)
    {
        this.pageManager = pageManager;
        this.defaultLayoutPortletName = defaultLayoutPortletName;
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
            if ((pageRootContentFragmentImpl != null) && pageRootContentFragmentImpl.isLocked())
            {
                pageRootContentFragmentImpl = (ContentFragmentImpl)pageRootContentFragmentImpl.getNonTemplateLayoutFragment();
            }
            if ((pageRootContentFragmentImpl == null) || pageRootContentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Page root content fragment not found or is locked");                
            }

            // retrieve current page or template and root fragment from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            BaseFragmentElement rootFragment = pageOrTemplate.getRootFragment();
            if (!(rootFragment instanceof Fragment))
            {
                throw new IllegalArgumentException("New fragment cannot be added to page root fragment");                
            }
            Fragment fragment = (Fragment)rootFragment;
            if (!Fragment.LAYOUT.equals(fragment.getType()))
            {
                throw new IllegalArgumentException("New fragment cannot be added to non-layout page root fragment");                
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
            updatePage(pageOrTemplate);
            
            // update content page context
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            contentFragmentImpl.initialize(this, pageOrTemplate, pageOrTemplate, newFragment, null, null, false, false);
            if (!Utils.isNull(row))
            {
                contentFragmentImpl.setLayoutRow(null, null, row);
            }
            if (!Utils.isNull(column))
            {
                contentFragmentImpl.setLayoutColumn(null, null, column);
            }
            pageRootContentFragmentImpl.getFragments().add(contentFragmentImpl);
            String newContentFragmentId = pageRootContentFragmentImpl.getId()+CONTENT_FRAGMENT_ID_SEPARATOR+contentFragmentImpl.getFragment().getId();
            contentFragmentImpl.setId(newContentFragmentId);            
            return contentFragmentImpl;
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#addFragmentReference(org.apache.jetspeed.om.page.ContentFragment, java.lang.String, int, int)
     */
    public ContentFragment addFragmentReference(ContentFragment contentFragment, String id, int row, int column)
    {
        log.debug("PageLayoutComponentImpl.addFragmentReference() invoked");
        try
        {
            // validate fragment definition parameters
            if (Utils.isNull(id))
            {
                throw new IllegalArgumentException("Fragment definition id not specified");
            }
            
            // validate content fragment
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            if (contentFragmentImpl.getDefinition() == null)
            {
                throw new IllegalArgumentException("Transient content fragments are not mutable");
            }            
            if (contentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Locked content fragment is not mutable");
            }
            if (!contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPageOrTemplate().getPath()))
            {
                throw new IllegalArgumentException("Only page fragments can be modified");
            }

            // retrieve current page or template and fragment from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentFragmentImpl.getPageOrTemplate().getPath());
            BaseFragmentElement parentFragment = pageOrTemplate.getFragmentById(contentFragmentImpl.getFragment().getId());
            if (!(parentFragment instanceof Fragment))
            {
                throw new IllegalArgumentException("New fragment cannot be added to parent fragment");
            }
            Fragment fragment = (Fragment)parentFragment;
            if (!Fragment.LAYOUT.equals(fragment.getType()))
            {
                throw new IllegalArgumentException("New fragment cannot be added to non-layout parent fragment");                
            }
            
            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);

            // create fragment reference and add to layout fragment
            FragmentReference newFragmentReference = pageManager.newFragmentReference();
            newFragmentReference.setRefId(id);
            if (!Utils.isNull(row))
            {
                newFragmentReference.setLayoutRow(row);
            }
            if (!Utils.isNull(column))
            {
                newFragmentReference.setLayoutColumn(column);
            }
            fragment.getFragments().add(newFragmentReference);

            // update page in page manager
            updatePage(pageOrTemplate);

            // update content context
            ContentFragmentImpl newContentFragmentImpl = newContentFragment(contentFragmentImpl.getId(), pageOrTemplate, pageOrTemplate, newFragmentReference);
            contentFragmentImpl.getFragments().add(newContentFragmentImpl);
            return newContentFragmentImpl;
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#addFragmentReference(org.apache.jetspeed.om.page.ContentFragment, java.lang.String)
     */
    public ContentFragment addFragmentReference(ContentFragment contentFragment, String id)
    {
        return addFragmentReference(contentFragment, id, -1, -1);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#addFragmentReference(org.apache.jetspeed.om.page.ContentPage, java.lang.String)
     */
    public ContentFragment addFragmentReference(ContentPage contentPage, String id)
    {
        log.debug("PageLayoutComponentImpl.addFragmentReference() invoked");
        try
        {
            // validate fragment definition parameters
            if (Utils.isNull(id))
            {
                throw new IllegalArgumentException("Fragment definition id not specified");
            }
            
            // get page root content fragment
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            ContentFragmentImpl pageRootContentFragmentImpl = contentPageImpl.getPageRootContentFragment();
            if ((pageRootContentFragmentImpl != null) && pageRootContentFragmentImpl.isLocked())
            {
                pageRootContentFragmentImpl = (ContentFragmentImpl)pageRootContentFragmentImpl.getNonTemplateLayoutFragment();
            }
            if ((pageRootContentFragmentImpl == null) || pageRootContentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Page root content fragment not found or is locked");                
            }

            // retrieve current page or template and root fragment from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPage.getPageOrTemplate().getPath());
            BaseFragmentElement rootFragment = pageOrTemplate.getRootFragment();
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
            
            // create fragment reference and add to layout root fragment
            FragmentReference newFragmentReference = pageManager.newFragmentReference();
            newFragmentReference.setRefId(id);
            fragment.getFragments().add(newFragmentReference);

            // update page in page manager
            updatePage(pageOrTemplate);

            // update content page context
            ContentFragmentImpl newContentFragmentImpl = newContentFragment(pageRootContentFragmentImpl.getId(), pageOrTemplate, pageOrTemplate, newFragmentReference);
            pageRootContentFragmentImpl.getFragments().add(newContentFragmentImpl);
            return newContentFragmentImpl;
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
        
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
            if (contentFragmentImpl.getDefinition() == null)
            {
                throw new IllegalArgumentException("Transient content fragments are not mutable");
            }            
            if (contentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Locked content fragment is not mutable");
            }
            if (!contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPageOrTemplate().getPath()))
            {
                throw new IllegalArgumentException("Only page fragments can be modified");
            }

            // retrieve current page or template and fragment from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentFragmentImpl.getPageOrTemplate().getPath());
            BaseFragmentElement parentFragment = pageOrTemplate.getFragmentById(contentFragmentImpl.getFragment().getId());
            if (!(parentFragment instanceof Fragment))
            {
                throw new IllegalArgumentException("New fragment cannot be added to parent fragment");                
            }
            Fragment fragment = (Fragment)parentFragment;
            if (!Fragment.LAYOUT.equals(fragment.getType()))
            {
                throw new IllegalArgumentException("New fragment cannot be added to non-layout parent fragment");                
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
            updatePage(pageOrTemplate);

            // update content context
            ContentFragmentImpl newContentFragmentImpl = newContentFragment(contentFragmentImpl.getId(), pageOrTemplate, null, pageOrTemplate, newFragment, false);
            contentFragmentImpl.getFragments().add(newContentFragmentImpl);
            return newContentFragmentImpl;
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
            if ((pageRootContentFragmentImpl != null) && pageRootContentFragmentImpl.isLocked())
            {
                pageRootContentFragmentImpl = (ContentFragmentImpl)pageRootContentFragmentImpl.getNonTemplateLayoutFragment();
            }
            if ((pageRootContentFragmentImpl == null) || pageRootContentFragmentImpl.isLocked())
            {
                throw new IllegalArgumentException("Page root content fragment not found or is locked");                
            }

            // retrieve current page or template and root fragment from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPage.getPageOrTemplate().getPath());
            BaseFragmentElement rootFragment = pageOrTemplate.getRootFragment();
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
            updatePage(pageOrTemplate);

            // update content page context
            ContentFragmentImpl newContentFragmentImpl = newContentFragment(pageRootContentFragmentImpl.getId(), pageOrTemplate, null, pageOrTemplate, newFragment, false);
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
            // retrieve current page or template and parent folders from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplates = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            Folder pageFolder = (Folder)pageOrTemplates.getParent();
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
            // retrieve current page or template and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            Folder documentOrderingFolder = (Folder)pageOrTemplate.getParent();

            // check for edit permission
            documentOrderingFolder.checkAccess(JetspeedActions.EDIT);

            // shift document order and update document ordering in page manager
            boolean update = shiftDocumentOrder(documentOrderingFolder, pageOrTemplate.getName(), pageOrTemplate.getType(), true);
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
            // retrieve current page or template and parent folders from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            Folder pageFolder = (Folder)pageOrTemplate.getParent();
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
            // retrieve current page or template and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            Folder documentOrderingFolder = (Folder)pageOrTemplate.getParent();

            // check for edit permission
            documentOrderingFolder.checkAccess(JetspeedActions.EDIT);

            // shift document order and update document ordering in page manager
            boolean update = shiftDocumentOrder(documentOrderingFolder, pageOrTemplate.getName(), pageOrTemplate.getType(), false);
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
            if ((fromContentFragmentImpl.getDefinition() == null) || (contentFragmentImpl.getDefinition() == null) || (toContentFragmentImpl.getDefinition() == null))
            {
                throw new IllegalArgumentException("Transient content fragments are not mutable");
            }            
            if (!fromContentFragmentImpl.getDefinition().getPath().equals(contentPageImpl.getPath()) ||
                !contentFragmentImpl.getDefinition().getPath().equals(contentPageImpl.getPath()) ||
                !toContentFragmentImpl.getDefinition().getPath().equals(contentPageImpl.getPath()))
            {
                throw new IllegalArgumentException("Cannot use move fragment operation between pages");                
            }
            boolean contentFragmentIsReference = (contentFragmentImpl.getReference() != null);
            
            // retrieve current page or template and fragments from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            BaseFragmentElement fromFragmentElement = pageOrTemplate.getFragmentById(fromContentFragmentImpl.getFragment().getId());
            if (!(fromFragmentElement instanceof Fragment))
            {
                throw new IllegalArgumentException("Move from fragmentId and page not consistent");
            }
            Fragment fromFragment = (Fragment)fromFragmentElement;
            String pageFragmentId = (contentFragmentIsReference ? contentFragmentImpl.getReference().getId() : contentFragmentImpl.getFragment().getId());
            BaseFragmentElement fragment = fromFragment.getFragmentById(pageFragmentId);
            if (fragment == null)
            {
                throw new IllegalArgumentException("Move fragmentId and page not consistent");                
            }
            BaseFragmentElement toFragmentElement = pageOrTemplate.getFragmentById(toContentFragmentImpl.getFragment().getId());
            if (!(toFragmentElement instanceof Fragment))
            {
                throw new IllegalArgumentException("Move to fragmentId and page not consistent");
            }
            Fragment toFragment = (Fragment)toFragmentElement;

            // check for edit permission
            pageOrTemplate.checkAccess(JetspeedActions.EDIT);

            // move page fragment and update page in page manager
            fragment = fromFragment.removeFragmentById(fragment.getId());
            toFragment.getFragments().add(fragment);
            updatePage(pageOrTemplate);

            // update content context
            fromContentFragmentImpl.removeFragmentById(fragmentId);
            toContentFragmentImpl.getFragments().add(contentFragmentImpl);
            String newContentFragmentId = toContentFragmentImpl.getId()+CONTENT_FRAGMENT_ID_SEPARATOR+contentFragmentImpl.getFragment().getId();
            contentFragmentImpl.setId(newContentFragmentId);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#newContentPage(org.apache.jetspeed.om.page.BaseFragmentsElement, org.apache.jetspeed.om.page.PageTemplate, java.util.Map)
     */
    public ContentPage newContentPage(BaseFragmentsElement pageOrTemplate, PageTemplate pageTemplate, Map<String, FragmentDefinition> fragmentDefinitions)
    {
        // generate content page
        BaseConcretePageElement concretePage = ((pageOrTemplate instanceof BaseConcretePageElement) ? (BaseConcretePageElement)pageOrTemplate : null);
        DynamicPage dynamicPage = ((pageOrTemplate instanceof DynamicPage) ? (DynamicPage)pageOrTemplate : null);
        FragmentDefinition fragmentDefinition = (((concretePage == null) && (pageOrTemplate instanceof FragmentDefinition)) ? (FragmentDefinition)pageOrTemplate : null);
        String contentPageId = pageOrTemplate.getId();
        ContentPageImpl contentPageImpl = new ContentPageImpl(this, contentPageId, pageOrTemplate, pageTemplate, fragmentDefinitions);
        // set/merge page attributes
        mergeContentPageAttributes(contentPageImpl, pageOrTemplate);
        contentPageImpl.setName(pageOrTemplate.getName());
        contentPageImpl.setPath(pageOrTemplate.getPath());
        contentPageImpl.setUrl(pageOrTemplate.getUrl());
        contentPageImpl.setHidden(pageOrTemplate.isHidden());        
        // merge template attributes
        mergeContentPageAttributes(contentPageImpl, pageTemplate);            
        // set effective default detectors from merged default
        // decorators or inherit page effective default decorators
        Map effectiveDefaultDecorators = null;
        String effectiveLayoutDefaultDecorator = contentPageImpl.getDefaultDecorator(Fragment.LAYOUT);
        if (effectiveLayoutDefaultDecorator == null)
        {
            if (concretePage != null)
            {
                effectiveLayoutDefaultDecorator = concretePage.getEffectiveDefaultDecorator(Fragment.LAYOUT);
            }
            else if (pageOrTemplate.getParent() != null)
            {
                effectiveLayoutDefaultDecorator = ((Folder)pageOrTemplate.getParent()).getEffectiveDefaultDecorator(Fragment.LAYOUT);
            }
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
            if (concretePage != null)
            {
                effectivePortletDefaultDecorator = concretePage.getEffectiveDefaultDecorator(Fragment.PORTLET);
            }
            else if (pageOrTemplate.getParent() != null)
            {
                effectiveLayoutDefaultDecorator = ((Folder)pageOrTemplate.getParent()).getEffectiveDefaultDecorator(Fragment.PORTLET);
            }
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
        if (dynamicPage != null)
        {
            contentPageImpl.setContentType(dynamicPage.getContentType());
            contentPageImpl.setInheritable(dynamicPage.isInheritable());
        }
        if (fragmentDefinition != null)
        {
            contentPageImpl.setDefId(fragmentDefinition.getDefId());
        }
        
        log.debug("PageLayoutComponentImpl.newContentPage(): construct ContentPage: id="+contentPageImpl.getId()+", path="+contentPageImpl.getPath());
        
        // generate root and nested content fragments
        BaseFragmentsElement definition = null;
        BaseFragmentElement rootFragment = null;
        boolean rootTemplate = false;
        if (pageTemplate != null)
        {
            definition = pageTemplate;
            rootFragment = definition.getRootFragment();
            rootTemplate = true;
        }
        if (rootFragment == null)
        {
            definition = pageOrTemplate;
            rootFragment = definition.getRootFragment();
        }
        if (rootFragment != null)
        {
            // generate content page/fragment hierarchy for page            
            ContentFragmentImpl rootContentFragmentImpl = newContentFragment(null, pageOrTemplate, fragmentDefinitions, definition, rootFragment, rootTemplate);
            // ensure that page/fragment hierarchy root is a layout
            // portlet by generating a transient locked parent
            // layout fragment if necessary
            if (!rootContentFragmentImpl.getType().equals(ContentFragment.LAYOUT))
            {
                ContentFragmentImpl layoutContentFragmentImpl = newContentFragment("", pageOrTemplate, null, null, null, null, rootTemplate, true);
                layoutContentFragmentImpl.setType(ContentFragment.LAYOUT);
                layoutContentFragmentImpl.setName(defaultLayoutPortletName);
                layoutContentFragmentImpl.getFragments().add(rootContentFragmentImpl);
                rootContentFragmentImpl = layoutContentFragmentImpl;                
            }
            // save content page/fragment hierarchy
            contentPageImpl.setRootFragment(rootContentFragmentImpl);
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
            // retrieve current page or template and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            BasePageElement page = ((pageOrTemplate instanceof BasePageElement) ? (BasePageElement)pageOrTemplate : null);
            Folder folder = (Folder)pageOrTemplate.getParent();
            
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
            String defaultLayoutDecorator = null;
            String defaultPortletDecorator = null;
            if (page != null)
            {
                defaultLayoutDecorator = page.getDefaultDecorator(Fragment.LAYOUT);
                if (defaultLayoutDecorator != null)
                {
                    newFolder.setDefaultDecorator(defaultLayoutDecorator, Fragment.LAYOUT);
                }
                defaultPortletDecorator = page.getDefaultDecorator(Fragment.PORTLET);
                if (defaultPortletDecorator != null)
                {
                    newFolder.setDefaultDecorator(defaultPortletDecorator, Fragment.PORTLET);
                }
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
        log.debug("PageLayoutComponentImpl.newSiblingPage() invoked");
        try
        {
            // retrieve current page or template and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            BasePageElement page = ((pageOrTemplate instanceof BasePageElement) ? (BasePageElement)pageOrTemplate : null);
            Folder folder = (Folder)pageOrTemplate.getParent();

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
            String defaultLayoutDecorator = null;
            String defaultPortletDecorator = null;
            if (page != null)
            {
                defaultLayoutDecorator = page.getDefaultDecorator(Fragment.LAYOUT);
                if (defaultLayoutDecorator != null)
                {
                    newPage.setDefaultDecorator(defaultLayoutDecorator, Fragment.LAYOUT);
                }
                defaultPortletDecorator = page.getDefaultDecorator(Fragment.PORTLET);
                if (defaultPortletDecorator != null)
                {
                    newPage.setDefaultDecorator(defaultPortletDecorator, Fragment.PORTLET);
                }
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
     * @see org.apache.jetspeed.layout.PageLayoutComponent#newSiblingDynamicPage(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingDynamicPage(ContentPage contentPage, String pageName, String contentType, String layoutName, String pageTitle, String pageShortTitle)
    {
        log.debug("PageLayoutComponentImpl.newSiblingDynamicPage() invoked");
        try
        {
            // retrieve current page or template and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            BasePageElement page = ((pageOrTemplate instanceof BasePageElement) ? (BasePageElement)pageOrTemplate : null);
            Folder folder = (Folder)pageOrTemplate.getParent();

            // check for edit permission
            folder.checkAccess(JetspeedActions.EDIT);

            // construct new sibling dynamic page
            String newDynamicPagePath = folder.getPath()+(folder.getPath().endsWith(Folder.PATH_SEPARATOR) ? "" : Folder.PATH_SEPARATOR)+pageName+DynamicPage.DOCUMENT_TYPE;
            if (pageManager.dynamicPageExists(newDynamicPagePath))
            {
                throw new IllegalArgumentException("Dynamic page "+newDynamicPagePath+" exists");
            }
            DynamicPage newDynamicPage = pageManager.newDynamicPage(newDynamicPagePath);
            newDynamicPage.setContentType(!Utils.isNull(contentType) ? contentType : "*");
            if (!Utils.isNull(layoutName) && (newDynamicPage.getRootFragment() instanceof Fragment))
            {
                ((Fragment)newDynamicPage.getRootFragment()).setName(layoutName);
            }
            if (!Utils.isNull(pageTitle))
            {
                newDynamicPage.setTitle(pageTitle);
            }
            if (!Utils.isNull(pageShortTitle))
            {
                newDynamicPage.setShortTitle(pageShortTitle);
            }
            String defaultLayoutDecorator = null;
            String defaultPortletDecorator = null;
            if (page != null)
            {
                defaultLayoutDecorator = page.getDefaultDecorator(Fragment.LAYOUT);
                if (defaultLayoutDecorator != null)
                {
                    newDynamicPage.setDefaultDecorator(defaultLayoutDecorator, Fragment.LAYOUT);
                }
                defaultPortletDecorator = page.getDefaultDecorator(Fragment.PORTLET);
                if (defaultPortletDecorator != null)
                {
                    newDynamicPage.setDefaultDecorator(defaultPortletDecorator, Fragment.PORTLET);
                }
            }

            // update new dynamic page in page manager
            pageManager.updateDynamicPage(newDynamicPage);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#newSiblingPageTemplate(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingPageTemplate(ContentPage contentPage, String templateName, String layoutName, String templateTitle, String templateShortTitle)
    {
        log.debug("PageLayoutComponentImpl.newSiblingPageTemplate() invoked");
        try
        {
            // retrieve current page or template and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            BasePageElement page = ((pageOrTemplate instanceof BasePageElement) ? (BasePageElement)pageOrTemplate : null);
            Folder folder = (Folder)pageOrTemplate.getParent();

            // check for edit permission
            folder.checkAccess(JetspeedActions.EDIT);

            // construct new sibling page template
            String newPageTemplatePath = folder.getPath()+(folder.getPath().endsWith(Folder.PATH_SEPARATOR) ? "" : Folder.PATH_SEPARATOR)+templateName+PageTemplate.DOCUMENT_TYPE;
            if (pageManager.pageTemplateExists(newPageTemplatePath))
            {
                throw new IllegalArgumentException("Page template "+newPageTemplatePath+" exists");
            }
            PageTemplate newPageTemplate = pageManager.newPageTemplate(newPageTemplatePath);
            if (newPageTemplate.getRootFragment() instanceof Fragment)
            {
                Fragment rootFragment = (Fragment)newPageTemplate.getRootFragment();
                if (!Utils.isNull(layoutName))
                {
                    rootFragment.setName(layoutName);
                }
                rootFragment.getFragments().add(pageManager.newPageFragment());
            }
            if (!Utils.isNull(templateTitle))
            {
                newPageTemplate.setTitle(templateTitle);
            }
            if (!Utils.isNull(templateShortTitle))
            {
                newPageTemplate.setShortTitle(templateShortTitle);
            }
            String defaultLayoutDecorator = null;
            String defaultPortletDecorator = null;
            if (page != null)
            {
                defaultLayoutDecorator = page.getDefaultDecorator(Fragment.LAYOUT);
                if (defaultLayoutDecorator != null)
                {
                    newPageTemplate.setDefaultDecorator(defaultLayoutDecorator, Fragment.LAYOUT);
                }
                defaultPortletDecorator = page.getDefaultDecorator(Fragment.PORTLET);
                if (defaultPortletDecorator != null)
                {
                    newPageTemplate.setDefaultDecorator(defaultPortletDecorator, Fragment.PORTLET);
                }
            }

            // update new page template in page manager
            pageManager.updatePageTemplate(newPageTemplate);
        }
        catch (Exception e)
        {
            throw new PageLayoutComponentException("Unexpected exception: "+e, e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.layout.PageLayoutComponent#newSiblingFragmentDefinition(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingFragmentDefinition(ContentPage contentPage, String definitionName, String defId, String portletName, String definitionTitle, String definitionShortTitle)
    {
        log.debug("PageLayoutComponentImpl.newSiblingFragmentDefinition() invoked");
        try
        {
            // retrieve current page or template and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            Folder folder = (Folder)pageOrTemplate.getParent();

            // check for edit permission
            folder.checkAccess(JetspeedActions.EDIT);

            // construct new sibling fragment definition
            String newFragmentDefinitionPath = folder.getPath()+(folder.getPath().endsWith(Folder.PATH_SEPARATOR) ? "" : Folder.PATH_SEPARATOR)+definitionName+FragmentDefinition.DOCUMENT_TYPE;
            if (pageManager.fragmentDefinitionExists(newFragmentDefinitionPath))
            {
                throw new IllegalArgumentException("PageTemplate "+newFragmentDefinitionPath+" exists");
            }
            FragmentDefinition newFragmentDefinition = pageManager.newFragmentDefinition(newFragmentDefinitionPath);
            if (newFragmentDefinition.getRootFragment() instanceof Fragment)
            {
                Fragment rootFragment = (Fragment)newFragmentDefinition.getRootFragment();                
                if (!Utils.isNull(defId))
                {
                    rootFragment.setId(defId);
                }
                if (!Utils.isNull(portletName))
                {
                    rootFragment.setType(Fragment.PORTLET);
                    rootFragment.setName(portletName);
                }
            }
            if (!Utils.isNull(definitionTitle))
            {
                newFragmentDefinition.setTitle(definitionTitle);
            }
            if (!Utils.isNull(definitionShortTitle))
            {
                newFragmentDefinition.setShortTitle(definitionShortTitle);
            }

            // update new fragment definition in page manager
            pageManager.updateFragmentDefinition(newFragmentDefinition);
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
            // retrieve current page or template and document ordering folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplates = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            String documentName = pageOrTemplates.getName();
            Folder documentOrderingFolder = (Folder)pageOrTemplates.getParent();

            // check for edit permission
            pageOrTemplates.checkAccess(JetspeedActions.EDIT);

            // remove in page manager
            removePage(pageOrTemplates);

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
            // retrieve current page or template and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            Folder folder = (Folder)pageOrTemplate.getParent();
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
            if (contentFragmentImpl.getDefinition() == null)
            {
                throw new IllegalArgumentException("Transient content fragments are not mutable");
            }            
            if ((contentFragmentImpl == null) || !contentFragmentImpl.getDefinition().getPath().equals(contentPageImpl.getPath()))
            {
                throw new IllegalArgumentException("FragmentId and page not consistent");                
            }
            if ((parentContentFragmentImpl[0] != null) && (!parentContentFragmentImpl[0].getDefinition().getPath().equals(contentPageImpl.getPageOrTemplate().getPath()) ||
                                                           parentContentFragmentImpl[0].isLocked()))
            {
                throw new IllegalArgumentException("Parent content fragment and page not consistent or locked");                
            }
            boolean contentFragmentIsReference = (contentFragmentImpl.getReference() != null);
            
            // retrieve current page or template and fragment from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            String pageFragmentId = (contentFragmentIsReference ? contentFragmentImpl.getReference().getId() : contentFragmentImpl.getFragment().getId());
            BaseFragmentElement fragment = pageOrTemplate.getFragmentById(pageFragmentId);
            if (fragment == null)
            {
                throw new IllegalArgumentException("Remove fragmentId and page not consistent");                
            }

            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);
            
            // remove fragment and update in page manager
            boolean update = (pageOrTemplate.removeFragmentById(pageFragmentId) != null);
            if (update)
            {
                updatePage(pageOrTemplate);
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
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateContent(org.apache.jetspeed.om.page.ContentPage, java.lang.String, java.lang.Boolean)
     */
    public void updateContent(ContentPage contentPage, String contentType, Boolean inheritable)
    {
        log.debug("PageLayoutComponentImpl.updateContent() invoked");
        try
        {
            // retrieve current dynamic page from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            if (!(pageOrTemplate instanceof DynamicPage))
            {
                throw new IllegalArgumentException("Content only supported on dynamic pages.");
            }
            DynamicPage dynamicPage = (DynamicPage)pageOrTemplate;
            
            // check for edit permission
            dynamicPage.checkAccess(JetspeedActions.EDIT);            

            // update default decorator and page in page manager
            boolean update = false;
            if (!Utils.isNull(contentType) && !contentType.equals(dynamicPage.getContentType()))
            {
                dynamicPage.setContentType(contentType);
                update = true;
            }
            if ((inheritable != null) && (inheritable.booleanValue() != dynamicPage.isInheritable()))
            {
                dynamicPage.setInheritable(inheritable.booleanValue());
                update = true;
            }
            if (update)
            {
                updatePage(dynamicPage);
            }

            // update content context
            if (!Utils.isNull(contentType))
            {
                contentPageImpl.setContentType(contentType);
            }
            if (inheritable != null)
            {
                contentPageImpl.setInheritable(inheritable.booleanValue());
            }
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
            BaseFragmentElement fragment = lookupPageOrTemplateFragment(contentFragmentImpl, scope);
            
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
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            if (!(pageOrTemplate instanceof BasePageElement))
            {
                throw new IllegalArgumentException("Decorators only supported on pages.");
            }
            BasePageElement page = (BasePageElement)pageOrTemplate;
            BaseConcretePageElement concretePage = ((page instanceof BaseConcretePageElement) ? (BaseConcretePageElement)page : null);
            
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
            if (concretePage != null)
            {
                effectiveDefaultDecorators.put(Fragment.LAYOUT, concretePage.getEffectiveDefaultDecorator(Fragment.LAYOUT));
                effectiveDefaultDecorators.put(Fragment.PORTLET, concretePage.getEffectiveDefaultDecorator(Fragment.PORTLET));
            }
            else
            {
                String layoutDefaultDecorator = page.getDefaultDecorator(Fragment.LAYOUT);
                if (layoutDefaultDecorator != null)
                {
                    effectiveDefaultDecorators.put(Fragment.LAYOUT, layoutDefaultDecorator);
                }
                String portletDefaultDecorator = page.getDefaultDecorator(Fragment.PORTLET);
                if (portletDefaultDecorator != null)
                {
                    effectiveDefaultDecorators.put(Fragment.PORTLET, portletDefaultDecorator);
                }
            }
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
            // retrieve current page or template and parent folder from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());
            Folder folder = (Folder)pageOrTemplate.getParent();

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
            if (contentFragmentImpl.getDefinition() == null)
            {
                throw new IllegalArgumentException("Transient content fragments are not mutable");
            }            
            if (!contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPageOrTemplate().getPath()))
            {
                throw new IllegalArgumentException("Only page fragments can be modified");
            }
            
            // retrieve current fragment and page or template from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentFragmentImpl.getPageOrTemplate().getPath());
            BaseFragmentElement foundFragment = pageOrTemplate.getFragmentById(contentFragmentImpl.getFragment().getId());
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
                updatePage(pageOrTemplate);
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
            BaseFragmentElement fragment = lookupPageOrTemplateFragment(contentFragmentImpl, scope);
            
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
                    fragment.setLayoutHeight(scope, scopeValue, height);
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
    public void updatePreferences(ContentFragment contentFragment, Map<String,?> preferences)
    {
        log.debug("PageLayoutComponentImpl.updatePreferences() invoked");
        try
        {
            // validate content fragment
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            if (contentFragmentImpl.getDefinition() == null)
            {
                throw new IllegalArgumentException("Transient content fragments are not mutable");
            }            
            if (!contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPageOrTemplate().getPath()))
            {
                throw new IllegalArgumentException("Only page fragments can be modified");
            }
            boolean contentFragmentIsReference = (contentFragmentImpl.getReference() != null);
            
            // retrieve current fragment and page or template from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentFragmentImpl.getPageOrTemplate().getPath());
            String pageFragmentId = (contentFragmentIsReference ? contentFragmentImpl.getReference().getId() : contentFragmentImpl.getFragment().getId());
            BaseFragmentElement fragment = pageOrTemplate.getFragmentById(pageFragmentId);
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
                        String [] prefValues = portletPreference.getValues();
                        if (prefValues != null)
                        {
                            preference.getValueList().addAll(Arrays.asList(prefValues));
                        }
                    }
                    else
                    {
                        throw new IllegalArgumentException("Unexpected preference value type");
                    }
                    fragment.getPreferences().add(preference);
                }
            }
            updatePage(pageOrTemplate);

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
            BaseFragmentElement fragment = lookupPageOrTemplateFragment(contentFragmentImpl, scope);
            
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
     * @see org.apache.jetspeed.layout.PageLayoutComponent#updateRefId(org.apache.jetspeed.om.page.ContentFragment, java.lang.String)
     */
    public void updateRefId(ContentFragment contentFragment, String refId)
    {
        log.debug("PageLayoutComponentImpl.updateRefId() invoked");
        try
        {
            // validate content fragment
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)contentFragment;
            if (contentFragmentImpl.getDefinition() == null)
            {
                throw new IllegalArgumentException("Transient content fragments are not mutable");
            }            
            boolean contentFragmentIsReference = (contentFragmentImpl.getReference() != null);
            if ((contentFragmentIsReference && !contentFragmentImpl.getReferenceDefinition().getPath().equals(contentFragmentImpl.getPageOrTemplate().getPath())) ||
                (!contentFragmentIsReference && !contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPageOrTemplate().getPath())))
            {
                throw new IllegalArgumentException("Only page fragment references can be modified");
            }
            
            // retrieve current fragment reference and page or template from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentFragmentImpl.getPageOrTemplate().getPath());
            String pageFragmentId = (contentFragmentIsReference ? contentFragmentImpl.getReference().getId() : contentFragmentImpl.getFragment().getId());
            BaseFragmentElement fragment = pageOrTemplate.getFragmentById(pageFragmentId);
            if (fragment == null)
            {
                throw new IllegalArgumentException("Fragment and page not consistent");                
            }
            if (!(fragment instanceof FragmentReference))
            {
                throw new IllegalArgumentException("Fragment reference required");                
            }
            FragmentReference fragmentReference = (FragmentReference)fragment;

            // check for edit permission
            fragmentReference.checkAccess(JetspeedActions.EDIT);            

            // update fragment reference in page manager
            boolean update = false;
            if (!refId.equals(fragmentReference.getRefId()))
            {
                fragmentReference.setRefId(refId);
                update = true;
            }
            if (update)
            {
                updatePage(pageOrTemplate);
            }

            // update content context
            contentFragmentImpl.setRefId(refId);
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
            BaseFragmentElement fragment = lookupPageOrTemplateFragment(contentFragmentImpl, scope);
            
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
            if (contentFragmentImpl.getDefinition() == null)
            {
                throw new IllegalArgumentException("Transient content fragments are not mutable");
            }            
            if (!contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPageOrTemplate().getPath()))
            {
                throw new IllegalArgumentException("Only page fragments can be modified");
            }
            boolean contentFragmentIsReference = (contentFragmentImpl.getReference() != null);
            
            // retrieve current fragment and page or template from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentFragmentImpl.getPageOrTemplate().getPath());
            String pageFragmentId = (contentFragmentIsReference ? contentFragmentImpl.getReference().getId() : contentFragmentImpl.getFragment().getId());
            BaseFragmentElement fragment = pageOrTemplate.getFragmentById(pageFragmentId);
            if (fragment == null)
            {
                throw new IllegalArgumentException("Fragment and page not consistent");                
            }

            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);            

            // update fragment preferences and page in page manager.
            
            // Initializing security constraints by setting empty one first to reset it after aggregating into fragmentConstraintsConstraints..
            // 
            //TODO: JS2-1259: BaseElementImpl doesn't consider null input; DBPM based security constraints update needs to be more tested.
            //      For now, set an empty one instead of null to avoid this problem as well as possible side effects.
            //
            //fragment.setSecurityConstraints(null);
            fragment.setSecurityConstraints(fragment.newSecurityConstraints());
            
            if ((constraints != null) && !constraints.isEmpty())
            {
                SecurityConstraints fragmentConstraints = fragment.newSecurityConstraints();
                String constraintsOwner = constraints.getOwner();
                if (constraintsOwner != null)
                {
                    fragmentConstraints.setOwner(constraintsOwner);
                }
                List constraintsConstraints = constraints.getSecurityConstraints();
                if ((constraintsConstraints != null) && !constraintsConstraints.isEmpty())
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
                if ((constraintsConstraintsRefs != null) && !constraintsConstraintsRefs.isEmpty())
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
            updatePage(pageOrTemplate);

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
            BaseFragmentElement fragment = lookupPageOrTemplateFragment(contentFragmentImpl, scope);

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
            // retrieve current page or template from page manager
            ContentPageImpl contentPageImpl = (ContentPageImpl)contentPage;
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentPageImpl.getPageOrTemplate().getPath());

            // check for edit permission
            pageOrTemplate.checkAccess(JetspeedActions.EDIT);            

            // update titles and page in page manager
            boolean update = false;
            if (!Utils.isNull(title))
            {
                if (!title.equals(pageOrTemplate.getTitle()))
                {
                    pageOrTemplate.setTitle(title);
                    update = true;
                }
            }
            if (!Utils.isNull(shortTitle))
            {
                if (!shortTitle.equals(pageOrTemplate.getShortTitle()))
                {
                    pageOrTemplate.setShortTitle(shortTitle);
                    update = true;
                }
            }
            if (update)
            {
                updatePage(pageOrTemplate);
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
    
    /**
     * Merge content page attributes from source PSML page or template.
     * 
     * @param contentPageImpl target content page
     * @param pageOrTemplate source PSML page or template
     */
    private void mergeContentPageAttributes(ContentPageImpl contentPageImpl, BaseFragmentsElement pageOrTemplate)
    {
        // merge content page attributes
        if ((contentPageImpl != null) && (pageOrTemplate != null))
        {
            BasePageElement page = ((pageOrTemplate instanceof BasePageElement) ? (BasePageElement)pageOrTemplate : null);
            if ((pageOrTemplate.getMetadata() != null) && (pageOrTemplate.getMetadata().getFields() != null))
            {
                Iterator fieldIter = pageOrTemplate.getMetadata().getFields().iterator();
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
            if (page != null)
            {
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
            }
            if (contentPageImpl.getShortTitle() == null)
            {
                contentPageImpl.setShortTitle(pageOrTemplate.getShortTitle());
            }
            if ((contentPageImpl.getSkin() == null) && (page != null))
            {
                contentPageImpl.setSkin(page.getSkin());
            }
            if (contentPageImpl.getTitle() == null)
            {
                contentPageImpl.setTitle(pageOrTemplate.getTitle());
            }
        }
    }
    
    /**
     * Generate content fragment hierarchy from PSML fragments. Content fragment
     * ids are generated by concatenation of all parent ids to ensure that
     * unique ids are generated per fragment path.
     * 
     * @param parentId content fragment parent id or null 
     * @param pageOrTemplate PSML page or template
     * @param fragmentDefinitions PSML fragment definitions
     * @param definition PSML fragment page, page template, or fragments definition
     * @param fragment PSML fragment
     * @param template template fragment flag
     * @return content fragment hierarchy or null if undefined
     */
    private ContentFragmentImpl newContentFragment(String parentId, BaseFragmentsElement pageOrTemplate, Map fragmentDefinitions, BaseFragmentsElement definition, BaseFragmentElement fragment, boolean template)
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
            contentFragmentId = ((parentId != null) ? parentId+CONTENT_FRAGMENT_ID_SEPARATOR+fragmentFragment.getId() : fragmentFragment.getId());
            contentFragmentImpl = newContentFragment(contentFragmentId, pageOrTemplate, fragmentDefinitions, definition, fragmentFragment, null, null, template, template);
            // set content fragment attributes
            mergeContentFragmentAttributes(contentFragmentImpl, fragmentFragment);
            // set content fragment security constraints
            setContentFragmentSecurityConstraints(contentFragmentImpl, fragmentFragment);
        }
        else if (fragment instanceof PageFragment)
        {
            PageFragment pageFragmentFragment = (PageFragment)fragment;
            contentFragmentId = ((parentId != null) ? parentId+CONTENT_FRAGMENT_ID_SEPARATOR+pageFragmentFragment.getId() : pageFragmentFragment.getId());
            // check if page fragment definition in page template
            // or if in primary page or template
            if ((definition != pageOrTemplate) || template)
            {
                // consume page fragment and build fragment hierarchy from page
                BaseFragmentElement pageRootFragment = pageOrTemplate.getRootFragment();
                if (pageRootFragment instanceof FragmentReference)
                {
                    // consume top level page fragment reference and build fragment
                    // hierarchy from referenced fragment
                    FragmentReference fragmentReferenceFragment = (FragmentReference)pageRootFragment;
                    contentFragmentId += CONTENT_FRAGMENT_ID_SEPARATOR+fragmentReferenceFragment.getId();
                    Fragment [] fragmentFragment = new Fragment[]{null};
                    contentFragmentImpl = newContentFragment(contentFragmentId, pageOrTemplate, fragmentDefinitions, pageOrTemplate, fragmentReferenceFragment, template, fragmentFragment);
                    // inherit page fragment attributes
                    mergeContentFragmentAttributes(contentFragmentImpl, pageFragmentFragment);
                    // inherit fragment reference attributes
                    mergeContentFragmentAttributes(contentFragmentImpl, fragmentReferenceFragment);
                    // set content fragment security constraints
                    setContentFragmentSecurityConstraints(contentFragmentImpl, fragmentReferenceFragment);
                    // test resolved reference id
                    if (fragmentFragment[0] != null)
                    {
                        // merge content fragment attributes
                        mergeContentFragmentAttributes(contentFragmentImpl, fragmentFragment[0]);
                    }
                    else
                    {
                        // constructed content fragment for fragment reference
                        // since reference id was not resolved
                        contentFragmentImpl.setType(ContentFragment.REFERENCE);                        
                    }
                }
                else if (pageRootFragment instanceof Fragment)
                {
                    // construct content fragment to reflect page fragment hierarchy
                    Fragment fragmentFragment = (Fragment)pageRootFragment;
                    contentFragmentId += CONTENT_FRAGMENT_ID_SEPARATOR+fragmentFragment.getId();
                    contentFragmentImpl = newContentFragment(contentFragmentId, pageOrTemplate, fragmentDefinitions, pageOrTemplate, fragmentFragment, null, null, false, false);
                    // inherit page fragment attributes
                    mergeContentFragmentAttributes(contentFragmentImpl, pageFragmentFragment);
                    // merge content fragment attributes
                    mergeContentFragmentAttributes(contentFragmentImpl, fragmentFragment);
                    // set content fragment security constraints
                    setContentFragmentSecurityConstraints(contentFragmentImpl, fragmentFragment);
                }
                // ensure that page/fragment hierarchy page root is a
                // layout portlet by generating a transient locked
                // parent layout fragment if necessary
                if (!contentFragmentImpl.getType().equals(ContentFragment.LAYOUT))
                {
                    ContentFragmentImpl layoutContentFragmentImpl = newContentFragment("", pageOrTemplate, null, null, null, null, false, true);
                    layoutContentFragmentImpl.setType(ContentFragment.LAYOUT);
                    layoutContentFragmentImpl.setName(defaultLayoutPortletName);
                    layoutContentFragmentImpl.getFragments().add(contentFragmentImpl);
                    contentFragmentImpl = layoutContentFragmentImpl;                
                }
            }
            else
            {
                // construct content fragment for page fragment
                contentFragmentImpl = newContentFragment(contentFragmentId, pageOrTemplate, definition, pageFragmentFragment, null, null, template, true);
                contentFragmentImpl.setType(ContentFragment.PAGE);
                // set content fragment attributes
                mergeContentFragmentAttributes(contentFragmentImpl, pageFragmentFragment);
                // set content fragment security constraints
                setContentFragmentSecurityConstraints(contentFragmentImpl, pageFragmentFragment);
            }
        }
        else if (fragment instanceof FragmentReference)
        {
            // consume fragment reference and build fragment hierarchy from
            // referenced fragment
            FragmentReference fragmentReferenceFragment = (FragmentReference)fragment;
            contentFragmentId = ((parentId != null) ? parentId+CONTENT_FRAGMENT_ID_SEPARATOR+fragmentReferenceFragment.getId() : fragmentReferenceFragment.getId());
            Fragment [] fragmentFragment = new Fragment[]{null};
            contentFragmentImpl = newContentFragment(contentFragmentId, pageOrTemplate, fragmentDefinitions, definition, fragmentReferenceFragment, template, fragmentFragment);
            // inherit fragment reference attributes
            mergeContentFragmentAttributes(contentFragmentImpl, fragmentReferenceFragment);
            // set content fragment security constraints
            setContentFragmentSecurityConstraints(contentFragmentImpl, fragmentReferenceFragment);
            // test resolved reference id
            if (fragmentFragment[0] != null)
            {
                // merge content fragment attributes
                mergeContentFragmentAttributes(contentFragmentImpl, fragmentFragment[0]);
            }
            else
            {
                // constructed content fragment for fragment reference
                // since reference id was not resolved
                contentFragmentImpl.setType(ContentFragment.REFERENCE);                
            }
        }
        return contentFragmentImpl;
    }

    /**
     * Generate content fragment hierarchy from a PSML fragment reference.
     * 
     * @param parentId content fragment parent id 
     * @param pageOrTemplate PSML page or template
     * @param fragmentDefinitions PSML fragment definitions
     * @param definition PSML fragment page, page template, or fragments definition
     * @param fragmentReference PSML fragment
     * @param template template fragment flag
     * @param fragmentFragment referenced root PSML fragment from fragment definition
     * @return content fragment hierarchy or null if undefined
     */
    private ContentFragmentImpl newContentFragment(String parentId, BaseFragmentsElement pageOrTemplate, Map fragmentDefinitions, BaseFragmentsElement definition, FragmentReference fragmentReference, boolean template, Fragment [] fragmentFragment)
    {
        // generate content fragment hierarchy for reference fragment from
        // fragment definition root fragment if located
        ContentFragmentImpl contentFragmentImpl = null;
        FragmentDefinition fragmentDefinition = (FragmentDefinition)((fragmentDefinitions != null) ? fragmentDefinitions.get(fragmentReference.getRefId()) : null);
        if ((fragmentDefinition != null) && (fragmentDefinition.getRootFragment() instanceof Fragment))
        {
            fragmentFragment[0] = (Fragment)fragmentDefinition.getRootFragment();
            String contentFragmentId = parentId+CONTENT_FRAGMENT_ID_SEPARATOR+fragmentFragment[0].getId();                
            contentFragmentImpl = newContentFragment(contentFragmentId, pageOrTemplate, fragmentDefinitions, fragmentDefinition, fragmentFragment[0], definition, fragmentReference, template, true);
        }
        else
        {
            contentFragmentImpl = newContentFragment(parentId, pageOrTemplate, definition, fragmentReference, null, null, template, true);
        }
        contentFragmentImpl.setRefId(fragmentReference.getRefId());
        return contentFragmentImpl;
    }

    /**
     * Generate content fragment reference for a PSML fragment reference.
     * 
     * @param parentId content fragment parent id 
     * @param pageOrTemplate PSML page or template
     * @param definition PSML fragment page, page template, or fragments definition
     * @param fragmentReference PSML fragment
     * @return content fragment hierarchy or null if undefined
     */
    private ContentFragmentImpl newContentFragment(String parentId, BaseFragmentsElement pageOrTemplate, BaseFragmentsElement definition, FragmentReference fragmentReference)
    {
        // generate content fragment reference for fragment reference
        String contentFragmentId = parentId+CONTENT_FRAGMENT_ID_SEPARATOR+fragmentReference.getId();                
        ContentFragmentImpl contentFragmentImpl = newContentFragment(contentFragmentId, pageOrTemplate, pageOrTemplate, fragmentReference, null, null, false, true);
        contentFragmentImpl.setType(ContentFragment.REFERENCE);
        contentFragmentImpl.setRefId(fragmentReference.getRefId());
        // inherit fragment reference attributes
        mergeContentFragmentAttributes(contentFragmentImpl, fragmentReference);
        // set content fragment security constraints
        setContentFragmentSecurityConstraints(contentFragmentImpl, fragmentReference);
        return contentFragmentImpl;
    }

    /**
     * Generate content fragment hierarchy from a PSML fragment.
     * 
     * @param id content fragment id 
     * @param pageOrTemplate PSML page or template
     * @param fragmentDefinitions PSML fragment definitions
     * @param definition PSML fragment page, page template, or fragments definition
     * @param fragment PSML fragment
     * @param reference PSML fragment reference
     * @param template template fragment flag
     * @param locked locked fragment flag
     * @return content fragment hierarchy or null if undefined
     */
    private ContentFragmentImpl newContentFragment(String id, BaseFragmentsElement pageOrTemplate, Map fragmentDefinitions, BaseFragmentsElement definition, Fragment fragment, BaseFragmentsElement referenceDefinition, FragmentReference reference, boolean template, boolean locked)
    {
        // generate content fragment hierarchy for fragment
        ContentFragmentImpl contentFragmentImpl = newContentFragment(id, pageOrTemplate, definition, fragment, referenceDefinition, reference, template, locked);
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
                ContentFragmentImpl newContentFragment = newContentFragment(id, pageOrTemplate, fragmentDefinitions, definition, childFragment, template);
                if (newContentFragment != null)
                {
                    contentFragmentImpl.getFragments().add(newContentFragment);
                }
            }
        }
        return contentFragmentImpl;
    }

    /**
     * Generate content fragment from a PSML base fragment.
     * 
     * @param id content fragment id
     * @param pageOrTemplate PSML page or template
     * @param definition PSML page, page template, or fragment definition
     * @param fragment PSML fragment
     * @param referenceDefinition PSML page or page template
     * @param reference PSML fragment reference
     * @param template template fragment flag
     * @param locked locked fragment flag
     * @return content fragment hierarchy or null if undefined
     */
    private ContentFragmentImpl newContentFragment(String id, BaseFragmentsElement pageOrTemplate, BaseFragmentsElement definition, BaseFragmentElement fragment, BaseFragmentsElement referenceDefinition, FragmentReference reference, boolean template, boolean locked)
    {
        // generate content fragment for fragment
        ContentFragmentImpl contentFragmentImpl = new ContentFragmentImpl(this, id, pageOrTemplate, definition, fragment, referenceDefinition, reference, template, locked);

        log.debug("PageLayoutComponentImpl.newContentFragment(): constructed ContentFragment: id="+id+", template="+template+", locked="+locked);
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
     * Lookup page or template fragment to be updated via
     * scoped fragment property based edits.
     * 
     * @param contentFragmentImpl target content fragment
     * @param scope target fragment property scope
     * @return fragment
     * @throws PageNotFoundException
     * @throws NodeException
     */
    private BaseFragmentElement lookupPageOrTemplateFragment(ContentFragmentImpl contentFragmentImpl, String scope) throws PageNotFoundException, NodeException
    {
        // classify and validate content fragment
        if (contentFragmentImpl.getDefinition() == null)
        {
            throw new IllegalArgumentException("Transient content fragments are not mutable");
        }
        String contentPageOrTemplatePath = contentFragmentImpl.getPageOrTemplate().getPath();
        String contentPageOrTemplateDefinitionPath = contentFragmentImpl.getDefinition().getPath();
        boolean contentFragmentPageDefinition = false;
        boolean contentFragmentPageReference = false;
        boolean contentFragmentInPage = false;
        if (!contentFragmentImpl.isTemplate())
        {
            contentFragmentPageDefinition = contentPageOrTemplateDefinitionPath.equals(contentPageOrTemplatePath);
            contentFragmentPageReference = ((contentFragmentImpl.getDefinition() instanceof FragmentDefinition) && !contentPageOrTemplateDefinitionPath.equals(contentPageOrTemplatePath) &&
                                                (contentFragmentImpl.getReferenceDefinition() != null) && contentFragmentImpl.getReferenceDefinition().getPath().equals(contentPageOrTemplatePath) && (contentFragmentImpl.getReference() != null));
            contentFragmentInPage = (contentFragmentPageDefinition || contentFragmentPageReference);
        }
        boolean contentFragmentTemplateDefinition = false;
        boolean contentFragmentTemplateReference = false;
        boolean contentFragmentInTemplate = false;
        if (!contentFragmentInPage)
        {
            contentFragmentTemplateDefinition = (contentFragmentImpl.getDefinition() instanceof PageTemplate);
            contentFragmentTemplateReference = ((contentFragmentImpl.getDefinition() instanceof FragmentDefinition) && (contentFragmentImpl.getReferenceDefinition() instanceof PageTemplate) && (contentFragmentImpl.getReference() != null));
            contentFragmentInTemplate = (contentFragmentTemplateDefinition || contentFragmentTemplateReference);
        }
        boolean userScopedUpdate = ((scope != null) && scope.equals(FragmentProperty.USER_PROPERTY_SCOPE));
        if (!contentFragmentInPage && (!userScopedUpdate || !contentFragmentInTemplate))
        {
            if (userScopedUpdate)
            {
                throw new IllegalArgumentException("Only page fragments, fragment references, template fragments, and template references are user scope mutable");
            }
            else
            {
                throw new IllegalArgumentException("Only page fragments and fragment references are mutable");
            }
        }
        
        // retrieve current fragment using page or page template from page manager
        BaseFragmentElement fragment = null;
        if (contentFragmentInPage)
        {
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentFragmentImpl.getPageOrTemplate().getPath());
            String pageFragmentId = (contentFragmentPageDefinition ? contentFragmentImpl.getFragment().getId() : contentFragmentImpl.getReference().getId());
            fragment = pageOrTemplate.getFragmentById(pageFragmentId);
        }
        else if (contentFragmentInTemplate)
        {
            String pageTemplatePath = (contentFragmentTemplateDefinition ? contentFragmentImpl.getDefinition().getPath() : contentFragmentImpl.getReferenceDefinition().getPath());
            PageTemplate pageTemplate = pageManager.getPageTemplate(pageTemplatePath);
            String pageTemplateFragmentId = (contentFragmentTemplateDefinition ? contentFragmentImpl.getFragment().getId() : contentFragmentImpl.getReference().getId());
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
     * Get page or template from page manager.
     * 
     * @param path path to page or template
     * @return page or dynamic page
     * @throws PageNotFoundException
     * @throws NodeException
     */
    private BaseFragmentsElement getPageOrTemplate(String path) throws PageNotFoundException, NodeException
    {
        if (path.endsWith(Page.DOCUMENT_TYPE))
        {
            return pageManager.getPage(path);
        }
        if (path.endsWith(DynamicPage.DOCUMENT_TYPE))
        {
            return pageManager.getDynamicPage(path);            
        }
        if (path.endsWith(PageTemplate.DOCUMENT_TYPE))
        {
            return pageManager.getPageTemplate(path);            
        }
        if (path.endsWith(FragmentDefinition.DOCUMENT_TYPE))
        {
            return pageManager.getFragmentDefinition(path);            
        }
        throw new PageNotFoundException("Unable to classify page or template path by type: "+path);
    }
    
    /**
     * Update page or template using page manager.
     * 
     * @param pageOrTemplate page or template to update
     * @throws NodeException
     * @throws PageNotUpdatedException
     */
    private void updatePage(BaseFragmentsElement pageOrTemplate) throws NodeException, PageNotUpdatedException
    {
        if (pageOrTemplate instanceof Page)
        {
            pageManager.updatePage((Page)pageOrTemplate);
            return;
        }
        if (pageOrTemplate instanceof DynamicPage)
        {
            pageManager.updateDynamicPage((DynamicPage)pageOrTemplate);
            return;
        }
        if (pageOrTemplate instanceof PageTemplate)
        {
            pageManager.updatePageTemplate((PageTemplate)pageOrTemplate);
            return;
        }
        if (pageOrTemplate instanceof FragmentDefinition)
        {
            pageManager.updateFragmentDefinition((FragmentDefinition)pageOrTemplate);
            return;
        }
        throw new PageNotUpdatedException("Unable to classify page by type: "+((pageOrTemplate != null) ? pageOrTemplate.getClass().getName() : "null"));
    }

    /**
     * Remove page or template using page manager.
     * 
     * @param pageOrTemplate page or template to update
     * @throws NodeException
     * @throws PageNotRemovedException
     */
    private void removePage(BaseFragmentsElement pageOrTemplate) throws NodeException, PageNotRemovedException
    {
        if (pageOrTemplate instanceof Page)
        {
            pageManager.removePage((Page)pageOrTemplate);
            return;
        }
        if (pageOrTemplate instanceof DynamicPage)
        {
            pageManager.removeDynamicPage((DynamicPage)pageOrTemplate);
            return;
        }
        if (pageOrTemplate instanceof PageTemplate)
        {
            pageManager.removePageTemplate((PageTemplate)pageOrTemplate);
            return;
        }
        if (pageOrTemplate instanceof FragmentDefinition)
        {
            pageManager.removeFragmentDefinition((FragmentDefinition)pageOrTemplate);
            return;
        }
        throw new PageNotRemovedException("Unable to classify page by type: "+((pageOrTemplate != null) ? pageOrTemplate.getClass().getName() : "null"));
    }

    public void reorderColumns(ContentFragment contentFragment, int maxColumns) {
        if (log.isDebugEnabled()) {
            log.debug("PageLayoutComponentImpl.reorderColumns() invoked");
        }
        try {
            // validate content fragment
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl) contentFragment;
            if (contentFragmentImpl.getDefinition() == null) {
                throw new IllegalArgumentException("Transient content fragments are not mutable");
            }
            if (contentFragmentImpl.isLocked()) {
                throw new IllegalArgumentException("Locked content fragment is not mutable");
            }
            if (!contentFragmentImpl.getDefinition().getPath().equals(contentFragmentImpl.getPageOrTemplate().getPath())) {
                throw new IllegalArgumentException("Only page fragments can be modified");
            }

            // retrieve current page or template and fragment from page manager
            BaseFragmentsElement pageOrTemplate = getPageOrTemplate(contentFragmentImpl.getPageOrTemplate().getPath());
            BaseFragmentElement parentFragment = pageOrTemplate.getFragmentById(contentFragmentImpl.getFragment().getId());
            if (!(parentFragment instanceof Fragment)) {
                throw new IllegalArgumentException("New fragment cannot be added to parent fragment");
            }
            Fragment fragment = (Fragment) parentFragment;
            if (!Fragment.LAYOUT.equals(fragment.getType())) {
                throw new IllegalArgumentException("New fragment cannot be added to non-layout parent fragment");
            }

            // check for edit permission
            fragment.checkAccess(JetspeedActions.EDIT);

            // Perform ReOrder
            int count = maxColumns - 1;
            int row = 0;
            for (Object f : contentFragmentImpl.getFragments()) {
                ContentFragmentImpl impl = (ContentFragmentImpl)f;
                impl.updateRowColumn(row, count);
                count = count - 1;
                if (count < 0) {
                    count = maxColumns - 1;
                    row++;
                }
            }

            // update page in page manager
            updatePage(pageOrTemplate);

        } catch (Exception e) {
            throw new PageLayoutComponentException("Unexpected exception: " + e, e);
        }

    }


}
