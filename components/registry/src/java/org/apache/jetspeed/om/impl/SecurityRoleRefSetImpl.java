/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
