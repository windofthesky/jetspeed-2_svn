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

import java.util.Collection;
import java.util.List;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.page.document.impl.DocumentImpl;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

/**
 * BaseFragmentsElementImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class BaseFragmentsElementImpl extends DocumentImpl implements BaseFragmentsElement
{
    private String ojbConcreteClass = getClass().getName();
    private Collection fragment;

    private BaseFragmentElementImpl removedFragment;

    /**
     * Default constructor to specify security constraints
     */
    public BaseFragmentsElementImpl()
    {
        super(new PageSecurityConstraintsImpl());
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
                removedFragment = (BaseFragmentElementImpl)this.fragment.iterator().next();
                this.fragment.clear();
            }

            // add new or reuse singleton fragment
            BaseFragmentElementImpl addFragment = (BaseFragmentElementImpl)fragment;
            BaseFragmentElementImpl reuseFragment = null;
            if (fragment instanceof FragmentImpl)
            {
                // add new fragment or copy configuration
                // from previously removed fragment
                if (removedFragment instanceof FragmentImpl)
                {
                    // reuse previously removed fragment
                    reuseFragment = removedFragment;
                    addFragment = reuseFragment;
                    removedFragment = null;
                    // TODO: move this logic to copy methods on implementations
                    FragmentImpl fragmentImpl = (FragmentImpl)fragment;
                    FragmentImpl reuseFragmentImpl = (FragmentImpl)reuseFragment;
                    reuseFragmentImpl.setName(fragmentImpl.getName());                
                    reuseFragmentImpl.setType(fragmentImpl.getType());
                    reuseFragmentImpl.getFragments().clear();
                    reuseFragmentImpl.getFragments().addAll(fragmentImpl.getFragments());
                }
            }
            else if (fragment instanceof FragmentReferenceImpl)
            {
                // add new fragment or copy configuration
                // from previously removed fragment
                if (removedFragment instanceof FragmentReferenceImpl)
                {
                    // reuse previously removed fragment
                    reuseFragment = removedFragment;
                    addFragment = reuseFragment;
                    removedFragment = null;
                    // TODO: move this logic to copy methods on implementations
                    FragmentReferenceImpl fragmentImpl = (FragmentReferenceImpl)fragment;
                    FragmentReferenceImpl reuseFragmentImpl = (FragmentReferenceImpl)reuseFragment;
                    reuseFragmentImpl.setRefId(fragmentImpl.getRefId());                
                }
            }
            else if (fragment instanceof PageFragmentImpl)
            {
                // add new fragment or copy configuration
                // from previously removed fragment
                if (removedFragment instanceof FragmentReferenceImpl)
                {
                    // reuse previously removed fragment
                    reuseFragment = removedFragment;
                    addFragment = reuseFragment;
                    removedFragment = null;
                }
            }
            if (reuseFragment != null)
            {
                // TODO: move this logic to copy methods on implementations
                reuseFragment.setTitle(baseFragmentImpl.getTitle());
                reuseFragment.setShortTitle(baseFragmentImpl.getShortTitle());
                reuseFragment.setSkin(baseFragmentImpl.getSkin());
                reuseFragment.setDecorator(baseFragmentImpl.getDecorator());
                reuseFragment.setState(baseFragmentImpl.getState());
                reuseFragment.setSecurityConstraints(baseFragmentImpl.getSecurityConstraints());
                reuseFragment.getProperties().clear();
                reuseFragment.getProperties().putAll(baseFragmentImpl.getProperties());
                reuseFragment.setPreferences(baseFragmentImpl.getPreferences());
            }
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
                removedFragment = (BaseFragmentElementImpl)this.fragment.iterator().next();
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
            if (rootFragment.getId().equals(id))
            {
                setRootFragment(null);
                return rootFragment;
            }
            else if (rootFragment instanceof FragmentImpl)
            {
                return ((FragmentImpl)rootFragment).removeFragmentById(id);
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
        BaseFragmentElementImpl rootFragment = (BaseFragmentElementImpl)getRootFragment();
        if (rootFragment instanceof FragmentImpl)
        {
            // return immutable filtered fragment list
            FragmentImpl rootFragmentImpl = (FragmentImpl)rootFragment;
            return rootFragmentImpl.filterFragmentsByAccess(rootFragmentImpl.getFragmentsByName(name), false);
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
