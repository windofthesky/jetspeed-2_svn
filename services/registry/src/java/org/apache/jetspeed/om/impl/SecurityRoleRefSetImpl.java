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
package org.apache.jetspeed.om.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.common.SecurityRoleRefSetCtrl;

/**
 * 
 * SecurityRoleRefSetImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class SecurityRoleRefSetImpl implements SecurityRoleRefSet, SecurityRoleRefSetCtrl, Serializable
{

    protected Collection innerCollection;

    public SecurityRoleRefSetImpl()
    {
        innerCollection = new ArrayList();
    }

    public SecurityRoleRefSetImpl(Collection collection)
    {
        innerCollection = collection;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefSet#get(java.lang.String)
     */
    public SecurityRoleRef get(String name)
    {
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext())
        {
			SecurityRoleRef roleRef = (SecurityRoleRef) itr.next();
            if (roleRef.getRoleName().equals(name))
            {
                return roleRef;
            }
        }

        return null;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefSetCtrl#add(java.lang.String, java.lang.String, java.lang.String)
     */
    public SecurityRoleRef add(String roleName, String roleLink, String description)
    {
        // TODO Fix me.  We should try not to directly use implementation classes
        SecurityRoleRefImpl newRef = new SecurityRoleRefImpl();
        newRef.setRoleName(roleName);
        newRef.setRoleLink(roleLink);
        newRef.setDescription(description);
        add(newRef);
        return newRef;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefSetCtrl#add(org.apache.pluto.om.common.SecurityRoleRef)
     */
    public SecurityRoleRef add(SecurityRoleRef securityRoleRef)
    {
        innerCollection.add(securityRoleRef);
        return securityRoleRef;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefSetCtrl#remove(java.lang.String)
     */
    public SecurityRoleRef remove(String name)
    {
        SecurityRoleRef roleRef = get(name);
        if(roleRef != null)
        {
        	innerCollection.remove(roleRef);
        }
        
        return roleRef;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefSetCtrl#remove(org.apache.pluto.om.common.SecurityRoleRef)
     */
    public void remove(SecurityRoleRef securityRoleRef)
    {
        innerCollection.remove(securityRoleRef);

    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        SecurityRoleRef ref = (SecurityRoleRef) o;
        if(innerCollection.contains(o))
        {
        	remove(o);
        }
        return innerCollection.add(o);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        SecurityRoleRef ref = (SecurityRoleRef) o;
        
        return innerCollection.remove(o);
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefSetCtrl#add(java.lang.String, java.lang.String, org.apache.pluto.om.common.DescriptionSet)
     */
    public SecurityRoleRef add(String roleName, String roleLink, DescriptionSet descriptions)
    {
        SecurityRoleRefImpl newRef = new SecurityRoleRefImpl();
        newRef.setRoleName(roleName);
        newRef.setRoleLink(roleLink);
        newRef.setDescriptionSet(descriptions);
        add(newRef);
        return newRef;
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c)
    {        
        return innerCollection.addAll(c);
    }

    /**
     * @see java.util.Collection#clear()
     */
    public void clear()
    {
        innerCollection.clear();

    }

    /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object o)
    {        
        return innerCollection.contains(o);
    }

    /**
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection c)
    {        
        return innerCollection.containsAll(c);
    }

    /**
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty()
    {        
        return innerCollection.isEmpty();
    }

    /**
     * @see java.util.Collection#iterator()
     */
    public Iterator iterator()
    {        
        return innerCollection.iterator();
    }

    /**
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c)
    {        
        return innerCollection.removeAll(c);
    }

    /**
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection c)
    {        
        return innerCollection.retainAll(c);
    }

    /**
     * @see java.util.Collection#size()
     */
    public int size()
    {        
        return innerCollection.size();
    }

    /**
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray()
    {        
        return innerCollection.toArray();
    }

    /**
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    public Object[] toArray(Object[] a)
    {        
        return innerCollection.toArray(a);
    }

    /**
     * @return
     */
    public Collection getInnerCollection()
    {
        return innerCollection;
    }

    /**
     * @param collection
     */
    public void setInnerCollection(Collection collection)
    {
        innerCollection = collection;
    }

}
