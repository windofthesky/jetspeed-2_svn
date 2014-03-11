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

package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @version $Id$
 */
public class FragmentImpl extends AbstractBaseFragmentElement implements Fragment, java.io.Serializable
{
    private String type = null;

    private List fragments = new ArrayList();

    private List fragmentElementImpls = new ArrayList();

    private String name;

    private FragmentList fragmentsList;

    /**
     * <p>
     * Default Constructor.
     * </p>
     */
    public FragmentImpl()
    {
    }

    public String getType()
    {
        return this.type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    List accessFragments()
    {
        return fragments;
    }

    public List<BaseFragmentElement> getFragments()
    {
        // create and return mutable fragments collection
        // filtered by view access
        if (fragmentsList == null)
        {
            fragmentsList = new FragmentList(this);
        }
        return filterFragmentsByAccess(fragmentsList);
    }

    /**
     * getFragmentElementImpls - get list of wrapped fragment elements
     *
     * @return wrapped element list
     */
    public List getFragmentElementImpls()
    {
        return fragmentElementImpls;
    }

    /**
     * setFragmentElementImpls - set list of wrapped fragment elements
     *
     * @param elements wrapped element list
     */
    public void setFragmentElementImpls(List elements)
    {
        fragmentElementImpls = elements;
    }
    
    /**
     * <p>
     * getName
     * </p>
     * 
     * @see org.apache.jetspeed.om.page.Fragment#getName()
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * <p>
     * setName
     * </p>
     * 
     * @see org.apache.jetspeed.om.page.Fragment#setName(java.lang.String)
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;

    }

    void setBaseFragmentsElement(AbstractBaseFragmentsElement baseFragmentsElement)
    {
        // set base fragments implementation
        super.setBaseFragmentsElement(baseFragmentsElement);
        // propagate to children
        if (fragments != null)
        {
            Iterator fragmentsIter = fragments.iterator();
            while (fragmentsIter.hasNext())
            {
                ((AbstractBaseFragmentElement)fragmentsIter.next()).setBaseFragmentsElement(baseFragmentsElement);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getFragmentById(java.lang.String)
     */
    public BaseFragmentElement getFragmentById( String id )
    {
        Stack stack = new Stack();
        Iterator i = getFragments().iterator();
        while (i.hasNext())
        {
            stack.push(i.next());
        }

        BaseFragmentElement f = (BaseFragmentElement) stack.pop();

        while ((f != null) && (!(f.getId().equals(id))))
        {
            if (f instanceof Fragment)
            {
                i = ((Fragment)f).getFragments().iterator();

                while (i.hasNext())
                {
                    stack.push(i.next());
                }
            }

            if (stack.size() > 0)
            {
                f = (BaseFragmentElement) stack.pop();
            }
            else
            {
                f = null;
            }
        }

        return f;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#removeFragmentById(java.lang.String)
     */
    public BaseFragmentElement removeFragmentById( String id )
    {
        // find fragment by id, tracking fragment parent
        Map parents = new HashMap();
        Stack stack = new Stack();
        Iterator i = getFragments().iterator();
        while (i.hasNext())
        {
            stack.push(i.next());
        }

        BaseFragmentElement f = (BaseFragmentElement) stack.pop();
        while ((f != null) && (!(f.getId().equals(id))))
        {
            if (f instanceof Fragment)
            {
                i = ((Fragment)f).getFragments().iterator();

                while (i.hasNext())
                {
                    BaseFragmentElement child = (BaseFragmentElement)i.next();
                    stack.push(child);
                    parents.put(child, f);
                }
            }

            if (stack.size() > 0)
            {
                f = (BaseFragmentElement) stack.pop();
            }
            else
            {
                f = null;
            }
        }

        // remove fragment from parent/fragments
        if (f != null)
        {
            BaseFragmentElement parent = (BaseFragmentElement)parents.get(f);
            if (parent != null)
            {
                if (parent instanceof Fragment)
                {
                    if (((Fragment)parent).getFragments().remove(f))
                    {
                        return f;
                    }
                }
            }
            else
            {
                if (getFragments().remove(f))
                {
                    return f;
                }
            }
        }

        // not found or removed
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.psml.AbstractElementImpl#getLogicalPermissionPath()
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
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseElementImpl#getPhysicalPermissionPath()
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
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseFragmentElement#validateFragments(org.apache.jetspeed.om.page.BaseFragmentValidationListener)
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
            Iterator fragmentsIter = fragments.iterator();
            while (fragmentsIter.hasNext())
            {
                if (!((AbstractBaseFragmentElement)fragmentsIter.next()).validateFragments(validationListener))
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     * @param generator id generator
     * @return dirty flag
     */
    public boolean unmarshalled(IdGenerator generator)
    {
        // notify super class implementation
        boolean dirty = super.unmarshalled(generator);
        
        // unwrap fragment elements and propagate
        // unmarshalled notification
        fragments.clear();
        Iterator fragmentElementIter = fragmentElementImpls.iterator();
        while (fragmentElementIter.hasNext())
        {
            // unwrap fragment element
            FragmentElementImpl fragmentElement = (FragmentElementImpl)fragmentElementIter.next();
            AbstractBaseFragmentElement fragment = (AbstractBaseFragmentElement)fragmentElement.getElement();
            fragments.add(fragment);
            
            // propagate unmarshalled notification
            dirty = fragment.unmarshalled(generator) || dirty;
        }

        return dirty;
    }

    /**
     * marshalling - notification that this instance is to
     *               be saved to the persistent store
     */
    public void marshalling()
    {
        // wrap menu elements and propagate
        // marshalling notification
        fragmentElementImpls.clear();
        Iterator fragmentIter = fragments.iterator();
        while (fragmentIter.hasNext())
        {
            // wrap fragment element
            AbstractBaseFragmentElement fragment = (AbstractBaseFragmentElement)fragmentIter.next();
            fragmentElementImpls.add(new FragmentElementImpl(fragment));

            // propagate marshalling notification
            fragment.marshalling();
        }

        // notify super class implementation
        super.marshalling();
    }

    /**
     * filterFragmentsByAccess
     *
     * Filter fragments list for view access.
     *
     * @param fragments list containing fragments to check
     * @return original list if all elements viewable, a filtered
     *         partial list, or null if all filtered for view access
     */
    List<BaseFragmentElement> filterFragmentsByAccess(List<BaseFragmentElement> fragments)
    {
        if ((fragments != null) && !fragments.isEmpty())
        {
            // check permissions and constraints, filter fragments as required
            List<BaseFragmentElement> filteredFragments = null;
            Iterator checkAccessIter = fragments.iterator();
            while (checkAccessIter.hasNext())
            {
                BaseFragmentElement fragment = (BaseFragmentElement) checkAccessIter.next();
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
                        Iterator copyIter = fragments.iterator();
                        while (copyIter.hasNext())
                        {
                            BaseFragmentElement copyFragment = (BaseFragmentElement)copyIter.next();
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
                // patch for JS2-633, security filtered (permission) lists
                // were returning null, we need an empty fragment list 
                return new FilteredFragmentList(this, filteredFragments);
            }
        }
        return fragments;
    }
}
