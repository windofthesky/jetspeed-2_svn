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

/**
 * FragmentList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class FragmentList extends AbstractList
{
    private FragmentImpl fragment;

    FragmentList(FragmentImpl fragment)
    {
        super();
        this.fragment = fragment;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // add and maintain page implementation reference
        fragment.accessFragments().add(index, element);
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
        return fragment.accessFragments().get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public Object remove(int index)
    {
        // implement for modifiable AbstractList
        return fragment.accessFragments().remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public Object set(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // set and maintain page implementation reference
        Object o = fragment.accessFragments().set(index, element);
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
        return fragment.accessFragments().size();
    }
}
