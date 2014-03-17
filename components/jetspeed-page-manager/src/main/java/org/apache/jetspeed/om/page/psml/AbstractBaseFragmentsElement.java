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

import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * AbstractBaseFragmentsElement
 *
 * @version $Id:$
 */
public abstract class AbstractBaseFragmentsElement extends DocumentImpl implements BaseFragmentsElement
{
    private static final long serialVersionUID = 1L;

    private BaseFragmentElement root = null;
    
    private FragmentElementImpl rootFragmentElementImpl = null;

    private int hashCode;

    /**
     * <p>
     * setId
     * </p>
     *
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseElement#setId(java.lang.String)
     * @param id
     */
    public void setId( String id )
    {
        // Cheaper to generate the hash code now then every call to hashCode()
        hashCode = (getClass().getName()+":"+id).hashCode();
        super.setId(id);        
    }
    
    /**
     * <p>
     * equals
     * </p>
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj
     * @return
     */
    public boolean equals( Object obj )
    {
        if (obj instanceof BaseFragmentsElement)
        {
            BaseFragmentsElement fragmentsElement = (BaseFragmentsElement) obj;
            return fragmentsElement.getId() != null && getId() != null && getId().equals(fragmentsElement.getId());
        }
        return false;
    }

    /**
     * <p>
     * hashCode
     * </p>
     * 
     * @see java.lang.Object#hashCode()
     * @return
     */
    public int hashCode()
    {       
        return hashCode;
    }

    public BaseFragmentElement getRootFragment()
    {
        return this.root;
    }

    public void setRootFragment( BaseFragmentElement root )
    {
        this.root = root;
        if (root instanceof AbstractBaseFragmentElement)
        {
            ((AbstractBaseFragmentElement)root).setBaseFragmentsElement(this);
        }        
    }

    /**
     * getRootFragmentElementImpl - get wrapped fragment element
     *
     * @return wrapped element
     */
    public FragmentElementImpl getRootFragmentElementImpl()
    {
        return rootFragmentElementImpl;
    }

    /**
     * setRootFragmentElementImpl - set wrapped fragment element
     *
     * @param element wrapped element
     */
    public void setRootFragmentElementImpl(FragmentElementImpl element)
    {
        rootFragmentElementImpl = element;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentsElement#getFragmentById(java.lang.String)
     */
    public BaseFragmentElement getFragmentById(String id)
    {
        Stack<BaseFragmentElement> stack = new Stack<BaseFragmentElement>();
        if (getRootFragment() != null)
        {
            stack.push(getRootFragment());
        }

        BaseFragmentElement f = stack.pop();

        while ((f != null) && (!(f.getId().equals(id))))
        {
            if (f instanceof Fragment)
            {
                for (BaseFragmentElement child : ((Fragment)f).getFragments())
                {
                    stack.push(child);
                }
            }

            if (stack.size() > 0)
            {
                f = stack.pop();
            }
            else
            {
                f = null;
            }
        }

        return f;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentsElement#removeFragmentById(java.lang.String)
     */
    public BaseFragmentElement removeFragmentById(String id)
    {
        // find fragment by id, tracking fragment parent
        Map<BaseFragmentElement,BaseFragmentElement> parents = new HashMap<BaseFragmentElement,BaseFragmentElement>();
        Stack<BaseFragmentElement> stack = new Stack<BaseFragmentElement>();
        if (getRootFragment() != null)
        {
            stack.push(getRootFragment());
        }
        BaseFragmentElement f = stack.pop();
        while ((f != null) && (!(f.getId().equals(id))))
        {
            if (f instanceof Fragment)
            {
                for (BaseFragmentElement child : ((Fragment)f).getFragments())
                {
                    stack.push(child);
                    parents.put(child, f);
                }
            }

            if (stack.size() > 0)
            {
                f = stack.pop();
            }
            else
            {
                f = null;
            }
        }

        // remove fragment from parent/page root
        if (f != null)
        {
            BaseFragmentElement parent = parents.get(f);
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
                if (f == root)
                {
                    root = null;
                    return f;
                }
            }
        }

        // not found or removed
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentsElement#getFragmentsByName(java.lang.String)
     */
    public List<BaseFragmentElement> getFragmentsByName(String name)
    {
        List<BaseFragmentElement> fragments = new ArrayList<BaseFragmentElement>();

        Stack<BaseFragmentElement> stack = new Stack<BaseFragmentElement>();
        if (getRootFragment() != null)
        {
            stack.push(getRootFragment());
        }

        BaseFragmentElement f = stack.pop();

        while (f != null)
        {
            if (f instanceof Fragment)
            {
                Fragment fragment = (Fragment)f;
                if ((fragment.getName() != null) && fragment.getName().equals(name))
                {
                    fragments.add(fragment);
                }

                for (BaseFragmentElement child : fragment.getFragments())
                {
                    stack.push(child);
                }
            }

            if (stack.size() > 0)
            {
                f = stack.pop();
            }
            else
            {
                f = null;
            }
        }

        return fragments;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentsElement#getFragmentsByInterface(java.lang.Class)
     */
    public List<BaseFragmentElement> getFragmentsByInterface(Class interfaceFilter)
    {
        List<BaseFragmentElement> fragments = new ArrayList<BaseFragmentElement>();

        Stack<BaseFragmentElement> stack = new Stack<BaseFragmentElement>();
        if (getRootFragment() != null)
        {
            stack.push(getRootFragment());
        }
        
        BaseFragmentElement f = (stack.isEmpty() ? null : stack.pop());

        while (f != null)
        {
            if ((interfaceFilter == null) || interfaceFilter.isInstance(f))
            {
                fragments.add(f);
            }

            if (f instanceof Fragment)
            {
                Fragment fragment = (Fragment)f;
                for (BaseFragmentElement child : fragment.getFragments())
                {
                    stack.push(child);
                }
            }

            f = (stack.isEmpty() ? null : stack.pop());
        }

        return fragments;
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

        // unwrap fragment element
        root = (AbstractBaseFragmentElement)((rootFragmentElementImpl != null) ? rootFragmentElementImpl.getElement() : null);
        
        // propagate unmarshalled notification
        // and page to root fragment
        if (root != null)
        {
            // propagate unmarshalled notification
            AbstractBaseFragmentElement rootFragment = (AbstractBaseFragmentElement)root;
            dirty = (rootFragment.unmarshalled(generator) || dirty);

            // propagate page
            rootFragment.setBaseFragmentsElement(this);
        }

        return dirty;
    }

    /**
     * marshalling - notification that this instance is to
     *               be saved to the persistent store
     */
    public void marshalling()
    {
        // wrap fragment element
        rootFragmentElementImpl = ((root != null) ? new FragmentElementImpl(root) : null);

        // propagate marshalling notification
        // to root fragment
        if (root != null)
        {
            ((AbstractBaseFragmentElement)root).marshalling();
        }

        // notify super class implementation
        super.marshalling();
    }
    
    /**
     * Validate fragments.
     * 
     * @return validated flag
     */
    public boolean validateFragments()
    {
        // validate fragments using validation listener
        AbstractBaseFragmentElement rootFragment = (AbstractBaseFragmentElement)getRootFragment();
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
