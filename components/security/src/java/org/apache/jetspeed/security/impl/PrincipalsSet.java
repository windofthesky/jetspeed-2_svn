package org.apache.jetspeed.security.impl;

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
 * @version $Id: $
 */

public class PrincipalsSet implements Set
{
    List principals = new LinkedList();
    Set set = new HashSet();

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

    /* (non-Javadoc)
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray()
    {
        return principals.toArray();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
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
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c)
    {
        set.addAll(c);
        return principals.addAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection c)
    {
        return set.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c)
    {
        set.removeAll(c);
        return principals.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection c)
    {
        set.retainAll(c);
        return principals.retainAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#iterator()
     */
    public Iterator iterator()
    {
        return principals.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    public Object[] toArray(Object[] a)
    {
        return principals.toArray(a);
    }

}
