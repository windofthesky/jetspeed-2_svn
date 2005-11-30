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
import java.util.ArrayList;
import java.util.List;

/**
 * PageSecurityConstraintsDefList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class PageSecurityConstraintsDefList extends AbstractList
{
    private PageSecurityImpl pageSecurity;

    private List removedConstraintsDefs;

    PageSecurityConstraintsDefList(PageSecurityImpl pageSecurity)
    {
        super();
        this.pageSecurity = pageSecurity;
    }

    /**
     * validateConstraintsDefForAdd
     *
     * Validates constraints def to be added to this list.
     *
     * @param constraintsDef constraints definition to add
     * @return list element to add
     */
    private SecurityConstraintsDefImpl validateConstraintsDefForAdd(SecurityConstraintsDefImpl constraintsDef)
    {
        // only non-null definitions supported
        if (constraintsDef == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        // make sure element is unique
        if (pageSecurity.accessConstraintsDefs().contains(constraintsDef))
        {
            throw new IllegalArgumentException("Unable to add duplicate entry to list: " + constraintsDef.getName());
        }
        // retrieve from removed list to reuse
        // previously removed element copying
        // security constraint defs
        if (removedConstraintsDefs != null)
        {
            int removedIndex = removedConstraintsDefs.indexOf(constraintsDef);
            if (removedIndex >= 0)
            {
                SecurityConstraintsDefImpl addConstraintsDef = constraintsDef;
                constraintsDef = (SecurityConstraintsDefImpl)removedConstraintsDefs.remove(removedIndex);
                constraintsDef.setSecurityConstraints(addConstraintsDef.getSecurityConstraints());
            }
        }
        return constraintsDef;
    }

    /**
     * getRemovedConstraintsDefs
     *
     * @return removed constraints defs tracking collection
     */
    private List getRemovedConstraintsDefs()
    {
        if (removedConstraintsDefs == null)
        {
            removedConstraintsDefs = new ArrayList(pageSecurity.accessConstraintsDefs().size());
        }
        return removedConstraintsDefs;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > pageSecurity.accessConstraintsDefs().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // verify constraints definition
        SecurityConstraintsDefImpl constraintsDef = validateConstraintsDefForAdd((SecurityConstraintsDefImpl)element);
        // add to underlying ordered list
        pageSecurity.accessConstraintsDefs().add(index, constraintsDef);
        // clear cached security constraints definition map
        pageSecurity.clearSecurityConstraintsDefsMap();
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public Object get(int index)
    {
        // implement for modifiable AbstractList
        return pageSecurity.accessConstraintsDefs().get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public Object remove(int index)
    {
        // implement for modifiable AbstractList
        SecurityConstraintsDefImpl removed = (SecurityConstraintsDefImpl)pageSecurity.accessConstraintsDefs().remove(index);
        if (removed != null)
        {
            // save removed element 
            getRemovedConstraintsDefs().add(removed);
            // clear cached security constraints definition map
            pageSecurity.clearSecurityConstraintsDefsMap();
        }
        return removed;
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public Object set(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // verify constraints definition
        SecurityConstraintsDefImpl newConstraintsDef = validateConstraintsDefForAdd((SecurityConstraintsDefImpl)element);
        // set in underlying ordered list
        SecurityConstraintsDefImpl constraintsDef = (SecurityConstraintsDefImpl)pageSecurity.accessConstraintsDefs().set(index, newConstraintsDef);
        // save replaced element
        getRemovedConstraintsDefs().add(constraintsDef);
        // clear cached security constraints definition map
        pageSecurity.clearSecurityConstraintsDefsMap();
        // return constraints definition
        return constraintsDef;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return pageSecurity.accessConstraintsDefs().size();
    }
}
