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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * FragmentList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentList implements List
{
    private FragmentImpl fragment;
    private List fragments;

    FragmentList(FragmentImpl fragment, List fragments)
    {
        this.fragment = fragment;
        this.fragments = fragments;
    }

    /**
     * getFragment
     *
     * Returns fragment implementation associated with this list.
     *
     * @return fragment implementation
     */
    FragmentImpl getFragment()
    {
        return fragment;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        // add and maintain page implementation reference
        boolean added = fragments.add(o);
        if (added && (fragment.getPage() != null) && (o instanceof FragmentImpl))
        {
            ((FragmentImpl)o).setPage(fragment.getPage());
        }
        return added;
    }
    
    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, Object element)
    {
        // add and maintain page implementation reference
        fragments.add(index, element);
        if ((fragment.getPage() != null) && (element instanceof FragmentImpl))
        {
            ((FragmentImpl)element).setPage(fragment.getPage());
        }
    }

    /* (non-Javadoc)
     * @see java.util.List#add(java.util.Collection)
     */
    public boolean addAll(Collection c)
    {
        // add and maintain page implementation reference
        boolean added = fragments.addAll(c);
        if (added && (fragment.getPage() != null))
        {
            Iterator addedIter = c.iterator();
            while (addedIter.hasNext())
            {
                Object o = addedIter.next();
                if (fragments.contains(o) && (o instanceof FragmentImpl))
                {
                    ((FragmentImpl)o).setPage(fragment.getPage());
                }
            }
        }
        return added;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.util.Collection)
     */
    public boolean addAll(int index, Collection c)
    {
        // add and maintain page implementation reference
        boolean added = fragments.addAll(index, c);
        if (added && (fragment.getPage() != null))
        {
            Iterator addedIter = c.iterator();
            while (addedIter.hasNext())
            {
                Object o = addedIter.next();
                if (fragments.contains(o) && (o instanceof FragmentImpl))
                {
                    ((FragmentImpl)o).setPage(fragment.getPage());
                }
            }
        }
        return added;
    }

    /* (non-Javadoc)
     * @see java.util.List#clear()
     */
    public void clear()
    {
        fragments.clear();
    }

    /* (non-Javadoc)
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object o)
    {
        return fragments.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.ListcontainsAll#(java.util.Collection)
     */
    public boolean containsAll(Collection c)
    {
        return fragments.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        return fragments.equals(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public Object get(int index)
    {
        return fragments.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#hashCode()
     */
    public int hashCode()
    {
        return fragments.hashCode();
    }

    /* (non-Javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o)
    {
        return fragments.indexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty()
    {
        return fragments.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.List#iterator()
     */
    public Iterator iterator()
    {
        return fragments.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object o)
    {
        return fragments.lastIndexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator()
     */
    public ListIterator listIterator()
    {
        return listIterator(0);
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator(int)
     */
    public ListIterator listIterator(final int index)
    {
        // return new iterator
        return new ListIterator()
            {
                ListIterator iter = FragmentList.this.fragments.listIterator(index);

                /* (non-Javadoc)
                 * @see java.util.ListIterator#add(java.lang.Object)
                 */
                public void add(Object o)
                {
                    // add and maintain page implementation reference
                    iter.add(o);
                    if ((FragmentList.this.fragment.getPage() != null) && (o instanceof FragmentImpl))
                    {
                        ((FragmentImpl)o).setPage(FragmentList.this.fragment.getPage());
                    }
                }

                /* (non-Javadoc)
                 * @see java.util.ListIterator#hasNext()
                 */
                public boolean hasNext()
                {
                    return iter.hasNext();
                }

                /* (non-Javadoc)
                 * @see java.util.ListIterator#hasPrevious()
                 */
                public boolean hasPrevious()
                {
                    return iter.hasPrevious();                    
                }

                /* (non-Javadoc)
                 * @see java.util.ListIterator#next()
                 */
                public Object next()
                {
                    return iter.next();
                }

                /* (non-Javadoc)
                 * @see java.util.ListIterator#nextIndex()
                 */
                public int nextIndex()
                {
                    return iter.nextIndex();
                }

                /* (non-Javadoc)
                 * @see java.util.ListIterator#previous()
                 */
                public Object previous()
                {
                    return iter.previous();
                }

                /* (non-Javadoc)
                 * @see java.util.ListIterator#previousIndex()
                 */
                public int previousIndex()
                {
                    return iter.previousIndex();
                }

                /* (non-Javadoc)
                 * @see java.util.ListIterator#remove()
                 */
                public void remove()
                {
                    iter.remove();
                }

                /* (non-Javadoc)
                 * @see java.util.ListIterator#set(java.lang.Object)
                 */
                public void set(Object o)
                {
                    // set and maintain page implementation reference
                    iter.set(o);
                    if ((FragmentList.this.fragment.getPage() != null) && (o instanceof FragmentImpl))
                    {
                        ((FragmentImpl)o).setPage(FragmentList.this.fragment.getPage());
                    }
                }
            };
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public Object remove(int index)
    {
        return fragments.remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        return fragments.remove(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c)
    {
        return fragments.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection c)
    {
        return fragments.retainAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public Object set(int index, Object element)
    {
        // set and maintain page implementation reference
        Object o = fragments.set(index, element);
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
        return fragments.size();
    }

    /* (non-Javadoc)
     * @see java.util.List#subList(int, int)
     */
    public List subList(int fromIndex, int toIndex)
    {
        return fragments.subList(fromIndex, toIndex);
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray()
     */
    public Object[] toArray()
    {
        return fragments.toArray();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray(java.lang.Object[])
     */
    public Object[] toArray(Object[] a) 
    {
        return fragments.toArray(a);
    }
}
