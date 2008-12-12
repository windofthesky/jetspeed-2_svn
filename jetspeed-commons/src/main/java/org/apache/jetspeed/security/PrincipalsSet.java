/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.security;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * PrincipalsSet - provides an ordered 'set' of principals required
 * for some profiling rules that are dependent on order of insert.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */

public class PrincipalsSet implements Set<Principal>
{
    List<Principal> principals = new LinkedList<Principal>();
    Set<Principal> set = new HashSet<Principal>();

    public PrincipalsSet()
    {}
    
    /* (non-Javadoc)
     * @see java.util.Collection#size()
     */
    public int size()
    {
        return principals.size();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#clear()
     */
    public void clear()
    {
        set.clear();
        principals.clear();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty()
    {
        return principals.isEmpty();
    }

    public <T> T[] toArray(T[] a)
    {
        return principals.toArray(a);
    }

    public Object[] toArray()
    {
        return principals.toArray();
    }
    
    /* (non-Javadoc)
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Principal o)
    {
        if (set.add(o))
        {
            principals.add(o);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object o)
    {
        return set.contains(o);
    }
    
    /* (non-Javadoc)
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        set.remove(o);
        return principals.remove(o);
    }
    
    /* (non-Javadoc)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c)
    {
        return set.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c)
    {
        set.removeAll(c);
        return principals.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c)
    {
        set.retainAll(c);
        return principals.retainAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#iterator()
     */
    public Iterator<Principal> iterator()
    {
        return principals.iterator();
    }

    public boolean addAll(Collection<? extends Principal> c)
    {
        set.addAll(c);
        return principals.addAll(c);
    }

}
