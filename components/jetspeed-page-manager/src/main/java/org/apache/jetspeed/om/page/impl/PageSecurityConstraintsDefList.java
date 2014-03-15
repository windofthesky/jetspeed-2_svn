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

import org.apache.jetspeed.om.page.SecurityConstraintsDef;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PageSecurityConstraintsDefList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class PageSecurityConstraintsDefList extends AbstractList<SecurityConstraintsDef>
{
    private PageSecurityImpl pageSecurity;

    private List<SecurityConstraintsDef> removedConstraintsDefs;

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
    private SecurityConstraintsDefImpl validateConstraintsDefForAdd(SecurityConstraintsDef constraintsDef)
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
                SecurityConstraintsDef addConstraintsDef = constraintsDef;
                constraintsDef = removedConstraintsDefs.remove(removedIndex);
                // TODO: move this logic to copy methods on implementations
                constraintsDef.setSecurityConstraints(addConstraintsDef.getSecurityConstraints());
            }
        }
        return (SecurityConstraintsDefImpl)constraintsDef;
    }

    /**
     * getRemovedConstraintsDefs
     *
     * @return removed constraints defs tracking collection
     */
    private List<SecurityConstraintsDef> getRemovedConstraintsDefs()
    {
        if (removedConstraintsDefs == null)
        {
            removedConstraintsDefs = Collections.synchronizedList(new ArrayList<SecurityConstraintsDef>());
        }
        return removedConstraintsDefs;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, SecurityConstraintsDef element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > pageSecurity.accessConstraintsDefs().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // verify constraints definition
        SecurityConstraintsDefImpl constraintsDef = validateConstraintsDefForAdd(element);
        // add to underlying ordered list
        pageSecurity.accessConstraintsDefs().add(index, constraintsDef);
        // clear cached security constraints definition map
        pageSecurity.clearSecurityConstraintsDefsMap();
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public SecurityConstraintsDef get(int index)
    {
        // implement for modifiable AbstractList
        return pageSecurity.accessConstraintsDefs().get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public SecurityConstraintsDef remove(int index)
    {
        // implement for modifiable AbstractList
        SecurityConstraintsDefImpl removed = pageSecurity.accessConstraintsDefs().remove(index);
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
    public SecurityConstraintsDef set(int index, SecurityConstraintsDef element)
    {
        // implement for modifiable AbstractList:
        // verify constraints definition
        SecurityConstraintsDefImpl newConstraintsDef = validateConstraintsDefForAdd((SecurityConstraintsDefImpl)element);
        // set in underlying ordered list
        SecurityConstraintsDefImpl constraintsDef = pageSecurity.accessConstraintsDefs().set(index, newConstraintsDef);
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
