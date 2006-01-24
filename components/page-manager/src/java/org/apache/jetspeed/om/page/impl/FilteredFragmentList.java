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

import java.util.AbstractList;
import java.util.List;
import java.util.ListIterator;

/**
 * FragmentList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class FilteredFragmentList extends AbstractList
{
    private FragmentImpl fragment;
    private List filteredList;

    FilteredFragmentList(FragmentImpl fragment, List filteredList)
    {
        super();
        this.fragment = fragment;
        this.filteredList = filteredList;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, Object element)
    {
        // implement for modifiable AbstractList
        filteredList.add(index, element);
        // add object to persistent list
        fragment.accessFragments().add(element);
        // maintain page implementation reference
        if ((fragment.getPage() != null) && (element instanceof FragmentImpl))
        {
            ((FragmentImpl)element).setPage(fragment.getPage());
        }
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public Object get(int index)
    {
        // implement for modifiable AbstractList
        return filteredList.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public Object remove(int index)
    {
        // implement for modifiable AbstractList
        Object o = filteredList.remove(index);
        // remove removed object from persistent list
        if (o != null)
        {
            ListIterator iter = fragment.accessFragments().listIterator();
            while (iter.hasNext())
            {
                if (iter.next() == o)
                {
                    iter.remove();
                    break;
                }
            }
        }
        return o;
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public Object set(int index, Object element)
    {
        // implement for modifiable AbstractList
        Object o = filteredList.set(index, element);
        // replace replaced object in persistent list
        if (o != null)
        {
            ListIterator iter = fragment.accessFragments().listIterator();
            while (iter.hasNext())
            {
                if (iter.next() == o)
                {
                    iter.set(element);
                    break;
                }
            }
        }
        // maintain page implementation reference
        if ((fragment.getPage() != null) && (element instanceof FragmentImpl))
        {
            ((FragmentImpl)element).setPage(fragment.getPage());
        }
        return o;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return filteredList.size();
    }
}
