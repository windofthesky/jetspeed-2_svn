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
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * FragmentImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentImpl extends BaseFragmentElementImpl implements Fragment
{
    private List<BaseFragmentElementImpl> fragments;
    private String type;

    private FragmentList fragmentsList;

    /**
     * accessFragments
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List<BaseFragmentElementImpl> accessFragments()
    {
        // create initial collection if necessary
        if (fragments == null)
        {
            fragments = DatabasePageManagerUtils.createList();
        }
        return fragments;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseFragmentElementImpl#setBaseFragmentsElement(org.apache.jetspeed.om.page.impl.BaseFragmentsElementImpl)
     */
    void setBaseFragmentsElement(BaseFragmentsElementImpl baseFragmentsElement)
    {
        // set page implementation
        super.setBaseFragmentsElement(baseFragmentsElement);
        // propagate to children
        if (fragments != null)
        {
            for (BaseFragmentElementImpl fragmentElement : fragments)
            {
                fragmentElement.setBaseFragmentsElement(baseFragmentsElement);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getFragmentById(java.lang.String)
     * @see org.apache.jetspeed.om.page.impl.BaseFragmentElementImpl#getFragmentById(java.lang.String)
     */
    public BaseFragmentElement getFragmentById(String id)
    {
        // check for match
        if (getId().equals(id))
        {
            return this;
        }
        // match children
        if (fragments != null)
        {
            for (BaseFragmentElementImpl fragmentElement : fragments)
            {
                BaseFragmentElement matchedFragment = fragmentElement.getFragmentById(id);
                if (matchedFragment != null)
                {
                    return matchedFragment;
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#removeFragmentById(java.lang.String)
     */
    public BaseFragmentElement removeFragmentById(String id)
    {
        // remove from deep children
        if (fragments != null)
        {
            for (Iterator<BaseFragmentElementImpl> fragmentsIter = fragments.iterator(); fragmentsIter.hasNext();)
            {
                BaseFragmentElementImpl fragment = fragmentsIter.next();
                if (!fragment.getId().equals(id))
                {
                    if (fragment instanceof FragmentImpl)
                    {
                        BaseFragmentElement removed = ((FragmentImpl)fragment).removeFragmentById(id);
                        if (removed != null)
                        {
                            return removed;
                        }
                    }
                }
                else
                {
                    try
                    {
                        // check access
                        fragment.checkAccess(JetspeedActions.EDIT);
                        
                        // remove fragment
                        fragmentsIter.remove();
                        return fragment;
                    }
                    catch (SecurityException se)
                    {
                    }
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseFragmentElementImpl#getFragmentsByName(java.lang.String)
     */
    List<BaseFragmentElement> getFragmentsByName(String name)
    {
        // check for match
        List<BaseFragmentElement> matchedFragments = super.getFragmentsByName(name);
        // match named children
        if (fragments != null)
        {
            for (BaseFragmentElementImpl fragmentElement : fragments)
            {
                List<BaseFragmentElement> matchedChildFragments = fragmentElement.getFragmentsByName(name);
                if (matchedChildFragments != null)
                {
                    if (matchedFragments == null)
                    {
                        matchedFragments = matchedChildFragments;
                    }
                    else
                    {
                        matchedFragments.addAll(matchedChildFragments);
                    }
                }
            }
        }
        return matchedFragments;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseFragmentElementImpl#getFragmentsByInterface(java.lang.Class)
     */
    List<BaseFragmentElement> getFragmentsByInterface(Class interfaceFilter)
    {
        // check for match
        List<BaseFragmentElement> matchedFragments = super.getFragmentsByInterface(interfaceFilter);
        // match children
        if (fragments != null)
        {
            for (BaseFragmentElementImpl fragmentElement : fragments)
            {
                List<BaseFragmentElement> matchedChildFragments = fragmentElement.getFragmentsByInterface(interfaceFilter);
                if (matchedChildFragments != null)
                {
                    if (matchedFragments == null)
                    {
                        matchedFragments = matchedChildFragments;
                    }
                    else
                    {
                        matchedFragments.addAll(matchedChildFragments);
                    }
                }
            }
        }
        return matchedFragments;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#resetCachedSecurityConstraints()
     */
    public void resetCachedSecurityConstraints()
    {
        // propagate to super and sub fragments
        super.resetCachedSecurityConstraints();
        if (fragments != null)
        {
            for (BaseFragmentElementImpl fragmentElement : fragments)
            {
                fragmentElement.resetCachedSecurityConstraints();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getLogicalPermissionPath()
     */
    public String getLogicalPermissionPath()
    {
        // use base fragments implementation path as base and append name
        if ((getBaseFragmentsElement() != null) && (getName() != null))
        {
            return getBaseFragmentsElement().getLogicalPermissionPath() + Folder.PATH_SEPARATOR + getName();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getPhysicalPermissionPath()
     */
    public String getPhysicalPermissionPath()
    {
        // use base fragments implementation path as base and append name
        if ((getBaseFragmentsElement() != null) && (getName() != null))
        {
            return getBaseFragmentsElement().getPhysicalPermissionPath() + Folder.PATH_SEPARATOR + getName();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getType()
     */
    public String getType()
    {
        return type;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setType(java.lang.String)
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getFragments()
     */
    public List<BaseFragmentElement> getFragments()
    {
        // create and return mutable fragments collection
        // filtered by view access
        if (fragmentsList == null)
        {
            fragmentsList = new FragmentList(this);
        }
        return filterFragmentsByAccess(fragmentsList, true);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseFragmentElementImpl#validateFragments(org.apache.jetspeed.om.page.BaseFragmentValidationListener)
     */
    protected boolean validateFragments(BaseFragmentValidationListener validationListener)
    {
        // validate fragment using validation listener
        if (!validationListener.validate(this))
        {
            return false;
        }
        // validate fragments using validation listener
        if (fragments != null)
        {
            for (BaseFragmentElementImpl fragmentElement : fragments)
            {
                if (!fragmentElement.validateFragments(validationListener))
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * filterFragmentsByAccess
     *
     * Filter fragments list for view access.
     *
     * @param fragments list containing fragments to check
     * @param mutable make returned list mutable
     * @return original list if all elements viewable, a filtered
     *         partial list, or null if all filtered for view access
     */
    List<BaseFragmentElement> filterFragmentsByAccess(List<BaseFragmentElement> fragments, boolean mutable)
    {
        if ((fragments != null) && !fragments.isEmpty())
        {
            // check permissions and constraints, filter fragments as required
            List<BaseFragmentElement> filteredFragments = null;
            for (BaseFragmentElement fragment : fragments)
            {
                try
                {
                    // check access
                    fragment.checkAccess(JetspeedActions.VIEW);

                    // add to filteredFragments fragments if copying
                    if (filteredFragments != null)
                    {
                        // permitted, add to filteredFragments fragments
                        filteredFragments.add(fragment);
                    }
                }
                catch (SecurityException se)
                {
                    // create filteredFragments fragments if not already copying
                    if (filteredFragments == null)
                    {
                        // not permitted, copy previously permitted fragments
                        // to new filteredFragments node set with same comparator
                        filteredFragments = new ArrayList<BaseFragmentElement>(fragments.size());
                        for (BaseFragmentElement copyFragment : fragments)
                        {
                            if (copyFragment != fragment)
                            {
                                filteredFragments.add(copyFragment);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
            }

            // return filteredFragments fragments if generated
            if (filteredFragments != null)
            {
                if (!filteredFragments.isEmpty())
                {
                    if (mutable)
                    {
                        return new FilteredFragmentList(this, filteredFragments);
                    }
                    else
                    {
                        return filteredFragments;
                    }
                }
                else
                {
                    return new FilteredFragmentList(this, filteredFragments);
                }
            }
        }
        return fragments;
    }
}
