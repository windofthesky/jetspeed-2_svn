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
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.document.impl.DocumentImpl;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * BaseFragmentsElementImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class BaseFragmentsElementImpl extends DocumentImpl implements BaseFragmentsElement
{
    private String ojbConcreteClass = getClass().getName();
    private Collection<BaseFragmentElement> fragment;

    /**
     * Default constructor to specify security constraints
     */
    public BaseFragmentsElementImpl()
    {
        super(new PageSecurityConstraintsImpl());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.impl.NodeImpl#newPageMetadata(java.util.Collection)
     */
    public PageMetadataImpl newPageMetadata(Collection<LocalizedField> fields)
    {
        PageMetadataImpl pageMetadata = new PageMetadataImpl(PageMetadataLocalizedFieldImpl.class);
        pageMetadata.setFields(fields);
        return pageMetadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getRootFragment()
     */
    public BaseFragmentElement getRootFragment()
    {
        // get singleton fragment; no access checks to
        // be made for root fragment
        if ((fragment != null) && !fragment.isEmpty())
        {
            BaseFragmentElementImpl rootFragment = (BaseFragmentElementImpl)fragment.iterator().next();
            if (rootFragment.getBaseFragmentsElement() != this)
            {
                // set base fragments implementation in root and children fragments
                rootFragment.setBaseFragmentsElement(this);
            }
            return rootFragment;
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setRootFragment(org.apache.jetspeed.om.page.BaseFragmentElement)
     */
    public void setRootFragment(BaseFragmentElement fragment)
    {
        // create singleton collection or remove existing
        // root fragment and save for reuse
        if (fragment instanceof BaseFragmentElementImpl)
        {
            BaseFragmentElementImpl baseFragmentImpl = (BaseFragmentElementImpl)fragment;
            if (this.fragment == null)
            {
                this.fragment = DatabasePageManagerUtils.createList();
            }
            else if (!this.fragment.isEmpty())
            {
                this.fragment.clear();
            }

            // add new or reuse singleton fragment
            BaseFragmentElementImpl addFragment = (BaseFragmentElementImpl)fragment;
            BaseFragmentElementImpl reuseFragment = null;
            this.fragment.add(addFragment);

            // set base fragments implementation in root and children fragments
            addFragment.setBaseFragmentsElement(this);
        }
        else if (fragment == null)
        {
            // delete existing fragment if required, saving
            // removed fragment for later reuse
            if ((this.fragment != null) && !this.fragment.isEmpty())
            {
                this.fragment.clear();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getFragmentById(java.lang.String)
     */
    public BaseFragmentElement getFragmentById(String id)
    {
        // get fragment by id and check access
        BaseFragmentElementImpl rootFragment = (BaseFragmentElementImpl)getRootFragment();
        if (rootFragment != null)
        {
            BaseFragmentElementImpl fragment = (BaseFragmentElementImpl)rootFragment.getFragmentById(id);
            if (fragment != null)
            {
                try
                {
                    fragment.checkAccess(JetspeedActions.VIEW);
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
    public BaseFragmentElement removeFragmentById(String id)
    {
        // remove fragment by id
        BaseFragmentElementImpl rootFragment = (BaseFragmentElementImpl)getRootFragment();
        if (rootFragment != null)
        {
            if (!rootFragment.getId().equals(id))
            {
                if (rootFragment instanceof FragmentImpl)
                {
                    return ((FragmentImpl)rootFragment).removeFragmentById(id);
                }
            }
            else
            {
                try
                {
                    // check access
                    rootFragment.checkAccess(JetspeedActions.EDIT);
                    
                    // remove fragment
                    setRootFragment(null);
                    return rootFragment;
                }
                catch (SecurityException se)
                {
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getFragmentsByName(java.lang.String)
     */
    public List<BaseFragmentElement> getFragmentsByName(String name)
    {
        // get fragments by name and filter by access
        BaseFragmentElementImpl rootFragment = (BaseFragmentElementImpl)getRootFragment();
        if (rootFragment != null)
        {
            if (rootFragment instanceof FragmentImpl)
            {
                // return immutable filtered fragment list
                FragmentImpl rootFragmentImpl = (FragmentImpl)rootFragment;
                return rootFragmentImpl.filterFragmentsByAccess(rootFragmentImpl.getFragmentsByName(name), false);
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentsElement#getFragmentsByInterface(java.lang.Class)
     */
    public List<BaseFragmentElement> getFragmentsByInterface(Class interfaceFilter)
    {
        // get fragments by interface and filter by access
        BaseFragmentElementImpl rootFragment = (BaseFragmentElementImpl)getRootFragment();
        if (rootFragment != null)
        {
            if (rootFragment instanceof FragmentImpl)
            {
                // return immutable filtered fragment list
                FragmentImpl rootFragmentImpl = (FragmentImpl)rootFragment;
                return rootFragmentImpl.filterFragmentsByAccess(rootFragmentImpl.getFragmentsByInterface(interfaceFilter), false);
            }
            else if ((interfaceFilter == null) || interfaceFilter.isInstance(rootFragment))
            {
                try
                {
                    // check access
                    rootFragment.checkAccess(JetspeedActions.VIEW);
                    // return immutable filtered fragment list
                    List<BaseFragmentElement> fragmentsList = new ArrayList<BaseFragmentElement>();
                    fragmentsList.add(rootFragment);
                    return fragmentsList;
                }
                catch (SecurityException se)
                {
                }
            }
        }
        return null;
    }

    /**
     * Validate fragments.
     * 
     * @return validated flag
     */
    public boolean validateFragments()
    {
        // validate fragments using validation listener
        BaseFragmentElementImpl rootFragment = (BaseFragmentElementImpl)getRootFragment();
        if (rootFragment != null)
        {
            BaseFragmentValidationListener validationListener = newBaseFragmentValidationListener();
            return (rootFragment.validateFragments(validationListener) && validationListener.validate());
        }
        return false;
    }
    
    /**
     * Fragment validation listener.
     * 
     * @return validation listener
     */
    protected abstract BaseFragmentValidationListener newBaseFragmentValidationListener();
}
