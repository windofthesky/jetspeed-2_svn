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
package org.apache.jetspeed.om.page.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.page.document.impl.DocumentImpl;
import org.apache.ojb.broker.core.proxy.ProxyHelper;

/**
 * PageImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PageImpl extends DocumentImpl implements Page
{
    private Collection fragment;
    private String skin;
    private String defaultLayoutDecorator;
    private String defaultPortletDecorator;
    private List menus;

    private PageMenuDefinitionList menuDefinitions;
    private FragmentImpl removedFragment;

    public PageImpl()
    {
        super(new PageSecurityConstraintsImpl());
    }

    /**
     * accessMenus
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessMenus()
    {
        // create initial collection if necessary
        if (menus == null)
        {
            menus = new ArrayList(2);
        }
        return menus;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#resetCachedSecurityConstraints()
     */
    public void resetCachedSecurityConstraints()
    {
        // propagate to super and fragments
        super.resetCachedSecurityConstraints();
        FragmentImpl rootFragment = (FragmentImpl)getRootFragment();
        if (rootFragment != null)
        {
            rootFragment.resetCachedSecurityConstraints();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.impl.NodeImpl#newPageMetadata(java.util.Collection)
     */
    public PageMetadataImpl newPageMetadata(Collection fields)
    {
        PageMetadataImpl pageMetadata = new PageMetadataImpl(PageMetadataLocalizedFieldImpl.class);
        pageMetadata.setFields(fields);
        return pageMetadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        this.skin = skinName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getEffectiveDefaultDecorator(java.lang.String)
     */
    public String getEffectiveDefaultDecorator(String fragmentType)
    {
        // get locally defined decorator
        String decorator = getDefaultDecorator(fragmentType);
        if (decorator == null)
        {
            // delegate to parent folder
            Folder parentFolder = (Folder)ProxyHelper.getRealObject(getParent());
            if (parentFolder != null)
            {
                return parentFolder.getEffectiveDefaultDecorator(fragmentType);
            }
        }
        return decorator;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String)
     */
    public String getDefaultDecorator(String fragmentType)
    {
        // retrieve supported decorator types
        if (fragmentType != null)
        {
            if (fragmentType.equals(Fragment.LAYOUT))
            {
                return defaultLayoutDecorator; 
            }
            if (fragmentType.equals(Fragment.PORTLET))
            {
                return defaultPortletDecorator; 
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String,java.lang.String)
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType)
    {
        // save supported decorator types
        if (fragmentType != null)
        {
            if (fragmentType.equals(Fragment.LAYOUT))
            {
                defaultLayoutDecorator = decoratorName; 
            }
            if (fragmentType.equals(Fragment.PORTLET))
            {
                defaultPortletDecorator = decoratorName; 
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getRootFragment()
     */
    public Fragment getRootFragment()
    {
        // get singleton fragment; no access checks to
        // be made for root fragment
        if ((fragment != null) && !fragment.isEmpty())
        {
            FragmentImpl rootFragment = (FragmentImpl)fragment.iterator().next();
            if (rootFragment.getPage() == null)
            {
                // set page implementation in root and children fragments
                rootFragment.setPage(this);
            }
            return rootFragment;
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setRootFragment(org.apache.jetspeed.om.page.Fragment)
     */
    public void setRootFragment(Fragment fragment)
    {
        // add new or reuse singleton fragment
        if (fragment instanceof FragmentImpl)
        {
            // create singleton collection or remove existing
            // root fragment and save for reuse
            if (this.fragment == null)
            {
                this.fragment = new ArrayList(1);
            }
            else if (!this.fragment.isEmpty())
            {
                removedFragment = (FragmentImpl)this.fragment.iterator().next();
                this.fragment.clear();
            }

            // add new fragment or copy configuration
            // from previously removed fragment
            if (removedFragment != null)
            {
                // reuse previously removed fragment
                FragmentImpl addFragment = (FragmentImpl)fragment;
                fragment = removedFragment;
                removedFragment = null;
                // TODO: move this logic to copy methods on implementations
                fragment.setName(addFragment.getName());
                fragment.setTitle(addFragment.getTitle());
                fragment.setShortTitle(addFragment.getShortTitle());
                fragment.setType(addFragment.getType());
                fragment.setSkin(addFragment.getSkin());
                fragment.setDecorator(addFragment.getDecorator());
                fragment.setState(addFragment.getState());
                fragment.setSecurityConstraints(addFragment.getSecurityConstraints());
                fragment.getProperties().clear();
                fragment.getProperties().putAll(addFragment.getProperties());
                fragment.setPreferences(addFragment.getPreferences());
                fragment.getFragments().clear();
                fragment.getFragments().addAll(addFragment.getFragments());
            }
            this.fragment.add(fragment);

            // set page implementation in root and children fragments
            ((FragmentImpl)fragment).setPage(this);
        }
        else if (fragment == null)
        {
            // delete existing fragment if required, saving
            // removed fragment for later reuse
            if ((this.fragment != null) && !this.fragment.isEmpty())
            {
                removedFragment = (FragmentImpl)this.fragment.iterator().next();
                this.fragment.clear();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getFragmentById(java.lang.String)
     */
    public Fragment getFragmentById(String id)
    {
        // get fragment by id and check access
        FragmentImpl rootFragment = (FragmentImpl)getRootFragment();
        if (rootFragment != null)
        {
            Fragment fragment = rootFragment.getFragmentById(id);
            if (fragment != null)
            {
                try
                {
                    fragment.checkAccess(SecuredResource.VIEW_ACTION);
                }
                catch (SecurityException se)
                {
                    fragment = null;
                }
            }
            return fragment;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#removeFragmentById(java.lang.String)
     */
    public Fragment removeFragmentById(String id)
    {
        // remove fragment by id
        FragmentImpl rootFragment = (FragmentImpl)getRootFragment();
        if (rootFragment != null)
        {
            if (rootFragment.getId().equals(id))
            {
                setRootFragment(null);
                return rootFragment;
            }
            else
            {
                return rootFragment.removeFragmentById(id);
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getFragmentsByName(java.lang.String)
     */
    public List getFragmentsByName(String name)
    {
        // get fragments by name and filter by access
        FragmentImpl rootFragment = (FragmentImpl)getRootFragment();
        if (rootFragment != null)
        {
            // return immutable filtered fragment list
            return rootFragment.filterFragmentsByAccess(rootFragment.getFragmentsByName(name), false);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getMenuDefinitions()
     */
    public List getMenuDefinitions()
    {
        // return mutable menu definition list
        // by using list wrapper to manage
        // element uniqueness
        if (menuDefinitions == null)
        {
            menuDefinitions = new PageMenuDefinitionList(this);
        }
        return menuDefinitions;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuDefinition()
     */
    public MenuDefinition newMenuDefinition()
    {
        return new PageMenuDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuExcludeDefinition()
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        return new PageMenuExcludeDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuIncludeDefinition()
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        return new PageMenuIncludeDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuOptionsDefinition()
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        return new PageMenuOptionsDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuSeparatorDefinition()
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        return new PageMenuSeparatorDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setMenuDefinitions(java.util.List)
     */
    public void setMenuDefinitions(List definitions)
    {
        // set menu definitions by replacing
        // existing entries with new elements if
        // new collection is specified
        List menuDefinitions = getMenuDefinitions();
        if (definitions != menuDefinitions)
        {
            // replace all menu definitions
            menuDefinitions.clear();
            if (definitions != null)
            {
                menuDefinitions.addAll(definitions);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        return DOCUMENT_TYPE;
    }
}
