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

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;

import java.util.AbstractList;

/**
 * SecurityConstraintList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class SecurityConstraintList extends AbstractList<SecurityConstraint>
{
    private SecurityConstraintsImpl constraints;

    SecurityConstraintList(SecurityConstraintsImpl constraints)
    {
        super();
        this.constraints = constraints;
    }

    /**
     * validateConstraintForAdd
     *
     * Validates constraint to be added to this list.
     *
     * @param constraint to add
     * @return list element to add
     */
    private SecurityConstraintImpl validateConstraintForAdd(SecurityConstraint constraint)
    {
        // validate constraint instance class
        if (constraint == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        if ((constraints.getSecurityConstraintClass() != null) &&
            !constraints.getSecurityConstraintClass().isInstance(constraint))
        {
            throw new ClassCastException("Unable to add list element instance: expected " + constraints.getSecurityConstraintClass().getName() + ", got " + constraint.getClass().getName() + ".");
        }
        return (SecurityConstraintImpl)constraint;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, SecurityConstraint element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > constraints.accessConstraints().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // verify constraint
        SecurityConstraintImpl constraint = validateConstraintForAdd(element);
        // add to underlying ordered list
        constraints.accessConstraints().add(index, constraint);
        // set apply order in added element
        if (index > 0)
        {
            constraint.setApplyOrder(constraints.accessConstraints().get(index-1).getApplyOrder() + 1);
        }
        else
        {
            constraint.setApplyOrder(0);
        }
        // maintain apply order in subsequent elements
        for (int i = index, limit = constraints.accessConstraints().size() - 1; (i < limit); i++)
        {
            SecurityConstraintImpl nextConstraint = constraints.accessConstraints().get(i + 1);
            if (nextConstraint.getApplyOrder() <= constraint.getApplyOrder())
            {
                // adjust apply order for next element
                nextConstraint.setApplyOrder(constraint.getApplyOrder() + 1);
                constraint = nextConstraint;
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
    public SecurityConstraint get(int index)
    {
        // implement for modifiable AbstractList
        return constraints.accessConstraints().get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public SecurityConstraint remove(int index)
    {
        // implement for modifiable AbstractList
        SecurityConstraint removed = constraints.accessConstraints().remove(index);
        if (removed != null)
        {
            // clear all cached security constraints
            constraints.clearAllSecurityConstraints();
        }
        return removed;
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public SecurityConstraint set(int index, SecurityConstraint element)
    {
        // implement for modifiable AbstractList:
        // verify constraint
        SecurityConstraintImpl newConstraint = validateConstraintForAdd(element);
        // set in underlying ordered list
        SecurityConstraintImpl constraint = constraints.accessConstraints().set(index, newConstraint);
        // set apply order in new element
        newConstraint.setApplyOrder(constraint.getApplyOrder());
        // clear all cached security constraints
        constraints.clearAllSecurityConstraints();
        // return constraint
        return constraint;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return constraints.accessConstraints().size();
    }
}
