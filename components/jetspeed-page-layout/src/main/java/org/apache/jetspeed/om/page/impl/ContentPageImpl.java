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
package org.apache.jetspeed.om.page.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.impl.PageLayoutComponentUtils;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.portlet.GenericMetadata;

/**
 * Immutable content page implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class ContentPageImpl implements ContentPage, PageLayoutComponentUtils
{
    private PageLayoutComponent pageLayoutComponent;
    private String id;
    private BaseFragmentsElement pageOrTemplate;
    private PageTemplate pageTemplate;
    private Map<String,FragmentDefinition> fragmentDefinitions;

    private ContentFragmentImpl rootContentFragment;
    private GenericMetadata metadata;
    private Map defaultDecorators;
    private Map effectiveDefaultDecorators;
    private String name;
    private String path;
    private String shortTitle;
    private String skin;
    private String title;
    private String url;
    private boolean hidden;
    private String contentType;
    private String defId;
    private boolean inheritable;
    
    /**
     * Construct new dynamic content page with
     * a transiently computed id.
     */
    public ContentPageImpl()
    {
        this.id = Integer.toHexString(System.identityHashCode(this));
    }
    
    /**
     * Construct new dynamic content page.
     *
     * @param id content page id
     */
    public ContentPageImpl(String id)
    {
        this.id = id;
    }

    /**
     * Construct content page with PSML page.
     * 
     * @param pageLayoutComponent PageLayoutComponent instance
     * @param id content page id
     * @param pageOrTemplate PSML page or template
     * @param pageTemplate PSML page template
     * @param fragmentDefinitions PSML fragment definitions
     */
    public ContentPageImpl(PageLayoutComponent pageLayoutComponent, String id, BaseFragmentsElement pageOrTemplate, PageTemplate pageTemplate, Map<String,FragmentDefinition> fragmentDefinitions)
    {
        this.pageLayoutComponent = pageLayoutComponent;
        this.id = id;
        this.pageOrTemplate = pageOrTemplate;
        this.pageTemplate = pageTemplate;
        this.fragmentDefinitions = fragmentDefinitions;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#checkAccess(java.lang.String)
     */
    public void checkAccess(String actions) throws SecurityException
    {
        // check security against underlying page
        if (pageOrTemplate != null)
        {
            pageOrTemplate.checkAccess(actions);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof ContentPageImpl)
        {
            ContentPageImpl cpi = (ContentPageImpl)o;
            return (((id == null) && (cpi.id == null)) || ((id != null) && id.equals(cpi.id)));
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getContentType()
     */
    public String getContentType()
    {
        return contentType;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getDefId()
     */
    public String getDefId()
    {
        return defId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getDefaultDecorator(java.lang.String)
     */
    public String getDefaultDecorator(String fragmentType)
    {
        return ((defaultDecorators != null) ? (String)defaultDecorators.get(fragmentType) : null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getEffectiveDefaultDecorator(java.lang.String)
     */
    public String getEffectiveDefaultDecorator(String fragmentType)
    {
        return ((effectiveDefaultDecorators != null) ? (String)effectiveDefaultDecorators.get(fragmentType) : null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentDefinitions()
     */
    public Map<String,FragmentDefinition> getFragmentDefinitions()
    {
        return fragmentDefinitions;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentById(java.lang.String)
     */
    public ContentFragment getFragmentById(String id)
    {
        if (rootContentFragment != null)
        {
            return rootContentFragment.getFragmentById(id, null);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentByFragmentId(java.lang.String)
     */
    public ContentFragment getFragmentByFragmentId(String id)
    {
        if (rootContentFragment != null)
        {
            return rootContentFragment.getFragmentByFragmentId(id, false);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentsByName(java.lang.String)
     */
    public List<ContentFragment> getFragmentsByName(String name)
    {
        if (rootContentFragment != null)
        {
            return rootContentFragment.getFragmentsByName(name, false);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getId()
     */
    public String getId()
    {
        return id;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
        if (metadata == null)
        {
            metadata = new ContentGenericMetadataImpl();
        }
        return metadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getPageOrTemplate()
     */
    public BaseFragmentsElement getPageOrTemplate()
    {
        return pageOrTemplate;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getPageLayoutComponent()
     */
    public PageLayoutComponent getPageLayoutComponent()
    {
        return pageLayoutComponent;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getPageTemplate()
     */
    public PageTemplate getPageTemplate()
    {
        return pageTemplate;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getPath()
     */
    public String getPath()
    {
        return path;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getRootFragment()
     */
    public ContentFragment getRootFragment()
    {
        return rootContentFragment;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getShortTitle()
     */
    public String getShortTitle()
    {
        return shortTitle;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getShortTitle(java.util.Locale)
     */
    public String getShortTitle(Locale locale)
    {        
        String localeSpecificShortTitle = getMetadata().getText("short-title", locale);
        if (localeSpecificShortTitle == null)
        {
            localeSpecificShortTitle = getMetadata().getText("title", locale);
        }
        return ((localeSpecificShortTitle != null) ? localeSpecificShortTitle : ((shortTitle != null) ? shortTitle : title));
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getTitle(java.util.Locale)
     */
    public String getTitle(Locale locale)
    {
        String localeSpecificTitle = getMetadata().getText("title", locale);
        return ((localeSpecificTitle != null) ? localeSpecificTitle : title);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getUrl()
     */
    public String getUrl()
    {
        return url;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return ((id != null) ? id.hashCode() : 0);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#isHidden()
     */
    public boolean isHidden()
    {
        return hidden;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#isInheritable()
     */
    public boolean isInheritable()
    {
        return inheritable;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#overrideDefaultDecorator(java.lang.String, java.lang.String)
     */
    public void overrideDefaultDecorator(String decoratorName, String fragmentType)
    {
        if ((decoratorName != null) && (decoratorName.length() > 0))
        {
            if (effectiveDefaultDecorators == null)
            {
                effectiveDefaultDecorators = new HashMap();
            }
            effectiveDefaultDecorators.put(fragmentType, decoratorName);
        }
        else if (effectiveDefaultDecorators != null)
        {
            effectiveDefaultDecorators.remove(fragmentType);            
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentByFragmentId(java.lang.String, boolean)
     */
    public ContentFragment getFragmentByFragmentId(String id, boolean nonTemplate)
    {
        if (rootContentFragment != null)
        {
            return rootContentFragment.getFragmentByFragmentId(id, nonTemplate);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentsByName(java.lang.String, boolean)
     */
    public List<ContentFragment> getFragmentsByName(String name, boolean nonTemplate)
    {
        if (rootContentFragment != null)
        {
            return rootContentFragment.getFragmentsByName(name, nonTemplate);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getNonTemplateRootFragment()
     */
    public ContentFragment getNonTemplateRootFragment()
    {
        if (rootContentFragment != null)
        {
            return rootContentFragment.getNonTemplateLayoutFragment();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#addFragmentAtRowColumn(org.apache.jetspeed.om.page.ContentFragment, int, int)
     */
    public ContentFragment addFragmentAtRowColumn(ContentFragment fragment, int row, int column)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            return pageLayoutComponent.addFragmentAtRowColumn(this, fragment, row, column);
        }
        else
        {
            // perform locally only            
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)fragment;
            if (!Utils.isNull(row))
            {
                contentFragmentImpl.setLayoutRow(row);
            }
            if (!Utils.isNull(column))
            {
                contentFragmentImpl.setLayoutColumn(column);
            }            
            ContentFragmentImpl rootContentFragmentImpl = (ContentFragmentImpl)getRootFragment();
            rootContentFragmentImpl.getFragments().add(contentFragmentImpl);
            return contentFragmentImpl;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#addFragmentReference(java.lang.String)
     */
    public ContentFragment addFragmentReference(String id)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            return pageLayoutComponent.addFragmentReference(this, id);
        }
        else
        {
            // perform locally only
            ContentFragmentImpl newContentFragmentImpl = new ContentFragmentImpl();
            newContentFragmentImpl.setType(ContentFragment.REFERENCE);
            newContentFragmentImpl.setRefId(id);
            ContentFragmentImpl rootContentFragmentImpl = (ContentFragmentImpl)getRootFragment();
            rootContentFragmentImpl.getFragments().add(newContentFragmentImpl);
            return newContentFragmentImpl;            
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#addPortlet(java.lang.String, java.lang.String)
     */
    public ContentFragment addPortlet(String type, String name)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            return pageLayoutComponent.addPortlet(this, type, name);
        }
        else
        {
            // perform locally only
            ContentFragmentImpl newContentFragmentImpl = new ContentFragmentImpl();
            newContentFragmentImpl.setType(type);
            newContentFragmentImpl.setName(name);            
            ContentFragmentImpl rootContentFragmentImpl = (ContentFragmentImpl)getRootFragment();
            rootContentFragmentImpl.getFragments().add(newContentFragmentImpl);
            return newContentFragmentImpl;            
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#decrementFolderInDocumentOrder()
     */
    public void decrementFolderInDocumentOrder()
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.decrementFolderInDocumentOrder(this);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#decrementInDocumentOrder()
     */
    public void decrementInDocumentOrder()
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.decrementInDocumentOrder(this);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentNestingLevel(java.lang.String)
     */
    public int getFragmentNestingLevel(String fragmentId)
    {
        if (rootContentFragment != null)
        {
            return rootContentFragment.getFragmentNestingLevel(fragmentId, 0);
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#incrementFolderInDocumentOrder()
     */
    public void incrementFolderInDocumentOrder()
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.incrementFolderInDocumentOrder(this);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#incrementInDocumentOrder()
     */
    public void incrementInDocumentOrder()
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.incrementInDocumentOrder(this);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#moveFragment(java.lang.String, java.lang.String, java.lang.String)
     */
    public void moveFragment(String fragmentId, String fromFragmentId, String toFragmentId)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.moveFragment(this, fragmentId, fromFragmentId, toFragmentId);
        }
        else
        {
            // perform locally only
            ContentFragmentImpl fromContentFragmentImpl = (ContentFragmentImpl)getFragmentById(fromFragmentId);
            ContentFragmentImpl contentFragmentImpl = (ContentFragmentImpl)fromContentFragmentImpl.getFragmentById(fragmentId);
            ContentFragmentImpl toContentFragmentImpl = (ContentFragmentImpl)getFragmentById(toFragmentId);
            if ((contentFragmentImpl != null) && (fromContentFragmentImpl != null) && (toContentFragmentImpl != null))
            {
                fromContentFragmentImpl.removeFragmentById(fragmentId);
                toContentFragmentImpl.getFragments().add(contentFragmentImpl);
            }
        }        
    }
    

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#newSiblingFolder(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingFolder(String folderName, String folderTitle, String folderShortTitle, String defaultPageLayoutName)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.newSiblingFolder(this, folderName, folderTitle, folderShortTitle, defaultPageLayoutName);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#newSiblingPage(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingPage(String pageName, String layoutName, String pageTitle, String pageShortTitle)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.newSiblingPage(this, pageName, layoutName, pageTitle, pageShortTitle);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#newSiblingDynamicPage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingDynamicPage(String pageName, String contentType, String layoutName, String pageTitle, String pageShortTitle)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.newSiblingDynamicPage(this, pageName, contentType, layoutName, pageTitle, pageShortTitle);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#newSiblingPageTemplate(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingPageTemplate(String templateName, String layoutName, String templateTitle, String templateShortTitle)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.newSiblingPageTemplate(this, templateName, layoutName, templateTitle, templateShortTitle);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#newSiblingFragmentDefinition(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void newSiblingFragmentDefinition(String definitionName, String defId, String portletName, String definitionTitle, String definitionShortTitle)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.newSiblingFragmentDefinition(this, definitionName, defId, portletName, definitionTitle, definitionShortTitle);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#removeFragment(java.lang.String)
     */
    public void removeFragment(String fragmentId)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.removeFragment(this, fragmentId);
        }
        else
        {
            // perform locally only
            removeFragmentById(fragmentId);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#remove()
     */
    public void remove()
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.remove(this);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#removeFolder()
     */
    public void removeFolder()
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.removeFolder(this);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#updateContent(java.lang.String, java.lang.Boolean)
     */
    public void updateContent(String contentType, Boolean inheritable)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateContent(this, contentType, inheritable);
        }
        else
        {
            // perform locally only
            if (!Utils.isNull(contentType))
            {
                setContentType(contentType);
            }
            if (inheritable != null)
            {
                setInheritable(inheritable.booleanValue());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#updateDefaultDecorator(java.lang.String, java.lang.String)
     */
    public void updateDefaultDecorator(String decoratorName, String fragmentType)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateDefaultDecorator(this, decoratorName, fragmentType);
        }
        else
        {
            // perform locally only
            decoratorName = (!Utils.isNull(decoratorName) ? decoratorName : null);
            if (decoratorName != null)
            {
                if (effectiveDefaultDecorators == null)
                {
                    effectiveDefaultDecorators = new HashMap();
                }
                effectiveDefaultDecorators.put(fragmentType, decoratorName);
            }
            else if (effectiveDefaultDecorators != null)
            {
                effectiveDefaultDecorators.remove(fragmentType);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#updateFolderTitles(java.lang.String, java.lang.String)
     */
    public void updateFolderTitles(String title, String shortTitle)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateFolderTitles(this, title, shortTitle);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#updateTitles(java.lang.String, java.lang.String)
     */
    public void updateTitles(String title, String shortTitle)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateTitles(this, title, shortTitle);
        }
        else
        {
            // perform locally only
            if (!Utils.isNull(title))
            {
                setTitle(title);
            }
            if (!Utils.isNull(shortTitle))
            {
                setShortTitle(shortTitle);
            }
        }
    }
    
    /**
     * Get content fragment associated with content page root fragment.
     * 
     * @return content fragment
     */
    public ContentFragmentImpl getPageRootContentFragment()
    {
        if (rootContentFragment != null)
        {
            if (pageOrTemplate != null)
            {
                // find first content fragment with page definition
                return rootContentFragment.getFragmentByDefinition(pageOrTemplate);
            }
            else
            {
                // transient content page assumes it is constructed
                // from page; return root content fragment
                return rootContentFragment;
            }
        }
        return null;
    }
    
    /**
     * Get content fragment and parent by id.
     * 
     * @param id content fragment id
     * @param parent returned parent content fragment
     * @return content fragment
     */
    public ContentFragmentImpl getFragmentById(String id, ContentFragmentImpl [] parentFragment)
    {
        if (rootContentFragment != null)
        {
            return rootContentFragment.getFragmentById(id, parentFragment);
        }
        return null;
    }

    /**
     * Remove content fragment by id.
     * 
     * @param id the id of fragment to remove
     * @return removed content fragment
     */
    public ContentFragmentImpl removeFragmentById(String id)
    {
        ContentFragmentImpl removed = null;
        if (rootContentFragment != null)
        {
            if (rootContentFragment.getId().equals(id))
            {
                removed = rootContentFragment;
                rootContentFragment = null;
            }
            else
            {
                removed = rootContentFragment.removeFragmentById(id);
            }
        }
        return removed;
    }

    /**
     * Set content page default decorators.
     * 
     * @param defaultDecorators the defaultDecorators to set
     */
    public void setDefaultDecorators(Map defaultDecorators)
    {
        this.defaultDecorators = defaultDecorators;
    }

    /**
     * Set content page decorators.
     * 
     * @param effectiveDefaultDecorators the effectiveDefaultDecorators to set
     */
    public void setEffectiveDefaultDecorators(Map effectiveDefaultDecorators)
    {
        this.effectiveDefaultDecorators = effectiveDefaultDecorators;
    }

    /**
     * Set content page name.
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Set content page path.
     * 
     * @param path the path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Set content fragments root.
     * 
     * @param rootContentFragment the rootContentFragment to set
     */
    public void setRootFragment(ContentFragmentImpl rootContentFragment)
    {
        this.rootContentFragment = rootContentFragment;
    }

    /**
     * Set content page short title.
     * 
     * @param shortTitle the shortTitle to set
     */
    public void setShortTitle(String shortTitle)
    {
        this.shortTitle = shortTitle;
    }

    /**
     * Set content page skin.
     * 
     * @param skin the skin to set
     */
    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    /**
     * Set content page title.
     * 
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Set content page url.
     * 
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    /**
     * Set content page hidden flag.
     * 
     * @param hidden the hidden to set
     */
    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    /**
     * Set dynamic page content type.
     * 
     * @param contentType content type
     */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    /**
     * Set fragment definition definition id.
     * 
     * @param defId definition id
     */
    public void setDefId(String defId)
    {
        this.defId = defId;
    }
    
    /**
     * Set dynamic page inheritable flag.
     * 
     * @param inheritable inheritable flag
     */
    public void setInheritable(boolean inheritable)
    {
        this.inheritable = inheritable;
    }

}
