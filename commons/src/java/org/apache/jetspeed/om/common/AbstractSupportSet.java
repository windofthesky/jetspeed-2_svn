/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.om.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * AbstractSupportSet
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public abstract class AbstractSupportSet implements Set, Serializable
{
    private Set supportSet;

    /**
     * 
     */
    public AbstractSupportSet()
    {
        supportSet = new HashSet();
    }

    /**
     * @param c
     */
    public AbstractSupportSet(Collection c)
    {
        supportSet = new HashSet(c);
    }

    /**
     * @param initialCapacity
     * @param loadFactor
     */
    public AbstractSupportSet(int initialCapacity, float loadFactor)
    {
        supportSet = new HashSet(initialCapacity, loadFactor);
    }

    /**
     * Uses the <code>wrappedSet</code> as the Set
     * used to back the abstract support set.  All operations
     * performed are performed directly on the <code>wrappedSet</code> 
     * 
     * @param wrappedSet
     */
    public AbstractSupportSet(Set wrappedSet)
    {
        this.supportSet = wrappedSet;
    }

    /**
     * @param initialCapacity
     */
    public AbstractSupportSet(int initialCapacity)
    {
        supportSet = new HashSet(initialCapacity);
    }

    // This section is supported interally by a HashSet

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        return supportSet.add(o);
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c)
    {
        return supportSet.addAll(c);
    }

    /**
     * @see java.util.Collection#clear()
     */
    public void clear()
    {
        supportSet.clear();
    }

    /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object o)
    {
        return supportSet.contains(o);
    }

    /**
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection c)
    {
        return supportSet.containsAll(c);
    }

    /**
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty()
    {
        return supportSet.isEmpty();
    }

    /**
     * @see java.util.Collection#iterator()
     */
    public Iterator iterator()
    {
        return supportSet.iterator();
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        return supportSet.remove(o);
    }

    /**
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c)
    {
        return supportSet.removeAll(c);
    }

    /**
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection c)
    {
        return supportSet.removeAll(c);
    }

    /**
     * @see java.util.Collection#size()
     */
    public int size()
    {
        return supportSet.size();
    }

    /**
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray()
    {
        return supportSet.toArray();
    }

    /**
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    public Object[] toArray(Object[] a)
    {
        return supportSet.toArray(a);
    }

    public Set getWrappedSet()
    {
        return supportSet;
    }

    public void setWrappedSet(Set wrappedSet)
    {
        supportSet = wrappedSet;
    }

}
