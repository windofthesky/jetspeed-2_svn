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

import java.io.Serializable;

/**
 * This class implements a wrapper used to implement
 * the ordered polymorphic fragment elements collection.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class FragmentElementImpl implements Serializable
{
    /**
     * element - wrapped menu element
     */
    private Object element;

    /**
     * MenuElementImpl - constructor
     */
    public FragmentElementImpl()
    {
    }

    /**
     * MenuElementImpl - constructor
     */
    public FragmentElementImpl(Object element)
    {
        this.element = element;
    }

    /**
     * getOption - get wrapped menu element
     */
    public Object getElement()
    {
        return element;
    }

    /**
     * getFragment - get wrapped fragment definition
     */
    public FragmentImpl getFragment()
    {
        if (element instanceof FragmentImpl)
        {
            return (FragmentImpl)element;
        }
        return null;
    }

    /**
     * setFragment - set wrapped fragment definition
     *
     * @param fragment fragment definition
     */
    public void setFragment(FragmentImpl fragment)
    {
        this.element = fragment;
    }

    /**
     * getFragmentReference - get wrapped fragment reference definition
     */
    public FragmentReferenceImpl getFragmentReference()
    {
        if (element instanceof FragmentReferenceImpl)
        {
            return (FragmentReferenceImpl)element;
        }
        return null;
    }

    /**
     * setFragmentReference - set wrapped fragment reference definition
     *
     * @param fragmentReference fragment reference definition
     */
    public void setFragmentReference(FragmentReferenceImpl fragmentReference)
    {
        this.element = fragmentReference;
    }

    /**
     * getPageFragment - get wrapped page fragment definition
     */
    public PageFragmentImpl getPageFragment()
    {
        if (element instanceof PageFragmentImpl)
        {
            return (PageFragmentImpl)element;
        }
        return null;
    }

    /**
     * setPageFragment - set wrapped page fragment definition
     *
     * @param pageFragment page fragment definition
     */
    public void setPageFragment(PageFragmentImpl pageFragment)
    {
        this.element = pageFragment;
    }
}
