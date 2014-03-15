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
package org.apache.jetspeed.om.page.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SecurityConstraintsRefList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class SecurityConstraintsRefList extends AbstractList<String>
{
    private SecurityConstraintsImpl constraints;

    private List<BaseSecurityConstraintsRef> removedConstraintsRefs;

    SecurityConstraintsRefList(SecurityConstraintsImpl constraints)
    {
        super();
        this.constraints = constraints;
    }

    /**
     * wrapNameStringForAdd
     *
     * Wraps and validates constraints ref name string
     * to be added to this list.
     *
     * @param name constraints ref name string to add
     * @return list element to add
     */
    private BaseSecurityConstraintsRef wrapNameStringForAdd(String name)
    {
        // only non-null names supported
        if (name == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        // wrap constraints ref name string; use
        // specific constraints ref name string wrapper
        BaseSecurityConstraintsRef constraintsRef = null;
        if (constraints.getSecurityConstraintsRefClass() != null)
        {
            try
            {
                constraintsRef = (BaseSecurityConstraintsRef)constraints.getSecurityConstraintsRefClass().newInstance();
            }
            catch (InstantiationException ie)
            {
                throw new ClassCastException("Unable to create constratins reference list element instance: " + constraints.getSecurityConstraintsRefClass().getName() + ", (" + ie + ").");
            }
            catch (IllegalAccessException iae)
            {
                throw new ClassCastException("Unable to create constraints reference list element instance: " + constraints.getSecurityConstraintsRefClass().getName() + ", (" + iae + ").");
            }
        }
        else
        {
            constraintsRef = new BaseSecurityConstraintsRef();
        }
        constraintsRef.setName(name);
        // make sure element is unique
        if (constraints.accessConstraintsRefs().contains(constraintsRef))
        {
            throw new IllegalArgumentException("Unable to add duplicate entry to list: " + constraintsRef.getName());
        }
        // retrieve from removed list to reuse
        // previously removed element
        if (removedConstraintsRefs != null)
        {
            int removedIndex = removedConstraintsRefs.indexOf(constraintsRef);
            if (removedIndex >= 0)
            {
                constraintsRef = removedConstraintsRefs.remove(removedIndex);
            }
        }
        return constraintsRef;
    }

    /**
     * getRemovedConstraintsRefs
     *
     * @return removed constraints refs tracking collection
     */
    private List<BaseSecurityConstraintsRef> getRemovedConstraintsRefs()
    {
        if (removedConstraintsRefs == null)
        {
            removedConstraintsRefs = Collections.synchronizedList(new ArrayList<BaseSecurityConstraintsRef>());
        }
        return removedConstraintsRefs;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, String element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > constraints.accessConstraintsRefs().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // wrap and verify constraints ref name string
        BaseSecurityConstraintsRef constraintsRef = wrapNameStringForAdd(element);
        // add to underlying ordered list
        constraints.accessConstraintsRefs().add(index, constraintsRef);
        // set apply order in added element
        if (index > 0)
        {
            constraintsRef.setApplyOrder(constraints.accessConstraintsRefs().get(index-1).getApplyOrder() + 1);
        }
        else
        {
            constraintsRef.setApplyOrder(0);
        }
        // maintain apply order in subsequent elements
        for (int i = index, limit = constraints.accessConstraintsRefs().size() - 1; (i < limit); i++)
        {
            BaseSecurityConstraintsRef nextConstraintsRef = constraints.accessConstraintsRefs().get(i + 1);
            if (nextConstraintsRef.getApplyOrder() <= constraintsRef.getApplyOrder())
            {
                // adjust apply order for next element
                nextConstraintsRef.setApplyOrder(constraintsRef.getApplyOrder() + 1);
                constraintsRef = nextConstraintsRef;
            }
            else
            {
                // apply order maintained for remaining list elements
                break;
            }
        }
        // clear all cached security constraints
        constraints.clearAllSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public String get(int index)
    {
        // implement for modifiable AbstractList:
        // unwrap constraints ref name string
        return constraints.accessConstraintsRefs().get(index).getName();
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public String remove(int index)
    {
        // implement for modifiable AbstractList
        BaseSecurityConstraintsRef removed = constraints.accessConstraintsRefs().remove(index);
        if (removed != null)
        {
            // save removed element 
            getRemovedConstraintsRefs().add(removed);
            // clear all cached security constraints
            constraints.clearAllSecurityConstraints();
        }
        return removed.getName();
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public String set(int index, String element)
    {
        // implement for modifiable AbstractList:
        // wrap and verify constraints ref name string
        BaseSecurityConstraintsRef newConstraintsRef = wrapNameStringForAdd(element);
        // set in underlying ordered list
        BaseSecurityConstraintsRef constraintsRef = constraints.accessConstraintsRefs().set(index, newConstraintsRef);
        // set apply order in new element
        newConstraintsRef.setApplyOrder(constraintsRef.getApplyOrder());
        // save replaced element
        getRemovedConstraintsRefs().add(constraintsRef);
        // clear all cached security constraints
        constraints.clearAllSecurityConstraints();
        // return unwrapped constraints ref name string
        return constraintsRef.getName();
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return constraints.accessConstraintsRefs().size();
    }
}
