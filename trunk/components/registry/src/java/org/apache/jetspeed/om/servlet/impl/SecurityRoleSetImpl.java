/*
 * Copyright 2000-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.om.servlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.jetspeed.om.common.servlet.MutableSecurityRoleSet;
import org.apache.pluto.om.common.SecurityRole;
import org.apache.pluto.om.common.SecurityRoleSet;

/**
 *
 * SecurityRoleRefSetImpl
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id$
 *
 */
public class SecurityRoleSetImpl implements SecurityRoleSet, MutableSecurityRoleSet, Serializable {

    protected Collection innerCollection;

    public SecurityRoleSetImpl() {
        innerCollection = new ArrayList();
    }

    public SecurityRoleSetImpl(Collection collection) {
        innerCollection = collection;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleSet#get(java.lang.String)
     */
    public SecurityRole get(String name) {
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext()) {
            SecurityRole role = (SecurityRole) itr.next();
            if (role.getRoleName().equals(name)) { return role; }
        }

        return null;
    }

    /**
     * @see org.apache.jetspeed.om.common.servlet.MutableSecurityRoleSet#add(org.apache.pluto.om.common.SecurityRole)
     */
    public SecurityRole add(SecurityRole securityRole) {
        if ( innerCollection.contains(securityRole)) {
            throw new IllegalArgumentException("SecurityRole "+securityRole.getRoleName()+" already defined.");
        }
        innerCollection.add(securityRole);
        return securityRole;
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o) {
        SecurityRole role = (SecurityRole) o;
        add(role);
        return true;
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        SecurityRole role = (SecurityRole) o;

        return innerCollection.remove(o);
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c) {
        // enforce unique role names in collection by adding them individually
        Iterator itr = c.iterator();
        while ( itr.hasNext() ) {
            add(itr.next());
        }
        return true;
    }

    /**
     * @see java.util.Collection#clear()
     */
    public void clear() {
        innerCollection.clear();

    }

    /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return innerCollection.contains(o);
    }

    /**
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection c) {
        return innerCollection.containsAll(c);
    }

    /**
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty() {
        return innerCollection.isEmpty();
    }

    /**
     * @see java.util.Collection#iterator()
     */
    public Iterator iterator() {
        return innerCollection.iterator();
    }

    /**
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c) {
        return innerCollection.removeAll(c);
    }

    /**
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection c) {
        return innerCollection.retainAll(c);
    }

    /**
     * @see java.util.Collection#size()
     */
    public int size() {
        return innerCollection.size();
    }

    /**
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray() {
        return innerCollection.toArray();
    }

    /**
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    public Object[] toArray(Object[] a) {
        return innerCollection.toArray(a);
    }

    /**
     * @return collection
     */
    public Collection getInnerCollection() {
        return innerCollection;
    }

    /**
     * @param collection
     */
    public void setInnerCollection(Collection collection) {
        innerCollection = collection;
    }

}
