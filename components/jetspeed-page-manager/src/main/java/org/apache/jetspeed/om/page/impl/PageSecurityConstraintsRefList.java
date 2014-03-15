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
 * PageSecurityConstraintsRefList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class PageSecurityConstraintsRefList extends AbstractList<String>
{
    private PageSecurityImpl pageSecurity;

    private List<PageSecurityGlobalSecurityConstraintsRef> removedConstraintsRefs;

    PageSecurityConstraintsRefList(PageSecurityImpl pageSecurity)
    {
        super();
        this.pageSecurity = pageSecurity;
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
    private PageSecurityGlobalSecurityConstraintsRef wrapNameStringForAdd(String name)
    {
        // only non-null names supported
        if (name == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        // wrap constraints ref name string
        PageSecurityGlobalSecurityConstraintsRef constraintsRef = new PageSecurityGlobalSecurityConstraintsRef();
        constraintsRef.setName(name);
        // make sure element is unique
        if (pageSecurity.accessGlobalConstraintsRefs().contains(constraintsRef))
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
    private List<PageSecurityGlobalSecurityConstraintsRef> getRemovedConstraintsRefs()
    {
        if (removedConstraintsRefs == null)
        {
            removedConstraintsRefs = Collections.synchronizedList(new ArrayList<PageSecurityGlobalSecurityConstraintsRef>());
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
        if ((index < 0) || (index > pageSecurity.accessGlobalConstraintsRefs().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // wrap and verify constraints ref name string
        PageSecurityGlobalSecurityConstraintsRef constraintsRef = wrapNameStringForAdd(element);
        // add to underlying ordered list
        pageSecurity.accessGlobalConstraintsRefs().add(index, constraintsRef);
        // set apply order in added element
        if (index > 0)
        {
            constraintsRef.setApplyOrder(pageSecurity.accessGlobalConstraintsRefs().get(index-1).getApplyOrder() + 1);
        }
        else
        {
            constraintsRef.setApplyOrder(0);
        }
        // maintain apply order in subsequent elements
        for (int i = index, limit = pageSecurity.accessGlobalConstraintsRefs().size() - 1; (i < limit); i++)
        {
            PageSecurityGlobalSecurityConstraintsRef nextConstraintsRef = pageSecurity.accessGlobalConstraintsRefs().get(i + 1);
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
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public String get(int index)
    {
        // implement for modifiable AbstractList:
        // unwrap constraints ref name string
        return pageSecurity.accessGlobalConstraintsRefs().get(index).getName();
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public String remove(int index)
    {
        // implement for modifiable AbstractList:
        // save removed element 
        PageSecurityGlobalSecurityConstraintsRef removed = pageSecurity.accessGlobalConstraintsRefs().remove(index);
        if (removed != null)
        {
            getRemovedConstraintsRefs().add(removed);
            return removed.getName();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public String set(int index, String element)
    {
        // implement for modifiable AbstractList:
        // wrap and verify constraints ref name string
        PageSecurityGlobalSecurityConstraintsRef newConstraintsRef = wrapNameStringForAdd(element);
        // set in underlying ordered list
        PageSecurityGlobalSecurityConstraintsRef constraintsRef = pageSecurity.accessGlobalConstraintsRefs().set(index, newConstraintsRef);
        // set apply order in new element
        newConstraintsRef.setApplyOrder(constraintsRef.getApplyOrder());
        // save replaced element
        getRemovedConstraintsRefs().add(constraintsRef);
        // return unwrapped constraints ref name string
        return constraintsRef.getName();
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return pageSecurity.accessGlobalConstraintsRefs().size();
    }
}
